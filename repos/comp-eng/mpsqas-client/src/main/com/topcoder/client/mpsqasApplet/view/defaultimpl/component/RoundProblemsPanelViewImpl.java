package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable.*;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.component.RoundProblemsPanelView;
import com.topcoder.client.mpsqasApplet.model.component.RoundProblemsPanelModel;
import com.topcoder.client.mpsqasApplet.controller.component.RoundProblemsPanelController;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;

/**
 * A panel on which a user can view some quick info about round problems, such
 * as the writer, difficulty, and problem statement.  From this panel, a user
 * can also go the the main problem panel for the problem.
 *
 * @author mitalub
 */
public class RoundProblemsPanelViewImpl extends RoundProblemsPanelView {

    private final static String[] SINGLE_PROB_COLS =
            {"Class", "Writer", "Division", "Points"};
    private final static int[] SINGLE_PROB_WIDTHS =
            {100, 100, 70, 70};
    private final static String[] TEAM_PROB_COLS = {"Name", "Writer"};
    private final static int[] TEAM_PROB_WIDTHS = {100, 100};
    private final static String[] LONG_PROB_COLS = {"Class", "Writer"};
    private final static int[] LONG_PROB_WIDTHS = {100, 100};

    private RoundProblemsPanelModel model;
    private RoundProblemsPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel singleProblemsLabel;
    private JLabel teamProblemsLabel;
    private JLabel longProblemsLabel;
    private SortableTable singleProblemsTable;
    private TreeTable teamProblemsTable;
    private SortableTable longProblemsTable;
    private JScrollPane singleProblemsTablePane;
    private JScrollPane teamProblemsTablePane;
    private JScrollPane longProblemsTablePane;
    private JButton viewSingleProblemButton;
    private JButton viewTeamProblemButton;
    private JButton viewLongProblemButton;

    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();
        setLayout(layout);
    }

    /**
     * Creates, lays out, and populates the components.
     */
    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            singleProblemsLabel = new JLabel("Single Problems:");
            singleProblemsLabel.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(singleProblemsLabel, gbc);
            add(singleProblemsLabel);

            teamProblemsLabel = new JLabel("Team Problems:");
            teamProblemsLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
            layout.setConstraints(teamProblemsLabel, gbc);
            add(teamProblemsLabel);
            
            longProblemsLabel = new JLabel("Long Problems:");
            longProblemsLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 2, 0, 1, 1, 1, 0);
            layout.setConstraints(longProblemsLabel, gbc);
            add(longProblemsLabel);

            singleProblemsTable = new SortableTable(SINGLE_PROB_COLS,
                    getSingleProblemsTable(),
                    SINGLE_PROB_WIDTHS);
            singleProblemsTablePane = new JScrollPane(singleProblemsTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
            layout.setConstraints(singleProblemsTablePane, gbc);
            add(singleProblemsTablePane);

            teamProblemsTable = new TreeTable(getTeamProblemsRoot(),
                    TEAM_PROB_COLS,
                    TEAM_PROB_WIDTHS);
            teamProblemsTable.getTree().setRootVisible(false);
            teamProblemsTablePane = new JScrollPane(teamProblemsTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 0, 0);
            layout.setConstraints(teamProblemsTablePane, gbc);
            add(teamProblemsTablePane);
            
            longProblemsTable = new SortableTable(LONG_PROB_COLS,
                    getLongProblemsTable(),
                    LONG_PROB_WIDTHS);
            longProblemsTablePane = new JScrollPane(longProblemsTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            GUIConstants.buildConstraints(gbc, 2, 1, 1, 1, 0, 0);
            layout.setConstraints(longProblemsTablePane, gbc);
            add(longProblemsTablePane);

            viewSingleProblemButton = new JButton("View Problem");
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
            layout.setConstraints(viewSingleProblemButton, gbc);
            add(viewSingleProblemButton);

            viewTeamProblemButton = new JButton("View Problem");
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 1);
            layout.setConstraints(viewTeamProblemButton, gbc);
            add(viewTeamProblemButton);
            
            viewLongProblemButton = new JButton("View Problem");
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 2, 2, 1, 1, 0, 1);
            layout.setConstraints(viewLongProblemButton, gbc);
            add(viewLongProblemButton);

            viewSingleProblemButton.addActionListener(new AppletActionListener(
                    "processViewSingleProblem", controller, false));
            viewTeamProblemButton.addActionListener(new AppletActionListener(
                    "processViewTeamProblem", controller, false));
            viewLongProblemButton.addActionListener(new AppletActionListener(
                    "processViewLongProblem", controller, false));
        }
    }

    /**
     * Returns an Object[][] of the problems to put in the table.
     */
    private Object[][] getSingleProblemsTable() {
        ArrayList problems = model.getSingleProblems();
        Object[][] tableData = new Object[problems.size()][5];
        for (int i = 0; i < problems.size(); i++) {
            tableData[i][0] = ((ProblemInformation) problems.get(i))
                    .getProblemComponents()[0].getClassName();
            tableData[i][1] = ((ProblemInformation) problems.get(i)).getWriter()
                    .getHandle();
            tableData[i][2] = DifficultyConstants.getDivisionName(
                    ((ProblemInformation) problems.get(i)).getDivision());
            tableData[i][3] = new Double(((ProblemInformation) problems.get(i))
                    .getPoints());
        }
        return tableData;
    }
    
    /**
     * Returns an Object[][] of the long problems to put in the table.
     */
    private Object[][] getLongProblemsTable() {
        ArrayList problems = model.getLongProblems();
        Object[][] tableData = new Object[problems.size()][3];
        for (int i = 0; i < problems.size(); i++) {
            tableData[i][0] = ((ProblemInformation) problems.get(i))
                    .getProblemComponents()[0].getClassName();
            tableData[i][1] = ((ProblemInformation) problems.get(i)).getWriter()
                    .getHandle();
        }
        return tableData;
    }

    /**
     * Returns the root of the team problem tree.
     */
    private MutableTreeTableNode getTeamProblemsRoot() {
        ArrayList problems = model.getTeamProblems();
        MutableTreeTableNode root = new MutableTreeTableNode(
                new Object[]{new HiddenValue("", -1), ""});
        MutableTreeTableNode node, node2;
        ProblemInformation info;
        ComponentInformation component;

        //for each problem, create a node for the problem, and then leafs for each
        //component in the problem.
        for (int i = 0; i < problems.size(); i++) {
            info = (ProblemInformation) problems.get(i);
            node = new MutableTreeTableNode(new Object[]{new HiddenValue(
                    info.getName(), info.getProblemId()),
                                                         info.getWriter().getHandle()});
            root.add(node);
            for (int j = 0; j < info.getProblemComponents().length; j++) {
                component = (ComponentInformation) info.getProblemComponents()[j];
                node2 = new MutableTreeTableNode(new Object[]{new HiddenValue(
                        component.getClassName(),
                        component.getComponentId()),
                                                              info.getWriter().getHandle()});
                node.add(node2);
            }
        }
        return root;
    }

    public void setController(ComponentController controller) {
        this.controller = (RoundProblemsPanelController) controller;
    }

    public void setModel(ComponentModel model) {
        this.model = (RoundProblemsPanelModel) model;
        model.addWatcher(this);
    }

    public String getName() {
        return "Round Problems";
    }

    public int getSelectedSingleProblemIndex() {
        return singleProblemsTable.getSelectedRow();
    }

    public Object[] getSelectedTeamProblemPath() {
        TreePath path = teamProblemsTable.getTree().getSelectionPath();
        return path == null ? null : path.getPath();
    }
    
    public int getSelectedLongProblemIndex() {
        return longProblemsTable.getSelectedRow();
    }
}
