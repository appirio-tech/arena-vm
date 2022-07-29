/*
* User: Michael Cervantes
* Date: Aug 16, 2002
* Time: 3:23:21 PM
*/
package com.topcoder.client.contestant.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.netCommon.contestantMessages.response.data.CoderComponentItem;
import com.topcoder.netCommon.contestantMessages.response.data.LongCoderComponentItem;

class CoderImpl implements Coder {

    private String handle;
    private Integer rating;
    private CoderComponentImpl[] components;
    private Double score;
    private Double finalScore;
    private int userType;
    private ArrayList memberNames = null;

    CoderImpl(String handle, int rating, double score, int userType, ArrayList memberNames) {
        this.handle = handle;
        this.rating = new Integer(rating);
        this.score = new Double(score);
        this.userType = userType;
        this.memberNames = memberNames;
    }

    public ArrayList getMemberNames() {
        return memberNames;
    }

    private List listeners = new LinkedList();

    public synchronized void addListener(Coder.Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(Coder.Listener listener) {
        listeners.remove(listener);
    }

    private synchronized void notifyListeners() {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            Listener listener = (Listener) listeners.get(i);
            listener.coderEvent(this);
        }
    }

    public Integer getRating() {
        return rating;
    }

    public String getHandle() {
        return handle;
    }

    public synchronized boolean hasComponents() {
        return components != null;
    }

    public synchronized CoderComponent[] getComponents() {
        return components;
    }

    public CoderComponent getComponent(Long componentID) {
        return getCoderComponent(componentID);
    }

    synchronized CoderComponentImpl getCoderComponent(Long componentID) {
        for (int i = 0; i < components.length; i++) {
            CoderComponentImpl coderComponent = components[i];
            if (coderComponent.getComponent().getID().equals(componentID)) {
                return coderComponent;
            }
        }
        throw new IllegalArgumentException("Bad component ID #" + componentID);
    }

    synchronized void setComponents(CoderComponentImpl[] components) {
        this.components = components;
        Arrays.sort(components, new Comparator() {
            public int compare(Object o1, Object o2) {
                CoderComponentImpl c1 = (CoderComponentImpl) o1;
                CoderComponentImpl c2 = (CoderComponentImpl) o2;
                return c1.getComponent().getPoints().compareTo(c2.getComponent().getPoints());
            }
        });
        notifyListeners();
    }

    void updateComponent(CoderComponentItem coderComponentItem) {
        for (int i = 0; i < components.length; i++) {
            CoderComponentImpl coderComponent = components[i];
            if (coderComponent.getComponent().getID().equals(coderComponentItem.getComponentID())) {
                coderComponent.setPoints(coderComponentItem.getPoints());
                coderComponent.setStatus(coderComponentItem.getStatus());
                coderComponent.setLanguage(coderComponentItem.getLanguage());
                coderComponent.setPassedSystemTests(coderComponentItem.getPassedSystemTest());
                updateLongComponentIfNeeded(coderComponent, coderComponentItem);
                notifyListeners();
                return;
            }
        }
        throw new IllegalArgumentException("Couldn't find matching component: " + coderComponentItem);
    }
    
    void updateComponentFromTable(CoderComponentItem coderComponentItem) {
        for (int i = 0; i < components.length; i++) {
            CoderComponentImpl coderComponent = components[i];
            if (coderComponent.getComponent().getID().equals(coderComponentItem.getComponentID())) {
                coderComponent.setPoints(coderComponentItem.getPoints());
                coderComponent.setStatus(coderComponentItem.getStatus());
                coderComponent.setLanguage(coderComponentItem.getLanguage());
                coderComponent.setPassedSystemTests(coderComponentItem.getPassedSystemTest());
                updateLongComponentIfNeeded(coderComponent, coderComponentItem);
                //notifyListeners();
                return;
            }
        }
        throw new IllegalArgumentException("Couldn't find matching component: " + coderComponentItem);
    }

    private void updateLongComponentIfNeeded(CoderComponentImpl coderComponent, CoderComponentItem coderComponentItem) {
        if  (coderComponentItem instanceof LongCoderComponentItem) {
            LongCoderComponentItem lcci = (LongCoderComponentItem) coderComponentItem;
            LongCoderComponentImpl lcc = (LongCoderComponentImpl) coderComponent;
            lcc.setExampleLastLanguage(lcci.getExampleLastLanguage());
            lcc.setExampleLastSubmissionTime(lcci.getExampleLastSubmissionTime());
            lcc.setExampleSubmissionCount(lcci.getExampleSubmissionCount());
            lcc.setLastSubmissionTime(lcci.getLastSubmissionTime());
            lcc.setSubmissionCount(lcci.getSubmissionCount());
        }
    }

    public Double getScore() {
        return score;
    }

    void setScore(double score) {
        this.score = new Double(score);
        notifyListeners();
    }
    
    void setScoreFromTable(double score) {
        this.score = new Double(score);
        //notifyListeners();
    }


    public int getUserType() {
        return userType;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }
}
