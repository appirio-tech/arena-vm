package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.component.GeneralContestInfoPanelView;
import com.topcoder.client.mpsqasApplet.model.component.GeneralContestInfoPanelModel;
import com.topcoder.client.mpsqasApplet.controller.component.GeneralContestInfoPanelController;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;

/**
 * A panel in which a user can view some general information about a contest
 * such as the contest name, times, problem writers, and problem testers.
 *
 * @author mitalub
 */
public class GeneralContestInfoPanelViewImpl extends GeneralContestInfoPanelView {

    private GeneralContestInfoPanelModel model;
    private GeneralContestInfoPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel contestNameLabel;
    private JLabel roundNameLabel;
    private JLabel codingStartLabel;
    private JLabel codingEndLabel;
    private JLabel challengeStartLabel;
    private JLabel challengeEndLabel;
    private JLabel problemWritersLabel;
    private JLabel problemTestersLabel;
    private JTextField contestNameField;
    private JTextField roundNameField;
    private JTextField codingStartField;
    private JTextField codingEndField;
    private JTextField challengeStartField;
    private JTextField challengeEndField;
    private JTextField problemWritersField;
    private JTextField problemTestersField;
    private JList problemWritersList;
    private JList problemTestersList;
    private JScrollPane problemWritersPane;
    private JScrollPane problemTestersPane;
    private JButton verifyContestButton;


    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     * Also, populates components with information in contestInfo.
     */
    public void update(Object arg) {
        ContestInformation contestInfo = model.getContestInformation();
        if (arg == null) {
            removeAll();

            contestNameLabel = new JLabel("Contest Name:");
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(contestNameLabel, gbc);
            add(contestNameLabel);

            roundNameLabel = new JLabel("Round Name:");
            GUIConstants.buildConstraints(gbc, 2, 0, 1, 1, 1, 0);
            layout.setConstraints(roundNameLabel, gbc);
            add(roundNameLabel);

            codingStartLabel = new JLabel("Coding Start:");
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 1);
            layout.setConstraints(codingStartLabel, gbc);
            add(codingStartLabel);

            codingEndLabel = new JLabel("Coding End:");
            GUIConstants.buildConstraints(gbc, 2, 1, 1, 1, 0, 0);
            layout.setConstraints(codingEndLabel, gbc);
            add(codingEndLabel);

            challengeStartLabel = new JLabel("Challenge Start:");
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
            layout.setConstraints(challengeStartLabel, gbc);
            add(challengeStartLabel);

            challengeEndLabel = new JLabel("Challenge End:");
            GUIConstants.buildConstraints(gbc, 2, 2, 1, 1, 0, 0);
            layout.setConstraints(challengeEndLabel, gbc);
            add(challengeEndLabel);

            contestNameField = new JTextField(contestInfo.getContestName());
            contestNameField.setEditable(false);
            contestNameField.setBackground(Color.white);
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 100, 0);
            layout.setConstraints(contestNameField, gbc);
            add(contestNameField);

            roundNameField = new JTextField(contestInfo.getRoundName());
            roundNameField.setEditable(false);
            roundNameField.setBackground(Color.white);
            GUIConstants.buildConstraints(gbc, 3, 0, 1, 1, 100, 0);
            layout.setConstraints(roundNameField, gbc);
            add(roundNameField);

            codingStartField = new JTextField(contestInfo.getStartCoding());
            codingStartField.setEditable(false);
            codingStartField.setBackground(Color.white);
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 0, 0);
            layout.setConstraints(codingStartField, gbc);
            add(codingStartField);

            codingEndField = new JTextField(contestInfo.getEndCoding());
            codingEndField.setEditable(false);
            codingEndField.setBackground(Color.white);
            GUIConstants.buildConstraints(gbc, 3, 1, 1, 1, 0, 0);
            layout.setConstraints(codingEndField, gbc);
            add(codingEndField);

            challengeStartField = new JTextField(contestInfo.getStartChallenge());
            challengeStartField.setEditable(false);
            challengeStartField.setBackground(Color.white);
            GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 0);
            layout.setConstraints(challengeStartField, gbc);
            add(challengeStartField);

            challengeEndField = new JTextField(contestInfo.getEndChallenge());
            challengeEndField.setEditable(false);
            challengeEndField.setBackground(Color.white);
            GUIConstants.buildConstraints(gbc, 3, 2, 1, 1, 0, 0);
            layout.setConstraints(challengeEndField, gbc);
            add(challengeEndField);

            problemWritersLabel = new JLabel("Problem Writers:");
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.NONE;
            GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 0);
            layout.setConstraints(problemWritersLabel, gbc);
            add(problemWritersLabel);

            problemTestersLabel = new JLabel("Problem Testers:");
            GUIConstants.buildConstraints(gbc, 2, 3, 1, 1, 0, 0);
            layout.setConstraints(problemTestersLabel, gbc);
            add(problemTestersLabel);

            problemWritersList = new JList(getWritersArray());
            problemWritersPane = new JScrollPane(problemWritersList);
            GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 0, 100);
            gbc.fill = GridBagConstraints.BOTH;
            layout.setConstraints(problemWritersPane, gbc);
            add(problemWritersPane);

            problemTestersList = new JList(getTestersArray());
            problemTestersPane = new JScrollPane(problemTestersList);
            GUIConstants.buildConstraints(gbc, 3, 3, 1, 1, 0, 0);
            layout.setConstraints(problemTestersPane, gbc);
            add(problemTestersPane);

/*
      verifyContestButton=new JButton("Verify Contest");
      gbc.fill=gbc.NONE;
      gbc.anchor=gbc.CENTER;
      GUIConstants.buildConstraints(gbc,0,4,4,1,0,1);
      layout.setConstraints(verifyContestButton,gbc);
      add(verifyContestButton);

      verifyContestButton.addActionListener(new AppletActionListener(
        "processVerifyContest",controller,false));
*/
        }
    }

    /**
     * Returns an Object[] of problem writers for the contest.
     */
    private Object[] getWritersArray() {
        return model.getContestInformation().getProblemWriters().toArray();
    }

    private Object[] getTestersArray() {
        return model.getContestInformation().getProblemTesters().toArray();
    }

    public void setController(ComponentController controller) {
        this.controller = (GeneralContestInfoPanelController) controller;
    }

    public void setModel(ComponentModel model) {
        this.model = (GeneralContestInfoPanelModel) model;
        model.addWatcher(this);
    }

    public String getName() {
        return "General Information";
    }
}
