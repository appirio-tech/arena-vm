/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 3, 2002
 * Time: 4:59:30 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import org.apache.log4j.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VerifyRoundFrame {

    private static final Logger logger = Logger.getLogger(VerifyRoundFrame.class);

    private JFrame myFrame;
    private JTextPane pane;

    public VerifyRoundFrame() {
        myFrame = new JFrame("Verify Round");
        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        buildMenu();
        pane = new JTextPane();
        pane.setBackground(Color.white);
        pane.setPreferredSize(new Dimension(600, 400));
        JScrollPane scroller = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        myFrame.getContentPane().add(scroller);
        myFrame.pack();
    }

    private void buildMenu() {
        JMenu file = new JMenu("File");
        file.setMnemonic('f');
        final JMenuItem saveButton = new JMenuItem("Save");
        saveButton.setMnemonic('s');
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                saveEvent();
            }
        });
        file.add(saveButton);

        final JMenuItem closeButton = new JMenuItem("Close");
        closeButton.setMnemonic('c');
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
        file.add(closeButton);


        JMenuBar bar = new JMenuBar();
        bar.add(file);
        myFrame.setJMenuBar(bar);
    }

    private void saveEvent() {
        String text = pane.getText();
        JFileChooser dialog = new JFileChooser();
        int response = dialog.showSaveDialog(myFrame);
        if (response == JFileChooser.APPROVE_OPTION) {
            File logFile = dialog.getSelectedFile();
            if (!logFile.exists() || logFile.canWrite()) {
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(
                            new FileWriter(logFile)
                    );
                    writer.write(text, 0, text.length());
                } catch (IOException e) {
                    logger.error(e);
                } finally {
                    try {
                        if (writer != null)
                            writer.close();
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            }
        }
    }

    public void dispose() {
        myFrame.dispose();
    }

    public void display(String text) {
        pane.setText("");
        pane.setText(text);
        pane.repaint();
        myFrame.pack();
        myFrame.setLocationRelativeTo(myFrame.getParent());
        myFrame.setVisible(true);
    }
}
