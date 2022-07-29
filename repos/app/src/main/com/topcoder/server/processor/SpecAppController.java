/*
 * User: Michael Cervantes
 * Date: Sep 4, 2002
 * Time: 11:55:42 PM
 */
package com.topcoder.server.processor;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.*;
import com.topcoder.server.services.*;
import com.topcoder.shared.util.logging.*;
import com.topcoder.shared.util.*;

import java.util.*;

class SpecAppController implements StoppableThread.Client {

    private static final Logger log = Logger.getLogger(SpecAppController.class);

    private SpecAppProcessor specAppProcessor;

    private boolean rotate = false;
    private long rotateDelayMS = 10 * 1000;

    private Round[] activeRounds;
    private int roundIndex = 0;
    private Iterator roomIterator;
    private int MAX_CODERS_TO_SHOW = 5;

    private StoppableThread stoppableThread;
    private HashMap watchlistMap;
    private Object mapLock;

    SpecAppController(SpecAppProcessor specAppProcessor) {
        this.specAppProcessor = specAppProcessor;
        stoppableThread = new StoppableThread(this, "SpecAppController.stoppableThread");
        watchlistMap = new HashMap();
        mapLock = new Object();
    }

    void start() {
        log.info("Starting..");
        stoppableThread.start();
    }

