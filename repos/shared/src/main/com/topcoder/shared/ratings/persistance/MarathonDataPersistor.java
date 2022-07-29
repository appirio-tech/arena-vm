/*
 * MarathonDataPersistor.java
 *
 * Created on January 5, 2007, 8:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.persistance;

import com.topcoder.shared.ratings.model.RatingData;
import com.topcoder.shared.util.DBMS;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Saves marathon ratings to algo_rating and long_comp_result
 * @author rfairfax
 */
public class MarathonDataPersistor implements DataPersistor {
    
    private int roundId;
    private Connection conn;
    
    /**
     * Creates a new instance of MarathonDataPersistor
     * @param roundId the round to save to
     * @param conn db connection to use
     */
    public MarathonDataPersistor(int roundId, Connection conn) {
        this.roundId = roundId;
        this.conn = conn;
    }

    /**
     * saves the data to the db
     * @param data new rating data
     */
    public void persistData(RatingData[] data) {
        //start by setting long_comp_result
        //TODO: maybe remove old_rating / vol and move to the record creation?
        String sqlStr = "update long_comp_result set rated_ind = 1, " +
            "old_rating = (select rating from algo_rating " +
            "   where coder_id = long_comp_result.coder_id and algo_rating_type_id = 3), " +
            "old_vol = (select vol from algo_rating " +
            "   where coder_id = long_comp_result.coder_id and algo_rating_type_id = 3), " +
            "new_rating = ?, " +
            "new_vol = ? " +
            "where round_id = ? and coder_id = ?";
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;
        
        try {
            psUpdate = conn.prepareStatement(sqlStr);
            for(int i = 0; i < data.length; i++) {
                psUpdate.clearParameters();
                psUpdate.setInt(1, data[i].getRating());
                psUpdate.setInt(2, data[i].getVolatility());
                psUpdate.setInt(3, roundId);
                psUpdate.setInt(4, data[i].getCoderID());
                psUpdate.executeUpdate();
            }
            
            psUpdate.close();
            //update algo_rating
            String updateSql = "update algo_rating set rating = ?, vol = ?," +
                    " round_id = ?, num_ratings = num_ratings + 1 " +
                    "where coder_id = ? and algo_rating_type_id = 3";
            
            psUpdate = conn.prepareStatement(updateSql);
            
            String insertSql = "insert into algo_rating (rating, vol, round_id, coder_id, algo_rating_type_id, num_ratings) " +
                    "values (?,?,?,?,3,1)";
            
            psInsert = conn.prepareStatement(insertSql);
            
            for(int i = 0; i < data.length; i++) {
                psUpdate.clearParameters();
                psUpdate.setInt(1, data[i].getRating());
                psUpdate.setInt(2, data[i].getVolatility());
                psUpdate.setInt(3, roundId);
                psUpdate.setInt(4, data[i].getCoderID());
                if(psUpdate.executeUpdate() == 0) {
                    psInsert.clearParameters();
                    psInsert.setInt(1, data[i].getRating());
                    psInsert.setInt(2, data[i].getVolatility());
                    psInsert.setInt(3, roundId);
                    psInsert.setInt(4, data[i].getCoderID());
                    psInsert.executeUpdate();
                }
            }
            
            psUpdate.close();
            psInsert.close();
            
            //mark the round as rated
            psUpdate = conn.prepareStatement("update round set rated_ind = 1 where round_id = ?");
            psUpdate.setInt(1, roundId);
            psUpdate.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(psUpdate);
            DBMS.close(psInsert);
        }
    }
    
}
