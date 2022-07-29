package com.topcoder.shared.util.dwload;

/**
 * TCLoad.java
 *
 * This is the base class for all TopCoder data loads. This is an abstract
 * class so that you can extend it and implement the performLoad method.
 *
 * @author Christopher Hopkins [TCid: darkstalker] (chrism_hopkins@yahoo.com)
 * @version $Revision$
 *
 */

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

public abstract class TCLoad {
    private static Logger log = Logger.getLogger(TCLoad.class);
    protected String USAGE_MESSAGE = null;

    /**
     * When DEBUG is set, the various selects are constrained to the range of
     * coder ids (not inclusive) between MIN and MAX coder id
     */
    protected static boolean DEBUG = true;
    protected static final int MIN_CODER_ID = 119675;
    protected static final int MAX_CODER_ID = 139515;

    /**
     * The reason this particular load failed. This should be a nicely
     * formatted message with plenty of information as it is printed to
     * stderr if a particular load fails
     */
    private String fReasonFailed = null;

    /**
     * Some constants that indicate where in the URL arraylist the source and
     * target URLs are stored. This is just in case we want to support more than
     * two database connections.
     */
    protected static final int SOURCE_DB = 0;
    protected static final int TARGET_DB = 1;

    /**
     * URL Strings for the various databases you wish to connect to. When you
     * want to get a connection to a particular database, you simply pass in
     * the index of the URL string you wish to connect to.
     */
    private ArrayList fDatabaseURLs = new ArrayList();

    /**
     * Hashtable which maintains all the open connection objects. The key here
     * is the index used when adding it to the database URL list.
     */
    private Hashtable fConnections = new Hashtable();

    /**
     * How often do you want a printout of the number of rows loaded during the
     * course of a given table load. This is in number of rows.
     */
    private static final int LOAD_PRINT_INTERVAL = 25;

    /**
     * Algorithm types
     */
    protected static final int TC_RATING_TYPE_ID = 1;
    protected static final int HS_RATING_TYPE_ID = 2;
    protected static final int MARATHON_RATING_TYPE_ID = 3;

    /**
     * Round types
     */
    protected static final int ROUND_TYPE_MARATHON_TOURNAMENT = 19;
    
    // PUBLIC METHODS

    /**
     * This method must be overridden by derived classes. It is called
     * to perform whatever load that class represents.
     */
    public abstract void performLoad() throws Exception;

    /**
     * This method must be overridden by derived classes. It is called
     * to set whatever parameters are passed to that load. The
     * parameters are passed as a Hashtable of param name -> param value
     * entries. All values are Strings. Return false if any parameters
     * are missing and set the reason failed to indicate which
     * parameters are missing.
     *
     * NOTE: For the time being, we are just going to get passed the args from
     *       the command line and we pull what we need from those.
     */
    public abstract boolean setParameters(Hashtable params);

    /**
     * Call this method to retrieve the reason the load failed. This is set by
     * derived load classes.
     */
    public String getReasonFailed() {
        return fReasonFailed;
    }

    /**
     * Call this method to set the URL necessary to connect to the source DB.
     */
    public void setSourceDBURL(String url) {
        try {
            fDatabaseURLs.remove(SOURCE_DB);
        } catch (Exception ex) {
        }

        fDatabaseURLs.add(SOURCE_DB, url);
    }

    /**
     * Call this method to build the source database connection.
     */
    public Connection buildSourceDBConn() throws SQLException {
        Connection conn = openConnection(SOURCE_DB);
        return conn;
    }

    /**
     * Call this method to set the URL necessary to connect to the target DB.
     */
    public void setTargetDBURL(String url) {
        try {
            fDatabaseURLs.remove(TARGET_DB);
        } catch (Exception ex) {
        }

        fDatabaseURLs.add(TARGET_DB, url);
    }

    /**
     * Call this method to build the target database connection.
     */
    public Connection buildTargetDBConn() throws SQLException {
        Connection conn = openConnection(TARGET_DB);
        return conn;
    }

    /**
     * Call this method to close all database connections that are
     * currently open.
     */
    public void closeDBConnections() throws SQLException {
        Connection conn;
        int numConns = fDatabaseURLs.size();
        for (int i = 0; i < numConns; i++) {
            Object ob = fConnections.get(new Integer(i));
            if (ob != null) {
                conn = (Connection) ob;
                conn.close();
            }
        }
    }

    // PROTECTED METHODS

    /**
     * Returns a Connection object which is a database connection to
     * the indexed URL string added through addDatabase.
     *
     * @param index The index into the list of database connections
     * @return The java.sql.Connection object which is a connection to that DB
     */
    protected Connection openConnection(int index) throws SQLException {
//        if (!checkArrayListIndex(index))
//            return null;

        String urlstr = (String) fDatabaseURLs.get(index);
        Connection conn = DriverManager.getConnection(urlstr);
        PreparedStatement ps = conn.prepareStatement("set lock mode to wait 5");
        ps.execute();
        ps.close();
        if(index == SOURCE_DB) {
            setSourceConnection(conn);
        } else if(index == TARGET_DB) {
            setTargetConnection(conn);
        }
        return conn;
    }

