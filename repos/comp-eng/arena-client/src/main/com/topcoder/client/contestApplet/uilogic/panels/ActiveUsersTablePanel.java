package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserNameEntry;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.netCommon.contest.ContestConstants;

public class ActiveUsersTablePanel extends UserTablePanel {
    protected String getTablePanelName() {
        return "active_user_table_panel";
    }

    protected String getTableName() {
        return "active_user_table";
    }

    protected String getMenuName() {
        return "active_user_table_menu";
    }

    private JFrame jf;

    public ActiveUsersTablePanel(ContestApplet ca, UIPage page, JFrame jf) {
        super(ca, page, "active_user_table_user_renderer", "active_user_table_header_renderer");
        this.jf = jf;

        page.getComponent("active_user_table_menu_info").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            });

        page.getComponent("active_user_table_menu_search").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    searchPopupEvent();
                }
            });
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected void infoPopupEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        int index = getTable().getSelectedRow();
        String handle = ((UserNameEntry) getTable().getValueAt(index, 1)).getName();
        ca.setCurrentFrame(jf);
        ca.requestCoderInfo(handle, ((UserNameEntry) getTable().getValueAt(index, 1)).getUserType());
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void searchPopupEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        int index = getTable().getSelectedRow();
        String handle = ((UserNameEntry) getTable().getValueAt(index, 1)).getName();
        ca.setCurrentFrame(jf);
        ca.getInterFrame().showMessage("Fetching search results...", ContestConstants.SEARCH);
        ca.getModel().getRequester().requestSearch(handle);
    }
}
