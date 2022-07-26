package com.topcoder.client.spectatorApp.controller;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.bio.BioRenderer;
import com.topcoder.client.spectatorApp.event.AnnounceCoderEvent;
import com.topcoder.client.spectatorApp.event.AnnounceDesignReviewBoardEvent;
import com.topcoder.client.spectatorApp.event.AnnounceDesignReviewBoardResultsEvent;
import com.topcoder.client.spectatorApp.event.AnnounceDevelopmentReviewBoardEvent;
import com.topcoder.client.spectatorApp.event.AnnounceDevelopmentReviewBoardResultsEvent;
import com.topcoder.client.spectatorApp.event.AnnounceTableResultsEvent;
import com.topcoder.client.spectatorApp.event.AnnounceTCSCoderEvent;
import com.topcoder.client.spectatorApp.event.AnnounceTCSWinnersEvent;
import com.topcoder.client.spectatorApp.event.AnnouncementListener;
import com.topcoder.client.spectatorApp.scoreboard.model.Contest;
import com.topcoder.client.spectatorApp.scoreboard.model.Round;
import com.topcoder.client.spectatorApp.scoreboard.model.RoundManager;
import com.topcoder.client.spectatorApp.scoreboard.view.AnnounceResultTablePanel;
import com.topcoder.client.spectatorApp.tcs.TCSBioRenderer;
import com.topcoder.client.spectatorApp.tcs.TCSReviewBoardRenderer;
import com.topcoder.client.spectatorApp.tcs.TCSScoreCard;
import com.topcoder.client.spectatorApp.tcs.TCSWinners;
import com.topcoder.client.spectatorApp.views.AnimatePanel;


/** Class handling the announceCoder messages */
class AnnouncementHandler implements AnnouncementListener {
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(AnnouncementHandler.class.getName());

	/** Parent controller */
    private final GUIController controller;

    /** Constructor */
	AnnouncementHandler(GUIController controller) {
		this.controller = controller;
	}

	public void announceCoder(AnnounceCoderEvent evt) {

        // Get round information
        Round round = RoundManager.getInstance().getRound(evt.getRoundID());
        if (round == null) {
            cat.error("RoundID: " + evt.getRoundID() + " was not found");
            return;
        }

        // Get associated contest
        Contest contest = round.getContest();
        if (contest == null) {
            cat.error("ContestID: " + round.getContestID() + " was not found");
            return;
        }

        // Create the renderer for the announcement
        AnimatePanel newRenderer = new BioRenderer(evt, round.getRoundName(), contest.getContestName(), contest.getLogoSmall(), contest.getSponsorLogo());

        // Switch the renderer
        controller.setSwitchRenderer(newRenderer);
    }

	public void announceTCSCoder(AnnounceTCSCoderEvent evt) {
		// Get round information
		Round round = RoundManager.getInstance().getRound(evt.getRoundID());
		if (round == null) {
			cat.error("RoundID: " + evt.getRoundID() + " was not found");
			return;
		}

		// Get associated contest
		Contest contest = round.getContest();
		if (contest == null) {
			cat.error("ContestID: " + round.getContestID() + " was not found");
			return;
		}

		// Create the renderer for the announcement
		AnimatePanel newRenderer = new TCSBioRenderer(evt, round.getRoundName(), contest.getContestName());

		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
	}

	public void announceDesignReviewBoard(AnnounceDesignReviewBoardEvent evt) {
		// Get round information
		Round round = RoundManager.getInstance().getRound(evt.getRoundID());
		if (round == null) {
			cat.error("RoundID: " + evt.getRoundID() + " was not found");
			return;
		}

		// Get associated contest
		Contest contest = round.getContest();
		if (contest == null) {
			cat.error("ContestID: " + round.getContestID() + " was not found");
			return;
		}

		// Create the renderer for the announcement
		AnimatePanel newRenderer = new TCSReviewBoardRenderer(evt, "Design Review Board", round.getRoundName(), contest.getContestName());

		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
	}

	public void announceDevelopmentReviewBoard(AnnounceDevelopmentReviewBoardEvent evt) {
		// Get round information
		Round round = RoundManager.getInstance().getRound(evt.getRoundID());
		if (round == null) {
			cat.error("RoundID: " + evt.getRoundID() + " was not found");
			return;
		}

		// Get associated contest
		Contest contest = round.getContest();
		if (contest == null) {
			cat.error("ContestID: " + round.getContestID() + " was not found");
			return;
		}

		// Create the renderer for the announcement
		AnimatePanel newRenderer = new TCSReviewBoardRenderer(evt, "Development Review Board", round.getRoundName(), contest.getContestName());

		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
	}

	public void announceDesignReviewBoardResults(AnnounceDesignReviewBoardResultsEvent evt) {
		// Get round information
		Round round = RoundManager.getInstance().getRound(evt.getRoundID());
		if (round == null) {
			cat.error("RoundID: " + evt.getRoundID() + " was not found");
			return;
		}

		// Get associated contest
		Contest contest = round.getContest();
		if (contest == null) {
			cat.error("ContestID: " + round.getContestID() + " was not found");
			return;
		}

		// Create the renderer for the announcement
		AnimatePanel newRenderer = new TCSScoreCard(evt, "Design - Scorecard", round.getRoundName(), contest.getContestName(), round.getTCSPlacements());

		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
	}

	public void announceDevelopmentReviewBoardResults(AnnounceDevelopmentReviewBoardResultsEvent evt) {
		// Get round information
		Round round = RoundManager.getInstance().getRound(evt.getRoundID());
		if (round == null) {
			cat.error("RoundID: " + evt.getRoundID() + " was not found");
			return;
		}

		// Get associated contest
		Contest contest = round.getContest();
		if (contest == null) {
			cat.error("ContestID: " + round.getContestID() + " was not found");
			return;
		}

		// Create the renderer for the announcement
		AnimatePanel newRenderer = new TCSScoreCard(evt, "Development - Scorecard", round.getRoundName(), contest.getContestName(), round.getTCSPlacements());

		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
	}

	public void announceTCSWinners(AnnounceTCSWinnersEvent evt) {
		// Get round information
		Round round = RoundManager.getInstance().getRound(evt.getRoundID());
		if (round == null) {
			cat.error("RoundID: " + evt.getRoundID() + " was not found");
			return;
		}

		// Get associated contest
		Contest contest = round.getContest();
		if (contest == null) {
			cat.error("ContestID: " + round.getContestID() + " was not found");
			return;
		}

		// Create the renderer for the announcement
		AnimatePanel newRenderer = new TCSWinners(evt, round.getRoundName(), contest.getContestName());

		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
	}

    public void announceTableResults(AnnounceTableResultsEvent evt) {
		// Get round information
		Round round = RoundManager.getInstance().getRound(evt.getRoundID());
		if (round == null) {
			cat.error("RoundID: " + evt.getRoundID() + " was not found");
			return;
		}

		// Get associated contest
		Contest contest = round.getContest();
		if (contest == null) {
			cat.error("ContestID: " + round.getContestID() + " was not found");
			return;
		}

		// Create the renderer for the announcement
		AnimatePanel newRenderer = new AnnounceResultTablePanel(evt, round.getRoundName(), contest.getContestName());

		// Switch the renderer
		controller.setSwitchRenderer(newRenderer);
    }
}
