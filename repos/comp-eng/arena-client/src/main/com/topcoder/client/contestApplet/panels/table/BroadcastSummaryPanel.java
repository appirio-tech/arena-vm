package com.topcoder.client.contestApplet.panels.table;

/**
 * This class presents the list of current broadcasts.  When first created,
 * the initial list is retrieved from the server.  While the panel is open,
 * any new broadcasts will trigger an update (@see BroadcastListener).
 */


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.frames.*;
import com.topcoder.client.contestant.*;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.SortElement;
import com.topcoder.netCommon.contestantMessages.*;

public final class BroadcastSummaryPanel extends TablePanel {

    // Column headers
    private static final String[] headers = new String[]{
        "T", "Time", "Status", "Message"
    };

    // Pops up when right-clicking on a row
    private final MenuItemInfo[] OPEN_POPUP = {
        new MenuItemInfo("Open", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openBroadcastEvent();
            }
        }),
    };
    
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
    }

    public BroadcastSummaryPanel(ContestApplet ca) {
        super(ca, "Summary", new BroadcastSummaryTableModel(ca.getModel().getBroadcastManager()));
        setContestPopup("", OPEN_POPUP);
        contestTable.setRowMargin(3);
        contestTable.setRowSelectionAllowed(true);
        contestTable.setColumnSelectionAllowed(false);
        contestTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        contestTable.getTableHeader().setBackground(Common.BG_COLOR);
        contestTable.getColumnModel().getColumn(0).setPreferredWidth(25);
        contestTable.getColumnModel().getColumn(0).setResizable(false);
        contestTable.getColumnModel().getColumn(0).setMaxWidth(25);
        contestTable.getColumnModel().getColumn(0).setCellRenderer(new BroadcastMessageRenderer());
        contestTable.getColumnModel().getColumn(1).setPreferredWidth(159);
        contestTable.getColumnModel().getColumn(1).setResizable(false);
        contestTable.getColumnModel().getColumn(1).setMaxWidth(159);
        contestTable.getColumnModel().getColumn(1).setCellRenderer(new DateRenderer());
        contestTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        contestTable.getColumnModel().getColumn(2).setMaxWidth(70);
        contestTable.getColumnModel().getColumn(2).setResizable(false);
        contestTable.getColumnModel().getColumn(2).setCellRenderer(new BroadcastMessageRenderer());
        contestTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        contestTable.getColumnModel().getColumn(3).setCellRenderer(new BroadcastMessageRenderer());

        contestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(enabled) {
                    mouseClickEvent(e);
                }
            }
        });

        contestTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                headerClickEvent(e);
            }
        });

        contestTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(enabled) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        openBroadcastEvent();
                }
            }
        });
    }


    private void mouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);

        if (SwingUtilities.isRightMouseButton(e))
            showContestPopup(e);
        else if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e))
            openBroadcastEvent();
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int col = getTable().getTableHeader().columnAtPoint(e.getPoint());
            if (col == -1) return;
            getTableModel().sort(col, (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0);
            getTable().getTableHeader().repaint();
        }
    }


    private synchronized void openBroadcastEvent() {
        int r = contestTable.getSelectedRow();
        if (r == -1) return;
        AdminBroadcast bc = (AdminBroadcast) tableModel.get(r);
        //we're already in the event queue
        BroadcastDialog bd = new BroadcastDialog(ca, bc,false);
        bd.show();
    }

    static Color getColorForType(String type) {
        switch (type.charAt(0)) {
        case 'G':
            return Common.CODER_BLUE;
        case 'R':
            return Common.CODER_GREEN;
        case 'P':
            return Common.CODER_RED;
        }
        return Color.white;
    }

    // Has to be static since we pass it to super()...no this pointer yet.
    static class BroadcastSummaryTableModel extends SortedTableModel implements BroadcastListener {

        /**
         * Listener method.  Called upon receipt of a new broadcast.
         * @param bc
         */
        public void newBroadcast(AdminBroadcast bc) {
            update(manager.getBroadcasts());
        }


        public void refreshBroadcasts() {
            update(manager.getBroadcasts());
        }

        public synchronized void readBroadcast(AdminBroadcast bc) {
            update(manager.getBroadcasts());
        }


        private BroadcastManager manager;

        public BroadcastSummaryTableModel(BroadcastManager manager) {
            super(headers, new Class[]{
                String.class,
                Long.class,
                String.class,
                String.class
            });
            this.manager = manager;
            addSortElement(new SortElement(1, true));
            manager.addBroadcastListener(this, true);
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            AdminBroadcast cur = (AdminBroadcast) get(rowIndex);
            switch (columnIndex) {
            case 0:
                if (cur instanceof ComponentBroadcast) {
                    return "P";
                } else if (cur instanceof RoundBroadcast) {
                    return "R";
                } else {
                    return "G";
                }
            case 1:
                return new Long(cur.getTime());
            case 2:
                return manager.hasRead(cur) ? "Read" : "Unread";
            case 3:
                return Common.htmlEncode(cur.getMessage());
            default:
                throw new IllegalArgumentException("Bad column: " + columnIndex);

            }
        }

        public int compare(Object o1, Object o2) {
            AdminBroadcast bc1 = (AdminBroadcast) o1;
            AdminBroadcast bc2 = (AdminBroadcast) o2;
            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                switch (col) {
                case 0:
                    {
                        int diff = bc1.getType() - bc2.getType();
                        //if (diff != 0) return sign*diff;
                        //break;
                        return sign * diff;
                    }
                case 1:
                    {
                        long diff = bc1.getTime() - bc2.getTime();
                        //if (diff != 0) return sign*(diff > 0 ? 1 : -1);
                        //break;
                        return sign * (diff > 0 ? 1 : -1);
                    }
                case 2:
                    {
                        boolean bc1Read = manager.hasRead(bc1);
                        boolean bc2Read = manager.hasRead(bc2);
                        if (bc1Read && !bc2Read) return -1;
                        if (!bc1Read && bc2Read) return 1;
                        return 0;
                        //break;
                    }
                case 3:
                    {
                        int diff = bc1.getMessage().compareTo(bc2.getMessage());
                        //if (diff != 0) return sign * diff;
                        //break;
                        return sign * diff;
                    }
                default:
                    throw new IllegalArgumentException("Bad column: " + sortElem);
                }
            }
            throw new IllegalStateException("Problem sorting broadcasts: " + getItemList());
        }

    }
}


