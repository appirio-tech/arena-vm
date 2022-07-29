package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.MainUserRoomModel;

import java.util.ArrayList;

/**
 * Default implementation of MainUserRoomModel.
 */
public class MainUserRoomModelImpl extends MainUserRoomModel {

    public void init() {
        users = new ArrayList();
    }

    public void setUsers(ArrayList arraylist) {
        users = arraylist;
    }

    public ArrayList getUsers() {
        return users;
    }

    private ArrayList users;
}
