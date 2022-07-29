package com.topcoder.client.mpsqasApplet.model;

/**
 * An interface for the status messages model.
 *
 * @author mitalub
 */
public abstract class StatusModel extends Model {

    public abstract StringBuffer getStatusMessages();

    public abstract void setStatusMessages(StringBuffer statusMessages);
    
    /**
     * Clears the status messages
     */
    public abstract void clearStatusMessages();
}
