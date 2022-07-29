package com.topcoder.farm.processor;

import java.io.File;

import org.springframework.stereotype.Component;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.farm.longtester.MarathonCodeCompileRequest;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.services.compiler.invoke.FarmCompiler;

/**
 * Processor for compiling marathon match code.
 * 
 * @author james
 */
@Component("marathonCompiler")
public class MarathonCompiler implements CodeProcessor {

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {
		MarathonCodeCompileRequest compileRequest = (MarathonCodeCompileRequest) request.getRequestData();
		FarmCompiler fc = new FarmCompiler();
		LongSubmission sub = fc.compileLong(compileRequest.getSubmission(), compileRequest.getProblemComponent());
		
		CodeProcessingResult result = new CodeProcessingResult(request.getMetadata(), sub);
		
		return result;
	}

}
