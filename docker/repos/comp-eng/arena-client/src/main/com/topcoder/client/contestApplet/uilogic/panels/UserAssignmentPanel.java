package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.view.AssignmentView;
import com.topcoder.client.contestant.view.ChallengeView;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.netCommon.contest.ComponentAssignmentData;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Problem;

public class UserAssignmentPanel implements ChallengeView, AssignmentView {
    private ContestApplet ca;
    private RoomModel room;
    private UIComponent commitButton;
    private UIComponent panelContent;

    public UserAssignmentPanel(ContestApplet ca, RoomModel room, UIPage page) {
        this.room = room;
        this.ca = ca;
        panelContent = page.getComponent("user_assignment_content_panel");
        commitButton = page.getComponent("commit_button");
        commitButton.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    commit();
                }
            });
        panelContent.setProperty("RoomModel", room);
        panelContent.setProperty("ComponentAssignmentData", ca.getModel().getComponentAssignmentData());
    }

    public void commit() {
        ca.getRequester().requestAssignComponents((ComponentAssignmentData) panelContent.getProperty("ComponentAssignmentData"));
    }

    // ChallengeView
    public void setTable(ArrayList table, ArrayList ranks) {
    }

    public void updateRow(String handle, ArrayList data) {
    }

    public void updateCell(String handle, int componentID, Object status) {
    }

    public void setArguments(DataType[] args, String msg) {
    }

    public void setChallengeProblem(Problem problem) {
    }

    public void updateChallengeTable(RoomModel room) {
    }
}
