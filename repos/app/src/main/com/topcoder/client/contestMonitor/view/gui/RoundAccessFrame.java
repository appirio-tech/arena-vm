/*
 * User: Mike Cervantes (emcee)
 * Date: May 16, 2002
 * Time: 10:57:26 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.model.RoundAccessTableModel;
import com.topcoder.server.AdminListener.response.RoundAccessItem;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;

import javax.swing.JButton;
import javax.swing.JDialog;
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

public class RoundAccessFrame implements ResponseCallback {

    private final CommandSender sender;
    private final RoundAccessTableModel model = new RoundAccessTableModel();
    private final JDialog frame;
    private JTable table;
    private JButton selectButton;

    public RoundAccessFrame(CommandSender sender, MonitorFrame parent) {
        this.sender = sender;
        frame = new JDialog(parent.getJFrame(), "Change Round", true);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    frame.hide();
            }
        });
        build();
    }

    protected int getPreferredTableHeight() {
        return 200;
    }

    protected int getPreferredTableWidth() {
        return 430;
    }


    private Runnable displayRunnable = new Runnable() {
        public void run() {
            frame.setLocationRelativeTo(frame.getParent());
            frame.setVisible(true);
        }
    };

    public void receivedResponse(Object response) {
        if (response instanceof RoundAccessResponse) {
            model.update(((RoundAccessResponse) response).getRounds());
            SwingUtilities.invokeLater(displayRunnable);
        } else {
            throw new IllegalArgumentException("Unrecognized response: " + response);
        }
    }

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
        insert(pane, frame.getContentPane(), gbc, 0, 0, 1, 1);

        buildSelectButton();
        selectButton.setMnemonic('S');

        gbc.weighty = 0;
        gbc.weightx = .1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        insert(selectButton, frame.getContentPane(), gbc, 0, 1, 1, 1);

        frame.pack();
    }

    protected void selectEvent(int row) {
        RoundAccessItem round = model.getRound(row);
        sender.sendChangeRound(round.getId());
        frame.hide();
    }

    private void buildSelectButton() {
        selectButton = new JButton("Select");
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectEvent(row);
                }
            }
        });
    }


    protected void setColumnWidths() {
        setColumnWidth(0, 20);
        setColumnWidth(1, 240);
        setColumnWidth(1, 160);
    }

    protected final void setColumnWidth(int col, int width) {
        table.getColumnModel().getColumn(col).setPreferredWidth(width);
    }

    private void buildTable() {
        table = new JTable(model);
        table.getColumnModel().setColumnSelectionAllowed(false);
        table.getColumnModel().setColumnMargin(3);
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
                    selectButton.doClick();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectButton.doClick();
                }
            }
        });
    }

    private void insert(Component comp, Container container, GridBagConstraints gbc, int x, int y, int xspan, int yspan) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = xspan;
        gbc.gridheight = yspan;
        container.add(comp, gbc);
    }
}
