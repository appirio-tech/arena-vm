package com.topcoder.server.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.request.BaseRequest;
import com.topcoder.netCommon.contestantMessages.request.ChatRequest;
import com.topcoder.netCommon.contestantMessages.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.request.MoveRequest;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateMenuResponse;
import com.topcoder.netCommon.contestantMessages.response.LoginResponse;
import com.topcoder.netCommon.contestantMessages.response.RoomInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.UnsynchronizeResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateChatResponse;
import com.topcoder.server.listener.monitor.ArenaMonitor;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.util.StoppableThread;

/**
 * The class that can be used a simple applet echo processor. You can "login", "chat" and "logout".
 *
 * @author  Timur Zambalayev
 */
public final class AppletEchoProcessor implements ArenaProcessor, StoppableThread.Client {

    private final Set connections = new HashSet();
    private final Map stateMap = new HashMap();
    private final StoppableThread thread = new StoppableThread(this, "AppletEchoListener");

    private ListenerInterface controller;
    private ArenaMonitor monitor;

    /**
     * Creates a new applet echo processor.
     */
    public AppletEchoProcessor() {
    }

    public void setListener(ListenerInterface controller) {
        this.controller = controller;
    }

    public void setArenaMonitor(ArenaMonitor monitor) {
        this.monitor = monitor;
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void newConnection(int id, String remoteIP) {
        Integer i = new Integer(id);
        assertTrue(connections.add(i));
        stateMap.put(i, new Integer(0));
    }

    private void send(int id, ArrayList list) {
        controller.send(id, list);
    }

    private BaseResponse getUnsync(BaseRequest request) {
        ArrayList unsyncData = new ArrayList();
        unsyncData.add(new Integer(request.getRequestType()));
        BaseResponse unsync = new UnsynchronizeResponse(request.getRequestType());
        return unsync;
    }

    public void receive(int id, Object object) {
        Integer conn_id = new Integer(id);
        assertTrue(connections.contains(conn_id));
        int state = ((Integer) stateMap.get(conn_id)).intValue();
        BaseRequest request = (BaseRequest) object;
        int type = request.getRequestType();
        System.out.println("type: " + type + " " + request);
        switch (state) {
        case 0:
            {
                if (!(request instanceof LoginRequest)) {
                    throw new RuntimeException("bad type: " + type + " " + state);
                }
                LoginRequest lr = (LoginRequest) request;
                String username = lr.getUserID();
                monitor.setUsername(id, username);
                System.out.println("LOGIN: " + username + " " + lr.getPassword());
                ArrayList list = new ArrayList();

                BaseResponse response = new LoginResponse(true);
                list.add(response);

                ArrayList names = new ArrayList();
                names.add("Practice Room");
                response = new CreateMenuResponse(ContestConstants.PRACTICE_ROOM_MENU, names, new ArrayList(), new ArrayList());
                list.add(response);

                list.add(getUnsync(request));
                send(id, list);
                state = 1;
            }
            break;
        case 1:
            {
                if (!(request instanceof MoveRequest)) {
                    throw new RuntimeException("bad type: " + type + " " + state);
                }
                MoveRequest mr = (MoveRequest) request;
                System.out.println("MOVE: " + mr.getMoveType() + " " + mr.getRoomID());
                ArrayList list = new ArrayList();
                BaseResponse response = new RoomInfoResponse(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM, "Lobby", CoreServices.getLobbyStatus());
                list.add(response);
                list.add(getUnsync(request));
                send(id, list);
                state = 2;
            }
            break;
        case 2:
            {
                switch (type) {
                case ContestConstants.CHAT:
                    ChatRequest cr = (ChatRequest) request;
                    String text = cr.getMsg();
                    monitor.chat(12, "Jim", text);
                    systemChat(id, text);
                    break;
                case ContestConstants.LOGOUT:
                    controller.shutdown(id);
                    state = 3;
                    break;
                case ContestConstants.ENTER:
                    break;
                default:
                    throw new RuntimeException("bad type: " + type + " " + state);
                }
            }
            break;
        default:
            throw new RuntimeException("not implemented: " + state);
        }
        stateMap.put(conn_id, new Integer(state));
    }

    private void systemChat(int id, String text) {
        ArrayList list = new ArrayList();
        BaseResponse response = new UpdateChatResponse(ContestConstants.SYSTEM_CHAT, text, -1, -1, ContestConstants.GLOBAL_CHAT_SCOPE);
        list.add(response);
        send(id, list);
    }

    public void lostConnection(int id) {
        Integer i = new Integer(id);
        assertTrue(connections.remove(i));
        stateMap.remove(i);
    }
    
    public void lostConnectionTemporarily(int connection_id) {
        lostConnection(connection_id);
    }

    private void assertTrue(boolean b) {
        if (!b) {
            throw new RuntimeException("assertion failed!");
        }
    }

    /**
     * Returns the name of this class.
     *
     * @return  the name of this class.
     */
    public String toString() {
        return "AppletEchoProcessor";
    }

    public void cycle() throws InterruptedException {
        Thread.sleep(10000);
    }

}
