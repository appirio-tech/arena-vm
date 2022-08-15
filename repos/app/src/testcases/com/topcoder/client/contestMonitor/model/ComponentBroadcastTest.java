package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.AdminListener.request.GetRoundProblemComponentsRequest;
import com.topcoder.server.AdminListener.request.LoginRequest;
import com.topcoder.server.AdminListener.response.GetRoundProblemComponentsAck;
import com.topcoder.server.AdminListener.response.LoginResponse;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A test which starts a monitor client, and then requests
 * all available components of selected round for which we
 * can broadcast a message. It doesn't test the actual broadcasting
 * of the messsage.
 *
 * @author giorgos
 * @since Admin Tool 2.0 Nov 18, 2003
 * @see GetRoundProblemComponentsRequest
 * @see GetRoundProblemComponentsAck
 */
public class ComponentBroadcastTest  extends TestCase implements MonitorTestClient.Client {
    /**
     * Default handle for logging in the admin monitor client
     */
    private static String handle = "gt494";

    /**
     * Default password for logging in the admin monitor client
     */
    private static String password = "password";

    /**
     * Default host the admin listener is running on
     */
    private static String adminListenerHost = "127.0.0.1";

    /**
     * Default port the admin listener is running on
     */
    private static int adminListenerPort = 5004;

    /**
     * Timeout for tests, after it expires tests fail
     */
    private static long TIMEOUT = 240 * 1000;

    /**
     * Default round id on which the test will be executed
     */
    private static int roundID = 4475;

    /**
     * Checks to see if a response to GetRoundProblemComponentsRequest arrived
     */
    private GetRoundProblemComponentsAck getRoundProblemComponentsAck = null;

    /**
     * Checks to see if login to the amdin monitor succeeded
     */
    private boolean loginSucceeded = false;

    /**
     * Simple timer to see how long the test phases took
     */
    private long startTime = 0;

    /**
     * Log used to produced some reporting while the test runs
     */
    private Logger log = Logger.getLogger(ComponentBroadcastTest.class);

    /**
     * Setup the test. Read the testdata.properties file which
     * provides the test configuration. The full path and filename
     * of the testdata.properties file can be retreived from the
     * system property with the same name.
     *
     * @throws Exception if excpetion occurs while setting up the test
     */
    protected void setUp() throws Exception {
        super.setUp();
        String propertiesFilename = System.getProperty("testdata.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        handle = properties.getProperty("handle", handle);
        password = properties.getProperty("password", password);
        adminListenerHost = properties.getProperty("adminListenerHost", adminListenerHost);
        adminListenerPort = Integer.parseInt(properties.getProperty("adminListenerPort", String.valueOf(adminListenerPort)));
        roundID = Integer.parseInt(properties.getProperty("roundID", String.valueOf(roundID)));
    }

    /**
     * Perform a headless login of the admin client
     *
     * @param client the monitor test client
     * @param handle the handle of the admin
     * @param password the password of the admin
     * @return a boolean indicating whether login succeeded
     * @throws java.lang.Exception if login times out
     */
    private synchronized boolean login(MonitorTestClient client, String handle, String password) throws Exception {
        loginSucceeded = false;

        client.sendRequest(new LoginRequest(handle, password.toCharArray()), LoginResponse.class);

        try {
            wait(TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loginSucceeded;
    }

    /**
     * Permorm all the actions a real admin would perform to send a component
     * broadcast.
     *
     * @throws java.lang.Exception if any exception happens during the process
     */
    public synchronized void testGetRoundProblemComponents() throws Exception {
        MonitorTestClient client = new MonitorTestClient(adminListenerHost, adminListenerPort, 1, this);
        client.connect(); // will throw an exception if it fails
        assertTrue("Could not login using handle " + handle + " and password " + password,
                login(client, handle, password));

        startTime = System.currentTimeMillis();

        GetRoundProblemComponentsRequest request = new GetRoundProblemComponentsRequest(roundID);

        assertTrue("GetRoundProblemComponentsRequest is not global", request.isGlobal());

        client.sendRequest(request, GetRoundProblemComponentsAck.class);

        long endTime = startTime + TIMEOUT;

        while (getRoundProblemComponentsAck == null) {
            if (endTime <= System.currentTimeMillis())
                fail("Test timed out while getting round problem components");
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime <= 0) {
                    continue; // don't just wait(0)!
                }
                wait(waitTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        assertTrue("GetRoundProblemComponentsAck has exception",
                !getRoundProblemComponentsAck.hasException());

        log.warn("Time was " + (System.currentTimeMillis() - startTime) + "ms");
        client.disconnect();
    }

    /**
     * Receiver of responses and acks that correspond to requests sent by the
     * test and login methods.
     *
     * @param id the client id
     * @param obj the response object
     * @param elapsedTime the time it took between request and response
     */
    public synchronized void receivedObject(int id, Object obj, long elapsedTime) {
        if (obj instanceof LoginResponse) {
            log.warn("Client[" + id + "]: LoginResponse took " + elapsedTime + "ms");
            loginSucceeded = ((LoginResponse) obj).getSucceeded();
            notify();
        } else if (obj instanceof GetRoundProblemComponentsAck) {
            log.warn("Client[" + id + "]: GetRoundProblemComponentsAck took " + elapsedTime + "ms");
            getRoundProblemComponentsAck = (GetRoundProblemComponentsAck) obj;
            notify();
        }
    }

}
