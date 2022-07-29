package com.topcoder.farm.processor;

import java.io.File;

import org.springframework.stereotype.Component;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.invoke.FarmCompiler;

/**
 * Processor for compiling the admin tool solutions.
 * 
 * @author james
 */
@Component("mpsqasCompiler")
public class MpsqasCompiler implements CodeProcessor {

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {
		
		MPSQASFiles files = (MPSQASFiles) request.getRequestData();
		FarmCompiler fc = new FarmCompiler();
		MPSQASFiles resultFiles = fc.compileMPSQAS(files);
		
		CodeProcessingResult result = new CodeProcessingResult(request.getMetadata(), resultFiles);
		
		return result;
	}

}
