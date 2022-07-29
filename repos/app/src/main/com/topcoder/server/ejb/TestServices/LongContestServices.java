package com.topcoder.server.ejb.TestServices;

import java.rmi.RemoteException;
import java.util.List;

import com.topcoder.netCommon.contest.SurveyAnswerData;
import com.topcoder.server.common.LongSubmissionId;
import com.topcoder.server.ejb.DBServices.DBServices;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.messaging.messages.LongCompileRequest;
import com.topcoder.shared.messaging.messages.LongCompileResponse;
import com.topcoder.shared.problem.ProblemComponent;

public interface LongContestServices {
    
    public static final int LONG_TEST_RESULT_TYPE_EXAMPLE = 0;
    public static final int LONG_TEST_RESULT_TYPE_NON_SYSTEM = 1;
    public static final int LONG_TEST_RESULT_TYPE_SYSTEM = 2;
    
    /**
     * Register the coderId in the given round. <p>
     * 
     * All checks are made by the service. If a check failed, the registration is aborted.
     * 
     * @param roundId The round to register the user
     * @param coderId The coder id
     * @param surveyData A list containing {@link SurveyAnswerData} for the active survey of the round. See {@link DBServices#getRegistration(int)}
     * 
     * @throws LongContestServicesException If the registration failed for any reason. 
     */
    void register(int roundId, int coderId, List surveyData) throws LongContestServicesException, RemoteException;
    
    /**
     * Save the source code <p> 
     * 
     * All checks are made by the service. If a check failed, the operation is aborted.
     * 
     * @param contestId The id of the contest
     * @param roundId The round id
     * @param componentId The component id
     * @param coderId The coder id
     * @param programText The source code to save
     * @param languageId The language of the source code
     * 
     * @throws LongContestServicesException If the operation failed for any reason. 
     */
    void save(int contestId, int roundId, int componentId, int coderId, String programText, int languageId) throws LongContestServicesException, RemoteException;

    /**
     * Submit. <p>
     * 
     * The request contains all required information for the submit. It also identifies if it is an example submission
     * or a full submimssion. <p> 
     * 
     * All checks are made by the service. If a check failed, the operation is aborted.
     * 
     * @param req The request containing all submission information
     * @return The result of the submission. If the compilation failed, the result contains information of it.
     * 
     * @throws LongContestServicesException If the operation failed for any reason except compilation errors.
     */
    LongCompileResponse submit(LongCompileRequest req) throws CompilationTimeoutException, LongContestServicesException, RemoteException;
    
    /**
     * Returns the history for the given coder and round.
     * 
     * No checks are made by the service.
     * 
     * @param roundId The round Id
     * @param coderId The coder Id 
     * 
     * @return The history 
     * 
     * @throws LongContestServicesException If the operation failed for any reason
     */
    LongCoderHistory getCoderHistory(int roundId, int coderId) throws LongContestServicesException, RemoteException;

    /**
     * Returns the results of the test for the given round/coder/component.<p>
     * 
     * <b>No checks are made by the service.</b>
     * 
     * @param roundId The round id
     * @param coderId The Coder Id
     * @param componentId The component Id
     * @param resultType The type of result: 0 - Last Example Test Results, 1 - Non Example Test Results, 2 -  Final Test Results.  
     * 
     * @return The results for the tests order by test case id
     * 
     * @throws LongContestServicesException  If the operation failed for any reason
     */
    LongTestResult[] getLongTestResults(int roundId, int coderId, int componentId, int resultType) throws LongContestServicesException, RemoteException;

    /**
     * Returns submimssion information.<p>
     * 
     * <b>No checks are made by the service.</b>
     * 
     * @param roundId The round id 
     * @param coderId The coder Id
     * @param componentId The component id
     * @param example If the submission required is an example submission
     * @param submissionNumber The submission number
     * 
     * @return The submission data
     * 
     * @throws LongContestServicesException If the operation failed for any reason
     */
    LongSubmissionData getSubmission(int roundId, int coderId, int componentId, boolean example, int submissionNumber) throws LongContestServicesException, RemoteException;


    /**
     * Get the problem component and opens the component if the coder has not opened it yet.<p>
     * 
     * All checks are made by the service.
     * 
     * @param roundId The round id 
     * @param componentId The component id 
     * @param coderId The coder querying the problem statement
     * @return The problem component
     * 
     * @throws LongContestServicesException If the operation failed for any reason
     */
    ProblemComponent getProblemComponent(int roundId, int componentId, int coderId) throws LongContestServicesException, RemoteException;

    
    /**
     * Returns the current long test queue status summary<p>
     *
     * @return A list of {@link LongTestQueueStatusItem}
     * @throws LongContestServicesException If the queue status cannot be obtained
     */
    public List getLongTestQueueStatus() throws LongContestServicesException, RemoteException;

