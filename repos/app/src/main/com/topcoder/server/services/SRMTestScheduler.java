/*
 * Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.farm.shared.util.concurrent.runner.ThreadPoolRunner;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.PracticeTestResultData;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Coder;
import com.topcoder.server.common.CoderComponent;
import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.Location;
import com.topcoder.server.common.Round;
import com.topcoder.server.common.Submission;
import com.topcoder.server.common.SystemTestAttributes;
import com.topcoder.server.common.User;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.server.ejb.TestServices.TestServicesException;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.server.ejb.TestServices.to.ComponentAndDependencyFiles;
import com.topcoder.server.farm.tester.TesterInvokerException;
import com.topcoder.server.farm.tester.srm.SRMTesterInvoker;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.util.ArrayUtils;
import com.topcoder.shared.util.StageQueue;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class is responsible for filling SRM test attributes with the required
 * components, scheduling them and handling results. <p>
 *
 * IMPORTANT NOTE:
 * This class should be used only from the Listener
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Update {@link #submitSystemTest(Submission, boolean, boolean, List, boolean,
            Set, int)} to populate solution into test attributes.</li>
       <li>Updated {@link #recordSystemTestResult(int, int, int, int, int, Object, boolean, double, int, int, String)}
       to handle failure message.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *     <li>Updated {@link SRMTestHandler#handlePracticeSystemTestResult(SystemTestAttributes)}
 *     to send results populated with maximum memory used.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), gevak, dexy
 * @version 1.2
 */
class SRMTestScheduler {

    private static final int SYSTEM_TEST_THREADS = 3;
    /**
     * Category for logging.
     */
    private static final Logger s_trace = Logger.getLogger(SRMTestScheduler.class);
    /**
     * SRM tester invoker
     */
    static final SRMTesterInvoker srmTester = SRMTesterInvoker.create("Core", new SRMTestHandler());
    /**
     * Containts Ids generated using {@link SRMTestScheduler#buildSystemTestId(Submission, boolean)} of
     * submissions that are being scheduled. It contains as a value the system test version being enqueued
     */
    private static final HashMap systemTestQueuingInProgress = new HashMap();

    private static SRMTestSchedulerState state = new AutoSystemTestDisabledState();

    /**
     * Submits a challenge test<p>
     *
     * The attributes:  solution, componentFiles, dependencyComponentFiles and compiledWebServiceClientFiles are
     * set by this method. Then the challenge is enqueued for processing. <p>
     *
     * At the time this method exits, challenge test was successfully enqueued for processing.
     *
     * Impl. note:
     * Results are handled through {@link SRMTestHandler#reportChallengeTestResult(ChallengeAttributes)}
     *
     * @param chal The challenge to submit
     * @throws Exception If an exception was thrown in the process
     */
    public static void submitChallengeTest(ChallengeAttributes chal) throws Exception {
        Location location = chal.getLocation();

        ComponentAndDependencyFiles componentAndDependencyFiles = TestService.getComponentAndDependencyFiles(location.getContestID(),
                location.getRoundID(),
                chal.getComponent().getProblemID(),
                chal.getComponent().getComponentID(),
                chal.getDefendantId(),
                ContestConstants.SUBMITTED_CLASS,
                ContestConstants.SUBMITTED_CLASS,
                true);


        Map compiledWebServiceClientFiles = TestService.processWebServiceRemoteFiles(componentAndDependencyFiles.getWebServiceRemoteFiles(), JavaLanguage.ID);

        chal.setSolution(componentAndDependencyFiles.getSolution());
        chal.setComponentFiles(componentAndDependencyFiles.getComponentFiles());
        chal.setDependencyComponentFiles(Arrays.asList(componentAndDependencyFiles.getDependencyComponentFiles()));
        chal.setCompiledWebServiceClientFiles(compiledWebServiceClientFiles);

        srmTester.challengeTest(chal);
    }

