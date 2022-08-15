package com.topcoder.client.mpsqasApplet.model;

import com.topcoder.netCommon.mpsqas.ContestInformation;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;

/**
 * An abstract class definining the methods in a View Contest Room Model.
 *
 * @author mitalub
 */
public abstract class ViewContestRoomModel extends Model {

    public abstract ContestInformation getContestInformation();

    public abstract void setContestInformation(ContestInformation info);
}
