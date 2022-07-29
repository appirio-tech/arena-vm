/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.ejb.TestServices;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBObject;

import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Results;
import com.topcoder.server.common.Submission;
import com.topcoder.server.common.SubmitResults;
import com.topcoder.server.ejb.TestServices.longtest.LongTestServices;
import com.topcoder.server.ejb.TestServices.to.ComponentAndDependencyFiles;
import com.topcoder.server.ejb.TestServices.to.SystemTestResultsBatch;
import com.topcoder.server.tester.CodeCompilation;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.server.webservice.WebServiceRemoteFile;

/**
 * Test services interface.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #recordSystemTestResult(int, int, int, int, int, Object, boolean, double, int, int, String)}
 *     to handle changes failure message.</li>
 *     <li>Added {@link #getComponentSolution(int)} for retrieving component by ID.</li>
 * </ol>
 * </p>
 *
 * @author gevak
 * @version 1.1
 */
public interface TestServices extends EJBObject, LongTestServices, LongContestServices {

    ComponentFiles getComponentFiles(int contestId, int roundId, int componentID, int coderId, int classFileType) throws RemoteException, TestServicesException;

    ComponentFiles[] getDependencyComponentFiles(int contestId, int roundId, int componentID, int coderId, int classFileType) throws RemoteException, TestServicesException;

    ComponentFiles[] getAllComponentFiles(int contestId, int roundId, int componentID, int coderId, int classFileType) throws RemoteException, TestServicesException;

    /**
     * Returns a Transfer Object containing required objects for testing:
     *  <li>ComponentFiles,
     *  <li>DependencyComponentFiles,
     *  <li>WebServiceClientsForProblem,
     *  <li>Solution if required
     *
     * @param contestId The id of the contest
     * @param roundId The id of round
     * @param problemId The id of problem
     * @param componentId The id of component
     * @param coderId The id of the coder
     * @param componentFilesType Type of class files of the component, {@link ContestConstants#SUBMITTED_CLASS} or {@link ContestConstants#COMPILED_CLASS}
     * @param dependencyFilesType Type of class files of the dependencies, {@link ContestConstants#SUBMITTED_CLASS} or {@link ContestConstants#COMPILED_CLASS}
     * @param solution true if the solution must be include in the result
     *
     * @return The transfer object.
     *
     * @throws TestServicesException If an exception was thrown trying to obtain required objects.
     */
    ComponentAndDependencyFiles getComponentAndDependencyFiles(int contestId, int roundId, int problemId, int componentId, int coderId, int componentFilesType, int dependencyFilesType, boolean solution) throws RemoteException, TestServicesException;

    String recordChallengeResults(ChallengeAttributes chal) throws RemoteException, TestServicesException;

    ArrayList retrieveTestCases(int componentID) throws RemoteException, TestServicesException;

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
     * @throws RemoteException If remote error occurs.
     * @throws TestServicesException If service exception occurs.
     */
    void recordSystemTestResult(int contestId, int coderId, int roundId, int problemId, int testCaseId, Object result,
            boolean succeeded, double execTime, int failure_reason, int systemTestVersion, String message)
                    throws RemoteException, TestServicesException;

    /**
     * Records all the results specfied in <code>resultBatch</code>
     *
     * @param resultBatch containing SystemTestResults to store
     *
     * @throws TestServicesException If an exception was raised during storage, and some tests could not be stored.
     */
    public void recordSystemTestResult(SystemTestResultsBatch resultBatch) throws TestServicesException, RemoteException;

    /**
     * Removes system test results for the given contest/round/coder/component and generates a new system test status version
     *
     * The value returned by this method should be used as arg when reporting system test result for a submission.
     *
     * @see TestServices#recordSystemTestResult(int, int, int, int, int, Object, boolean, double, int, long)
     */
    public int resetSystemTestState(int contestId, int coderId, int roundId, int componentId) throws TestServicesException, RemoteException;

