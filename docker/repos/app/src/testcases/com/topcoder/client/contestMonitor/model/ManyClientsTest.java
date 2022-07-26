/*
* Test many clients connected at once, see if the system can still handle commands in a reasonable time.
* This simulates an actual real-world load situation, in which many clients are connected, and a few will
* run some commands in a given second.
* Author: John Waymouth
* Date: Jul 5, 2002
* Time: 2:43:03 AM
*/
package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.AdminListener.request.ChangeRoundRequest;
import com.topcoder.server.AdminListener.request.GetAllContestsRequest;
import com.topcoder.server.AdminListener.request.GetLoggingStreamsRequest;
import com.topcoder.server.AdminListener.request.LoginRequest;
import com.topcoder.server.AdminListener.request.RefreshAccessRequest;
import com.topcoder.server.AdminListener.response.ChangeRoundResponse;
import com.topcoder.server.AdminListener.response.GetAllContestsAck;
import com.topcoder.server.AdminListener.response.GetLoggingStreamsAck;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.RefreshAccessResponse;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class ManyClientsTest extends TestCase implements MonitorTestClient.Client {

    private static final String handle = "td";
    private static final String password = "foo";
    private static final String host = "172.16.20.30";
    private static final int port = 5017;
    private static final long TIMEOUT = 60 * 1000;
    private static final int NUM_CONNECTIONS = 30;
    private static final int testRound = 4225;

    /** A few requests to throw at the server */
    private static final Object requests[] = new Object[]{new RefreshAccessRequest(new Integer(testRound)),
                                                          new ChangeRoundRequest(testRound), new GetAllContestsRequest(),
                                                          new GetLoggingStreamsRequest(),
                                                          new RefreshAccessRequest(new Integer(testRound))};
    private static final Class responses[] = new Class[]{RefreshAccessResponse.class, ChangeRoundResponse.class,
                                                         GetAllContestsAck.class, GetLoggingStreamsAck.class,
                                                         RefreshAccessResponse.class};

    private int failedLogins = 0;
    private int successfulLogins = 0;
    private int numResponses = 0;

    private Logger log = Logger.getLogger(LoginTest.class);

    public ManyClientsTest(String method) {
        super(method);
    }

    public synchronized void testManyClients() throws Exception {
        MonitorTestClient testClient[] = new MonitorTestClient[NUM_CONNECTIONS];

        for (int i = 0; i < testClient.length; i++) {
            testClient[i] = new MonitorTestClient(host, port, i, this);
            testClient[i].connect();
            testClient[i].sendRequest(new LoginRequest(handle, password.toCharArray()), LoginResponse.class);
        }

        long startTime = System.currentTimeMillis();
        long endTime = startTime + TIMEOUT;
        while ((failedLogins + successfulLogins) < NUM_CONNECTIONS) {
            if (endTime <= System.currentTimeMillis())
                fail("Test timed out awaiting login responses");
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime <= 0)
                    continue; // don't just wait(0)!
                wait(waitTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Login time was " + (System.currentTimeMillis() - startTime) + "ms");

        assertEquals("Timed out while logging in clients", successfulLogins, NUM_CONNECTIONS);


        // now throw a few requests at it, and see if they arrive.
        // not really concerned with the type/content of requests or responses,
        // only concerned with the fact that a response arrives, and that it
        // is of the correct type (guaranteed by the MonitorTestClient), which implies
        // that it went to the right place.

        startTime = System.currentTimeMillis();
        for (int i = 0; i < requests.length; i++)
            testClient[i].sendRequest(requests[i], responses[i]);

        endTime = startTime + TIMEOUT;

        while (numResponses < responses.length) {
            if (endTime <= System.currentTimeMillis())
                fail("Test timed out awaiting command responses");
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime <= 0)
                    continue;
                wait(waitTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Command response time was " + (System.currentTimeMillis() - startTime) + "ms");

        assertEquals("Timed out awaiting responses, or responses not received", numResponses, responses.length);

        for (int i = 0; i < NUM_CONNECTIONS; i++)
            testClient[i].disconnect();
    }

    public synchronized void receivedObject(int id, Object response, long timeElapsed) {
        if (response instanceof LoginResponse) {
            log.info("LoginResponse received after " + timeElapsed);
            if (((LoginResponse) response).getSucceeded())
                successfulLogins++;
            else
                failedLogins++;
            notify();
        } else {
            log.info(response.getClass() + " received after " + timeElapsed);
            // type verified by MonitorTestClient.  So, just notify the waiting thread.
            numResponses++;
            notify();
        }
    }
}