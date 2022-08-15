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
import com.topcoder.client.contestApplet.uilogic.frames.ImportantMessageDialog;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyAdapter;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse;
import com.topcoder.netCommon.contestantMessages.response.GetImportantMessagesResponse.ImportantMessage;

public class ImportantMessageSummaryPanel extends TablePanel {
    // Column headers
    private static final String[] headers = new String[]{
        "Read", "Message"
    };

    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
    }
    
    public void update(GetImportantMessagesResponse resp) {
        getTableModel().update(resp.getItems());
    }

    protected String getTableName() {
        return "summary_table";
    }

    protected String getMenuName() {
        return "summary_table_menu";
    }

    public ImportantMessageSummaryPanel(ContestApplet ca, UIPage page) {
        super(ca, page, new ImportantMessageSummaryTableModel());

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
                            openMessageEvent();
                    }
                }
            });

        page.getComponent("summary_table_menu_info").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openMessageEvent();
                }
            });
    }

    private void mouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);

        if (SwingUtilities.isRightMouseButton(e))
            showContestPopup(e);
        else if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e))
            openMessageEvent();
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int col = getTable().getTableHeader().columnAtPoint(e.getPoint());
            if (col == -1) return;
            getTableModel().sort(col, (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0);
            getTable().getTableHeader().repaint();
        }
    }


    private synchronized void openMessageEvent() {
        int r = ((Integer) table.getProperty("SelectedRow")).intValue();
        if (r == -1) return;
        ImportantMessage bc = (ImportantMessage) tableModel.get(r);
        ImportantMessageDialog md = new ImportantMessageDialog(ca, bc.getMessage());
        md.show();
    }

    // Has to be static since we pass it to super()...no this pointer yet.
    static class ImportantMessageSummaryTableModel extends SortedTableModel {

        /**
         * Listener method.  Called upon receipt of a new broadcast.
         * @param bc
         */

        public ImportantMessageSummaryTableModel() {
            super(headers, new Class[]{
                Long.class,
                String.class
            });
            addSortElement(new SortElement(0, true));
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            ImportantMessage cur = (ImportantMessage) get(rowIndex);
            switch (columnIndex) {
            case 0:
                return new Long(cur.getTime());
            case 1:
                return Common.htmlEncode(cur.getMessage());
            default:
                throw new IllegalArgumentException("Bad column: " + columnIndex);

            }
        }

        public int compare(Object o1, Object o2) {
            ImportantMessage bc1 = (ImportantMessage) o1;
            ImportantMessage bc2 = (ImportantMessage) o2;
            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                switch (col) {
                case 0:
                    {
                        long diff = bc1.getTime() - bc2.getTime();
                        //if (diff != 0) return sign*(diff > 0 ? 1 : -1);
                        //break;
                        return sign * (diff > 0 ? 1 : -1);
                    }
                case 1:
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
