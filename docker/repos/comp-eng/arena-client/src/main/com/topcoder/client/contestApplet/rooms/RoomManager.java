package com.topcoder.client.contestApplet.rooms;

/**
 * RoomManager.java
 *
 * Created on June 2, 2001, 8:43 PM
 */

import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.panels.main.MainMenuPanel;
import com.topcoder.client.contestApplet.uilogic.frames.RoomInfoFrame;
import com.topcoder.client.contestApplet.uilogic.panels.IntermissionPanelManager;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.TimeOutException;
import com.topcoder.client.contestant.view.RoomViewManager;
import com.topcoder.client.ui.event.UIWindowAdapter;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * This class manages the different types of rooms in the TopCoder applets.
 * Responsibilities include creating the rooms, clearing out the rooms, and
 * switching rooms.
 *
 * There has been some dispute concerning when the rooms should be created.
 *
 * Here are the two options :
 *
 *   1. create all the rooms at startup
 *       -- disadvantage - initial overhead of loading the applet.
 *       -- advantage - once the applet is loaded it is much quicker.
 *
 *   2. create the rooms initially when they are needed the first time.
 *       -- disadvantage - slow room load the first time.
 *       -- advantage - distribute room creation time.
 *
 * Currently option number 2 is being used. Instead of recreating each room
 * type when it is needed, the rooms types are only created once, cleared
 *  and reused when needed.
 *
 * Here is the sequence of events to load a new room.
 *
 * 1. A change of room is requested (selected from the menu....etc)
 * 2. loadRoom()/loadRoom(type) is called to prepare to load a new room
 * 3. loadNewRoom() is called by loadRoom()
 * 4. loadIntermission() is called by loadNewRoom()
 * 5. createNewRoom() is called by the moveResponse() or the loadRoom thread
 * 6. finally the intermission screen is replaced by the new room.
 *
 * @author  Alex Roman
 * @version
 */
public final class RoomManager implements RoomViewManager {

    private ContestApplet ca = null;
    private RoomModule currentRoom = null;
    protected String roomVersion = null;
    private RoomModule loginRoom = null;
    private LobbyRoom lobbyRoom = null;
    private CoderRoom coderRoom = null;
    private CoderRoom longCoderRoom = null;
    private TeamCoderRoom teamCoderRoom = null;
    private CoderRoom spectatorRoom = null;
    private int currentRoomType = ContestConstants.LOGIN_ROOM;
    private long currentRoomID = ContestConstants.ANY_ROOM;
    private IntermissionPanelManager is = null;
    private Thread loadRoom = null;
    private JPanel mainPanel = null;
    private JPanel navPanel = null;
    private MainMenuPanel menuPanel = null;

    private int intermissionType = IntermissionPanelManager.LOGIN_INTERMISSION_PANEL;
    private boolean loading = false;
    private Object loadingLock = new Object();


    public RoomManager(ContestApplet ca, String roomVersion) {
        this.ca = ca;
        this.roomVersion = roomVersion;
        is = new IntermissionPanelManager(ca);
        try {
            loginRoom = (RoomModule) Class.forName("com.topcoder.client.contestApplet.rooms." + roomVersion + "LoginRoom")
                    .getConstructor(new Class[]{ca.getClass()}).newInstance(new Object[]{ca});         
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Error instantiating login room: " + e.getMessage());
        }
    }

    /**
     * Physically create the room type and all of its components, and prepare it
     * for first time usage. Because of the overhead of creating all the room
     * types on startup, each room type is only initially created on a need to
     * use basis.
     *
     * @param roomID          An int representing the room type to create.
     * @return RoomModule     The newly created room object.
     */
    ////////////////////////////////////////////////////////////////////////////////
    private RoomModule createRoom(RoomModel room)
            ////////////////////////////////////////////////////////////////////////////////
    {
        RoomModule r = null;

//        System.out.println("room.getType().intValue() = "  + room.getType().intValue());
        switch (room.getType().intValue()) {
        case ContestConstants.LOBBY_ROOM:
            r = lobbyRoom = new LobbyRoom(ca);
            break;
        case ContestConstants.MODERATED_CHAT_ROOM:
            r = new LobbyRoom(ca, room.getType().intValue());
            break;
        case ContestConstants.CONTEST_ROOM:
        case ContestConstants.CODER_ROOM:
        case ContestConstants.ADMIN_ROOM:
        case ContestConstants.PRACTICE_CODER_ROOM:
            if (room.getRoundModel().getRoundType().isLongRound()) {
                r = longCoderRoom = new LongCoderRoom(ca);
            } else {
                r = coderRoom = new CoderRoom(ca);
            }
            break;
        case ContestConstants.TEAM_ADMIN_ROOM:
        case ContestConstants.TEAM_CODER_ROOM:
        case ContestConstants.TEAM_PRACTICE_CODER_ROOM:
            r = teamCoderRoom = new TeamCoderRoom(ca);
            break;
        default:
            r = loginRoom = new TopCoderLoginRoom(ca);
            break;
        }
        currentRoomType = room.getType().intValue();

        return (r);
    }

