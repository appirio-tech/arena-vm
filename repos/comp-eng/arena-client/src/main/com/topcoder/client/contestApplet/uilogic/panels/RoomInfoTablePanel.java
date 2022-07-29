package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.panels.table.LeaderboardTableModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.LeaderListener;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;

public class RoomInfoTablePanel extends TablePanel implements LeaderListener {
    private LeaderboardTableModel leaderboardTableModel;
    private RoundModel roundModel;
    private UIComponent panel;
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on ) {
        enabled = on;
    }

    public void setEnabled(boolean on) {
        setPanelEnabled(on);
        panel.setProperty("enabled", Boolean.valueOf(on));
    }

    protected String getTableName() {
        return "room_info_table";
    }

    protected String getMenuName() {
        return "room_info_table_menu";
    }

    public RoomInfoTablePanel(ContestApplet ca, UIPage page) {
        super(ca, page, new LeaderboardTableModel(ca.getModel()));
        this.leaderboardTableModel = (LeaderboardTableModel) tableModel;
        panel = page.getComponent("room_info_table_panel");
        page.getComponent("room_info_table_menu_info").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    popupEvent();
                }
            });

        page.getComponent("room_info_table_user_renderer").setProperty("model", ca.getModel());

        table.addEventListener("mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(enabled) {
                        doubleClickEvent(e);
                    }
                }
            });

        getTable().getTableHeader().addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    headerClickEvent(e);
                }
            });
    }


    private void doubleClickEvent(MouseEvent e) {
        if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e)) {
            popupEvent();
        }
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int col = getTable().getTableHeader().columnAtPoint(e.getPoint());
            if (col == -1) return;
            boolean shift = (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0;
            getTableModel().sort(col, shift);
            getTable().getTableHeader().repaint();
        }
    }

    private void popupEvent() {
        int index = getTable().getSelectedRow();
        //String handle = ((JLabel)contestTable.getValueAt(index, 1)).getText();
        ca.setCurrentFrame(ca.getMainFrame());
        ca.getRoomManager().watch(leaderboardTableModel.getLeaderboardItem(index).getRoomID());
    }

    public void clear() {
        leaderboardTableModel.clear();
        if (roundModel != null) {
            roundModel.removeLeaderListener(this);
            this.roundModel = null;
        }
    }

    public void setRound(RoundModel roundModel) {
        clear();
        this.roundModel = roundModel;
        this.roundModel.addLeaderListener(this);
        if (roundModel.hasLeaderboard()) {
            LeaderboardItem[] leaderboard = roundModel.getLeaderboard();
            leaderboardTableModel.update(Arrays.asList(leaderboard));
        }
    }

    public void updateLeader(RoomModel room) {
        if (roundModel != null && roundModel.hasLeaderboard()) {
            LeaderboardItem[] leaderboard = roundModel.getLeaderboard();
            leaderboardTableModel.update(Arrays.asList(leaderboard));
        }
    }
}
