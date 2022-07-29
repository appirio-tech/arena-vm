package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.uilogic.panels.AbstractSummaryTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.ChallengeTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.CodingTimerPanel;
import com.topcoder.client.contestApplet.uilogic.panels.LongSummaryTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.ResultDisplayTypeSelectionPanel;
import com.topcoder.client.contestApplet.uilogic.panels.TimerPanel;
import com.topcoder.client.contestApplet.uilogic.panels.UserAssignmentPanel;
import com.topcoder.client.contestApplet.uilogic.views.PrettyToggleProvider;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.ResultDisplayType;

public class ChallengeFrame implements FrameLogic, PrettyToggleProvider {
    private UIComponent frame;
    private ContestApplet parentFrame = null;

    // declared global for handling/referencing
    private AbstractSummaryTablePanel challengePanel = null;
    private CodingTimerPanel timerPanel = null;
    private boolean once = true;

    private UIComponent jrb2 = null;
    private UserAssignmentPanel userAssignmentPanel = null;
    private RoomModel room;
    
    private UIPage page;

    private boolean enabled = true;
    private boolean open = false;
    private ResultDisplayTypeSelectionPanel resultDisplayTypeSelectionPanel;

    public ChallengeFrame(ContestApplet parent, RoomModel room) {
        parentFrame = parent;
        this.room = room;
        page = parent.getCurrentUIManager().getUIPage("challenge_frame", true);
        frame = page.getComponent("root_frame");
        create();
    }

    private void create() {
        if (room.getRoundModel().getRoundProperties().hasChallengePhase()) {
            page.getComponent("instruction_nochallengephase_scroll_pane").setProperty("Visible", Boolean.FALSE);
            Component mouseless = (Component) page.getComponent("instruction_pane").getEventSource();
            MouseListener[] listeners = mouseless.getMouseListeners();
            for (int i=0;i<listeners.length;++i) {
                mouseless.removeMouseListener(listeners[i]);
            }
        } else {
            page.getComponent("instruction_scroll_pane").setProperty("Visible", Boolean.FALSE);
            Component mouseless = (Component) page.getComponent("instruction_nochallengephase_pane").getEventSource();
            MouseListener[] listeners = mouseless.getMouseListeners();
            for (int i=0;i<listeners.length;++i) {
                mouseless.removeMouseListener(listeners[i]);
            }
        }

        if (room.getRoundModel() != null && room.getRoundModel().getRoundType().isLongRound()) {
            challengePanel = new LongSummaryTablePanel(parentFrame, room, this, true, page);
            page.getComponent("challenge_table_panel").setProperty("Visible", Boolean.FALSE);
        } else {
            challengePanel = new ChallengeTablePanel(parentFrame, room, this, page);
            page.getComponent("long_summary_table_panel").setProperty("Visible", Boolean.FALSE);
        }
        timerPanel = new CodingTimerPanel(parentFrame, page);

        if (parentFrame.getModel().getUserInfo().isCaptain() &&
            (parentFrame.getModel().getCurrentRoom().getType().intValue() == ContestConstants.TEAM_CODER_ROOM ||
             parentFrame.getModel().getCurrentRoom().getType().intValue() == ContestConstants.TEAM_PRACTICE_CODER_ROOM ||
             parentFrame.getModel().getCurrentRoom().getType().intValue() == ContestConstants.TEAM_ADMIN_ROOM)) {

            userAssignmentPanel = new UserAssignmentPanel(parentFrame, room, page);
            page.getComponent("user_assignment_panel").setProperty("visible", Boolean.TRUE);
        } else {
            page.getComponent("user_assignment_panel").setProperty("visible", Boolean.FALSE);
        }

        if(CommonData.showSystemTestsPerCoder(parentFrame.getCompanyName()) && !room.isPracticeRoom()) {
            page.getComponent("view_results_panel").setProperty("visible", Boolean.TRUE);
            page.getComponent("view_results_button").addEventListener("action", new UIActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //request results here
                        parentFrame.getRequester().requestSystestResults(room.getRoundModel().getRoundID().intValue());
                
                    }
                });
        } else {
            page.getComponent("view_results_panel").setProperty("visible", Boolean.FALSE);
        }

        if (room.getRoundModel().getRoundType().isLongRound()) {
            page.getComponent("pretty_toggle_panel").setProperty("Visible", Boolean.FALSE);
        } else {
            jrb2 = page.getComponent("pretty_on");
            resultDisplayTypeSelectionPanel = new ResultDisplayTypeSelectionPanel(page, room.getRoundModel(), new ResultDisplayTypeSelectionPanel.Listener() {
                public void typeChanged(ResultDisplayType newType) {
                    challengePanel.updateView(newType);
                }
            });
        }

        frame.performAction("pack");
    }

    public UIComponent getFrame() {
        return frame;
    }

    public void setPanelEnabled(boolean on) {
        enabled = on;
        challengePanel.setPanelEnabled(on);
    }

    public void showFrame(boolean enabled) {
        if (once) {
            Common.setLocationRelativeTo(parentFrame.getMainFrame(), (JFrame) frame.getEventSource());
            once = false;
        }
        frame.performAction("show");
        if (!open)
            parentFrame.getRequester().requestOpenSummary(room.getRoomID().longValue());
        open = true;
        MoveFocus.moveFocus(challengePanel.getTable());
    }

    public boolean getPrettyToggle() {
        return ((Boolean) jrb2.getProperty("Selected")).booleanValue();
    }

    public void hide() {
        frame.performAction("hide");
        challengePanel.closeSourceViewer();
        if (open && enabled)
            parentFrame.getRequester().requestCloseSummary(room.getRoomID().longValue());
        open = false;
    }

    public ResultDisplayType getViewType() {
        return resultDisplayTypeSelectionPanel.getSelectedType();
    }

    public TimerPanel getTimerPanel() {
        return (timerPanel);
    }

    public AbstractSummaryTablePanel getChallengePanel() {
        return (this.challengePanel);
    }


    public boolean hasAssignmentPanel() {
        return userAssignmentPanel != null;
    }

    public UserAssignmentPanel getAssignmentPanel() {
        return userAssignmentPanel;
    }

    public void dispose() {
        frame.destroy();
    }
}
