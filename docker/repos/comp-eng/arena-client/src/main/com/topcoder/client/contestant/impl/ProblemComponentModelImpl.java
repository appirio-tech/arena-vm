/*
 * User: Michael Cervantes
 * Date: Aug 28, 2002
 * Time: 11:38:43 PM
 */
package com.topcoder.client.contestant.impl;

import com.topcoder.client.contestant.*;
import com.topcoder.shared.problem.*;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;

//import java.util.*;

class ProblemComponentModelImpl implements ProblemComponentModel {

    private Long componentID;
    private Integer componentType;
    private ProblemModel problemModel;
    private Double points;
    private String className;
    private ComponentChallengeData componentChallengeData;

//    private List listeners = new LinkedList();
    private ProblemComponent serverComponentObject;

    public ProblemComponentModelImpl(Long componentID, Integer componentType, Double points, String className, ComponentChallengeData componentChallengeData, ProblemModel problemModel) {
        this.componentID = componentID;
        this.componentType = componentType;
        this.problemModel = problemModel;
        this.points = points;
        this.className = className;
        this.componentChallengeData = componentChallengeData;
    }

    synchronized void setServerComponentObject(ProblemComponent serverComponentObject) {
        this.serverComponentObject = serverComponentObject;
    }

    synchronized void unsetServerComponentObject() {
        this.serverComponentObject = null;
    }

    public ComponentChallengeData getComponentChallengeData() {
        return componentChallengeData;
    }

    public Long getID() {
        return componentID;
    }

    public ProblemComponent getComponent() {
        if (serverComponentObject == null) {
            System.out.println("ProblemComponentModel.getComponent() called, but serverComponentObject is null.");
        }
        return serverComponentObject;
    }

    public Integer getComponentTypeID() {
        return componentType;
    }

    public ProblemModel getProblem() {
        return problemModel;
    }

    public Double getPoints() {
        return points;
    }

    public String getClassName() {
        return className;
    }


    public boolean hasSignature() {
        return serverComponentObject != null;
    }

    public String getMethodName() {
        return serverComponentObject.getMethodName();
    }

    public DataType getReturnType() {
        return serverComponentObject.getReturnType();
    }

    public DataType[] getParamTypes() {
        return serverComponentObject.getParamTypes();
    }

    public String[] getParamNames() {
        return serverComponentObject.getParamNames();
    }

    public boolean hasStatement() {
        return serverComponentObject != null;
    }

    public boolean hasIntro() {
        return serverComponentObject.getIntro() != null;
    }

    public Element getIntro() {
        return serverComponentObject.getIntro();
    }

    public boolean hasSpec() {
        return serverComponentObject.getSpec() != null;
    }


    public Element getSpec() {
        return serverComponentObject.getSpec();
    }

    public boolean hasNotes() {
        return serverComponentObject.getNotes().length > 0;
    }

    public Element[] getNotes() {
        return serverComponentObject.getNotes();
    }

    public boolean hasConstraints() {
        return serverComponentObject.getConstraints().length > 0;
    }


    public Constraint[] getConstraints() {
        return serverComponentObject.getConstraints();
    }

    public boolean hasTestCases() {
        return serverComponentObject.getTestCases().length > 0;
    }

    public TestCase[] getTestCases() {
        return serverComponentObject.getTestCases();
    }

//    public WebService[] getWebServices() {
//        return new WebService[0];
//    }

    public boolean hasDefaultSolution() {
        return serverComponentObject.getDefaultSolution().length() > 0;
    }

    public String getDefaultSolution() {
        return serverComponentObject.getDefaultSolution();
    }


//    synchronized void notifyListeners() {
//        for (Iterator listenerIterator = listeners.iterator(); listenerIterator.hasNext();) {
//            Listener listener = (Listener) listenerIterator.next();
//            listener.updateProblemComponentModel(this);
//        }
//    }
//
//    public synchronized void addListener(ProblemComponentModel.Listener listener) {
//        if (!listeners.contains(listener)) {
//            listeners.add(listener);
//        }
//    }
//
//    public synchronized void removeListener(ProblemComponentModel.Listener listener) {
//        listeners.remove(listener);
//        if (listeners.size() == 0) {
//            serverComponentObject = null; // free up the memory
//        }
//    }

}
