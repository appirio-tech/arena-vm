/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.farm.processor;

import java.io.File;

import org.springframework.stereotype.Component;

import com.topcoder.arena.exception.ArenaRuntimeException;
import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.tester.type.mpsqas.CPPMPSQASTest;
import com.topcoder.services.tester.type.mpsqas.DotNetMPSQASTest;
import com.topcoder.services.tester.type.mpsqas.JAVAMPSQASTest;
import com.topcoder.services.tester.type.mpsqas.PythonMPSQASTest;

/**
 * Processor for testing admin tool solutions.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Updated {@link #processRequest(CodeProcessingRequest, File, File)} method to support Python3.</li>
 * </ol>
 * </p>
 *
 * @author james, liuliquan
 * @version 1.1
 */
@Component("mpsqasTester")
public class MpsqasTester implements CodeProcessor {

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {
		
		MPSQASFiles files = (MPSQASFiles) request.getRequestData();
		
		try {
            switch (files.getLanguage()) {
            case ContestConstants.JAVA:
                JAVAMPSQASTest.processJavaMPSQASTest(files);
                break;

            case ContestConstants.CPP:
                CPPMPSQASTest.processCPPMPSQASTest(files);
                break;

            case ContestConstants.CSHARP:
            case ContestConstants.VB:
                DotNetMPSQASTest.processDotNetMPSQASTest(files);
                break;

            case ContestConstants.PYTHON:
                PythonMPSQASTest.processPythonMPSQASTest(files, false);
                break;

            case ContestConstants.PYTHON3:
                PythonMPSQASTest.processPythonMPSQASTest(files, true);
                break;
            default:
                throw new IllegalStateException("Invalid language.");
            }
            return new CodeProcessingResult(request.getMetadata(), files);
        } catch (Exception e) {
        	throw new ArenaRuntimeException(e);
        }
	}

}
