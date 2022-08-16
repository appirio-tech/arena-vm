/*
 * TeamList.java Created on June 26, 2002, 11:54 PM
 */

package com.topcoder.netCommon.contestantMessages.lists;

import java.util.Collection;

/**
 * Defines the information of a team. The information of the team is stored in an array list. The information is stored
 * as team name, team rank, team leader handle, team leader rank, team availability, number of team members, and the
 * current status of the team in the array list respectively.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: TeamRowList.java 72143 2008-08-06 05:54:59Z qliu $
 */
public class TeamRowList extends ListWrapper {
    /**
     * Creates a new instance of TeamList. All information is initialized as invalid.
     */
    public TeamRowList() {
        super(7);
    }

    /**
     * Creates a new instance of TeamList. All information is copied from the given collection.
     * 
     * @param al ArrayList containing team properties
     */
    public TeamRowList(Collection al) {
        super(al);
    }

    /**
     * Creates a new instance of TeamList. All information is given.
     * 
     * @param team the team name.
     * @param rank the team rank.
     * @param captain the team leader handle.
     * @param crank the team leader rank.
     * @param avail the number of available team positions.
     * @param memb the number of current team members.
     * @param status the status of the team.
     * @see #set(String, int, String, int, int, int, String)
     */
    public TeamRowList(String team, int rank, String captain, int crank, int avail, int memb, String status) {
        super(7);
        set(team, rank, captain, crank, avail, memb, status);
    }

    /**
     * Creates an ArrayList representation of a team row.
     * 
     * @param team the team name
     * @param rank the team's rank
     * @param captain the team's captain
     * @param crank the team captain's rank
     * @param avail the number of members on the team (the number that joined in pickup)
     * @param memb the number of members enrolled for this competition
     * @param status the status of the team (e.g., "closed" when captains done picking team, "pending" if user is
     *            awaiting response for team, etc)
     */
    public void set(String team, int rank, String captain, int crank, int avail, int memb, String status) {
        setName(team);
        setRank(rank);
        setCaptain(captain);
        setCaptainRank(crank);
        setAvailable(avail);
        setMembers(memb);
        setStatus(status);
    }

    /**
     * Sets the team name.
     * 
     * @param name the team name.
     */
    public void setName(String name) {
        set(0, name);
    }

    /**
     * Sets the team rank.
     * 
     * @param rank the team rank.
     */
    public void setRank(int rank) {
        set(1, rank);
    }

    /**
     * Sets the team leader handle.
     * 
     * @param name the team leader handle.
     */
    public void setCaptain(String name) {
        set(2, name);
    }

    /**
     * Sets the team leader rank.
     * 
     * @param crank the team leader rank.
     */
    public void setCaptainRank(int crank) {
        set(3, crank);
    }

    /**
     * Sets the number of available team positions.
     * 
     * @param num the number of available team positions.
     */
    public void setAvailable(int num) {
        set(4, num);
    }

    /**
     * Sets the number of current team members.
     * 
     * @param num the number of current team members.
     */
    public void setMembers(int num) {
        set(5, num);
    }

    /**
     * Sets the status of the team.
     * 
     * @param status the status of the team.
     */
    public void setStatus(String status) {
        set(6, status);
    }

    /**
     * Gets the team name.
     * 
     * @return the team name.
     */
    public String getName() {
        return (String) get(0);
    }

    /**
     * Gets the team rank.
     * 
     * @return the team rank.
     */
    public int getRank() {
        return getInt(1);
    }

    /**
     * Gets the team leader handle.
     * 
     * @return the team leader handle.
     */
    public String getCaptain() {
        return (String) get(2);
    }

    /**
     * Gets the team leader rank.
     * 
     * @return the team leader rank.
     */
    public int getCaptainRank() {
        return getInt(3);
    }

    /**
     * Gets the number of available team positions.
     * 
     * @return the number of available team positions.
     */
    public int getAvailable() {
        return getInt(4);
    }

    /**
     * Gets the number of current team members.
     * 
     * @return the number of current team members.
     */
    public int getMembers() {
        return getInt(5);
    }

    /**
     * Gets the status of the team.
     * 
     * @return the status of the team.
     */
    public String getStatus() {
        return (String) get(6);
    }
}
