package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.response.RoundStatsResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the statistics of a problem component of a user in a 'Weakest Link' round. The handle of the user is not
 * stored in this class, but in the corresponding <code>RoundStatsResponse</code>.
 * 
 * @author Qi Liu
 * @version $Id: RoundStatsProblem.java 72343 2008-08-15 06:09:22Z qliu $
 * @see RoundStatsResponse
 */
public final class RoundStatsProblem implements CustomSerializable {
    /** Represents the class name of the problem component. */
    private String className;

    /** Represents the final score which the user earns. */
    private double earnedPoints;

    /** Represents the maximum score of the problem component. */
    private double pointValue;

    /** Represents the status of the solution to the problem component. */
    private String statusString;

    /** Represents the time of the submission of the solution. */
    private String timeToSubmit;

    /** Represents the ID of the problem component. */
    private long componentId;

    /**
     * Creates a new instance of <code>RoundStatsProblem</code>. It is required by custom serialization.
     */
    public RoundStatsProblem() {
    }

    /**
     * Creates a new instance of <code>RoundStatsProblem</code>.
     * 
     * @param className the class name of the problem component.
     * @param earnedPoints the final score which the user earns.
     * @param pointValue the maximum score of the problem component.
     * @param statusString the status of the solution to the problem component.
     * @param timeToSubmit the time of the submission of the solution.
     * @param componentId the ID of the problem component.
     */
    public RoundStatsProblem(String className, double earnedPoints, double pointValue, String statusString,
        String timeToSubmit, long componentId) {
        this.className = className;
        this.earnedPoints = earnedPoints;
        this.pointValue = pointValue;
        this.statusString = statusString;
        this.timeToSubmit = timeToSubmit;
        this.componentId = componentId;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(className);
        writer.writeDouble(earnedPoints);
        writer.writeDouble(pointValue);
        writer.writeString(statusString);
        writer.writeString(timeToSubmit);
        writer.writeLong(componentId);
    }

    public void customReadObject(CSReader reader) throws IOException {
        className = reader.readString();
        earnedPoints = reader.readDouble();
        pointValue = reader.readDouble();
        statusString = reader.readString();
        timeToSubmit = reader.readString();
        componentId = reader.readLong();
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
     * Gets the final score which the user earns.
     * 
     * @return the final score which the user earns.
     */
    public double getEarnedPoints() {
        return earnedPoints;
    }

    /**
     * Gets the maximum score of the problem component.
     * 
     * @return the maximum score of the problem component.
     */
    public double getPointValue() {
        return pointValue;
    }

    /**
     * Gets the status of the solution to the problem component.
     * 
     * @return the status of the solution to the problem component.
     */
    public String getStatusString() {
        return statusString;
    }

    /**
     * Gets the time of the submission of the solution.
     * 
     * @return the time of the submission of the solution.
     */
    public String getTimeToSubmit() {
        return timeToSubmit;
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the ID of the problem component.
     */
    public long getComponentId() {
        return componentId;
    }

    public String toString() {
        return "className=" + className + ", earnedPoints=" + earnedPoints + ", pointValue=" + pointValue
            + ", statusString=" + statusString + ", timeToSubmit=" + timeToSubmit + ", componentId=" + componentId;
    }

}
