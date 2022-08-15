/*
 * RecalcPlaced.java
 *
 * Created on February 17, 2006, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.topcoder.server.ejb.TestServices.LongContestServices;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.shared.util.DBMS;

/**
 *
 * @author rfairfax
 */
public class RecalcLongSystests {
    
    /** Creates a new instance of RecalcPlaced */
    public RecalcLongSystests() {
    }
    
    public static void main(String[] args) {
        int roundId = Integer.parseInt(args[0]);
        try {
            go(DBMS.getDirectConnection(), roundId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void go(Connection c, int roundId) {
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            
            LongContestServices ts = LongContestServicesLocator.getService();
            
            // update ROOM_RESULT.room_placed
            StringBuffer sqlStr = new StringBuffer();
            sqlStr.append("select lcr.round_id, lcr.coder_id ");
            sqlStr.append("from long_comp_result lcr ");
            sqlStr.append("where lcr.round_id = ? ");
            sqlStr.append("and lcr.point_total is not null ");
            sqlStr.append("and (select count(*) from long_system_test_result lsr ");
            sqlStr.append("where lsr.round_id = lcr.round_id ");
            sqlStr.append("and lsr.coder_id = lcr.coder_id ");
            sqlStr.append("and lsr.component_id = (select component_id from round_component where round_id = lcr.round_id) ");
            sqlStr.append("and lsr.test_action = 12 ");
            sqlStr.append(") <> (select count(*) from system_test_case where component_id = (select component_id from round_component where round_id = lcr.round_id) and system_flag = 1)");
            
            ps = c.prepareStatement(sqlStr.toString());
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            
            sqlStr = new StringBuffer();
            sqlStr.append("select stc.test_case_id from system_test_case stc ");
            sqlStr.append("where stc.component_id = (select component_id from round_component where round_id = ?) ");
            sqlStr.append("and system_flag = 1 ");
            sqlStr.append("and not exists (select * from long_system_test_result where round_id = ? ");
            sqlStr.append("and coder_id = ? ");
            sqlStr.append("and component_id = stc.component_id ");
            sqlStr.append("and test_case_id = stc.test_case_id ");
            sqlStr.append("and test_action = 12)");

            // Loop over the rows updating room result
            while(rs.next()) {
                ps2 = c.prepareStatement(sqlStr.toString());

                int coderId = rs.getInt("coder_id");

                ps2.setInt(1, roundId);
                ps2.setInt(2, roundId);
                ps2.setInt(3, coderId);
                
                rs2 = ps2.executeQuery();
                while(rs2.next()) {
                    long testCase = rs2.getLong("test_case_id");
                    try {
                    ts.queueLongSystemTestCase(roundId, new int[] {coderId}, new long[] {testCase});
                    } catch(Exception e) {
                        
                    }
                }

                rs2.close();
                ps2.close();
                ps2 = null;
            }
            
            rs.close();
            rs = null;
            
            ps.close();
            ps = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(null, ps2, rs2);
            DBMS.close(c, ps, rs);
        }
    }
    
}
