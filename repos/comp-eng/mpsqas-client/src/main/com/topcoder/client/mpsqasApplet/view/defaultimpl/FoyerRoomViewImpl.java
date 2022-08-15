package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.FoyerRoomView;
import com.topcoder.client.mpsqasApplet.controller.FoyerRoomController;
import com.topcoder.client.mpsqasApplet.model.FoyerRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

/**
 * Default implementation of foyer room view.
 *
 * @author mitalub
 */
public class FoyerRoomViewImpl extends JPanelView implements FoyerRoomView {

    private final static String[] UNREAD_CORRESPONDENCE_COLS = {"Class"};

    private GridBagConstraints gbc;
    private GridBagLayout layout;

    private JLabel titleL;
    private JLabel descL;
    private JLabel unreadCorrespondenceL;
    private SortableTable unreadCorrespondenceT;
    private JScrollPane unreadCorrespondenceSP;
    private JButton viewProblemB;

    private FoyerRoomModel model;
    private FoyerRoomController controller;

    public void init() {
        model = MainObjectFactory.getFoyerRoomModel();
        controller = MainObjectFactory.getFoyerRoomController();
        model.addWatcher(this);
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        setLayout(layout);
    }

    public void update(Object arg) {
        removeAll();

        titleL = new JLabel("MPSQAS");
        titleL.setFont(DefaultUIValues.HEADER_FONT);
        if (model.isFullRoom()) {
            GUIConstants.buildConstraints(gbc, 0, 0, 2, 1, 0, 1);
        } else {
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        }
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        layout.setConstraints(titleL, gbc);
        add(titleL);

        descL = new JLabel("Member Problem Submission and Quality Assurance System.");
        if (model.isFullRoom()) {
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 1);
        } else {
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 1);
        }
        layout.setConstraints(descL, gbc);
        add(descL);

        if (model.isFullRoom()) {
            unreadCorrespondenceL = new JLabel("Problems with new correspondence ("
                    + model.getProblemList().size() + "):");
            unreadCorrespondenceL.setFont(DefaultUIValues.BOLD_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 2, 2, 1, 0, 1);
            layout.setConstraints(unreadCorrespondenceL, gbc);
            add(unreadCorrespondenceL);

            unreadCorrespondenceT = new SortableTable(UNREAD_CORRESPONDENCE_COLS,
                    getUnreadCorrespondence());
            unreadCorrespondenceSP = new JScrollPane(unreadCorrespondenceT,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            unreadCorrespondenceSP.setPreferredSize(new Dimension(300, 300));
            GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 1, 100);
            gbc.fill = GridBagConstraints.BOTH;
            layout.setConstraints(unreadCorrespondenceSP, gbc);
            add(unreadCorrespondenceSP);

            JPanel spacer = new JPanel();
            GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 2, 0);
            layout.setConstraints(spacer, gbc);
            add(spacer);

            viewProblemB = new JButton("View Problem");
            GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
            gbc.anchor = GridBagConstraints.EAST;
            gbc.fill = GridBagConstraints.NONE;
            layout.setConstraints(viewProblemB, gbc);
            add(viewProblemB);

            JPanel spacer2 = new JPanel();
            GUIConstants.buildConstraints(gbc, 0, 5, 2, 1, 0, 50);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.WEST;
            layout.setConstraints(spacer2, gbc);
            add(spacer2);

            unreadCorrespondenceT.addMouseListener(
                    new AppletMouseListener("problemRowClicked", this, "mouseClicked"));
            viewProblemB.addActionListener(
                    new AppletActionListener("processViewProblem", controller, false));
        } else {
            JPanel spacer = new JPanel();
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 100);
            gbc.fill = GridBagConstraints.BOTH;
            layout.setConstraints(spacer, gbc);
            add(spacer);
        }
    }

    /**
     * Returns a list of problems with new correspondence from problems
     * ArrayList in tabular form.
     */
    private Object[][] getUnreadCorrespondence() {
        String[] problems = model.getProblemNameList();
        Object[][] data = new Object[problems.length][1];
        for (int i = 0; i < problems.length; i++) {
            data[i][0] = problems[i];
        }
        return data;
    }

    /**
     * If a problem is double clicked, opens the problem.
     */
    public void problemRowClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            controller.processViewProblem();
        }
    }

    /**
     * Returns current selected problem index.
     */
    public int getSelectedProblemIndex() {
        return unreadCorrespondenceT.getSelectedRow();
    }
}
