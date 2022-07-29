package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.MainApplicationRoomModel;

import java.util.ArrayList;

/**
 * Default implementation of MainApplicationRoomModel.
 *
 * @author mitalub
 */
public class MainApplicationRoomModelImpl extends MainApplicationRoomModel {

    public void init() {
        applications = new ArrayList();
    }

    public void setApplications(ArrayList arraylist) {
        applications = arraylist;
    }

    public ArrayList getApplications() {
        return applications;
    }

    private ArrayList applications;
}
