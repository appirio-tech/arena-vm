package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.topcoder.netCommon.mpsqas.ProblemInformation;
import com.topcoder.netCommon.mpsqas.SolutionInformation;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.component.AllSolutionPanelView;
import com.topcoder.client.mpsqasApplet.model.component.AllSolutionPanelModel;
import com.topcoder.client.mpsqasApplet.controller.component.AllSolutionPanelController;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;

/**
 * Allows the user to view all the solutions (Writer's and tester's) to a
 * problem, and test all of them with the same arguments.
 *
 * @author mitalub
 */
public class AllSolutionPanelViewImpl extends AllSolutionPanelView {

    private final static String[] ALL_SOLUTIONS_COLS =
            {"Coder", "Primary", "Language"};

    private final static int[] ALL_SOLUTIONS_COLS_WIDTHS = {80, 80, 100};

    private AllSolutionPanelModel model;
    private AllSolutionPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JLabel solutionsTitle;
    private SortableTable solutionsTable;
    private JScrollPane solutionsTableScrollPane;
    private JTextArea solutionText;
    private JScrollPane solutionScrollPane;
    private JButton testButton;
    private JButton compareButton;
    private Box buttonBox;

    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);
    }

    public void setController(ComponentController controller) {
        this.controller = (AllSolutionPanelController) controller;
    }

    public void setModel(ComponentModel model) {
        this.model = (AllSolutionPanelModel) model;
        model.addWatcher(this);
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     * Also, populates components with information in problemInfo.
     */
    public void update(Object arg) {
        if (arg == null) {
            removeAll();
            ArrayList solutions = model.getSolutions();
            JPanel panel1 = new JPanel();
            JPanel panel2 = new JPanel();
            GridBagLayout layout1 = new GridBagLayout();
            GridBagLayout layout2 = new GridBagLayout();
            panel1.setLayout(layout1);
            panel2.setLayout(layout2);

            solutionsTitle = new JLabel("Solutions:");
            solutionsTitle.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout1.setConstraints(solutionsTitle, gbc);
            panel1.add(solutionsTitle);

            Object[][] tableData = new Object[solutions.size()][3];
            int i;
            for (i = 0; i < solutions.size(); i++) {
                tableData[i][0] = ((SolutionInformation) solutions.get(i)).getHandle();
                tableData[i][1] = new Boolean(
                        ((SolutionInformation) solutions.get(i)).isPrimary());
                tableData[i][2] =
                        ((SolutionInformation) solutions.get(i)).getLanguage().getName();
            }

            solutionsTable = new SortableTable(ALL_SOLUTIONS_COLS,
                    tableData,
                    ALL_SOLUTIONS_COLS_WIDTHS);
            solutionsTableScrollPane = new JScrollPane(solutionsTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
            layout1.setConstraints(solutionsTableScrollPane, gbc);
            panel1.add(solutionsTableScrollPane);

            solutionText = new JTextArea();
            solutionText.setEditable(false);
            solutionScrollPane = new JScrollPane(solutionText,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 100);
            layout2.setConstraints(solutionScrollPane, gbc);
            panel2.add(solutionScrollPane);

            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 40, 100);
            layout.setConstraints(panel1, gbc);
            add(panel1);

            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 100, 0);
            layout.setConstraints(panel2, gbc);
            add(panel2);

            buttonBox = Box.createHorizontalBox();
            testButton = new JButton("Test All");
            compareButton = new JButton("System Test All");

            buttonBox.add(testButton);
            buttonBox.add(Box.createHorizontalStrut(5));
            buttonBox.add(compareButton);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 1);
            layout.setConstraints(buttonBox, gbc);
            add(buttonBox);

            testButton.addActionListener(new AppletActionListener("processTestAll",
                    controller, false));
            compareButton.addActionListener(new AppletActionListener(
                    "processSystemTestAll", controller, false));
            solutionsTable.getSelectionModel().addListSelectionListener(
                    new AppletListListener("processSolutionSelected", controller, false));
        }
    }

    public String getName() {
        return "All Solutions";
    }

    /**
     * Returns the index of the selected solution.
     */
    public int getSelectedSolutionIndex() {
        return solutionsTable.getSelectedRow();
    }

    /**
     * Sets the preview text for the solution.
     */
    public void setPreviewText(String preview) {
        solutionText.setText(preview);
        solutionText.setCaretPosition(0);
    }
}
