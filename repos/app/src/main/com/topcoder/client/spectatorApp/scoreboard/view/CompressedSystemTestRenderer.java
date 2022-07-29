package com.topcoder.client.spectatorApp.scoreboard.view;

import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;

public class CompressedSystemTestRenderer extends RankedSystemTestRenderer 
{
	public CompressedSystemTestRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker) {
		super(roomTitle, contestName, pointTracker);
	}
}
