package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.netCommon.mpsqas.communication.message.Message;
import com.topcoder.netCommon.mpsqas.communication.message.Room;
import com.topcoder.netCommon.mpsqas.communication.message.NewStatusMessage;
import com.topcoder.server.mpsqas.room.*;
import com.topcoder.server.mpsqas.listener.MPSQASProcessor;
import com.topcoder.server.ejb.MPSQASServices.MPSQASServices;
import com.topcoder.server.ejb.ProblemServices.ProblemServices;

import java.util.Stack;
import java.security.Key;

import org.apache.log4j.Logger;

/**
 * Represents a connection to an applet as a <code>Peer</code>.
 *
 * @author Logan Hanks
 */
public class MPSQASProcessorPeer
        implements Peer {

    private static Logger logger = Logger.getLogger(MPSQASProcessorPeer.class);

    public static final int NEW_PROBLEM = Integer.MIN_VALUE;

    private int id;
    private int userId = -1;
    private MPSQASProcessor processor;
    private MPSQASServices services;
    private ProblemServices problemServices;
    private int currentApplicationId = -1;
    private int currentProblemId = -1;
    private int currentComponentId = -1;
    private int currentWebServiceId = -1;
    private int currentContestId = -1;
    private int currentUserId = -1;
    private boolean admin;
    private boolean loggedIn;
    private boolean writer;
    private boolean tester;
    private String username = "<unauthorized user>";
    private Stack roomStack = new Stack();
    private int roomStackIndex = -1;
    private boolean entering = false;
    private Key encryptKey;

    /**
     * Constructs a new peer for a new client connection.
     *
     * @param id        id of the client connection
     * @param processor message processor for the client
     */
    public MPSQASProcessorPeer(int id, MPSQASProcessor processor, MPSQASServices services, ProblemServices problemServices) {
        this.id = id;
        this.processor = processor;
        this.services = services;
        this.problemServices = problemServices;
    }

    /**
     * @return the connection id this peer represents
     */
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEncryptKey(Key key) {
        encryptKey = key;
    }

    public Key getEncryptKey() {
        return encryptKey;
    }

    /**
     * @return the message processor that processes messages received from this peer
     */
    public MPSQASProcessor getProcessor() {
        return processor;
    }

    public MPSQASServices getServices() {
        return services;
    }

    public ProblemServices getProblemServices() {
        return problemServices;
    }

    public void sendMessage(Message message) {
        Logger.getLogger(getClass()).debug("Sending message: " + message);
        processor.sendMessage(id, message);
    }

    public void sendMessage(String s) {
        sendMessage(new NewStatusMessage(s));
    }

    public void sendErrorMessage(String s, Exception e) {
        sendMessage(new NewStatusMessage(true, s, e));
        if (entering) {
            moveToNewRoom(new FoyerMoveRequestImpl());
        }
    }

    public void sendErrorMessage(String s) {
        sendMessage(new NewStatusMessage(true, s));
        if (entering) {
            moveToNewRoom(new FoyerMoveRequestImpl());
        }
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void moveToNewRoom(Room room) {
        roomStackIndex++;
        if (roomStack.size() > roomStackIndex) {
            int pops = roomStack.size() - roomStackIndex;
            for (int i = 0; i < pops; i++) roomStack.pop();

/*  Iterator caused concurrent modification exception
            roomStack.removeAll(
                    roomStack.subList(roomStackIndex, roomStack.size()));
*/
        }
        roomStack.add(room);
        roomStackIndex = roomStack.size() - 1;
        entering = true;
        room.enter(this);
        entering = false;
    }

    public void moveToRoom(int distance) {
        roomStackIndex += distance;
        roomStackIndex = Math.min(roomStack.size() - 1, roomStackIndex);
        roomStackIndex = Math.max(0, roomStackIndex);
        if (roomStack.size() > roomStackIndex) {
            Room room = (Room) roomStack.get(roomStackIndex);
            entering = true;
            room.enter(this);
            entering = false;
        }
    }

    public boolean isWriter() {
        return writer;
    }

    public void setWriter(boolean writer) {
        this.writer = writer;
    }

    public boolean isTester() {
        return tester;
    }

    public void setTester(boolean tester) {
        this.tester = tester;
    }

    public Room getCurrentRoom() {
        return (Room) roomStack.get(roomStackIndex);
    }

    public int getCurrentApplicationId() {
        return currentApplicationId;
    }

    public void setCurrentApplicationId(int currentApplicationId) {
        this.currentApplicationId = currentApplicationId;
    }

    public int getCurrentWebServiceId() {
        return currentWebServiceId;
    }

    public void setCurrentWebServiceId(int currentWebServiceId) {
        this.currentWebServiceId = currentWebServiceId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentContestId(int currentContestId) {
        this.currentContestId = currentContestId;
    }

    public int getCurrentContestId() {
        return currentContestId;
    }

    public void setCurrentProblemId(int currentProblemId) {
        this.currentProblemId = currentProblemId;
    }

    public int getCurrentProblemId() {
        return currentProblemId;
    }

    public void setCurrentComponentId(int currentComponentId) {
        this.currentComponentId = currentComponentId;
    }

    public int getCurrentComponentId() {
        return currentComponentId;
    }

    /**
     * Returns true if room represents the current room of the user.
     */
    public boolean inRoom(Room room) {
        Room currentRoom = getCurrentRoom();

        return
                (room instanceof ApplicationRoom
                && currentRoom instanceof ApplicationRoom
                && getCurrentApplicationId() ==
                ((ApplicationRoom) room).getApplicationType())
                ||
                (room instanceof ViewApplicationRoom
                && currentRoom instanceof ViewApplicationRoom
                && getCurrentApplicationId() ==
                ((ViewApplicationRoom) room).getApplicationId())
                ||
                (room instanceof ViewContestRoom
                && currentRoom instanceof ViewContestRoom
                && getCurrentContestId() ==
                ((ViewContestRoom) room).getContestId())
                ||
                (room instanceof ViewComponentRoom
                && currentRoom instanceof ViewComponentRoom
                && getCurrentComponentId() ==
                ((ViewComponentRoom) room).getComponentId())
                ||
                //Here, let a component room = view single problem room if the
                //component ids are equal:
                (room instanceof ViewComponentRoom
                && currentRoom instanceof ViewProblemRoom
                && getCurrentComponentId() ==
                ((ViewComponentRoom) room).getComponentId())
                ||
                (room instanceof ViewProblemRoom
                && currentRoom instanceof ViewProblemRoom
                && getCurrentProblemId() ==
                ((ViewProblemRoom) room).getProblemId())
                ||
                //We'll be loose here and let the peer be in the room if the room
                //is a view problem room and the user is in a team problem room,
                //assuming same problem ids.
                (room instanceof ViewProblemRoom
                && currentRoom instanceof ViewTeamProblemRoom
                && getCurrentProblemId() ==
                ((ViewProblemRoom) room).getProblemId())
                ||
                (room instanceof ViewTeamProblemRoom
                && currentRoom instanceof ViewTeamProblemRoom
                && getCurrentProblemId() ==
                ((ViewTeamProblemRoom) room).getProblemId())
                ||
                (room instanceof ViewLongProblemRoom
                && currentRoom instanceof ViewLongProblemRoom
                && getCurrentProblemId() ==
                ((ViewLongProblemRoom) room).getProblemId())
                ||
                (room instanceof ViewUserRoom
                && currentRoom instanceof ViewUserRoom
                && getCurrentUserId() ==
                ((ViewUserRoom) room).getUserId())
                ||
                (room instanceof ViewWebServiceRoom
                && currentRoom instanceof ViewWebServiceRoom
                && getCurrentWebServiceId() ==
                ((ViewWebServiceRoom) room).getWebServiceId())
                ||
                (room instanceof FoyerRoom
                && currentRoom instanceof FoyerRoom)
                ||
                (room instanceof MainApplicationRoom
                && currentRoom instanceof MainApplicationRoom)
                ||
                (room instanceof MainContestRoom
                && currentRoom instanceof MainContestRoom)
                ||
                (room instanceof MainProblemRoom
                && currentRoom instanceof MainProblemRoom)
                ||
                (room instanceof MainTeamProblemRoom
                && currentRoom instanceof MainTeamProblemRoom)
                ||
                (room instanceof MainLongProblemRoom
                && currentRoom instanceof MainLongProblemRoom)
                ||
                (room instanceof MainUserRoom
                && currentRoom instanceof MainUserRoom)
                ||
                (room instanceof PendingApprovalRoom
                && currentRoom instanceof PendingApprovalRoom)
                ||
                (room instanceof TeamPendingApprovalRoom
                && currentRoom instanceof TeamPendingApprovalRoom)
                ||
                (room instanceof LongPendingApprovalRoom
                && currentRoom instanceof LongPendingApprovalRoom);
    }

    public String toString() {
        return "MPSQASProcessorPeer[id=" + id + ", userId=" + userId +
                ", username=" + username + ", loggedIn=" + loggedIn +
                ", admin=" + admin + ", writer=" + writer + ", tester=" +
                tester + "]";
    }
}
