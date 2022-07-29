package com.topcoder.shared.netCommon.screening;

import java.util.ArrayList;

public final class ScreeningConstants {

    public static final int PROTOCOL_VERSION = 1;

    // PROBLEM types:  1=Single 2=Team 3=Example 4=Test Set B 5=Test Set A
    public static final int PROBLEM_SRM = 5;
    public static final int PROBLEM_EXAMPLE = 3;
    public static final int PROBLEM_COMPANY = 4;

    // Get ProblemLabels based on ProblemID or ComponentID
    public static final int BY_PROBLEMID = 0;
    public static final int BY_COMPONENTID = 1;

    public final static int GENERIC = 2;

    public final static int TEXT_AREA = 0;
    public final static int LABEL = 1;

    //GET_PROBLEM types
    public final static int EDIT_SOURCE_RW = 1001;
    public final static int EDIT_SOURCE_RO = 1002;

    //Request types
    public final static int COMPILE = 1;
    public final static int ERROR = 4;
    public final static int LOGIN = 9;
    public final static int LOGOUT = 10;
    public final static int GET_PROBLEM_SETS = 11;
    public final static int SUBMIT_PROBLEM = 14;
    public final static int TEST = 15;
    public final static int GET_PROBLEM = 30;
    public final static int SAVE = 34;
    public final static int SEARCH = 41;
    public final static int POP_UP_GENERIC_RQ = 71;
    public final static int CLOSE_PROBLEM = 72;
    public final static int KEEP_ALIVE_REQUEST = 77;
    public final static int TUNNEL_IP_REQUEST = 78;
    public final static int OPEN_PROBLEM_SET = 79;
    public final static int TERMS_REQUEST = 80;
    
    //history types
    public final static int HISTORY_LOGIN = 1;
    public final static int HISTORY_LOGOUT = 2;
    public final static int HISTORY_OPEN_PROBLEM = 3;
    public final static int HISTORY_COMPILE = 4;
    public final static int HISTORY_TEST = 5;
    public final static int HISTORY_SAVE = 6;
    public final static int HISTORY_SUBMIT = 7;

    // Regarding multiple submissions
    public static boolean ACCEPT_MULTIPLE_SUBMISSIONS = true;

    //Status for a Problem in ascending vs chronological order.
    public final static int NOT_OPENED = 110;  //Not opened
    public final static int LOOKED_AT = 120;  //Opened. Not yet compiled
    public final static int COMPILED_UNSUBMITTED = 121;  //Compiled, but not yet submitted
    public final static int SUBMITTED = 130;
    public final static int OUT_OF_TIME = 140;
    public final static int SYSTEM_TEST_SUCCEEDED = 150;  //Submitted
    public final static int SYSTEM_TEST_FAILED = 160;

    public static boolean isCompiled(int status) {
        return status >= COMPILED_UNSUBMITTED;
    }

    public final static int TIMEOUT_MILLIS = 20000;
    public final static long CODING_LENGTH = 4500000;

    //ProblemFile Types
    public final static int SUBMITTED_CLASS = 2;
    public final static int COMPILED_CLASS = 1;

    public final static int SOLVE = 2;

    //please remove language and editor constants, as then can now be found in
    //common.DBMS
    // Language
    //public final static int DEFAULT_LANG = 0;
    //public final static int JAVA = 1;
    //public final static int CPP = 3;
    //public final static int CSHARP = 4;

    // Editor
    public final static int STD_EDITOR = 0;
    public final static int VI = 1;

    // For identifying messages.
    public static final String SAVE_RESPONSE = "Save Results";
    public static final String COMPILE_RESPONSE = "Compile Results";
    public static final String TEST_RESPONSE = "Test Results";
    public static final String SUBMIT_RESPONSE = "Submission Results";

    public static final String OPEN = "Open";
    public static final String UN_OPENED = "Un-Opened";
    public static final String COMPLETE = "Complete";
    public static final String EXPIRED = "Out of Time";
    public static final String IN_PROGRESS = "In-Progress";

    // For closing pop-ups.
    public static final long ACTION_KILL_TIME = 30000;


    ////////////////////////////////////////////////////////////////////////////////
    // This method takes an object and formats it as a nice looking String.
    ////////////////////////////////////////////////////////////////////////////////
    public static String makePretty(Object result) {
        if (result == null)
            return "<null>";

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
                } else if (type.equals("class [I")) {	 // multi-dimensional int array
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
        } else if (result instanceof String) {
            return "\"" + result + "\"";
        }


        return result.toString();
    }

}
