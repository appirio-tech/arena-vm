/*
 * Copyright (C) 2007-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.farm.tester.mpsqas;
import com.topcoder.farm.client.invoker.AsyncInvocationResponse;
import com.topcoder.farm.client.invoker.FarmException;
import com.topcoder.farm.client.invoker.FarmFactory;
import com.topcoder.farm.client.invoker.FarmInvoker;
import com.topcoder.farm.client.invoker.InvokerConfiguration;
import com.topcoder.farm.client.util.FarmFactoryProvider;
import com.topcoder.farm.client.util.HierarchicalIdBuilder;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.farm.shared.processorproperties.RequerimentsBuilder;
import com.topcoder.server.farm.longtester.LongTestRequerimentsBuilder;
import com.topcoder.server.farm.tester.TesterInvokerException;
import com.topcoder.server.farm.tester.TesterRequerimentsBuilder;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.ProblemConstants;


/**
 * This class is resposible for testing MPSQAS non long solutions.<p>
 *
 * Farm interaction and processor selection is resolved by this class.<p>
 *
 * Farm Information: This class registers into the farm as client MPSQASTest-ID
 * where the ID is given by the creator of this class.
 *
 *
 * <p>
 * Changes in 1.0 (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Updated {@link #scheduleTest(String, int, Invocation, int, boolean, int)}  method.</li>
 * <li>Updated {@link #buildRequeriments(int languageId, int testAction, int roundType)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in 1.1 (Fix Tester Choosing Issue for Testing Writer Solution v1.0):
 * <ol>
 * <li>Updated {@link #mpsqasTest(MPSQASFiles files)} to add component type in schedule test</li>
 * <li>Updated {@link #scheduleTest(String, int, Invocation, int, boolean,int, int)}</li>
 * <li>Updated {@link #buildRequeriments(int languageId, int testAction, int roundType, int componentType)}</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn, TCSASSEMBLER
 * @version 1.1
 */
public class MPSQASTesterInvoker {
    /*
     * Test request ids:
     *
     * MPSQAS test id
     * RequestId = C{contestId}.R{roundId}.P{problemId}.U{userId}..
     */
    private static final char TYPE_ACTION = 'A';
    private static final char TYPE_SOLUTION = 'S';
    private static final char TYPE_ID = 'I';

    /**
     * Prefix value used by this tester
     */
    private static final String TESTER_ID_PREFIX = "MPSQASTest";

    /**
     * Time to wait for test result
     */
    private static final long MPSQAS_TEST_TIMEOUT = 30000;

    /**
     * The tester instance id. This id is used as suffix for the farm
     * client identifier.
     */
    private String testerId;
    private long id = System.currentTimeMillis();

    protected MPSQASTesterInvoker(String testerId) {
        this.testerId = testerId;
    }

    /**
     * Creates a new MPSQASTesterInvoker and configures it.<p>
     *
     * NOTE: It is not allowed to create multiple instances of a MPSQASTesterInvoker using the same
     * tester id suffix. Doing this will produce unpredictable results. It is client user responsability
     * to ensure that only one instance for ths given Id is created.
     *
     * @param id The id suffix to use for farm identification
     *
     * @return The new MPSQASTesterInvoker instance.
     */
    public static MPSQASTesterInvoker create(String id) {
        String testerName = getTesterName(id);
        FarmFactory factory = FarmFactoryProvider.getConfiguredFarmFactory();

        if (!factory.isConfiguredInvoker(testerName)) {
            InvokerConfiguration configuration = new InvokerConfiguration();
            configuration.setCancelOnRegistration(false);
            configuration.setDeliverOnRegistration(true);
            factory.configureInvoker(testerName, configuration);
        }

        return new MPSQASTesterInvoker(testerName);
    }

