/*
 * Copyright (C) 2007-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.farm.tester.srm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.topcoder.farm.client.invoker.FarmException;
import com.topcoder.farm.client.invoker.FarmFactory;
import com.topcoder.farm.client.invoker.FarmInvoker;
import com.topcoder.farm.client.invoker.InvokerConfiguration;
import com.topcoder.farm.client.util.FarmFactoryProvider;
import com.topcoder.farm.client.util.HierarchicalIdBuilder;
import com.topcoder.farm.client.util.HierarchicalIdDisassembler;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationRequestRef;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.Location;
import com.topcoder.server.common.SystemTestAttributes;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.server.farm.tester.TesterInvokerException;
import com.topcoder.server.farm.tester.TesterRequerimentsBuilder;
import com.topcoder.shared.common.ServicesConstants;

/**
 * This class is resposible for testing SRM submissions.<p>
 *
 * Farm interaction and processor selection is resolved by this class.<p>
 *
 * Farm Information: This class registers into the farm as client SRMComp-ID
 * where the ID is given by the creator of this class.
 *
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Update {@link #userTest(UserTestAttributes userTest)}  method.</li>
 * <li>Update {@link #challengeTest(ChallengeAttributes chal)}  method.</li>
 * <li>Update {@link #systemTest(SystemTestAttributes attr)}  method.</li>
 * <li>Update {@link #buildRequeriments(int languageId, int testAction, int roundType)}  method.</li>
 * <li>Update {@link #scheduleTest(String ,int, Invocation, int, boolean, int, boolean, int)}  method.</li>
 * <li>Update {@link #scheduleTest(String ,int, Invocation, int, boolean, int, int, boolean, int)}  method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.0
 */
public class SRMTesterInvoker {
    /*
     * Test request ids:
     *
     * UserTest and Challenge test ids
     * RequestId = A{action}.U{userId}.C{contestId}.R{roundId}.R{roomId}.X{componentId}..
     *
     * System Test and Practice System test ids
     * RequestId = A{action}.U{userId}.C{contestId}.R{roundId}.R{roomId}.X{componentId}.T{testCaseId}..
     */
    private static final char TYPE_ACTION = 'A';
    private static final char TYPE_ROUND = 'R';
    private static final char TYPE_ROOM = 'O';
    private static final char TYPE_CODER = 'U';
    private static final char TYPE_CONTEST = 'C';
    private static final char TYPE_COMPONENT = 'X';
    private static final char TYPE_EXCLUSIVE = 'Z';
    private static final char TYPE_TEST = 'T';

    /**
     * Prefix value used by this tester
     */
    private static final String TESTER_ID_PREFIX = "SRMTest";


    /**
     * The tester instance id. This id is used as suffix for the farm
     * client identifier.
     */
    private String testerId;

    protected SRMTesterInvoker(String testerId) {
        this.testerId = testerId;
    }

    /**
     * Creates a new SRMTesterInvoker and configures it.<p>
     *
     * NOTE: It is not allowed to create multiple instances of a SRMTesterInvoker using the same
     * tester id suffix. Doing this will produce unpredictable results. It is client user responsability
     * to ensure that only one instance for ths given Id is created.
     *
     * @param id The id suffix to use for farm identification
     * @param handler The SRMTesterHandler that will be used to notify test results
     *
     * @return The new SRMTesterInvoker instance.
     */
    public static SRMTesterInvoker create(String id, SRMTesterHandler handler) {
        String testerName = getTesterName(id);
        FarmFactory factory = FarmFactoryProvider.getConfiguredFarmFactory();
        if (!factory.isConfiguredInvoker(testerName)) {
            factory.configureHandler(testerName, new SRMTesterFarmHandler(handler));
            InvokerConfiguration configuration = new InvokerConfiguration();
            configuration.setCancelOnRegistration(false);
            configuration.setDeliverOnRegistration(true);
            factory.configureInvoker(testerName, configuration);
        }
        return new SRMTesterInvoker(testerName);
    }

