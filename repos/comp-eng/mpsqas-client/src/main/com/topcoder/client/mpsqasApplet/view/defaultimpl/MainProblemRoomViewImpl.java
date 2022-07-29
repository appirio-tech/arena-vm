package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.MainProblemRoomView;
import com.topcoder.client.mpsqasApplet.controller.MainProblemRoomController;
import com.topcoder.client.mpsqasApplet.model.MainProblemRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.netCommon.mpsqas.DifficultyConstants;
import com.topcoder.netCommon.mpsqas.StatusConstants;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.netCommon.mpsqas.ProblemInformation;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Default view implementation for Main Problem Room.
 *
 * @author mitalub
 */
public class MainProblemRoomViewImpl extends JPanelView
        implements MainProblemRoomView {

    private static String[] FULL_PROBLEM_NAMES = {"Writer", "Modified", "Class",
                                                  "Method", "Division", "Difficulty", "Status"};
    private static String[] NO_WRITER_PROBLEM_NAMES =
            {"Modified", "Class", "Method", "Division", "Difficulty", "Status"};
    private static String[] NO_STATUS_PROBLEM_NAMES =
            {"Writer", "Modified", "Class", "Method", "Division", "Difficulty"};

    private static int[] FULL_PROBLEM_WIDTHS =
            {600, 1000, 1000, 1000, 600, 600, 1000};
    private static int[] NO_WRITER_PROBLEM_WIDTHS =
            {1000, 1000, 1000, 600, 600, 1000};
    private static int[] NO_STATUS_PROBLEM_WIDTHS =
            {600, 1000, 1000, 1000, 600, 600};

    private static int[] TABLE_ORDER =
            {MessageConstants.ALL_PROBLEMS,
             MessageConstants.USER_WRITTEN_PROBLEMS,
             MessageConstants.USER_TESTING_PROBLEMS,
             MessageConstants.PENDING_APPROVAL_PROBLEMS,
             MessageConstants.PENDING_SUBMISSION_PROBLEMS};

    private static String[] TABLE_NAMES =
            {"All Problems", "Developing Problems", "Testing Problems",
             "Pending Proposals", "Pending Submissions"};

    private MainProblemRoomController controller;
    private MainProblemRoomModel model;

    private ArrayList keyOrder;  //order of the table types
    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private HashMap tables;
    private JPanel[] panels;
    private JButton[] viewButtons;
    private JButton[] newButtons;
    private Component[] components;  //split panes
    private GridBagLayout[] layouts;

    /**
     * Stores controller and model and sets up main layout.
     */
    public void init() {
        controller = MainObjectFactory.getMainProblemRoomController();
        model = MainObjectFactory.getMainProblemRoomModel();

        layout = new GridBagLayout();
        setLayout(layout);
        gbc = new GridBagConstraints();

        model.addWatcher(this);
    }

    /**
     * Creates a problem table for each pair in model.getSingleProblems().
     */
    public void update(Object arg) {
        removeAll();
        gbc.insets = new Insets(5, 5, 5, 5);

        HashMap problems = model.getProblems();

        //order the keys so the tables always come out in the same order
        int i;
        keyOrder = new ArrayList();
        ArrayList titles = new ArrayList();
        Set keySet = problems.keySet();

        for (i = 0; i < TABLE_ORDER.length; i++) {
            if (keySet.contains(new Integer(TABLE_ORDER[i]))) {
                keyOrder.add(new Integer(TABLE_ORDER[i]));
                titles.add(TABLE_NAMES[i]);
            }
        }

        //initiate the arrays
        int numTables = keyOrder.size();
        tables = new HashMap();
        panels = new JPanel[numTables];
        viewButtons = new JButton[numTables];
        newButtons = new JButton[numTables];
        components = new Component[numTables];
        layouts = new GridBagLayout[numTables];

        //layout all the tables
        SortableTable table;
        ArrayList problemList;
        JScrollPane scrollPane;
        JLabel title, spacer;
        JSplitPane splitPane;
        Object[][] tableData;
        String[] tableColumns;
        int[] tableWidths;
        gbc.insets = new Insets(5, 5, 5, 5);
        for (i = 0; i < numTables; i++) {
            //set the layouts
            layouts[i] = new GridBagLayout();
            panels[i] = new JPanel();
            panels[i].setLayout(layouts[i]);

            problemList = (ArrayList) problems.get(keyOrder.get(i));

            //add the title
            title = new JLabel(titles.get(i) + " (" + problemList.size() + "):");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 0, 2, 1, 0, 1);
            layouts[i].setConstraints(title, gbc);
            panels[i].add(title);

            //get the data and column headers for in preperation for the table
            int statusColumn, modifiedColumn;
            switch (((Integer) keyOrder.get(i)).intValue()) {
            case MessageConstants.USER_WRITTEN_PROBLEMS:
                tableData = getTableDataNoWriter(problemList);
                tableColumns = NO_WRITER_PROBLEM_NAMES;
                tableWidths = NO_WRITER_PROBLEM_WIDTHS;
                statusColumn = 5;
                modifiedColumn = 0;
                break;
            case MessageConstants.PENDING_APPROVAL_PROBLEMS:
            case MessageConstants.PENDING_SUBMISSION_PROBLEMS:
                tableData = getTableDataNoStatus(problemList);
                tableColumns = NO_STATUS_PROBLEM_NAMES;
                tableWidths = NO_STATUS_PROBLEM_WIDTHS;
                statusColumn = -1;
                modifiedColumn = 1;
                break;
            default:
                tableData = getTableData(problemList);
                tableColumns = FULL_PROBLEM_NAMES;
                tableWidths = FULL_PROBLEM_WIDTHS;
                statusColumn = 6;
                modifiedColumn = 1;
            }

            //add the table
            table = new SortableTable(tableColumns, tableData, tableWidths);
            if(statusColumn != -1)
                table.setSortOrder(statusColumn,true);//first sort by status, ascending
            if(modifiedColumn != -1)
                table.setSortOrder(modifiedColumn,false);//then sort by date, descending
            tables.put(keyOrder.get(i), table);
            scrollPane = new JScrollPane(table,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 100);
            layouts[i].setConstraints(scrollPane, gbc);
            panels[i].add(scrollPane);

            //add Create Problem button, or a spacer
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            if (((Integer) keyOrder.get(i)).intValue() ==
                    MessageConstants.USER_WRITTEN_PROBLEMS) {
                newButtons[i] = new JButton("Create Problem");
                newButtons[i].addActionListener(new AppletActionListener(
                        "processCreateProblem", controller, false));
                GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 99, 1);
                layouts[i].setConstraints(newButtons[i], gbc);
                panels[i].add(newButtons[i]);
            } else {
                spacer = new JLabel();
                GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 99, 1);
                layouts[i].setConstraints(spacer, gbc);
                panels[i].add(spacer);
            }

            //add the view problem button
            viewButtons[i] = new JButton("View Problem");
            viewButtons[i].addActionListener(new AppletActionListener(
                    "viewButtonPressed", this, true));
            GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 1, 1);
            layouts[i].setConstraints(viewButtons[i], gbc);
            panels[i].add(viewButtons[i]);

            //make the next nested split pane.
            if (i == 0) {
                //base case, no split pane, just the panel
                components[i] = panels[i];
            } else {
                //make a split pane between this panel and the previous component
                splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                splitPane.setDividerLocation(i * 400 / numTables);
                splitPane.setTopComponent(components[i - 1]);
                splitPane.setBottomComponent(panels[i]);
                components[i] = splitPane;
            }
        }

        //add the primary component, the last element in the component array.
        if (components.length > 0) {
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(components[components.length - 1], gbc);
            add(components[components.length - 1]);
        }
    }

    /**
     * Returns Object[][] to initiate table with all columns.
     */
    private Object[][] getTableData(ArrayList problems) {
        Object[][] data = new Object[problems.size()][7];
        ProblemInformation info;
        for (int i = 0; i < problems.size(); i++) {
            info = (ProblemInformation) problems.get(i);
            data[i][0] = info.getWriter().getHandle();
            data[i][1] = info.getLastModified();
            data[i][2] = info.getProblemComponents()[0].getClassName();
            data[i][3] = info.getProblemComponents()[0].getMethodName();
            data[i][4] = DifficultyConstants.getDivisionName(info.getDivision());
            data[i][5] = DifficultyConstants.getDifficultyName(
                    info.getDifficulty());
            data[i][6] = StatusConstants.getStatusName(info.getStatus());
        }
        return data;
    }

    /**
     * Returns Object[][] to initiate table with no writer column.
     */
    private Object[][] getTableDataNoWriter(ArrayList problems) {
        Object[][] data = new Object[problems.size()][6];
        ProblemInformation info;
        for (int i = 0; i < problems.size(); i++) {
            info = (ProblemInformation) problems.get(i);
            data[i][0] = info.getLastModified();
            data[i][1] = info.getProblemComponents()[0].getClassName();
            data[i][2] = info.getProblemComponents()[0].getMethodName();
            data[i][3] = DifficultyConstants.getDivisionName(info.getDivision());
            data[i][4] = DifficultyConstants.getDifficultyName(
                    info.getDifficulty());
            data[i][5] = StatusConstants.getStatusName(info.getStatus());
        }
        return data;
    }

    /**
     * Returns Object[][] to initiate table with no status column.
     */
    private Object[][] getTableDataNoStatus(ArrayList problems) {
        Object[][] data = new Object[problems.size()][6];
        ProblemInformation info;
        for (int i = 0; i < problems.size(); i++) {
            info = (ProblemInformation) problems.get(i);
            data[i][0] = info.getWriter().getHandle();
            data[i][1] = info.getLastModified();
            data[i][2] = info.getProblemComponents()[0].getClassName();
            data[i][3] = info.getProblemComponents()[0].getMethodName();
            data[i][4] = DifficultyConstants.getDivisionName(info.getDivision());
            data[i][5] = DifficultyConstants.getDifficultyName(
                    info.getDifficulty());
        }
        return data;
    }

    /**
     * @param tableTypeId The id of the table to get the selected row from.
     */
    public int getSelectedProblemIndex(int tableTypeId) {
        return ((JTable) tables.get(new Integer(tableTypeId))).getSelectedRow();
    }

    /**
     * Called when a view button is clicked.  Determines which table's view
     * button was clicked and lets the controller know.
     */
    public void viewButtonPressed(ActionEvent e) {
        for (int i = 0; i < viewButtons.length; i++) {
            if (e.getSource() == viewButtons[i]) {
                controller.processViewProblem(((Integer) keyOrder.get(i)).intValue());
                return;
            }
        }
    }
}
