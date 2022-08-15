package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.ResponseCallback;
import com.topcoder.server.AdminListener.response.BlobColumnResponse;
import com.topcoder.shared.dataAccess.StringUtilities;
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

public class ObjectLoaderFrame extends JFrame implements ResponseCallback {

    private static final Logger log = Logger.getLogger(ObjectLoaderFrame.class);
    // If these types are changed, code changes also need to be made elsewhere in this file.
    private static final String types[] = {"ArrayList", "Boolean", "Character", "Double", "Integer", "Long", "String",
                                           "boolean[]", "char[]", "double[]", "int[]", "long[]", "String[]"};
    private static final String listTypes[] = {"Boolean", "Character", "Double", "Integer", "Long", "String",
                                               "boolean[]", "char[]", "double[]", "int[]", "long[]", "String[]"};
    private MonitorFrame frame;
    private CommandSender sender;

    // Window elements.  There are a lot of them...
    private JLabel tableLabel, columnLabel, objectTypeLabel, listTypeLabel, dataLabel, whereClauseLabel;
    private JLabel objectContentsLabel;
    private JComboBox tableBox, columnBox, objectTypeBox, listTypeBox;
    private JTextField dataField, whereClauseField;
    private JTextArea objectContentsArea;
    private JButton addToListButton, clearListButton, loadObjectButton, cancelButton;
    private JCheckBox uniqueCheckbox;
    private ArrayList loadedObject = new ArrayList();
    private Map tableColumns;

