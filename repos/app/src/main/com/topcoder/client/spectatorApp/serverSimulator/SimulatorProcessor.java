/**
 * SimulatorProcessor.java
 *
 * Description:		Note: all sorts of race conditions can occur in this class
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.serverSimulator;

//package com.topcoder.server.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.security.Key;

import org.apache.log4j.Category;

import com.topcoder.netCommon.contestantMessages.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.request.ExchangeKeyRequest;
import com.topcoder.netCommon.contestantMessages.response.ExchangeKeyResponse;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;
import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.MessagePacket;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;
import com.topcoder.shared.netCommon.messages.spectator.SpectatorLoginResult;

public class SimulatorProcessor implements ProcessorInterface {

    static final Category cat = Category.getInstance(SimulatorProcessor.class.getName());

	Contest contest;
    ListenerInterface list;
    ArrayList<Integer> connections = new ArrayList();
    ArrayList events = new ArrayList();
    boolean firstTime = true;
    Key key;

    public SimulatorProcessor() {
//       contest = new TeamContest(this);
//		contest = new Contest(this);
		contest = new EightPersonContest(this);
    }

    public void setListener(ListenerInterface listener) {
        list = listener;
    }

    public void start() {
    }

    public void newConnection(int connection_id, String remoteIP) {
        firstTime = true;
        connections.add(connection_id);
    }

    public void receive(int connection_id, Object request) {

        MessagePacket p = (MessagePacket) request;
        cat.info("(Received) " + p);
        List l = p.getMessages();
        for (Iterator itr = l.iterator(); itr.hasNext();) {
            receive(itr.next());
        }

    }

    public void receive(Object request) {

        cat.info("(Receive) " + request);

        if (request instanceof ExchangeKeyRequest) {
            MessageEncryptionHandler handler = new MessageEncryptionHandler();
            handler.setRequestKey(((ExchangeKeyRequest) request).getKey());
            sendEvent(new ExchangeKeyResponse(handler.generateReplyKey()));
            key = handler.getFinalKey();
        } else if (request instanceof LoginRequest) {
            LoginRequest r = (LoginRequest) request;
            sendEvent(new SpectatorLoginResult(r.getUserID(), r.getPassword(), true, ""), false);
            sendEvent(new PhaseChange(contest.currentPhase, contest.currentTime), false);

            // Assume it's a watch room
            //m.add(new DefineRoom(contest.room, contest.coders, contest.problems));
            if (firstTime) {
                MessagePacket m = new MessagePacket();
                firstTime = false;
                Object[] e = events.toArray();
                for (int x = 0; x < e.length; x++) System.out.println(e[x]);
                //for(int x=0;x<e.length;x++) if(e[x] instanceof ProblemEvent || e[x] instanceof ProblemResult || e[x] instanceof TimerUpdate) m.add((Message)e[x]);
                for (int x = 0; x < e.length; x++) m.add((Message) e[x]);
                sendEvent(m, false);
            }
        }

    }

    public void sendEvent(Object f) {
        sendEvent(f, true);
    }

    public void sendEvent(Object f, boolean store) {
        if (store) {
            cat.info("(Stored) " + f);
            events.add(f);
        }
        for(int connID : connections) {
            if (connID >= 0) {
                if (f instanceof MessagePacket) {
                    cat.info("(Send) " + f);
                    list.send(connID, f);
                } else {
                    MessagePacket packet = new MessagePacket();
                    packet.add((Message) f);
                    cat.info("(Send) " + f);
                    list.send(connID, packet);
                }
            }
        }
    }

    public void lostConnection(int connection_id) {
        connections.remove(connection_id);
    }

    public void lostConnectionTemporarily(int connection_id) {
        connections.remove(connection_id);
    }

    public void stop() {
    }

}


/* @(#)SimulatorProcessor.java */
