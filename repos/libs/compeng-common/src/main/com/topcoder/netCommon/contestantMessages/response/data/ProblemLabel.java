package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

import java.util.Arrays;

/**
 * Defines non-sensitive information of a problem assigned to a division of a round. The sensitive information such as
 * problem statement, example test cases is not available, and should be available to users assigned to the contest room
 * after coding phase only.<br>
 * Note: A problem must have one and only one problem component with the type of
 * <code>ContestConstants.COMPONENT_TYPE_MAIN</code>.
 * 
 * @author Lars Backstrom
 * @version $Id: ProblemLabel.java 72424 2008-08-20 08:06:01Z qliu $
 */
public final class ProblemLabel implements Serializable, Cloneable, CustomSerializable {
    /** Represents the name of the problem. */
    private String name;

    /** Represents the ID of the problem. */
    private Long problemID;

    /** Represents the division of the round. */
    private Integer divisionID;

    /** Represents the problem components belonging to the problem. */
    private ComponentLabel[] components;

    /** Represents the type of the problem. */
    private Integer problemTypeID;

    /**
     * Creates a new instance of <code>ProblemLabel</code>. It is required by custom serialization.
     */
    public ProblemLabel() {
    }

    /**
     * Creates a new instance of <code>ProblemLabel</code>. There is no copy.
     * 
     * @param name the name of the problem.
     * @param problemID the ID of the problem.
     * @param divisionID the division of the round.
     * @param problemTypeID the type of the problem.
     * @param components the problem components belonging to the problem.
     * @see #getProblemTypeID()
     */
    public ProblemLabel(String name, long problemID, int divisionID, int problemTypeID, ComponentLabel[] components) {
        this.name = name;
        this.problemID = new Long(problemID);
        this.divisionID = new Integer(divisionID);
        this.components = components;
        this.problemTypeID = new Integer(problemTypeID);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(problemID.intValue());
        writer.writeString(name);
        writer.writeInt(divisionID.intValue());
        writer.writeObjectArray(components);
        writer.writeInt(problemTypeID.intValue());
    }

    public void customReadObject(CSReader reader) throws IOException {
        problemID = new Long(reader.readInt());
        name = reader.readString();
        divisionID = new Integer(reader.readInt());
        components = (ComponentLabel[]) reader.readObjectArray(ComponentLabel.class);
        problemTypeID = new Integer(reader.readInt());
    }

    /**
     * Gets the name of the problem.
     * 
     * @return the problem name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the ID of the problem.
     * 
     * @return the problem ID.
     */
    public Long getProblemID() {
        return problemID;
    }

    /**
     * Gets the type of the problem.
     * 
     * @return the type of the problem.
     * @see ContestConstants#SINGLE_PROBLEM_TYPE_ID
     * @see ContestConstants#TEAM_PROBLEM_TYPE_ID
     */
    public Integer getProblemTypeID() {
        return problemTypeID;
    }

    /**
     * Gets the problem components belonging to the problem. There is no copy.
     * 
     * @return the problem components.
     */
    public ComponentLabel[] getComponents() {
        return components;
    }

    /**
     * Gets the primary problem component of the problem. The primary problem component has the problem component type
     * of <code>ContestConstants.COMPONENT_TYPE_MAIN</code>. If there is no primary problem component,
     * <code>null</code> is returned (it should never happen).
     * 
     * @return the primary problem component.
     */
    public ComponentLabel getPrimaryComponent() {
        for (int i = 0; i < components.length; i++) {
            if (components[i].getComponentTypeID().equals(new Integer(ContestConstants.COMPONENT_TYPE_MAIN))) {
                return components[i];
            }
        }
        return null;
    }

    public String toString() {
        return "ProblemLabel[problemID=" + problemID + ",name=" + name + ",divisionID=" + divisionID + ",problemTypeID="
            + problemTypeID + ", components=" + (components == null ? null : Arrays.asList(components))+ "]";
    }
}
