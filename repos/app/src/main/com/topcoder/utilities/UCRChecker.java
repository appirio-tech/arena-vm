package com.topcoder.utilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessor;
import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessorFactory;
import com.topcoder.shared.util.DBMS;

public class UCRChecker {
    
    public static final Logger log = Logger.getLogger(UCRChecker.class);

    public static void main(String[] args) {
        UCRChecker tmp = new UCRChecker();
        Connection c = null;
        
        try {
            //c = DBMS.getConnection(DBMS.DW_DATASOURCE_NAME);
            Class.forName("com.informix.jdbc.IfxDriver");
            c = DBMS.getDirectConnection();
            //c.setAutoCommit(false);
            tmp.run(c, Integer.parseInt(args[0]));
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
    
    public void run(Connection conn, int roundId) {
        //get every round, in order
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sqlStr = "select cs.coder_id, cs.component_id, s.submission_text, s.language_id, u.handle, c.class_name, c.method_name " +
                        "from component_state cs, submission s, user u, component c " +
                        "where cs.round_id = ? " +
                        "and cs.status_id in (130,131,150) " +
                        "and s.submission_number = cs.submission_number " +
                        "and s.component_state_id = cs.component_state_id " +
                        "and u.user_id = cs.coder_id " +
                        "and c.component_id = cs.component_id";
        
        try {
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                System.out.print(".");
                UCRProcessor proc = UCRProcessorFactory.getProcessor(rs.getInt("language_id"));
                proc.initialize(rs.getString("class_name"), rs.getString("method_name"), rs.getString("submission_text"));
                String ret = "";
                try {
                    ret = proc.checkCode();
                } catch (Exception e) {
                    System.out.println("");
                    System.out.println("Exception - " + rs.getString("handle") + "," + rs.getString("class_name")); 
                    System.out.println(rs.getString("submission_text"));
                    e.printStackTrace();
                }
                
                if(!ret.equals("")) {
                    System.out.println("");
                    System.out.println("Invalid - " + rs.getString("handle") + "," + rs.getString("class_name"));
                    System.out.println(rs.getString("submission_text"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(ps);
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

