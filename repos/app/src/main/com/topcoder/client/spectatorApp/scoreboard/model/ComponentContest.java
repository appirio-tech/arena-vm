package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.Constants.AppealStatus;
import com.topcoder.client.spectatorApp.controller.GUIController;
import com.topcoder.client.spectatorApp.event.ComponentAppealEvent;
import com.topcoder.client.spectatorApp.event.ComponentContestAdapter;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.PhaseListener;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;
import com.topcoder.shared.netCommon.messages.spectator.ComponentData;

public class ComponentContest implements CompetitionTracker<ComponentCoder> {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(ComponentContest.class.getName());
	
	private final long trumpTime = Long.getLong("com.topcoder.client.spectatorApp.scoreboard.model.trumpTime", 5000);
	
	private final int contestID;
	private final int roundID;
	private final ComponentData componentData;
	private final ComponentCoder[] coders;
	private final CoderData[] reviewers;
	
	private final int[][] scores;  // scores[coderID][reviewerID]
	private final PointDirection[] totalPointDirection; // array[coderid]
	
	private final Map<Long, AppealStatusTime>[][] appeals;  // appeals[coderID][reviewerID]
	
	private boolean hideScores = false;
	
	/** Handler for component contest definitions */
	private final ComponentContestHandler contestHandler = new ComponentContestHandler();

	private final PointValueChangeSupport pointValueChangeHandler = new PointValueChangeSupport();
	private final AppealChangeSupport appealChangeHandler = new AppealChangeSupport();
	private final TotalChangeSupport totalChangeHandler = new TotalChangeSupport();
	private final PhaseHandler phaseHandler = new PhaseHandler();
	
	private final ComponentContestPlacementTracker placementTracker;
	
	public ComponentContest(int contestID, int roundID, ComponentData data, List<ComponentCoder> coders, List<CoderData> reviewers)
	{
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentData = data;
		
		int idx = 0;
		this.coders = new ComponentCoder[coders.size()];
		this.totalPointDirection = new PointDirection[coders.size()];
		for(Iterator<ComponentCoder> itr = coders.iterator(); itr.hasNext(); ) {
			this.totalPointDirection[idx] = PointDirection.NoChange;
			this.coders[idx++] = itr.next();
		}
		
		idx = 0;
		this.reviewers = new CoderData[reviewers.size()];
		for(Iterator<CoderData> itr = reviewers.iterator(); itr.hasNext(); ) {
			this.reviewers[idx++] = itr.next();
		}
		
		scores = new int[coders.size()][reviewers.size()];
		
		appeals = new HashMap[coders.size()][reviewers.size()];
		for(int x = 0; x < coders.size(); x++) {
			for(int y = 0; y < reviewers.size(); y++) {
				appeals[x][y] = new HashMap<Long, AppealStatusTime>();
			}
		}
		
		placementTracker = new ComponentContestPlacementTracker(this);
		SpectatorEventProcessor.getInstance().addComponentContestListener(contestHandler);
		SpectatorEventProcessor.getInstance().addPhaseListener(phaseHandler);
	}

	public int getContestID() {
		return contestID;
	}
	
	public int getRoundID() {
		return roundID;
	}
	
	public long getComponentID() {
		return componentData.getComponentID();
	}
	
	public String getComponentName() {
		return componentData.getName();
	}
	
	public int getCoderCount() {
		return coders.length;
	}
	
	public int getReviewerCount() {
		return reviewers.length;
	}
	
	public void setHidingScores(boolean hideScores) {
		if (this.hideScores != hideScores) {
			this.hideScores = hideScores;
			
			GUIController.getInstance().enablePainting(false);
			for (int x = 0; x < coders.length; x++) {
				for(int y = 0; y < reviewers.length; y++) {
					firePointValueChange(x, y, getScore(x, y));
				}
				fireTotalChange(x, getAverageScore(x));
			}
			GUIController.getInstance().enablePainting(true);
		}
	}
	
	public boolean isHidingScores() {
		return hideScores;
	}
	
	public int getHighestScore() {
//		if (hideScores) {
//			return -1;
//		}
		
		int highestScore = 0;
		for(int x = 0; x < coders.length; x++) {
			highestScore = Math.max(highestScore, getAverageScore(x));
		}
		return highestScore;
	}
	public int getScore(int coderIdx, int reviewerIdx) {
		if (hideScores) {
			return -1;
		}
		
		if (!isCoderIdxValid(coderIdx) || !isReviewerIdxValid(reviewerIdx)) {
			return 0;
		} else {
			return scores[coderIdx][reviewerIdx];
		}
	}
	
