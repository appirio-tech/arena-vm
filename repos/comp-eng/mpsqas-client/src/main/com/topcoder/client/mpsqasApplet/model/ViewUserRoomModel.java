package com.topcoder.client.mpsqasApplet.model;

import com.topcoder.netCommon.mpsqas.UserInformation;

public abstract class ViewUserRoomModel extends Model {

    public abstract void setUserInformation(UserInformation info);

    public abstract UserInformation getUserInformation();
}