    /**
     * Load the initial room when the applet starts (Login Room)
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void loadInitRoom()
            ////////////////////////////////////////////////////////////////////////////////
    {
        blockIfLoading();
        try {
        	if(currentRoom!=null) currentRoom.leave();
        	
            this.mainPanel = ca.getMainFrame().getMainPanel();
            this.navPanel = ca.getMainFrame().getNavPanel();
            this.menuPanel = ca.getMainFrame().getMenuPanel();
            currentRoomType = ContestConstants.LOGIN_ROOM;            
            currentRoom = loginRoom;
            mainPanel.removeAll();
            mainPanel.add(loginRoom.reload());
            loginRoom.enter();
        } finally {
            doneLoading();
        }
    }

    private void blockIfLoading() {
        synchronized (loadingLock) {
            while (loading) {
                try {
                    loadingLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            loading = true;
        }
    }

    private void doneLoading() {
        synchronized (loadingLock) {
            loading = false;
            loadingLock.notify();
        }
    }


    /**
     * Load room request. It loads an available room of the given room type.
     *
     * @param roomType       An int representing the room type to load.
     * @param intermissionType An int representing the type of intermission screen while loading the room.
     */
    public void loadRoom(int roomType, int intermissionType) {
        loadRoom(roomType, ContestConstants.ANY_ROOM, intermissionType);
    }


    /**
     * Load room request. Do any last minute checks/preparations before deciding
     * to start the load room process, which spawns off two threads to complete the
     * task. The threads are required in order to allow quick refreshing of the
     * swing components. Otherwise the applet would freeze during the reloading
     * process.
     *
     * @param roomType       An int representing the room type to load.
     * @param roomID         An int representing the ID of the room to load.
     * @param intermissionType An int representing the type of intermission screen while loading the room.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void loadRoom(int roomType, long roomID, int intermissionType)
            ////////////////////////////////////////////////////////////////////////////////
    {
//        System.out.println("loadRoom("+roomType+", "+roomID+", "+intermissionType+") called.");

        blockIfLoading();
        this.intermissionType = intermissionType;
        // part with the last room on good terms
        try {
            if (!currentRoom.leave())
                return;
            // find a new room
            currentRoomType = roomType;
            currentRoomID = roomID;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // spawns a thread to load the new room
        if (roomType == ContestConstants.LOGIN_ROOM) {
            loadNewRoom(false);
        } else {
            loadNewRoom(true);
        }
    }


    /**
     * Prepare and load the new room that has been selected.
     *
     * 1. Prepare a new loadRoom thread which will reload/create the new Room.
     * 2. Execute loadIntermission(), which will spawn off a new thread, which
     *    will load and intermission panel, then execute the newly created
     *    loadRoom thread from step 1.
     * 3. when the loadRoom thread has finished preparing the new room for occupancy,
     *    it will, remove the intermission panel, and replace it with the new room
     *    panel.
     *
     * @param requestMove     boolean which determines if a move request is called
     */
    ////////////////////////////////////////////////////////////////////////////////
    private void loadNewRoom(final boolean requestMove)
            ////////////////////////////////////////////////////////////////////////////////
    {
        loadRoom = new Thread(new Runnable() {
            public void run() {
                boolean logoff = false;
                String msgTitle = null;
                String msgText = null;

                try {
                    if (requestMove) {
            			while (true) {
            			    try {
            			        moveToRoom(currentRoomType, currentRoomID);
            			    } catch (TimeOutException e) {
            			        if (ContestConstants.isPracticeRoomType(currentRoomType)) {
            			            // When the room is a practice room, continue waiting.
            			            Thread.yield();
            			            continue;
            			        }
            			        // Otherwise, throw the exception
            			        throw e;
            			    }
            			    break;
            			}
                    } else {
                        loadLoginRoom();
                    }
//                    System.out.println("5. adding displaying new room");
                } catch (TimeOutException e) {
                    msgTitle = "Request Timeout";
                    msgText ="Your Move request timed out...logging you off";
                    logoff = true;
                } catch (RuntimeException e) {
                    msgTitle = "Error";
                    msgText =  "Error while processing your move request, logging you off...";
                    logoff = true;
                    throw e;
                } finally {
                    doneLoading();
                    if (logoff) {
                        ca.getModel().logoff();
                        Common.showMessage(msgTitle, msgText, ca.getMainFrame());
                    }
                }
            }
        }, "Room Loader");

        loadIntermission();
    }

