package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.AssignComponentsResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateProblemsResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines non-sensitive information of a problem component assigned to a division of a round. The sensitive information
 * such as problem statement, example test cases is not available, and should be available to users assigned to the
 * contest room after coding phase only. The problem component can be compared according to the maximum score. 
 * 
 * @author Lars Backstrom
 * @version $Id: ComponentLabel.java 72424 2008-08-20 08:06:01Z qliu $
 * @see AssignComponentsResponse
 * @see CreateProblemsResponse
 * @see ProblemLabel
 */
public class ComponentLabel implements Serializable, Cloneable, CustomSerializable, Comparable {
    /** Represents the name of the class of the problem component. */
    private String className;

    /** Represents the maximum score of the problem component. */
    private Double pointValue;

    /** Represents the ID of the problem which the problem component belongs to. */
    private Long problemID;

    /** Represents the ID of the problem component. */
    private Long componentID;

    /** Represents the division of the round. */
    private Integer divisionID;

    /** Represents the type of the problem component. */
    private Integer componentTypeID;

    /** Represents the information to challenge the problem component. */
    private ComponentChallengeData componentChallengeData;

    /**
     * Creates a new instance of <code>ComponentLabel</code>. It is required by custom serialization.
     */
    public ComponentLabel() {
    }

    /**
     * Creates a new instance of <code>ComponentLabel</code>.
     * 
     * @param className the name of the class of the problem component.
     * @param problemID the ID of the problem which the problem component belongs to.
     * @param componentID the ID of the problem component.
     * @param pointValue the maximum score of the problem component.
     * @param divisionID the division of the round.
     * @param componentTypeID the type of the problem component.
     * @param componentChallengeData the information to challenge the problem component.
     * @see #getComponentTypeID()
     */
    public ComponentLabel(String className, long problemID, long componentID, double pointValue, int divisionID,
        int componentTypeID, ComponentChallengeData componentChallengeData) {
        this.className = className;
        this.problemID = new Long(problemID);
        this.componentID = new Long(componentID);
        this.pointValue = new Double(pointValue);
        this.divisionID = new Integer(divisionID);
        this.componentTypeID = new Integer(componentTypeID);
        this.componentChallengeData = componentChallengeData;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(problemID.intValue());
        writer.writeString(className);
        writer.writeInt(componentID.intValue());
        writer.writeDouble(pointValue.doubleValue());
        writer.writeInt(divisionID.intValue());
        writer.writeInt(componentTypeID.intValue());
        writer.writeObject(componentChallengeData);
    }

    public void customReadObject(CSReader reader) throws IOException {
        problemID = new Long(reader.readInt());
        className = reader.readString();
        componentID = new Long(reader.readInt());
        pointValue = new Double(reader.readDouble());
        divisionID = new Integer(reader.readInt());
        componentTypeID = new Integer(reader.readInt());
        componentChallengeData = (ComponentChallengeData) reader.readObject();
    }

    /**
     * Gets the name of the class of the problem component.
     * 
     * @return the class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets the ID of the problem which the problem component belongs to.
     * 
     * @return the problem ID.
     */
    public Long getProblemID() {
        return problemID;
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the problem component ID.
     */
    public Long getComponentID() {
        return componentID;
    }

    /**
     * Gets the maximum score of the problem component.
     * 
     * @return the maximum score of the problem component.
     */
    public Double getPointValue() {
        return pointValue;
    }

    /**
     * Gets the division of the round.
     * 
     * @return the division of the round.
     */
    public Integer getDivisionID() {
        return divisionID;
    }

    /**
     * Gets the type of the problem component.
     * 
     * @return the type of the problem component.
     * @see ContestConstants#COMPONENT_TYPE_MAIN
     */
    public Integer getComponentTypeID() {
        return componentTypeID;
    }

    /**
     * Gets the information to challenge the problem component.
     * 
     * @return the information to challenge the problem component.
     */
    public ComponentChallengeData getComponentChallengeData() {
        return componentChallengeData;
    }

    public String toString() {
        return "ProblemLabel[problemID=" + problemID + ",componentID=" + componentID + ",className=" + className
            + ",points=" + pointValue + ",divisionID=" + divisionID + ",componentTypeID=" + componentTypeID + "]";
    }

    public int compareTo(Object o) {
        ComponentLabel componentLabel = (ComponentLabel) o;
        double diff = pointValue.doubleValue() - componentLabel.getPointValue().doubleValue();
        int result;
        if (diff < 0) {
            result = -1;
        } else if (diff > 0) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }
}
