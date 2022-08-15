/**
 * A problem-specific broadcast message.
 * 
 * @author Michael Cervantes (emcee)
 * @since Apr 6, 2002
 */
package com.topcoder.netCommon.contestantMessages;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a broadcast message from the admins to all registered users in a round. The broadcast message is intented to
 * be about a specific problem in the round.
 * 
 * @author Michael Cervantes (emcee)
 * @version $Id: ComponentBroadcast.java 72093 2008-08-05 07:34:40Z qliu $
 */
public class ComponentBroadcast extends RoundBroadcast {
    /** Represents the return type of the problem component. */
    private String returnType;

    /** Represents the method signature of the problem component. */
    private String methodSignature;

    /** Represents the class name of the problem component. */
    private String className;

    /** Represents the maximum score of the problem component. */
    private int pointValue;

    /** Represents the division of the problem component. */
    private int division;

    /** Represents the ID of the problem component. */
    private int componentID;

    public int compareTo(Object o) {
        int sc = super.compareTo(o);
        if (sc != 0)
            return sc;
        ComponentBroadcast pb = (ComponentBroadcast) o;
        if (pb.division != division)
            return division - pb.division;
        return pb.componentID - componentID;
    }

    /**
     * Gets the return type of the method of the problem component.
     * 
     * @return the return type of the method.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Sets the return type of the method of the problem component.
     * 
     * @param returnType the return type of the method.
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the ID of the problem component.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Sets the ID of the problem component.
     * 
     * @param componentID the ID of the problem component.
     */
    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    /**
     * Gets the division of the problem component.
     * 
     * @return the division of the problem component.
     */
    public int getDivision() {
        return division;
    }

    /**
     * Sets the division of the problem component.
     * 
     * @param division the division of the problem component.
     */
    public void setDivision(int division) {
        this.division = division;
    }

    public int getType() {
        return ContestConstants.BROADCAST_TYPE_ADMIN_COMPONENT;
    }

    /**
     * Gets the method signature of the problem component.
     * 
     * @return the method signature of the problem component.
     */
    public String getMethodSignature() {
        return methodSignature;
    }

    /**
     * Sets the method signature of the problem component.
     * 
     * @param methodSignature the method signature of the problem component.
     */
    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    /**
     * Gets the class name of the problem component.
     * 
     * @return the class name of the problem component.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name of the problem component.
     * 
     * @param className the class name of the problem component.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the maximum score of the problem component.
     * 
     * @return the maximum score of the problem component.
     */
    public int getPointValue() {
        return pointValue;
    }

    /**
     * Gets the maximum score of the problem component.
     * 
     * @param pointValue the maximum score of the problem component.
     */
    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(componentID);
        writer.writeInt(pointValue);
        writer.writeString(className);
        writer.writeString(methodSignature);
        writer.writeString(returnType);
        writer.writeInt(division);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        componentID = reader.readInt();
        pointValue = reader.readInt();
        className = reader.readString();
        methodSignature = reader.readString();
        returnType = reader.readString();
        division = reader.readInt();
    }

    /**
     * Creates a new instance of <code>ComponentBroadcast</code>. It is required by custom serialization.
     */
    public ComponentBroadcast() {
    }

    /**
     * Creates a new instance of <code>ComponentBroadcast</code>. The information is cloned from another
     * broadcast.
     * 
     * @param other the other problem-specific broadcast.
     */
    public ComponentBroadcast(ComponentBroadcast other) {
        super(other.getTime(), other.getMessage(), other.getRoundID(), null);
        componentID = other.componentID;
    }

    /**
     * Creates a new instance of <code>ComponentBroadcast</code>. The time, the message text, the round ID and the name
     * of the round are given. All problem component specific information is given as well.
     * 
     * @param time the time of the broadcast.
     * @param message the message text of the broadcast.
     * @param roundID the ID of the round to be broadcasted.
     * @param roundName the name of the round to be broadcasted.
     * @param problemID the ID of the problem component.
     * @param division the division of the problem component.
     * @param pointValue the maximum score of the problem component.
     * @param returnType the return type of the method.
     * @param methodSig the method signature of the problem component.
     * @param className the class name of the problem component.
     */
    public ComponentBroadcast(long time, String message, int roundID, String roundName, int problemID, int division,
        int pointValue, String returnType, String methodSig, String className) {
        super(time, message, roundID, roundName);
        this.returnType = returnType;
        methodSignature = methodSig;
        this.className = className;
        this.pointValue = pointValue;
        this.division = division;
        this.componentID = problemID;
    }

    public String toString() {
        return "component_broadcast(" + getTime() + ", " + getMessage() + ", " + getRoundID() + ", " + getRoundName()
            + ", " + getComponentID() + ", " + getDivision() + ", " + getPointValue() + ", " + getClassName() + ", "
            + getMethodSignature() + ")";
    }

    private boolean strEq(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    public boolean equals(Object r) {
        if (r != null && r instanceof ComponentBroadcast) {
            ComponentBroadcast rhs = (ComponentBroadcast) r;
            return rhs.getType() == getType() && rhs.getTime() == getTime() && rhs.getRoundID() == getRoundID()
                && rhs.getComponentID() == getComponentID() && rhs.getDivision() == getDivision()
                && rhs.getPointValue() == getPointValue() && strEq(getRoundName(), rhs.getRoundName())
                && strEq(getClassName(), rhs.getClassName()) && strEq(getMethodSignature(), rhs.getMethodSignature())
                && strEq(getReturnType(), rhs.getReturnType()) && strEq(getMessage(), rhs.getMessage());
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode() * division;
    }
}
