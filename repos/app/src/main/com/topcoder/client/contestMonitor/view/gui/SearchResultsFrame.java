package com.topcoder.client.contestMonitor.view.gui;

import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.dataAccess.resultSet.ResultSetTableModel;

public class SearchResultsFrame extends JFrame {

    private static final Logger log = Logger.getLogger(SearchResultsFrame.class);
    private MonitorFrame frame;
    private ResultSetContainer results;

    // Window elements
    private JButton saveButton;

    public SearchResultsFrame(MonitorFrame frame, ResultSetContainer results) {
        super("Search results");
        this.frame = frame;
        this.results = results;

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                SearchResultsFrame.this.dispose();
            }
        }
        );

        saveButton = new JButton("Save results to file");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savePressed();
            }
        });
        saveButton.setMnemonic('S');

        TableModel model = new ResultSetTableModel(results);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Lay out elements
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add(scrollPane, gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        contentPane.add(saveButton, gbc);


        pack();

        // Set window location based on parent
        Point newLocation = frame.getCenteredLocation(getSize());
        setLocation((int) newLocation.getX(), (int) newLocation.getY());

        setVisible(true);
    }

    // Button press actions
    private void savePressed() {
        try {
            FileDialog dialog = new FileDialog(this, "Save to file", FileDialog.SAVE);
            dialog.show();
            String directory = dialog.getDirectory();
            if (directory == null) {
                // User canceled
                return;
            }

            // Append the separator if necessary
            if (directory.length() > 0 && directory.lastIndexOf(File.separator) != directory.length() - 1) {
                directory = directory + File.separator;
            }
            String fullPath = directory + dialog.getFile();
            File saveFile = new File(fullPath);
            Writer outputWriter = new BufferedWriter(new FileWriter(saveFile));

            // Write out header information
            StringBuffer columns = new StringBuffer();
            for (int col = 0; col < results.getColumnCount(); col++) {
                if (col > 0) {
                    columns.append(",");
                }
                columns.append(results.getColumnName(col));
            }
            outputWriter.write(columns.toString() + "\n");
            for (int i = 0; i < columns.length(); i++) {
                outputWriter.write("-");
            }
            outputWriter.write("\n");

            // Write out data
            for (int row = 0; row < results.getRowCount(); row++) {
                for (int col = 0; col < results.getColumnCount(); col++) {
                    if (col > 0) {
                        outputWriter.write(",");
                    }
                    String appendString = results.getItem(row, col).toString();
                    if (appendString.indexOf(",") >= 0) {
                        char quote = '"';
                        outputWriter.write(quote + appendString + quote);
                    } else {
                        outputWriter.write(appendString);
                    }
                }
                outputWriter.write("\n");
            }
            outputWriter.close();
            frame.displayMessage("Search results successfully saved to " + fullPath);
        } catch (Exception e) {
            log.error("Error saving file", e);
            if (e instanceof FileNotFoundException) {
                frame.displayMessage("Error saving file; it appears to be in use by another program.");
            } else {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                frame.displayMessage("Error saving file:\n" + sw);
            }
        }
    }
}

