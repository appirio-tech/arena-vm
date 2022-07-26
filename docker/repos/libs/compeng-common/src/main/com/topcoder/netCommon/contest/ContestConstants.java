/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.contest;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import com.topcoder.netCommon.contest.round.RoundType;
/**
 * <p>
 * the contest constant variables definition.
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - R Language Compilation Support):
 * <ol>
 * <li>Added {@link #R} field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Added {@link #BATCH_TEST} constants for request type of BatchTestRequest.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, dexy
 * @version 1.2
 */
public final class ContestConstants implements RoundTypes {

    private ContestConstants() {
    }

    public static final int PROTOCOL_VERSION = 2;

    public static final int TC_RATING = 1;
    public static final int TCHS_RATING = 2;
    public static final int MM_RATING = 3;

    public static final int RANDOM_SEEDING = 1;
    public static final int IRON_MAN_SEEDING = 2;
    public static final int NCAA_STYLE = 3;
    public static final int EMPTY_ROOM_SEEDING = 4;
    public static final int WEEKEST_LINK_SEEDING = 5;
    public static final int ULTRA_RANDOM_SEEDING = 6;
    public static final int TCO05_SEEDING = 7;
    public static final int DARTBOARD_SEEDING = 8;
    public static final int TCHS_SEEDING = 9;
    public static final int ULTRA_RANDOM_DIV2_SEEDING = 10;


    // Special column types
    public static final int COLUMN_TYPE_BLOB = 11;
    public static final int COLUMN_TYPE_TEXT = 12;

    // DIVISION types
    public static final int DIVISION_ADMIN = -1;
    public static final int DIVISION_ONE = 1;
    public static final int DIVISION_TWO = 2;

    //Room types, db
    public static final int ADMIN_ROOM_TYPE_ID = 1;
    public static final int TEAM_ADMIN_ROOM_TYPE_ID = 8;
    public static final int CONTEST_ROOM_TYPE_ID = 2;
    public static final int PRACTICE_ROOM_TYPE_ID = 3;
    public static final int MODERATED_CHAT_ROOM_TYPE_ID = 4;
    public static final int LOBBY_ROOM_TYPE_ID = 5;
    public static final int TEAM_CONTEST_ROOM_TYPE_ID = 6;
    public static final int TEAM_PRACTICE_ROOM_TYPE_ID = 7;

    public static final boolean isPracticeRoomTypeID(int type) {
        return type == ADMIN_ROOM_TYPE_ID || type == TEAM_ADMIN_ROOM_TYPE_ID || type == PRACTICE_ROOM_TYPE_ID
            || type == MODERATED_CHAT_ROOM_TYPE_ID || type == LOBBY_ROOM_TYPE_ID || type == TEAM_PRACTICE_ROOM_TYPE_ID;
    }

    //TODO.. why don't these ROOM and ROOM_TYPE_ID's match up???
    // Desination types
    public static final int INVALID_ROOM = -2;
    public static final int ANY_ROOM = -1;
    public static final int LOGIN_ROOM = 0;
    public static final int SPECTATOR_ROOM = 1;
    public static final int CODER_ROOM = 2;
    public static final int TEAM_CODER_ROOM = 9;
    public static final int LOBBY_ROOM = 3;
    public static final int PRACTICE_CODER_ROOM = 4;
    public static final int TEAM_PRACTICE_CODER_ROOM = 10;
    public static final int PRACTICE_SPECTATOR_ROOM = 5;
    public static final int WATCH_ROOM = 6;
    public final static int MODERATED_CHAT_ROOM = 8;
    public static final int ADMIN_ROOM = 12;
    public static final int TEAM_ADMIN_ROOM = 13;

    // This is dummy room type, representing entering a contest
    public static final int CONTEST_ROOM = 7;

