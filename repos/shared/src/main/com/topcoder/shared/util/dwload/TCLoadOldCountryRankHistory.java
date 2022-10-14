package com.topcoder.shared.util.dwload;

/**
 * It fills the country_rank_history table with the history for all the rounds from the beginning
 * up to the last one.
 * Just TARGET_DB is used; data is taken from algo_rating_history in that db.
 * 
 * @author Cucu
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

public class TCLoadOldCountryRankHistory extends TCLoad {

    private static Logger log = Logger.getLogger(TCLoadOldCountryRankHistory.class);

    /**
     * Constructor. Set our usage message here.
     */
    public TCLoadOldCountryRankHistory() {
        log.debug("TCLoadOldCountryRankHistory constructor called...");
        USAGE_MESSAGE =  "TCLoadOldCountryRankHistory - No Parameters";
    }


    /**
     * This method is passed any parameters passed to this load
     */
    public boolean setParameters(Hashtable params) {
        return true;
    }

    /**
     * This method performs the load for the round information tables
     */
    public void performLoad() throws Exception {
        log.debug("performLoad called...");
        try {

            clearCountryRankHistory();
            
            List rounds = getRounds();
            
            for (int i = 0; i < rounds.size(); i++) {
                int round = ((Integer) rounds.get(i)).intValue();
                log.debug("loading round " + round + " (" + (i+1) + "/" + rounds.size() + ")");
                
                List ratings = getRatings(round);
                List countryRank = calculateCountryRank(ratings);
                loadCountryRankHistory(countryRank, round);                    
            }
            log.info("SUCCESS:  country rank history loaded");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }
    /**
     * claer table country_rank_historY.
     */
    private void clearCountryRankHistory() throws Exception {
        PreparedStatement psDel = prepareStatement("delete from country_rank_history", TARGET_DB);
        psDel.executeUpdate();
    }



    /**
     * Load table country_rank_history from a list of CountryRank objects passed in list parameter.
     */
    private void loadCountryRankHistory(List list, int roundId) throws Exception {
        StringBuffer query = null;
        PreparedStatement psIns = null;
        int count = 0;

        try {
            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO country_rank_history (country_code, member_count, rating, rank, percentile, algo_rating_type_id, round_id)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            int size = list.size();
            for (int j = 0; j < size; j++) {
                CountryRank cr = (CountryRank) list.get(j);
                psIns.clearParameters();
                psIns.setString(1, cr.getCountryCode());
                psIns.setInt(2, cr.getMemberCount());
                psIns.setDouble(3, cr.getRating());
                psIns.setInt(4, cr.getRank());
                psIns.setDouble(5,  cr.getPercentile());
                psIns.setInt(6, 1); // load ratings for regular competitions
                psIns.setDouble(7, roundId);
                count += psIns.executeUpdate();
            }

            log.info(count + " records loaded for round " + roundId);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'country_rank_history' table failed for overall rating rank.\n" +
                    sqle.getMessage());
        } finally {
            close(psIns);
        }

    }

    /**
     * Get a list of Integer's that are the different round numbers in algo_rating_history table. 
     */
   private List getRounds() throws Exception {
       PreparedStatement psSel = null;
       ResultSet rs = null;
       
       try {
           List l = new ArrayList();
           
           psSel = prepareStatement(
                   "select distinct round_id from algo_rating_history order by round_id", TARGET_DB);
           
           rs = psSel.executeQuery();
           while (rs.next()) {
               l.add(new Integer(rs.getInt("round_id")));
           }
           
           return l;
       } catch (SQLException sqle) {
           DBMS.printSqlException(true, sqle);
           throw new Exception("Couldn't retrieve the list of rounds.");
       } finally {
           close(psSel);
       }
   }

   /**
    * Get the coder's rating for a round. Just the coders that were active on that round
    * are returned.
    * 
    * @param roundId 
    * @return
    * @throws Exception
    */
    private List getRatings(int roundId) throws Exception {
        StringBuffer query = null;
        PreparedStatement psSel = null;
        ResultSet rs = null;
        List ret = null;

        try {
            // coder_rank_history is joined to select just the active coders.
            query = new StringBuffer(200);
            query.append(" select arh.coder_id ");
            query.append(" , arh.rating ");
            query.append(" , c.comp_country_code as country_code ");
            query.append(" from algo_rating_history arh ");
            query.append(" , coder_rank_history crh ");
            query.append(" , coder c ");
            query.append(" where arh.round_id = crh.round_id ");
            query.append(" and arh.coder_id = crh.coder_id ");
            query.append(" and c.coder_id = arh.coder_id ");
            query.append(" and crh.coder_rank_type_id = 2 ");
            query.append(" and arh.round_id = ?  ");

            psSel = prepareStatement(query.toString(), TARGET_DB);
            psSel.setInt(1, roundId);
            
            rs = psSel.executeQuery();
            ret = new ArrayList();
            while (rs.next()) {
               ret.add(new CoderRating(rs.getLong("coder_id"), rs.getInt("rating"), rs.getString("country_code")));
            }


        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Get list of  ratings failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
        return ret;

    }


   
    /**
     * Calculate the country rating from a list of coder ratings.
     * It also fills the rank and percentile.
     *
     * @param list list of CoderRating
     * @return a list of CountryRank with the country's ranking
     */
    private List calculateCountryRank(List ratings) {
        CoderRating cr = null;
        Collections.sort(ratings);

        int size = ratings.size();
        Map countryRating = new HashMap();

        // Add all the coders to their country's rating
        for (int i = 0; i < size; i++) {
            cr = (CoderRating) ratings.get(i);
            String cc = cr.getCountryCode();
            
            if (cc == null) continue;

            CountryRank r = (CountryRank) countryRating.get(cc);
            if (r == null) {
                r = new CountryRank(cc);
                countryRating.put(cc, r);
            }
            r.addCoder(cr.getRating());
        }

        // copy to l just the countries with at least 10 coders
        ArrayList l = new ArrayList();

        for(Iterator it = countryRating.values().iterator(); it.hasNext(); )
        {
            CountryRank r = (CountryRank) it.next();
            if (r.getMemberCount() >= 10) {
                l.add(r);
            }
        }

        Collections.sort(l);
        int rank = 0;
        double rating = -1;
        size = l.size();
        for (int i = 0; i < size; i++) {
            CountryRank r = (CountryRank) l.get(i);

            if (Math.abs(rating - r.getRating()) >= 0.01) {
                rank = i + 1;
            }
            rating = r.getRating();


            r.setRank(rank);
            r.setPercentile((double) 100 * ((double) (size - rank) / size));
        }

        return l;
    }

    private class CoderRating implements Comparable {
        private long coderId = 0;
        private int rating = 0;
        private String countryCode = null;
        
        CoderRating(long coderId, int rating, String countryCode) {
            this.coderId = coderId;
            this.rating = rating;
            this.countryCode = countryCode;
        }

        public int compareTo(Object other) {
            if (((CoderRating) other).getRating() > rating)
                return 1;
            else if (((CoderRating) other).getRating() < rating)
                return -1;
            else
                return 0;
        }

        long getCoderId() {
            return coderId;
        }

        int getRating() {
            return rating;
        }

        void setCoderId(long coderId) {
            this.coderId = coderId;
        }

        void setRating(int rating) {
            this.rating = rating;
        }


        String getCountryCode() {
            return countryCode;
        }

        void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String toString() {
            return new String(coderId + ":" + rating + ":");
        }

    }

       private class CountryRank implements Comparable {
        private final String countryCode;

        /**
         * Con
         */
        private final static double R = 0.87;

        private int memberCount = 0;

        /**
         * Sum of the rating, without multiplying by the factor
         */
        private double ratingSum = 0;

        /**
         * Calculated rating.  Negative value indicates that the value needs to be calculated
         */
        private double rating = -1;

        private int rank;
        private double percentile;

        public CountryRank(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public int getMemberCount() {
            return memberCount;
        }
        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }
        public double getPercentile() {
            return percentile;
        }
        public void setPercentile(double percentile) {
            this.percentile = percentile;
        }
        public int getRank() {
            return rank;
        }
        public void setRank(int rank) {
            this.rank = rank;
        }

        public double getRating() {
            // if it is negative it means that the value needs to be calculated
            if (rating < 0) {
                if (memberCount == 0) {
                    throw new IllegalArgumentException("can't calculate the country rating when there are no members");
                }

                rating = ratingSum * ((1 - R) / (1 - Math.pow(R, memberCount)));
            }
            return rating;
        }

        public void addCoder(int coderRating)
        {
            ratingSum += coderRating * Math.pow(R, memberCount);
            memberCount++;
        }

        public int compareTo(Object other) {
            if (((CountryRank) other).getRating() > getRating())
                return 1;
            else if (((CountryRank) other).getRating() < getRating())
                return -1;
            else
                return 0;
        }


    }

}

