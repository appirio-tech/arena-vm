package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.ViewLongProblemRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewLongProblemRoomModel;
import com.topcoder.client.mpsqasApplet.controller.ViewLongProblemRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;

import java.awt.*;
import javax.swing.*;

/**
 * Default implementation of ViewLongProblemRoomView, which holds
 * other views in a tabbed pane.
 *
 * @author mktong
 */
public class ViewLongProblemRoomViewImpl extends JPanelView
        implements ViewLongProblemRoomView {

    private ViewLongProblemRoomController controller;
    private ViewLongProblemRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel titleLabel;
    private JTabbedPane tabbedPane;

    private JButton submitButton;        //Submit
    private JButton saveStatementButton; //Save Statement
    private JButton cancelTestsButton; //Save Statement

    public void init() {
        model = MainObjectFactory.getViewLongProblemRoomModel();
        controller = MainObjectFactory.getViewLongProblemRoomController();

        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);

        tabbedPane = new JTabbedPane();

        model.addWatcher(this);
    }

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

            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
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
            buttonBox.add(Box.createHorizontalStrut(5));
            cancelTestsButton = new JButton("Cancel Tests");
            cancelTestsButton.setToolTipText("Cancels all scheduled tests.");
            buttonBox.add(cancelTestsButton);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
            layout.setConstraints(buttonBox, gbc);
            add(buttonBox);

            submitButton.addActionListener(new AppletActionListener(
                    "processSubmit", controller, false));
            if (saveStatementButton != null) {
                saveStatementButton.addActionListener(new AppletActionListener(
                        "processSaveStatement", controller, false));
            }
            cancelTestsButton.addActionListener(new AppletActionListener(
                    "processCancelTests", controller, false));
        }
        if (arg == null || arg.equals(UpdateTypes.PROBLEM_MODIFIED)) {
            submitButton.setEnabled(model.canSubmit());
            if (saveStatementButton != null) {
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
}
