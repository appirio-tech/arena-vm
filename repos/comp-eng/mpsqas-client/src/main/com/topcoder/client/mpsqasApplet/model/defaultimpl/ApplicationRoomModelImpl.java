package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.ApplicationRoomModel;

import java.util.ArrayList;

/**
 * Default implementation of ApplicationRoomModel.
 *
 * @author mitalub
 */
public class ApplicationRoomModelImpl extends ApplicationRoomModel {

    private int type;

    public void init() {
        type = -1;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
