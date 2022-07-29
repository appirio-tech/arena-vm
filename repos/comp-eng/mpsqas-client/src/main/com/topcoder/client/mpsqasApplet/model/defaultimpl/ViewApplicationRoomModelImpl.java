package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.ViewApplicationRoomModel;
import com.topcoder.netCommon.mpsqas.ApplicationInformation;

import java.util.ArrayList;

/**
 * Default implementation of View Application Room model.
 *
 * @author mitalub
 */
public class ViewApplicationRoomModelImpl extends ViewApplicationRoomModel {

    ApplicationInformation info;

    public void init() {
        info = null;
    }

    public void setApplicationInformation(ApplicationInformation info) {
        this.info = info;
    }

    public ApplicationInformation getApplicationInformation() {
        return info;
    }
}
