package com.topcoder.farm.processor;

import java.io.File;

import org.springframework.stereotype.Component;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.common.Submission;
import com.topcoder.services.compiler.invoke.FarmCompiler;

/**
 * Processor for compiling match code.
 * 
 * @author james
 */
@Component("matchCompiler")
public class MatchCompiler implements CodeProcessor {

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {
		
		Submission sub = (Submission) request.getRequestData();
		FarmCompiler fc = new FarmCompiler();
		Submission resultSub = fc.compile(sub);
		
		CodeProcessingResult result = new CodeProcessingResult(request.getMetadata(), resultSub);
		
		return result;
	}

}
