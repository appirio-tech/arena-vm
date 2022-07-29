/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 20, 2002
 * Time: 3:25:41 AM
 */
package com.topcoder.server.common;

import com.topcoder.netCommon.contest.ContestConstants;

public abstract class BaseCoder implements Coder {

    private int m_language = ContestConstants.JAVA;
    private boolean m_eligible = false;
    // user object
    private int m_ID;
    private int m_rating;
    private int m_oldRating;
    // Username
    private String name;
    private boolean m_attended = false;
    // represents what contest he is in.
    private int m_contestID;
    private int m_roundID;
    private int m_divisionID;
    // what room is he assigned
    protected int m_roomID;

    public BaseCoder(int userID, String name, int div, Round contest, int roomID, int rating, int language) {
        m_ID = userID;
        this.name = name;
        m_contestID = contest.getContestID();
        m_roundID = contest.getRoundID();
        m_roomID = roomID;
        m_divisionID = div;
        m_rating = rating;
        m_language = language;
        //m_cacheKey = getCacheKey(m_userID, m_contestID, m_roundID);
    }

    public boolean isEligible() {
        return m_eligible;
    }

    public void setEligible(boolean value) {
        m_eligible = value;
    }

    public final int getID() {
        return m_ID;
    }

    public final int getRating() {
        return m_rating;
    }

    public final int getOldRating() {
        return m_oldRating;
    }

    public final void setOldRating(int r) {
        m_oldRating = r;
    }

    public final String getName() {
        return name;
    }

    public boolean getAttended() {
        return m_attended;
    }

    public void setAttended(boolean value) {
        m_attended = value;
    }

    public final int getContestID() {
        return m_contestID;
    }

    public final int getRoundID() {
        return m_roundID;
    }

    public final int getDivisionID() {
        return m_divisionID;
    }

    public final int getRoomID() {
        return m_roomID;
    }


    private int m_points;

    public int getPoints() {
        return m_points;
    }

    public void setPoints(int points) {
        m_points = points;
    }

    public int getLanguage() {
        return m_language;
    }

    private CoderHistory m_history = new CoderHistory();

    public final CoderHistory getHistory() {
        return m_history;
    }

    public final void setHistory(CoderHistory hist) {
        m_history = hist;
        m_points = m_history.getTotalPoints(); 
    }

    public String toString() {
        return "id=" + m_ID + ", userName=" + name + ", rating=" + m_rating + ", oldRating=" + m_oldRating +
                ", language=" + m_language + ", eligible=" + m_eligible + ", attended=" + m_attended +
                ", contestID=" + m_contestID + ", roundID=" + m_roundID + ", divisionID=" + m_divisionID +
                ", roomID=" + m_roomID + ", history=" + m_history + ", points=" + m_points;
    }

    public long[] getComponentIDs() {
        return null;
    }

    public int getFinalPoints() {
        return getPoints();
    }
    
    public abstract BaseCoderComponent newCoderComponent(int id, int componentId, int pointValue);
}
