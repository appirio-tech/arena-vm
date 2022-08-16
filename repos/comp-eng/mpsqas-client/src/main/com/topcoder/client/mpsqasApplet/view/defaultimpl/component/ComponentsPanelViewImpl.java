package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

//Note: commented out stuff relating to combo box is when moving from
//associated web services with components to web services with problems
//11-26-02, mitalub

import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentsPanelView;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentsPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentsPanelModel;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable.*;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.common.HiddenObject;
import com.topcoder.netCommon.mpsqas.*;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

/**
 * Default implementation of the components panel view.  Contains a
 * table of the web service, with the buttons and fields to add another
 * web service, or to remove a selected web service.  Contains a tree table,
 * representing the problem and its components, with the buttons and fields
 * to add another component.
 *
 * @author mitalub
 */
public class ComponentsPanelViewImpl extends ComponentsPanelView {

    private static final String[] COMPONENTS_COL_NAMES =
            {"Class", "Method", "Last Modified"};
    private static final int[] COMPONENTS_COL_WIDTHS =
            {100, 100, 100};

/*
  private static final String[] WEBSERVICES_COL_NAMES =
          {"Name", "Component"};
  private static final int[] WEBSERVICES_COL_WIDTHS =
          {100, 100};
*/
    private static final String[] WEBSERVICES_COL_NAMES = {"Name"};
    private static final int[] WEBSERVICES_COL_WIDTHS = {100};

    private ComponentsPanelModel model;
    private ComponentsPanelController controller;

