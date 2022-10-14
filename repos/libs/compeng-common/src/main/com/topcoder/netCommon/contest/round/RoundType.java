/*
 * Copyright (C) 2007-2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.contest.round;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.netCommon.contest.round.text.ComponentNameBuilder;
import com.topcoder.netCommon.contest.round.text.NamedComponentNameBuilder;
import com.topcoder.netCommon.contest.round.text.PointsComponentNameBuilder;
import com.topcoder.netCommon.contest.round.text.ScoringTypeComponentNameBuilder;
import com.topcoder.server.common.Rating;
import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.language.Python3Language;
import com.topcoder.shared.language.PythonLanguage;
import com.topcoder.shared.language.VBLanguage;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;

/**
 * Defines a type of round. This class includes all common properties shared by a type of round.
 *
 * <p>
 * Changes in version 1.1 (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Added {@link #SRM_ROUND_QA_TYPE}  field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Python3 Support):
 * <ol>
 * <li>Updated {@link #allLanguages} field to add Python3 language.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn, liuliquan
 * @verion 1.2
 */
public class RoundType implements RoundTypeProperties, Serializable, ResolvedCustomSerializable {
    /** Represents the map of IDs and all round type singletons. */
    private static final Map allTypes = new HashMap(32);

    /** Represents the ID of the round type. */
    private int id;

    /** Represents if this type of round has a challenge phase. */
    private transient boolean hasChallengePhase;

    /** Represents if this type of round may have several divisions. */
    private transient boolean hasDivisions;

    /** Represents the type of ratings affected by this type. */
    private transient int ratingType;

    /**
     * Represents if this type of round uses different time of coding phase for different coders. Used for some 24-hour
     * TCO/TCCC qualification rounds.
     */
    private transient boolean allowsPerUserCodingTime;

    /** Represents if this type of round is a practice round. */
    private transient boolean practiceRound;

    /** Represents if this type of round is a marathon round. */
    private transient boolean longRound;

    /** Represents if this type of round is a highschool round. */
    private transient boolean hsRound;

    /** Represents if this type of round is a team-based round. */
    private transient boolean teamRound;

    /** Represents the custom properties of this type. */
    private transient RoundCustomProperties defaultRoundProperties;

    /** Represents if the system test will stop on one system test case failure. */
    private transient boolean mustStopSystemTestsOnFailure;

    /** Represents if this type of round is visible to already registered users. */
    private transient boolean visibleOnlyForRegisteredUsers;

    /** Represents if this type of round needs system room assignment. */
    private transient boolean useRoomAssignamentProcess;

    /** Represents if this type of round automatically ends the contest and publish the result after system tests. */
    private transient boolean autoEndContestAfterSystemTests;

    /** Represents if this type of round allows user to see the system test result during the contest. */
    private transient boolean summaryEnabledDuringContest;

    /** Represents if this type of round allows user to see the history of the coder in the contest. */
    private transient boolean coderHistoryEnabled;

    /** Represents if this type of round has a registration phase. */
    private transient boolean hasRegistrationPhase;

    /** Represents the component name builder for this type of round. */
    private transient ComponentNameBuilder componentNameBuilder;

    protected RoundType(int id, boolean hasChallengePhase, boolean hasDivisions, int ratingType,
        boolean partialCodingTime, boolean practiceRoundType, boolean longContestRoundType, boolean teamRoundType,
        boolean hsRoundType, boolean mustStopSystemTestsOnFailure, boolean visibleOnlyForRegisteredUser,
        boolean useRoomAssignamentProcess, boolean autoEndContestAfterSystemTests, boolean summaryEnabledDuringContest,
        boolean coderHistoryEnabled, boolean hasRegistrationPhase, ComponentNameBuilder componentNameBuilder,
        RoundCustomProperties defaultRoundProperties) {
        super();
        this.id = id;
        this.hasChallengePhase = hasChallengePhase;
        this.hasDivisions = hasDivisions;
        this.ratingType = ratingType;
        this.allowsPerUserCodingTime = partialCodingTime;
        this.practiceRound = practiceRoundType;
        this.longRound = longContestRoundType;
        this.teamRound = teamRoundType;
        this.hsRound = hsRoundType;
        this.mustStopSystemTestsOnFailure = mustStopSystemTestsOnFailure;
        this.visibleOnlyForRegisteredUsers = visibleOnlyForRegisteredUser;
        this.useRoomAssignamentProcess = useRoomAssignamentProcess;
        this.autoEndContestAfterSystemTests = autoEndContestAfterSystemTests;
        this.summaryEnabledDuringContest = summaryEnabledDuringContest;
        this.coderHistoryEnabled = coderHistoryEnabled;
        this.defaultRoundProperties = defaultRoundProperties;
        this.hasRegistrationPhase = hasRegistrationPhase;
        this.componentNameBuilder = componentNameBuilder;

        allTypes.put(new Integer(id), this);
    }

