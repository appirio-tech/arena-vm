/*
 * LongTestResultsFrame
 * 
 * Created 07/30/2007
 */
package com.topcoder.client.contestApplet.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.panels.table.LongTestResultsSummaryPanel;
import com.topcoder.netCommon.contestantMessages.response.LongTestResultsResponse;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: LongTestResultsFrame.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongTestResultsFrame extends JFrame {
    private ContestApplet parentFrame = null;
    private LongTestResultsSummaryPanel summaryPanel = null;
    private LongTestResultsResponse response;

    public LongTestResultsFrame(ContestApplet parent, LongTestResultsResponse response) {
        super(response.getHandle() + "'s "+response.getResultTypeString());
        this.parentFrame = parent;
        this.response = response;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        create();
        Common.setLocationRelativeTo(parentFrame.getCurrentFrame(), this);
        setVisible(true);
    }
    
    public void create() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        summaryPanel = new LongTestResultsSummaryPanel(parentFrame, response);
        summaryPanel.setPreferredSize(new Dimension(600, 250));
        panel.add(summaryPanel, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
    }
}