    public static final boolean isPracticeRoomType(int roomType) {
        return roomType == PRACTICE_CODER_ROOM ||
            roomType == TEAM_PRACTICE_CODER_ROOM ||
            roomType == ADMIN_ROOM ||
            roomType == TEAM_ADMIN_ROOM;  // TODO - is this right?
    }

    public static final int ADMIN_LOBBY_ROOM_ID = 0;

    //broadcast types
    public static final int BROADCAST_TYPE_ADMIN_GENERIC = 0;
    public static final int BROADCAST_TYPE_ADMIN_ROUND = 1;
    public static final int BROADCAST_TYPE_ADMIN_COMPONENT = 2;

    public static final int BROADCAST_ROUND_ALL = -2;

    //Response info:

    //UPDATE_PREFERENCES types
    public final static int LANGUAGE = 0;
    public final static int EDITOR = 1;

    //MENU types
    //public final static int      ACTIVE_CONTEST_MENU            = 0;
    //public final static int      ACTIVE_ROOM_MENU               = 1;
    public final static int PRACTICE_ROOM_MENU = 2;
    public static final int LOBBY_MENU = 3;
    /*
     * Da Twink Daddy - 05/07/2002 - Added ACTIVE_CHAT_MENU = 3
     */
    /** Constant indicating the "Active Chats" menu.  */
    public final static int ACTIVE_CHAT_MENU = 4;

    //USER_LIST types
    public final static int ROOM_USERS = 0;
    public final static int ASSIGNED_USERS = 1;
    public final static int ACTIVE_USERS = 2;
    public final static int REGISTERED_USERS = 3;
    public final static int TEAM_AVAILABLE_USERS = 4;
    public final static int TEAM_MEMBER_USERS = 5;

    //USER_LIST_UPDATE types
    public final static int ADD = 0;
    public final static int REMOVE = 1;
    //public final static int      CHANGE                         = 2;

    //POP_UP_GENERIC types
    public final static int CONTEST_REGISTRATION = 0;
    public final static int ROOM_MOVE = 1;
    public final static int GENERIC = 2;
    public final static int CONTEST_REGISTRATION_SURVEY = 4;
    public final static int SUBMIT_RESULTS = 5;

    public final static int TEXT_AREA = 0;
    public final static int LABEL = 1;
    public final static int WRAPPING_TEXT_AREA = 2;

    //CHAT types
    public final static int USER_CHAT = 0;
    public final static int SYSTEM_CHAT = 1;
    public final static int EMPH_SYSTEM_CHAT = 2;
    public final static int IRC_CHAT = 3;
    /** Indicates chat text is an approved question.  */
    public final static int MODERATED_CHAT_QUESTION_CHAT = 4;
    /**
     * Indicates chat text is from the speaker in the moderated chat.
     */
    public final static int MODERATED_CHAT_SPEAKER_CHAT = 5;

    public final static int WHISPER_TO_YOU_CHAT = 6;

    //CHAT scopes
    public final static int GLOBAL_CHAT_SCOPE = 1;
    public final static int TEAM_CHAT_SCOPE = 2;

    //GET_PROBLEM types
    public final static int VIEW_SOURCE = 1000;
    public final static int EDIT_SOURCE_RW = 1001;
    public final static int EDIT_SOURCE_RO = 1002;

    // Survey question types
    public final static int RADIO_BUTTON = 1;
    public final static int CHECK_BOX = 2;
    public final static int SHORT_ANSWER = 3;
    public final static int LONG_ANSWER = 4;

