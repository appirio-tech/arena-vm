package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.LoginRoomModel;

/**
 * Implementation of Login Room model.
 *
 * @author mitalub
 */
public class LoginRoomModelImpl extends LoginRoomModel {

    private String status;

    public void init() {
        status = "    ";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
