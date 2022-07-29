/*
 * RecalcPlaced.java
 *
 * Created on February 17, 2006, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import com.topcoder.server.ejb.TestServices.LongContestServices;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.shared.util.DBMS;

/**
 *
 * @author rfairfax
 */
public class QueueLongTestCases3 {
    private static int roundId;
    private static int componentId;


    public QueueLongTestCases3() {
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("args : roundId componentId");
            roundId = Integer.parseInt(args[0]);
            componentId = Integer.parseInt(args[1]);
            go();
        } catch (NamingException ex) {
            Logger.getLogger(QueueLongTestCases3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(QueueLongTestCases3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void go() throws NamingException, RemoteException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            LongContestServices ts = LongContestServicesLocator.getService();
            c = DBMS.getDirectConnection();

            ps  = c.prepareStatement("select count(*) from system_test_case lcs where component_id = ? and system_flag = 1");
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();
            int stCount = rs.getInt(1);
            if (stCount == 0) {
                System.out.println("No system test cases were found for component: "+componentId);
                return;
            }
            DBMS.close(ps, rs);
            System.out.println("Total number of System tests: "+stCount);
            
            String sql = "select lcs.coder_id, count(*) from long_component_state lcs, long_system_test_result l where lcs.round_id = ? and lcs.submission_number >= 1 and l.round_id = lcs.round_id and l.coder_id = lcs.coder_id and l.test_action = 12 and l.component_id = lcs.component_id group by 1 having count(*) < "+stCount;
            ps = c.prepareStatement(sql);
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            
            int cnt = 0;
            
            String sql2 = "select test_case_id from system_test_case lcs where component_id = ? and system_flag = 1 and not exists (select 1 from long_system_test_result where round_id = ? and coder_id = ? and test_case_id = lcs.test_case_id)";
            PreparedStatement ps2 = null;
            ResultSet rs2 = null;
            ps2 = c.prepareStatement(sql2);
            try {
                while(rs.next()) {
                    int coderId = rs.getInt(1);
                    System.out.println("Queuing missing cases for coder: " + coderId);
                    boolean f = false;
                    ps2.setInt(1, componentId);
                    ps2.setInt(2, roundId);
                    ps2.setInt(3, coderId);
                    rs2 = ps2.executeQuery();
                    
                    ArrayList testCases = new ArrayList();
                    while(rs2.next()) {
                        f = true;
                        testCases.add(new Integer(rs2.getInt(1)));
                    }
                    DBMS.close(rs2);
                    if(f) {
                        cnt++;
                        System.out.println(cnt + ") Queuing "+testCases.size()+" cases: "+testCases);
                        long[] testCaseIds = new long[testCases.size()];
                        for(int i = 0; i < testCases.size(); i++) {
                            testCaseIds[i] = ((Integer)testCases.get(i)).intValue();
                        }
                        ts.queueLongSystemTestCase(roundId, new int[] {coderId}, testCaseIds);
                    }
                }
            } finally {
                DBMS.close(ps2, rs2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(c, ps, rs);
        }
    }
    
}
