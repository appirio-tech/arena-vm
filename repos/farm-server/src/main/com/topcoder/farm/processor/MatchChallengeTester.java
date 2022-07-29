package com.topcoder.farm.processor;

import java.io.File;

import org.springframework.stereotype.Component;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.services.tester.type.challenge.ChallengeTest;

/**
 * Processor for testing a match challenge.
 * 
 * @author james
 */
@Component("matchChallengeTester")
public class MatchChallengeTester implements CodeProcessor {

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {
		
		ChallengeAttributes attr = (ChallengeAttributes) request.getRequestData();
		
		ChallengeAttributes testResult = new ChallengeTest(workFolder).process(attr);
		
		CodeProcessingResult result = new CodeProcessingResult(request.getMetadata(), testResult);
		
		return result;
	}

}
