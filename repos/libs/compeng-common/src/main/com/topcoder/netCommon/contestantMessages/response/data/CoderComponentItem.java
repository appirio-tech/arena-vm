/*
 * User: Michael Cervantes Date: Aug 20, 2002 Time: 12:18:24 AM
 */
package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.UpdateCoderComponentResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the status of a solution for a problem component. The status also includes the score and optionally the
 * number of passed system tests.<br>
 * Note: The user information is not included in this item. Such information is included in the response or in other
 * response data.
 * 
 * @author Michael Cervantes
 * @version $Id: CoderComponentItem.java 72424 2008-08-20 08:06:01Z qliu $
 * @see CoderItem
 * @see UpdateCoderComponentResponse
 */
public class CoderComponentItem implements CustomSerializable, Serializable {
    /** Represents the ID of the problem component. */
    private Long componentID;

    /** Represents the current score of the solution, multiplied by 100. */
    private Integer points;

    /** Represents the status of the solution. */
    private Integer status;

    /** Represents the ID of the programming language of the solution. */
    private Integer language;

    /** Represents the number of passed system tests. It can be <code>null</code>. */
    private Integer passedSystemTests;

    /**
     * Creates a new instance of <code>CoderComponentItem</code>. It is required by custom serialization.
     */
    public CoderComponentItem() {
    }

    /**
     * Creates a new instance of <code>CoderComponentItem</code>. The programming language is set as Java, and the
     * number of passed system test cases is unset.
     * 
     * @param componentID the ID of the problem component.
     * @param points the current score of the solution, multiplied by 100.
     * @param status the status of the solution.
     * @see #getStatus()
     */
    public CoderComponentItem(long componentID, int points, int status) {
        this(componentID, points, status, 1);
    }

    /**
     * Creates a new instance of <code>CoderComponentItem</code>. The number of passed system test cases is unset.
     * 
     * @param componentID the ID of the problem component.
     * @param points the current score of the solution, multiplied by 100.
     * @param status the status of the solution.
     * @param language the ID of the programming language.
     * @see #getStatus()
     */
    public CoderComponentItem(long componentID, int points, int status, int language) {
        this.componentID = new Long(componentID);
        this.points = new Integer(points);
        this.status = new Integer(status);
        this.language = new Integer(language);
    }

    /**
     * Creates a new instance of <code>CoderComponentItem</code>.
     * 
     * @param componentID the ID of the problem component.
     * @param points the current score of the solution, multiplied by 100.
     * @param status the status of the solution.
     * @param language the ID of the programming language.
     * @param passedSystemTests the number of passed system test cases.
     * @see #getStatus()
     */
    public CoderComponentItem(long componentID, int points, int status, int language, Integer passedSystemTests) {
        this(componentID, points, status, language);
        this.passedSystemTests = passedSystemTests;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(componentID.longValue());
        writer.writeInt(points.intValue());
        writer.writeInt(status.intValue());
        writer.writeInt(language.intValue());
        writer.writeObject(passedSystemTests);
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
     * Gets the current score of the solution, multiplied by 100.
     * 
     * @return the current score of the solution.
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * Gets the status of the solution.
     * 
     * @return the status of the solution.
     * @see ContestConstants#NOT_OPENED
     * @see ContestConstants#REASSIGNED
     * @see ContestConstants#LOOKED_AT
     * @see ContestConstants#COMPILED_UNSUBMITTED
     * @see ContestConstants#NOT_CHALLENGED
     * @see ContestConstants#CHALLENGE_FAILED
     * @see ContestConstants#CHALLENGE_SUCCEEDED
     * @see ContestConstants#SYSTEM_TEST_FAILED
     * @see ContestConstants#SYSTEM_TEST_SUCCEEDED
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Gets the ID of the programming language.
     * 
     * @return the programming language ID.
     */
    public Integer getLanguage() {
        return language;
    }

    /**
     * Gets the number of passed system test cases. It can be <code>null</code> when it is unavailable.
     * 
     * @return the number of passed system test cases.
     */
    public Integer getPassedSystemTest() {
        return passedSystemTests;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        componentID = new Long(reader.readLong());
        points = new Integer(reader.readInt());
        status = new Integer(reader.readInt());
        language = new Integer(reader.readInt());
        passedSystemTests = (Integer) reader.readObject();

    }

    public boolean equals(Object o) {
        if (!(o instanceof CoderComponentItem))
            return false;

        CoderComponentItem coderComponentItem = (CoderComponentItem) o;

        if (!componentID.equals(coderComponentItem.componentID))
            return false;
        if (!points.equals(coderComponentItem.points))
            return false;
        if (!status.equals(coderComponentItem.status))
            return false;
        if (!language.equals(coderComponentItem.language))
            return false;
        return true;
    }

    public int hashCode() {
        return componentID.hashCode() + points.hashCode() + status.hashCode() + language.hashCode();
    }

    public String toString() {
        return "CoderComponentItem [componentID=" + componentID + ",points=" + points + ",status=" + status
            + ",language=" + language + "]";
    }
}
