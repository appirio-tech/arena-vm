/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JButton;
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
import com.topcoder.client.mpsqasApplet.controller.component.LongStatementPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.model.component.LongStatementPanelModel;
import com.topcoder.client.mpsqasApplet.model.defaultimpl.component.LongStatementPanelModelImpl;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.component.LongStatementPanelView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.UIHelper;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.UIHelper.ComboItem;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletFocusListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletListListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.SortableTable;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.widget.PanelTextField;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.netCommon.mpsqas.CustomBuildSetting;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.ComponentCategory;
import com.topcoder.shared.problem.Constraint;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.UserConstraint;

/**
 * The LongStatementPanel is a panel through which a user can edit the
 * different parts of a long problem statement.
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 *  Version 1.1(TC Competition Engine Code Execution Time Issue) change notes:
 *  <ol>
 *      <li>Added {@link #getExecutionTimeLimit()} to get the execution time limit.</li>
 *      <li>Updated {@link #makeDefinitionPanel()} to support the execution time limit editing.</li>
 *  </ol>
 * </p>
 * 
 * <p>
 *  Version 1.2(TC Competition Engine - Code Compilation Issues) change notes:
 *  <ol>
 *      <li>Added {@link #getExecutionTimeLimit()} to get the execution time limit.</li>
 *      <li>Updated {@link #makeDefinitionPanel()} to support the execution time limit editing.</li>
 *  </ol>
 * </p>
 *
 * <p>
 *  Version 1.3 (Release Assembly - Round Type Option Support For SRM Problem version 1.0) change notes:
 *  <ol>
 *      <li>Refactor to use the helper classes and helper methods in {@link UIHelper}.</li>
 *  </ol>
 * </p>
 *
 * <p>
 * Version 1.4 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
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
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.5 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #pythonCommandLabel}} field.</li>
 *      <li>Added {@link #pythonCommandTextField}} field.</li>
 *      <li>Added {@link #pythonApprovedPathLabel}} field.</li>
 *      <li>Added {@link #pythonApprovedPathTextField} field.</li>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 *      <li>Added {@link #getPythonApprovedPath()} method.</li>
 *      <li>Update {@link #makeSettingsPanel} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.6 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
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
 * <strong>Thread Safety: </strong><br/>
 * This class mutates not thread-safe UI, thus it's not thread-safe.
 * </p>
 * <p>
 *
 * <p>
 * Changes in 1.7 (Release Assembly - TopCoder Competition Engine Improvement Series 1):
 * <ol>
 * <li>Added {@link #submissionRateLabel} UI element.</li>
 * <li>Added {@link #submissionRateTextField} UI element.</li>
 * <li>Added {@link #exampleSubmissionRateLabel} UI element.</li>
 * <li>Added {@link #exampleSubmissionRateTextField} UI element.</li>
 * <li>Updated {@link #makeSettingsPanel()} method
 * to populate the submission rate setting UI elements (see above).</li>
 * <li>Added {@link #getSubmissionRate()} method.</li>
 * <li>Added {@link #getExampleSubmissionRate()} method.</li>
 * <li>Added {@link #getSubmissionRateValue(PanelTextField, String)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in 1.8 (Release Assembly - TopCoder Competition Engine Improvement Series 2 v1.0):
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
 * Changes in version 1.9 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #makeDefinitionPanel()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.0 (TopCoder Competition Engine - Stack Size Configuration For MM Problems v1.0):
 * <ol>
 *      <li>Added {@link #stackLimitTextField} field.</li>
 *      <li>Added {@link #stackLimitLabel} field.</li>
 *      <li>Updated {@link #makeDefinitionPanel()} method.</li>
 *      <li>Added {@link #getStackLimit(ComponentInformation ci)} method</li>
 *      <li>Added {@link #getStackLimit()} method</li>
 * </ol>
 * </p>
 *
 * @author mktong, savon_cn, gevak, Selena
 * @version 2.0
 */
public class LongStatementPanelViewImpl extends LongStatementPanelView {

    /**
     * <p>
     * the drop down list captions.
     * </p>
     */
    private String[] STATEMENT_PARTS =
            {"Definition", "Methods", "Exposed Methods", "Introduction", "Notes", "Constraints","Settings"};
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
    
    private static String[] METHOD_COLS =
        {"Name", "Parameters", "Returns"};
    private static int[] METHOD_WIDTHS =
        {500, 1000, 300};

    private LongStatementPanelModel model;
    private LongStatementPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel partLabel;
    private JComboBox partComboBox;
    
    private JLabel definitionLabel;
    private JLabel methodsLabel;
    private JLabel exposedMethodsLabel;
    private JLabel introductionLabel;
    private JLabel notesLabel;
    private JLabel constraintsLabel;
    /**
     * Represents the settings overview label.
     * @since 1.4
     */
    private JLabel settingsLabel;

    private JPanel currentPanel;
    private JPanel definitionPanel;
    private JPanel methodsPanel;
    private JPanel exposedMethodsPanel;
    private JPanel introductionPanel;
    private JPanel notesPanel;
    private JPanel constraintsPanel;
    
    /**
     * Represents the settings panel.
     * @since 1.4
     */
    private JPanel settingsPanel;

    private GridBagLayout definitionLayout;
    private GridBagLayout methodsLayout;
    private GridBagLayout exposedMethodsLayout;
    private GridBagLayout introductionLayout;
    private GridBagLayout notesLayout;
    private GridBagLayout constraintsLayout;
    /**
     * Represents the settings layout.
     * @since 1.4
     */
    private GridBagLayout settingsLayout;

    private JLabel classNameLabel;
    private JLabel memLimitLabel;
    private JLabel methodNameLabel;
    private JLabel parametersLabel;
    private JLabel returnsLabel;
    private JLabel exposedMethodNameLabel;
    private JLabel exposedParametersLabel;
    private JLabel exposedReturnsLabel;
    private JLabel roundTypeLabel;
    private JLabel codeLengthLimitLabel;
    /**
     * Represents the execution time limit text label.
     * @since 1.1
     */
    private JLabel executionTimeLimitLabel;
    /**
     * Represents the compile time limit text label.
     * @since 1.2
     */
    private JLabel compileTimeLimitLabel;

