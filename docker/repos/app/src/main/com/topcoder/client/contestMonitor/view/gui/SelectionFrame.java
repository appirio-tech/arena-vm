/*
 * User: Mike Cervantes (emcee)
 * Date: May 16, 2002
 * Time: 10:57:26 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.SortedTableModel;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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

public abstract class SelectionFrame {

    protected JDialog frame;
    protected SortedTableModel tableModel;
    private JTable table;
    private JButton addButton;
    private JButton modifyButton;
    private JButton deleteButton;
    private FrameWaiter waiter;

    public SelectionFrame(String name, SortedTableModel tableModel, JDialog parent) {
        frame = new JDialog(parent, name, false);
        waiter = new FrameWaiter(frame);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    frame.hide();
            }
        });
        this.tableModel = tableModel;
    }

    public SelectionFrame(String name, SortedTableModel tableModel, JFrame parent) {
        frame = new JDialog(parent, name, false);
        waiter = new FrameWaiter(frame);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    frame.hide();
            }
        });
        this.tableModel = tableModel;
    }


    private Runnable displayRunnable = new Runnable() {
        public void run() {
            frame.setLocationRelativeTo(frame.getParent());
            frame.setVisible(true);
        }
    };

    public void display() {
        SwingUtilities.invokeLater(displayRunnable);
    }

    protected abstract int getPreferredTableWidth();

    protected abstract int getPreferredTableHeight();

    protected void build() {
        JPanel panel = new JPanel(new GridBagLayout());
        frame.setContentPane(panel);
        buildTable();
        table.setPreferredScrollableViewportSize(new Dimension(getPreferredTableWidth(), getPreferredTableHeight()));
        JScrollPane pane = new JScrollPane(table);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = gbc.weighty = 1;
        insert(pane, frame.getContentPane(), gbc, 0, 0, 3, 1);

        buildAddButton();
        buildModifyButton(table);
        buildDeleteButton(table);
        addButton.setMnemonic('A');
        modifyButton.setMnemonic('M');
        deleteButton.setMnemonic('D');

        gbc.weighty = 0;
        gbc.weightx = .1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        insert(addButton, frame.getContentPane(), gbc, 0, 1, 1, 1);
        insert(modifyButton, frame.getContentPane(), gbc, 1, 1, 1, 1);
        insert(deleteButton, frame.getContentPane(), gbc, 2, 1, 1, 1);

        frame.pack();
    }

    protected abstract void deleteEvent(int row);

    private void buildDeleteButton(final JTable table) {
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    deleteEvent(row);
                }
            }
        });
    }

    protected abstract void modifyEvent(int row);

    private void buildModifyButton(final JTable table) {
        modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    modifyEvent(row);
                }
            }
        });
    }


    protected abstract void addEvent();

    private void buildAddButton() {
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEvent();
            }
        });
    }

    protected abstract void setColumnWidths();

    protected final void setColumnWidth(int col, int width) {
        table.getColumnModel().getColumn(col).setPreferredWidth(width);
    }

    private void buildTable() {
        table = new JTable(tableModel);
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
                        tableModel.sort(column, shiftPressed);
                    }
                }
            }
        });
        table.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    modifyButton.doClick();
                }
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteButton.doClick();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modifyButton.doClick();
                }
            }
        });
    }

    protected FrameWaiter getWaiter() {
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
