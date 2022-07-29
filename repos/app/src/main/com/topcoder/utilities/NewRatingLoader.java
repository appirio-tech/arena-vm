/*
 * NewRatingLoader.java
 *
 * Created on May 12, 2005, 2:10 PM
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
public class NewRatingLoader {
    
    /** Creates a new instance of NewRatingLoader */
    public NewRatingLoader() {
    }
    
    public static final Logger log = Logger.getLogger(NewRatingLoader.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection c = null;
        Connection cDW = null;
        PreparedStatement ps = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        
        try {
            //c = DBMS.getConnection(DBMS.DW_DATASOURCE_NAME);
            Class.forName("com.informix.jdbc.IfxDriver");
            c = DriverManager.getConnection("jdbc:informix-sqli://192.168.14.51:2020/informixoltp:INFORMIXSERVER=informixoltp_tcp;user=coder;password=teacup");
            c.setAutoCommit(false);
            
            cDW = DriverManager.getConnection("jdbc:informix-sqli://192.168.14.52:2022/topcoder_dw:INFORMIXSERVER=datawarehouse_tcp;user=coder;password=teacup");
            
            //get a list of every succeeded system test record
            String sSQL = "select coder_id, round_id, old_rating, new_rating from room_result where new_rating is not null and new_rating > 0";
            ps = cDW.prepareStatement(sSQL);
            
            
            String sUpdate = "update room_result set old_rating = ?, new_rating = ? where coder_id = ? and round_id = ?";
            psUpdate = c.prepareStatement(sUpdate);
            
            long count = 0;
            log.info("STARTING");

            rs = ps.executeQuery();
            while(rs.next()) {
                count++;
                if(count % 100 == 0) {
                    c.commit();
                    log.info("PROCESSED " + count + " RECORDS");
                }
                psUpdate.clearParameters();
                psUpdate.setLong(1, rs.getLong("old_rating"));
                psUpdate.setLong(2, rs.getLong("new_rating"));
                psUpdate.setLong(3, rs.getLong("coder_id"));
                psUpdate.setLong(4, rs.getLong("round_id"));
                psUpdate.executeUpdate();
            }
            c.commit();
                
            rs.close();
            rs = null;

            ps.close();
            ps = null;
            
            psUpdate.close();
            psUpdate = null;
            
            //load rating table
            
            sSQL = "select coder_id, rating, vol, num_ratings from rating where rating > 0";
            ps = cDW.prepareStatement(sSQL);
            
            sUpdate = "update rating set rating = ?, rating_no_vol = ?, num_ratings = ?, vol = ? where coder_id = ? and rating <> -1";
            psUpdate = c.prepareStatement(sUpdate);
            
            count = 0;
            log.info("STARTING");

            rs = ps.executeQuery();
            while(rs.next()) {
                count++;
                if(count % 100 == 0) {
                    c.commit();
                    log.info("PROCESSED " + count + " RECORDS");
                }
                psUpdate.clearParameters();
                psUpdate.setLong(1, rs.getLong("rating"));
                psUpdate.setLong(2, rs.getLong("rating"));
                psUpdate.setLong(3, rs.getLong("num_ratings"));
                psUpdate.setLong(4, rs.getLong("vol"));
                psUpdate.setLong(5, rs.getLong("coder_id"));
                psUpdate.executeUpdate();
            }
            c.commit();
            
            rs.close();
            rs = null;

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
            try {
                if (cDW != null) c.close();
            } catch (Exception e1) {
            }
        }
    }
    
}
