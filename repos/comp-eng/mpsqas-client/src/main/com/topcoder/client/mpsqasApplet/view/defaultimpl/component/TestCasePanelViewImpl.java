package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.JTextComponent;

import com.topcoder.client.mpsqasApplet.common.MPSQASRendererFactory;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.controller.component.TestCasePanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.model.component.TestCasePanelModel;
import com.topcoder.client.mpsqasApplet.util.XMLParser;
import com.topcoder.client.mpsqasApplet.view.component.TestCasePanelView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletFocusListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletListListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletMouseListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.SortableTable;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.widget.PanelTextField;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.TestCase;

/**
 * The TestCasePanel is a panel through which a user can edit the
 * different parts to the problem statement.
 *
 * @author mitalub
 */
public class TestCasePanelViewImpl extends TestCasePanelView {

    private static final String[] TEST_CASE_HEADERS = {"Case", "Example"};
    private static final int[] TEST_CASE_WIDTHS = {100, 100};
    private static final boolean[] TEST_CASE_EDITABLES = {false, true};

    private TestCasePanelModel model;
    private TestCasePanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private SortableTable testCaseTable;
    private JTextComponent[] paramValueField;
    private JTextArea annotationTextArea;
    private AppletListListener listener;

    private JPanel currentCasePanel;

    private JButton newButton;
    private JButton deleteButton;
    private JButton testButton;
    private JButton upButton;
    private JButton downButton;
    private JButton randomButton;
    private JTextField testcaseFile;

    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     * Also, populates components with information in problemInfo.
     */
    public void update(Object arg) {
        if (arg == null || arg.equals(UpdateTypes.PARAM_TYPES)) {
            removeAll();

            JLabel title = new JLabel("Test Cases: ");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 2, 1, 0, 1);
            layout.setConstraints(title, gbc);
            add(title);

            testCaseTable = new SortableTable(getColumnHeaders(),
                    getTestCaseData(),
                    getColumnWidths(),
                    getColumnEditables());
            testCaseTable.addMouseListener(new AppletMouseListener(
                    "processFlagsChange", controller, "mouseClicked", false));
//            testCaseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JScrollPane testCaseScrollPane = new JScrollPane(
                    testCaseTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 2, 40, 0);
            gbc.fill = GridBagConstraints.BOTH;
            layout.setConstraints(testCaseScrollPane, gbc);
            add(testCaseScrollPane);

