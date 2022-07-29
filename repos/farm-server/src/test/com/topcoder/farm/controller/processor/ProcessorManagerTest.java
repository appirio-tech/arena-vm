/*
 * ProcessorManagerTest
 * 
 * Created 08/09/2006
 */
package com.topcoder.farm.controller.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.topcoder.farm.controller.dao.DAOFactory;
import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.InvocationIdDequeuer;
import com.topcoder.farm.processor.api.ProcessorInvocationRequest;
import com.topcoder.farm.processor.api.ProcessorInvocationResponse;
import com.topcoder.farm.processor.api.ProcessorNodeCallback;
import com.topcoder.farm.shared.util.Pair;

/**
 * Test case for ProcessorManager class
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorManagerTest extends TestCase {
    private static final String DEFAULT_PROC_NAME = "1";
    private ProcessorManager manager;
    private Map<String, Long> indexes = new HashMap();

    protected void setUp() throws Exception {
        DAOFactory.configureInstance(new MockDAOFactory());
        manager = new ProcessorManager(buildDequeuer());
    }
    
    protected void tearDown() throws Exception {
        manager.release();
        indexes.clear();
    }
    
    /**
     * Tests that removing a processor not registered returns false
     */
    public void testRemoveNotRegistered() {
        assertFalse(manager.removeProcessor(DEFAULT_PROC_NAME));
    }
    
    /**
     * Tests that adding a processor, really add it and 
     * that no invocation is assigned and than when removed,
     * the unregistered method of the callback is not invoked 
     */
    public void testAdd() throws Exception {
        TestNodeCallback cb = buildCallback(DEFAULT_PROC_NAME);
        manager.addProcessor(DEFAULT_PROC_NAME, cb);
        assertEquals(0, cb.reportedItems.size());
        assertTrue(manager.removeProcessor(DEFAULT_PROC_NAME));
        assertEquals(0, cb.reportedItems.size());
    }
    
    /**
     * Tests that after removing a processor, no more assignamets occurs even 
     * if setProcessorAsAvailable is called. 
     */
    public void test2AddRemovePrevious() throws Exception {
        TestNodeCallback cb = buildCallback(DEFAULT_PROC_NAME);
        TestNodeCallback cb2 = buildCallback("2");
        manager.addProcessor(DEFAULT_PROC_NAME, cb);
        manager.addProcessor(DEFAULT_PROC_NAME, cb2);
        manager.setProcessorAsAvailable(DEFAULT_PROC_NAME, true);
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
        assertEquals(1, cb2.reportedItems.size());
        assertTrue(manager.removeProcessor(DEFAULT_PROC_NAME));
        assertEquals(1, cb.reportedItems.get(0).getFst().intValue());
        assertEquals(0, cb2.reportedItems.get(0).getFst().intValue());
    }
    
    /**
     * Tests that seting a processor as available produces an invocation on 
     * the method processInvocationRequest of the callback. 
     */
    public void testSetAvailableReleasesOneInvocation() throws Exception {
        TestNodeCallback cb = buildCallback(DEFAULT_PROC_NAME);
        manager.addProcessor(DEFAULT_PROC_NAME, cb);
        manager.setProcessorAsAvailable(DEFAULT_PROC_NAME, true);
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
        assertTrue(manager.removeProcessor(DEFAULT_PROC_NAME));
    }

    /**
     * Tests that multiple calls to processorReceivedResponse releases multiples invocations 
     * to method processInvocationRequest of the callback.  
     */
    public void testManyReceivedResponseReleasesManyInvocations() throws Exception {
        TestNodeCallback cb = buildCallback(DEFAULT_PROC_NAME);
        manager.addProcessor(DEFAULT_PROC_NAME, cb);
        manager.setProcessorAsAvailable(DEFAULT_PROC_NAME, true);
        for (int i = 0; i < 5; i++) {
            manager.processorReceivedResponse(DEFAULT_PROC_NAME, new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
            Thread.sleep(100);
        }
        Thread.sleep(200);
        assertEquals(6, cb.reportedItems.size());
        assertTrue(manager.removeProcessor(DEFAULT_PROC_NAME));
    }
    
    
    /**
     * Tests that after setting processor as unavailable no more invocations are sent using  
     * the method processInvocationRequest of the callback.  
     */
    public void testSetUnavailableAvoidsNewInvocationForProcessor() throws Exception {
        TestNodeCallback cb = buildCallback(DEFAULT_PROC_NAME);
        manager.addProcessor(DEFAULT_PROC_NAME, cb);
        manager.setProcessorAsAvailable(DEFAULT_PROC_NAME, true);
        for (int i = 0; i < 5; i++) {
            manager.processorReceivedResponse(DEFAULT_PROC_NAME, new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
            Thread.sleep(100);
        }
        manager.setProcessorAsAvailable(DEFAULT_PROC_NAME, false);
        for (int i = 0; i < 5; i++) {
            manager.processorReceivedResponse(DEFAULT_PROC_NAME, new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
            Thread.sleep(100);
        }
        Thread.sleep(200);
        assertEquals(6, cb.reportedItems.size());
        assertTrue(manager.removeProcessor(DEFAULT_PROC_NAME));
    }
    
    /**
     * Tests that multiple calls to processorReceivedResponse with difference takeResources 
     * releases multiples invocations (sum(taken)+intialSize) to method processInvocationRequest of the callback.  
     */
    public void testReleasingMoreResourcesGeneratesReleasesMoreTasks() throws Exception {
        TestNodeCallback cb = buildCallback("3");
        manager.addProcessor("3", cb);
        manager.setProcessorAsAvailable("3", true);
        manager.processorReceivedResponse("3", new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
        manager.processorReceivedResponse("3", new ProcessorInvocationResponse(Long.valueOf(1), 2, null));
        manager.processorReceivedResponse("3", new ProcessorInvocationResponse(Long.valueOf(1), 3, null));
        Thread.sleep(200);
        assertEquals(9, cb.reportedItems.size());
        assertTrue(manager.removeProcessor("3"));
    }

    /**
     * Tests that multiple calls to processorReceivedResponse with difference takeResources 
     * releases multiples invocations (sum(taken)+intialSize) to method processInvocationRequest of the callback.  
     */
    public void testRequiredResourcesGreaterThanOne() throws Exception {
        MockDAOFactory instance = (MockDAOFactory) DAOFactory.getInstance();
        instance.setRequiredResourcesForTasks(3);
        TestNodeCallback cb = buildCallback("4");
        manager.addProcessor("4", cb);
        manager.setProcessorAsAvailable("4", true);
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
        manager.processorReceivedResponse("4", new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
        manager.processorReceivedResponse("4", new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
        Thread.sleep(200);
        assertEquals(2, cb.reportedItems.size());
        assertTrue(manager.removeProcessor("4"));
    }

    /**
     * Tests 0 uses forces full resources assignatio for task 
     */
    public void testZeroForceFullResourceAssignation() throws Exception {
        MockDAOFactory instance = (MockDAOFactory) DAOFactory.getInstance();
        instance.setRequiredResourcesForTasks(0);
        TestNodeCallback cb = buildCallback("4");
        manager.addProcessor("4", cb);
        manager.setProcessorAsAvailable("4", true);
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
        manager.processorReceivedResponse("4", new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
        manager.processorReceivedResponse("4", new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
        manager.processorReceivedResponse("4", new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
        manager.processorReceivedResponse("4", new ProcessorInvocationResponse(Long.valueOf(1), 1, null));
        Thread.sleep(200);
        assertEquals(2, cb.reportedItems.size());
        assertTrue(manager.removeProcessor("4"));
    }
    
    /**
     * Tests that after removing a processor, no more assignaments occur even 
     * if setsetProcessorAsAvailable is called 
     */
    public void testAfterRemoveNoMoreAssignaments() throws Exception {
        TestNodeCallback cb = buildCallback(DEFAULT_PROC_NAME);
        manager.addProcessor(DEFAULT_PROC_NAME, cb);
        manager.setProcessorAsAvailable(DEFAULT_PROC_NAME, true);
        Thread.sleep(200);
        manager.removeProcessor(DEFAULT_PROC_NAME);
        manager.setProcessorAsAvailable(DEFAULT_PROC_NAME, true);
        Thread.sleep(200);
        assertEquals(1, cb.reportedItems.size());
    }

    private TestNodeCallback buildCallback(String id) {
        indexes.put(id, new Long(0));
        return new TestNodeCallback();
    }

    private DequeuerObtainer buildDequeuer() {
        return new DequeuerObtainer() {
            public InvocationIdDequeuer getDequeuerFor(final ProcessorProperties processorData) {
                return new InvocationIdDequeuer() {
                    public Long dequeueInvocationId() {
                        String processorId = processorData.getName();
                        long v = indexes.get(processorId ).longValue()+1;
                        indexes.put(processorId, new Long(v));
                        return new Long(Long.parseLong(processorId)*100+v);
                    }
                };
            }
        };
    }
    
    
    private static class TestNodeCallback implements ProcessorNodeCallback {
        List<Pair<Integer,Object>> reportedItems = new ArrayList();
        
        public void processInvocationRequest(ProcessorInvocationRequest request) {
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(0), request));
        }
        public void unregistered(String cause) {
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(1), cause));
        }
        public void disconnect(String cause) {
            //TODO TEST
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(2), cause));
        }
        public void shutdown() {
            //TODO TEST
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(3), null));
        }
        public void forceReRegistration() {
            //TODO TEST
            reportedItems.add(new Pair<Integer, Object>(Integer.valueOf(4), null));
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