    /**
     * Submits a user test<p>
     *
     * The attributes:  solution, componentFiles, dependencyComponentFiles and compiledWebServiceClientFiles are
     * set by this method. Then the user test is enqueued for processing.<p>
     *
     * At the time this method exits, user test was successfully enqueued for processing.
     *
     * Impl. note:
     * Results are handled through {@link SRMTestHandler#reportUserTestResult(UserTestAttributes)}
     *
     * @param userTest The userTest to submit
     * @throws Exception If an exception was thrown in the process
     */
    public static void submitUserTest(UserTestAttributes userTest) throws Exception {
        Location location = userTest.getLocation();

        ComponentAndDependencyFiles componentAndDependencyFiles = TestService.getComponentAndDependencyFiles(location.getContestID(),
                location.getRoundID(),
                userTest.getComponent().getProblemID(),
                userTest.getComponent().getComponentID(),
                userTest.getCoderId(),
                ContestConstants.COMPILED_CLASS,
                ContestConstants.SUBMITTED_CLASS,
                true);

        Map compiledWebServiceClientFiles = TestService.processWebServiceRemoteFiles(componentAndDependencyFiles.getWebServiceRemoteFiles(), JavaLanguage.ID);

        userTest.setSolution(componentAndDependencyFiles.getSolution());
        userTest.setComponentFiles(componentAndDependencyFiles.getComponentFiles());
        userTest.setDependencyComponentFiles(Arrays.asList(componentAndDependencyFiles.getDependencyComponentFiles()));
        userTest.setCompiledWebServiceClientFiles(compiledWebServiceClientFiles);
        srmTester.userTest(userTest);
    }


    /**
     * Runs system test for the given round, components and coder.<p>
     *
     * Systems test are enqueued in an Async manner.
     *
     * Impl. note:
     * Results are handled through {@link SRMTestHandler#reportSystemTestResult(SystemTestAttributes)
     *
     * @param contestId The contest id
     * @param roundId The round id.
     * @param coderId The coder id, 0 all coders
     * @param reference If the test should be run in exclusive mode
     * @param componentIds The component ids to test. null or empty -> all components in the round.
     * @throws SRMTestSchedulerStateException if SRMTestScheduler is not in a valid state to start system test cases.
     */
    public static void execSystemTest(int contestId, int roundId, int coderId,
            boolean reference, int[] componentIds) throws SRMTestSchedulerStateException {
        if (!isPossibleToStartManualSystemTests()) {
            //This is a simple check, this should not be happening, but just in case we make an simple test
            throw new SRMTestSchedulerStateException("There is at least one process queueing system test cases");
        }
        execSystemTest(false, contestId, roundId, coderId, reference, componentIds);
    }


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
    public static void execAutoSystemTest(int roomId, int coderId, int componentId) {
        state.execAutoSystemTest(roomId, coderId, componentId);
    }

    /**
     * Runs practice system test round, components and coder.
     *
     * Practice Systems test are enqueued in an Async manner.
     *
     * Impl. note:
     * Results are handled through {@link SRMTestHandler#reportSystemTestResult(SystemTestAttributes)
     *
     * @param contestId The contest id
     * @param roundId The round id.
     * @param coderId The coder id, 0 all coders
     * @param componentIds The component ids to test. null or empty -> all components in the round.
     */
    public static void execPracticeSystemTest(int contestId, int roundId, int coderId, int[] componentIds) {
        execSystemTest(true, contestId, roundId, coderId, false, componentIds);
    }

    public static void enableAutoSystemTests(long minTimeBetweenChallengeCollection, int maxNumberOfAutoSystemTestEnqueuers, int maxNumberOfAutoSystemTestResultsReporters, long minTimeBetweenResultReports, int maxSizeResultReportBatch) {
        state = new AutoSystemTestEnabledState(
                minTimeBetweenChallengeCollection,
                maxNumberOfAutoSystemTestEnqueuers,
                maxNumberOfAutoSystemTestResultsReporters,
                minTimeBetweenResultReports,
                maxSizeResultReportBatch);
    }

    private static void testCoder(Coder coder, int userId, int roomID, boolean real, boolean reference,
            int[] componentIds, boolean allTestCases, Set testCasesIds) throws Exception {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("testCoder(" + coder + ", " + userId + "," + roomID + ", " + real + ", "+ ArrayUtils.asString(componentIds) );
        }