    public void setSourceConnection(Connection c) {
        fConnections.put(new Integer(SOURCE_DB), c);
    }

    public void setTargetConnection(Connection c) {
        fConnections.put(new Integer(TARGET_DB), c);
    }

    /**
     * Convenience method for closing ResultSet objects since we need to close
     * them all over the place.
     */
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

    /**
     * Convenience method for closing Statement and PreparedStatement
     * objects since we need to close them all over the place.
     */
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

    /**
     * Derived classes should call this method to set a message as to why the
     * load failed and then return false from performLoad.
     */
    protected void setReasonFailed(String reasonfailed) {
        fReasonFailed = reasonfailed;
    }

    /**
     * Call this method to create a PreparedStatement for a given database
     * connection.
     */
    protected PreparedStatement prepareStatement(String sqlStr, int connIdx)
            throws SQLException {

//        if (!checkArrayListIndex(connIdx))
//            return null;

        Connection conn = getOpenConnection(connIdx);
        if (conn == null)
            return null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlStr);
        } catch (SQLException e) {
            log.error("Error for query: \n" + sqlStr);
            throw e;
        }
        return ps;
    }

    /**
     * Convenience method for getting a connection out of the Hashtable
     */
    protected Connection getOpenConnection(int idx) {
        Object ob = fConnections.get(new Integer(idx));
        if (ob == null)
            return null;
        return (Connection) ob;
    }

    /**
     * Call this method to create a Statement for a given database connection.
     */
    protected Statement createStatement(int connIdx)
            throws SQLException {
        if (!checkArrayListIndex(connIdx))
            return null;

        Connection conn = getOpenConnection(connIdx);
        if (conn == null)
            return null;

        return conn.createStatement();
    }

    /**
     * Call this method to set a possibly null byte value in a
     * PreparedStatement. This method checks to see if the byte[] passed
     * is null. If it is, then a null is set in the PreparedStatement
     * with a type of BINARY
     */
    protected void setBytes(PreparedStatement pstmt, int index, byte[] value)
            throws SQLException {
        if (value != null)
            pstmt.setBytes(index, value);
        else
            pstmt.setNull(index, java.sql.Types.BINARY);
    }

    /**
     * Call this method to retrieve a byte array from a ResultSet. This
     * traps any exceptions and returns null if an exception is caught.
     * Use this on columns of type 'Text'
     */
    protected byte[] getBytes(ResultSet rs, int index) {
        byte[] b = null;

        try {
            b = DBMS.serializeTextString(DBMS.getTextString(rs, index));
        } catch (Exception e) {
            return null;
        }

        return b;
    }

    /**
     * Call this method to retrieve a byte array from a ResultSet as a
     * BlobObject. This traps any exceptions and returns null if an
     * exception is caught. Use this on columns of type 'byte'
     */
    protected byte[] getBlobObject(ResultSet rs, int index) {
        byte[] b = null;

        try {
            b = DBMS.serializeBlobObject(DBMS.getBlobObject(rs, index));
        } catch (Exception e) {
            return null;
        }

        return b;
    }

    protected void printLoadProgress(int count, String table) {
        if (count % LOAD_PRINT_INTERVAL == 0) {
            log.info("Loaded " + count + " rows for " + table + "...");
        }
    }

    /**
     * Call this method to lookup a calendar_id from the calendar table
     * based on the Timestamp passed in. This assumes that a calendar
     * table exists in the database represented by the Connection object
     * corresponding to the connection index passed in.
     */
    protected int lookupCalendarId(java.sql.Timestamp date, int connIdx)
            throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        StringBuffer query = null;

        int year = cal.get(Calendar.YEAR);

        // The month is based on 0 for January so we need to add 1 to get
        // the right lookup value
        int month_of_year = cal.get(Calendar.MONTH) + 1;

        int day_of_month = cal.get(Calendar.DAY_OF_MONTH);

        PreparedStatement psSel = null;
        ResultSet rs = null;

        query = new StringBuffer(100);
        query.append("SELECT calendar_id ");
        query.append("  FROM calendar ");
        query.append(" WHERE year = ? ");
        query.append("   AND month_numeric = ? ");
        query.append("   AND day_of_month = ? ");
        psSel = prepareStatement(query.toString(), connIdx);

        psSel.setInt(1, year);
        psSel.setInt(2, month_of_year);
        psSel.setInt(3, day_of_month);

        try {
            rs = psSel.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Unable to locate calendar_id for " +
                        date.toString());
            }
        } catch (SQLException sqle) {
            throw sqle;
        } finally {
            close(rs);
            close(psSel);
        }
    }
    
    protected int lookupTimeId(java.sql.Timestamp date, int connIdx) throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        StringBuffer query = null;

        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        PreparedStatement psSel = null;
        ResultSet rs = null;

        query = new StringBuffer(100);
        query.append("SELECT time_id ");
        query.append("  FROM time ");
        query.append(" WHERE minute = ? ");
        query.append("   AND hour_24 = ? ");
        psSel = prepareStatement(query.toString(), connIdx);

        psSel.setInt(1, minute);
        psSel.setInt(2, hour);

        try {
            rs = psSel.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Unable to locate time_id for " +
                        date.toString());
            }
        } catch (SQLException sqle) {
            throw sqle;
        } finally {
            close(rs);
            close(psSel);
        }
    }

    
    /**
     * Convenience method for retrieving an integer parameter from
     * the Hashtable of parameters passed to this load.
     */
    protected Integer retrieveIntParam(String paramName, Hashtable params,
                                       boolean optional, boolean mustBePositive)
            throws Exception {
        String tmp = (String) params.get(paramName);
        if (tmp == null) {
            if (!optional)
                throw new Exception("Please specify a " + paramName + ".\n" +
                        USAGE_MESSAGE);
            else
                return null;
        }

        try {
            int value = Integer.valueOf(tmp).intValue();
            if (mustBePositive && value < 0)
                throw new Exception(paramName + " must be a positive integer.");
            return new Integer(value);
        } catch (Exception ex) {
            throw new Exception("Invalid " + paramName + ": " + tmp +
                    ".\n" + ex.getMessage());
        }
    }


    /**
     * Convenience method for retrieving an integer parameter from
     * the Hashtable of parameters passed to this load.
     */
    protected Boolean retrieveBooleanParam(String paramName, Hashtable params,
                                           boolean optional)
            throws Exception {
        String tmp = (String) params.get(paramName);
        if (tmp == null) {
            if (!optional)
                throw new Exception("Please specify a " + paramName + ".\n" +
                        USAGE_MESSAGE);
            else
                return null;
        }

        try {
            return Boolean.valueOf(tmp);
        } catch (Exception ex) {
            throw new Exception("Invalid " + paramName + ": " + tmp +
                    ".\n" + ex.getMessage());
        }
    }


    /**
     * This method executes a given statement and returns the
     * ResultSet. We use this so we can perform timings on the various
     * query executions.
     */
    protected ResultSet executeQuery(PreparedStatement ps, String queryName)
            throws SQLException {
        long start = System.currentTimeMillis();
        ResultSet rs = ps.executeQuery();
        log.info("Time in " + queryName + " query: " +
                (System.currentTimeMillis() - start));
        return rs;
    }

    /**
     * Gets the type for this round (high school or regular).
     *
     * @param round round to determine its type
     * @return TC_HS_RATING_TYPE_ID or TC_RATING_TYPE_ID
     * @throws Exception if the type couldn't be retrieved
     */
    protected int getRoundType(int round) throws Exception    {
        PreparedStatement psSel = null;
        ResultSet rs = null;
        StringBuffer query = null;
        try {
            query = new StringBuffer(100);
            query.append(" SELECT algo_rating_type_id ");
            query.append("   FROM round r, round_type_lu rt ");
            query.append("   WHERE r.round_type_id = rt.round_type_id ");
            query.append("   AND r.round_id = ? ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            psSel.setInt(1, round);
            rs = psSel.executeQuery();
            if (!rs.next()) {
                throw new Exception("Can't find an entry for round " + round + " in round table");
            }

            return rs.getInt("algo_rating_type_id");

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Get round type failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
    }

    /**
     * Gets the Season_id for this round.
     *
     * @param round round to determine its season
     * @return the season_id for that round, or -1 if it doesn't belong to any season
     * @throws Exception if the type couldn't be retrieved
     */
    protected int getSeasonId(int round) throws Exception    {
        PreparedStatement psSel = null;
        ResultSet rs = null;
        StringBuffer query = null;
        try {
            query = new StringBuffer(100);
            query.append(" SELECT c.season_id ");
            query.append(" FROM round r, contest c ");
            query.append(" WHERE c.contest_id = r.contest_id ");
            query.append(" AND r.round_id = ? ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            psSel.setInt(1, round);
            rs = psSel.executeQuery();
            if (!rs.next()) {
                return -1;
            }

            return rs.getString(1) == null? -1 : rs.getInt(1);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("getSeasonId failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(psSel);
        }
    }

    // PRIVATE METHODS

    /**
     * Convenience method to make sure the passed index is valid for our
     * ArrayList of URLs.
     */
    private boolean checkArrayListIndex(int idx) {
        if (idx < 0 || idx >= fConnections.size())
            return false;
        return true;
    }




}
