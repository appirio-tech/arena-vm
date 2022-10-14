package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestant.view.ChallengeView;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.netCommon.contest.ResultDisplayType;

public abstract class AbstractSummaryTablePanel implements ChallengeView {
    public AbstractSummaryTablePanel() {
    }
    public abstract void setPanelEnabled(boolean on);
    public abstract void closeSourceViewer();
    public abstract void updateView(ResultDisplayType displayType);
    public abstract UIComponent getTable();
}
