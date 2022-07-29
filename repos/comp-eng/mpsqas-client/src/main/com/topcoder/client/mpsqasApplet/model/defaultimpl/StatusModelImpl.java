package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.StatusModel;

/**
 * An implementation of the status messages model.
 *
 * @author mitalub
 */
public class StatusModelImpl extends StatusModel {

    private StringBuffer statusMessages;

    public void init() {
        statusMessages = new StringBuffer();
    }

    public StringBuffer getStatusMessages() {
        return statusMessages;
    }

    public void setStatusMessages(StringBuffer statusMessages) {
        this.statusMessages = statusMessages;
    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.model.StatusModel#clearStatusMessages()
     */
    public void clearStatusMessages() {
        this.statusMessages = new StringBuffer();
    }
}
