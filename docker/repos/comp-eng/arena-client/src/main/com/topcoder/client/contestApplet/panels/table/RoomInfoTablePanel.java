package com.topcoder.client.contestApplet.panels.table;

import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestant.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;


public final class RoomInfoTablePanel extends TablePanel implements LeaderListener {

    private LeaderboardTableModel leaderboardTableModel;
    private RoundModel roundModel;

    private final MenuItemInfo[] USER_POPUP = {
        new MenuItemInfo("Info", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popupEvent();
            }
        }),
    };
    
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on ) {
        enabled = on;
    }

    public RoomInfoTablePanel(ContestApplet ca) {
        super(ca, "Room Leaders", new LeaderboardTableModel(ca.getModel()), true);
        this.leaderboardTableModel = (LeaderboardTableModel) tableModel;
        setContestPopup("Contestants Info", USER_POPUP);
        contestTable.getTableHeader().setDefaultRenderer(new ContestTableHeaderRenderer(true,null,12));

        TableColumnModel columnModel = contestTable.getColumnModel();
        columnModel.getColumn(0).setWidth(50);
        columnModel.getColumn(0).setMinWidth(50);
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setWidth(TCIcon.DEFAULT_WIDTH * 2 + 3);
        columnModel.getColumn(1).setMinWidth(TCIcon.DEFAULT_WIDTH * 2 + 3);
        columnModel.getColumn(1).setMaxWidth(TCIcon.DEFAULT_WIDTH * 2 + 3);
        columnModel.getColumn(1).setCellRenderer(new RankRenderer());
        columnModel.getColumn(2).setCellRenderer(new UserNameRenderer(ca.getModel(), null, 12));
        columnModel.getColumn(3).setWidth(40);
        columnModel.getColumn(3).setMinWidth(40);
        columnModel.getColumn(3).setMaxWidth(40);
        columnModel.getColumn(4).setWidth(50);
        columnModel.getColumn(4).setMinWidth(50);
        columnModel.getColumn(4).setMaxWidth(50);
        columnModel.getColumn(5).setWidth(10);
        columnModel.getColumn(5).setMinWidth(10);
        columnModel.getColumn(5).setMaxWidth(10);

        contestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(enabled) {
                    doubleClickEvent(e);
                }
            }
        });

        contestTable.getTableHeader().addMouseListener(new MouseAdapter() {
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
        int index = contestTable.getSelectedRow();
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
