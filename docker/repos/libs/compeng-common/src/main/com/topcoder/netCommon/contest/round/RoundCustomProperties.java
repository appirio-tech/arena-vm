/*
 * RoundCustomProperties Created 09/12/2007
 */
package com.topcoder.netCommon.contest.round;

import java.io.Serializable;

import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.shared.language.Language;

/**
 * Defines an interface which contains detailed information of certain round type properties.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: RoundCustomProperties.java 72313 2008-08-14 07:16:48Z qliu $
 */
public interface RoundCustomProperties extends Serializable {
    /** Represents the coding time which means there is no different start time for coding phase for different users. */
    public static final Long NO_PER_USER_CODING_TIME = new Long(0);

    /**
     * Gets the coding time for each user if there is different start time for coding phase. It is in minutes.
     * 
     * @return the coding time for each different user.
     */
    Long getPerUserCodingTime();

    /**
     * Gets all allowed methods to show scores/status in the summary window on the client.
     * 
     * @return allowed methods to show scores/status.
     */
    ResultDisplayType[] getAllowedScoreTypesToShow();

    /**
     * Gets a flag indicating if the scores of other coders can be displayed.
     * 
     * @return <code>true</code> if showing other coders' scores is allowed; <code>false</code> otherwise.
     */
    Boolean getShowScoresOfOtherCoders();

    /**
     * Gets all allowed programming languages.
     * 
     * @return allowed programming languages.
     */
    Language[] getAllowedLanguages();

    /**
     * Gets a flag indicating if the programming language is allowed.
     * 
     * @param lang the programming language.
     * @return <code>true</code> if the programming langauage is allowed; <code>false</code> otherwise.
     */
    boolean allowsLanguage(Language lang);

    /**
     * Gets the coding time. This coding time overrides the default coding time. It is in minutes.
     * 
     * @return the coding time.
     */
    Long getCodingLengthOverride();
}