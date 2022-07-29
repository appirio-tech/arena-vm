/*
 * Author: Michael Cervantes (emcee)
 * Date: Jul 24, 2002
 * Time: 6:46:39 PM
 */
package com.topcoder.utilities.teams_migration;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.logging.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;

public class FixPathNames {

    private static final Logger s_trace = Logger.getLogger(FixPathNames.class);

    public static void fixPathNames(Connection conn) throws Exception {
//        try {
        updateCompilationTable(conn);
        updateSubmissionTable(conn);
        updateSolutionTable(conn);
//        }
//        finally {
//            if (conn != null) conn.close();
//        }
    }

    private static void updateCompilationTable(Connection conn) throws Exception {
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        try {
            selectStmt = conn.prepareStatement("SELECT component_state_id,sort_order,path FROM compilation_class_file");
            updateStmt = conn.prepareStatement("UPDATE compilation_class_file SET path = ? WHERE component_state_id = ? AND sort_order = ?");
            rs = selectStmt.executeQuery();
            while (rs.next()) {
                long componentStateID = rs.getLong(1);
                int sortOrder = rs.getInt(2);
                String path = rs.getString(3);
                String newPath = convertPath(path);
                if (!path.equals(newPath)) {
                    updateStmt.clearParameters();
                    updateStmt.setString(1, newPath);
                    updateStmt.setLong(2, componentStateID);
                    updateStmt.setInt(3, sortOrder);
                    updateStmt.executeUpdate();
                }
            }
        } finally {
            close(null, selectStmt, rs);
            close(null, updateStmt, null);
        }
    }


    private static void updateSubmissionTable(Connection conn) throws Exception {
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        try {
            selectStmt = conn.prepareStatement("SELECT component_state_id,sort_order,submission_number,path FROM submission_class_file");
            updateStmt = conn.prepareStatement("UPDATE submission_class_file SET path = ? WHERE component_state_id = ? AND sort_order = ? AND submission_number = ?");
            rs = selectStmt.executeQuery();
            while (rs.next()) {
                long componentStateID = rs.getLong(1);
                int sortOrder = rs.getInt(2);
                int submissionNumber = rs.getInt(3);
                String path = rs.getString(4);
                String newPath = convertPath(path);
                if (!path.equals(newPath)) {
                    updateStmt.clearParameters();
                    updateStmt.setString(1, newPath);
                    updateStmt.setLong(2, componentStateID);
                    updateStmt.setInt(3, sortOrder);
                    updateStmt.setInt(4, submissionNumber);
                    updateStmt.executeUpdate();
                }
            }
        } finally {
            close(null, selectStmt, rs);
            close(null, updateStmt, null);
        }
    }

    private static void updateSolutionTable(Connection conn) throws Exception {
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        try {
            selectStmt = conn.prepareStatement("SELECT solution_id,sort_order,path FROM solution_class_file");
            updateStmt = conn.prepareStatement("UPDATE solution_class_file SET path = ? WHERE solution_id = ? AND sort_order = ?");
            rs = selectStmt.executeQuery();
            while (rs.next()) {
                long solutionID = rs.getLong(1);
                int sortOrder = rs.getInt(2);
                String path = rs.getString(3);
                String newPath = convertPath(path);
                if (!path.equals(newPath)) {
                    updateStmt.clearParameters();
                    updateStmt.setString(1, newPath);
                    updateStmt.setLong(2, solutionID);
                    updateStmt.setInt(3, sortOrder);
                    updateStmt.executeUpdate();
                }
            }
        } finally {
            close(null, selectStmt, rs);
            close(null, updateStmt, null);
        }
    }


    private static String convertPath(String path) {
        String newPath = path;
        if (path.startsWith("/app/submissions/cpp/")) {
            newPath = path.substring("/app/submissions/cpp/".length());
        } else if (path.startsWith("/export/home/cpp/app/submissions/cpp/")) {
            newPath = path.substring("/export/home/cpp/app/submissions/cpp/".length());
        } else if (path.matches(".*\\..*\\..*\\..*")) {
            newPath = path.replace('.', '/') + ".class";
        }
        System.out.println("Converted " + path + " to " + newPath);
        return newPath;
    }

    private static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            printException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                printException(e);
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (Exception e) {
                    printException(e);
                }
            }
        }
    }

    // Query utilities to make life easier
    private static void printException(Exception e) {
        try {
            if (e instanceof SQLException) {
                String sqlErrorDetails = DBMS.getSqlExceptionString((SQLException) e);
                s_trace.error("Admin services EJB: SQLException caught\n" + sqlErrorDetails, e);
            } else {
                s_trace.error("Admin services EJB: Exception caught", e);
            }
        } catch (Exception ex) {
            s_trace.error("Admin services EJB: Error printing exception!");
        }
    }

}

