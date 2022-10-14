package com.topcoder.client.contestApplet.frames;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.panels.table.PracticeSystestResultsSummaryPanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResponse;
import com.topcoder.netCommon.contestantMessages.response.PracticeSystemTestResultResponse;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class PracticeSystestResultsFrame extends JFrame {
    
    private ContestApplet parentFrame = null;
    private PracticeSystestResultsSummaryPanel summaryPanel = null;
    private JProgressBar progress = null;
    private Map testCounts = null;
    private Map testsCompleted = null;
    private Map statusMap = null;
    
    private JPanel status = null;
    private Map labelMap = null;
    private static ProblemComponentModel[] components = null;
            
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        summaryPanel.setPanelEnabled(on);
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
            lbl.setForeground(Color.RED);
        } else if(testCount == testTotal) {
            lbl.setForeground(Color.GREEN);
        }
    }
    
    private void updateProgressStatus() {
        int r = 0;
        for(Iterator i = testsCompleted.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            r += ((Integer)testsCompleted.get(key)).intValue();
        }
        progress.setValue(r);
        progress.setString(r + "/" + getTotalTestCount());
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
        
        status.removeAll();
        
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
            JLabel lbl = new JLabel("" + getCompVal(compId));
            lbl.setForeground(Color.WHITE);
            lbl.setBackground(Color.BLACK);
            lbl.setOpaque(true);
            lbl.setHorizontalAlignment(JLabel.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            
            GridBagConstraints gbc2 = Common.getDefaultConstraints();
            gbc2.insets = new Insets(0,0,0,0);
            gbc2.fill = GridBagConstraints.BOTH;
            Common.insertInPanel(lbl, status, gbc2, xIdx, 0, 1, 1, 1, 1);
            
            labelMap.put(key, lbl);
            
            xIdx++;
        }        
        
        progress.setMaximum(getTotalTestCount());
        progress.setValue(0);
        progress.setStringPainted(true);
        
        
        
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
        super("Practice System Test Results");
        parentFrame = parent;
        
        create();
    }
    
    public void showFrame() {
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), this);
        show();
        MoveFocus.moveFocus(summaryPanel.getTable());
    }
    
    /**
     * Create the room
     */
    public void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();
        
        UIManager.put("ProgressBar.selectionBackground",Common.PT_COLOR);
        UIManager.put("ProgressBar.selectionForeground",Color.WHITE);
        
        // create all the panels/panes
        progress = new JProgressBar();
        progress.setBorder(null);
        progress.setBackground(Common.WPB_COLOR);
        progress.setForeground(Color.BLACK);
        progress.setBorderPainted(false);
        
        JPanel pnl = new JPanel();
        pnl.setBorder(Common.getTitledBorder("Progress"));
        pnl.setBackground(Common.WPB_COLOR);
        pnl.setLayout(new GridBagLayout());
        GridBagConstraints gbc2 = Common.getDefaultConstraints();
        gbc2.insets = new Insets(0,0,0,0);
        gbc2.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(progress, pnl, gbc2, 0, 0, 1, 1, 1, 1);
        
        status = new JPanel();
        
        status.setBorder(Common.getTitledBorder("Status"));
        status.setBackground(Common.WPB_COLOR);
        status.setLayout(new GridBagLayout());
        gbc2 = Common.getDefaultConstraints();
        gbc2.insets = new Insets(0,0,0,0);
        gbc2.fill = GridBagConstraints.BOTH;
        //Common.insertInPanel(progress, pnl, gbc2, 0, 0, 1, 1, 1, 1);
        
        
        summaryPanel = new PracticeSystestResultsSummaryPanel(parentFrame);
        summaryPanel.setPreferredSize(new Dimension(575, 250));
        
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Common.insertInPanel(pnl, getContentPane(), gbc, 0, 1, 1, 1, 0, 0);
        Common.insertInPanel(status, getContentPane(), gbc, 0, 2, 1, 1, 0, 0);
        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(summaryPanel, getContentPane(), gbc, 0, 3, 1, 1, .5, 1.0);
        pack();
    }

}
