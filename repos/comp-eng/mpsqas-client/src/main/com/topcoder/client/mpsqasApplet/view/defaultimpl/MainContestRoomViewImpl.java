package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.MainContestRoomView;
import com.topcoder.client.mpsqasApplet.model.MainContestRoomModel;
import com.topcoder.client.mpsqasApplet.controller.MainContestRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.netCommon.mpsqas.ContestInformation;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Default implementation of Main Contest Room View.
 *
 * @author mitalub
 */
public class MainContestRoomViewImpl extends JPanelView
        implements MainContestRoomView {

    private static final String[] MAIN_CONTEST_COLS = {"Name", "Time", "Role"};

    private static final int[] MAIN_CONTEST_COLS_WIDTHS = {600, 200, 140};

    private MainContestRoomController controller;
    private MainContestRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    //components
    private JLabel title;
    private JTable contestTable;
    private JScrollPane contestTablePane;
    private JButton viewButton;

    public void init() {
        model = MainObjectFactory.getMainContestRoomModel();
        controller = MainObjectFactory.getMainContestRoomController();

        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);

        model.addWatcher(this);
    }

    public void update(Object arg) {
        removeAll();

        title = new JLabel("Your Contests:");
        title.setFont(DefaultUIValues.HEADER_FONT);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        layout.setConstraints(title, gbc);
        add(title);

        Object[][] tableData;
        int i,j;
        ArrayList contests = model.getContests();
        ContestInformation contestInfo;
        tableData = new Object[contests.size()][3];
        for (i = 0; i < contests.size(); i++) {
            contestInfo = (ContestInformation) contests.get(i);
            tableData[i][0] = contestInfo.getContestName();
            tableData[i][1] = contestInfo.getStartCoding();
            tableData[i][2] = contestInfo.getRole();
        }

        contestTable = new SortableTable(MAIN_CONTEST_COLS,
                tableData,
                MAIN_CONTEST_COLS_WIDTHS);

        contestTablePane = new JScrollPane(contestTable,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
        layout.setConstraints(contestTablePane, gbc);
        add(contestTablePane);

        viewButton = new JButton("View Contest");
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
        layout.setConstraints(viewButton, gbc);
        add(viewButton);

        contestTable.addMouseListener(new AppletMouseListener(
                "contestRowClicked", this, "mouseClicked"));
        viewButton.addActionListener(new AppletActionListener(
                "processViewContest", controller, false));
    }

    /**
     * Called when a user clicks one of the contests in the contest table.
     *
     * @param e The MouseEvent of the row being clicked.
     */
    public void contestRowClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e))
            controller.processViewContest();
    }

    public int getSelectedContestIndex() {
        return contestTable.getSelectedRow();
    }
}