    /**
     * Gets the singleton of round type with the given ID.
     * 
     * @param roundTypeId the ID of the round type.
     * @return the singleton of round type with the ID.
     */
    public static RoundType get(int roundTypeId) {
        return get(new Integer(roundTypeId));
    }

    /**
     * Gets the singleton of round type with the given ID.
     * 
     * @param roundTypeId the ID of the round type.
     * @return the singleton of round type with the ID.
     */
    public static RoundType get(Integer roundTypeId) {
        return (RoundType) allTypes.get(roundTypeId);
    }

    public boolean hasChallengePhase() {
        return hasChallengePhase;
    }

    public boolean hasDivisions() {
        return hasDivisions;
    }

    /**
     * Gets the ID of the round type.
     * 
     * @return the ID of the round type.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets a flag indicating if the type of the round is a marathon round type.
     * 
     * @return <code>true</code> if the type is a marathon round type; <code>false</code> otherwise.
     */
    public boolean isLongRound() {
        return longRound;
    }

    public boolean allowsPerUserCodingTime() {
        return allowsPerUserCodingTime;
    }

    /**
     * Gets a flag indicating if the type of the round is a practice round type.
     * 
     * @return <code>true</code> if the type is a practice round type; <code>false</code> otherwise.
     */
    public boolean isPracticeRound() {
        return practiceRound;
    }

    public int getRatingType() {
        return ratingType;
    }

    /**
     * Gets a flag indicating if the type of the round is a highschool round type.
     * 
     * @return <code>true</code> if the type is a highschool round type; <code>false</code> otherwise.
     */
    public boolean isHsRound() {
        return hsRound;
    }

    /**
     * Gets a flag indicating if the type of the round is a team-based round type.
     * 
     * @return <code>true</code> if the type is a team-based round type; <code>false</code> otherwise.
     */
    public boolean isTeamRound() {
        return teamRound;
    }

    /**
     * Gets custom properties of the round type.
     * 
     * @return custom properties of the round type.
     */
    public RoundCustomProperties getDefaultRoundProperties() {
        return defaultRoundProperties;
    }

    public boolean mustStopSystemTestsOnFailure() {
        return mustStopSystemTestsOnFailure;
    }

    public boolean isVisibleOnlyForRegisteredUsers() {
        return visibleOnlyForRegisteredUsers;
    }

    public boolean useRoomAssignamentProcess() {
        return useRoomAssignamentProcess;
    }

    public boolean autoEndContestAfterSystemTests() {
        return autoEndContestAfterSystemTests;
    }

    public boolean hasRegistrationPhase() {
        return hasRegistrationPhase;
    }

    public boolean isSummaryEnabledDuringContest() {
        return summaryEnabledDuringContest;
    }

    public boolean isCoderHistoryEnabled() {
        return coderHistoryEnabled;
    }

    /**
     * Gets the component name builder for this type of round.
     * 
     * @return the component name builder which creates the name of a problem component.
     */
    public ComponentNameBuilder getComponentNameBuilder() {
        return componentNameBuilder;
    }

    public Object readResolve() {
        return get(id);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.id = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(this.id);
    }

    private static final Language[] allLanguages = new Language[] {JavaLanguage.JAVA_LANGUAGE,
        CPPLanguage.CPP_LANGUAGE, CSharpLanguage.CSHARP_LANGUAGE, VBLanguage.VB_LANGUAGE,
        PythonLanguage.PYTHON_LANGUAGE, Python3Language.PYTHON3_LANGUAGE};

    /**
     * @deprecated
     * @since BUGR-9137
     */
    private static final Language[] nonPythonLanguages = new Language[] {JavaLanguage.JAVA_LANGUAGE,
        CPPLanguage.CPP_LANGUAGE, CSharpLanguage.CSHARP_LANGUAGE, VBLanguage.VB_LANGUAGE};

    private static final Language[] cppLanguageOnly = new Language[] {CPPLanguage.CPP_LANGUAGE};

    private static final ResultDisplayType[] ALGO_VIEW_TYPES = new ResultDisplayType[] {ResultDisplayType.STATUS,
        ResultDisplayType.POINTS};

    private static final ResultDisplayType[] EDUCATION_VIEW_TYPES = new ResultDisplayType[] {ResultDisplayType.STATUS};

