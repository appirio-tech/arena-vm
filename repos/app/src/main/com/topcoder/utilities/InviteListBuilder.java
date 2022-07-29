/*
 * InviteListBuilder.java
 *
 * Created on March 4, 2005, 2:31 PM
 */

package com.topcoder.utilities;

import com.topcoder.shared.util.DBMS;
import java.sql.*;
/**
 *
 * @author rfairfax
 */
public class InviteListBuilder {
    
    
    private static String sDriverName = "com.informix.jdbc.IfxDriver";
    
    /** Creates a new instance of InviteListBuilder */
    public InviteListBuilder() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Class.forName(sDriverName);

            Connection conn;

            conn = DBMS.getDirectConnection();

            //String sSQL = "select rr.coder_id, r.rating, r.vol, il.seed, il.rating as trating from room_result rr, rating r, invite_list il " +
              //          "where il.round_id = rr.round_id and il.coder_id = rr.coder_id and rr.round_id in (8058) and advanced = 'Y' and r.coder_id = rr.coder_id order by 2 desc";
            String sSQL = "select rr.coder_id, rr.rating, rr.vol from invite_list rr, algo_rating r  " +
                    "where rr.round_id in (10500) and r.coder_id = rr.coder_id and r.algo_rating_type_id = 3 " +
                    " and rr.coder_id in (select coder_id from long_comp_result where round_id = 10500 and attended = 'Y') " +
                    " order by 2 desc";

            PreparedStatement ps = conn.prepareStatement(sSQL);

            ResultSet rs = ps.executeQuery();

            int i = 0;

            int oldrating = 0;
            int count = 0;

            while(rs.next()) {
                if(rs.getInt("rating") != oldrating) {
                    i = i + count + 1;
                    count = 0;
                    oldrating = rs.getInt("rating");
                } else {
                    count++;
                }

                String sInsert = "update invite_list set seed = ?, rating = ?, vol = ?, tournament_rating = ? where coder_id = ? and round_id = 10501 ";
                //String sInsert = "update invite_list set seed = ? where coder_id = ? and round_id = 10501 ";
                PreparedStatement ps2 = conn.prepareStatement(sInsert);
                ps2.setInt(1, i);
                ps2.setInt(2, rs.getInt("rating"));
                ps2.setInt(3, rs.getInt("vol"));
                ps2.setInt(4, rs.getInt("rating"));
                ps2.setInt(5, rs.getInt("coder_id"));
                
                ps2.execute();
                ps2.close();
            }

            rs.close();
            ps.close();

            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
