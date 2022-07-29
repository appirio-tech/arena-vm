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

public class ProbToComp {

    //  WARNING - only run this on a pre-Teams database!
    // it assumes only traditional single-component problems exist in the DB
    public static void migrateParameters(Connection conn) throws Exception {
        PreparedStatement ps = null,ps2 = null,ps3 = null,ps4 = null, ps5 = null, ps6 = null;
        ResultSet rs = null;
        ResultSet rs2 = null,rs3 = null,rs4 = null;

        try {
//            ps = conn.prepareStatement("DELETE FROM round_component");
//            ps.executeUpdate();
//            ps.close();
//            ps = conn.prepareStatement("DELETE FROM component_parameter_xref");
//            ps.executeUpdate();
//            ps.close();
//            ps = conn.prepareStatement("DELETE FROM parameter");
//            ps.executeUpdate();
//            ps.close();
//            ps = conn.prepareStatement("DELETE FROM component_solution_xref");
//            ps.executeUpdate();
//            ps.close();
//            ps = conn.prepareStatement("DELETE FROM component");
//            ps.executeUpdate();
//            ps.close();
//
//            ps = conn.prepareStatement("INSERT INTO component (component_id,problem_id,result_type_id,method_name,class_name,default_solution,component_type_id,status) SELECT p.problem_id,p.problem_id,p.result_type_id,p.method_name,p.class_name,p.default_solution,1,status FROM staging_problem p");
//            ps.executeUpdate();
//            ps.close();
//            ps = conn.prepareStatement("INSERT INTO round_component (component_id,round_id,submit_order,open_order,difficulty_id,division_id,points)  SELECT DISTINCT rp.problem_id,rp.round_id,rp.submit_order,rp.open_order,rp.difficulty_id,rp.division_id,rp.points FROM round_problem rp");
//            ps.executeUpdate();
//            ps.close();


            ps = conn.prepareStatement("SELECT problem_id,param_types FROM problem ORDER BY problem_id");
            rs = ps.executeQuery();
            int paramID = 1;
            Map dataTypes = getDataTypes(conn);
            ps2 = conn.prepareStatement("INSERT INTO parameter (parameter_id,component_id,data_type_id,name,sort_order) VALUES (?,?,?,?,?)");
            while (rs.next()) {
                int idx = 1;
                int problemID = rs.getInt(idx++);
                System.out.println("Processing problem #" + problemID);
                ArrayList paramTypes = (ArrayList) DBMS.getBlobObject(rs, idx++);

                for (int i = 0; i < paramTypes.size(); i++) {
                    String param = (String) paramTypes.get(i);
                    ps2.clearParameters();
                    idx = 1;
                    ps2.setInt(idx++, paramID);
                    ps2.setInt(idx++, problemID);
                    ps2.setObject(idx++, dataTypes.get(param));
                    ps2.setString(idx++, "");
                    ps2.setInt(idx++, i + 1);
                    ps2.executeUpdate();
                    paramID++;
                }
            }
            ps.close();
            ps2.close();


//            ps = conn.prepareStatement("INSERT INTO component_solution_xref (component_id,solution_id) SELECT problem_id,solution_id FROM problem_solution");
//            ps.executeUpdate();
//            ps.close();
//            ps = conn.prepareStatement("UPDATE component_solution_xref SET primary_solution=0 WHERE solution_id IN ( SELECT solution_id FROM problem_solution WHERE primary_solution = 'N' )");
//            ps.executeUpdate();
//            ps.close();
//            ps = conn.prepareStatement("UPDATE component_solution_xref SET primary_solution=1 WHERE solution_id IN ( SELECT solution_id FROM problem_solution WHERE primary_solution = 'Y' )");
//            ps.executeUpdate();
//            ps.close();

//            ps4 = conn.prepareStatement("SELECT * FROM component c JOIN parameter p ON c.component_id = p.component_id JOIN data_type dt ON p.data_type_id = dt.data_type_id ORDER BY p.component_id,p.sort_order");
//            rs2 = ps4.executeQuery();
//            System.out.println("ProblemComponent table:\n");
//            while (rs2.next()) {
//                int cols = ps4.getMetaData().getColumnCount();
//                for (int i = 1; i <= cols; i++) {
//                    System.out.print(ps4.getMetaData().getColumnName(i)+"="+rs2.getObject(i)+",");
//                }
//                System.out.println("");
//            }
//            System.out.println("");
//            System.out.println("------------------------------------");
//            System.out.println("");
//            System.out.println("Round-ProblemComponent table:\n");
//
//
//            ps5 = conn.prepareStatement("SELECT * FROM round_component ORDER BY component_id");
//            rs3 = ps5.executeQuery();
//            while (rs3.next()) {
//                int cols = ps5.getMetaData().getColumnCount();
//                for (int i = 1; i <= cols; i++) {
//                    System.out.print(ps5.getMetaData().getColumnName(i)+"="+rs3.getObject(i)+",");
//                }
//                System.out.println("");
//            }

//            if (args.length > 0) {
//                if (args[0].equals("commit")) {
//                    conn.commit();
//                }
//                else {
//                    conn.rollback();
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

    private static Map getDataTypes(Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Map r = new HashMap();
            ps = conn.prepareStatement("SELECT data_type_id, data_type_desc FROM data_type WHERE data_type_id < 1000");
            rs = ps.executeQuery();
            while (rs.next()) {
                r.put(rs.getObject(2), rs.getObject(1));
            }
            return r;
        } finally {
            close(rs);
            close(ps);
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
