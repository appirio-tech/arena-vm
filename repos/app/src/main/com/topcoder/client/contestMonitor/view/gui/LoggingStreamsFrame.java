/*
 * User: Mike Cervantes (emcee)
 * Date: May 16, 2002
 * Time: 10:57:26 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.LoggingStreamsTableModel;
import com.topcoder.server.util.logging.net.StreamID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggingStreamsFrame {

    protected JFrame myFrame;
    protected LoggingStreamsTableModel model;
    private JTable table;
    private LoggingFrameManager loggingFrameManager;

    public LoggingStreamsFrame(LoggingFrameManager loggingFrameManager) {
        myFrame = new JFrame("Logging Streams");
        myFrame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    myFrame.hide();
            }
        });
        this.loggingFrameManager = loggingFrameManager;
        this.model = loggingFrameManager.getLoggingStreamsModel();
        build();
    }


    public void display() {
        myFrame.setLocationRelativeTo(myFrame.getParent());
        myFrame.setVisible(true);
    }

    protected int getPreferredTableWidth() {
        return 640;
    }

    protected int getPreferredTableHeight() {
        return 200;
    }

    protected void build() {
        JPanel panel = new JPanel(new GridBagLayout());
        myFrame.setContentPane(panel);
        buildTable();
        table.setPreferredScrollableViewportSize(new Dimension(getPreferredTableWidth(), getPreferredTableHeight()));
        JScrollPane pane = new JScrollPane(table);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = gbc.weighty = 1;
        insert(pane, myFrame.getContentPane(), gbc, 0, 0, 1, 1);

        JButton refresh = new JButton("Refresh");
        refresh.setMnemonic('R');
        refresh.setPreferredSize(new Dimension(0, 20));
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loggingFrameManager.refreshStreams();
            }
        });
        gbc.weighty = 0;
        insert(refresh, panel, gbc, 0, 1, 1, 1);
        pane.requestFocus();
        myFrame.pack();
    }


    protected void setColumnWidths() {
        setColumnWidth(0, 240);
        setColumnWidth(1, 120);
        setColumnWidth(2, 120);
        setColumnWidth(3, 180);
    }

    protected final void setColumnWidth(int col, int width) {
        table.getColumnModel().getColumn(col).setPreferredWidth(width);
    }

    private void buildTable() {
        table = new JTable(model);
        table.getColumnModel().setColumnSelectionAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        setColumnWidths();
        final SimpleDateFormat format = new SimpleDateFormat("EEE MMM d h:mm a yyyy");
        table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
            protected void setValue(Object value) {
                if (value instanceof Date) {
                    Date d = (Date) value;
                    if (d.getTime() == 0)
                        value = "";
                    else
                        value = format.format(d);
                }
                super.setValue(value);
            }
        });
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int viewColumn = table.getColumnModel().getColumnIndexAtX(e.getX());
                    int column = table.convertColumnIndexToModel(viewColumn);
                    if (column != -1) {
                        boolean shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) != 0;
                        model.sort(column, shiftPressed);
                    }
                }
            }
        });
        table.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    openStreamEvent();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openStreamEvent();
                }
            }
        });
    }

    private void openStreamEvent() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            StreamID id = (StreamID) model.getStream(row);
            loggingFrameManager.displayLoggingFrame(id);
        }
    }

    private void insert(Component comp, Container container, GridBagConstraints gbc, int x, int y, int xspan, int yspan) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = xspan;
        gbc.gridheight = yspan;
        container.add(comp, gbc);
    }
}
