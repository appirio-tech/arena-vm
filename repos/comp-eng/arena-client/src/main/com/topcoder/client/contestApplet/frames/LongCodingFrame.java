/*
 * LongCodingFrame
 * 
 * Created 06/05/2007
 */
package com.topcoder.client.contestApplet.frames;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.panels.coding.MultiMethodProblemInfoPanel;
import com.topcoder.client.contestApplet.panels.coding.ProblemInfoComponent;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongCodingFrame.java 67962 2008-01-15 15:57:53Z mural $
 */
public class LongCodingFrame extends CodingFrame {
    private JButton resultsButton;
    private JButton subHistoryButton;
    private JButton exHistoryButton;
    private JButton queueButton;

    public LongCodingFrame(ContestApplet parent) {
        super(parent);
    }

    protected ButtonDef[] createButtonDefs() {
        ButtonDef[] oldDef = super.createButtonDefs();
        ButtonDef[] defs = new ButtonDef[4]; 
        
        defs[0] = oldDef[0];
        defs[1] = oldDef[1];
        defs[3] = oldDef[4];
        defs[2] = new ButtonDef("g_example_but.gif", " no_g_example_but.gif", "Test Examples", null, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        exampleLongButtonEvent();
                    }
                });
        
        defs[3].toolTipText = "Full submit";
        defs[3].actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitLongButtonEvent();
            }
        };
        return defs;
    }
    
    private void viewLastExampleButtonEvent() {
        if(isPanelEnabled()) {
            ContestApplet parentFrame = getParentFrame();
            Contestant model = parentFrame.getModel();
            parentFrame.setCurrentFrame(this);
            parentFrame.getInterFrame().showMessage("Fetching results...", this, ContestConstants.LONG_TEST_RESULTS_REQUEST);
            parentFrame.getRequester().requestLongTestResults(getComponentId(), getRoomModel().getRoomID().longValue(), model.getCurrentUser(), 0);
        }
    }

    private void submitLongButtonEvent() {
        if(isPanelEnabled()) {
            setButtons(false, false, false, false, false, false, false, false);
            ContestApplet parentFrame = getParentFrame();
            parentFrame.setCurrentFrame(this);
            parentFrame.getInterFrame().showMessage("Submitting...", this, ContestConstants.SUBMIT_PROBLEM);
            parentFrame.getRequester().requestSubmitLong(getComponentId(), getSourceCode(), getCurrentLanguageId(), false);
        }
    }
    
    private void exampleLongButtonEvent() {
        if(isPanelEnabled()) {
            setButtons(false, false, false, false, false, false, false, false);
            ContestApplet parentFrame = getParentFrame();
            parentFrame.setCurrentFrame(this);
            parentFrame.getInterFrame().showMessage("Example submission...", this, ContestConstants.TEST);
            parentFrame.getRequester().requestSubmitLong(getComponentId(), getSourceCode(), getCurrentLanguageId(), true);
        }
    }
    
    protected void viewSubmissionHistoryButtonEvent() {
        if(isPanelEnabled()) {
            ContestApplet parentFrame = getParentFrame();
            parentFrame.setCurrentFrame(this);
            Contestant model = parentFrame.getModel();
            getParentFrame().requestSubmissionHistory(model.getCurrentUser(), getRoomModel().getRoomID().longValue(), ContestConstants.SINGLE_USER, false);
        }
    }


    protected void viewExampleHistoryButtonEvent() {
        if(isPanelEnabled()) {
            ContestApplet parentFrame = getParentFrame();
            parentFrame.setCurrentFrame(this);
            Contestant model = parentFrame.getModel();
            getParentFrame().requestSubmissionHistory(model.getCurrentUser(), getRoomModel().getRoomID().longValue(), ContestConstants.SINGLE_USER, true);
        }
    }


    protected void viewQueueButtonEvent() {
        if(isPanelEnabled()) {
            ContestApplet parentFrame = getParentFrame();
            parentFrame.setCurrentFrame(this);
            parentFrame.getInterFrame().showMessage("Fetching queue status...", this, ContestConstants.VIEW_QUEUE_REQUEST);
            getParentFrame().getRequester().requestViewQueueStatus();
        }
    }
    
    
    protected ProblemInfoComponent newProblemInfoPanel() {
        return new MultiMethodProblemInfoPanel(getParentFrame(), 1);
    }
    
    protected JPanel newUnderProblemInfoPanel() {
        JPanel sponsorPanel = super.newUnderProblemInfoPanel();
        JPanel panel = new JPanel(new GridLayout(1,4));
        panel.setOpaque(false);
        resultsButton = createButton(new ButtonDef("g_vexample_but.gif", " no_g_vexample_but.gif", "View last example submission results", null, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        viewLastExampleButtonEvent();
                    }
                }));
        subHistoryButton = createButton(new ButtonDef("g_subhistory_but.gif", " no_g_subhistory_but.gif", "View submission history", null, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        viewSubmissionHistoryButtonEvent();
                    }
                }));
        
        exHistoryButton = createButton(new ButtonDef("g_exhistory_but.gif", " no_g_exhistory_but.gif", "View example history", null, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        viewExampleHistoryButtonEvent();
                    }
                }));
        queueButton = createButton(new ButtonDef("g_queue_but.gif", " no_g_queue_but.gif", "View queue status", null, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        viewQueueButtonEvent();
                    }
                }));
        panel.add(resultsButton);
        panel.add(subHistoryButton);
        panel.add(exHistoryButton);
        panel.add(queueButton);

        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(panel, BorderLayout.NORTH);
        combinedPanel.add(sponsorPanel,BorderLayout.CENTER);
        return combinedPanel;
    }

    protected void updateButtons(boolean b) {
        resultsButton.setEnabled(b);
        subHistoryButton.setEnabled(b);
        exHistoryButton.setEnabled(b);
        queueButton.setEnabled(b);
    }
    
    protected String getDefaultEditor() {
        return "Standard";
    }
    
    protected boolean isEditorAllowed() {
        return false;
    }
}
