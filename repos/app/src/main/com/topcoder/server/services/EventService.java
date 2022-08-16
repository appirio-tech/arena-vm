/**
 * Class EventService
 *
 * Author: Hao Kung
 *
 * Description: This class will contain all the static methods for use by
 * anyone who wants to send user/room/contest/global events
 */

package com.topcoder.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.ActionEvent;
import com.topcoder.server.common.ChatEvent;
import com.topcoder.server.common.CompileEvent;
import com.topcoder.server.common.ResponseEvent;
import com.topcoder.server.common.Submission;
import com.topcoder.server.common.TCEvent;
import com.topcoder.server.common.TestEvent;
import com.topcoder.server.processor.Processor;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.messaging.TopicMessagePublisher;
import com.topcoder.shared.util.DBMS;


public final class EventService {

    /**
     * Category for logging.
     */
    private static final Logger s_trace = Logger.getLogger(EventService.class);

    /**
     * Stores all the settings for running the processors.
     */
    private static ResourceBundle s_settings;
    private static boolean s_sendReplay = false;

    private static TopicMessagePublisher m_eventPublisher;
    private static final Object s_requestLock = new Object();
    private static LinkedList s_requestQueue = new LinkedList();

    /* Static initialization block for the topic stuff */
    static {
        s_trace.debug("Initializing EventService...");
        try {
            m_eventPublisher = new TopicMessagePublisher(ApplicationServer.JMS_FACTORY, DBMS.EVENT_TOPIC);
            m_eventPublisher.setPersistent(true);
            m_eventPublisher.setFaultTolerant(false);
        } catch (Exception e) {
            s_trace.fatal("Failed to initialize EventService", e);
        }

        try {
            s_settings = ResourceBundle.getBundle("EventService");

            // replay only if set to true;
            s_sendReplay = s_settings.getString("EventService.sendReplay").trim().equals("true");
            s_trace.debug("ReplayMode = " + s_sendReplay);

        } catch (MissingResourceException mre) {
            s_trace.error("Failed to load EventService Settings", mre);
        }


        EventRunner runner = new EventRunner();
        Thread runnerThread = new Thread(runner, "EventService.EventRunner");
        runnerThread.start();

        s_trace.debug("Initialized EventService");
    }

    private EventService() {
    }

