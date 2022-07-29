package com.topcoder.server.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * <p>
 * Modifications for AdminTool 2.0 are :
 * </p>
 * <p>
 * New public constant representing newly defined &quot;room assignment&quot; round
 * segment is added.
 * </p>
 * @author TCDEVELOPER
 */
public final class ServerContestConstants {

    public static final int ORACLE_COMPANY_ID = 26;

    // Room Types
    public static final int ADMIN_ROOM_TYPE_ID = 1;
    public static final int CONTEST_ROOM_TYPE_ID = 2;
    public static final int MODERATED_CHAT_ROOM_TYPE_ID = 4;

    // admin group id
    //public static final int ADMIN_GROUP_ID = 13;
    // round_segment ids
    public static final int REGISTRATION_SEGMENT_ID = 1;
    public static final int CODING_SEGMENT_ID = 2;
    public static final int INTERMISSION_SEGMENT_ID = 3;
    public static final int CHALLENGE_SEGMENT_ID = 4;
    public static final int SYSTEM_TEST_SEGMENT_ID = 5;
    /*added by SYHAAS 2002-05-18*/
    public static final int MODERATED_CHAT_SEGMENT_ID = 6;
    /**
     * An int constant representing newly defined "room assignment" round
     * segment.
     * 
     * @since Admin Tool 2.0
     */
    public static final int ROOM_ASSIGNMENT_SEGMENT_ID = 7;

    // admin group id
    public static final int CODER_GROUP_ID = 10;
    public static final int ADMIN_LEVEL_ONE_GROUP_ID = 13;
    public static final int ADMIN_LEVEL_TWO_GROUP_ID = 14;

    public static final String GROUP_ADMIN = "group_Admin";
    public static final String GROUP_WRITER_TESTER = "group_Tester/Writer";
    public static final String GROUP_STUDENT = "group_student";
    public static final String GROUP_COMPETITION_USER = "group_Competition User";
    public static final String GROUP_HS_COMPETITION_USER = "group_HS Competition User";

    public static final int SINGLE_PROBLEM = 1;
    public static final int TEAM_PROBLEM = 2;
    public static final int LONG_PROBLEM = 3;
    
    public static final String HASH_SECRET = "746c80dbdc541fe829898aa01d9e30118bab5d6b9fe94fd052a40069385f5628";

    //public static final int MAIN_COMPONENT = 1;
    private ServerContestConstants() {
    }


    ///////////////////////////////////////////////////////////////////////////////////
    public static final java.sql.Timestamp getCurrentTimestamp(java.sql.Connection conn) throws Exception {
        ///////////////////////////////////////////////////////////////////////////////////
        Timestamp result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            ps = conn.prepareStatement("SELECT CURRENT FROM dual");

            rs = ps.executeQuery();
            if (rs.next()) result = rs.getTimestamp(1);
        } catch (Exception e) {
            throw new Exception("common.Common:getCurrentDate: Error " + e);
        } finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
            } catch (Exception c) {
            }
        }
        return result;
    }

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

    public static boolean isLobby(int id) {
        return (id == 0 || (id > 9 && id < 20));
    }

    /*
    public static String getAgreeMsg()
    {
    String retVal = "";
    try {
    File inFile = new File(ApplicationServer.IAGREE);
    int len = (int) inFile.length();
    FileReader in = new FileReader(inFile);
    char [] cbuf = new char [len];

    in.read(cbuf, 0, len);
    retVal = new String(cbuf);
    } catch (Exception e) { e.printStackTrace(); }

    return retVal;
    }
    */

}
