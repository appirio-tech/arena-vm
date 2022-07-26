/*
 * RoundTypeProperties Created 09/12/2007
 */
package com.topcoder.netCommon.contest.round;

/**
 * Defines an interface which represents the common properties of a type of round.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: RoundTypeProperties.java 72092 2008-08-05 06:27:07Z qliu $
 */
public interface RoundTypeProperties {
    /**
     * Gets a flag indicating if this type of round has a challenge phase.
     * 
     * @return <code>true</code> if the type has a challenge phase; <code>false</code> otherwise.
     */
    boolean hasChallengePhase();

    /**
     * Gets a flag indicating if this type of round may have divisions.
     * 
     * @return <code>true</code> if the type may have divisions; <code>false</code> otherwise.
     */
    boolean hasDivisions();

    /**
     * Gets a flag indicating if this type of round has different start time of coding phase for different users.
     * 
     * @return <code>true</code> if the type has different start time of coding phase; <code>false</code> otherwise.
     */
    boolean allowsPerUserCodingTime();

    /**
     * Gets the type of ratings affected and used by this type of round.
     * 
     * @return the type of ratings affected and used by this type of round.
     */
    int getRatingType();

    /**
     * Gets a flag indicating if this type of round stops the system test of a solution when there is a failure in the
     * system test case.
     * 
     * @return <code>true</code> if the type stops the system test on failure; <code>false</code> otherwise.
     */
    boolean mustStopSystemTestsOnFailure();

    /**
     * Gets a flag indicating if this type of round is visible only to registered users.
     * 
     * @return <code>true</code> if the type is visible only to registered users; <code>false</code> otherwise.
     */
    boolean isVisibleOnlyForRegisteredUsers();

    /**
     * Gets a flag indicating if this type of round uses system room assignment.
     * 
     * @return <code>true</code> if the type uses system room assignment; <code>false</code> otherwise.
     */
    boolean useRoomAssignamentProcess();

    /**
     * Gets a flag indicating if this type of round automatically ends the contest and publishes result after system
     * tests.
     * 
     * @return <code>true</code> if the type automatically ends the contest after system tests; <code>false</code>
     *         otherwise.
     */
    boolean autoEndContestAfterSystemTests();

    /**
     * Gets a flag indicating if this type of round allows users to see the system test result during the contest.
     * 
     * @return <code>true</code> if the type allows users to see system test result during the contest;
     *         <code>false</code> otherwise.
     */
    boolean isSummaryEnabledDuringContest();

    /**
     * Gets a flag indicating if this type of round allows users to see the history of a coder in the round.
     * 
     * @return <code>true</code> if the type allows users to see the history of a coder; <code>false</code>
     *         otherwise.
     */
    boolean isCoderHistoryEnabled();

    /**
     * Gets a flag indicating if this type of round has a registration phase.
     * 
     * @return <code>true</code> if the type has a registration phase; <code>false</code> otherwise.
     */
    boolean hasRegistrationPhase();
}