    private SortableTable webServicesTable;
    private TreeTable componentsTable;
    private JTextField webServiceNameTextField;
    private JTextField componentClassTextField;
    private JTextField componentMethodTextField;
    private JButton addComponentButton;
    private JButton removeComponentButton;
    private JButton viewComponentButton;
    private JButton addWebServiceButton;
    private JButton removeWebServiceButton;
    private JButton viewWebServiceButton;
//  private JComboBox componentComboBox;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    public void init() {
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        setLayout(layout);
    }

    public void setModel(ComponentModel model) {
        this.model = (ComponentsPanelModel) model;
        model.addWatcher(this);
    }

    public void setController(ComponentController controller) {
        this.controller = (ComponentsPanelController) controller;
    }

    /**
     * Lays out the screen.
     */
    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            JPanel componentsPanel = new JPanel();
            GridBagLayout componentsLayout = new GridBagLayout();
            componentsPanel.setLayout(componentsLayout);

            JPanel webServicesPanel = new JPanel();
            GridBagLayout webServicesLayout = new GridBagLayout();
            webServicesPanel.setLayout(webServicesLayout);

            componentsPanel.setBorder(new EtchedBorder());
            webServicesPanel.setBorder(new EtchedBorder());

            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 5, 5, 5);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(componentsPanel, gbc);
            add(componentsPanel);

            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
            layout.setConstraints(webServicesPanel, gbc);
            add(webServicesPanel);

            int width = model.isEditable() ? 2 : 1;
            int xweight = model.isEditable() ? 0 : 1;

            JLabel componentsTitle = new JLabel("Components: ");
            componentsTitle.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, width, 1, xweight, 1);
            componentsLayout.setConstraints(componentsTitle, gbc);
            componentsPanel.add(componentsTitle);

            componentsTable = new TreeTable(getComponentsRoot(),
                    COMPONENTS_COL_NAMES, COMPONENTS_COL_WIDTHS);
            componentsTable.getTree().setRootVisible(false);
            componentsTable.fullyExpand();
            JScrollPane componentsScrollPane = new JScrollPane(componentsTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, width, 1, 0, 100);
            componentsLayout.setConstraints(componentsScrollPane, gbc);
            componentsPanel.add(componentsScrollPane);

            if (model.isEditable()) {
                JLabel componentClassLabel = new JLabel("New Component Class & Method:");
                GUIConstants.buildConstraints(gbc, 0, 2, 2, 1, 0, 1);
                componentsLayout.setConstraints(componentClassLabel, gbc);
                componentsPanel.add(componentClassLabel);

                componentClassTextField = new JTextField();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 1, 1);
                componentsLayout.setConstraints(componentClassTextField, gbc);
                componentsPanel.add(componentClassTextField);

                componentMethodTextField = new JTextField();
                GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 1, 0);
                componentsLayout.setConstraints(componentMethodTextField, gbc);
                componentsPanel.add(componentMethodTextField);

            }
            Box buttonBox = Box.createHorizontalBox();

            if (model.isEditable()) {
                addComponentButton = new JButton("Add");
                addComponentButton.addActionListener(new AppletActionListener(
                        "processAddComponent", controller, false));
                buttonBox.add(addComponentButton);
                buttonBox.add(Box.createHorizontalStrut(5));

                removeComponentButton = new JButton("Remove");
                removeComponentButton.addActionListener(new AppletActionListener(
                        "processRemoveComponent", controller, false));
                buttonBox.add(removeComponentButton);
                buttonBox.add(Box.createHorizontalStrut(5));
            }

            viewComponentButton = new JButton("View");
            viewComponentButton.addActionListener(new AppletActionListener(
                    "processViewComponent", controller, false));
            buttonBox.add(viewComponentButton);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            int y = model.isEditable() ? 4 : 2;
            GUIConstants.buildConstraints(gbc, 0, y, width, 1, 0, 1);
            componentsLayout.setConstraints(buttonBox, gbc);
            componentsPanel.add(buttonBox);

            JLabel webServicesTitle = new JLabel("Web Services: ");
            webServicesTitle.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            webServicesLayout.setConstraints(webServicesTitle, gbc);
            webServicesPanel.add(webServicesTitle);

            webServicesTable = new SortableTable(WEBSERVICES_COL_NAMES,
                    getWebServicesData(), WEBSERVICES_COL_WIDTHS);
            JScrollPane webServicesScrollPane = new JScrollPane(webServicesTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
            webServicesLayout.setConstraints(webServicesScrollPane, gbc);
            webServicesPanel.add(webServicesScrollPane);

            if (model.isEditable()) {
                JLabel webServiceNameLabel = new JLabel("New Service Name:");
                GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
                webServicesLayout.setConstraints(webServiceNameLabel, gbc);
                webServicesPanel.add(webServiceNameLabel);

                webServiceNameTextField = new JTextField();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 1);
                webServicesLayout.setConstraints(webServiceNameTextField, gbc);
                webServicesPanel.add(webServiceNameTextField);

//        componentComboBox = new JComboBox();
//        HiddenObject[] components = getComponentNames();
//        for(int i = 0; i < components.length; i++)
//        {
//          componentComboBox.addItem(components[i]);
//        }
//        GUIConstants.buildConstraints(gbc, 1, 3, 1, 1, 1, 1);
//        webServicesLayout.setConstraints(componentComboBox, gbc);
//        webServicesPanel.add(componentComboBox);
            }

            buttonBox = Box.createHorizontalBox();

            if (model.isEditable()) {
                addWebServiceButton = new JButton("Add");
                addWebServiceButton.addActionListener(new AppletActionListener(
                        "processAddWebService", controller, false));
                buttonBox.add(addWebServiceButton);
                buttonBox.add(Box.createHorizontalStrut(5));

                removeWebServiceButton = new JButton("Remove");
                removeWebServiceButton.addActionListener(new AppletActionListener(
                        "processRemoveWebService", controller, false));
                buttonBox.add(removeWebServiceButton);
                buttonBox.add(Box.createHorizontalStrut(5));
            }

            viewWebServiceButton = new JButton("View");
            viewWebServiceButton.addActionListener(new AppletActionListener(
                    "processViewWebService", controller, false));
            buttonBox.add(viewWebServiceButton);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            y = model.isEditable() ? 4 : 2;
            GUIConstants.buildConstraints(gbc, 0, y, width, 1, 0, 1);
            webServicesLayout.setConstraints(buttonBox, gbc);
            webServicesPanel.add(buttonBox);
        } else if (arg.equals(UpdateTypes.COMPONENTS_LIST)) {
            componentsTable.updateRoot(getComponentsRoot());
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    componentsTable.fullyExpand();
                }
            });

