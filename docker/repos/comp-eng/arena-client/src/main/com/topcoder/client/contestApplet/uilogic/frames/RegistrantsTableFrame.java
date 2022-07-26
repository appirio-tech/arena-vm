package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.HSRegistrantsTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.RegistrantsTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.UserTablePanel;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;

public class RegistrantsTableFrame extends TableFrame implements UserListListener {
    public static final int OTHER = 0;
    public static final int HIGH_SCHOOL = 1;
    public static final int MARATHON = 2;

    private boolean enabled = true;
    private int type;
    private UIPage page;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        tp.setPanelEnabled(on);
    }
    
    public UserTablePanel tp;

    protected UIComponent createFrame() {
        page = parentFrame.getCurrentUIManager().getUIPage("registrants_frame", true);
        return page.getComponent("root_frame");
    }

    public RegistrantsTableFrame(ContestApplet parentFrame, int type) {
        super(parentFrame);
        this.type = type;

        if (type == HIGH_SCHOOL) {
            page.getComponent("registrants_table_content_panel").setProperty("visible", Boolean.FALSE);
            page.getComponent("hs_registrants_table_content_panel").setProperty("visible", Boolean.TRUE);
            tp = new HSRegistrantsTablePanel(parentFrame, page, (JFrame) frame.getEventSource());
        } else if (type == MARATHON) {
            page.getComponent("registrants_table_content_panel").setProperty("visible", Boolean.TRUE);
            page.getComponent("div1_label").setProperty("visible", Boolean.FALSE);
            page.getComponent("div2_label").setProperty("visible", Boolean.FALSE);
            page.getComponent("hs_registrants_table_content_panel").setProperty("visible", Boolean.FALSE);
            tp = new RegistrantsTablePanel(parentFrame, page, (JFrame) frame.getEventSource());
        } else if (type == OTHER) {
            page.getComponent("registrants_table_content_panel").setProperty("visible", Boolean.TRUE);
            page.getComponent("hs_registrants_table_content_panel").setProperty("visible", Boolean.FALSE);
            tp = new RegistrantsTablePanel(parentFrame, page, (JFrame) frame.getEventSource());
        }

        create(tp);

        Common.setLocationRelativeTo(parentFrame.getMainFrame(), (JFrame) frame.getEventSource());
    }

    public void updateUserList(final UserListItem[] items) {
        EventQueue.invokeLater(new Runnable() {
                public void run() {
                    ((UserTablePanel) getTablePanel()).clear();
                    ((UserTablePanel) getTablePanel()).updateUserList(items);

                    frame.performAction("validate");
                    frame.performAction("repaint");
                    frame.performAction("pack");
                    frame.performAction("show");
                }
            });
    }
}
