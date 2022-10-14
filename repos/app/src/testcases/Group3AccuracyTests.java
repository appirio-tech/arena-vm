/**
 *
 * Copyright © 2003, TopCoder, Inc. All rights reserved
 */

//insert correct package depending on test location
//package com.topcoder.server.AdminListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.topcoder.server.AdminListener.*;
import com.topcoder.server.AdminListener.request.*;
import com.topcoder.server.AdminListener.response.*;

import java.util.Hashtable;
import com.topcoder.client.contestMonitor.model.*;

/**
 * <p>This class tests the accuracy of WarehouseLoadRequest.</p>
 *
 * @author Billy Francisco
 * @version 1.0
 */
public class Group3AccuracyTests extends TestCase implements MonitorTestClient.Client {

    //constants to be used for warehouse requests
    private static final int[] requestIDs = {
        AdminConstants.REQUEST_WAREHOUSE_LOAD_AGGREGATE,
        AdminConstants.REQUEST_WAREHOUSE_LOAD_CODER,
        AdminConstants.REQUEST_WAREHOUSE_LOAD_EMPTY,
        AdminConstants.REQUEST_WAREHOUSE_LOAD_RANK,
        AdminConstants.REQUEST_WAREHOUSE_LOAD_REQUESTS,
        AdminConstants.REQUEST_WAREHOUSE_LOAD_ROUND
    };
    
    //used to connect to the server
    private static final String host = "192.168.1.100";
    private static final int port = 5004;
    private static final int id = 123;
    private static final String handle = "gt494";
    private static final String password = "password";
    private static final long TIMEOUT = 60 * 1000;
    
    private WarehouseLoadRequest wlr;
    private MonitorTestClient testClient;
    private Hashtable params;
    
    private boolean successfulLogin = false;
    private WarehouseLoadAck warehouseLoadAck = null;
    
    public static Test suite() {
        return(new TestSuite(Group3AccuracyTests.class));
    }
    
    /**
     * Initialize the hashtable, set up the connection
     */
    public synchronized void setUp() {
        params = new Hashtable();
        params.put("roundid", "1");
        params.put("fullload", "false");
        params.put("failed", "0");
        
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
     * Check the constructor/accessors
     */
    public synchronized void testConstructor() {
        for(int i=0; i<requestIDs.length; i++) {
            wlr = new WarehouseLoadRequest(requestIDs[i], params);
            //check accessors
            assertEquals("incorrect requestID", requestIDs[i], wlr.getRequestID());
            assertEquals("incorrect params", params, wlr.getParams());
        }
    }
    
    /**
     * Check the sending of the WarehouseLoadRequest
     */
    public synchronized void testSendRequest() {
        wlr = new WarehouseLoadRequest(requestIDs[3], params);
        try {
            testClient.sendRequest(wlr, WarehouseLoadAck.class);
            //wait for the response
            wait(TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception sending/waiting");
        }
        assertNotNull("no WarehouseLoadAck sent back", warehouseLoadAck);
    }
    
    /**
     * This method is used to receive the responses
     */
    public synchronized void receivedObject(int id, Object obj, long elapsedTime) {
        if(obj instanceof LoginResponse) {
            if (((LoginResponse) obj).getSucceeded()) {
                successfulLogin = true;
            }
            notify();
        }
        else if(obj instanceof WarehouseLoadAck) {
            warehouseLoadAck = (WarehouseLoadAck)obj;
            notify();
        }
    }
}
