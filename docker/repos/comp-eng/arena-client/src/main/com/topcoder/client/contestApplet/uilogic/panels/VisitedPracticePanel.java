package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyAdapter;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contestantMessages.response.CreateVisitedPracticeResponse;
import com.topcoder.netCommon.contestantMessages.response.data.RoundData;


public class VisitedPracticePanel extends TablePanel {
    
    // Column headers
    private static final String[] headers = new String[]{
        "Room Name"
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

    protected String getTableName() {
        return "practice_room_table";
    }

    protected String getMenuName() {
        return "practice_room_table_menu";
    }

    public VisitedPracticePanel(ContestApplet ca, UIPage page) {
        super(ca, page, new VisitedPracticeTableModel(ca));

        table.addEventListener("mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(enabled) {
                        mouseClickEvent(e);
                    }
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

        page.getComponent("practice_room_table_menu_open").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openMessageEvent();
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
        int r = ((Integer) table.getProperty("SelectedRow")).intValue();
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