    public ObjectLoaderFrame(MonitorFrame frame, CommandSender sender) {
        super("Blob object load");
        this.frame = frame;
        this.sender = sender;
        setResizable(false);

        // Initialize window elements
        // Labels
        tableLabel = new JLabel("Table name");
        columnLabel = new JLabel("Column name");
        objectTypeLabel = new JLabel("Object type");
        listTypeLabel = new JLabel("List element type");
        dataLabel = new JLabel("Data entry:");
        whereClauseLabel = new JLabel("Where clause (use fully qualified column names):");
        objectContentsLabel = new JLabel("List object contents:");

        // Drop downs
        tableBox = new JComboBox();
        tableBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateColumnBox();
            }
        });

        columnBox = new JComboBox();
        objectTypeBox = new JComboBox(types);
        objectTypeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshListFields();
            }
        });

        listTypeBox = new JComboBox(listTypes);

        // Buttons
        addToListButton = new JButton("Add to list");
        addToListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addToListPressed();
            }
        });

        clearListButton = new JButton("Clear list");
        clearListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearListPressed();
            }
        });

        loadObjectButton = new JButton("Load object");
        loadObjectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadObjectPressed();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelPressed();
            }
        });

        // Others
        dataField = new JTextField();
        whereClauseField = new JTextField();
        objectContentsArea = new JTextArea(5, 40);
        objectContentsArea.setEditable(false);
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
        upperSelectors.add(objectTypeLabel);
        upperSelectors.add(objectTypeBox);
        upperSelectors.add(listTypeLabel);
        upperSelectors.add(listTypeBox);

        contentPane.add(upperSelectors, BorderLayout.NORTH);

        // Middle
        JPanel middle = new JPanel();
        middle.setLayout(new BorderLayout());
        // Upper middle
        JPanel upperMiddle = new JPanel();
        upperMiddle.setLayout(new BorderLayout());
        upperMiddle.add(dataLabel, BorderLayout.NORTH);
        upperMiddle.add(dataField, BorderLayout.CENTER);

        JPanel listButtons = new JPanel();
        listButtons.setLayout(new FlowLayout());
        listButtons.add(addToListButton);
        listButtons.add(clearListButton);

        upperMiddle.add(listButtons, BorderLayout.SOUTH);
        middle.add(upperMiddle, BorderLayout.NORTH);

        // Lower middle
        JPanel lowerMiddle = new JPanel();
        lowerMiddle.setLayout(new BorderLayout());
        lowerMiddle.add(objectContentsLabel, BorderLayout.NORTH);
        JScrollPane scroller = new JScrollPane(objectContentsArea);
        lowerMiddle.add(scroller, BorderLayout.CENTER);
        lowerMiddle.add(whereClauseLabel, BorderLayout.SOUTH);

        middle.add(lowerMiddle, BorderLayout.SOUTH);

        contentPane.add(middle, BorderLayout.CENTER);

        // Lower
        JPanel lower = new JPanel();
        lower.setLayout(new BorderLayout());
        lower.add(whereClauseField, BorderLayout.NORTH);
        lower.add(uniqueCheckbox, BorderLayout.CENTER);

        JPanel actionButtons = new JPanel();
        actionButtons.setLayout(new FlowLayout());
        actionButtons.add(loadObjectButton);
        actionButtons.add(cancelButton);

        lower.add(actionButtons, BorderLayout.SOUTH);

        contentPane.add(lower, BorderLayout.SOUTH);

        // Wrap up
        pack();

        // Set the fields to proper initial state
        refreshFields(null);
    }

    private boolean isListObject() {
        String selection = (String) objectTypeBox.getSelectedItem();
        return selection.equals("ArrayList");
    }

    private void updateListFieldVisibility(boolean visible) {
        listTypeLabel.setVisible(visible);
        listTypeBox.setVisible(visible);
        addToListButton.setVisible(visible);
        clearListButton.setVisible(visible);
        objectContentsLabel.setVisible(visible);
        objectContentsArea.setVisible(visible);
    }

    private void refreshListFields() {
        loadedObject = new ArrayList();
        refreshObjectContents();
        listTypeBox.setSelectedIndex(0);

        updateListFieldVisibility(isListObject());
    }

    private void refreshObjectContents() {
        objectContentsArea.setText(StringUtilities.makePretty(loadedObject));
    }

    private void refreshFields(BlobColumnResponse response) {
        dataField.setText("");
        whereClauseField.setText("");
        uniqueCheckbox.setSelected(true);
        objectTypeBox.setSelectedIndex(0);
        refreshListFields();

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
    private void addToListPressed() {
        try {
            Object o = getEntryObject((String) listTypeBox.getSelectedItem());
            loadedObject.add(o);
            refreshObjectContents();
        } catch (Exception e) {
            frame.displayMessage("Error parsing data entry:\n" + e.getMessage());
        }
    }

    private void clearListPressed() {
        loadedObject = new ArrayList();
        refreshObjectContents();
    }

    private void loadObjectPressed() {
        Object sendObject = null;
        if (isListObject()) {
            sendObject = loadedObject;
        } else {
            try {
                sendObject = getEntryObject((String) objectTypeBox.getSelectedItem());
            } catch (Exception e) {
                frame.displayMessage("Error parsing data entry:\n" + e);
                return;
            }
        }

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
        sender.sendObjectUpdateRequest(tableName, columnName, whereClause, sendObject, unique);
        setVisible(false);
    }

    private void cancelPressed() {
        setVisible(false);
    }

    private boolean getBoolean(String data) throws Exception {
        if (data.equals("true")) {
            return true;
        } else if (data.equals("false")) {
            return false;
        }
        throw new Exception("Booleans must be true or false");
    }

    private char getChar(String data) throws Exception {
        if (data.length() != 1) {
            throw new Exception("Character data must be exactly one character in length");
        }
        return data.charAt(0);
    }

    private int getInt(String data) throws Exception {
        try {
            return Integer.parseInt(data);
        } catch (Exception e) {
            char quote = '"';
            throw new Exception("The string " + quote + data + quote + " is not a valid integer");
        }
    }

    private long getLong(String data) throws Exception {
        try {
            return Long.parseLong(data);
        } catch (Exception e) {
            char quote = '"';
            throw new Exception("The string " + quote + data + quote + " is not a valid long");
        }
    }

    private double getDouble(String data) throws Exception {
        try {
            return Double.parseDouble(data);
        } catch (Exception e) {
            char quote = '"';
            throw new Exception("The string " + quote + data + quote + " is not a valid double");
        }
    }

    private Object getEntryObject(String objectType) throws Exception {
        String data = dataField.getText().trim();
        if (objectType.equals("Boolean")) {
            return new Boolean(getBoolean(data));
        } else if (objectType.equals("Character")) {
            return new Character(getChar(data));
        } else if (objectType.equals("Double")) {
            return new Double(getDouble(data));
        } else if (objectType.equals("Integer")) {
            return new Integer(getInt(data));
        } else if (objectType.equals("Long")) {
            return new Long(getLong(data));
        } else if (objectType.equals("String")) {
            return data;
        }

        // We have an array type
        int i, numElements = 0;
        String tokens[] = null;
        if (data.length() > 0) {
            String delimiter = data.substring(0, 1);
            data = data.substring(1);
            // The "delimiter" argument is actually a regular expression in the
            // split() function.  All characters are treated normally except for
            // the backslash character, which is "\\".
            char backslash = '\\';
            if (delimiter.charAt(0) == backslash) {
                delimiter = delimiter + backslash;
            }
            tokens = data.split(delimiter, -1);
            numElements = tokens.length;
        }

        if (objectType.equals("boolean[]")) {
            boolean ab[] = new boolean[numElements];
            for (i = 0; i < numElements; i++) {
                ab[i] = getBoolean(tokens[i]);
            }
            return ab;
        } else if (objectType.equals("char[]")) {
            char ac[] = new char[numElements];
            for (i = 0; i < numElements; i++) {
                ac[i] = getChar(tokens[i]);
            }
            return ac;
        } else if (objectType.equals("double[]")) {
            double ad[] = new double[numElements];
            for (i = 0; i < numElements; i++) {
                ad[i] = getDouble(tokens[i]);
            }
            return ad;
        } else if (objectType.equals("int[]")) {
            int ai[] = new int[numElements];
            for (i = 0; i < numElements; i++) {
                ai[i] = getInt(tokens[i]);
            }
            return ai;
        } else if (objectType.equals("long[]")) {
            long al[] = new long[numElements];
            for (i = 0; i < numElements; i++) {
                al[i] = getLong(tokens[i]);
            }
            return al;
        } else if (objectType.equals("String[]")) {
            if (tokens == null) {
                return new String[0];
            }
            return tokens;
        }

        throw new Exception("Unknown object type " + objectType);
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
