package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Category;

import com.topcoder.netCommon.contest.ContestConstants;

public final class Registration implements Serializable {

    /**
     * Category for logging.
     */
    private static Category s_trace = Category.getInstance(Registration.class.getName());

    private String m_cacheKey;

    public String getCacheKey() {
        return m_cacheKey;
    }

    public static String getCacheKey(int eventID) {
        return "Registration." + eventID;
    }

    private ArrayList m_invitees = new ArrayList(256);

    public final void addInviteList(int userid) {
        m_invitees.add(new Integer(userid));
    }

    public boolean isInvitationOnly() {
        return invitationType != ContestConstants.NOT_INVITATIONAL;
    }

    private int invitationType = ContestConstants.NOT_INVITATIONAL;

    public void setInvitationType(int type) {
        invitationType = type;
    }

    public boolean isInvited(int userid) {
        switch (invitationType) {
        case ContestConstants.NORMAL_INVITATIONAL:
            return m_invitees.contains(new Integer(userid));
        case ContestConstants.NEGATE_INVITATIONAL:
            return !m_invitees.contains(new Integer(userid));
        default:
            return true;
        }
    }
    
    private LinkedHashSet m_regList = new LinkedHashSet(512);

    public final void register(User u) {
        s_trace.info("Register: " + u);
        if (u == null) {
            return;
        }

        synchronized (m_regList) {
            Integer id = new Integer(u.getID());
            if (m_regList.contains(id)) {
                return;
            }
            m_regList.add(id);

            boolean added = false;
            for (int i = 0; !added && i < m_userRatings.size(); i++) {
                int rating = ((Integer) m_userRatings.get(i)).intValue();
                if (rating < u.getRating(m_ratingType).getRating()) {
                    m_userNames.add(i, u.getName());
                    m_userRatings.add(i, new Integer(u.getRating(m_ratingType).getRating()));
                    m_userCountries.add(i, u.getCountryName());
                    m_userTeams.add(i, u.getTeamName());
                    added = true;
                }
            }
            if (!added) {
                m_userNames.add(u.getName());
                m_userRatings.add(new Integer(u.getRating(m_ratingType).getRating()));
                m_userCountries.add(u.getCountryName());
                m_userTeams.add(u.getTeamName());
            }
        }
    }

    public final void unregister(User u) {
        s_trace.info("Unregister: " + u);
        if (u == null) {
            return;
        }

        synchronized (m_regList) {
            m_regList.remove(new Integer(u.getID()));
            int idx = m_userNames.indexOf(u.getName());
            if (idx != -1) {
                m_userNames.remove(idx);
                m_userRatings.remove(idx);
                m_userCountries.remove(idx);
                m_userTeams.remove(idx);
            }
        }
    }


    public boolean isRegistered(int userid) {
        synchronized (m_regList) {
            return m_regList.contains(new Integer(userid));
        }
    }

    private String m_iAgreeStr;

    public final void setIAgreeString(String str) {
        m_iAgreeStr = str;
    }

    public String getIAgreeString() {
        return m_iAgreeStr;
    }

    private int m_regLimit = 0;

    public final void setRegLimit(int limit) {
        m_regLimit = limit;
    }

    //public final int getRegLimit() { return m_regLimit;}
    public boolean isContestFull() {
        return m_regList.size() >= m_regLimit;
    }

    private ArrayList m_surveyQuestions = new ArrayList(4);

    public final void addSurveyQuestion(SurveyQuestion sq) {
        m_surveyQuestions.add(sq);
    }

    public final void addSurveyQuestions(List sqs) {
        m_surveyQuestions.addAll(sqs);
    }
    
    public final int numSurveyQuestions() {
        return m_surveyQuestions.size();
    }

    public boolean hasSurveyQuestions() {
        return m_surveyQuestions.size() > 0;
    }

    public Iterator getSurveyQuestions() {
        return m_surveyQuestions.iterator();
    }

    private int m_eventID;

    public String toString() {
        return "eventID=" + m_eventID + ", regLimit=" + m_regLimit + ", iAgreeStr=" + m_iAgreeStr +
                ", surveyQuestions=" + m_surveyQuestions + ", invitees=" + m_invitees + ", regList=" + m_regList +
                ", userNames=" + m_userNames + ", userRatings=" + m_userRatings + 
				", userCountries=" + m_userCountries;
    }

    private ArrayList m_userNames = new ArrayList();

    public final ArrayList getUserNames() {
        return m_userNames;
    }

    private ArrayList m_userRatings = new ArrayList();

    public final ArrayList getUserRatings() {
        return m_userRatings;
    }
    
    private ArrayList m_userCountries = new ArrayList();

    public final ArrayList getUserCountries() {
        return m_userCountries;
    }
    
    private ArrayList m_userTeams = new ArrayList();
    
    public final ArrayList getUserTeams() {
        return m_userTeams;
    }
    
    private int m_ratingType;

    public Registration(int eventID, int ratingType) {
        m_eventID = eventID;
        m_ratingType = ratingType;
        m_cacheKey = getCacheKey(m_eventID);
    }
    
    public int getRegisteredCount() {
        synchronized (m_regList) {
            return m_regList.size();
        }
    }
    
    public List getRegisteredUserIds() {
        synchronized (m_regList) {
            return new ArrayList(m_regList);
        }
    }
}
