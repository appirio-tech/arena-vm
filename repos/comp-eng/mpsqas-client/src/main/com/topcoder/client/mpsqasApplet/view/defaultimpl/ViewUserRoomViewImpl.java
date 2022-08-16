package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.ViewUserRoomView;
import com.topcoder.client.mpsqasApplet.model.ViewUserRoomModel;
import com.topcoder.client.mpsqasApplet.controller.ViewUserRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.netCommon.mpsqas.*;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 * A Room to display detailed information about a user.
 *
 * @author mitalub
 */
public class ViewUserRoomViewImpl extends JPanelView
        implements ViewUserRoomView {

    private final static String[] PROBLEM_COLS = {"Time", "Problem", "Type",
                                                  "Components", "Division", "Difficulty", "Status", "User Type",
                                                  "Paid", "Pending"};

    private final static int[] PROBLEM_WIDTHS = {300, 300, 300, 300, 300, 300,
                                                 300, 300, 300, 300};

    private final static boolean[] PROBLEM_EDITABLES = {false, false, false,
                                                        false, false, false, false, false, false, true};

    private ViewUserRoomController controller;
    private ViewUserRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel titleL;
    private JLabel handleL;
    private JLabel nameL;
    private JLabel emailL;
    private JLabel paidL;
    private JLabel pendingL;
    private JLabel userTypeL;
    private JLabel problemsL;

    private JTextField handleTF;
    private JTextField nameTF;
    private JTextField emailTF;
    private JTextField paidTF;
    private JTextField pendingTF;
    private JTextField userTypeTF;

    private SortableTable problemsT;
    private JScrollPane problemsSP;

    private JButton saveButton;
    private JButton viewProblemButton;

    public void init() {
        model = MainObjectFactory.getViewUserRoomModel();
        controller = MainObjectFactory.getViewUserRoomController();

        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);

        model.addWatcher(this);
    }

    public void update(Object arg) {
        removeAll();
        UserInformation userInfo = model.getUserInformation();

        titleL = new JLabel("User Information: ");
        titleL.setFont(DefaultUIValues.HEADER_FONT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        GUIConstants.buildConstraints(gbc, 0, 0, 4, 1, 0, 1);
        add(titleL);

        handleL = new JLabel("Handle: ");
        GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 1);
        layout.setConstraints(handleL, gbc);
        add(handleL);

        handleTF = new JTextField(userInfo.getHandle());
        handleTF.setEditable(false);
        handleTF.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 100, 0);
        layout.setConstraints(handleTF, gbc);
        add(handleTF);

        nameL = new JLabel("Name: ");
        GUIConstants.buildConstraints(gbc, 2, 1, 1, 1, 1, 0);
        layout.setConstraints(nameL, gbc);
        add(nameL);

        nameTF = new JTextField(userInfo.getFirstName() + " " +
                userInfo.getLastName());
        nameTF.setEditable(false);
        nameTF.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 3, 1, 1, 1, 100, 0);
        layout.setConstraints(nameTF, gbc);
        add(nameTF);

        userTypeL = new JLabel("User Type: ");
        GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
        layout.setConstraints(userTypeL, gbc);
        add(userTypeL);

        StringBuffer userType = new StringBuffer(100);
        if (userInfo.isWriter()) {
            userType.append("Writer");
        }
        if (userInfo.isTester()) {
            if (userInfo.isWriter()) {
                userType.append(" and ");
            }
            userType.append("Tester");
        }

        userTypeTF = new JTextField(userType.toString());
        userTypeTF.setEditable(false);
        userTypeTF.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 0);
        layout.setConstraints(userTypeTF, gbc);
        add(userTypeTF);

        emailL = new JLabel("Email: ");
        GUIConstants.buildConstraints(gbc, 2, 2, 1, 1, 0, 1);
        layout.setConstraints(emailL, gbc);
        add(emailL);

        emailTF = new JTextField(userInfo.getEmail());
        emailTF.setEditable(false);
        emailTF.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 3, 2, 1, 1, 0, 0);
        layout.setConstraints(emailTF, gbc);
        add(emailTF);

        paidL = new JLabel("Paid to Date:");
        GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 1);
        layout.setConstraints(paidL, gbc);
        add(paidL);

        paidTF = new JTextField("$" + userInfo.getPaid());
        paidTF.setEditable(false);
        paidTF.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 0, 0);
        layout.setConstraints(paidTF, gbc);
        add(paidTF);

        pendingL = new JLabel("Pending Payment:");
        GUIConstants.buildConstraints(gbc, 2, 3, 1, 1, 0, 0);
        layout.setConstraints(pendingL, gbc);
        add(pendingL);

        pendingTF = new JTextField("$" + userInfo.getPending());
        pendingTF.setEditable(false);
        pendingTF.setBackground(Color.white);
        GUIConstants.buildConstraints(gbc, 3, 3, 1, 1, 0, 0);
        layout.setConstraints(pendingTF, gbc);
        add(pendingTF);

        problemsL = new JLabel("Writing Problems: ");
        GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
        layout.setConstraints(problemsL, gbc);
        add(problemsL);

        problemsT = new SortableTable(PROBLEM_COLS,
                getProblems(),
                PROBLEM_WIDTHS,
                PROBLEM_EDITABLES);
        problemsSP = new JScrollPane(problemsT,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GUIConstants.buildConstraints(gbc, 0, 5, 4, 1, 0, 100);
        gbc.fill = GridBagConstraints.BOTH;
        layout.setConstraints(problemsSP, gbc);
        add(problemsSP);

        Box box = Box.createHorizontalBox();
        viewProblemButton = new JButton("View Problem");
        viewProblemButton.addActionListener(new AppletActionListener(
                "processViewProblem", controller, false));
        box.add(viewProblemButton);
        box.add(Box.createHorizontalStrut(5));

        saveButton = new JButton("Save Pending Amounts");
        saveButton.addActionListener(new AppletActionListener(
                "processSave", controller, false));
        box.add(saveButton);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        GUIConstants.buildConstraints(gbc, 0, 6, 4, 1, 0, 1);
        layout.setConstraints(box, gbc);
        add(box);
    }

    /**
     * Returns the problems the user is associated with
     */
    private Object[][] getProblems() {
        UserInformation userInfo = model.getUserInformation();
        Object[][] data = new Object[userInfo.getProblems().size()][10];
        ProblemInformation problemInfo;
        for (int i = 0; i < userInfo.getProblems().size(); i++) {
            problemInfo = (ProblemInformation) userInfo.getProblems().get(i);
            data[i][0] = problemInfo.getLastModified();
            data[i][1] = problemInfo.getName();
            data[i][2] = problemInfo.getProblemTypeID() ==
                    ApplicationConstants.SINGLE_PROBLEM ?
                    "Single" : problemInfo.getProblemTypeID() ==
                        ApplicationConstants.TEAM_PROBLEM ? "Team" : "Long";
            data[i][3] = new Integer(problemInfo.getNumComponents());
            data[i][4] = DifficultyConstants.getDivisionName(
                    problemInfo.getDivision());
            data[i][5] = DifficultyConstants.getDifficultyName(
                    problemInfo.getDifficulty());
            data[i][6] = StatusConstants.getStatusName(problemInfo.getStatus());
            data[i][7] = problemInfo.getUserType() ==
                    ApplicationConstants.PROBLEM_WRITER ? "Writer" : "Tester";
            data[i][8] = new Double(problemInfo.getPaid());
            data[i][9] = new Double(problemInfo.getPending());
        }
        return data;
    }

    /**
     * Returns the amount in the pending column of the indexth row.
     */
    public double getPending(int index) {
        return Double.parseDouble(problemsT.getAbsoluteValueAt(index, 9)
                .toString());
    }

    public int getSelectedProblemIndex() {
        return problemsT.getSelectedRow();
    }
}