    /**
     * Represents the GCC Build Command Label.
     * @since 1.4
     */
    private JLabel gccBuildCommandLabel;
    /**
     * Represents the cpp approved path Label.
     * @since 1.4
     */
    private JLabel cppApprovedPathLabel;
    
    /**
     * Represents the Python Command Label.
     * @since 1.5
     */
    private JLabel pythonCommandLabel;
    /**
     * Represents the python approved path Label.
     * @since 1.5
     */
    private JLabel pythonApprovedPathLabel;

    /**
     * Represents the submission rate label.
     * @since 1.7
     */
    private JLabel submissionRateLabel;

    /**
     * Represents the example submission rate label.
     * @since 1.7
     */
    private JLabel exampleSubmissionRateLabel;

    /**
     * Represents the execution stack size limit label.
     * @since 2.0
     */
    private JLabel stackLimitLabel;

    /**
     * Represents the execution stack size limit.
     * @since 2.0
     */
    private PanelTextField stackLimitTextField;

    private PanelTextField classNameTextField;
    private PanelTextField exposedClassNameTextField;
    private PanelTextField memLimitTextField;
    private PanelTextField codeLengthLimitTextField;

    /**
     * Represents the GCC build command combo box.
     * @since 1.8
     */
    private JComboBox gccBuildCommandComboBox;

    /**
     * Represents the CPP approved path combo box.
     * @since 1.8
     */
    private JComboBox cppApprovedPathComboBox;

    /**
     * Represents the Python build command combo box.
     * @since 1.8
     */
    private JComboBox pythonCommandComboBox;

    /**
     * Represents the Python approved path combo box.
     * @since 1.8
     */
    private JComboBox pythonApprovedPathComboBox;

    /**
     * Represents the execution time limit text field.
     * @since 1.1
     */
    private PanelTextField executionTimeLimitTextField;
    /**
     * Represents the compile time limit text field.
     * @since 1.2
     */
    private PanelTextField compileTimeLimitTextField;

    /**
     * Represents the submission rate text field.
     * @since 1.7
     */
    private PanelTextField submissionRateTextField;

    /**
     * Represents the example submission rate text field.
     * @since 1.7
     */
    private PanelTextField exampleSubmissionRateTextField;

    private PanelTextField methodNameTextField;
    private PanelTextField parametersTextField;
    private PanelTextField returnsTextField;
    private PanelTextField exposedParametersTextField;
    private PanelTextField exposedReturnsTextField;
    private PanelTextField exposedMethodNameTextField;
    private JComboBox roundTypeCombo;

    private SortableTable methodsTable;
    private JScrollPane methodsScrollPane;
    private JButton methodsAddButton;
    private JButton methodsEditButton;
    private JButton methodsRemoveButton;
    
