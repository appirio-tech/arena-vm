/**
 *
 * Copyright © 2003, TopCoder, Inc. All rights reserved
 */

//package goes here

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.topcoder.client.contestMonitor.model.*;
import com.topcoder.server.common.BackupCopy;
import com.topcoder.server.AdminListener.*;
import com.topcoder.server.AdminListener.request.*;
import com.topcoder.server.AdminListener.response.*;
import java.util.*;

/**
 * <p>This class tests the accuracy of
 * backing up and restoring among tables</p>
 *
 * @author billy
 * @version 1.0
 */
public class Group3Phase2AccuracyTests extends TestCase implements MonitorTestClient.Client {

    /**
     * Constants used for the connection
     * Note: change host/port to correct value
     */
    private static final String host = "192.168.1.100";
    private static final int port = 5004;
    private static final int id = 123;
    private static final String handle = "gt494";
    private static final String password = "password";
    private static final long TIMEOUT = 60 * 1000;
    private static final int roundID = 4475;
    
    private MonitorTestClient testClient;
    private int val = 0;
    
    //global variables used by the receivedObject method
    private boolean successfulLogin = false;
    private BackupTablesAck backupAck = null;
    private GetBackupCopiesResponse backupResponse = null;
    private RestoreTablesAck restoreAck = null;
    
    public static Test suite() {
        return(new TestSuite(Group3AccuracyTests.class));
    }
    
    /**
     * Set up the connection to the server
     */
    public synchronized void setUp() {
        testClient = new MonitorTestClient(host, port, id, this);
        
        try {
            testClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not make connection to " + host + ":" + port);
        }
        
        long startTime = System.currentTimeMillis();
        try {
            testClient.sendRequest(new LoginRequest(handle,
                password.toCharArray()), LoginResponse.class);
            wait((startTime + TIMEOUT) - System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
        
        assertTrue("unsuccessful login", successfulLogin);
    }
    
    /**
     * Disconnect from the server
     */
    public synchronized void tearDown() {
        testClient.disconnect();
    }
    
    /**
     * Request tables to be backed up
     */
    public synchronized void testBackupTablesRequest() {
        BackupTablesRequest btr = new BackupTablesRequest(roundID);
        //first fill this request with the table values
        for(int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++) {
            btr.addTableName(AdminConstants.TABLES_TO_BACKUP[i]);
        }
        btr.setComment("all tables to backup");
        
        //now send this request
        try {
            testClient.sendRequest(btr, BackupTablesAck.class);
            //wait for the response
            wait(TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
        
        assertNotNull("no ack sent back - BackupTablesRequest", backupAck);
        assertFalse("exception in BackupTablesAck", backupAck.hasException());
    }
    
    /**
     * Now test the retrieval of the backups
     */
    public synchronized void testGetBackupCopiesRequest() {
        GetBackupCopiesRequest gbcr = new GetBackupCopiesRequest(roundID);
        //send a request, expecting back a GetBackupCopiesResponse
        try {
            testClient.sendRequest(gbcr, GetBackupCopiesResponse.class);
            //wait for the response
            wait(TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception calling wait()");
        }
        
        assertNotNull("backupResponse is null", backupResponse); 
        //make sure backupResponse has something and has no exceptions
        assertTrue("no backup copies",
            backupResponse.getBackupCopies().size() > 0); 
        assertFalse("exception with backup copies",
            backupResponse.hasException());

        val = ((BackupCopy)backupResponse.getBackupCopies().get(0)).getID();

        String[] tableNames =
            ((BackupCopy)backupResponse.getBackupCopies().get(0))
                .getTableNames();
        assertEquals("different number of table names",
            AdminConstants.TABLES_TO_BACKUP.length, tableNames.length);
        //now check each one
        for(int i = 0; i < tableNames.length; i++) {
            boolean found = false;
            for(int j = 0; j < tableNames.length; j++) {
                if(tableNames[j].equals(AdminConstants.TABLES_TO_BACKUP[i])) {
                    found = true;
                    break;
                }
            } //for j
            assertTrue("different names in arrays", found);
        } //for i
    }
    
    /**
     * Test the restoration of the tables
     */
    public synchronized void testRestoreTablesRequest() {
        RestoreTablesRequest rtr = new RestoreTablesRequest(val);
        //input each of the table names to recover
        for(int i = 0; i < AdminConstants.TABLES_TO_BACKUP.length; i++) {
            rtr.addTableName(AdminConstants.TABLES_TO_BACKUP[i]);
        }
        
        //send the request - we expect an acknowledgement
        try {
            testClient.sendRequest(rtr, RestoreTablesAck.class);
        
            //wait for the response
            wait(TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception calling wait()");
        }
        
        //check the acknowledgement - not null
        assertNotNull("no ack sent back - RestoreTablesRequest", restoreAck);
    }
        
    
    /**
     * This method is used in order to receive the responses
     */
    public synchronized void receivedObject(int id, Object obj, long elapsedTime) {
        if(obj instanceof LoginResponse) {
            if (((LoginResponse) obj).getSucceeded()) {
                successfulLogin = true;
            }
            notify();
        }
        else if(obj instanceof BackupTablesAck) {
            backupAck = (BackupTablesAck)obj;
            notify();
        }
        else if(obj instanceof GetBackupCopiesResponse) {
            backupResponse = (GetBackupCopiesResponse)obj;
            notify();
        }
        else if(obj instanceof RestoreTablesAck) {
            restoreAck = (RestoreTablesAck)obj;
            notify();
        }
    }
}