    /**
     * <p>
     * Schedules the user test for execution.
     * The result will be notified through the handler.
     * </p>
     * @param userTest The user test to schedule
     * @throws TesterInvokerException  If the test could not be scheduled
     */
    public void userTest(UserTestAttributes userTest) throws TesterInvokerException {
        scheduleTest(
                ServicesConstants.USER_TEST_ACTION,
                userTest.getCoderId(),
                userTest.getLocation(),
                userTest.getComponent().getComponentID(),
                new SRMUserTestInvocation(userTest),
                userTest.getLanguage(),
                false,
                -1,
                InvocationRequest.PRIORITY_NORMAL, true, userTest.getComponent().getRoundType());
    }

    /**
     * <p>
     * Schedules the challenge test for execution.
     * The result will be notified through the handler.
     * </p>
     * @param chal The challenge test to schedule
     * @throws TesterInvokerException  If the test could not be scheduled
     */
    public void challengeTest(ChallengeAttributes chal) throws TesterInvokerException {
        scheduleTest(
                ServicesConstants.CHALLENGE_TEST_ACTION,
                chal.getChallengerId(),
                chal.getLocation(),
                chal.getComponent().getComponentID(),
                new SRMChallengeTestInvocation(chal),
                chal.getLanguage(),
                chal.isExclusiveExecution(),
                -1,
                InvocationRequest.PRIORITY_NORMAL, true, chal.getComponent().getRoundType());
    }

    /**
     * <p>
     * Schedules a system test for execution.
     * The result will be notified through the handler.
     * </p>
     * @param chal The system test to schedule
     * @throws TesterInvokerException  If the test could not be scheduled
     */
    public void systemTest(SystemTestAttributes attr) throws TesterInvokerException {
        scheduleTest(
                attr.isPractice() ? ServicesConstants.PRACTICE_TEST_ACTION : ServicesConstants.SYSTEM_TEST_ACTION,
                attr.getSubmission().getCoderID(),
                attr.getSubmission().getLocation(),
                attr.getComponent().getComponentID(),
                new SRMSystemTestInvocation(attr),
                attr.getSubmission().getLanguage(),
                attr.isExclusiveExecution(),
                attr.getTestCaseId(),
                InvocationRequest.PRIORITY_LOW + (attr.isExclusiveExecution() ? -1 : 0), false, attr.getComponent().getRoundType());
    }
    /**
     * <p>
     * schedule the test.
     * </p>
     * @param action
     *       the action.
     * @param coderId
     *       the coder id.
     * @param location
     *       the location.
     * @param componentId
     *       the component id.
     * @param invocation
     *       the invocation.
     * @param language
     *       the language.
     * @param exclusive
     *       check if it is exclusive test.
     * @param testCaseId
     *        the test case id.
     * @param priorityCorrection
     *       if it is needed to priority.
     * @param cancelPrevious
     *       if it is needed to cancel the previous test.
     * @param roundType
     *       the round type.
     * @throws TesterInvokerException
     */
    private void scheduleTest(int action, int coderId, Location location, int componentId, Invocation invocation, int language, boolean exclusive, int testCaseId, int priorityCorrection, boolean cancelPrevious, int roundType) throws TesterInvokerException {
        HierarchicalIdBuilder idBuilder = obtainIdBuilder(action, coderId, location, componentId);
        if (testCaseId > 0) {
            idBuilder.add(TYPE_TEST, testCaseId);
        }
        idBuilder.add(TYPE_EXCLUSIVE, exclusive ? 1 : 0);
        scheduleTest(idBuilder.getId(), action, invocation, language, exclusive, priorityCorrection, cancelPrevious, roundType);
    }
    /**
     * <p>
     * schedule the test.
     * </p>
     * @param id
     *       connection id.
     * @param action
     *       the action.
     * @param invocation
     *       the invocation.
     * @param language
     *       the language.
     * @param exclusive
     *       check if it is exclusive test.
     * @param priorityCorrection
     *       if it is needed to priority.
     * @param cancelPrevious
     *       if it is needed to cancel the previous test.
     * @param roundType
     *       the round type.
     * @return nothing.
     * @throws TesterInvokerException
     *          if any error occur during schedule test.
     */
    private Object scheduleTest(String id, int action, Invocation invocation, int language, boolean exclusive, int priorityCorrection, boolean cancelPrevious, int roundType) throws TesterInvokerException {
        if (cancelPrevious) {
            try {
                getInvoker().getClientNode().cancelPendingRequests(id);
            } catch (Exception e) {
            }
        }
        try {
            InvocationRequirements requirements = buildRequeriments(language, action, roundType);
            InvocationRequest request = new InvocationRequest(id, requirements, invocation);
            request.setPriority(priorityCorrection);
            if (exclusive) {
                request.setRequestAsExclusiveProcessorUsage();
            }
            getInvoker().scheduleInvocation(request);
            return null;
        } catch (Exception e) {
            throw new TesterInvokerException(e.getMessage(), e);
        }
    }


