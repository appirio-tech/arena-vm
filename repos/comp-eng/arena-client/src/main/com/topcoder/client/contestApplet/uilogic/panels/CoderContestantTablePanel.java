package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserNameEntry;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserTableModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIMouseAdapter;

public class CoderContestantTablePanel extends UserTablePanel {
    protected String getTablePanelName() {
        return "contestant_table_panel";
    }

    protected String getTableName() {
        return "contestant_table";
    }

    protected String getMenuName() {
        return "contestant_table_menu";
    }

    public CoderContestantTablePanel(ContestApplet ca, UIPage page) {
        this(ca, page, CommonData.contestantHeader);
    }

    public CoderContestantTablePanel(ContestApplet ca, UIPage page, String[] header) {
        super(ca, page, new UserTableModel(ca.getModel(), header) {
            protected boolean isLeader(String handle) {
                RoomModel roomModel = contestantModel.getCurrentRoom();
                if (roomModel != null && roomModel.hasLeader()) {
                    return roomModel.getLeader().getUserName().equals(handle);
                } else {
                    return false;
                }
            }
        }, "user_table_user_renderer", "user_table_header_renderer");

        table.addEventListener("mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(enabled) {
                        rightClickEvent(e);
                        doubleClickEvent(e);
                    }
                }
            });
        userTableModel.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    tableCountEvent();
                }
            });
        page.getComponent("contestant_table_menu_info").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            });
        page.getComponent("contestant_table_menu_history").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent();
                }
            });

        userTableModel.sort(0, true);
    }

    private void historyPopupEvent() {
        int index = ((Integer)table.getProperty("SelectedRow")).intValue();
        UserNameEntry entry = (UserNameEntry) table.performAction("getValueAt", new Object[] {new Integer(index), new Integer(1)});
        ca.setCurrentFrame(ca.getMainFrame());
        ca.requestCoderHistory(entry.getName(), ca.getModel().getCurrentRoom().getRoomID().longValue(),
                               entry.getUserType());
    }
}