    //Internal types
    //public final static int      SUBMISSION_CHALLENGE_IN        = 200;
    //public final static int      LEADER_BOARD_ROW_IN            = 201;
    //public final static int      OPEN_PROBLEM_IN                = 202;
    //public final static int      REGISTRATION_INFO_IN           = 203;
    //public final static int      CREATE_CHALLENGE_TABLE_IN      = 204;
    //public final static int      ACTIVATE_CONTEST_IN            = 205;
    //public final static int      PRINT_USER_CACHE               = 206;
    //public final static int      SIMPLE_ERROR                   = 207;
    //public final static int      EXIT_PLAYER                    = 20;
    //public final static int      ROUND_START_STOP               = 21;
    //public final static int      SYSTEMTEST                     = 29;
    //public final static int      ADMIN_BROADCAST                = 45;
    //public final static int      REMOVE_SERVER                  = 48;
    //public final static int      INIT_SSM                       = 49;
    //public final static int      NEW_CODER                      = 50;
    //public final static int      RESET                          = 51;
    //public final static int      PRINT_CACHE                    = 52;
    //public final static int      ADD_TIME                       = 59;
    //public final static int      NEW_LEADERBOARD                = 65;
    //public final static int      SIMPLE_MESSAGE                 = 208;// Normal dialog box
    //public final static int      SIMPLE_BIG_MESSAGE             = 209;// Horizontal scrollbar

    //Request types
    public final static int CHALLENGE = 0;
    public final static int CHALLENGE_INFO = 1;
    public final static int CHAT = 3;
    public final static int COMPILE = 4;
    public final static int ENTER = 5;
    public final static int ERROR = 6;
    public final static int GUEST_LOGIN = 7;
    public final static int LOGIN = 9;
    public final static int LOGOUT = 10;
    public final static int MOVE = 11;
    public final static int ENTER_ROUND = 12;
    public final static int SUBMIT_PROBLEM = 14;
    public final static int TEST = 15;
    public final static int TEST_INFO = 16;
    public final static int GET_PROBLEM = 30;
    public final static int SAVE = 34;
    public final static int CODER_HISTORY = 37;
    public final static int GET_CHALLENGE_PROBLEM = 40;
    public final static int SEARCH = 41;
    public final static int CODER_INFO = 42;
    public final static int TOGGLE_CHAT = 43;
    public final static int SET_LANGUAGE = 44;
    public final static int CLEAR_PRACTICER = 53;
    public final static int REGISTER_INFO = 54;
    public final static int REGISTER = 55;
    public final static int REGISTER_USERS = 61;
    public final static int WATCH = 62;
    public final static int UNWATCH = 63;
    public final static int PRACTICE_SYSTEM_TEST = 64;
    public final static int LOGGED_IN_USERS = 68;
    public final static int POP_UP_GENERIC_RQ = 71;
    public final static int CLOSE_PROBLEM = 72;
    public final static int SPECTATOR_LOGIN = 73;
    public final static int GET_LEADER_BOARD = 74;
    public final static int CLOSE_LEADER_BOARD = 75;
    public final static int SPECTATOR_REQUEST = 76;
    public final static int KEEP_ALIVE_REQUEST = 77;
    //public static final int TUNNEL_IP_REQUEST = 78;
    public static final int OPEN_SUMMARY_REQUEST = 79;
    public static final int CLOSE_SUMMARY_REQUEST = 80;
    public final static int GET_TEAM_LIST = 78;
    public final static int CLOSE_TEAM_LIST = 79;
    public final static int JOIN_TEAM = 80;
    public final static int LEAVE_TEAM = 81;
    public final static int ADD_TEAM_MEMBER = 82;
    //public final static int      REMOVE_TEAM_MEMBER             = 83;
    public final static int VISITED_PRACTICE = 83;
    public static final int GET_ADMIN_BROADCAST = 100;
    public static final int ASSIGN_COMPONENT = 101;
    public static final int UNASSIGN_COMPONENT = 102;
    public static final int REGISTER_ROOM = 103;
    public static final int REGISTER_WEAKEST_LINK_TEAM = 104;
    public final static int GET_COMPONENT = 105;
    public static final int ASSIGN_COMPONENTS = 106;
    public final static int CLEAR_PRACTICE_PROBLEM = 107;
    public final static int AUTO_SYSTEST = 108;
    public final static int SYSTEST_RESULTS = 109;
    /**
     * Request type for BatchTestRequest.
     * @since 1.2.
     */
    public final static int BATCH_TEST = 124;



