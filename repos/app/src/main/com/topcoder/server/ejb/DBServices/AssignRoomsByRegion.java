package com.topcoder.server.ejb.DBServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;

final class AssignRoomsByRegion {

    private static final Logger cat = Logger.getLogger(AssignRoomsByRegion.class);

    private static void closePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static List getRegisteredCodersList(Connection conn, int roundID) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "SELECT er.coder_id, cr.rating, cr.rating_no_vol, i.region_code, i.seed " +
                "FROM round_registration er, invite_list i, rating cr " +
                "WHERE er.round_id = ? AND er.round_id = i.round_id AND er.coder_id = i.coder_id AND " +
                "er.coder_id = cr.coder_id " +
                "ORDER BY seed";
        try {
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, roundID);
            rs = ps.executeQuery();
            List coderList = new ArrayList();
            while (rs.next()) {
                int coderID = rs.getInt(1);
                int rating = rs.getInt(2);
                int rating_no_vol = rs.getInt(3);
                String region_code = rs.getString(4);
                int seed = rs.getInt(5);
                coderList.add(new AssignRoomsItem(coderID, rating, rating_no_vol, region_code, seed));
            }
            return coderList;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            closePreparedStatement(ps);
        }
    }

    private static List[] getCodersByRegionArray(List coderList) {
        Set regionSet = new HashSet();
        for (Iterator it = coderList.iterator(); it.hasNext();) {
            AssignRoomsItem item = (AssignRoomsItem) it.next();
            regionSet.add(item.getRegion());
        }
        List regionList = new ArrayList(regionSet);
        Collections.sort(regionList);
        int numRegions = regionList.size();
        List[] array = new List[numRegions];
        for (int i = 0; i < array.length; i++) {
            List list = new LinkedList();
            String currRegion = (String) regionList.get(i);
            for (Iterator it = coderList.iterator(); it.hasNext();) {
                AssignRoomsItem item = (AssignRoomsItem) it.next();
                if (currRegion.equals(item.getRegion())) {
                    list.add(item);
                }
            }
            array[i] = list;
        }
        return array;
    }

    private static void removeAndAdd(List roomList, int i, List coderList) {
        ((List) roomList.get(i)).add(coderList.remove(0));
    }

    private static List assignRooms(List coderList, int codersPerRoom) {
        List roomList = new ArrayList();
        int numCoders = coderList.size();
        int numRooms = (numCoders + codersPerRoom - 1) / codersPerRoom;
        for (int i = 0; i < numRooms; i++) {
            roomList.add(new ArrayList());
        }
        while (coderList.size() > 0) {
            if (coderList.size() >= numRooms) {
                for (int i = 0; i < numRooms; i++) {
                    removeAndAdd(roomList, i, coderList);
                }
            }
            for (int i = numRooms - 1; i >= 0 && coderList.size() > 0; i--) {
                removeAndAdd(roomList, i, coderList);
            }
        }
        return roomList;
    }

    private static void printRoomList(List roomList) {
        for (int i = 0; i < roomList.size(); i++) {
            debug("room: " + (i + 1));
            List list = (List) roomList.get(i);
            for (int j = 0; j < list.size(); j++) {
                debug("seed: " + (j + 1) + ", coder: " + list.get(j));
            }
        }
    }

    private static void insertRooms(Connection conn, int roomIds[], int roundID) throws SQLException {
        PreparedStatement ps = null;
        String sqlStr = "INSERT INTO room (room_id,round_id,name,division_id,room_type_id) VALUES (?, ?, ?, ?, ?)";
        try {
            ps = conn.prepareStatement(sqlStr);
            for (int i = 0; i < roomIds.length; i++) {
                String name = "Room " + (i + 1);
                int divisionID = 1;
                ps.setInt(1, roomIds[i]);
                ps.setInt(2, roundID);
                ps.setString(3, name);
                ps.setInt(4, divisionID);
                ps.setInt(5, ServerContestConstants.CONTEST_ROOM_TYPE_ID);
                debug("Adding room, id=" + roomIds[i] + ", name=" + name);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new RuntimeException("ERROR: Creating rooms for room assignments");
                }
            }
        } finally {
            closePreparedStatement(ps);
        }
    }

    private static void insertCoders(Connection conn, List roomList, int roomIds[], int roundID) throws SQLException {
        PreparedStatement ps = null;
        String sqlStr = "INSERT INTO room_result (round_id,room_id,coder_id,room_seed,point_total,attended,advanced) " +
                "VALUES (?,?,?,?,0,'N','N')";
        try {
            ps = conn.prepareStatement(sqlStr);
            for (int i = 0; i < roomList.size(); i++) {
                List list = (List) roomList.get(i);
                for (int j = 0; j < list.size(); j++) {
                    int seed = j + 1;
                    AssignRoomsItem item = (AssignRoomsItem) list.get(j);
                    int coderID = item.getCoderID();
                    ps.setInt(1, roundID);
                    ps.setInt(2, roomIds[i]);
                    ps.setInt(3, coderID);
                    ps.setInt(4, seed);
                    ps.executeUpdate();
                }
            }
        } finally {
            closePreparedStatement(ps);
        }
    }

    private static void updateOldRating(Connection conn, int roundID) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sqlStr = "UPDATE room_result SET old_rating = (SELECT rating FROM rating " +
                    "WHERE coder_id = room_result.coder_id) WHERE round_id = ?";
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, roundID);
            int rows = ps.executeUpdate();
            debug(rows + " rows updated in ROOM_RESULT (SET old_rating)");
        } finally {
            closePreparedStatement(ps);
        }
    }

    private static void updateNewRating(Connection conn, int roundID) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sqlStr = "UPDATE room_result SET new_rating = old_rating WHERE round_id = ?";
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, roundID);
            int rows = ps.executeUpdate();
            debug(rows + " rows updated in ROOM_RESULT (SET new_rating)");
        } finally {
            closePreparedStatement(ps);
        }
    }

    private static void updateRoundStatus(Connection conn, int roundID) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sqlStr = "UPDATE round SET status = 'A' WHERE round_id = ?";
            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1, roundID);
            int rows = ps.executeUpdate();
            if (rows != 1) {
                error("ERROR: Updating ROUND_SEGMENTS on NEW_LEADERBOARD response.");
            }
        } finally {
            closePreparedStatement(ps);
        }
    }

    // dpecora - change to get room id's from a sequence
    static void assignRoomsByRegion(int roundID, boolean isFinal, int codersPerRoom) {
        debug("assignRoomsByRegion, roundID=" + roundID + ", isFinal=" + isFinal);
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            List coderList = getRegisteredCodersList(conn, roundID);
            List[] codersByRegionArray = getCodersByRegionArray(coderList);
            List roomList = new ArrayList();
            for (int i = 0; i < codersByRegionArray.length; i++) {
                roomList.addAll(assignRooms(codersByRegionArray[i], codersPerRoom));
            }
            printRoomList(roomList);

            if (isFinal) {
                int roomIds[] = new int[roomList.size()];
                for (int i = 0; i < roomIds.length; i++) {
                    roomIds[i] = IdGeneratorClient.getSeqIdAsInt(DBMS.ROOM_SEQ);
                }

                insertRooms(conn, roomIds, roundID);
                insertCoders(conn, roomList, roomIds, roundID);
                updateOldRating(conn, roundID);
                updateNewRating(conn, roundID);
                updateRoundStatus(conn, roundID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void error(String msg) {
        cat.error(msg);
    }

    private static void info(String msg) {
        cat.info(msg);
    }

    private static void debug(String msg) {
        cat.debug(msg);
    }

}
