/*
 * Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
 */
 
/*
 * LongTesterInvoker
 * 
 * Created 09/13/2006
 */
package com.topcoder.server.farm.longtester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.topcoder.farm.client.invoker.FarmException;
import com.topcoder.farm.client.invoker.FarmFactory;
import com.topcoder.farm.client.invoker.FarmInvoker;
import com.topcoder.farm.client.invoker.InvokerConfiguration;
import com.topcoder.farm.client.util.FarmFactoryProvider;
import com.topcoder.farm.client.util.HierarchicalIdBuilder;
import com.topcoder.farm.client.util.HierarchicalIdDisassembler;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationRequestSummaryItem;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.LongSubmissionId;
import com.topcoder.server.farm.common.RoundUtils;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class is responsible for testing long submission and solutions.<p>
 * 
 * Farm interaction and processor selection is resolved by this class.<p>
 * 
 * Farm Information: This class registers into the farm as client LongTester-ID
 * where the ID is given by the creator of this class.
 * 
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added parameter <code>executionTimeLimit</code> to methods
 *      {@link #testSubmission(LongSubmissionId, ComponentFiles, Solution, long[], int, int, int)},
 *      {@link #systemTestSubmission(LongSubmissionId, ComponentFiles, long[], int, int, int)},
 *      {@link #testSolution(int, ComponentFiles, Solution, long[], Object[][], Integer, int, int)},
 *      {@link #doTestsOnSubmission(LongSubmissionId, ComponentFiles, long[], int, int, int, int, HierarchicalIdBuilder, int)}.</li>
 *  </ul> 
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added parameter <code>cppApprovedPath</code> to methods
 *      {@link #testSubmission(LongSubmissionId, ComponentFiles, Solution, long[], int, int, int, String)},
 *      {@link #systemTestSubmission(LongSubmissionId, ComponentFiles, long[], int, int, int, String)},
 *      {@link #testSolution(int, ComponentFiles, Solution, long[], Object[][], Integer, int, int, String)},
 *      {@link #doTestsOnSubmission(LongSubmissionId, ComponentFiles, long[], int, int, int, int, HierarchicalIdBuilder, int)}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added parameter <code>pythonCommand</code>,<code>pythonApprovedPath</code> to methods
 *      {@link #testSubmission(LongSubmissionId, ComponentFiles, Solution, long[], int, int, int, String,String,String)},
 *      {@link #systemTestSubmission(LongSubmissionId, ComponentFiles, long[], int, int, int, String,String,String)},
 *      {@link #testSolution(int, ComponentFiles, Solution, long[], Object[][], Integer, int, int, String,String,String)},
 *      {@link #doTestsOnSubmission(LongSubmissionId, ComponentFiles, long[], int, int, int, String, String, String ,int, HierarchicalIdBuilder, int)}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update these methods to custom setting refactor
 *      {@link #testSubmission(LongSubmissionId, ComponentFiles, Solution, long[], int, int, int, String,String,String)},
 *      {@link #systemTestSubmission(LongSubmissionId, ComponentFiles, long[], int, int, int, String,String,String)},
 *      {@link #testSolution(int, ComponentFiles, Solution, long[], Object[][], Integer, ProblemCustomSettings)},
 *      {@link #doTestsOnSubmission(LongSubmissionId, ComponentFiles, long[], int, ProblemCustomSettings ,int, HierarchicalIdBuilder, int)}.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.4
 */
public class LongTesterInvoker {
    //TODO we should split this class into one for long submission testing, and another for MPSQAS long testing
    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(LongTesterInvoker.class);
    
    /*
     * All generated request ids by this Class must have at least 4 dots.
     * 
     * 
     * Invocation request id of Submission:
     * R{roundId}.A{testAction}.U{coderId}.S{(1|0 example)(submissionNumber)}.T{testCaseId}..
     * 
     * Invocation request id for score caculation 
     * R{roundId}.A{SCORE_ACTION}.B{testAction}.I{longTestScoreGenerationID}
     * 
     * Invocation request id of MPSQAS solution test:
     * A7.A7.G{groupId}.A7.T{testCaseId}..  //Uggly but until split class we need some pattern
     */

    /*
     * Prefixes used to store objects on the farm
     * 
     * Submission Test
     * R{roundId}.A10.U{coderId}
     * 
     * System Test for round
     * R{roundId}.A12
     * 
     * MPSQAS test for solution
     * A7.A7.G{groupId}.A7
     */
    
    /*
     * Stored Objects keys :
     * 
     * SolutionKey  = {prefix}.OX..
     * 
     * TestCaseKey  = {prefix}.T{testCaseId}..
     *
     * UserComponentFilesKey  = {prefix}.U{coderId}.S{(1|0 example)(submissionNumber)}..
     */
    private static final char TYPE_TEST = 'T';
    private static final char TYPE_ROUND = 'R';
    private static final char TYPE_SUBMISSION = 'S';
    private static final char TYPE_SOLUTION = 'O';
    private static final char TYPE_CODER = 'U';
    private static final char TYPE_ACTION = 'A';
    private static final char TYPE_GROUP = 'G';
    private static final char TYPE_ACTION_GENERATING_REQUEST = 'B';
    private static final char TYPE_RESULT_ID = 'I';
    
    private static final String LONG_TESTER_ID_PREFIX = "LongTester";
    
    private static final int ACTION_SCORE = 13;
    
    
    /**
     * The tester instance id. This id is used as suffix for the farm 
     * client identifier.
     */
    private String testerId;

    

    protected LongTesterInvoker(String testerId) {
        this.testerId = testerId;
    }

    public static LongTesterInvoker configure(String id, LongTesterHandler handler) {
        return create(id, handler);
    }

    /**
     * Creates a new LongTester and configures it.<p>
     * 
     * NOTE: It is not allowed to create multiple instances of a LongTester using the same
     * tester id suffix. Doing this will produce unpredictable results. It client user responsability
     * to ensure that only one instance for ths given Id is created. 
     * 
     * @param id The id suffix to use for farm identification
     * @param handler The handler that will handle tests results
     * 
     * @return The new LongTester instance.
     */
    public static LongTesterInvoker create(String id, LongTesterHandler handler) {
        String testerName = getTesterName(id);
        log.info("Configuring longtest invoker with id="+testerName);
        FarmFactory farmFactory = FarmFactoryProvider.getConfiguredFarmFactory();
        farmFactory.configureHandler(testerName, new LongTesterFarmHandler(handler));
        InvokerConfiguration invokerConfiguration = new InvokerConfiguration();
        invokerConfiguration.setCancelOnRegistration(false);
        invokerConfiguration.setDeliverOnRegistration(true);
        farmFactory.configureInvoker(testerName, invokerConfiguration);
        return new LongTesterInvoker(testerName);
    }

    /**
     * Tests the submission against all given testCases .<p>
     * 
     * Test cases must be stored on the farm previously. To store TestCases for a submission test
     * see {@link LongTesterInvoker#storeTestCase(LongSubmissionId, long, Object[])};
     * 
     * Results of the tests are reported through {@link LongTesterHandler#reportLongTestSubmissionResult(LongSubmissionId, long, LongTestResults)}
     * 
     * @param submissionId The id of the submission
     * @param componentFiles The component files of the submission to test
     * @param primarySolution The primary solution required to test the submission.
     * @param testCaseIds The test cases identifiers to run against the submission
     * @param roundType The round type where the submission is being tested
     * @param custom problem customization.
     *  
     * @throws LongTesterException If the one of the test could not be scheduled
     */
    public void testSubmission(LongSubmissionId submissionId, ComponentFiles componentFiles, Solution primarySolution, long[] testCaseIds,
        int roundType, ProblemCustomSettings custom) throws LongTesterException {
        HierarchicalIdBuilder prefix = buildUserTestRelatedPrefix(submissionId.getRoundId(), submissionId.getCoderId());
        storePrimarySolution(primarySolution, buildSolutionId(prefix));
        doTestsOnSubmission(submissionId, componentFiles, testCaseIds, roundType, custom,
                ServicesConstants.LONG_TEST_ACTION, prefix, getTestSubmissionPriority());
    }
    
    /**
     * System test the submission against all test cases given.<p>
     * 
     * Test cases must be stored on the farm previously. To store system tests cases for a submission system test
     * see {@link LongTesterInvoker#storeTestCase(int, long, Object[])}. Also, primary solution should be stored 
     * previously using the method {@link LongTesterInvoker#storePrimarySolution(int, Solution)}.<p>
     * 
     * If there are system test cases for the same submission pending for execution, the last stored component files
     * will be used.<p>
     *
     * Results of the tests are reported through {@link LongTesterHandler#reportLongSystemTestSubmissionResult(LongSubmissionId, long, LongTestResults)}
     *      
     * @param submissionId The id of the submission
     * @param componentFiles The component files of the submission to test
     * @param testCaseIds The test cases identifiers to run against the submission 
     * @param roundType The round type where the submission is being tested
     * @param custom problem customization.
     *  
     * @throws LongTesterException If the one of the test could not be scheduled
     */
    public void systemTestSubmission(LongSubmissionId id, ComponentFiles componentFiles, long[] testCaseIds,
        int roundType, ProblemCustomSettings custom) throws LongTesterException {
        doTestsOnSubmission(id, componentFiles, testCaseIds, roundType, custom,
                ServicesConstants.LONG_SYSTEM_TEST_ACTION, buildRoundSystemTestRelatedPrefix(id.getRoundId()),
                getSystemTestSubmissionPriority());
    }

    /**
     * Tests the solution against all given testCases .<p>
     * 
     * Results of the tests are reported through {@link LongTesterHandler#reportLongTestSolutionResult(int, long, LongTestResults)}
     * 
     * @param groupId The id of the group generated to test the solution
     * @param componentFiles The component files of the solution to test
     * @param primarySolution The primary solution required to test the solution.
     * @param testCaseIds The test cases identifiers generated to tests the solution 
     * @param args An array containing an Object[] with  args of each test case
     * @param roundType The round type where the submission is being tested, null if no roundType is used 
     * @param custom problem customization.
     *  
     * @throws LongTesterException If the one of the test could not be scheduled
     */
    public void testSolution(int groupId, ComponentFiles componentFiles, Solution primarySolution, long[] testCaseIds,
        Object[][] args, Integer roundType, ProblemCustomSettings custom) throws LongTesterException {
        int memLimit = custom.getMemLimit();
        InvocationRequirements requirements = buildRequeriments(roundType, memLimit, componentFiles.getLanguageId());

        HierarchicalIdBuilder context = buildGroupRelatedPrefix(groupId);
        
        String solutionKey = buildSolutionId(context);
        String componentFilesKey = context.buildId(TYPE_SOLUTION, 'S');
        storePrimarySolution(primarySolution, solutionKey);
        
        try {
            storeComponentFiles(componentFiles, componentFilesKey);
        } catch (Exception e1) {
            throw new LongTesterException(e1);
        }
        
        try {
            for (int i = 0; i < testCaseIds.length; i++) {
                long testCaseId = testCaseIds[i];
                FarmLongTestRequest longTest = new FarmLongTestRequest();
                longTest.setCodeType(FarmLongTestRequest.CODE_TYPE_SOLUTION);
                longTest.setProblemCustomSettings(custom);
                longTest.setMaxThreadCount(RoundUtils.maxThreadsForRoundType(roundType));
                longTest.setArguments(args[i]);
                
                LongTestInvocation invocation = new LongTestInvocation(longTest);
                LongTestId attachId = new LongTestId(FarmLongTestRequest.CODE_TYPE_SOLUTION, new Integer(groupId), testCaseId, ServicesConstants.MPSQAS_TEST_ACTION);
                InvocationRequest request = new InvocationRequest(context.buildId(TYPE_TEST, testCaseId), attachId, requirements, invocation);
                request.addSharedObjectRef("solution", solutionKey);
                request.addSharedObjectRef("componentFiles", componentFilesKey);
                getInvoker().scheduleInvocation(request);
            }
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }

    /**
     * Calculates the overall scores in the context of a long submission test finalization.<p>
     * 
     * Results of the score calculation is reported through {@link LongTesterHandler#reportCalculateTestScoreResult(LongSubmissionId, LongRoundScores)}
     *  
     * @param id The id of the solution finalized
     * @param solution The primary solution that will be used to calculate the scores
     * @param longTestResults The LongRoundScores used for overall score calculation
     * @param roundType the round type for the component, which is used for selecting test processor
     * 
     * @throws LongTesterException If overall scores calculation not be scheduled
     */
    public void calculateTestScores(LongSubmissionId id, Solution solution, LongRoundScores longTestResults, int roundType) throws LongTesterException {
        doCalculateScores(id.getRoundId(), id,  solution, longTestResults, ServicesConstants.LONG_TEST_ACTION, roundType);
    }

    
    /**
     * Calculates the overall scores in the context of a round system test finalization.<p>
     * 
     * Results of the score calculation is reported through {@link LongTesterHandler#reportCalculateSystemTestScoreSubmissionResult(int, LongRoundScores)}
     *  
     * @param roundId The id of the round
     * @param solution The primary solution that will be used to calculate the scores
     * @param longTestResults The LongRoundScores used for overall score calculation
     * @param roundType the round type for the component, which is used for selecting test processor
     * 
     * @throws LongTesterException If overall scores calculation not be scheduled
     */
    public void calculateSystemTestScores(int roundId, Solution solution, LongRoundScores longTestResults, int roundType) throws LongTesterException {
        doCalculateScores(roundId, new Integer(roundId), solution, longTestResults, ServicesConstants.LONG_SYSTEM_TEST_ACTION, roundType);
    }
    
    /**
     * Returns true if there are not enqueued items for testing for user 
     * 
     * @param roundId The id of round 
     * @param coderId The id of the coder 
     * 
     * @return true if no test is enqueued for the user and round
     * @throws LongTesterException If was not possible to obtain the status
     */
    public boolean isQueueEmptyForUser(int roundId, long coderId) throws LongTesterException {
        String requestId = buildUserTestRelatedPrefix(roundId, coderId).getPrefix();
        try {
            return getInvoker().countPendingRequests(requestId).intValue() == 0;
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }

    /**
     * Cancels all enqueued tests for the given user
     * 
     * @param roundId The id of round 
     * @param coderId The id of the coder
     * 
     * @throws LongTesterException If was not possible to cancel all tests for the user
     */
    public void cancelTestsForUser(int roundId, long coderId) throws LongTesterException {
        String requestId = buildUserTestRelatedPrefix(roundId, coderId).getPrefix();
        try {
            getInvoker().cancelPendingRequests(requestId);
            getInvoker().removeSharedObjects(requestId);
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    /**
     * Returns true if there are not enqueued system tests for the given round 
     * 
     * @param roundId The id of round 
     * 
     * @return true if no system test is enqueued for the round
     * @throws LongTesterException If was not possible to obtain the status
     */
    public boolean isEmptyOfSystemTestsForRound(int roundId) throws LongTesterException {
        String requestId = buildRoundSystemTestRelatedPrefix(roundId).getPrefix();
        try {
            return getInvoker().countPendingRequests(requestId).intValue() == 0;
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    /**
     * Returns true if there are not enqueued system test for the the given user and round 
     * 
     * @param roundId The id of round 
     * @param coderId The id of the coder 
     * 
     * @return true if no test is enqueued for the user and round
     * @throws LongTesterException If was not possible to obtain the status
     */
    public boolean isEmptyOfSystemTestsForUser(int roundId, long coderId) throws LongTesterException {
        String requestId = buildRequestIdPrefix(buildRoundSystemTestRelatedPrefix(roundId), coderId).getPrefix();
        try {
            return getInvoker().countPendingRequests(requestId).intValue() == 0;
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    /**
     * Returns true if there are not enqueued items for testing for the round (user or system) 
     * 
     * @param roundId The id of round 
     * 
     * @return true if no test is enqueued for the round
     * @throws LongTesterException If was not possible to obtain the status
     */
    public boolean isQueueEmptyForRound(int roundId) throws LongTesterException {
        String requestId = buildRoundRelatedPrefix(roundId).getPrefix();
        try {
            return getInvoker().countPendingRequests(requestId).intValue() == 0;
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    /**
     * Cancels and remove from the Test queue all system test cases enqueued for the round
     * 
     * @param roundId The id of the round
     */
    public void cancelSystemTestsForRound(int roundId) throws LongTesterException {
        String requestId = buildRoundSystemTestRelatedPrefix(roundId).getPrefix();
        try {
            getInvoker().cancelPendingRequests(requestId);
            getInvoker().removeSharedObjects(requestId);
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    /**
     * Cancels and remove from the Test queue all test cases (system and user) enqueued for the round
     * 
     * @param roundId The id of the round
     */
    public void cancelTestsForRound(int roundId) throws LongTesterException {
        String requestId = buildRoundRelatedPrefix(roundId).getPrefix();
        try {
            getInvoker().cancelPendingRequests(requestId);
            getInvoker().removeSharedObjects(requestId);
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    /**
     * Cancels all enqueued tests for the given group
     * 
     * @param testGroupId The id of the group 
     * 
     * @throws LongTesterException If was not possible to cancel all tests for the group
     */
    public void cancelTestsForGroup(int testGroupId) throws LongTesterException {
        String requestId = buildGroupRelatedPrefix(testGroupId).getPrefix();
        try {
            getInvoker().cancelPendingRequests(requestId);
            getInvoker().removeSharedObjects(requestId);
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }

    
    /**
     * Returns the current queue status for this LongTesterInvoker<p>
     * 
     * Information is summarized in the followinf way:
     *  Submission tests are grouped by testAction, roundId, coderId, example
     *  Solution tests are grouped by testAction, groupId
     * 
     * @return A list of {@link LongTesterSummaryItem}
     */
    public List queueStatus() throws LongTesterException {
        try {
            List enqueuedRequestsSummary = getInvoker().getClientNode().getEnqueuedRequestsSummary("", ""+HierarchicalIdBuilder.DELIMITER, 4);
            ArrayList result = new ArrayList(enqueuedRequestsSummary.size());
            HierarchicalIdDisassembler disassembler = new HierarchicalIdDisassembler();
            for (Iterator it = enqueuedRequestsSummary.iterator(); it.hasNext();) {
                InvocationRequestSummaryItem item = (InvocationRequestSummaryItem) it.next();
                disassembler.parseId(item.getRequestIdPrefix());
                if (disassembler.getValueAsInt(1) == ServicesConstants.MPSQAS_TEST_ACTION) {
                    result.add(new LongTesterSummaryItem(
                                disassembler.getValueAsInt(1), 
                                disassembler.getValueAsInt(2), 
                                item));
                } else if (disassembler.getValueAsInt(1) == ServicesConstants.LONG_TEST_ACTION ||
                            disassembler.getValueAsInt(1) == ServicesConstants.LONG_SYSTEM_TEST_ACTION)   { 
                    result.add(new LongTesterSummaryItem(
                                disassembler.getValueAsInt(1), 
                                disassembler.getValueAsInt(0), 
                                disassembler.getValueAsInt(2), 
                                disassembler.getValueAsString(3).charAt(0) == '1',
                                Integer.parseInt(disassembler.getValueAsString(3).substring(1)),
                                item));
                } else {
                    log.debug("Ignoring invocation summary:" + item.getRequestIdPrefix());
                    //We ignore other kind of invocation
                }
            }
            return result;
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }

    /**
     * Returns the number of pending invocations owned by this Tester. <p>
     * 
     * This value includes submission tests, submission system tests, solution tests, 
     * and score calculation invocations.
     * 
     * @return The number of pending invocations
     * @throws LongTesterException If the number of pending invocations could not be obtained
     */
    public int getNumberOfPendingInvocations() throws LongTesterException {
        try {
            return getInvoker().getClientNode().countPendingRequests().intValue();
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    /**
     * Stores the primary solution in the context of the given round
     * 
     * @param roundId the id of the round
     * @param solution The solution to store
     * @throws LongTesterException If the solution could not be stored
     */
    public void storePrimarySolution(int roundId, Solution solution) throws LongTesterException {
        HierarchicalIdBuilder prefix = buildRoundSystemTestRelatedPrefix(roundId);
        storePrimarySolution(solution, buildSolutionId(prefix));
    }

    /**
     * Returns true if primary solution for the round has been stored 
     * 
     * @param roundId The id of the round
     * @return true if stored
     * @throws LongTesterException If solution storage check could not be performed
     */
    public boolean isStoredPrimarySolution(int roundId) throws LongTesterException {
        HierarchicalIdBuilder prefix = buildRoundSystemTestRelatedPrefix(roundId);
        try {
            return getInvoker().countSharedObjects(buildSolutionId(prefix)).intValue() == 1;
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }

    /**
     * Store test case in the context of the given submission
     * 
     * @param id the id of the submission
     * @param testCaseId The id of the testCase
     * @param args The args for the test case
     * @throws LongTesterException If the test case could not be stored
     */
    public void storeTestCase(LongSubmissionId id, long testCaseId, Object[] args) throws LongTesterException {
        HierarchicalIdBuilder prefix = buildUserTestRelatedPrefix(id.getRoundId(), id.getCoderId());
        storeTestCase(args, buildTestCaseId(prefix, testCaseId));
    }
    
    /**
     * Store test case in the context of the given round
     * 
     * @param roundId the id of the round
     * @param testCaseId The id of the testCase
     * @param args The args for the test case
     * @throws LongTesterException If the test case could not be stored
     */
    public void storeTestCase(int roundId, long testCaseId, Object[] objects) throws LongTesterException {
        HierarchicalIdBuilder prefix = buildRoundSystemTestRelatedPrefix(roundId);
        storeTestCase(objects, buildTestCaseId(prefix, testCaseId));
    }
    
    /**
     * Returns true if the test case have been stored for the round has been stored 
     * 
     * @param roundId The id of the round
     * @param testCaseId the id of the test case
     * @return true if stored
     * @throws LongTesterException If test case storage check could not be performed
     */
    public boolean isStoredTestCase(int roundId, long testCaseId) throws LongTesterException {
        HierarchicalIdBuilder prefix = buildRoundSystemTestRelatedPrefix(roundId);
        try {
            return getInvoker().countSharedObjects(buildTestCaseId(prefix, testCaseId)).intValue() == 1;
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }

    /**
     * Release all resources taken by this tester
     */
    public void releaseTester() {
        FarmFactory.getInstance().releaseInvoker(getTesterName(testerId));
    }

    /**
     * Ensures that the connection to the farm is made, to start listening for responses to previous invocations
     * 
     * @throws LongTesterException if the connection to the farm could not be established.
     */
    public void ensureListeningResults() throws LongTesterException{
        try {
            getInvoker();
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    /**
     * <p>
     * do the submission test.
     * </p>
     * @param id the submission id of long problem.
     * @param componentFiles the component files.
     * @param testCaseIds the test case ids.
     * @param roundType the round type.
     * @param memLimit the memory limit.
     * @param custom the problem custom settings
     * @param testAction the test action.
     * @param context the build context.
     * @param priorityCorrection the priority correction.
     * @throws LongTesterException
     *       if error occur during the submission test.
     */
    private void doTestsOnSubmission(LongSubmissionId id,
            ComponentFiles componentFiles, long[] testCaseIds, int roundType,
            ProblemCustomSettings custom, int testAction,
            HierarchicalIdBuilder context, int priorityCorrection) throws LongTesterException {
        
        InvocationRequirements requirements = buildRequeriments(roundType, custom.getMemLimit(), componentFiles.getLanguageId());

        String solutionKey = buildSolutionId(context);
        String componentFilesKey = buildUserComponentFilesId(context, id);
        try {
            storeComponentFiles(componentFiles, componentFilesKey);
        } catch (DuplicatedIdentifierException e1) {
            log.warn("Duplicate identifier when trying to store component files with key="+componentFilesKey+". Using previous stored component files.");
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
        HierarchicalIdBuilder requestIdBuilder = buildRequestIdPrefix(buildRoundAndActionPrefix(id.getRoundId(), testAction), id);
        try {
            for (int i = 0; i < testCaseIds.length; i++) {
                long testCaseId = testCaseIds[i];

                FarmLongTestRequest longTest = new FarmLongTestRequest();
                longTest.setCodeType(FarmLongTestRequest.CODE_TYPE_SUBMISSION);
                longTest.setProblemCustomSettings(custom);
                longTest.setMaxThreadCount(RoundUtils.maxThreadsForRoundType(roundType));
                
                Invocation invocation = new LongTestInvocation(longTest);
                
                LongTestId attachId = new LongTestId(FarmLongTestRequest.CODE_TYPE_SUBMISSION, id, testCaseId, testAction);
                InvocationRequest request = new InvocationRequest(requestIdBuilder.buildId(TYPE_TEST, testCaseId), attachId, requirements, invocation);
                if (testAction == ServicesConstants.LONG_SYSTEM_TEST_ACTION) {
                    request.setRequestAsExclusiveProcessorUsage();
                }
                request.addSharedObjectRef("solution", solutionKey);
                request.addSharedObjectRef("componentFiles", componentFilesKey);
                request.addSharedObjectRef("arguments", buildTestCaseId(context, testCaseId));
                request.setPriority(priorityCorrection);
                getInvoker().scheduleInvocation(request);
            }
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    private void doCalculateScores(int roundId, Object id, Solution solution, LongRoundScores longTestResults, int testAction, int roundType) throws LongTesterException {
        //R{roundId}.A99.B{testAction}.I{resultGenerationId}..
        String requestId = buildRoundAndActionPrefix(roundId, ACTION_SCORE)
                                        .add(TYPE_ACTION_GENERATING_REQUEST, testAction).
                                        buildId(TYPE_RESULT_ID, longTestResults.getResultGenerationId());
        InvocationRequirements requirements = buildRecalcScoreRequirements(roundType);
        try {
            Invocation invocation = new LongTestRecalculateScoresInvocation(solution, longTestResults);
            InvocationRequest request = new InvocationRequest(requestId, new LongTestScoreId(testAction, id), requirements, invocation);
            request.setPriority(InvocationRequest.PRIORITY_HIGH);
            getInvoker().scheduleInvocation(request);
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }
    
    private void storeTestCase(Object[] objects, String testCaseKey) throws LongTesterException {
        try {
            getInvoker().storeSharedObject(testCaseKey, objects);
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }

    private void storeComponentFiles(ComponentFiles componentFiles, String componentFilesKey) throws DuplicatedIdentifierException, NotAllowedToRegisterException, FarmException {
        getInvoker().storeSharedObject(componentFilesKey, componentFiles);
    }
    
    private void storePrimarySolution(Solution solution, String solutionKey) throws LongTesterException {
        try {
            getInvoker().storeSharedObject(solutionKey, solution);
        } catch (Exception e) {
            throw new LongTesterException(e);
        }
    }

    /**
     * Build the requirements builder for long test of score calculation.
     * @param roundType the round type used to select test processor.
     * @return the built invocation requirements
     */
    private InvocationRequirements buildRecalcScoreRequirements(int roundType) {
        LongTestRequerimentsBuilder builder = new LongTestRequerimentsBuilder();
        builder.languageId(ContestConstants.JAVA);
        builder.roundType(roundType);
        InvocationRequirements requirements = builder.buildRequeriments();
        return requirements;
    }

    private InvocationRequirements buildRequeriments(int roundType, int memLimit, int languageId) {
        return buildRequeriments(new Integer(roundType), memLimit, languageId);
    }
    
    private InvocationRequirements buildRequeriments(Integer roundType, int memLimit, int languageId) {
        LongTestRequerimentsBuilder builder = new LongTestRequerimentsBuilder();
        builder.minMemoryAvailable(memLimit);
        if (roundType != null) {
            builder.roundType(roundType.intValue());
        }
        builder.languageId(languageId);
        InvocationRequirements requirements = builder.buildRequeriments();
        return requirements;
    }

    private FarmInvoker getInvoker() throws NotAllowedToRegisterException, FarmException {
        return FarmFactory.getInstance().getInvoker(testerId);
    }
    
    private String buildTestCaseId(HierarchicalIdBuilder builder, long testCaseId) {
        return builder.buildId(TYPE_TEST, testCaseId);
    }
    
    private String buildSolutionId(HierarchicalIdBuilder builder) {
        return builder.buildId(TYPE_SOLUTION, "X");
    }

    private String buildUserComponentFilesId(HierarchicalIdBuilder b, LongSubmissionId id) {
        HierarchicalIdBuilder builder = new HierarchicalIdBuilder(b).add(TYPE_CODER, id.getCoderId());
        return builder.buildId(TYPE_SUBMISSION, buildSubmissionId(id.isExample(), id.getSubmissionNumber()));
    }

    private String buildSubmissionId(boolean example, int submissionNumber) {
        return (example ? "1" : "0") + submissionNumber;
    }

    private HierarchicalIdBuilder buildGroupRelatedPrefix(long testGroupId) {
        return new HierarchicalIdBuilder()
            .add(TYPE_ACTION, ServicesConstants.MPSQAS_TEST_ACTION)
            .add(TYPE_ACTION, ServicesConstants.MPSQAS_TEST_ACTION)
            .add(TYPE_GROUP, testGroupId).add(TYPE_ACTION, ServicesConstants.MPSQAS_TEST_ACTION);
    }

    private HierarchicalIdBuilder buildRequestIdPrefix(HierarchicalIdBuilder context, LongSubmissionId id) {
        return buildRequestIdPrefix(context, id.getCoderId())
            .add(TYPE_SUBMISSION, (id.isExample() ? "1" : "0") + id.getSubmissionNumber());
    }
    
    private HierarchicalIdBuilder buildRequestIdPrefix(HierarchicalIdBuilder context, long coderId) {
        return new HierarchicalIdBuilder(context)
            .add(TYPE_CODER, coderId);
    }
    
    private HierarchicalIdBuilder buildUserTestRelatedPrefix(int roundId, long coderId) {
        return buildRoundAndActionPrefix(roundId, ServicesConstants.LONG_TEST_ACTION)
                    .add(TYPE_CODER, coderId);
    }
    
    private HierarchicalIdBuilder buildRoundAndActionPrefix(int roundId, int action) {
        return buildRoundRelatedPrefix(roundId).add(TYPE_ACTION, action);
    }
    
    private HierarchicalIdBuilder buildRoundSystemTestRelatedPrefix(int roundId) {
        return buildRoundAndActionPrefix(roundId, ServicesConstants.LONG_SYSTEM_TEST_ACTION);
    }
    
    private HierarchicalIdBuilder buildRoundRelatedPrefix(int roundId) {
        return new HierarchicalIdBuilder()
                    .add(TYPE_ROUND, roundId);
    }
    
    private static String getTesterName(String id) {
        return LONG_TESTER_ID_PREFIX+"-"+id;
    }
    
    private int getSystemTestSubmissionPriority() {
        return InvocationRequest.PRIORITY_LOW;
    }
    
    private int getTestSubmissionPriority() {
        return InvocationRequest.PRIORITY_NORMAL;
    }
    
    protected void finalize() throws Throwable {
        try {
            releaseTester();
        } catch (Throwable e) {
        }
    }

    /**
     * LongTestInvoker results handler.<p>
     * 
     * Implementators of this interface are responsible for handling responses for test results.<p>
     * 
     * NOTE: Results for the same submission and test case id might arrive more than one time. 
     */
    public static interface LongTesterHandler {
        /**
         * This method is called every time a result for a long test case is ready.
         * 
         * @param id The id of the tested submission 
         * @param testCaseId The id of the test case
         * @param testResults The result of the test
         * @return true if the result was handled successfully
         */
        boolean reportLongTestSubmissionResult(LongSubmissionId id, long testCaseId, LongTestResults testResults);
        
        /**
         * This method is called every time a result for a long system test case is ready.
         * 
         * @param id The id of the tested submission 
         * @param testCaseId The id of the test case
         * @param testResults The result of the test
         * @return true if the result was handled successfully 
         */
        boolean reportLongSystemTestSubmissionResult(LongSubmissionId id, long testCaseId, LongTestResults testResults);
        
        /**
         * This method is called every time a result for a mpsqas long test case is ready.
         * 
         * @param int the Id of the Test group
         * @param testCaseId The id of the test case
         * @param testResults The result of the test
         * @return true if the result was handled successfully
         */
        boolean reportLongTestSolutionResult(int groupId, long testCaseId, LongTestResults testResults);
        

        /**
         * This method is called every time a result for an score calculation invoked
         * using {@link LongTesterInvoker#calculateTestScores(LongSubmissionId, Solution, LongRoundScores)} is ready.
         * 
         * @param id The long submission id for which the score calculation was made
         * @param lrr The LongRoundScores produced as a result of the calculation
         * @return true if the result was handled successfully
         */
        boolean reportCalculateTestScoreResult(LongSubmissionId id, LongRoundScores lrr);

        /**
         * This method is called every time a result for an score calculation invoked
         * using {@link LongTesterInvoker#calculateSystemTestScores(int, Solution, LongRoundScores)} is ready.
         * 
         * @param roundId The id of the round for which the score calculation was made
         * @param lrr The LongRoundScores produced as a result of the calculation
         * @return true if the result was handled successfully
         */
        boolean reportCalculateSystemTestScoreSubmissionResult(int roundId, LongRoundScores lrr);
    }
}
