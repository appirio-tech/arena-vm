package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.view.component.AdminProblemPanelView;
import com.topcoder.client.mpsqasApplet.controller.component.AdminProblemPanelController;
import com.topcoder.client.mpsqasApplet.model.component.AdminProblemPanelModel;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.netCommon.mpsqas.SolutionInformation;
import com.topcoder.netCommon.mpsqas.UserInformation;
import com.topcoder.netCommon.mpsqas.HiddenValue;
import com.topcoder.netCommon.mpsqas.StatusConstants;

import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

/**
 * Default implementation of admin problem panel view.
 * Allows an admin to choose the primary solution of a problem, the status
 * of a problem, and the testers of the problem.
 *
 * @author mitalub
 */
public class AdminProblemPanelViewImpl extends AdminProblemPanelView {

    private static String[] TESTER_COL_NAMES = {"Handle"};

    private AdminProblemPanelController controller;
    private AdminProblemPanelModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JPanel testerPanel;
    private JPanel primaryPanel;
    private GridBagLayout tlayout;

    private JLabel statusTitleLabel;
    private JLabel primaryTitleLabel;
    private JLabel testerTitleLabel;
    private JComboBox statusSelection;
    private JComboBox primarySelection;

    private JScrollPane availTestersSP;
    private JScrollPane schedTestersSP;
    private JButton removeTesterButton;
    private SortableTable availTestersT;
    private SortableTable schedTestersT;

    private JLabel testersLabel;
    private JLabel availTestersLabel;
    private JButton addTesterButton;
    private JButton submitButton;

    private JTextArea statementArea;
    private JScrollPane statementPane;

    public void init() {
        setLayout(layout = new GridBagLayout());
        gbc = new GridBagConstraints();
    }

    public void setController(ComponentController controller) {
        this.controller = (AdminProblemPanelController) controller;
    }

    public void setModel(ComponentModel model) {
        this.model = (AdminProblemPanelModel) model;
        model.addWatcher(this);
    }

    public void update(Object arg) {
        if (arg == null) {
            removeAll();
            tlayout = new GridBagLayout();
            testerPanel = new JPanel();
            testerPanel.setLayout(tlayout);

            statusTitleLabel = new JLabel("Status:");
            statusTitleLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = gbc.WEST;
            gbc.fill = gbc.NONE;
            layout.setConstraints(statusTitleLabel, gbc);
            add(statusTitleLabel, gbc);

            statusSelection = new JComboBox();
            int index = -1;
            for (int i = 0; i < StatusConstants.STATUS_IDS.length; i++) {
                if (StatusConstants.STATUS_IDS[i] == model.getStatus()) {
                    index = i;
                }
                statusSelection.addItem(StatusConstants.getStatusName(
                        StatusConstants.STATUS_IDS[i]));
            }
            if (model.containsStatus()) {
                statusSelection.setSelectedIndex(index);
            } else {
                statusSelection.setEditable(false);
            }
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 1);
            layout.setConstraints(statusSelection, gbc);
            add(statusSelection);

            primaryTitleLabel = new JLabel("Primary Solution: ");
            primaryTitleLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
            layout.setConstraints(primaryTitleLabel, gbc);
            add(primaryTitleLabel);

            primarySelection = new JComboBox();
            primarySelection.addItem(new HiddenValue("-------------", -1));
            if (model.getSolutions() == null) {
                primarySelection.setEditable(false);
            } else {
                index = 0;
                SolutionInformation solutionInfo;
                for (int i = 0; i < model.getSolutions().size(); i++) {
                    solutionInfo = (SolutionInformation) model.getSolutions().get(i);
                    if (solutionInfo.isPrimary()) {
                        index = i + 1;
                    }
                    primarySelection.addItem(new HiddenValue(solutionInfo.getHandle(),
                            solutionInfo.getSolutionId()));
                }
                primarySelection.setSelectedIndex(index);
            }
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 0, 0);
            layout.setConstraints(primarySelection, gbc);
            add(primarySelection);

