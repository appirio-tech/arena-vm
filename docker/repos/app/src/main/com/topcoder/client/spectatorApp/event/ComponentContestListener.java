package com.topcoder.client.spectatorApp.event;

import java.util.EventListener;
import java.util.List;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;
import com.topcoder.shared.netCommon.messages.spectator.ComponentData;

public interface ComponentContestListener extends EventListener {
	public void defineContest(int contestID, int roundID, ComponentData data, List<ComponentCoder> coders, List<CoderData> reviewers);
	public void scoreUpdate(int contestID, int roundID, long componentID, int coderID, int reviewerID, int score);
	public void appealUpdate(ComponentAppealEvent event);
	public void showComponent(ShowComponentEvent event);
	public void showComponentResults(ShowComponentResultsEvent event);
}
