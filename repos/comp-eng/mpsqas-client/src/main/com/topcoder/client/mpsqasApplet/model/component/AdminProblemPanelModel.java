package com.topcoder.client.mpsqasApplet.model.component;

import java.util.ArrayList;

/**
 * Abstract class defining methods in an admin problem panel model.
 *
 * @author mitalub
 */
public abstract class AdminProblemPanelModel extends ComponentModel {

    public abstract void setStatus(int status);

    public abstract int getStatus();

    public abstract void setSolutions(ArrayList solutions);

    public abstract ArrayList getSolutions();

    public abstract void setAvailableTesters(ArrayList availableTesters);

    public abstract ArrayList getAvailableTesters();

    public abstract void setScheduledTesters(ArrayList scheduledTesters);

    public abstract ArrayList getScheduledTesters();

    public abstract void setContainsStatus(boolean containsStatus);

    public abstract boolean containsStatus();
}
