package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.PhaseListener;
import com.topcoder.client.spectatorApp.event.ShowPlacementEvent;
import com.topcoder.client.spectatorApp.event.ShowPlacementListener;
import com.topcoder.client.spectatorApp.event.ShowTCSPlacementEvent;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;

public class ScoreboardPlacementTracker implements CompetitionPlacementTracker<CoderRoomData> {
	
	/** Logging support */
	private static final Category cat = Category.getInstance(ScoreboardPlacementTracker.class.getName());

	/** Related tracker */
	private final ScoreboardPointTracker tracker;
	
	/** Handler for total point changes in tracker */
	private final TotalChangeHandler handler = new TotalChangeHandler();
	
	/** Support for those wishing to listen for placement changes */
	private final PlacementChangeSupport changeSupport = new PlacementChangeSupport();
	
	/** Handler for phase changes */
	private final PhaseHandler phaseHandler = new PhaseHandler();
	
	/** List of all the coders (sorted in coder id order) */
	private final List<CoderRoomData> coders;
	
	/** The associated total scores for the coder (same order as coders) */
	private final int[] scores;
	
	/** The associated placement for the coder (ie first coder [index 0] is placed 3rd */
	private final int[] coderPlacement;
	
	/** Coders in placement order (ie placement of 1 [index 0] is coderID blah blah */
	private final int[] placementOrder;
	
	/** The current phase */
	private int currentPhase = Constants.PHASE_NONE;
	
	/** The places to show */
	private final boolean[] placesToShow;
	
	/** The listener for show messages */
	private final ShowPlacementHandler showHandler = new ShowPlacementHandler();
	
	/** The listener for status change messages */
	private final StatusChangeHandler statusChangeHandler = new StatusChangeHandler();
	
	private int lastSystemTestCoderId = -1;
	
	/** Comparator used for comparing coders */
	private static Comparator<CoderRoomData> coderComparator = new Comparator<CoderRoomData>() {
		public int compare(CoderRoomData crd1, CoderRoomData crd2) {
			return crd1.getCoderID() - crd2.getCoderID();
		}
	};
	
	/** Create the tracker */
	ScoreboardPlacementTracker(ScoreboardPointTracker tracker)
	{
		this.tracker = tracker;
		tracker.addTotalChangeListener(handler);
		tracker.addStatusChangeListener(statusChangeHandler);
		SpectatorEventProcessor.getInstance().addPhaseListener(phaseHandler);
		currentPhase = PhaseTracker.getInstance().getPhaseID();

		SpectatorEventProcessor.getInstance().addShowPlacementListener(showHandler);
		
		coders = tracker.getCoders();
		Collections.sort(coders, coderComparator);
		
		int coderMax = coders.size();
		scores = new int[coderMax];
		coderPlacement = new int[coderMax];
		placementOrder = new int[coderMax];
		
		placesToShow = new boolean[coderMax];
		Arrays.fill(placesToShow, true);
		
		for(int x = 0; x < coderMax; x++) {
			scores[x] = tracker.getTotalScore(coders.get(x).getHandle());
			coderPlacement[x] = 0;
			placementOrder[x] = x;
		}

		calculatePlacement();
		firePlacement(); // force a fire to notify everyone of changes
	}
	
	/* (non-Javadoc)
	 * @see com.topcoder.client.spectatorApp.scoreboard.model.CompetitionPlacementTracker#dispose()
	 */
	public void dispose()
	{
		tracker.removeTotalChangeListener(handler);
		tracker.removeStatusChangeListener(statusChangeHandler);
		SpectatorEventProcessor.getInstance().removePhaseListener(phaseHandler);
		SpectatorEventProcessor.getInstance().removeShowPlacementListener(showHandler);
	}
	
	/* (non-Javadoc)
	 * @see com.topcoder.client.spectatorApp.scoreboard.model.CompetitionPlacementTracker#getPlacement(int)
	 */
	public synchronized int getPlacement(int coderID)
	{
		int idx = findCoder(coderID);
		if (idx < 0) {
			return -1;
		} else {
			// During the coding phase - don't bother with placement
			// if the score is 0
                        //commented - rfairfax
			/*if (scores[idx] == 0 && currentPhase == ContestConstants.CODING_PHASE) {
				return -1;
			}*/
			return coderPlacement[idx] + 1;
		}
	}

	/** Determines if the specified coder id is tied with anyone */
	public synchronized boolean isTied(int coderID)
	{
		// No ties right now
		return false;
	}

