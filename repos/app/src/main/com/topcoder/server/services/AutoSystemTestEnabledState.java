/*
 * Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.Room;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.Submission;
import com.topcoder.server.ejb.ProblemServices.ProblemServicesLocator;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.server.ejb.TestServices.to.SystemTestResult;
import com.topcoder.server.ejb.TestServices.to.SystemTestResultsBatch;
import com.topcoder.server.services.SRMTestScheduler.SystemTestRequest;
import com.topcoder.server.util.ArrayUtils;
import com.topcoder.shared.util.logging.Logger;

/**
 * This state contains almost all the logic required when auto system
 * test option is enabled.<p>
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Update {@link #recordSystemTestResult(int, int, int, int, int)} to handle failure message.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), gevak
 * @version 1.1
 */
public class AutoSystemTestEnabledState implements SRMTestSchedulerState {
    /*
     * System test are enqueued asynchronously. When a execAutoSystemTest is invoked, the request
     * is added to a set of pending system test to enqueue: pendingAutoSystemTest. If a new request
     * is added for a coder/component/round  before processing a previous one, the former is just replaced
     * by the later. The number of threads used for enqueuing auto system tests is maxNumberOfAutoSystemTestEnqueuers.
     *
     * Results are held in memory and reported to the TestSevices in batch. This is done to reduce latency, and reduce
     * the number of EJB calls required. Every minTimeBetweenResultReports or if there are maxSizeResultReportBatch pending
     * results, the batch is sent to the service and results are stored.
     *
     * Successful challenges are held in memory and reported to the ProblemService in order to convert them into system
     * test cases. This is done every minTimeBetweenChallengeCollection and if some of the challenges could be converted,
     * system test cases are enqueue for all submissions that require it. They are the ones that don't failed any system test
     * and were not successfully challenged..
     */


    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(AutoSystemTestEnabledState.class);

    /**
     * Contains all pending AutoSystemTest(s) that should be enqueued.
     * Access to this set must be synchronized on pendingAutoSystemTest
     */
    private LinkedHashSet pendingAutoSystemTest = new LinkedHashSet();

    /**
     * Number of processes that are enqueueing system tests.
     * Access to this property must be synchronized on enqueueingSystemTestsCountMutex
     */
    private int enqueueingSystemTestsCount = 0;
    private Object enqueueingSystemTestsCountMutex = new Object();

    /**
     * Contains all pending SystemTestResults(s) that should be reported for storage.
     * Access to this property must be synchronized on pendingResultsMutex
     */
    private List pendingResults = new LinkedList();
    private Object pendingResultsMutex = new Object();

    /**
     * Contains all pending ChallengesAttributes(s) that should be converted to system tests cases
     * Access to this set must be synchronized on pendingChallengesMutex
     */
    private List pendingChallenges = new LinkedList();
    private Object pendingChallengesMutex = new Object();

    /**
     * The max number of results that can be pending if reporters are idle.
     */
    private int maxSizeResultReportBatch;


