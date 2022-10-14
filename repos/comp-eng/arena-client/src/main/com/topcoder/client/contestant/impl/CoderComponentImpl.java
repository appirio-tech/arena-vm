/*
* User: Michael Cervantes
* Date: Aug 16, 2002
* Time: 3:23:33 PM
*/
package com.topcoder.client.contestant.impl;

import java.util.LinkedList;
import java.util.List;

import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.view.EventService;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.Language;

class CoderComponentImpl implements CoderComponent {

    private EventService eventService;
    private ProblemComponentModelImpl component;
    private Integer points;
    private Integer status;
    private Integer language;
    private Integer passedSystemTests;
    private Coder coder;

    private String sourceCode;

    private final List listeners = new LinkedList();
    
    CoderComponentImpl() {
    }

    CoderComponentImpl(ProblemComponentModelImpl component, int languageID, int points, int status, Coder coder, Integer passedSystemTests, EventService eventService) {
        this.component = component;
        this.points = new Integer(points);
        this.status = new Integer(status);
        this.coder = coder;
        this.eventService = eventService;
        this.language = new Integer(languageID);
        this.passedSystemTests = passedSystemTests;
    }

    public ProblemComponentModel getComponent() {
        return component;
    }

    public synchronized Integer getPoints() {
        return points;
    }

    public synchronized Integer getStatus() {
        return status;
    }
    
    public synchronized Integer getLanguageID() {
        return language;
    }

    public Coder getCoder() {
        return coder;
    }

    synchronized void setPoints(Integer points) {
        this.points = points;
        notifyListeners();
    }

    public synchronized void setLanguage(Integer languageID) {
        this.language = languageID;
        notifyListeners();
    }

    synchronized void setStatus(Integer status) {
        this.status = status;
        notifyListeners();
    }

    public synchronized boolean hasSourceCode() {
        return getSourceCodeLanguage() != null && sourceCode != null;
    }

    public synchronized String getSourceCode() {
        return sourceCode;
    }
    
    synchronized void setSourceCode(int languageID, String sourceCode) {
        if (sourceCode != null && listeners.size() > 0 && sourceCode.length() > 0) {
//            language = BaseLanguage.getLanguage(languageID);
            setSourceCodeLanguage(languageID);
            this.sourceCode = sourceCode;
            notifyListeners();
        } else {
            this.sourceCode = null;
//            language = null;
        }
    }

    protected void setSourceCodeLanguage(int languageID) {
        language = new Integer(languageID);
    }
    
    public Language getSourceCodeLanguage() {
        return getLanguage();
    }

    public synchronized Language getLanguage() {
        if(language==null ) return null;
        return BaseLanguage.getLanguage(language.intValue());
    }

    public synchronized void addListener(CoderComponent.Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(CoderComponent.Listener listener) {
        listeners.remove(listener);
        if (listeners.size() == 0) {
            sourceCode = null;  // Free for GC
            //language = null;
        }
    }

    private synchronized void notifyListeners() {
        final Listener[] listeners = cloneListeners();
        try {
            eventService.invokeLater(new Runnable() {
                public void run() {
                    for (int i = listeners.length - 1; i >= 0; i--) {
                        listeners[i].coderComponentEvent(CoderComponentImpl.this);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized Listener[] cloneListeners() {
        return (Listener[]) listeners.toArray(new Listener[listeners.size()]);
    }

    public Integer getPassedSystemTests() {
        return passedSystemTests;
    }

    public void setPassedSystemTests(Integer passedSystemTests) {
        this.passedSystemTests = passedSystemTests;
    }

}