    void stop() {
        log.info("Stopping..");
        try {
            stoppableThread.stopThread();
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    synchronized void setRotating(boolean rotate) {
        if (log.isDebugEnabled()) {
            log.debug("Set rotation = " + rotate);
        }
        this.rotate = rotate;
    }

    synchronized void setRotateDelay(int rotateDelaySeconds) {
        if (rotateDelaySeconds < 1) {
            throw new IllegalArgumentException("Invalid rotate delay, must be >= 1 second: " + rotateDelaySeconds + " sec");
        }
        if (log.isDebugEnabled()) {
            log.debug("Setting delay = " + rotateDelaySeconds);
        }
        this.rotateDelayMS = rotateDelaySeconds * 1000;
    }

    // Currently the scoreboard is in charge of requesting rooms to watch
    /*
    protected ContestRound[] getActiveRounds() {
        return CoreServices.getAllActiveRounds();
    }

    private ContestRoom getNextRoom() {
//        log.debug("Get next room called");
        if (roomIterator == null || !roomIterator.hasNext()) {
//            log.debug("Resetting room iterator");
            if (activeRounds == null || activeRounds.length == 0) {
                activeRounds = getActiveRounds();
            }
            if (activeRounds.length == 0) {
//                log.debug("No rounds found");
                return null;
            }
            if (++roundIndex >= activeRounds.length) {
//                log.debug("Resetting round index");
                activeRounds = getActiveRounds();
                roundIndex = 0;
            }
            roomIterator = activeRounds[roundIndex].getAllRoomIDs();
//            log.debug("Displaying round: " + activeRounds[roundIndex]);
        }
        if (roomIterator.hasNext()) {
            return getRoom((Number) roomIterator.next());
        }
        else {
            return null;
        }
    }
    */

    private BaseCodingRoom getRoom(int roomID) {
        try {
            BaseCodingRoom room = (BaseCodingRoom) CoreServices.getRoom(roomID, false);
            if (room != null) {
                return room;
            } else {
                throw new IllegalArgumentException("Bad room ID: " + roomID);
            }
        } catch (Exception e) {
            log.error("Error retrieving room " + roomID, e);
            return null;
        }
    }

    private WeakestLinkTeam getTeam(int teamID) throws Exception {
        try {
            WeakestLinkTeam team = CoreServices.getWeakestLinkTeam(teamID);
            if (team != null) {
                return team;
            } else {
                throw new IllegalArgumentException("Bad team ID: " + teamID);
            }
        } catch (Exception e) {
            log.error("Error retrieving team data for team " + teamID, e);
            throw e;
        }
    }

    public void registerRoom(int connectionID, int roomID) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Registering a room " + roomID);
            }
            BaseCodingRoom room = getRoom(roomID);
            if (room.isAdminRoom()) {
                log.error("Register room: Room ID " + roomID + " is an admin room");
                return;
            }
            int coderIDs[] = room.getCoderIDs();
            if (coderIDs.length == 0) {
                log.error("Register room: Room ID " + roomID + " has no coders!  Room not registered.");
                return;
            }
            register(connectionID, roomID, Watchlist.ROOM_TYPE, room.getRoundID(), coderIDs);
            log.info("Register room: Registering room " + roomID + " with " + coderIDs.length + " coders");
        } catch (Exception e) {
            log.error("Register room: No room ID " + roomID + " found in database");
            return;
        }
    }

    public void registerWeakestLinkTeam(int connectionID, int teamID) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Registering a team " + teamID);
            }
            WeakestLinkTeam team = getTeam(teamID);
            int coderIDs[] = team.getCoderIds();
            if (coderIDs.length == 0) {
                log.error("Register weakest link team: Team " + teamID + " for round " + team.getRoundId() + " has no members!  Team not registered");
                return;
            }
            register(connectionID, teamID, Watchlist.WEAKEST_LINK_TEAM_TYPE, team.getRoundId(), coderIDs);
            log.info("Register weakest link team: Registering team " + teamID + " for round " + team.getRoundId() + " with " + coderIDs.length + " coders");
        } catch (Exception e) {
            log.error("Register weakest link team: team ID " + teamID + " not registered");
            return;
        }
    }

    private void register(int connectionID, int itemID, int itemType, int roundId, int coderIDs[]) {
        synchronized (mapLock) {
            if (coderIDs.length == 0) {
                throw new IllegalArgumentException("Cannot register an item with no coders");
            }
            Watchlist wl = (Watchlist) watchlistMap.get(new Integer(connectionID));
            if (wl == null) {
                wl = new Watchlist();
                watchlistMap.put(new Integer(connectionID), wl);
            }
            wl.addWatch(itemID, itemType, roundId, coderIDs);
        }
    }

    // Don't keep broadcasting updates to dropped connections
    public void removeConnection(int connectionID) {
        synchronized (mapLock) {
            watchlistMap.remove(new Integer(connectionID));
        }
    }

    public void cycle() throws InterruptedException {
//        log.debug("SpecAppController cycling...");

        long sleepMS;
        boolean tempRotate = false;
        synchronized (this) {
            sleepMS = rotateDelayMS;
            tempRotate = rotate;
        }

//        log.debug("SpecAppController sleeping for " + sleepMS + " ms");

        Thread.sleep(sleepMS);

        //log.debug("SpecAppController woke up");

        if (tempRotate) {
            //log.debug("Rotation activated");

            // Old functionality
            /*
            ContestRoom room = getNextRoom();

            if (room != null && !room.isAdminRoom()) {
//                log.debug("Sending show room");
                specAppProcessor.showRoom(room);
            }
            else {
//                log.debug("No rooms to show");
            }
            */

            // For each connection, we need to send it its next show message
            synchronized (mapLock) {
                Iterator it = watchlistMap.keySet().iterator();
                while (it.hasNext()) {
                    Integer connectionId = (Integer) it.next();
                    if (log.isDebugEnabled()) {
                        log.debug("Sending message to connection " + connectionId);
                    }
                    Watchlist wl = (Watchlist) watchlistMap.get(connectionId);
                    Watch w = wl.getNextWatch(MAX_CODERS_TO_SHOW);
                    if (w == null) {
                        log.debug("Skipping this connection - nothing to watch in this contest phase");
                        continue;
                    }
                    int watchType = w.getType();
                    int watchId = w.getID();
                    int coderIDs[] = w.getCoderIDs();
                    if (log.isDebugEnabled()) {
                        log.debug("Next watch found, of type " + watchType + " and object ID " + watchId);
                    }

                    try {
                        if (watchType == Watchlist.ROOM_TYPE) {
                            specAppProcessor.showRoom(connectionId.intValue(), getRoom(watchId), coderIDs);
                        } else if (watchType == Watchlist.WEAKEST_LINK_TEAM_TYPE) {
                            specAppProcessor.showWeakestLinkTeam(connectionId.intValue(), watchId, coderIDs);
                        }
                    } catch (Exception e) {
                        // If we get here, it's probably due to a problem with getRoom()
                        log.error("Exception retrieving data to show", e);
                    }
                }
            }
        }
    }

    public void showRoom(int connectionID, Number roomID) {
        BaseCodingRoom room = getRoom(roomID.intValue());
        if (room != null) {
            if (room.isAdminRoom()) {
                throw new IllegalArgumentException("Can't show the admin room!");
            }
            log.info("Setting current spec app room: " + room);
            int coderIDs[] = room.getCoderIDs();
            specAppProcessor.showRoom(connectionID, room, coderIDs);
        }
    }

    public void broadcastShowRoom(Number roomID) {
        BaseCodingRoom room = getRoom(roomID.intValue());
        if (room != null) {
            if (room.isAdminRoom()) {
                throw new IllegalArgumentException("Can't show the admin room!");
            }
            log.info("Setting current spec app room: " + room);
            int coderIDs[] = room.getCoderIDs();
            specAppProcessor.broadcastShowRoom(room, coderIDs);
        }
    }

    public void announceAdvancingCoders(long roundID, int numAdvancing) {
        specAppProcessor.announceAdvancingCoders(roundID, numAdvancing);
    }

    // The Watchlist class keeps track of items to watch for a given connection.
    // Synchronization unnecessary because it's already used inside a lock in the
    // controller code above.
    private class Watchlist {

        static final int ROOM_TYPE = 1;
        static final int WEAKEST_LINK_TEAM_TYPE = 2;

        private ArrayList watchItems;
        private int watchIndex, numWatches;

        Watchlist() {
            watchItems = new ArrayList();
            watchIndex = 0;
            numWatches = 0;
        }

        void addWatch(int itemID, int itemType, int roundID, int coderIDs[]) {
            if (coderIDs.length == 0) {
                throw new IllegalArgumentException("Cannot add a watch on an item with no coders");
            }
            watchItems.add(new WatchEntry(itemID, itemType, roundID, coderIDs.length - 1, 0, coderIDs));
            numWatches++;
        }

        Watch getNextWatch(int codersToWatch) {
            if (numWatches == 0) {
                return null;
            }

            int startWatchIndex = watchIndex;
            for (; ;) {
                WatchEntry w = (WatchEntry) watchItems.get(watchIndex);
                int startCoder = w.getNextCoderIndex();
                int endCoder = startCoder + codersToWatch - 1;
                int lastCoder = w.getLastCoderIndex();
                if (endCoder < lastCoder) {
                    w.setNextCoderIndex(endCoder + 1);
                } else {
                    endCoder = lastCoder;
                    w.setNextCoderIndex(0);
                    watchIndex++;
                    if (watchIndex >= numWatches) {
                        watchIndex = 0;
                    }
                    if (watchIndex == startWatchIndex) {
                        // Nothing in our list is watchable in this phase
                        return null;
                    }
                }

                // Check to see whether what we have is watchable.  If not, loop to get the next item.
                // - For a weakest link round, room watches are only allowed before the
                // voting phase and team watches are only allowed during or after the voting
                // phase.
                // - For a contest round, only room watches are allowed, regardless of phase.
                Round cr = CoreServices.getContestRound(w.getRoundID());
                int phaseId = cr.getPhase();
                if (cr instanceof WeakestLinkRound) {
                    if (phaseId >= ContestConstants.VOTING_PHASE && w.getItemType() == ROOM_TYPE) continue;
                    if (phaseId < ContestConstants.VOTING_PHASE && w.getItemType() == WEAKEST_LINK_TEAM_TYPE) continue;
                } else {
                    if (w.getItemType() == WEAKEST_LINK_TEAM_TYPE) continue;
                }

                int length = endCoder - startCoder + 1;
                int idlist[] = new int[length];
                System.arraycopy(w.getCoderIDs(), startCoder, idlist, 0, length);
                return new Watch(w.getItemID(), w.getItemType(), idlist);
            }
        }
    }

    // The WatchEntry class keeps track of a given item to watch
    private class WatchEntry {

        private final int itemID;
        private final int itemType;
        private final int roundID;
        private final int lastCoderIndex;
        private int nextCoderIndex;
        private int coderIDs[];

        WatchEntry(int itemID, int itemType, int roundID, int lastCoderIndex, int nextCoderIndex, int coderIDs[]) {
            this.itemID = itemID;
            this.itemType = itemType;
            this.roundID = roundID;
            this.lastCoderIndex = lastCoderIndex;
            setNextCoderIndex(nextCoderIndex);
            this.coderIDs = coderIDs;
        }

        int getItemID() {
            return itemID;
        }

        int getItemType() {
            return itemType;
        }

        int getRoundID() {
            return roundID;
        }

        int getLastCoderIndex() {
            return lastCoderIndex;
        }

        int getNextCoderIndex() {
            return nextCoderIndex;
        }

        void setNextCoderIndex(int index) {
            if (index > lastCoderIndex) {
                throw new IllegalArgumentException("Next coder index cannot exceed last coder index");
            }
            nextCoderIndex = index;
        }

        int[] getCoderIDs() {
            return coderIDs;
        }
    }

    // The Watch class is sent to the controller with the information about
    // the next item to watch.
    private class Watch {

        private int itemID;
        private int itemType;
        private int coderIDs[];

        Watch(int itemID, int itemType, int coderIDs[]) {
            this.itemID = itemID;
            this.itemType = itemType;
            this.coderIDs = coderIDs;
        }

        int getID() {
            return itemID;
        }

        int getType() {
            return itemType;
        }

        int[] getCoderIDs() {
            return coderIDs;
        }
    }
}