    /**
     * Returns the number of submissions that are in the queue.<p>
     *
     * This number does not include submission being system tested
     *
     * @return The count
     * @throws LongContestServicesException If the number of submission could not be obtained
     */
    public int getNumberOfSubmissionOnLongTestQueue() throws LongContestServicesException, RemoteException;
    
    
    /**
     * Open the component if it has not been opened yet
     * 
     * <b>No checks are made by the service.</b>
     *  
     * @param contestId The id of the contest
     * @param roundId The id of the round
     * @param componentId The component id
     * @param coderId The coder id
     * @return true if the component was opened, false if it was alread opened
     * 
     * @throws LongContestServicesException if the operation failed for any reason
     */
    boolean openComponentIfNotOpened(int contestId, int roundId, int componentId, int coderId) throws LongContestServicesException, RemoteException;

    /**
     * Returns the total number of system test for the given round
     * 
     * @param roundId the round id
     * 
     * @return the number
     * 
     * @throws LongContestServicesException  if the operation failed for any reason
     */
    int getTotalSystemTests(int roundId) throws LongContestServicesException, RemoteException;
    
    /**
     * Returns the number of system tests for the given that have already finished
     *  
     * @param roundId  The round id 
     * @return the number
     * 
     * @throws LongContestServicesException  if the operation failed for any reason
     */
    int getSystemTestsDone(int roundId) throws LongContestServicesException, RemoteException;
    
    /**
     * Returns an array containing all allowed languages for the given round.
     * 
     * @param roundId The id of the round.
     * @return A non empty array containing allowed languages.
     */
    public Language[] getAllowedLanguagesForRound(int roundId) throws LongContestServicesException, RemoteException;
    

    /*
     * Methods for Internal use, called mainly for callback handlers. Others are exported for admin purposes
     */
    public void recordLongTestResult(LongSubmissionId lt, long testId, int testAction, LongTestResults result) throws RemoteException, LongContestServicesException;

    public void updateFinalScores(LongSubmissionId id, LongRoundScores lrr) throws RemoteException, LongContestServicesException;

    /**
     * Updates the final scores for the round, using the results passed as arguments
     *
     * @param lrr LongRoundResults[] with the final scores for each component in the round
     *
     * @throws LongContestServicesException If the scores could not be updated
     */
    public void updateLongSystemTestFinalScores(LongRoundScores[] lrr) throws RemoteException, LongContestServicesException;

    public void queueLongTestCases(int roundID, long coderID, int componentID, boolean example) throws RemoteException, LongContestServicesException;
    
    //A
    /**
     * Admin option. Recompiles all last submissions for all coder participating in the given
     * round.
     *
     * @param roundId The round for wich all submissions whould be recompiled.
     * @throws LongContestServicesException if an exception is throw while trying to recompile submissions
     */
    public void recompileAllRound(int roundId) throws LongContestServicesException, RemoteException;

    //A
    /**
    * Starts system tests for the long round.
    *
    * @param roundId id of the round
    *
    * @return number of test cases scheduled to run.
    *
    * @throws LongContestServicesException if system tests could not be started.
    */
    public int startLongSystemTests(int roundId) throws LongContestServicesException, RemoteException;

    //A
    /**
     * Enqueues the given system test cases for the last submissions of the coders in the
     * given round. <p>
     * This method can be used to re schedule a system test cases that has failed to be processed
     * in the farm. <p>
     *
     * @param roundId id of the round
     * @param coderIds Ids of the coders
     * @param testCaseIds the ids of the test cases to run against the submissions
     *
     * @throws LongContestServicesException if system tests could not be enqueued.
     */
    public void queueLongSystemTestCase(int roundId, int[] coderIds, long[] testCaseIds) throws LongContestServicesException, RemoteException;

    /**
     * Enqueues the given system test cases for the last submissions of all the coders in the
     * given round. <p>
     * This method can be used to re schedule a system test cases that has failed to be processed
     * in the farm or for adding new system test cases. <p>
     *
     * @param roundId id of the round
     * @param testCaseIds the ids of the test cases to run against the submissions
     *
     * @throws LongContestServicesException if system tests could not be enqueued.
     */
    public void queueLongSystemTestCase(int roundId, long[] testCaseIds) throws LongContestServicesException, RemoteException;
    
    //A
    /**
     * Schedules long system test overall score calculation.
     *
     * @param roundId The id of the round
     * @param componentId The id of the component
     * @throws LongContestServicesException If the score calculation cannot be scheduled
     */
    public void calculateSystemTestScore(int roundId, int componentId) throws LongContestServicesException, RemoteException;

    
    //A
    /**
     * Generates a LongSubmission for the given request and makes the same process all CompileMessageHandler would do. <p>
     *
     * <li> Compiles the submission and store the result using administrator methods
     * <li> Queues Test cases if compilation succeeded.
     *
     * @param req The LongCompileRequest to submit.
     * @return The LongCompileResponse generated when trying to submit.
     *
     * @throws CompilationTimeoutException If compilation request timeout.
     * @throws LongContestServicesException If the submission could not be done
     */
    public LongCompileResponse adminSubmitLong(LongCompileRequest req) throws CompilationTimeoutException, LongContestServicesException, RemoteException;
}