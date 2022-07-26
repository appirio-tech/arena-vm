package com.topcoder.farm.processor;

import java.io.File;

import org.springframework.stereotype.Component;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.farm.longtester.MarathonCodeScoringRequest;
import com.topcoder.services.tester.type.longtest.FarmLongTester;
import com.topcoder.shared.common.LongRoundScores;

/**
 * Processor for calculating marathon match scores.
 * 
 * @author james
 */
@Component("marathonCodeScorer")
public class MarathonCodeScorer implements CodeProcessor {

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {

		MarathonCodeScoringRequest scoringRequest = (MarathonCodeScoringRequest) request.getRequestData();

		LongRoundScores scores = new FarmLongTester(rootFolder, workFolder).recalculateFinalScores(
				scoringRequest.getSolution(), scoringRequest.getScores());
		
		CodeProcessingResult result = new CodeProcessingResult(request.getMetadata(), scores);
		
		return result;
	}

}