    /**
     * modify from <code>nonPythonLanguages</code> to <code>allLanguages</code>
     * @since BUGR-9137
     */
    private static final RoundCustomProperties ALGO_ROUNDS_PROPS = new RoundCustomPropertiesImpl(
        RoundCustomProperties.NO_PER_USER_CODING_TIME, true, ALGO_VIEW_TYPES, allLanguages, null);
    /**
     * modify from <code>nonPythonLanguages</code> to <code>allLanguages</code>
     * @since BUGR-9137
     */
    private static final RoundCustomProperties ALGO_PRACTICE_ROUNDS_PROPS = new RoundCustomPropertiesImpl(null, true,
        ALGO_VIEW_TYPES, allLanguages, new Long(75 * 60 * 1000));
    /**
     * modify from <code>nonPythonLanguages</code> to <code>allLanguages</code>
     * @since BUGR-9137
     */
    private static final RoundCustomProperties LONG_ALGO_ROUNDS_PROPS = new RoundCustomPropertiesImpl(new Long(
        60 * 60 * 1000), true, ALGO_VIEW_TYPES, allLanguages, null);

    /**
     * modify from <code>nonPythonLanguages</code> to <code>allLanguages</code>
     * @since BUGR-9137
     */
    private static final RoundCustomProperties EDUCATION_ALGO_ROUNDS_PROPS = new RoundCustomPropertiesImpl(
        RoundCustomProperties.NO_PER_USER_CODING_TIME, true, EDUCATION_VIEW_TYPES, allLanguages, null);

    private static final RoundCustomProperties MM_ROUNDS_PROPS = new RoundCustomPropertiesImpl(
        RoundCustomProperties.NO_PER_USER_CODING_TIME, true, ALGO_VIEW_TYPES, allLanguages, null);

    /**
     * modify from <code>nonPythonLanguages</code> to <code>allLanguages</code>
     * @since BUGR-9137
     */
    private static final RoundCustomProperties MM_INTEL_ROUNDS_PROPS = new RoundCustomPropertiesImpl(
        RoundCustomProperties.NO_PER_USER_CODING_TIME, true, ALGO_VIEW_TYPES, allLanguages, null);

    private static final RoundCustomProperties MM_AMD_ROUND_PROPS = new RoundCustomPropertiesImpl(
        RoundCustomProperties.NO_PER_USER_CODING_TIME, true, ALGO_VIEW_TYPES, cppLanguageOnly, null);

    private static final ComponentNameBuilder POINT_BUILDER = new PointsComponentNameBuilder();

    private static final ComponentNameBuilder NAMED_BUILDER = new NamedComponentNameBuilder();

    private static final ComponentNameBuilder MIXED_BUILDER = new ScoringTypeComponentNameBuilder();

