/**
 * ScoreboardCoderTracker.java Description: The data model used to track which
 * problem a coder is looking at
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.PhaseListener;
import com.topcoder.client.spectatorApp.event.ProblemAdapter;
import com.topcoder.client.spectatorApp.event.ProblemNotificationEvent;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;
import com.topcoder.shared.netCommon.messages.spectator.ProblemData;

public class ScoreboardCoderTracker {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(ScoreboardCoderTracker.class.getName());

	/** The coders assigned to the room */
	private ArrayList<CoderRoomData> coders;

	/** The problems assigned to the room */
	private ArrayList<ProblemData> problems;

	/** The roomID this model is associated with */
	private int roomID;

	/** Array holding who is in what room */
	private ArrayList[][] occupants;

	/** Support for coder moves */
	private CoderMoveSupport coderMoveSpt = new CoderMoveSupport();

	/** Handler of Problem events * */
	private ProblemHandler problemHandler = new ProblemHandler();

	/** Handler of Phase events * */
	private PhaseHandler phaseHandler = new PhaseHandler();

	/** Current phase */
	private int phaseID;

	/**
	 * Constructor for the Coding model
	 * 
	 * @param roomID
	 *           the roomID this model is associated with
	 * @param coders
	 *           the coders assigned to this room
	 * @param problems
	 *           the problems assigned to this room
	 */
	public ScoreboardCoderTracker(int roomID, List<CoderRoomData> coders, List<ProblemData> problems) {
		this.roomID = roomID;
		this.coders = new ArrayList<CoderRoomData>(coders);
		this.problems = new ArrayList<ProblemData>(problems);
		
		// Sort the problem by point value
		Collections.sort(this.problems, new ProblemComparator());
		
		// Initialize the internal structures
		occupants = new ArrayList[coders.size()][problems.size()];
		for (int r = occupants.length - 1; r >= 0; r--) {
			for (int c = occupants[r].length - 1; c >= 0; c--) {
				occupants[r][c] = new ArrayList();
			}
		}
		// Register for events
		SpectatorEventProcessor.getInstance().addProblemListener(problemHandler);
		SpectatorEventProcessor.getInstance().addPhaseListener(phaseHandler);
		// Set the phase that is currently active
		//PhaseTracker phaseTracker = PhaseTracker.getInstance();
		// setPhase(phaseTracker.getPhaseID(), phaseTracker.getTimeAllocated());
	}

	/**
	 * Releases resources being used
	 */
	public void dispose() {
		SpectatorEventProcessor.getInstance().removeProblemListener(problemHandler);
		SpectatorEventProcessor.getInstance().removePhaseListener(phaseHandler);
	}

	/**
	 * Returns the count of coders for this room
	 * 
	 * @returns the number of coders
	 */
	public int getCoderCount() {
		return coders.size();
	}

	/**
	 * Returns the count of the problems for this room
	 * 
	 * @returns the number of problems
	 */
	public int getProblemCount() {
		return problems.size() + 1;
	}

	/**
	 * Returns the index of a coder
	 * 
	 * @param coderHandle
	 *           the handle of the coder to find
	 * @returns the index position of the coder. Returns -1 if not found
	 */
	public final int indexOfCoder(String coderHandle) {
		if (coderHandle == null) return -1;
		for (int x = coders.size() - 1; x >= 0; x--) {
			if (coderHandle.equals(((CoderRoomData) coders.get(x)).getHandle())) return x;
		}
		return -1;
	}

	/**
	 * Returns the index of a problem
	 * 
	 * @param problemID
	 *           the identifier of a problem
	 * @returns the index position of the problem. Returns -1 if not found
	 */
	public final int indexOfProblem(int problemID) {
		for (int x = problems.size() - 1; x >= 0; x--) {
			if (problemID == ((ProblemData) problems.get(x)).getProblemID()) return x;
		}
		return -1;
	}

	/**
	 * Returns the coders at a specific location
	 * 
	 * @param r
	 *           the row
	 * @param c
	 *           the column
	 * @returns a List of coder handles that are in a specific location
	 * @see java.util.List
	 */
	public final List getCoders(int r, int c) {
		return (ArrayList) occupants[r][c].clone();
	}

	/**
	 * Finds the problem the coder is on
	 * 
	 * @returns the location - null if not found
	 */
	public Location findCoder(String coderHandle) {
		// Loop through looking for the coder
		for (int r = coders.size() - 1; r >= 0; r--) {
			for (int c = problems.size() - 1; c >= 0; c--) {
				// If we found the coder - close the problem
				if (occupants[r][c].contains(coderHandle)) return new Location(r, c);
			}
		}
		return null;
	}

	/**
	 * Processes a coder opening a problem
	 * 
	 * @param int
	 *           r the row
	 * @param int
	 *           c the column
	 * @param coder
	 *           the coder that is opening the problem
	 */
	private final void processOpen(int row, int col, String coder) {
		// Is the row/column correct?
		if (row < 0 || col < 0) return;
		// Already opened (maybe opened again!)
		if (occupants[row][col].contains(coder)) return;
		// First - check to see if the coder was in a different problem
		for (int r = coders.size() - 1; r >= 0; r--) {
			for (int c = problems.size() - 1; c >= 0; c--) {
				// If we found the coder - close the problem
				if (occupants[r][c].contains(coder)) processClosed(r, c, coder);
			}
		}
		// add the coder to the problem
		occupants[row][col].add(coder);
		// fire off the notification event
		coderMoveSpt.fireProblemOpened(new CoderMoveEvent(this, row, col, coder));
	}

	/**
	 * Processes a coder closing a problem
	 * 
	 * @param int
	 *           r the row
	 * @param int
	 *           c the column
	 * @param coder
	 *           the coder that is closing the problem
	 */
	private final void processClosed(int row, int col, String coder) {
		// Is the row/column correct?
		if (row < 0 || col < 0) return;
		// Find the coder
		int pos = occupants[row][col].indexOf(coder);
		// If found
		if (pos >= 0) {
			// Remove the coder to the problem
			occupants[row][col].remove(pos);
			// fire off the notification event
			coderMoveSpt.fireProblemClosed(new CoderMoveEvent(this, row, col, coder));
		}
	}

	/**
	 * Adds a listener of type
	 * com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener
	 * 
	 * @param listener
	 *           the listener to add
	 */
	public synchronized void addCoderMoveListener(com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener listener) {
		coderMoveSpt.addCoderMoveListener(listener);
	}

	/**
	 * Removes a listener of type
	 * com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener
	 * 
	 * @param listener
	 *           the listener to remove
	 */
	public synchronized void removeCoderMoveListener(com.topcoder.client.spectatorApp.scoreboard.model.CoderMoveListener listener) {
		coderMoveSpt.removeCoderMoveListener(listener);
	}

	/**
	 * Resets the occupants of each problem Will fire a problem closed event for
	 * anyone left in the problem
	 */
	private final void reset(int phaseID) {
		// Ignore same phase sets (phase likely was extended)
		if (this.phaseID == phaseID) return;
		this.phaseID = phaseID;
		for (int r = occupants.length - 1; r >= 0; r--) {
			for (int c = occupants[r].length - 1; c >= 0; c--) {
				for (int x = occupants[r][c].size() - 1; x >= 0; x--) {
					String coder = (String) occupants[r][c].remove(x);
					coderMoveSpt.fireProblemClosed(new CoderMoveEvent(this, r, c, coder));
				}
			}
		}
	}

	/** Class handling the problem event notification messages */
	private class ProblemHandler extends ProblemAdapter {
		public void opened(ProblemNotificationEvent evt) {
			// Ignore messages not destined for our room
			if (evt.getRoomID() != roomID) return;
			int r = indexOfCoder(evt.getWriter());
			if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
			int sr = indexOfCoder(evt.getSourceCoder());
			if (sr < 0) cat.error("Source Coder: " + evt.getSourceCoder() + " is not assigned to the room");
			int c = indexOfProblem(evt.getProblemID());
			if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
			// If any is not found return;
			if (r < 0 || c < 0 || sr < 0) return;
			// process the opening
			processOpen(r, c, evt.getSourceCoder());
		}

		public void closed(ProblemNotificationEvent evt) {
			// Ignore messages not destined for our room
			if (evt.getRoomID() != roomID) return;
			int r = indexOfCoder(evt.getWriter());
			if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
			int sr = indexOfCoder(evt.getSourceCoder());
			if (sr < 0) cat.error("Source Coder: " + evt.getSourceCoder() + " is not assigned to the room");
			int c = indexOfProblem(evt.getProblemID());
			if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
			// If any is not found return;
			if (r < 0 || c < 0 || sr < 0) return;
			// process the opening
			processClosed(r, c, evt.getSourceCoder());
		}
	}

	/** Class used to sort the problems by value */
	private class ProblemComparator implements java.util.Comparator<ProblemData> {
		public int compare(ProblemData o1, ProblemData o2) {
			int r1 = o1.getPointValue();
			int r2 = o2.getPointValue();
			if (r1 < r2) return -1;
			if (r1 == r2) return 0;
			return 1;
		}
	}

	/** Class handling the phase change messages */
	private class PhaseHandler implements PhaseListener {
		public void unknown(PhaseEvent evt, int phaseID) {
			reset(phaseID);
		}

		public void contestInfo(PhaseEvent evt) {
			reset(ContestConstants.SPECTATOR_CONTESTINFO);
		}

		public void announcements(PhaseEvent evt) {
			reset(ContestConstants.SPECTATOR_ANNOUNCEMENTS);
		}

		public void coding(PhaseEvent evt) {
			reset(ContestConstants.CODING_PHASE);
		}

		public void intermission(PhaseEvent evt) {
			reset(ContestConstants.INTERMISSION_PHASE);
		}

		public void challenge(PhaseEvent evt) {
			reset(ContestConstants.CHALLENGE_PHASE);
		}

		public void systemTesting(PhaseEvent evt) {
			reset(ContestConstants.SYSTEM_TESTING_PHASE);
		}

		public void endContest(PhaseEvent evt) {
			reset(ContestConstants.CONTEST_COMPLETE_PHASE);
		}

		public void voting(PhaseEvent evt) {
			reset(ContestConstants.VOTING_PHASE);
		}

		public void votingTie(PhaseEvent evt) {
			reset(ContestConstants.TIE_BREAKING_VOTING_PHASE);
		}

		public void componentAppeals(PhaseEvent evt) {
			reset(ContestConstants.COMPONENT_CONTEST_APPEALS);
		}

		public void componentResults(PhaseEvent evt) {
			reset(ContestConstants.COMPONENT_CONTEST_RESULTS);
		}

		public void componentEndContest(PhaseEvent evt) {
			reset(ContestConstants.COMPONENT_CONTEST_END);
		}
	}
}
