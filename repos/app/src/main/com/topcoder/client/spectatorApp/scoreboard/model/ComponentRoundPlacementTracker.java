package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.PhaseListener;
import com.topcoder.client.spectatorApp.event.ShowPlacementEvent;
import com.topcoder.client.spectatorApp.event.ShowPlacementListener;
import com.topcoder.client.spectatorApp.event.ShowTCSPlacementEvent;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;

public class ComponentRoundPlacementTracker implements CompetitionPlacementTracker<ComponentCoder> {
	
	/** Logging support */
	private static final Category cat = Category.getInstance(ComponentRoundPlacementTracker.class.getName());

	/** Related tracker */
	private final ComponentRound tracker;
	
	/** Handler for total point changes in tracker */
	private final TotalChangeHandler handler = new TotalChangeHandler();
	
	/** Support for those wishing to listen for placement changes */
	private final PlacementChangeSupport changeSupport = new PlacementChangeSupport();
	
	/** Handler for phase changes */
	private final PhaseHandler phaseHandler = new PhaseHandler();
	
	/** List of all the coders (sorted in coder id order) */
	private final List<ComponentCoder> coders;
	
	/** The associated total scores for the coder (same order as coders) */
	private final int[] scores;
	
	/** The associated placement for the coder (ie first coder [index 0] is placed 3rd */
	private final int[] coderPlacement;
	
	/** Coders in placement order (ie placement of 1 [index 0] is coderID blah blah */
	private final int[] placementOrder;
	
	/** The current phase */
//	private int currentPhase = Constants.PHASE_NONE;
	
	/** The places to show */
	private final boolean[] placesToShow;
	
	/** The listener for show messages */
	private final ShowPlacementHandler showHandler = new ShowPlacementHandler();
	
	/** Comparator used for comparing coders */
	private static Comparator<ComponentCoder> coderComparator = new Comparator<ComponentCoder>() {
		public int compare(ComponentCoder crd1, ComponentCoder crd2) {
			return crd1.getCoderID() - crd2.getCoderID();
		}
	};
	
	/** Create the tracker */
	ComponentRoundPlacementTracker(ComponentRound tracker)
	{
		this.tracker = tracker;
		tracker.addTotalChangeListener(handler);
		SpectatorEventProcessor.getInstance().addPhaseListener(phaseHandler);
//		currentPhase = PhaseTracker.getInstance().getPhaseID();

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
			int idx = tracker.indexOfCoder(coders.get(x).getCoderID());
			scores[x] = tracker.getTotalWager(idx);
			coderPlacement[x] = x;
			placementOrder[x] = x;
		}

