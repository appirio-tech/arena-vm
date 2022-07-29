package com.topcoder.client.contestMonitor.model;

/**
 * Load test the GetAllContestsRequest request to the admin listener server.
 * Verifies that all responses arrive before a specified timeout, and that
 * all are consistent (equal to eachother).  This would, of course, fail
 * if a contest was added between requests, so use with caution.
 *
 * This command is expected to tax the following areas: connection to the
 * admin listener server, connection to the EJB server, EJB's connection
 * to the database server.  It is a good representation of an extreme load
 * situation.  It is already known that the GetAllContestsRequest request
 * takes about 45 seconds when run from a modem.
 *
 */

import com.topcoder.server.AdminListener.request.GetAllContestsRequest;
import com.topcoder.server.AdminListener.request.LoginRequest;
import com.topcoder.server.AdminListener.response.GetAllContestsAck;
import com.topcoder.server.AdminListener.response.LoginResponse;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.util.Collection;

public class GetAllContestsTest extends TestCase implements MonitorTestClient.Client {

    private static final String handle = "gt494";
    private static final String password = "password";
    private static final String host = "192.168.0.5";
    private static final int port = 9994;
    private static final long TIMEOUT = 120 * 1000;
    private static final int NUM_CONNECTIONS = 1;

    private Logger log = Logger.getLogger(GetAllContestsTest.class);

    private Collection[] response = new Collection[NUM_CONNECTIONS];
    private int numResponses;
    private long startTime = 0;

    private boolean loginSucceeded;

    public GetAllContestsTest(String method) {
        super(method);
    }

    private synchronized boolean login(MonitorTestClient client, String handle, String password) throws Exception {
        loginSucceeded = false;

        client.sendRequest((Object) (new LoginRequest(handle, password.toCharArray())), LoginResponse.class);

        try {
            wait(TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loginSucceeded;
    }

    public synchronized void testGetAllContests() throws Exception {
        MonitorTestClient testClient[] = new MonitorTestClient[NUM_CONNECTIONS];

        for (int i = 0; i < NUM_CONNECTIONS; i++) {
            testClient[i] = new MonitorTestClient(host, port, i, this);
            testClient[i].connect(); // will throw an exception if it fails
            assertTrue("Could not login using handle " + handle + " and password " + password,
                    login(testClient[i], handle, password));
        }

        // this penalizes the server for time spent sending the request
        startTime = System.currentTimeMillis();
        for (int i = 0; i < NUM_CONNECTIONS; i++)
            testClient[i].sendRequest(new GetAllContestsRequest(), GetAllContestsAck.class);

        long endTime = startTime + TIMEOUT;
        while (numResponses < NUM_CONNECTIONS) {
            if (endTime <= System.currentTimeMillis())
                fail("Test timed out");
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime <= 0)
                    continue; // don't just wait(0)!
                wait(waitTime); // check numResponses after it's been updated
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        log.warn("Total time was " + (System.currentTimeMillis() - startTime) + "ms");

        assertTrue("Too many responses received", numResponses <= NUM_CONNECTIONS);

        for (int i = 1; i < NUM_CONNECTIONS; i++)
            assertTrue("Responses are inconsistent", response[0].equals(response[i]));

        for (int i = 0; i < NUM_CONNECTIONS; i++)
            testClient[i].disconnect();
    }

    public synchronized void receivedObject(int id, Object obj, long elapsedTime) {
        if (obj instanceof LoginResponse) {
            loginSucceeded = ((LoginResponse) obj).getSucceeded();
            notify();
        } else if (obj instanceof GetAllContestsAck) {
            log.warn("Client[" + id + "]: GetAllContestsAck took " + elapsedTime);
            if (numResponses < NUM_CONNECTIONS)
                response[numResponses++] = (Collection) ((GetAllContestsAck) obj).getContests();
            else
                numResponses++;
            notify();
        }
    }
}


