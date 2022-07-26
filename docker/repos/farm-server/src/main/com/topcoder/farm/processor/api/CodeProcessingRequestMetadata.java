package com.topcoder.farm.processor.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Contains the metadata related to a code processing request.
 * 
 * @author james
 * 
 */
public class CodeProcessingRequestMetadata {
	private String resultsQueueName;
	private String requestId;
	private ActionType action;
	private String processorName;
	private String requestTag;
	private boolean synchronous;
	private String resultHandlerName;
	private Object processorData;
	private boolean practice = false;
	private Integer roundId;
	private AppType app;
	private LanguageType language;
	private Integer syncTimeout;
	private Integer processorTimeout;

	public static enum ActionType {
		COMPILE, TEST, SCORE
	}

	public static enum AppType {
		MATCH, MARATHON, ADMIN
	}

	public static enum LanguageType {
		JAVA, CPP, DOTNET, PYTHON, R
	}

	public CodeProcessingRequestMetadata() {

	}

	public CodeProcessingRequestMetadata(AppType requestorType, ActionType actionType,
			LanguageType languageType, String requestTag, Integer roundId) {
		this.action = actionType;
		this.roundId = roundId;
		this.app = requestorType;
		this.language = languageType;
		this.requestTag = requestTag;
	}

	/**
	 * The name of the queue where processing results should be sent.
	 */
	public String getResultsQueueName() {
		return resultsQueueName;
	}

	/**
	 * The name of the queue where processing results should be sent.
	 */
	public void setResultsQueueName(String resultsQueueName) {
		this.resultsQueueName = resultsQueueName;
	}

	/**
	 * A unique id for this request. This is generally system generated.
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * A unique id for this request. This is generally system generated.
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * The type of processing action to perform
	 */
	public ActionType getAction() {
		return action;
	}

	/**
	 * The type of processing action to perform
	 */
	public void setAction(ActionType actionType) {
		this.action = actionType;
	}

	/**
	 * The logical name of the processor to use for this request.
	 */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * The logical name of the processor to use for this request.
	 */
	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	/**
	 * A tag used by the caller to identify the request. 
	 */
	public String getRequestTag() {
		return requestTag;
	}

	/**
	 * A tag used by the caller to identify the request. 
	 */
	public void setRequestTag(String requestTag) {
		this.requestTag = requestTag;
	}

	/**
	 * Whether the client is waiting for a response synchronously.
	 */
	public boolean isSynchronous() {
		return synchronous;
	}

	/**
	 * Whether the client is waiting for a response synchronously.
	 */
	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}

	/**
	 * The name of the handler class that handles the CodeProcessingResult.
	 */
	public String getResultHandlerName() {
		return resultHandlerName;
	}

	/**
	 * The name of the handler class that handles the CodeProcessingResult.
	 */
	public void setResultHandlerName(String resultHandlerName) {
		this.resultHandlerName = resultHandlerName;
	}

	/**
	 * An optional data object used by the processor
	 */
	@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
	public Object getProcessorData() {
		return processorData;
	}

	/**
	 * An optional data object used by the processor
	 */
	public void setProcessorData(Object invocationData) {
		this.processorData = invocationData;
	}

	/**
	 * Whether this is a request for a practice match
	 */
	public boolean isPractice() {
		return practice;
	}

	/**
	 * Whether this is a request for a practice match
	 */
	public void setPractice(boolean practice) {
		this.practice = practice;
	}

	/**
	 * The ID of the associated round, if applicable
	 */
	public Integer getRoundId() {
		return roundId;
	}

	/**
	 * The ID of the associated round, if applicable
	 */
	public void setRoundId(Integer roundId) {
		this.roundId = roundId;
	}

	/**
	 * The type of application making the request
	 */
	public AppType getApp() {
		return app;
	}

	/**
	 * The type of application making the request
	 */
	public void setApp(AppType requestorType) {
		this.app = requestorType;
	}

	/**
	 * The programming language of the request
	 */
	public LanguageType getLanguage() {
		return language;
	}

	/**
	 * The programming language of the request
	 */
	public void setLanguage(LanguageType languageType) {
		this.language = languageType;
	}

	/**
	 * The synchronous timeout in seconds
	 */
	public Integer getSyncTimeout() {
		return syncTimeout;
	}

	/**
	 * The synchronous timeout in seconds
	 */
	public void setSyncTimeout(Integer timeout) {
		this.syncTimeout = timeout;
	}

	/**
	 * The timeout for processor execution 
	 */
	public Integer getProcessorTimeout() {
		return processorTimeout;
	}

	/**
	 * The timeout for processor execution 
	 */
	public void setProcessorTimeout(Integer execTimeout) {
		this.processorTimeout = execTimeout;
	}

}
