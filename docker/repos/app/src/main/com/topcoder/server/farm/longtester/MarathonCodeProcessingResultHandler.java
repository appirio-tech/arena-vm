package com.topcoder.server.farm.longtester;

import org.springframework.stereotype.Component;

import com.topcoder.arena.code.CodeProcessingResultHandler;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.server.farm.BaseCodeProcessingResultHandler;

@Component("marathonCodeResultHandler")
public class MarathonCodeProcessingResultHandler extends BaseCodeProcessingResultHandler implements
		CodeProcessingResultHandler {

	@Override
	protected void handleResponse(InvocationResponse response) {
		if (response != null) {
			new LongTesterFarmHandler().handleResult(response);
		}
	}

}
