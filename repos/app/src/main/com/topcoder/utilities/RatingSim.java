package com.topcoder.utilities;
import java.util.*;
import java.sql.*;
import java.text.DecimalFormat;

import com.topcoder.netCommon.contest.*;
import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.sql.InformixSimpleDataSource;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class RatingSim {
    
    public static final Logger log = Logger.getLogger(RatingSim.class);

    public static void main(String[] args) {
        RatingSim tmp = new RatingSim();
        Connection c = null;
        
        try {
            //c = DBMS.getConnection(DBMS.DW_DATASOURCE_NAME);
            Class.forName("com.informix.jdbc.IfxDriver");
            c = DriverManager.getConnection("jdbc:informix-sqli://192.168.14.52:2022/topcoder_dw:INFORMIXSERVER=datawarehouse_tcp;user=coder;password=teacup");
            //c.setAutoCommit(false);
            tmp.runRatings(c);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
/*            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
            }*/
            try {
                if (c != null) c.close();
            } catch (Exception e1) {
            }
        }
    }
    
    public void runRatings(Connection conn) {
        //get every round, in order
        PreparedStatement ps;
        ResultSet rs;
        
        String sqlStr = "select ct.name||' - '||r.name as contest_name, r.*,c.date, " +
                    "(select count(*) from room_result rr where rr.round_id = r.round_id and rr.rated_flag = 1 and rr.division_id = 1) as div1count, " +
                    "(select count(*) from room_result rr where rr.round_id = r.round_id and rr.rated_flag = 1 and rr.division_id = 2) as div2count " +
                    "from round r, calendar c, contest ct  " +
                    "where round_type_id in (1,2,10) " +
                    "and short_name <> '' " +
                    "and c.calendar_id = r.calendar_id " +
                    "and ct.contest_id = r.contest_id " +
                    "order by c.date, r.round_id";
        
        try {
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();
            int i = 0;
            while(rs.next()) {
                log.info("PROCESSING: " + rs.getString("contest_name"));
                boolean byDiv = true;
                if(rs.getInt("div2count") == 0)
                    byDiv = false;
                
                switch(rs.getInt("round_id")) {
                    case 2001:
                    case 2002:
                    case 3000:
                    case 3001:
                    case 3002:
                    case 3003:
                    case 2005:
                    case 2006:
                    case 2007:
                    case 2008:
                    case 2009:
                    case 3004:
                    case 3005:
                    case 3006:
                    case 3007:
                    case 3008:
                    case 3009:
                    case 3010:
                    case 3011:
                    case 3012:
                    case 3013:
                    case 3014:
                    case 3015:
                    case 3016:
                    case 3017:
                    case 3018:
                    case 3019:
                    case 3020:
                    case 3021:
                    case 3023:
                    case 3024:
                    case 3025:
                    case 3026:
                    case 3027:
                    case 3028:
                    case 4000:
                    case 4001:
                    case 50:
                    case 4002:
                    case 4003:
                    case 51:
                    case 52:
                    case 4005:
                    case 4007:
                    case 3999:
                    case 4008:
                    case 4009:
                    case 4010:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                    case 4011:
                    case 4012:
                    case 4013:
                    case 4014:
                    case 4015:
                    case 4016:
                    case 4017:
                    case 4018:
                    case 3998:
                    case 4020:
                    case 4021:
                    case 4022:
                    case 4023:
                    case 4024:
                    case 4025:
                    case 4026:
                    case 4027:
                    case 4028:
                    case 4029:
                    case 4030:
                    case 4031:
                    case 4032:
                    case 4033:
                    case 4045:
                    case 4050:
                    case 4055:
                    case 4060:
                        byDiv=false;
                        break;
                    default:
                        break;
                }
                
                runRatings(conn, rs.getInt("round_id"), byDiv);
                loadRatingHistory(conn, rs.getInt("round_id"));
                loadOverallRatingRankHistory(conn, rs.getInt("round_id"));
                i++;
                //if(i==2)
                  //  break;
            }
            rs.close();
            ps.close();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    private void loadRatingHistory(Connection conn, int roundId) {
        int count = 0;
        int retVal = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            // Get all the coders that participated in this round
            query = new StringBuffer(100);
            query.append(" select coder_id, rating, vol, num_ratings");
            query.append(" from rating");
            query.append(" where num_ratings > 0");

            psSel = conn.prepareStatement(query.toString());

            query = new StringBuffer(100);
            query.append("insert into rating_history (coder_id, round_id, rating, vol, num_ratings)");
            query.append("values (?,?,?,?,?)");
            psIns = conn.prepareStatement(query.toString());

            rs = psSel.executeQuery();

            while (rs.next()) {
                psIns.clearParameters();
                psIns.setLong(1, rs.getLong("coder_id"));
                psIns.setLong(2, roundId);
                psIns.setInt(3, rs.getInt("rating"));
                psIns.setInt(4, rs.getInt("vol"));
                psIns.setInt(5, rs.getInt("num_ratings"));

                retVal = psIns.executeUpdate();
                count = count + retVal;
                if (retVal != 1) {
                    throw new SQLException("TCLoadCoders: Insert for coderId " +
                            rs.getLong("coder_id") +
                            " modified " + retVal + " rows, not one.");
                }
            }

            log.info("Rating records updated = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
        } finally {
            close(rs);
            close(psSel);
        }
    }
    
    private void loadOverallRatingRankHistory(Connection c, int roundId) {
        log.debug("loadOverallRatingRankHistory called...");
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
            query.append( " INSERT");
            query.append(   " INTO coder_rank_history (coder_id, round_id, percentile, rank, coder_rank_type_id)");
            query.append( " VALUES (?, ?, ?, ?, " + 1 + ")");
            psIns = c.prepareStatement(query.toString());
         

            /* if it's the most recent round that we're loading
             * we can pull from the rating table, if not,
             * then we have to go and build up the ratings of
             * all the people who have competed up until the time
             * of the given round
             */
            ratings = getCoderRatingsForRound(c, roundId);
            
            coderCount = ratings.size();
            
            // delete all the records for the overall rating rank type

            int i = 0;
            int rating = 0;
            int rank = 0;
            int size = ratings.size();
            int tempRating = 0;
            int tempCoderId = 0;
            for (int j=0; j<size; j++) {
                i++;
                tempRating = ((CoderRating)ratings.get(j)).getRating();
                tempCoderId = ((CoderRating)ratings.get(j)).getCoderId();
                if (tempRating != rating) {
                    rating = tempRating;
                    rank = i;
                }
                psIns.setInt(1, tempCoderId);
                psIns.setInt(2, roundId);
                psIns.setFloat(3, (float)100*((float)(coderCount-rank)/coderCount));
                psIns.setInt(4, rank);
                count += psIns.executeUpdate();
            }
            log.info("Records loaded for overall rating rank history load: " + count);
                    
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
            close(psDel);
        }

    }
    
    private List getCoderRatingsForRound(Connection c, int roundId) {
        log.debug("getCoderRatingsForRound called...");
        PreparedStatement ps = null; 
        StringBuffer query = null;
        ResultSet rs = null;
        List ret = null;
        List rounds = null;
        long startTime = System.currentTimeMillis();

        try {
            
            query = new StringBuffer(100);
            query.append( " SELECT rr.coder_id");
            query.append(       "  ,rr.rating");
            query.append(   " FROM rating rr");
            query.append(        " ,coder c");
            query.append(  " WHERE ");
            query.append(    " rr.rating > 0 ");
            query.append(    " AND rr.coder_id = c.coder_id");
            query.append(    " AND c.status = 'A'");
            ps = c.prepareStatement(query.toString());
           
            HashMap tempHash = new HashMap();
            rs = ps.executeQuery();
            while (rs.next()) {
                tempHash.put(new Integer(rs.getInt("coder_id")), new Integer(rs.getInt("rating")));
            }

            ret = new ArrayList(tempHash.size());
            Iterator it = tempHash.entrySet().iterator();
            Map.Entry entry = null;
            CoderRating cr = null;
            while (it.hasNext()) {
                entry = (Map.Entry)it.next();
                int coderId = ((Integer)entry.getKey()).intValue();
                int rating = ((Integer)entry.getValue()).intValue();
                ret.add(new CoderRating(coderId, rating));
            }
                
            Collections.sort(ret);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
        } finally {
            close(rs);
            close(ps);
        }
        long endTime = System.currentTimeMillis();
        log.info("TIME IN METHOD: " + (endTime-startTime) + " milliseconds");
        return ret;


    }
    
    private class CoderRating implements Comparable {
        private int _coderId = 0;
        private int _rating = 0;
        CoderRating(int coderId, int rating) { 
            _coderId = coderId;
            _rating = rating;
        }
        public int compareTo(Object other) {
            if (((CoderRating)other).getRating()>_rating) return 1;
            else if (((CoderRating)other).getRating()<_rating) return -1;
            else return 0;
        }
        int getCoderId() {return _coderId;}
        int getRating() {return _rating;}
        void setCoderId(int coderId) {_coderId = coderId;}
        void setRating(int rating) {_rating = rating;}
        public String toString() { return new String(_coderId+":"+_rating); }
    }
    
    protected void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException sqle) {
            log.error("Error closing ResultSet.");
            sqle.printStackTrace();
        }
    }
    
    protected void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException sqle) {
            log.error("Error closing Statement.");
            sqle.printStackTrace();
        }
    }

    public void runRatings(Connection c, int roundId, boolean byDiv) throws Exception {
        try {
            if (byDiv) {
                doIt(c, roundId, ContestConstants.DIVISION_ONE, true);
                doIt(c, roundId, ContestConstants.DIVISION_TWO, true);
            } else {
                doIt(c, roundId, 0, false);
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    private class CoderData {
        public int rating;
        public int vol;
        public int timesplayed;
        public CoderData() {
            
        }
        public CoderData(int rating, int vol, int timesplayed) {
            this.rating = rating;
            this.vol = vol;
            this.timesplayed = timesplayed;
        }
    }
    
    private HashMap coders = new HashMap();
    
    private void updateCoder(String c, int rating, int vol, int timesplayed)  {
        coders.put(c, new CoderData(rating,vol,timesplayed));
    }
    
    private int getRating(String c) {
        if(coders.containsKey(c)) {
            return ((CoderData)coders.get(c)).rating;
        } else {
            return 0;
        }
    }
    
    private int getVol(String c) {
        if(coders.containsKey(c)) {
            return ((CoderData)coders.get(c)).vol;
        } else {
            return 0;
        }
    }
    
    private int getTimesPlayed(String c) {
        if(coders.containsKey(c)) {
            return ((CoderData)coders.get(c)).timesplayed;
        } else {
            return 0;
        }
    }

    private void doIt(Connection conn, int roundId, int divisionId, boolean byDiv) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs2 = null;
        StringBuffer sqlStr = new StringBuffer(400);
        int room,newrating,newvol,coder,retVal,newratingnovol;
        Vector names,ratings,volatilities,timesplayed,scores,endratings,endvols,endratingsnovol,ratingsplusprov,namesplusprov,
                volatilitiesplusprov,timesplayedplusprov,scoresplusprov,stringnames,stringnamesplusprov;
        ArrayList results = new ArrayList();
        ArrayList resultsplusprov = new ArrayList();

        try {
            Timestamp currentTime = ServerContestConstants.getCurrentTimestamp(conn);

            names = new Vector();
            namesplusprov = new Vector();
            ratings = new Vector();
            ratingsplusprov = new Vector();
            volatilities = new Vector();
            volatilitiesplusprov = new Vector();
            timesplayed = new Vector();
            timesplayedplusprov = new Vector();
            scores = new Vector();
            scoresplusprov = new Vector();
            stringnames = new Vector();
            stringnamesplusprov = new Vector();

            //get a bunch of info of coders in the room
            if (byDiv) {
                sqlStr.replace(0, sqlStr.length(), "SELECT rr.coder_id,rr.final_points,rr.attended,c.handle ");
                sqlStr.append("FROM coder c,room_result rr, room r ");
                sqlStr.append("WHERE rr.round_id = ? and c.coder_id=rr.coder_id and r.room_id = rr.room_id and r.division_id = ? and rr.rated_flag = 1");

                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundId);
                ps.setInt(2, divisionId);
                rs2 = ps.executeQuery();
            } else {
                sqlStr.replace(0, sqlStr.length(), "SELECT rr.coder_id,rr.final_points,rr.attended,c.handle ");
                sqlStr.append("FROM coder c,room_result rr, room r ");
                sqlStr.append("WHERE rr.round_id = ? and c.coder_id=rr.coder_id and r.room_id = rr.room_id and rr.rated_flag = 1 and r.division_id <> " + ContestConstants.DIVISION_ADMIN);

                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, roundId);
                rs2 = ps.executeQuery();
            }

            //add each coder and his info to the vectors.
            String handle = "";

            log.info("PROCESSING CODERS");
            
            while (rs2.next()) {
                //if (rs2.getString(6).equals("Y")) {
                namesplusprov.add("" + rs2.getInt("coder_id"));
                timesplayedplusprov.add(new Integer(getTimesPlayed("" + rs2.getInt("coder_id")))); 
                scoresplusprov.add(new Double(rs2.getDouble("final_points")));
                handle = rs2.getString("handle");
                if (handle == null) handle = "";
                stringnamesplusprov.add(handle);

                int rating = getRating("" + rs2.getInt("coder_id")); 
                
                if (rating > 0) {
                    volatilitiesplusprov.add(new Double(getVol("" + rs2.getInt("coder_id")))); 
                    ratingsplusprov.add(new Double(rating)); 
                    names.add("" + rs2.getInt("coder_id"));
                    ratings.add(new Double(rating));
                    volatilities.add(new Double(getVol("" + rs2.getInt("coder_id"))));
                    timesplayed.add(new Integer(getTimesPlayed("" + rs2.getInt("coder_id"))));
                    scores.add(new Double(rs2.getDouble("final_points")));
                    stringnames.add(handle);
                } else {
                    volatilitiesplusprov.add(new Double(515));
                    ratingsplusprov.add(new Double(1200.0));
                }

                //}
            }
            rs2.close();
            rs2 = null;
            ps.close();
            ps = null;

            //run qubits rating algorithm on them
            log.info("Ratings for non-provisional: round " + roundId + ", div " + divisionId + ":");

            results = rateEvent(names, ratings, volatilities, timesplayed, scores, stringnames, false);

            log.info("Ratings for provisional: round " + roundId + ", div " + divisionId + ":");

            resultsplusprov = rateEvent(namesplusprov, ratingsplusprov, volatilitiesplusprov, timesplayedplusprov, scoresplusprov, stringnamesplusprov, true);

            endratings = (Vector) results.get(2);
            endvols = (Vector) results.get(1);
            endratingsnovol = (Vector) results.get(0);

            while (endratings.size() > 0) {
                newrating = (int)Math.round(((Double) endratings.remove(0)).doubleValue());
                newratingnovol = (int)Math.round(((Double) endratingsnovol.remove(0)).doubleValue());
                newvol = (int)Math.round(((Double) endvols.remove(0)).doubleValue());
                coder = (new Integer(names.remove(0).toString())).intValue();

                //log.info("updating coder " + coder + " to " + newrating);

                sqlStr.replace(0, sqlStr.length(), "UPDATE room_result SET old_rating = ?, new_rating = ? WHERE round_id = ? AND coder_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setDouble(1, getRating("" + coder));
                ps.setDouble(2, newrating);
                ps.setInt(3, roundId);
                ps.setInt(4, coder);
                retVal = ps.executeUpdate();
                ps.close();
                ps = null;
                
                sqlStr.replace(0, sqlStr.length(), "UPDATE rating SET rating = ?, rating_no_vol = ?, num_ratings = ?, vol=? WHERE coder_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setDouble(1, newrating);
                ps.setDouble(2, newrating);
                ps.setInt(3, getTimesPlayed("" + coder)+1);
                ps.setInt(4, newvol);
                ps.setInt(5, coder);
                retVal = ps.executeUpdate();
                ps.close();
                ps = null;

                updateCoder("" + coder, newrating, newvol, getTimesPlayed("" + coder) + 1);
            } // end while loop over endratings


            endratings = (Vector) resultsplusprov.get(2);
            endvols = (Vector) resultsplusprov.get(1);
            endratingsnovol = (Vector) resultsplusprov.get(0);
            names = (Vector) resultsplusprov.get(3);
            log.debug("************************************************");
            log.debug("************************************************");
            log.debug("************************************************");

            while (endratings.size() > 0) {
                newrating = (int)Math.round(((Double) endratings.remove(0)).doubleValue());
                newratingnovol = (int)Math.round(((Double) endratingsnovol.remove(0)).doubleValue());
                newvol = (int)Math.round(((Double) endvols.remove(0)).doubleValue());
                coder = (new Integer(names.remove(0).toString())).intValue();
                //log.info("updating coder " + coder + " to " + newrating);

                sqlStr.replace(0, sqlStr.length(), "UPDATE room_result SET old_rating = ?, new_rating = ? WHERE round_id = ? AND coder_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setDouble(1, getRating("" + coder));
                ps.setDouble(2, newrating);
                ps.setInt(3, roundId);
                ps.setInt(4, coder);
                retVal = ps.executeUpdate();
                ps.close();
                ps = null;
                
                sqlStr.replace(0, sqlStr.length(), "UPDATE rating SET rating = ?, rating_no_vol = ?, num_ratings = ?, vol=? WHERE coder_id = ?");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setDouble(1, newrating);
                ps.setDouble(2, newrating);
                ps.setInt(3, getTimesPlayed("" + coder)+1);
                ps.setInt(4, newvol);
                ps.setInt(5, coder);
                retVal = ps.executeUpdate();
                ps.close();
                ps = null;

                updateCoder("" + coder, newrating, newvol, getTimesPlayed("" + coder) + 1);

            }  // end while loop over endratings

// All changes made
        } catch (Exception e) {
            log.error(e);
            throw e;
        } finally {
            try {
                if (rs2 != null) rs2.close();
            } catch (Exception e1) {
            }
            try {
                if (ps != null) ps.close();
            } catch (Exception e1) {
            }
        }
    }

//-------------FUNCTIONS AND VARIABLES USED BY QUBITS RATING SYSTEM-----------------------
    int STEPS = 100;
    double initialScore = 1200.0;
    double oneStdDevEquals = 1200.0; /* rating points */
    double initialVolatility = 515;
    double firstVolatility = 385;
    double initialWeight = 0.60;
    double finalWeight = 0.18;
    double volatilityWeight = 0;
    double matchStdDevEquals = 500.0;	/* rating points */
    int people = 0;
    double sqiv = 0.0;
    int people2 = 0;
    double sqfv = 0.0;
    double fv = 0.0;
    int people3 = 0;
    double sb = 0.0;
    int people4 = 0;
    double sqdf = 0.0;

    /**
     *
     * @param names
     * @param ratings
     * @param volatilities
     * @param timesPlayed
     * @param scores
     * @param stringnames
     * @param prov if true, the list includes people that have been never been rated before this round
     * @return
     */
    private ArrayList rateEvent(Vector names, Vector ratings, Vector volatilities, Vector timesPlayed, Vector scores, Vector stringnames, boolean prov) {
        Vector eranks = new Vector();
        Vector eperf = new Vector();
        Vector ranks = new Vector();
        Vector perf = new Vector();
        Vector newVolatility = new Vector();
        Vector newRating = new Vector();
        Vector newRatingWithVol = new Vector();
        int i, j;
        double aveVol = 0,rating,vol;

        /* COMPUTE AVERAGE RATING */
        double rave = 0.0;
        for (i = 0; i < ratings.size(); i++) {
            rave += ((Double) ratings.elementAt(i)).doubleValue();
        }
        rave /= ratings.size();

        /* COMPUTE COMPETITION FACTOR */
        double rtemp = 0, vtemp = 0;
        for (i = 0; i < ratings.size(); i++) {
            vtemp += sqr(((Double) volatilities.elementAt(i)).doubleValue());
            rtemp += sqr(((Double) ratings.elementAt(i)).doubleValue() - rave);
        }
        matchStdDevEquals = Math.sqrt(vtemp / ratings.size() + rtemp / (ratings.size() - 1));

        /* COMPUTE EXPECTED RANKS */
        for (i = 0; i < names.size(); i++) {
            ranks.addElement(new Double(0));
            perf.addElement(new Double(0));
            double est = 0.5;
            double myskill = (((Double) ratings.elementAt(i)).doubleValue() - initialScore) / oneStdDevEquals;
            double mystddev = ((Double) volatilities.elementAt(i)).doubleValue() / oneStdDevEquals;
            for (j = 0; j < names.size(); j++) {
                est += winprobabilitynew(((Double) ratings.elementAt(j)).doubleValue(), ((Double) ratings.elementAt(i)).doubleValue() , ((Double) volatilities.elementAt(j)).doubleValue(), ((Double) volatilities.elementAt(i)).doubleValue() );
            }
            eranks.addElement(new Double(est));
            eperf.addElement(new Double(-normsinv((est - .5) / names.size())));
        }

        /* COMPUTE ACTUAL RANKS */
        for (i = 0; i < names.size();) {
            double max = Double.NEGATIVE_INFINITY;
            int count = 0;

            for (j = 0; j < names.size(); j++) {
                if (((Double) scores.elementAt(j)).doubleValue() >= max && ((Double) ranks.elementAt(j)).doubleValue() == 0) {
                    if (((Double) scores.elementAt(j)).doubleValue() == max)
                        count++;
                    else
                        count = 1;
                    max = ((Double) scores.elementAt(j)).doubleValue();
                }
            }
            for (j = 0; j < names.size(); j++) {
                if (((Double) scores.elementAt(j)).doubleValue() == max) {
                    ranks.setElementAt(new Double(i + 0.5 + count / 2.0), j);
                    perf.setElementAt(new Double(-normsinv((i + count / 2.0) / names.size())), j);
                }
            }
            i += count;
        }

        /* UPDATE RATINGS */
        for (i = 0; i < names.size(); i++) {
            double diff = ((Double) perf.elementAt(i)).doubleValue() - ((Double) eperf.elementAt(i)).doubleValue();
            sqdf += diff * diff;
            people4++;
            double oldrating = ((Double) ratings.elementAt(i)).doubleValue();
            double performedAs = oldrating + diff * matchStdDevEquals;
            double weight = (initialWeight - finalWeight) / (((Integer) timesPlayed.elementAt(i)).intValue() + 1) + finalWeight;

            //get weight - reduce weight for highly rated people
            weight = 1 / (1 - weight) - 1;
            if (oldrating >= 2000 && oldrating < 2500) weight = weight * 4.5 / 5.0;
            if (oldrating >= 2500) weight = weight * 4.0 / 5.0;

            double newrating = (oldrating + weight * performedAs) / (1 + weight);

            //get and inforce a cap
            double cap = 150 + 1500 / (2 + ((Integer) timesPlayed.elementAt(i)).intValue());
            if (oldrating - newrating > cap) newrating = oldrating - cap;
            if (newrating - oldrating > cap) newrating = oldrating + cap;
            if (newrating < 1) newrating = 1;

            newRating.addElement(new Double(newrating));

            if (((Integer) timesPlayed.elementAt(i)).intValue() != 0) {
                if (((Integer) timesPlayed.elementAt(i)).intValue() == 1) {
                    fv += (performedAs - oldrating);
                    sqfv += (performedAs - oldrating) * (performedAs - oldrating);
                    people2++;
                } else {
                    sb += (performedAs - oldrating) * (performedAs - oldrating);
                    people3++;
                }
                double oldVolatility = ((Double) volatilities.elementAt(i)).doubleValue();
                
                //newVolatility.addElement(new Double(Math.sqrt((oldVolatility * oldVolatility
                //        + weight * (diff * matchStdDevEquals) * (diff * matchStdDevEquals) / (1 + weight)) / (1 + weight))));
                newVolatility.addElement(new Double(Math.sqrt((oldVolatility*oldVolatility) / (1+weight) + ((newrating-oldrating)*(newrating-oldrating))/ weight)));
            } else {
                sqiv += (performedAs - oldrating) * (performedAs - oldrating);
                people++;
                newVolatility.addElement(new Double(firstVolatility));
            }
        }
        aveVol = 385;

        for (i = 0; i < newVolatility.size(); i++) {
            rating = ((Double) newRating.elementAt(i)).doubleValue();
            vol = ((Double) newVolatility.elementAt(i)).doubleValue();
            newRatingWithVol.addElement(new Double(rating + volatilityWeight * (aveVol - vol)));
            //if this includes includes the people that have never been rated before
            //and this particular person has been rated before them, then pull them out of the lists
            if (prov && ((Integer) timesPlayed.elementAt(i)).intValue() > 0) {
                names.remove(i);
                timesPlayed.remove(i);
                ratings.remove(i);
                volatilities.remove(i);
                eranks.remove(i);
                eperf.remove(i);
                scores.remove(i);
                ranks.remove(i);
                perf.remove(i);
                newRating.remove(i);
                newVolatility.remove(i);
                newRatingWithVol.remove(i);
                stringnames.remove(i);
                i--;
            }
        }

        log.info("Handle   Player  # Rate Vol Es.R E.SD  Score  Ac.R A.SD D.SD N.RT N.V N.VR");
        for (i = 0; i < names.size(); i++) {
            if (!prov || (prov && ((Integer) timesPlayed.elementAt(i)).intValue() == 0))
                log.info(
                        fS1((String) stringnames.elementAt(i)) + " " +
                        ((String) names.elementAt(i)) + "  " +
                        ((Integer) timesPlayed.elementAt(i)).intValue() + " " +
                        in4((Double) ratings.elementAt(i)) + " " +
                        ((Double) volatilities.elementAt(i)).intValue() + " " +
                        rat((Double) eranks.elementAt(i)) + " " +
                        fD2((Double) eperf.elementAt(i)) + " " +
                        scr((Double) scores.elementAt(i)) + " " +
                        rat((Double) ranks.elementAt(i)) + " " +
                        fD2((Double) perf.elementAt(i)) + " " +
                        fD2(new Double(((Double) perf.elementAt(i)).doubleValue() -
                        ((Double) eperf.elementAt(i)).doubleValue())) +
                        " " + in4((Double) newRating.elementAt(i)) + " " +
                        ((Double) newVolatility.elementAt(i)).intValue()
                        + " " + in4((Double) newRatingWithVol.elementAt(i))
                );
        }

        ArrayList al = new ArrayList();
        al.add(newRating);
        al.add(newVolatility);
        al.add(newRatingWithVol);
        al.add(names);
        return al;
    }

    private String fS1(String in) {
        if (in.length() > 8) return in.substring(0, 8);
        while (in.length() < 8) in = in + " ";
        return in;
    }

    private String fD1(Double in) {
        DecimalFormat oneDigit = new DecimalFormat("0.0");
        return oneDigit.format(in.doubleValue());
    }

    private String fD2(Double in) {
        DecimalFormat oneDigit = new DecimalFormat("0.0");
        DecimalFormat twoDigits = new DecimalFormat("0.00");
        if (in.doubleValue() < 0)
            return oneDigit.format(in.doubleValue());
        return twoDigits.format(in.doubleValue());
    }

    private String scr(Double in) {
        DecimalFormat sixDigits = new DecimalFormat("0000.00");
        DecimalFormat fiveDigits = new DecimalFormat("000.00");
        if (in.doubleValue() < 0)
            return fiveDigits.format(in.doubleValue());
        return sixDigits.format(in.doubleValue());
    }

    private String rat(Double in) {
        DecimalFormat twoDigits = new DecimalFormat("00.0");
        DecimalFormat threeDigits = new DecimalFormat("000.");
        if (in.doubleValue() < 100)
            return twoDigits.format(in.doubleValue());
        return threeDigits.format(in.doubleValue());
    }

    private String in4(Double in) {
        DecimalFormat oneDigit = new DecimalFormat("0000");
        return oneDigit.format(in.doubleValue());
    }

    private double sqr(double j) {
        return j * j;
    }

    private double normsinv(double p) {
        return normsinvnew(p);
    }
    
    private double normsinvnew(double p) {
    /* ********************************************
     * Original algorythm and Perl implementation can
     * be found at:
     * http://www.math.uio.no/~jacklam/notes/invnorm/index.html
     * Author:
     *  Peter J. Acklam
     *  jacklam@math.uio.no
     * ****************************************** */
        
        // Define break-points.
        // variable for result
        if(p <= 0) return Double.NEGATIVE_INFINITY;
        else if(p >= 1) return Double.POSITIVE_INFINITY;
        
        double z = 0;

        // Rational approximation for lower region:
        if( p < P_LOW )
        {
          double q  = Math.sqrt(-2*Math.log(p));
          z = (((((NORMINV_C[0]*q+NORMINV_C[1])*q+NORMINV_C[2])*q+NORMINV_C[3])*q+NORMINV_C[4])*q+NORMINV_C[5]) / ((((NORMINV_D[0]*q+NORMINV_D[1])*q+NORMINV_D[2])*q+NORMINV_D[3])*q+1);
        }
        // Rational approximation for upper region:
        else if ( P_HIGH < p )
        {
          double q  = Math.sqrt(-2*Math.log(1-p));
          z = -(((((NORMINV_C[0]*q+NORMINV_C[1])*q+NORMINV_C[2])*q+NORMINV_C[3])*q+NORMINV_C[4])*q+NORMINV_C[5]) / ((((NORMINV_D[0]*q+NORMINV_D[1])*q+NORMINV_D[2])*q+NORMINV_D[3])*q+1);
        }
        // Rational approximation for central region:
        else
        {
          double q = p - 0.5D;
          double r = q * q;
          z = (((((NORMINV_A[0]*r+NORMINV_A[1])*r+NORMINV_A[2])*r+NORMINV_A[3])*r+NORMINV_A[4])*r+NORMINV_A[5])*q / (((((NORMINV_B[0]*r+NORMINV_B[1])*r+NORMINV_B[2])*r+NORMINV_B[3])*r+NORMINV_B[4])*r+1);
        }
        
        z = refine(z, p);
        return z;
    }
    

    private static final double P_LOW  = 0.02425D;
    private static final double P_HIGH = 1.0D - P_LOW;

    // Coefficients in rational approximations.
    private static final double NORMINV_A[] =
    { -3.969683028665376e+01,  2.209460984245205e+02,
    -2.759285104469687e+02,  1.383577518672690e+02,
    -3.066479806614716e+01,  2.506628277459239e+00 };

    private static final double NORMINV_B[] =
    { -5.447609879822406e+01,  1.615858368580409e+02,
    -1.556989798598866e+02,  6.680131188771972e+01,
    -1.328068155288572e+01 };

    private static final double NORMINV_C[] =
    { -7.784894002430293e-03, -3.223964580411365e-01,
    -2.400758277161838e+00, -2.549732539343734e+00,
    4.374664141464968e+00,  2.938163982698783e+00 };

    private static final double NORMINV_D[] =
    { 7.784695709041462e-03,  3.224671290700398e-01,
    2.445134137142996e+00,  3.754408661907416e+00 };

    public static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                            t * ( 1.00002368 +
                                            t * ( 0.37409196 + 
                                            t * ( 0.09678418 + 
                                            t * (-0.18628806 + 
                                            t * ( 0.27886807 + 
                                            t * (-1.13520398 + 
                                            t * ( 1.48851587 + 
                                            t * (-0.82215223 + 
                                            t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    }
    
    public static double erfc(double z) {
        return 1.0 - erf(z);
    }

    public static double refine(double x, double d)
    {
        if( d > 0 && d < 1)
        {
          double e = 0.5D * erfc(-x/Math.sqrt(2.0D)) - d;
          double u = e * Math.sqrt(2.0D*Math.PI) * Math.exp((x*x)/2.0D);
          x = x - u/(1.0D + x*u/2.0D);
        }
        return x;
    }

    private double norminv(double p, double mean, double stddev) {
        return mean + normsinv(p) * stddev;
    }
    
    private double winprobabilitynew(double r1, double r2, double v1, double v2) {
        return (erf((r1-r2)/Math.sqrt(2.0*(v1*v1+v2*v2)))+1.0)*.5;
    }

//----------------------END RATING FUNCTIONS-----------------------------------------------

}

