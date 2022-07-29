package com.topcoder.server.farm.compiler.srm;

import org.springframework.stereotype.Component;

import com.topcoder.arena.code.CodeProcessingResultHandler;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.server.common.Submission;
import com.topcoder.server.farm.BaseCodeProcessingResultHandler;

@Component("srmCompileResultHandler")
public class SRMCompileResultHandler extends BaseCodeProcessingResultHandler implements CodeProcessingResultHandler {

	@Override
	protected void handleResponse(InvocationResponse response) {
		new SRMCompilationCurrentHandler().reportSubmissionCompilationResult(
				(SRMCompilationId) response.getAttachment(), (Submission) response.getResult().getReturnValue());

	}

}
