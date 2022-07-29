package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.panels.ActiveUsersTablePanel;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;

public class ActiveUsersTableFrame extends TableFrame implements UserListListener {
    private UIPage page;
    
    public void setPanelEnabled(boolean on) {
        tp.setPanelEnabled(on);
    }
    
    private ActiveUsersTablePanel tp;

    protected UIComponent createFrame() {
        page = parentFrame.getCurrentUIManager().getUIPage("active_user_table_frame", true);
        return page.getComponent("root_frame");
    }

    public ActiveUsersTableFrame(ContestApplet parentFrame) {
        super(parentFrame);

        tp = new ActiveUsersTablePanel(parentFrame, page, (JFrame) page.getComponent("root_frame").getEventSource());

        create(tp);
    }


    public void updateUserList(final UserListItem[] items) {
        EventQueue.invokeLater(new Runnable() {
                public void run() {
                    getTablePanel().clear();
                    ((ActiveUsersTablePanel) getTablePanel()).updateUserList(items);
                    frame.performAction("show");
                }
            });
    }
}