		calculatePlacement();
		firePlacement(); // force a fire to notify everyone of changes
	}
	
	/** Dispose of resources */
	public void dispose()
	{
		tracker.removeTotalChangeListener(handler);
		SpectatorEventProcessor.getInstance().removePhaseListener(phaseHandler);
		SpectatorEventProcessor.getInstance().removeShowPlacementListener(showHandler);
	}
	
	/** The the placement for the given coder id */
	public synchronized int getPlacement(int coderID)
	{
		int idx = findCoder(coderID);
		if (idx < 0) {
			return -1;
		} else {
			// During the coding phase - don't bother with placement
			// if the score is being hidden
			if (scores[idx] < 0) {
				return -1;
			}
			return coderPlacement[idx] + 1;
		}
	}

	/** Determines if the specified coder id is tied with anyone */
	public synchronized boolean isTied(int coderID)
	{
		int idx = findCoder(coderID);
		if (idx < 0) {
			return false;
		} else {
			// During the coding phase - don't bother with placement
			// if the score is being hidden
			if (scores[idx] < 0) {
				return false;
			}
			
			if (idx > 0 && coderPlacement[idx] == coderPlacement[idx - 1]) return true;
			return coderPlacement[idx] == coderPlacement[idx - 1];
		}
	}

	/** Get the list of coders sorted in their placement order */
	public synchronized List<ComponentCoder> getCodersByPlacement()
	{
		List<ComponentCoder> list = new ArrayList<ComponentCoder>();
		for(int x = 0; x < placesToShow.length; x++) {
			if (placesToShow[x]) {
				list.add(coders.get(placementOrder[x]));
			}
		}
		return list;
	}
	
	public synchronized List<ComponentCoder> getCodersByPlacementUnfiltered()
	{
		List<ComponentCoder> list = new ArrayList<ComponentCoder>();
		for(int x = 0; x < placesToShow.length; x++) {
			list.add(coders.get(placementOrder[x]));
		}
		return list;
	}
	
	/** Get the placement change support */
	public PlacementChangeSupport getChangeSupport()
	{
		return changeSupport;
	}
	
	private int findCoder(int coderID)
	{
		return Collections.binarySearch(coders, new ComponentCoder(coderID, "", -1, -1), coderComparator);
	}
	
	private synchronized void calculatePlacement()
	{
		boolean changeHappened = calculateSimplePlacement();
		
		if (changeHappened) {
			if (cat.isDebugEnabled()) {
				cat.debug(">>> Sorting Result Begin");
				for (int x = 0; x < coders.size(); x++) {
					cat.debug(">>> " + coders.get(x).getHandle() + " (" + coders.get(x).getRank() + ") is in " + (coderPlacement[x]+1) + " with " + scores[x]);
				}
				cat.debug(">>> Sorting Result End");
			}
			firePlacement();
		}
	}
	
	/** Sorts by points, rating */
	private boolean calculateSimplePlacement() {
		
		// Descending...
		int[] tempScores = new int[scores.length];
		System.arraycopy(scores, 0, tempScores, 0, scores.length);
		boolean changeHappened = false;
		
		// Loop through all the 'places' 
		for (int place = 0; place < tempScores.length; place++) {
			// Make the coder at the given place the 'high' coder for now
			int newHigh = -1;
			
			// Loop through all the coders.
			for (int r = 0; r < tempScores.length; r++) {
				
				// Skip coders we already have done
				if (tempScores[r] == Integer.MIN_VALUE) continue;
				
				// If the coder has a high score or high highest component score, make them the new high
				if (newHigh == -1 || (tempScores[r] > tempScores[newHigh]
				|| (tempScores[r] == tempScores[newHigh] 
				&& tracker.getHighestScore(r) > tracker.getHighestScore(newHigh)))) {
					newHigh = r;
				} 
			}
			
			// If the coder placement changed, update it and mark the sort as a change happened
			if (coderPlacement[newHigh] != place || placementOrder[place] != newHigh) {
				placementOrder[place] = newHigh;
				coderPlacement[newHigh] = place;
				changeHappened = true;
			}
			
			// Mark the score as processed..
			tempScores[newHigh] = Integer.MIN_VALUE;
		}		
		
		// Reprocess to mark ties
		for (int r = 1; r < placementOrder.length; r++) {
			// Tied?
			if (scores[placementOrder[r]] == scores[placementOrder[r-1]]) {
				if (coderPlacement[placementOrder[r]] != coderPlacement[placementOrder[r-1]]) {
					changeHappened = true;
					coderPlacement[placementOrder[r]] = coderPlacement[placementOrder[r-1]];
				}
			}
		}
		
		// Return the results
		return changeHappened;
	}
	
	private void firePlacement()
	{
		changeSupport.fireUpdatePlacement(new PlacementChangeEvent(ComponentRoundPlacementTracker.this));
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
	
	/** Class handling the phase change messages */
	private class PhaseHandler implements PhaseListener {
		public void unknown(PhaseEvent evt, int phaseID) {
//			currentPhase = phaseID;
			firePlacement();
		}

		public void contestInfo(PhaseEvent evt) {
//			currentPhase = ContestConstants.SPECTATOR_CONTESTINFO;
			firePlacement();
		}

		public void announcements(PhaseEvent evt) {
//			currentPhase = ContestConstants.SPECTATOR_ANNOUNCEMENTS;
			firePlacement();
		}

		public void coding(PhaseEvent evt) {
//			currentPhase = ContestConstants.CODING_PHASE;
			firePlacement();
		}

		public void intermission(PhaseEvent evt) {
//			currentPhase = ContestConstants.INTERMISSION_PHASE;
			firePlacement();
		}

		public void challenge(PhaseEvent evt) {
//			currentPhase = ContestConstants.CHALLENGE_PHASE;
			firePlacement();
		}

		public void systemTesting(PhaseEvent evt) {
//			currentPhase = ContestConstants.SYSTEM_TESTING_PHASE;
			firePlacement();
		}

		public void endContest(PhaseEvent evt) {
//			currentPhase = ContestConstants.CONTEST_COMPLETE_PHASE;
			calculatePlacement();
			firePlacement();
		}
		
		public void voting(PhaseEvent evt) {
//			currentPhase = ContestConstants.VOTING_PHASE;
			calculatePlacement();
			firePlacement();
		}
		
		public void votingTie(PhaseEvent evt) {
//			currentPhase = ContestConstants.TIE_BREAKING_VOTING_PHASE;
			calculatePlacement();
			firePlacement();
		}

		public void componentAppeals(PhaseEvent evt) {
//			currentPhase = ContestConstants.COMPONENT_CONTEST_APPEALS;
			firePlacement();
		}

		public void componentResults(PhaseEvent evt) {
//			currentPhase = ContestConstants.COMPONENT_CONTEST_RESULTS;
			firePlacement();
		}

		public void componentEndContest(PhaseEvent evt) {
//			currentPhase = ContestConstants.COMPONENT_CONTEST_END;
			calculatePlacement();
			firePlacement();
		}
	}

	private class ShowPlacementHandler implements ShowPlacementListener
	{
		public void showPlacements(ShowPlacementEvent evt) {
		}	
		
		public void showTCSPlacements(ShowTCSPlacementEvent evt) {
			synchronized(ComponentRoundPlacementTracker.this) {
				Arrays.fill(placesToShow, false);
				for (int x : evt.getShowPlacements()) {
					if (x > 0 && x <= placesToShow.length) {
						placesToShow[x-1] = true;
					}
				}
			}
			firePlacement();
		}
	}
}
