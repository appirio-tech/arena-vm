/**
 * ScoreboardPointTracker.java Description: The data model used during the
 * coding phase for a scoreboard room
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.AnimationManager;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.HeartBeatTimer;
import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.event.AnimationListener;
import com.topcoder.client.spectatorApp.event.LongProblemNotificationEvent;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.PhaseListener;
import com.topcoder.client.spectatorApp.event.ProblemAdapter;
import com.topcoder.client.spectatorApp.event.ProblemNotificationEvent;
import com.topcoder.client.spectatorApp.event.ProblemResultAdapter;
import com.topcoder.client.spectatorApp.event.ProblemResultEvent;
import com.topcoder.client.spectatorApp.event.SystemTestResultsAdapter;
import com.topcoder.client.spectatorApp.event.SystemTestResultsEvent;
import com.topcoder.client.spectatorApp.event.SystemTestResultsListener;
import com.topcoder.client.spectatorApp.event.TimerAdapter;
import com.topcoder.client.spectatorApp.event.TimerEvent;
import com.topcoder.client.spectatorApp.netClient.DispatchThread;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;
import com.topcoder.shared.netCommon.messages.spectator.ProblemData;

public class ScoreboardPointTracker implements CompetitionTracker<CoderRoomData> {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(ScoreboardPointTracker.class.getName());

	/** The coders assigned to the room */
	private ArrayList<CoderRoomData> coders;

	/** The problems assigned to the room */
	private ArrayList<ProblemData> problems;

	/** The roomID this model is associated with */
	private int roomID;

	/** The current point values */
	private int[][] pointValue;

	/** The prior phase total point values */
	private int[] priorTotalValue;

	/** Point Value Direction */
	private PointDirection[] totalValueDirection;

	/** The overall score */
	private int[] totalScore;

	/** The challenge adjustments */
	private int[] challengeValue;

	/** Whether a player won the room or not */
	private boolean[] winner;

	/** The status of the problem */
	private int[][] status;
        
        /** Is the coder currently working on this problem */
        private boolean[][] opened;

	/**
	 * Time (in seconds) when the problem was opened (as in time left in the
	 * phase when..)
	 */
	private int[][] timeOpened;

	/**
	 * If the problem was successfully challenged, holds the name of the
	 * challenger
	 */
	private String[][] challengerName;

	/** History of open/challenged for the current phase */
	private ArrayList<List<CoderHistory>> history;

	/** Teams the coders are part of */
	private Team[] teams;

	/** The current phase */
	private int phaseID;

	/** Length of the current phase */
	private int phaseLength = 0;

	/** The official time left in the phase (updated per second and corrected by server) */
	private long officialTimeLeft = 0;

	/** The estimated time left in the phase (update per animation) */
	private long estimatedTimeLeft = Long.MAX_VALUE;
	
	/** Update lock */
	// private Object lock = new Object();
	/** Support class for total changes */
	private TotalChangeSupport totalChangeSpt = new TotalChangeSupport();

	/** Support class for point value changes */
	private PointValueChangeSupport pointValueChangeSpt = new PointValueChangeSupport();

	/** Support class for problem status changes */
	private StatusChangeSupport statusChangeSpt = new StatusChangeSupport();
        
        private SubmissionCountSupport submissionCountSpt = new SubmissionCountSupport();

	/** Timer used to detemine how often to recalculate open problems */
	// private Timer timer;
	/** Handler for Problem events */
	private ProblemHandler problemHandler = new ProblemHandler();

	/** Handler for Problem Result events */
	private ProblemResultHandler problemResultHandler = new ProblemResultHandler();

	/** Handler for Phase Events */
	private PhaseHandler phaseHandler = new PhaseHandler();

	/** Handler for Timer update events */
	private TimerHandler timeHandler = new TimerHandler();

	/** Handler for Animation events */
	private AnimationHandler animationHandler = new AnimationHandler();

	/** Handler for system results events */
	private SystemResultHandler systemHandler = new SystemResultHandler();

	/** Cache used for system tests */
	private ProblemResultEvent[][] problemResultCache;

	/** The placement tracker */
	private final CompetitionPlacementTracker<CoderRoomData> placementTracker;
	
        
        //private int[][] pointValue;
        private int[][] submissionCount;
        private int[][] submissionTime;
        private int[][] exampleCount;
        private int[][] exampleTime;
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
	public ScoreboardPointTracker(int roomID, List<CoderRoomData> coders, List<ProblemData> problems) {
		this.roomID = roomID;
		this.coders = new ArrayList<CoderRoomData>(coders);
		this.problems = new ArrayList<ProblemData>(problems);
		
		// Sort the problem by point value
		Collections.sort(this.problems, new ProblemComparator());
		
		// Initialize the internal structures
		pointValue = new int[coders.size()][problems.size()];
		priorTotalValue = new int[coders.size()];
		totalValueDirection = new PointDirection[coders.size()];
		for(int x=0;x<totalValueDirection.length;x++) totalValueDirection[x] = PointDirection.NoChange;
		status = new int[coders.size()][problems.size()];
                opened = new boolean[coders.size()][problems.size()];
		timeOpened = new int[coders.size()][problems.size()];
		problemResultCache = new ProblemResultEvent[coders.size()][problems.size()];
		totalScore = new int[coders.size()];
		challengeValue = new int[coders.size()];
		challengerName = new String[coders.size()][problems.size()];
                submissionCount = new int[coders.size()][problems.size()];
                submissionTime = new int[coders.size()][problems.size()];
                exampleCount = new int[coders.size()][problems.size()];
                exampleTime = new int[coders.size()][problems.size()];
		
		// Show everyone as not winning the room
		winner = new boolean[coders.size()];
		Arrays.fill(winner, false);
		
		// Get the teams each coder is assigned to
		teams = new Team[coders.size()];
		TeamManager teamManager = TeamManager.getInstance();
		for (int x = coders.size() - 1; x >= 0; x--) {
			CoderRoomData coderData = coders.get(x);
			teams[x] = teamManager.getAssignedTeam(coderData.getCoderID());
			if (teams[x] != null) teams[x].updateCoderData(coderData);
		}
		
		// Initialize the history
		history = new ArrayList<List<CoderHistory>>();
		for (int x = coders.size() - 1; x >= 0; x--) history.add(new ArrayList<CoderHistory>());
		
		// Register for events
		SpectatorEventProcessor.getInstance().addProblemListener(problemHandler);
		SpectatorEventProcessor.getInstance().addProblemResultListener(problemResultHandler);
		SpectatorEventProcessor.getInstance().addPhaseListener(phaseHandler);
		SpectatorEventProcessor.getInstance().addSystemTestResultsListener(systemHandler);
		HeartBeatTimer.getInstance().addTimerListener(timeHandler);
		AnimationManager.getInstance().addAnimationListener(animationHandler);
		
		// Recalculate open problems every quarter second
		// timer = new Timer(250, new TimeHandler());
		// timer.setRepeats(true);
		// Set the phase that is currently active
		PhaseTracker phaseTracker = PhaseTracker.getInstance();
		setPhase(phaseTracker.getPhaseID(), phaseTracker.getTimeAllocated());
		
		placementTracker = new ScoreboardPlacementTracker(this);
	}

	/**
	 * Releases resources being used
	 */
	public void dispose() {
		SpectatorEventProcessor.getInstance().removeProblemListener(problemHandler);
		SpectatorEventProcessor.getInstance().removeProblemResultListener(problemResultHandler);
		SpectatorEventProcessor.getInstance().removePhaseListener(phaseHandler);
		SpectatorEventProcessor.getInstance().removeSystemTestResultsListener(systemHandler);
		HeartBeatTimer.getInstance().removeTimerListener(timeHandler);
		AnimationManager.getInstance().removeAnimationListener(animationHandler);
		placementTracker.dispose();
	}

	/* (non-Javadoc)
	 * @see com.topcoder.client.spectatorApp.scoreboard.model.CompetitionTracker#getPlacementTracker()
	 */
	public CompetitionPlacementTracker<CoderRoomData> getPlacementTracker()
	{
		return placementTracker;
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
		return problems.size();
	}

	/**
	 * Returns the problem data for a specific index position
	 * 
	 * @returns the problem data
	 * @see com.topcoder.netCommon.spectatorMessages.ProblemData
	 */
	public ProblemData getProblem(int idx) {
		return problems.get(idx);
	}

	/* (non-Javadoc)
	 * @see com.topcoder.client.spectatorApp.scoreboard.model.CompetitionTracker#getCoder(int)
	 */
	public CoderRoomData getCoder(int idx) {
		return coders.get(idx);
	}

	/**
	 * Returns all the coders as a List
	 * 
	 * @returns list of CoderRoomData objects
	 * @see java.util.List
	 */
	public List<CoderRoomData> getCoders() {
		return new ArrayList<CoderRoomData>(coders);
	}

	/**
	 * Returns all the problems as a List
	 * 
	 * @returns list of ProblemData objects
	 * @see java.util.List
	 */
	public List<ProblemData> getProblems() {
		return new ArrayList<ProblemData>(problems);
	}

	/**
	 * Returns the point value for a specific coder/problem
	 * 
	 * @param coderHandle
	 *           the handle of the coder
	 * @param problemID
	 *           the identifier of the problem
	 * @returns an int representing the value (divide by 100 for decimals).
	 *          Returns -1 if not found
	 */
	public int getPointValue(String coderHandle, int problemID) {
		// Find the coder index position
		int r = indexOfCoder(coderHandle);
		if (r < 0) return -1;
		
		// Find the problem index position
		int c = indexOfProblem(problemID);
		if (c < 0) return -1;
		
		// Return the point value
		return getPointValue(r, c);
	}

	/**
	 * Returns the point value for a specific index position
	 * 
	 * @param r
	 *           the row index
	 * @param c
	 *           the column index
	 * @returns an int representing the value (divide by 100 for decimals)
	 */
	public int getPointValue(int r, int c) {
		return pointValue[r][c];
	}

	/**
	 * Returns the total point value for a specific index position from the prior
	 * phase
	 * 
	 * @param r
	 *           the row index
	 * @param c
	 *           the column index
	 * @returns an int representing the value (divide by 100 for decimals)
	 */
	public int getPriorTotalValue(int r) {
		return priorTotalValue[r];
	}

	/**
	 * Returns the total value direction for a specific index position
	 * 
	 * @param r
	 *           the row index
	 * @returns an int representing the direction the point value is going
	 */
	public PointDirection getTotalValueDirection(int r) {
		return totalValueDirection[r];
	}

	/**
	 * Returns the status of the problem
	 * 
	 * @param coderHandle
	 *           the handle of the coder
	 * @param problemID
	 *           the identifier of the problem
	 * @returns an int representing the status. Returns -1 if not found
	 */
	public int getProblemStatus(String coderHandle, int problemID) {
		// Find the coder index position
		int r = indexOfCoder(coderHandle);
		if (r < 0) return -1;
		
		// Find the problem index position
		int c = indexOfProblem(problemID);
		if (c < 0) return -1;
		
		// Return the point value
		return getProblemStatus(r, c);
	}

	/**
	 * Returns the status of the problem
	 * 
	 * @param r
	 *           the row index
	 * @param c
	 *           the column index
	 * @returns an int representing the status of the problem
	 */
	public int getProblemStatus(int r, int c) {
		return status[r][c];
	}
        
        public boolean isProblemOpen(int r, int c) {
            return opened[r][c];
        }

	/**
	 * Returns the name of the challenger
	 * 
	 * @param r
	 *           the row index
	 * @param c
	 *           the column index
	 * @returns an String representing the coder handle of the challenger - null
	 *          if not challenged
	 */
	public String getChallengerName(int r, int c) {
		return challengerName[r][c];
	}

	/**
	 * Returns the value of any challenges for the given row
	 * @param r the row index
	 * @return an int representing the challenge adjustment of the row
	 */
	public int getChallengeValue(int r) {
		return challengeValue[r];
	}
        
        public int getSubmissionCount(int r, int c) {
            return submissionCount[r][c];
        }
        
        public int getSubmissionTime(int r, int c) {
            return submissionTime[r][c];
        }
        
        public int getExampleCount(int r, int c) {
            return exampleCount[r][c];
        }
        
        public int getExampleTime(int r, int c) {
            return exampleTime[r][c];
        }

	/**
	 * Returns the total score for a specific coder
	 * 
	 * @param coderHandle
	 *           the handle of the coer
	 * @returns an int representing the value (divide by 100 for decimals).
	 *          Returns -1 if coder not found
	 */
	public int getTotalScore(String coderHandle) {
		// Find the coder
		int r = indexOfCoder(coderHandle);
		if (r < 0) return -1;
		
		// Return the total score
		return getTotalScore(r);
	}

	/**
	 * Returns the total score for a specific row
	 * 
	 * @param r
	 *           the row index
	 * @returns an int representing the value (divide by 100 for decimals)
	 */
	public int getTotalScore(int r) {
		return totalScore[r];
	}

	/** Returns true if the specified row has any positive problem point totals
	 * 
	 * @param r the row index
	 * @return true if any problem has a positive point total
	 */
	public boolean hasProblemPoints(int r) {
		for (int c = problems.size() - 1; c >= 0; c --) {
			if (getPointValue(r, c) > 0) return true;
		}
		return false;
	}
	
	/**
	 * Returns the history of the current phase
	 * 
	 * @param r
	 *           the row index
	 * @returns an arraylist containing CoderHistory objects
	 * @see CoderHistory
	 */
	public List<CoderHistory> getHistory(int r) {
		return history.get(r);
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
			if (coderHandle.equals(coders.get(x).getHandle())) return x;
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.topcoder.client.spectatorApp.scoreboard.model.CompetitionTracker#indexOfCoder(int)
	 */
	public int indexOfCoder(int coderID) {
		// Loop through the coders
		for (int y = coders.size() - 1; y >= 0; y--) {
			// Is it a match?
			if (coders.get(y).getCoderID() == coderID) {
				return y;
			}
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
			if (problemID == problems.get(x).getProblemID()) return x;
		}
		return -1;
	}

	/**
	 * Returns whether the coder is a winner or not. Note: more than one person
	 * can win a room
	 * 
	 * @param row
	 *           the row position of the coder
	 * @returns true if the coder has won the room
	 */
	public final boolean isWinner(int r) {
		// Verify parm is correct
		if (r < 0 || r >= winner.length) return false;
		
		// Return whether they are a winner
		return winner[r];
	}

	/** Determines if the given row has any problem that is in system testing status
	 * 
	 * @param r the row position of the coder to test
	 * @return true if all problems are marked as system testing
	 */
	public final boolean isCoderSystemTesting(int r)
	{
		if (phaseID != ContestConstants.SYSTEM_TESTING_PHASE) return false;
		
		if (r < 0 || r >= coders.size()) return false;
		for (int c = 0; c < problems.size(); c++) {
			if (getProblemStatus(r, c) == Constants.PROBLEM_SYSTEMTESTING) {
				return true;
			}
		}
		return false;
	}
	
	/** Determines if the given row has any problem that has a result
	 * 
	 * @param r the row position of the coder to test
	 * @return true if all problems are marked as system testing done
	 */
        
        public final boolean isEveryOneDone() {
            
            for(int r = 0; r < coders.size(); r++) {
                if (getTotalScore(r) <= 0) continue;
            
                for (int c = 0; c < problems.size(); c++) {
			int status = getProblemStatus(r, c);
			if (status != Constants.PROBLEM_SYSTEMTESTED_FAILED 
		   &&  status != Constants.PROBLEM_SYSTEMTESTED_PASSED) { 
				return false;
			}
		}
            }
            return true;
        }
        
	public final boolean isCoderSystemTestDone(int r)
	{
		if (phaseID == ContestConstants.CONTEST_COMPLETE_PHASE) return true;
		if (phaseID != ContestConstants.SYSTEM_TESTING_PHASE) return false;
		
		if (r < 0 || r >= coders.size()) return false;
		
		// Someone with no score is already done!
		if (!hasProblemPoints(r)) return true;
		
		for (int c = 0; c < problems.size(); c++) {
			int status = getProblemStatus(r, c);
			if (status == Constants.PROBLEM_SYSTEMTESTED_FAILED 
		   ||  status == Constants.PROBLEM_SYSTEMTESTED_PASSED) { 
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Recalculates the final total. Fires a total change notification if
	 * different than current total
	 * 
	 * @param r
	 *           the row to recalculate
	 * @returns the final total for the row
	 */
	private final int recalcFinalTotal(int r) {
		// Calculate the points
		int total = 0;
		for (int c = pointValue[r].length - 1; c >= 0; c--) {
			if (status[r][c] != Constants.PROBLEM_OPENED) total += pointValue[r][c];
		}
		
		// Total is equal to the points + any challenge adjustment
		total += challengeValue[r];
		
		// If different, update the total and fire a notification off
		if (total != totalScore[r]) {
			updateTotalValueDirection(r, totalScore[r] < total ? PointDirection.IncreasingValue : PointDirection.DecreasingValue);
			totalScore[r] = total;
			totalChangeSpt.fireUpdateTotal(new TotalChangeEvent(this, r, total));
		}
		
		// If assigned to a team - update the team total
		if (teams[r] != null) teams[r].updateCoderTotal(coders.get(r).getCoderID(), total);
		
		// Return the total score for the row
		return total;
	}

	/**
	 * Updates the point value for a problem. If the value is different, fires
	 * off a notification and recalculates the final total
	 * 
	 * @param r
	 *           the row to set
	 * @param c
	 *           the column to set
	 * @param value
	 *           the value to set the row/column to
	 */
	private final void updatePointValue(int r, int c, int value) {
		if (pointValue[r][c] != value) {
			// Save the value
			pointValue[r][c] = value;
			
			// Fire off notification and recalc the total
			pointValueChangeSpt.fireUpdatePointValue(new PointValueChangeEvent(this, r, c, value));
			
			// Recalc the final total
			recalcFinalTotal(r);
		}
	}

	/**
	 * Updates the direction of the total value
	 * 
	 * @param r
	 *           the row to set
	 * @param value
	 *           the value to set the row/column to
	 */
	private final void updateTotalValueDirection(int r, PointDirection value) {
		totalValueDirection[r] = value;
	}

	/**
	 * Updates the challenge value for a row then recalculates the final total
	 * 
	 * @param r
	 *           the row to set
	 * @param c
	 *           the column to set
	 * @param value
	 *           the value to set the row/column to
	 */
	private final void updateChallengeValue(int r, int value) {
		challengeValue[r] += value;
		recalcFinalTotal(r);
	}
        
        private final void updateOpened(int r, int c, boolean viewing) {
            opened[r][c] = viewing;
        }

	/**
	 * Updates the challenge value for a row then recalculates the final total
	 * 
	 * @param r
	 *           the row to set
	 * @param c
	 *           the column to set
	 * @param newStatus
	 *           the new status to set
	 * @param relatedCoder
	 *           the coder that caused the status change
	 */
	private final void updateStatus(int r, int c, int newStatus, String relatedCoder) {
		if (status[r][c] != newStatus) {
			status[r][c] = newStatus;
			
			// If the status is a successful challenge - save the name of the
			// challenger
			if (newStatus == Constants.PROBLEM_CHALLENGE_SUCCESS) challengerName[r][c] = relatedCoder;
			
			// Fire off a status change event
			statusChangeSpt.fireUpdateStatus(new StatusChangeEvent(this, r, c, newStatus, relatedCoder));
		}
	}

	/**
	 * Set's a particular person as a room winner
	 * 
	 * @param handle
	 *           the handle that is a winner
	 */
	public final void setWinner(String handle) {
		// Find the coder
		int r = indexOfCoder(handle);
		if (r < 0) {
			cat.error("Player: " + handle + " is not assigned to the room");
		} else {
			winner[r] = true;
		}
	}

	/**
	 * Set's a particular person as a room winner
	 * 
	 * @param handle
	 *           the handle that is a winner
	 */
	public final int getWinner() {
		for (int x = 0; x < winner.length; x++) {
			if (winner[x]) return x;
		}
		return -1;
	}

	/**
	 * Set's up processing for the specific phase
	 * 
	 * @param phaseID
	 *           the identifier of the phase
	 * @param timeAllocated
	 *           the time allocated to the phase
	 */
	private final void setPhase(int phaseID, int timeAllocated) {
		// If the phase is restarted (because time was added as an example) -
		// ignore the change
		if (this.phaseID == phaseID) return;
		
		// Grab a lock on the data
		// synchronized(lock) {
		// Save the length of the phase
		this.phaseID = phaseID;
		this.phaseLength = timeAllocated;
		this.estimatedTimeLeft = Long.MAX_VALUE; // reset the estimated time left
		
		// Trap phase changes prior to a status being updated
		if (status == null) return;
		
		// Loop through all the rows/cols
		for (int r = 0; r < status.length; r++) {
			// Clear the history for the row
			history.get(r).clear();
			updateTotalValueDirection(r, PointDirection.NoChange);
			
			// Save the current point total to the prior point total
			// (ignore this on an endcontest)...
			if (phaseID != ContestConstants.CONTEST_COMPLETE_PHASE) priorTotalValue[r] = totalScore[r];
			if (phaseID == ContestConstants.CODING_PHASE) priorTotalValue[r] = 0;
			
			// Loop through the columns
			for (int c = 0; c < status[r].length; c++) {
				// If it's the coding phase - reset everything
				if (phaseID == ContestConstants.CODING_PHASE) {
					updateStatus(r, c, Constants.PROBLEM_NOTOPENED, "");
					timeOpened[r][c] = 0;
					updatePointValue(r, c, 0);
					updateChallengeValue(r, 0);
					
					// If not, reset the timeopened and clear the pointValue if the
					// status was problem opened
					// (because pointvalue represented pending points)
				} else {
					timeOpened[r][c] = 0;
					if (status[r][c] == Constants.PROBLEM_OPENED) {
						updatePointValue(r, c, 0);
					}
				}
			}
		}
		// }
		// Start the timer in the coding phase, stop it otherwise
		// if(phaseID == Constants.PHASE_CODING) timer.start(); else timer.stop();
	}

	/**
	 * Updates the status of a coder/problem
	 * 
	 * @param evt
	 *           the problem notificaiton event
	 * @param newStatus
	 *           the new status of the problem
	 */
	private void updateStatusMessage(ProblemNotificationEvent evt, int newStatus) {
		// If message is not for our room, return
		if (evt.getRoomID() != roomID) return;
		
		// Find the coder/problem
		int r = indexOfCoder(evt.getWriter());
		if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
		
		int sr = indexOfCoder(evt.getSourceCoder());
		if (sr < 0) cat.error("Source Coder: " + evt.getSourceCoder() + " is not assigned to the room");
		
		int c = indexOfProblem(evt.getProblemID());
		if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
		
		// If any is not found return;
		if (r < 0 || c < 0 || sr < 0) return;
		
		// Change the status to submitted and cancel the countdown
		// The point update will come in a notification..
		// synchronized(lock) {
		updateStatus(r, c, newStatus, evt.getSourceCoder());
		timeOpened[r][c] = 0;
		// }
	}
        
        private void updateSubmitCounts(LongProblemNotificationEvent evt) {
		// If message is not for our room, return
		if (evt.getRoomID() != roomID) return;
		
		// Find the coder/problem
		int r = indexOfCoder(evt.getWriter());
		if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
		
		int c = indexOfProblem(evt.getProblemID());
		if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
		
		// If any is not found return;
		if (r < 0 || c < 0) return;
		
		// Change the status to submitted and cancel the countdown
		// The point update will come in a notification..
		 //synchronized(lock) {
                     submissionCount[r][c] = evt.getSubmissionCount();
                     submissionTime[r][c] = evt.getSubmissionTime();
                     exampleCount[r][c] = evt.getExampleCount();
                     exampleTime[r][c] = evt.getExampleTime();
                     
                submissionCountSpt.fireUpdate(new SubmissionCountEvent(this, r, evt.getSubmissionCount(), evt.getSubmissionTime(), evt.getExampleCount(), evt.getExampleTime()));
		//updateStatus(r, c, newStatus, evt.getSourceCoder());
		//timeOpened[r][c] = 0;
		 //}
	}

	/**
	 * Recalculates the estimated values for the open problems
	 */
	private final void recalcOpenProblems() {
		// If it's not the coding phase - ignore the request
		if (phaseID != ContestConstants.CODING_PHASE) return;
		
		// Calculate the elapsed time
		long phaseLengthTime = CommonRoutines.calcTimePerSecond(phaseLength);
		
		// Lock the values
		// synchronized(lock) {
		// Loop through all the values (columns by rows is faster)
		for (int c = problems.size() - 1; c >= 0; c--) {
			// Get the value of the problem
			int valueOfProblem = getProblem(c).getPointValue();
			
			// Loop through the rows
			for (int r = coders.size() - 1; r >= 0; r--) {
				// Is it currently open?
				if (status[r][c] == Constants.PROBLEM_OPENED) {
					long elapsedTime = CommonRoutines.calcTimePerSecond(timeOpened[r][c]) - estimatedTimeLeft;
					
					// Update the total
					updatePointValue(r, c, (int) (100 * (valueOfProblem * (.3 + .7 / (10.0 * Math.pow((double) elapsedTime / phaseLengthTime, 2.0) + 1)))));
				}
			}
		}
		// }
	}

	/**
	 * Adds a listener of for final total changes
	 * 
	 * @param listener
	 *           the listener to add
	 */
	public synchronized void addTotalChangeListener(TotalChangeListener listener) {
		totalChangeSpt.addTotalChangeListener(listener);
	}

	/**
	 * Removes a listener for total changes
	 * 
	 * @param listener
	 *           the listener to remove
	 */
	public synchronized void removeTotalChangeListener(TotalChangeListener listener) {
		totalChangeSpt.removeTotalChangeListener(listener);
	}
        
        public synchronized void addSubmissionCountListener(SubmissionCountListener listener) {
		submissionCountSpt.addListener(listener);
	}

	/**
	 * Removes a listener for total changes
	 * 
	 * @param listener
	 *           the listener to remove
	 */
	public synchronized void removeSubmissionCountListener(SubmissionCountListener listener) {
		submissionCountSpt.removeListener(listener);
	}

	/**
	 * Adds a listener for point value changes
	 * 
	 * @param listener
	 *           the listener to add
	 */
	public synchronized void addPointValueChangeListener(PointValueChangeListener listener) {
		pointValueChangeSpt.addPointValueChangeListener(listener);
	}

	/**
	 * Removes a listener for point value changes
	 * 
	 * @param listener
	 *           the listener to remove
	 */
	public synchronized void removePointValueChangeListener(PointValueChangeListener listener) {
		pointValueChangeSpt.removePointValueChangeListener(listener);
	}

	/**
	 * Adds a listener of type
	 * com.topcoder.client.spectatorApp.scoreboard.model.StatusChangeListener
	 * 
	 * @param listener
	 *           the listener to add
	 */
	public synchronized void addStatusChangeListener(com.topcoder.client.spectatorApp.scoreboard.model.StatusChangeListener listener) {
		statusChangeSpt.addStatusChangeListener(listener);
	}

	/**
	 * Removes a listener of type
	 * com.topcoder.client.spectatorApp.scoreboard.model.StatusChangeListener
	 * 
	 * @param listener
	 *           the listener to remove
	 */
	public synchronized void removeStatusChangeListener(com.topcoder.client.spectatorApp.scoreboard.model.StatusChangeListener listener) {
		statusChangeSpt.removeStatusChangeListener(listener);
	}

	/** Class handling the problem event notification messages */
	private class ProblemHandler extends ProblemAdapter {
                public void closed(ProblemNotificationEvent evt) {
                        // If message is not for our room, return
			if (evt.getRoomID() != roomID) return;
			
			// Find the coder/problem
			int r = indexOfCoder(evt.getWriter());
			if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
			
			int sr = indexOfCoder(evt.getSourceCoder());
			if (sr < 0) cat.error("Source Coder: " + evt.getSourceCoder() + " is not assigned to the room");
			
			int c = indexOfProblem(evt.getProblemID());
			if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
			
			// If any is not found return;
			if (r < 0 || c < 0 || sr < 0) return;
                        
                        updateOpened(r,c,false);
                }
		public void opened(ProblemNotificationEvent evt) {
			// If message is not for our room, return
			if (evt.getRoomID() != roomID) return;
			
			// Find the coder/problem
			int r = indexOfCoder(evt.getWriter());
			if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
			
			int sr = indexOfCoder(evt.getSourceCoder());
			if (sr < 0) cat.error("Source Coder: " + evt.getSourceCoder() + " is not assigned to the room");
			
			int c = indexOfProblem(evt.getProblemID());
			if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
			
			// If any is not found return;
			if (r < 0 || c < 0 || sr < 0) return;
                        
                        updateOpened(r,c,true);
                        
			// Add to the history of the row
			// No longer track the open events here
			// history[sr].add(new CoderHistory(CoderHistory.OPENED, c,
			// evt.getWriter(), 0));
			// If the problem was not already opened - open it and start the count
			// down
			if (status[r][c] == Constants.PROBLEM_NOTOPENED && phaseID == ContestConstants.CODING_PHASE) {
				// synchronized(lock) {
				updateStatus(r, c, Constants.PROBLEM_OPENED, evt.getSourceCoder());
				timeOpened[r][c] = evt.getTimeLeft();
				updatePointValue(r, c, problems.get(c).getPointValue() * 100);
				// }
			}
		}

		public void submitted(ProblemNotificationEvent evt) {
			updateStatusMessage(evt, Constants.PROBLEM_SUBMITTED);
		}

		public void challenging(ProblemNotificationEvent evt) {
			updateStatusMessage(evt, Constants.PROBLEM_CHALLENGING);
		}

		public void systemTesting(ProblemNotificationEvent evt) {
			updateStatusMessage(evt, Constants.PROBLEM_SYSTEMTESTING);
		}
                public void longProblemInfo(LongProblemNotificationEvent evt) {
                    updateSubmitCounts(evt);
                }
	}

	/** Class handling the problem result notification messages */
	private class ProblemResultHandler extends ProblemResultAdapter {
		public void submitted(ProblemResultEvent evt) {
			// If message is not for our room, return
			if (evt.getRoomID() != roomID) return;
                        
                        cat.info(("Got submission for " + evt.getWriter() + ": " + evt.getResult()));
			
			int r = indexOfCoder(evt.getWriter());
			if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
			
			int sr = indexOfCoder(evt.getSourceCoder());
			if (sr < 0) cat.error("Source Coder: " + evt.getSourceCoder() + " is not assigned to the room");
			
			int c = indexOfProblem(evt.getProblemID());
			if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
			
			// If any is not found return;
			if (r < 0 || c < 0 || sr < 0) return;
			
			// Turn off the counting down and set the true point value
			updateStatus(r, c, Constants.PROBLEM_SUBMITTED, evt.getSourceCoder());
			
			//updatePointValue(r, c, (int) (evt.getValue() * 100.0));
			updatePointValue(r, c, (int) (evt.getValue()));
			
			// If not the coding phase - reset the prior total value
			// This is likely due to a restart of the spec (so we could
			// get submissions [ie updates] of a score in a different phase)
			if (phaseID != ContestConstants.CODING_PHASE) {
				priorTotalValue[r] = totalScore[r];
				updateTotalValueDirection(r, PointDirection.NoChange);
			}
		}

		public void challenged(ProblemResultEvent evt) {
			// If message is not for our room, return
			if (evt.getRoomID() != roomID) return;
			// Find the coder/problem
			int r = indexOfCoder(evt.getWriter());
			if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
			
			int sr = indexOfCoder(evt.getSourceCoder());
			if (sr < 0) cat.error("Source Coder: " + evt.getSourceCoder() + " is not assigned to the room");
			
			int c = indexOfProblem(evt.getProblemID());
			if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
			
			// If any is not found return;
			if (r < 0 || c < 0 || sr < 0) return;
			
			// Set the result
			if (evt.getResult() == ProblemResultEvent.SUCCESSFUL) {
				updateStatus(r, c, Constants.PROBLEM_CHALLENGE_SUCCESS, evt.getSourceCoder());
				timeOpened[r][c] = 0;
				updatePointValue(r, c, 0);
				updateChallengeValue(sr, (int) Math.round(evt.getValue() * 100.0));
				
				// updateChallengeValue(sr, (int) (evt.getValue()));
				// Add to the history of the row
				history.get(sr).add(new CoderHistory(CoderHistory.CHALLENGED, c, evt.getWriter(), (int) Math.round(evt.getValue() * 100.0)));
				
				// history[sr].add(new CoderHistory(CoderHistory.CHALLENGED, c,
				// evt.getWriter(), (int) (evt.getValue())));
			} else {
				updateStatus(r, c, Constants.PROBLEM_CHALLENGE_FAILED, evt.getSourceCoder());
				timeOpened[r][c] = 0;
				updateChallengeValue(sr, -1 * (int) Math.round(evt.getValue() * 100.0 * .5));
				// updateChallengeValue(sr, -1 * (int) (evt.getValue()));
				// Add to the history of the row
				history.get(sr).add(new CoderHistory(CoderHistory.CHALLENGED, c, evt.getWriter(), -1 * (int) Math.round(evt.getValue() * 100.0)));
				// history[sr].add(new CoderHistory(CoderHistory.CHALLENGED, c,
				// evt.getWriter(), -1 * (int) (evt.getValue())));
			}
		}

		public void systemTested(ProblemResultEvent evt) {
			// If message is not for our room, return
			if (evt.getRoomID() != roomID) return;
			int r = indexOfCoder(evt.getWriter());
			if (r < 0) cat.error("Problem Writer: " + evt.getWriter() + " is not assigned to the room");
			int c = indexOfProblem(evt.getProblemID());
			if (c < 0) cat.error("Problem ID: " + evt.getProblemID() + " is not assigned to the room");
			// If any is not found return;
			if (r < 0 || c < 0) return;
			// Save the problem result
			problemResultCache[r][c] = evt;
		}
	}

	/** Class handling the phase change messages */
	private class PhaseHandler implements PhaseListener {
		public void unknown(PhaseEvent evt, int phaseID) {
			setPhase(phaseID, evt.getTimeAllocated());
		}

		public void contestInfo(PhaseEvent evt) {
			setPhase(ContestConstants.SPECTATOR_CONTESTINFO, evt.getTimeAllocated());
		}

		public void announcements(PhaseEvent evt) {
			setPhase(ContestConstants.SPECTATOR_ANNOUNCEMENTS, evt.getTimeAllocated());
		}

		public void coding(PhaseEvent evt) {
			setPhase(ContestConstants.CODING_PHASE, evt.getTimeAllocated());
		}

		public void intermission(PhaseEvent evt) {
			setPhase(ContestConstants.INTERMISSION_PHASE, evt.getTimeAllocated());
		}

		public void challenge(PhaseEvent evt) {
			setPhase(ContestConstants.CHALLENGE_PHASE, evt.getTimeAllocated());
		}

		public void systemTesting(PhaseEvent evt) {
			setPhase(ContestConstants.SYSTEM_TESTING_PHASE, evt.getTimeAllocated());
		}

		public void endContest(PhaseEvent evt) {
			setPhase(ContestConstants.CONTEST_COMPLETE_PHASE, evt.getTimeAllocated());
		}

		public void voting(PhaseEvent evt) {
			setPhase(ContestConstants.VOTING_PHASE, evt.getTimeAllocated());
		}

		public void votingTie(PhaseEvent evt) {
			setPhase(ContestConstants.TIE_BREAKING_VOTING_PHASE, evt.getTimeAllocated());
		}

		public void componentAppeals(PhaseEvent evt) {
			setPhase(ContestConstants.COMPONENT_CONTEST_APPEALS, evt.getTimeAllocated());
		}

		public void componentResults(PhaseEvent evt) {
			setPhase(ContestConstants.COMPONENT_CONTEST_RESULTS, evt.getTimeAllocated());
		}

		public void componentEndContest(PhaseEvent evt) {
			setPhase(ContestConstants.COMPONENT_CONTEST_END, evt.getTimeAllocated());
		}
	}

	/** Class handling the time left notices */
	private class TimerHandler extends TimerAdapter {
		public void timerUpdate(TimerEvent evt) {
			officialTimeLeft = CommonRoutines.calcTimePerSecond(evt.getTimeLeft());
			
			// If our official time left is less than estimated, get estimated up to it
			if (officialTimeLeft < estimatedTimeLeft) {
				estimatedTimeLeft = officialTimeLeft;
			}
			
			// If estimated is more than a second less than official, pull it back
			if (estimatedTimeLeft < officialTimeLeft - CommonRoutines.getCurrentTimeUnit()) {
				estimatedTimeLeft = officialTimeLeft - CommonRoutines.getCurrentTimeUnit();
			}
			//recalcOpenProblems();
		}
	}

	/** Class handling the time left notices */
	private class AnimationHandler implements AnimationListener {
		private long timePerUnit;
		private long lastUpdate = -1;
		public AnimationHandler() 
		{
			int timesPerSecond = Integer.getInteger("com.topcoder.client.spectatorApp.scoreboard.model.RecalcPerSecond", 1);
			if (timesPerSecond < 1) timesPerSecond = 1;
			
			timePerUnit = CommonRoutines.getCurrentTimeUnit() / timesPerSecond;
		}
		
		public void animate(long now, long diff) {
			// Ignore if no time left
			if (phaseID != ContestConstants.CODING_PHASE || estimatedTimeLeft <= 0) {
				return;
			}

			// If our estimated time is greater than the official time
			// adjust it
			if (estimatedTimeLeft > officialTimeLeft) {
				estimatedTimeLeft = officialTimeLeft + diff;
				lastUpdate = estimatedTimeLeft;
			}
			
			// Offset the estimate by the difference
			estimatedTimeLeft -= diff;
			
			// Don't allow the estimate to get 1 second beyond the official time
			if (officialTimeLeft - CommonRoutines.getCurrentTimeUnit() > estimatedTimeLeft) {
				estimatedTimeLeft = officialTimeLeft - CommonRoutines.getCurrentTimeUnit();
			}
			
			// If estimated time is beyond the last update +- timePerUnit, mark last update as unknown
			if (Math.abs(estimatedTimeLeft - lastUpdate) > timePerUnit) {
				lastUpdate = -1;
			}
			
			// Recalc the problems
			if (estimatedTimeLeft <= lastUpdate || lastUpdate == -1) {
				recalcOpenProblems();
				lastUpdate = estimatedTimeLeft - timePerUnit;
			}
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

		public boolean equals(Object o) {
			return this == o;
		}
	}

	/** Handler for system test results */
	private class SystemResultHandler extends SystemTestResultsAdapter implements SystemTestResultsListener {
		public void showProblemResults(final SystemTestResultsEvent evt) {
			// Run in separate thread to prevent hold up of the message
			new Thread(new Runnable() {
				public void run() {
					// Make sure it's the system test phase
					if (PhaseTracker.getInstance().getPhaseID() != ContestConstants.SYSTEM_TESTING_PHASE) {
						cat.error("Ignoring system test results show - phase is NOT the system test phase");
						return;
					}
					
					// Get the index of the problem being tested
					int c = indexOfProblem(evt.getResultsID());
					if (c < 0) {
						cat.error("Unknown Problem ID: " + evt.getResultsID() + " - ignoring");
						return;
					}
					
					// Send the in progress messages
					for (int r = coders.size() - 1; r >= 0; r--)	fireInProgress(r, c);
					
					// Wait the delay
					try {
						Thread.sleep(evt.getDelay() * 1000);
					} catch (Throwable t) {
					}
					
					// Fire off the results
					for (int r = coders.size() - 1; r >= 0; r--)	fireResults(r, c);
					
					// Wait the delay
					try {
						Thread.sleep(evt.getDelay() * 1000);
					} catch (Throwable t) {
					}
					
					// Fire off the end contest
					fireEndContest(evt.getDelay());
				}
			}).start();
		}

		public void showCoderAllResults(final SystemTestResultsEvent evt) {
			// Run in separate thread to prevent hold up of the message
			new Thread(new Runnable() {
				public void run() {
					// Make sure it's the system test phase
					if (PhaseTracker.getInstance().getPhaseID() != ContestConstants.SYSTEM_TESTING_PHASE) {
						cat.error("Ignoring system test results show - phase is NOT the system test phase");
						return;
					}

					// Get a copy of all the coders in reverse placement order
					List<? extends CoderData> al = getPlacementTracker().getCodersByPlacementUnfiltered();
					Collections.reverse(al);
					
					// Go through them and kick off an event each time
					boolean pointsTested = false;
					for (CoderData data : al) {
						// Wait the delay
						if (pointsTested) {
							try {
								Thread.sleep(evt.getDelay() * 1000);
							} catch (Throwable t) {
							}
						}
						
						// Fire off the result
						pointsTested = fireCoderResult(new SystemTestResultsEvent(this, evt.getRoundID(), data.getCoderID(), evt.getDelay()));
					}
				}
			}).start();
		}

		public void showCoderResults(final SystemTestResultsEvent evt) {
			// Run in separate thread to prevent hold up of the message
			new Thread(new Runnable() {
				public void run() {
					fireCoderResult(evt);
				}
			}).start();
		}

		/** Fire off the results for a given coder */
		private boolean fireCoderResult(final SystemTestResultsEvent evt) {
			// Make sure it's the system test phase
			if (PhaseTracker.getInstance().getPhaseID() != ContestConstants.SYSTEM_TESTING_PHASE) {
				cat.error("Ignoring system test results show - phase is NOT the system test phase");
				return false;
			}
			
			// Get the coder identified in the id
			int r = indexOfCoder(evt.getResultsID());
			if (r < 0) {
				cat.error("Unknown Coder ID: " + evt.getResultsID() + " - ignoring");
				return false;
			}
			

			// Determine if there were points to test..
			boolean pointsToTest = getTotalScore(r) >= 0.0;
			
			// Send the in progress messages
			for (int c = problems.size() - 1; c >= 0; c--) {
				fireInProgress(r, c);
			}
			
			
			// Wait the delay (if there were any points that need testing
			if (pointsToTest) {
				try {
					Thread.sleep(evt.getDelay() * 1000);
				} catch (Throwable t) {
				}
			}
			
			// Fire off the results
			for (int c = problems.size() - 1; c >= 0; c--) fireResults(r, c);
			
			// Fire off the end contest
			fireEndContest(evt.getDelay());
			
			// Return whether we had points to test
			return pointsToTest;
		}
		
		/** Helper method to fire off the in progress message */
		private void fireInProgress(int r, int c) {
			if (problemResultCache[r][c] != null) {
				updateStatusMessage(problemResultCache[r][c], Constants.PROBLEM_SYSTEMTESTING);
			}
		}

		/** Helper method to fire off the results message */
		private void fireResults(int r, int c) {
			if (problemResultCache[r][c] != null) {
				// Set the result
				if (problemResultCache[r][c].getResult() == ProblemResultEvent.SUCCESSFUL) {
                                   
					updateStatus(r, c, Constants.PROBLEM_SYSTEMTESTED_PASSED, problemResultCache[r][c].getSourceCoder());
					// updatePointValue(r, c, (int)
					// (problemResultCache[r][c].getValue() * 100.0));
					 updatePointValue(r, c, (int) (problemResultCache[r][c].getValue() ));
				} else {
                                    
					updateStatus(r, c, Constants.PROBLEM_SYSTEMTESTED_FAILED, problemResultCache[r][c].getSourceCoder());
                                        updatePointValue(r, c, 0);
					
				}
			}
		}

		/** Helper method to fire end contest if all status's have been received */
		private void fireEndContest(int delay) {
			// Look through all the problems
			boolean found = false;
			for (int r = coders.size() - 1; r >= 0; r--) {
				for (int c = problems.size() - 1; c >= 0; c--) {
					// Get the probelm status
					int status = getProblemStatus(r, c);
					// See if the result is still pending.
					// 1) Problem wasn't opened
					if (status == Constants.PROBLEM_SUBMITTED || status == Constants.PROBLEM_CHALLENGE_FAILED) {
						found = true;
						break;
					}
				}
			}
			// If we found a non-finished problem - return;
			if (found) return;
			
			// YEAH - everything done - let's do end contest
			// Wait the delay
			try {
				Thread.sleep(delay * 1000);
			} catch (Throwable t) {
			}
			
			// Dispatch the end of contest message
			DispatchThread.getInstance().queueMessage(new PhaseChange(ContestConstants.CONTEST_COMPLETE_PHASE, 0));
		}
	}
}
