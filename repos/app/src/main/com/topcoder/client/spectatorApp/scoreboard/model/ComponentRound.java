package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.event.ComponentContestAdapter;
import com.topcoder.client.spectatorApp.event.PhaseAdapter;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.ShowComponentResultsEvent;
import com.topcoder.client.spectatorApp.netClient.DispatchThread;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;

public class ComponentRound implements CompetitionTracker<ComponentCoder> {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(ComponentRound.class.getName());
	
	private final int contestID;
	private final int roundID;
	private final List<ComponentCoder> coders = new ArrayList<ComponentCoder>();
	private final List<ComponentContest> components = new ArrayList<ComponentContest>();
	
	private final PointValueChangeSupport pointValueChangeHandler = new PointValueChangeSupport();
	private final TotalChangeSupport totalChangeHandler = new TotalChangeSupport();
	
	private final ContestTotalChangeListener totalChangeListener = new ContestTotalChangeListener();
	private final PlacementHandler placementHandler = new PlacementHandler();
	private final PhaseHandler phaseHandler = new PhaseHandler();
	private final ShowResultsHandler resultsHandler = new ShowResultsHandler();
	
	private final Map<Integer, Integer> scores = new HashMap<Integer, Integer>();
	
	private ComponentRoundPlacementTracker placementTracker = null;
	
	public ComponentRound(int contestID, int roundID)
	{
		this.contestID = contestID;
		this.roundID = roundID;
		
		SpectatorEventProcessor.getInstance().addPhaseListener(phaseHandler);
		SpectatorEventProcessor.getInstance().addComponentContestListener(resultsHandler);
	}

	public int getContestID() {
		return contestID;
	}
	
	public int getRoundID() {
		return roundID;
	}
	
	private void updateTotalScore(int coderID, int score) {
		final int idx = indexOfCoder(coderID);
		if (idx < 0) {
			cat.warn(">>> Unknown coderid: " + coderID);
			return;
		}
		
		final Integer oldScore = scores.get(coderID);
		if (oldScore == null || oldScore != score) {
			scores.put(coderID, score);
			fireTotalChange(idx, score);
		}
	}
	
	public void addComponentContest(final ComponentContest contest) {
		// Create the list of coders if not found
		if (coders.size() == 0) {
			coders.addAll(contest.getCoders());
			placementTracker = new ComponentRoundPlacementTracker(this);
		}
		
		// Validate the new contest has the same coders
		if (!coders.containsAll(contest.getCoders())) {
			throw new IllegalArgumentException("ComponentContest (" + contest.getComponentID() + ") contains a different set of coders [" + coders + "] versus [" + contest.getCoders() + "]");
		}

		// Add the contest
		// See if we've already got it
		int idx = -1;
 		for(int x = 0; x < components.size(); x++) {
			if (components.get(x).getComponentID() == contest.getComponentID()) {
				idx = x;
				break;
			}
		}
		
		if (idx == -1) {
			components.add(contest);
			idx = components.size() - 1;
		} else {
			components.get(idx).removeTotalChangeListener(totalChangeListener);
			components.get(idx).getPlacementTracker().getChangeSupport().removeListener(placementHandler);
			components.set(idx, contest);
		}
		
		// Add a listener for changes
		components.get(idx).addTotalChangeListener(totalChangeListener);
		contest.getPlacementTracker().getChangeSupport().addListener(placementHandler);
		
		// Fire off change notifications for the new contest
		for(int x = 0; x < coders.size(); x++) {
			final int coderID = coders.get(x).getCoderID();
			firePointValueChange(x, idx, contest.getAverageScore(contest.indexOfCoder(coderID)));
		}
		
		recalculateTotals();
	}
	
	public int getCoderCount() {
		return coders.size();
	}
	
	public int getComponentCount() {
		return components.size();
	}
	
	public int getHighestScore(int coderIdx) {
		if (!isCoderIdxValid(coderIdx)) {
			return 0;
		}
		
		int max = -1;
		for (ComponentContest contest : components) {
			if (!contest.isHidingScores()) {
				max = Math.max(max, contest.getAverageScore(coderIdx));
			}
		}
		return max;
	}
	
	private void recalculateTotals() {
		// Recalculate total score for each coder
		for(int x = 0; x < coders.size(); x++) {
			ComponentCoder coder = coders.get(x);
	
			int totalScore = 0;
			boolean found = false;
			for(ComponentContest component : components) {
				if (!component.isHidingScores()) {
					final ComponentCoder coderWager = component.getCoder(component.indexOfCoder(coder.getCoderID()));
					final int placement = component.getPlacementTracker().getPlacement(coder.getCoderID());
					if (placement > 0) {
						found = true;
						int wager = coderWager.getWager();
						totalScore += wager / placement;
					}
//					cat.warn(">>> Coder: " + coder.getHandle() + ": " + wager + " / " + placement + " = " + (wager / placement) + "  (" + totalScore  + ")");
				}
			}
			if (!found) {
				updateTotalScore(coder.getCoderID(), -1);
			} else {
				updateTotalScore(coder.getCoderID(), totalScore);
			}
		}
	}

	public int getScore(int coderIdx, int componentIdx) {
		if (!isCoderIdxValid(coderIdx) || !isComponentIdxValid(componentIdx)) {
			return 0;
		} else {
			ComponentContest cc = components.get(componentIdx);
			int ccCoderIdx = cc.indexOfCoder(coders.get(coderIdx).getCoderID());
			return cc.getAverageScore(ccCoderIdx);
		}
	}
	
