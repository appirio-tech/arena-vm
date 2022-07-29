package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.WebServiceRoomView;
import com.topcoder.client.mpsqasApplet.model.WebServiceRoomModel;
import com.topcoder.client.mpsqasApplet.controller.WebServiceRoomController;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.widget.PanelTextField;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 * Default implementation of WebServiceRoomView, which holds
 * other views in a tabbed pane.
 *
 * @author mitalub
 */
public class WebServiceRoomViewImpl extends JPanelView
        implements WebServiceRoomView {

    private final static String[] CLASS_COL_NAMES = {"Helper Classes"};

    private WebServiceRoomController controller;
    private WebServiceRoomModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JLabel titleLabel;
    private JTabbedPane tabbedPane;

    private JButton deployButton;

    private JPanel mainPanel;
    private GridBagLayout mainLayout;
    private SortableTable classTable;
    private JButton removeClassButton;
    private JButton addClassButton;
    private JTextField classTextField;
    private PanelTextField interfaceTextField;
    private PanelTextField implementationTextField;

    public void init() {
        model = MainObjectFactory.getWebServiceRoomModel();
        controller = MainObjectFactory.getWebServiceRoomController();

        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);

        tabbedPane = new JTabbedPane();

        model.addWatcher(this);
    }

    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            titleLabel = new JLabel("Web Service (" +
                    model.getWebServiceInformation().getName() + "):");
            titleLabel.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(titleLabel, gbc);
            add(titleLabel);

            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 100);
            layout.setConstraints(tabbedPane, gbc);
            add(tabbedPane);

            Box buttonBox = Box.createHorizontalBox();

            if (model.isEditable()) {
                deployButton = new JButton("Build & Deploy");
                deployButton.addActionListener(new AppletActionListener(
                        "processDeploy", controller, false));
                buttonBox.add(deployButton);
                buttonBox.add(Box.createHorizontalStrut(5));
            }

            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 1, 1);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            layout.setConstraints(buttonBox, gbc);
            add(buttonBox);

            //Make the main web services panel, through which the user
            //can add and remove classes from the web service.
            mainPanel = new JPanel(mainLayout = new GridBagLayout());
            JLabel title = new JLabel("Web Service Classes:");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 4, 1, 0, 1);
            mainLayout.setConstraints(title, gbc);
            mainPanel.add(title);

            JLabel interfaceLabel = new JLabel("Interface Class:");
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 1, 1);
            mainLayout.setConstraints(interfaceLabel, gbc);
            mainPanel.add(interfaceLabel);

            interfaceTextField = new PanelTextField(1,
                    model.getWebServiceInformation().getInterfaceClass());
            interfaceTextField.setEditable(model.isEditable());
            interfaceTextField.getJTextField()
                    .addFocusListener(new AppletFocusListener(
                            "processInterfaceChanged", controller, "focusLost", false));
            gbc.fill = GridBagConstraints.HORIZONTAL;
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 0, 0);
            mainLayout.setConstraints(interfaceTextField, gbc);
            mainPanel.add(interfaceTextField);

            JLabel implementationLabel = new JLabel("Implementation Class:");
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
            mainLayout.setConstraints(implementationLabel, gbc);
            mainPanel.add(implementationLabel);

            implementationTextField = new PanelTextField(1,
                    model.getWebServiceInformation().getImplementationClass());
            implementationTextField.setEditable(model.isEditable());
            implementationTextField.getJTextField()
                    .addFocusListener(new AppletFocusListener(
                            "processImplementationChanged", controller, "focusLost", false));
            GUIConstants.buildConstraints(gbc, 1, 2, 1, 1, 0, 0);
            mainLayout.setConstraints(implementationTextField, gbc);
            mainPanel.add(implementationTextField);

            JLabel classLabel = new JLabel("Helper Class: ");
            GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 1);
            mainLayout.setConstraints(classLabel, gbc);
            mainPanel.add(classLabel);

            classTextField = new JTextField();
            classTextField.setEditable(model.isEditable());
            gbc.fill = GridBagConstraints.HORIZONTAL;
            GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 100, 0);
            mainLayout.setConstraints(classTextField, gbc);
            mainPanel.add(classTextField);

            addClassButton = new JButton("Add Class");
            addClassButton.setEnabled(model.isEditable());
            addClassButton.addActionListener(new AppletActionListener(
                    "processAddClass", controller, false));
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 2, 3, 1, 1, 1, 0);
            mainLayout.setConstraints(addClassButton, gbc);
            mainPanel.add(addClassButton);

            removeClassButton = new JButton("Remove Class");
            removeClassButton.setEnabled(model.isEditable());
            removeClassButton.addActionListener(new AppletActionListener(
                    "processRemoveClass", controller, false));
            GUIConstants.buildConstraints(gbc, 3, 3, 1, 1, 1, 0);
            gbc.anchor = GridBagConstraints.WEST;
            mainLayout.setConstraints(removeClassButton, gbc);
            mainPanel.add(removeClassButton);

            classTable = new SortableTable(CLASS_COL_NAMES,
                    getClassTableData());
            JScrollPane classListScrollPane = new JScrollPane(classTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.anchor = GridBagConstraints.CENTER;
            GUIConstants.buildConstraints(gbc, 0, 4, 4, 1, 0, 100);
            mainLayout.setConstraints(classListScrollPane, gbc);
            mainPanel.add(classListScrollPane);
            classListScrollPane.setMinimumSize(new Dimension(200, 1));

            tabbedPane.insertTab("Classes", null, mainPanel, null, 0);
        } else if (arg.equals(UpdateTypes.CLASS_LIST)) {
            classTable.setData(getClassTableData());
        }
    }

    /**
     * Returns an Object[][] containing data to populate the class table.
     */
    private Object[][] getClassTableData() {
        ArrayList classes = model.getWebServiceInformation().getHelperClasses();
        Object[][] data = new Object[classes.size()][1];
        for (int i = 0; i < classes.size(); i++) {
            data[i][0] = (String) classes.get(i);
        }
        return data;
    }

    public String getClassName() {
        return classTextField.getText();
    }

    public int getSelectedClassIndex() {
        return classTable.getSelectedRow();
    }

    /**
     * Clears the text in the class text field.
     */
    public void clearClass() {
        classTextField.setText("");
    }

    public void addComponent(ComponentView componentView) {
        String name = componentView.getName();
        tabbedPane.addTab(name, componentView);
        repaint();
    }

    public void removeComponent(ComponentView componentView) {
        tabbedPane.remove(componentView);
        repaint();
    }

    /**
     * Removes all the tabs from the TabbedPane.
     */
    public void removeAllComponents() {
        tabbedPane.removeAll();
    }

    public String getInterface() {
        return interfaceTextField.getText();
    }

    public String getImplementation() {
        return implementationTextField.getText();
    }
}
