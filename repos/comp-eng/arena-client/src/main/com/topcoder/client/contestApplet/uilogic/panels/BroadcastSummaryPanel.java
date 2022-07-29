package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.frames.BroadcastDialog;
import com.topcoder.client.contestant.BroadcastListener;
import com.topcoder.client.contestant.BroadcastManager;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyAdapter;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;
import com.topcoder.netCommon.contestantMessages.RoundBroadcast;

public class BroadcastSummaryPanel extends TablePanel {
    // Column headers
    private static final String[] headers = new String[]{
        "T", "Time", "Status", "Message"
    };

    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
    }
    
    protected String getTableName() {
        return "summary_table";
    }

    protected String getMenuName() {
        return "summary_table_menu";
    }

    public BroadcastSummaryPanel(ContestApplet ca, UIPage page) {
        super(ca, page, new BroadcastSummaryTableModel(ca.getModel().getBroadcastManager()));

        table.addEventListener("mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(enabled) {
                        mouseClickEvent(e);
                    }
                }
            });

        ((JTableHeader) table.getProperty("TableHeader")).addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    headerClickEvent(e);
                }
            });

        table.addEventListener("key", new UIKeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if(enabled) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER)
                            openBroadcastEvent();
                    }
                }
            });

        page.getComponent("summary_table_menu_info").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openBroadcastEvent();
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
        int r = getTable().getSelectedRow();
        if (r == -1) return;
        AdminBroadcast bc = (AdminBroadcast) tableModel.get(r);
        //we're already in the event queue
        BroadcastDialog bd = new BroadcastDialog(ca, bc, false);
        bd.show();
    }

    // Has to be static since we pass it to super()...no this pointer yet.
    private static class BroadcastSummaryTableModel extends SortedTableModel implements BroadcastListener {

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