    /**
     * Releases all resources taken by this tester.
     */
    public void releaseTester() {
        FarmFactory.getInstance().releaseInvoker(getTesterName(testerId));
    }

    /**
     * Cancels all tests scheduled by this SRMTesterInvoker
     *
     * @throws TesterInvokerException if an any exception was thrown while trying to cancel pending tests
     */
    public void cancelTests() throws TesterInvokerException {
        try {
            getInvoker().getClientNode().cancelPendingRequests();
        } catch (Exception e) {
            throw new TesterInvokerException(e.getMessage(), e);
        }
    }

    /**
     * Cancels all System tests enqueued for the given submission.
     *
     * @param submission The submission for which system tests should be cancelled
     * @param practiceTests If practice system tests should be cancelled
     */
    public void cancelSystemTestsOnSubmission(int contestId, int coderId, int roundId, int roomId, int componentId, boolean practiceTests) throws TesterInvokerException {
        HierarchicalIdBuilder idBuilder =
            obtainIdBuilder(
                            practiceTests ? ServicesConstants.PRACTICE_TEST_ACTION : ServicesConstants.SYSTEM_TEST_ACTION,
                            contestId,
                            coderId,
                            roundId,
                            roomId,
                            componentId);
        try {
            getInvoker().cancelPendingRequests(idBuilder.getPrefix());
        } catch (Exception e) {
            throw new TesterInvokerException("Could not cancel system test cases: "+e.getMessage(), e);
        }
    }

    /**
     * Removes all enqueued system tests for the given test case, in the given contest and round.
     *
     * @param contestId The id of the contest
     * @param roundId The id of the round
     * @param testCaseId The test case id that should be cancelled
     */
    public void cancelSystemTestsForTestCase(int contestId, int roundId, int testCaseId) throws TesterInvokerException {
        HierarchicalIdBuilder idBuilder = new HierarchicalIdBuilder();
        idBuilder.add(TYPE_ACTION, ServicesConstants.SYSTEM_TEST_ACTION);
        idBuilder.add(TYPE_CONTEST, contestId);
        idBuilder.add(TYPE_ROUND, roundId);
        String testCasePattern = idBuilder.getPrefix()+"%."+TYPE_TEST+testCaseId;

        try {
            getInvoker().cancelPendingRequests(testCasePattern);
        } catch (Exception e) {
            throw new TesterInvokerException("Could not cancel system test cases: "+e.getMessage(), e);
        }

    }

    /**
     * Returns a set of all pending system test case ids for the submission identified by the given args
     *
     * @param contestId The id of the contest
     * @param coderId The id of the coder
     * @param roundId The id of the round
     * @param roomId The id of the room
     * @param componentId The id of the component
     * @param practiceTests If practice system tests should be returned
     *
     * @return An Integer list representing test cases ids
     * @throws TesterInvokerException If the set could not be obtained
     */
    public Set getPendingSystemTestCaseIdsForSubmission(int contestId, int coderId, int roundId, int roomId, int componentId, boolean practiceTests) throws TesterInvokerException {
        HierarchicalIdBuilder idBuilder =
            obtainIdBuilder(
                            practiceTests ? ServicesConstants.PRACTICE_TEST_ACTION : ServicesConstants.SYSTEM_TEST_ACTION,
                            contestId,
                            coderId,
                            roundId,
                            roomId,
                            componentId);
        try {
            List enqueuedRequests = getInvoker().getClientNode().getPendingRequests(idBuilder.getPrefix());
            HashSet ids = new HashSet(enqueuedRequests.size());
            HierarchicalIdDisassembler disassembler = new HierarchicalIdDisassembler();
            for (Iterator it = enqueuedRequests.iterator(); it.hasNext(); ) {
                InvocationRequestRef ref = (InvocationRequestRef) it.next();
                disassembler.parseId(ref.getRequestId());
                ids.add(new Integer(disassembler.getValueAsInt(6)));
            }
            return ids;
        } catch (Exception e) {
            throw new TesterInvokerException("Could not get system test cases Ids: "+e.getMessage(), e);
        }
    }


