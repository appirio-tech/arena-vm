/**
 * Class Contest
 *
 * Author: Hao Kung
 *
 * Description: This class will contain all information about a contest and its rounds
 */
package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Category;

public final class Contest implements Serializable {

    /**
     * Category for logging.
     */
    private static Category s_trace = Category.getInstance(Contest.class.getName());

    private String m_cacheKey;

    public final String getCacheKey() {
        return m_cacheKey;
    }

    public static String getCacheKey(int id) {
        return "Contest:" + id;
    }

    /*
     * Constructors
     */
    public Contest(int contestId, String contestName, ArrayList rounds) {
        m_contestName = contestName;
        m_contestID = contestId;
        m_cacheKey = getCacheKey(m_contestID);
        m_rounds = rounds;
    }

    public String toString() {
        return "contestID=" + m_contestID + ", contestName=" + m_contestName +
                ", rounds=" + m_rounds;
    }

    /*
     * Data members/getter/setters
     */

    private int m_contestID;

    public final int getContestID() {
        return m_contestID;
    }
    //public final void setContestID(int id) { m_contestID = id;}

    private String m_contestName;

    public final String getContestName() {
        return m_contestName;
    }
    //public final void setContestName(String name) { m_contestName = name;}

    private ArrayList m_rounds = new ArrayList(10); // holds the ids of the contest rooms for this contest

    public Iterator getRounds() {
        return m_rounds.iterator();
    }

    public final void addRound(int roundID) {
        m_rounds.add(new Integer(roundID));
    }
}