    //reconnect request
    public final static int RECONNECT = 110;

    public final static int READ_MESSAGE_REQUEST = 111;
    public final static int GET_IMPORTANT_MESSAGES_REQUEST = 112;
    public final static int SYNCH_TIME_REQUEST = 113;
    public final static int GET_CURRENT_APPLET_VERSION_REQUEST = 114;

    public final static int FORWARDER_LOGIN = 115;

    // verification
    public final static int VERIFY_REQUEST = 116;
    public final static int VERIFY_RESULT_REQUEST = 117;
    public final static String VERIFY_CLASS_NAME = "com.topcoder.temporary.Verify";
    public final static String VERIFY_METHOD_NAME = "verify";

    // error reporting
    public final static int ERROR_REPORT = 118;

    //Long Contest submit
    public final static int LONG_SUBMIT_REQUEST = 119;
    public final static int LONG_TEST_RESULTS_REQUEST = 120;
    public final static int GET_SOURCE_CODE_REQUEST = 121;
    public final static int VIEW_QUEUE_REQUEST = 122;

    // Encryption
    public final static int EXCHANGE_KEY_REQUEST = 123;

    public static final boolean isPracticeRoundType(int roundType) {
        return RoundType.get(roundType).isPracticeRound();
    }

    public static boolean isLongRoundType(Integer roundType) {
        return RoundType.get(roundType).isLongRound();
    }

    //INVITATIONAL TYPES
    public static final int NOT_INVITATIONAL = 0;
    public static final int NORMAL_INVITATIONAL = 1;
    public static final int NEGATE_INVITATIONAL = 2;

    //PROBLEM TYPES
    public static final int SINGLE_PROBLEM_TYPE_ID = 1;
    public static final int TEAM_PROBLEM_TYPE_ID = 2;

    // COMPONENT TYPES
    public static final int COMPONENT_TYPE_MAIN = 1;
    //public static final int COMPONENT_TYPE_SUPPORTING       = 2;

    //Clear practice rooms type
    public static final int CLEAR_PRACTICE_NOT_OPEN = 0;
    public static final int CLEAR_PRACTICE_NOT_SUBMIT = 1;
    public static final int CLEAR_PRACTICE_NOT_SUBMIT_RECENT = 2;
    public static final int CLEAR_PRACTICE_ALL = 3;

    // Phase constants
    /** An int constant representing "inactive" phase of contest.*/
    public static final int INACTIVE_PHASE = 0;

    /** An int constant representing "starts in" phase of contest.*/
    public static final int STARTS_IN_PHASE = 1;

    /** An int constant representing "registration" phase of contest.*/
    public static final int REGISTRATION_PHASE = 2;

    /** An int constant representing "almost contest" phase of contest.*/
    public static final int ALMOST_CONTEST_PHASE = 3;

    /** An int constant representing "coding" phase of contest.*/
    public static final int CODING_PHASE = 4;

    /** An int constant representing "intermission" phase of contest.*/
    public static final int INTERMISSION_PHASE = 5;

    /** An int constant representing "challenge" phase of contest.*/
    public static final int CHALLENGE_PHASE = 6;

    /** An int constant representing "pending system tests" phase of contest.*/
    public static final int PENDING_SYSTESTS_PHASE = 7;

    /** An int constant representing "system testing" phase of contest.*/
    public static final int SYSTEM_TESTING_PHASE = 8;

    /** An int constant representing "contest complete" phase of contest.*/
    public static final int CONTEST_COMPLETE_PHASE = 9;

    /** An int constant representing "voting" phase of contest.*/
    public final static int VOTING_PHASE = 10;

    /** An int constant representing "tie breaking voting" phase of contest.*/
    public final static int TIE_BREAKING_VOTING_PHASE = 11;