	/* (non-Javadoc)
	 * @see com.topcoder.client.spectatorApp.scoreboard.model.CompetitionPlacementTracker#getCodersByPlacement()
	 */
	public synchronized List<CoderRoomData> getCodersByPlacement()
	{
		List<CoderRoomData> list = new ArrayList<CoderRoomData>();
		for(int x = 0; x < placesToShow.length; x++) {
			if (placesToShow[x]) {
				list.add(coders.get(placementOrder[x]));
			}
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.topcoder.client.spectatorApp.scoreboard.model.CompetitionPlacementTracker#getCodersByPlacementUnfiltered()
	 */
	public synchronized List<CoderRoomData> getCodersByPlacementUnfiltered()
	{
		List<CoderRoomData> list = new ArrayList<CoderRoomData>();
		for(int x = 0; x < placesToShow.length; x++) {
			list.add(coders.get(placementOrder[x]));
		}
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.topcoder.client.spectatorApp.scoreboard.model.CompetitionPlacementTracker#getChangeSupport()
	 */
	public PlacementChangeSupport getChangeSupport()
	{
		return changeSupport;
	}
	
	private int findCoder(int coderID)
	{
		return Collections.binarySearch(coders, new CoderRoomData(coderID, "", -1, -1), coderComparator);
	}
	
	private synchronized void calculatePlacement()
	{
		boolean changeHappened;
		if (currentPhase == ContestConstants.SYSTEM_TESTING_PHASE) {
			changeHappened = calculateSystemTestPlacement();
		} else {
			changeHappened = calculateSimplePlacement();
		}
		
		if (changeHappened) {
			if (cat.isInfoEnabled()) {
				final StringBuffer buf = new StringBuffer(2000);
				for (int x = 0; x < coders.size(); x++) {
					if (x != 0) {
						buf.append(" --> ");
					}
					buf.append(coders.get(x).getHandle() + " (" + coders.get(x).getRank() + ") is in " + (coderPlacement[x]+1) + " with " + scores[x] + " - status: " + getProblemStatus(coders.get(x).getCoderID()));
				}
				cat.info(buf.toString());
			}
			firePlacement();
		}
	}
	
	/** 
	 * Sorts by overall problem status, points, rating 
	 */
	private boolean calculateSystemTestPlacement()
	{
		// Descending...
		int[] tempScores = new int[scores.length];
		System.arraycopy(scores, 0, tempScores, 0, scores.length);
		
		boolean changeHappened = false;
		for (int place = 0; place < tempScores.length; place++) {
			
			// State
			int newHigh = -1;
			CoderRoomData highCoder = null;
			int highProblemStatus = -1;
			
			// See if any are higher
			for (int r = 0; r < tempScores.length; r++) {
				// Don't bother with already assigned stuff
				if (tempScores[r] == Integer.MIN_VALUE) continue;
				
				// Get the coder for the row and their status
				CoderRoomData coder = coders.get(r);
				int problemStatus = getProblemStatus(coder.getCoderID());
				
				// Ensure 0.00 or less people stay at the bottom
				if (tempScores[r] <= 0) {
					problemStatus = 0; // bottom of the deck
				}
				
				// If a higher status or higher score (same status) or higher rank (same status/score), make them the new high coder
				if (newHigh == -1 || (problemStatus > highProblemStatus
				   || (problemStatus == highProblemStatus
				       && (tempScores[r] > tempScores[newHigh] 
				           || (tempScores[r] == tempScores[newHigh] && coder.getRank() > highCoder.getRank()))))) {
					newHigh = r;
					highCoder = coders.get(r);
					highProblemStatus = getProblemStatus(highCoder.getCoderID());
				} 
			}
			
			// If something changed, update and mark this as a change
			if (coderPlacement[newHigh] != place || placementOrder[place] != newHigh) {
				placementOrder[place] = newHigh;
				coderPlacement[newHigh] = place;
				changeHappened = true;
			}
			
			// Mark score as having been processed
			tempScores[newHigh] = Integer.MIN_VALUE;
		}
		
		// Return the status of the sort, FIX ME!
		return changeHappened;
	}
	
	/** Part of the sort - we sort unknown results on top, testing in the middle, and known to the bottom */ 
	private int getProblemStatus(int coderID) {
		// statuses in 
		int row = tracker.indexOfCoder(coderID);
		if (row < 0) return 0;
		
		if (coderID == lastSystemTestCoderId) {
			return 3;
		}
		
		if (tracker.isCoderSystemTestDone(row)) {
			return 1;
		}
                
		if (tracker.isCoderSystemTesting(row)) {
			lastSystemTestCoderId = coderID;
			return 2;
		}
		return 4;
	}
	
	/** Sorts by points, rating */
	private boolean calculateSimplePlacement() {
		
		// Descending...
		int[] tempScores = new int[scores.length];
		System.arraycopy(scores, 0, tempScores, 0, scores.length);
		boolean changeHappened = false;
		
                int oldScore = Integer.MAX_VALUE;
                int oldPlace = 0;
		// Loop through all the 'places' 
		for (int place = 0; place < tempScores.length; place++) {
			// Make the coder at the given place the 'high' coder for now
			int newHigh = -1;
			CoderRoomData highCoder = null;
			
                        int score = 0;
			// Loop through all the coders.
			for (int r = 0; r < tempScores.length; r++) {
				
				// Skip coders we already have done
				if (tempScores[r] == Integer.MIN_VALUE) continue;
				
				// If the coder has a high score or high rank (if score is equal), make them the new high
				if (newHigh == -1 || (tempScores[r] > tempScores[newHigh] 
				|| (tempScores[r] == tempScores[newHigh] 
				&& coders.get(r).getRank() > highCoder.getRank()))) {
					newHigh = r;
					highCoder = coders.get(r);
                                        score = tempScores[r];
				} 
			}
                        
                        int pl = 0;
                        if(score == oldScore) {
                            pl = oldPlace;
                        } else {
                            pl = place;
                            oldPlace = pl;
                            oldScore = score;
                        }
                        
			// If the coder placement changed, update it and mark the sort as a change happened
			if (coderPlacement[newHigh] != pl || placementOrder[place] != newHigh) {
				placementOrder[place] = newHigh;
				coderPlacement[newHigh] = pl;
				changeHappened = true;
			}
			
			// Mark the score as processed..
			tempScores[newHigh] = Integer.MIN_VALUE;
		}		
		
		// Return the results
		return changeHappened;
	}
	
	private void firePlacement()
	{
		changeSupport.fireUpdatePlacement(new PlacementChangeEvent(ScoreboardPlacementTracker.this));
	}
	
	private class TotalChangeHandler implements TotalChangeListener
	{
		public void updateTotal(TotalChangeEvent evt) {
			int coderID = tracker.getCoder(evt.getRow()).getCoderID();
			int idx = findCoder(coderID);
			if (idx < 0) {
				cat.error(">>> unknown coder id: " + coderID);
			} else {
				scores[idx] = evt.getNewValue();
				calculatePlacement();
			}
		}
	}
	
	private class StatusChangeHandler implements StatusChangeListener {
		public void updateStatus(StatusChangeEvent evt) {
			// Only bother with status changes in the system test phase
			// (the only time the status matters to the sort)
			if (currentPhase == ContestConstants.SYSTEM_TESTING_PHASE) {
				calculatePlacement();
			}
		}
	}
	
	/** Class handling the phase change messages */
	private class PhaseHandler implements PhaseListener {
		public void unknown(PhaseEvent evt, int phaseID) {
			currentPhase = phaseID;
			firePlacement();
		}

		public void contestInfo(PhaseEvent evt) {
			currentPhase = ContestConstants.SPECTATOR_CONTESTINFO;
			firePlacement();
		}

		public void announcements(PhaseEvent evt) {
			currentPhase = ContestConstants.SPECTATOR_ANNOUNCEMENTS;
			firePlacement();
		}

		public void coding(PhaseEvent evt) {
			currentPhase = ContestConstants.CODING_PHASE;
			firePlacement();
		}

		public void intermission(PhaseEvent evt) {
			currentPhase = ContestConstants.INTERMISSION_PHASE;
			firePlacement();
		}

		public void challenge(PhaseEvent evt) {
			currentPhase = ContestConstants.CHALLENGE_PHASE;
			firePlacement();
		}

		public void systemTesting(PhaseEvent evt) {
			currentPhase = ContestConstants.SYSTEM_TESTING_PHASE;
			firePlacement();
		}

		public void endContest(PhaseEvent evt) {
			currentPhase = ContestConstants.CONTEST_COMPLETE_PHASE;
			lastSystemTestCoderId = -1;
			calculatePlacement();
			firePlacement();
		}
		
		public void voting(PhaseEvent evt) {
			currentPhase = ContestConstants.VOTING_PHASE;
			lastSystemTestCoderId = -1;
			calculatePlacement();
			firePlacement();
		}
		
		public void votingTie(PhaseEvent evt) {
			currentPhase = ContestConstants.TIE_BREAKING_VOTING_PHASE;
			lastSystemTestCoderId = -1;
			calculatePlacement();
			firePlacement();
		}

		public void componentAppeals(PhaseEvent evt) {
			currentPhase = ContestConstants.COMPONENT_CONTEST_APPEALS;
			firePlacement();
		}

		public void componentResults(PhaseEvent evt) {
			currentPhase = ContestConstants.COMPONENT_CONTEST_RESULTS;
			firePlacement();
		}

		public void componentEndContest(PhaseEvent evt) {
			currentPhase = ContestConstants.COMPONENT_CONTEST_END;
			calculatePlacement();
			firePlacement();
		}
	}

	private class ShowPlacementHandler implements ShowPlacementListener
	{
		public void showPlacements(ShowPlacementEvent evt) {
			synchronized(ScoreboardPlacementTracker.this) {
				Arrays.fill(placesToShow, false);
				for (int x : evt.getShowPlacements()) {
					if (x > 0 && x <= placesToShow.length) {
						placesToShow[x-1] = true;
					}
				}
			}
			firePlacement();
		}	
                public void showTCSPlacements(ShowTCSPlacementEvent evt) {
                    
                }
	}
}
