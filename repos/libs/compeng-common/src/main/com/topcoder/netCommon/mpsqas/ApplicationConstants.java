package com.topcoder.netCommon.mpsqas;

import java.util.*;

/**
 *  ApplicationConstants is a list of constants used by
 *  the application and applet servers, Applet does not
 *  have access to these constants.
 */
public class ApplicationConstants {

    /** Dev  Addresses*/
/*
  public static  String COMPILER_IP="172.16.1.152";
  public static  String TESTER_IP="172.16.1.152";
  public static  int GET_COMPILE_PORT=5032;
  public static  int PUT_COMPILE_PORT=5033;
  public static  int GET_TEST_PORT=5034;
  public static  int PUT_TEST_PORT=5036;
  public static  int INTERNAL_COMMUNICATION_PORT=5137;
  public static  String APPLET_SERVER_IP="172.16.1.153";
  public static  String BASE_CODE_PATH="/home/weblog5/classes";
*/

    /* QA Glastonbury */
/*
  public static  String COMPILER_IP="172.16.212.52";;
  public static  String TESTER_IP="172.16.212.52";
  public static  int GET_COMPILE_PORT=5032;
  public static  int PUT_COMPILE_PORT=5033;
  public static  int GET_TEST_PORT=5034;
  public static  int PUT_TEST_PORT=5035;
  public static  int INTERNAL_COMMUNICATION_PORT=5036;
  public static  String APPLET_SERVER_IP="172.16.210.55";
*/

    /* Prod */
    public static String COMPILER_IP = "172.16.20.40";
    public static String TESTER_IP = "172.16.20.40";
    public static int GET_COMPILE_PORT = 5032;
    public static int PUT_COMPILE_PORT = 5033;
    public static int GET_TEST_PORT = 5034;
    public static int PUT_TEST_PORT = 5035;
    public static int INTERNAL_COMMUNICATION_PORT = 5036;
    public static String APPLET_SERVER_IP = "172.16.20.30";
    public static String BASE_CODE_PATH = "/export/home/mpsqas/app";

    /**internal message types*/
    public static final int CORRESPONDENCE_BROADCAST_IN = 0,
    ROUND_SCHEDULE_BROADCAST_IN = 1,
    PENDING_PROPOSAL_BROADCAST_IN = 2,
    PENDING_SUBMISSION_BROADCAST_IN = 3,
    PROBLEM_MODIFIED_BROADCAST_IN = 4,
    PENDING_APPLICATION_BROADCAST_IN = 5;

    /**groups*/
    public static int ADMIN_GROUP = 13;
    public static int PROBLEM_WRITER_GROUP = 30;
    public static int PROBLEM_TESTER_GROUP = 31;

    /**how long to wait for tests  / compiles / other messages*/
    public static long TIME_OUT_MILLIS = 10000;

    /**minumum number of test cases allowed in a user's submission*/
    public static int MIN_TEST_CASES = 5;

    /**contest segments*/
    public static int CODING_SEGMENT = 1;
    public static int CHALLENGE_SEGMENT = 2;

    /**how long after the start of a contest until it is really over */
    public static long DISPLAY_OLD_CONTEST = 86400000l; //24 hours

    /**problem user types*/
    public static final int PROBLEM_WRITER = 5;
    public static final int PROBLEM_TESTER = 6;
    public static final int PROBLEM_ADMIN = 100;

    /**correspondence from email address*/
    public static String FROM_EMAIL_ADDRESS = "contest@topcoder.com";

    /**application status*/
    public static int APPLICATION_PENDING = 1;
    public static int APPLICATION_REJECTED = 2;
    public static int APPLICATION_ACCEPTED = 3;

    /**example constnats*/
    public static int EXAMPLE = 1;
    public static int NOT_EXAMPLE = 0;

    /**timing for background processor*/
    public static long BACKGROUND_CHECK_FREQUENCY = 10000;
    public static long CHAT_LOG_FREQUENCY = 20000;
    public static long UPCOMING_CONTESTS_CHECK_FREQUENCY = 20000;
    public static long PING_FREQUENCY = 200000;

    /**the smallest round id for a "real" contest (mpsqas ignores contests with
     lower ids)*/
    public static int REAL_CONTEST_ID_LOWER_BOUND = 2000;

    /**payment*/
    public static int[][] WRITING_PAYMENT = {{-1,-1,-1,-1},
                                             {-1, 150, 175, 200}, //div 1
                                             {-1, 100, 125, 150}}; //div 2
    //for now this is 225 regardless, it's per set
    public static int[][] TESTING_PAYMENT = {{-1,-1,-1,-1},
                                             {-1,225, 225, 225}, //div 1
                                             {-1,225, 225, 225}};    //div 2
    
