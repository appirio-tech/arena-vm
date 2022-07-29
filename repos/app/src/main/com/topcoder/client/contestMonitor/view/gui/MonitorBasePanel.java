package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.shared.util.StoppableThread;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.Component;

abstract class MonitorBasePanel implements StoppableThread.Client {

    private final Component scrollPane;
    private final JTextPane textPane;
    private final StoppableThread thread;
    private final CommandSender sender;

    MonitorBasePanel(CommandSender sender) {
        this.sender = sender;
        textPane = new JTextPane();
        scrollPane = new JScrollPane(textPane);
        thread = new StoppableThread(this, "MonitorBasePanel");
    }

    final CommandSender getSender() {
        return sender;
    }

    final void start() {
        thread.start();
    }

    final void stop() {
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    final void insertString(String message) {
        Document doc = textPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), message, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    final Component getScrollPane() {
        return scrollPane;
    }

}
