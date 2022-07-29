package com.topcoder.netCommon.mpsqas;

/**
 * Constants used in specifying data in messages.
 *
 * @author mitalub
 */
public class MessageConstants {

    /**Test how many*/
    public static final int TEST_ONE = 1;
    public static final int TEST_ALL = 2;

    /**ApplicationTypes*/
    public static final int TESTER_APPLICATION = 0;
    public static final int WRITER_APPLICATION = 1;

    /**Problem group describers for getting problems*/
    public static final int PROBLEMS_FOR_CONTEST = 1;
    public static final int SCHED_PROBLEMS_FOR_CONTEST = 2;
    public static final int PROBLEMS_WITH_STATUS = 3;
    public static final int USER_WRITTEN_PROBLEMS = 4;
    public static final int USER_TESTING_PROBLEMS = 5;
    public static final int ALL_PROBLEMS = 6;
    public static final int PENDING_APPROVAL_PROBLEMS = 7;
    public static final int PENDING_SUBMISSION_PROBLEMS = 8;

    /** types of statements */
    public static final int PROBLEM_STATEMENT = 1;
    public static final int COMPONENT_STATEMENT = 2;
}
