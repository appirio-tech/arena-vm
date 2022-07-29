package com.topcoder.server.ejb.TrackingServices;

import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.Tracking;
import com.topcoder.server.ejb.BaseEJB;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;

public class TrackingServicesBean extends BaseEJB {

    private static boolean VERBOSE = false;
    //private SessionContext ctx;

    ////////////////////////////////////////////////////////////////////////////////
    public void ejbCreate()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (VERBOSE) s_trace.debug("TrackingBean: ejbCreate called.");
    }


    /**
     * This method is called from the ejbRemove method, which is responsible for
     * cleaning up any open connections or free up any other system resources
     * that are no longer needed.
     */
    ////////////////////////////////////////////////////////////////////////////////
    /*
	private synchronized void cleanUp()
		////////////////////////////////////////////////////////////////////////////////
	{
		if (VERBOSE) Log.msg("Cleaning Up TrackingBean...");
	}
    */

    private final static Logger s_trace = Logger.getLogger(TrackingServicesBean.class);

    //store a tracking object in the DB
    public void storeTracking(Tracking t) throws RemoteException {

        java.sql.Connection con = null;

        PreparedStatement ps = null;
        PreparedStatement ps2 = null;

        int request_id = getNextUniqueRequestID();
        int request_type_id = t.getRequestTypeID();
        int coder_id = t.getCoderID();
        int round_id = t.getRoundID();
        int room_id = t.getRoomID();
        int connection_id = t.getConnectionID();
        int server_id = t.getServerID();
        long timestamp = t.getTimestamp();

        if (room_id == -2) {
            s_trace.error("We had another -2 room :");
            s_trace.error(request_id + "," + request_type_id + "," + coder_id + "," + round_id + "," + room_id + "," + connection_id + "," + server_id + "," + timestamp);
            return;
        }
        try {
            con = DBMS.getConnection();

            switch (request_type_id) {
            case ContestConstants.WATCH:

                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp, room_id, round_id, open_window) VALUES(?,?,?,?,?,?,?,?,?)");
                ps.setInt(7, room_id);
                ps.setInt(8, round_id);
                ps.setTimestamp(9, new Timestamp(timestamp));
                break;
            case ContestConstants.UNWATCH:
                ps2 = con.prepareStatement("UPDATE request SET close_window = ?, last_request_type_id = ? WHERE round_id = ? AND room_id = ? AND connection_id = ? AND server_id = ? AND request_type_id = ? AND (close_window IS NULL)");
                ps2.setTimestamp(1, new Timestamp(timestamp));
                ps2.setInt(2, request_type_id);
                ps2.setInt(3, round_id);
                ps2.setInt(4, room_id);
                ps2.setInt(5, connection_id);
                ps2.setInt(6, server_id);
                ps2.setInt(7, ContestConstants.WATCH);
                break;
            case ContestConstants.MOVE:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp, room_id, round_id, open_window) VALUES(?,?,?,?,?,?,?,?,?)");
                ps.setInt(7, room_id);
                ps.setInt(8, round_id);
                ps.setTimestamp(9, new Timestamp(timestamp));

                ps2 = con.prepareStatement("UPDATE request SET close_window = ?, last_request_type_id = ? WHERE connection_id = ? AND server_id = ? AND request_type_id = ? AND (close_window IS NULL)");
                ps2.setTimestamp(1, new Timestamp(timestamp));
                ps2.setInt(2, request_type_id);
                ps2.setInt(3, connection_id);
                ps2.setInt(4, server_id);
                ps2.setInt(5, ContestConstants.MOVE);
                break;
            case ContestConstants.CODER_INFO:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp) VALUES(?,?,?,?,?,?)");
                break;
            case ContestConstants.CODER_HISTORY:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp, room_id, round_id) VALUES(?,?,?,?,?,?,?,?)");
                ps.setInt(7, room_id);
                ps.setInt(8, round_id);
                break;
            case ContestConstants.GET_PROBLEM:
            case ContestConstants.GET_COMPONENT:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp, room_id, round_id) VALUES(?,?,?,?,?,?,?,?)");
                ps.setInt(7, room_id);
                ps.setInt(8, round_id);
                break;
            case ContestConstants.GET_CHALLENGE_PROBLEM:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp, room_id, round_id) VALUES(?,?,?,?,?,?,?,?)");
                ps.setInt(7, room_id);
                ps.setInt(8, round_id);
                break;
            case ContestConstants.GET_LEADER_BOARD:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp, open_window) VALUES(?,?,?,?,?,?,?)");
                ps.setTimestamp(7, new Timestamp(timestamp));
                break;
            case ContestConstants.CLOSE_LEADER_BOARD:
                ps2 = con.prepareStatement("UPDATE request SET close_window = ?, last_request_type_id = ? WHERE connection_id = ? AND server_id = ? AND request_type_id = ? AND (close_window IS NULL)");
                ps2.setTimestamp(1, new Timestamp(timestamp));
                ps2.setInt(2, request_type_id);
                ps2.setInt(3, connection_id);
                ps2.setInt(4, server_id);
                ps2.setInt(5, ContestConstants.GET_LEADER_BOARD);
                break;
            case ContestConstants.OPEN_SUMMARY_REQUEST:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp, open_window) VALUES(?,?,?,?,?,?,?)");
                ps.setTimestamp(7, new Timestamp(timestamp));
                break;
            case ContestConstants.CLOSE_SUMMARY_REQUEST:
                ps2 = con.prepareStatement("UPDATE request SET close_window = ?, last_request_type_id = ? WHERE connection_id = ? AND server_id = ? AND request_type_id = ? AND (close_window IS NULL)");
                ps2.setTimestamp(1, new Timestamp(timestamp));
                ps2.setInt(2, request_type_id);
                ps2.setInt(3, connection_id);
                ps2.setInt(4, server_id);
                ps2.setInt(5, ContestConstants.OPEN_SUMMARY_REQUEST);
                break;
            case ContestConstants.LOGGED_IN_USERS:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp) VALUES(?,?,?,?,?,?)");
                break;
            case ContestConstants.REGISTER_USERS:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp) VALUES(?,?,?,?,?,?)");
                break;
            case ContestConstants.SEARCH:
                ps = con.prepareStatement("INSERT INTO request (request_id, request_type_id, coder_id, connection_id, server_id, timestamp) VALUES(?,?,?,?,?,?)");
                break;
            case -1:
                if (connection_id == -1) {
                    ps2 = con.prepareStatement("UPDATE request SET close_window = ? WHERE server_id = ? AND (request_type_id = ? OR request_type_id = ? OR request_type_id = ?) AND (close_window IS NULL)");
                    ps2.setTimestamp(1, new Timestamp(timestamp));
                    ps2.setInt(2, server_id);
                    ps2.setInt(3, ContestConstants.GET_LEADER_BOARD);
                    ps2.setInt(4, ContestConstants.WATCH);
                    ps2.setInt(5, ContestConstants.MOVE);
                } else {
                    ps2 = con.prepareStatement("UPDATE request SET close_window = ? WHERE connection_id = ? AND server_id = ? AND (request_type_id = ? OR request_type_id = ? OR request_type_id = ?) AND (close_window IS NULL)");
                    ps2.setTimestamp(1, new Timestamp(timestamp));
                    ps2.setInt(2, connection_id);
                    ps2.setInt(3, server_id);
                    ps2.setInt(4, ContestConstants.GET_LEADER_BOARD);
                    ps2.setInt(5, ContestConstants.WATCH);
                    ps2.setInt(6, ContestConstants.MOVE);
                }
                break;
            }
            int retVal = 0;
            if (ps2 != null) {
                retVal = ps2.executeUpdate();
            }
            if (ps != null) {
                ps.setInt(1, request_id);
                ps.setInt(2, request_type_id);
                ps.setInt(3, coder_id);
                ps.setInt(4, connection_id);
                ps.setInt(5, server_id);
                ps.setTimestamp(6, new Timestamp(timestamp));
                retVal = ps.executeUpdate();
                if (retVal != 1)
                    s_trace.error("ERROR: Inserting REQUEST");
            }
        } catch (Exception e) {
            s_trace.error("ERROR: Failed to update REQUEST table");
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        } finally {
            try {
                if (ps2 != null) ps2.close();
            } catch (Exception ignore) {
                s_trace.error("Close E: " + ignore);
            }
            try {
                if (ps != null) ps.close();
            } catch (Exception ignore) {
                s_trace.error("Close E: " + ignore);
            }
            try {
                if (con != null) con.close();
            } catch (Exception ignore) {
                s_trace.error("Close E: " + ignore);
            }
        }
    }

//takes all items in the database since given time, and generates a tracking object for each one.  These it puts in an ArrayList, which is returned.
    public ArrayList retrieveSince(long time) throws RemoteException {
        return null;
    }

    /**
     * Returns the next server id from the sequence in the db
     */
    public int getNextUniqueRequestID() throws RemoteException {
        try {
            return IdGeneratorClient.getSeqIdAsInt(DBMS.REQUEST_SEQ);
        } catch (Exception e) {
            throw new RemoteException(e.toString());
        }
    }
}
