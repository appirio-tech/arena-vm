/**
 * CodingPhaseRenderer renders for more than 4 people Description: Coding Phase
 * renderer
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import com.topcoder.client.spectatorApp.widgets.SDesignBackground;

public class DesignContestRenderer extends AbstractScreenContestRenderer {
	/** Constructs the panel */
	public DesignContestRenderer(String[] computerNames, String path, String[] handles, String title) {
		super(computerNames, path, handles, new DesignHeaderPanel(title), new SDesignBackground());
	}
}
