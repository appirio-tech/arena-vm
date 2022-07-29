package com.topcoder.server.farm.tester.srm;

import org.springframework.stereotype.Component;

import com.topcoder.arena.code.CodeProcessingResultHandler;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.server.farm.BaseCodeProcessingResultHandler;
import com.topcoder.server.services.SRMTestHandler;

@Component
public class MatchTestResultHandler extends BaseCodeProcessingResultHandler implements CodeProcessingResultHandler {

	@Override
	protected void handleResponse(InvocationResponse response) {
		new SRMTesterFarmHandler(new SRMTestHandler()).handleResult(response);

	}

}