	public int getAverageScore(int coderIdx) {
		if (hideScores) {
			return -1;
		}
		
		if (!isCoderIdxValid(coderIdx)) {
			return 0;
		}
		
		int totalScore = getTotalScore(coderIdx);
		if (totalScore == 0) {
			return 0;
		} else {
			return (int) Math.round((double) totalScore / reviewers.length);
		}
	}
	
	public int getTotalScore(int coderIdx) {
		if (hideScores) {
			return -1;
		}
		
		if (!isCoderIdxValid(coderIdx)) {
			return 0;
		}
		
		int totalScore = 0;
		for(int y = 0; y < reviewers.length; y++) {
			totalScore += scores[coderIdx][y];
		}
		return totalScore;
	}
        
        private int getAppealCount(int coderIdx, int reviewerIdx,AppealStatus status) {
            int count = 0;
            
            Map<Long, AppealStatusTime> statuses = appeals[coderIdx][reviewerIdx];
            
            for(Iterator<AppealStatusTime> itr = statuses.values().iterator(); itr.hasNext(); ) {
                    final AppealStatusTime entry = itr.next();

                    final AppealStatus entryStatus = entry.status;

                    if(entryStatus == status)
                        count++;
            }

            return count;
        }
        
        public int getPendingAppealCount(int coderIdx, int reviewerIdx) {
            return getAppealCount(coderIdx, reviewerIdx, AppealStatus.Pending);
        }
        
        public int getFailedAppealCount(int coderIdx, int reviewerIdx) {
            return getAppealCount(coderIdx, reviewerIdx, AppealStatus.Failed);
        }
        
        public int getSuccessfulAppealCount(int coderIdx, int reviewerIdx) {
            return getAppealCount(coderIdx, reviewerIdx, AppealStatus.Successful);
        }
	
	public Constants.AppealStatus getAppealStatus(int coderIdx, int reviewerIdx) {
		if (hideScores) {
			return AppealStatus.None;
		}
		
		if (!isCoderIdxValid(coderIdx) || !isReviewerIdxValid(reviewerIdx)) {
			return AppealStatus.None;
		}
		
		Map<Long, AppealStatusTime> statuses = appeals[coderIdx][reviewerIdx];
		if (statuses == null || statuses.isEmpty()) {
			return AppealStatus.None;
		}

		long currentStatusTime = 0;
		AppealStatus currentStatus = AppealStatus.None;
		
		long lastResultTime = 0;
		AppealStatus lastResultStatus = AppealStatus.None;
		
		boolean pendingFound = false;
		
		for(Iterator<AppealStatusTime> itr = statuses.values().iterator(); itr.hasNext(); ) {
			final AppealStatusTime entry = itr.next();
			
			final long time = entry.statusTime;
			final AppealStatus status = entry.status;
			
			if (time > currentStatusTime) {
				currentStatusTime = time;
				currentStatus = status;
			}
		
			if (status == AppealStatus.Failed || status == AppealStatus.Successful) {
				if (time > lastResultTime) {
					lastResultTime = time;
					lastResultStatus = status;
				}
			} else if (status == AppealStatus.Pending) {
				pendingFound = true;
			}
		}
		
		if (lastResultTime + trumpTime > System.currentTimeMillis() && lastResultStatus != AppealStatus.None ) {
			return lastResultStatus;
		} else {
			if (pendingFound) {
				return AppealStatus.Pending;
			} else {
				return currentStatus;
			}
		}
	}

	public ComponentCoder getCoder(int coderIdx) {
		if (!isCoderIdxValid(coderIdx)) return null;
		return coders[coderIdx];
	}
	
	public CoderData getReviewer(int reviewerIdx) {
		if (!isReviewerIdxValid(reviewerIdx)) return null;
		return reviewers[reviewerIdx];
	}
	
	public List<ComponentCoder> getCoders() {
		return Arrays.asList(coders);
	}
	
	public List<CoderData> getReviewers() {
		return Arrays.asList(reviewers);
	}
	
