/*
 * ClientManagerTest
 * 
 * Created 08/09/2006
 */
package com.topcoder.farm.controller.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.dao.DAOFactory;
import com.topcoder.farm.controller.exception.ClientNotListeningException;
import com.topcoder.farm.controller.exception.UnregisteredClientException;
import com.topcoder.farm.controller.processor.MockDAOFactory;
import com.topcoder.farm.shared.invocation.InvocationFeedback;
import com.topcoder.farm.shared.util.Pair;

/**
 * Test case for ClientManager class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientManagerTest extends TestCase {
    private static final String CLIENT_ID = "1";
    private ClientManager manager;
    private Map<String, Long> indexes = new HashMap();

    protected void setUp() throws Exception {
        super.setUp();
        DAOFactory.configureInstance(new MockDAOFactory());
        manager = new ClientManager();
    }
    
    protected void tearDown() throws Exception {
        manager.release();
        manager = null;
        super.tearDown();
    }
    
    /**
     * Tests that removing a client not registered returns false
     */
    public void testRemoveNotRegistered() {
        assertFalse(manager.removeClient(CLIENT_ID));
    }
    
    /**
     * Tests that adding a client, really add it    
     * and when is removed the callback is not invoked
     */
    public void testAdd() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        assertEquals(0, cb.reportedItems.size());
        assertTrue(manager.removeClient(CLIENT_ID));
        assertEquals(0, cb.reportedItems.size());
    }
    
    
    /**
     * Tests that notifyClientResponse on a unregistered client throws exception  
     */
    public void testNotifyClientResponseUnregisteredClientException() throws Exception {
        try {
            manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
            fail("UnregisteredClientException");
        } catch (UnregisteredClientException e) {
        }
    }

    /**
     * Tests that notifyClientResponse on a client that is not listening throws exception  
     */
    public void testNotifyClientResponseClientNotListeningException() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        try {
            manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
            fail("ClientNotListeningException");
        } catch (ClientNotListeningException e) {
        }
    }

    /**
     * Tests that notifyClientResponses on a unregistered client throws exception  
     */
    public void testNotifyClientResponsesUnregisteredClientException() throws Exception {
        ArrayList<InvocationResponse> l = buildList();
        try {
            manager.notifyClientResponses(CLIENT_ID, l);
            fail("UnregisteredClientException");
        } catch (UnregisteredClientException e) {
        }
    }
    
    
    /**
     * Tests that notifyClientResponses on a client that is not listening throws exception  
     */
    public void testNotifyClientResponsesClientNotListeningException() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        ArrayList<InvocationResponse> l = buildList();
        try {
            manager.notifyClientResponses(CLIENT_ID, l);
            fail("ClientNotListeningException");
        } catch (ClientNotListeningException e) {
        }
    }
    
    
    /**
     * Tests that notifyClientResponse invokes the callback 
     */
    public void testNotifyClientResponse() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
        Thread.sleep(100);
        assertEquals(1, cb.reportedItems.size());
        cb.reportedItems.get(0).getFst().equals(Integer.valueOf(0));
        assertTrue(manager.removeClient(CLIENT_ID));
        assertEquals(1, cb.reportedItems.size());
    }
    
    /**
     * Tests that multiple calls to notifyClientResponse produces multiples invocations 
     * to method reportInvocationResult of the callback.  
     */
    public void testManyNotifyClientResponse() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        for (int i = 0; i < 5; i++) {
            manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
            Thread.sleep(100);
        }
        Thread.sleep(200);
        assertEquals(5, cb.reportedItems.size());
        assertTrue(manager.removeClient(CLIENT_ID));
    }
    
    /**
     * Tests that notifyClientResponses invokes list.size() times the callback 
     */
    public void testNotifyClientResponses() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        ArrayList<InvocationResponse> l = buildList();
        manager.notifyClientResponses(CLIENT_ID, l);
        Thread.sleep(200);
        assertEquals(l.size(), cb.reportedItems.size());
        cb.reportedItems.get(0).getFst().equals(Integer.valueOf(0));
        assertTrue(manager.removeClient(CLIENT_ID));
        assertEquals(l.size(), cb.reportedItems.size());
    }
    
    
    /**
     * Tests that multiple calls to notifyClientResponses produces multiples invocations 
     * to method reportInvocationResult of the callback.  
     */
    public void testManyNotifyClientResponses() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        for (int i = 0; i < 5; i++) {
            ArrayList<InvocationResponse> l = buildList();
            manager.notifyClientResponses(CLIENT_ID, l);
            Thread.sleep(100);
        }
        Thread.sleep(200);
        assertEquals(15, cb.reportedItems.size());
        assertTrue(manager.removeClient(CLIENT_ID));
    }
    
    /**
     * Tests that after adding a second client, no more reports occurs 
     * in the first registered one 
     */
    public void test2AddRemovePrevious() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        TestNodeCallback cb2 = buildCallback("2");
        manager.addClient(CLIENT_ID, cb);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb2);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
        Thread.sleep(100);
        assertEquals(1, cb.reportedItems.size());
        assertEquals(1, cb2.reportedItems.size());
        assertTrue(manager.removeClient(CLIENT_ID));
        assertEquals(1, cb.reportedItems.get(0).getFst().intValue());
        assertEquals(0, cb2.reportedItems.get(0).getFst().intValue());
    }
    
    /**
     * Tests that after adding a new client the listening state must be set again 
     */
    public void test2AddRequired2SetListening() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        TestNodeCallback cb2 = buildCallback("2");
        manager.addClient(CLIENT_ID, cb);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb2);
        try {
            manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
            fail("ClientNotListeningException");
        } catch (ClientNotListeningException e) {
        }
    }
    
    
    /**
     * Tests that after removing a client, no more invocation are reported and 
     * that an exception is thrown
     */
    public void testAfterRemoveNoMoreReports() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
        Thread.sleep(200);
        manager.removeClient(CLIENT_ID);
        try { 
            manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
            fail("UnregisteredClientException");
        } catch (UnregisteredClientException e) {
        }
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
    }

    
    /**
     * Tests that after removing a client, no more invocation are reported and 
     * that an exception is thrown
     */
    public void testAfterDisconnectNoMoreReports() throws Exception {
        TestNodeCallback cb = buildCallback(CLIENT_ID);
        manager.addClient(CLIENT_ID, cb);
        manager.setAsListeningResultsIfRegistered(CLIENT_ID);
        manager.notifyDisconnect("Disconnected");
        Thread.sleep(200);
        try { 
            manager.notifyClientResponse(CLIENT_ID, buildInvocation(CLIENT_ID));
            fail("ClientNotListeningException");
        } catch (ClientNotListeningException e) {
        }
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
    }
    

    private ArrayList<InvocationResponse> buildList() {
        ArrayList<InvocationResponse> l = new ArrayList<InvocationResponse>();
        l.add(buildInvocation(CLIENT_ID));
        l.add(buildInvocation(CLIENT_ID));
        l.add(buildInvocation(CLIENT_ID));
        return l;
    }
    
    private TestNodeCallback buildCallback(String id) {
        return new TestNodeCallback();
    }

    private InvocationResponse buildInvocation(String clientId) {
        Long l = indexes.get(clientId);
        if (l == null) {
            l = new Long(0);
            indexes.put(clientId, l);
        }
        long v = l.longValue()+1;
        indexes.put(clientId, new Long(v));
        return new InvocationResponse(""+(Long.parseLong(clientId)*100+v), new Long(Long.parseLong(clientId)*100+v), null);
    }
    
    
    private static class TestNodeCallback implements ClientNodeCallback {
        List<Pair<Integer,Object>> reportedItems = new ArrayList();
        
        public void unregistered(String cause) {
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(1), cause));
        }

        public void reportInvocationResult(InvocationResponse response) {
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(0), response));
        }
        public void disconnect(String cause) {
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(2), cause));
        }

        @Override
        public void reportInvocationFeedback(InvocationFeedback response) {
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(0), response));
        }
        
        @Override
        public String getEndpointString() {
            return "NONE";
        }
        
        @Override
        public String getEndpointIP() {
            return "NONE";
        }
    }

}