	public int getTotalWager(int coderIdx) {
		if (!isCoderIdxValid(coderIdx)) {
			return 0;
		}

		int coderID = coders.get(coderIdx).getCoderID();
		
		if (scores.get(coderID) == null) {
			return 0;
		}
		
		return scores.get(coderID);
	}
	
	public ComponentCoder getCoder(int coderIdx) {
		if (!isCoderIdxValid(coderIdx)) return null;
		return coders.get(coderIdx);
	}

	public boolean isWinningCoder(int coderIdx) {
		if (!isCoderIdxValid(coderIdx)) return false;
		
		int highestScore = -1;
		for(int x = 0; x < coders.size(); x++) {
//			cat.error(">>> " + x + ":" + getTotalWager(x) + ":" + highestScore);
			highestScore = Math.max(highestScore, getTotalWager(x));
		}
//		cat.error(">>>>>>>>>>> CoderIdx: " + coderIdx + "  " + highestScore + "=" + getTotalWager(coderIdx));
		return getTotalWager(coderIdx) == highestScore;
	}
	
	public ComponentContest getComponent(int componentIdx) {
		if (!isComponentIdxValid(componentIdx)) return null;
		return components.get(componentIdx);
	}
	
	public ComponentContest getComponentID(long componentID) {
		for (ComponentContest contest : components) {
			if (contest.getComponentID() == componentID) {
				return contest;
			}
		}
		return null;
	}
	
	public List<ComponentCoder> getCoders() {
		return new ArrayList<ComponentCoder>(coders);
	}
	
	public List<ComponentContest> getComponents() {
		return new ArrayList<ComponentContest>(components);
	}
	
	public int indexOfCoder(int coderID)	{
		for(int x = 0; x < coders.size(); x++) {
			if (coders.get(x).getCoderID() == coderID) return x;
		}
		cat.warn("Unknown coderID: " + coderID);
		return -1;
	}
	
	public int indexOfComponent(long componentID)	{
		for(int x = 0; x < components.size(); x++) {
			if (components.get(x).getComponentID() == componentID) return x;
		}
		cat.warn("Unknown ComponentID: " + componentID);
		return -1;
	}

	public ComponentRoundPlacementTracker getPlacementTracker() {
		return placementTracker;
	}
	
	public void addPointValueListener(PointValueChangeListener listener) {
		pointValueChangeHandler.addPointValueChangeListener(listener);
	}
	
	public void removePointValueListener(PointValueChangeListener listener) {
		pointValueChangeHandler.removePointValueChangeListener(listener);
	}
	
	private void firePointValueChange(int coderIdx, int componentIdx, int newScore) {
		pointValueChangeHandler.fireUpdatePointValue(new PointValueChangeEvent(this, coderIdx, componentIdx, newScore));
	}
	
	public void addTotalChangeListener(TotalChangeListener listener) {
		totalChangeHandler.addTotalChangeListener(listener);
	}
	
	public void removeTotalChangeListener(TotalChangeListener listener) {
		totalChangeHandler.removeTotalChangeListener(listener);
	}
	
	private void fireTotalChange(int coderIdx, int newValue) {
		totalChangeHandler.fireUpdateTotal(new TotalChangeEvent(this, coderIdx, newValue));
	}
	
	private boolean isCoderIdxValid(int coderIdx) {
		return coderIdx >=0 && coderIdx < coders.size();
	}
	
	private boolean isComponentIdxValid(int componentIdx) {
		return componentIdx >=0 && componentIdx < components.size();
	}

	public void dispose()
	{
		SpectatorEventProcessor.getInstance().removePhaseListener(phaseHandler);
		SpectatorEventProcessor.getInstance().removeComponentContestListener(resultsHandler);
		placementTracker.dispose();
		for (ComponentContest contest : components) {
			contest.removeTotalChangeListener(totalChangeListener);
			contest.getPlacementTracker().getChangeSupport().removeListener(placementHandler);
		}
	}
	
	private class PlacementHandler implements PlacementChangeListener {
		public void placementChanged(PlacementChangeEvent evt) {
			recalculateTotals();
		}
	}
	
	private class ContestTotalChangeListener implements TotalChangeListener {
		public void updateTotal(TotalChangeEvent evt) {
			ComponentContest contest = (ComponentContest) evt.getSource();
			ComponentCoder coder = contest.getCoder(evt.getRow());
			
			int x = indexOfCoder(coder.getCoderID());
			int y = indexOfComponent(contest.getComponentID());
			firePointValueChange(x, y, evt.getNewValue());
		}
	}
	
	private class ShowResultsHandler extends ComponentContestAdapter {
		@Override
		public void showComponentResults(final ShowComponentResultsEvent event) {
			if (event.getContestID() != contestID || event.getRoundID() != roundID) return;
			
			new Thread(new Runnable() {
				public void run() {
					for (ComponentContest contest : components) {
						try {
							Thread.sleep(event.getDelay() * 1000);
						} catch (Throwable t) {
						}
						contest.setHidingScores(false);
					}
					
					try {
						Thread.sleep(event.getDelay() * 1000);
					} catch (Throwable t) {
					}
					DispatchThread.getInstance().queueMessage(new PhaseChange(ContestConstants.COMPONENT_CONTEST_END, 0));
				}
			}).start();
		}
	}
	
	private class PhaseHandler extends PhaseAdapter {
		@Override
		public void componentResults(PhaseEvent evt) {
			for (ComponentContest contest : components) {
				contest.setHidingScores(true);
			}
		}
		@Override
		public void componentEndContest(PhaseEvent evt) {
			for (ComponentContest contest : components) {
				contest.setHidingScores(false);
			}
		}
	}
}
