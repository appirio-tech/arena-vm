package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable.*;
import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.MainTeamProblemRoomView;
import com.topcoder.client.mpsqasApplet.controller.MainTeamProblemRoomController;
import com.topcoder.client.mpsqasApplet.model.MainTeamProblemRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.problem.ProblemComponent;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * Default view implementation for Main Problem Room.
 *
 * @author mitalub
 */
public class MainTeamProblemRoomViewImpl extends JPanelView
        implements MainTeamProblemRoomView {

    private final static String[] NO_WRITER_PROBLEM_NAMES =
            {"Problem", "Last Modified", "Status"};
    private final static int[] NO_WRITER_PROBLEM_WIDTHS =
            {100, 100, 100};
    private final static String[] NO_STATUS_PROBLEM_NAMES =
            {"Problem", "Writer", "Last Modified"};
    private final static int[] NO_STATUS_PROBLEM_WIDTHS =
            {100, 100, 100};
    private final static String[] FULL_PROBLEM_NAMES =
            {"Problem", "Writer", "Last Modified", "Status"};
    private final static int[] FULL_PROBLEM_WIDTHS =
            {100, 100, 100, 100};

    private static int[] TABLE_ORDER =
            {MessageConstants.ALL_PROBLEMS,
             MessageConstants.USER_WRITTEN_PROBLEMS,
             MessageConstants.USER_TESTING_PROBLEMS,
             MessageConstants.PENDING_APPROVAL_PROBLEMS,
             MessageConstants.PENDING_SUBMISSION_PROBLEMS};

    private static String[] TABLE_NAMES =
            {"All Problems", "Developing Problems", "Testing Problems",
             "Pending Proposals", "Pending Submissions"};

    private MainTeamProblemRoomController controller;
    private MainTeamProblemRoomModel model;

    private ArrayList keyOrder; //order of the table types
    private HashMap tables;
    private JPanel[] panels;
    private JButton[] viewButtons;
    private JButton[] newButtons;
    private Component[] components;  //split panes
    private GridBagLayout[] layouts;

    //layout
    private GridBagLayout layout;
    private GridBagConstraints gbc;

    /**
     * Peforms initial tasks of storing model and controller and setting
     * up the layout.
     */
    public void init() {
        controller = MainObjectFactory.getMainTeamProblemRoomController();
        model = MainObjectFactory.getMainTeamProblemRoomModel();

        layout = new GridBagLayout();
        setLayout(layout);
        gbc = new GridBagConstraints();
        model.addWatcher(this);
    }

    /**
     * Lays out screen based on problem list in model.  Arguments are ignored.
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

        //layout out all the tables
        TreeTable treeTable;
        ArrayList problemList;
        JScrollPane scrollPane;
        JLabel title, spacer;
        JSplitPane splitPane;
        MutableTreeTableNode root;
        String[] tableColumns;
        int[] tableWidths;

        for (i = 0; i < numTables; i++) {
            //set the layouts
            layouts[i] = new GridBagLayout();
            panels[i] = new JPanel(layouts[i]);

            problemList = (ArrayList) problems.get(keyOrder.get(i));

            title = new JLabel(titles.get(i) + " (" + problemList.size() + "):");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 0, 2, 1, 0, 1);
            layouts[i].setConstraints(title, gbc);
            panels[i].add(title);

            switch (((Integer) keyOrder.get(i)).intValue()) {
            case MessageConstants.USER_WRITTEN_PROBLEMS:
                root = getTreeRootNoWriter(problemList);
                tableColumns = NO_WRITER_PROBLEM_NAMES;
                tableWidths = NO_WRITER_PROBLEM_WIDTHS;
                break;
            case MessageConstants.PENDING_APPROVAL_PROBLEMS:
            case MessageConstants.PENDING_SUBMISSION_PROBLEMS:
                root = getTreeRootNoStatus(problemList);
                tableColumns = NO_STATUS_PROBLEM_NAMES;
                tableWidths = NO_STATUS_PROBLEM_WIDTHS;
                break;
            default:
                root = getTreeRoot(problemList);
                tableColumns = FULL_PROBLEM_NAMES;
                tableWidths = FULL_PROBLEM_WIDTHS;
            }

            //add the treetable
            treeTable = new TreeTable(root, tableColumns, tableWidths);
            treeTable.getTree().setRootVisible(false);
            tables.put(keyOrder.get(i), treeTable);
            scrollPane = new JScrollPane(treeTable,
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
     * Returns the root to initiate a tree table with all columns.
     */
    private MutableTreeTableNode getTreeRoot(ArrayList problems) {
        MutableTreeTableNode root = new MutableTreeTableNode(
                new Object[]{new HiddenValue("", -1), "", "", ""});
        MutableTreeTableNode node, node2;
        ProblemInformation info;
        ComponentInformation component;

        //for each problem, create a node for the problem, and then leafs for each
        //component in the problem.
        for (int i = 0; i < problems.size(); i++) {
            info = (ProblemInformation) problems.get(i);
            node = new MutableTreeTableNode(new Object[]{new HiddenValue(
                    info.getName(), info.getProblemId()),
                                                         info.getWriter().getHandle(),
                                                         info.getLastModified(),
                                                         StatusConstants.getStatusName(info.getStatus())});
            root.add(node);
            for (int j = 0; j < info.getProblemComponents().length; j++) {
                component = (ComponentInformation) info.getProblemComponents()[j];
                node2 = new MutableTreeTableNode(new Object[]{new HiddenValue(
                        component.getClassName(), component.getComponentId()),
                                                              info.getWriter().getHandle(),
                                                              component.getLastModified(),
                                                              StatusConstants.getStatusName(info.getStatus())});
                node.add(node2);
            }
        }
        return root;
    }

    /**
     * Returns the root to initiate a tree table with all columns except writer.
     */
    private MutableTreeTableNode getTreeRootNoWriter(ArrayList problems) {
        MutableTreeTableNode root = new MutableTreeTableNode(
                new Object[]{new HiddenValue("", -1), "", ""});
        MutableTreeTableNode node, node2;
        ProblemInformation info;
        ComponentInformation component;

        //for each problem, create a node for the problem, and then leafs for each
        //component in the problem.
        for (int i = 0; i < problems.size(); i++) {
            info = (ProblemInformation) problems.get(i);
            node = new MutableTreeTableNode(new Object[]{new HiddenValue(
                    info.getName(), info.getProblemId()),
                                                         info.getLastModified(),
                                                         StatusConstants.getStatusName(info.getStatus())});
            root.add(node);
            for (int j = 0; j < info.getProblemComponents().length; j++) {
                component = (ComponentInformation) info.getProblemComponents()[j];
                node2 = new MutableTreeTableNode(new Object[]{new HiddenValue(
                        component.getClassName(), component.getComponentId()),
                                                              component.getLastModified(),
                                                              StatusConstants.getStatusName(info.getStatus())});
                node.add(node2);
            }
        }
        return root;
    }

    /**
     * Returns the root to initiate a tree table with all columns except status.
     */
    private MutableTreeTableNode getTreeRootNoStatus(ArrayList problems) {
        MutableTreeTableNode root = new MutableTreeTableNode(
                new Object[]{new HiddenValue("", -1), "", ""});
        MutableTreeTableNode node, node2;
        ProblemInformation info;
        ComponentInformation component;

        //for each problem, create a node for the problem, and then leafs for each
        //component in the problem.
        for (int i = 0; i < problems.size(); i++) {
            info = (ProblemInformation) problems.get(i);
            node = new MutableTreeTableNode(new Object[]{new HiddenValue(
                    info.getName(), info.getProblemId()),
                                                         info.getWriter().getHandle(),
                                                         info.getLastModified()});
            root.add(node);
            for (int j = 0; j < info.getProblemComponents().length; j++) {
                component = (ComponentInformation) info.getProblemComponents()[j];
                node2 = new MutableTreeTableNode(new Object[]{new HiddenValue(
                        component.getClassName(),
                        component.getComponentId()),
                                                              info.getWriter().getHandle(),
                                                              component.getLastModified()});
                node.add(node2);
            }
        }
        return root;
    }

    /**
     * Called when the view button is clicked for a table, determines the
     * table type of the table and calls the controllers method to process
     * the selection.
     */
    public void viewButtonPressed(ActionEvent e) {
        for (int i = 0; i < viewButtons.length; i++) {
            if (e.getSource() == viewButtons[i]) {
                controller.processViewProblem(((Integer) keyOrder.get(i)).intValue());
                return;
            }
        }
    }

    /**
     * Returns the selected path in the specified table.
     */
    public Object[] getSelectedProblemPath(int tableTypeId) {
        TreePath path = ((TreeTable) tables.get(new Integer(tableTypeId))).getTree()
                .getSelectionPath();
        return path == null ? null : path.getPath();
    }
}
