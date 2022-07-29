/*
 * FarmFactoryTest
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;


/**
 * Test case for FarmFactory class 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class FarmFactoryTest { // extends MTTestCase { 
//    private FarmFactory factory;
//
//    protected void setUp() throws Exception {
//        super.setUp();
//        FarmFactory.configure(buildConfig());
//        factory =  FarmFactory.getInstance();
//        ClientNodeBuilderMock.reset();
//    }
//    
//    protected void tearDown() throws Exception {
//        FarmFactory.releaseInstance();
//        super.tearDown();
//    }
//
//    /**
//     * Tests that calling configure more than once throws IllegalStateException
//     */
//    public void testConfigureAgain() throws Exception {
//        try {
//            FarmFactory.configure(buildConfig());
//            fail("Expected IllegalStateException");
//        } catch (IllegalStateException e) {
//            //Expected
//        }
//    }
//
//    /**
//     * Tests that getInvoker returns an invoker for the given Id
//     */
//    public void testGetInvoker() throws Exception {
//        FarmInvoker invoker = factory.getInvoker("C1");
//        assertNotNull(invoker);
//        assertEquals("C1", invoker.getId());
//        assertEquals(1, ClientNodeBuilderMock.getCounter());
//    }
//
//    /**
//     * Tests that calling getInvoker for the same id more than once
//     * returns the same instance 
//     */
//    public void testGetInvokerSameInstance() throws Exception {
//        FarmInvoker invoker = factory.getInvoker("C1");
//        assertNotNull(invoker);
//        assertEquals("C1", invoker.getId());
//        FarmInvoker invoker2 = factory.getInvoker("C1");
//        assertTrue(invoker == invoker2);
//        assertEquals(1, ClientNodeBuilderMock.getCounter());
//    }
//
//    /**
//     * Tests that after calling release, getInvoker throws IllegalStateException
//     * @throws Exception
//     */
//    public void testRelease() throws Exception {
//        FarmInvoker invoker = factory.getInvoker("C1");
//        assertNotNull(invoker);
//        assertEquals("C1", invoker.getId());
//        FarmFactory.releaseInstance();
//        try {
//            factory.getInvoker("C1");
//            fail("Expected IllegalStateException");
//        } catch (IllegalStateException e) {
//        }
//    }
//    
//    /**
//     * Tests that when creating invokers, creation are handled without interference
//     * between them. Some synchronization is needed but they don't exclude each other during
//     * creation
//     */
//    public void testMTLockDontInterfere() throws Exception {
//        final List invokers = Collections.synchronizedList(new ArrayList(20));
//        Set invokersId = new HashSet();
//        for(int i=0; i < 20; i++) {
//            final int j = i;
//            invokersId.add("C"+j);
//            run(new Runnable() {
//                public void run() {
//                    try {
//                        invokers.add(factory.getInvoker("C"+j));
//                    } catch (RuntimeException e) {
//                        //Mock doesn't throw exceptions
//                    } catch (Exception e) {
//                        //Mock doesn't throw exceptions
//                    }
//                }
//            });
//        }
//        startTiming();
//        startAll();
//        waitAll();
//        endTiming();
//        for (Iterator it = invokers.iterator(); it.hasNext();) {
//            FarmInvoker element = (FarmInvoker) it.next();
//            assertTrue(invokersId.contains(element.getId()));
//            invokersId.remove(element.getId());
//        }
//        assertEquals(0, invokersId.size());
//        assertEquals(20, ClientNodeBuilderMock.getCounter());
//        assertTrue(getTiming() < 2000);
//    }
//    
//    
//    /**
//     * Tests that when creating invokers, creation are handled without interference
//     * between them. Some synchronization is needed but they don't exclude each other during
//     * creation. Additional when concurrent request for the same client are made, only one invoker is
//     * created and returned.
//     */
//    public void testMTLockDontInterfereMultiClient() throws Exception {
//        final List invokers = Collections.synchronizedList(new ArrayList(200));
//        Set invokersId = new HashSet(20);
//        Set invokersIdCreated = new HashSet(20);
//        IdentityHashMap identities = new IdentityHashMap(200);
//        for(int i=0; i < 20; i++) {
//            final int j = i;
//            invokersId.add("C"+j);
//            for (int h = 0; h < 10; h++) {
//                run(new Runnable() {
//                    public void run() {
//                        try {
//                            invokers.add(factory.getInvoker("C"+j));
//                        } catch (RuntimeException e) {
//                            //Mock doesn't throw exceptions
//                        } catch (Exception e) {
//                            //Mock doesn't throw exceptions
//                        }
//                    }
//                });
//            }
//        }
//        startTiming();
//        startAll();
//        waitAll();
//        endTiming();
//        for (Iterator it = invokers.iterator(); it.hasNext();) {
//            FarmInvoker element = (FarmInvoker) it.next();
//            invokersIdCreated.add(element.getId());
//            identities.put(element, new Object());
//        }
//        assertEquals(invokersId, invokersIdCreated);
//        assertEquals(20, ClientNodeBuilderMock.getCounter());
//        assertEquals(20, identities.size());
//        assertEquals(200, invokers.size());
//        assertTrue(getTiming() < 2500);
//    }
//
//    
//    private ClientConfiguration buildConfig() {
//        ClientConfiguration configuration = new ClientConfiguration();
//        configuration.setAddresses(new InetSocketAddress[] {new InetSocketAddress(10)});
//        configuration.setProcessorThreadPoolSize(1);
//        configuration.setClientNodeBuilderClassName(ClientNodeBuilderMock.class.getName());
//        return configuration;
//    }
}
