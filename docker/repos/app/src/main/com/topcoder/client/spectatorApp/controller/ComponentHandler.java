package com.topcoder.client.spectatorApp.controller;

import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.event.ComponentContestAdapter;
import com.topcoder.client.spectatorApp.event.ShowComponentEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContest;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContestManager;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentRound;
import com.topcoder.client.spectatorApp.scoreboard.model.Round;
import com.topcoder.client.spectatorApp.scoreboard.model.RoundManager;
import com.topcoder.client.spectatorApp.scoreboard.view.ComponentAppealsPanel;
import com.topcoder.client.spectatorApp.scoreboard.view.ComponentResultsDonePanel;
import com.topcoder.client.spectatorApp.scoreboard.view.ComponentResultsPanel;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.netCommon.contest.ContestConstants;

public class ComponentHandler extends ComponentContestAdapter {
	
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(ComponentHandler.class.getName());

	/** Parent controller */
	private final GUIController controller;


	/** Constructs the handler from the controller */
	ComponentHandler(GUIController controller) {
		this.controller = controller;
	}
	
	@Override
	public void showComponent(ShowComponentEvent event) {
		int phaseID = PhaseTracker.getInstance().getPhaseID();
		
		if (phaseID == ContestConstants.COMPONENT_CONTEST_APPEALS) {
			showComponentAppeals(event);
		} else if (phaseID == ContestConstants.COMPONENT_CONTEST_RESULTS) {
			showComponentResults(event);
		} else if (phaseID == ContestConstants.COMPONENT_CONTEST_END) {
			showComponentResultsDone(event);
		} else {
			cat.warn("Unknown phase id for a component contest: " + phaseID);
		}
	}
	
	private void showComponentAppeals(ShowComponentEvent event) {
		final ComponentContest contest = ComponentContestManager.getInstance().getComponentContest(event.getComponetID());
		if (contest == null) {
			cat.info("ComponentID: " + event.getComponetID() + " has not been defined yet");
			return;
		}
		
		final Round round = RoundManager.getInstance().getRound(event.getRoundID());
		if (round == null) {
			cat.info("Round: " + event.getRoundID() + " has not been defined yet");
			return;
		}
		
		// Determine the new renderer
		final AnimatePanel newRenderer = new ComponentAppealsPanel(round, contest);
		
		// Not found...
		if (newRenderer == null) {
			// Save the show room event
			controller.setShowEvent(event);
			return;
		}
		
		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
		
		// Set the last show event
		controller.setShowEvent(event);
	}
	
	private void showComponentResults(ShowComponentEvent event) {
		final ComponentRound contest = ComponentContestManager.getInstance().getComponentRound(event.getRoundID());
		if (contest == null) {
			cat.info("Component roundID: " + event.getRoundID() + " has not been defined yet");
			return;
		}
		
		final Round round = RoundManager.getInstance().getRound(event.getRoundID());
		if (round == null) {
			cat.info("Round: " + event.getRoundID() + " has not been defined yet");
			return;
		}
		
		// Determine the new renderer
		final AnimatePanel newRenderer = new ComponentResultsPanel(round.getRoundName(), contest);
		
		// Not found...
		if (newRenderer == null) {
			// Save the show room event
			controller.setShowEvent(event);
			return;
		}
		
		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
		
		// Set the last show event
		controller.setShowEvent(event);
	}

	private void showComponentResultsDone(ShowComponentEvent event) {
		final ComponentRound contest = ComponentContestManager.getInstance().getComponentRound(event.getRoundID());
		if (contest == null) {
			cat.info("Component roundID: " + event.getRoundID() + " has not been defined yet");
			return;
		}
		
		final Round round = RoundManager.getInstance().getRound(event.getRoundID());
		if (round == null) {
			cat.info("Round: " + event.getRoundID() + " has not been defined yet");
			return;
		}
		
		// Determine the new renderer
		final AnimatePanel newRenderer = new ComponentResultsDonePanel(round.getRoundName(), contest);
		
		// Not found...
		if (newRenderer == null) {
			// Save the show room event
			controller.setShowEvent(event);
			return;
		}
		
		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
		
		// Set the last show event
		controller.setShowEvent(event);
	}
}
