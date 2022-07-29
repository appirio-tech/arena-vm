package com.topcoder.farm.processor.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Contains result information about a code processing task.
 * 
 * @author james
 */
public class CodeProcessingResult {
	private Object resultData;
	private String errorMessage;
	private String errorDetails;
	private CodeProcessingRequestMetadata metadata;

	public CodeProcessingResult() {

	}

	public CodeProcessingResult(CodeProcessingRequestMetadata metadata) {
		this.metadata = metadata;
	}
	
	public CodeProcessingResult(CodeProcessingRequestMetadata metadata, Object resultData) {
		this.metadata = metadata;
		this.resultData = resultData;
	}

	public CodeProcessingResult(CodeProcessingRequestMetadata metadata, String errorMessage, String errorDetails) {
		this.metadata = metadata;
		this.errorMessage = errorMessage;
		this.errorDetails = errorDetails;
	}

	/**
	 * A result object that contains specific data about the result.
	 */
	@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
	public Object getResultData() {
		return resultData;
	}

	/**
	 * A result object that contains specific data about the result.
	 */
	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}

	/**
	 * An error message if an error occurred; otherwise, null
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * An error message if an error occurred; otherwise, null
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * If an error occurred, may optionally contain additional detail (typically a stack trace)
	 */
	public String getErrorDetails() {
		return errorDetails;
	}

	/**
	 * If an error occurred, may optionally contain additional detail (typically a stack trace)
	 */
	public void setErrorDetails(String errorDetails) {
		this.errorDetails = errorDetails;
	}

	/**
	 * Metadata from the original request
	 */
	public CodeProcessingRequestMetadata getMetadata() {
		return metadata;
	}

	/**
	 * Metadata from the original request
	 */
	public void setMetadata(CodeProcessingRequestMetadata metadata) {
		this.metadata = metadata;
	}
}
