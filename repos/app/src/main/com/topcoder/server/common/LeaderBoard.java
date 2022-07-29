/**
 * Class LeaderBoard
 *
 * Author: Hao Kung
 *
 * Description: This class will contain all leaderboard info
 */

package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.util.logging.Logger;

public class LeaderBoard implements Serializable {

    private static Logger trace = Logger.getLogger(LeaderBoard.class);

    protected String m_cacheKey;

    public final String getCacheKey() {
        return m_cacheKey;
    }

    public static String getCacheKey(long rID) {
        return "LB." + rID;
    }

    public LeaderBoard(int roundID) {
        m_roundID = roundID;
        m_cacheKey = getCacheKey(m_roundID);
    }

    private long m_roundID;
    private HashMap m_roomMap = new HashMap();
    private ArrayList board;

    private LeaderboardItem[] m_boardData;


    public void initialize(Round contest) {
        if (contest.getRoundID() != m_roundID)
            throw new IllegalArgumentException("Invalid contest passed to intialize. RoundID = " + m_roundID);

        m_roomMap = new HashMap();
        board = new ArrayList(contest.getNumRooms());
        int count = 0;
        for (Iterator roomIDs = contest.getAllRoomIDs(); roomIDs.hasNext();) {
            int roomID = ((Integer) roomIDs.next()).intValue();
            Room baseRoom = CoreServices.getRoom(roomID, false);
            if (baseRoom != null && baseRoom instanceof BaseCodingRoom) {
                BaseCodingRoom room = (BaseCodingRoom) baseRoom;
                if (room.getLeader() == null) {
                    room.updateLeader(); // TODO save to cache?
                }
                if (!room.isAdminRoom()) { 
                    if (room.getLeader() != null) {
                        RoomLeaderInfo leaderInfo = room.getLeaderInfo();
                        Coder leader = leaderInfo.getCoder();
                        board.add(new LeaderboardItem(
                                room.getRoomID(),
                                leader.getName(),
                                leader.getRating(),
                                leaderInfo.getSeed(),
                                leaderInfo.getPoints(),
                                leaderInfo.isCloseContest()
                        ));
                    } else {
                        board.add(null);
                    }
                    // TODO : might need to sort the rows by room number and then use the right count on them.
                    m_roomMap.put(new Integer(roomID), new Integer(count));
                    count++;
                }
            } else {
                trace.error("Invalid room returned for id: " + roomID + " Room = " + baseRoom);
            }
        }
        buildBoardArray();
    }

    private void buildBoardArray() {
        ArrayList resultBoard = new ArrayList(board);
        for (Iterator iter = resultBoard.iterator(); iter.hasNext();) {
            if (iter.next() == null) {
                iter.remove();
            }
        }
        m_boardData = (LeaderboardItem[]) resultBoard.toArray(new LeaderboardItem[resultBoard.size()]);
    }

    private int getRow(BaseCodingRoom room) {
        return ((Integer) m_roomMap.get(new Integer(room.getRoomID()))).intValue();
    }

    public void updateLeader(BaseCodingRoom room) {
        int row = getRow(room);
        RoomLeaderInfo leaderInfo = room.getLeaderInfo();
        Coder leader = leaderInfo.getCoder();
        LeaderboardItem item = new LeaderboardItem(
                room.getRoomID(),
                leader.getName(),
                leader.getRating(),
                leaderInfo.getSeed(),
                leaderInfo.getPoints(),
                leaderInfo.isCloseContest()
        );
        boolean rebuild = board.get(row) == null;
        board.set(row, item);
        if (rebuild) {
            buildBoardArray();
        } else {
            m_boardData[row] = item;
        }
    }

    public long getRoundID() {
        return m_roundID;
    }

    public LeaderboardItem[] getItems() {
        return m_boardData;
    }

    public boolean isInitialized() {
        return m_boardData != null && m_boardData.length > 0;
    }

}
