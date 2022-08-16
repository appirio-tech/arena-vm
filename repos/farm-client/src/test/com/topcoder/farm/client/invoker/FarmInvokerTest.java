/*
 * FarmInvokerTest
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.farm.client.api.ClientNodeMock;
import com.topcoder.farm.client.api.ClientNodeMock.ClientEvent;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.shared.invocation.InvocationFeedback;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.farm.test.util.MTTestCase;

/**
 * Test case for FarmInvoker class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class FarmInvokerTest extends MTTestCase {
    private ClientNodeMock nodeMock;
    private FarmInvoker invoker;
    public List responses;

    protected void setUp() throws Exception {
        super.setUp();
        nodeMock = new ClientNodeMock("CL");
        invoker = new FarmInvoker(nodeMock, new MockHandler(), new MockHandler());
        responses = Collections.synchronizedList(new LinkedList());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        invoker.release();
        invoker = null;
    }

    /**
     * Invoking scheduleInvocation delegates to ClientNode
     */
    public void testSchedule() throws Exception {
        invoker.scheduleInvocation(buildInvocatioRequest(1));
        ClientEvent event = (ClientEvent) nodeMock.getEvents().get(0);
        assertEquals(ClientEvent.SCHEDULE, event.getType());
    }

    /**
     * Invoking scheduleInvocationSync delegates to ClientNode and returns
     * AsyncInvocationResponse object
     */
    public void testScheduleSync() throws Exception {
        AsyncInvocationResponse response = invoker.scheduleInvocationSync(buildInvocatioRequest(1));
        ClientEvent event = (ClientEvent) nodeMock.getEvents().get(0);
        assertEquals(ClientEvent.SCHEDULE, event.getType());
        assertNotNull(response);
        assertFalse(response.isDone());
    }

    /**
     * After scheduling synch if a result is received for the invocation, It is
     * set in the AsyncInvocationResponse
     */
    public void testScheduleSyncWithResponse() throws Exception {
        final int reqId = 1;
        AsyncInvocationResponse response = invoker.scheduleInvocationSync(buildInvocatioRequest(reqId));
        run(new Runnable() {
            public void run() {
                sleep(200);
                nodeMock.forceInvocationResultReceived(buildResponse(reqId));
            }
        });
        startAll();
        try {
            InvocationResponse result = response.get(300);
            assertEquals(new Long(reqId), result.getResult().getReturnValue());
        } finally {
            waitAll(1);
        }
    }

    /**
     * After scheduling synch if a result is received for the invocation and the
     * AsyncInvocationResponse was cancelled the result notify to the default
     * handler
     */
    public void testScheduleSyncWithCancel() throws Exception {
        final int reqId = 1;
        AsyncInvocationResponse response = invoker.scheduleInvocationSync(buildInvocatioRequest(reqId));
        run(new Runnable() {
            public void run() {
                sleep(400);
                nodeMock.forceInvocationResultReceived(buildResponse(reqId));
            }
        });
        startAll();
        try {
            try {
                response.get(100);
                fail("Expected timeout");
            } catch (TimeoutException e) {
                assertTrue(response.cancel());
            }
            sleep(450);
            assertEquals(((InvocationResponse) responses.get(0)).getResult().getReturnValue(), new Long(reqId));
        } finally {
            waitAll(1);
        }
    }

    /**
     * After scheduling synch if a disconnection occurs, the async objects are
     * released and a a NodeDisconnectedException is thrown for each one.
     */
    public void testScheduleSyncWithDisconnection() throws Exception {
        final int reqId = 1;
        AsyncInvocationResponse response = invoker.scheduleInvocationSync(buildInvocatioRequest(reqId));
        AsyncInvocationResponse response2 = invoker.scheduleInvocationSync(buildInvocatioRequest(reqId + 1));
        run(new Runnable() {
            public void run() {
                sleep(100);
                nodeMock.forceNodeDisconnected("Disconnected");
                nodeMock.forceInvocationResultReceived(buildResponse(reqId));
            }
        });
        startAll();
        startTiming();
        try {
            try {
                response.get(300);
                fail("expected NodeDisconnectedException");
            } catch (NodeDisconnectedException e) {
            }
            assertTrue(lapTiming() < 150);
            try {
                response2.get(300);
                fail("expected NodeDisconnectedException");
            } catch (NodeDisconnectedException e) {
            }
            assertTrue(lapTiming() < 200);
        } finally {
            waitAll(1);
        }
    }

    /**
     * If a result is received and nobody is waiting for that response, the
     * result notify to the default handler
     */
    public void testInvocationResultReceivedForNoInvocation() throws Exception {
        final int reqId = 1;
        run(new Runnable() {
            public void run() {
                sleep(100);
                nodeMock.forceInvocationResultReceived(buildResponse(reqId));
            }
        });
        startAll();
        try {
            sleep(200);
            assertEquals(((InvocationResponse) responses.get(0)).getResult().getReturnValue(), new Long(reqId));
        } finally {
            waitAll(1);
        }
    }

    /**
     * If a result is received and nobody is waiting for that response, the
     * result notify to the default handler.
     */
    public void testInvocationResultReceivedForAsyncInvocation() throws Exception {
        final int reqId = 1;
        invoker.scheduleInvocation(buildInvocatioRequest(reqId));
        run(new Runnable() {
            public void run() {
                sleep(100);
                nodeMock.forceInvocationResultReceived(buildResponse(reqId));
            }
        });
        startAll();
        try {
            sleep(200);
            assertEquals(((InvocationResponse) responses.get(0)).getResult().getReturnValue(), new Long(reqId));
        } finally {
            waitAll(1);
        }
    }

    private InvocationRequest buildInvocatioRequest(long reqId) {
        InvocationRequest request = new InvocationRequest();
        request.setId("" + reqId);
        request.setAttachment(new Long(reqId));
        return request;
    }

    private InvocationResponse buildResponse(final int reqId) {
        InvocationResult result = new InvocationResult();
        result.setReturnValue(new Long(reqId));
        return new InvocationResponse("" + reqId, new Long(reqId), result);
    }

    public class MockHandler implements InvocationResultHandler, InvocationFeedbackHandler {
        public boolean handleResult(InvocationResponse response) {
            responses.add(response);
            return true;
        }

        public void handleFeedback(InvocationFeedback feedback) {
            responses.add(feedback);
        }
    }
}
