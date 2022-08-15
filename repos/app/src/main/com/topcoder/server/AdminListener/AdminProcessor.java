package com.topcoder.server.AdminListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.topcoder.server.AdminListener.request.BackEndChangeRoundRequest;
import com.topcoder.server.AdminListener.request.BackEndLoginRequest;
import com.topcoder.server.AdminListener.request.BackEndRefreshAccessRequest;
import com.topcoder.server.AdminListener.request.BackEndRoundAccessRequest;
import com.topcoder.server.AdminListener.request.ChangeRoundRequest;
import com.topcoder.server.AdminListener.request.ClientCommandRequest;
import com.topcoder.server.AdminListener.request.ContestMonitorRequest;
import com.topcoder.server.AdminListener.request.GetLoggingStreamsRequest;
import com.topcoder.server.AdminListener.request.IncomingMessage;
import com.topcoder.server.AdminListener.request.LoggingStreamSubscribeRequest;
import com.topcoder.server.AdminListener.request.LoggingStreamUnsubscribeRequest;
import com.topcoder.server.AdminListener.request.LoginRequest;
import com.topcoder.server.AdminListener.request.MonitorRequest;
import com.topcoder.server.AdminListener.request.MonitorSetupRequest;
import com.topcoder.server.AdminListener.request.ProcessedAtBackEndRequest;
import com.topcoder.server.AdminListener.request.RefreshAccessRequest;
import com.topcoder.server.AdminListener.request.RoundAccessRequest;
import com.topcoder.server.AdminListener.request.SecurityCheck;
import com.topcoder.server.AdminListener.request.ServerReplySecurityCheck;
import com.topcoder.server.AdminListener.request.SetAdminForwardingAddressRequest;
import com.topcoder.server.AdminListener.request.SetForwardingAddressRequest;
import com.topcoder.server.AdminListener.request.TestConnection;
import com.topcoder.server.AdminListener.response.BackEndResponse;
import com.topcoder.server.AdminListener.response.ContestManagementAck;
import com.topcoder.server.AdminListener.response.ContestServerResponse;
import com.topcoder.server.AdminListener.response.GetLoggingStreamsAck;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.VettedServerResponse;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.server.listener.monitor.ChatItem;
import com.topcoder.server.listener.monitor.MonitorStatsItem;
import com.topcoder.server.listener.monitor.QuestionItem;
import com.topcoder.server.util.QueueReaderThread;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class was modified for AdminTool 2.0.
 * <p>Updated receivedClientMessage() method to  to pass the TCSubject in the
 * ChangeRoundRequest and the RefreshAccessRequest requests.
 *
 * @author TCDEVELOPER
 */
public class AdminProcessor implements ProcessorInterface, QueueReaderThread.Client {

    private AdminListener listener;
    private LoggingServer loggingServer;
    private QueueReaderThread reader;
    private ContestManagementProcessor contestManagementProcessor;
    private TCLinkedQueue incomingMessageQueue;
    private TCLinkedQueue backEndQueue;
    private Map validCommandSources;
    private static final Logger log = Logger.getLogger(AdminProcessor.class);
    private Object loginLock;

    
    AdminProcessor(AdminListener listener, TCLinkedQueue incomingMessageQueue, TCLinkedQueue backEndQueue, LoggingServer loggingServer, ContestManagementProcessor contestManagementProcessor) {
        this.listener = listener;
        this.incomingMessageQueue = incomingMessageQueue;
        this.contestManagementProcessor = contestManagementProcessor;
        this.loggingServer = loggingServer;
        this.backEndQueue = backEndQueue;
        reader = new QueueReaderThread(incomingMessageQueue, this, "AdminProcessor.QueueReader");
        validCommandSources = new HashMap();
        loginLock = new Object();
    }

    // Functions supporting record of logged in clients together with their user IDs.
    // Note: validCommandSources is used by AdminServicesBean to run security checks on server
    // responses.
    private void addLoggedInClient(int connectionId, long userId) {
        synchronized (loginLock) {
            validCommandSources.put(new Integer(connectionId), new Long(userId));
        }
    }

    private void removeLoggedInClient(int connectionId) {
        synchronized (loginLock) {
            validCommandSources.remove(new Integer(connectionId));
            AdminForwardingThread aft = (AdminForwardingThread) forwardingThreads.remove(new Integer(connectionId));
            if (aft != null) {
                aft.stop();
            }
        }
    }

    private boolean isClientLoggedIn(int connectionId) {
        synchronized (loginLock) {
            return validCommandSources.containsKey(new Integer(connectionId));
        }
    }

