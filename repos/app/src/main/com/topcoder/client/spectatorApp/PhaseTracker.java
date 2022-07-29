/**
 * PhaseTracker.java Description: Tracks the current phase - implements a
 * singleton pattern
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp;

import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.PhaseListener;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.netCommon.contest.ContestConstants;

public class PhaseTracker {
	/** The singleton instance of this class */
	private static PhaseTracker phaseTracker = null;

	/** The prior phase */
	private int priorPhaseID = Constants.PHASE_NONE;

	/** The Current phase */
	private int phaseID = Constants.PHASE_NONE;

	/** Time allocated (in seconds) for the phase */
	private int timeAllocated;

	/**
	 * Private Constructor for the phase tracker
	 */
	private PhaseTracker() {
		SpectatorEventProcessor.getInstance().addPhaseListener(new PhaseHandler());
	}

	/**
	 * Returns the singleton instance of the phase tracker
	 * 
	 * @returns the singleton instance of the phase tracker
	 */
	public synchronized static final PhaseTracker getInstance() {
		if (phaseTracker == null) phaseTracker = new PhaseTracker();
		return phaseTracker;
	}

	/**
	 * Gets the current phaseID
	 * 
	 * @returns the current phaseID
	 */
	public int getPhaseID() {
		return phaseID;
	}

	/**
	 * Gets the prior phaseID
	 * 
	 * @returns the prior phaseID
	 */
	public int getPriorPhaseID() {
		return priorPhaseID;
	}

	/**
	 * Gets the time allocated to the phase
	 * 
	 * @returns the time (in seconds) allocated to the phase
	 */
	public int getTimeAllocated() {
		return timeAllocated;
	}

	/**
	 * Gets the time allocated to the phase
	 * 
	 * @returns the time (in seconds) allocated to the phase
	 */
	public boolean isContestOngoing() {
		return phaseID == ContestConstants.CODING_PHASE
		|| phaseID == ContestConstants.INTERMISSION_PHASE 
		|| phaseID == ContestConstants.CHALLENGE_PHASE
		|| phaseID == ContestConstants.SYSTEM_TESTING_PHASE
		|| phaseID == ContestConstants.VOTING_PHASE
		|| phaseID == ContestConstants.TIE_BREAKING_VOTING_PHASE
		|| phaseID == ContestConstants.CONTEST_COMPLETE_PHASE;
	}

	/**
	 * Set's the current phase
	 * 
	 * @param the
	 *           phaseID
	 * @param the
	 *           time allocated
	 */
	private void setPhaseID(int phaseID, int timeAllocated) {
		this.priorPhaseID = this.phaseID;
		this.phaseID = phaseID;
		this.timeAllocated = timeAllocated;
	}

	/** class handling phase change messages */
	private class PhaseHandler implements PhaseListener {
		public void unknown(PhaseEvent evt, int phaseID) {}

		public void contestInfo(PhaseEvent evt) {
			setPhaseID(ContestConstants.SPECTATOR_CONTESTINFO, evt.getTimeAllocated());
		}

		public void announcements(PhaseEvent evt) {
			setPhaseID(ContestConstants.SPECTATOR_ANNOUNCEMENTS, evt.getTimeAllocated());
		}

		public void coding(PhaseEvent evt) {
			setPhaseID(ContestConstants.CODING_PHASE, evt.getTimeAllocated());
		}

		public void intermission(PhaseEvent evt) {
			setPhaseID(ContestConstants.INTERMISSION_PHASE, evt.getTimeAllocated());
		}

		public void challenge(PhaseEvent evt) {
			setPhaseID(ContestConstants.CHALLENGE_PHASE, evt.getTimeAllocated());
		}

		public void systemTesting(PhaseEvent evt) {
			setPhaseID(ContestConstants.SYSTEM_TESTING_PHASE, evt.getTimeAllocated());
		}

		public void endContest(PhaseEvent evt) {
			setPhaseID(ContestConstants.CONTEST_COMPLETE_PHASE, evt.getTimeAllocated());
		}

		public void voting(PhaseEvent evt) {
			setPhaseID(ContestConstants.VOTING_PHASE, evt.getTimeAllocated());
		}

		public void votingTie(PhaseEvent evt) {
			setPhaseID(ContestConstants.TIE_BREAKING_VOTING_PHASE, evt.getTimeAllocated());
		}

		public void componentAppeals(PhaseEvent evt) {
			setPhaseID(ContestConstants.COMPONENT_CONTEST_APPEALS, evt.getTimeAllocated());
		}

		public void componentResults(PhaseEvent evt) {
			setPhaseID(ContestConstants.COMPONENT_CONTEST_RESULTS, evt.getTimeAllocated());
		}

		public void componentEndContest(PhaseEvent evt) {
			setPhaseID(ContestConstants.COMPONENT_CONTEST_END, evt.getTimeAllocated());
		}
	}
}
