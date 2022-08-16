package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.ResponseCallback;
import com.topcoder.server.AdminListener.response.BlobColumnResponse;
import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ObjectSearchFrame extends JFrame implements ResponseCallback {

    private static final Logger log = Logger.getLogger(ObjectSearchFrame.class);
    private MonitorFrame frame;
    private CommandSender sender;

    // Window elements.
    private JLabel tableLabel, columnLabel, searchTextLabel, whereClauseLabel;
    private JComboBox tableBox, columnBox;
    private JTextField searchTextField, whereClauseField;
    private JButton searchButton, cancelButton;
    private Map tableColumns;

    public ObjectSearchFrame(MonitorFrame frame, CommandSender sender) {
        super("Blob object search");
        this.frame = frame;
        this.sender = sender;
        setResizable(false);

        // Initialize window elements
        // Labels
        tableLabel = new JLabel("Table name");
        columnLabel = new JLabel("Column name");
        searchTextLabel = new JLabel("Search text");
        whereClauseLabel = new JLabel("Where clause (use fully qualified column names):");

        // Drop downs
        tableBox = new JComboBox();
        tableBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateColumnBox();
            }
        });

        columnBox = new JComboBox();

        // Buttons
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchPressed();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelPressed();
            }
        });

        // Others
        searchTextField = new JTextField(15);
        whereClauseField = new JTextField();

        // Lay out the window:
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Upper fields
        JPanel upperSelectors = new JPanel();
        upperSelectors.setLayout(new GridLayout(3, 2));
        upperSelectors.add(tableLabel);
        upperSelectors.add(tableBox);
        upperSelectors.add(columnLabel);
        upperSelectors.add(columnBox);
        upperSelectors.add(searchTextLabel);
        upperSelectors.add(searchTextField);

        contentPane.add(upperSelectors, BorderLayout.NORTH);

        // Some more miscellaneous fields
        JPanel middleFields = new JPanel();
        middleFields.setLayout(new BorderLayout());
        middleFields.add(whereClauseLabel, BorderLayout.NORTH);
        middleFields.add(whereClauseField, BorderLayout.SOUTH);
        contentPane.add(middleFields, BorderLayout.CENTER);

        // Search/cancel buttons
        JPanel actionButtons = new JPanel();
        actionButtons.setLayout(new FlowLayout());
        actionButtons.add(searchButton);
        actionButtons.add(cancelButton);

        contentPane.add(actionButtons, BorderLayout.SOUTH);

        // Wrap up
        pack();

        // Set the fields to proper initial state
        refreshFields(null);
    }

    private void refreshFields(BlobColumnResponse response) {
        searchTextField.setText("");
        whereClauseField.setText("");

        if (response == null) {
            return;
        }

        // Check to see if we have data for the object and list type boxes
        boolean succeeded = response.getSucceeded();
        if (!succeeded) {
            log.error("Blob column retrieval failed");
            frame.displayMessage("Unable to retrieve blob column information from database!");
            return;
        }

        tableColumns = response.getTableColumns();
        if (tableColumns.size() == 0) {
            log.error("Ugh!  No blob column information retrieved.");
            frame.displayMessage("Unable to find blob column information in database!");
            return;
        }

        // Refresh the table and column boxes
        tableBox.removeAllItems();
        Iterator it = tableColumns.keySet().iterator();
        while (it.hasNext()) {
            String table = (String) it.next();
            tableBox.addItem(table);
        }
        tableBox.setSelectedIndex(0);

        updateColumnBox();
    }

    private void updateColumnBox() {
        columnBox.removeAllItems();
        String tableSelected = (String) tableBox.getSelectedItem();
        ArrayList columns = (ArrayList) tableColumns.get(tableSelected);
        Iterator it = columns.iterator();
        while (it.hasNext()) {
            String column = (String) it.next();
            columnBox.addItem(column);
        }
        columnBox.setSelectedIndex(0);
    }

    // Button press actions
    private void searchPressed() {
        String tableName = (String) tableBox.getSelectedItem();
        String columnName = (String) columnBox.getSelectedItem();
        String searchText = searchTextField.getText();
        String whereClause = whereClauseField.getText().trim();
        if (searchText.equals("") || whereClause.equals("")) {
            String message;
            if (searchText.equals("")) {
                message = "You have not entered any search text.";
            } else {
                message = "You have not entered a where clause.";
            }
            if (!frame.confirmDialog(message + "This means that it is\n" +
                    "possible that EVERY row in the table could be retrieved.\n" +
                    "Of course, this might take a while, and will hog the database and\n" +
                    "the admin services EJB.\n\nAre you sure you want to do this?")) {
                return;
            }
        }
        sender.sendObjectSearchRequest(tableName, columnName, searchText, whereClause);
        setVisible(false);
    }

    private void cancelPressed() {
        setVisible(false);
    }

    public void receivedResponse(Object response) {
        if (!(response instanceof BlobColumnResponse)) {
            log.error("Received wrong response type!");
            return;
        }

        BlobColumnResponse blobResponse = (BlobColumnResponse) response;
        refreshFields(blobResponse);

        // Set window location based on parent
        Point newLocation = frame.getCenteredLocation(getSize());
        setLocation((int) newLocation.getX(), (int) newLocation.getY());

        // Show the window
        setVisible(true);
    }
}

