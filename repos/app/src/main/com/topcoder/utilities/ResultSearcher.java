package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.naming.*;
import javax.ejb.*;

import com.topcoder.server.common.*;
import com.topcoder.netCommon.contest.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;


public class ResultSearcher {

    public static void main(String[] args) {
        if ((args.length != 2)) {
            System.out.println("This program is used to search system test result values.");
            System.out.println("Usage: java com.topcoder.utilities.ResultSearcher <phrase> <round_id where clause>");
            System.out.println("Ex. java com.topcoder.utilities.ResultSearcher \"Segment Fault\" \"between 4000 and 4010\"");
            return;
        }

        String phrase = "";
        String where = "";

        ArrayList sysTestArrList;

        try {

            phrase = args[0];
            where = args[1];
            sysTestArrList = getSysTestResults(phrase, where);
            printSysTestInfo(sysTestArrList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return phrase - String containing the phrase to search for
     * @return where - String containing the where clause to constrain on the round
     * @author ademich
     */
    public static ArrayList getSysTestResults(String phrase, String where) throws Exception {
        ArrayList sysTestArrList = new ArrayList();
        HashMap sysTestInfo = null;


        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String received = null;

        StringBuffer txtGetRoundProblems = new StringBuffer();
        txtGetRoundProblems.append(" SELECT str.received, str.problem_id, p.class_name, str.coder_id, u.handle, ").
                append(" str.round_id, c.name ").
                append(" FROM system_test_result str, problem p, user u, round r, contest c ").
                append(" WHERE str.succeeded = 0 ").
                append("   AND str.round_id ").
                append(where).
                append("   AND str.problem_id = p.problem_id ").
                append("   AND str.coder_id = u.user_id ").
                append("   AND str.round_id = r.round_id ").
                append("   AND r.contest_id = c.contest_id ");

        try {
            conn = DBMS.getDirectConnection();

            ps = conn.prepareStatement(txtGetRoundProblems.toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                received = ContestConstants.makePretty(DBMS.getBlobObject(rs, 1));
                if (received.indexOf(phrase) != -1) {
                    sysTestInfo = new HashMap();
                    sysTestInfo.put("problem_id", new Integer(rs.getInt(2)));
                    sysTestInfo.put("class_name", rs.getString(3));
                    sysTestInfo.put("coder_id", new Integer(rs.getInt(4)));
                    sysTestInfo.put("handle", rs.getString(5));
                    sysTestInfo.put("round_id", new Integer(rs.getInt(6)));
                    sysTestInfo.put("contest_name", rs.getString(7));
                    sysTestInfo.put("received", received);
                    sysTestArrList.add(sysTestInfo);
                }
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


        return sysTestArrList;

    }

    /**
     * This method is used to display system test result information in a
     * meaningful manner.
     *
     * @param sysTestArrList - ArrayList of HashMaps
     * @author ademich
     */
    public static void printSysTestInfo(ArrayList sysTestArrList) throws Exception {

        StringBuffer output = new StringBuffer(400);
        HashMap sysTestInfo = null;

        for (int i = 0; i < sysTestArrList.size(); i++) {
            sysTestInfo = (HashMap) sysTestArrList.get(i);

            output.append("\n***********************************\n").
                    append("Problem Id: " + sysTestInfo.get("problem_id")).
                    append("\nClass Name: " + sysTestInfo.get("class_name")).
                    append("\nCoder Id: " + sysTestInfo.get("coder_id")).
                    append("\nHandle : " + sysTestInfo.get("handle")).
                    append("\nRound Id : " + sysTestInfo.get("round_id")).
                    append("\nContest Name: " + sysTestInfo.get("contest_name")).
                    append("\nReceived : " + sysTestInfo.get("received"));
        }
        output.append("\n***********************************\n");

        System.out.println(output.toString());

    }

}

