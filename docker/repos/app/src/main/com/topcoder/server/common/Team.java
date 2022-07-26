/**
 * Class User
 *
 * Author: Hao Kung
 *
 * Description: This class will contain information about a team in the system
 */
package com.topcoder.server.common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import com.topcoder.netCommon.contest.ContestConstants;


public class Team implements Serializable {

    protected String m_cacheKey;

    public final String getCacheKey() {
        return m_cacheKey;
    }

    /*
    * Constructors
    */
    public Team(int id, String name, User captain) {
        m_name = name;
        setCaptain(captain);
        setID(id);
    }

    public Team(int id, String name, int rating, int languageID, int numRatedEvents,
            Timestamp regDate, String quote, User captain) {
        setID(id);
        m_name = name;
        setRating(rating);
        setLanguage(languageID);
        setNumRatedEvents(numRatedEvents);
        setRegDate(regDate);
        setQuote(quote);
        setCaptain(captain);
    }

    public String toString() {
        return "id=" + m_id + ", name=" + m_name + ", rating=" + m_rating + ", lang=" + m_lang +
                ", numRatedEvents=" + m_numRatedEvents + ", eligible=" + m_eligible + ", regDate=" + m_regDate +
                ", quote=" + m_quote + ", room=" + m_contestRoomID + ",members=" + m_members + ",memberNames=" + m_memberNames;
    }

    /*
    * Data members/getter/setters
    */

    // Username
    private String m_name;

    public final String getName() {
        return m_name;
    }
    //public final void setName(String name) { m_userName = name;}

    private int m_captainID;

    public final int getCaptainID() {
        return m_captainID;
    }

    public void setCaptain(User captain) {
        m_captainID = captain.getID();
        addMember(captain);
    }

    private int m_id;

    public final int getID() {
        return m_id;
    }

    private final void setID(int id) {
        m_id = id;
        m_cacheKey = getCacheKey(m_id);
    }

    private int m_lang = ContestConstants.JAVA;

    public final int getLanguage() {
        return m_lang;
    }

    public final void setLanguage(int lang) {
        m_lang = lang;
    }

    private int m_rating;

    public final int getRating() {
        return m_rating;
    }

    public final void setRating(int r) {
        m_rating = r;
    }

    private int m_teamTypeID;

    public final int getTeamTypeID() {
        return m_teamTypeID;
    }

    public final void setTeamTypeID(int r) {
        m_teamTypeID = r;
    }

    private int m_numRatedEvents;

    public final void setNumRatedEvents(int num) {
        m_numRatedEvents = num;
    }

    public final int getNumRatedEvents() {
        return m_numRatedEvents;
    }

    boolean m_eligible = true;

    public boolean isEligible() {
        return m_eligible;
    }

    public void setEligible(boolean value) {
        m_eligible = value;
    }

    private Timestamp m_regDate;

    private final void setRegDate(Timestamp time) {
        m_regDate = time;
    }

    // id's of the team members
    private ArrayList m_members = new ArrayList();
    private ArrayList m_memberNames = new ArrayList();

    public void addMember(User u) {
        u.setTeamID(m_id);
        m_members.add(new Integer(u.getID()));
        m_memberNames.add(u.getName());
    }

    public Collection getMembers() {
        return m_members;
    }

    public boolean isMember(int userId) {
        return m_members.contains(new Integer(userId));
    }

    public Collection getMemberNames() {
        return m_memberNames;
    }

    public void removeMember(User u) {
        u.setTeamID(-1);
        m_members.remove(new Integer(u.getID()));
        m_memberNames.remove(u.getName());
    }

    public int getSize() {
        return m_members.size();
    }

    // id's of the potential team members
    private ArrayList m_interestedCoders = new ArrayList();
    private ArrayList m_interestedNames = new ArrayList();

    public Collection getInterestedCoders() {
        return m_interestedCoders;
    }

    public void addInterestedCoder(User u) {
        m_interestedCoders.add(new Integer(u.getID()));
        m_interestedNames.add(u.getName());
    }

    public void removeInterestedCoder(User u) {
        m_interestedCoders.remove(new Integer(u.getID()));
        m_interestedNames.remove(u.getName());
    }


    public int getAvailable() {
        return 0;
    }

    private String m_quote;

    private final void setQuote(String q) {
        m_quote = q;
    }

    // this is the team's actual assigned room
    protected int m_contestRoomID = ContestConstants.INVALID_ROOM;

    public int getContestRoom() {
        return m_contestRoomID;
    }

    public void setContestRoom(int id) {
        m_contestRoomID = id;
    }

    public static String getCacheKey(int id) {
        return "Team." + id;
    }

    public String getTeamInfo() {
        StringBuffer retVal = new StringBuffer(200);
        retVal.append("Team Name:    ");
        retVal.append(m_name);
        retVal.append("\nTeam Members: ");
        for (int i = 0; i < m_memberNames.size(); i++) {
            retVal.append(m_memberNames.get(i) + ((i < m_memberNames.size() - 1) ? ", " : ""));
        }
        retVal.append("\nRating:       ");
        if (m_numRatedEvents == 0)
            retVal.append("Not Rated");
        else
            retVal.append(m_rating);
        retVal.append("\nRated events: ");
        retVal.append(m_numRatedEvents);
        retVal.append("\nQuote:        ");
        retVal.append(m_quote == null ? "" : m_quote);
        retVal.append("\n");
        return retVal.toString();
    }

}
