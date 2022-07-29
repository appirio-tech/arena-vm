package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.rooms.CoderRoom;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.netCommon.contest.ContestConstants;

public class ProblemPanel {
    private UIComponent phaseStatus;
    private UIComponent phaseDesc;
    private UIComponent summary;
    private boolean enabled = true;
    private CoderRoom coderRoom;

    public ProblemPanel(ContestApplet ca, UIPage page, CoderRoom coderRoom, UIComponent problemList) {
        this.coderRoom = coderRoom;
        phaseStatus = page.getComponent("phase_status");
        phaseDesc = page.getComponent("phase_description");
        summary = page.getComponent("problem_list_summary");

        summary.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(enabled) {
                        statusWindowEvent(e);
                    }
                }
            });

        setPhase(ContestConstants.INACTIVE_PHASE);
    }

    public void setPanelEnabled(boolean on) {
        enabled = on;
        refreshSummaryButton();
    }

    public void setPhase(int index) {
        switch (index) {
        case ContestConstants.INACTIVE_PHASE:
            phaseStatus.setProperty("Text", "COMPETITION STATUS : COMPETITION INACTIVE");
            phaseDesc.setProperty("Text", "The contest is currently not running.");
            break;
        case ContestConstants.REGISTRATION_PHASE:
            phaseStatus.setProperty("Text", "COMPETITION STATUS : REGISTRATION PHASE");
            phaseDesc.setProperty("Text", "Select event registration from the main menu.");
            break;
        case ContestConstants.STARTS_IN_PHASE:
        case ContestConstants.ALMOST_CONTEST_PHASE:
            phaseStatus.setProperty("Text", "COMPETITION STATUS : STARTS IN");
            phaseDesc.setProperty("Text", "The competition will start at the end of the countdown.");
            break;
        case ContestConstants.CODING_PHASE:
            phaseStatus.setProperty("Text", "COMPETITION STATUS : CODING PHASE");
            phaseDesc.setProperty("Text", "Select a problem from the problem list.");
//            } else {
//                phaseDesc.setProperty("Text", "Please select the status window from the tools menu.");
//            }
            break;
        case ContestConstants.INTERMISSION_PHASE:
            phaseStatus.setProperty("Text", "COMPETITION STATUS : INTERMISSION");
            phaseDesc.setProperty("Text", "Please wait for the challenge phase to start.");
            break;
        case ContestConstants.CHALLENGE_PHASE:
            phaseStatus.setProperty("Text", "COMPETITION STATUS : CHALLENGE PHASE");
            phaseDesc.setProperty("Text", "Please select the status window from the tools menu.");
            break;
        case ContestConstants.VOTING_PHASE:
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            phaseStatus.setProperty("Text", "COMPETITION STATUS : SYSTEM TESTING PHASE");
            phaseDesc.setProperty("Text", "Please wait for the system test phase to end.");
            break;
        case ContestConstants.MODERATED_CHATTING_PHASE:
            phaseStatus.setProperty("Text", "MODERATED CHAT STATUS : CHATTING");
            phaseDesc.setProperty("Text", "Ask questions with /moderator.");
            break;
        default:
            System.err.println("Unknown phase (" + index + ").");
            break;
        }
        refreshSummaryButton();
    }

    private void refreshSummaryButton() {
        boolean summaryEnabled = enabled && coderRoom.getRoomModel() != null && coderRoom.getRoomModel().getRoundModel().canDisplaySummary();
        summary.setProperty("enabled", Boolean.valueOf(summaryEnabled));
    }
    
    private void statusWindowEvent(ActionEvent e) {
        coderRoom.challengeButtonEvent(e);
    }
}
