/*
 * CheckThreading.java
 *
 * Created on February 24, 2006, 11:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessor;
import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessorFactory;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author rfairfax
 */
public class CheckThreading {
    
    public static final Logger log = Logger.getLogger(CheckThreading.class);

    public static void main(String[] args) {
        CheckThreading tmp = new CheckThreading();
        Connection c = null;
        
        try {
            //c = DBMS.getConnection(DBMS.DW_DATASOURCE_NAME);
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
            DBMS.close(c);
        }
    }
    
    /** Creates a new instance of CheckThreading */
    public CheckThreading() {
    }
    
    public void run(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sqlStr = "select u.handle, s.submission_text  " +
                        "from long_component_state cs, long_submission s, user u " +
                        "where cs.round_id = ? " +
                        "and s.submission_number = cs.submission_number " +
                        "and s.example = 0 " +
                        "and s.long_component_state_id = cs.long_component_state_id " +
                        "and u.user_id = cs.coder_id ";
        
        try {
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, 9892);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                String text = rs.getString("submission_text");
                if(text.indexOf("Thread") != -1) {
                    System.out.println(rs.getString("handle"));
                } else if(text.indexOf("pthread") != -1) {
                    System.out.println(rs.getString("handle"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
}