    /** An int constant representing "moderated chatting" phase of contest.*/
    public final static int MODERATED_CHATTING_PHASE = 12;

    public static final int REGISTRATION_SEGMENT_ID = 1;
    public static final int CODING_SEGMENT_ID = 2;
    public static final int INTERMISSION_SEGMENT_ID = 3;
    public static final int CHALLENGE_SEGMENT_ID = 4;
    public static final int SYSTEM_TEST_SEGMENT_ID = 5;
    /*added by SYHAAS 2002-05-18*/
    public static final int MODERATED_CHAT_SEGMENT_ID = 6;

    public static final int[] SEGMENTS = {REGISTRATION_SEGMENT_ID,
                                          CODING_SEGMENT_ID,
                                          INTERMISSION_SEGMENT_ID,
                                          CHALLENGE_SEGMENT_ID,
                                          SYSTEM_TEST_SEGMENT_ID,
                                          MODERATED_CHAT_SEGMENT_ID};

    public static final String[] SEGMENT_NAMES = {"Registration",
                                                  "Coding",
                                                  "Intermission",
                                                  "Challenge",
                                                  "System Testing",
                                                  "Moderated Chat"};
    /**
     * An array holding the final static constants representing contest
     * phases. This array should be used in conjunction with PHASE_NAMES
     * array to emulate the mapping of final static constants representing
     * contest phases to their respective String representations.<p>
     * This array must have the same number of elements as PHASE_NAMES array.<p>
     * <i>i</code>th element of PHASE_NAMES array corresponds to constant
     * representing Contest Phase ID contained in <i>i</i>th element of this
     * array.<p>
     * In case of necessity of addition of new Phase ID constant or removal of
     * existing Phase ID constant new element containing added constant should
     * be added to this array or element containing removed constant should be
     * removed from this array respectively.
     *
     * @since  Admin Tool 2.0
     *
     * @see    INACTIVE_PHASE
     * @see    STARTS_IN_PHASE
     * @see    REGISTRATION_PHASE
     * @see    ALMOST_CONTEST_PHASE
     * @see    CODING_PHASE
     * @see    INTERMISSION_PHASE
     * @see    CHALLENGE_PHASE
     * @see    PENDING_SYSTESTS_PHASE
     * @see    SYSTEM_TESTING_PHASE
     * @see    CONTEST_COMPLETE_PHASE
     * @see    VOTING_PHASE
     * @see    TIE_BREAKING_VOTING_PHASE
     * @see    MODERATED_CHATTING_PHASE
     */
    public static final int[] PHASES = {INACTIVE_PHASE,
                                        STARTS_IN_PHASE,
                                        REGISTRATION_PHASE,
                                        ALMOST_CONTEST_PHASE,
                                        CODING_PHASE,
                                        INTERMISSION_PHASE,
                                        CHALLENGE_PHASE,
                                        PENDING_SYSTESTS_PHASE,
                                        SYSTEM_TESTING_PHASE,
                                        CONTEST_COMPLETE_PHASE,
                                        VOTING_PHASE,
                                        TIE_BREAKING_VOTING_PHASE,
                                        MODERATED_CHATTING_PHASE};

