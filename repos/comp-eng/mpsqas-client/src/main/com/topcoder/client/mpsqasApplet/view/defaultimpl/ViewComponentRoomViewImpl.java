package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.ViewComponentRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewComponentRoomModel;
import com.topcoder.client.mpsqasApplet.controller.ViewComponentRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.netCommon.mpsqas.ComponentInformation;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 * Default implementation of ViewComponentRoomView, which holds
 * other views in a tabbed pane.
 *
 * @author mitalub
 */
public class ViewComponentRoomViewImpl extends JPanelView
        implements ViewComponentRoomView {

    private ViewComponentRoomController controller;
    private ViewComponentRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel titleLabel;
    private JTabbedPane tabbedPane;

    private JButton saveButton;

    public void init() {
        model = MainObjectFactory.getViewComponentRoomModel();
        controller = MainObjectFactory.getViewComponentRoomController();

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
            titleLabel = new JLabel("ProblemComponent (" +
                    (className.equals("") ? "New Problem" : className) + ")");
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

            saveButton = new JButton("Save");
            saveButton.addActionListener(new AppletActionListener(
                    "processSave", controller, false));
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 1, 1);
            layout.setConstraints(saveButton, gbc);
            add(saveButton);
        }
        if (arg == null || arg.equals(UpdateTypes.PROBLEM_MODIFIED)) {
            saveButton.setEnabled(model.canSubmit());
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
