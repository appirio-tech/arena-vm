/*
 * Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.services;

import java.rmi.RemoteException;

import com.topcoder.server.common.ChallengeAttributes;

/**
 *  STR test scheduler state.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #recordSystemTestResult(int, int, int, int, int, Object, boolean, double, int, int, String)}
 *     to handle changes failure message.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), gevak
 * @version 1.1
 */
public interface SRMTestSchedulerState {

    public boolean isAutoSystemTestsEnabled();

    public void execAutoSystemTest(int roomId, int coderId, int componentId);

    public void addChallengeAsSystemTestCase(ChallengeAttributes chal);

    public void addSystemTestMark();

    public void removeSystemTestMark();

    public boolean isPossibleToStartManualSystemTests();

    public boolean isPossibleToCancelATestCase();

    /**
     * Records system test result.
     *
     * @param contestId Contest ID.
     * @param coderId Coder ID.
     * @param roundId Round ID.
     * @param problemId Problem ID.
     * @param testCaseId Test case ID.
     * @param result Result.
     * @param succeeded Succeeded.
     * @param execTime Execution time.
     * @param failure_reason Failure reason code.
     * @param systemTestVersion System test version.
     * @param message Failure message.
     */
    public void recordSystemTestResult(int contestID, int coderID, int roundID, int componentID, int testCaseId,
            Object resultValue, boolean passed, long execTime, int failure, int systemTestVersion, String message);
}