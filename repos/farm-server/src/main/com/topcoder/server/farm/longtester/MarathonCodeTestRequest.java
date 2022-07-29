package com.topcoder.server.farm.longtester;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;

public class MarathonCodeTestRequest {

	private FarmLongTestRequest longTestRequest;
	private ComponentFiles componentFiles;
	private Solution solution;

	public MarathonCodeTestRequest() {

	}

	public MarathonCodeTestRequest(FarmLongTestRequest longTestRequest, ComponentFiles componentFiles, Solution solution) {
		super();
		this.longTestRequest = longTestRequest;
		this.componentFiles = componentFiles;
		this.solution = solution;
	}

	public FarmLongTestRequest getLongTestRequest() {
		return longTestRequest;
	}

	public void setLongTestRequest(FarmLongTestRequest longTestRequest) {
		this.longTestRequest = longTestRequest;
	}

	public ComponentFiles getComponentFiles() {
		return componentFiles;
	}

	public void setComponentFiles(ComponentFiles componentFiles) {
		this.componentFiles = componentFiles;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}
}
