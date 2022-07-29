/*
 * AbstractSummaryTablePanel
 * 
 * Created 06/13/2007
 */
package com.topcoder.client.contestApplet.panels.table;

import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.topcoder.client.contestant.view.ChallengeView;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 * @author Diego Belfer (mural)
 * @version $Id: AbstractSummaryTablePanel.java 67962 2008-01-15 15:57:53Z mural $
 */
public abstract class AbstractSummaryTablePanel extends JPanel implements ChallengeView {

    public AbstractSummaryTablePanel() {
        super();
    }

    public AbstractSummaryTablePanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public AbstractSummaryTablePanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public AbstractSummaryTablePanel(LayoutManager layout) {
        super(layout);
    }

    public abstract void setPanelEnabled(boolean on);
    public abstract void closeSourceViewer();
    public abstract void updateView(ResultDisplayType displayType);
    public abstract JComponent getTable();
}
