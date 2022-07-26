/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.ViewProblemRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewProblemRoomModel;
import com.topcoder.client.mpsqasApplet.controller.ViewProblemRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.netCommon.mpsqas.HiddenValue;
import com.topcoder.netCommon.mpsqas.DifficultyConstants;

import java.awt.*;
import javax.swing.*;

/**
 * Default implementation of ViewProblemRoomView, which holds
 * other views in a tabbed pane.
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #update(Object)} method.</li>
 * </ol>
 * </p>
 * @author mitalub, savon_cn
 * @version 1.0
 */
public class ViewProblemRoomViewImpl extends JPanelView
        implements ViewProblemRoomView {

    private ViewProblemRoomController controller;
    private ViewProblemRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel titleLabel;
    private JTabbedPane tabbedPane;

    private JButton submitButton;        //Submit
    private JButton saveStatementButton; //Save Statement

    private JComboBox divisionBox;
    private JComboBox difficultyBox;

    public void init() {
        model = MainObjectFactory.getViewProblemRoomModel();
        controller = MainObjectFactory.getViewProblemRoomController();

        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);

        tabbedPane = new JTabbedPane();

        model.addWatcher(this);
    }

    /**
     * <p>
     * update the view problem event change.
     * </p>
     * @param arg the component arguments.
     */
    public void update(Object arg) {
        if (arg == null) {
            removeAll();
            String className = model.getComponentInformation().getClassName();
            titleLabel = new JLabel("Problem (" +
                    (className.equals("") ? "New Problem" : className) + "):");
            titleLabel.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 100, 1);
            layout.setConstraints(titleLabel, gbc);
            add(titleLabel);

            int selectedIndex = 0;
            divisionBox = new JComboBox();
            for (int i = 0; i < DifficultyConstants.DIVISION_IDS.length; i++) {
                if (model.getProblemInformation().getDivision() ==
                        DifficultyConstants.DIVISION_IDS[i]) {
                    selectedIndex = i;
                }
                divisionBox.addItem(DifficultyConstants.getDivisionName(
                        DifficultyConstants.DIVISION_IDS[i]));
            }
            divisionBox.setSelectedIndex(selectedIndex);
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
            layout.setConstraints(divisionBox, gbc);
            add(divisionBox);

            difficultyBox = new JComboBox();
            for (int i = 0; i < DifficultyConstants.DIFFICULTY_IDS.length; i++) {
                if (model.getProblemInformation().getDifficulty() ==
                        DifficultyConstants.DIFFICULTY_IDS[i]) {
                    selectedIndex = i;
                }
                difficultyBox.addItem(DifficultyConstants.getDifficultyName(
                        DifficultyConstants.DIFFICULTY_IDS[i]));
            }
            difficultyBox.setSelectedIndex(selectedIndex);
            GUIConstants.buildConstraints(gbc, 2, 0, 1, 1, 1, 0);
            layout.setConstraints(difficultyBox, gbc);
            add(difficultyBox);

            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 3, 1, 0, 100);
            layout.setConstraints(tabbedPane, gbc);
            add(tabbedPane);

            Box buttonBox = Box.createHorizontalBox();

            submitButton = new JButton("Submit");
            buttonBox.add(submitButton);
            if (model.isStatementEditable()) {
                buttonBox.add(Box.createHorizontalStrut(5));
                saveStatementButton = new JButton("Save Statement");
                buttonBox.add(saveStatementButton);
                saveStatementButton.addActionListener(new AppletActionListener(
                        "processSaveStatement", controller, false));
            }
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 0, 2, 3, 1, 0, 1);
            layout.setConstraints(buttonBox, gbc);
            add(buttonBox);

            submitButton.addActionListener(new AppletActionListener(
                    "processSubmit", controller, false));

        }
        if (arg == null || arg.equals(UpdateTypes.PROBLEM_MODIFIED)) {
            submitButton.setEnabled(model.canSubmit());
            if(saveStatementButton!=null) {
                saveStatementButton.setEnabled(model.canSubmit());
            }
        }
    }

    public void addComponent(ComponentView componentView) {
        String name = componentView.getName();
        tabbedPane.addTab(name, componentView);
        repaint();
    }

    /**
     * Removes all the tabs from the TabbedPane.
     */
    public void removeAllComponents() {
        tabbedPane.removeAll();
    }

    public int getDivision() {
        return ((HiddenValue) divisionBox.getSelectedItem()).getValue();
    }

    public int getDifficulty() {
        return ((HiddenValue) difficultyBox.getSelectedItem()).getValue();
    }
}
