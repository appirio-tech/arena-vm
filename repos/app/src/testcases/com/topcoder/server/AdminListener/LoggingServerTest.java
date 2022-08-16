/*
* Author: Michael Cervantes (emcee)
* Date: Jun 7, 2002
* Time: 10:31:30 PM
*
* Modified by: John Waymouth (coderlemming)
* Modified on: Jun 29, 2002
*   Added tests for high logging load.
*/

package com.topcoder.server.AdminListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootCategory;

import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.server.util.logging.net.LoggingMessage;
import com.topcoder.server.util.logging.net.StreamID;
import com.topcoder.server.util.logging.net.TCLoggingEvent;
import com.topcoder.server.util.logging.net.TCSocketAppender;


public class LoggingServerTest extends TestCase {

    int port = 5030;

    Logger testLogger = Logger.getLogger(LoggingServerTest.class);

    public LoggingServerTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(LoggingServerTest.class);
    }


    private static final long TIMEOUT = 1000;
    private static final long CONNECTTIMEOUT = 1000;

    TCLinkedQueue logQueue;
    private TCSocketAppender appender;
    private static final int RECONNECT_DELAY = 5;

    // load constants
    private static final int NUM_STREAMS = 10;
    private static final int NUM_SUBSCRIBERS = 10;
    private static final int NUM_MESSAGES = 10;

    static class QueueItem {

        TCLoggingEvent event;
        int connectionID;
        long timeReceived;
        StreamID streamID;

        public QueueItem(TCLoggingEvent event, int connectionID, StreamID streamID) {
            this.event = event;
            this.connectionID = connectionID;
            this.timeReceived = System.currentTimeMillis();
            this.streamID = streamID;
        }
    }

    LoggingServer loggingServer;
    ListenerInterface sender = new ListenerInterface() {
        public void start() throws IOException {
        }

        public void stop() {
        }

        public void send(int connection_id, Object response) {
            logQueue.put(new QueueItem(((LoggingMessage) response).getEvent(), connection_id, ((LoggingMessage) response).getStreamID()));
        }

        public void shutdown(int connection_id) {
        }

        public void shutdown(int connection_id, boolean notifyProcessor) {
        }

        public void banIP(String ipAddress) {
        }

        public int getConnectionsSize() {
            return 0;
        }

        public int getResponseQueueSize() {
            return 0;
        }

        public int getInTrafficSize() {
            return 0;
        }

        public int getOutTrafficSize() {
            return 0;
        }

        public int getMaxConnectionId() {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getMinConnectionId() {
            // TODO Auto-generated method stub
            return 0;
        }
    };

    Logger logger;
    StreamID streamID;

    public void setUp() {
        logQueue = new TCLinkedQueue();
        loggingServer = new LoggingServer(sender, port);
        loggingServer.start();
    }

    private static String FIRST_MESSAGE = "First Message";
    private static String SECOND_MESSAGE = "Second Message";
    private static String THIRD_MESSAGE = "Third Message";
    private static String FOURTH_MESSAGE = "Fourth Message";
    private static String FIFTH_MESSAGE = "Fifth Message";


    public void testLoggingServer() {
        logger = new RootCategory((Level) Priority.DEBUG);
        appender = new TCSocketAppender();
        appender.setIdentifier("LoggingServer Test Stream");
        appender.setOwner(System.getProperty("user.name"));
        appender.setReconnectionDelay(RECONNECT_DELAY);
        appender.setRemoteHost("127.0.0.1");
        appender.setPort(port);
        appender.activateOptions();
        streamID = appender.getStreamID();
        streamID.setHost("127.0.0.1");
        new Hierarchy(logger);
        logger.addAppender(appender);

        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collection streams = loggingServer.getSupportedStreams();
        assertTrue("Missing logging stream", streams.contains(streamID));
        loggingServer.addSubscriber(1, streamID);
        logger.info(FIRST_MESSAGE);
        QueueItem item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue");
        }
        assertTrue("Timed out waiting for logging event", item != null);
        assertTrue(item.connectionID == 1);
        assertEquals(item.event.getLevel().toInt(), Level.INFO_INT);
        assertEquals(FIRST_MESSAGE, item.event.getMessage());

        loggingServer.addSubscriber(2, streamID);

        // test catch up
        item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue");
        }
        assertTrue("Timed out waiting for logging event", item != null);
        assertTrue(item.connectionID == 2);
        assertEquals(item.event.getLevel().toInt(), Level.INFO_INT);
        assertEquals(FIRST_MESSAGE, item.event.getMessage());


        logger.error(SECOND_MESSAGE);
        int idsum = 0;
        item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue");
        }
        assertTrue("Timed out waiting for logging event", item != null);
        idsum += item.connectionID;
        assertEquals(item.event.getLevel().toInt(), Level.ERROR_INT);
        assertEquals(SECOND_MESSAGE, item.event.getMessage());

        item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue");
        }
        assertTrue("Timed out waiting for logging event", item != null);
        idsum += item.connectionID;
        assertEquals(item.event.getLevel().toInt(), Level.ERROR_INT);
        assertEquals(SECOND_MESSAGE, item.event.getMessage());

        assertEquals("Expected events on connections 1 and 2", idsum, 3);

        logger.removeAppender(appender);
        appender.close();

        appender = new TCSocketAppender(streamID);
        appender.setRemoteHost("127.0.0.1");
        appender.setPort(port);
        appender.setReconnectionDelay(RECONNECT_DELAY);
        appender.activateOptions();
        logger.addAppender(appender);

        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        streams = loggingServer.getSupportedStreams();
        assertTrue("Missing logging stream after reconnect", streams.contains(streamID));

        logger.debug(THIRD_MESSAGE);

        idsum = 0;
        item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue after reconnect");
        }
        assertTrue("Timed out waiting for logging event after reconnect", item != null);
        idsum += item.connectionID;
        assertEquals(item.event.getLevel().toInt(), Level.DEBUG_INT);
        assertEquals(THIRD_MESSAGE, item.event.getMessage());

        item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue after reconnect");
        }
        assertTrue("Timed out waiting for logging event after reconnect", item != null);
        idsum += item.connectionID;
        assertEquals(item.event.getLevel().toInt(), Level.DEBUG_INT);
        assertEquals(THIRD_MESSAGE, item.event.getMessage());

        assertEquals("Expected events on connections 1 and 2 after reconnect", idsum, 3);


        loggingServer.removeSubscriber(2, streamID);
        logger.warn(FOURTH_MESSAGE);
        idsum = 0;
        item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue after first unsubscribe");
        }
        assertTrue("Timed out waiting for logging event after first unsubscribe", item != null);
        idsum += item.connectionID;
        assertEquals(item.event.getLevel().toInt(), Level.WARN_INT);
        assertEquals(FOURTH_MESSAGE, item.event.getMessage());
        assertEquals(item.connectionID, 1);

        item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue after reconnect");
        }
        assertNull("Unexpected event after first unsubscribe", item);

        loggingServer.removeSubscriber(1);
        logger.info(FIFTH_MESSAGE);
        item = null;
        try {
            item = (QueueItem) logQueue.poll(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted while polling queue after first unsubscribe");
        }
        assertNull("Unexpected event after second unsubscribe", item);
    }

    public void testManyStreamsOneSubscriber() {
        long timeSent;

        // Load test #1: subscribe a user to many streams at once, then send
        // some log messages
        // Ensure: All messages arrive unchanged and in order.

        logger = new RootCategory((Level) Priority.DEBUG);
        new Hierarchy(logger);

        TCSocketAppender appender[] = new TCSocketAppender[NUM_STREAMS];
        StreamID ids[] = new StreamID[NUM_STREAMS];

        for (int i = 0; i < NUM_STREAMS; i++) {
            ids[i] = new StreamID("" + i, "127.0.0.1", System.getProperty("user.name"), new Date(System.currentTimeMillis()));

            appender[i] = new TCSocketAppender(ids[i]);
            appender[i].setIdentifier("LoggingServer Test Stream #" + i);
            appender[i].setOwner(System.getProperty("user.name"));
            appender[i].setReconnectionDelay(RECONNECT_DELAY);
            appender[i].setRemoteHost("127.0.0.1");
            appender[i].setPort(port);
            appender[i].activateOptions();

            logger.addAppender(appender[i]);
        }

        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collection streams = loggingServer.getSupportedStreams();
        for (int i = 0; i < NUM_STREAMS; i++)
            assertTrue("Stream #" + i + " missing", streams.contains(ids[i]));

        for (int i = 0; i < NUM_STREAMS; i++)
            loggingServer.addSubscriber(1, ids[i]);

//	testLogger.info("Sending log messages at " + System.currentTimeMillis());

        // stores the highest message received for each stream, to check order
        int received[] = new int[NUM_STREAMS];
        int numReceived = 0;

        Arrays.fill(received, 0);

        QueueItem item;

        // this penalizes the loggingServer for time spent logging.
        timeSent = System.currentTimeMillis();
        for (int i = 0; i < NUM_MESSAGES; i++)
            logger.info("" + i);

        long endTime = timeSent + TIMEOUT;
        testLogger.info("timeSent = " + timeSent + " endTime = " + endTime + " time is " + System.currentTimeMillis());
        while (numReceived < NUM_STREAMS * NUM_MESSAGES) {
            if (endTime <= System.currentTimeMillis())
                fail("Test timed out, received " + numReceived + " messages");
            item = null;
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime > 0) {
                    item = (QueueItem) logQueue.poll(waitTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail("Interrupted while polling queue");
            }
            if (item == null)
                continue;
            assertTrue(item.connectionID == 1);
            assertEquals(item.event.getLevel().toInt(), Level.INFO_INT);
            int msgNum = Integer.parseInt(item.event.getMessage());
            assertTrue(msgNum < NUM_MESSAGES);
            int streamNum = Integer.parseInt(item.streamID.getName());
            assertTrue(streamNum < NUM_STREAMS);
            assertEquals("Message arrived out of order", received[streamNum], msgNum);
            assertTrue("Message delivery time exceeded timeout", (item.timeReceived - timeSent) <= TIMEOUT);
//		testLogger.info("Message " + msgNum + " from stream " + streamNum + " arrived at " + item.timeReceived);
            received[streamNum]++;
            numReceived++;
        }

        for (int i = 0; i < NUM_STREAMS; i++)
            appender[i].close();

    }

    public void testOneStreamManySubscribers() {
        long timeSent;

        // Load test #2: subscribe many users to one stream, then send
        // some log messages
        // Ensure: All messages arrive unchanged and in order.

        logger = new RootCategory((Level) Priority.DEBUG);
        new Hierarchy(logger);

        TCSocketAppender appender;

        streamID = new StreamID("Test Logging Stream", "127.0.0.1", System.getProperty("user.name"), new Date(System.currentTimeMillis()));

        appender = new TCSocketAppender(streamID);
        appender.setIdentifier("LoggingServer Test Stream");
        appender.setOwner(System.getProperty("user.name"));
        appender.setReconnectionDelay(RECONNECT_DELAY);
        appender.setRemoteHost("127.0.0.1");
        appender.setPort(port);
        appender.activateOptions();

        logger.addAppender(appender);

        try {
            Thread.sleep(CONNECTTIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collection streams = loggingServer.getSupportedStreams();
        assertTrue("Stream missing", streams.contains(streamID));

        for (int i = 0; i < NUM_SUBSCRIBERS; i++)
            loggingServer.addSubscriber(i, streamID);

        timeSent = System.currentTimeMillis();


        // stores the highest message received for each subscriber, to check order
        int received[] = new int[NUM_STREAMS];
        int numReceived = 0;

        Arrays.fill(received, 0);

        QueueItem item;

        // this penalizes the loggingServer for time spent logging.
        for (int i = 0; i < NUM_MESSAGES; i++)
            logger.info("" + i);

        long endTime = timeSent + TIMEOUT;
        while (numReceived < NUM_SUBSCRIBERS * NUM_MESSAGES) {
            if (endTime <= System.currentTimeMillis())
                fail("Test timed out, received " + numReceived + " messages");
            item = null;
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime > 0)
                    item = (QueueItem) logQueue.poll(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail("Interrupted while polling queue");
            }
            if (item == null)
                continue;

            assertEquals(item.event.getLevel().toInt(), Level.INFO_INT);
            int msgNum = Integer.parseInt(item.event.getMessage());
            assertTrue(msgNum < NUM_MESSAGES);
            assertTrue(item.connectionID < NUM_SUBSCRIBERS);
            assertEquals("Message arrived out of order", received[item.connectionID], msgNum);
            assertTrue("Message delivery time exceeded timeout", (item.timeReceived - timeSent) <= TIMEOUT);
            received[item.connectionID]++;
            numReceived++;
        }

        appender.close();
    }

    public void testManyStreamsManySubscribers() {
        long timeSent;

        // Load test #3: subscribe many users to many streams, then send
        // some log messages.
        // Ensures: All messages arreive unchanged, in order, to everyone.

        logger = new RootCategory((Level) Priority.DEBUG);
        new Hierarchy(logger);

        TCSocketAppender appender[] = new TCSocketAppender[NUM_STREAMS];
        StreamID ids[] = new StreamID[NUM_STREAMS];

        for (int i = 0; i < NUM_STREAMS; i++) {
            ids[i] = new StreamID("" + i, "127.0.0.1", System.getProperty("user.name"), new Date(System.currentTimeMillis()));

            appender[i] = new TCSocketAppender(ids[i]);
            appender[i].setIdentifier("LoggingServer Test Stream #" + i);
            appender[i].setOwner(System.getProperty("user.name"));
            appender[i].setReconnectionDelay(RECONNECT_DELAY);
            appender[i].setRemoteHost("127.0.0.1");
            appender[i].setPort(port);
            appender[i].activateOptions();

            logger.addAppender(appender[i]);
        }

        try {
            Thread.sleep(CONNECTTIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Collection streams = loggingServer.getSupportedStreams();
        for (int i = 0; i < NUM_STREAMS; i++)
            assertTrue("Stream #" + i + " missing", streams.contains(ids[i]));

        for (int i = 0; i < NUM_STREAMS; i++)
            for (int j = 0; j < NUM_SUBSCRIBERS; j++)
                loggingServer.addSubscriber(j, ids[i]);

        int nstreams = NUM_STREAMS;
        int received[][] = new int[NUM_SUBSCRIBERS][nstreams];
        // received[i][j] corresponds to the number of logging messages received
        // so far by connection i, from stream j.
        int numReceived = 0;

        for (int i = 0; i < NUM_SUBSCRIBERS; i++)
            Arrays.fill(received[i], 0);

        QueueItem item;


        // this penalizes the loggingServer for time spent logging.
        timeSent = System.currentTimeMillis();

        for (int i = 0; i < NUM_MESSAGES; i++)
            logger.info("" + i);

        long endTime = timeSent + TIMEOUT;
        while (numReceived < NUM_MESSAGES * NUM_STREAMS * NUM_SUBSCRIBERS) {
            if (endTime <= System.currentTimeMillis())
                fail("Test timed out, received " + numReceived + " messages");
            item = null;
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime > 0)
                    item = (QueueItem) logQueue.poll(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail("Interrupted while polling queue");
            }
            if (item == null)
                continue;

            assertEquals(item.event.getLevel().toInt(), Level.INFO_INT);
            int msgNum = Integer.parseInt(item.event.getMessage());
            assertTrue(msgNum < NUM_MESSAGES);
            int streamNum = Integer.parseInt(item.streamID.getName());
            assertTrue(streamNum < NUM_STREAMS);
            assertTrue(item.connectionID < NUM_SUBSCRIBERS);
            assertEquals("Message arrived out of order", received[item.connectionID][streamNum], msgNum);
            assertTrue("Message delivery time exceeded timeout", (item.timeReceived - timeSent) <= TIMEOUT);
            received[item.connectionID][streamNum]++;
            numReceived++;
        }

        for (int i = 0; i < NUM_STREAMS; i++)
            appender[i].close();

    }

    protected void tearDown() throws Exception {
        loggingServer.shutdown();
    }

}