    /**
     * An array holding the String representations of final static constants
     * representing contest phases. This array should be used in conjunction
     * with PHASES array to emulate the mapping of final static constants
     * representing contest phases to their respective String representations.
     * This array must have the same number of elements as PHASE array.<p>
     * A final static constant representing Contest Phase ID contained in<i>i
     * </code>th element of PHASE array corresponds to String representation
     * contained in <i>i</i>th element of this array.
     * In case of necessity of addition of new Phase ID constant or removal of
     * existing Phase ID constant new element containing the String
     * representation of added constant should be added to this array or element
     * containing the String representation of removed constant should be
     * removed from this array respectively.
     *
     * @since  Admin Tool 2.0
     *
     * @see    INACTIVE_PHASE
     * @see    STARTS_IN_PHASE
     * @see    REGISTRATION_PHASE
     * @see    ALMOST_CONTEST_PHASE
     * @see    CODING_PHASE
     * @see    INTERMISSION_PHASE
     * @see    CHALLENGE_PHASE
     * @see    PENDING_SYSTESTS_PHASE
     * @see    SYSTEM_TESTING_PHASE
     * @see    CONTEST_COMPLETE_PHASE
     * @see    VOTING_PHASE
     * @see    TIE_BREAKING_VOTING_PHASE
     * @see    MODERATED_CHATTING_PHASE
     */
    public static final String[] PHASE_NAMES = {"Inactive",
                                                "Starts in",
                                                "Registration",
                                                "Almost contest",
                                                "Coding",
                                                "Intermission",
                                                "Challenge",
                                                "Pending systests",
                                                "System testing",
                                                "Contest complete",
                                                "Voting",
                                                "Tie breaking",
                                                "Moderated chatting"};

    public static final int SPECTATOR_CONTESTINFO = 77700;
    public static final int SPECTATOR_ANNOUNCEMENTS = 77701;
    public static final int COMPONENT_CONTEST_APPEALS = 77702;
    public static final int COMPONENT_CONTEST_RESULTS = 77703;
    public static final int COMPONENT_CONTEST_END = 77704;

    public static final int[] SPECTATOR_PHASES = {INACTIVE_PHASE,
                                                  STARTS_IN_PHASE,
                                                  REGISTRATION_PHASE,
                                                  CODING_PHASE,
                                                  INTERMISSION_PHASE,
                                                  CHALLENGE_PHASE,
                                                  PENDING_SYSTESTS_PHASE,
                                                  SYSTEM_TESTING_PHASE,
                                                  CONTEST_COMPLETE_PHASE,
                                                  VOTING_PHASE,
                                                  TIE_BREAKING_VOTING_PHASE,
                                                  SPECTATOR_CONTESTINFO,
                                                  SPECTATOR_ANNOUNCEMENTS,
                                                  COMPONENT_CONTEST_APPEALS,
                                                  COMPONENT_CONTEST_RESULTS,
                                                  COMPONENT_CONTEST_END,
    };

    public static final String[] SPECTATOR_PHASE_NAMES = {"None",
                                                          "Pre-Contest",
                                                          "Pre-Contest",
                                                          "Coding",
                                                          "Intermission",
                                                          "Challenge",
                                                          "Pending Tests",
                                                          "System Testing",
                                                          "Post-Contest",
                                                          "Voting",
                                                          "Tie-Break",
                                                          "Contest Info",
                                                          "Announcements",
                                                          "Appeals",
                                                          "Results",
                                                          "Complete"
    };

    public static boolean isTeamProblem(int problemTypeID) {
        return problemTypeID == TEAM_PROBLEM_TYPE_ID;
    }

    //public final static String[] HEADERS                        = {"Coder", "250 point", "500 point", "1000 point", "Score"};
    public final static String PRACTICE_STATUS = "Coding, viewing source, and challenging are always enabled in practice rooms.";

    // Regarding challenging a piece of code after you failed once
    public static boolean ACCEPT_REPEAT_CHALLENGES = true;

    // Regarding many people challenging the same piece of code
    public static final boolean ACCEPT_MULTIPLE_CHALLENGES = false;

    // Regarding multiple submissions
    //Quick fix to avoid recompilation. A better way should be implemented
    public static final boolean ACCEPT_MULTIPLE_SUBMISSIONS = Boolean.valueOf(System.getProperty("contestconstants.ACCEPT_MULTIPLE_SUBMISSIONS", "true")).booleanValue();

    //*************************

    // Qualification Request/Response  types

    public final static int SURVEY_QUESTION = 1;
    public final static int ELIGIBLE_QUESTION = 2;