/*
      componentComboBox.removeAllItems();
      HiddenObject[] components = getComponentNames();
      for(int i = 0; i < components.length; i++)
      {
        componentComboBox.addItem(components[i]);
      }
*/
        } else if (arg.equals(UpdateTypes.WEBSERVICES_LIST)) {
            webServicesTable.setData(getWebServicesData());
        }
    }

    /**
     * Returns a HiddenObject[] with all the component names and
     * component information objects.
     */
/*
  private HiddenObject[] getComponentNames()
  {
    HiddenObject[] components = new HiddenObject[model.getComponents().size()];
    for(int i = 0; i < model.getComponents().size(); i++)
    {
      components[i] = new HiddenObject(((ComponentInformation)model
              .getComponents().get(i)).getClassName(),
              model.getComponents().get(i));
    }
    return components;
  }
*/

    /**
     * Returns the root of a populated tree representing the components
     * of the problem.
     */
    private MutableTreeTableNode getComponentsRoot() {
        ArrayList components = model.getComponents();
        HashMap componentHash = new HashMap();
        ComponentInformation info;

        MutableTreeTableNode root = new MutableTreeTableNode(
                new Object[]{"", "", ""});
        MutableTreeTableNode mainComponent = root;

        //add them all to the hash
        for (int i = 0; i < components.size(); i++) {
            info = (ComponentInformation) components.get(i);
            componentHash.put(info,
                    new MutableTreeTableNode(new Object[]{
                        new HiddenObject(info.getClassName(), info),
                        info.getMethodName(),
                        info.getLastModified()}));
            if (info.getComponentTypeID() == ApplicationConstants.MAIN_COMPONENT) {
                mainComponent = (MutableTreeTableNode) componentHash.get(info);
            }
        }

        //set all the parent / child relationships
        for (int i = 0; i < components.size(); i++) {
            info = (ComponentInformation) components.get(i);
            if (info.getComponentTypeID() == ApplicationConstants.MAIN_COMPONENT) {
                root.add((MutableTreeTableNode) componentHash.get(info));
            } else {
                mainComponent.add((MutableTreeTableNode) componentHash.get(info));
            }
        }

        return root;
    }

    /**
     * Returns a populated Object[][] containing the table data for the
     * web services table.
     */
    private Object[][] getWebServicesData() {
        if (model.getWebServiceTableData() == null) {
            controller.updateModelWebServices();
        }
        return model.getWebServiceTableData();
    }

    /**
     * Returns the selected component in the combo box.
     */
/*
  public ComponentInformation getSelectedComponent()
  {
    return (ComponentInformation)((HiddenObject)componentComboBox
            .getSelectedItem()).getObject();
  }
*/

    /**
     * Returns the path to the selected component in the tree.
     */
    public Object[] getSelectedComponentPath() {
        TreePath path = componentsTable.getTree().getSelectionPath();
        return path == null ? null : path.getPath();
    }

    public int getSelectedWebServiceIndex() {
        return webServicesTable.getSelectedRow();
    }

    public String getWebServiceName() {
        return webServiceNameTextField.getText();
    }

    public String getComponentClassName() {
        return componentClassTextField.getText();
    }

    public String getComponentMethodName() {
        return componentMethodTextField.getText();
    }

    public void clearWebService() {
        webServiceNameTextField.setText("");
    }

    public void clearComponent() {
        componentClassTextField.setText("");
        componentMethodTextField.setText("");
    }

    public String getName() {
        return "Components";
    }
}
