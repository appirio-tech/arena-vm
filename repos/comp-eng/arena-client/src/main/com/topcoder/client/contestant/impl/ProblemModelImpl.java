/*
* User: Michael Cervantes
* Date: Aug 28, 2002
* Time: 11:38:32 PM
*/
package com.topcoder.client.contestant.impl;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.problem.*;
import com.topcoder.shared.language.Language;
import com.topcoder.client.contestant.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.client.render.ProblemRenderer;

import java.util.*;
import java.util.List;

class ProblemModelImpl implements ProblemModel {

    private Problem serverProblemObject;
    private RoundModelImpl roundModel;
    private Integer division;
    private String name;
    private Long problemID;
    private Integer problemTypeID;
    private ProblemComponentModelImpl[] components;
    private List listeners = new LinkedList();
    private EventService eventService;

    public ProblemModelImpl(Long problemID, RoundModelImpl roundModel, Integer division, String name, Integer problemTypeID,
            EventService eventService) {
        this.problemID = problemID;
        this.roundModel = roundModel;
        this.division = division;
        this.name = name;
        this.problemTypeID = problemTypeID;
        this.eventService = eventService;
    }

    public ProblemComponentModel[] getComponents() {
        return components;
    }

    public ProblemComponentModel getPrimaryComponent() {
        for (int i = 0; i < components.length; i++) {
            if (components[i].getComponentTypeID().intValue() == ContestConstants.COMPONENT_TYPE_MAIN) {
                return components[i];
            }
        }
        return null;
    }

    public Long getProblemID() {
        return problemID;
    }

    public String getName() {
        return name;
    }

    public RoundModel getRound() {
        return roundModel;
    }

    public Integer getDivision() {
        return division;
    }

    public Integer getProblemType() {
        return problemTypeID;
    }

    public boolean hasComponents() {
        return components != null && components.length > 0;
    }

    public boolean hasProblemStatement() {
//        return serverProblemObject != null && serverProblemObject.getProblemText().length() > 0;
        return serverProblemObject != null;
    }

    public boolean hasIntro() {
        return serverProblemObject.getProblemText() != null;
    }

    public String getIntro() {
        return serverProblemObject.getProblemText();
    }


    public String getProblemStatement() {
        return serverProblemObject.getProblemText();
    }

    public String toHTML(Language language) {
        String html = "";
        try {
            html = new ProblemRenderer(serverProblemObject).toHTML(language);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return html;
    }

    public Problem getProblem() {
        return serverProblemObject;
    }

    synchronized void notifyListeners() {
        final Listener[] listeners = cloneListeners();
        try {
            eventService.invokeLater(new Runnable() {
                public void run() {
                    for (int i = listeners.length - 1; i >= 0; i--) {
                        listeners[i].updateProblemModel(ProblemModelImpl.this);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized void notifyListenersReadOnly() {
        final Listener[] listeners = cloneListeners();
        try {
            eventService.invokeLater(new Runnable() {
                public void run() {
                    for (int i = listeners.length - 1; i >= 0; i--) {
                        listeners[i].updateProblemModelReadOnly(ProblemModelImpl.this);
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


    public synchronized void addListener(Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(Listener listener) {
        listeners.remove(listener);
        if (listeners.size() == 0) {
            serverProblemObject = null; // free up the memory
            for (int i = 0; i < components.length; i++) {
                ProblemComponentModelImpl component = components[i];
                component.unsetServerComponentObject(); // free up the memory
            }
        }
    }

    synchronized void setComponents(ProblemComponentModelImpl[] components) {
        this.components = components;
        notifyListeners();
    }

    synchronized void setServerProblemObject(Problem serverProblemObject) {
        if (listeners.size() > 0) {
            this.serverProblemObject = serverProblemObject;
            notifyListeners();
        }
    }

    synchronized void setReadOnlyServerProblemObject(Problem serverProblemObject) {
        if (listeners.size() > 0) {
            this.serverProblemObject = serverProblemObject;
            notifyListenersReadOnly();
        }
    }
}
