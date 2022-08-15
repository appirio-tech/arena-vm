/**
 * Constants.java Description: Holder of commonly used constants
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp;

import java.awt.Color;
import com.topcoder.netCommon.contest.ContestConstants;

public class Constants {
	public final static int PHASE_NONE = -1;
	
	public static enum AppealStatus {
		None,
		Pending,
		Successful,
		Failed,
	};

	public final static int PROBLEM_NOTOPENED = 0;
	public final static int PROBLEM_OPENED = 1;
	public final static int PROBLEM_CLOSED = 2;
	public final static int PROBLEM_COMPILING = 3;
	public final static int PROBLEM_TESTING = 4;
	public final static int PROBLEM_SUBMITTING = 5;
	public final static int PROBLEM_SUBMITTED = 6;
	public final static int PROBLEM_CHALLENGING = 7;
	public final static int PROBLEM_CHALLENGE_SUCCESS = 8;
	public final static int PROBLEM_CHALLENGE_FAILED = 9;
	public final static int PROBLEM_SYSTEMTESTING = 10;
	public final static int PROBLEM_SYSTEMTESTED_PASSED = 11;
	public final static int PROBLEM_SYSTEMTESTED_FAILED = 12;

	private static final int[] rankBreaks = { 3000, 2200, 1500, 1200, 900, 1, 0, Integer.MIN_VALUE };

	private static final Color[] rankColors = { Color.decode("0xff0000"), // Red
				Color.decode("0xff0000"), // Red
				Color.decode("0xffff00"), // Yellow
				Color.decode("0x66cccc"), // Blue
				Color.decode("0x99ff33"), // Green
				Color.decode("0xcccccc"), // Grey
				Color.white, Color.decode("0xff9933") }; // Admin orange

	public final static String phaseText(int phaseID) {
		for(int x = 0; x < ContestConstants.SPECTATOR_PHASES.length; x++) {
			if (phaseID ==ContestConstants.SPECTATOR_PHASES[x]) return ContestConstants.SPECTATOR_PHASE_NAMES[x];
		}
		return "Unknown";
	}

	public final static Color getRankColor(int rank) {
		// Loop the the ranks looking for the matching rank
		// If found - return the color related to it
		for (int x = 0; x < rankBreaks.length; x++) {
			if (rank >= rankBreaks[x]) return rankColors[x];
		}
		// Unknown - really not possible but just in case
		return Color.white;
	}
}
