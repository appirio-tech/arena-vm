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
public class TCO06Fix1 {
    
    public static final Logger log = Logger.getLogger(CheckThreading.class);

    public static void main(String[] args) {
        TCO06Fix1 tmp = new TCO06Fix1();
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
    public TCO06Fix1() {
    }
    
    private static final int round1 = 9905;
    private static final int round2 = 9906;
    
    public void run(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sqlStr = "select rr.coder_id, case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = rr.round_id) then 1 else 0 end as round1open, " +
                "case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = " + round2 + ") then 1 else 0 end as round2open, " +
                "rr.point_total, rr2.point_total, " +
                "case when exists(select coder_id from component_state, compilation  where round_id in (" + round1 + ") and compilation.component_state_id " +
                " = component_state.component_state_id and coder_id = rr.coder_id and compilation.open_time <=1141148269618) then " + round1 + " else " + round2 + " end as new_round, u.handle " +
                "from room_result rr, room_result rr2, user u " +
                "where rr.round_id = " + round1 + " " +
                "and rr2.round_id = " + round2 + " and rr2.coder_id = rr.coder_id " +
                "and u.user_id = rr.coder_id " +
                "and case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = " + round2 + ") then 1 else 0 end = 1 " +
                "and case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = rr.round_id) then 1 else 0 end =1 " +
                "and exists(select coder_id from component_state, compilation  where round_id in (" + round1 + "," + round2 + ") and compilation.component_state_id " +
                " = component_state.component_state_id and coder_id = rr.coder_id and compilation.open_time <=1141148269618);";
        
        try {
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                System.out.println("--" + rs.getString("handle"));
                System.out.println("delete from round_registration where coder_id = " + rs.getInt("coder_id") + " and round_id = " + rs.getInt("new_round") + ";");
                System.out.println("delete from room_result where coder_id = " + rs.getInt("coder_id") + " and round_id = " + rs.getInt("new_round") + ";");
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