    private Long getConnectionUserId(int connectionId) {
        synchronized (loginLock) {
            return (Long) validCommandSources.get(new Integer(connectionId));
        }
    }

    private List tempSources = new LinkedList();
    
    private void broadcastToLoggedInClients(Object response) {
        // Leaves us open to potentially sending to inactive connection IDs
        // This is better than holding this lock for the whole time.
        synchronized (loginLock) {
            tempSources.clear();
            tempSources.addAll(validCommandSources.keySet());
        }
        for (Iterator it = tempSources.iterator(); it.hasNext();) {
            Integer id = (Integer) it.next();
            listener.send(id.intValue(), response);
        }
    }

    // Processor interface
    /**
     * Sets the listener that should be used with this processor.  Not used since
     * we want the listener to be an AdminListener, not a generic listener (has functions
     * like clientBroadcast() which we may want to use) plus it shouldn't need to change
     * after processor creation in any case.
     *
     * @param   listener    the listener.
     */
    public void setListener(ListenerInterface listener) {
    }

    /**
     * Starts the processor.
     */
    public void start() {
        reader.start();
    }

    /**
     * Notifies the processor of the new connection that has a unique connection ID. The connection was
     * made from the given remove IP address.
     *
     * @param   connection_id       the unique connection ID.
     * @param   remoteIP            the remote IP address in the textual form from which the connection was made.
     */
    private boolean forwarding = false;
    private HashMap forwardingThreads = new HashMap();
    private String host;
    private int port;

