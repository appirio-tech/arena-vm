package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.PracticeSystestResultsSummaryPanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse;

public class PracticeSystestResultsFrame implements FrameLogic {
    private UIComponent frame;
    private UIPage page;
    private ContestApplet parentFrame = null;
    private PracticeSystestResultsSummaryPanel summaryPanel = null;
    private UIComponent progress = null;
    private UIComponent statusLabelTemplate = null;
    private Map testCounts = null;
    private Map testsCompleted = null;
    private Map statusMap = null;
    
    private UIComponent status = null;
    private Map labelMap = null;
    private static ProblemComponentModel[] components = null;
            
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        summaryPanel.setPanelEnabled(on);
    }

    public UIComponent getFrame() {
        return frame;
    }
    
    public void update(PracticeSystemTestResultResponse resp) {
        summaryPanel.update(resp);
        //System.out.println(resp);
        
        if(resp.getResultData().isSucceeded()) {
            //increment the count by one
            testsCompleted.put(new Integer(resp.getResultData().getComponentId()), 
                               new Integer(((Integer)testsCompleted.get(new Integer(resp.getResultData().getComponentId()))).intValue() + 1));
        } else {
            statusMap.put(new Integer(resp.getResultData().getComponentId()), new Boolean(false));
            //set the count to the total
            testsCompleted.put(new Integer(resp.getResultData().getComponentId()), 
                               new Integer(((Integer)testCounts.get(new Integer(resp.getResultData().getComponentId()))).intValue()));
        }
        checkLabel(resp.getResultData().getComponentId());
        updateProgressStatus();
    }
    
    public static int getCompVal(int componentID) {
        for(int i = 0; i < components.length; i++) {
            if(componentID == components[i].getID().intValue())
                return components[i].getPoints().intValue();
        }
        return 0;
    }

    private void checkLabel(int compId) {
        int testCount = ((Integer)testsCompleted.get(new Integer(compId))).intValue();
        int testTotal = ((Integer)testCounts.get(new Integer(compId))).intValue();
        boolean status = ((Boolean)statusMap.get(new Integer(compId))).booleanValue();
        JLabel lbl = ((JLabel)labelMap.get(new Integer(compId)));
                
        if(!status) {
            lbl.setForeground((Color) page.getComponent("status_error_color").getEventSource());
        } else if(testCount == testTotal) {
            lbl.setForeground((Color) page.getComponent("status_finish_color").getEventSource());
        }
    }
    
    private void updateProgressStatus() {
        int r = 0;
        for(Iterator i = testsCompleted.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            r += ((Integer)testsCompleted.get(key)).intValue();
        }
        progress.setProperty("Value", new Integer(r));
        progress.setProperty("String", r + "/" + getTotalTestCount());
    }
    
    private class PointValueComparator implements Comparator {
        public int compare(Object arg0, Object arg1) {
            int comp1 = ((Integer)arg0).intValue();
            int comp2 = ((Integer)arg1).intValue();
            
            int val1 = getCompVal(comp1);
            int val2 = getCompVal(comp2);
            int diff = val1 - val2;
            return diff;
        }
    }
    
    public void reset(PracticeSystemTestResponse resp) {
        summaryPanel.getTableModel().clear();
        summaryPanel.reset();
        
        status.performAction("removeAll");
        
        RoomModel roomModel = parentFrame.getModel().getCurrentRoom();
        RoundModel roundModel = roomModel.getRoundModel();
        components = roundModel.getAssignedComponents(roomModel.getDivisionID());
        
        testCounts = resp.getTestCaseCountByComponentId();
        testsCompleted = new HashMap();
        statusMap = new HashMap();
        labelMap = new HashMap();
        
        int xIdx = 0;

        ArrayList al = new ArrayList();
        for(Iterator i = testCounts.keySet().iterator(); i.hasNext();) {
            al.add(i.next());
        }
        Collections.sort(al, new PointValueComparator());
        
        for(Iterator i = al.iterator(); i.hasNext();) {
            Object key = i.next();
            int compId = ((Integer)key).intValue();
            
            testsCompleted.put(key, new Integer(0));
            
            //setup problem amounts
            statusMap.put(key, new Boolean(true));
            JLabel lbl = (JLabel) statusLabelTemplate.performAction("clone");
            lbl.setText("" + getCompVal(compId));
            
            GridBagConstraints gbc2 = Common.getDefaultConstraints();
            gbc2.insets = new Insets(0,0,0,0);
            gbc2.fill = GridBagConstraints.BOTH;
            Common.insertInPanel(lbl, (JPanel) status.getEventSource(), gbc2, xIdx, 0, 1, 1, 1, 1);
            
            labelMap.put(key, lbl);
            
            xIdx++;
        }        
        
        progress.setProperty("Maximum", new Integer(getTotalTestCount()));
        progress.setProperty("Value", new Integer(0));
    }
    
    private int getTotalTestCount() {
        int r = 0;
        for(Iterator i = testCounts.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            r += ((Integer)testCounts.get(key)).intValue();
        }
        return r;
    }
    
    /**
     * Class constructor
     */
    public PracticeSystestResultsFrame(ContestApplet parent) {
        parentFrame = parent;
        page = parent.getCurrentUIManager().getUIPage("practice_systest_results_frame", true);
        frame = page.getComponent("root_frame");
        statusLabelTemplate = page.getComponent("status_label_template");        
        create();
    }
    
    public void showFrame() {
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), (JFrame) frame.getEventSource());
        frame.performAction("show");
        MoveFocus.moveFocus(summaryPanel.getTable());
    }
    
    /**
     * Create the room
     */
    public void create() {
        // create all the panels/panes
        progress = page.getComponent("progress_bar");
        status = page.getComponent("status_panel");
        summaryPanel = new PracticeSystestResultsSummaryPanel(parentFrame, page);
        frame.performAction("pack");
    }

}
