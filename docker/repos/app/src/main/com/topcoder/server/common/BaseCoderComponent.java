/*
 * BaseCoderComponent
 * 
 * Created 05/31/2007
 */
package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.Formatters;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id: BaseCoderComponent.java 71140 2008-06-10 05:24:27Z dbelfer $
 */
public abstract class BaseCoderComponent implements Serializable {
    private static final Logger log = Logger.getLogger(BaseCoderComponent.class);
    
    private long openedTime = 0;
    private long submittedTime = 0;
    private final LinkedList m_viewers = new LinkedList();
    private int m_submitPoints;
    private final int m_componentID;
    private final int m_coderID;
    private String m_programText;
    private String m_submittedProgramText;
    private int m_submittedLanguage;
    private int m_status = ContestConstants.NOT_OPENED;
    private int m_language = ContestConstants.JAVA;
    private int m_pointValue = 250;

    
    public BaseCoderComponent(int coderID, int componentID, int pointValue) {
        m_coderID = coderID;
        m_componentID = componentID;
        m_pointValue = pointValue;
    }

    public final void addViewer(String name) {
        log.info(name + " is viewing problem [cID = " + m_coderID + ", comp_id=" + m_componentID + " , val = " + m_pointValue + "]");
        synchronized (m_viewers) {
            if (m_viewers.contains(name)) {
                log.error("Trying to add " + name + " to viewers but he already is in the list!!");
            } else {
                m_viewers.add(name);
            }
        }
    }

    public final void removeViewer(String name) {
        log.info(name + " is no longer viewing problem [cID = " + m_coderID + ", val = " + m_pointValue + "]");
        synchronized (m_viewers) {
            m_viewers.remove(name);
        }
    }

    public final Collection getViewers() {
        // return a copy so we don't worry about modification exceptions
        ArrayList viewers;
        synchronized (m_viewers) {
            viewers = new ArrayList(m_viewers);
        }
        return viewers;
    }

    public final int getSubmittedValue() {
        return m_submitPoints;
    }

    public final void setSubmittedValue(int pts) {
        m_submitPoints = pts;
    }

    public int getComponentID() {
        return m_componentID;
    }


    public int getCoderID() {
        return m_coderID;
    }

    public final String getProgramText() {
        return m_programText;
    }

    public final void setProgramText(String src) {
        m_programText = src;
    }

    public final String getSubmittedProgramText() {
        return m_submittedProgramText;
    }

    public final void setSubmittedProgramText(String src) {
        m_submittedProgramText = src;
    }

    public final int getSubmittedLanguage() {
        return m_submittedLanguage;
    }

    public final void setSubmittedLanguage(int languageID) {
        m_submittedLanguage = languageID;
    }

    public int getStatus() {
        return m_status;
    }

    public void setStatus(int status) {
        m_status = status;
    }

    // returns true if we need to run sys test on it, i.e. not challenge failed
    public String getStatusString() {
        switch (m_status) {
        case ContestConstants.NOT_OPENED:
            return "Unopened";
        case ContestConstants.LOOKED_AT:
//      case ContestConstants.PASSED:
        case ContestConstants.COMPILED_UNSUBMITTED:
            return "Opened";
        case ContestConstants.NOT_CHALLENGED:   // submitted
        case ContestConstants.CHALLENGE_FAILED:
            return Formatters.getDoubleString(m_submitPoints) + " points";
        case ContestConstants.CHALLENGE_SUCCEEDED:
            return "Challenge Succeeded";
        case ContestConstants.SYSTEM_TEST_FAILED:
            return "Failed System Test";
        case ContestConstants.SYSTEM_TEST_SUCCEEDED:
            return "Passed System Test";
        }
        return "Unopened";
    }
    
    public abstract boolean isWritable();

    public int getLanguage() {
        return m_language;
    }

    public void setLanguage(int language) {
        m_language = language;
    }

    public int getPointValue() {
        return m_pointValue;
    }

    public String toString() {
        return "componentID=" + m_componentID + ", submitPoints=" + m_submitPoints +
                ", status=" + m_status + ", language=" + m_language + ", pointValue=" + m_pointValue +
                ", programText=" + m_programText + ", submittedProgramText=" + m_submittedProgramText;
    }


    public int getEarnedPoints() {
        switch (m_status) {
        case ContestConstants.SYSTEM_TEST_SUCCEEDED:
        case ContestConstants.CHALLENGE_FAILED:
        case ContestConstants.NOT_CHALLENGED:   // submitted
            return getSubmittedValue();
        case ContestConstants.NOT_OPENED:
        case ContestConstants.LOOKED_AT:
        case ContestConstants.COMPILED_UNSUBMITTED:
        case ContestConstants.CHALLENGE_SUCCEEDED:
        case ContestConstants.SYSTEM_TEST_FAILED:
            return 0;
        }
        throw new IllegalStateException("Invalid component state: " + m_status);
    }

    public boolean isOpened() {
        return m_status >= ContestConstants.LOOKED_AT;
    }

    public boolean isSubmitted() {
        return m_status >= ContestConstants.NOT_CHALLENGED;
    }
    public boolean isSystemTested() {
        return m_status == ContestConstants.SYSTEM_TEST_FAILED ||
                m_status == ContestConstants.SYSTEM_TEST_SUCCEEDED;
    }

    public long getOpenedTime() {
        return openedTime;
    }

    public void setOpenedTime(long openedTime) {
        this.openedTime = openedTime;
    }

    public long getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(long submittedTime) {
        this.submittedTime = submittedTime;
    }
    
    
    protected void updateFrom(BaseCoderComponent source) {
        this.openedTime = source.openedTime;
        this.submittedTime = source.submittedTime;
        this.m_submitPoints = source.m_submitPoints;
        this.m_programText = source.m_programText;
        this.m_submittedProgramText = source.m_submittedProgramText;
        this.m_submittedLanguage = source.m_submittedLanguage;
        this.m_status = source.m_status;
        this.m_language = source.m_language;
        this.m_pointValue = source.m_pointValue;
    }
}
