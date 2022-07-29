package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.ResponseCallback;
import com.topcoder.server.AdminListener.response.TextColumnResponse;
import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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

public class TextLoaderFrame extends JFrame implements ResponseCallback {

    private static final Logger log = Logger.getLogger(TextLoaderFrame.class);
    private MonitorFrame frame;
    private CommandSender sender;

    // Window elements.  There are a lot of them...
    private JLabel tableLabel, columnLabel, whereClauseLabel;
    private JLabel textContentsLabel;
    private JComboBox tableBox, columnBox;
    private JTextField whereClauseField;
    private JTextArea textContentsArea;
    private JButton loadTextButton, cancelButton;
    private JCheckBox uniqueCheckbox;
    private Map tableColumns;

    public TextLoaderFrame(MonitorFrame frame, CommandSender sender) {
        super("Text object load");
        this.frame = frame;
        this.sender = sender;
        setResizable(false);

        // Initialize window elements
        // Labels
        tableLabel = new JLabel("Table name");
        columnLabel = new JLabel("Column name");
        whereClauseLabel = new JLabel("Where clause (use fully qualified column names):");
        textContentsLabel = new JLabel("Text:");

        // Drop downs
        tableBox = new JComboBox();
        tableBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateColumnBox();
            }
        });

        columnBox = new JComboBox();

        loadTextButton = new JButton("Load text");
        loadTextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTextPressed();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelPressed();
            }
        });

        // Others
        whereClauseField = new JTextField();
        textContentsArea = new JTextArea(5, 40);
        uniqueCheckbox = new JCheckBox("Update one row only", true);

        // Lay out the window:
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Upper drop downs
        JPanel upperSelectors = new JPanel();
        upperSelectors.setLayout(new GridLayout(4, 2));
        upperSelectors.add(tableLabel);
        upperSelectors.add(tableBox);
        upperSelectors.add(columnLabel);
        upperSelectors.add(columnBox);

        contentPane.add(upperSelectors, BorderLayout.NORTH);

        // Middle
        JPanel middle = new JPanel();
        middle.setLayout(new BorderLayout());
        middle.add(textContentsLabel, BorderLayout.NORTH);
        JScrollPane scroller = new JScrollPane(textContentsArea);
        middle.add(scroller, BorderLayout.CENTER);
        middle.add(whereClauseLabel, BorderLayout.SOUTH);

        contentPane.add(middle, BorderLayout.CENTER);

        // Lower
        JPanel lower = new JPanel();
        lower.setLayout(new BorderLayout());
        lower.add(whereClauseField, BorderLayout.NORTH);
        lower.add(uniqueCheckbox, BorderLayout.CENTER);

        JPanel actionButtons = new JPanel();
        actionButtons.setLayout(new FlowLayout());
        actionButtons.add(loadTextButton);
        actionButtons.add(cancelButton);

        lower.add(actionButtons, BorderLayout.SOUTH);

        contentPane.add(lower, BorderLayout.SOUTH);

        // Wrap up
        pack();

        // Set the fields to proper initial state
        refreshFields(null);
    }


    private void refreshFields(TextColumnResponse response) {
        whereClauseField.setText("");
        uniqueCheckbox.setSelected(true);

        if (response == null) {
            return;
        }

        // Check to see if we have data for the object and list type boxes
        boolean succeeded = response.getSucceeded();
        if (!succeeded) {
            log.error("Text column retrieval failed");
            frame.displayMessage("Unable to retrieve text column information from database!");
            return;
        }

        tableColumns = response.getTableColumns();
        if (tableColumns.size() == 0) {
            log.error("Ugh!  No text column information retrieved.");
            frame.displayMessage("Unable to find text column information in database!");
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


    private void loadTextPressed() {
        Object sendObject = null;
        sendObject = textContentsArea.getText();
        // Send off the object
        String tableName = (String) tableBox.getSelectedItem();
        String columnName = (String) columnBox.getSelectedItem();
        String whereClause = whereClauseField.getText();
        boolean unique = uniqueCheckbox.isSelected();
        if (!unique) {
            if (!frame.confirmDialog("You have cleared the 'update one row only' field.  This means that,\n" +
                    "depending on your where clause, multiple rows in the database could\n" +
                    "be changed, possibly EVERY row in the table.\n\n" +
                    "Do you want to do this?")) {
                return;
            }
        }
        sender.sendTextUpdateRequest(tableName, columnName, whereClause, sendObject, unique);
        setVisible(false);
    }

    private void cancelPressed() {
        setVisible(false);
    }


    public void receivedResponse(Object response) {
        if (!(response instanceof TextColumnResponse)) {
            log.error("Received wrong response type!");
            return;
        }

        TextColumnResponse textResponse = (TextColumnResponse) response;
        refreshFields(textResponse);

        // Set window location based on parent
        Point newLocation = frame.getCenteredLocation(getSize());
        setLocation((int) newLocation.getX(), (int) newLocation.getY());

        // Show the window
        setVisible(true);
    }
}
