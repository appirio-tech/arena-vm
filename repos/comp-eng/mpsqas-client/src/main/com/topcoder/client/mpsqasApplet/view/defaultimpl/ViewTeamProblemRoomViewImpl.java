package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.ViewTeamProblemRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewTeamProblemRoomModel;
import com.topcoder.client.mpsqasApplet.controller.ViewTeamProblemRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;

import java.awt.*;
import javax.swing.*;

/**
 * Default implementation of ViewTeamProblemRoomView, which holds
 * other component views in a tabbed pane.
 *
 * @author mitalub
 */
public class ViewTeamProblemRoomViewImpl extends JPanelView
        implements ViewTeamProblemRoomView {

    private ViewTeamProblemRoomController controller;
    private ViewTeamProblemRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel titleLabel;
    private JTabbedPane tabbedPane;

    private JButton submitButton;
    private JButton saveStatementButton;

    public void init() {
        model = MainObjectFactory.getViewTeamProblemRoomModel();
        controller = MainObjectFactory.getViewTeamProblemRoomController();

        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);

        tabbedPane = new JTabbedPane();

        model.addWatcher(this);
    }

    /**
     * Lays out the panel with an empty tabbed pane.  Also, checks if
     * the user can submit and sets the submit button enabled property
     * accordingly.
     */
    public void update(Object arg) {
        if (arg == null) {
            removeAll();
            String className = model.getProblemInformation().getName();
            titleLabel = new JLabel("Problem (" +
                    (className.equals("") ? "New Problem" : className) + "):");

            titleLabel.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(titleLabel, gbc);
            add(titleLabel);

            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 100);
            layout.setConstraints(tabbedPane, gbc);
            add(tabbedPane);

            Box buttonBox = Box.createHorizontalBox();

            submitButton = new JButton("Submit");
            buttonBox.add(submitButton);
            if (model.isStatementEditable()) {
                buttonBox.add(Box.createHorizontalStrut(5));
                saveStatementButton = new JButton("Save Statement");
                buttonBox.add(saveStatementButton);
            }
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 0, 2, 3, 1, 0, 1);
            layout.setConstraints(buttonBox, gbc);
            add(buttonBox);

            submitButton.addActionListener(new AppletActionListener(
                    "processSubmit", controller, false));
            if (model.isStatementEditable()) {
                saveStatementButton.addActionListener(new AppletActionListener(
                        "processSaveStatement", controller, false));
            }
        }
        if (arg == null || arg.equals(UpdateTypes.PROBLEM_MODIFIED)) {
            submitButton.setEnabled(model.canSubmit());
            if (model.isStatementEditable()) {
                saveStatementButton.setEnabled(model.canSubmit());
            }
        }
    }

    /**
     * Adds a ComponentView to the tabbed pane.
     */
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
}