    /**
     * The removeSystemTestsForSubmission is responsible for removing system test results and changing the version to
     * avoid storing new test results.
     */
    public void removeSystemTestsForSubmission(int contestId, int coderId, int roundId, int componentId) throws TestServicesException, RemoteException;

    /**
     * Get the current test version for the given contest/round/coder/component component. This flag is only returned
     * if the current component status is {@link com.topcoder.netCommon.contest.ContestConstants#NOT_CHALLENGE} or
     * {@link com.topcoder.netCommon.contest.ContestConstants#CHALLENGE_FAILED}.
     *
     * Negative values of the flag indicates that a system test case has already failed for the given component state.
     *
     * The value returned by this method should be used as arg when reporting system test result for a submission.
     */
    public int getCurrentSystemTestVersion(int contestId, int coderId, int roundId, int componentId) throws TestServicesException, RemoteException;


    /**
     * Get a Set containing ids of all system test cases that are missing for the submission identified by the given arguments.
     *
     * @param contestId The id of the contest
     * @param coderId The id of the coder
     * @param roundId The id of round
     * @param componentId The id of component
     *
     * @return A Set of Integers representing test case ids.
     *
     * @throws TestServicesException If the set could not be obtained.
     */
    public Set getPendingSystemTestCaseIdsForSubmission(int contestId, int coderId, int roundId, int componentId) throws TestServicesException, RemoteException;


    /**
     * Removes all results related to the given round and test case. The system test version of the component will be fixed
     * if the component had failed the given test case.
     *
     * @param roundId The id of round
     * @param testCaseId The id of testCase for which results must be deleted
     *
     * @throws TestServicesException If the the action could not be accomplished.
     */
    public void deleteSystemTestResultsAndFixVersion(int roundId, int testCaseId) throws TestServicesException, RemoteException;

    /**
     * Updates component state status of all "ComponentStates" of the given round. <p>
     *
     * Status is set to {@link ContestConstants#SYSTEM_TEST_SUCCEEDED} or {@link ContestConstants#SYSTEM_TEST_FAILED}
     * if all system tests for the related submission succeeded or not
     * and if the current status of the component state is
     * {@link ContestConstants#NOT_CHALLENGED} or {@link ContestConstants#CHALLENGE_FAILED}.
     *
     * If status is changed to SYSTEM_TEST_FAILED, points are deducted from the room.
     *
     * @param roundId The if of the round.
     * @throws TestServicesException If the update process failed.
     */
    public void updateComponentStateAndPointsFromSystemTestResults(int roundId) throws TestServicesException, RemoteException;

    Solution getComponentSolution(String className) throws RemoteException, TestServicesException;

    /**
     * Retrieves component solution.
     *
     * @param componentId Component ID.
     * @return Solution.
     * @throws RemoteException If remote error occurs.
     * @throws TestServicesException If service error occurs.
     * @since 1.1
     */
    Solution getComponentSolution(int componentId) throws RemoteException, TestServicesException;

    int getSystestsLeft(int roundID) throws RemoteException, TestServicesException;

    int getTotalSystests(int roundID) throws RemoteException, TestServicesException;

    Results saveComponent(long contestId, long roundId, long componentId, long coderId, String programText, int languageID) throws RemoteException;

    SubmitResults replaySubmit(Submission sub) throws RemoteException;

    SubmitResults submitProblem(Submission sub, long codingLength, boolean restrictPerUserTime) throws RemoteException;

    void recordCompileStatus(CodeCompilation sub) throws RemoteException, TestServicesException;

    void setWebServiceClients(String serviceName, int languageID, List sourceFiles) throws TestServicesException, RemoteException;

    void saveCompiledWebServiceClients(long sourceFileID, WebServiceRemoteFile[] classFiles) throws TestServicesException, RemoteException;

    WebServiceRemoteFile[] getWebServiceClientsForProblem(long problemID, int languageID) throws TestServicesException, RemoteException;

    boolean isTeamComponent(long componentId) throws RemoteException, TestServicesException;
}