    public void newConnection(int connection_id, String remoteIP) {
        if (log.isDebugEnabled())
            log.debug("Got new connection " + connection_id);
        if (forwarding) {
            try {
                log.debug("putting forwarder for " + connection_id);
                forwardingThreads.put(new Integer(connection_id), new AdminForwardingThread(host, port));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notifies the processor that a new request was received from the given connection ID, which can
     * represent either the contest listener server or an admin monitor client.  The request is simply
     * put on the incoming message queue.  The processor's queue reader thread will later read the
     * object off the queue and call receivedQueueItem() below.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     * @param   obj                 the received request.
     */
    public void receive(int connection_id, Object obj) {
/*
        noisy!
        if (log.isDebugEnabled())
            log.debug("Placing message from " + connection_id + " on the incoming queue");
*/
        // This method must enable the incoming message queue to determine accurately
        // where the object came from.  We do this by slapping a sender ID on it.
        incomingMessageQueue.put(new IncomingMessage(connection_id, obj));
    }

    /**
     * Notifies the processor that we lost connection with the client represents by the given connection ID.
     *
     * @param   connection_id       the connection ID that represents a socket connection with the given client.
     */
    public void lostConnection(int connection_id) {
        if (AdminConstants.isClient(connection_id)) {
            removeLoggedInClient(connection_id);
            loggingServer.removeSubscriber(connection_id);
        }
    }
    
    public void lostConnectionTemporarily(int connection_id) {
        if (AdminConstants.isClient(connection_id)) {
            removeLoggedInClient(connection_id);
            loggingServer.removeSubscriber(connection_id);
        }
    }

    /**
     * Stops the processor.
     */
    public void stop() {
        reader.stop();
    }

    // Queue reader thread client interface
    public void receivedQueueItem(Object item) {
        if (!(item instanceof IncomingMessage)) {
            log.error("Urk!  Invalid message type found in queue: " + item.getClass().toString());
        }
        IncomingMessage message = ((IncomingMessage) item);
        int sender = message.getSenderId();
        if (AdminConstants.isBackEnd(sender)) {
            receivedBackEndMessage(message);
        } else if (AdminConstants.isClient(sender)) {
            receivedClientMessage(message);
            if (forwarding) {
                log.debug("forwarding: " + message.getRequestObject());
                Object req = message.getRequestObject();
                if (!(req instanceof SetForwardingAddressRequest || req instanceof SetAdminForwardingAddressRequest)) {
                    //don't want any infinite loops, so only forward non-forwarding requests
                    AdminForwardingThread aft = (AdminForwardingThread) forwardingThreads.get(new Integer(sender));
                    if (aft != null) {
                        aft.forward(req);
                    }
                }
            }
        } else if (AdminConstants.isContestListener(sender)) {
            receivedContestServerMessage(message);
        } else {
            log.error("Received message from unknown connection " + sender);
        }
    }

    private void receivedBackEndMessage(IncomingMessage message) {
        Object messageObject = message.getRequestObject();
        if (log.isDebugEnabled()) {
            log.debug("Received message from back end: " + messageObject.getClass().toString());
        }
        // Handle client commands that passed the security check
        if (messageObject instanceof ClientCommandRequest) {
            ClientCommandRequest clientRequest = (ClientCommandRequest) messageObject;
            Object request = clientRequest.getRequestObject();
            if (request instanceof ProcessedAtBackEndRequest) {
                backEndQueue.put(clientRequest);
                if (log.isDebugEnabled())
                    log.debug("Monitor request passed along to back end: " + request.getClass().toString());
            } else if (request instanceof ContestMonitorRequest) {
                listener.send(AdminConstants.CONTEST_LISTENER_CONNECTION_ID, clientRequest);
                if (log.isDebugEnabled())
                    log.debug("Monitor request passed along to contest server: " + request.getClass().toString());
            } else if (request instanceof LoggingStreamSubscribeRequest) {
                processLoggingStreamSubscribe(clientRequest.getSenderId(), (LoggingStreamSubscribeRequest) request);
            } else if (request instanceof LoggingStreamUnsubscribeRequest) {
                processLoggingStreamUnsubscribe(clientRequest.getSenderId(), (LoggingStreamUnsubscribeRequest) request);
            } else if (request instanceof GetLoggingStreamsRequest) {
                log.debug("request instanceof GetLoggingStreamsRequest");
                processGetLoggingStreams(clientRequest.getSenderId());
            } else if (request instanceof SetAdminForwardingAddressRequest) {
                SetAdminForwardingAddressRequest setAddress = (SetAdminForwardingAddressRequest) request;
                String address = setAddress.getAddress();
                int idx = address.indexOf(":");
                if (idx == -1)
                    forwarding = false;
                else if (!setAddress.isDone()) {
                    host = address.substring(0, idx);
                    try {
                        port = Integer.parseInt(address.substring(idx + 1));
                        log.debug("forwarding on");
                        forwarding = true;
                    } catch (Exception e) {
                        forwarding = false;
                    }
                }
            } else {
                log.error("Unrecognized ClientCommandRequest type: " + request.getClass().toString());
            }
            return;
        }

        // Handle server replies that passed the security check
        if (messageObject instanceof VettedServerResponse) {
            try {
                VettedServerResponse response = (VettedServerResponse) messageObject;
                List recipients = response.getAllowedRecipients();
                Iterator it = recipients.iterator();
                while (it.hasNext()) {
                    int recipientId = ((Integer) it.next()).intValue();
                    listener.send(recipientId, response.getResponseObject());
                }
            } catch (Exception e) {
                log.error("Error processing vetted server response", e);
            }
            return;
        }

        // Handle back end responses
        if (messageObject instanceof BackEndResponse) {
            BackEndResponse backEndResponse = (BackEndResponse) messageObject;
            int recipient = backEndResponse.getRecipientId();
            Object response = backEndResponse.getResponseObject();

            // If this is a successful login we need to record it in the set of client
            // connections to accept commands from, as well as pass on the first response message
            if (response instanceof LoginResponse) {
                log.info("Finishing log in process for connection " + recipient);
                LoginResponse resp = (LoginResponse) response;
                boolean succeeded = resp.getSucceeded();
                synchronized (loginLock) {
                    if (isClientLoggedIn(recipient)) {
                        log.error("Login response given to a client already logged in");
                        return;
                    }
                    if (succeeded) {
                        addLoggedInClient(recipient, resp.getUserId()); 
                    }
                }

                listener.send(recipient, response);

                if (succeeded) {
                    log.info("Log in succeeded, sending other responses for connsection " + recipient);
                    listener.send(AdminConstants.CONTEST_LISTENER_CONNECTION_ID, new ClientCommandRequest(recipient, new MonitorSetupRequest()));

                    // TODO (emcee) - check for CM authorization (though this data isn't all that sensitive)
                    // dpecora - you could examine the login response to see if contest management
                    // is part of the allowed function set.  However, this doesn't cover the case where
                    // contest management rights are granted later on in the session.
                    ContestManagementAck contestManagementSetup =
                            contestManagementProcessor.getContestManagementSetupMessage();
                    listener.send(recipient, contestManagementSetup);
                }

                log.info("All log in responses sent for connection " + recipient);
                return;
            }

            // Not a login response.  Deliver the message normally.
            listener.send(recipient, response);
            return;
        }

        log.error("Unknown incoming back end message type: " + messageObject.getClass().toString());
    }

    /**
     * This method was modified for AdminTool 2.0 to pass the TCSubject from
     * the ChangeRoundRequest and the RefreshAccessRequest to the respective
     * back end request.
     * @param message from client
     */
    private void receivedClientMessage(IncomingMessage message) {
        Object request = message.getRequestObject();
        int sender = message.getSenderId();

        // Disregard test connection message
        if (request instanceof TestConnection) {
            return;
        }

        // Otherwise, it had better be a monitor request
        if (!(request instanceof MonitorRequest)) {
            log.error("Received invalid message type from client " + sender + ": " + request.getClass().toString());
            return;
        }

        boolean isLoggedIn;
        Long userId = null;

        synchronized (loginLock) {
            isLoggedIn = isClientLoggedIn(sender);
            if (isLoggedIn) {
                userId = getConnectionUserId(sender);
            }
        }

        // Handle logins
        if (request instanceof LoginRequest) {
            log.debug("Front end login request received:" + request);
            if (isLoggedIn) {
                log.error("Redundant login request received from connection " + sender);
                return;
            }
            LoginRequest lr = (LoginRequest) request;
            backEndQueue.put(new BackEndLoginRequest(message.getSenderId(), lr.getHandle(), lr.getPassword()));
            return;
        }

        // Not a login message, so client should already be logged in.
        if (!isLoggedIn) {
            log.error("Command request received from a client at connection " + sender + " who is not logged in!");
            return;
        }
        try {
            // Change round requests and refresh access information requests can always be executed.  Feed them
            // immediately to the back end.
            if (request instanceof RoundAccessRequest) {
                backEndQueue.put(new BackEndRoundAccessRequest(sender, userId.longValue()));
                return;
            } else if (request instanceof ChangeRoundRequest) {
                ChangeRoundRequest req = (ChangeRoundRequest)request;
                BackEndChangeRoundRequest bereq = new BackEndChangeRoundRequest(
                    sender, userId.longValue(),req.getRoundId());
                backEndQueue.put(bereq);
                return;
            } else if (request instanceof RefreshAccessRequest) {
                RefreshAccessRequest req = (RefreshAccessRequest)request;
                BackEndRefreshAccessRequest bereq = new BackEndRefreshAccessRequest(
                        sender, userId.longValue(),req.getRoundId());
                backEndQueue.put(bereq);
                return;
            }

            // Otherwise, stick on the back end queue for a security check.
            backEndQueue.put(new SecurityCheck(sender, userId.longValue(), request));
        } catch (Exception e) {
            log.error("Error delivering message of type " + request.getClass().toString() +
                    " from client " + sender + " to back end", e);
        }
    }

    private void receivedContestServerMessage(IncomingMessage message) {
        Object messageObject = message.getRequestObject();
        if (!(messageObject instanceof ContestServerResponse)) {
            log.error("Received invalid message type from contest server: " + messageObject.getClass().toString());
            return;
        }
        ContestServerResponse cr = (ContestServerResponse) messageObject;
        int recipient = cr.getRecipientId();
        Object response = cr.getResponseObject();

        if (log.isDebugEnabled() && !(response instanceof MonitorStatsItem)) {
            log.debug("Received message from contest server: " + response.getClass().toString());
        }

        // Screen chat items for who can receive them
        if (response instanceof ChatItem || response instanceof QuestionItem) {
            backEndQueue.put(new ServerReplySecurityCheck(recipient, response, validCommandSources));
            return;
        }

        if (recipient == AdminConstants.RECIPIENT_ALL) {
            broadcastToLoggedInClients(response);
        } else {
            listener.send(recipient, response);
        }
    }

    private void processLoggingStreamSubscribe(int connection_id, LoggingStreamSubscribeRequest request) {
        if (log.isDebugEnabled())
            log.debug("Processing logging stream subscribe request from " + connection_id);
        loggingServer.addSubscriber(connection_id, request.getStreamID());
    }

    private void processLoggingStreamUnsubscribe(int connection_id, LoggingStreamUnsubscribeRequest request) {
        if (log.isDebugEnabled())
            log.debug("Processing logging stream unsubscribe request from " + connection_id);
        loggingServer.removeSubscriber(connection_id, request.getStreamID());
    }

    private void processGetLoggingStreams(int connection_id) {
        if (log.isDebugEnabled())
            log.debug("Processing get logging streams request from " + connection_id);
        GetLoggingStreamsAck response = new GetLoggingStreamsAck(
                loggingServer.getSupportedStreams()
        );
        log.debug("Stream #: " + response.getStreams().size());
        listener.send(connection_id, response);
    }
}