    private SortableTable exposedMethodsTable;
    private JScrollPane exposedMethodsScrollPane;
    private JButton exposedMethodsAddButton;
    private JButton exposedMethodsEditButton;
    private JButton exposedMethodsRemoveButton;
    
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
        this.controller = (LongStatementPanelController) controller;
    }

    /** Saves this view's model, and sets itself as an observer to the model. */
    public void setModel(ComponentModel model) {
        this.model = (LongStatementPanelModel) model;
        model.addWatcher(this);
    }


    /**
     * <p>Get the stack size limit.</p>
     * @param ci the problem component.
     * @return the stack limit in megabytes.
     * @since 2.0
     */
    private int getStackLimit(ComponentInformation ci) {
        int compId = ci.getComponentId();
        int stackLimitValue = ProblemComponent.DEFAULT_MM_STACK_LIMIT;
        //if new problem, we must set stack limit to default
        if (compId != -1) {
            stackLimitValue = model.getComponentInformation().getProblemCustomSettings().getStackLimit();
        }
        return stackLimitValue;
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     * Also, populates components with information in problemInfo.
     *
     * @param arg the updated object.
     */
    public void update(Object arg) {
        if (arg == null) //make all
        {
            removeAll();

            definitionPanel = new JPanel();
            methodsPanel = new JPanel();
            exposedMethodsPanel = new JPanel();
            introductionPanel = new JPanel();
            notesPanel = new JPanel();
            constraintsPanel = new JPanel();
            settingsPanel = new JPanel();

            definitionLayout = new GridBagLayout();
            methodsLayout = new GridBagLayout();
            exposedMethodsLayout = new GridBagLayout();
            introductionLayout = new GridBagLayout();
            notesLayout = new GridBagLayout();
            constraintsLayout = new GridBagLayout();
            settingsLayout = new GridBagLayout();

            definitionPanel.setLayout(definitionLayout);
            methodsPanel.setLayout(methodsLayout);
            exposedMethodsPanel.setLayout(exposedMethodsLayout);
            introductionPanel.setLayout(introductionLayout);
            notesPanel.setLayout(notesLayout);
            constraintsPanel.setLayout(constraintsLayout);
            settingsPanel.setLayout(settingsLayout);

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

            makeDefinitionPanel();
            makeMethodsPanel();
            makeExposedMethodsPanel();
            makeIntroductionPanel();
            makeNotesPanel();
            makeConstraintsPanel();
            makeSettingsPanel();

            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 100);
            layout.setConstraints(definitionPanel, gbc);
            add(definitionPanel);
            currentPanel = definitionPanel;

            partComboBox.addActionListener(new AppletActionListener(
                    "processPartSelection", controller, false));
                       
            // add displayTestCase() as the first entry of the method table
            if (methodsTable.getRowCount() == 0) {
                methodNameTextField.setText("displayTestCase");
                parametersTextField.setText("String s");
                returnsTextField.setText("String");
                processAddMethod();
                methodNameTextField.setText("");
                parametersTextField.setText("");
                returnsTextField.setText("");
            }
            
        } else if (arg.equals(UpdateTypes.PARAM_TYPES)) //remake constraints
        {
            makeConstraintsPanel();
            //parametersTextField.setText(getParamString());

            if (model.getCurrentPart() == LongStatementPanelModelImpl.CONSTRAINTS) {
                repaint();
            }

        } else if (arg.equals(UpdateTypes.STATEMENT_PART_CHANGE)) {
            remove(currentPanel);

            //update the current panel
            switch (model.getCurrentPart()) {
            case LongStatementPanelModelImpl.DEFINITION:
                currentPanel = definitionPanel;
                break;
            case LongStatementPanelModelImpl.METHODS:
                currentPanel = methodsPanel;
                break;
            case LongStatementPanelModelImpl.EXPOSED_METHODS:
                currentPanel = exposedMethodsPanel;
                break;
            case LongStatementPanelModelImpl.INTRODUCTION:
                currentPanel = introductionPanel;
                break;
            case LongStatementPanelModelImpl.NOTES:
                currentPanel = notesPanel;
                break;
            case LongStatementPanelModelImpl.CONSTRAINTS:
                currentPanel = constraintsPanel;
                break;
            case LongStatementPanelModelImpl.SETTINGS:
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
        } else if (arg.equals(UpdateTypes.METHODS_PART_CHANGE)) {
            methodsLabel.setText("Methods ("+model.getComponentInformation().getAllMethodNames().length+"):");
            update(UpdateTypes.STATEMENT_PART_CHANGE);
        } else if (arg.equals(UpdateTypes.EXPOSED_METHODS_PART_CHANGE)) {
            System.out.println("HERE2");
            System.out.println(model.getComponentInformation().getAllExposedMethodNames().length);
            exposedMethodsLabel.setText("Exposed Methods ("+model.getComponentInformation().getAllExposedMethodNames().length+"):");
            update(UpdateTypes.STATEMENT_PART_CHANGE);
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
                CustomBuildSetting.MM_GCC_BUILD_COMMAND,
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
                CustomBuildSetting.MM_CPP_APPROVED_PATH,
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
                CustomBuildSetting.MM_PYTHON_COMMAND,
                model.getComponentInformation().getProblemCustomSettings().getPythonCommand());
        pythonCommandComboBox.addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        sizedCombo = UIHelper.sizeComponent(pythonCommandComboBox, 1);
        GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 100, 0);
        settingsLayout.setConstraints(sizedCombo, gbc);
        settingsPanel.add(sizedCombo);

        pythonApprovedPathLabel = new JLabel("Python Approved Path:");
        GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
        settingsLayout.setConstraints(pythonApprovedPathLabel, gbc);
        settingsPanel.add(pythonApprovedPathLabel);

        pythonApprovedPathComboBox = UIHelper.createCustomBuildSettingComboBox(
                CustomBuildSetting.MM_PYTHON_APPROVED_PATH,
                model.getComponentInformation().getProblemCustomSettings().getPythonApprovedPath());
        pythonApprovedPathComboBox.addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        sizedCombo = UIHelper.sizeComponent(pythonApprovedPathComboBox, 1);
        GUIConstants.buildConstraints(gbc, 1, 4, 1, 1, 0, 1);
        settingsLayout.setConstraints(sizedCombo, gbc);
        settingsPanel.add(sizedCombo);

        // Add submission rate setting UI.
        submissionRateLabel = new JLabel("Submission rate (mins):");
        GUIConstants.buildConstraints(gbc, 0, 5, 1, 1, 0, 1);
        settingsLayout.setConstraints(submissionRateLabel, gbc);
        settingsPanel.add(submissionRateLabel);

        submissionRateTextField = new PanelTextField(0.2,
                model.getComponentInformation().getSubmissionRate() > 0 ?
                "" + model.getComponentInformation().getSubmissionRate() : "");
        submissionRateTextField.getJTextField()
                .addFocusListener(new AppletFocusListener(
                        "processStatementChange", controller, "focusLost", false));
        GUIConstants.buildConstraints(gbc, 1, 5, 1, 1, 0, 1);
        settingsLayout.setConstraints(submissionRateTextField, gbc);
        settingsPanel.add(submissionRateTextField);

        // Add example submission rate setting UI.
        exampleSubmissionRateLabel = new JLabel("Example submission rate (mins):");
        GUIConstants.buildConstraints(gbc, 0, 6, 1, 1, 0, 1);
        settingsLayout.setConstraints(exampleSubmissionRateLabel, gbc);
        settingsPanel.add(exampleSubmissionRateLabel);

        exampleSubmissionRateTextField = new PanelTextField(0.2,
                model.getComponentInformation().getExampleSubmissionRate() > 0 ?
                "" + model.getComponentInformation().getExampleSubmissionRate() : "");
        exampleSubmissionRateTextField.getJTextField()
                .addFocusListener(new AppletFocusListener(
                        "processStatementChange", controller, "focusLost", false));
        GUIConstants.buildConstraints(gbc, 1, 6, 1, 1, 0, 1);
        settingsLayout.setConstraints(exampleSubmissionRateTextField, gbc);
        settingsPanel.add(exampleSubmissionRateTextField);

        //add empty label to make the component positioned at north
        JLabel emptyLabel = new JLabel("");
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 0, 6, 2, 1, 0, 100);
        settingsLayout.setConstraints(emptyLabel, gbc);
        settingsPanel.add(emptyLabel);
        
        settingsPanel.setBorder(new EtchedBorder());
    }
    /**
     * Builds the definition panel.
     */
    private void makeDefinitionPanel() {
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
        
        memLimitLabel = new JLabel("Memory Limit (MB):");        
        GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
        definitionLayout.setConstraints(memLimitLabel, gbc);
        definitionPanel.add(memLimitLabel);

        memLimitTextField = new PanelTextField(.2,
                model.getComponentInformation().getReturnType().getDescription());
        memLimitTextField.setText(String.valueOf(model.getComponentInformation().getProblemCustomSettings().getMemLimit()));
        memLimitTextField.getJTextField().addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 1);
        definitionLayout.setConstraints(memLimitTextField, gbc);
        definitionPanel.add(memLimitTextField);

        codeLengthLimitLabel = new JLabel("Code Length Limit (Bytes):");
        GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 1);
        definitionLayout.setConstraints(codeLengthLimitLabel, gbc);
        definitionPanel.add(codeLengthLimitLabel);

        codeLengthLimitTextField = new PanelTextField(.2,
                model.getComponentInformation().getReturnType().getDescription());
        codeLengthLimitTextField.setText(String.valueOf(model.getComponentInformation().getCodeLengthLimit()));
        codeLengthLimitTextField.getJTextField().addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 0, 1);
        definitionLayout.setConstraints(codeLengthLimitTextField, gbc);
        definitionPanel.add(codeLengthLimitTextField);
        
        executionTimeLimitLabel = new JLabel("Execution Time Limit (MS):");
        GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
        definitionLayout.setConstraints(executionTimeLimitLabel, gbc);
        definitionPanel.add(executionTimeLimitLabel);
        
        executionTimeLimitTextField = new PanelTextField(.2,
                model.getComponentInformation().getReturnType().getDescription());
        executionTimeLimitTextField.setText(String.valueOf(model.getComponentInformation().
                getProblemCustomSettings().getExecutionTimeLimit()));
        executionTimeLimitTextField.getJTextField().addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        GUIConstants.buildConstraints(gbc, 1, 4, 1, 1, 0, 1);
        definitionLayout.setConstraints(executionTimeLimitTextField, gbc);
        definitionPanel.add(executionTimeLimitTextField);
        
        compileTimeLimitLabel = new JLabel("Compile Time Limit (MS):");
        GUIConstants.buildConstraints(gbc, 0, 5, 1, 1, 0, 1);
        definitionLayout.setConstraints(compileTimeLimitLabel, gbc);
        definitionPanel.add(compileTimeLimitLabel);
        
        compileTimeLimitTextField = new PanelTextField(.2,
                model.getComponentInformation().getReturnType().getDescription());
        compileTimeLimitTextField.setText(String.valueOf(model.getComponentInformation().
                getProblemCustomSettings().getCompileTimeLimit()));
        compileTimeLimitTextField.getJTextField().addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        GUIConstants.buildConstraints(gbc, 1, 5, 1, 1, 0, 1);
        definitionLayout.setConstraints(compileTimeLimitTextField, gbc);
        definitionPanel.add(compileTimeLimitTextField);

        stackLimitLabel = new JLabel("Stack size limit (MB)");
        GUIConstants.buildConstraints(gbc, 0, 6, 1, 1, 0, 1);
        definitionLayout.setConstraints(stackLimitLabel, gbc);
        definitionPanel.add(stackLimitLabel);

        int stackLimit = getStackLimit(model.getComponentInformation());
        stackLimitTextField = new PanelTextField(.2, (stackLimit > 0) ? String.valueOf(stackLimit) : "");
        stackLimitTextField.getJTextField().addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        GUIConstants.buildConstraints(gbc, 1, 6, 1, 1, 0, 1);
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
        GUIConstants.buildConstraints(gbc, 1, 7, 1, 1, 0, 100);
        definitionLayout.setConstraints(jsp, gbc);
        definitionPanel.add(jsp);

        // add round type drop-down list with label
        roundTypeLabel = new JLabel("Round Type:");        
        GUIConstants.buildConstraints(gbc, 0, 8, 1, 1, 0, 1);
        definitionLayout.setConstraints(roundTypeLabel, gbc);
        definitionPanel.add(roundTypeLabel);

        roundTypeCombo = UIHelper.createProblemRoundTypeComboBox(
                ApplicationConstants.LONG_PROBLEM, model.getComponentInformation().getRoundType());
        roundTypeCombo.addFocusListener(new AppletFocusListener(
                "processStatementChange", controller, "focusLost", false));
        JComponent sizedCombo = UIHelper.sizeComponent(roundTypeCombo, .5);

        GUIConstants.buildConstraints(gbc, 1, 8, 1, 1, 0, 1);
        definitionLayout.setConstraints(sizedCombo, gbc);
        definitionPanel.add(sizedCombo);
        
        definitionPanel.setBorder(new EtchedBorder());
    }

    private void makeMethodsPanel() {
        methodsLabel = new JLabel("Methods ("+model.getComponentInformation().getAllMethodNames().length+"):");
        methodsLabel.setFont(DefaultUIValues.HEADER_FONT);
        GUIConstants.buildConstraints(gbc, 0, 0, 4, 1, 1, 1);
        methodsLayout.setConstraints(methodsLabel, gbc);
        methodsPanel.add(methodsLabel);
        
        // methods table
        methodsTable = new SortableTable(METHOD_COLS, getMethodsTableData(), METHOD_WIDTHS);
        methodsTable.setSortOrder(0,true);
        methodsTable.getSelectionModel().addListSelectionListener(
                new AppletListListener("processMethodSelected", this, false));
        methodsScrollPane = new JScrollPane(methodsTable,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GUIConstants.buildConstraints(gbc, 0, 1, 4, 1, 1, 100);
        methodsLayout.setConstraints(methodsScrollPane, gbc);
        methodsPanel.add(methodsScrollPane);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        methodsAddButton = new JButton("Add Method");
        methodsAddButton.addActionListener(new AppletActionListener(
                "processAddMethod", this, false));
        GUIConstants.buildConstraints(gbc, 0, 2, 2, 1, 99, 1);
        methodsLayout.setConstraints(methodsAddButton, gbc);
        methodsPanel.add(methodsAddButton);

        methodsEditButton = new JButton("Edit Method");
        methodsEditButton.addActionListener(new AppletActionListener(
                "processEditMethod", this, false));
        GUIConstants.buildConstraints(gbc, 2, 2, 1, 1, 1, 1);
        methodsLayout.setConstraints(methodsEditButton, gbc);
        methodsPanel.add(methodsEditButton);
        
        methodsRemoveButton = new JButton("Remove Method");
        methodsRemoveButton.addActionListener(new AppletActionListener(
                "processRemoveMethod", this, false));
        GUIConstants.buildConstraints(gbc, 3, 2, 1, 1, 1, 1);
        methodsLayout.setConstraints(methodsRemoveButton, gbc);
        methodsPanel.add(methodsRemoveButton);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        methodNameLabel = new JLabel("Method Name:");
        GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 1);
        methodsLayout.setConstraints(methodNameLabel, gbc);
        methodsPanel.add(methodNameLabel);

        methodNameTextField = new PanelTextField(.3,
                model.getComponentInformation().getMethodName());
        GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 50, 1);
        methodsLayout.setConstraints(methodNameTextField, gbc);
        methodsPanel.add(methodNameTextField);

        parametersLabel = new JLabel("Parameters:");
        GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
        methodsLayout.setConstraints(parametersLabel, gbc);
        methodsPanel.add(parametersLabel);

        parametersTextField = new PanelTextField(.8, getParamString());
        GUIConstants.buildConstraints(gbc, 1, 4, 1, 1, 50, 1);
        methodsLayout.setConstraints(parametersTextField, gbc);
        methodsPanel.add(parametersTextField);

        returnsLabel = new JLabel("Returns:");
        GUIConstants.buildConstraints(gbc, 0, 5, 1, 1, 0, 1);
        methodsLayout.setConstraints(returnsLabel, gbc);
        methodsPanel.add(returnsLabel);

        returnsTextField = new PanelTextField(.2,
                model.getComponentInformation().getReturnType().getDescription());
        GUIConstants.buildConstraints(gbc, 1, 5, 1, 1, 50, 1);
        methodsLayout.setConstraints(returnsTextField, gbc);
        methodsPanel.add(returnsTextField);
        
        methodsPanel.setBorder(new EtchedBorder());
    }
    
    private void makeExposedMethodsPanel() {
        
        exposedMethodsLabel = new JLabel("Exposed Methods ("+model.getComponentInformation().getAllExposedMethodNames().length+"):");
        exposedMethodsLabel.setFont(DefaultUIValues.HEADER_FONT);
        GUIConstants.buildConstraints(gbc, 0, 0, 4, 1, 1, 1);
        exposedMethodsLayout.setConstraints(exposedMethodsLabel, gbc);
        exposedMethodsPanel.add(exposedMethodsLabel);
        
        JLabel exposedClassNameLabel = new JLabel("Class Name:");
        GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 1);
        exposedMethodsLayout.setConstraints(exposedClassNameLabel, gbc);
        exposedMethodsPanel.add(exposedClassNameLabel);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        exposedClassNameTextField = new PanelTextField(.3,model.getComponentInformation().getExposedClassName());
        GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 50, 1);
        exposedMethodsLayout.setConstraints(exposedClassNameTextField, gbc);
        exposedMethodsPanel.add(exposedClassNameTextField);
                
        // methods table
        gbc.fill = GridBagConstraints.BOTH;
        exposedMethodsTable = new SortableTable(METHOD_COLS, getExposedMethodsTableData(), METHOD_WIDTHS);
        exposedMethodsTable.setSortOrder(0,true);
        exposedMethodsTable.getSelectionModel().addListSelectionListener(
                new AppletListListener("processExposedMethodSelected", this, false));
        exposedMethodsScrollPane = new JScrollPane(exposedMethodsTable,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GUIConstants.buildConstraints(gbc, 0, 2, 4, 1, 1, 100);
        exposedMethodsLayout.setConstraints(exposedMethodsScrollPane, gbc);
        exposedMethodsPanel.add(exposedMethodsScrollPane);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        exposedMethodsAddButton = new JButton("Add Method");
        exposedMethodsAddButton.addActionListener(new AppletActionListener(
                "processAddExposedMethod", this, false));
        GUIConstants.buildConstraints(gbc, 0, 3, 2, 1, 99, 1);
        exposedMethodsLayout.setConstraints(exposedMethodsAddButton, gbc);
        exposedMethodsPanel.add(exposedMethodsAddButton);

        exposedMethodsEditButton = new JButton("Edit Method");
        exposedMethodsEditButton.addActionListener(new AppletActionListener(
                "processEditExposedMethod", this, false));
        GUIConstants.buildConstraints(gbc, 2, 3, 1, 1, 1, 1);
        exposedMethodsLayout.setConstraints(exposedMethodsEditButton, gbc);
        exposedMethodsPanel.add(exposedMethodsEditButton);
        
        exposedMethodsRemoveButton = new JButton("Remove Method");
        exposedMethodsRemoveButton.addActionListener(new AppletActionListener(
                "processRemoveExposedMethod", this, false));
        GUIConstants.buildConstraints(gbc, 3, 3, 1, 1, 1, 1);
        exposedMethodsLayout.setConstraints(exposedMethodsRemoveButton, gbc);
        exposedMethodsPanel.add(exposedMethodsRemoveButton);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        exposedMethodNameLabel = new JLabel("Method Name:");
        GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 0, 1);
        exposedMethodsLayout.setConstraints(exposedMethodNameLabel, gbc);
        exposedMethodsPanel.add(exposedMethodNameLabel);

        exposedMethodNameTextField = new PanelTextField(.3,
                "");
        GUIConstants.buildConstraints(gbc, 1, 4, 1, 1, 50, 1);
        exposedMethodsLayout.setConstraints(exposedMethodNameTextField, gbc);
        exposedMethodsPanel.add(exposedMethodNameTextField);

        exposedParametersLabel = new JLabel("Parameters:");
        GUIConstants.buildConstraints(gbc, 0, 5, 1, 1, 0, 1);
        exposedMethodsLayout.setConstraints(exposedParametersLabel, gbc);
        exposedMethodsPanel.add(exposedParametersLabel);

        exposedParametersTextField = new PanelTextField(.8, "");
        GUIConstants.buildConstraints(gbc, 1, 5, 1, 1, 50, 1);
        exposedMethodsLayout.setConstraints(exposedParametersTextField, gbc);
        exposedMethodsPanel.add(exposedParametersTextField);

        exposedReturnsLabel = new JLabel("Returns:");
        GUIConstants.buildConstraints(gbc, 0, 6, 1, 1, 0, 1);
        exposedMethodsLayout.setConstraints(exposedReturnsLabel, gbc);
        exposedMethodsPanel.add(exposedReturnsLabel);

        exposedReturnsTextField = new PanelTextField(.2,
                "");
        GUIConstants.buildConstraints(gbc, 1, 6, 1, 1, 50, 1);
        exposedMethodsLayout.setConstraints(exposedReturnsTextField, gbc);
        exposedMethodsPanel.add(exposedReturnsTextField);
        
        exposedMethodsPanel.setBorder(new EtchedBorder());
    }
    
    private void makeIntroductionPanel() {
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
    }
    
    private void makeNotesPanel() {
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

        int y = 1, maxWidth = 2;

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
        return getParamString(0);
    }
    
    private String getParamString(int idx) {
        StringBuffer params = new StringBuffer(50);
        DataType[] types = model.getComponentInformation().getParamTypes(idx);
        String[] names = model.getComponentInformation().getParamNames(idx);

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
    
    private String getExposedParamString(int idx) {
        StringBuffer params = new StringBuffer(50);
        DataType[] types = model.getComponentInformation().getExposedParamTypes(idx);
        String[] names = model.getComponentInformation().getExposedParamNames(idx);

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
    
    private Object[][] getExposedMethodsTableData() {
        int cnt = model.getComponentInformation().getAllExposedMethodNames().length;
        Object[][] data = new Object[cnt][3];
        for (int i = 0; i < cnt; i++) {
            data[i][0] = model.getComponentInformation().getAllExposedMethodNames()[i];
            data[i][1] = getExposedParamString(i);
            data[i][2] = model.getComponentInformation().getAllExposedReturnTypes()[i].getDescription();
        }
        return data;
    }
    
    private Object[][] getMethodsTableData() {
        int cnt = model.getComponentInformation().getAllMethodNames().length;
        Object[][] data = new Object[cnt][3];
        for (int i = 0; i < cnt; i++) {
            data[i][0] = model.getComponentInformation().getAllMethodNames()[i];
            data[i][1] = getParamString(i);
            data[i][2] = model.getComponentInformation().getAllReturnTypes()[i].getDescription();
        }
        return data;
    }

    /** Returns a name for this view. */
    public String getName() {
        return "Statement Editor";
    }

    public String getClassName() {
        return classNameTextField.getText();
    }
    
    public String getExposedClassName() {
        return exposedClassNameTextField.getText();
    }
    
    public int getMethodCount() {
        return methodsTable.getTableModel().getRowCount();
    }
    
    public int getExposedMethodCount() {
        return exposedMethodsTable.getTableModel().getRowCount();
    }

    /**
     * Get the GCC build command.
     * @return the GCC build command.
     * @since 1.4
     */
    public String getGccBuildCommand() {
        return ((ComboItem) gccBuildCommandComboBox.getSelectedItem()).getId();
    }

    /**
     * Get the CPP approved path.
     * @return The CPP approved path.
     * @since 1.4
     */
    public String getCppApprovedPath() {
        return ((ComboItem) cppApprovedPathComboBox.getSelectedItem()).getId();
    }

    /**
     * Get the Python build command.
     * @return The Python build command.
     * @since 1.4
     */
    public String getPythonCommand() {
        return ((ComboItem) pythonCommandComboBox.getSelectedItem()).getId();
    }

    /**
     * Get the Python approved path.
     * @return The Python approved path.
     * @since 1.4
     */
    public String getPythonApprovedPath() {
        return ((ComboItem) pythonApprovedPathComboBox.getSelectedItem()).getId();
    }

    /**
     * Gets submission rate.
     *
     * @return Submission rate, in minutes. Non-positive integer means that it's not set.
     * @since 1.7
     */
    public int getSubmissionRate() {
        return getSubmissionRateValue(submissionRateTextField, "Submission rate");
    }

    /**
     * Gets example submission rate.
     *
     * @return Example submission rate, in minutes. Non-positive integer means that it's not set.
     * @since 1.7
     */
    public int getExampleSubmissionRate() {
        return getSubmissionRateValue(exampleSubmissionRateTextField, "Example submission rate");
    }

    public String[] getMethodNames() {
        Object[][] data = methodsTable.getTableModel().getData();
        String[] methodNames = new String[methodsTable.getTableModel().getRowCount()];
        for (int i=0; i<methodNames.length; i++) {
            methodNames[i] = (String)data[i][0];
        }
        return methodNames;
    }
    
    public String[] getParameters() {
        Object[][] data = methodsTable.getTableModel().getData();
        String[] parameters = new String[methodsTable.getTableModel().getRowCount()];
        for (int i=0; i<parameters.length; i++) {
            parameters[i] = (String)data[i][1];
        }
        return parameters;
    }
    
    public String[] getExposedReturnTypes() {
        Object[][] data = exposedMethodsTable.getTableModel().getData();
        String[] returnTypes = new String[exposedMethodsTable.getTableModel().getRowCount()];
        for (int i=0; i<returnTypes.length; i++) {
            returnTypes[i] = (String)data[i][2];
        }
        return returnTypes;
    }
    
    public String[] getExposedMethodNames() {
        Object[][] data = exposedMethodsTable.getTableModel().getData();
        String[] methodNames = new String[exposedMethodsTable.getTableModel().getRowCount()];
        for (int i=0; i<methodNames.length; i++) {
            methodNames[i] = (String)data[i][0];
        }
        return methodNames;
    }
    
    public String[] getExposedParameters() {
        Object[][] data = exposedMethodsTable.getTableModel().getData();
        String[] parameters = new String[exposedMethodsTable.getTableModel().getRowCount()];
        for (int i=0; i<parameters.length; i++) {
            parameters[i] = (String)data[i][1];
        }
        return parameters;
    }
    
    public String[] getReturnTypes() {
        Object[][] data = methodsTable.getTableModel().getData();
        String[] returnTypes = new String[methodsTable.getTableModel().getRowCount()];
        for (int i=0; i<returnTypes.length; i++) {
            returnTypes[i] = (String)data[i][2];
        }
        return returnTypes;
    }
    
    public DataType[] getReturnDataTypes() {
        String[] returnTypes = getReturnTypes();
        DataType[] returnDataTypes = new DataType[returnTypes.length];
        for (int i=0; i<returnTypes.length; i++) {
            returnDataTypes[i] = new DataType(returnTypes[i]);
        }
        return returnDataTypes;
    }
    
    public DataType[] getExposedReturnDataTypes() {
        String[] returnTypes = getExposedReturnTypes();
        DataType[] returnDataTypes = new DataType[returnTypes.length];
        for (int i=0; i<returnTypes.length; i++) {
            returnDataTypes[i] = new DataType(returnTypes[i]);
        }
        return returnDataTypes;
    }
    
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
            memLimitTextField.setText(String.valueOf(ProblemComponent.DEFAULT_MEM_LIMIT));
        }
        return memLimit;
    }
    
    /**
     * Gets the stack size limit in megabytes.
     * @return the stack size limit in megabytes.
     * @since 2.0
     */
    public int getStackLimit() {
        int stackLimit = ProblemComponent.DEFAULT_MM_STACK_LIMIT;
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

    public int getCodeLengthLimit() {
        int codeLengthLimit = 0;
        boolean codeLengthLimitValid = true;
        try {
            codeLengthLimit = Integer.parseInt(codeLengthLimitTextField.getText());
            if (codeLengthLimit <= 0) codeLengthLimitValid = false;
        } catch (NumberFormatException nfe) {
            codeLengthLimitValid = false;
        }
        if (!codeLengthLimitValid) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Code length limit must be a positive integer.", true);
            codeLengthLimitTextField.setText(String.valueOf(Integer.MAX_VALUE));
        }
        return codeLengthLimit;
    }
    
    /**
     * Gets the execution time limit.
     * 
     * @return the execution time limit.
     * @since 1.1
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
            executionTimeLimitTextField.setText(String.valueOf(ProblemComponent.DEFAULT_EXECUTION_TIME_LIMIT));
        }
        return executionTimeLimit;
    }
    
    /**
     * Gets the compile time limit.
     * 
     * @return the compile time limit.
     * @since 1.2
     */
    public int getCompileTimeLimit() {
        int compileTimeLimit = 0;
        boolean compileTimeLimitValid = true;
        try {
            compileTimeLimit = Integer.parseInt(compileTimeLimitTextField.getText());
            if (compileTimeLimit <= 0) {
                compileTimeLimitValid = false;
            }
        } catch (NumberFormatException nfe) {
            compileTimeLimitValid = false;
        }
        if (!compileTimeLimitValid) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Compile time limit must be a positive integer.", true);
            compileTimeLimitTextField.setText(String.valueOf(ProblemComponent.DEFAULT_COMPILE_TIME_LIMIT));
        }
        return compileTimeLimit;
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
    
    public void processAddExposedMethod() {
        Object[] row = new Object[3];
        row[0] = exposedMethodNameTextField.getText();
        row[1] = exposedParametersTextField.getText();
        row[2] = exposedReturnsTextField.getText();
        Object[][] oldData = exposedMethodsTable.getTableModel().getData();
        Object[][] data = new Object[oldData.length+1][];
        for (int i=0; i<oldData.length; i++) {
            data[i] = oldData[i];
        }
        data[data.length-1] = row;
        exposedMethodsTable.getTableModel().setData(data);
        exposedMethodsTable.getTableModel().fireTableRowsInserted(data.length-1, data.length-1);
        controller.processStatementChange();
        boolean paramsValid = controller.processParamTypeChange();
        
        if (!paramsValid) {
            exposedMethodsTable.getTableModel().setData(oldData);
            exposedMethodsTable.getTableModel().fireTableRowsDeleted(data.length-1, data.length-1);
            controller.processStatementChange();
            controller.processParamTypeChange();
        }
        update(UpdateTypes.EXPOSED_METHODS_PART_CHANGE);   
    }
    
    public void processEditExposedMethod() {
        int selectedRow = exposedMethodsTable.getSelectedRow();
        Object[][] data = exposedMethodsTable.getTableModel().getData();
        
        String oldMethodName = (String)data[selectedRow][0];
        String oldParameters = (String)data[selectedRow][1];
        String oldReturns = (String)data[selectedRow][2];
        data[selectedRow][0] = exposedMethodNameTextField.getText();
        data[selectedRow][1] = exposedParametersTextField.getText();
        data[selectedRow][2] = exposedReturnsTextField.getText();
        exposedMethodsTable.getTableModel().fireTableRowsUpdated(selectedRow, selectedRow);
        controller.processStatementChange();
        boolean paramsValid = controller.processParamTypeChange();

        if (!paramsValid) {
            data[selectedRow][0] = oldMethodName;
            data[selectedRow][1] = oldParameters;
            data[selectedRow][2] = oldReturns;
            exposedMethodsTable.getTableModel().fireTableRowsUpdated(selectedRow, selectedRow);
            controller.processStatementChange();
            controller.processParamTypeChange();
        }
        update(UpdateTypes.EXPOSED_METHODS_PART_CHANGE);
    }
    
    public void processRemoveExposedMethod() {
        int selectedRow = exposedMethodsTable.getSelectedRow();
        
        Object[][] oldData = exposedMethodsTable.getTableModel().getData();
        Object[][] data = new Object[oldData.length-1][];
        for (int i=0; i<selectedRow; i++) {
            data[i] = oldData[i];
        }
        for (int i=selectedRow+1; i<oldData.length; i++) {
            data[i-1] = oldData[i];
        }
        exposedMethodsTable.getTableModel().setData(data);
        exposedMethodsTable.getTableModel().fireTableRowsDeleted(selectedRow, selectedRow);
        controller.processStatementChange();
        controller.processParamTypeChange();
        update(UpdateTypes.EXPOSED_METHODS_PART_CHANGE);

    }
    
    public void processExposedMethodSelected() {
        int selectedRow = exposedMethodsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Object[][] data = exposedMethodsTable.getTableModel().getData();
            exposedMethodNameTextField.setText((String)data[selectedRow][0]);
            exposedParametersTextField.setText((String)data[selectedRow][1]);
            exposedReturnsTextField.setText((String)data[selectedRow][2]);
        }
    }
    
    public void processAddMethod() {
        Object[] row = new Object[3];
        row[0] = methodNameTextField.getText();
        row[1] = parametersTextField.getText();
        row[2] = returnsTextField.getText();
        Object[][] oldData = methodsTable.getTableModel().getData();
        Object[][] data = new Object[oldData.length+1][];
        for (int i=0; i<oldData.length; i++) {
            data[i] = oldData[i];
        }
        data[data.length-1] = row;
        methodsTable.getTableModel().setData(data);
        methodsTable.getTableModel().fireTableRowsInserted(data.length-1, data.length-1);
        controller.processStatementChange();
        boolean paramsValid = controller.processParamTypeChange();
        
        if (!paramsValid) {
            methodsTable.getTableModel().setData(oldData);
            methodsTable.getTableModel().fireTableRowsDeleted(data.length-1, data.length-1);
            controller.processStatementChange();
            controller.processParamTypeChange();
        }
        update(UpdateTypes.METHODS_PART_CHANGE);   
    }
    
    public void processEditMethod() {
        int selectedRow = methodsTable.getSelectedRow();
        Object[][] data = methodsTable.getTableModel().getData();
        if (selectedRow > 0) {            
            String oldMethodName = (String)data[selectedRow][0];
            String oldParameters = (String)data[selectedRow][1];
            String oldReturns = (String)data[selectedRow][2];
            data[selectedRow][0] = methodNameTextField.getText();
            data[selectedRow][1] = parametersTextField.getText();
            data[selectedRow][2] = returnsTextField.getText();
            methodsTable.getTableModel().fireTableRowsUpdated(selectedRow, selectedRow);
            controller.processStatementChange();
            boolean paramsValid = controller.processParamTypeChange();
            
            if (!paramsValid) {
                data[selectedRow][0] = oldMethodName;
                data[selectedRow][1] = oldParameters;
                data[selectedRow][2] = oldReturns;
                methodsTable.getTableModel().fireTableRowsUpdated(selectedRow, selectedRow);
                controller.processStatementChange();
                controller.processParamTypeChange();
            }
            update(UpdateTypes.METHODS_PART_CHANGE);
        } else if (selectedRow == 0) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "The 'displayTestCase' method cannot be edited. ", true);
        }
    }
    
    public void processRemoveMethod() {
        int selectedRow = methodsTable.getSelectedRow();
        if (selectedRow > 0) {
            Object[][] oldData = methodsTable.getTableModel().getData();
            Object[][] data = new Object[oldData.length-1][];
            for (int i=0; i<selectedRow; i++) {
                data[i] = oldData[i];
            }
            for (int i=selectedRow+1; i<oldData.length; i++) {
                data[i-1] = oldData[i];
            }
            methodsTable.getTableModel().setData(data);
            methodsTable.getTableModel().fireTableRowsDeleted(selectedRow, selectedRow);
            controller.processStatementChange();
            controller.processParamTypeChange();
            update(UpdateTypes.METHODS_PART_CHANGE);
        } else if (selectedRow == 0) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "The 'displayTestCase' method cannot be removed. ", true);
        }
    }
    
    public void processMethodSelected() {
        int selectedRow = methodsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Object[][] data = methodsTable.getTableModel().getData();
            methodNameTextField.setText((String)data[selectedRow][0]);
            parametersTextField.setText((String)data[selectedRow][1]);
            returnsTextField.setText((String)data[selectedRow][2]);
        }
    }
    
    private String getTextRepresentation(Element e) {
        try {
            return MPSQASRendererFactory.getInstance().getRenderer(e).toHTML(JavaLanguage.JAVA_LANGUAGE);
        } catch (Exception e1) {
            return "error trying to render element" + e.toXML();
        }
    }

    /**
     * Gets value from a submission rate setting UI element.
     * It validates the value and shows validation error to user if necessary.
     *
     * @param field Source UI element.
     * @param fieldName Field name.
     * @return Submission rate value. -1 if no value entered or value is invalid.
     * @since 1.7
     */
    private int getSubmissionRateValue(PanelTextField field, String fieldName) {
        if (field.getText().trim().length() == 0) return -1;
        int value = 0;
        boolean valueValid = true;
        try {
            value = Integer.parseInt(field.getText());
            if (value <= 0) valueValid = false;
        } catch (NumberFormatException nfe) {
            valueValid = false;
        }
        if (!valueValid) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    fieldName + " must be empty or a positive integer.", true);
            field.setText("");
        }
        return value;
    }
}
