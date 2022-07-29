package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.naming.*;
import javax.ejb.*;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;

public class RoundSystemTests {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("This program is used to print System Test Information for a round.");
            System.out.println("Usage: java com.topcoder.utilities.RoundSystemTests <round_id> ");
            return;
        }

        int round_id = Integer.parseInt(args[0]);

        ArrayList sysTestInfo;

        try {

            sysTestInfo = getRoundSysTestInfo(round_id);
            printRoundSysTestInfo(sysTestInfo);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * This method is used to display round system test characteristics in a
     * meaningful manner.
     *
     * @param sysTestInfo - ArrayList of HashMaps
     * @author ademich
     */
    public static void printRoundSysTestInfo(ArrayList sysTestInfo) throws Exception {

        HashMap sysTestInfoHash;
        StringBuffer output = new StringBuffer(400);
        String succeeded = "";
        String handle = "";
        String new_handle = "";
        String class_name = "";
        String new_class_name = "";

        for (int i = 0; i < sysTestInfo.size(); i++) {
            sysTestInfoHash = (HashMap) sysTestInfo.get(i);

            new_handle = (String) sysTestInfoHash.get("handle");
            new_class_name = (String) sysTestInfoHash.get("class_name");


            if (((Integer) sysTestInfoHash.get("succeeded")).intValue() == 1) {
                succeeded = "Successful";
            } else {
                succeeded = "Unsuccessful";
            }

            if (!handle.equals(new_handle)) {
                output.append("\n***********************************\n").
                        append("Handle: " + new_handle);
                handle = new_handle;
                class_name = "";
            }
            if (!class_name.equals(new_class_name)) {
                output.append("\n\tClass Name: " + new_class_name);
                class_name = new_class_name;
            }
            output.append("\n\t\tStatus: " + succeeded);

            output.append("\n\t\tArguments: " + makePretty(sysTestInfoHash.get("args")));
            output.append("\n\t\tExpected Result: " + makePretty(sysTestInfoHash.get("received")));
            output.append("\n\t\tReceived Result: " + makePretty(sysTestInfoHash.get("expected_result")) + "\n");

        }


        System.out.println(output.toString());
    }


    /*****************************************************************************************
     * Retrieves round system test characteristics from the
     * SYSTEM_TEST_RESULT, USER, PROBLEM and SYSTEM_TEST_CASE tables for a round
     *
     * @param - round_id - int that uniquely identifies a Round
     * @exception RemoteException
     * @return ArrayList of HashMaps
     *****************************************************************************************
     **/
    public static ArrayList getRoundSysTestInfo(int round_id) throws Exception {
        System.out.println("In getRoundSysTestInfo");

        ArrayList sysTestInfo = new ArrayList(100);

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        HashMap sysTestInfoHash;
        Object blobObject = new Object();


        StringBuffer sysTestInfotxt = new StringBuffer();
        sysTestInfotxt.append(" SELECT u.handle, p.class_name, str.received, str.succeeded, ").
                append("        stc.expected_result, stc.args, str.test_case_id ").
                append(" FROM system_test_result str, user u, problem p, system_test_case stc ").
                append(" WHERE  str.round_id = ? AND ").
                append("        str.coder_id = u.user_id AND ").
                append("        str.problem_id = p.problem_id AND ").
                append("        str.test_case_id = stc.test_case_id ").
                append(" ORDER BY u.handle, p.class_name, str.succeeded ");


        try {
            conn = DBMS.getDirectConnection();

            ps = conn.prepareStatement(sysTestInfotxt.toString());
            ps.setInt(1, round_id);

            rs = ps.executeQuery();

            while (rs.next()) {
                sysTestInfoHash = new HashMap();
                sysTestInfoHash.put("handle", rs.getString(1));
                sysTestInfoHash.put("class_name", rs.getString(2));
                sysTestInfoHash.put("succeeded", new Integer(rs.getInt(4)));

                try {
                    blobObject = DBMS.getBlobObject(rs, 3);
                } catch (Exception tce) {
                    tce.printStackTrace();
                }

                if (blobObject != null) {
                    sysTestInfoHash.put("received", blobObject);
                } else {
                    System.out.println("***NULL received result*** " + rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getInt(7));
                }

                try {
                    blobObject = DBMS.getBlobObject(rs, 5);
                } catch (Exception tce) {
                    tce.printStackTrace();
                }

                if (blobObject != null) {
                    sysTestInfoHash.put("expected_result", blobObject);
                } else {
                    System.out.println("***NULL expected_result*** " + rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getInt(7));
                }

                try {
                    blobObject = DBMS.getBlobObject(rs, 6);
                } catch (Exception tce) {
                    tce.printStackTrace();
                }

                if (blobObject != null) {
                    sysTestInfoHash.put("args", blobObject);
                } else {
                    System.out.println("***NULL args*** " + rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getInt(7));
                }

                sysTestInfo.add(sysTestInfoHash);
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

        return sysTestInfo;

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
