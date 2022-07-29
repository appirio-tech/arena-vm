package com.topcoder.shared.util.dwload;

/**
 * TCLoadRank loads the rank and rank history tables.  There are many
 * different type of potential ranks, so this load has been separated
 * from the rest of the aggregate load.
 *
 * This load populated the rank and rank history tables for the following
 * types of ranks
 *
 * <ul>
 * <li>coder_rank - overall by rating</li>
 * <li>coder_rank_history - overall by rating</li>
 * </ul>
 *
 * Note: This load cannot be run for old rounds
 *
 * @author Greg Paul
 * @author Christopher Hopkins [TCid: darkstalker] (chrism_hopkins@yahoo.com)
 * @version $Revision$
 */

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TCLoadRank extends TCLoad {
    protected int roundId = 0;
    private boolean FULL_LOAD = false;
    private boolean TEAMS_RANK_FULL_LOAD = false;

    private static Logger log = Logger.getLogger(TCLoadRank.class);
    protected static final int OVERALL_RATING_RANK_TYPE_ID = 1;
    protected static final int ACTIVE_RATING_RANK_TYPE_ID = 2;

    private static final int TC_RATING_TYPE_ID = 1;
    private static final int TC_HS_RATING_TYPE_ID = 2;
/*
    private static final int SRM_ROUNDTYPE = 1;
    private static final int TOURNAMENT_ROUND_TYPE = 2;
    private static final int AVERAGE_RATING_RANK_TYPE_ID = 1;
*/

    /**
     * Constructor. Set our usage message here.
     */
    public TCLoadRank() {
        log.debug("TCLoadRank constructor called...");
        USAGE_MESSAGE = new String(
                "TCLoadRank parameters - defaults in ():\n" +
                        "  -roundid number       : Round ID to load\n" +
                        "  [-fullload boolean] : true-clean rank load, false-selective  (false)\n" +
                        "  [-teamsrankfullload boolean] : true-does a full load for teams rank (false)\n");
    }


    /**
     * This method is passed any parameters passed to this load
     */
    public boolean setParameters(Hashtable params) {
        log.debug("setParameters called...");
        try {
            Boolean tmpBool;
            roundId = retrieveIntParam("roundid", params, false, true).intValue();

            tmpBool = retrieveBooleanParam("fullload", params, true);
            if (tmpBool != null) {
                FULL_LOAD = tmpBool.booleanValue();
                log.info("New fullload flag is " + FULL_LOAD);
            }

            tmpBool = retrieveBooleanParam("teamsrankfullload", params, true);
            if (tmpBool != null) {
                TEAMS_RANK_FULL_LOAD = tmpBool.booleanValue();
                log.info("New teamsrankfullload flag is " + TEAMS_RANK_FULL_LOAD);
            }

        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            return false;
        }

        return true;
    }

    /**
     * This method performs the load for the round information tables
     */
    public void performLoad() throws Exception {
        log.debug("performLoad called...");
        try {

            long start = System.currentTimeMillis();

            if (TEAMS_RANK_FULL_LOAD) {
                teamsRankFullLoad();
                log.info("SUCCESS: teams rank fully loaded.");

                return;
            }

            // determine if the round is regular or HS.
            int algoType = getRoundType(roundId);
            log.info("Round type=" + algoType);

            List l = getRatingsForRound(algoType);
            log.info("got " + l.size() + " records in " + (System.currentTimeMillis() - start) + " milliseconds");
            loadRatingRank(OVERALL_RATING_RANK_TYPE_ID, algoType, l);

            loadRatingRankHistory(OVERALL_RATING_RANK_TYPE_ID, algoType, l);
            loadRatingRankHistory(ACTIVE_RATING_RANK_TYPE_ID, algoType, l);

            loadRatingRank(ACTIVE_RATING_RANK_TYPE_ID, algoType, l);


            loadCountryRatingRank(OVERALL_RATING_RANK_TYPE_ID, algoType, l);
            loadStateRatingRank(OVERALL_RATING_RANK_TYPE_ID, algoType, l);

            // school rating just for regular competitions, not for HS
            if (algoType == TC_RATING_TYPE_ID) {
                loadSchoolRatingRank(OVERALL_RATING_RANK_TYPE_ID, algoType, l);
            }

            loadCountryRatingRank(ACTIVE_RATING_RANK_TYPE_ID, algoType, l);
            loadStateRatingRank(ACTIVE_RATING_RANK_TYPE_ID, algoType, l);

            List countryRank = calculateCountryRank(l);
            loadCountryRank(algoType, countryRank);
            loadCountryRankHistory(algoType, countryRank);

            // school rating just for regular competitions, not for HS
            if (algoType == TC_RATING_TYPE_ID) {
                loadSchoolRatingRank(ACTIVE_RATING_RANK_TYPE_ID, algoType, l);
            }
//            loadAgeGroupAvgRatingRank();


            int seasonId = getSeasonId(roundId);

            if (seasonId >= 0) {
                List ratings = getSeasonRatingsForRound(seasonId);

                Collections.sort(ratings);

                loadSeasonRatingRank(seasonId, ratings);
                loadSeasonRatingRankHistory(seasonId, ratings);

                List teamPoints = getTeamPoints(seasonId);
                Collections.sort(teamPoints);

                loadSeasonTeamRank(seasonId, teamPoints);
                loadSeasonTeamRankHistory(seasonId, teamPoints);

                List seasonCountryRank = calculateCountryRank(ratings);

                loadSeasonCountryRank(seasonId, seasonCountryRank);
                loadSeasonCountryRankHistory(seasonId, seasonCountryRank);

            }


            log.info("SUCCESS: Rank load ran successfully for round " + roundId + ".");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }

    private void teamsRankFullLoad() throws Exception {
        log.debug("teamsRankFullLoad called...");

        PreparedStatement psSel = null;
        ResultSet rs = null;

        try {

            StringBuffer query = new StringBuffer(100);
            query.append(" select r.round_id, c.season_id ");
            query.append(" from round r, contest c ");
            query.append(" where r.contest_id = c.contest_id ");
            query.append(" and r.round_type_id in (17,18) ");
            query.append(" and not c.season_id is null ");
            query.append(" order by c.start_date ");

            psSel = prepareStatement(query.toString(), TARGET_DB);
            rs = psSel.executeQuery();

            Set seasons = new HashSet();
            while (rs.next()) {
                roundId = rs.getInt("round_id");
                int sId = rs.getInt("season_id");
                log.info("Loading team rank history for round " + roundId);
                seasons.add(new Integer(sId));
                List tp = getTeamPoints(sId, roundId);
                Collections.sort(tp);
                loadSeasonTeamRankHistory(sId, tp);
            }

            for (Iterator it = seasons.iterator(); it.hasNext();) {
                int sId = ((Integer) it.next()).intValue();
                log.info("Loading season team rank for season " + sId);
                List tp = getTeamPoints(sId);
                Collections.sort(tp);
                loadSeasonTeamRank(sId, tp);
            }

        } finally {
            close(rs);
            close(psSel);
        }
    }

    /**
     * Loads the coder_rank table with information about
     * overall rating rank.
     */
    protected void loadRatingRank(int rankType, int ratingType, List list) throws Exception {
        log.debug("loadRatingRank called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        int count = 0;
        int coderCount = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM coder_rank");
            query.append(" WHERE coder_rank_type_id = " + rankType);
            query.append(" AND algo_rating_type_id = " + ratingType);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO coder_rank (coder_id, percentile, rank, coder_rank_type_id, algo_rating_type_id)");
            query.append(" VALUES (?, ?, ?, " + rankType + ", " + ratingType + ")");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            /* coder_rank table should be kept "up-to-date" so get the most recent stuff
             * from the rating table
             */
            //ratings = getCurrentCoderRatings(rankType == ACTIVE_RATING_RANK_TYPE_ID);

            ArrayList ratings = new ArrayList(list.size());
            CoderRating cr = null;
            for (int i = 0; i < list.size(); i++) {
                cr = (CoderRating) list.get(i);
                if ((rankType == ACTIVE_RATING_RANK_TYPE_ID && cr.isActive()) ||
                        rankType != ACTIVE_RATING_RANK_TYPE_ID) {
                    ratings.add(cr);
                }
            }
            Collections.sort(ratings);
            coderCount = ratings.size();

            // delete all the records for the overall rating rank type
            psDel.executeUpdate();

            int i = 0;
            int rating = 0;
            int rank = 0;
            int size = ratings.size();
            int tempRating = 0;
            long tempCoderId = 0;
            for (int j = 0; j < size; j++) {
                i++;
                tempRating = ((CoderRating) ratings.get(j)).getRating();
                tempCoderId = ((CoderRating) ratings.get(j)).getCoderId();
                if (tempRating != rating) {
                    rating = tempRating;
                    rank = i;
                }
                psIns.setLong(1, tempCoderId);
                psIns.setFloat(2, (float) 100 * ((float) (coderCount - rank) / coderCount));
                psIns.setInt(3, rank);
                count += psIns.executeUpdate();
                printLoadProgress(count, "overall rating rank");
            }
            log.info("Records loaded for overall rating rank load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'coder_rank' table failed for overall rating rank.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }

    }


    /**
     * Load table country_rank from a list of CountryRank objects passed in list parameter.
     */
    private void loadCountryRank(int ratingType, List list) throws Exception {
        log.debug("loadCountryRank called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;
        int count = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM country_rank");
            query.append(" WHERE algo_rating_type_id = " + ratingType);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO country_rank (country_code, member_count, rating, rank, percentile, algo_rating_type_id)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the previous records
            psDel.executeUpdate();

            int size = list.size();
            for (int j = 0; j < size; j++) {
                CountryRank cr = (CountryRank) list.get(j);

                psIns.clearParameters();
                psIns.setString(1, cr.getCountryCode());
                psIns.setInt(2, cr.getMemberCount());
                psIns.setDouble(3, cr.getRating());
                psIns.setInt(4, cr.getRank());
                psIns.setDouble(5, cr.getPercentile());
                psIns.setInt(6, ratingType);
                count += psIns.executeUpdate();

                printLoadProgress(count, "country rank");
            }
            log.info("Records loaded for country rank load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'country_rank' table failed for overall rating rank.\n" +
                    sqle.getMessage());
        } finally {
            close(psIns);
            close(psDel);
        }

    }

    /**
     * Load table country_rank_history from a list of CountryRank objects passed in list parameter.
     */
    private void loadCountryRankHistory(int ratingType, List list) throws Exception {
        log.debug("loadCountryRankHistory called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;
        int count = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM country_rank_history");
            query.append(" WHERE algo_rating_type_id = " + ratingType);
            query.append(" AND round_id = " + roundId);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO country_rank_history (country_code, member_count, rating, rank, percentile, algo_rating_type_id, round_id)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the previous records
            psDel.executeUpdate();

            int size = list.size();
            for (int j = 0; j < size; j++) {
                CountryRank cr = (CountryRank) list.get(j);
                psIns.clearParameters();
                psIns.setString(1, cr.getCountryCode());
                psIns.setInt(2, cr.getMemberCount());
                psIns.setDouble(3, cr.getRating());
                psIns.setInt(4, cr.getRank());
                psIns.setDouble(5, cr.getPercentile());
                psIns.setInt(6, ratingType);
                psIns.setDouble(7, roundId);
                count += psIns.executeUpdate();

                printLoadProgress(count, "country rank history");
            }
            log.info("Records loaded for country rank history load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'country_rank_history' table failed for overall rating rank.\n" +
                    sqle.getMessage());
        } finally {
            close(psIns);
            close(psDel);
        }

    }

    /**
     * Load table season_country_rank from a list of CountryRank objects passed in list parameter.
     */
    private void loadSeasonCountryRank(int seasonId, List list) throws Exception {
        log.debug("loadSeasonCountryRank called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;
        int count = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM season_country_rank");
            query.append(" WHERE season_id = " + seasonId);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO season_country_rank (season_id, country_code, member_count, rating, rank, percentile)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the previous records
            psDel.executeUpdate();

            int size = list.size();
            for (int j = 0; j < size; j++) {
                CountryRank cr = (CountryRank) list.get(j);

                psIns.clearParameters();
                psIns.setInt(1, seasonId);
                psIns.setString(2, cr.getCountryCode());
                psIns.setInt(3, cr.getMemberCount());
                psIns.setDouble(4, cr.getRating());
                psIns.setInt(5, cr.getRank());
                psIns.setDouble(6, cr.getPercentile());
                count += psIns.executeUpdate();

                printLoadProgress(count, "season_country rank");
            }
            log.info("Records loaded for season country rank load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'season_country_rank' table failed for overall rating rank.\n" +
                    sqle.getMessage());
        } finally {
            close(psIns);
            close(psDel);
        }

    }

    /**
     * Load table season_country_rank_history from a list of CountryRank objects passed in list parameter.
     */
    private void loadSeasonCountryRankHistory(int seasonId, List list) throws Exception {
        log.debug("loadSeasonCountryRankHistory called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psIns = null;
        int count = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM season_country_rank_history");
            query.append(" WHERE season_id = " + seasonId);
            query.append(" AND round_id = " + roundId);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO season_country_rank_history (season_id, country_code, member_count, rating, rank, percentile, round_id)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the previous records
            psDel.executeUpdate();

            int size = list.size();
            for (int j = 0; j < size; j++) {
                CountryRank cr = (CountryRank) list.get(j);

                psIns.clearParameters();
                psIns.setInt(1, seasonId);
                psIns.setString(2, cr.getCountryCode());
                psIns.setInt(3, cr.getMemberCount());
                psIns.setDouble(4, cr.getRating());
                psIns.setInt(5, cr.getRank());
                psIns.setDouble(6, cr.getPercentile());
                psIns.setInt(7, roundId);
                count += psIns.executeUpdate();

                printLoadProgress(count, "season country rank history");
            }
            log.info("Records loaded for season country rank history load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'season_country_rank_history' table failed for overall rating rank.\n" +
                    sqle.getMessage());
        } finally {
            close(psIns);
            close(psDel);
        }

    }

    /**
     * Loads the season_rank table with information about the ranks in the season
     */
    private void loadSeasonRatingRank(int seasonId, List ratings) throws Exception {
        log.debug("loadSeasonRatingRank called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        int count = 0;
        int coderCount = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM season_rank");
            query.append(" WHERE season_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);
            psDel.setInt(1, seasonId);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO season_rank (coder_id, season_id, rank, percentile)");
            query.append(" VALUES (?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            coderCount = ratings.size();

            // delete all the records for the overall rating rank type
            psDel.executeUpdate();

            int i = 0;
            int rating = 0;
            int rank = 0;
            int size = ratings.size();
            int tempRating = 0;
            long tempCoderId = 0;
            for (int j = 0; j < size; j++) {
                i++;
                tempRating = ((CoderRating) ratings.get(j)).getRating();
                tempCoderId = ((CoderRating) ratings.get(j)).getCoderId();
                if (tempRating != rating) {
                    rating = tempRating;
                    rank = i;
                }
                psIns.setLong(1, tempCoderId);
                psIns.setInt(2, seasonId);
                psIns.setInt(3, rank);
                psIns.setFloat(4, (float) 100 * ((float) (coderCount - rank) / coderCount));
                count += psIns.executeUpdate();
                printLoadProgress(count, "season rating rank");
            }
            log.info("Records loaded for season rating rank load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'season_rank' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }

    }

    /**
     * Loads the season_team_rank table with information about the team ranks in the season
     */
    private void loadSeasonTeamRank(int seasonId, List teamPoints) throws Exception {
        log.debug("loadSeasonTeamRank called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        int count = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM season_team_rank");
            query.append(" WHERE season_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);
            psDel.setInt(1, seasonId);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO season_team_rank (team_id, season_id, rank, percentile, team_rank_type_id, team_points)");
            query.append(" VALUES (?, ?, ?, ?, 1, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the records for the overall rating rank type
            psDel.executeUpdate();

            int i = 0;
            double points = -1;
            int rank = 0;
            int size = teamPoints.size();
            double tempPoints = 0;
            long tempTeamId = 0;
            for (int j = 0; j < size; j++) {
                i++;
                tempPoints = ((TeamPoints) teamPoints.get(j)).getPoints();
                tempTeamId = ((TeamPoints) teamPoints.get(j)).getTeamId();
                if (tempPoints != points) {
                    points = tempPoints;
                    rank = i;
                }
                psIns.setLong(1, tempTeamId);
                psIns.setInt(2, seasonId);
                psIns.setInt(3, rank);
                psIns.setFloat(4, (float) 100 * ((float) (size - rank) / size));
                psIns.setInt(5, (int) tempPoints);
                
                count += psIns.executeUpdate();
                printLoadProgress(count, "season team rank");
            }
            log.info("Records loaded for season team rank load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'season_team_rank' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }
    }

    /**
     * Loads the season_team_rank_history table with information about the team ranks in the season
     */
    private void loadSeasonTeamRankHistory(int seasonId, List teamPoints) throws Exception {
        log.debug("loadSeasonTeamRankHistory called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        int count = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM season_team_rank_history");
            query.append(" WHERE season_id = ?");
            query.append(" AND round_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);
            psDel.setInt(1, seasonId);
            psDel.setInt(2, roundId);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO season_team_rank_history (team_id, season_id, rank, percentile, round_id, team_rank_type_id, team_points)");
            query.append(" VALUES (?, ?, ?, ?, ?, 1, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the records for the overall rating rank type
            psDel.executeUpdate();

            int i = 0;
            double points = -1;
            int rank = 0;
            int size = teamPoints.size();
            double tempPoints = 0;
            long tempTeamId = 0;
            for (int j = 0; j < size; j++) {
                i++;
                tempPoints = ((TeamPoints) teamPoints.get(j)).getPoints();
                tempTeamId = ((TeamPoints) teamPoints.get(j)).getTeamId();
                if (tempPoints != points) {
                    points = tempPoints;
                    rank = i;
                }
                psIns.setLong(1, tempTeamId);
                psIns.setInt(2, seasonId);
                psIns.setInt(3, rank);
                psIns.setFloat(4, (float) 100 * ((float) (size - rank) / size));
                psIns.setInt(5, roundId);
                psIns.setInt(6, (int) tempPoints);
                count += psIns.executeUpdate();
                printLoadProgress(count, "season team rank history");
            }
            log.info("Records loaded for season team rank history load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'season_team_rank_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }

    }

    /**
     * Loads the season_rank table with information about the ranks in the season
     */
    private void loadSeasonRatingRankHistory(int seasonId, List ratings) throws Exception {
        log.debug("loadSeasonRatingRankHistory called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        int count = 0;
        int coderCount = 0;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM season_rank_history");
            query.append(" WHERE season_id = ?");
            query.append(" AND round_id = ?");
            psDel = prepareStatement(query.toString(), TARGET_DB);
            psDel.setInt(1, seasonId);
            psDel.setInt(2, roundId);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO season_rank_history (coder_id, season_id, rank, percentile, round_id)");
            query.append(" VALUES (?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            coderCount = ratings.size();

            // delete all the records for the overall rating rank type
            psDel.executeUpdate();

            int i = 0;
            int rating = 0;
            int rank = 0;
            int size = ratings.size();
            int tempRating = 0;
            long tempCoderId = 0;
            for (int j = 0; j < size; j++) {
                i++;
                tempRating = ((CoderRating) ratings.get(j)).getRating();
                tempCoderId = ((CoderRating) ratings.get(j)).getCoderId();
                if (tempRating != rating) {
                    rating = tempRating;
                    rank = i;
                }
                psIns.setLong(1, tempCoderId);
                psIns.setInt(2, seasonId);
                psIns.setInt(3, rank);
                psIns.setFloat(4, (float) 100 * ((float) (coderCount - rank) / coderCount));
                psIns.setInt(5, roundId);
                count += psIns.executeUpdate();
                printLoadProgress(count, "season rating rank history");
            }
            log.info("Records loaded for season rating rank history load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'season_rank_history' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }

    }


    protected void loadRatingRankHistory(int rankType, int ratingType, List list) throws Exception {
        log.debug("loadRatingRankHistory called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        int count = 0;
        int coderCount = 0;
        List ratings = null;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM coder_rank_history");
            query.append(" WHERE coder_rank_type_id = " + rankType);
            query.append(" AND round_id = " + roundId);
            query.append(" AND algo_rating_type_id = " + ratingType);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO coder_rank_history (coder_id, round_id, percentile, rank, coder_rank_type_id, algo_rating_type_id)");
            query.append(" VALUES (?, ?, ?, ?, " + rankType + "," + ratingType + ")");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            if (rankType == ACTIVE_RATING_RANK_TYPE_ID) {
                ratings = new ArrayList();
                for (Iterator i = list.iterator(); i.hasNext();) {
                    CoderRating rating = (CoderRating) i.next();
                    if (rating.active) {
                        ratings.add(rating);
                    }
                }
            } else {
                ratings = list;
            }

            coderCount = ratings.size();
            Collections.sort(ratings);

            // delete all the recordsfor the rating rank type
            psDel.executeUpdate();

            int i = 0;
            int rating = 0;
            int rank = 0;
            int size = ratings.size();
            int tempRating = 0;
            long tempCoderId = 0;
            for (int j = 0; j < size; j++) {
                i++;
                tempRating = ((CoderRating) ratings.get(j)).getRating();
                tempCoderId = ((CoderRating) ratings.get(j)).getCoderId();
                if (tempRating != rating) {
                    rating = tempRating;
                    rank = i;
                }
                psIns.setLong(1, tempCoderId);
                psIns.setInt(2, roundId);
                psIns.setFloat(3, (float) 100 * ((float) (coderCount - rank) / coderCount));
                psIns.setInt(4, rank);
                count += psIns.executeUpdate();
                printLoadProgress(count, "rating rank history");
            }
            log.info("Records loaded for rating rank history load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'coder_rank_history' table failed for rating rank.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }

    }


    /**
     * Loads the country_coder_rank table with information about
     * rating rank within a country.
     */
    protected void loadCountryRatingRank(int rankType, int ratingType, List list) throws Exception {
        log.debug("loadCountryRatingRank called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        //PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        //ResultSet rs = null;
        int count = 0;
        int coderCount = 0;
        List ratings = null;
        CoderRating curr = null;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM country_coder_rank");
            query.append(" WHERE coder_rank_type_id = " + rankType);
            query.append(" AND algo_rating_type_id = " + ratingType);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO country_coder_rank (coder_id, percentile, rank, rank_no_tie, ");
            query.append("       country_code, coder_rank_type_id, algo_rating_type_id)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the records from the country ranking table
            psDel.executeUpdate();


            HashMap countries = new HashMap();
            String tempCode = null;
            List tempList = null;
            CoderRating temp = null;

            for (int i = 0; i < list.size(); i++) {
                temp = (CoderRating) list.get(i);
                if ((rankType == ACTIVE_RATING_RANK_TYPE_ID && temp.isActive()) ||
                        rankType != ACTIVE_RATING_RANK_TYPE_ID) {
                    tempCode = temp.getCountryCode();
                    if (countries.containsKey(tempCode)) {
                        tempList = (List) countries.get(tempCode);
                    } else {
                        tempList = new ArrayList(100);
                    }
                    tempList.add(list.get(i));
                    countries.put(tempCode, tempList);
                    tempList = null;
                }
            }


            for (Iterator it = countries.entrySet().iterator(); it.hasNext();) {
                ratings = (List) ((Map.Entry) it.next()).getValue();
                Collections.sort(ratings);
                coderCount = ratings.size();

                int i = 0;
                int rating = 0;
                int rank = 0;
                int size = ratings.size();
                int tempRating = 0;
                long tempCoderId = 0;
                for (int j = 0; j < size; j++) {
                    i++;
                    tempRating = ((CoderRating) ratings.get(j)).getRating();
                    tempCoderId = ((CoderRating) ratings.get(j)).getCoderId();
                    curr = (CoderRating) ratings.get(j);
                    if (tempRating != rating) {
                        rating = tempRating;
                        rank = i;
                    }
                    psIns.setLong(1, tempCoderId);
                    psIns.setFloat(2, (float) 100 * ((float) (coderCount - rank) / coderCount));
                    psIns.setInt(3, rank);
                    psIns.setInt(4, j + 1);
                    psIns.setString(5, curr.getCountryCode());
                    psIns.setInt(6, rankType);
                    psIns.setInt(7, ratingType);
                    count += psIns.executeUpdate();
                    printLoadProgress(count, "country coder rating rank");
                }
            }
            log.info("Records loaded for country coder rating rank load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'country_coder_rank' table failed for " + curr.toString() + ".\n" +
                    sqle.getMessage());
        } finally {
            close(psIns);
            close(psDel);
        }

    }


    /**
     * Loads the state_coder_rank table with information about
     * rating rank within a state.
     */
    protected void loadStateRatingRank(int rankType, int ratingType, List list) throws Exception {
        log.debug("loadStateRatingRank called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        //PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        //ResultSet rs = null;
        int count = 0;
        int coderCount = 0;
        List ratings = null;
        CoderRating curr = null;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM state_coder_rank");
            query.append(" WHERE coder_rank_type_id = " + rankType);
            query.append(" AND algo_rating_type_id = " + ratingType);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO state_coder_rank (coder_id, percentile, rank, rank_no_tie, state_code, coder_rank_type_id, algo_rating_type_id)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the records from the country ranking table
            psDel.executeUpdate();


            HashMap states = new HashMap();
            String tempCode = null;
            List tempList = null;
            CoderRating temp = null;

            for (int i = 0; i < list.size(); i++) {
                temp = (CoderRating) list.get(i);
                if ((rankType == ACTIVE_RATING_RANK_TYPE_ID && temp.isActive()) ||
                        rankType != ACTIVE_RATING_RANK_TYPE_ID) {
                    tempCode = temp.getStateCode();
                    if (tempCode != null && !tempCode.trim().equals("")) {
                        if (states.containsKey(tempCode)) {
                            tempList = (List) states.get(tempCode);
                        } else {
                            tempList = new ArrayList(100);
                        }
                        tempList.add(list.get(i));
                        states.put(tempCode, tempList);
                        tempList = null;
                    }
                }
            }


            for (Iterator it = states.entrySet().iterator(); it.hasNext();) {
                ratings = (List) ((Map.Entry) it.next()).getValue();
                Collections.sort(ratings);
                coderCount = ratings.size();

                int i = 0;
                int rating = 0;
                int rank = 0;
                int size = ratings.size();
                int tempRating = 0;
                long tempCoderId = 0;
                for (int j = 0; j < size; j++) {
                    i++;
                    tempRating = ((CoderRating) ratings.get(j)).getRating();
                    tempCoderId = ((CoderRating) ratings.get(j)).getCoderId();
                    curr = ((CoderRating) ratings.get(j));
                    if (tempRating != rating) {
                        rating = tempRating;
                        rank = i;
                    }
                    psIns.setLong(1, tempCoderId);
                    psIns.setFloat(2, (float) 100 * ((float) (coderCount - rank) / coderCount));
                    psIns.setInt(3, rank);
                    psIns.setInt(4, j + 1);
                    psIns.setString(5, curr.getStateCode());
                    psIns.setInt(6, rankType);
                    psIns.setInt(7, ratingType);
                    count += psIns.executeUpdate();
                    printLoadProgress(count, "state coder rating rank");
                }
            }
            log.info("Records loaded for state coder rating rank load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'state_coder_rank' table failed for state coder rating rank for " + curr.toString() + ".\n" +
                    sqle.getMessage());
        } finally {
/*
            close(rs);
            close(psSel);
*/
            close(psIns);
            close(psDel);
        }

    }


    /**
     * Loads the school_coder_rank table with information about
     * rating rank within a school.
     */
    protected void loadSchoolRatingRank(int rankType, int ratingType, List list) throws Exception {
        log.debug("loadSchoolRatingRank called...");
        StringBuffer query = null;
        PreparedStatement psDel = null;
        //PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        //ResultSet rs = null;
        int count = 0;
        int coderCount = 0;
        List ratings = null;
        CoderRating curr = null;

        try {

            query = new StringBuffer(100);
            query.append(" DELETE");
            query.append(" FROM school_coder_rank");
            query.append(" WHERE coder_rank_type_id = " + rankType);
            query.append(" AND algo_rating_type_id = " + ratingType);
            psDel = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT");
            query.append(" INTO school_coder_rank (coder_id, percentile, rank, rank_no_tie, school_id, coder_rank_type_id, algo_rating_type_id)");
            query.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");
            psIns = prepareStatement(query.toString(), TARGET_DB);

            // delete all the records from the country ranking table
            psDel.executeUpdate();


            HashMap schools = new HashMap();
            Long tempId = null;
            List tempList = null;
            CoderRating temp = null;

            for (int i = 0; i < list.size(); i++) {
                temp = (CoderRating) list.get(i);
                if ((rankType == ACTIVE_RATING_RANK_TYPE_ID && temp.isActive()) ||
                        rankType != ACTIVE_RATING_RANK_TYPE_ID) {
                    if (temp.getSchoolId() > 0) {
                        tempId = new Long(temp.getSchoolId());
                        if (schools.containsKey(tempId)) {
                            tempList = (List) schools.get(tempId);
                        } else {
                            tempList = new ArrayList(10);
                        }
                        tempList.add(list.get(i));
                        schools.put(tempId, tempList);
                        tempList = null;
                    }
                }
            }

            for (Iterator it = schools.entrySet().iterator(); it.hasNext();) {
                ratings = (List) ((Map.Entry) it.next()).getValue();
                Collections.sort(ratings);
                coderCount = ratings.size();

                int i = 0;
                int rating = 0;
                int rank = 0;
                int size = ratings.size();
                int tempRating = 0;
                long tempCoderId = 0;
                for (int j = 0; j < size; j++) {
                    i++;
                    tempRating = ((CoderRating) ratings.get(j)).getRating();
                    tempCoderId = ((CoderRating) ratings.get(j)).getCoderId();
                    curr = (CoderRating) ratings.get(j);
                    if (tempRating != rating) {
                        rating = tempRating;
                        rank = i;
                    }
                    psIns.setLong(1, tempCoderId);
                    psIns.setFloat(2, (float) 100 * ((float) (coderCount - rank) / coderCount));
                    psIns.setInt(3, rank);
                    psIns.setInt(4, j + 1);
                    psIns.setLong(5, curr.getSchoolId());
                    psIns.setInt(6, rankType);
                    psIns.setInt(7, ratingType);
                    count += psIns.executeUpdate();
                    printLoadProgress(count, "school coder rating rank");
                }
            }
            log.info("Records loaded for school coder rating rank load: " + count);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'school_coder_rank' table failed for " + curr.toString() + ".\n" +
                    sqle.getMessage());
        } finally {
/*
            close(rs);
            close(psSel);
*/
            close(psIns);
            close(psDel);
        }

    }


    protected List<CoderRating> getRatingsForRound(int algoType) throws Exception {
        StringBuffer query = null;
        PreparedStatement psSel = null;
        ResultSet rs = null;
        List<CoderRating> ret = null;

        try {

            query = new StringBuffer(1000);
            query.append("select r.coder_id ");
            query.append(" , r.rating ");
            query.append(" , cs.school_id ");
            query.append(" , c.coder_type_id ");
            query.append(" , c.comp_country_code as country_code ");
            query.append(" , c.state_code ");
            
            
            if (algoType == MARATHON_RATING_TYPE_ID) {
                query.append(" , case when exists (select '1'");
                query.append("                       from long_comp_result lcr ");
                query.append("                         , round r1 ");
                query.append("                         , calendar cal ");
                query.append("                         , round_type_lu  rt ");
                query.append("                       where lcr.round_id = r1.round_id ");
                query.append("                         and lcr.attended = 'Y' ");
                query.append("                         and r1.round_type_id = rt.round_type_id ");
                query.append("                         and rt.algo_rating_type_id = " + MARATHON_RATING_TYPE_ID); 
                query.append("                         and lcr.rated_ind = 1  ");
                query.append("                         and r1.calendar_id = cal.calendar_id  ");
                query.append("                         and lcr.coder_id = r.coder_id ");
                query.append("                         and cal.calendar_id <= (select calendar_id from round where round_id = r.round_id)  ");
                query.append("                         and cal.date >= (select c2.date - interval(180) day(9) to day from round r2, calendar c2 ");
                query.append("                             where r2.calendar_id = c2.calendar_id and r2.round_id = r.round_id)) ");
                query.append("                 then 1 else 0 end as active ");
            } else {
                query.append(" , case when exists (select '1'");
                query.append("                       from room_result rr");
                query.append("                          , round r1");
                query.append("                          , calendar cal");
                query.append("                          , round_type_lu  rt");
                query.append("                      where rr.round_id = r1.round_id");
                query.append("                        and rr.attended = 'Y'");
                query.append("                        and r1.round_type_id = rt.round_type_id");
                query.append("                        and rt.algo_rating_type_id = r.algo_rating_type_id ");
                query.append("                        and rr.rated_flag = 1 ");
                query.append("                        and r1.calendar_id = cal.calendar_id");
                query.append("                        and rr.coder_id = r.coder_id");
                query.append("                        and cal.calendar_id <= (select calendar_id from round where round_id = r.round_id)");
                query.append("                        and cal.date >= (select c2.date - interval(180) day(9) to day from round r2, calendar c2");
                query.append("                                                  where r2.calendar_id = c2.calendar_id and r2.round_id = r.round_id))");
                query.append("                 then 1 else 0 end as active ");                
            }

                                       
                                       
            query.append("  from algo_rating_history r ");
            query.append(" , outer current_school cs ");
            query.append(" , coder c ");
            query.append(" where r.coder_id = cs.coder_id");
            query.append(" and r.coder_id = c.coder_id ");
            query.append(" and r.algo_rating_type_id = " + algoType);
            query.append(" and r.num_ratings > 0 ");
            query.append(" and c.status = 'A' ");
            query.append(" and r.round_id = ?");

            psSel = prepareStatement(query.toString(), TARGET_DB);
            psSel.setInt(1, roundId);

            rs = psSel.executeQuery();
            ret = new ArrayList<CoderRating>();
            while (rs.next()) {
                //pros
                if (rs.getInt("coder_type_id") == 2) {
                    ret.add(new CoderRating(rs.getLong("coder_id"), rs.getInt("rating"),
                            0, rs.getInt("active") == 1, rs.getString("country_code"),
                            rs.getString("state_code")));
                } else {
                    ret.add(new CoderRating(rs.getLong("coder_id"), rs.getInt("rating"),
                            rs.getInt("school_id"), rs.getInt("active") == 1, rs.getString("country_code"),
                            rs.getString("state_code")));
                }
            }


        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Get list of current ratings failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
        return ret;

    }


    private List getSeasonRatingsForRound(int seasonId) throws Exception {
        StringBuffer query = null;
        PreparedStatement psSel = null;
        ResultSet rs = null;
        List ret = null;

        try {

            query = new StringBuffer(200);
            query.append(" SELECT r.coder_id ");
            query.append(" ,r.rating ");
            query.append(" , c.comp_country_code as country_code ");
            query.append(" FROM season_algo_rating_history r");
            query.append(" ,coder c ");
            query.append("  WHERE r.coder_id = c.coder_id ");
            query.append(" AND r.round_id = ?");

            psSel = prepareStatement(query.toString(), TARGET_DB);

            psSel.setInt(1, roundId);
            rs = psSel.executeQuery();
            ret = new ArrayList();
            while (rs.next()) {
                ret.add(new CoderRating(rs.getLong("coder_id"), rs.getInt("rating"),
                        0, true, rs.getString("country_code"), "NA"));
            }
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Get list of current season ratings failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
        return ret;

    }

    private List getTeamPoints(int seasonId) throws Exception {
        return getTeamPoints(seasonId, -1);
    }

    private List getTeamPoints(int seasonId, long lastRoundId) throws Exception {
        StringBuffer query = null;
        PreparedStatement psSel = null;
        ResultSet rs = null;
        PreparedStatement psPoints = null;
        ResultSet rsPoints = null;
        List ret = null;

        try {
            java.sql.Date maxDate = null;
            if (lastRoundId >= 0) {
                query = new StringBuffer(200);
                query.append(" select c.start_date ");
                query.append(" from round r, contest c ");
                query.append(" where r.contest_id = c.contest_id ");
                query.append(" and r.round_id = ? ");
                psSel = prepareStatement(query.toString(), TARGET_DB);

                psSel.setLong(1, lastRoundId);
                rs = psSel.executeQuery();
                if (!rs.next()) {
                    throw new IllegalArgumentException("round " + lastRoundId + " not found (to be used as last round");
                }
                maxDate = rs.getDate(1);
            }

            query = new StringBuffer(200);
            query.append(" SELECT team_id");
            query.append(" FROM team_round tp, round r, contest c ");
            query.append(" WHERE tp.round_id = r.round_id ");
            query.append(" AND r.contest_id = c.contest_id ");
            query.append(" AND c.season_id = ?");
            if (maxDate != null) {
                query.append(" AND c.start_date <= ?");
            }
            query.append(" GROUP BY tp.team_id ");
            query.append(" HAVING count(team_points) >= 4 ");


            psSel = prepareStatement(query.toString(), TARGET_DB);

            psSel.setInt(1, seasonId);
            if (maxDate != null) {
                psSel.setDate(2, maxDate);
            }
            rs = psSel.executeQuery();

            query = new StringBuffer(200);
            query.append(" SELECT team_points ");
            query.append(" FROM team_round tp, round r, contest c ");
            query.append(" WHERE tp.round_id = r.round_id  ");
            query.append(" AND r.contest_id = c.contest_id  ");
            query.append(" AND c.season_id = ? ");
            query.append(" AND tp.team_id = ? ");
            query.append(" AND not team_points is null ");
            query.append(" ORDER BY team_points ");

            psPoints = prepareStatement(query.toString(), TARGET_DB);

            ret = new ArrayList();
            while (rs.next()) {
                int teamId = rs.getInt(1);
                psPoints.clearParameters();
                psPoints.setInt(1, seasonId);
                psPoints.setInt(2, teamId);
                rsPoints = psPoints.executeQuery();

                int points = 0;
                for (int i = 0; i < 4; i++) {
                    rsPoints.next();
                    points += rsPoints.getInt(1);
                }

                ret.add(new TeamPoints(teamId, points));
            }


        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Get list of current season ratings failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
            close(psPoints);
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
    private List calculateCountryRank(List list) {
        ArrayList ratings = new ArrayList(list.size());
        CoderRating cr = null;
        for (int i = 0; i < list.size(); i++) {
            cr = (CoderRating) list.get(i);
            if (cr.isActive()) {
                ratings.add(cr);
            }
        }
        Collections.sort(ratings);

        int size = ratings.size();
        Map countryRating = new HashMap();

        // Add all the coders to their country's rating
        for (int i = 0; i < size; i++) {
            cr = (CoderRating) ratings.get(i);
            String cc = cr.getCountryCode();

            if (cc == null || (cc.trim().length() == 0)) continue;

            CountryRank r = (CountryRank) countryRating.get(cc);
            if (r == null) {
                r = new CountryRank(cc);
                countryRating.put(cc, r);
            }
            r.addCoder(cr.getRating());
        }

        // copy to l just the countries with at least 10 coders
        ArrayList l = new ArrayList();

        for (Iterator it = countryRating.values().iterator(); it.hasNext();) {
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

    protected class CoderRating implements Comparable<CoderRating> {
        private long coderId = 0;
        private int rating = 0;
        private long schoolId = 0;
        private boolean active = false;
        private String countryCode = null;
        private String stateCode = null;

        CoderRating(long coderId, int rating, long schoolId, boolean active, String countryCode, String stateCode) {
            this.coderId = coderId;
            this.rating = rating;
            this.schoolId = schoolId;
            this.active = active;
            this.countryCode = countryCode;
            this.stateCode = stateCode;
        }

        public int compareTo(CoderRating other) {
            if (other.getRating() > rating)
                return 1;
            else if (other.getRating() < rating)
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

        long getSchoolId() {
            return schoolId;
        }

        void setSchoolId(long schoolId) {
            this.schoolId = schoolId;
        }

        boolean isActive() {
            return active;
        }

        void setActive(boolean active) {
            this.active = active;
        }

        String getStateCode() {
            return stateCode;
        }

        void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }

        String getCountryCode() {
            return countryCode;
        }

        void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String toString() {
            return new String(coderId + ":" + rating + ":" + schoolId + ":" + active + ":" + stateCode + ":" + countryCode);
        }

    }

    private class TeamPoints implements Comparable {
        private long teamId = 0;
        private double points = 0;


        public TeamPoints(long teamId, double points) {
            this.teamId = teamId;
            this.points = points;
        }

        public double getPoints() {
            return points;
        }

        public void setPoints(double points) {
            this.points = points;
        }

        public long getTeamId() {
            return teamId;
        }

        public void setTeamId(long teamId) {
            this.teamId = teamId;
        }

        public int compareTo(Object other) {
            if (((TeamPoints) other).getPoints() < getPoints())
                return 1;
            else if (((TeamPoints) other).getPoints() > getPoints())
                return -1;
            else
                return 0;
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

        public void addCoder(int coderRating) {
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

