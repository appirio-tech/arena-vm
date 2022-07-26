package com.topcoder.farm.processor;

import java.io.File;

import org.springframework.stereotype.Component;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.common.SystemTestAttributes;
import com.topcoder.services.tester.type.system.SystemTest;

/**
 * Processor for running match system tests.
 * 
 * @author james
 */
@Component("matchSystemTester")
public class MatchSystemTester implements CodeProcessor {

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {
		
		SystemTestAttributes attr = (SystemTestAttributes) request.getRequestData();
		
		SystemTestAttributes testResult = new SystemTest(workFolder).process(attr);
		
		CodeProcessingResult result = new CodeProcessingResult(request.getMetadata(), testResult);
		
		return result;
	}

}