    //public final static int CODERANSWERS = 100;
    //public final static int VALIDATEQUALIFICATIONATTEMPT = 101;
    //public final static int QUALLOGIN = 102;
    //public final static int SETQUESTION = 103;
    //public final static int QUALRESPONSE = 104;
    //public final static int GETQUESTION = 105;
    //public final static int DISCONNECT = 106;

    // Problem difficulty
    //public final static int EASY_POINT_VAL = 250;
    //public final static int MED_POINT_VAL = 500;
    //public final static int HARD_POINT_VAL = 1000;
    public final static int UNSUCCESSFUL_CHALLENGE = -25;
    public final static int EASY_CHALLENGE = 50;
    //public final static int MED_CHALLENGE = 50;
    //public final static int HARD_CHALLENGE = 50;
    public final static int SUCCESSIVE_CHALLENGE = 50;

    // Coder problem stati
    public final static int NOT_OPENED = 110;// Default
    public final static int REASSIGNED = 111;  //Team problem that has been assigned to another team member
    public final static int LOOKED_AT = 120;//Opened. Not yet compiled
    public final static int COMPILED_UNSUBMITTED = 121;// Compiled, but not yet submitted
    public final static int PASSED = 122;// Moving on without submitting
    public final static int NOT_CHALLENGED = 130;// Submitted
    public final static int CHALLENGE_FAILED = 131;
    public final static int CHALLENGE_SUCCEEDED = 140;
    public final static int SYSTEM_TEST_FAILED = 160;
    public final static int SYSTEM_TEST_SUCCEEDED = 150;

    /*
      public static boolean isCompiled(int status) {
      return status >= COMPILED_UNSUBMITTED;
      }
    */

    //challenge stati
    public final static int NORMAL_CHALLENGE = 90;
    //public final static int OVERTURNED_CHALLENGE = 91;
    public final static int NULLIFIED_CHALLENGE = 92;

    //ProblemFile Types
    public final static int SUBMITTED_CLASS = 2;
    public final static int COMPILED_CLASS = 1;

    public final static int TIMEOUT_MILLIS = 30 * 1000;


    //user types
    public final static int SINGLE_USER = 1;
    public final static int TEAM_USER = 2;

    //Payment Types
    public final static int CONTEST_PAYMENT = 1;
    //public final static int      CONTRACT_PAYMENT               = 2;
    //public final static int      PROBLEM_PAYMENT                = 3;
    //public final static int      CODER_REFERRAL_PAYMENT         = 4;
    public final static int CHARITY_PAYMENT = 5;

    /**
     * please remove language and editor constants, as then can now be found in
     * common.DBMS
     */
    // Language
    public final static int DEFAULT_LANG = 0;
    public final static int JAVA = 1;
    public final static int CPP = 3;
    public final static int CSHARP = 4;
    public final static int VB = 5;
    public final static int PYTHON = 6;
    /**
     * the R language id.
     */
    public final static int R = 7;

    public final static String[] LANG_NAMES =
    {"", "Java", "", "C++", "C#", "Visual Basic","Python"};

    // Editor
    public final static int STD_EDITOR = 0;
    //public final static int VI = 1;

    // Ratings
    public final static int DIVISION_SPLIT = 1200;

    public static final String SUN_PRACTICE_DUMMY_FIRST_NAME = "SUN_PRACTICE_DUMMY_FIRST_NAME_3634863463449638";

    // POPS - 9/12/2002 - note: must match SunAssigned in CommonData
    public static final String COMPANY_SUN = "Sun";

