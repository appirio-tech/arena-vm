/**
 * IntermissionPhaseRenderer.java
 *
 * Description:		Intermission phase renderer
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.view;

import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;


public class SmallIntermissionRenderer extends SmallCodingRenderer {

    /** Constructs the trailer panel */
    public SmallIntermissionRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker) {
        super(roomTitle, contestName, pointTracker);
    }
}
