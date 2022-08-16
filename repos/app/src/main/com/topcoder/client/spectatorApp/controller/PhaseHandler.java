package com.topcoder.client.spectatorApp.controller;

import java.util.List;
import com.topcoder.client.spectatorApp.event.PhaseAdapter;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.ShowComponentEvent;
import com.topcoder.client.spectatorApp.event.ShowRoomEvent;
import com.topcoder.client.spectatorApp.event.TeamEvent;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContest;
import com.topcoder.client.spectatorApp.scoreboard.model.ComponentContestManager;
import com.topcoder.client.spectatorApp.scoreboard.model.Room;
import com.topcoder.client.spectatorApp.scoreboard.model.RoomManager;
import com.topcoder.client.spectatorApp.scoreboard.model.Team;
import com.topcoder.client.spectatorApp.scoreboard.model.TeamManager;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;

/**
 * Class handling the phase change messages to resend the last known show event
 * to resync the display
 */
class PhaseHandler extends PhaseAdapter {
	/** reference to the logging category */
//	private static final Category cat = Category.getInstance(PhaseHandler.class.getName());

	/** Parent controller */
	private final GUIController controller;

	/** Constructor */
	PhaseHandler(GUIController controller) {
		this.controller = controller;
	}

	@Override
	public void coding(PhaseEvent evt) {
		resendShow(ContestConstants.CODING_PHASE);
	}

	@Override
	public void intermission(PhaseEvent evt) {
		resendShow(ContestConstants.INTERMISSION_PHASE);
	}

	@Override
	public void challenge(PhaseEvent evt) {
		resendShow(ContestConstants.CHALLENGE_PHASE);
	}

	@Override
	public void systemTesting(PhaseEvent evt) {
		resendShow(ContestConstants.SYSTEM_TESTING_PHASE);
	}

	@Override
	public void endContest(PhaseEvent evt) {
		resendShow(ContestConstants.CONTEST_COMPLETE_PHASE);
	}

	@Override
	public void voting(PhaseEvent evt) {
		resendShow(ContestConstants.VOTING_PHASE);
	}

	@Override
	public void votingTie(PhaseEvent evt) {
		resendShow(ContestConstants.TIE_BREAKING_VOTING_PHASE);
	}

	@Override
	public void componentAppeals(PhaseEvent evt) {
		resendShow(ContestConstants.COMPONENT_CONTEST_APPEALS);
	}
	
	@Override
	public void componentResults(PhaseEvent evt) {
		resendShow(ContestConstants.COMPONENT_CONTEST_RESULTS);
	}
	
	@Override
	public void componentEndContest(PhaseEvent evt) {
		resendShow(ContestConstants.COMPONENT_CONTEST_END);
	}
	
	private void resendShow(int phaseID) {
		if (phaseID == ContestConstants.VOTING_PHASE || phaseID == ContestConstants.TIE_BREAKING_VOTING_PHASE) {
			if (controller.getShowEvent() != null && controller.getShowEvent() instanceof TeamEvent) {
				controller.getSwitchRenderer().setSomeObject(null); // Forces a redraw
				controller.getTeamHandler().showTeam((TeamEvent) controller.getShowEvent());
			} else {
				Team team = TeamManager.getInstance().getFirstTeam();
				if (team != null) {
					controller.getSwitchRenderer().setSomeObject(null); // Forces a redraw
					controller.getTeamHandler().showTeam(new TeamEvent(this, team.getTeamID()));
				}
			}
		} else if (phaseID == ContestConstants.COMPONENT_CONTEST_APPEALS || phaseID == ContestConstants.COMPONENT_CONTEST_RESULTS || phaseID == ContestConstants.COMPONENT_CONTEST_END) {
			if (controller.getShowEvent() != null && controller.getShowEvent() instanceof ShowComponentEvent) {
				controller.getComponentHandler().showComponent((ShowComponentEvent) controller.getShowEvent());
			} else {
				ComponentContest contest = ComponentContestManager.getInstance().getDefaultContest();
				if (contest != null) {
					ShowComponentEvent event = new ShowComponentEvent(this, contest.getContestID(), contest.getRoundID(), contest.getComponentID());
					controller.getComponentHandler().showComponent(event);
				}
			}
		} else {
			if (controller.getShowEvent() != null && controller.getShowEvent() instanceof ShowRoomEvent) {
				controller.getSwitchRenderer().setSomeObject(null); // Forces a
																						// redraw
				controller.getRoomHandler().showRoom((ShowRoomEvent) controller.getShowEvent());
			} else {
				// If no show event and one of the main phases - create an
				// artificial one...
				if (phaseID == ContestConstants.CODING_PHASE 
							|| phaseID == ContestConstants.INTERMISSION_PHASE 
							|| phaseID == ContestConstants.CHALLENGE_PHASE 
							|| phaseID == ContestConstants.SYSTEM_TESTING_PHASE
							|| phaseID == ContestConstants.CONTEST_COMPLETE_PHASE) {
					// Get the first room
					Room room = RoomManager.getInstance().getFirstRoom();
					if (room != null) {
						// Get the list of coders in the room
						List l = room.getPointTracker().getCoders();
						int[] coderIDs = new int[Math.min(5, l.size())];
						for (int x = 0; x < coderIDs.length; x++)
							coderIDs[x] = ((CoderRoomData) l.get(x)).getCoderID();
						// Show the room
						controller.getRoomHandler().showRoom(new ShowRoomEvent(this, room.getRoomID()));
					}
				}
			}
		}
	}
}
