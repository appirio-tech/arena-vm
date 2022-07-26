/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:38:43 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public abstract class InputFrame {

    protected JDialog frame;
    private GridBagConstraints gbc = new GridBagConstraints();
    private int itemRow = 0;
    private JPanel itemPanel;
    private JPanel buttonPanel;
    private int buttonIndex = 0;
    private FrameWaiter waiter;


    public InputFrame(String name, JDialog parent) {
        frame = new JDialog(parent, name, false);
        waiter = new FrameWaiter(frame);
    }

    protected void addItem(String name, Component comp) {
        addItem(name, comp, false);
    }

    protected void addItem(String name, Component comp, boolean fill) {
        addItem(name, comp, .5, 0, fill ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE);
    }


    protected void addItem(String name, Component comp, double weightx, double weighty, int fill) {
        if (name != null) {
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.insets = new Insets(3, 2, 3, 3);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            insert(new JLabel(name + ':', JLabel.RIGHT), itemPanel, gbc, 0, itemRow, 1, 1);
        }
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = new Insets(4, 3, 4, 2);
        gbc.fill = fill;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        insert(comp, itemPanel, gbc, 1, itemRow, 1, 1);

        itemRow++;
    }

    protected void addButton(AbstractButton button) {
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 2, 0, 2);
        gbc.weightx = 0;
        gbc.weighty = 0;
        insert(button, buttonPanel, gbc, buttonIndex, 0, 1, 1);
        buttonIndex++;
    }

    protected abstract void addButtons();

    protected abstract void addItems();

    protected final void build() {
        frame.setContentPane(new JPanel(new GridBagLayout()));
        itemPanel = new JPanel(new GridBagLayout());
        buttonPanel = new JPanel(new GridBagLayout());
        buttonIndex = itemRow = 0;
        addItems();
        addButtons();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = .5;
        gbc.weighty = .1;
        gbc.insets = new Insets(10, 10, 10, 10);
        insert(itemPanel, frame.getContentPane(), gbc, 0, 0, 1, 1);
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = .5;
        insert(buttonPanel, frame.getContentPane(), gbc, 0, 1, 1, 1);
        frame.pack();
    }


    private Runnable displayRunnable = new Runnable() {
        public void run() {
            itemPanel.revalidate();
            itemPanel.repaint();
            frame.setLocationRelativeTo(frame.getParent());
            frame.setVisible(true);
        }
    };

    public void display() {
        SwingUtilities.invokeLater(displayRunnable);
    }

    public void dispose() {
        frame.dispose();
    }

    protected FrameWaiter getFrameWaiter() {
        return waiter;
    }

    private void insert(Component comp, Container container, GridBagConstraints gbc, int x, int y, int xspan, int yspan) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = xspan;
        gbc.gridheight = yspan;
        container.add(comp, gbc);
    }


}