            currentCasePanel = getCurrentCasePanel();
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 70, 100);
            layout.setConstraints(currentCasePanel, gbc);
            add(currentCasePanel);

            Box buttonBox = Box.createHorizontalBox();
            upButton = new JButton("Up");
            upButton.addActionListener(new AppletActionListener("processMoveTestCaseUp", controller, false));
            buttonBox.add(upButton);
            buttonBox.add(Box.createHorizontalStrut(5));

            downButton = new JButton("Down");
            downButton.addActionListener(new AppletActionListener("processMoveTestCaseDown", controller, false));
            buttonBox.add(downButton);
            buttonBox.add(Box.createHorizontalStrut(5));

            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(new AppletActionListener(
                    "processDeleteTestCase", controller, false));
            buttonBox.add(deleteButton);
            buttonBox.add(Box.createHorizontalStrut(5));

            newButton = new JButton("New");
            newButton.addActionListener(new AppletActionListener(
                    "processAddTestCase", controller, false));
            buttonBox.add(newButton);
            buttonBox.add(Box.createHorizontalStrut(5));

            testButton = new JButton("Test");
            testButton.addActionListener(new AppletActionListener(
                    "processTestTestCase", controller, false));
            buttonBox.add(testButton);
            buttonBox.add(Box.createHorizontalStrut(5));

            randomButton = new JButton("Add from file:");
            randomButton.addActionListener(new AppletActionListener(
                    "processAddRandomTestCases", controller, false));
            testcaseFile = new JTextField(10);
            
            arrangeFromFileLayoutComponents(buttonBox);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 1);
            layout.setConstraints(buttonBox, gbc);
            add(buttonBox);

            listener = new AppletListListener("processTestCaseSelected",
                    this, true);
            testCaseTable.getSelectionModel().addListSelectionListener(listener);

            if (arg != null && arg.equals(UpdateTypes.PARAM_TYPES)) {
                invalidate();
                validate();
                repaint();
            }
        } else if (arg.equals(UpdateTypes.TEST_CASE_LIST)
                || arg.equals(UpdateTypes.CURRENT_TEST_CASE)) {
                if(arg.equals(UpdateTypes.CURRENT_TEST_CASE)){
                    controller.processCurrentCaseChange();
                    int index = getSelectedTestCaseIndex();
                    if (index == -1) {
                        model.setCurrentCaseIndex(-1);
                    } else {
                        model.setCurrentCaseIndex(index);
                        model.setCurrentCase(model.getComponentInformation()
                                .getTestCases()[index]);
                    }
                }
            testCaseTable.getSelectionModel().removeListSelectionListener(listener);
            testCaseTable.setData(getTestCaseData());
            testCaseTable.getTableModel().fireTableDataChanged();

            if (model.getCurrentCaseIndex() == -1) {
                testCaseTable.clearSelection();
                for (int i = 0; i < paramValueField.length; i++) {
                    paramValueField[i].setText("");
                    paramValueField[i].setEditable(false);
                    paramValueField[i].setBackground(Color.lightGray);
                }
                annotationTextArea.setText("");
                annotationTextArea.setEditable(false);
                annotationTextArea.setBackground(Color.lightGray);
            } else {
                testCaseTable.setRowSelectionInterval(model.getCurrentCaseIndex(),
                        model.getCurrentCaseIndex());
                for (int i = 0; i < paramValueField.length; i++) {
                    paramValueField[i].setText(model.getCurrentCase().getInput()[i]);
                    paramValueField[i].setEditable(true);
                    paramValueField[i].setBackground(Color.white);
                }
                if (model.getCurrentCase().getAnnotation() == null) {
                    annotationTextArea.setText("");
                } else {
                    String annote = getTextRepresentation(model.getCurrentCase().getAnnotation());
                    annotationTextArea.setText(annote);
                }
                annotationTextArea.setEditable(true);
                annotationTextArea.setBackground(Color.white);

            }
            testCaseTable.getSelectionModel().addListSelectionListener(listener);
        }
    }

    /**
     * This method allows to the view to arrange randomButton, testcaseFile components
     * and add other necessary components. 
     */
    protected void arrangeFromFileLayoutComponents(JComponent containingBox) {
       containingBox.add(randomButton);
       containingBox.add(Box.createHorizontalStrut(5));
       containingBox.add(testcaseFile);
    }

    /**
     * @return An aaray containing editable property value for each column of the table
     */
    protected boolean[] getColumnEditables() {
        return TEST_CASE_EDITABLES;
    }

    /**
     * @return The width of each column of the table
     */
    protected int[] getColumnWidths() {
        return TEST_CASE_WIDTHS;
    }

    /**
     * @return The header of each column
     */
    protected String[] getColumnHeaders() {
        return TEST_CASE_HEADERS;
    }

    /**
     * Returns an Object[][] for use in table initialization containing the
     * test case data in the model.
     */
    protected Object[][] getTestCaseData() {
        TestCase[] testCases = model.getComponentInformation().getTestCases();
        Object[][] data = new Object[testCases.length][2];
        for (int i = 0; i < testCases.length; i++) {
            data[i][0] = new Integer(i);
            data[i][1] = new Boolean(testCases[i].isExample());
        }
        return data;
    }

    /**
     * Returns a JPanel containing the current test case.
     */
    private JPanel getCurrentCasePanel() {
        JPanel panel = new JPanel();
        GridBagLayout panelLayout = new GridBagLayout();
        panel.setLayout(panelLayout);

        DataType[] paramTypes = model.getComponentInformation().getParamTypes();

        String[] args;
        String annote;

        if (model.getCurrentCaseIndex() >= 0) {
            args = model.getCurrentCase().getInput();
            if (model.getCurrentCase().getAnnotation() != null) {
                annote = getTextRepresentation(model.getCurrentCase().getAnnotation());
            } else {
                annote = "";
            }
        } else {
            annote = "";
            args = new String[paramTypes.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = "";
            }
        }

        int y = 0;
        int i = 0;

        paramValueField = new JTextComponent[paramTypes.length];
        JLabel[] paramTypeLabel = new JLabel[paramTypes.length];

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        PanelTextField ptf;
        JTextArea jta;
        JScrollPane jsp;

        //make a type: value for each param type
        for (i = 0; i < paramTypes.length; i++) {
            paramTypeLabel[i] = new JLabel(paramTypes[i].getDescription());
            GUIConstants.buildConstraints(gbc, 0, y, 1, 1, 1, 1);
            panelLayout.setConstraints(paramTypeLabel[i], gbc);
            panel.add(paramTypeLabel[i]);

            if (paramTypes[i].getDimension() < 1) {
                ptf = new PanelTextField(GUIConstants.getTextFieldWidth(paramTypes[i]),
                        args[i]);
                paramValueField[i] = ptf.getJTextField();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                GUIConstants.buildConstraints(gbc, 1, y++, 1, 1, 100, 1);
                panelLayout.setConstraints(ptf, gbc);
                panel.add(ptf);
            } else {
                jta = new JTextArea(args[i]);
                paramValueField[i] = jta;
                jsp = new JScrollPane(jta);
                gbc.fill = GridBagConstraints.BOTH;
                GUIConstants.buildConstraints(gbc, 1, y++, 1, 1, 100, 15);
                panelLayout.setConstraints(jsp, gbc);
                panel.add(jsp);
            }

            paramValueField[i].addFocusListener(new AppletFocusListener(
                    "processCurrentCaseChange", controller, "focusLost", false));
            if (model.getCurrentCaseIndex() < 0) {
                paramValueField[i].setEditable(false);
                paramValueField[i].setBackground(Color.lightGray);
            }
        }

        JLabel annotationLabel = new JLabel("Annotation:");
        gbc.fill = GridBagConstraints.BOTH;
        if (paramTypes.length > 0) {
            GUIConstants.buildConstraints(gbc, 0, y++, 2, 1, 0, 1);
        } else {
            GUIConstants.buildConstraints(gbc, 0, y++, 1, 1, 100, 1);
        }
        panelLayout.setConstraints(annotationLabel, gbc);
        panel.add(annotationLabel);

        annotationTextArea = new JTextArea(XMLParser.removeOuterTag(annote));
        annotationTextArea.addFocusListener(new AppletFocusListener(
                "processCurrentCaseChange", controller, "focusLost", false));
        annotationTextArea.setLineWrap(true);
        annotationTextArea.setWrapStyleWord(true);
        if (model.getCurrentCaseIndex() < 0) {
            annotationTextArea.setEditable(false);
            annotationTextArea.setBackground(Color.lightGray);
        }
        JScrollPane annotationScrollPane = new JScrollPane(annotationTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GUIConstants.buildConstraints(gbc, 0, y++, 2, 1, 0, 100);
        panelLayout.setConstraints(annotationScrollPane, gbc);
        panel.add(annotationScrollPane);

        return panel;
    }


    /** Saves this view's controller */
    public void setController(ComponentController controller) {
        this.controller = (TestCasePanelController) controller;
    }

    /** Saves this view's model, and sets itself as an observer to the model. */
    public void setModel(ComponentModel model) {
        this.model = (TestCasePanelModel) model;
        model.addWatcher(this);
    }

    /** Returns a name for this view. */
    public String getName() {
        return "Test Data";
    }

    /** Returns the index of the currently selected test case */
    public int getSelectedTestCaseIndex() {
        return testCaseTable.getSelectedRow();
    }

    /** Returns an ArrayList of Strings of the arguments the user has entered.*/
    public String[] getArgs() {
        String[] args = new String[paramValueField.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = paramValueField[i].getText();
        }
        return args;
    }

    /** Returns the current annotation. */
    public String getAnnotation() {
        return annotationTextArea.getText();
    }

    /**
     * Returns true if the test case at the specified index is checked to be
     * an example.
     */
    public boolean isExample(int index) {
        return ((Boolean) testCaseTable.getAbsoluteValueAt(index, 1))
                .booleanValue();

    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.view.component.TestCasePanelView#isSystemTest(int)
     * Returns false, only long test case could be system tests
     */
    public boolean isSystemTest(int index) {
        return false;
    }

    /**
     * If this is no longer a ListSelectionEvent in a rapid chain, calls the
     * controller's method to update the value.
     */
    public void processTestCaseSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting())
            controller.processTestCaseSelected();
    }

    public String getTestcaseFile() {
        return testcaseFile.getText();
    }

    protected JButton getRandomButton() {
        return randomButton;
    }
    
    protected JTextField getTestcaseFileComponent() {
        return testcaseFile;
    }
    
    /**
     * @return Returns the model.
     */
    protected TestCasePanelModel getModel() {
        return model;
    }

    /**
     * @return Returns the testCaseTable.
     */
    protected SortableTable getTestCaseTable() {
        return testCaseTable;
    }
    
    private String getTextRepresentation(Element e) {
        try {
            return MPSQASRendererFactory.getInstance().getRenderer(e).toHTML(JavaLanguage.JAVA_LANGUAGE);
        } catch (Exception e1) {
            return "Error trying to render element " + e.toXML();
        }
    }

    /**
     * @see com.topcoder.client.mpsqasApplet.view.component.TestCasePanelView#getAddRamdomTestCaseData()
     */
    public RandomTestCaseData getAddRamdomTestCaseData() {
        return new RandomTestCaseData(getTestcaseFile());
    }

   
}
