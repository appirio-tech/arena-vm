/*
 * RecalcPlaced.java
 *
 * Created on February 17, 2006, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.server.ejb.TestServices.LongContestServices;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.shared.util.DBMS;
import java.rmi.RemoteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

/**
 *
 * @author rfairfax
 */
public class QueueLongTestCases2 {
    
    /** Creates a new instance of RecalcPlaced */
    public QueueLongTestCases2() {
    }
    
    public static void main(String[] args) {
        try {
            go();
        } catch (NamingException ex) {
            Logger.getLogger(QueueLongTestCases2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(QueueLongTestCases2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void go() throws NamingException, RemoteException {
        try {
            LongContestServices ts = LongContestServicesLocator.getService();
            
            Connection c = DBMS.getDirectConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            
            String sql = "select test_case_id from system_test_case stc where stc.component_id = 7256  and system_flag = 1";
            ps = c.prepareStatement(sql);
            
            rs = ps.executeQuery();
            
            int cnt = 0;
            
            while(rs.next() && cnt <= 1000) {
                int id = rs.getInt(1);
                System.out.println("PROCESSING: " + id);
                boolean f = false;
                String sql2 = "select coder_id from long_component_state lcs where lcs.round_id = 10929 and lcs.submission_number >= 1 and not exists (select 1 from long_system_test_result where round_id = lcs.round_id and coder_id = lcs.coder_id and test_case_id = ?)";
                PreparedStatement ps2 = null;
                ResultSet rs2 = null;
                
                ps2 = c.prepareStatement(sql2);
                ps2.setInt(1, id);
                rs2 = ps2.executeQuery();
                
                ArrayList coders = new ArrayList();
                while(rs2.next()) {
                    f = true;
                    coders.add(new Integer(rs2.getInt(1)));
                }
                if(f) {
                    cnt++;
                    int[] coderIds = new int[coders.size()];
                    for(int i = 0; i < coders.size(); i++) {
                        coderIds[i] = ((Integer)coders.get(i)).intValue();
                    }
                    ts.queueLongSystemTestCase(10929, coderIds, new long[] {id});
                }
                rs2.close();
                ps2.close();
                
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
    
}