        int type = real ? ServicesConstants.SYSTEM_TEST_ACTION : ServicesConstants.PRACTICE_TEST_ACTION;
        Map testCaseCountByComponentId = new HashMap();
        List submissions = new ArrayList();
        long currentTime = CoreServices.getCurrentDBTime();
        long[] coderComponentIDs = coder.getComponentIDs();
        Set componentsToTest = null;
        if (componentIds != null && componentIds.length > 0) {
            componentsToTest = new HashSet();
            for (int i = 0; i < componentIds.length; i++) {
                int l = componentIds[i];
                componentsToTest.add(new Long(l));
            }
        }
        Map tempCachePracticeTestCases  = new HashMap();
        for (int i = 0; i < coderComponentIDs.length; i++) {
            if(componentsToTest == null || componentsToTest.contains(new Long(coderComponentIDs[i]))) {
                Submission sub = buildSubmission(coder, coderComponentIDs[i], roomID, currentTime);
                if (sub != null) {
                    if (s_trace.isDebugEnabled()) {
                        s_trace.debug("        coderid=" + coder.getUserIDForComponent((int)coderComponentIDs[i]));
                    }

                    Integer componentId = new Integer(sub.getComponentID());
                    if (type == ServicesConstants.SYSTEM_TEST_ACTION) {
                        s_trace.debug("    real: ");
                        submitSystemTest(sub, reference, false, TestCaseCache.getTestCasesForComponent(sub.getComponentID()), allTestCases, testCasesIds, Integer.MAX_VALUE);
                    } else {
                        ArrayList testCases = TestService.retrieveTestCases(sub.getComponentID());
                        tempCachePracticeTestCases.put(componentId, testCases);
                        testCaseCountByComponentId.put(componentId, new Integer(((List) testCases.get(0)).size()));
                        //We must delay sending because we first need to test case information to notify the client
                        submissions.add(sub);
                    }
                }
            }
        }
        if (type == ServicesConstants.PRACTICE_TEST_ACTION && submissions.size() > 0) {
            //We send notification about starting practice system test cases
            EventService.sendPracticeSystemTestStart(userId, testCaseCountByComponentId);
            for (Iterator it = submissions.iterator(); it.hasNext(); ) {
                Submission sub = (Submission) it.next();
                List testCases = (List) tempCachePracticeTestCases.get(new Integer(sub.getComponentID()));
                submitSystemTest(sub, reference, true, testCases, allTestCases, testCasesIds, Integer.MAX_VALUE);
            }
        }
    }

    /**
     * Cancels and removes information related to the given round/testCase. This is an admin option and it should be
     * used only in case of test case modification/removal.
     *
     * @param contestId The id of the contest
     * @param roundId the id of the round
     * @param testCaseId The id of the test case
     *
     * @throws SRMTestSchedulerStateException If the SRMTestScheduler is not in a valid state to cancel
     *                                        and remove a system test case
     * @throws Exception If the cancellation could not be done.
     */
    public static void cancelSystemTestCaseTesting(int contestId, int roundId, int testCaseId) throws SRMTestSchedulerStateException, Exception {
        //This is a simple check, this should not be happening, but just in case we make an simple test
        if (!isPossibleToCancelATestCase()) {
            throw new SRMTestSchedulerStateException("There is at least one process queueing system test cases");
        }
        if (srmTester.existsPedingSystemTestForRound(contestId, roundId)) {
            throw new SRMTestSchedulerStateException("There are pending system tests for the round");
        }
        //srmTester.cancelSystemTestsForTestCase(contestId, roundId, testCaseId);
        TestServicesLocator.getService().deleteSystemTestResultsAndFixVersion(roundId, testCaseId);
    }

    /**
     *
     *
     * @param submission Submission.
     * @param exclusiveMode Exclusive mode.
     * @param isPractice Is practice flag.
     * @param testCases Test cases.
     * @param resetPrevious Reset previous flag.
     * @param testCaseIdsToInclude Test case ids to include.
     * @param maxTestCaseId Max test case ID.
     * @throws TesterInvokerException If invoker error occurs.
     * @throws TestServicesException If service error occurs.
     * @throws RemoteException If remote error occurs.
     * @throws NamingException If naming error occurs.
     * @throws CreateException If create error occurs.
     */
    static void submitSystemTest(Submission submission,
            boolean exclusiveMode, boolean isPractice, List testCases, boolean resetPrevious,
            Set testCaseIdsToInclude, int maxTestCaseId) throws TesterInvokerException,
                                TestServicesException, RemoteException, NamingException,
                                CreateException {
        Location location = submission.getLocation();
        String queueId = buildSystemTestId(submission.getCoderID(), location, submission.getComponentID(), isPractice);


        if (exclusiveMode && !isPractice) {
            EventService.sendAdminMessage(ContestConstants.SYSTEM_CHAT, "System> Reference Testing - " + submission.getCoderID() + ".\n");
        }

        //We need to obtain the system test version and cancel previous test if needed
        Set pendingIds = null; //PendingIds == null means All tests cases
        int systemTestVersion = 1;
        if (resetPrevious) {
            systemTestVersion = resetSystemTestsOnSubmission(submission.getCoderID(), location, submission.getComponentID(), isPractice);
        } else if (!isPractice && isAutoSystemTestsEnabled()) {
            systemTestVersion = getCurrentSystemTestVersion(submission.getCoderID(), location, submission.getComponentID());
            if (systemTestVersion > 0) {
                if (testCaseIdsToInclude != null && testCaseIdsToInclude.size() > 0) {
                    pendingIds = testCaseIdsToInclude;
                } else {
                    pendingIds = TestServicesLocator.getService().getPendingSystemTestCaseIdsForSubmission(location.getContestID(), submission.getCoderID(), location.getRoundID(), submission.getComponentID());
                    if (!pendingIds.isEmpty()) {
                        Set inFarmIds = srmTester.getPendingSystemTestCaseIdsForSubmission(location.getContestID(), submission.getCoderID(), location.getRoundID(), location.getRoomID(), submission.getComponentID(), isPractice);
                        pendingIds.removeAll(inFarmIds);
                        if (pendingIds.size() == 0) {
                            return;
                        }
                    }
                }
            }
        }

        //A negative system test version indicates some system test already failed. No need to test anymore
        if (systemTestVersion <= 0) {
            return;
        }

        if (!addSystemTestQueuingInProgress(queueId, systemTestVersion)) {
            s_trace.warn("Trying to add system tests for a submission while another thread is doing it");
            //We check this just in case someone do something unexpected
            //This could happen now, just exit
            //throw new IllegalStateException("Trying to add system tests for the submission while another thread is doing it");
            return;
        }
        try {
            s_trace.info("Enqueuing system tests for coderId="+submission.getCoderID()+", componentId="+submission.getComponent().getComponentID());
            ComponentFiles componentFiles = null;
            List dependencyComponentFiles = null;
            Map compiledWebServiceClientFiles = null;
            ComponentAndDependencyFiles componentAndDependencyFiles = null;

            ArrayList ids = (ArrayList) testCases.get(0);
            ArrayList args = (ArrayList) testCases.get(1);
            ArrayList results = (ArrayList) testCases.get(2);

            for (int i = 0; i < ids.size(); i++) {
                if (!isSystemTestQueuingInProgress(queueId, systemTestVersion)) {
                    //If system tests were aborted due to some case failure, stop enqueuing
                    break;
                }
                Number id = (Number) ids.get(i);
                if (pendingIds != null && !pendingIds.contains(id)) {
                    //If we are enqueueing only pending test cases, skip test cases already queued.
                    continue;
                }
                if (id.intValue() > maxTestCaseId) {
                    break;
                }

                if (componentFiles == null) {
                    componentAndDependencyFiles = TestService.getComponentAndDependencyFiles(
                            location .getContestID(),
                            location.getRoundID(),
                            submission.getComponent().getProblemID(),
                            submission.getComponent().getComponentID(),
                            submission.getCoderID(),
                            ContestConstants.SUBMITTED_CLASS,
                            ContestConstants.SUBMITTED_CLASS,
                            true);

                    componentFiles = componentAndDependencyFiles.getComponentFiles();

                    dependencyComponentFiles = Arrays.asList(componentAndDependencyFiles.getDependencyComponentFiles());

                    compiledWebServiceClientFiles = TestService.processWebServiceRemoteFiles(componentAndDependencyFiles.getWebServiceRemoteFiles(), JavaLanguage.ID);
                }

                SystemTestAttributes attr = new SystemTestAttributes();
                attr.setArgs(((List)args.get(i)).toArray());
                attr.setComponentFiles(componentFiles);
                attr.setCompiledWebServiceClientFiles(compiledWebServiceClientFiles);
                attr.setDependencyComponentFiles(dependencyComponentFiles);
                attr.setExclusiveExecution(exclusiveMode);
                attr.setExpectedResult(results.get(i));
                attr.setSubmission(submission);
                attr.setTestCaseId(id.intValue());
                attr.setTestCaseIndex(i);
                attr.setPractice(isPractice);
                attr.setSystemTestVersion(systemTestVersion);
                attr.setSolution(componentAndDependencyFiles.getSolution());
                srmTester.systemTest(attr);
            }
        } finally {
            removeSystemTestQueuingInProgress(queueId, systemTestVersion);
        }
    }

    private static boolean addSystemTestQueuingInProgress(String queueId, int systemTestVersion) {
        synchronized (systemTestQueuingInProgress) {
            QueueData data = (QueueData) systemTestQueuingInProgress.get(queueId);
            if (data != null && data.systemTestVersion > systemTestVersion) {
                return false;
            } else if (data == null) {
                data = new QueueData(systemTestVersion);
                systemTestQueuingInProgress.put(queueId, data);
            } else {
                data.count++;
            }
            return true;
        }
    }

    private static boolean isSystemTestQueuingInProgress(String queueId, int systemTestVersion) {
        synchronized (systemTestQueuingInProgress) {
            QueueData data = (QueueData) systemTestQueuingInProgress.get(queueId);
            return data != null && systemTestVersion == data.systemTestVersion;
        }
    }

    private static void removeSystemTestQueuingInProgress(String queueId, int systemTestVersion) {
        synchronized (systemTestQueuingInProgress) {
            QueueData value = (QueueData) systemTestQueuingInProgress.get(queueId);
            if  (value != null && systemTestVersion == value.systemTestVersion) {
                if (value.count == 1) {
                    systemTestQueuingInProgress.remove(queueId);
                } else {
                    value.count--;
                }
            }
        }
    }

    private static void removeSystemQueuingInProgress(String queueId) {
        synchronized (systemTestQueuingInProgress) {
            systemTestQueuingInProgress.remove(queueId);
        }
    }

    private static class QueueData {
        int systemTestVersion = 0;
        int count = 1;

        public QueueData(int systemTestVersion) {
            this.systemTestVersion = systemTestVersion;
        }
    }

    static void cancelSystemTestsOnSubmission(int coderId, Location location, int componentId, boolean isPractice, Integer systemTestVersion) throws TesterInvokerException {
        String queueId = buildSystemTestId(coderId, location, componentId, isPractice);
        if (systemTestVersion == null) {
            removeSystemQueuingInProgress(queueId);
        } else {
            removeSystemTestQueuingInProgress(queueId, systemTestVersion.intValue());
        }

        //We remove all pending system tests for the submission
        srmTester.cancelSystemTestsOnSubmission(
                location.getContestID(),
                coderId,
                location.getRoundID(),
                location.getRoomID(),
                componentId,
                isPractice);
    }

    private static int resetSystemTestsOnSubmission(int coderId, Location location, int componentId, boolean isPractice) throws TesterInvokerException, TestServicesException, RemoteException, NamingException, CreateException {
        cancelSystemTestsOnSubmission(coderId, location, componentId, isPractice, null);

        if (!isPractice) {
            //If it is not a practice submission we must reset component state information
            return TestServicesLocator.getService().resetSystemTestState(
                    location.getContestID(),
                    coderId,
                    location.getRoundID(),
                    componentId);
        } else {
            return 1;
        }
    }


    static void cancelAndRemoveSystemTestsOnSubmission(int coderId, Location location, int componentId) throws TesterInvokerException, TestServicesException, RemoteException, NamingException, CreateException {
        cancelSystemTestsOnSubmission(coderId, location, componentId, false, null);

        TestServicesLocator.getService().removeSystemTestsForSubmission(
                location.getContestID(),
                coderId,
                location.getRoundID(),
                componentId);
    }

    private static int getCurrentSystemTestVersion(int coderId, Location location, int componentId) {
        //If it is not a practice submission we must get current flag to be able to report results
        try {
            return TestServicesLocator.getService().getCurrentSystemTestVersion(
                    location.getContestID(),
                    coderId,
                    location.getRoundID(),
                    componentId);
        } catch (Exception e) {
            s_trace.error("Exception when trying to obtain system test version for component. coder="+coderId+", componentId="+componentId+", roundId="+location.getRoundID());
            return -1;
        }
    }

    private static String buildSystemTestId(int coderId, Location location, int componentId, boolean practice) {
        return location.getRoomID()+"|"+coderId+"|"+componentId+"|" + practice;
    }

    // if coderId > 0 just test him
    static final class SystemTestRequest {

        private final int m_contestID;
        private final int m_roundID;
        private final int m_coderID;
        private final boolean isPracticeSystemTest;
        private final boolean reference;
        private int[] componentIds;
        /**
         * If this set contains any id, then no pending test cases are searched.
         * Every test case for any component whose id is in set will be included.
         */
        private Set testCaseIdsToAdd;

        private int m_roomID = ContestConstants.INVALID_ROOM;

        private SystemTestRequest(boolean isPracticeSystemTest, int contestID, int roundID, int coderID,
                boolean reference, int[] componentIds) {
            this(isPracticeSystemTest, contestID, roundID, coderID, reference, componentIds, new HashSet());
        }

        SystemTestRequest(boolean isPracticeSystemTest, int contestID, int roundID, int coderID,
                boolean reference, int[] componentIds, Set testCaseIdsToAdd) {

            this.isPracticeSystemTest = isPracticeSystemTest;
            this.m_contestID = contestID;
            this.m_roundID = roundID;
            this.m_coderID = coderID;
            this.reference = reference;
            this.componentIds = componentIds;
            this.testCaseIdsToAdd = testCaseIdsToAdd;
        }

        public boolean isReference() {
            return reference;
        }

        //private int getContestID() { return m_contestID;}
        private int getRoundID() {
            return m_roundID;
        }

        private int getCoderID() {
            return m_coderID;
        }

        private int getRoomID() {
            return m_roomID;
        }

        private void setRoomID(int id) {
            m_roomID = id;
        }

        private boolean isPracticeSystemTest() {
            return isPracticeSystemTest;
        }

        public int hashCode() {
            return m_contestID + m_roundID + m_coderID + m_roomID;
        }

        public boolean equals(Object o) {
            if (o instanceof SystemTestRequest) {
                SystemTestRequest r = (SystemTestRequest) o;
                return m_contestID == r.m_contestID && m_roundID == r.m_roundID && m_coderID == r.m_coderID && m_roomID == r.m_roomID;
            }
            return false;
        }

        public Set getTestCaseIdsToAdd() {
            return testCaseIdsToAdd;
        }

        public String toString() {
            return "SystemTestRequest Contest = " + m_contestID + " Round = " + m_roundID + " Coder = " + m_coderID +" pratice = "+isPracticeSystemTest;
        }
        public int[] getComponentIds() {
            return componentIds;
        }
    }

    private static final class SystemTestRunner implements Runnable {
        private SystemTestRequest m_request;
        private SystemTestRunner(SystemTestRequest request) {
            m_request = request;
        }
        public void run() {
            try {
                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Running system test on: " + m_request);
                }
                internalExecSystemTest(m_request);
                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Completed system test on: " + m_request);
                }
            } catch (Throwable t) {
                s_trace.error("Failure running system test", t);
            }
        }
    }

    private static void execSystemTest(boolean isPracticeSystemTest, int contestId, int roundId, int coderId,
            boolean reference, int[] componentIds) {
        SystemTestRequest request = new SystemTestRequest(isPracticeSystemTest, contestId, roundId, coderId, reference, componentIds);
        if (coderId > 0) {
            User user = CoreServices.getUser(coderId, false);
            request.setRoomID(user.getRoomID());
        }
        StageQueue.addTask(new SystemTestRunner(request));
    }

    static void internalExecSystemTest(final SystemTestRequest request) {
        // get contest info, all the coders, all the submissions
        // iterate thru them and send test messages
        final int coderId = request.getCoderID();
        final boolean reference = request.isReference();

        if (request.isPracticeSystemTest()) {
            ContestRoom room = (ContestRoom) CoreServices.getRoom(request.getRoomID(), false);
            Coder coder = room.getCoder(coderId);
            try {
                testCoder(coder, coderId, request.getRoomID(), false, false, request.getComponentIds(), true, null);
            } catch (Exception e) {
                s_trace.error("Could not schedule practice system tests", e);
            }
        } else {
            EventService.sendAdminMessage(ContestConstants.SYSTEM_CHAT, "System> Starting - " + request.toString() + ".\n");
            addSystemTestMark();
            try {
                Round contest = CoreServices.getContestRound(request.getRoundID());
                Iterator rooms = contest.getAllRoomIDs();
                ThreadPoolRunner runner = new ThreadPoolRunner("System Test Queueing time:"+System.currentTimeMillis(), SYSTEM_TEST_THREADS);
                try {
                roomsLoop:
                    while (rooms.hasNext()) {
                        final int roomId = ((Integer) rooms.next()).intValue();
                        s_trace.debug("doing sys test on room : " + roomId);
                        try {
                            ContestRoom room = (ContestRoom) CoreServices.getRoom(roomId, false);
                            if (!room.isAdminRoom()) {
                                Iterator coders = room.getAllCoders();
                                while (coders.hasNext()) {
                                    final Coder coder = (Coder) coders.next();
                                    if (coderId > 0 && coderId != coder.getID()) {
                                        continue;
                                    }
                                    runner.run(new Runnable() {
                                        public void run() {
                                            try {
                                                testCoder(coder, coderId, roomId, true, reference, request.getComponentIds(), false, request.getTestCaseIdsToAdd());
                                            } catch (Exception e) {
                                                s_trace.error("Exception in execSystemTest", e);
                                                EventService.sendAdminMessage(ContestConstants.SYSTEM_CHAT,
                                                        "System> Exception in system test for coderId: "+ coderId +" message="+ e.getMessage() + ".\n");
                                            }
                                        }
                                    });
                                    if (coderId > 0) {
                                        break roomsLoop;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            s_trace.error("Exception in execSystemTest", e);
                            EventService.sendAdminMessage(ContestConstants.SYSTEM_CHAT,
                                    "System> Exception in system test: " + e.getMessage() + ".\n");
                        }
                    }
                } finally {
                    runner.stopAccepting();
                    s_trace.info("Start waiting for pool termination: "+runner);
                    runner.awaitAllTaskTermination();
                    s_trace.info("End waiting for pool termination: "+runner);
                }
            } catch (Throwable t) {
                s_trace.error("Exception running system test", t);
                EventService.sendAdminMessage(ContestConstants.SYSTEM_CHAT,
                        "System> Error running system test: " + t.getMessage() + ".\n");
            } finally {
                removeSystemTestMark();
                if (s_trace.isDebugEnabled()) {
                    s_trace.debug("Completed system test messages: " + request);
                }
                EventService.sendAdminMessage(ContestConstants.SYSTEM_CHAT,
                        "System> Sent all test messages for - " + request.toString() + ".\n");
            }
        }
    }

    static Submission buildSubmission(Coder coder, long componentId, int roomId, long currentTime) {
        CoderComponent cp = (CoderComponent) coder.getComponent(componentId);
        if (cp.isSubmitted() && cp.sysTestCheck()) {
            if (s_trace.isDebugEnabled()) {
                s_trace.debug("    sys testing CoderComponent: " + cp);
            }
            Submission sub = new Submission(new Location(
                    coder.getContestID(), coder.getRoundID(), roomId),
                    CoreServices.getRoundComponent(coder.getRoundID(), cp.getComponentID(), coder.getDivisionID()),
                    cp.getSubmittedProgramText(),
                    cp.getSubmittedLanguage());

            sub.setSubmitTime(currentTime);
            sub.setCoderId(coder.getUserIDForComponent(cp.getComponentID()));
            return sub;
        }
        return null;
    }

    static void addSystemTestMark() {
        state.addSystemTestMark();
    }

    static void removeSystemTestMark() {
        state.removeSystemTestMark();
    }

    static boolean isPossibleToStartManualSystemTests() {
        return state.isPossibleToStartManualSystemTests();
    }

    static boolean isPossibleToCancelATestCase() {
        return state.isPossibleToCancelATestCase();
    }

    /**
     * The recordSystemTestResult is responsible for recording the results
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
     * @throws TestServicesException If service error occurs.
     */
    static void recordSystemTestResult(int contestID, int coderID, int roundID, int componentID, int testCaseId,
        Object resultValue, boolean passed, long execTime, int failure, int systemTestVersion, String message) {
        state.recordSystemTestResult(contestID, coderID, roundID, componentID, testCaseId, resultValue,
                passed, execTime, failure, systemTestVersion, message);
    }

    static boolean isAutoSystemTestsEnabled() {
        return state.isAutoSystemTestsEnabled();
    }

    static void addChallengeAsSystemTestCase(ChallengeAttributes chal) {
        state.addChallengeAsSystemTestCase(chal);
    }


    /**
     * SRMTesterInvoker.SRMTesterHandler implementation containing current logic for handling
     * SRM tests results. <p>
     *
     * Implementation was extracted from many classes:
     *  <li> com.topcoder.services.message.handler.TestMessageHandler
     *  <li> com.topcoder.services.tester.type.system.(Language)SystemTest
     *  <li> com.topcoder.services.tester.type.user.(Language)UserTest
     *  <li> com.topcoder.services.tester.type.challenge.(Language)ChallengeTest
     *
     * <p>
     * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
     * <ol>
     *       <li>Updated {@link #handlePracticeSystemTestResult(SystemTestAttributes)} to handle check answer response.</li>
     *       <li>Updated {@link #handleRealSystemTestResult(SystemTestAttributes)} to handle check answer response.</li>
     * </ol>
     * </p>
     *
     * @author gevak
     * @version 1.1
     */
    private static class SRMTestHandler implements SRMTesterInvoker.SRMTesterHandler {
        private final Logger log = Logger.getLogger(SRMTestHandler.class);

        public void reportUserTestResult(UserTestAttributes userTest) {
            EventService.sendTestResults(userTest.getCoderId(), ServicesConstants.USER_TEST_ACTION, userTest, userTest.getSubmitTime());
        }

        public void reportChallengeTestResult(ChallengeAttributes chal) {
            try {
                if (chal.isTimeOut() && !chal.isExclusiveExecution()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Rescheduling challenge test due to timeout: "+chal);
                    }
                    chal.setExclusiveExecution(true);
                    srmTester.challengeTest(chal);
                } else {
                    if (!chal.isSystemFailure()) {
                        String error = TestService.recordChallengeResults(chal);
                        if (!error.equals(""))  // It means someone has beaten them to it
                        {
                            chal.setResultCode(ChallengeAttributes.RESULT_SYSTEM_FAILURE);  // to prevent from being broadcast to everyone.
                            chal.setMessage(error);
                        } else {
                            Location location = chal.getLocation();
                            if (chal.isSuccesfulChallenge() && !ContestConstants.isPracticeRoomType(CoreServices.getRoom(location.getRoomID()).getType()) && isAutoSystemTestsEnabled()) {
                                cancelAndRemoveSystemTestsOnSubmission(chal.getDefendantId(), chal.getLocation(), chal.getComponentId());
                                addChallengeAsSystemTestCase(chal);
                            }
                        }
                    }
                    EventService.sendTestResults(chal.getChallengerId(), ServicesConstants.CHALLENGE_TEST_ACTION, chal, chal.getSubmitTime());
                }
            } catch (Exception e) {
                log.error("Exception processing Challenge Response", e);
            }
        }

        public void reportSystemTestResult(SystemTestAttributes attr) {
            try {
                if (attr.isTimeOut() && !attr.isExclusiveExecution()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Rescheduling system test due to timeout: "+attr);
                    }
                    attr.setExclusiveExecution(true);
                    srmTester.systemTest(attr);
                } else {
                    int roundID = attr.getSubmission().getLocation().getRoundID();
                    boolean mustStopSystemTestsOnFailure = CoreServices.getContestRound(roundID).getRoundProperties().mustStopSystemTestsOnFailure();
                    if (!attr.isCorrect()) {
                        if (mustStopSystemTestsOnFailure) {
                            log.info("Aborting system tests due to test "+attr);
                            Submission submission = attr.getSubmission();
                            cancelSystemTestsOnSubmission(submission.getCoderID(), submission.getLocation(), submission.getComponentID(), attr.isPractice(), new Integer(attr.getSystemTestVersion()));
                        }
                    }
                    if (attr.isPractice()) {
                        handlePracticeSystemTestResult(attr);
                    } else {
                        handleRealSystemTestResult(attr);
                    }
                }
            } catch (Exception e) {
                log.error("Could not handle System Test Result: ", e);
                log.error("SystemTestAttributes : "+attr);
            }
        }

        /**
         * Handles practice system test result.
         *
         * @param attr System test attributes.
         */
        private void handlePracticeSystemTestResult(SystemTestAttributes attr) {
            Submission submission = attr.getSubmission();
            PracticeTestResultData data = new PracticeTestResultData(
                    submission.getRoundID(),
                    submission.getLocation().getRoomID(),
                    submission.getComponentID(),
                    attr.getTestCaseIndex(),
                    attr.isCorrect(),
                    attr.getMessage(),
                    attr.getArgs(),
                    attr.getExpectedResult(),
                    attr.getResultValue(),
                    attr.getExecTime(),
                    attr.getMaxMemoryUsed(),
                    attr.getCheckAnswerResponse());

            EventService.sendTestResults(attr.getSubmission().getCoderID(), ServicesConstants.PRACTICE_TEST_ACTION, data, -1);
        }

        /**
         * Handles real system test result.
         *
         * @param attr System test attributes.
         */
        private void handleRealSystemTestResult(SystemTestAttributes attr) {
            Submission submission = attr.getSubmission();
            Location location = submission.getLocation();
            int failure = 0;
            if (!attr.isCorrect()) {
                if (attr.isIncorrect()) {
                    failure = ServicesConstants.FAILURE_INCORRECT_RESULT;
                } else {
                    if (attr.isSystemFailure()) {
                        failure = ServicesConstants.FAILURE_SYSTEM_ERROR;
                    } else if (attr.isException()) {
                        failure = ServicesConstants.FAILURE_EXCEPTION;
                    } else {
                        failure = ServicesConstants.FAILURE_TIMEOUT;
                    }
                }
            }
            recordSystemTestResult(location.getContestID(), submission.getCoderID(), submission.getRoundID(),
                attr.getComponent().getComponentID(),
                attr.getTestCaseId(), attr.getResultValue(),
                attr.isPassed(), attr.getExecTime(), failure, attr.getSystemTestVersion(),
                attr.getCheckAnswerResponse());
        }
    }
}
