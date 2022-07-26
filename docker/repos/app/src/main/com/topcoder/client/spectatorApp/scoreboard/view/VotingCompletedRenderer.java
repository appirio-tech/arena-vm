/**
 * SystemTestRenderer.java
 *
 * Description:		Coding Phase renderer
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import com.topcoder.client.spectatorApp.scoreboard.model.Team;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;


public class VotingCompletedRenderer extends VotingRenderer implements AnimatePanel {

    /** Constructs the panel */
    public VotingCompletedRenderer(Team team, String contestName) {

		super(team, contestName);
        CoderRoomData[] coderData = team.getCoderData();

 
		// Get the test layer
		VotingPanel testLayer = (VotingPanel)getLayer(VOTINGLAYER);
			
		// Get the row layers
		AnimatePanel[] rows = testLayer.getLayers();
			
		// Add our highlight filter to each row layer
		for(int x=0;x<rows.length;x++) {
			testLayer.setLayer(x, rows[x], new HighlighterFilter(team, coderData[x].getCoderID()));
		}
    }
		 
}
    
/** The highlight filter to highlight the winner */
class HighlighterFilter implements LayeredPanel.LayeredFilter {

	/** Reference to the point tracker */
	private Team team;
	
	/** The coder idx */
	private int coderID;
		
	public HighlighterFilter(Team team, int coderID) {
		this.team = team;
		this.coderID = coderID;
	}

	/** Implementation of the filter */		
	public void filter(Graphics2D g2D) {
		// Set the alpha composite of the winner/loser
		if (team.isVotedOut(coderID)) {
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		} else {
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));
		}

	}
 }
