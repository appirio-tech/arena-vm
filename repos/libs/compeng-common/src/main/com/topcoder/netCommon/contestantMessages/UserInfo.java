/*
* Copyright (C) 2002 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.netCommon.contestantMessages;

import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.*;

/**
 * Defines a class which represents the information about the logged in user.
 *
 * <p>
 * Changes in version 1.1 (Web Arena UI Member Photo Display v1.0):
 * <ol>
 *      <li>Add {@link #avatar} field and its getter,setter methods.</li>
 *      <li>Update {@link #UserInfo(String handle, boolean guest, boolean admin, boolean captain,long lastLogin,
 *          int numRatedEvents,int rating, boolean isWeakestLinkParticipant, String team, String avatar)} method.</li>
 *      
 * </ol>
 * </p>
 * @author Michael Cervantes (emcee)
 * @version 1.1
 */
public final class UserInfo implements Serializable, CustomSerializable, Cloneable {
    /** Represents the team name which the user belongs to. */
    private String team;

    /** Represents a flag indicating if the user is a team leader. */
    private boolean captain;

    /** Represents the rating of the user. At present, it is the algorithm rating. */
    private int rating;

    /** Represents the number of rated algorithm rounds. */
    private int numRatedEvents;

    /** Represents a flag indicating if the user is an admin. */
    private boolean admin;

    /** Represents the time of user's last log in. */
    private long lastLogin;

    /** Represents a flag indicating if the user is a guest. */
    private boolean guest;

    /** Represents the handle of the user. */
    private String handle = "";

    /** Represents the map of preferences of the user saved on server. */
    private HashMap preferences = new HashMap();

    /** Represents a flag indicating if the user is a 'Weakest Link' round participant. */
    private boolean isWeakestLinkParticipant;

    /**
     * The user avatar path.
     * @since 1.1
     */
    private String avatar;
    /**
     * Creates a new instance of <code>UserInfo</code>. It is required by custom serialization.
     */
    public UserInfo() {
    }

    /**
     * Creates a new instance of <code>UserInfo</code>. The handle and the rating of the user are given.
     * 
     * @param handle the handle of the user.
     * @param rating the rating of the user.
     */
    public UserInfo(String handle, int rating) {
        this.handle = handle;
        this.rating = rating;
    }

    /**
     * Creates a new instance of <code>UserInfo</code>. All information is given.
     * 
     * @param handle the handle of the user.
     * @param guest <code>true</code> if the user is a guest; <code>false</code> otherwise.
     * @param admin <code>true</code> if the user is an admin; <code>false</code> otherwise.
     * @param captain <code>true</code> if the user is a team leader; <code>false</code> otherwise.
     * @param lastLogin the time of the last login of the user.
     * @param numRatedEvents the number of rated algorithm rounds of the user.
     * @param rating the current algorithm rating of the user.
     * @param isWeakestLinkParticipant <code>true</code> if the user is a participant of 'Weakest Link' round;
     *            <code>false</code> otherwise.
     * @param team the name of the team which the user belongs to.
     * @param avatar the user avatar path.
     */
    public UserInfo(String handle, boolean guest, boolean admin, boolean captain, long lastLogin, int numRatedEvents,
        int rating, boolean isWeakestLinkParticipant, String team, String avatar) {
        this.handle = handle;
        this.guest = guest;
        this.admin = admin;
        this.lastLogin = lastLogin;
        this.numRatedEvents = numRatedEvents;
        this.rating = rating;
        this.isWeakestLinkParticipant = isWeakestLinkParticipant;
        this.captain = captain;
        this.team = team;
        this.avatar = avatar;
    }

    /**
     * Clears the information of this user info.
     */
    public void clear() {
        handle = "";
        team = "";
        guest = false;
        admin = false;
        lastLogin = 0;
        numRatedEvents = 0;
        rating = 0;
        avatar = "";
    }

    public boolean equals(Object r) {
        if (r instanceof UserInfo) {
            UserInfo rhs = (UserInfo) r;
            return rhs.handle.equals(handle) && (rhs.admin == admin) && (rhs.guest == guest) && (rhs.rating == rating)
                && (rhs.numRatedEvents == numRatedEvents) && (rhs.lastLogin == lastLogin);
        }
        return false;
    }

