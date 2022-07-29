package com.topcoder.client.mpsqasApplet.model;

import com.topcoder.netCommon.mpsqas.ApplicationInformation;

public abstract class ViewApplicationRoomModel extends Model {

    public abstract void setApplicationInformation(ApplicationInformation info);

    public abstract ApplicationInformation getApplicationInformation();
}
