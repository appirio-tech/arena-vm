package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.GeneralContestInfoPanelModel;
import com.topcoder.netCommon.mpsqas.ContestInformation;

public class GeneralContestInfoPanelModelImpl
        extends GeneralContestInfoPanelModel {

    private ContestInformation contestInfo;

    public void init() {
    }

    public void setContestInformation(ContestInformation contestInfo) {
        this.contestInfo = contestInfo;
    }

    public ContestInformation getContestInformation() {
        return contestInfo;
    }
}
