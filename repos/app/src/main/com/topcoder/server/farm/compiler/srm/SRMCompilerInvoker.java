/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.farm.compiler.srm;

import com.topcoder.client.spectatorApp.scoreboard.model.Contest;
import com.topcoder.farm.client.invoker.FarmException;
import com.topcoder.farm.client.invoker.FarmFactory;
import com.topcoder.farm.client.invoker.FarmInvoker;
import com.topcoder.farm.client.invoker.InvokerConfiguration;
import com.topcoder.farm.client.util.FarmFactoryProvider;
import com.topcoder.farm.client.util.HierarchicalIdBuilder;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.server.common.Submission;
import com.topcoder.server.farm.compiler.CompilerInvokerException;
import com.topcoder.server.farm.compiler.CompilerRequerimentsBuilder;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.SimpleComponent;

/**
 * This class is resposible for compiling SRM submissions.<p>
 *
 * Farm interaction and processor selection is resolved by this class.<p>
 *
 * Farm Information: This class registers into the farm as client SRMComp-ID
 * where the ID is given by the creator of this class.
 *
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Update {@link #compileSubmission(Submission sub)}  method.</li>
 * <li>Update {@link #buildRequeriments(int languageId,int roundType)}  method.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), savon_cn
 * @version 1.0
 */
public class SRMCompilerInvoker {
    /*
     * Compilation request ids:
     *
     * SRM compilation id
     * RequestId = C{contestId}.R{roundId}.P{problemId}.U{userId}.. 
     */
    private static final char TYPE_ROUND = 'R';
    private static final char TYPE_CODER = 'U';
    private static final char TYPE_CONTEST = 'C';
    private static final char TYPE_PROBLEM = 'P';

    /**
     * Prefix value used by this compiler
     */
    private static final String COMPILER_ID_PREFIX = "SRMComp";

    /**
     * The compiler instance id. This id is used as suffix for the farm
     * client identifier.
     */
    private String compilerId;

    protected SRMCompilerInvoker(String compilerId) {
        this.compilerId = compilerId;
    }

    /**
     * Creates a new SRMCompilerInvoker and configures it.<p>
     *
     * NOTE: It is not allowed to create multiple instances of a SRMCompilerInvoker using the same
     * compiler id suffix. Doing this will produce unpredictable results. It is client user responsability
     * to ensure that only one instance for ths given Id is created.
     *
     * @param id The id suffix to use for farm identification
     *
     * @return The new SRMCompilerInvoker instance.
     */
    public static SRMCompilerInvoker create(String id, SRMCompilerHandler handler) {
        String compilerName = getCompilerName(id);
        FarmFactory factory = FarmFactoryProvider.getConfiguredFarmFactory();
        if (!factory.isConfiguredInvoker(compilerName)) {
            factory.configureHandler(compilerName, new SRMCompilerFarmHandler(handler));
            InvokerConfiguration configuration = new InvokerConfiguration();
            configuration.setCancelOnRegistration(true);
            configuration.setDeliverOnRegistration(false);
            factory.configureInvoker(compilerName, configuration);
        }
        return new SRMCompilerInvoker(compilerName);
    }

    /**
     * <p>
     * Compiles the given Submission.
     *
     * The submission is scheduled for compilation, the compilation result will be handled
     * by the SRMCompilerHandler provided during creation.
     * </p>
     *
     * @param sub The submission to compile
     * 
     * @throws CompilerInvokerException if an any exception was thrown during the compilation process.
     */
    public void compileSubmission(Submission sub) throws  CompilerInvokerException {

        HierarchicalIdBuilder idBuilder = new HierarchicalIdBuilder();
        
        idBuilder.add(TYPE_CODER, sub.getCoderID());
        idBuilder.add(TYPE_CONTEST, sub.getLocation().getContestID());
        idBuilder.add(TYPE_ROUND, sub.getLocation().getRoundID());
        String id = idBuilder.buildId(TYPE_PROBLEM, sub.getComponent().getProblemID());

        Invocation invocation = new SRMCompilationInvocation(sub);

        try {
            getInvoker().cancelPendingRequests(id);
        } catch (Exception e) {
        }
        try {
            SimpleComponent simpleComponent = CoreServices.getSimpleComponent(sub.getComponent().getComponentID());
            InvocationRequirements requirements = buildRequeriments(sub.getLanguage(),simpleComponent.getRoundType());
            SRMCompilationId compilationId = buildSRMCompilationId(sub);
            InvocationRequest request = new InvocationRequest(id, compilationId, requirements, invocation);
            getInvoker().scheduleInvocation(request);
        } catch (Exception e) {
            throw new CompilerInvokerException(e.getMessage(), e);
        }
    }

    /**
     * Builds an SRMCompilation Id for the given submission
     * 
     * @param sub The submission
     * @return The id
     */
    public SRMCompilationId buildSRMCompilationId(Submission sub) {
        return new SRMCompilationId(sub.getLocation().getContestID(), sub.getRoundID(), sub.getComponent().getProblemID(), sub.getCoderID());
    }


    /**
     * Releases all resources taken by this compiler.
     */
    public void releaseCompiler() {
        FarmFactory.getInstance().releaseInvoker(getCompilerName(compilerId));
    }
    
    /**
     * Cancels all compilations scheduled by this SRMCompilerInvoker 
     * 
     * @throws CompilerInvokerException if an any exception was thrown while trying to cancel pending compilations
     */
    public void cancelCompilations() throws CompilerInvokerException {
        try {
            getInvoker().getClientNode().cancelPendingRequests();
        } catch (Exception e) {
            throw new CompilerInvokerException(e.getMessage(), e);
        }
    }
    /**
     * <p>
     * build the compilation requirement.
     * </p>
     * @param languageId
     *          the language id.
     * @param roundType
     *          the round type.
     * @return the invocation requirement.
     */
    private InvocationRequirements buildRequeriments(int languageId,int roundType) {
        CompilerRequerimentsBuilder builder = new CompilerRequerimentsBuilder();
        builder.languageId(languageId);
        builder.threadingAllowed(false);
        builder.compilerActionId(ServicesConstants.CONTEST_COMPILE_ACTION);
        builder.roundType(roundType);
        InvocationRequirements requirements = builder.buildRequeriments();
        return requirements;
    }

    private FarmInvoker getInvoker() throws NotAllowedToRegisterException, FarmException {
        return FarmFactoryProvider.getConfiguredFarmFactory().getInvoker(compilerId);
    }

    private static String getCompilerName(String id) {
        return COMPILER_ID_PREFIX+"-"+id;
    }

    protected void finalize() throws Throwable {
        try {
            releaseCompiler();
        } catch (Throwable e) {
        }
    }
    
    /**
     * SRMCompilerInvoker results handler.<p>
     * 
     * Implementators of this interface are responsible for handling responses of compilation request.<p>
     * 
     * NOTE: Results for the same submission might arrive more than one time. 
     */
    public static interface SRMCompilerHandler {
        
        /**
         * This method is called every time a result for a Submission compilation is ready.
         * 
         * @param id The id of the compilation request 
         * @param submission The submission as returned by the compiler
         * @return true if the result was handled successfully
         */
        boolean reportSubmissionCompilationResult(SRMCompilationId id, Submission sub);
    }
}
