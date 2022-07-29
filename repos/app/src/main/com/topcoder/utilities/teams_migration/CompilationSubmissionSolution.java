/*
 * Author: Michael Cervantes (emcee)
 * Date: Jul 1, 2002
 * Time: 12:42:54 AM
 */
package com.topcoder.utilities.teams_migration;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

public class CompilationSubmissionSolution {

    //  WARNING - only run this on a pre-Teams database!
    // it assumes only traditional single-component problems exist in the DB
    public static void createFiles(Connection conn) throws Exception {
        PreparedStatement ps = null,ps2 = null,ps3 = null,ps4 = null, ps5 = null, ps6 = null;
        ResultSet rs = null;
        ResultSet rs2 = null,rs3 = null,rs4 = null;

        try {
            ps3 = conn.prepareStatement("DELETE FROM compilation_class_file WHERE component_state_id = ?");

            ps = conn.prepareStatement("SELECT component_state_id,compilation_class_file FROM compilation WHERE compilation_class_file IS NOT NULL ORDER BY component_state_id");
            rs = ps.executeQuery();
            ps2 = conn.prepareStatement("INSERT INTO compilation_class_file (component_state_id,sort_order,path,class_file) VALUES (?,?,?,?)");
            while (rs.next()) {
                int idx = 1;
                int componentStateID = rs.getInt(idx++);
                System.out.println("Processing component state #" + componentStateID);
//                try {
                Object problemFiles = DBMS.getBlobObject(rs, idx);
                Field classListField = problemFiles.getClass().getDeclaredField("classList");
                classListField.setAccessible(true);
                HashMap classList = (HashMap) classListField.get(problemFiles);
                int k = 1;
                for (Iterator it = classList.keySet().iterator(); it.hasNext();) {
                    String path = (String) it.next();
                    System.out.println("Processing file " + path);
                    byte[] b = (byte[]) classList.get(path);
                    ps3.clearParameters();
                    ps3.setInt(1, componentStateID);
                    ps3.executeUpdate();
                    ps2.clearParameters();
                    ps2.setInt(1, componentStateID);
                    ps2.setInt(2, k++);
                    ps2.setString(3, path);
                    ps2.setBytes(4, b);
                    ps2.executeUpdate();
                }
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            ps.close();
            ps2.close();
            ps3.close();

            ps3 = conn.prepareStatement("DELETE FROM solution_class_file WHERE solution_id = ?");
            ps = conn.prepareStatement("SELECT solution_id,solution_class FROM solution WHERE solution_class IS NOT NULL ORDER BY solution_id");
            rs = ps.executeQuery();
            ps2 = conn.prepareStatement("INSERT INTO solution_class_file (solution_id,sort_order,path,class_file) VALUES (?,?,?,?)");
            while (rs.next()) {
                int idx = 1;
                int solutionID = rs.getInt(idx++);
                System.out.println("Processing solution #" + solutionID);
//                try {
                HashMap classList = (HashMap) DBMS.getBlobObject(rs, idx);
                int k = 1;
                for (Iterator it = classList.keySet().iterator(); it.hasNext();) {
                    String path = (String) it.next();
                    System.out.println("Processing file " + path);
                    byte[] b = (byte[]) classList.get(path);
                    ps3.clearParameters();
                    ps3.setInt(1, solutionID);
                    ps3.executeUpdate();
                    ps2.clearParameters();
                    ps2.setInt(1, solutionID);
                    ps2.setInt(2, k++);
                    ps2.setString(3, path);
                    ps2.setBytes(4, b);
                    ps2.executeUpdate();
                }
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            ps.close();
            ps2.close();
            ps3.close();

            ps3 = conn.prepareStatement("DELETE FROM submission_class_file WHERE component_state_id = ?");
            ps = conn.prepareStatement("SELECT component_state_id,submission_number,submission_class_file FROM submission WHERE submission_class_file IS NOT NULL ORDER BY component_state_id");
            rs = ps.executeQuery();
            ps2 = conn.prepareStatement("INSERT INTO submission_class_file (component_state_id,submission_number,sort_order,path,class_file) VALUES (?,?,?,?,?)");
            while (rs.next()) {
                int idx = 1;
                int componentStateID = rs.getInt(idx++);
                int submissionNumber = rs.getInt(idx++);
                System.out.println("Processing submission - component state #" + componentStateID + " , submission_number #" + submissionNumber);
//                try {
                Object problemFiles = DBMS.getBlobObject(rs, idx);
                Field classListField = problemFiles.getClass().getDeclaredField("classList");
                classListField.setAccessible(true);
                HashMap classList = (HashMap) classListField.get(problemFiles);
                int k = 1;
                for (Iterator it = classList.keySet().iterator(); it.hasNext();) {
                    String path = (String) it.next();
                    System.out.println("Processing file " + path);
                    byte[] b = (byte[]) classList.get(path);
                    ps3.clearParameters();
                    ps3.setInt(1, componentStateID);
                    ps3.executeUpdate();
                    ps2.clearParameters();
                    ps2.setInt(1, componentStateID);
                    ps2.setInt(2, submissionNumber);
                    ps2.setInt(3, k++);
                    ps2.setString(4, path);
                    ps2.setBytes(5, b);
                    ps2.executeUpdate();
                }
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
            System.out.println("Done processing");
            ps.close();
            ps2.close();
            ps3.close();

//            if (args.length > 0) {
//                if (args[0].equals("commit")) {
//                    conn.commit();
//                    System.out.println("Committed");
//                }
//                else {
//                    conn.rollback();
//                    System.out.println("Rollback");
//                }
//            }
        } finally {
            close(rs4);
            close(rs3);
            close(rs2);
            close(rs);
            close(ps);
            close(ps2);
            close(ps3);
            close(ps4);
            close(ps5);
            close(ps6);
//            close(conn);
        }

    }

    private static void close(Object o) {
        if (o == null) return;
        try {
            if (o instanceof ResultSet) {
                ((ResultSet) o).close();
            }
            if (o instanceof PreparedStatement) {
                ((PreparedStatement) o).close();
            }
            if (o instanceof Connection) {
                ((Connection) o).close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
