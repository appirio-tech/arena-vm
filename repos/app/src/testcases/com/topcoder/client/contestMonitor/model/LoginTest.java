package com.topcoder.client.contestMonitor.model;

/**
 * Tests to ensure that an SSL connection can be opened to the server, and that
 * logging in works.
 *
 * @author John Waymouth (coderlemming)
 */

import com.topcoder.server.AdminListener.request.LoginRequest;
import com.topcoder.server.AdminListener.response.LoginResponse;
import junit.framework.TestCase;
import org.apache.log4j.Logger;


public class LoginTest extends TestCase implements MonitorTestClient.Client {

    private static final String handle = "td";
    private static final String password = "foo";
    private static final String host = "172.16.20.30";
    private static final int port = 5017;
    private static final long TIMEOUT = 60 * 1000;
    private static final int NUM_CONNECTIONS = 10;

    private int failedLogins;
    private int successfulLogins;

    private Logger log = Logger.getLogger(LoginTest.class);

    public synchronized void setUp() {
        failedLogins = 0;
        successfulLogins = 0;
    }

    public LoginTest(String test) {
        super(test);
    }

    public synchronized void testConnectAndLogin() throws Exception {
        MonitorTestClient testClient = new MonitorTestClient(host, port, 0, this);

        testClient.connect(); // will throw an exception if it fails

        long startTime = System.currentTimeMillis();
        testClient.sendRequest(new LoginRequest(handle, password.toCharArray()), LoginResponse.class);

        try {
            wait((startTime + TIMEOUT) - System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("Test whether login succeeded", 1, successfulLogins);
        assertEquals("Too many responses received", 1, successfulLogins + failedLogins);
        testClient.disconnect();
    }

    public synchronized void testManyConcurrentLogins() throws Exception {

        MonitorTestClient testClient[] = new MonitorTestClient[NUM_CONNECTIONS];

        for (int i = 0; i < testClient.length; i++) {
            testClient[i] = new MonitorTestClient(host, port, i, this);
            testClient[i].connect();
        }

        // two loops so that the connect() calls don't take up time, causing
        // later requests not to run concurrently

        // this penalizes the server for time spent sending the request
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < testClient.length; i++) {
            testClient[i].sendRequest(new LoginRequest(handle, password.toCharArray()), LoginResponse.class);
        }


        long endTime = startTime + TIMEOUT;
        while ((failedLogins + successfulLogins) < NUM_CONNECTIONS) {
            if (endTime <= System.currentTimeMillis())
                fail("Test timed out");
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime <= 0)
                    continue; // don't just wait(0)!
                wait(waitTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.warn("Total time was " + (System.currentTimeMillis() - startTime) + "ms");

        assertEquals("Test whether all logins succeeded", NUM_CONNECTIONS, successfulLogins);
        assertEquals("Too many responses received", NUM_CONNECTIONS, successfulLogins + failedLogins);

        for (int i = 0; i < testClient.length; i++)
            testClient[i].disconnect();
    }

    public synchronized void receivedObject(int id, Object obj, long elapsedTime) {
        log.warn("Client[" + id + "]: Login response took " + elapsedTime);
        if (((LoginResponse) obj).getSucceeded()) {
            successfulLogins++;
        } else {
            failedLogins++;
        }
        notify();
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LoginTest.class);
    }

}
