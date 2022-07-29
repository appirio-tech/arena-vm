/*
 * Copyright (C) 2006-2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import com.topcoder.client.mpsqasApplet.common.MPSQASRendererFactory;
import com.topcoder.client.mpsqasApplet.common.OpenMaxSizeConstraint;
import com.topcoder.client.mpsqasApplet.common.OpenMinSizeConstraint;
import com.topcoder.client.mpsqasApplet.common.OpenValidValuesConstraint;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.controller.component.StatementPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.model.component.StatementPanelModel;
import com.topcoder.client.mpsqasApplet.model.defaultimpl.component.StatementPanelModelImpl;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.component.StatementPanelView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletFocusListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.widget.PanelTextField;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.UIHelper;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.UIHelper.ComboItem;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.netCommon.mpsqas.CustomBuildSetting;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.ComponentCategory;
import com.topcoder.shared.problem.Constraint;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.problem.UserConstraint;

/**
 * The StatementPanel is a panel through which a user can edit the
 * different parts to the problem statement.
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Added {@link #ROUND_TYPES} field.</li>
 * <li>Added {@link #roundTypeLabel} field.</li>
 * <li>Added {@link #roundTypeCombo} field.</li>
 * <li>Update {@link #update(Object arg)} method to add round type component.</li>
 * <li>Added {@link #sizeComponent(JComponent component, double percentWidth)} method.</li>
 * <li>Added {@link #ComboItem} class.</li>
 * <li>Added {@link #getRoundType()} method.</li>
 * </ol>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #STATEMENT_PARTS} field.</li>
 *      <li>Added {@link #settingsPanel} field.</li>
 *      <li>Added {@link #settingsLabel} field.</li>
 *      <li>Added {@link #gccBuildCommandLabel}} field.</li>
 *      <li>Added {@link #gccBuildCommandTextField}} field.</li>
 *      <li>Added {@link #cppApprovedPathLabel} field.</li>
 *      <li>Added {@link #cppApprovedPathTextField} field.</li>
 *      <li>Update {@link #update(Object arg)} method.</li>
 *      <li>Added {@link #makeSettingsPanel} method.</li>
 *      <li>Added {@link #getGccBuildCommand()} method.</li>
 *      <li>Added {@link #getCppApprovedPath()} method.</li>
 *      <li>Added {@link #pythonCommandLabel}} field.</li>
 *      <li>Added {@link #pythonCommandTextField}} field.</li>
 *      <li>Added {@link #pythonApprovedPathLabel}} field.</li>
 *      <li>Added {@link #pythonApprovedPathTextField} field.</li>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 *      <li>Added {@link #getPythonApprovedPath()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.3 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Updated class to use problem round type lookup values, obtained from back-end
 * (see {@link com.topcoder.netCommon.mpsqas.LookupValues}), instead of hard-coded values.
 * Specifically, hard-coded constant has been removed and {@link #update(Object)} method has been updated.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.4 (Release Assembly - TopCoder Competition Engine Improvement Series 2 v1.0):
 * <ol>
 * <li>
 * Added combo boxes for custom build settings. Specifically {@link #gccBuildCommandComboBox},
 * {@link #cppApprovedPathComboBox}, {@link #pythonApprovedPathComboBox} and {@link #pythonCommandComboBox}.
 * </li>
 * <li>
 * Removed text fields for above mentioned settings.
 * </li>
 * <li>
 * Updated related getters {@link #getGccBuildCommand()}, {@link #getCppApprovedPath()},
 * {@link #getPythonApprovedPath()} and {@link #getPythonCommand()} to deal with new combo boxes
 * instead of old text fields
 * </li>
 * <li>
 * Updated {@link #makeSettingsPanel()} to deal with new combo boxes instead of old text fields.
 * </li>
 * <li>
 * Updated {@link #getRoundType()} to work with string typed IDs of the combo box items.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Add {@link #executionTimeLimitLabel} field.</li>
 *      <li>Add {@link #executionTimeLimitTextField} field.</li>
 *      <li>Add {@link #memLimitTextField} field.</li>
 *      <li>Add {@link #memLimitLabel} field.</li>
 *      <li>Add {@link #getDefaultExecutionTimeLimit()} method.</li>
 *      <li>Update {@link #update(Object arg)} method.</li>
 *      <li>Add {@link #getExecutionTimeLimit()} method</li>
 *      <li>Add {@link #getMemLimit()} method</li>
 *      <li>Add {@link #getMemLimit(ComponentInformation ci)} method</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #getMemLimit(ComponentInformation ci)} method.</li>
 * </ol>
 * </p>
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class mutates not thread-safe UI, thus it's not thread-safe.
 * </p>
 * <p>
 *
 * <p>
 * Changes in version 1.7 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Added {@link #stackLimitTextField} field.</li>
 *      <li>Added {@link #stackLimitLabel} field.</li>
 *      <li>Updated {@link #update(Object arg)} method.</li>
 *      <li>Added {@link #getStackLimit()} method</li>
 *      <li>Added {@link #getStackLimit(ComponentInformation ci)} method</li>
 * </ol>
 * </p>
 *
 * @author mitalub, savon_cn, gevak, Selena
 * @version 1.7
 */
