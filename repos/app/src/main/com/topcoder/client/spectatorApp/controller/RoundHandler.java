package com.topcoder.client.spectatorApp.controller;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.event.RoundAdapter;
import com.topcoder.client.spectatorApp.event.RoundEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.Contest;
import com.topcoder.client.spectatorApp.scoreboard.model.Round;
import com.topcoder.client.spectatorApp.scoreboard.model.RoundManager;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LogoRenderer;


/** Class handling the show round messages */
class RoundHandler extends RoundAdapter {

	/** reference to the logging category */
	private static final Category cat = Category.getInstance(RoundHandler.class.getName());
	
	/** Parent controller */
    private final GUIController controller;

    /** Constructor */
	RoundHandler(GUIController controller) {
		this.controller = controller;
	}

	public void showRound(RoundEvent evt) {

        // Get the round associated with the event
        Round round = RoundManager.getInstance().getRound(evt.getRoundID());
        if (round == null) {
            cat.info("RoundID: " + evt.getRoundID() + " has not been defined yet");
            return;
        }

        // Get the associated contest
        Contest contest = round.getContest();
        if (contest == null) {
            cat.info("ContestID: " + round.getContestID() + " has not been defined yet");
            return;
        }
        
        // Ensure the large logo has been defined
        if(contest.getLogoLarge()==null) {
            cat.info("The large logo for ContestID: " + round.getContestID() + " has not been defined yet");
            return;
        }

        // Create the renderer
        AnimatePanel newRenderer = new LogoRenderer(round.getRoundName(), contest.getContestName(), contest.getLogoLarge(), contest.getSponsorLogo());

        // Switch the renderer
        controller.setSwitchRenderer(newRenderer);
    }
}
