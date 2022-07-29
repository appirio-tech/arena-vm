package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.AdminProblemPanelModel;

import java.util.ArrayList;

/**
 * Default implementation of the admin problem panel model
 *
 * @author mitalub
 */
public class AdminProblemPanelModelImpl extends AdminProblemPanelModel {

    private boolean containsStatus;
    private int status;
    private ArrayList solutions;
    private ArrayList availableTesters;
    private ArrayList scheduledTesters;

    public void init() {
        containsStatus = true;
        status = -1;
        solutions = new ArrayList();
        availableTesters = new ArrayList();
        scheduledTesters = new ArrayList();
    }

    public void setContainsStatus(boolean containsStatus) {
        this.containsStatus = containsStatus;
    }

    public boolean containsStatus() {
        return containsStatus;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setSolutions(ArrayList solutions) {
        this.solutions = solutions;
    }

    public ArrayList getSolutions() {
        return solutions;
    }

    public void setAvailableTesters(ArrayList availableTesters) {
        this.availableTesters = availableTesters;
    }

    public ArrayList getAvailableTesters() {
        return availableTesters;
    }

    public void setScheduledTesters(ArrayList scheduledTesters) {
        this.scheduledTesters = scheduledTesters;
    }

    public ArrayList getScheduledTesters() {
        return scheduledTesters;
    }
}
