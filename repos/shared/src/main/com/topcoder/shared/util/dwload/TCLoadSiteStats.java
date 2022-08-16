package com.topcoder.shared.util.dwload;

/**
 * TCLoadSiteStats
 * 
 * This class loads from site stats DB into transactional.
 * For the moment, it just loads the last site hit date of each user.
 * 
 * @author cuuc
 */

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

public class TCLoadSiteStats extends TCLoad {
    private static Logger log = Logger.getLogger(TCLoadSiteStats.class);
    protected java.sql.Timestamp fStartTime = null;
    protected java.sql.Timestamp fLastLogTime = null;

    public static int SITE_HITS_LOAD = 6;

    public TCLoadSiteStats() {
        DEBUG = false;
    }

    /**
     * This method is passed any parameters passed to this load
     */
    public boolean setParameters(Hashtable params) {
        return true;
    }

    /**
     * This method performs the load for the coder information tables
     */
    public void performLoad() throws Exception {
        try {
            fStartTime = new java.sql.Timestamp(System.currentTimeMillis());

            getLastUpdateTime();

            loadLastSiteHit();

            setLastUpdateTime();

            log.info("SUCCESS: Site Stats load ran successfully.");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }


    private void getLastUpdateTime() throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer query = null;

        query = new StringBuffer(100);
        query.append("select timestamp from update_log where log_id = ");
        query.append("(select max(log_id) from update_log where log_type_id = " + SITE_HITS_LOAD + ")");

        try {
            stmt = createStatement(SOURCE_DB);
            rs = stmt.executeQuery(query.toString());
            if (rs.next()) {
                fLastLogTime = rs.getTimestamp(1);
                log.info("Date is " + fLastLogTime.toString());
            } else {
                // A little misleading here as we really didn't hit a SQL
                // exception but all we are doing outside this method is
                // catchin and setting the reason for failure to be the
                // message of the exception.
                throw new SQLException("Last log time not found in " +
                        "update_log table.");
            }
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Failed to retrieve last log time.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(stmt);
        }
    }


    private void loadLastSiteHit() throws Exception {
        int count = 0;
        int retVal = 0;
        PreparedStatement psSel = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        StringBuffer query = null;

        try {
            query = new StringBuffer(100);
            query.append(" select coder_id, max(timestamp) as last_hit from site_hit ");
            query.append(" where timestamp > ?");
            query.append(" and coder_id is not null");
            query.append(" group by coder_id");

            psSel = prepareStatement(query.toString(), SOURCE_DB);
            psSel.setTimestamp(1, fLastLogTime);


            query = new StringBuffer(100);
            query.append(" UPDATE user SET last_site_hit_date = ? WHERE user_id = ?");
            psUpd = prepareStatement(query.toString(), TARGET_DB);

            rs = executeQuery(psSel, "loadSiteHit");

            while (rs.next()) {
                psUpd.setTimestamp(1, rs.getTimestamp("last_hit"));
                psUpd.setInt(2, rs.getInt("coder_id"));
                retVal = psUpd.executeUpdate();

                count = count + retVal;
                printLoadProgress(count, "site_hit");
            }

            log.info("coders last hit date modifed: " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of site_hit table failed.\n" +
                    sqle.getMessage());
        }
    }


    private void setLastUpdateTime() throws Exception {
        PreparedStatement psUpd = null;
        StringBuffer query = null;

        try {
            int retVal = 0;
            query = new StringBuffer(100);
            query.append("INSERT INTO update_log ");
            query.append("      (log_id ");        // 1
            query.append("       ,calendar_id ");  // 2
            query.append("       ,timestamp ");   // 3
            query.append("       ,log_type_id) ");   // 4
            query.append("VALUES (0, ?, ?, " + SITE_HITS_LOAD + ")");
            psUpd = prepareStatement(query.toString(), SOURCE_DB);

            int calendar_id = lookupCalendarId(fStartTime, TARGET_DB);
            psUpd.setInt(1, calendar_id);
            psUpd.setTimestamp(2, fStartTime);

            retVal = psUpd.executeUpdate();
            if (retVal != 1) {
                throw new SQLException("SetLastUpdateTime " +
                        " modified " + retVal + " rows, not one.");
            }
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Failed to set last log time.\n" +
                    sqle.getMessage());
        } finally {
            close(psUpd);
        }
    }

}