public class StatementPanelViewImpl extends StatementPanelView {

    /**
     * <p>
     * the drop down list captions.
     * </p>
     * @since 1.1
     */
    private String[] STATEMENT_PARTS =
            {"Definition", "Introduction", "Notes", "Constraints","Settings"};
    private String DEFINITION_HELP =
            "Enter the parameter types as a comma delimited list of parameter "
            + "type / value pairs. \n\n"
            + "For example:\n  int number, int[] values";
    private String INTRODUCTION_HELP =
            "Enter an introduction to the problem.  Simple XML tags are allowed "
            + " to format the introduction. \n"
            + "The supported tags are: \n"
            + " * <pre> .. </pre>\n"
            + " * <tt> .. </tt>\n"
            + " * <i> .. </i>\n"
            + " * <b> .. </b>\n"
            + " * <p> .. </p>\n"
            + " * <br> .. </br>\n"
            + " * <sub> .. </sub>\n"
            + " * <sup> .. </sup>\n"
            + " * <hX> .. </hX> (X is integer from 1 - 5)\n"
            + " * <hr></hr>\n"
            + " * <ul> .. <li> .. </li> .. </ul>\n"
            + " * <ol> .. <li> .. </li> .. </ol>\n"
            + " * <img src=\"...\"></img>\n";
    private String NOTES_HELP =
            "Enter notes for the problem as a carriage-return delimited list.  "
            + "Simple XML tags can be used to format the notes.\n"
            + "The supported tags are: \n"
            + " * <pre> .. </pre>\n"
            + " * <tt> .. </tt>\n"
            + " * <i> .. </i>\n"
            + " * <b> .. </b>\n"
            + " * <p> .. </p>\n"
            + " * <br> .. </br>\n"
            + " * <sub> .. </sub>\n"
            + " * <sup> .. </sup>\n"
            + " * <hX> .. </hX> (X is integer from 1 - 5)\n"
            + " * <hr></hr>\n"
            + " * <ul> .. <li> .. </li> .. </ul>\n"
            + " * <ol> .. <li> .. </li> .. </ol>\n"
            + " * <img src=\"...\"></img>\n";
    private String CONSTRAINTS_HELP =
            "Enter constraint for the problem as a carriage-return delimited "
            + "list.  Simple XML tags can be used to format the constraint.\n"
            + "The supported tags are: \n"
            + " * <pre> .. </pre>\n"
            + " * <tt> .. </tt>\n"
            + " * <i> .. </i>\n"
            + " * <b> .. </b>\n"
            + " * <p> .. </p>\n"
            + " * <br> .. </br>\n"
            + " * <sub> .. </sub>\n"
            + " * <sup> .. </sup>\n"
            + " * <hX> .. </hX> (X is integer from 1 - 5)\n"
            + " * <hr></hr>\n"
            + " * <ul> .. <li> .. </li> .. </ul>\n"
            + " * <ol> .. <li> .. </li> .. </ol>\n"
            + " * <img src=\"...\"></img>\n";

    private StatementPanelModel model;
    private StatementPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel partLabel;
    private JComboBox partComboBox;

    private JLabel definitionLabel;
    private JLabel introductionLabel;
    private JLabel notesLabel;
    private JLabel constraintsLabel;
    /**
     * add round type label.
     */
    private JLabel roundTypeLabel;
    /**
     * add round type selectable component.
     */
    private JComboBox roundTypeCombo;
    
    private JPanel currentPanel;
    private JPanel definitionPanel;
    private JPanel introductionPanel;
    private JPanel notesPanel;
    private JPanel constraintsPanel;

    /**
     * Represents the settings panel.
     * @since 1.1
     */
    private JPanel settingsPanel;
    
    private GridBagLayout definitionLayout;
    private GridBagLayout introductionLayout;
    private GridBagLayout notesLayout;
    private GridBagLayout constraintsLayout;
    /**
     * Represents the settings layout.
     * @since 1.1
     */
    private GridBagLayout settingsLayout;
    private JLabel classNameLabel;
    private JLabel methodNameLabel;
    private JLabel parametersLabel;
    private JLabel returnsLabel;
    /**
     * Represents the settings overview label.
     * @since 1.1
     */
    private JLabel settingsLabel;
    /**
     * Represents the GCC Build Command Label.
     * @since 1.1
     */
    private JLabel gccBuildCommandLabel;
    /**
     * Represents the cpp approved path Label.
     * @since 1.1
     */
    private JLabel cppApprovedPathLabel;
    
    /**
     * Represents the Python Command Label.
     * @since 1.1
     */
    private JLabel pythonCommandLabel;
    /**
     * Represents the python approved path Label.
     * @since 1.1
     */
    private JLabel pythonApprovedPathLabel;
    
    private PanelTextField classNameTextField;
    private PanelTextField methodNameTextField;
    private PanelTextField parametersTextField;
    private PanelTextField returnsTextField;

