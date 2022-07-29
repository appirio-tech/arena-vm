/*
 * User: Michael Cervantes Date: Aug 20, 2002 Time: 12:16:20 AM
 */
package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Arrays;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a user in a user list of a contest round. It contains more contest-related information such as status of
 * problem components attempted by the user, the total score of the user, and the handles of team members if the user is
 * a team user.
 * 
 * @author Michael Cervantes
 * @version $Id: CoderItem.java 72424 2008-08-20 08:06:01Z qliu $
 */
public class CoderItem extends UserListItem {
    /** Represents the status of problem components attempted by the user. */
    private CoderComponentItem[] components;

    /** Represents the current total score of the user. */
    private Double totalPoints;

    /** Represents the handles of the team members if the user is a team user. */
    private ArrayList memberNames;

    /**
     * Creates a new instance of <code>CoderItem</code>. It is required by custom serialization.
     */
    public CoderItem() {
    }

    /**
     * Creates a new instance of <code>CoderItem</code>. The team member list is initialized as <code>null</code>.
     * There is no copy.
     * 
     * @param name the handle of the user.
     * @param rating the rating of the user.
     * @param points the current total score of the user.
     * @param components the status of problem components attempted by the user.
     * @param userType the type of the user.
     * @see #getUserType()
     */
    public CoderItem(String name, int rating, double points, CoderComponentItem[] components, int userType) {
        super(name, rating, userType);
        this.totalPoints = new Double(points);
        this.components = components;
    }

    /**
     * Gets the handles of the team members if the user is a team user. Otherwise, <code>null</code>. The list
     * contains strings. There is no copy.
     * 
     * @return the handles of the team members.
     */
    public ArrayList getMemberNames() {
        return memberNames;
    }

    /**
     * Sets the handles of the team members if the user is a team user. The list contains strings. There is no copy.
     * 
     * @param memberNames the handles of the team members.
     */
    public void setMemberNames(ArrayList memberNames) {
        this.memberNames = memberNames;
    }

    /**
     * Gets the status of problem components attempted by the user. There is no copy.
     * 
     * @return the status of problem components
     */
    public CoderComponentItem[] getComponents() {
        return components;
    }

    /**
     * Gets the current total score of the user.
     * 
     * @return the current total score.
     */
    public Double getTotalPoints() {
        return totalPoints;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(components);
        writer.writeDouble(totalPoints.doubleValue());
        writer.writeArrayList(memberNames);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        this.components = (CoderComponentItem[]) reader.readObjectArray(CoderComponentItem.class);
        this.totalPoints = new Double(reader.readDouble());
        this.memberNames = reader.readArrayList();
    }

    public String toString() {
        return "CoderItem - " + super.toString() + ", total points = " + totalPoints + ", components = "
            + Arrays.asList(components) + ", memberNames = " + memberNames;
    }
}
