/**
 *
 * Copyright © 2003, TopCoder, Inc. All rights reserved
 */

import junit.framework.TestCase;
import com.topcoder.server.AdminListener.response.*;
import com.topcoder.server.AdminListener.request.*;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.common.BackupCopy;
import com.topcoder.client.contestMonitor.model.*;

/**
 * Tests performance of several types of requests to adminListener.
 *
 * @author valeriy
 * @since Admin Tool 2.0 Nov 18, 2003
 */

public class GroupThreeStressTests extends TestCase implements MonitorTestClient.Client {

    private static final String handle = "gt494";
    private static final String password = "password";
    private static final String host = "192.168.0.101";
    private static final int port = 9994;
    private static final long TIMEOUT = 240 * 1000;
    private static final int roundID = 4475;

    private BackupTablesAck backupTablesAck = null;
    private GetBackupCopiesResponse getBackupCopiesResponse = null;
    private RestoreTablesAck restoreTablesAck = null;
    private GetRoundProblemComponentsAck roundProblemAck = null;
    private boolean loginSucceeded;

    public GroupThreeStressTests(String method) {
        super(method);
    }

    /** Tests performance of GetRoundProblemComponentsRequest
     */
    public void testGetRoundProblemComponents() throws Exception {
        run("GetRoundProblemComponents", 10, new Runnable() {
            public void run() {
                runGetRoundProblemComponents();
            }
        });
    }

    /** Tests performance of BackupTablesRequest
     */
    public void testBackupTables() throws Exception {
        run("BackupTables", 10, new Runnable() {
            public void run() {
                runBackupTables();
            }
        });
    }

    /** Tests performance of GetBackupCopiesRequest
     */
    public void testGetBackupCopies() throws Exception {
        run("GetGetBackupCopies", 10, new Runnable() {
            public void run() {
                runGetBackupCopies();
            }
        });
    }

    /** Tests performance of RestoreTablesRequest
     */
    public void testRestoreTables() throws Exception {
        run("RestoreTables", 10, new Runnable() {
            public void run() {
                runRestoreTables();
            }
        });
    }

