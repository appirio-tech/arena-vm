package com.topcoder.client.mpsqasApplet.model;

import java.util.HashMap;

import com.topcoder.netCommon.mpsqas.WebServiceInformation;

/**
 * Abstract model for the web service room.
 *
 * @author mitalub
 */
public abstract class WebServiceRoomModel extends Model {

    public abstract void setWebServiceInformation(
            WebServiceInformation webServiceInformation);

    public abstract WebServiceInformation getWebServiceInformation();

    public abstract void setClassViews(HashMap classViews);

    public abstract HashMap getClassViews();

    public abstract void setIsEditable(boolean editable);

    public abstract boolean isEditable();
}