    /**
     * Creates a new AutoSystemTestEnabledState.
     *
     * @param minTimeBetweenChallengeCollection Min time between successful challenge collection.
     * @param maxNumberOfAutoSystemTestEnqueuers Max number of threads for enqueuing auto system test
     * @param maxNumberOfAutoSystemTestResultsReporters Max number of threads for reporting system test results
     * @param minTimeBetweenResultReports Min time to wait between reports if the maxSizeResultReportBatch is not reached
     * @param maxSizeResultReportBatch The max size of the report batch
     */
    public AutoSystemTestEnabledState(long minTimeBetweenChallengeCollection, int maxNumberOfAutoSystemTestEnqueuers, int maxNumberOfAutoSystemTestResultsReporters, long minTimeBetweenResultReports, int maxSizeResultReportBatch) {
        for (int i =0; i < maxNumberOfAutoSystemTestEnqueuers; i++) {
            Thread thread = new Thread(new AutoSystemTestEnqueuer(), "autoST-enqueuer-"+i);
            thread.setDaemon(true);
            thread.start();
        }
        Thread t = new Thread(new ChallengeCollector(minTimeBetweenChallengeCollection),"ChallengeCollector");
        t.setDaemon(true);
        t.start();
        this.maxSizeResultReportBatch = maxSizeResultReportBatch;
        for (int i =0; i < maxNumberOfAutoSystemTestResultsReporters; i++) {
            Thread thread = new Thread(new AutoSystemTestReporter(minTimeBetweenResultReports, maxSizeResultReportBatch), "autoST-reporter-"+i);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public boolean isAutoSystemTestsEnabled() {
        return true;
    }


    /*
     * SYSTEM TEST QUEUEING CHECK CODE BEGINS
     */
    public void addSystemTestMark() {
        synchronized (enqueueingSystemTestsCountMutex) {
            enqueueingSystemTestsCount++;
        }
    }

    public void removeSystemTestMark() {
        synchronized (enqueueingSystemTestsCountMutex) {
            enqueueingSystemTestsCount--;
        }
    }

    public boolean isSystemTestMarkSet() {
        synchronized (enqueueingSystemTestsCountMutex) {
            return enqueueingSystemTestsCount > 0;
        }
    }

    public boolean isPossibleToStartManualSystemTests() {
        return !isSystemTestMarkSet() && !existPendingChallenges() && !existPendingAutoSystemTests();
    }

    public boolean isPossibleToCancelATestCase() {
        return !isSystemTestMarkSet() && !existPendingAutoSystemTests();
    }



    /*
     * AUTO SYSTEM TEST CODE BEGINS
     */

    /**
     * Runs auto system test for the given room, coder and component.<p>
     *
     * Systems test are enqueued asynchronously.
     *
     * Impl. note:
     * Results are handled through {@link SRMTestHandler#reportSystemTestResult(SystemTestAttributes)
     *
     * @param roomId The roomId id.
     * @param coderId The coder id.
     * @param componentId The component id to test
     *
     */
    public void execAutoSystemTest(int roomId, int coderId, int componentId) {
        ArrayList testCasesForComponent = TestCaseCache.getTestCasesForComponent(componentId);
        ArrayList ids = (ArrayList) testCasesForComponent.get(0);
        Number maxTestCaseId = (Number) ids.get(ids.size()-1);
        synchronized (pendingAutoSystemTest) {
            pendingAutoSystemTest.add(new AutoSystemTest(roomId, coderId, componentId, maxTestCaseId.intValue()));
            pendingAutoSystemTest.notify();
        }
    }

    private AutoSystemTest nextAutoSystemTest() throws InterruptedException {
        synchronized (pendingAutoSystemTest) {
            while (pendingAutoSystemTest.size() == 0) {
                log.info("No pending auto system test...waiting");
                pendingAutoSystemTest.wait();
            }
            Iterator it = pendingAutoSystemTest.iterator();
            AutoSystemTest task = (AutoSystemTest) it.next();
            it.remove();
            return task;
        }
    }

    public boolean existPendingAutoSystemTests() {
        synchronized (pendingAutoSystemTest) {
            return pendingAutoSystemTest.size() > 0;
        }
    }

    private static class AutoSystemTest {
        int coderId;
        int componentId;
        int roomId;
        int maxTestCaseId;

        public AutoSystemTest(int roomId, int coderId, int componentId, int maxTestCaseId) {
            this.roomId = roomId;
            this.coderId = coderId;
            this.componentId = componentId;
            this.maxTestCaseId = maxTestCaseId;
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + coderId;
            result = PRIME * result + componentId;
            result = PRIME * result + roomId;
            return result;
        }

        public boolean equals(Object obj) {
        	if (obj == null) {
        		return false;
        	}
            final AutoSystemTest other = (AutoSystemTest) obj;
            return coderId == other.coderId && componentId == other.componentId && roomId == other.roomId;
        }
    }

    public class AutoSystemTestEnqueuer implements Runnable {
        public void run() {
            try {
                while (true) {
                    AutoSystemTest test = nextAutoSystemTest();
                    Submission sub;
                    addSystemTestMark();
                    try {
                        Room baseRoom = CoreServices.getRoom(test.roomId, true);
                        try {
                            ContestRoom room = (ContestRoom) baseRoom;
                            Coder coder = room.getCoder(test.coderId);
                            sub = SRMTestScheduler.buildSubmission(coder, test.componentId, test.roomId, CoreServices.getCurrentDBTime());
                        } finally {
                            CoreServices.releaseLock(Room.getCacheKey(test.roomId));
                        }
                        if (sub == null) {
                            continue;
                        }
                        List testCases = TestCaseCache.getTestCasesForComponent(sub.getComponentID());

                        SRMTestScheduler.submitSystemTest(sub, false, false, testCases, true, null, test.maxTestCaseId);
                    } catch (Exception e) {
                        log.error("Exception while trying to queue system test cases coderId="+test.coderId+" componentId="+test.componentId, e);
                    } finally {
                        removeSystemTestMark();
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }



    /*
     *  AUTO CHALLENGE COLLECTION SECTION BEGINS
     */
    public void addChallengeAsSystemTestCase(ChallengeAttributes chal) {
        synchronized (pendingChallengesMutex) {
            pendingChallenges.add(chal);
        }
    }

    private boolean existPendingChallenges() {
        synchronized (pendingChallengesMutex) {
            return pendingChallenges.size() > 0;
        }
    }

    private final class ChallengeCollector implements Runnable {
        private long minMsToWait;

        public ChallengeCollector(long minMsToWait) {
            this.minMsToWait = minMsToWait;
        }

        public void run() {
            long lastTimeStamp = System.currentTimeMillis();
            while (true) {
                try {
                    long diff = System.currentTimeMillis() - lastTimeStamp;
                    if (diff < minMsToWait) {
                        Thread.sleep(minMsToWait - diff);
                    }
                    lastTimeStamp = System.currentTimeMillis();
                    processPendingChallengesSystemTestCase();
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    //This should not happen
                    log.error("Unexpected exception!!!",e);
                }
            }
        }
    }

    void processPendingChallengesSystemTestCase() {
        log.debug("Processing pending successful challenges");
        try {
            List challengesToProcess =  null;
            synchronized (pendingChallengesMutex) {
                if (pendingChallenges.size() > 0) {
                    challengesToProcess = pendingChallenges;
                    pendingChallenges = new LinkedList();
                }
            }
            if (challengesToProcess != null) {
                addSystemTestMark();
                try {
                    log.debug("Found pending successful challenges");
                    processChallenges(challengesToProcess);
                } finally {
                    removeSystemTestMark();
                }
            }
        } catch (Exception e) {
            log.error("Exception while creating system test cases from challenges",e);
        }
    }

    private void processChallenges(List challengesToProcess) {
        Map componentChallenges = collectChallengesByComponent(challengesToProcess);
        Set componentsSet = new HashSet();
        Set testCaseIds = new HashSet();
        for (Iterator it = componentChallenges.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            int componentId = ((Integer) entry.getKey()).intValue();
            List chals = (List) entry.getValue();
            int[] ids = createTestCasesFromChallenges(componentId, chals);
            if (ids.length > 0) {
                componentsSet.add(new Integer(componentId));
                for (int i = 0; i < ids.length; i++) {
                    testCaseIds.add(new Integer(ids[i]));
                }
            }
        }
        if (testCaseIds.size() > 0) {
            Round[] rounds = CoreServices.getAllActiveRounds();
            TestCaseCache.removeTestCasesFromCache(componentsSet);
            for (int i = 0; i < rounds.length; i++) {
                boolean mustTestRound = false;
                Round round = rounds[i];
                Collection divisions = round.getDivisions();
                for (Iterator it = divisions.iterator(); it.hasNext() && !mustTestRound; ) {
                    Integer divId = (Integer) it.next();
                    List comps = round.getDivisionComponents(divId.intValue());
                    for (Iterator itC = comps.iterator(); itC.hasNext() && !mustTestRound; ) {
                        Integer compId = (Integer) itC.next();
                        mustTestRound = componentsSet.contains(compId);
                    }
                }
                if (mustTestRound) {
                    systemTestOnNewTestCases(round.getRoundID(), round.getContestID(), componentsSet, testCaseIds);
                }
            }
        }
    }

    private void systemTestOnNewTestCases(int roundId, int contestId, Set componentsSet, Set testCaseIds) {
        SystemTestRequest request = new SystemTestRequest(
                false,
                contestId,
                roundId,
                0,
                false,
                ArrayUtils.getIntArray(componentsSet),
                testCaseIds);
        SRMTestScheduler.internalExecSystemTest(request);
    }

    private int[] createTestCasesFromChallenges(int componentId, List chals) {
        try {
            Object[][] args = new Object[chals.size()][];
            Object[] results = new Object[chals.size()];
            int index = 0;
            for (Iterator itC = chals.iterator(); itC.hasNext(); ) {
                ChallengeAttributes chal = (ChallengeAttributes) itC.next();
                args[index] = chal.getArgs();
                results[index] = chal.getExpectedResult();
                index++;
            }
            if (log.isDebugEnabled()) {
                log.debug("Found "+results.length+" successful challenges for componentId="+componentId);
            }
            return ProblemServicesLocator.getService().addTestCasesToComponent(componentId, args, results);
        } catch (Exception e) {
            log.error("Error while trying to generate system test cases from challenges for component: "+componentId+" challenges="+chals, e);
            return new int[0];
        }
    }

    private Map collectChallengesByComponent(Collection challengesToProcess) {
        Map componentChallenges = new HashMap();
        for (Iterator it = challengesToProcess.iterator(); it.hasNext(); ) {
            ChallengeAttributes chal = (ChallengeAttributes) it.next();
            Integer componentId = new Integer(chal.getComponentId());
            List chals = (List) componentChallenges.get(componentId);
            if (chals == null) {
                chals = new LinkedList();
                componentChallenges.put(componentId, chals);
            }
            chals.add(chal);
        }
        return componentChallenges;
    }



    /**
     * This method is responsible for recording the results
     * of a particular test case.
     *
     * @param contestId Contest ID.
     * @param coderId Coder ID.
     * @param roundId Round ID.
     * @param componentId Component ID.
     * @param testCaseId Test case ID.
     * @param resultObj Result object.
     * @param succeeded Succeeded.
     * @param execTime Execution time.
     * @param failure_reason Failure reason code.
     * @param systemTestVersion System test version.
     * @param message Failure message.
     */
    public void recordSystemTestResult(int contestID, int coderID, int roundID, int componentID, int testCaseId,
       Object resultValue, boolean passed, long execTime, int failure, int systemTestVersion, String message) {
       SystemTestResult result = new SystemTestResult(contestID, roundID, coderID, componentID, testCaseId,
               resultValue, passed, execTime, failure, systemTestVersion, message);
       synchronized (pendingResultsMutex) {
           pendingResults.add(result);
           if (pendingResults.size() == maxSizeResultReportBatch) {
               pendingResultsMutex.notify();
           }
       }
    }

    private long lastReporterTimeStamp = System.currentTimeMillis();;
    private final class AutoSystemTestReporter implements Runnable {
        private long minMsToWait;
        private int maxSizeResultReportBatch;

        public AutoSystemTestReporter(long minMsToWait, int maxSizeResultReportBatch) {
            this.minMsToWait = minMsToWait;
            this.maxSizeResultReportBatch = maxSizeResultReportBatch;
        }

        public void run() {
            while (true) {
                try {
                    List results = null;
                    synchronized (pendingResultsMutex) {
                        long diff = System.currentTimeMillis() - lastReporterTimeStamp;
                        while (pendingResults.size() < maxSizeResultReportBatch && diff < minMsToWait) {
                            pendingResultsMutex.wait(minMsToWait - diff);
                            diff = System.currentTimeMillis() - lastReporterTimeStamp;
                        }
                        if (pendingResults.size() > 0) {
                            results = pendingResults;
                            pendingResults = new LinkedList();
                        }
                        lastReporterTimeStamp = System.currentTimeMillis();
                    }

                    if (results != null) {
                        try {
                            SystemTestResult[] resultArray = (SystemTestResult[]) results.toArray(new SystemTestResult[results.size()]);
                            TestServicesLocator.getService().recordSystemTestResult(new SystemTestResultsBatch(resultArray));
                        } catch (Exception e) {
                            log.error("Could not record results: ",e);
                            log.error("Results="+results);
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    //This should not happen
                    log.error("Unexpected exception!!!",e);
                }
            }
        }
    }
}
