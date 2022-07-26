package com.topcoder.utilities;
import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessor;
import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessorFactory;
import com.topcoder.shared.util.DBMS;
import java.sql.*;

import org.apache.log4j.Logger;

public class GCJFix {
    
    public static final Logger log = Logger.getLogger(GCJFix.class);

    public static void main(String[] args) {
        GCJFix tmp = new GCJFix();
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
        
        String sqlStr = "select cs.component_state_id, cs.coder_id, cs.round_id, u.handle " +
                            "from component_state cs, submission s, user u " +
                            "where s.submission_number = cs.submission_number " +
                            "and s.component_state_id = cs.component_state_id " +
                            "and s.submission_number >= 2 " +
                            "and u.user_id = cs.coder_id and cs.status_id = 150 " +
                            "and cs.round_id >= 10000";
        
        try {
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                int coder_id = rs.getInt("coder_id");
                int round_id = rs.getInt("round_id");
                int csi = rs.getInt("component_state_id");
                String handle = rs.getString("handle");
                
                String sql2 = "select submission_number, submission_text, submission_points from submission s " +
                    "where component_state_id = ? " +
                    "order by submission_number desc";
                
                ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, csi);
                rs2 = ps2.executeQuery();
                
                rs2.next();
                int num = rs2.getInt("submission_number");
                String text = rs2.getString("submission_text");
                double points = 0;
                
                rs2.next();
                points = rs2.getDouble("submission_points");
                if(text.equals(rs2.getString("submission_text"))) {
                    while(rs2.next()) {
                        if(text.equals(rs2.getString("submission_text"))) {
                            points = rs2.getDouble("submission_points");
                        } else {
                            break;
                        }
                    }
                    
                    System.out.println("R:" + round_id + ":C:" + handle + "," + points);
                    System.out.println("update submission set points = " + points + " where component_state_id = " + csi + " and submission_number = " + num + ";");
                    System.out.println("update component_state set points = " + points + " where component_state_id = " + csi);
                    
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

