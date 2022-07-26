/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.farm.compiler;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.topcoder.arena.code.CodeService;
import com.topcoder.farm.client.util.HierarchicalIdBuilder;
import com.topcoder.farm.controller.configuration.ApplicationContextProvider;
import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.ActionType;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.AppType;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.common.CodeUtil;
import com.topcoder.server.farm.longtester.MarathonCodeCompileRequest;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class is resposible for compiling long submission and solutions.<p>
 *
 * Farm interaction and processor selection is resolved by this class.<p>
 *
 * Farm Information: This class registers into the farm as client CompilerInvoker-ID
 * where the ID is given by the creator of this class.
 *
 * <p>
 *  Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added {@link #LOGGER} field.</li>
 *      <li>Added {@link #compileLongSubmission(LongSubmission, ProblemComponent)} and {@link #compileMPSQAS(MPSQASFiles)}
 *      to use the configurable compile time limit when compiling.</li> 
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in (Round Type Option Support For SRM Problem 1.0):
 * <ol>
 * <li>Update {@link #buildRequeriments(int, boolean, int, int)}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #compileMPSQAS(MPSQASFiles mpsqasFiles)} method.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), savon_cn
 * @version 1.2
 */
@Deprecated
public class CompilerInvoker {
    /**
     * Represents the logger.
     * @since 1.1
     */
    private static final Logger logger = Logger.getLogger(CompilerInvoker.class);
    
    /*
     * Compilation request ids:
     *
     * Long submission id
     * RequestId = R{roundId}.U{coderId}.C{componentId}..
     *
     * MPSQAS compilation id
     * RequestId = O{solutionId}.Q{sequentialId}..
     */
    private static final char TYPE_ROUND = 'R';
//    private static final char TYPE_SOLUTION = 'O';
    private static final char TYPE_CODER = 'U';
    private static final char TYPE_COMPONENT = 'C';
//    private static final char TYPE_SEQUENTIAL = 'C';

    private static final String COMPILER_ID_PREFIX = "Compiler";

    /**
     * The compiler instance id. This id is used as suffix for the farm
     * client identifier.
     */
    private String compilerId;

    /**
     * Sequential number used to generated unique identifiers.
     */
//    private long sequenceId;
    
    private CodeService codeService;

    protected CompilerInvoker(String compilerId) {
        this.compilerId = compilerId;
//        this.sequenceId = System.currentTimeMillis();
        codeService = ApplicationContextProvider.getContext().getBean(CodeService.class);
    }

    /**
     * Creates a new CompilerInvoker and configures it.<p>
     *
     * NOTE: It is not allowed to create multiple instances of a CompilerInvoker using the same
     * compiler id suffix. Doing this will produce unpredictable results. It is client user responsability
     * to ensure that only one instance for ths given Id is created.
     *
     * @param id The id suffix to use for farm identification
     *
     * @return The new CompilerInvoker instance.
     */
    public static CompilerInvoker create(String id) {
        String compilerName = getCompilerName(id);
//        FarmFactory factory = FarmFactoryProvider.getConfiguredFarmFactory();
//        if (!factory.isConfiguredInvoker(compilerName)) {
//            InvokerConfiguration configuration = new InvokerConfiguration();
//            configuration.setCancelOnRegistration(true);
//            configuration.setDeliverOnRegistration(false);
//            factory.configureInvoker(compilerName, configuration);
//        }
        return new CompilerInvoker(compilerName);
    }

    /**
     * Compiles a Long Submission for the given problemComponent
     *
     * @param sub The submission to compile
     * @param problemComponent The problem component which the submission belongs to
     *
     * @return A new LongSubmission object updated with the compilation result.
     *
     * @throws CompilerTimeoutException if the compilation request timeout.
     * @thrown CompilerInvokerException if an any exception was thrown during the compilation process.
     */
	public LongSubmission compileLongSubmission(LongSubmission sub, ProblemComponent problemComponent)
			throws CompilerTimeoutException, CompilerInvokerException {

		HierarchicalIdBuilder idBuilder = new HierarchicalIdBuilder();
		idBuilder.add(TYPE_ROUND, sub.getRoundID());
		idBuilder.add(TYPE_CODER, sub.getCoderID());
		String id = idBuilder.buildId(TYPE_COMPONENT, sub.getComponentID());

		// Invocation invocation = new LongCompilationInvocation(sub,
		// problemComponent);

		try {
			// getInvoker().cancelPendingRequests(id);
			codeService.deletePendingCodeRequests(compilerId, new CodeProcessingRequestMetadata(AppType.MARATHON,
					ActionType.COMPILE, CodeUtil.toLanguageType(sub.getLanguageID()), id, sub.getRoundID()));
		} catch (Exception e) {
			logger.warn("Unable to cancel pending requests: " + e.getMessage(), e);
		}
		int timeout = problemComponent.getProblemCustomSettings().getCompileTimeLimit();
		try {

			MarathonCodeCompileRequest req = new MarathonCodeCompileRequest(problemComponent, sub);

			// InvocationRequirements requirements = buildRequeriments(language,
			// threadingAllowed, action, roundType);
			// InvocationRequest request = new InvocationRequest(id,
			// requirements, invocation);
			// AsyncInvocationResponse response =
			// getInvoker().scheduleInvocationSync(request);

			CodeProcessingRequest request = new CodeProcessingRequest(req, id, AppType.MARATHON,
					ActionType.COMPILE, CodeUtil.toLanguageType(sub.getLanguageID()), "marathonCompiler", null,
					sub.getRoundID(), null);
			request.getMetadata().setSynchronous(true);
			request.getMetadata().setSyncTimeout(timeout);

			Future<CodeProcessingResult> resultPromise = codeService.sendToProcessor(request);
			CodeProcessingResult result = resultPromise.get(timeout, TimeUnit.SECONDS);

			logger.info("Compile Time Limit: " + timeout);
			if (result.getErrorMessage() != null) {
				throw new CompilerInvokerException("Compilation process thrown an exception: "
						+ result.getErrorMessage());
			}
			return (LongSubmission) result.getResultData();
		} catch (CompilerInvokerException e) {
			throw e;
		} catch (Exception e) {
			throw new CompilerInvokerException(e.getMessage(), e);
		}

		// return (LongSubmission) doCompile(
		// id,
		// sub.getLanguage(),
		// invocation,
		// problemComponent.getProblemCustomSettings().getCompileTimeLimit(),
		// RoundUtils.isThreadingAllowed(problemComponent.getRoundType()),
		// ServicesConstants.LONG_COMPILE_ACTION,
		// problemComponent.getRoundType());
	}


    /**
     * Compiles MPSQAS files
     *
     * @param mpsqasFiles MPSQAS files to compiler
     *
     * @return A new MPSQASFiles object updated with the compilation result.
     *
     * @throws CompilerTimeoutException if the compilation request timeout
     * @thrown CompilerInvokerException if an any exception was thrown during the compilation process
     */
//    public MPSQASFiles compileMPSQAS(MPSQASFiles mpsqasFiles) throws CompilerTimeoutException, CompilerInvokerException {
//        HierarchicalIdBuilder idBuilder = new HierarchicalIdBuilder();
//        idBuilder.add(TYPE_SOLUTION, mpsqasFiles.getSolutionId());
//        String id = idBuilder.buildId(TYPE_SEQUENTIAL, nextId());
//        Invocation invocation = new MPSQASCompilationInvocation(mpsqasFiles);
//        return (MPSQASFiles) doCompile(
//                    id, 
//                    mpsqasFiles.getLanguage(), 
//                    invocation, 
//                    mpsqasFiles.getProblemCustomSettings().getCompileTimeLimit(), 
//                    mpsqasFiles.isThreadingAllowed(), 
//                    ServicesConstants.MPSQAS_COMPILE_ACTION,
//                    mpsqasFiles.getRoundType());
//    }


    /**
     * Releases all resources taken by this compiler.
     */
    public void releaseCompiler() {
//        FarmFactory.getInstance().releaseInvoker(getCompilerName(compilerId));
    }

//	private Object doCompile(String id, int language, Invocation invocation, long timeout, boolean threadingAllowed,
//			int action, int roundType, int roundId) throws CompilerInvokerException, CompilerTimeoutException {
//        
//    }
    /**
     * <p>
     * build the invocation requirement.
     * </p>
     * @param languageId
     *         the language id.
     * @param threadingAllowed
     *         if it is allowed the thread.
     * @param action
     *         the action.
     * @param roundType
     *         the round type.
     * @return the invocation requirement.
     */
//    private InvocationRequirements buildRequeriments(int languageId, boolean threadingAllowed, int action, int roundType) {
//        CompilerRequerimentsBuilder builder = new CompilerRequerimentsBuilder();
//        builder.languageId(languageId);
//        builder.threadingAllowed(threadingAllowed);
//        builder.compilerActionId(action);
//        builder.roundType(roundType);
//        InvocationRequirements requirements = builder.buildRequeriments();
//        return requirements;
//    }
//
//    private FarmInvoker getInvoker() throws NotAllowedToRegisterException, FarmException {
//        return FarmFactoryProvider.getConfiguredFarmFactory().getInvoker(compilerId);
//    }

    private static String getCompilerName(String id) {
        return COMPILER_ID_PREFIX+"-"+id;
    }

//    private synchronized long nextId() {
//        return sequenceId++;
//    }
//
//    protected void finalize() throws Throwable {
//        try {
//            releaseCompiler();
//        } catch (Throwable e) {
//        }
//    }
}
