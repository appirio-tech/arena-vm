package com.topcoder.server.mpsqas.listener;

import com.topcoder.server.mpsqas.listener.impl.MPSQASProcessorPeer;

import java.lang.reflect.*;
import java.util.*;

import com.topcoder.server.mpsqas.broadcast.*;
import com.topcoder.server.mpsqas.room.*;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import org.apache.log4j.Logger;

/**
 * Processes broadcasts.  Given a broadcast, determines which
 * connections should receive it, builds the response, and sends it.
 *
 * @author mitalub
 */
public class BroadcastProcessor {

    private MPSQASProcessor processor;
    private static Logger logger = Logger.getLogger(BroadcastProcessor.class);

    /**
     * Stores the listener's processor.
     */
    public BroadcastProcessor(MPSQASProcessor processor) {
        this.processor = processor;
    }

    /**
     * Takes a broadcast and calls the processing method for the broadcast
     * using reflection.  The processing method name must be: <br>
     *
     * <code>process</code> + (the name of the broadcast class) <br>
     *
     * So, for the example, if the broadcast is a ProblemModifiedBroadcast,
     * the process method signature must be: <br>
     *
     * <code>public void processProblemModifiedBroadcast(ProblemModifiedBroadcast
     *                                            broadcast); </code>.
     */
    public synchronized void processBroadcast(Broadcast broadcast) {
        logger.info("Processing broadcast: " + broadcast);

        //call the process method for the broadcast, using reflection.
        String processMethodName = "";
        try {
            Class[] classes = {broadcast.getClass()};
            processMethodName = classes[0].getName();

            //prepend "process" and remove the package.
            processMethodName = "process" + processMethodName.substring(
                    processMethodName.lastIndexOf(".") + 1);

            //invoke the method
            Method method = this.getClass().getMethod(processMethodName, classes);
            method.invoke(this, new Object[]{broadcast});
        } catch (IllegalAccessException iae) {
            logger.error("Broadcast processing method (" + processMethodName +
                    ") must be public.", iae);
        } catch (NoSuchMethodException nsme) {
            logger.error("No such broadcast processing method: " + processMethodName,
                    nsme);
        } catch (Exception e) {
            logger.error("Error processing broadcast.", e);
        }
    }

    /**
     * Sends message to a List of Peers.
     */
    private void sendResponse(List peers, Message response) {
        for (int i = 0; i < peers.size(); i++) {
            ((Peer) peers.get(i)).sendMessage(response);
        }
    }

    /**
     * Determines the type of correspondence (problem or component) and
     * gets all users in the appropriate room and sends the response.
     */
    public void processNewCorrespondenceBroadcast(NewCorrespondenceBroadcast
            broadcast) {

        Message response = new NewCorrespondenceResponse(
                broadcast.getCorrespondence());
        final int id = broadcast.getId();
        int type = broadcast.getType();
        List peers = new ArrayList();

        if (type == NewCorrespondenceBroadcast.PROBLEM_CORRESPONDENCE) {
            peers = processor.getPeersInRoom(new ViewProblemRoom() {
                public int getProblemId() {
                    return id;
                }

                public void enter(Peer peer) {
                }
            });
        } else if (type == NewCorrespondenceBroadcast.COMPONENT_CORRESPONDENCE) {
            peers = processor.getPeersInRoom(new ViewComponentRoom() {
                public int getComponentId() {
                    return id;
                }

                public void enter(Peer peer) {
                }
            });
        }

        sendResponse(peers, response);
    }

    /**
     * Determines the users in the problem room and sends a problem modified
     * response.
     */
    public void processProblemModifiedBroadcast(ProblemModifiedBroadcast
            broadcast) {
        Message response = new ProblemModifiedResponse(broadcast.getHandle());
        final int problemId = broadcast.getProblemId();
        List peers = processor.getPeersInRoom(new ViewProblemRoom() {
            public int getProblemId() {
                return problemId;
            }

            public void enter(Peer peer) {
            }
        });

        //remove the peer with the specified connection id, he is the one
        //who modified and shouldn't get the broadcast.
        for (int i = 0; i < peers.size(); i++) {
            if (((MPSQASProcessorPeer) peers.get(i)).getId()
                    == broadcast.getConnectionId()) {
                peers.remove(i--);
            }
        }

        sendResponse(peers, response);
    }

    /**
     * Determines the users in the problem room and sends a problem modified
     * response.
     */
    public void processComponentModifiedBroadcast(ComponentModifiedBroadcast
            broadcast) {
        Message response = new ProblemModifiedResponse(broadcast.getHandle());
        final int componentId = broadcast.getComponentId();
        List peers = processor.getPeersInRoom(new ViewComponentRoom() {
            public int getComponentId() {
                return componentId;
            }

            public void enter(Peer peer) {
            }
        });

        //remove the peer with the specified connection id, he is the one
        //who modified and shouldn't get the broadcast.
        for (int i = 0; i < peers.size(); i++) {
            if (((MPSQASProcessorPeer) peers.get(i)).getId()
                    == broadcast.getConnectionId()) {
                peers.remove(i--);
            }
        }

        sendResponse(peers, response);
    }
}
