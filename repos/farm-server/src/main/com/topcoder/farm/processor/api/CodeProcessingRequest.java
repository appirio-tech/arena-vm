package com.topcoder.farm.processor.api;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.ActionType;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.LanguageType;
import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.AppType;

/**
 * A request to the processor to perform an action on code.
 * 
 * @author james
 * 
 */
public class CodeProcessingRequest {
	private Object requestData;
	private CodeProcessingRequestMetadata metadata;

	public CodeProcessingRequest() {

	}

	public CodeProcessingRequest(Object requestData, String requestTag, AppType requestorType, ActionType actionType,
			LanguageType languageType, String processorName, String resultHandlerName, Integer roundId,
			Object invocationData) {
		this.requestData = requestData;
		metadata = new CodeProcessingRequestMetadata(requestorType, actionType, languageType, requestTag, roundId);
		metadata.setRequestId(UUID.randomUUID().toString());
		metadata.setProcessorName(processorName);
		metadata.setResultHandlerName(resultHandlerName);
		metadata.setProcessorData(invocationData);
	}

	/**
	 * The associated metadata for this request
	 */
	public CodeProcessingRequestMetadata getMetadata() {
		return metadata;
	}

	/**
	 * The associated metadata for this request
	 */
	public void setMetadata(CodeProcessingRequestMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * A object that encapsulates the details of the request that is passed to
	 * the processor
	 */
	@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
	public Object getRequestData() {
		return requestData;
	}

	/**
	 * A object that encapsulates the details of the request that is passed to
	 * the processor
	 */
	public void setRequestData(Object requestData) {
		this.requestData = requestData;
	}

}
