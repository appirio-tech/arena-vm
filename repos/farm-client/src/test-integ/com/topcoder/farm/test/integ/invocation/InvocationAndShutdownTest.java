/*
 * InvocationAndShutdownTest
 * 
 * Created 09/28/2006
 */
package com.topcoder.farm.test.integ.invocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.topcoder.farm.client.invoker.AsyncInvocationResponse;
import com.topcoder.farm.client.invoker.FarmFactory;
import com.topcoder.farm.client.invoker.FarmInvoker;
import com.topcoder.farm.client.invoker.InvocationResultHandler;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.farm.shared.util.concurrent.WaitFlag;
import com.topcoder.farm.test.common.ThreadLauncherInvocation;
import com.topcoder.farm.test.integ.config.ClientTestIntegConfigurator;

/**
 * In order to run this test case, a controller must be running and configured 
 * to listen clients. Additionally only processor PR4-1 must be running and connected to the
 * farm.
 * 
 * Test case of multi-Invocation processing features of a processor
 * 
 * IMPORTANT: This test case forces the processor to shutdown
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationAndShutdownTest extends TestCase {
    private FarmFactory factory;
    private FarmInvoker invoker;
    private List responses = Collections.synchronizedList(new ArrayList());
    private WaitFlag responseArrived = new WaitFlag();

    public InvocationAndShutdownTest() {
        
    }

    protected void setUp() throws Exception {
        super.setUp();
        try {
            ClientTestIntegConfigurator.configure();
            factory = FarmFactory.getInstance();
            FarmFactory.getInstance().configureHandler("CL1", new HandlerToList(responses, responseArrived));
            invoker = factory.getInvoker("CL1");
        } catch (Exception e) {
            throw (IllegalStateException) new IllegalStateException()
                    .initCause(e);
        }
        invoker.getClientNode().cancelPendingRequests();
        responses.clear();
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        FarmFactory.releaseInstance();
    }

    public void testNormalExitOfThreads() throws Exception {
        invoker.scheduleInvocation(buildRequest("A-1-1;", 500, 10, 10, 0));
        invoker.scheduleInvocation(buildRequest("A-1-2;", 500, 10, 10, 0));
        invoker.scheduleInvocation(buildRequest("A-1-3;", 500, 10, 10, 0));
        invoker.scheduleInvocation(buildRequest("A-1-4;", 500, 10, 10, 0));
        AsyncInvocationResponse response = invoker.scheduleInvocationSync(buildRequest("A-1-5;", 500, 10, 10, 0));
        response.get(2000);
        assertEquals(4, responses.size());
        
        assertResponse("A-1-1;", 0, responses);
        assertResponse("A-1-2;", 1, responses);
        assertResponse("A-1-3;", 2, responses);
        assertResponse("A-1-4;", 3, responses);
    }
    
    public void testThreadsMustBeInterruted() throws Exception {
        invoker.scheduleInvocation(buildRequest("A-1-1;", 600, 10, 10, 0));
        invoker.scheduleInvocation(buildRequest("A-1-2;", 600, 10, 10, 0));
        invoker.scheduleInvocation(buildRequest("A-1-3;", 300, 10, 5, 5));
        invoker.scheduleInvocation(buildRequest("A-1-4;", 600, 10, 10, 0));
        AsyncInvocationResponse response = invoker.scheduleInvocationSync(buildRequest("A-1-5;", 700, 10, 10, 0));
        response.get(2000);
        assertEquals(4, responses.size());
        
        assertResponse("A-1-1;", 0, responses);
        assertResponse("A-1-2;", 1, responses);
        assertResponse("A-1-3;", 2, responses);
        assertResponse("A-1-4;", 3, responses);
    }
    
    
    public void testProcessorShutdown() throws Exception {
        invoker.scheduleInvocation(buildRequest("A-1-1;", 900, 10, 10, 0));
        invoker.scheduleInvocation(buildRequest("A-1-2;", 900, 10, 10, 0));
        invoker.scheduleInvocation(buildRequest("A-1-3;", 200, 10, 5, 4));
        invoker.scheduleInvocation(buildRequest("A-1-4;", 100, 10, 10, 0));
        Thread.sleep(1200);
        
        assertEquals(3, responses.size());
        
        assertResponse("A-1-3;", 0, responses);
        assertResponse("A-1-1;", 1, responses);
        assertResponse("A-1-2;", 2, responses);
    }
    
    
    private void assertResponse(String id, int i, List enqueuedRequests) {
        InvocationResponse response = ((InvocationResponse)enqueuedRequests.get(i));
        assertEquals(id, response.getRequestId());
        assertEquals(id+id, response.getAttachment());
    }
    
    InvocationRequest buildRequest(String requestId, int mainThreadWait,int launch, int stop, int interrupt) {
        InvocationRequest request = new InvocationRequest();
        request.setId(requestId);
        request.setRequirements(new InvocationRequirements());
        request.setAttachment(requestId+requestId);
        request.setInvocation(new ThreadLauncherInvocation(requestId, "CL1",  mainThreadWait, launch, stop, interrupt));
        return request;
    }
    
    private static final class HandlerToList implements InvocationResultHandler {
        private List responses;
        private WaitFlag responseArrived;
        
        public HandlerToList(List responses, WaitFlag responseArrived) {
            this.responses = responses;
            this.responseArrived = responseArrived;
        }

        public boolean handleResult(InvocationResponse response) {
            responses.add(response);
            responseArrived.set();
            return true;
        }
    }

}
