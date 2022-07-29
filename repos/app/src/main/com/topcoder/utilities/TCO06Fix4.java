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
public class TCO06Fix4 {
    
    public static final Logger log = Logger.getLogger(CheckThreading.class);

    public static void main(String[] args) {
        TCO06Fix4 tmp = new TCO06Fix4();
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
    public TCO06Fix4() {
    }
    
    private static final int round1 = 9901;
    private static final int round2 = 9905;
    private static final int round3 = 9906;
    
    public void run(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sqlStr = "select rr.coder_id, case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = rr.round_id) then 1 else 0 end as round1open,  " +
            "case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = " + round2 + ") then 1 else 0 end as round2open, " +
            "case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = " + round3 + ") then 1 else 0 end as round3open, " +
            "case when exists(select coder_id from component_state  where coder_id = rr.coder_id and round_id = " + round3 + ") then " + round3 + " when exists(select coder_id from component_state  where coder_id = rr.coder_id and round_id = " + round2 + ") then " + round2 + " else " + round1 + " end as new_round, u.handle " +
            "from room_result rr, user u " +
            "where rr.round_id = " + round1 + " " +
            "and u.user_id = rr.coder_id " +
            "and( case when exists(select coder_id from component_state  where coder_id = rr.coder_id and round_id = " + round3 + ") then 1 else 0 end = 1 " +
            "or case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = rr.round_id) then 1 else 0 end =1 " +
            "or case when exists(select coder_id from component_state  where coder_id = rr.coder_id and round_id = " + round2 + ") then 1 else 0 end = 1) " +
            " " +
            "and not ( ( case when exists(select coder_id from component_state  where coder_id = rr.coder_id and round_id = " + round3 + ") then 1 else 0 end = 1 " +
            "and case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = rr.round_id) then 1 else 0 end =1) " +
            "or ( case when exists(select coder_id from component_state  where coder_id = rr.coder_id and round_id = " + round2 + ") then 1 else 0 end = 1 " +
            "and case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = rr.round_id) then 1 else 0 end =1) " +
            "or ( case when exists(select coder_id from component_state  where coder_id = rr.coder_id and round_id = " + round3 + ") then 1 else 0 end = 1 " +
            "and case when exists(select coder_id from component_state where coder_id = rr.coder_id and round_id = " + round2 + ") then 1 else 0 end =1)) " +
            " " +
            "and not exists(select coder_id from component_state, compilation  where round_id in (" + round1 + "," + round2 + "," + round3 + ") and compilation.component_state_id " +
            " = component_state.component_state_id and coder_id = rr.coder_id and compilation.open_time <=1141148269618) " +
            "order by u.handle";
        
        try {
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                System.out.println("--" + rs.getString("handle"));
                String s = "";
                if(rs.getInt("new_round") == round1)
                    s = round2 + "," + round3;
                if(rs.getInt("new_round") == round2)
                    s = round1 + "," + round3;
                if(rs.getInt("new_round") == round3)
                    s = round1 + "," + round2;
                
                System.out.println("delete from round_registration where coder_id = " + rs.getInt("coder_id") + " and round_id in ( " + s + ");");
                System.out.println("delete from room_result where coder_id = " + rs.getInt("coder_id") + " and round_id in ( " + s + ");");
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
