package com.topcoder.client.contestApplet.panels;

//import java.util.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contest.ContestConstants;

public final class TimeLine extends JPanel {

    //private ContestApplet ca = null;
    private JLabel emptyLabel = null;
    private JLabel codingLabel = null;
    private JLabel intermissionLabel = null;
    private JLabel challengeLabel = null;
    private JLabel systemLabel = null;
    
    

    ////////////////////////////////////////////////////////////////////////////////
    public TimeLine(ContestApplet ca) {
        this(ca, "0.gif", "1.gif", "2.gif", "3.gif", "4.gif");
        
    }
    
    public TimeLine(ContestApplet ca, String emptyImage, String codingImage, String intermissionImage, String challengeImage, String systemImage) {
        super(new GridBagLayout());

        setOpaque(false);

        emptyLabel = new JLabel(Common.getImage(emptyImage, ca));
        codingLabel = new JLabel(Common.getImage(codingImage, ca));
        intermissionLabel = new JLabel(Common.getImage(intermissionImage, ca));
        challengeLabel = new JLabel(Common.getImage(challengeImage, ca));
        systemLabel = new JLabel(Common.getImage(systemImage, ca));
        setPhase(ContestConstants.INACTIVE_PHASE);
        create();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void create()
            ////////////////////////////////////////////////////////////////////////////////
    {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.insets = new Insets(0, 0, 0, 0);
        Common.insertInPanel(emptyLabel, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
        Common.insertInPanel(codingLabel, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
        Common.insertInPanel(intermissionLabel, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
        Common.insertInPanel(challengeLabel, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
        Common.insertInPanel(systemLabel, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setPhase(int index)
            ////////////////////////////////////////////////////////////////////////////////
    {
        switch (index) {
        case ContestConstants.INACTIVE_PHASE:
        case ContestConstants.REGISTRATION_PHASE:
        case ContestConstants.STARTS_IN_PHASE:
        case ContestConstants.ALMOST_CONTEST_PHASE:
        case ContestConstants.MODERATED_CHATTING_PHASE:
            emptyLabel.setVisible(true);
            codingLabel.setVisible(false);
            intermissionLabel.setVisible(false);
            challengeLabel.setVisible(false);
            systemLabel.setVisible(false);
            break;
        case ContestConstants.CODING_PHASE:
            codingLabel.setVisible(true);
            emptyLabel.setVisible(false);
            intermissionLabel.setVisible(false);
            challengeLabel.setVisible(false);
            systemLabel.setVisible(false);
            break;
        case ContestConstants.INTERMISSION_PHASE:
            intermissionLabel.setVisible(true);
            emptyLabel.setVisible(false);
            codingLabel.setVisible(false);
            challengeLabel.setVisible(false);
            systemLabel.setVisible(false);
            break;
        case ContestConstants.CHALLENGE_PHASE:
            challengeLabel.setVisible(true);
            emptyLabel.setVisible(false);
            codingLabel.setVisible(false);
            intermissionLabel.setVisible(false);
            systemLabel.setVisible(false);
            break;
        case ContestConstants.VOTING_PHASE:
        case ContestConstants.TIE_BREAKING_VOTING_PHASE:
        case ContestConstants.PENDING_SYSTESTS_PHASE:
        case ContestConstants.SYSTEM_TESTING_PHASE:
        case ContestConstants.CONTEST_COMPLETE_PHASE:
            systemLabel.setVisible(true);
            emptyLabel.setVisible(false);
            codingLabel.setVisible(false);
            intermissionLabel.setVisible(false);
            challengeLabel.setVisible(false);
            break;
        default :
            System.err.println("Unknown phase (" + index + ").");
            emptyLabel.setVisible(false);
            codingLabel.setVisible(false);
            intermissionLabel.setVisible(false);
            challengeLabel.setVisible(false);
            systemLabel.setVisible(false);
            break;
        }
    }

    public void updateIcons(String emptyImage, String codingImage, String intermissionImage, String challengeImage, String systemImage, Object myClass) {
        emptyLabel.setIcon(Common.getImage(emptyImage, myClass));
        codingLabel.setIcon(Common.getImage(codingImage, myClass));
        intermissionLabel.setIcon(Common.getImage(intermissionImage, myClass));
        challengeLabel.setIcon(Common.getImage(challengeImage, myClass));
        systemLabel.setIcon(Common.getImage(systemImage, myClass));
        this.invalidate();
    }
}