    public String toString() {
        return "handle=" + handle + "," + "admin=" + admin + "," + "guest=" + guest + "," + "rating=" + rating + ","
            + "numRatedEvents=" + numRatedEvents + "," + "lastLogin=" + lastLogin + ", pathImage=" + avatar;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        captain = reader.readBoolean();
        rating = reader.readInt();
        numRatedEvents = reader.readInt();
        admin = reader.readBoolean();
        lastLogin = reader.readLong();
        guest = reader.readBoolean();
        handle = reader.readString();
        team = reader.readString();
        preferences = reader.readHashMap();
        isWeakestLinkParticipant = reader.readBoolean();
        avatar = reader.readString();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(captain);
        writer.writeInt(rating);
        writer.writeInt(numRatedEvents);
        writer.writeBoolean(admin);
        writer.writeLong(lastLogin);
        writer.writeBoolean(guest);
        writer.writeString(handle);
        writer.writeString(team);
        writer.writeHashMap(preferences);
        writer.writeBoolean(isWeakestLinkParticipant);
        writer.writeString(avatar);
    }

    /**
     * Gets a flag indicating if the user is a guest.
     * 
     * @return <code>true</code> if the user is a guest; <code>false</code> otherwise.
     */
    public boolean isGuest() {
        return guest;
    }

    /**
     * Gets a flag indicating if the user is a team leader.
     * 
     * @return <code>true</code> if the user is a team leader; <code>false</code> otherwise.
     */
    public boolean isCaptain() {
        return captain;
    }

    /**
     * Sets a flag indicating if the user is a team leader.
     * 
     * @param captain <code>true</code> if the user is a team leader; <code>false</code> otherwise.
     */
    public void setCaptain(boolean captain) {
        this.captain = captain;
    }

    /**
     * Gets the handle of the user.
     * 
     * @return the handle of the user.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the handle of the user.
     * 
     * @param h the handle of the user.
     */
    public void setHandle(String h) {
        handle = h;
    }

    /**
     * Gets a flag indicating if the user is an admin.
     * 
     * @return <code>true</code> if the user is an admin; <code>false</code> otherwise.
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Gets a flag indicating if it is the user's first visit.
     * 
     * @return <code>true</code> if it is the user's first visit; <code>false</code> otherwise.
     */
    public boolean isFirstTimeUser() {
        return lastLogin == 0;
    }

    /**
     * Gets the last login time of the user. The time is represented by the number of milliseconds since January 1,
     * 1970, 00:00:00 GMT.
     * 
     * @return the last login time of the user.
     * @see java.util.Date#getTime()
     */
    public long getLastLogin() {
        return lastLogin;
    }

    /**
     * Sets the last login time of the user. The time is represented by the number of milliseconds since January 1,
     * 1970, 00:00:00 GMT.
     * 
     * @param lastLogin the last login time of the user.
     * @see java.util.Date#getTime()
     */
    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Gets the number of rated algorithm rounds which the user participated in.
     * 
     * @return the number of rated algorithm rounds.
     */
    public int getNumRatedEvents() {
        return numRatedEvents;
    }

    /**
     * Gets the current algorithm rating of the user.
     * 
     * @return the current algorithm rating of the user.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Gets the map of preferences which the user saved on the server. There is no copy.
     * 
     * @return the map of preferences which the user saved on the server.
     */
    public HashMap getPreferences() {
        return preferences;
    }

    /**
     * Sets the map of preferences which the user saved on the server. There is no copy.
     * 
     * @param preferences the map of preferences which the user saved on the server.
     */
    public void setPreferences(HashMap preferences) {
        this.preferences = preferences;
    }

    /**
     * Gets a flag indicating if the user is a participant of 'Weakest Link' round.
     * 
     * @return <code>true</code> if the user is a participant of 'Weakest Link' round; <code>false</code> otherwise.
     */
    public boolean isWeakestLinkParticipant() {
        return isWeakestLinkParticipant;
    }

    /**
     * Gets the name of the team which the user belongs to.
     * 
     * @return the name of the team.
     */
    public String getTeam() {
        return team;
    }

    /**
     * Gets the user avatar path.
     * @return the user avatar path.
     * @since 1.1
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Sets the user avatar path.
     * @param avatar the user avatar path.
     * @since 1.1
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
