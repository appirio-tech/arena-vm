package com.topcoder.server.farm;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.topcoder.arena.code.CodeProcessingResultHandler;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.services.ControllerServices;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.farm.shared.invocation.ExceptionData;
import com.topcoder.farm.shared.invocation.InvocationResult;

public abstract class BaseCodeProcessingResultHandler implements CodeProcessingResultHandler, ApplicationContextAware {
	private static final Logger logger = Logger.getLogger(BaseCodeProcessingResultHandler.class);
	
	private ControllerServices controller;
	
	protected BaseCodeProcessingResultHandler() {
		
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		controller = applicationContext.getBean(ControllerServices.class);
	}
	
	@Override
	public void handleResult(CodeProcessingResult result) {
		InvocationResult ir = new InvocationResult();
		if (result.getErrorMessage() != null && !result.getErrorMessage().isEmpty()) {
			ir.setExceptionThrown(true);
			ExceptionData ed = new ExceptionData();
			ed.setExceptionString(result.getErrorMessage());
			ed.setExceptionStackTrace(result.getErrorDetails());
			ir.setExceptionData(ed);
		}
		ir.setReturnValue(result.getResultData());

		InvocationResponse response = new InvocationResponse();
		// assuming request tag is the old invocation id
		response.setRequestId(result.getMetadata().getRequestTag());
		response.setResult(ir);
		response.setAttachment(result.getMetadata().getProcessorData());

		try {
			handleResponse(response);
		} catch (Exception e) {
			logger.error("Unable to handle invocation response: " + e.getMessage(), e);
		}

		try {
			if (result.getMetadata().getRequestTag() != null && !result.getMetadata().getRequestTag().isEmpty()
					&& !result.getMetadata().getRequestTag().startsWith("R")) {
				controller.bulkDeleteInvocations(Collections.singleton(Long.parseLong(result.getMetadata()
						.getRequestTag())));
			}
		} catch (Exception e) {
			logger.error("Unable to delete invocation:" + e.getMessage(), e);
		}
	}
	
	protected abstract void handleResponse(InvocationResponse response);
	
}