            testerTitleLabel = new JLabel("Problem Testers:");
            testerTitleLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 0, 2, 2, 1, 0, 1);
            layout.setConstraints(testerTitleLabel, gbc);
            add(testerTitleLabel);

            availTestersLabel = new JLabel("Available Testers:");
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 100, 1);
            tlayout.setConstraints(availTestersLabel, gbc);
            testerPanel.add(availTestersLabel);

            testersLabel = new JLabel("Scheduled Testers:");
            GUIConstants.buildConstraints(gbc, 2, 0, 1, 1, 100, 1);
            tlayout.setConstraints(testersLabel, gbc);
            testerPanel.add(testersLabel);

            availTestersT = new SortableTable(TESTER_COL_NAMES,
                    getAllTesters());
            availTestersSP = new JScrollPane(availTestersT);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 3, 0, 0);
            tlayout.setConstraints(availTestersSP, gbc);
            testerPanel.add(availTestersSP);

            addTesterButton = new JButton("Add ->");
            gbc.fill = GridBagConstraints.HORIZONTAL;
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 1, 1);
            tlayout.setConstraints(addTesterButton, gbc);
            testerPanel.add(addTesterButton);

            removeTesterButton = new JButton("<- Remove");
            GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 1);
            tlayout.setConstraints(removeTesterButton, gbc);
            testerPanel.add(removeTesterButton);


            schedTestersT = new SortableTable(TESTER_COL_NAMES,
                    getCurrentTesters());
            schedTestersSP = new JScrollPane(schedTestersT);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 2, 1, 1, 3, 0, 0);
            tlayout.setConstraints(schedTestersSP, gbc);
            testerPanel.add(schedTestersSP);

            JLabel spacer2 = new JLabel("");
            GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 0, 100);
            tlayout.setConstraints(spacer2, gbc);
            testerPanel.add(spacer2);

            GUIConstants.buildConstraints(gbc, 0, 3, 2, 1, 0, 100);
            gbc.fill = gbc.BOTH;
            layout.setConstraints(testerPanel, gbc);
            add(testerPanel);

            submitButton = new JButton("Save Changes");
            gbc.fill = gbc.NONE;
            gbc.anchor = gbc.CENTER;
            GUIConstants.buildConstraints(gbc, 0, 4, 2, 1, 0, 1);
            layout.setConstraints(submitButton, gbc);
            add(submitButton);

            addTesterButton.addActionListener(new AppletActionListener(
                    "processAddTester", controller, false));
            removeTesterButton.addActionListener(new AppletActionListener(
                    "processRemoveTester", controller, false));
            submitButton.addActionListener(new AppletActionListener(
                    "processSubmit", controller, false));
        } else if (arg.equals(UpdateTypes.SCHEDULED_TESTERS)) {
            schedTestersT.setData(getCurrentTesters());
        }
    }

    /**
     * Returns an Object[][] to put in the current scheduled testers tables.
     */
    private Object[][] getCurrentTesters() {
        int i;
        Object[][] data = new Object[model.getScheduledTesters().size()][1];
        for (i = 0; i < model.getScheduledTesters().size(); i++) {
            data[i][0] = ((UserInformation) model.getScheduledTesters().get(i))
                    .getHandle();
        }
        return data;
    }


    /**
     * Returns an Object[][] representint a table of all the testers.
     */
    private Object[][] getAllTesters() {
        Object[][] tableData = new Object[model.getAvailableTesters().size()][1];
        for (int i = 0; i < model.getAvailableTesters().size(); i++) {
            tableData[i][0] = ((UserInformation) model.getAvailableTesters().get(i))
                    .getHandle();
        }
        return tableData;
    }

    public int getSelectedAvailableTesterIndex() {
        return availTestersT.getSelectedRow();
    }

    public int getSelectedScheduledTesterIndex() {
        return schedTestersT.getSelectedRow();
    }

    public int getStatus() {
        return ((HiddenValue) statusSelection.getSelectedItem()).getValue();
    }

    public int getPrimarySolution() {
        if (primarySelection.getSelectedItem() instanceof HiddenValue) {
            return ((HiddenValue) primarySelection.getSelectedItem()).getValue();
        } else {
            return -1;
        }
    }

    public String getName() {
        return "Admin";
    }
}
