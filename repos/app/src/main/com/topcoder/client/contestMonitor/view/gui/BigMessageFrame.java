package com.topcoder.client.contestMonitor.view.gui;

import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextPane;

public class BigMessageFrame extends JFrame {

    private static final Logger log = Logger.getLogger(BigMessageFrame.class);
    private MonitorFrame frame;

    // Window elements
    private JButton saveButton;

    public BigMessageFrame(MonitorFrame frame, String message) {
        super("Results");
        this.frame = frame;

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                BigMessageFrame.this.dispose();
            }
        }
        );

        JTextPane pane = new JTextPane();
        pane.setText(message);
        JScrollPane scrollPane = new JScrollPane(pane);

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


        pack();

        // Set window location based on parent
        Point newLocation = frame.getCenteredLocation(getSize());
        setLocation((int) newLocation.getX(), (int) newLocation.getY());

        setVisible(true);
    }
}

