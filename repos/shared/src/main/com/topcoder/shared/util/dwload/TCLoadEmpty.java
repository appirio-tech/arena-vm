package com.topcoder.shared.util.dwload;

/*****************************************************************************
 * TCLoadRequests.java
 *
 * TCLoadCoders loads applet requests from the RDBMS to the Data Warehouse.
 *
 * @author Matthew Lahut [TCid: Garzahd] (mlahut@andrew.cmu.edu)
 * Much thanks to Chris Hopkins [TCid: darkstalker] (chrism_hopkins@yahoo.com)
 * for the template
 * @version $Revision$
 *
 *****************************************************************************/

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

public class TCLoadEmpty extends TCLoad {
    protected java.sql.Timestamp fStartTime = null;
    protected java.sql.Timestamp fLastLogTime = null;

    public TCLoadEmpty() {
        //DEBUG = false;
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
            doContests();
            doRounds();
            doRooms();

            System.out.println("Ran successfully.");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }

    private void doContests() throws Exception {
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        StringBuffer query = null;

        ResultSet rs = null;
        int count = 0;
        int retVal = 0;

        try {
            // Our select statement
            query = new StringBuffer(100);
            query.append("SELECT rt.contest_id ");           // 1
            query.append("       ,rt.name ");     // 2
            query.append("       ,rt.start_date ");            // 3
            query.append("       ,rt.end_date ");            // 4
            query.append("       ,rt.status ");             // 5
            query.append("       ,rt.group_id");         // 7
            query.append("       ,rt.region_code ");         // 8
            query.append("       ,rt.ad_text ");       // 9
            query.append("       ,rt.ad_start ");           // 10
            query.append("       ,rt.ad_end ");           //11
            query.append("       ,rt.ad_task ");        //12
            query.append("       ,rt.ad_command ");        //13
            query.append("  FROM contest rt ");

            psSel = prepareStatement(query.toString(), SOURCE_DB);
            // Our insert statement
            query = new StringBuffer(100);
            query.append("INSERT INTO contest");
            query.append("       (contest_id ");           // 1
            query.append("       ,name ");     // 2
            query.append("       ,start_date ");            // 3
            query.append("       ,end_date ");            // 4
            query.append("       ,status ");             // 5
            query.append("       ,group_id");         // 7
            query.append("       ,region_code ");         // 8
            query.append("       ,ad_text ");       // 9
            query.append("       ,ad_start ");           // 10
            query.append("       ,ad_end ");           //11
            query.append("       ,ad_task ");        //12
            query.append("       ,ad_command) ");        //13
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?,");  // 10
            query.append("?,?)");                    // 13

            psIns = prepareStatement(query.toString(), TARGET_DB);
            rs = psSel.executeQuery();
            while (rs.next()) {

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // request_id
                psIns.setString(2, rs.getString(2));  // request_type_id
                psIns.setTimestamp(3, rs.getTimestamp(3));  // open_window
                psIns.setTimestamp(4, rs.getTimestamp(4));  // open_period
                psIns.setString(5, rs.getString(5));  // request_type_id
                psIns.setInt(6, rs.getInt(6));  // request_type_id
                psIns.setString(7, rs.getString(7));  // request_type_id
                psIns.setString(8, rs.getString(8));  // open_window
                psIns.setTimestamp(9, rs.getTimestamp(9));  // open_period
                psIns.setTimestamp(10, rs.getTimestamp(10));  // open_period
                psIns.setString(11, rs.getString(11));  // request_type_id
                psIns.setString(12, rs.getString(12));  // request_type_id

                //System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)+" "+rs.getInt(4)+" "+rs.getInt(5)+rs.getTimestamp(10).toString());
                try {
                    retVal = psIns.executeUpdate();
                } catch (Exception e) {//e.printStackTrace();
                }
                count += retVal;

                printLoadProgress(count, "request");
            }

            System.out.println("Records loaded for request: " + count);
        } catch (Exception sqle) {
            sqle.printStackTrace();
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
        }
    }


