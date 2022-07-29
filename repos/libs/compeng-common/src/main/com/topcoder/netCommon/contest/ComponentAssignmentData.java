package com.topcoder.netCommon.contest;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Represents a set of component - user assignments, and also contains a list of all the team members available for
 * assignment.
 * 
 * @author Tim Bulat
 * @version $Id: ComponentAssignmentData.java 72046 2008-07-31 06:47:43Z qliu $
 */
public class ComponentAssignmentData implements Serializable, CustomSerializable {
    /** Represents the ID of the team and the ID of the round. */
    private int teamID, roundID;

    /**
     * List of members on the team.
     */
    private ArrayList teamMembers = new ArrayList();

    /**
     * HashMap of componentId -> coderId of assignments
     */
    private HashMap assignments = new HashMap();

    /**
     * Creates a new instance of <code>ComponentAssignmentData</code>. It is required by custom serialization.
     */
    public ComponentAssignmentData() {
        this(-1, -1);
    }

    /**
     * Creates a new instance of <code>ComponentAssignmentData</code>. The team problem component assignment is for
     * the given team in the given round.
     * 
     * @param teamID the ID of the team.
     * @param roundID the ID of the round.
     */
    public ComponentAssignmentData(int teamID, int roundID) {
        this.teamID = teamID;
        this.roundID = roundID;
    }

    /**
     * Gets a key used to uniquely identify this team problem component assignment.
     * 
     * @return a unique key.
     */
    public String getCacheKey() {
        return getCacheKey(teamID, roundID);
    }

    /**
     * Gets a key used to uniquely identify the team problem component assignment for the given team in the given round.
     * 
     * @param teamID the ID of the team.
     * @param roundID the ID of the round.
     * @return a unique key.
     */
    public static String getCacheKey(int teamID, int roundID) {
        return "ComponentAssignmentData.TeamID" + teamID + ".RoundID" + roundID;
    }

    /**
     * Sets the team members for the assignment. The items in the list should be <code>UserListItem</code>.
     * There is no copy.
     * 
     * @param teamMembers the team members.
     */
    public void setTeamMembers(ArrayList teamMembers) {
        this.teamMembers = teamMembers;
    }

    /**
     * Adds a team member to the team of this assignment.
     * 
     * @param user the team member to be added.
     */
    public void addTeamMember(UserListItem user) {
        this.teamMembers.add(user);
    }

    /**
     * Gets the team members for the assignment. The items in the list are <code>UserListItem</code>. There is no copy.
     * 
     * @return the team members.
     */
    public ArrayList getTeamMembers() {
        return teamMembers;
    }

    /**
     * Sets the map of the problem component assignment. The keys are IDs of problem components,
     * and the values are IDs of users. There is no copy.
     * 
     * @param assignments the map of the assignment.
     */
    public void setAssignments(HashMap assignments) {
        this.assignments = assignments;
    }

    /**
     * Assigns the problem component to the team member.
     * 
     * @param componentId the ID of the problem component.
     * @param userId the ID of the team member.
     */
    public void assignComponent(int componentId, int userId) {
        this.assignments.put(new Integer(componentId), new Integer(userId));
    }

    /**
     * Unassigns the problem component.
     * 
     * @param componentId the ID of the problem component.
     */
    public void unassignComponent(int componentId) {
        this.assignments.remove(new Integer(componentId));
    }

    /**
     * Gets the map of the problem component assignment. The keys are IDs of problem components,
     * and the values are IDs of users. There is no copy.
     * 
     * @return the map of the assignment.
     */
    public HashMap getAssignments() {
        return assignments;
    }

    /**
     * Gets the assigned team member for the problem component. If nobody is assigned to the component,
     * -1 is returned.
     * 
     * @param componentID the ID of the problem component.
     * @return the ID of the team member assigned to the problem component.
     */
    public int getAssignedUserForComponent(int componentID) {
        if (assignments.get(new Integer(componentID)) == null) {
            return -1;
        }
        return ((Integer) assignments.get(new Integer(componentID))).intValue();
    }

    /**
     * Gets all assigned components. A copy is returned.
     * 
     * @return the IDs of all assigned components.
     */
    public int[] getAssignedComponents() {
        Set set = assignments.keySet();
        int[] assignedComponents = new int[set.size()];
        int index = 0;
        for (Iterator i = set.iterator(); i.hasNext();) {
            assignedComponents[index++] = ((Integer) i.next()).intValue();
        }
        return assignedComponents;
    }

    /**
     * Gets the ID of the team of this assignment.
     * 
     * @return the ID of the team.
     */
    public int getTeamID() {
        return teamID;
    }

    /**
     * Gets the ID of the round of this assignment.
     * 
     * @return the ID of the round.
     */
    public int getRoundID() {
        return roundID;
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeInt(teamID);
        csWriter.writeInt(roundID);
        csWriter.writeArrayList(teamMembers);
        csWriter.writeHashMap(assignments);
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        teamID = csReader.readInt();
        roundID = csReader.readInt();
        teamMembers = csReader.readArrayList();
        assignments = csReader.readHashMap();
    }

    public String toString() {
        return "ComponentAssignmentData[assignments=" + assignments + ",teamMembers=" + teamMembers + "]";
    }
}
