package com.topcoder.client.testerApplet;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.topcoder.client.connectiontype.ConnectionType;

public class MainFrame extends JFrame {
    private String host;
    private int port;
    private String tunnel;
    private JTextPane log;
    private JTextField threads;
    private JTextField size;
    private JButton start;
    private JCheckBox useSSL;
    private JTextField batch;

    public MainFrame(String host, int port, String tunnel) {
        super("TopCoder Arena Connection Tester");
        this.host = host;
        this.port = port;
        this.tunnel = tunnel;

        GridBagConstraints gbc = new GridBagConstraints();
        getContentPane().setLayout(new GridBagLayout());

        log = new JTextPane();
        log.setEditable(false);
        JScrollPane pane = new JScrollPane(log);
        pane.setPreferredSize(new Dimension(400, 200));
        pane.setBorder(BorderFactory.createEtchedBorder());
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        getContentPane().add(pane, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        getContentPane().add(new JPanel(), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        getContentPane().add(new JLabel("Packet Size:"), gbc);
        gbc.gridx = 2;
        size = new JTextField("1024", 4);
        getContentPane().add(size, gbc);
        gbc.gridx = 3;
        getContentPane().add(new JLabel("Threads:"), gbc);
        gbc.gridx = 4;
        threads = new JTextField("10", 3);
        getContentPane().add(threads, gbc);
        gbc.gridx = 5;
        getContentPane().add(new JLabel("Batches:"), gbc);
        gbc.gridx = 6;
        batch = new JTextField("1", 3);
        getContentPane().add(batch, gbc);
        gbc.gridx = 7;
        useSSL = new JCheckBox("Use SSL", true);
        getContentPane().add(useSSL, gbc);
        gbc.gridx = 8;
        start = new JButton("Start");
        getContentPane().add(start, gbc);
        start.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    startTest();
                }
            });
    }

    public void startTest() {
        start.setEnabled(false);
        int tmp = 10;
        try {
            tmp = Integer.parseInt(threads.getText());
        } catch (NumberFormatException e) {
        }
        final int threadNum = tmp;

        tmp = 1024;
        try {
            tmp = Integer.parseInt(size.getText());
        } catch (NumberFormatException e) {
        }
        final int packetSize = tmp;
        final boolean ssl = useSSL.isSelected();
        final int batches = Integer.parseInt(batch.getText());
        new Thread(new Runnable() {
            public void run() {
                new TestProcess(host, port, tunnel, ssl, threadNum, packetSize, batches, 1, ConnectionType.getAvailableTypes()) {
                    protected void bareAppendLog(String text) {
                        appendLog(text);
                    }
                }.runTest();
            }
        }).start();
}

    private void appendLog(String text) {
        log.setText(log.getText() + text + "\n");
        repaint();
    }

}
