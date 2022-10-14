package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.WebServiceRoomModel;
import com.topcoder.netCommon.mpsqas.WebServiceInformation;

import java.util.HashMap;

/**
 * Default implementation of the web service room model.
 *
 * @author mitalub
 */
public class WebServiceRoomModelImpl extends WebServiceRoomModel {

    private WebServiceInformation webServiceInformation;
    private HashMap classViews;
    private boolean editable;

    public void init() {
        classViews = new HashMap();
        webServiceInformation = null;
        editable = true;
    }

    public void setWebServiceInformation(WebServiceInformation webServiceInfo) {
        this.webServiceInformation = webServiceInfo;
    }

    public WebServiceInformation getWebServiceInformation() {
        return webServiceInformation;
    }

    public void setClassViews(HashMap classViews) {
        this.classViews = classViews;
    }

    public HashMap getClassViews() {
        return classViews;
    }

    public void setIsEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }
}
