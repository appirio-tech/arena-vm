package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.AdminListener.request.LoginRequest;
import com.topcoder.server.AdminListener.request.WarehouseLoadRequest;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.WarehouseLoadAck;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;


/**
 * A base class for warehouse load tests
 *
 * @author giorgos
 * @since Admin Tool 2.0 Nov 26, 2003
 * @see WarehouseLoadRequest
 * @see WarehouseLoadAck
 * @see com.topcoder.utilities.dwload.TCLoadAggregate
 * @see com.topcoder.utilities.dwload.TCLoadCoders
 * @see com.topcoder.utilities.dwload.TCLoadEmpty
 * @see com.topcoder.utilities.dwload.TCLoadRank
 * @see com.topcoder.utilities.dwload.TCLoadRequests
 * @see com.topcoder.utilities.dwload.TCLoadRound
 * @see com.topcoder.utilities.dwload.TCLoadUtility
 */
public abstract class WarehouseLoadTest extends TestCase implements MonitorTestClient.Client {
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

    // TODO: During integration tune timeout according to LONG_TIMEOUT_MS
    /**
     * Timeout for tests, after it expires tests fail
     * The timeout is particularly long because warehouse load
     * take a long time.
     *
     * @see ContestManagementController#LONG_TIMEOUT_MS
     */
    private static long TIMEOUT = 20000 * 1000;

    /**
     * Default round id on which the test will be executed
     */
    private static int roundID = 4475;

    /**
     * Default fullload for warehouse load requests to be tested
     */
    private static String warehouseLoadFullLoad = "true";

    /**
     * Checks to see if a response to WarehouseLoadRequest arrived
     */
    private WarehouseLoadAck warehouseLoadAck = null;

    /**
     * Log used to produced some reporting while the test runs
     */
    private Logger log = Logger.getLogger(WarehouseLoadTest.class);
    /**
     * Checks to see if login to the amdin monitor succeeded
     */
    private boolean loginSucceeded = false;

    /**
     * Simple timer to see how long the test phases took
     */
    private long startTime = 0;

    /**
     * Get the warehouse load id to be ran
     * @return the warehouse load request
     */
    public int getWarehouseLoadRequestId() {
        return warehouseLoadRequestId;
    }

    /**
     * Set the warehouse load id to be ran
     * @param warehouseLoadRequestId to be ran
     */
    public void setWarehouseLoadRequestId(int warehouseLoadRequestId) {
        this.warehouseLoadRequestId = warehouseLoadRequestId;
    }

    /**
     * The warehouse load request id that is going to be ran
     */
    private int warehouseLoadRequestId;

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
        warehouseLoadFullLoad = properties.getProperty("warehouseLoadFullLoad", warehouseLoadFullLoad);
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
     * Permorm all the actions a real admin would perform to run each warehouse
     * load reuqest once. The requests are ran in a linear manner (i.e not
     * concurrently)
     *
     * @throws java.lang.Exception if any exception happens during the process
     */
    public synchronized void testWarehouseLoad() throws Exception {
        MonitorTestClient client = new MonitorTestClient(adminListenerHost, adminListenerPort, 1, this);
        client.connect(); // will throw an exception if it fails
        assertTrue("Could not login using handle " + handle + " and password " + password,
                login(client, handle, password));

        startTime = System.currentTimeMillis();

        Hashtable params = new Hashtable();
        params.put("fullload", warehouseLoadFullLoad);
        params.put("roundid", new Integer(roundID).toString());

        WarehouseLoadRequest request = new WarehouseLoadRequest(warehouseLoadRequestId, params);
        long endTime = startTime + TIMEOUT;

        warehouseLoadAck = null;
        log.warn("Doing load: " + request.getRequestID());
        client.sendRequest(request, WarehouseLoadAck.class);
        while (warehouseLoadAck == null) {
            if (endTime <= System.currentTimeMillis()) {
                fail("Test timed out while doing warehouse load: " + request.getRequestID());
            }
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime <= 0)
                    continue; // don't just wait(0)!
                wait(waitTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        assertTrue("WarehouseLoadAck has exception " + warehouseLoadAck.getException(), !warehouseLoadAck.hasException());

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
        } else if (obj instanceof WarehouseLoadAck) {
            log.warn("Client[" + id + "]: WarehouseLoadAck took " + elapsedTime + "ms");
            warehouseLoadAck = (WarehouseLoadAck) obj;
            notify();
        }
    }
}