    private void doRounds() throws Exception {
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        StringBuffer query = null;

        ResultSet rs = null;
        int count = 0;
        int retVal = 0;

        try {
            // Our select statement
            query = new StringBuffer(100);
            query.append("SELECT rt.round_id");           // 1
            query.append("       ,rt.contest_id ");     // 2
            query.append("       ,rt.name ");            // 3
            query.append("       ,rt.status ");            // 4
            query.append("       ,rt.notes ");           // 8
            query.append("       ,rt.invitational ");        //9
            query.append("       ,rt.round_type_id ");       // 10
            query.append("       ,rl.round_type_desc ");       // 10
            query.append("  FROM round rt, round_type_lu rl");
            query.append(" WHERE rt.round_type_id = rl.round_type_id");

            psSel = prepareStatement(query.toString(), SOURCE_DB);
            // Our insert statement
            query = new StringBuffer(100);
            query.append("INSERT INTO round");
            query.append("       (round_id ");           // 1
            query.append("       ,contest_id ");           // 1
            query.append("       ,name ");            // 3
            query.append("       ,status ");            // 4
            query.append("       ,notes ");           // 8
            query.append("       ,invitational ");        //9
            query.append("       ,round_type_id");       // 10
            query.append("       ,round_type_desc) ");       // 10
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?)");  // 7

            psIns = prepareStatement(query.toString(), TARGET_DB);
            rs = psSel.executeQuery();
            while (rs.next()) {

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // request_id
                psIns.setInt(2, rs.getInt(2));  // request_id
                psIns.setString(3, rs.getString(3));  // request_type_id
                psIns.setString(4, rs.getString(4));  // request_type_id
                psIns.setString(5, rs.getString(5));  // request_type_id
                psIns.setInt(6, rs.getInt(6));  // request_type_id
                psIns.setInt(7, rs.getInt(7));  // request_type_id
                psIns.setString(8, rs.getString(8));  // request_type_id

                //System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)+" "+rs.getInt(4)+" "+rs.getInt(5)+rs.getTimestamp(10).toString());

                try {
                    retVal = psIns.executeUpdate();
                } catch (Exception e) {
                }

                count += retVal;

                printLoadProgress(count, "request");
            }
            System.out.println("Records loaded for request: " + count);
        } catch (Exception sqle) {
            sqle.printStackTrace();
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
        }
    }

    private void doRooms() throws Exception {
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        StringBuffer query = null;

        ResultSet rs = null;
        int count = 0;
        int retVal = 0;

        try {
            // Our select statement
            query = new StringBuffer(100);
            query.append("SELECT rt.room_id");           // 1
            query.append("       ,rt.round_id ");     // 2
            query.append("       ,rt.name ");            // 3
            query.append("       ,rt.state_code ");            // 4
            query.append("       ,rt.country_code ");         // 5
            query.append("       ,rt.region_code ");       // 6
            query.append("       ,rt.division_id ");           //7
            query.append("       ,rt.room_type_id ");        //8
            query.append("       ,rt.eligible ");        //9
            query.append("       ,rt.unrated ");        //10
            query.append("  FROM room rt ");
            query.append("   WHERE NOT EXISTS ");
            query.append("       (SELECT 'pops' ");
            query.append("          FROM room ro ");
            query.append("         WHERE ro.room_id = rt.room_id ");
            query.append("           AND ro.room_type_id <> 1)");

            psSel = prepareStatement(query.toString(), SOURCE_DB);
            // Our insert statement
            query = new StringBuffer(100);
            query.append("INSERT INTO room");
            query.append("       (room_id ");           // 1
            query.append("       ,round_id ");     // 2
            query.append("       ,name ");            // 3
            query.append("       ,state_code ");             // 4
            query.append("       ,country_code ");         // 5
            query.append("       ,region_code ");           // 6
            query.append("       ,division_id ");           //7
            query.append("       ,room_type_id ");        //8
            query.append("       ,eligible ");        //9
            query.append("       ,unrated)");        //10
            query.append("VALUES (");
            query.append("?,?,?,?,?,?,?,?,?,?)");  // 10

            psIns = prepareStatement(query.toString(), TARGET_DB);
            rs = psSel.executeQuery();
            while (rs.next()) {

                psIns.clearParameters();
                psIns.setInt(1, rs.getInt(1));  // request_id
                psIns.setInt(2, rs.getInt(2));  // request_id
                psIns.setString(3, rs.getString(3));  // request_type_id
                psIns.setString(4, rs.getString(4));  // request_type_id
                psIns.setString(5, rs.getString(5));  // request_type_id
                psIns.setString(6, rs.getString(6));  // request_type_id
                psIns.setInt(7, rs.getInt(7));  // request_type_id
                psIns.setInt(8, rs.getInt(8));  // request_type_id
                psIns.setInt(9, rs.getInt(9));  // request_type_id
                psIns.setInt(10, rs.getInt(10));  // request_type_id

                //System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)+" "+rs.getInt(4)+" "+rs.getInt(5)+rs.getTimestamp(10).toString());
                try {
                    retVal = psIns.executeUpdate();
                } catch (Exception e) {
                }
                count += retVal;

                printLoadProgress(count, "request");
            }
            System.out.println("Records loaded for request: " + count);
        } catch (Exception sqle) {
            sqle.printStackTrace();
        } finally {
            close(rs);
            close(psSel);
            close(psIns);
        }
    }


}