    ////////////////////////////////////////////////////////////////////////////////
    // This method takes an object and formats it as a nice looking String.
    ////////////////////////////////////////////////////////////////////////////////
    public static String makePretty(Object result) {
        if (result == null) {
            return "<null>";
        }

        if (result.getClass().isArray()) {
            StringBuffer buf = new StringBuffer(250);
            Class cType = result.getClass().getComponentType();

            buf.append("{");
            if (Array.getLength(result) > 0 ) {
                try {
                    if (int.class.equals(cType)) {
                        buf.append(((int[]) result)[0]);
                        for (int i = 1; ; i++) {
                            buf.append(", " + ((int[]) result)[i]);
                        }
                    } else if (double.class.equals(cType)) {
                        buf.append(((double[]) result)[0]);
                        for (int i = 1; ; i++) {
                            buf.append(", " + ((double[]) result)[i]);
                        }
                    } else if (float.class.equals(cType)) {
                        buf.append(((float[]) result)[0]);
                        for (int i = 1; ; i++) {
                            buf.append(", " + ((float[]) result)[i]);
                        }
                    } else if (boolean.class.equals(cType)) {
                        buf.append(((boolean[]) result)[0]);
                        for (int i = 1; ; i++) {
                            buf.append(", " + ((boolean[]) result)[i]);
                        }
                    } else if (long.class.equals(cType)) {
                        buf.append(((long[]) result)[0]);
                        for (int i = 1; ; i++) {
                            buf.append(", " + ((long[]) result)[i]);
                        }
                    } else if (char.class.equals(cType)) {
                        buf.append(((char[]) result)[0]);
                        for (int i = 1; ; i++) {
                            buf.append(", " + ((char[]) result)[i]);
                        }
                    } else if (byte.class.equals(cType)) {
                        buf.append(((byte[]) result)[0]);
                        for (int i = 1; ; i++) {
                            buf.append(", " + ((byte[]) result)[i]);
                        }
                    } else if (short.class.equals(cType)) {
                        buf.append(((short[]) result)[0]);
                        for (int i = 1; ; i++) {
                            buf.append(", " + ((short[]) result)[i]);
                        }
                    } else {
                        int aLength = Array.getLength(result);
                        for (int i = 0; i < aLength; i++) {
                            buf.append(makePretty(Array.get(result, i)));
                            if (i != (aLength - 1)) {
                                buf.append(", ");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            buf.append("}");
            return buf.toString();
        } else if (result instanceof Collection) {
            StringBuffer buf = new StringBuffer(250);
            Collection tmp = (Collection) result;
            buf.append("[");
            for (Iterator it = tmp.iterator(); it.hasNext(); ) {
                buf.append(makePretty(it.next()));
                if (it.hasNext()) {
                    buf.append(", ");
                }
            }
            buf.append("]");
            return buf.toString();
        } else if (result instanceof java.lang.String) {
            return "\"" + result + "\"";
        }
        return result.toString();
    }


    /**
     * This method creates a String representing a path name to a user's compiled
     * class files.
     *
     *@param coder_id    - int - unique id identifying a specific user.
     *@param contest_id  - int - unique id identifying a specific contest.
     *@param round_id    - int - unique id identifying a specific round.
     *@param problem_id  - int - unique id identifying a specific problem.
     *@return            String - The full path name to the user's compiled class
     *      files.
     *@author            ademich
     */
    /*
////////////////////////////////////////////////////////////////////////////////
public static String getClassesPath(int coder_id, int contest_id, int round_id, int problem_id) {
////////////////////////////////////////////////////////////////////////////////
StringBuffer classesPathBuf = new StringBuffer();
classesPathBuf.append("u").append(coder_id).
append("/c").append(contest_id).
append("/r").append(round_id).
append("/p").append(problem_id);

return classesPathBuf.toString();
}
    */

    // Practice group IDs
    public static final int PRACTICE_GROUP_TOURNAMENTS_ID = 1;
    public static final int PRACTICE_GROUP_SRMS_ID = 2;
    public static final int PRACTICE_GROUP_TCHS_ID = 3;
    public static final int PRACTICE_GROUP_MARATHONS_ID = 4;

    public static final int APPLET_SSL_PORT_OFFSET = 10;
}
