package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.ViewUserRoomModel;
import com.topcoder.netCommon.mpsqas.UserInformation;

import java.util.ArrayList;

/**
 * Default implementation of View User Room model.
 *
 * @author mitalub
 */
public class ViewUserRoomModelImpl extends ViewUserRoomModel {

    UserInformation info;

    public void init() {
        info = null;
    }

    public void setUserInformation(UserInformation info) {
        this.info = info;
    }

    public UserInformation getUserInformation() {
        return info;
    }
}
