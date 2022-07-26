package com.topcoder.server.farm.longtester;

import com.topcoder.server.tester.Solution;
import com.topcoder.shared.common.LongRoundScores;

public class MarathonCodeScoringRequest {

	private Solution solution;
	private LongRoundScores scores;

	public MarathonCodeScoringRequest() {

	}

	public MarathonCodeScoringRequest(Solution solution, LongRoundScores scores) {
		this.solution = solution;
		this.scores = scores;
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	public LongRoundScores getScores() {
		return scores;
	}

	public void setScores(LongRoundScores scores) {
		this.scores = scores;
	}

}
