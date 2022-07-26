/*
 * RoundProperties Created 09/12/2007
 */
package com.topcoder.netCommon.contest.round;

import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.shared.language.Language;

/**
 * Defines the properties of a round. The properties of a round contains common properties of the type of the round and
 * some custom properties. The custom properties may override the common properties.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: RoundProperties.java 72092 2008-08-05 06:27:07Z qliu $
 */
public class RoundProperties implements RoundCustomProperties, RoundTypeProperties {
    /** Represents the common properties shared by the type of round. */
    private RoundType typeProperties;

    /** Represents the custom properties belongs to this round only. */
    private RoundCustomProperties customProperties;

    /**
     * Represents a flag indicating if the name of the problem component is represented by scores. It is initialized
     * lazily.
     */
    private transient Boolean scoredRound;

    /**
     * Creates a new instance of <code>RoundProperties</code>. It is required by custom serialization.
     */
    public RoundProperties() {
    }

    /**
     * Creates a new instance of <code>RoundProperties</code>. The properties consists of common properties of the
     * type of the round, and custom properties.
     * 
     * @param typeProperties the type of the round.
     * @param customProperties the custom properties of the round.
     */
    public RoundProperties(RoundType typeProperties, RoundCustomProperties customProperties) {
        this.typeProperties = typeProperties;
        this.customProperties = customProperties;
    }

    public boolean allowsPerUserCodingTime() {
        return typeProperties.allowsPerUserCodingTime();
    }

    public int getRatingType() {
        return typeProperties.getRatingType();
    }

    public boolean hasChallengePhase() {
        return typeProperties.hasChallengePhase();
    }

    public boolean hasDivisions() {
        return typeProperties.hasDivisions();
    }

    public boolean isVisibleOnlyForRegisteredUsers() {
        return typeProperties.isVisibleOnlyForRegisteredUsers();
    }

    public boolean mustStopSystemTestsOnFailure() {
        return typeProperties.mustStopSystemTestsOnFailure();
    }

    public boolean useRoomAssignamentProcess() {
        return typeProperties.useRoomAssignamentProcess();
    }

    public boolean autoEndContestAfterSystemTests() {
        return typeProperties.autoEndContestAfterSystemTests();
    }

    public boolean isSummaryEnabledDuringContest() {
        return typeProperties.isSummaryEnabledDuringContest();
    }

    public boolean isCoderHistoryEnabled() {
        return typeProperties.isCoderHistoryEnabled();
    }

    public boolean hasRegistrationPhase() {
        return typeProperties.hasRegistrationPhase();
    }

    public ResultDisplayType[] getAllowedScoreTypesToShow() {
        if (customProperties.getAllowedScoreTypesToShow() == null) {
            return typeProperties.getDefaultRoundProperties().getAllowedScoreTypesToShow();
        }
        return customProperties.getAllowedScoreTypesToShow();
    }

    public Long getPerUserCodingTime() {
        if (customProperties.getPerUserCodingTime() == null) {
            return typeProperties.getDefaultRoundProperties().getPerUserCodingTime();
        }
        return customProperties.getPerUserCodingTime();
    }

    public Boolean getShowScoresOfOtherCoders() {
        if (customProperties.getShowScoresOfOtherCoders() == null) {
            return typeProperties.getDefaultRoundProperties().getShowScoresOfOtherCoders();
        }
        return customProperties.getShowScoresOfOtherCoders();
    }

    public Language[] getAllowedLanguages() {
        if (customProperties.getAllowedLanguages() == null) {
            return typeProperties.getDefaultRoundProperties().getAllowedLanguages();
        }
        return customProperties.getAllowedLanguages();
    }

    public boolean allowsLanguage(Language lang) {
        if (customProperties.getAllowedLanguages() == null) {
            return typeProperties.getDefaultRoundProperties().allowsLanguage(lang);
        }
        return customProperties.allowsLanguage(lang);
    }

    public Long getCodingLengthOverride() {
        if (customProperties.getCodingLengthOverride() == null) {
            return typeProperties.getDefaultRoundProperties().getCodingLengthOverride();
        }
        return customProperties.getCodingLengthOverride();
    }

    /**
     * Gets a flag indicating if different start time of coding phase for different users should be used.
     * 
     * @return <code>true</code> if different start time of coding phase should be used; <code>false</code>
     *         otherwise.
     */
    public boolean usesPerUserCodingTime() {
        return allowsPerUserCodingTime()
            && !RoundProperties.NO_PER_USER_CODING_TIME.equals(this.getPerUserCodingTime());
    }

    /**
     * Gets a flag indicating if the problem component name should be represented by scores.
     * 
     * @return <code>true</code> if the problem component name should be represented by scores; <code>false</code>
     *         otherwise.
     */
    public boolean usesScore() {
        if (scoredRound == null) {
            scoredRound = Boolean.valueOf(allowsScoreType(ResultDisplayType.POINTS));
        }
        return scoredRound.booleanValue();
    }

    /**
     * Gets a flag indicating if the method is allowed to show scores/status in the summary window on the client.
     * 
     * @param resultDisplayType the method to show scores/status.
     * @return <code>true</code> if the displaying method is allowed; <code>false</code> otherwise.
     */
    public boolean allowsScoreType(ResultDisplayType resultDisplayType) {
        ResultDisplayType[] scores = getAllowedScoreTypesToShow();
        for (int i = 0; i < scores.length; i++) {
            if (resultDisplayType.equals(scores[i])) {
                return true;
            }
        }
        return false;
    }
}
