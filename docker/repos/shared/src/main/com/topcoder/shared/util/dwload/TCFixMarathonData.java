package com.topcoder.shared.util.dwload;

import com.topcoder.shared.util.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Fix some missing data for marathon matches:
 * 
 * - Loads algo_rating_history for past marathon matches, from long_comp_result.
 * 
 * - Load coder_rank_history, coder_rank, country_coder_rank, state_coder_rank and school_coder_rank
 * 
 * @author Cucu
 *
 */
public class TCFixMarathonData extends TCLoadRank {

    private static Logger log = Logger.getLogger(TCLoadRound.class);

    @Override
    public void performLoad() throws Exception {
        List<Integer> rounds = getRounds();
        
        clearData();
        
        List<CoderRating> l = null;
        Integer prevRoundId = null;
        for (Integer rId : rounds) {
            // needed for ranks
            roundId = rId;
            
            log.info("Loading round " + roundId);

            if (prevRoundId != null) {
                copyHistory(prevRoundId, roundId);
            }
            loadHistory(roundId);

            l = getRatingsForRound(MARATHON_RATING_TYPE_ID);
            loadRatingRankHistory(OVERALL_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);
            loadRatingRankHistory(ACTIVE_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);

            prevRoundId = roundId;
        }
        
        loadRatingRank(OVERALL_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);
        loadRatingRank(ACTIVE_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);

        loadCountryRatingRank(OVERALL_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);
        loadCountryRatingRank(ACTIVE_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);

        loadStateRatingRank(OVERALL_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);
        loadStateRatingRank(ACTIVE_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);

        loadSchoolRatingRank(OVERALL_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);
        loadSchoolRatingRank(ACTIVE_RATING_RANK_TYPE_ID, MARATHON_RATING_TYPE_ID, l);

        
        updateAlgoRating();
        
        // set the time_id column for every round
        setRoundsTime();
        
        log.info("SUCCESS");
    }

    private void clearData() throws Exception {
        PreparedStatement psDel = null;
        
        psDel = prepareStatement("delete from algo_rating_history where algo_rating_type_id= 3", TARGET_DB);
        psDel.executeUpdate();

        psDel = prepareStatement("delete from algo_rating where algo_rating_type_id= 3", TARGET_DB);
        psDel.executeUpdate();

    }

