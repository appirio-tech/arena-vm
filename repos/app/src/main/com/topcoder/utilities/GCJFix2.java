package com.topcoder.utilities;
import com.topcoder.shared.util.DBMS;
import java.sql.*;

import org.apache.log4j.Logger;

public class GCJFix2 {
    
    public static final Logger log = Logger.getLogger(GCJFix.class);

    public static void main(String[] args) {
        GCJFix2 tmp = new GCJFix2();
        Connection c = null;
        
        try {
            //c = DBMS.getConnection(DBMS.DW_DATASOURCE_NAME);
            Class.forName("com.informix.jdbc.IfxDriver");
            c = DBMS.getDirectConnection();
            //c.setAutoCommit(false);
            tmp.run(c);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
/*            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
            }*/
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
            }
        }
    }
    
    public void run(Connection conn) {
        //get every round, in order
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        
        String sqlStr = "select cs.component_id, cs.component_state_id, cs.coder_id, cs.round_id, u.handle " +
                            "from component_state cs, submission s, user u " +
                            "where s.submission_number = cs.submission_number " +
                            "and s.component_state_id = cs.component_state_id " +
                            "and u.user_id = cs.coder_id " +
                            "and cs.status_id = 160 " + 
                            "and s.language_id in (4,5) " +
                            "and cs.round_id in ( 10101,10102,10103,10104,10100)";
        
        try {
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                int comp_id = rs.getInt("component_id");
                int coder_id = rs.getInt("coder_id");
                int round_id = rs.getInt("round_id");
                int csi = rs.getInt("component_state_id");
                String handle = rs.getString("handle");
                
                String sql2 = "select received from system_test_result " +
                    "where component_id = ? and coder_id = ? and round_id = ? and succeeded = 0 ";
                
                //System.out.println(comp_id);
                //System.out.println(coder_id);
                //System.out.println(round_id);
                
                ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, comp_id);
                ps2.setInt(2, coder_id);
                ps2.setInt(3, round_id);
                rs2 = ps2.executeQuery();
                
                rs2.next();
                Object o = DBMS.getBlobObject(rs2, 1);
                if(o instanceof String) {
                    if(o.equals("Internal error. This is usually a fatal stack overflow error or OutOfMemoryException.")) {
                        System.out.println("CODER:" + coder_id + "ROUND:" + round_id + ":" + o);
                    }
                }
                
                
                rs2.close();
                ps2.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(ps);
            close(rs2);
            close(ps2);
        }
    }
    
    
    protected void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException sqle) {
            log.error("Error closing ResultSet.");
            sqle.printStackTrace();
        }
    }
    
    protected void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException sqle) {
            log.error("Error closing Statement.");
            sqle.printStackTrace();
        }
    }


}