    /**
     * Represents the GCC build command combo box.
     * @since 1.4
     */
    private JComboBox gccBuildCommandComboBox;

    /**
     * Represents the CPP approved path combo box.
     * @since 1.4
     */
    private JComboBox cppApprovedPathComboBox;

    /**
     * Represents the Python build command combo box.
     * @since 1.4
     */
    private JComboBox pythonCommandComboBox;

    /**
     * Represents the Python approved path combo box.
     * @since 1.4
     */
    private JComboBox pythonApprovedPathComboBox;

    /**
     * Represents the execution time limit text label.
     * @since 1.5
     */
    private JLabel executionTimeLimitLabel;

    /**
     * Represents the execution time limit text field.
     * @since 1.5
     */
    private PanelTextField executionTimeLimitTextField;

    /**
     * Represents the execution memory limit label.
     * @since 1.5
     */
    private JLabel memLimitLabel;

    /**
     * Represents the execution memory limit.
     * @since 1.5
     */
    private PanelTextField memLimitTextField;

    /**
     * Represents the execution stack size limit label.
     * @since 1.7
     */
    private JLabel stackLimitLabel;

    /**
     * Represents the execution stack size limit.
     * @since 1.7
     */
    private PanelTextField stackLimitTextField;

    private JTextArea introductionTextArea;
    private JScrollPane introductionScrollPane;

    private JTextArea notesTextArea;
    private JScrollPane notesScrollPane;

