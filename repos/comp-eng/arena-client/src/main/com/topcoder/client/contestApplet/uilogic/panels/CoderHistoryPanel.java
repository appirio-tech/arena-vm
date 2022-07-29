package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.*;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.SortElement;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.CoderHistoryResponse;
import com.topcoder.netCommon.contestantMessages.response.data.CoderHistoryData;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.client.contestApplet.uilogic.frames.MessageDialog;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserNameEntry;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

public class CoderHistoryPanel extends TablePanel {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    // Column headers
    private static final String[] headers = new String[]{
        "Time", "Action", "Coder", "Problem", "Points"
    };

    protected String getTableName() {
        return "coder_history_table";
    }

    protected String getMenuName() {
        return "coder_history_table_menu";
    }

    private boolean enabled = true;

    public void setPanelEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private final UIComponent frame;
    private final ContestApplet parent;

    public CoderHistoryPanel(ContestApplet ca, UIPage page, CoderHistoryResponse response, UIComponent frame) {
        super(ca, page, new CoderHistoryTableModel(response.getHistoryData()));
        this.parent = ca;
        this.frame = frame;
        String coder = response.getName();
        String title = coder + "'s history";
        ((TitledBorder) page.getComponent("coder_history_table_panel").getProperty("Border")).setTitle(title);

        final TableCellRenderer renderer = (TableCellRenderer) page.getComponent("coder_history_score_column").getProperty("cellrenderer");
        page.getComponent("coder_history_score_column").setProperty("cellrenderer", new TableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component component = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    CoderHistoryData data = (CoderHistoryData) tableModel.get(row);

                    if (data.getPoints() < 0) {
                        component.setForeground(Color.RED);
                    } else {
                        component.setForeground(Color.GREEN);
                    }

                    return component;
                }
            });

        page.getComponent("coder_history_user_renderer").setProperty("model", ca.getModel());

        table.addEventListener("Mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(enabled) {
                        mouseClickEvent(e);
                    }
                }
            });

        ((JTableHeader) table.getProperty("TableHeader")).addMouseListener(new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    headerClickEvent(e);
                }
            });

        table.addEventListener("Key", new UIKeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if(enabled) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER)
                            openMessageEvent();
                    }
                }
            });

        page.getComponent("coder_history_table_menu_info").addEventListener("action", new UIActionListener() {
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

    private void openMessageEvent() {
        int r = ((Integer) table.getProperty("SelectedRow")).intValue();
        if (r == -1) return;
        CoderHistoryData data = (CoderHistoryData) tableModel.get(r);
        String text = data.getDetail();
        MessageDialog md = new MessageDialog(parent, frame, "Coder History Info", text);
        md.show();
    }
    static class CoderHistoryTableModel extends SortedTableModel {
        private static final NumberFormat SCORE_FORMAT = new DecimalFormat("0.00");

        /**
         * Listener method.  Called upon receipt of a new broadcast.
         * @param bc
         */

        public CoderHistoryTableModel(CoderHistoryData[] data) {
            super(headers, new Class[]{
                String.class,
                UserNameEntry.class,
                String.class,
                String.class,
                String.class,
            });
            addSortElement(new SortElement(0, true));
            addSortElement(new SortElement(1, true));
            addSortElement(new SortElement(2, true));
            addSortElement(new SortElement(3, true));
            addSortElement(new SortElement(4, true));
            update(new ArrayList(Arrays.asList(data)));
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            CoderHistoryData cur = (CoderHistoryData) get(rowIndex);
            switch (columnIndex) {
            case 0:
                return DATE_FORMAT.format(cur.getTime());
            case 1:
                return cur.getActionDescription();
            case 2:
                {
                    UserListItem item = cur.getCoder();
                    return new UserNameEntry(item.getUserName(), item.getUserRating(), false, item.getUserType());
                }
            case 3:
                if (cur.getComponentValue() >= 0) {
                    return Integer.toString(cur.getComponentValue());
                } else {
                    return "N/A";
                }
            case 4:
                if (Double.isNaN(cur.getPoints())) {
                    return "N/A";
                } else {
                    return Common.formatScore(cur.getPoints());
                }
            default:
                throw new IllegalArgumentException("Bad column: " + columnIndex);

            }
        }
        
        public int compare(Object o1, Object o2) {
            CoderHistoryData bc1 = (CoderHistoryData) o1;
            CoderHistoryData bc2 = (CoderHistoryData) o2;
            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                switch (col) {
                case 0:
                    {
                        Date val1 = bc1.getTime();
                        Date val2 = bc2.getTime();
                        int diff = val1.compareTo(val2);
                        if (diff != 0)
                            return diff;
                        break;
                    }
                case 1:
                    {
                        int val1 = bc1.getAction();
                        int val2 = bc2.getAction();
                        int diff = (val1 < val2) ? -1 : ((val1 > val2) ? 1 : 0);
                        if (diff != 0)
                            return diff;
                        break;
                    }
                case 2:
                    {
                        UserListItem val1 = bc1.getCoder();
                        UserListItem val2 = bc2.getCoder();
                        int diff = val1.compareTo(val2);
                        if (diff != 0)
                            return diff;
                        break;
                    }
                case 3:
                    {
                        int val1 = bc1.getComponentValue();
                        int val2 = bc2.getComponentValue();
                        int diff = val2 - val1;
                        if(diff != 0)
                            return sign * (diff > 0 ? 1 : -1 );
                        break;
                    }
                case 4:
                    {
                        double val1 = bc1.getPoints();
                        double val2 = bc2.getPoints();
                        int diff = Double.compare(val1, val2);
                        if (diff != 0)
                            return diff;
                        break;
                    }
                default:
                    
                    throw new IllegalArgumentException("Bad column: " + sortElem);
                }
            }
            return 0;
        }

    }
}
