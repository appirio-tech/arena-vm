package com.topcoder.client.mpsqasApplet.model;

import java.util.ArrayList;

public abstract class MainUserRoomModel extends Model {

    public abstract void setUsers(ArrayList users);

    public abstract ArrayList getUsers();
}