    public static LinkedList getAndClearEventQueue() {
        synchronized (s_requestLock) {
            while (s_requestQueue.size() < 1) {
                try {
                    s_requestLock.wait();
                } catch (InterruptedException ie) {
                }
            }
            LinkedList result = s_requestQueue;
            s_requestQueue = new LinkedList();
            return result;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendGlobalEvent(TCEvent event) {
        sendGlobalEvent(event, false);
    }

    private static void sendGlobalEvent(TCEvent event, boolean noProcessor) {
        // don't send replay unless you have to
        if (event.isReplayEvent() && !s_sendReplay) {
            return;
        }

        s_trace.debug("Sending: " + event);

        if (noProcessor) {
            try {
                LinkedList events = new LinkedList();
                events.add(event);
                //s_trace.debug("GT: Before sendGlobalEvent publish");
                m_eventPublisher.pubMessage(s_eventProps, events);
                //s_trace.debug("GT: After sendGlobalEvent publish");
            } catch (Exception e) {
                s_trace.error("Failed to publishMessage", e);
            }
        } else {
            synchronized (s_requestLock) {
                s_requestQueue.add(event);
                s_requestLock.notifyAll();
            }
        }
    }

    public static final String EVENT_KEY = "EVENT_SERVICE_ID";

    private static final HashMap s_eventProps = new HashMap();
    private static final int s_serverID;

    static {
        s_eventProps.put(TCEvent.TYPE, new Integer(TCEvent.LIST_TYPE));
        // TODO use a better mechanism to generate a unique ID for the server.
        Random random = new Random();
        s_serverID = random.nextInt();
        s_eventProps.put(EVENT_KEY, new Integer(s_serverID));
        s_trace.info("SERVER-ID="+s_serverID);
    }

    public static int getServerID() {
        return s_serverID;
    }

    public static boolean handleGlobalSend(LinkedList list) {
        try {
            // Send local events first.
            for (Iterator i = list.iterator(); i.hasNext();) {
                Processor.dispatchEvent((TCEvent) i.next());
            }
            Processor.flushPendingEvents();
            //s_trace.debug("GT: Before handleGlobalSend publish");
            return m_eventPublisher.pubMessage(s_eventProps, list);
        } catch (Exception e) {
            s_trace.error("Failed to send list of events", e);
        }
        return false;
    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    public static boolean handleGlobalEvent(TCEvent event)
    {
        s_trace.info("sendGlobalEvent");
        try
        {
            HashMap props = new HashMap();
            props.put(TCEvent.TYPE, new Integer(event.getEventType()));
            return m_eventPublisher.pubMessage(props, event);
        }
        catch(Exception e)
        {
            s_trace.error("Failed to sendGlobalEvent", e);
        }
        return false;
    }

    public static void sendMenuUpdate( String contestName, String newStatus )
    {
        Response response = ResponseProcessor.getContestMenuResponse( contestName, newStatus );
        ResponseEvent event = new ResponseEvent( TCEvent.ALL_TARGET, -1, response );
        sendGlobalEvent( event );
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendCompileResults(Submission submission) {
        s_trace.debug("sendCompileResults");
        CompileEvent event = new CompileEvent(submission);
        sendGlobalEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendTestResults(int userID, int type, Object data, long submitTime) {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("sendTestResults type = " + type + " User = " + userID);
        }
        int testType;
        if (type == ServicesConstants.CHALLENGE_TEST_ACTION)	// If it's a challenge, parse the object as a challenge
        {
            testType = ContestConstants.CHALLENGE;
        } else if (type == ServicesConstants.USER_TEST_ACTION) // If it's simply the coder's own testing, indicate this
        {
            testType = ContestConstants.TEST;
        } else if (type == ServicesConstants.PRACTICE_TEST_ACTION) {
            testType = ContestConstants.PRACTICE_SYSTEM_TEST;
        } else if (type == ServicesConstants.AUTO_TEST_ACTION) {
            testType = ContestConstants.AUTO_SYSTEST;
        } else {
            s_trace.error("Unknown test type: " + type);
            return;
        }
        TestEvent event = new TestEvent(testType, userID, data, submitTime);

        sendGlobalEvent(event);
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public static void sendPracticeSystemTestStart(int userID, Object data) {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("sendPracticeSystemTestStart type = " + ServicesConstants.PRACTICE_TEST_ACTION + " User = " + userID);
        }
        int testType = ContestConstants.PRACTICE_SYSTEM_TEST;
        TestEvent event = new TestEvent(testType, userID, data, -1);
        sendGlobalEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendAction(int userID, int action) {
        s_trace.debug("sendAction");
        ActionEvent event = new ActionEvent(userID, action);
        sendGlobalEvent(event);
    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    public static void sendResponseToRoom(int roomID, Response response)
    {
        s_trace.info("sendResponseToRoom");
        ResponseEvent event = new ResponseEvent(TCEvent.ROOM_TARGET, roomID, response);
        sendGlobalEvent(event);
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendResponseToRoom(int roomID, ArrayList responses) {
        s_trace.debug("sendResponseToRoom");
        ResponseEvent event = new ResponseEvent(TCEvent.ROOM_TARGET, roomID, responses);
        sendGlobalEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendAdminMessage(int chatStyle, String message) {
        s_trace.debug("sendAdminMessage");
        ChatEvent event = new ChatEvent(TCEvent.ADMIN_TARGET, 0, chatStyle, message, ContestConstants.GLOBAL_CHAT_SCOPE);
        sendGlobalEvent(event);
    }
    
    public static void sendAdminMessage(int chatStyle, String message, int userID) {
        s_trace.debug("sendAdminMessage");
        ChatEvent event = new ChatEvent(TCEvent.ADMIN_TARGET, 0, chatStyle, message, ContestConstants.GLOBAL_CHAT_SCOPE);
        event.setCoderID(userID);
        //event.setUserMessage(true);
        sendGlobalEvent(event);
    }
    
    public static void sendAdminMessage(int chatStyle, String message, int fromUserID, int roomID, String prefix, int rating) {
        s_trace.debug("sendAdminMessage");
        ChatEvent event = new ChatEvent(TCEvent.ADMIN_TARGET, roomID, chatStyle, message, ContestConstants.GLOBAL_CHAT_SCOPE);
        event.setCoderID(fromUserID);
        //event.setUserMessage(true);
        event.setPrefix(prefix);
        event.setUserRating(rating);
        sendGlobalEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendUserMessage(int fromUserID, int roomID, int roundID, int userID, int chatStyle, String message) {
        s_trace.debug("sendUserMessage");
        sendUserMessage(fromUserID, roomID, roundID, userID, chatStyle, message, null, 0);
    }
    
    public static void sendUserMessage(int fromUserID, int roomID, int roundID, int userID, int chatStyle, String message, String prefix, int rating) {
        s_trace.debug("sendUserMessage");
        ChatEvent event = new ChatEvent(TCEvent.USER_TARGET, userID, chatStyle, message, ContestConstants.GLOBAL_CHAT_SCOPE);
        event.setCoderID(fromUserID);
        event.setRoomID(roomID);
        event.setRoundID(roundID);
        event.setUserMessage(true);
        event.setPrefix(prefix);
        event.setUserRating(rating);
        sendGlobalEvent(event);
    }
    public static void sendReplayEvent(TCEvent event) {
        // Only send out replay events if you aren't in replay mode
        if (!Processor.inReplayMode() && s_sendReplay) {
            event.setReplayEvent(true);
            sendGlobalEvent(event);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendRoomSystemMessage(int roomID, String message) {
        s_trace.debug("sendRoomSystemMessage");
        ChatEvent event = new ChatEvent(TCEvent.ROOM_TARGET, roomID, ContestConstants.SYSTEM_CHAT, message,
                ContestConstants.GLOBAL_CHAT_SCOPE);
        sendGlobalEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendRoomUserMessage(int coderID, int roomID, int roundID, int chatStyle, String message) {
        sendRoomUserMessage(coderID, roomID, roundID, chatStyle, message, null, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendRoomUserMessage(int coderID, int roomID, int roundID, int chatStyle, String message, String prefix, int rating) {
        s_trace.debug("sendRoomUserMessage");
        ChatEvent event = new ChatEvent(TCEvent.ROOM_TARGET, roomID, chatStyle, message, ContestConstants.GLOBAL_CHAT_SCOPE);
        event.setCoderID(coderID);
        event.setRoomID(roomID);
        event.setRoundID(roundID);
        event.setPrefix(prefix);
        event.setUserRating(rating);
        event.setUserMessage(true);
        sendGlobalEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void sendRoomUserMessage(int coderID, int roomID, int roundID, int chatStyle, String message, String prefix, int rating,
            int teamID) {
        s_trace.debug("sendRoomUserMessage");
        ChatEvent event = new ChatEvent(TCEvent.ROOM_TARGET, roomID, chatStyle, message, ContestConstants.TEAM_CHAT_SCOPE);
        event.setCoderID(coderID);
        event.setRoomID(roomID);
        event.setRoundID(roundID);
        event.setTeamID(teamID);
        event.setPrefix(prefix);
        event.setUserRating(rating);
        event.setUserMessage(true);
        sendGlobalEvent(event);
    }

    /**
     * This method is called from the ejbRemove method, which is responsible for
     * cleaning up any open connections or free up any other system resources
     * that are no longer needed.
     */
    ////////////////////////////////////////////////////////////////////////////////
    /*
    public static void cleanUp()
    {
        s_trace.debug("EventService.cleanUp");
        if(m_eventPublisher != null)
        {
            m_eventPublisher.close();
            m_eventPublisher = null;
        }
    }
    */

}