	public PointDirection getTotalPointDirection(int coderIdx) {
		if (hideScores || !isCoderIdxValid(coderIdx)) return PointDirection.NoChange;
		return totalPointDirection[coderIdx];
	}
	
	public int indexOfCoder(int coderID)	{
		for(int x = 0; x < coders.length; x++) {
			if (coders[x].getCoderID() == coderID) return x;
		}
		cat.warn("Unknown coderID: " + coderID);
		return -1;
	}
	
	public int indexOfReviewer(int reviewerID)	{
		for(int x = 0; x < reviewers.length; x++) {
			if (reviewers[x].getCoderID() == reviewerID) return x;
		}
		cat.warn("Unknown ReviewerID: " + reviewerID);
		return -1;
	}

	public ComponentContestPlacementTracker getPlacementTracker() {
		return placementTracker;
	}
	
	public void addPointValueListener(PointValueChangeListener listener) {
		pointValueChangeHandler.addPointValueChangeListener(listener);
	}
	
	public void removePointValueListener(PointValueChangeListener listener) {
		pointValueChangeHandler.removePointValueChangeListener(listener);
	}
	
	private void firePointValueChange(int coderIdx, int reviewerIdx, int newScore) {
		pointValueChangeHandler.fireUpdatePointValue(new PointValueChangeEvent(this, coderIdx, reviewerIdx, newScore));
	}
	
	public void addAppealStatusListener(AppealChangeListener listener) {
		appealChangeHandler.addListener(listener);
	}
	
	public void removeAppealStatusListener(AppealChangeListener listener) {
		appealChangeHandler.removeListener(listener);
	}
	
	private void fireAppealStatusChange(int coderIdx, int reviewerIdx, AppealStatus newStatus) {
		appealChangeHandler.fireProblemOpened(new AppealChangeEvent(this, coderIdx, reviewerIdx, newStatus));
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
	
	private void updateScore(int coderIdx, int reviewerIdx, int score) {
		if (!isCoderIdxValid(coderIdx)) {
			cat.warn("Unknown CoderIdx=" + coderIdx);
			return;
		}
		if (!isReviewerIdxValid(reviewerIdx)) {
			cat.warn("Unknown reviewrIdx=" + reviewerIdx);
			return;
		}
		
		if (score != scores[coderIdx][reviewerIdx]) {
			totalPointDirection[coderIdx] = score > scores[coderIdx][reviewerIdx] ? PointDirection.IncreasingValue : PointDirection.DecreasingValue;
			scores[coderIdx][reviewerIdx] = score;
			firePointValueChange(coderIdx, reviewerIdx, score);
			fireTotalChange(coderIdx, getAverageScore(coderIdx));
		}
	}
	
	private void updateAppeal(int coderIdx, int reviewerIdx, long appealID, Constants.AppealStatus status) {
		if (!isCoderIdxValid(coderIdx)) {
			cat.warn("Unknown CoderIdx=" + coderIdx);
			return;
		}
		if (!isReviewerIdxValid(reviewerIdx)) {
			cat.warn("Unknown reviewrIdx=" + reviewerIdx);
			return;
		}
		
		final Map<Long, AppealStatusTime> statuses = appeals[coderIdx][reviewerIdx];
		
		final AppealStatusTime currentStatus = statuses.get(appealID);
		if (currentStatus == null || currentStatus.status != status) {
			statuses.put(appealID, new AppealStatusTime(status));
			
			fireAppealStatusChange(coderIdx, reviewerIdx, status);
		}
	}
	
	private boolean isCoderIdxValid(int coderIdx) {
		return coderIdx >=0 && coderIdx < coders.length;
	}
	
	private boolean isReviewerIdxValid(int reviewerIdx) {
		return reviewerIdx >=0 && reviewerIdx < reviewers.length;
	}

	public void dispose()
	{
		placementTracker.dispose();
		SpectatorEventProcessor.getInstance().removeComponentContestListener(contestHandler);
		SpectatorEventProcessor.getInstance().removePhaseListener(phaseHandler);
	}
	
	/** Class handling the define component connection messages */
	private class ComponentContestHandler extends ComponentContestAdapter {
		@Override
		public void scoreUpdate(int contestID, int roundID, long componentID, int coderID, int reviewerID, int score) {
			if (contestID != ComponentContest.this.contestID 
			|| roundID != ComponentContest.this.roundID 
			|| componentID != ComponentContest.this.componentData.getComponentID()) {
				return;
			}
			updateScore(indexOfCoder(coderID), indexOfReviewer(reviewerID), score);
		}
		
		@Override
		public void appealUpdate(ComponentAppealEvent event) {
			if (event.getContestID() != ComponentContest.this.contestID 
			|| event.getRoundID() != ComponentContest.this.roundID 
			|| event.getComponetID() != ComponentContest.this.componentData.getComponentID()) {
   			return;
			}
			updateAppeal(indexOfCoder(event.getCoderID()), indexOfReviewer(event.getReviewerID()), event.getAppealID(), event.getStatus());
		}
	}
	
	private class PhaseHandler implements PhaseListener {

		private int priorPhase = ContestConstants.SPECTATOR_PHASES[0];
		
		public void unknown(PhaseEvent evt, int phaseID) {
			if (phaseID != priorPhase) {
				resetScores();
				resetScoreDirection();
				priorPhase = phaseID;
			}
		}

		public void contestInfo(PhaseEvent evt) {
			if (priorPhase != ContestConstants.SPECTATOR_CONTESTINFO) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.SPECTATOR_CONTESTINFO;
			}
		}

		public void announcements(PhaseEvent evt) {
			if (priorPhase != ContestConstants.SPECTATOR_ANNOUNCEMENTS) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.SPECTATOR_ANNOUNCEMENTS;
			}
		}