    /** Executes request specified number of times
     *
     * @param name request name
     * @param opCount number of executions
     * @param r runnable that will perform actual execution of requests
     */
    public void run(String name, int opCount, Runnable r) throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < opCount; i++) {
            r.run();
        }
        long total = System.currentTimeMillis() - start;
        System.out.println(name+" "+opCount+" "+total+" "+((double)opCount/total*1000));
    }

    /** Performs login to adminListener
     */
    private boolean login(MonitorTestClient client, String handle, String password) throws Exception {
        loginSucceeded = false;

        client.sendRequest((Object) (new LoginRequest(handle, password.toCharArray())), LoginResponse.class);

        try {
            wait(TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginSucceeded;
    }

    /** Executes one GetRoundProblemComponents request
     */
    public void runGetRoundProblemComponents() {
        try {
            MonitorTestClient client = new MonitorTestClient(host, port, 1, this);
            client.connect(); // will throw an exception if it fails
            assertTrue("Could not login using handle " + handle + " and password " + password,
                login(client, handle, password));

            GetRoundProblemComponentsRequest request = new GetRoundProblemComponentsRequest(roundID);
    
            client.sendRequest(request, GetRoundProblemComponentsAck.class);

            long endTime = System.currentTimeMillis() + TIMEOUT;
            while (roundProblemAck == null) {
                if (endTime <= System.currentTimeMillis())
                    fail("Test timed out while backing up");
                try {
                    long waitTime = endTime - System.currentTimeMillis();
                    if (waitTime <= 0)
                        continue; // don't just wait(0)!
                    wait(waitTime); // check numResponses after it's been updated
                } catch (InterruptedException e) {
                }
            }

            assertTrue("GetRoundProblemComponentsAck has exception", !roundProblemAck.hasException());

            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** Executes one BackupTables request
     */
    public void runBackupTables() {
        try {
            MonitorTestClient client = new MonitorTestClient(host, port, 1, this);
            client.connect(); // will throw an exception if it fails
            assertTrue("Could not login using handle " + handle + " and password " + password,
                login(client, handle, password));

            BackupTablesRequest request = new BackupTablesRequest(roundID);
            for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++)
                request.addTableName(AdminConstants.TABLES_TO_BACKUP[i]);

            client.sendRequest(request, BackupTablesAck.class);

            long endTime = System.currentTimeMillis() + TIMEOUT;
            while (backupTablesAck == null) {
                if (endTime <= System.currentTimeMillis())
                    fail("Test timed out while backing up");
                try {
                    long waitTime = endTime - System.currentTimeMillis();
                    if (waitTime <= 0)
                        continue; // don't just wait(0)!
                    wait(waitTime); // check numResponses after it's been updated
                } catch (InterruptedException e) {
                }
            }

            assertTrue("BackupTablesAck has exception", !backupTablesAck.hasException());

            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** Executes one GetBackupCopies request
     */
    public void runGetBackupCopies() {
        try {
            MonitorTestClient client = new MonitorTestClient(host, port, 1, this);
            client.connect(); // will throw an exception if it fails
            assertTrue("Could not login using handle " + handle + " and password " + password,
                login(client, handle, password));

            GetBackupCopiesRequest request = new GetBackupCopiesRequest(roundID);
            client.sendRequest(request, GetBackupCopiesResponse.class);
        
            long endTime = System.currentTimeMillis() + TIMEOUT;
            while (getBackupCopiesResponse == null) {
                if (endTime <= System.currentTimeMillis())
                    fail("Test timed out while getting backup copies");
                try {
                    long waitTime = endTime - System.currentTimeMillis();
                    if (waitTime <= 0)
                        continue; // don't just wait(0)!
                    wait(waitTime); // check numResponses after it's been updated
                } catch (InterruptedException e) {
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
    
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** Executes one RestoreTables request
     */
    public void runRestoreTables() {
        try {
            MonitorTestClient client = new MonitorTestClient(host, port, 1, this);
            client.connect(); // will throw an exception if it fails
            assertTrue("Could not login using handle " + handle + " and password " + password,
                login(client, handle, password));

            RestoreTablesRequest request = new RestoreTablesRequest(
                ((BackupCopy) getBackupCopiesResponse.getBackupCopies().get(0)).getID()
            );
            for (int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++)
                request.addTableName(AdminConstants.TABLES_TO_BACKUP[i]);

            client.sendRequest(request, RestoreTablesAck.class);

            long endTime = System.currentTimeMillis() + TIMEOUT;
            while (restoreTablesAck == null) {
                if (endTime <= System.currentTimeMillis())
                    fail("Test timed out while restoring tables");
                try {
                    long waitTime = endTime - System.currentTimeMillis();
                    if (waitTime <= 0)
                        continue; // don't just wait(0)!
                    wait(waitTime); // check numResponses after it's been updated
                } catch (InterruptedException e) {
                }
            }
            assertTrue("RestoreTablesAck has exception", !restoreTablesAck.hasException());

            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public synchronized void receivedObject(int id, Object obj, long elapsedTime) {
        if (obj instanceof LoginResponse) {
            System.out.println("Client[" + id + "]: LoginResponse took " + elapsedTime + "ms");
            loginSucceeded = ((LoginResponse) obj).getSucceeded();
            notify();
        } else if (obj instanceof BackupTablesAck) {
            System.out.println("Client[" + id + "]: BackupTablesAck took " + elapsedTime + "ms");
            backupTablesAck = (BackupTablesAck) obj;
            notify();
        } else if (obj instanceof GetBackupCopiesResponse) {
            System.out.println("Client[" + id + "]: GetBackupCopiesResponse took " + elapsedTime + "ms");
            getBackupCopiesResponse = (GetBackupCopiesResponse) obj;
            notify();
        } else if (obj instanceof RestoreTablesAck) {
            System.out.println("Client[" + id + "]: RestoreTablesAck took " + elapsedTime + "ms");
            restoreTablesAck = (RestoreTablesAck) obj;
            notify();
        } else if (obj instanceof GetRoundProblemComponentsAck) {
            System.out.println("Client[" + id + "]: GetRoundProblemComponentsAck took " + elapsedTime + "ms");
            roundProblemAck = (GetRoundProblemComponentsAck) obj;
            notify();
        }
    }
}
