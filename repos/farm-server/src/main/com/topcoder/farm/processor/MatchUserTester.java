package com.topcoder.farm.processor;

import java.io.File;

import org.springframework.stereotype.Component;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.common.UserTestAttributes;
import com.topcoder.services.tester.type.user.UserTest;

/**
 * Processor for running a user's test on their code
 * 
 * @author james
 */
@Component("matchUserTester")
public class MatchUserTester implements CodeProcessor {

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {
		
		UserTestAttributes attr = (UserTestAttributes) request.getRequestData();
		
		UserTestAttributes testResult = new UserTest(workFolder).process(attr);
		
		CodeProcessingResult result = new CodeProcessingResult(request.getMetadata(), testResult);
		
		return result;
	}

}