    private void createNewRoom(RoomModel room) {
        if (currentRoom != null && !currentRoom.leave()) {
            return;
        }

        // find a new room
        currentRoomType = room.getType().intValue();
//System.out.println("currentRoomType: " + currentRoomType + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("2. switching rooms");
        switch (currentRoomType) {
        case ContestConstants.MODERATED_CHAT_ROOM:
        case ContestConstants.LOBBY_ROOM:
            currentRoom = lobbyRoom;
            break;
        case ContestConstants.SPECTATOR_ROOM:
        case ContestConstants.PRACTICE_SPECTATOR_ROOM:
            currentRoom = spectatorRoom;
            break;
        case ContestConstants.ADMIN_ROOM:
        case ContestConstants.CONTEST_ROOM:
        case ContestConstants.CODER_ROOM:
        case ContestConstants.PRACTICE_CODER_ROOM:
            if (room.getRoundModel().getRoundType().isLongRound()) {
                currentRoom = longCoderRoom;
            } else {
                currentRoom = coderRoom;
            }
            break;
        case ContestConstants.TEAM_ADMIN_ROOM:
        case ContestConstants.TEAM_CODER_ROOM:
        case ContestConstants.TEAM_PRACTICE_CODER_ROOM:
            currentRoom = teamCoderRoom;
            break;
        default:
            currentRoom = loginRoom;
            break;
        }

//        System.out.println("3. creating room incase it doesn't exist");
        // get rid of any excess baggage if there is any
        if (currentRoom == null) {
            currentRoom = createRoom(room);
        }
        currentRoom.clear();
    }

    private void loadLoginRoom() {
        if (currentRoom != null && !currentRoom.leave()) {
            return;
        }
        currentRoom = loginRoom;
        currentRoom.clear();
        showCurrentRoom();
    }

    /**
     * The loadIntermission method is a thread responsible for switching two
     * rooms.
     *
     *  1. removes the current/old room panel from the applet frame.
     *  2. loads the temporary intermission screen.
     *  3. spawns off a new thread to load the new room.
     *
     *  NOTE: this method should only be called from the loadNewRoom() method, since
     *        certain variables must be set in order for the entire process to work.
     */
    private void loadIntermission() {
        try {
            Thread intermission = new Thread(new Runnable() {
                public void run() {
                    try {
                        navPanel.setVisible(false);   // hide the top panel
                        MoveFocus.moveFocus(ca.getMainFrame());
                        mainPanel.removeAll();
                        mainPanel.add(is.getIntermissionPanel(intermissionType));
                        mainPanel.revalidate();
                        mainPanel.repaint();
                        loadRoom.setDaemon(true);
                        loadRoom.start();
                    } catch (RuntimeException e) {
                        doneLoading();
                        throw e;
                    }
                }
            }, "Intermission");
            intermission.start();
        } catch (RuntimeException e) {
            doneLoading();
            throw e;
        }
    }

    /**
     * Retrieve a handle to the currently active room type/object.
     *
     * @return RoomModule      Returns a handle to the active room.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public RoomModule getCurrentRoom()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (currentRoom);
    }

    /**
     * Retrieve the current room ID.
     *
     * @return int      Returns an integer representing the current active room.
     */
    public int getCurrentRoomType() {
        return (currentRoomType);
    }

    public long getCurrentRoomID() {
        return currentRoomID;
    }

    public boolean leave() {
        return currentRoom.leave();
    }


    private void moveToRoom(int roomType, long roomID) throws TimeOutException {
        ca.getModel().move(roomType, roomID);
    }


    public void watch(final long roomID) {
        RoomModel room = ca.getModel().getRoom(roomID);
        if (!room.hasWatchView()) {
            try {
                room = ca.getModel().watch(roomID);
                final RoomInfoFrame rif = new RoomInfoFrame(ca, room);
                room.setWatchView(rif);
                rif.getFrame().addEventListener("window", new UIWindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        try {
                            ca.getModel().unwatch(roomID);
                        } finally {
                            if(rif.isEnabled())
                                rif.unsetModel();
                        }
                    }
                });
            } catch (TimeOutException e) {
                ca.popup(ContestConstants.LABEL, "Watch Request", "Your watch request timed out.");
                return;
            }
        }
        ((RoomInfoFrame) room.getWatchView()).showFrame(true);
    }
    
    private void showCurrentRoom() {
        menuPanel.setMenuConfig(currentRoomType, currentRoom.roomModel);
        JPanel r = currentRoom.reload();
                   
        //better safe than sorry
        currentRoom.setConnectionStatus(true);
                    
        MoveFocus.moveFocus(mainPanel);
        mainPanel.removeAll();
        r.revalidate();
        r.repaint();
        mainPanel.add(r);
        mainPanel.revalidate();
        mainPanel.repaint();

        if (currentRoomType != ContestConstants.LOGIN_ROOM) {
            navPanel.setVisible(true); // show the top panel if not Login screen
        }

        currentRoom.resetFocus();      // more focus fixes
        currentRoom.enter();           // focus it
    }

    public void setCurrentRoom(RoomModel room) {
        createNewRoom(room);
        room.setCurrentRoomView(currentRoom);

        showCurrentRoom();
    }
    
    public void addRoom(RoomModel room) {}
    public void removeRoom(RoomModel room) {}
    public void clearRooms() {}
}
