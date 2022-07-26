package com.topcoder.client.contestApplet.panels.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.MenuItemInfo;
import com.topcoder.client.contestApplet.panels.html.IntermissionPanelManager;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;
import com.topcoder.netCommon.contestantMessages.response.data.RoundData;
public final class VisitedPracticePanel extends TablePanel {
    
    // Column headers
    private static final String[] headers = new String[]{
        "Room Name"
    };

    // Pops up when right-clicking on a row
    private final MenuItemInfo[] OPEN_POPUP = {
        new MenuItemInfo("Open", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openMessageEvent();
                }
            }),
    };
    
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
    }
    
    public void update(CreateVisitedPracticeResponse resp) {
        int[] roundIDs = resp.getRoundIDs();
        ArrayList list = new ArrayList(roundIDs.length);
        for (int i = 0; i < roundIDs.length; i++) {
            list.add(new Integer(roundIDs[i]));
        }
        getTableModel().update(list);
    }
    
    public VisitedPracticePanel(ContestApplet ca) {
        super(ca, "Rooms where you have opened at least one problem", new VisitedPracticeTableModel(ca));
        setContestPopup("", OPEN_POPUP);
        contestTable.setRowMargin(3);
        contestTable.setRowSelectionAllowed(true);
        contestTable.setColumnSelectionAllowed(false);
        contestTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        contestTable.getTableHeader().setBackground(Common.BG_COLOR);
        contestTable.getColumnModel().getColumn(0).setMinWidth(200);
        contestTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        contestTable.getColumnModel().getColumn(0).setResizable(true);
        contestTable.getColumnModel().getColumn(0).setMaxWidth(500);
        contestTable.getColumnModel().getColumn(0).setCellRenderer(new RoundRenderer());

        contestTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(enabled) {
                        mouseClickEvent(e);
                    }
                }
            });

        contestTable.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if(enabled) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER)
                            openMessageEvent();
                    }
                }
            });
        
    }
    
    private void mouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);

        if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e))
            openMessageEvent();
    }
    
    // 
    private synchronized void openMessageEvent() {
        int r = contestTable.getSelectedRow();
        if (r < 0)  {
            return;
        } else {
            RoundModel round = (RoundModel) getTableModel().getValueAt(r,0);
            ca.getRoomManager().loadRoom(round.getCoderRooms()[0].getType().intValue(),
                                          round.getCoderRooms()[0].getRoomID().longValue(),
                                         IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
        }
    }
    
    // Has to be static since we pass it to super()...no this pointer yet.
    static class VisitedPracticeTableModel extends SortedTableModel {
        private Contestant model;

        /**
         * Listener method.  Called upon receipt of a new broadcast.
         * @param ca 
         * @param bc
         */

        public VisitedPracticeTableModel(ContestApplet ca) {
            super(headers, new Class[]{
                RoundData.class
            });
            this.model = ca.getModel();
            addSortElement(new SortElement(0, true));
        }


        public Object getValueAt(int rowIndex, int columnIndex) {
            Integer round = (Integer) get(rowIndex);
            return model.getRound(round.intValue());
        }
        
        public int compare(Object o1, Object o2) {
            Integer id1 = (Integer) o1;
            Integer id2 = (Integer) o2;
            RoundModel round1 = model.getRound(id1.longValue());
            RoundModel round2 = model.getRound(id2.longValue());
            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int sign = sortElem.isOpposite() ? -1 : 1;
                int diff = round1.getContestName().compareTo(round2.getContestName());
                return sign * diff;
            }
            throw new IllegalStateException("Problem sorting broadcasts: " + getItemList());
        }
    }
}