    private JLabel descLabel;
    private JTextArea freeFormTextArea;
    private JScrollPane freeFormScrollPane;
    private ArrayList constraintsTextFields;
    private JCheckBox[] categories;

    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);
    }

    /** Saves this view's controller */
    public void setController(ComponentController controller) {
        this.controller = (StatementPanelController) controller;
    }

    /** Saves this view's model, and sets itself as an observer to the model. */
    public void setModel(ComponentModel model) {
        this.model = (StatementPanelModel) model;
        model.addWatcher(this);
    }
    /**
     * <p>get the default srm execution time limit.</p>
     * @return the execution time limit.
     */
    private String getDefaultExecutionTimeLimit() {
        //new component
        if (model.getComponentInformation().getComponentId() == -1) {
            return String.valueOf(ProblemComponent.DEFAULT_SRM_EXECUTION_TIME_LIMIT);
        } else {
            return String.valueOf(model.getComponentInformation().getProblemCustomSettings().getExecutionTimeLimit());
        }
    }
    /**
     * Build the settings panel.
     */
    private void makeSettingsPanel() {
        settingsLabel = new JLabel("Settings:");
        settingsLabel.setFont(DefaultUIValues.HEADER_FONT);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        settingsLayout.setConstraints(settingsLabel, gbc);
        settingsPanel.add(settingsLabel);

        gccBuildCommandLabel = new JLabel("GCC Build Command:");
        GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 1);
        settingsLayout.setConstraints(gccBuildCommandLabel, gbc);
        settingsPanel.add(gccBuildCommandLabel);

        gccBuildCommandComboBox = UIHelper.createCustomBuildSettingComboBox(
                CustomBuildSetting.SRM_GCC_BUILD_COMMAND,
                model.getComponentInformation().getProblemCustomSettings().getGccBuildCommand());
        gccBuildCommandComboBox.addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        JComponent sizedCombo = UIHelper.sizeComponent(gccBuildCommandComboBox, 1);
        GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 100, 0);
        settingsLayout.setConstraints(sizedCombo, gbc);
        settingsPanel.add(sizedCombo);

        cppApprovedPathLabel = new JLabel("CPP Approved Path:");
        GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
        settingsLayout.setConstraints(cppApprovedPathLabel, gbc);
        settingsPanel.add(cppApprovedPathLabel);

        cppApprovedPathComboBox = UIHelper.createCustomBuildSettingComboBox(
                CustomBuildSetting.SRM_CPP_APPROVED_PATH,
                model.getComponentInformation().getProblemCustomSettings().getCppApprovedPath());
        cppApprovedPathComboBox.addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        sizedCombo = UIHelper.sizeComponent(cppApprovedPathComboBox, 1);
        GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 1);
        settingsLayout.setConstraints(sizedCombo, gbc);
        settingsPanel.add(sizedCombo);

        pythonCommandLabel = new JLabel("Python Command:");
        GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 1, 1);
        settingsLayout.setConstraints(pythonCommandLabel, gbc);
        settingsPanel.add(pythonCommandLabel);

        pythonCommandComboBox = UIHelper.createCustomBuildSettingComboBox(
                CustomBuildSetting.SRM_PYTHON_COMMAND,
                model.getComponentInformation().getProblemCustomSettings().getPythonCommand());
        pythonCommandComboBox.addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        sizedCombo = UIHelper.sizeComponent(pythonCommandComboBox, 1);
        GUIConstants.buildConstraints(gbc, 1, 3, 1, 1,  100, 0);
        settingsLayout.setConstraints(sizedCombo, gbc);
        settingsPanel.add(sizedCombo);

        pythonApprovedPathLabel = new JLabel("Python Approved Path:");
        GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
        settingsLayout.setConstraints(pythonApprovedPathLabel, gbc);
        settingsPanel.add(pythonApprovedPathLabel);

        pythonApprovedPathComboBox = UIHelper.createCustomBuildSettingComboBox(
                CustomBuildSetting.SRM_PYTHON_APPROVED_PATH,
                model.getComponentInformation().getProblemCustomSettings().getPythonApprovedPath());
        pythonApprovedPathComboBox.addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        sizedCombo = UIHelper.sizeComponent(pythonApprovedPathComboBox, 1);
        GUIConstants.buildConstraints(gbc, 1, 4, 1, 1, 0, 1);
        settingsLayout.setConstraints(sizedCombo, gbc);
        settingsPanel.add(sizedCombo);

        //add empty label to make the component positioned at north
        JLabel emptyLabel = new JLabel("");
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 0, 5, 2, 1, 0, 100);
        settingsLayout.setConstraints(emptyLabel, gbc);
        settingsPanel.add(emptyLabel);
        
        settingsPanel.setBorder(new EtchedBorder());
    }

    /**
     * <p>get the memory limit.</p>
     * @param ci the problem component.
     * @return the memory limit.
     * @since 1.5
     */
    private int getMemLimit(ComponentInformation ci) {
        int compId = ci.getComponentId();
        int memLimitValue = ProblemComponent.DEFAULT_SRM_MEM_LIMIT;
        //if new problem, we must set mem limit to 256M
        if(compId != -1) {
            memLimitValue = model.getComponentInformation().getProblemCustomSettings().getMemLimit();
        }
        return memLimitValue;
    }

    /**
     * <p>Get the stack size limit.</p>
     * @param ci the problem component.
     * @return the memory limit.
     * @since 1.7
     */
    private int getStackLimit(ComponentInformation ci) {
        int compId = ci.getComponentId();
        int stackLimitValue = ProblemComponent.DEFAULT_SRM_STACK_LIMIT;
        //if new problem, we must set mem limit to default
        if (compId != -1) {
            stackLimitValue = model.getComponentInformation().getProblemCustomSettings().getStackLimit();
        }
        return stackLimitValue;
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     * Also, populates components with information in problemInfo.
     *
     * @param arg an instance of <code>UpdateTypes</code> indicating the update type.
     */
    public void update(Object arg) {
        if (arg == null) //make all
        {
            removeAll();

            definitionPanel = new JPanel();
            introductionPanel = new JPanel();
            notesPanel = new JPanel();
            constraintsPanel = new JPanel();

            definitionLayout = new GridBagLayout();
            introductionLayout = new GridBagLayout();
            notesLayout = new GridBagLayout();
            constraintsLayout = new GridBagLayout();

            definitionPanel.setLayout(definitionLayout);
            introductionPanel.setLayout(introductionLayout);
            notesPanel.setLayout(notesLayout);
            constraintsPanel.setLayout(constraintsLayout);

            partLabel = new JLabel("Select problem statement part to edit: ");
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

            definitionLabel = new JLabel("Definition:");
            definitionLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            definitionLayout.setConstraints(definitionLabel, gbc);
            definitionPanel.add(definitionLabel);

            classNameLabel = new JLabel("Class Name:");
            gbc.fill = GridBagConstraints.HORIZONTAL;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 1);
            definitionLayout.setConstraints(classNameLabel, gbc);
            definitionPanel.add(classNameLabel);

            classNameTextField = new PanelTextField(.3,
                    model.getComponentInformation().getClassName());
            classNameTextField.getJTextField()
                    .addFocusListener(new AppletFocusListener(
                            "processStatementChange", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 100, 0);
            definitionLayout.setConstraints(classNameTextField, gbc);
            definitionPanel.add(classNameTextField);

            methodNameLabel = new JLabel("Method Name:");
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
            definitionLayout.setConstraints(methodNameLabel, gbc);
            definitionPanel.add(methodNameLabel);

            methodNameTextField = new PanelTextField(.3,
                    model.getComponentInformation().getMethodName());
            methodNameTextField.getJTextField()
                    .addFocusListener(new AppletFocusListener(
                            "processStatementChange", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 1);
            definitionLayout.setConstraints(methodNameTextField, gbc);
            definitionPanel.add(methodNameTextField);

            parametersLabel = new JLabel("Parameters:");
            GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 1);
            definitionLayout.setConstraints(parametersLabel, gbc);
            definitionPanel.add(parametersLabel);

            parametersTextField = new PanelTextField(.8, getParamString());
            parametersTextField.getJTextField()
                    .addFocusListener(new AppletFocusListener(
                            "processParamTypeChange", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 0, 1);
            definitionLayout.setConstraints(parametersTextField, gbc);
            definitionPanel.add(parametersTextField);

            returnsLabel = new JLabel("Returns:");
            GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
            definitionLayout.setConstraints(returnsLabel, gbc);
            definitionPanel.add(returnsLabel);

            returnsTextField = new PanelTextField(.2,
                    model.getComponentInformation().getReturnType().getDescription());
            returnsTextField.getJTextField().addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 4, 1, 1, 0, 1);
            definitionLayout.setConstraints(returnsTextField, gbc);
            definitionPanel.add(returnsTextField);

            executionTimeLimitLabel = new JLabel("Time limit (ms)");
            GUIConstants.buildConstraints(gbc, 0, 5, 1, 1, 0, 1);
            definitionLayout.setConstraints(executionTimeLimitLabel, gbc);
            definitionPanel.add(executionTimeLimitLabel);
            
            executionTimeLimitTextField = new PanelTextField(.2,
                    getDefaultExecutionTimeLimit());
            executionTimeLimitTextField.getJTextField().addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 5, 1, 1, 0, 1);
            definitionLayout.setConstraints(executionTimeLimitTextField, gbc);
            definitionPanel.add(executionTimeLimitTextField);

            memLimitLabel = new JLabel("Memory limit (MB)");
            GUIConstants.buildConstraints(gbc, 0, 6, 1, 1, 0, 1);
            definitionLayout.setConstraints(memLimitLabel, gbc);
            definitionPanel.add(memLimitLabel);

            memLimitTextField = new PanelTextField(.2,
                    String.valueOf(getMemLimit(model.getComponentInformation())));
            memLimitTextField.getJTextField().addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 6, 1, 1, 0, 1);
            definitionLayout.setConstraints(memLimitTextField, gbc);
            definitionPanel.add(memLimitTextField);
            
            stackLimitLabel = new JLabel("Stack size limit (MB)");
            GUIConstants.buildConstraints(gbc, 0, 7, 1, 1, 0, 1);
            definitionLayout.setConstraints(stackLimitLabel, gbc);
            definitionPanel.add(stackLimitLabel);

            int stackLimit = getStackLimit(model.getComponentInformation());
            stackLimitTextField = new PanelTextField(.2, (stackLimit > 0) ? String.valueOf(stackLimit) : "");
            stackLimitTextField.getJTextField().addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 7, 1, 1, 0, 1);
            definitionLayout.setConstraints(stackLimitTextField, gbc);
            definitionPanel.add(stackLimitTextField);

            ArrayList cat = model.getComponentInformation().getCategories();

            JScrollPane jsp = new JScrollPane();
            JPanel p = new JPanel(new GridLayout(0,1));
            categories = new JCheckBox[cat.size()];
            for(int i = 0; i<cat.size(); i++){
                ComponentCategory cc = (ComponentCategory)cat.get(i);
                categories[i] = new JCheckBox(cc.getName());
                categories[i].addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
                categories[i].setSelected(cc.getChecked());
                p.add(categories[i]);
                
            }
            
            jsp.getViewport().add(p);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 1, 8, 1, 1, 0, 100);
            definitionLayout.setConstraints(jsp , gbc);
            definitionPanel.add(jsp);
            
            //add round type selectable component
            roundTypeLabel = new JLabel("Round Type:");
            GUIConstants.buildConstraints(gbc, 0, 9, 1, 1, 0, 1);
            definitionLayout.setConstraints(roundTypeLabel, gbc);
            definitionPanel.add(roundTypeLabel);

            roundTypeCombo = UIHelper.createProblemRoundTypeComboBox(
                    ApplicationConstants.SINGLE_PROBLEM, model.getComponentInformation().getRoundType());
            roundTypeCombo.addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
            JComponent sizedCombo = UIHelper.sizeComponent(roundTypeCombo, .5);
           
            GUIConstants.buildConstraints(gbc, 1, 9, 1, 1, 0, 1);
            definitionLayout.setConstraints(sizedCombo, gbc);
            definitionPanel.add(sizedCombo);
            
            definitionPanel.setBorder(new EtchedBorder());

            introductionLabel = new JLabel("Introduction:");
            introductionLabel.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            introductionLayout.setConstraints(introductionLabel, gbc);
            introductionPanel.add(introductionLabel);

            introductionTextArea = new JTextArea(getTextRepresentation(model.getComponentInformation().getIntro()));
            introductionTextArea.setLineWrap(true);
            introductionTextArea.setWrapStyleWord(true);
            introductionTextArea.addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
            introductionScrollPane = new JScrollPane(
                    introductionTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 100);
            introductionLayout.setConstraints(introductionScrollPane, gbc);
            introductionPanel.add(introductionScrollPane);

            JPanel introductionHelpPanel = getHelpPanel(INTRODUCTION_HELP);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 30);
            introductionLayout.setConstraints(introductionHelpPanel, gbc);
            introductionPanel.add(introductionHelpPanel);

            introductionPanel.setBorder(new EtchedBorder());

            notesLabel = new JLabel("Notes:");
            notesLabel.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            notesLayout.setConstraints(notesLabel, gbc);
            notesPanel.add(notesLabel);

            notesTextArea = new JTextArea(getNotesString());
            notesTextArea.setLineWrap(true);
            notesTextArea.setWrapStyleWord(true);
            notesTextArea.addFocusListener(new AppletFocusListener(
                    "processStatementChange", controller, "focusLost", false));
            notesScrollPane = new JScrollPane(
                    notesTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 100);
            notesLayout.setConstraints(notesScrollPane, gbc);
            notesPanel.add(notesScrollPane);

            JPanel notesHelpPanel = getHelpPanel(NOTES_HELP);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 30);
            notesLayout.setConstraints(notesHelpPanel, gbc);
            notesPanel.add(notesHelpPanel);

            notesPanel.setBorder(new EtchedBorder());

            makeConstraintsPanel();

            settingsPanel = new JPanel();
            settingsLayout = new GridBagLayout();
            settingsPanel.setLayout(settingsLayout);
            makeSettingsPanel();
            
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 100);
            layout.setConstraints(definitionPanel, gbc);
            add(definitionPanel);
            currentPanel = definitionPanel;

            partComboBox.addActionListener(new AppletActionListener(
                    "processPartSelection", controller, false));
        } else if (arg.equals(UpdateTypes.PARAM_TYPES)) //remake constraints
        {
            makeConstraintsPanel();
            parametersTextField.setText(getParamString());

            if (model.getCurrentPart() == StatementPanelModelImpl.CONSTRAINTS) {
                repaint();
            }

        } else if (arg.equals(UpdateTypes.STATEMENT_PART_CHANGE)) {
            remove(currentPanel);

            //update the current panel
            switch (model.getCurrentPart()) {
            case StatementPanelModelImpl.DEFINITION:
                currentPanel = definitionPanel;
                break;
            case StatementPanelModelImpl.INTRODUCTION:
                currentPanel = introductionPanel;
                break;
            case StatementPanelModelImpl.NOTES:
                currentPanel = notesPanel;
                break;
            case StatementPanelModelImpl.CONSTRAINTS:
                currentPanel = constraintsPanel;
                break;
            case StatementPanelModelImpl.SETTINGS:
                currentPanel = settingsPanel;
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

    /**
     * Builds a constraints table based on the constraints in the model.
     * Assumes every OpenMinSizeConstraint is followed by a OpenMaxSizeConstraint.
     *
     * For every numeric value, enter its minimum and maximum value
     * For every character value, enter the valid values
     * For every array (interpret a String as an array of char):
     *  - Enter its minimum and maximum length
     *  - Specify constraints on values each element of the array may have
     *    (this means that for a 2d array or a String[], you'd have the same
     *     sort of array constraints to specify)
     * Or "free-form" user-generated constraints
     */
    private void makeConstraintsPanel() {
        constraintsPanel.removeAll();

        constraintsLabel = new JLabel("Constraints:");
        constraintsLabel.setFont(DefaultUIValues.HEADER_FONT);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        constraintsLayout.setConstraints(constraintsLabel, gbc);
        constraintsPanel.add(constraintsLabel);

        if (model.getSpecifiedConstraints() == null) {
            controller.buildSpecifiedConstraints();
        }
        Constraint[] constraints = model.getSpecifiedConstraints();

        PanelTextField inputTextField;
        constraintsTextFields = new ArrayList();

        int y = 1, count, maxWidth = 2;

        //find out if there are any min size / max size constraints
        //If there are, the max width will be wider.
        for (int i = 0; maxWidth == 2 && i < constraints.length; i++) {
            if (constraints[i] instanceof OpenMinSizeConstraint) maxWidth = 4;
        }

        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] instanceof OpenMinSizeConstraint) {
                //set up min / max length pair
                descLabel = new JLabel(((OpenMinSizeConstraint) constraints[i])
                        .getPromptString());
                GUIConstants.buildConstraints(gbc, 0, y, 1, 1, 1, 1);
                constraintsLayout.setConstraints(descLabel, gbc);
                constraintsPanel.add(descLabel);

                inputTextField = new PanelTextField(.5,
                        ((OpenMinSizeConstraint) constraints[i]).getInputString());
                inputTextField.getJTextField().addFocusListener(new AppletFocusListener(
                        "processStatementChange", controller, "focusLost", false));
                GUIConstants.buildConstraints(gbc, 1, y, 1, 1, 100, 0);
                constraintsLayout.setConstraints(inputTextField, gbc);
                constraintsPanel.add(inputTextField);
                constraintsTextFields.add(inputTextField);

                i++;

                //assume minsize is followed by max size
                descLabel = new JLabel(((OpenMaxSizeConstraint) constraints[i])
                        .getPromptString());
                GUIConstants.buildConstraints(gbc, 2, y, 1, 1, 1, 0);
                constraintsLayout.setConstraints(descLabel, gbc);
                constraintsPanel.add(descLabel);

                inputTextField = new PanelTextField(.5,
                        ((OpenMaxSizeConstraint) constraints[i]).getInputString());
                inputTextField.getJTextField().addFocusListener(new AppletFocusListener(
                        "processStatementChange", controller, "focusLost", false));
                GUIConstants.buildConstraints(gbc, 3, y, 1, 1, 100, 0);
                constraintsLayout.setConstraints(inputTextField, gbc);
                constraintsPanel.add(inputTextField);
                constraintsTextFields.add(inputTextField);

                y++;
            } else if (constraints[i] instanceof OpenValidValuesConstraint) {
                //set up valid value constraints field
                descLabel = new JLabel(((OpenValidValuesConstraint) constraints[i])
                        .getPromptString());
                GUIConstants.buildConstraints(gbc, 0, y, 1, 1, 1, 1);
                constraintsLayout.setConstraints(descLabel, gbc);
                constraintsPanel.add(descLabel);

                inputTextField = new PanelTextField(.6,
                        ((OpenValidValuesConstraint) constraints[i]).getInputString());
                inputTextField.getJTextField().addFocusListener(new AppletFocusListener(
                        "processStatementChange", controller, "focusLost", false));
                if (maxWidth == 4) {
                    GUIConstants.buildConstraints(gbc, 1, y, 3, 1, 0, 0);
                } else {
                    GUIConstants.buildConstraints(gbc, 1, y, 1, 1, 100, 0);
                }
                constraintsLayout.setConstraints(inputTextField, gbc);
                constraintsPanel.add(inputTextField);
                constraintsTextFields.add(inputTextField);

                y++;
            }
        }

        descLabel = new JLabel("Free form:");
        GUIConstants.buildConstraints(gbc, 0, y, 1, 1, 0, 1);
        constraintsLayout.setConstraints(descLabel, gbc);
        constraintsPanel.add(descLabel);

        freeFormTextArea = new JTextArea(getConstraintsString());
        freeFormTextArea.addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        freeFormTextArea.setLineWrap(true);
        freeFormTextArea.setWrapStyleWord(true);
        freeFormScrollPane = new JScrollPane(freeFormTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 0, y + 1, maxWidth, 1, 0, 100);
        constraintsLayout.setConstraints(freeFormScrollPane, gbc);
        constraintsPanel.add(freeFormScrollPane);

        JPanel constraintsHelpPanel = getHelpPanel(CONSTRAINTS_HELP);
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 0, y + 2, maxWidth, 1, 0, 30);
        constraintsLayout.setConstraints(constraintsHelpPanel, gbc);
        constraintsPanel.add(constraintsHelpPanel);

        constraintsPanel.setBorder(new EtchedBorder());
    }

    /**
     * Returns a JPanel containing a help box containing helpText.
     *
     * @param helpText Text to display.
     */
    static JPanel getHelpPanel(String helpText) {
        JPanel helpPanel = new JPanel();
        GridBagLayout helpPanelLayout = new GridBagLayout();
        helpPanel.setLayout(helpPanelLayout);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel spacer1 = new JPanel();
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        helpPanelLayout.setConstraints(spacer1, gbc);
        helpPanel.add(spacer1);

        JTextArea helpTextArea = new JTextArea(helpText);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setEditable(false);
        JScrollPane helpScrollPane = new JScrollPane(helpTextArea);
        helpScrollPane.setPreferredSize(new Dimension(100, 100));
        GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
        helpPanelLayout.setConstraints(helpScrollPane, gbc);
        helpPanel.add(helpScrollPane);

        JPanel spacer2 = new JPanel();
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 2, 0, 1, 1, 1, 1);
        helpPanelLayout.setConstraints(spacer2, gbc);
        helpPanel.add(spacer2);

        return helpPanel;
    }

    /**
     * Returns a String representation of the notes in the model.
     */
    private String getNotesString() {
        StringBuffer sb = new StringBuffer(200);
        Element[] notes = model.getComponentInformation().getNotes();
        for (int i = 0; i < notes.length; i++) {
            sb.append(getTextRepresentation(notes[i]));
            if (i < notes.length - 1) {
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    /**
     * Returns a String representation of the free form constraints in the model.
     */
    private String getConstraintsString() {
        StringBuffer sb = new StringBuffer(200);
        Element[] constraints = model.getComponentInformation().getConstraints();
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] instanceof UserConstraint) {
                sb.append(getTextRepresentation(constraints[i]));
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    /**
     * Returns a String representation of the parameters in the model.
     */
    private String getParamString() {
        StringBuffer params = new StringBuffer(50);
        DataType[] types = model.getComponentInformation().getParamTypes();
        String[] names = model.getComponentInformation().getParamNames();

        for (int i = 0; i < types.length; i++) {
            params.append(types[i].getDescription());
            params.append(" ");
            params.append(names[i]);
            if (i < types.length - 1) {
                params.append(", ");
            }
        }
        return params.toString();
    }

    /** Returns a name for this view. */
    public String getName() {
        return "Statement Editor";
    }

    public String getClassName() {
        return classNameTextField.getText();
    }

    public String getMethodName() {
        return methodNameTextField.getText();
    }

    public String getReturnType() {
        return returnsTextField.getText();
    }

    public String getIntroduction() {
        return introductionTextArea.getText();
    }

    public String getNotes() {
        return notesTextArea.getText();
    }

    public String[] getSpecifiedConstraints() {
        String[] constraints = new String[constraintsTextFields.size()];
        for (int i = 0; i < constraints.length; i++) {
            constraints[i] = ((PanelTextField) constraintsTextFields.get(i)).getText();
        }
        return constraints;
    }

    public String getFreeFormConstraints() {
        return freeFormTextArea.getText();
    }

    /** Returns the String of text entered by the user for the parameters. */
    public String getParameters() {
        return parametersTextField.getText();
    }

    /** Returns the index of the selected problem part in the combo box. */
    public int getSelectedPart() {
        return partComboBox.getSelectedIndex();
    }

    /**
     * Sets the value in the <code>index</code>th constraint text field to be
     * <code>text</code>.
     */
    public void setConstraintText(String text, int index) {
        ((PanelTextField) constraintsTextFields.get(index)).setText(text);
    }

    public boolean isCategoryChecked(int idx){
        return categories[idx].isSelected();
    }

    private String getTextRepresentation(Element e) {
        try {
            return MPSQASRendererFactory.getInstance().getRenderer(e).toHTML(JavaLanguage.JAVA_LANGUAGE);
        } catch (Exception e1) {
            return "error trying to render element" + e.toXML();
        }
    }

    /**
     * <p>
     * get the round type of problem.
     * </p>
     * @return the round type.
     */
    public int getRoundType() {
        ComboItem item = (ComboItem) roundTypeCombo.getSelectedItem();
        if (item != null) {
            try {
                return Integer.parseInt(item.getId());
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Get the GCC build command.
     * @return the GCC build command.
     * @since 1.1
     */
    public String getGccBuildCommand() {
        return ((ComboItem) gccBuildCommandComboBox.getSelectedItem()).getId();
    }

    /**
     * Get the CPP approved path.
     * @return The CPP approved path.
     * @since 1.1
     */
    public String getCppApprovedPath() {
        return ((ComboItem) cppApprovedPathComboBox.getSelectedItem()).getId();
    }

    /**
     * Get the Python build command.
     * @return The Python build command.
     * @since 1.1
     */
    public String getPythonCommand() {
        return ((ComboItem) pythonCommandComboBox.getSelectedItem()).getId();
    }

    /**
     * Get the Python approved path.
     * @return The Python approved path.
     * @since 1.1
     */
    public String getPythonApprovedPath() {
        return ((ComboItem) pythonApprovedPathComboBox.getSelectedItem()).getId();
    }
    /**
     * Gets the execution time limit.
     * 
     * @return the execution time limit.
     * @since 1.5
     */
    public int getExecutionTimeLimit() {
        int executionTimeLimit = 0;
        boolean executionTimeLimitValid = true;
        try {
            executionTimeLimit = Integer.parseInt(executionTimeLimitTextField.getText());
            if (executionTimeLimit <= 0) {
                executionTimeLimitValid = false;
            }
        } catch (NumberFormatException nfe) {
            executionTimeLimitValid = false;
        }
        if (!executionTimeLimitValid) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Execution time limit must be a positive integer.", true);
            executionTimeLimitTextField.setText(getDefaultExecutionTimeLimit());
        }
        return executionTimeLimit;
    }

    /**
     * Gets the execution memory limit.
     * @return the execution memory limit.
     * @since 1.5
     */
    public int getMemLimit() {
        int memLimit = 0;
        boolean memLimitValid = true;
        try {
            memLimit = Integer.parseInt(memLimitTextField.getText());
            if (memLimit <= 0) memLimitValid = false;
        } catch (NumberFormatException nfe) {
            memLimitValid = false;
        }
        if (!memLimitValid) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Memory limit must be a positive integer.", true);
            memLimit = getMemLimit(model.getComponentInformation());
            memLimitTextField.setText(String.valueOf(memLimit));
        }
        return memLimit;
    }

    /**
     * Gets the stack size limit in megabytes.
     * @return the stack size limit in megabytes.
     * @since 1.7
     */
    public int getStackLimit() {
        int stackLimit = ProblemComponent.DEFAULT_SRM_STACK_LIMIT;
        boolean stackLimitValid = true;
        if (stackLimitTextField.getText().trim().length() > 0)
        {
            try {
                stackLimit = Integer.parseInt(stackLimitTextField.getText());
                if (stackLimit <= 0 || stackLimit > ProblemComponent.MAX_STACK_LIMIT) {
                    stackLimitValid = false;
                }
            } catch (NumberFormatException nfe) {
                stackLimitValid = false;
            }
        }
        if (!stackLimitValid) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Stack size limit must be an integer in [1, "
                    + ProblemComponent.MAX_STACK_LIMIT + "] range or empty.", true);
            stackLimit = getStackLimit(model.getComponentInformation());
            stackLimitTextField.setText((stackLimit > 0) ? String.valueOf(stackLimit) : "");
        }
        return stackLimit;
    }
}
