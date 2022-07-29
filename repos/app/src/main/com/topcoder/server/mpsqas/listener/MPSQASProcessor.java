package com.topcoder.server.mpsqas.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.topcoder.netCommon.mpsqas.communication.message.Message;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.message.Room;
import com.topcoder.server.ejb.MPSQASServices.MPSQASServices;
import com.topcoder.server.ejb.ProblemServices.ProblemServices;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.server.mpsqas.listener.impl.MPSQASProcessorPeer;
import com.topcoder.shared.util.SimpleResourceBundle;

/**
 * Processes connections and messages received from clients.
 *
 * @author Logan Hanks
 */
public class MPSQASProcessor
        implements ProcessorInterface {

    private Logger logger;
    private ListenerInterface listener;
    private MPSQASServices services;
    private ProblemServices problemServices;
    private HashMap peers = new HashMap();
    private HashMap queues = new HashMap();
//    private MPSQASProcessorQueue queue = new MPSQASProcessorQueue();
    private static SimpleResourceBundle s_processorSettings = SimpleResourceBundle.getBundle("MPSQASProcessor");

    public MPSQASProcessor(MPSQASServices services, ProblemServices problemServices) {
        logger = Logger.getLogger(getClass().getName());
        logger.info("MPSQASProcessor initializing...");

        this.services = services;
        this.problemServices = problemServices;
        //queue.start();
        //startQueue();

    }
//Each client get their own queue now so no one was to wait for anyone else, and their stuff is still all sequential
/*    private void startQueue() {
        int threadCount = 3;
        try {
            threadCount = s_processorSettings.getInt("mpsqas.processor.threads");
        } catch (Exception e) {
            logger.info(e);
        }
        for (int i = 0; i < threadCount; i++) {
            MPSQASProcessorQueue runner = new MPSQASProcessorQueue();
            Thread requestThread = new Thread(runner, "MPSQASProcessorQueue." + i);
            requestThread.start();
        }
    }
    */

    public void setListener(ListenerInterface listener) {
        this.listener = listener;
    }

    public void start() {
        logger.info("starting");
    }

    public void stop() {
        logger.info("stopping");
    }

    public void newConnection(int connection_id, String remoteIP) {
        logger.info("new connection (" + connection_id + "): " + remoteIP);
        peers.put(new Integer(connection_id), new MPSQASProcessorPeer(connection_id, this, services, problemServices));
        MPSQASProcessorQueue runner = new MPSQASProcessorQueue();
        Thread requestThread = new Thread(runner, "MPSQASProcessorQueue." + connection_id);
        requestThread.start();
        queues.put(new Integer(connection_id),runner);
    }

    public void receive(int connection_id, Object request) {
        logger.info("received object (" + connection_id + "): " + request);

        MPSQASProcessorPeer peer = (MPSQASProcessorPeer) peers.get(new Integer(connection_id));

        if (request instanceof MessageProcessor) {
            MessageProcessor impl = (MessageProcessor) request;
            MPSQASProcessorQueue queue = (MPSQASProcessorQueue)queues.get(new Integer(connection_id));
            queue.add(impl, peer);
        } else {
            logger.error("received unimplemented request or non-message request");
            peer.sendErrorMessage("Not yet implemented");
            listener.shutdown(connection_id);
        }
    }

    public void lostConnection(int connection_id) {
        logger.info("lost connection " + connection_id);
        MPSQASProcessorQueue queue = (MPSQASProcessorQueue)queues.get(new Integer(connection_id));
        queue.connectionLost();
        queues.remove(new Integer(connection_id));
        peers.remove(new Integer(connection_id));
    }
    
    public void lostConnectionTemporarily(int connection_id) {
        lostConnection(connection_id);
    }

    public void sendMessage(int id, Message message) {
        listener.send(id, message);
    }

    public List getPeersInRoom(Room room) {
        Iterator iter = peers.entrySet().iterator();
        MPSQASProcessorPeer peer;
        ArrayList al_peers = new ArrayList();

        while (iter.hasNext()) {
            peer = (MPSQASProcessorPeer) ((Map.Entry) iter.next()).getValue();
            if (peer.inRoom(room)) {
                al_peers.add(peer);
            }
        }
        return al_peers;
    }

    public List getPeers() {
        Iterator iter = peers.entrySet().iterator();
        ArrayList al_peers = new ArrayList();

        while (iter.hasNext()) {
            al_peers.add(((Map.Entry) iter.next()).getValue());
        }
        return al_peers;
    }
}
