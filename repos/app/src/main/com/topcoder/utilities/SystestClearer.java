/*
 * SystestClearer.java
 *
 * Created on May 12, 2005, 10:16 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author rfairfax
 */
public class SystestClearer {
    
    /** Creates a new instance of SystestClearer */
    public SystestClearer() {
    }
    
    /**
     * @param args the command line arguments
     */
    
    public static final Logger log = Logger.getLogger(SystestClearer.class);
    public static void main(String[] args) {
        // TODO code application logic here
        Connection c = null;
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        
        try {
            //c = DBMS.getConnection(DBMS.DW_DATASOURCE_NAME);
            Class.forName("com.informix.jdbc.IfxDriver");
            c = DriverManager.getConnection("jdbc:informix-sqli://192.168.14.51:2020/informixoltp:INFORMIXSERVER=informixoltp_tcp;user=coder;password=teacup");
            c.setAutoCommit(false);
            
            //get a list of every succeeded system test record
            String sSQL = "select first 100 coder_id, round_id, component_id, test_case_id from system_test_result where succeeded = 1 and received is not null";
            ps = c.prepareStatement(sSQL);
            
            
            String sUpdate = "update system_test_result set received = null where coder_id = ? and round_id = ? and component_id = ? and test_case_id = ?";
            psUpdate = c.prepareStatement(sUpdate);
            
            boolean quit = false;
            long count = 0;
            log.info("STARTING");
            while(!quit) {
                rs = ps.executeQuery();
                quit = true;
                while(rs.next()) {
                    quit = false;
                    count++;
                    psUpdate.clearParameters();
                    psUpdate.setLong(1, rs.getLong("coder_id"));
                    psUpdate.setLong(2, rs.getLong("round_id"));
                    psUpdate.setLong(3, rs.getLong("component_id"));
                    psUpdate.setLong(4, rs.getLong("test_case_id"));
                    psUpdate.executeUpdate();
                }
                c.commit();
                log.info("PROCESSED " + count + " RECORDS");

                rs.close();
                rs = null;
                
            }
            ps.close();
            ps = null;
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
            }
            try {
                if (rs != null) rs.close();
            } catch (Exception e1) {
            }
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
            }
            try {
                if (psUpdate != null) psUpdate.close();
            } catch (Exception e1) {
            }
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
            }
        }
    }
    
}
