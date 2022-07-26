/**
 * IntermissionPhaseRenderer.java
 *
 * Description:		Intermission phase renderer
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.view;

import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;


public class RankedIntermissionRenderer extends RankedCodingRenderer {

    /** Constructs the trailer panel */
    public RankedIntermissionRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker) {
        super(roomTitle, contestName, pointTracker);
    }
}
