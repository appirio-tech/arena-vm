package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.uilogic.panels.AbstractSummaryTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.ChallengeTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.ChatPanel;
import com.topcoder.client.contestApplet.uilogic.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.uilogic.panels.LongSummaryTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.ResultDisplayTypeSelectionPanel;
import com.topcoder.client.contestApplet.uilogic.panels.UserTablePanel;
import com.topcoder.client.contestApplet.uilogic.views.PrettyToggleProvider;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.view.RoomView;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contest.ResultDisplayType;

public class RoomInfoFrame implements FrameLogic, RoomView, PrettyToggleProvider {
    private UIComponent frame;
    private UIPage page;
    private RoomModel model;
    private ContestApplet parentFrame = null;

    //private SourceViewer src = null;
    private AbstractSummaryTablePanel challengePanel = null;
    private UserTablePanel userPanel = null;
    private ChatPanel chatPanel = null;
    private boolean once = true;
    private UIComponent jrb2 = null;

    private boolean enabled = true;
    private ResultDisplayTypeSelectionPanel resultDisplayTypeSelectionPanel;
    private ContestSponsorPanel sponsorPanel;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        userPanel.setPanelEnabled(on);
        chatPanel.setPanelEnabled(on);
        challengePanel.setPanelEnabled(on);
    }

    public UIComponent getFrame() {
        return frame;
    }
    
    public boolean getPrettyToggle() {
        return ((Boolean) jrb2.getProperty("Selected")).booleanValue();
    }
    
    
    /**
     * Class constructor
     */
    public RoomInfoFrame(ContestApplet parent, RoomModel room) {
        page = parent.getCurrentUIManager().getUIPage("room_info_frame", true);
        frame = page.getComponent("root_frame");
        jrb2 = page.getComponent("pretty_on");
        parentFrame = parent;
        this.model = room;
        //create();
    }
    
    public RoomModel getModel() {
        return model;
    }
    
    public boolean hasRoomModel() {
        return model != null;
    }
    
    public void showFrame(boolean enabled) {
        if (once) {
            Common.setLocationRelativeTo(parentFrame.getMainFrame(), (Component) frame.getEventSource());
            once = false;
        }
        frame.performAction("show");
        MoveFocus.moveFocus(challengePanel.getTable());
    }

    private boolean isLongRound() {
        return model.getRoundModel() != null && model.getRoundModel().getRoundType().isLongRound();
    }
    
    private void create() {
        if (isLongRound()) {
            page.getComponent("challenge_table_panel").setProperty("Visible", Boolean.FALSE);
            page.getComponent("pretty_toggle_panel").setProperty("Visible", Boolean.FALSE);
            challengePanel = new LongSummaryTablePanel(parentFrame, model, this, true, page);
        } else {
            page.getComponent("long_summary_table_panel").setProperty("Visible", Boolean.FALSE);
            challengePanel = new ChallengeTablePanel(parentFrame, model, this, page);
        }
        chatPanel = new ChatPanel(parentFrame, page);
        chatPanel.readonly(true);
        userPanel = new UserTablePanel(parentFrame, page);
        sponsorPanel = new ContestSponsorPanel(page.getComponent("sponsor_logo"), CommonData.getSponsorWatchRoomImageAddr(parentFrame.getSponsorName(), getModel()));

        resultDisplayTypeSelectionPanel = new ResultDisplayTypeSelectionPanel(page, model.getRoundModel(), new ResultDisplayTypeSelectionPanel.Listener() {
            public void typeChanged(ResultDisplayType newType) {
                challengePanel.updateView(newType);
            }
        });
        frame.performAction("pack");
    }


    public void setModel(RoomModel model) {
        this.model = model;
        create();
        model.addChatView(chatPanel);
        model.addUserListView(userPanel);
        model.addUserListView(chatPanel);
        model.addChallengeView(challengePanel);
        userPanel.updateUserList(model.getUsers());
    }

    public void unsetModel() {
        model.removeChallengeView(challengePanel);
        model.removeUserListView(chatPanel);
        model.removeUserListView(userPanel);
        model.removeChatView(chatPanel);
        model.unsetWatchView();
        frame.performAction("hide");
    }

    public void setName(String name) {
        frame.setProperty("title", ((String) frame.getProperty("title")) + " - " + name);
        frame.performAction("repaint");
    }

    public void setStatus(String status) {
    }


    public String toString() {
        return "Room Info Frame - " + model;
    }

    public void dispose() {
        frame.performAction("dispose");
    }

    public void hide() {
        frame.performAction("hide");
    }
}
