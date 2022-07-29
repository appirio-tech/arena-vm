package com.topcoder.client.spectatorApp.event;

import java.util.List;
import com.topcoder.client.spectatorApp.AbstractListenerSupport;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;
import com.topcoder.shared.netCommon.messages.spectator.ComponentData;

public class ComponentContestSupport extends AbstractListenerSupport<ComponentContestListener> {
	public void fireDefineContest(int contestID, int roundID, ComponentData data, List<ComponentCoder> coders, List<CoderData> reviewers) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).defineContest(contestID, roundID, data, coders, reviewers);
		}		
	}
	
	public void fireScoreUpdate(int contestID, int roundID, long componentID, int coderID, int reviewerID, int score) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).scoreUpdate(contestID, roundID, componentID, coderID, reviewerID, score);
		}		
	}

	public void fireAppealUpdate(ComponentAppealEvent event) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).appealUpdate(event);
		}		
	}

	public void fireShowComponent(ShowComponentEvent event) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).showComponent(event);
		}		
	}
	
	public void fireShowComponentResults(ShowComponentResultsEvent event) {
		// Fire the event to all listeners (done in reverse order from how they
		// were added).
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).showComponentResults(event);
		}
	}
}
