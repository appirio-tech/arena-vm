package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.netCommon.contest.Answer;

public interface QuestionPanel {
    Answer getAnswer();
    UIComponent getPanel();
}
