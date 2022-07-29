package com.topcoder.server.processor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundType;
import com.topcoder.server.common.ContestRound;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.server.services.TestService;

final class SystemTestProgress {

    private static final Logger LOG = Logger.getLogger(SystemTestProgress.class);

    private static final int SHORT_DELAY = 1 * 1000;
    private static final int DELAY = 15 * 1000;

    private static Map roundMap = new ConcurrentHashMap();

    private SystemTestProgress() {
    }

    private interface TestObtainer extends Runnable {

        int getTotalTests();

        int getTestsDone();

        void stop();

    }
    
    private static class STRunnable implements TestObtainer {

        int i_contestID;
        int i_roundID;
        volatile int i_totalTests;
        volatile int i_testsDone;
        volatile boolean i_stop = true;

        public STRunnable(int contestID, int roundID) {
            i_contestID = contestID;
            i_roundID = roundID;
            i_stop = false;
        }

        public void run() {
            boolean init = false;
            LOG.info("Starting system test progress monitoring for roundId="+i_roundID);
            while (!i_stop) {

                try {
                    while (!init && !i_stop) {
                        try {
                            i_totalTests = TestService.getTotalSystests(i_roundID);
                            init = true;
                        } catch (Throwable e) {
                            error("", e);
                            sleep(SHORT_DELAY);
                        }
                    }
                    if (i_stop) {
                        return;
                    }
                    int systestsLeft = 0;
                    try {
                        systestsLeft = TestService.getSystestsLeft(i_roundID);
                    } catch (Throwable e) {
                        error("", e);
                        sleep(SHORT_DELAY);
                    }
                    i_testsDone = i_totalTests - systestsLeft;
                    AdminCommands.announcePhase(i_roundID, ContestConstants.SYSTEM_TESTING_PHASE);
                    if (systestsLeft <= 0) {
                        LOG.info("Exiting system test progress monitoring (system test left=0) for roundId="+i_roundID);
                        removeObtainer(i_roundID);
                        Processor.systemTestEndedForRound(i_roundID);
                        return;
                    }
                    sleep(DELAY);
                } catch (Throwable e) {
                    error("", e);
                }
            }
            LOG.info("Exiting system test progress monitoring (stopped) for roundId="+i_roundID);
        }

        public int getTotalTests() {
            return i_totalTests;
        }

        public int getTestsDone() {
            return i_testsDone;
        }

        public void stop() {
            i_stop = true;
        }

    }

    
    private static class LongSTRunnable implements TestObtainer {
        int i_contestID;
        int i_roundID;
        volatile int i_totalTests;
        volatile int i_testsDone;
        volatile boolean i_stop = true;

        public LongSTRunnable(int contestID, int roundID) {
            i_contestID = contestID;
            i_roundID = roundID;
            i_stop = false;
        }

        public void run() {
            int j = 0;
            while (!i_stop) {
                try {
                    while (!i_stop && j % 4 == 0) {
                        try {
                            i_totalTests = LongContestServicesLocator.getService().getTotalSystemTests(i_roundID);
                            break;
                        } catch (Throwable e) {
                            error("", e);
                            sleep(SHORT_DELAY);
                        }
                    }
                    if (i_stop) {
                        return;
                    }
                    try {
                        int systemTestsDone = LongContestServicesLocator.getService().getSystemTestsDone(i_roundID);
                        if (systemTestsDone > i_totalTests) {
                            j = -1;
                        } else {
                            i_testsDone = systemTestsDone;
                        }
                    } catch (Throwable e) {
                        error("", e);
                        sleep(SHORT_DELAY);
                    }
                    if (!i_stop) {
                        AdminCommands.announcePhase(i_roundID, ContestConstants.SYSTEM_TESTING_PHASE);
                    }
                    sleep(DELAY);
                } catch (Throwable e) {
                    error("", e);
                }
                j++;
            }
        }

        public int getTotalTests() {
            return i_totalTests;
        }

        public int getTestsDone() {
            return i_testsDone;
        }

        public void stop() {
            i_stop = true;
        }

    }
    
    static int getTotalTests(int contestID, int roundID) {
        TestObtainer runnable = getTestObtainer(contestID, roundID);
        if(runnable == null) {
            return 0;
        }
        return runnable.getTotalTests();
    }

    static int getTestsDone(int contestID, int roundID) {
        TestObtainer runnable = getTestObtainer(contestID, roundID);
        if(runnable == null) {
            return 0;
        }
        return runnable.getTestsDone();
    }

    static void start(int contestID, int roundID, RoundType roundType) {
        TestObtainer runnable;
        if (roundType.isLongRound()) {
            runnable = new LongSTRunnable(contestID, roundID);
        } else {
            runnable = new STRunnable(contestID, roundID);
        }
        String key = ContestRound.getCacheKey(roundID);
        stop(contestID, roundID);
        roundMap.put(key, runnable);
        Thread t = new Thread(runnable);
        t.start();
    }

    static void startIfNotRunning(int contestID, int roundID, RoundType roundType) {
        if (isRunning(contestID, roundID)) {
            return;
        }
        start(contestID, roundID, roundType);
    }

    
    private static void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    static void stop(int contestID, int roundID) {
        TestObtainer runnable = removeObtainer(roundID);
        if (runnable != null) {
            runnable.stop();
        }
    }

    private static TestObtainer removeObtainer(int roundID) {
        String key = ContestRound.getCacheKey(roundID);
        TestObtainer runnable = (TestObtainer) roundMap.remove(key);
        return runnable;
    }

    private static TestObtainer getTestObtainer(int contestID, int roundID) {
        String key = ContestRound.getCacheKey(roundID);
        TestObtainer runnable = (TestObtainer) roundMap.get(key);
        return runnable;
    }
    
    static boolean isRunning(int contestID, int roundID) {
        TestObtainer runnable = getTestObtainer(contestID, roundID);
        return runnable != null;        
    }

    private static void error(Object message, Throwable t) {
        LOG.error(message, t);
    }

}