		public void coding(PhaseEvent evt) {
			if (priorPhase != ContestConstants.CODING_PHASE) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.CODING_PHASE;
			}
		}

		public void intermission(PhaseEvent evt) {
			if (priorPhase != ContestConstants.INTERMISSION_PHASE) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.INTERMISSION_PHASE;
			}
		}

		public void challenge(PhaseEvent evt) {
			if (priorPhase != ContestConstants.CHALLENGE_PHASE) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.CHALLENGE_PHASE;
			}
		}

		public void systemTesting(PhaseEvent evt) {
			if (priorPhase != ContestConstants.SYSTEM_TESTING_PHASE) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.SYSTEM_TESTING_PHASE;
			}
		}

		public void endContest(PhaseEvent evt) {
			if (priorPhase != ContestConstants.CONTEST_COMPLETE_PHASE) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.CONTEST_COMPLETE_PHASE;
			}
		}

		public void voting(PhaseEvent evt) {
			if (priorPhase != ContestConstants.VOTING_PHASE) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.VOTING_PHASE;
			}
		}

		public void votingTie(PhaseEvent evt) {
			if (priorPhase != ContestConstants.TIE_BREAKING_VOTING_PHASE) {
				resetScores();
				resetScoreDirection();
				priorPhase = ContestConstants.TIE_BREAKING_VOTING_PHASE;
			}
		}

		public void componentAppeals(PhaseEvent evt) {
			// don't reset the scores - a full update occurs in the define part
			if (priorPhase != ContestConstants.COMPONENT_CONTEST_APPEALS) {
				setHidingScores(false);
				resetScoreDirection();
				priorPhase = ContestConstants.COMPONENT_CONTEST_APPEALS;
			}
		}

		public void componentResults(PhaseEvent evt) {
			if (priorPhase != ContestConstants.COMPONENT_CONTEST_RESULTS) {
				setHidingScores(true);
				resetScoreDirection();
				priorPhase = ContestConstants.COMPONENT_CONTEST_RESULTS;
			}
		}

		public void componentEndContest(PhaseEvent evt) {
			if (priorPhase != ContestConstants.COMPONENT_CONTEST_END) {
				setHidingScores(false);
				resetScoreDirection();
				priorPhase = ContestConstants.COMPONENT_CONTEST_END;
			}
		}
		
		private void resetScores() {
			for(int x = 0;x < coders.length; x++) {
				for(int y = 0; y < reviewers.length; y++) {
					scores[x][y] = 0;
					appeals[x][y].clear();
				}
			}
		}
		
		private void resetScoreDirection() {
			for(int x = 0;x < coders.length; x++) {
				totalPointDirection[x] = PointDirection.NoChange;
			}
		}
	}
	
	private class AppealStatusTime {
		private long statusTime;
		private AppealStatus status;
		public AppealStatusTime(AppealStatus status) {
			statusTime = System.currentTimeMillis();
			this.status = status;
		}
	}
}