    /**
     * <p>
     * to do the mpqas test.
     * </p>
     * @param files
     *       the mpsqas submitted file.
     * @return the mpsqas file with result.
     * @throws TesterInvokerException
     *          if no process can be scheduled or other exception throws.
     */
    public MPSQASFiles mpsqasTest(MPSQASFiles files) throws TesterInvokerException {
        HierarchicalIdBuilder idBuilder = new HierarchicalIdBuilder();
        idBuilder.add(TYPE_ACTION, ServicesConstants.MPSQAS_TEST_ACTION);
        idBuilder.add(TYPE_SOLUTION, files.getSolutionId());

        return (MPSQASFiles) scheduleTest(idBuilder.buildId(TYPE_ID, nextId()), ServicesConstants.MPSQAS_TEST_ACTION,
            new MPSQASTestInvocation(files), files.getLanguage(), false, files.getRoundType(),
            files.getComponentType());
    }

    private synchronized long nextId() {
        return id++;
    }

    /**
     * <p>
     * schedule the mpsqas test.
     * </p>
     * @param id
     *       the test id.
     * @param action
     *       the test action.
     * @param invocation
     *       the mpsqas test invocation.
     * @param language
     *       the test language.
     * @param exclusive
     *       if this is exclusive test.
     * @param roundType
     *       the round type of problem.
     * @param componentType
     *       the component type of problem 1=Main Individual Problem, 2=Main Long Problem.
     * @return schedule test result.
     * @throws TesterInvokerException
     *         if any error occur during schedule test.
     */
    private Object scheduleTest(String id, int action, Invocation invocation, int language, boolean exclusive,
        int roundType, int componentType) throws TesterInvokerException {
        try {
            InvocationRequirements requirements = buildRequeriments(language, action, roundType, componentType);
            InvocationRequest request = new InvocationRequest(id, requirements, invocation);

            if (exclusive) {
                request.setRequestAsExclusiveProcessorUsage();
            }

            AsyncInvocationResponse response = getInvoker().scheduleInvocationSync(request);
            InvocationResponse invRespose = response.get(MPSQAS_TEST_TIMEOUT);
            InvocationResult result = invRespose.getResult();

            if (result.isExceptionThrown()) {
                throw new TesterInvokerException("Exception while processing your tests:\n"
                    + result.getExceptionData());
            }

            return result.getReturnValue();
        } catch (TesterInvokerException e) {
            throw e;
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
     * Cancels all tests scheduled by this MPSQASTesterInvoker
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
     * <p>
     * build the mpsqas test requirement.
     * </p>
     * @param languageId
     *         the language id.
     * @param testAction
     *         the test action.
     * @param roundType
     *         the round type.
     * @param componentType
     *       the component type of problem 1=Main Individual Problem, 2=Main Long Problem.
     * @return the build requirement.
     */
    private InvocationRequirements buildRequeriments(int languageId, int testAction, int roundType, int componentType) {    	
        RequerimentsBuilder builder;       
        if(componentType == ProblemConstants.MAIN_COMPONENT) {
            TesterRequerimentsBuilder srmBuilder = new TesterRequerimentsBuilder();
            srmBuilder.languageId(languageId);
            srmBuilder.testerActionId(testAction);
            srmBuilder.roundType(roundType);
            builder = srmBuilder;
        } else {
            LongTestRequerimentsBuilder longBuilder = new LongTestRequerimentsBuilder();
            longBuilder.roundType(roundType);
            longBuilder.languageId(languageId);
            builder = longBuilder;
        }
        InvocationRequirements requirements = builder.buildRequeriments();
        return requirements;
    }

    private FarmInvoker getInvoker() throws NotAllowedToRegisterException, FarmException {
        return FarmFactoryProvider.getConfiguredFarmFactory().getInvoker(testerId);
    }

    private static String getTesterName(String id) {
        return TESTER_ID_PREFIX + "-" + id;
    }

    protected void finalize() throws Throwable {
        try {
            releaseTester();
        } catch (Throwable e) {
        }
    }
}
