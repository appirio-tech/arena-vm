/*
 * Copyright (C) 2007 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.services;

import java.sql.SQLException;

import com.topcoder.server.common.ChallengeAttributes;

/**
 * Implements the processing logic required before calling into the services package.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Update {@link #recordSystemTestResult(int, int, int, int, int, Object,
 *     boolean, long, int, int, String)} to handle failure message.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), gevak
 * @version 1.1
 */
public class AutoSystemTestDisabledState implements SRMTestSchedulerState {

    public void addChallengeAsSystemTestCase(ChallengeAttributes chal) {
    }

    public void addSystemTestMark() {
    }

    public void execAutoSystemTest(int roomId, int coderId, int componentId) {
    }

    public boolean isAutoSystemTestsEnabled() {
        return false;
    }

    public boolean isPossibleToCancelATestCase() {
        return true;
    }

    public boolean isPossibleToStartManualSystemTests() {
        return true;
    }

    public void removeSystemTestMark() {
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
        TestService.recordSystemTestResult(contestID, coderID, roundID, componentID, testCaseId, resultValue,
                passed, execTime,failure, systemTestVersion, message);
    }
}
