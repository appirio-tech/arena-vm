package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.controller.component.GeneralContestInfoPanelController;
import com.topcoder.client.mpsqasApplet.model.component.GeneralContestInfoPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.GeneralContestInfoPanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;

/**
 * Default implementation of GeneralContestInfoPanelController .
 *
 * @author mitalub
 */
public class GeneralContestInfoPanelControllerImpl
        extends GeneralContestInfoPanelController {

    GeneralContestInfoPanelModel model;
    GeneralContestInfoPanelView view;

    public void init() {
    }

    public void close() {
    }

    public void setModel(ComponentModel model) {
        this.model = (GeneralContestInfoPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (GeneralContestInfoPanelView) view;
    }

    public void processVerifyContest() {
        MainObjectFactory.getContestRequestProcessor().verifyContest();
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
