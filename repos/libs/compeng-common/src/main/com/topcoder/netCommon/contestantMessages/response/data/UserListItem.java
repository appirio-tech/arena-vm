package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.CreateUserListResponse;
import com.topcoder.netCommon.contestantMessages.response.RegisteredUsersResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateUserListResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a user in the user list. The information includes handle, proper rating according to the type of the round if
 * available or the algorithm rating, name of the team which the user belongs to, the ID of the user, the type of the
 * user, and the country of the user. When the ID or the type of user is unknown, they are -1. When the team name or
 * country is not available, they are empty strings. The type of user can be either normal user or team user (in team
 * rounds). The user can be compared according to the handle (case insensitive).
 * 
 * @author Matthew P. Suhocki
 * @version $Id: UserListItem.java 72424 2008-08-20 08:06:01Z qliu $
 * @see CreateUserListResponse
 * @see RegisteredUsersResponse
 * @see UpdateUserListResponse
 * @see CoderHistoryData
 */
public class UserListItem implements CustomSerializable, Serializable, Comparable {
    /** Represents the handle of the user. */
    private String userName;

    /** Represents the rating of the user. */
    private int userRating;

    /** Represents the name of the team which the user belongs to. */
    private String teamName;

    /** Represents the ID of the user. */
    private int userID;

    /** Represents the type of the user. */
    private int userType;

    /** Represents the country of the user. */
    private String countryName;

    /**
     * Creates a new instance of <code>UserListItem</code>. It is required by custom serialization.
     */
    public UserListItem() {
    }

    /**
     * Creates a new instance of <code>UserListItem</code>. The ID and the type of the user are unknown (-1). The
     * user does not belong to any team or country.
     * 
     * @param name the handle of the user.
     * @param rank the rating of the user.
     */
    public UserListItem(String name, int rank) {
        this(name, rank, "", -1, -1, "");
    }

    /**
     * Creates a new instance of <code>UserListItem</code>. The ID of the user is unknown (-1). The user does not
     * belong to any team or country.
     * 
     * @param name the handle of the user.
     * @param rank the rating of the user.
     * @param userType the type of the user.
     * @see #getUserType()
     */
    public UserListItem(String name, int rank, int userType) {
        this(name, rank, "", -1, userType, "");
    }

    /**
     * Creates a new instance of <code>UserListItem</code>. The user does not belong to any team or country.
     * 
     * @param name the handle of the user.
     * @param rank the rating of the user.
     * @param id the ID of the user.
     * @param userType the type of the user.
     * @see #getUserType()
     */
    public UserListItem(String name, int rank, int id, int userType) {
        this(name, rank, "", id, userType, "");
    }

    /**
     * Creates a new instance of <code>UserListItem</code>. The ID of the user is unknown (-1). The user does not
     * belong to any country.
     * 
     * @param name the handle of the user.
     * @param rank the rating of the user.
     * @param team the name of the team which the user belongs to.
     * @param userType the type of the user.
     * @see #getUserType()
     */
    public UserListItem(String name, int rank, String team, int userType) {
        this(name, rank, team, -1, userType, "");
    }

    /**
     * Creates a new instance of <code>UserListItem</code>. The user does not belong to any country.
     * 
     * @param name the handle of the user.
     * @param rank the rating of the user.
     * @param team the name of the team which the user belongs to.
     * @param id the ID of the user.
     * @param userType the type of the user.
     * @see #getUserType()
     */
    public UserListItem(String name, int rank, String team, int id, int userType) {
        this(name, rank, team, id, userType, "");
    }

    /**
     * Creates a new instance of <code>UserListItem</code>.
     * 
     * @param name the handle of the user.
     * @param rank the rating of the user.
     * @param team the name of the team which the user belongs to.
     * @param id the ID of the user.
     * @param userType the type of the user.
     * @param country the country of the user.
     * @see #getUserType()
     */
    public UserListItem(String name, int rank, String team, int id, int userType, String country) {
        userName = name;
        userRating = rank;
        teamName = team;
        userID = id;
        this.userType = userType;
        countryName = country;
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeString(userName);
        csWriter.writeInt(userRating);
        csWriter.writeInt(userID);
        csWriter.writeString(teamName);
        csWriter.writeInt(userType);
        csWriter.writeString(countryName);
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        userName = csReader.readString();
        userRating = csReader.readInt();
        userID = csReader.readInt();
        teamName = csReader.readString();
        userType = csReader.readInt();
        countryName = csReader.readString();
    }

    /**
     * Gets the handle of the user.
     * 
     * @return the handle of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the rating of the user.
     * 
     * @return the rating of the user.
     */
    public int getUserRating() {
        return userRating;
    }

    /**
     * Gets the name of the team which the user belongs to. If the user does not belong to any team, empty string ('')
     * is returned.
     * 
     * @return the name of the team.
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Gets the name of the country which the user belongs to. If the user does not belong to any country, empty string
     * ('') is returned.
     * 
     * @return the name of the country.
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Sets the handle of the user.
     * 
     * @param name the handle of the user.
     */
    public void setUserName(String name) {
        userName = name;
    }

    /**
     * Sets the rating of the user.
     * 
     * @param rating the rating of the user.
     */
    public void setUserRating(int rating) {
        userRating = rating;
    }

    /**
     * Sets the name of the team which the user belongs to. If the user does not belong to any team, empty string ('')
     * should be set.
     * 
     * @param name the name of the team.
     */
    public void setTeamName(String name) {
        teamName = name;
    }

    /**
     * Sets the name of the country which the user belongs to. If the user does not belong to any country, empty string
     * ('') should be set.
     * 
     * @param name the name of the country.
     */
    public void setCountryName(String name) {
        countryName = name;
    }

    public String toString() {
        return userName;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UserListItem))
            return false;
        return userName.equals(((UserListItem) obj).getUserName());
    }

    public int compareTo(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            return userName.toLowerCase().compareTo(s.toLowerCase());
        } else if (o instanceof UserListItem) {
            UserListItem entry = (UserListItem) o;
            return userName.toLowerCase().compareTo(entry.userName.toLowerCase());
        }
        throw new IllegalStateException("Compared UserListItem to " + o.getClass().getName());
    }

    public int hashCode() {
        return userName.hashCode();
    }

    /**
     * Gets the ID of the user.
     * 
     * @return the user ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the type of the user.
     * 
     * @return the user type.
     * @see ContestConstants#SINGLE_USER
     * @see ContestConstants#TEAM_USER
     */
    public int getUserType() {
        return userType;
    }

}