    /*public static int[][] TESTING_PAYMENT = {{-1,-1,-1,-1},
                                             {-1,50, 50, 50}, //div 1
                                             {-1,25, 25, 25}};    //div 2*/

    /**application strings*/
    public static String SERVER_ERROR = "Server Error.  Please contact us.";
    public static String HORIZONTAL_RULE =
            "----------------------------------------------------\n";

    /**User group describers for getting users*/
    public static int TESTERS_FOR_PROBLEM = 1;
    public static int ALL_TESTERS = 2;
    public static int ALL_USERS = 3;
    public static int TESTERS_FOR_COMPONENT = 4;
    public static int ALL_ADMINS = 5;
    public static int WRITERS_FOR_COMPONENT = 6;

    public static final int PRIMARY_SOLUTION = 1;
    public static final int SECONDARY_SOLUTION = 0;

    public static final int SINGLE_PROBLEM = 1;
    public static final int TEAM_PROBLEM = 2;
    public static final int LONG_PROBLEM = 3;

    public static final int MAIN_COMPONENT = 1;
    public static final int SECONDARY_COMPONENT = 2;

    public static final String AUTO_GENERATED_END_COMMENT_FLAG = "/*end autogenerated code*/";

    /**
     * Returns a good looking string representation of an object.
     *
     * @param result The object to make pretty.
     */
    public static String makePretty(Object result) {
        if (result == null) return "null";

        if (result.getClass().isArray()) {
            StringBuffer buf = new StringBuffer(250);
            String type = result.getClass().getComponentType().toString();
            buf.append("{");
            try {
                if (type.equals("int")) {
                    buf.append(((int[]) result)[0]);
                    for (int i = 1; ; i++)
                        buf.append(", " + ((int[]) result)[i]);
                } else if (type.equals("double")) {
                    buf.append(((double[]) result)[0]);
                    for (int i = 1; ; i++)
                        buf.append(", " + ((double[]) result)[i]);
                } else if (type.equals("class java.lang.String")) {
                    buf.append("\"" + ((String[]) result)[0] + "\"");
                    for (int i = 1; ; i++)
                        buf.append(", \"" + ((String[]) result)[i] + "\"");
                } else if (type.equals("float")) {
                    buf.append(((float[]) result)[0]);
                    for (int i = 1; ; i++)
                        buf.append(", " + ((float[]) result)[i]);
                } else if (type.equals("boolean")) {
                    buf.append(((boolean[]) result)[0]);
                    for (int i = 1; ; i++)
                        buf.append(", " + ((boolean[]) result)[i]);
                } else if (type.equals("long")) {
                    buf.append(((long[]) result)[0]);
                    for (int i = 1; ; i++)
                        buf.append(", " + ((long[]) result)[i]);
                } else if (type.equals("char")) {
                    buf.append(((char[]) result)[0]);
                    for (int i = 1; ; i++)
                        buf.append(", " + ((char[]) result)[i]);
                } else if (type.equals("byte")) {
                    buf.append(((byte[]) result)[0]);
                    for (int i = 1; ; i++)
                        buf.append(", " + ((byte[]) result)[i]);
                } else if (type.equals("short")) {
                    buf.append(((short[]) result)[0]);
                    for (int i = 1; ; i++)
                        buf.append(", " + ((short[]) result)[i]);
                } else if (type.equals("class java.lang.Object")) {
                    buf.append(makePretty(((Object[]) result)[0]));
                    for (int i = 1; ; i++)
                        buf.append(", " + makePretty(((Object[]) result)[i]));
                } else if (type.equals("class [I")) {  // multi-dimensional int array
                    int[][] iArray = (int[][]) result;
                    for (int i = 0; i < iArray.length; i++) {
                        buf.append(makePretty(iArray[i]));
                        if (i != (iArray.length - 1))
                            buf.append(", ");
                    }
                } else if (type.equals("class [Ljava.lang.String;")) { // multi-dimensional string array
                    String[][] sArray = (String[][]) result;
                    for (int i = 0; i < sArray.length; i++) {
                        buf.append(makePretty(sArray[i]));
                        if (i != (sArray.length - 1))
                            buf.append(", ");
                    }

                }
            } catch (Exception e) {
            }
            buf.append("}");

            return buf.toString();
        } else if (result instanceof java.util.ArrayList) {
            StringBuffer buf = new StringBuffer(250);
            ArrayList tmp = (ArrayList) result;
            buf.append("[");
            for (int i = 0; i < tmp.size(); i++) {
                buf.append(makePretty(tmp.get(i)));
                if (i != tmp.size() - 1)
                    buf.append(", ");
            }
            buf.append("]");
            return buf.toString();
        } else if (result instanceof java.lang.String) {
            return "\"" + result + "\"";
        }


        return result.toString();
    }
}
