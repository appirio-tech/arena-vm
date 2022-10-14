package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.model.ViewContestRoomModel;
import com.topcoder.netCommon.mpsqas.ContestInformation;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;

import java.util.ArrayList;

/**
 * Default implmentation of ViewContestRoomModel
 *
 * @author mitalub
 */
public class ViewContestRoomModelImpl extends ViewContestRoomModel {

    private ContestInformation info;
    private ComponentModel[] componentModels;
    private int[] panelTypes;

    public void init() {
        info = null;
        componentModels = null;
        panelTypes = null;
    }

    public ContestInformation getContestInformation() {
        return info;
    }

    public void setContestInformation(ContestInformation info) {
        this.info = info;
    }
}
