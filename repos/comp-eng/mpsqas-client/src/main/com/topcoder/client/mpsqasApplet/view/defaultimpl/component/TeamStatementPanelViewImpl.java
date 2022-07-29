package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.common.OpenMaxSizeConstraint;
import com.topcoder.client.mpsqasApplet.common.OpenMinSizeConstraint;
import com.topcoder.client.mpsqasApplet.common.OpenValidValuesConstraint;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.widget.PanelTextField;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.component.TeamStatementPanelView;
import com.topcoder.client.mpsqasApplet.model.component.TeamStatementPanelModel;
import com.topcoder.client.mpsqasApplet.controller.component.TeamStatementPanelController;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.controller.defaultimpl.component.TeamStatementPanelControllerImpl;
import com.topcoder.client.mpsqasApplet.model.defaultimpl.component.TeamStatementPanelModelImpl;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 * The TeamStatementPanel is a panel through which a user can edit the
 * different parts to the problem statement.
 *
 * @author mitalub
 */
public class TeamStatementPanelViewImpl extends TeamStatementPanelView {

    private String[] STATEMENT_PARTS =
            {"Definition", "Introduction"};
    private String INTRODUCTION_HELP =
            "Enter an introduction to the problem.  Simple XML tags are allowed "
            + " to format the introduction. \n"
            + "The supported tags are: \n"
            + " * <b> .. </b>\n"
            + " * <i> .. </i>\n"
            + " * etc\n";

    private TeamStatementPanelModel model;
    private TeamStatementPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JComboBox partComboBox;
    private JPanel currentPanel;
    private JPanel definitionPanel;
    private JPanel introductionPanel;

    private GridBagLayout definitionLayout;
    private GridBagLayout introductionLayout;

    private PanelTextField problemNameField;
    private JTextArea introductionTextArea;

    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);
    }

    /** Saves this view's controller */
    public void setController(ComponentController controller) {
        this.controller = (TeamStatementPanelController) controller;
    }

    /** Saves this view's model, and sets itself as an observer to the model. */
    public void setModel(ComponentModel model) {
        this.model = (TeamStatementPanelModel) model;
        model.addWatcher(this);
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     * Also, populates components with information in problemInfo.
     */
    public void update(Object arg) {
        if (arg == null) //make all
        {
            removeAll();

            definitionPanel = new JPanel(definitionLayout = new GridBagLayout());
            introductionPanel = new JPanel(introductionLayout = new GridBagLayout());

            JLabel partLabel = new JLabel("Select problem statement part to edit: ");
            partLabel.setFont(DefaultUIValues.BOLD_FONT);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);
            layout.setConstraints(partLabel, gbc);
            add(partLabel);

            partComboBox = new JComboBox();
            for (int i = 0; i < STATEMENT_PARTS.length; i++) {
                partComboBox.addItem(STATEMENT_PARTS[i]);
            }
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 100, 0);
            layout.setConstraints(partComboBox, gbc);
            add(partComboBox);

            JLabel definitionLabel = new JLabel("Definition");
            definitionLabel.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            GUIConstants.buildConstraints(gbc, 0, 0, 2, 1, 0, 1);
            definitionLayout.setConstraints(definitionLabel, gbc);
            definitionPanel.add(definitionLabel);

            JLabel problemNameLabel = new JLabel("Problem Name: ");
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 1);
            definitionLayout.setConstraints(problemNameLabel, gbc);
            definitionPanel.add(problemNameLabel);

            problemNameField = new PanelTextField(0.5, model.getProblemInformation()
                    .getName());
            problemNameField.getJTextField()
                    .addFocusListener(new AppletFocusListener(
                            "processStatementChange", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 100, 1);
            definitionLayout.setConstraints(problemNameField, gbc);
            definitionPanel.add(problemNameField);

            JLabel spacer = new JLabel();
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 2, 2, 1, 0, 100);
            definitionLayout.setConstraints(spacer, gbc);
            definitionPanel.add(spacer);

            definitionPanel.setBorder(new EtchedBorder());

            JLabel introductionLabel = new JLabel("Introduction:");
            introductionLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            introductionLayout.setConstraints(introductionLabel, gbc);
            introductionPanel.add(introductionLabel);

            introductionTextArea = new JTextArea(model.getProblemInformation()
                    .getProblemText());
            introductionTextArea.addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
            introductionTextArea.setLineWrap(true);
            introductionTextArea.setWrapStyleWord(true);
            JScrollPane introductionScrollPane = new JScrollPane(introductionTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 100);
            introductionLayout.setConstraints(introductionScrollPane, gbc);
            introductionPanel.add(introductionScrollPane);

            JPanel introductionHelpPanel = StatementPanelViewImpl.getHelpPanel(
                    INTRODUCTION_HELP);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 30);
            introductionLayout.setConstraints(introductionHelpPanel, gbc);
            introductionPanel.add(introductionHelpPanel);

            introductionPanel.setBorder(new EtchedBorder());

            currentPanel = definitionPanel;
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 100);
            layout.setConstraints(currentPanel, gbc);
            add(currentPanel);

            partComboBox.addActionListener(new AppletActionListener(
                    "processPartSelection", controller, false));
        } else if (arg.equals(UpdateTypes.STATEMENT_PART_CHANGE)) {
            remove(currentPanel);

            //update the current panel
            switch (model.getCurrentPart()) {
            case TeamStatementPanelModelImpl.DEFINITION:
                currentPanel = definitionPanel;
                break;
            case TeamStatementPanelModelImpl.INTRODUCTION:
                currentPanel = introductionPanel;
                break;
            }
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 100);
            layout.setConstraints(currentPanel, gbc);
            add(currentPanel);

            invalidate();
            validate();
            repaint();
        }
    }

    /** Returns a name for this view. */
    public String getName() {
        return "Statement Editor";
    }

    public int getSelectedPart() {
        return partComboBox.getSelectedIndex();
    }

    public String getProblemName() {
        return problemNameField.getText();
    }

    public String getIntroduction() {
        return introductionTextArea.getText();
    }
}
