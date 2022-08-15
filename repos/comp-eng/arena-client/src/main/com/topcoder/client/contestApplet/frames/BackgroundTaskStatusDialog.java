/*
 * BackgroundTaskStatusDialog
 * 
 * Created 04/18/2007
 */
package com.topcoder.client.contestApplet.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.topcoder.client.contestApplet.common.Common;

/**
 * Modal Dialog to show while a background task is being carried out<p>
 * 
 * It allows cancellation and status update.
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: BackgroundTaskStatusDialog.java 60987 2007-05-14 20:54:48Z thefaxman $
 */
public class BackgroundTaskStatusDialog extends JDialog {
	private JPanel panel;
    private JLabel msg;
    private JButton cancelButton;

    public BackgroundTaskStatusDialog(Frame frame, String title, String message) {
        super(frame, title);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        getRootPane().setBackground(Common.BG_COLOR);
        getContentPane().setBackground(Common.BG_COLOR);
        panel = new JPanel(new BorderLayout(10,10));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300,80));
        getContentPane().add(panel);
        setResizable(false);
        create(message);
        setModal(true);
        pack();
        setLocationRelativeTo(frame);
    }

    public void updateMessage(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                msg.setText(text);
                pack();
                repaint();
            }
        
        });
    }        

    private void create(String message) {
        msg = new JLabel(message, SwingConstants.CENTER);
        msg.setForeground(Common.FG_COLOR);
        msg.setFont(new Font(null, Font.PLAIN, 12));
        panel.add(msg, BorderLayout.CENTER);
        
        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cancelPanel.setOpaque(false);
        cancelButton = Common.getButton("Cancel");
        cancelPanel.add(cancelButton);
        panel.add(cancelPanel, BorderLayout.SOUTH);
    }

    public void addCancelActionListener(ActionListener listener) {
            cancelButton.addActionListener(listener);
    }
    
    public void removeCancelActionListener(ActionListener listener) {
        cancelButton.removeActionListener(listener);
    }
}
