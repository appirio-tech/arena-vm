package com.topcoder.server.processor;

import java.util.HashMap;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * Tracks applet state for the given user.  Primarily intended for translating
 * between indexes and ids for rooms/challenges/etc.
 */
final class UserState {

    /**
     * Category for logging.
     */
    private static Logger trace = Logger.getLogger(UserState.class);

    private static HashMap s_userStates = new HashMap();

    static synchronized UserState getUserState(int userID) {
        UserState state = (UserState) s_userStates.get(new Integer(userID));
        if (state == null) {
            state = new UserState();
            if (trace.isDebugEnabled()) trace.debug("Creating a new state for ID = " + userID + " state =  " + state);
            s_userStates.put(new Integer(userID), state);
        }
        return state;
    }

    static synchronized boolean hasUserState(int userID) {
        return s_userStates.get(new Integer(userID)) != null;
    }

    static synchronized void removeUserState(int userID) {
        s_userStates.remove(new Integer(userID));
    }

//    private ArrayList m_lobbyRoomList;
//    synchronized void setLobbyRoomList( ArrayList roomList ) { m_lobbyRoomList = roomList; }
//
//    private ArrayList m_practiceRoomList;
//    synchronized void setPracticeRoomList( ArrayList roomList ) { m_practiceRoomList = roomList; }

    /*
    public synchronized int contestRoomIDToIndex( int id )
    {
        return m_contestRoomList.indexOf( new Integer( id ) );
    }
    */

    /**
     * Translates a given roomID to an index.
     */
//    synchronized int roomIDToIndex( int id )
//    {
//        try
//        {
//            int index = practiceRoomIDToIndex( id );
//            if( index == -1 )
//            {
//                index = activeRoomIDToIndex( id );
//                if( index == -1 )
//                {
//					index = activeChatIDToIndex(id);
//					if(index == -1)
//					{
//                        index = lobbyRoomIDToIndex( id );
//                        if( index == -1 )
//                           trace.debug( "Failed to find index for ID: " + id + " activeRooms = " + m_activeRoomList );
//					}
//                }
//            }
//            return index;
//        }
//        catch( NullPointerException npe )
//        {
//            // User lists havent been initialized possible due to not having logged in yet.
//            trace.debug( "roomIDToIndex error", npe );
//            return -1;
//        }
//    }

    private boolean m_receiveChat = true;

    void setReceiveChat(boolean value) {
        m_receiveChat = value;
    }

    boolean getReceiveChat() {
        return m_receiveChat;
    }

    private boolean m_canTestOrSubmit = false;

    void setCanTestOrSubmit(boolean value) {
        m_canTestOrSubmit = value;
    }

    public boolean canTestOrSubmit() {
        return m_canTestOrSubmit;
    }

    private long m_busyTime = -1;

    private long getBusyTime() {
        return m_busyTime;
    }

    private void setBusyTime(long time) {
        m_busyTime = time;
    }

    boolean isBusy() {
        return (System.currentTimeMillis() - getBusyTime()) < ContestConstants.TIMEOUT_MILLIS;
    }

    void resetBusyTime() {
        setBusyTime(-1);
    }

    void setCurrentBusyTime() {
        setBusyTime(System.currentTimeMillis());
    }

    static class ProblemState {
        static final int CLOSE = 0;
        static final int CODING = 1;
        static final int CHALLENGING = 2;

        private int state;
        private int roomId;
        private int userId;
        private int problemId;

        ProblemState(int state, int roomId, int userId, int problemId) {
            this.state = state;
            this.roomId = roomId;
            this.userId = userId;
            this.problemId = problemId;
        }

        int getState() {
            return state;
        }

        int getRoomID() {
            return roomId;
        }

        int getUserID() {
            return userId;
        }

        int getProblemID() {
            return problemId;
        }
    }

    static final ProblemState PROBLEM_CLOSE = new ProblemState(ProblemState.CLOSE, -1, -1, -1);

    private ProblemState problemState = PROBLEM_CLOSE;

    void setProblemState(ProblemState problemState) {
        this.problemState = problemState;
    }

    ProblemState getProblemState() {
        return problemState;
    }
}
