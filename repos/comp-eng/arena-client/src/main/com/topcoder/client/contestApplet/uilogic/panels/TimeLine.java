/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestApplet.uilogic.panels;

import javax.swing.ImageIcon;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contest.ContestConstants;
/**
 * <p>
 * Changes in version 1.1 (Fix issue 162)
 * <ol>
 *      <li>Update {@link #setPhase(int index, boolean isPracticeRound)} method.</li>
 *      <li>Update {@link #updateIcons(String emptyImage, String codingImage, String intermissionImage,
 *              String challengeImage, String systemImage, String practiceImage, Object myClass)} method.</li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.1
 *
 */
public class TimeLine {
    private UIComponent emptyLabel;
    private UIComponent codingLabel;
    private UIComponent intermissionLabel;
    private UIComponent challengeLabel;
    private UIComponent systemLabel;
    private UIComponent practiceLabel;
    private UIPage page;

    public TimeLine(ContestApplet ca, UIPage page) {
        this.page = page;
        emptyLabel = page.getComponent("phase_empty_label");
        codingLabel = page.getComponent("phase_coding_label");
        intermissionLabel = page.getComponent("phase_intermission_label");
        challengeLabel = page.getComponent("phase_challenge_label");
        systemLabel = page.getComponent("phase_system_label");
        practiceLabel = page.getComponent("phase_practice_label");
    }
    /**
     * Set the phase time line in room.
     * @param index the phase type
     * @param isPracticeRound check if it is practice round
     */
    public void setPhase(int index, boolean isPracticeRound) {
        switch (index) {
        case ContestConstants.INACTIVE_PHASE:
        case ContestConstants.REGISTRATION_PHASE:
        case ContestConstants.STARTS_IN_PHASE:
        case ContestConstants.ALMOST_CONTEST_PHASE:
        case ContestConstants.MODERATED_CHATTING_PHASE:
            emptyLabel.setProperty("visible", Boolean.TRUE);
            codingLabel.setProperty("visible", Boolean.FALSE);
            intermissionLabel.setProperty("visible", Boolean.FALSE);
            challengeLabel.setProperty("visible", Boolean.FALSE);
            systemLabel.setProperty("visible", Boolean.FALSE);
            practiceLabel.setProperty("visible", Boolean.FALSE);
            break;
        case ContestConstants.CODING_PHASE:
            if (isPracticeRound) {
                codingLabel.setProperty("visible", Boolean.FALSE);
                practiceLabel.setProperty("visible", Boolean.TRUE);
            } else {
                codingLabel.setProperty("visible", Boolean.TRUE);
                practiceLabel.setProperty("visible", Boolean.FALSE);
            }

            emptyLabel.setProperty("visible", Boolean.FALSE);
            intermissionLabel.setProperty("visible", Boolean.FALSE);
            challengeLabel.setProperty("visible", Boolean.FALSE);
            systemLabel.setProperty("visible", Boolean.FALSE);
            break;
        case ContestConstants.INTERMISSION_PHASE:
            intermissionLabel.setProperty("visible", Boolean.TRUE);
            emptyLabel.setProperty("visible", Boolean.FALSE);
            codingLabel.setProperty("visible", Boolean.FALSE);
            challengeLabel.setProperty("visible", Boolean.FALSE);
            systemLabel.setProperty("visible", Boolean.FALSE);
            practiceLabel.setProperty("visible", Boolean.FALSE);
            break;
        case ContestConstants.CHALLENGE_PHASE:
            challengeLabel.setProperty("visible", Boolean.TRUE);
            emptyLabel.setProperty("visible", Boolean.FALSE);
            codingLabel.setProperty("visible", Boolean.FALSE);
            intermissionLabel.setProperty("visible", Boolean.FALSE);
            systemLabel.setProperty("visible", Boolean.FALSE);
            practiceLabel.setProperty("visible", Boolean.FALSE);
            break;
        case ContestConstants.VOTING_PHASE:
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            systemLabel.setProperty("visible", Boolean.TRUE);
            emptyLabel.setProperty("visible", Boolean.FALSE);
            codingLabel.setProperty("visible", Boolean.FALSE);
            intermissionLabel.setProperty("visible", Boolean.FALSE);
            challengeLabel.setProperty("visible", Boolean.FALSE);
            practiceLabel.setProperty("visible", Boolean.FALSE);
            break;
        default :
            System.err.println("Unknown phase (" + index + ").");
            emptyLabel.setProperty("visible", Boolean.FALSE);
            codingLabel.setProperty("visible", Boolean.FALSE);
            intermissionLabel.setProperty("visible", Boolean.FALSE);
            challengeLabel.setProperty("visible", Boolean.FALSE);
            systemLabel.setProperty("visible", Boolean.FALSE);
            practiceLabel.setProperty("visible", Boolean.FALSE);
            break;
        }
    }
    /**
     * Update the phase icons
     * @param emptyImage the empty phase.
     * @param codingImage the coding phase.
     * @param intermissionImage the intermission phase.
     * @param challengeImage the challenge phase.
     * @param systemImage the system phase.
     * @param practiceImage the practice phase.
     * @param myClass the class of phase.
     */
    public void updateIcons(String emptyImage, String codingImage, String intermissionImage, String challengeImage, String systemImage, String practiceImage, Object myClass) {
        emptyLabel.setProperty("icon", (ImageIcon) page.getComponent(emptyImage).getProperty("value"));
        codingLabel.setProperty("icon", (ImageIcon) page.getComponent(codingImage).getProperty("value"));
        intermissionLabel.setProperty("icon", (ImageIcon) page.getComponent(intermissionImage).getProperty("value"));
        challengeLabel.setProperty("icon", (ImageIcon) page.getComponent(challengeImage).getProperty("value"));
        systemLabel.setProperty("icon", (ImageIcon) page.getComponent(systemImage).getProperty("value"));
        practiceLabel.setProperty("icon", (ImageIcon) page.getComponent(practiceImage).getProperty("value"));
    }
}
