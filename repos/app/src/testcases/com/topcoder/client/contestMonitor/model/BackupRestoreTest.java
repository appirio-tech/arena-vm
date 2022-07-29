package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.BackupTablesRequest;
import com.topcoder.server.AdminListener.request.GetBackupCopiesRequest;
import com.topcoder.server.AdminListener.request.LoginRequest;
import com.topcoder.server.AdminListener.request.RestoreTablesRequest;
import com.topcoder.server.AdminListener.response.BackupTablesAck;
import com.topcoder.server.AdminListener.response.GetBackupCopiesResponse;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.RestoreTablesAck;
import com.topcoder.server.common.BackupCopy;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A test which starts a monitor client, creates a backup copy for a
 * specified round, then retrieves the available backups for that round
 * and restores one of them. In the process it checks whether any exceptions
 * occured.
 *
 * @author giorgos
 * @since Admin Tool 2.0 Nov 18, 2003
 * @see GetBackupCopiesRequest
 * @see GetBackupCopiesResponse
 * @see BackupTablesRequest
 * @see BackupTablesAck
 * @see RestoreTablesRequest
 * @see RestoreTablesAck
 */
public class BackupRestoreTest extends TestCase implements MonitorTestClient.Client {
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
     * Log used to produced some reporting while the test runs
     */
    private Logger log = Logger.getLogger(BackupRestoreTest.class);

    /**
     * Checks to see if a response to BackupTablesRequest arrived
     */
    private BackupTablesAck backupTablesAck = null;

    /**
     * Checks to see if a response to GetBackupCopiesRequest arrived
     */
    private GetBackupCopiesResponse getBackupCopiesResponse = null;

    /**
     * Checks to see if a response to RestoreTablesRequest arrived
     */
    private RestoreTablesAck restoreTablesAck = null;

    /**
     * Checks to see if login to the amdin monitor succeeded
     */
    private boolean loginSucceeded = false;

    /**
     * Simple timer to see how long the test phases took
     */
    private long startTime = 0;

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
     * @throws Exception if login times out
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
     * Permorm all the actions a real admin would perform to create a backup
     * and then restore it.
     *
     * @throws Exception if any exception happens during the process
     */
    public synchronized void testBackupRestore() throws Exception {
        MonitorTestClient client = new MonitorTestClient(adminListenerHost, adminListenerPort, 1, this);
        client.connect(); // will throw an exception if it fails
        assertTrue("Could not login using handle " + handle + " and password " + password,
                login(client, handle, password));

        startTime = System.currentTimeMillis();

        BackupTablesRequest request = new BackupTablesRequest(roundID);
        for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++)
            request.addTableName(AdminConstants.TABLES_TO_BACKUP[i]);

        client.sendRequest(request, BackupTablesAck.class);

        long endTime = startTime + TIMEOUT;
        while (backupTablesAck == null) {
            if (endTime <= System.currentTimeMillis()) {
                fail("Test timed out while backing up");
            }
            try {
                long waitTime = endTime - System.currentTimeMillis();
                if (waitTime <= 0) {
                    continue; // don't just wait(0)!
                }
                wait(waitTime); // check numResponses after it's been updated
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        assertTrue("BackupTablesAck has exception", !backupTablesAck.hasException());

        GetBackupCopiesRequest request2 = new GetBackupCopiesRequest(roundID);
        client.sendRequest(request2, GetBackupCopiesResponse.class);
        while (getBackupCopiesResponse == null) {
            if (endTime <= System.currentTimeMillis()) {
                fail("Test timed out while getting backup copies");
            }
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

        assertTrue("GetBackupCopiesResponse has exception", !getBackupCopiesResponse.hasException());
        assertTrue("GetBackupCopiesResponse is empty", getBackupCopiesResponse.getBackupCopies().size() != 0);

        // check that all backup copies have correct round id
        for (int i = 0; i < getBackupCopiesResponse.getBackupCopies().size(); i++) {
            assertEquals(
                    "GetBackupCopiesResponse[" + i + "] round id is wrong",
                    roundID,
                    ((BackupCopy) getBackupCopiesResponse.getBackupCopies().get(i)).getRoundID()
            );
        }

        RestoreTablesRequest request3 = new RestoreTablesRequest(
                ((BackupCopy) getBackupCopiesResponse.getBackupCopies().get(0)).getID()
        );
        for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++)
            request3.addTableName(AdminConstants.TABLES_TO_BACKUP[i]);

        client.sendRequest(request3, RestoreTablesAck.class);
        while (restoreTablesAck == null) {
            if (endTime <= System.currentTimeMillis()) {
                fail("Test timed out while restoring tables");
            }
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
        assertTrue("RestoreTablesAck has exception", !restoreTablesAck.hasException());

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
        } else if (obj instanceof BackupTablesAck) {
            log.warn("Client[" + id + "]: BackupTablesAck took " + elapsedTime + "ms");
            backupTablesAck = (BackupTablesAck) obj;
            notify();
        } else if (obj instanceof GetBackupCopiesResponse) {
            log.warn("Client[" + id + "]: GetBackupCopiesResponse took " + elapsedTime + "ms");
            getBackupCopiesResponse = (GetBackupCopiesResponse) obj;
            notify();
        } else if (obj instanceof RestoreTablesAck) {
            log.warn("Client[" + id + "]: RestoreTablesAck took " + elapsedTime + "ms");
            restoreTablesAck = (RestoreTablesAck) obj;
            notify();
        }
    }
}
