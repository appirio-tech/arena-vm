package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.naming.*;
import javax.ejb.*;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;
import com.topcoder.netCommon.contest.*;

import java.sql.*;


public class ArgSearcher {

    public static void main(String[] args) {
        if ((args.length < 1) || args.length > 2) {
            System.out.println("This program is used to search problem param_types.");
            System.out.println("Usage: java com.topcoder.utilities.ArgSearcher <type> <where clause/optional>");
            return;
        }

        String phrase = "";
        String where = "";

        ArrayList problemsArrList;

        try {

            phrase = args[0];
            if (args.length == 2) {
                where = args[1];
            }
            problemsArrList = getProblems(phrase, where);
            printProblemInfo(problemsArrList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return phrase - String containing the args to search for
     * @return where - String containing the contraint with which to search the param_types on
     * @author ademich
     */
    public static ArrayList getProblems(String param_type, String where) throws Exception {
        ArrayList problemsArrList = new ArrayList();
        HashMap problems = null;


        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String args = null;

        StringBuffer txtGetProblemParams = new StringBuffer();
        txtGetProblemParams.append(" SELECT p.problem_id, p.class_name, p.param_types ").
                append(" FROM problem p ");
        if (!where.equals("")) {
            txtGetProblemParams.append(" WHERE ").
                    append(where);
        }
        txtGetProblemParams.append(" ORDER BY problem_id ");

        try {
            conn = DBMS.getDirectConnection();

            ps = conn.prepareStatement(txtGetProblemParams.toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                args = ContestConstants.makePretty(DBMS.getBlobObject(rs, 3));
                if (args.indexOf(param_type) != -1) {
                    problems = new HashMap();
                    problems.put("problem_id", new Integer(rs.getInt(1)));
                    problems.put("class_name", rs.getString(2));
                    problems.put("args", args);
                    problemsArrList.add(problems);
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


        return problemsArrList;

    }

    /**
     * This method is used to display problem information in a
     * meaningful manner.
     *
     * @param problemsArrList - ArrayList of HashMaps
     * @author ademich
     */
    public static void printProblemInfo(ArrayList problemsArrList) throws Exception {

        StringBuffer output = new StringBuffer(400);
        HashMap problems = null;

        for (int i = 0; i < problemsArrList.size(); i++) {
            problems = (HashMap) problemsArrList.get(i);

            output.append("\n***********************************\n").
                    append("Problem Id: " + problems.get("problem_id")).
                    append("\nClass Name: " + problems.get("class_name")).
                    append("\nArgs: " + problems.get("args"));
        }
        output.append("\n***********************************\n");

        System.out.println(output.toString());

    }

}

