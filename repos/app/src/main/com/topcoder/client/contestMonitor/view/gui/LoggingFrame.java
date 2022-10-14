/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 3, 2002
 * Time: 4:59:30 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.shared.util.StoppableThread;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.server.util.logging.net.TCLoggingEvent;
import com.topcoder.server.util.logging.net.StreamID;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
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

public class LoggingFrame {

    private static final Logger logger = Logger.getLogger(LoggingFrame.class);

    private StreamID id;
    private JFrame myFrame;
    private JTextPane pane;
    private Document doc;

    private LoggingFrameManager loggingFrameManager;

    private StoppableThread thread;

    private Layout layout = new PatternLayout("%d [%t] %c - %m%n");

    private boolean autoScroll = true;
    private TCLinkedQueue queue = new TCLinkedQueue();
    private LoggingFramePreferences pref;

    public LoggingFrame(LoggingFrameManager loggingFrameManager, StreamID id) {
        this.id = id;
        this.loggingFrameManager = loggingFrameManager;

        myFrame = new JFrame(id.toString());
        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        pref = new LoggingFramePreferences(myFrame);

        buildMenu();
        pane = new JTextPane();
        pane.setBackground(Color.white);
        doc = pane.getDocument();
        pane.setPreferredSize(new Dimension(600, 400));
        JScrollPane scroller = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        myFrame.getContentPane().add(scroller);
        myFrame.pack();

        thread = new StoppableThread(new LoggingRunner(), "LoggingFrame.runner - " + id);
        thread.start();
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

        final JMenuItem closeAllButton = new JMenuItem("Close All");
        closeAllButton.setMnemonic('a');
        closeAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
                loggingFrameManager.closeAllLoggingFrames();
            }
        });
        file.add(closeAllButton);


        JMenu options = new JMenu("Options");
        options.setMnemonic('o');
        final JMenuItem customizeButton = new JMenuItem("Customize");
        customizeButton.setMnemonic('c');
        customizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pref.showFrame();
            }
        });
        options.add(customizeButton);
        final JCheckBoxMenuItem autoScrollButton = new JCheckBoxMenuItem("Auto-scroll", true);
        autoScrollButton.setMnemonic('r');
        autoScrollButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autoScroll = autoScrollButton.isSelected();
            }
        });
        options.add(autoScrollButton);


        JMenuBar bar = new JMenuBar();
        bar.add(file);
        bar.add(options);
        myFrame.setJMenuBar(bar);
    }

    private void saveEvent() {
        String text = getText();
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

    public void log(TCLoggingEvent event) {
        queue.put(event);
    }

    public void dispose() {
        loggingFrameManager.closeLoggingFrame(id);
        myFrame.dispose();
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    public void show() {
        myFrame.setLocationRelativeTo(myFrame.getParent());
        myFrame.setVisible(true);
    }

    private class LoggingRunner implements StoppableThread.Client {

        //LoggingEvent event;
        TCLoggingEvent event;

        public void cycle() throws InterruptedException {
            /*
            event = (LoggingEvent) queue.take();
            String text = layout.format(event);
            String[] s = event.getThrowableStrRep();
            if (s != null && layout.ignoresThrowable()) {
                int len = s.length;
                for (int i = 0; i < len; i++) {
                    text += s[i] + Layout.LINE_SEP;
                }
            } else if (!text.endsWith(Layout.LINE_SEP))
                text += Layout.LINE_SEP;
            try {
                insertText(text, event.getLevel());
            } catch (Exception e) {
                logger.error(e);
            }
             */
            event = (TCLoggingEvent)queue.take();
            
            
            try {
                insertText(event);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }
    
    private synchronized void insertText(TCLoggingEvent event) throws BadLocationException {
        String text = event.getMessage();
        if (!text.endsWith(Layout.LINE_SEP)) {
                text += Layout.LINE_SEP;
        }
        
        int length = doc.getLength();
        doc.insertString(length, event.getMessage(), pref.getAttributes(event.getLevel()));
        
        if (autoScroll) {
            pane.setCaretPosition(length);
        }
        
        if(length > 30000) {
            doc.remove(0,length-15000);
        }
    }

    private synchronized String getText() {
        return pane.getText();
    }

}
