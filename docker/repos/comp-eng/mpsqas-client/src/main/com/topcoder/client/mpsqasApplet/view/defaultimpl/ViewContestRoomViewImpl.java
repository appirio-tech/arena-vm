package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.netCommon.mpsqas.ContestInformation;
import com.topcoder.client.mpsqasApplet.view.ViewContestRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewContestRoomModel;
import com.topcoder.client.mpsqasApplet.controller.ViewContestRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.netCommon.mpsqas.UserInformation;
import com.topcoder.netCommon.mpsqas.ProblemInformation;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 * Default implementation of ViewContestRoomView, which holds
 * other views in a tabbed pane.
 *
 * @author mitalub
 */
public class ViewContestRoomViewImpl extends JPanelView
        implements ViewContestRoomView {

    private ViewContestRoomController controller;
    private ViewContestRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel titleLabel;
    private JTabbedPane tabbedPane;

    public void init() {
        model = MainObjectFactory.getViewContestRoomModel();
        controller = MainObjectFactory.getViewContestRoomController();

        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);

        tabbedPane = new JTabbedPane();

        model.addWatcher(this);
    }

    public void update(Object arg) {
        removeAll();
        ContestInformation contestInfo = model.getContestInformation();
        titleLabel = new JLabel("Contest Information (" + contestInfo.getContestName()
                + "):");
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
    }

    public void addComponent(ComponentView componentView) {
        String name = componentView.getName();
        tabbedPane.addTab(name, componentView);

        repaint();
    }

    public void removeAllComponents() {
        tabbedPane = new JTabbedPane();
    }
}