    public boolean existsPedingSystemTestForRound(int contestId, int roundId) throws TesterInvokerException {
        HierarchicalIdBuilder idBuilder = obtainIdBuilder(ServicesConstants.SYSTEM_TEST_ACTION, contestId, roundId);
        try {
            return getInvoker().getClientNode().countPendingRequests(idBuilder.getPrefix()).intValue() > 0 ;
        } catch (Exception e) {
            throw new TesterInvokerException("Could not get system test cases Ids: "+e.getMessage(), e);
        }

    }
    private HierarchicalIdBuilder obtainIdBuilder(int actionId, int coderId, Location location, int componentId) {
        return obtainIdBuilder(actionId, location.getContestID(), coderId, location.getRoundID(), location.getRoomID(), componentId);
    }

    private HierarchicalIdBuilder obtainIdBuilder(int actionId, int contestId, int coderId, int roundId, int roomId, int componentId) {
        HierarchicalIdBuilder idBuilder = obtainIdBuilder(actionId, contestId, roundId);
        idBuilder.add(TYPE_COMPONENT, componentId);
        idBuilder.add(TYPE_ROOM, roomId);
        idBuilder.add(TYPE_CODER, coderId);
        return idBuilder;
    }

    private HierarchicalIdBuilder obtainIdBuilder(int actionId, int contestId, int roundId) {
        HierarchicalIdBuilder idBuilder = new HierarchicalIdBuilder();
        idBuilder.add(TYPE_ACTION, actionId);
        idBuilder.add(TYPE_CONTEST, contestId);
        idBuilder.add(TYPE_ROUND, roundId);
        return idBuilder;
    }
    /**
     * <p>
     * build the requirement invocation.
     * </p>
     * @param languageId
     *         the language id.
     * @param testAction
     *         the test action id.
     * @param roundType
     *         the round type.
     * @return the requirement invocation.
     */
    private InvocationRequirements buildRequeriments(int languageId, int testAction, int roundType) {
        TesterRequerimentsBuilder builder = new TesterRequerimentsBuilder();
        builder.languageId(languageId);
        builder.testerActionId(testAction);
        builder.roundType(roundType);
        InvocationRequirements requirements = builder.buildRequeriments();
        return requirements;
    }

    private FarmInvoker getInvoker() throws NotAllowedToRegisterException, FarmException {
        return FarmFactoryProvider.getConfiguredFarmFactory().getInvoker(testerId);
    }

    private static String getTesterName(String id) {
        return TESTER_ID_PREFIX+"-"+id;
    }

    protected void finalize() throws Throwable {
        try {
            releaseTester();
        } catch (Throwable e) {
        }
    }

    /**
     * SRMTesterInvoker results handler.<p>
     *
     * Implementators of this interface are responsible for handling responses of test request.<p>
     *
     * NOTE: Results for the same submission might arrive more than one time.
     */
    public static interface SRMTesterHandler {

        /**
         * This method is called every time a result for a UserTest is ready.
         *
         * @param userTest the user test attributes containing results
         */
        public void reportUserTestResult(UserTestAttributes userTest);

        /**
         * This method is called every time a result for a Challenge test is ready.
         *
         * @param chal the challenge test attributes containing results
         */
        public void reportChallengeTestResult(ChallengeAttributes chal);

        /**
         * This method is called every time a result for a System test is ready.
         *
         * @param attr the system test attributes containing results
         */
        public void reportSystemTestResult(SystemTestAttributes attr);
    }
}
