package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.panels.MultiMethodProblemInfoPanel;
import com.topcoder.client.contestApplet.uilogic.panels.ProblemInfoComponent;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.netCommon.contest.ContestConstants;

public class LongCodingFrame extends CodingFrame {
    public LongCodingFrame(ContestApplet parent) {
        super(parent, parent.getCurrentUIManager().getUIPage("long_coding_frame", true));
    }

    protected Map createButtonDef() {
        Map map = super.createButtonDef();
        map.remove("test_button");
        map.remove("compile_button");
        map.put("example_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    exampleLongButtonEvent();
                }
            }, null));
        map.put("example_results_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    viewLastExampleButtonEvent();
                }
            }, null));
        map.put("submission_history_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    viewSubmissionHistoryButtonEvent();
                }
            }, null));
        map.put("example_history_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    viewExampleHistoryButtonEvent();
                }
            }, null));
        map.put("queue_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    viewQueueButtonEvent();
                }
            }, null));
        ((ButtonDef) map.get("submit_button")).listener = new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    submitLongButtonEvent();
                }
            };
        return map;
    }
    
    private void viewLastExampleButtonEvent() {
        if(isPanelEnabled()) {
            ContestApplet parentFrame = getParentFrame();
            Contestant model = parentFrame.getModel();
            parentFrame.setCurrentFrame((JFrame) getFrame().getEventSource());
            parentFrame.getInterFrame().showMessage("Fetching results...", (JFrame) getFrame().getEventSource(), ContestConstants.LONG_TEST_RESULTS_REQUEST);
            parentFrame.getRequester().requestLongTestResults(getComponentId(), getRoomModel().getRoomID().longValue(), model.getCurrentUser(), 0);
        }
    }

    private void submitLongButtonEvent() {
        if(isPanelEnabled()) {
            if (Common.confirm("Warning", "Would you like to submit your code ?", (JFrame) getFrame().getEventSource())) {
                setButtons(false, false, false, false, false, false, false, false);
                ContestApplet parentFrame = getParentFrame();
                parentFrame.setCurrentFrame((JFrame) getFrame().getEventSource());
                parentFrame.getInterFrame().showMessage("Submitting...", (JFrame) getFrame().getEventSource(), ContestConstants.SUBMIT_PROBLEM);
                parentFrame.getRequester().requestSubmitLong(getComponentId(), getSourceCode(), getCurrentLanguageId(), false);
            }
        }
    }
    
    private void exampleLongButtonEvent() {
        if(isPanelEnabled()) {
            if (Common.confirm("Warning", "Would you like to run your code against example test cases ?", (JFrame) getFrame().getEventSource())) {
                setButtons(false, false, false, false, false, false, false, false);
                ContestApplet parentFrame = getParentFrame();
                parentFrame.setCurrentFrame((JFrame) getFrame().getEventSource());
                parentFrame.getInterFrame().showMessage("Example submission...", (JFrame) getFrame().getEventSource(), ContestConstants.TEST);
                parentFrame.getRequester().requestSubmitLong(getComponentId(), getSourceCode(), getCurrentLanguageId(), true);
            }
        }
    }
    
    protected void viewSubmissionHistoryButtonEvent() {
        if(isPanelEnabled()) {
            ContestApplet parentFrame = getParentFrame();
            parentFrame.setCurrentFrame((JFrame) getFrame().getEventSource());
            Contestant model = parentFrame.getModel();
            getParentFrame().requestSubmissionHistory(model.getCurrentUser(), getRoomModel().getRoomID().longValue(), ContestConstants.SINGLE_USER, false);
        }
    }


    protected void viewExampleHistoryButtonEvent() {
        if(isPanelEnabled()) {
            ContestApplet parentFrame = getParentFrame();
            parentFrame.setCurrentFrame((JFrame) getFrame().getEventSource());
            Contestant model = parentFrame.getModel();
            getParentFrame().requestSubmissionHistory(model.getCurrentUser(), getRoomModel().getRoomID().longValue(), ContestConstants.SINGLE_USER, true);
        }
    }


    protected void viewQueueButtonEvent() {
        if(isPanelEnabled()) {
            ContestApplet parentFrame = getParentFrame();
            parentFrame.setCurrentFrame((JFrame) getFrame().getEventSource());
            parentFrame.getInterFrame().showMessage("Fetching queue status...", (JFrame) getFrame().getEventSource(), ContestConstants.VIEW_QUEUE_REQUEST);
            getParentFrame().getRequester().requestViewQueueStatus();
        }
    }
    
    protected ProblemInfoComponent newProblemInfoPanel() {
        return new MultiMethodProblemInfoPanel(getParentFrame(), 1, page);
    }
    
    protected String getDefaultEditor() {
        return "Standard";
    }
    
    protected boolean isEditorAllowed() {
        return false;
    }
}
