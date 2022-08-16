package com.topcoder.client.spectatorApp.controller;

import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.PhaseTracker;
import com.topcoder.client.spectatorApp.event.TeamAdapter;
import com.topcoder.client.spectatorApp.event.TeamEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.Contest;
import com.topcoder.client.spectatorApp.scoreboard.model.Round;
import com.topcoder.client.spectatorApp.scoreboard.model.Team;
import com.topcoder.client.spectatorApp.scoreboard.model.TeamManager;
import com.topcoder.client.spectatorApp.scoreboard.view.VotingCompletedRenderer;
import com.topcoder.client.spectatorApp.scoreboard.view.VotingRenderer;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.SwitchRenderer;
import com.topcoder.netCommon.contest.ContestConstants;

/** Class handling the show team messages */
class TeamHandler extends TeamAdapter {
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(TeamHandler.class.getName());

	/** The parent controller */
	private final GUIController controller;

	/** Constructor */
	TeamHandler(GUIController controller) {
		this.controller = controller;
	}

	public void showTeam(TeamEvent evt) {
		SwitchRenderer switchRenderer = controller.getSwitchRenderer();
		try {
			// Check to see if we are already showing the team
			if (switchRenderer.getSomeObject() != null && switchRenderer.getSomeObject() instanceof TeamEvent && ((TeamEvent) switchRenderer.getSomeObject()).getTeamID() == evt.getTeamID()) {
				return;
			}
			// Get the team
			Team team = TeamManager.getInstance().getTeam(evt.getTeamID());
			if (team == null) {
				cat.info("TeamID: " + evt.getTeamID() + " has not been defined yet");
				return;
			}
			// Get round information
			Round round = team.getRound();
			if (round == null) {
				cat.error("RoundID: " + team.getRoundID() + " was not found");
				return;
			}
			// Get associated contest
			Contest contest = round.getContest();
			if (contest == null) {
				cat.error("ContestID: " + round.getContestID() + " was not found");
				return;
			}
			// Determine the new renderer
			AnimatePanel newRenderer = null;
			switch (PhaseTracker.getInstance().getPhaseID()) {
			// Voting phase
			case ContestConstants.VOTING_PHASE: {
				newRenderer = new VotingRenderer(team, contest.getContestName());
				break;
			}
			// Voting phase
			case ContestConstants.TIE_BREAKING_VOTING_PHASE: {
				newRenderer = new VotingRenderer(team, contest.getContestName());
				break;
			}
			// Voting results phase
			case ContestConstants.CONTEST_COMPLETE_PHASE: {
				newRenderer = new VotingCompletedRenderer(team, contest.getContestName());
				break;
			}
			// Unknown
			default: {
				// Set the last show event
				controller.setShowEvent(evt);
				return;
			}
			}
			// Setup the switch to the new team
			if (switchRenderer.getSomeObject() != null && switchRenderer.getSomeObject() instanceof TeamEvent) {
				switchRenderer.setMoveMessage(team.getTeamName(), contest.getContestName(), "Moving to " + team.getTeamName());
				controller.setSwitchTime(System.currentTimeMillis());
			}
			switchRenderer.setSomeObject(evt);
			// Switch the renderer
			controller.setSwitchRenderer(newRenderer);
			// Set the last show event
			controller.setShowEvent(evt);
		} catch (Throwable t) {
			cat.error("ShowTeam", t);
		}
	}
}
