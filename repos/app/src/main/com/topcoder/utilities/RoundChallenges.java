package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.naming.*;
import javax.ejb.*;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;

public class RoundChallenges {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("This program is used to print Challenge for a round.");
            System.out.println("Usage: java com.topcoder.utilities.RoundChallenges <round_id> ");
            return;
        }

        int round_id = Integer.parseInt(args[0]);

        ArrayList challengeInfo;

        try {

            challengeInfo = getRoundChallengeInfo(round_id);
            printRoundChallengeInfo(challengeInfo);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * This method is used to display round challenge characteristics in a
     * meaningful manner.
     *
     * @param challengeInfo - ArrayList of HashMaps
     * @author ademich
     */
    public static void printRoundChallengeInfo(ArrayList challengeInfo) throws Exception {

        HashMap challengeInfoHash;
        StringBuffer output = new StringBuffer(400);
        String succeeded = "";

        for (int i = 0; i < challengeInfo.size(); i++) {
            challengeInfoHash = (HashMap) challengeInfo.get(i);

            if (((Integer) challengeInfoHash.get("succeeded")).intValue() == 1) {
                succeeded = "Successful";
            } else {
                succeeded = "Unsuccessful";
            }

            output.append("\n***********************************\n").
                    append("Defendant: " + challengeInfoHash.get("handle")).
                    append("\nClass Name: " + challengeInfoHash.get("class_name")).
                    append("\nStatus: " + succeeded).
                    append("\nMessage: " + challengeInfoHash.get("message"));

            output.append("\nArguments: " + makePretty(challengeInfoHash.get("args")));
            output.append("\nExpected Result: " + makePretty(challengeInfoHash.get("received")));
            output.append("\nReceived Result: " + makePretty(challengeInfoHash.get("expected_result")));
            output.append("\n***********************************\n");
        }


        System.out.println(output.toString());
    }


    /*****************************************************************************************/
    public static Object getBlobObject(ResultSet rs, int column)
            throws Exception {
        Object retVal = null;
        ObjectInputStream ois = null;
        Blob bl = null;
        ByteArrayInputStream bs = null;

        try {

            InputStream is = rs.getBinaryStream(column);
            if (is != null)
                ois = new ObjectInputStream(is);

            if (ois != null) {
                retVal = ois.readObject();
            }

            if (ois != null) {
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DBMS: getBlobObject - error: " + e);
        }

        return retVal;

    }


    /*****************************************************************************************
     * Retrieves round challenge characteristics from the
     * CHALLENGE, USER, and PROBLEM tables for a round
     *
     * @param - round_id - int that uniquely identifies a Round
     * @exception RemoteException
     * @return ArrayList of HashMaps
     *****************************************************************************************
     **/
    public static ArrayList getRoundChallengeInfo(int round_id) throws Exception {
        System.out.println("In getRoundChallengeInfo");

        ArrayList challengeInfo = new ArrayList(100);

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        HashMap challengeInfoHash;
        Object blobObject = new Object();


        StringBuffer chalInfotxt = new StringBuffer();
        chalInfotxt.append(" SELECT u.handle, p.class_name, c.received, c.succeeded, ").
                append("        c.expected, c.args, c.message, c.challenge_id ").
                append(" FROM challenge c, user u, problem p ").
                append(" WHERE  c.round_id = ? AND ").
                append("        c.defendant_id = u.user_id AND ").
                append("        c.problem_id = p.problem_id ").
                append(" ORDER BY u.handle, p.class_name, c.succeeded ");


        try {
            conn = DBMS.getDirectConnection();

            ps = conn.prepareStatement(chalInfotxt.toString());
            ps.setInt(1, round_id);

            rs = ps.executeQuery();

            while (rs.next()) {
                challengeInfoHash = new HashMap();
                challengeInfoHash.put("handle", rs.getString(1));
                challengeInfoHash.put("class_name", rs.getString(2));
                challengeInfoHash.put("succeeded", new Integer(rs.getInt(4)));
                challengeInfoHash.put("message", rs.getString(7));

                try {
                    blobObject = DBMS.getBlobObject(rs, 3);
                } catch (Exception tce) {
                    tce.printStackTrace();
                }

                if (blobObject != null) {
                    challengeInfoHash.put("received", blobObject);
                } else {
                    System.out.println("***NULL received result*** " + rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getInt(8));
                }

                try {
                    blobObject = DBMS.getBlobObject(rs, 5);
                } catch (Exception tce) {
                    tce.printStackTrace();
                }

                if (blobObject != null) {
                    challengeInfoHash.put("expected_result", blobObject);
                } else {
                    System.out.println("***NULL expected_result*** " + rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getInt(8));
                }

                try {
                    blobObject = DBMS.getBlobObject(rs, 6);
                } catch (Exception tce) {
                    tce.printStackTrace();
                }

                if (blobObject != null) {
                    challengeInfoHash.put("args", blobObject);
                } else {
                    System.out.println("***NULL args*** " + rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getInt(8));
                }

                challengeInfo.add(challengeInfoHash);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ignore) {
            }
        }

        return challengeInfo;

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
