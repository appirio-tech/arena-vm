package com.topcoder.server.farm.compiler.srm;

import com.topcoder.server.common.Submission;
import com.topcoder.shared.problem.SimpleComponent;

public class SRMCompilationRequest {

	private Submission submission;
	private SimpleComponent component;

	public SRMCompilationRequest() {

	}

	public SRMCompilationRequest(Submission submission, SimpleComponent component) {
		this.submission = submission;
		this.component = component;
	}

	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	public SimpleComponent getComponent() {
		return component;
	}

	public void setComponent(SimpleComponent component) {
		this.component = component;
	}

}
