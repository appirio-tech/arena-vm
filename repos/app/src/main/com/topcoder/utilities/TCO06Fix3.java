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
public class TCO06Fix3 {
    
    public static final Logger log = Logger.getLogger(CheckThreading.class);

    public static void main(String[] args) {
        TCO06Fix3 tmp = new TCO06Fix3();
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
    public TCO06Fix3() {
    }
    
    private static final int round1 = 9905;
    private static final int round2 = 9906;
    
    public void run(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sqlStr = "select rr.coder_id, handle from round_registration rr, round_registration rr2, user u where rr.round_id = " + round1 + " and rr2.round_id = " + round2 + " and rr.coder_id = rr2.coder_id and u.user_id = rr.coder_id";
        
        try {
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                System.out.println("--" + rs.getString("handle"));
                System.out.println("delete from round_registration where coder_id = " + rs.getInt("coder_id") + " and round_id = " + round2 + ";");
                System.out.println("delete from room_result where coder_id = " + rs.getInt("coder_id") + " and round_id = " + round2 + ";");
                System.out.println("");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
}