    private void updateAlgoRating() throws SQLException {
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psSelNumCompetitions = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        StringBuffer query = new StringBuffer(100); 
        query.append("select lcr.round_id, coder_id, new_rating, new_vol ");
        query.append("from long_comp_result lcr, ");
        query.append("round_segment rs ");
        query.append("where lcr.round_id = rs.round_id ");
        query.append("and rs.segment_id = 2 ");
        query.append("and lcr.attended='Y' ");
        query.append("and lcr.rated_ind = 1 ");
        query.append("order by coder_id, rs.start_time ");
        psSel = prepareStatement(query.toString(), SOURCE_DB);

        query = new StringBuffer(100);
        query.append("INSERT INTO algo_rating(coder_id, rating, vol, num_ratings, algo_rating_type_id, ");
        query.append("                         lowest_rating, highest_rating, first_rated_round_id, last_rated_round_id, num_competitions) ");
        query.append(" VALUES (?,?,?,?,?,?,?,?,?,?)");
        psIns = prepareStatement(query.toString(), TARGET_DB);
        
        query = new StringBuffer(100);
        query.append("SELECT count(*) ");
        query.append(" FROM long_comp_result ");
        query.append(" WHERE coder_id =? ");
        query.append(" AND attended='Y' ");
        psSelNumCompetitions = prepareStatement(query.toString(), TARGET_DB);

        try {
            
            rs = psSel.executeQuery();
            
            boolean end = false;
            
            int prevCoder = -1;
            int minRating = 0;
            int maxRating = 0;
            int firstRoundId=0;
            int lastRoundId =0;
            int lastRating = 0;
            int lastVol = 0;
            int numRatings = 0;
            
            int count = 0;
            while (!end) {
                end = !rs.next();
                
                int coder = -1;
                int rating = -1;
                int round = -1;
                int vol = -1;
                
                if (!end) {
                    coder = rs.getInt("coder_id");
                    rating = rs.getInt("new_rating");
                    round = rs.getInt("round_id");
                    vol = rs.getInt("new_vol");
                }

                if (coder != prevCoder || end) {
                    if (prevCoder > 0) {
                        psSelNumCompetitions.clearParameters();
                        psSelNumCompetitions.setInt(1, prevCoder);
                        rs2 = psSelNumCompetitions.executeQuery();
                        rs2.next();
                        int numCompetitions = rs2.getInt(1);
                        close(rs2);
                        
                        psIns.clearParameters();
                        psIns.setInt(1, prevCoder);
                        psIns.setInt(2, lastRating);
                        psIns.setInt(3, lastVol);
                        psIns.setInt(4, numRatings);
                        psIns.setInt(5, MARATHON_RATING_TYPE_ID);
                        psIns.setInt(6, minRating);
                        psIns.setInt(7, maxRating);
                        psIns.setInt(8, firstRoundId);
                        psIns.setInt(9, lastRoundId);
                        psIns.setInt(10, numCompetitions);
                        
                        int retVal = psIns.executeUpdate();
                                            
                        
                        count = count + retVal;
                    }
                    
                    minRating = rating;
                    maxRating = rating;
                    firstRoundId = round;
                    prevCoder = coder;
                    numRatings = 0;
                } else {                
                    if (rating < minRating) minRating = rating;
                    if (rating > maxRating) maxRating = rating;
                }
                lastRoundId = round;
                lastRating = rating;
                lastVol = vol;
                numRatings++;
                
            }
            
            log.info("algo_rating updated " + count + " rows" );

        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psSelNumCompetitions);
        }

    }

    private void loadHistory(Integer roundId) throws SQLException {

        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;

        StringBuffer query = new StringBuffer(100);
        query.append("SELECT coder_id, coder_id, new_rating, new_vol ");
        query.append("FROM long_comp_result ");
        query.append("WHERE round_id = ? ");
        query.append("AND rated_ind=1 ");
        psSel = prepareStatement(query.toString(), TARGET_DB);
        psSel.setInt(1, roundId);
        
        query = new StringBuffer(100);
        query.append("UPDATE algo_rating_history SET rating=?, vol=?, num_ratings=num_ratings+1 ");
        query.append("WHERE round_id = ? AND coder_id=?");
        psUpd = prepareStatement(query.toString(), TARGET_DB);
        
        query = new StringBuffer(100);
        query.append("INSERT INTO algo_rating_history(coder_id, round_id, algo_rating_type_id, rating,vol, num_ratings) ");
        query.append("VALUES (?,?,3,?,?,1)");
        psIns = prepareStatement(query.toString(), TARGET_DB);
        
        int count = 0;
        try {
            rs = psSel.executeQuery();
            
            while(rs.next()) {
                psUpd.clearParameters();
                psUpd.setInt(1, rs.getInt("new_rating"));
                psUpd.setInt(2, rs.getInt("new_vol"));
                psUpd.setInt(3, roundId);
                psUpd.setInt(4, rs.getInt("coder_id"));
                int retVal = psUpd.executeUpdate();
                
                if (retVal == 0) {
                    psIns.clearParameters();
                    psIns.setInt(1, rs.getInt("coder_id"));
                    psIns.setInt(2, roundId);
                    psIns.setInt(3, rs.getInt("new_rating"));
                    psIns.setInt(4, rs.getInt("new_vol"));
                    psIns.executeUpdate();                    
                }
                
                count++;
                printLoadProgress(count, " ratings inserted");                
            }
            
            log.info("algo_history_rating inserted from long_comp_result: " + count);
            
        } finally {
            close(rs);
            close (psSel);
            close (psIns);
            close (psUpd);
        }

    }

    /**
     * Copy the algo_rating_history rows from a previous round to the actual one.
     * 
     * @param prevRoundId
     * @param roundId
     * @throws SQLException
     */
    private void copyHistory(Integer prevRoundId, Integer roundId) throws SQLException {
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        
        StringBuffer query = new StringBuffer(100);
        query.append("SELECT coder_id, algo_rating_type_id, rating,vol, num_ratings ");
        query.append(" from algo_rating_history where round_id = ?");
        psSel = prepareStatement(query.toString(), TARGET_DB);
        psSel.setInt(1, prevRoundId);
        
        query = new StringBuffer(100);
        query.append("INSERT INTO algo_rating_history(coder_id, round_id, algo_rating_type_id, rating,vol, num_ratings) ");
        query.append("VALUES (?,?,?,?,?,?)");
        psIns = prepareStatement(query.toString(), TARGET_DB);
               
        int count = 0;
        try {
            rs = psSel.executeQuery();
            
            while(rs.next()) {
                psIns.clearParameters();
                psIns.setInt(1, rs.getInt("coder_id"));
                psIns.setInt(2, roundId);
                psIns.setInt(3, rs.getInt("algo_rating_type_id"));
                psIns.setInt(4, rs.getInt("rating"));
                psIns.setInt(5, rs.getInt("vol"));
                psIns.setInt(6, rs.getInt("num_ratings"));
                psIns.executeUpdate();
                
                count++;
                printLoadProgress(count, " ratings copied");                
            }

            log.info("algo_history_rating copied from " + prevRoundId + " to " + roundId + ": " + count);
            
        } finally {
            close(rs);
            close (psSel);
            close (psIns);
        }

        
    }

    
    
    /**
     * Set the time_id in rounds.  It's not just for marathon, but for every round.
     * @throws SQLException
     */
    private void setRoundsTime() throws SQLException {
        PreparedStatement psUpd = null;
        PreparedStatement psSel = null;
        ResultSet rs = null;
        
        StringBuffer query = new StringBuffer(100);
        query.append("select r.round_id, start_time ");
        query.append("from round r, round_segment rs ");
        query.append("where r.round_id = rs.round_id ");
        query.append("and rs.segment_id = 2");
        psSel = prepareStatement(query.toString(), SOURCE_DB);
        
        query = new StringBuffer(100);
        query.append("UPDATE round set time_id = ? where round_id =? ");
        psUpd = prepareStatement(query.toString(), TARGET_DB);
               
        int count = 0;
        try {
            rs = psSel.executeQuery();
            
            while(rs.next()) {
                int time_id = lookupTimeId(rs.getTimestamp("start_time"), TARGET_DB);
                int round_id = rs.getInt("round_id");
                
                psUpd.clearParameters();

                psUpd.setInt(1, time_id);
                psUpd.setInt(2, round_id);
                
                int retVal = psUpd.executeUpdate();
                
                if (retVal == 0) {
                    log.info("round " + round_id + " not found in DW");
                }
                count += retVal;
            }

            log.info("updated time_id in " + count + " rounds");
            
        } finally {
            close(rs);
            close (psSel);
            close (psUpd);
        }

        
    }

    @Override
    public boolean setParameters(Hashtable params) {
        return true;
    }
    
    /*
    private List<Integer> getRounds() throws SQLException {
        List<Integer> rounds = new ArrayList<Integer>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        StringBuffer query = new StringBuffer(100);
        query.append("SELECT r.round_id ");
        query.append("FROM round r, round_type_lu rt, round_segment rs "); 
        query.append("WHERE r.round_type_id = rt.round_type_id "); 
        query.append("AND rs.round_id = r.round_id ");
        query.append("AND rs.segment_id = 2 ");
        query.append("AND algo_rating_type_id = 3 ");
        query.append("AND rated_ind = 1 ");
        query.append("ORDER BY rs.start_time "); 
        
        try {
            ps = prepareStatement(query.toString(), SOURCE_DB);
            rs = ps.executeQuery();
            while(rs.next()) {
                rounds.add(rs.getInt("round_id"));
            }
        } finally {
            close(rs);
            close (ps);
        }
        return rounds;
    }
    */


    
    private List<Integer> getRounds() throws SQLException {
        List<Integer> rounds = new ArrayList<Integer>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        StringBuffer query = new StringBuffer(100);
        query.append("select round_id from round ");
        query.append("where round_type_id in (13,15,19) ");
        query.append("order by rating_order");
        
        try {
            ps = prepareStatement(query.toString(), TARGET_DB);
            rs = ps.executeQuery();
            while(rs.next()) {
                rounds.add(rs.getInt("round_id"));
            }
        } finally {
            close(rs);
            close (ps);
        }
        return rounds;
    }

}