    // id, hasChallengePhase, hasDivisions, ratingType, allowsPerUserCodingTime, practiceRound, longRound, teamRound,
    // hsRound, mustStopSystemTestsOnFailure, visibleOnlyForRegisteredUser, useRoomAssignamentProcess
    // autoEndContestAfterSystemTest summaryEnabledDuringContest, coderHistoryEnabled, hasRegistrationPhase,
    // componentNameBuilder, defaultRoundProperties
    /** Represents the type of all lobbies. */
    public static final RoundTypeProperties LOBBY_ROUND_TYPE = new RoundType(ContestConstants.LOBBY_ROUND_TYPE_ID,
        false, false, Rating.ALGO, false, false, false, false, false, true, false, true, false, true, true, true,
        POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all algorithm forwarded rounds. */
    public static final RoundTypeProperties FORWARDER_ROUND_TYPE = new RoundType(
        ContestConstants.FORWARDER_ROUND_TYPE_ID, true, true, Rating.ALGO, false, false, false, false, false, true,
        false, true, false, true, true, true, POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all algorithm single rounds. */
    public static final RoundTypeProperties SRM_ROUND_TYPE = new RoundType(ContestConstants.SRM_ROUND_TYPE_ID, true,
        true, Rating.ALGO, false, false, false, false, false, true, false, true, false, true, true, true,
        POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all algorithm single qa rounds. */
    public static final RoundTypeProperties SRM_ROUND_QA_TYPE = new RoundType(ContestConstants.SRM_QA_ROUND_TYPE_ID, true,
        true, Rating.ALGO, false, false, false, false, false, true, false, true, false, true, true, true,
        POINT_BUILDER, ALGO_ROUNDS_PROPS);
    
    /** Represents the type of all algorithm tournament rounds. */
    public static final RoundTypeProperties TOURNAMENT_ROUND_TYPE = new RoundType(
        ContestConstants.TOURNAMENT_ROUND_TYPE_ID, true, true, Rating.ALGO, false, false, false, false, false, true,
        false, true, false, true, true, true, POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all algorithm practice rounds. */
    public static final RoundTypeProperties PRACTICE_ROUND_TYPE = new RoundType(
        ContestConstants.PRACTICE_ROUND_TYPE_ID, true, false, Rating.ALGO, false, true, false, false, false, true,
        false, false, false, true, true, false, POINT_BUILDER, ALGO_PRACTICE_ROUNDS_PROPS);

    /** Represents the type of all moderated chat rooms. */
    public static final RoundTypeProperties MODERATED_CHAT_ROUND_TYPE = new RoundType(
        ContestConstants.MODERATED_CHAT_ROUND_TYPE_ID, true, false, Rating.ALGO, false, true, false, false, false,
        true, false, false, false, true, true, false, POINT_BUILDER, ALGO_PRACTICE_ROUNDS_PROPS);

    /** Represents the type of all team algorithm single rounds. */
    public static final RoundTypeProperties TEAM_SRM_ROUND_TYPE = new RoundType(
        ContestConstants.TEAM_SRM_ROUND_TYPE_ID, true, true, Rating.ALGO, false, false, false, true, false, true,
        false, true, false, true, true, true, NAMED_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all team algorithm tournament rounds. */
    public static final RoundTypeProperties TEAM_TOURNAMENT_ROUND_TYPE = new RoundType(
        ContestConstants.TEAM_TOURNAMENT_ROUND_TYPE_ID, true, true, Rating.ALGO, false, false, false, true, false,
        true, false, true, false, true, true, true, NAMED_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all team algorithm practice rounds. */
    public static final RoundTypeProperties TEAM_PRACTICE_ROUND_TYPE = new RoundType(
        ContestConstants.TEAM_PRACTICE_ROUND_TYPE_ID, true, false, Rating.ALGO, false, true, false, true, false, true,
        false, false, false, true, true, false, NAMED_BUILDER, ALGO_PRACTICE_ROUNDS_PROPS);

    /** Represents the type of all team algorithm 'Weakest Link' rounds. */
    public static final RoundTypeProperties WEAKEST_LINK_ROUND_TYPE = new RoundType(
        ContestConstants.WEAKEST_LINK_ROUND_TYPE_ID, true, true, Rating.ALGO, false, false, false, false, false, true,
        false, true, false, true, true, true, POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all algorithm private-labeled tournament rounds. E.g. Google Code Jam 2005. */
    public static final RoundTypeProperties PRIVATE_LABEL_TOURNAMENT_ROUND_TYPE = new RoundType(
        ContestConstants.PRIVATE_LABEL_TOURNAMENT_ROUND_TYPE_ID, true, true, Rating.ALGO, false, false, false, false,
        false, true, false, true, false, true, true, true, POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all algorithm 24-hour qualification rounds. */
    public static final RoundTypeProperties LONG_ROUND_TYPE = new RoundType(ContestConstants.LONG_ROUND_TYPE_ID, false,
        true, Rating.ALGO, true, false, false, false, false, true, false, true, false, true, true, false,
        POINT_BUILDER, LONG_ALGO_ROUNDS_PROPS);

    /** Represents the type of all algorithm college tour rounds. */
    public static final RoundTypeProperties INTRO_EVENT_ROUND_TYPE = new RoundType(
        ContestConstants.INTRO_EVENT_ROUND_TYPE_ID, true, true, Rating.ALGO, false, false, false, false, false, true,
        false, true, false, true, true, true, POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all marathon rounds. */
    public static final RoundTypeProperties LONG_PROBLEM_ROUND_TYPE = new RoundType(
        ContestConstants.LONG_PROBLEM_ROUND_TYPE_ID, false, false, Rating.MM, false, false, true, false, false, false,
        false, false, false, true, true, false, NAMED_BUILDER, MM_ROUNDS_PROPS);

    /** Represents the type of all marathon QA rounds. */
    public static final RoundTypeProperties LONG_PROBLEM_QA_ROUND_TYPE = new RoundType(
            ContestConstants.LONG_PROBLEM_QA_ROUND_TYPE_ID, false, false, Rating.MM, false, false, true, false, false, false,
            false, false, false, true, true, false, NAMED_BUILDER, MM_ROUNDS_PROPS);

    /** Represents the type of all marathon forwarded rounds. */
    public static final RoundTypeProperties FORWARDER_LONG_ROUND_TYPE = new RoundType(
        ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID, false, false, Rating.MM, false, false, true, false, false,
        false, false, false, false, true, true, false, NAMED_BUILDER, MM_ROUNDS_PROPS);

    /** Represents the type of all marathon practice rounds. */
    public static final RoundTypeProperties LONG_PROBLEM_PRACTICE_ROUND_TYPE = new RoundType(
        ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID, false, false, Rating.MM, false, true, true, false, false,
        false, false, false, false, true, true, false, NAMED_BUILDER, MM_ROUNDS_PROPS);

    /** Represents the type of all marathon Intel multithread rounds. */
    public static final RoundTypeProperties INTEL_LONG_PROBLEM_ROUND_TYPE = new RoundType(
        ContestConstants.INTEL_LONG_PROBLEM_ROUND_TYPE_ID, false, false, Rating.MM, false, false, true, false, false,
        false, false, false, false, true, true, false, NAMED_BUILDER, MM_INTEL_ROUNDS_PROPS);

    /** Represents the type of all marathon Intel multithread practice rounds. */
    public static final RoundTypeProperties INTEL_LONG_PROBLEM_PRACTICE_ROUND_TYPE = new RoundType(
        ContestConstants.INTEL_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID, false, false, Rating.MM, false, true, true, false,
        false, false, false, false, false, true, true, false, NAMED_BUILDER, MM_INTEL_ROUNDS_PROPS);

    /** Represents the type of all marathon tournament rounds. */
    public static final RoundTypeProperties LONG_PROBLEM_TOURNAMENT_ROUND_TYPE = new RoundType(
        ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID, false, false, Rating.MM, false, false, true, false,
        false, false, false, false, false, true, true, false, NAMED_BUILDER, MM_ROUNDS_PROPS);

    /** Represents the type of all marathon AMD multicore rounds. */
    public static final RoundTypeProperties AMD_LONG_PROBLEM_ROUND_TYPE = new RoundType(
        ContestConstants.AMD_LONG_PROBLEM_ROUND_TYPE_ID, false, false, Rating.MM, false, false, true, false, false,
        false, false, false, false, true, true, false, NAMED_BUILDER, MM_AMD_ROUND_PROPS);

    /** Represents the type of all marathon AMD multicore practice rounds. */
    public static final RoundTypeProperties AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE = new RoundType(
        ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID, false, false, Rating.MM, false, true, true, false,
        false, false, false, false, false, true, true, false, NAMED_BUILDER, MM_AMD_ROUND_PROPS);
    
    /** Represents the type of all marathon CUDA rounds. */
    public static final RoundTypeProperties CUDA_LONG_PROBLEM_ROUND_TYPE = new RoundType(
    		ContestConstants.CUDA_LONG_PROBLEM_ROUND_TYPE_ID, false, false, Rating.MM, false, false, true, false,
    		false, false, false, false, false,  true,  true, false, NAMED_BUILDER, MM_ROUNDS_PROPS);
    
    /** Represents the type of all marathon CUDA practice rounds. */
    public static final RoundTypeProperties CUDA_LONG_PROBLEM_PRACTICE_ROUND_TYPE = new RoundType(
    		ContestConstants.CUDA_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID, false, false, Rating.MM, false, true, true, false,
    		false, false, false, false, false,  true,  true, false, NAMED_BUILDER, MM_ROUNDS_PROPS);

    /** Represents the type of all highschool algorithm single rounds. */
    public static final RoundTypeProperties HS_SRM_ROUND_TYPE = new RoundType(ContestConstants.HS_SRM_ROUND_TYPE_ID,
        true, true, Rating.HS, false, false, false, false, true, true, false, true, false, true, true, true,
        POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all highschool algorithm tournament rounds. */
    public static final RoundTypeProperties HS_TOURNAMENT_ROUND_TYPE = new RoundType(
        ContestConstants.HS_TOURNAMENT_ROUND_TYPE_ID, true, true, Rating.HS, false, false, false, false, true, true,
        false, true, false, true, true, true, POINT_BUILDER, ALGO_ROUNDS_PROPS);

    /** Represents the type of all algorithm educational rounds. */
    public static final RoundTypeProperties EDUCATION_ALGO_ROUND_TYPE = new RoundType(
        ContestConstants.EDUCATION_ALGO_ROUND_TYPE_ID, false, false, Rating.ALGO, true, false, false, false, false,
        false, true, false, true, false, true, false, MIXED_BUILDER, EDUCATION_ALGO_ROUNDS_PROPS);
    
}
