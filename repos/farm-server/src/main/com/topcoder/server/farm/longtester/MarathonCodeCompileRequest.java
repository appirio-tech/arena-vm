package com.topcoder.server.farm.longtester;

import com.topcoder.server.tester.LongSubmission;
import com.topcoder.shared.problem.ProblemComponent;

public class MarathonCodeCompileRequest {

	private ProblemComponent problemComponent;
	private LongSubmission submission;

	public MarathonCodeCompileRequest() {
	}

	public MarathonCodeCompileRequest(ProblemComponent problemComponent, LongSubmission submission) {
		this.problemComponent = problemComponent;
		this.submission = submission;
	}

	public ProblemComponent getProblemComponent() {
		return problemComponent;
	}

	public void setProblemComponent(ProblemComponent problemComponent) {
		this.problemComponent = problemComponent;
	}

	public LongSubmission getSubmission() {
		return submission;
	}

	public void setSubmission(LongSubmission submission) {
		this.submission = submission;
	}

}
