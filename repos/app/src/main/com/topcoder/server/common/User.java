/*
* Copyright (C) ? - 2015 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.StringUtil;

/**
 * This class will contain information about a user in the system.
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Added {@link #isUserActive()} to check if the user is active.</li>
 * <li>Added {@link #setUserStatus(String)} to set the user status. </li>
 * <li>Updated {@link #User()} constructor to initialize userStatus</li>
 * </ol>
 * </p>
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Add Match Super User Role v1.0):
 * <ol>
 * <li>Added new field m_isAdmin4Web and its getter and setter.</li>
 * <li>Updated the full arguments constructor and the toString methods. </li>
 * </ol>
 * </p>
 * @author Hao Kung, TCSASSEMBLER
 * @version 1.1
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class User implements Serializable, Comparable {

    private String m_cacheKey;
    private String m_countryName;
    private String m_stateCode;
    private String m_userStatus;

    public final String getCacheKey() {
        return m_cacheKey;
    }

    /*
     * Constructors
     */
    public User(int id, String name) {
        m_userName = name;
        setID(id);
    }

    /**
     * Full constructor of the user instance.
     * <p>
     *  Changes in 1.1: add new parameter isAdmin4Web.
     * </p>
     * @param id the id
     * @param name the name
     * @param languageID the language id
     * @param regDate the registration date
     * @param lastLogin the last login date
     * @param quote the quote
     * @param isLevelTwoAdmin if the user is level two admin
     * @param isLevelOneAdmin if the user is level one admin
     * @param isCaptain if the user is captain
     * @param countryName the country name
     * @param stateCode the state code
     * @param emailStatus the email status
     * @param coderType the coder type
     * @param school the school
     * @param schoolViewable the school viewable
     * @param userStatus the user status
     * @param isAdmin4Web if the user is an admin for web
     */
    public User(int id, String name, int languageID, Timestamp regDate, 
            Timestamp lastLogin, String quote, boolean isLevelTwoAdmin,
            boolean isLevelOneAdmin, boolean isCaptain, String countryName, 
            String stateCode, int emailStatus, String coderType, String school, 
            int schoolViewable, String userStatus, boolean isAdmin4Web) {
        setID(id);
        m_userName = name;
        setLanguage(languageID);
        setRegDate(regDate);
        setLastLogin(lastLogin);
        if (quote != null) setQuote(quote);
        setLevelTwoAdmin(isLevelTwoAdmin);
        setLevelOneAdmin(isLevelOneAdmin);
        setCaptain(captain);
        setCountryName(countryName);
        setStateCode(stateCode);
        setEmailStatus(emailStatus);
        setCoderType(coderType);
        setSchool(school);
        setSchoolViewable(schoolViewable);
        setUserStatus(userStatus);
        setAdmin4Web(isAdmin4Web);

        m_ratings = new ArrayList();
        m_seasons = new ArrayList();
    }

    /**
     * The string representation of the current user object.
     * <p>
     *  Changes in 1.1: add new field m_isAdmin4Web to the string representation result.
     * </p>
     * @return the string result
     */
    public String toString() {
        return "id=" + m_id + ", handle=" + m_userName + ", lang=" + m_lang + 
                ", eligible=" + m_eligible + ", regDate=" + m_regDate + ", lastLogin=" + m_lastLogin + 
                ", quote=" + m_quote + ", roomID=" + m_roomID + ", contestRoomID=" + m_contestRoomID +
                ", watchedRooms=" + m_watchedRooms + ", roomType=" + m_roomType + ", isLevelTwoAdmin=" + m_isLevelTwoAdmin +
                ", isLevelOneAdmin=" + m_isLevelOneAdmin + ", isGuest=" + m_isGuest +
                ", teamID=" + m_teamID + ", countryName=" + m_countryName + ", stateCode=" + m_stateCode +
                ", coderType=" + m_coderType + ", school=" + m_school + ", schoolViewable=" + m_schoolViewable + ", isForwarder=" + m_isForwarder +
                ", admin4Web=" + m_isAdmin4Web;
    }

    /*
     * Data members/getter/setters
     */
    
    private List m_ratings;
    private List m_seasons;
    
    public void addSeason(int s) {
        if(!m_seasons.contains(new Integer(s)))
            m_seasons.add(new Integer(s));
    }
    
    public boolean hasSeason(int s) {
        return m_seasons.contains(new Integer(s));
    }
    
    /**
     * Adds a new rating to this user
     * @param r The rating to add
     */
    public void addRating(Rating r) {
        //check to see if the rating type is existing
        
        for(Iterator i = m_ratings.iterator(); i.hasNext(); ) {
            Rating r2 = (Rating)i.next();
            if(r.getRatingType() == r2.getRatingType()) {
                i.remove();
            }
        }
        
        m_ratings.add(r);
    }
    
    public Rating getRating(int type) {
        for(int i = 0; i < m_ratings.size(); i++) {
            Rating r = (Rating)m_ratings.get(i);
            if(r.getRatingType() == type)
                return r;
        }
        
        //return null;
        //I'm still debating this decision, but I've decided to not return null here
        //putting null checks for certain rating types all over the place doesn't make much sense
        //, so I'm going to return a basic 0 rating object
        //
        // We'll see if I regret this
        
        if(isLevelTwoAdmin()) {
            return new Rating(type, -1, 0, 0, null, "");
        } else {
            return new Rating(type, 0, 0, 0, null, "");
        }
    }
    
    // Username
    private final String m_userName;

    public final String getName() {
        return m_userName;
    }
    
    public int emailStatus;
    
    private final void setEmailStatus(int s) {
        emailStatus = s;
    }
    
    public final int getEmailStatus() {
        return emailStatus;
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

    private boolean m_eligible = true;

    public boolean isEligible() {
        return m_eligible;
    }

    public void setEligible(boolean value) {
        m_eligible = value;
    }
    
    private int m_seed = 1;

    public int getSeed() {
        return m_seed;
    }

    public void setSeed(int value) {
        m_seed = value;
    }
    
    private int m_tourneyRating = 0;
    
    public void setTournamentRating(int rating) {
        m_tourneyRating = rating;
    }
    
    public int getTournamentRating() {
        return m_tourneyRating;
    }

    private Timestamp m_regDate;

    private final void setRegDate(Timestamp time) {
        m_regDate = time;
    }

    private boolean captain;

    public void setCaptain(boolean captain) {
        this.captain = captain;
    }

    public boolean isCaptain() {
        return captain;
    }

    public String getCountryName() {
        return m_countryName;
    }

    public void setCountryName(String m_country) {
        this.m_countryName = m_country;
    }

    public String getStateCode() {
        return m_stateCode;
    }

    public void setStateCode(String m_stateCode) {
        this.m_stateCode = m_stateCode;
    }

    public void setUserStatus(String m_userStatus) {
        this.m_userStatus = m_userStatus;
    }
    /**
     * <p>
     * to check if the user is activate currently.
     * </p>
     * @return true=user is activate, false=not activate
     */
    public boolean isUserActive() {
        return m_userStatus!=null&&m_userStatus.equals("A");
    }
    private Timestamp m_lastLogin;
    
    private final void setLastLogin(Timestamp time) {
        m_lastLogin = time;
    }
    
    public Timestamp getLastLogin() {
        return m_lastLogin;
    }

    private String m_quote = "";

    private final void setQuote(String q) {
        m_quote = q;
    }

    private int m_roomID = ContestConstants.INVALID_ROOM;

    public int getRoomID() {
        return m_roomID;
    }

    public void setRoomID(int room) {
        m_roomID = room;
    }

    // this is his actual assigned room
    private int m_contestRoomID = ContestConstants.INVALID_ROOM;

    public int getContestRoom() {
        return m_contestRoomID;
    }

    public void setContestRoom(int id) {
        m_contestRoomID = id;
    }

    private final List m_watchedRooms = Collections.synchronizedList(new LinkedList());

    public void addWatchedRoom(int roomID) {
        m_watchedRooms.add(new Integer(roomID));
    }

    public void removeWatchedRoom(int roomID) {
        m_watchedRooms.remove(new Integer(roomID));
    }

    public boolean hasWatchedRoom(int roomID) {
        return m_watchedRooms.contains(new Integer(roomID));
    }

    private final List m_watchedDivSummaryRooms = Collections.synchronizedList(new LinkedList());

    public void addWatchedDivSummaryRoom(int roomID) {
        m_watchedDivSummaryRooms.add(new Integer(roomID));
    }

    public void removeWatchedDivSummaryRoom(int roomID) {
        m_watchedDivSummaryRooms.remove(new Integer(roomID));
    }

    public boolean hasWatchedDivSummaryRoom(int roomID ) {
        return m_watchedDivSummaryRooms.contains(new Integer(roomID));
    }
    
    public Iterator getWatchedDivSummaryRooms() {
        ArrayList rooms = new ArrayList(m_watchedDivSummaryRooms);
        return rooms.iterator();
    }

    public Iterator getWatchedRooms() {
        ArrayList rooms = new ArrayList(m_watchedRooms);
        return rooms.iterator();
    }

    // TODO: Move this somewhere
    public static final int NO_TEAM = -1;
    private int m_teamID = NO_TEAM;

    public void setTeamID(int team) {
        m_teamID = team;
    }

    public int getTeamID() {
        return m_teamID;
    }

    public boolean isOnTeam() {
        return m_teamID != NO_TEAM;
    }

    private String m_teamName = "";

    public String getTeamName() {
        return m_teamName;
    }

    public void setTeamName(String teamName) {
        this.m_teamName = teamName;
    }


    // TODO get the real room type from somewhere or expose another mechanism
    // to get the room type.
    private int m_roomType = ContestConstants.LOBBY_ROOM;

    public int getRoomType() {
        return m_roomType;
    }

    public void setRoomType(int type) {
        m_roomType = type;
    }

    public static String getCacheKey(int id) {
        return "User." + id;
    }

    private boolean m_isLevelTwoAdmin = false;

    public final void setLevelTwoAdmin(boolean admin) {
        m_isLevelTwoAdmin = admin;
    }

    public boolean isLevelTwoAdmin() {
        return m_isLevelTwoAdmin;
    }

    private boolean m_isLevelOneAdmin = false;

    public final void setLevelOneAdmin(boolean admin) {
        m_isLevelOneAdmin = admin;
    }

    public boolean isLevelOneAdmin() {
        return m_isLevelOneAdmin;
    }

    /**
     * The flag to show if the current user is a web admin.
     * @since 1.1
     */
    private boolean m_isAdmin4Web = false;

    /**
     * Set the web admin flag.
     * @param admin4Web the admin value to be set.
     * @since 1.1
     */
    public final void setAdmin4Web(boolean admin4Web) {
        m_isAdmin4Web = admin4Web;
    }

    /**
     * Get the web admin flag.
     * @return true if the user is an admin for web
     * @since 1.1
     */
    public boolean isAdmin4Web() {
        return m_isAdmin4Web;
    }

    private boolean m_isGuest = false;

    public void setGuest(boolean guest) {
        m_isGuest = guest;
    }

    public boolean isGuest() {
        return m_isGuest;
    }

    private boolean m_isSpec = false;

    public void setSpectator(boolean spec) {
        m_isSpec = spec;
    }

    public boolean isSpectator() {
        return m_isSpec;
    }
    
    private boolean m_isForwarder = false;

    public void setForwarder(boolean b) {
        m_isForwarder = b;
    }

    public boolean isForwarder() {
        return m_isForwarder;
    }
    
    private String m_coderType = "";

    public void setCoderType(String coder_type) {
        m_coderType = coder_type;
    }

    public String getCoderType() {
        return m_coderType;
    }
       
    private String m_school = "";

    public void setSchool(String school) {
        m_school = school;
    }

    public String getSchool() {
        return m_school;
    }
    
    private int m_schoolViewable = 0;

    public void setSchoolViewable(int schoolViewable) {
        m_schoolViewable = schoolViewable;
    }

    public int getSchoolViewable() {
        return m_schoolViewable;
    }
    
    private boolean competitionUser = false;
    
    public boolean isCompetitionUser() {
        return competitionUser;
    }
    
    public void setCompetitionUser(boolean b) {
        competitionUser = b;
    }
    
    private boolean hsCompetitionUser = false;
    
    public boolean isHSCompetitionUser() {
        return hsCompetitionUser;
    }
    
    public void setHSCompetitionUser(boolean b) {
        hsCompetitionUser = b;
    }
    
    public static final int NO_REGION = -1;
    
    private int m_hsRegion = NO_REGION;
    
    public int getHSRegion() {
        return m_hsRegion;
    }
    
    public void setHSRegion(int r) {
        m_hsRegion = r;
    }
       
    private static final int INFO_LABEL_LENGTH = 24;
    
    public String getCoderInfo() {
        StringBuffer retVal = new StringBuffer(200);
        retVal.append(StringUtil.padRight("Handle: ", INFO_LABEL_LENGTH-1));
        retVal.append(m_userName);
        //draw out all ratings
        for(Iterator i = m_ratings.iterator(); i.hasNext(); ) {
            Rating r = (Rating)i.next();
            String desc = r.getDesc();
            if(!desc.trim().equals("")) {
                desc = desc + " ";
            }
            
            retVal.append(StringUtil.padRight("\n" + desc + "Rating (highest): ", INFO_LABEL_LENGTH));
            if (isLevelTwoAdmin() || r.getRating() < 0)
                retVal.append("Admin");
            else if (r.getNumRatings() == 0)
                retVal.append("Not Rated");
            else {
                retVal.append(r.getRating());
                retVal.append(" (");
                retVal.append(r.getHighestRating());
                retVal.append(")");
            }
            retVal.append(StringUtil.padRight("\n" + desc + "Rated Matches: ", INFO_LABEL_LENGTH));
            retVal.append(r.getNumRatings());
         
            if (r.getLastRatedEvent() != null) {
                retVal.append(StringUtil.padRight("\n" + desc + "Last Match: ", INFO_LABEL_LENGTH));
                DateFormat mediumFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
                retVal.append(mediumFormatter.format(r.getLastRatedEvent()));
            }
        }
        
        if (m_regDate != null) {
            retVal.append(StringUtil.padRight("\nMember since: ", INFO_LABEL_LENGTH));
            DateFormat mediumFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
            retVal.append(mediumFormatter.format(m_regDate));
        }
        
        if ( m_countryName != null && !m_countryName.equals("")) {
            retVal.append(StringUtil.padRight("\nCountry: ", INFO_LABEL_LENGTH));
            retVal.append(m_countryName);
        }
        if ( m_stateCode != null && !m_stateCode.equals("")) {
            retVal.append(StringUtil.padRight("\nState: ", INFO_LABEL_LENGTH));
            retVal.append(m_stateCode);
        }
        if ( m_coderType != null && !m_coderType.equals("")) {
            retVal.append(StringUtil.padRight("\nCoder type: ", INFO_LABEL_LENGTH));
            retVal.append(m_coderType);
        }
        if ( m_coderType != null && m_coderType.equals("Student") && m_school != null && !m_school.equals("") && m_schoolViewable != 0) {
            retVal.append(StringUtil.padRight("\nSchool: ", INFO_LABEL_LENGTH));
            retVal.append(m_school);
        }
        String default_lang = ContestConstants.LANG_NAMES[m_lang];
        if ( default_lang != null && !default_lang.equals("")) {
        	retVal.append(StringUtil.padRight("\nDefault language: ", INFO_LABEL_LENGTH));
        	retVal.append(default_lang);
        }
        if ( m_quote != null && !m_quote.equals("") ){
            retVal.append(StringUtil.padRight("\nQuote: ", INFO_LABEL_LENGTH));
            retVal.append(m_quote);
            retVal.append("\n");
        }
        return retVal.toString();
    }

    /**
     * This is just to sort users based on rating, highest first.
     * @param o another User
     * @return compares based on rating
     */
    public int compareTo(Object o) {
        User user = (User) o;
        //Note: this assumes there is always an algo rating
        //Probably not the best way to do things
        return user.getRating(Rating.ALGO).getRating() - getRating(Rating.ALGO).getRating();
    }
}
