package com.topcoder.arena.code;

public class CodeServiceConfiguration {

	private String resultQueueName;
	private long resultMonitorInterval = 250;
	private int maxMonitorMessages = 2;
	private long syncCheckInterval = 200;

	public String getResultQueueName() {
		return resultQueueName;
	}

	public void setResultQueueName(String resultQueueName) {
		this.resultQueueName = resultQueueName;
	}

	public long getResultMonitorInterval() {
		return resultMonitorInterval;
	}

	public void setResultMonitorInterval(long resultMonitorIterval) {
		this.resultMonitorInterval = resultMonitorIterval;
	}

	public int getMaxMonitorMessages() {
		return maxMonitorMessages;
	}

	public void setMaxMonitorMessages(int maxMonitorMessages) {
		this.maxMonitorMessages = maxMonitorMessages;
	}

	public long getSyncCheckInterval() {
		return syncCheckInterval;
	}

	public void setSyncCheckInterval(long syncCheckInterval) {
		this.syncCheckInterval = syncCheckInterval;
	}

	
	
	
}
