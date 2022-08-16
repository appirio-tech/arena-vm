/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * QueueManagerTest
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.topcoder.farm.controller.model.InvocationData;
import com.topcoder.farm.controller.model.ProcessorData;
import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.QueueManagerStatus.QueueManagerItemStatus;
import com.topcoder.farm.shared.expression.BooleanExpression;
import com.topcoder.farm.shared.expression.Expression;
import com.topcoder.farm.shared.expression.Expressions;
import com.topcoder.farm.shared.invocation.InvocationRequirements;
import com.topcoder.farm.test.util.MTTestCase;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #createBothQueue()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class QueueManagerTest extends MTTestCase {
    private QueueManager manager;
    private InMemoryQueueServices services;
    private ProcessorProperties properties1;
    private ProcessorProperties properties2;
    private InvocationIdDequeuer queue1;
    private InvocationIdDequeuer queue2;
    private QueueDataAssembler assembler = new QueueDataAssembler();

    
    /**
     * Test setUp.
     * 
     * Creates a QueueManager that uses a InMemoryQueueServices.
     * the service will provide two active ProcessorProperties
     *      
     *    - Id=Long(1), Props[processor-type=Integer(1),memory=Integer(1024)]
     *    - Id=Long(2), Props[processor-type=Integer(2),memory=Integer(512)]
     *    
     *  The manager is not intialized in this method, so each test method
     *  should call initialize method.
     */
    protected void setUp() throws Exception {
        super.setUp();
        services = new InMemoryQueueServices();
        properties1 = new ProcessorProperties();
        properties1.setId(new Long(1));
        properties1.setDescription("Description 1");
        properties1.addProperty("processor-type", new Integer(1));
        properties1.addProperty("memory", new Integer(1024));
        services.addProperties(properties1);
        properties2 = new ProcessorProperties();
        properties2.setId(new Long(2));
        properties2.setDescription("Description 2");
        properties2.addProperty("processor-type", new Integer(2));
        properties2.addProperty("memory", new Integer(512));
        services.addProperties(properties2);
        manager = new QueueManager(services);
    }


    /**
     * Tests that when the manager is initialized with 2 invocations for processor
     * 1. Both invocations can be obtained from queue1 in the proper order
     */
    public void testBasicOrder() throws Exception {
        //Adding 2 test with same priority that can run in processor1
        services.addInvocationData(buildInvocationQueueData(1, 5, 1, 512));
        services.addInvocationData(buildInvocationQueueData(2, 5, 1, 1024));
        
        manager.initialize();
        createBothQueue();
        
        run(new Runnable() {
            public void run() {
                try {
                    assertEquals(new Long(1), queue1.dequeueInvocationId());
                    assertEquals(new Long(2), queue1.dequeueInvocationId());
                } catch (InterruptedException e) {
                    fail("getNextTaskId never exited");
                }
            }
        });
        startAll();
        waitAll(200);
    }

    
    /**
     * Tests that when the manager is initialized with 2 invocations for processor
     * 1. Both invocations can be obtained from queue1 in the proper order. In this case
     * the priority of the second invocation is higher than the priority of the first one
     */
    public void testInvertOrder() throws Exception {
        //Adding 2 test in inverted priority order. Both  can run in processor1
        services.addInvocationData(buildInvocationQueueData(1, 10, 1, 512));
        services.addInvocationData(buildInvocationQueueData(2, 5, 1, 1024));
        manager.initialize();
        createBothQueue();
        
        run(new Runnable() {
            public void run() {
                try {
                    assertEquals(new Long(2), queue1.dequeueInvocationId());
                    assertEquals(new Long(1), queue1.dequeueInvocationId());
                } catch (InterruptedException e) {
                    fail("getNextTaskId never exited");
                }            }
        });
        startAll();
        waitAll(200);
    }
    
    
    /**
     * Tests that when no invocation is available on the queue1 the thread
     * is blocked, even if another invocation is available on queue2
     */
    public void testWaitIfNoDataAvailable() throws Exception {
        //Adding 2 test with the same priority. One for each processor
        services.addInvocationData(buildInvocationQueueData(1, 5, 2, 512));
        services.addInvocationData(buildInvocationQueueData(2, 5, 1, 512));
        manager.initialize();
        createBothQueue();
        
        run(new Runnable() {
            public void run() {
                try {
                    try {
                        assertEquals(new Long(2), queue1.dequeueInvocationId());
                    } catch (Exception e) {
                        fail("getNextTaskId never exited");
                    }
                    queue1.dequeueInvocationId();
                } catch (InterruptedException e) {
                    //Expected interrupt
                }
            }
        });
        startAll();
        waitAll(200);
    }
    
    
    /**
     * Tests that the other of invocation than can be distributed on both processors
     * are given in the proper carelless the queue being polled  
     */
    public void testSequentialDistributionOnProcessor() throws Exception {
        //Adding 4 test availables for both processor. 2 priority = 5 and 2 priority = 1
        services.addInvocationData(buildInvocationQueueData(1, 5, 0, 512));
        services.addInvocationData(buildInvocationQueueData(2, 5, 0, 512));
        services.addInvocationData(buildInvocationQueueData(3, 1, 0, 512));
        services.addInvocationData(buildInvocationQueueData(4, 1, 0, 512));
        manager.initialize();
        createBothQueue();
        
        run(new Runnable() {
            public void run() {
                try {
                    //Expecting higher priorities first, independently of the processor
                    assertEquals(new Long(3), queue1.dequeueInvocationId());
                    assertEquals(new Long(4), queue2.dequeueInvocationId());
                    assertEquals(new Long(1), queue2.dequeueInvocationId());
                    assertEquals(new Long(2), queue1.dequeueInvocationId());
                } catch (Exception e) {
                    fail("getNextTaskId never exited");
                }
            }
        });
        startAll();
        waitAll(200);
    }

    
    /**
     * Tests that when queue manager is accessed concurrently, the invocations
     * are properly distributed among queues
     */
    public void testMTExecution() throws Exception {
        //Adding 4 test availables for both processor. two of priority = 5 and two priority = 1
        //Plus one test for each processor of priority = 1
        services.addInvocationData(buildInvocationQueueData(5, 5, 0, 512));
        services.addInvocationData(buildInvocationQueueData(6, 5, 0, 512));
        services.addInvocationData(buildInvocationQueueData(1, 1, 0, 512)); 
        services.addInvocationData(buildInvocationQueueData(2, 1, 0, 512));
        services.addInvocationData(buildInvocationQueueData(3, 1, 0, 1024));
        services.addInvocationData(buildInvocationQueueData(4, 1, 2, 512));
        manager.initialize();
        createBothQueue();
        
        final List p1List = new ArrayList();
        final List p2List = new ArrayList(); 
        run(new Runnable() {
            public void run() {
                try {
                    while(true) {
                        p1List.add(queue1.dequeueInvocationId());
                        Thread.yield();
                    }
                } catch (Exception e) {
                }
            }
        });
        
        run(new Runnable() {
            public void run() {
                try {
                    while(true) {
                        p2List.add(queue2.dequeueInvocationId());
                        Thread.yield();
                    }
                } catch (Exception e) {
                }
            }
        });
        startAll();
        waitAll(200);
        assertFalse(p1List.contains(new Integer(4)));
        assertFalse(p2List.contains(new Integer(3)));
        assertEquals(6, p1List.size()+p2List.size());
        assertFalse(p1List.removeAll(p2List));
        assertFalse(p2List.removeAll(p1List));
        isOrdered(p1List);
        isOrdered(p2List);
    }

    
    /**
     * Tests that polling and enqueuing, the invocation are distributed propertly among the
     * queues
     */
    public void testEnqueue() throws Exception {
        //Adding 4 test availables for both processor. two of priority = 5 and two priority = 1
        //Plus one test for each processor of priority = 1
        manager.initialize();
        createBothQueue();
        
        final List p1List = new ArrayList();
        final List p2List = new ArrayList(); 
        run(new Runnable() {
            public void run() {
                try {
                    while(true) {
                        p1List.add(queue1.dequeueInvocationId());
                        Thread.yield();
                    }
                } catch (Exception e) {
                }
            }
        });
        
        run(new Runnable() {
            public void run() {
                try {
                    while(true) {
                        p2List.add(queue2.dequeueInvocationId());
                        Thread.yield();
                    }
                } catch (Exception e) {
                }
            }
        });
        startAll();
        
        manager.enqueue(buildInvocationData(1, 1, 2, 512));
        manager.enqueue(buildInvocationData(2, 1, 1, 512));
        manager.enqueue(buildInvocationData(3, 1, 0, 1024));
        manager.enqueue(buildInvocationData(4, 1, 0, 512));
        manager.enqueue(buildInvocationData(5, 1, 1, 0));
        manager.enqueue(buildInvocationData(6, 1, 2, 0));
        
        waitAll(200);
        assertFalse(p1List.contains(new Integer(1)));
        assertFalse(p1List.contains(new Integer(6)));
        assertFalse(p2List.contains(new Integer(2)));
        assertFalse(p2List.contains(new Integer(3)));
        assertFalse(p2List.contains(new Integer(5)));

        assertEquals(6, p1List.size()+p2List.size());
        assertFalse(p1List.removeAll(p2List));
        assertFalse(p2List.removeAll(p1List));
        isOrdered(p1List);
        isOrdered(p2List);
    }
    
    
    /**
     * Tests that an exception is thrown if no matching processor properties exists 
     * in the queue manager
     */
    public void testEnqueueException() throws Exception {
        manager.initialize();
        createBothQueue();
        try {
            manager.enqueue(buildInvocationData(1, 1, 2, 1024));
            fail("Expected exception");
        } catch (UnavailableProcessorForRequirementsException e) {
            //Expected exception
        }
    }

    public void testStatus() throws Exception {
        manager.initialize();
        createBothQueue();

        manager.enqueue(buildInvocationData(1, 1, 2, 512));
        manager.enqueue(buildInvocationData(2, 1, 1, 512));
        manager.enqueue(buildInvocationData(3, 1, 0, 1024));
        manager.enqueue(buildInvocationData(4, 1, 0, 512));
        manager.enqueue(buildInvocationData(5, 1, 1, 0));
        manager.enqueue(buildInvocationData(6, 1, 2, 0));
        
        QueueManagerStatus status = manager.getStatus(true);
        assertEquals(6, status.getSize());
        
        HashSet<Long> set1 = new HashSet<Long>(Arrays.asList(new Long[] {new Long(2), new Long(3), new Long(4), new Long(5)}));
        HashSet<Long> set2 = new HashSet<Long>(Arrays.asList(new Long[] {new Long(1), new Long(4), new Long(6)}));
        
        HashSet<Long> allSet = new HashSet<Long>(set1);
        allSet.addAll(set2);
        assertEquals(6, status.getSize());
        assertEquals(allSet, new HashSet(status.getEnqueueItems()));
        assertQueueEqualIds(status.getQueueStatus(), 1, set1);
        assertQueueEqualIds(status.getQueueStatus(), 2, set2);
        
    }
    

    /**
     * Test functionality of removeAll
     * @throws Exception
     */
    public void testRemoveAll() throws Exception {
        manager.initialize();
        createBothQueue();

        manager.enqueue(buildInvocationData(1, 1, 2, 512));
        manager.enqueue(buildInvocationData(2, 1, 1, 512));
        manager.enqueue(buildInvocationData(3, 1, 0, 1024));
        manager.enqueue(buildInvocationData(4, 1, 0, 512));
        manager.enqueue(buildInvocationData(5, 1, 1, 0));
        manager.enqueue(buildInvocationData(6, 1, 2, 0));
        
        HashSet<Long> remainSet = new HashSet<Long>(Arrays.asList(new Long[] {new Long(1), new Long(4), new Long(6)}));
        HashSet<Long> removeSet = new HashSet<Long>(Arrays.asList(new Long[] {new Long(3), new Long(5), new Long(2)}));
        manager.removeAll(removeSet);
        
        
        QueueManagerStatus status = manager.getStatus(true);
        assertEquals(3, status.getSize());
        assertFalse(new HashSet(status.getEnqueueItems()).removeAll(removeSet));
        assertTrue(status.getEnqueueItems().containsAll(remainSet));
        
        for (QueueManagerItemStatus item : status.getQueueStatus().values()) {
            for (InvocationQueueHeaderData data : item.getItems()) {
                assertFalse(removeSet.contains(new Long(data.getId())));
            }
        }
    }

    
    private void assertQueueEqualIds(Map<Long, QueueManagerItemStatus> queueStatus, int i, HashSet<Long> set) {
        for (InvocationQueueHeaderData data : queueStatus.get(new Long(i)).getItems()) {
            assertTrue(set.contains(new Long(data.getId())));
        }
        for (Long id : set) {
            boolean belongs = false;
            for (InvocationQueueHeaderData data : queueStatus.get(new Long(i)).getItems()) {
                belongs |= data.getId() == id.longValue();
            }
            assertTrue(belongs);
        }
    }
    
    /**
     * Helper method to check is the list ordered
     */
    private boolean isOrdered(List list) {
        if (list.size() < 2) { 
            return true;
        }
        Iterator it = list.iterator();
        Comparable prev = (Comparable) it.next();
        for (; it.hasNext();) {
            Comparable obj = (Comparable) it.next();
            if (prev.compareTo(obj) > 0) {
                return false;
            }
            prev = obj;
        }
        return true;
    }

    /**
     * Retrieves from the manager both queues
     * The processor1 queue and the processor2 queue
     */
    private void createBothQueue() {
        queue1 = manager.getQueueForProcessor(properties1);
        queue2 = manager.getQueueForProcessor(properties2);
    }
    
    
    
    private InvocationQueueData buildInvocationQueueData(int id, int priority, int cpuType, int minMem) {
        InvocationQueueData data = new InvocationQueueData();
        InvocationQueueHeaderData header = new InvocationQueueHeaderData(
                    new Long(id), 
                    new Date(),
                    new Date(System.currentTimeMillis()+ 100000),
                    priority);
        data.setHeader(header);
        InvocationRequirements requirements = buildRequirements(cpuType, minMem);
        data.setRequirements(requirements);
        return data;
    }


    private InvocationQueueData buildInvocationData(int id, int priority, int cpuType, int minMem) {
        InvocationData data = new InvocationData();
        data.setId(new Long(id));
        data.setPriority(priority);
        data.setReceivedDate(new Date());
        data.setDropDate(new Date(System.currentTimeMillis()+ 100000));
        data.setAsPending();
        InvocationRequirements requirements = buildRequirements(cpuType, minMem);
        data.setRequirements(requirements);
        return assembler.buildQueueDataFor(data);
    }
    
    private InvocationRequirements buildRequirements(int cpuType, int minMem) {
        InvocationRequirements requirements = new InvocationRequirements();
        Expression expression1 = BooleanExpression.TRUE;
        Expression expression2 = BooleanExpression.TRUE;
        if (cpuType != 0) {
            expression1 = Expressions.eq("processor-type", new Integer(cpuType));
        }
        if (minMem != 0) {
            expression2 = Expressions.ge("memory", new Integer(minMem));
        }
        requirements.setFilterExpression(Expressions.and(expression1, expression2));
        return requirements;
    }
}
