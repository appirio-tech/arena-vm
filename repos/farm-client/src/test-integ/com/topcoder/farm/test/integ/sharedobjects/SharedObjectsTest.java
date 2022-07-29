/*
 * SharedObjectsTest
 * 
 * Created 09/22/2006
 */
package com.topcoder.farm.test.integ.sharedobjects;


/**
 * In order to run this test case, a controller must be running and configured 
 * to listen clients. Additionally only processor PR1-1 must be running and connected to the
 * farm.
 * 
 * Test case of SharedObjects storage features
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SharedObjectsTest  { // extends TestCase {
//    private FarmFactory factory;
//    private FarmInvoker invoker;
//
//    public SharedObjectsTest() {
//    }
//
//    protected void setUp() throws Exception {
//        super.setUp();
//        try {
//            ClientTestIntegConfigurator.configure();
//            factory = FarmFactory.getInstance();
//            invoker = factory.getInvoker("CL1");
//        } catch (Exception e) {
//            throw (IllegalStateException) new IllegalStateException()
//                    .initCause(e);
//        }
//        invoker.getClientNode().cancelPendingRequests();
//    }
//    
//    protected void tearDown() throws Exception {
//        FarmFactory.releaseInstance();
//        super.tearDown();
//    }
//
//    public void testStoredObjectCanBeUsedInOneInvocation() throws Exception {
//        invoker.storeSharedObject("text-value", "STRING_TEST");
//
//        InvocationRequest inv = buildInvocationRequest("I1;", "NOT_USED", true);
//        inv.addSharedObjectRef("text", "text-value");
//        AsyncInvocationResponse aresponse = invoker.scheduleInvocationSync(inv);
//        InvocationResponse response = aresponse.get(5000);
//        TestInvocation result = getTestInvocationResult(response);
//        assertEquals("STRING_TEST", result.getText());
//    }
//
//    public void testExceptionOnDupKey() throws Exception {
//        invoker.storeSharedObject("text-value", "STRING_TEST");
//        try {
//            invoker.storeSharedObject("text-value", "DUP-VALUE");
//            fail("Expected Exception");
//        } catch (DuplicatedIdentifierException e) {
//            //Expected
//        }
//    }
//
//    public void testExceptionRemovingReferenced() throws Exception {
//        invoker.storeSharedObject("text-value", "STRING_TEST");
//        InvocationRequest inv = buildInvocationRequest("I1;", "NOT_USED", false);
//        inv.addSharedObjectRef("text", "text-value");
//        invoker.scheduleInvocation(inv);
//        try {
//            invoker.removeSharedObjects("text-value");
//            fail("Expected exception");
//        } catch (SharedObjectReferencedException e) {
//            // Expected
//        }
//    }
//    
//    public void testCountSharedObjects() throws Exception {
//        invoker.storeSharedObject("text-value-1-1", "STRING_TEST");
//        invoker.storeSharedObject("text-value-1-2", "STRING_TEST");
//        invoker.storeSharedObject("text-value-2-1", "STRING_TEST");
//        invoker.storeSharedObject("text-value-2-2", "STRING_TEST");
//        invoker.storeSharedObject("text-other", "STRING_TEST");
//        Integer c1 = invoker.countSharedObjects("text-value-1");
//        assertEquals(new Integer(2), c1);
//        Integer c2 = invoker.countSharedObjects("text-value");
//        assertEquals(new Integer(4), c2);
//        Integer c3 = invoker.countSharedObjects("text-");
//        assertEquals(new Integer(5), c3);
//    }
//    
//    
//    public void testRemoveSharedObjects() throws Exception {
//        invoker.storeSharedObject("text-value-1-1", "STRING_TEST");
//        invoker.storeSharedObject("text-value-1-2", "STRING_TEST");
//        invoker.storeSharedObject("text-value-2-1", "STRING_TEST");
//        invoker.storeSharedObject("text-value-2-2", "STRING_TEST");
//        invoker.storeSharedObject("text-other", "STRING_TEST");
//        
//        invoker.removeSharedObjects("text-value-1-1");
//        Integer c1 = invoker.countSharedObjects("text-value-1");
//        assertEquals(new Integer(1), c1);
//        invoker.removeSharedObjects("text-value");
//        Integer c2 = invoker.countSharedObjects("text-value");
//        assertEquals(new Integer(0), c2);
//        Integer c3 = invoker.countSharedObjects("text-");
//        assertEquals(new Integer(1), c3);
//    }
//
//    private TestInvocation getTestInvocationResult(InvocationResponse response) {
//        assertFalse(response.getResult().isExceptionThrown());
//        return (TestInvocation) response.getResult().getReturnValue();
//    }
//
//    
//    InvocationRequest buildInvocationRequest(String requestId, String text, boolean accept) {
//        InvocationRequest request = new InvocationRequest();
//        request.setId(requestId);
//        request.setRequirements(new InvocationRequirements());
//        if (!accept) {
//            request.getRequirements().setFilterExpression(Expressions.eq(TestDataPropertiesHelper.PROC_PROP_ID, new Integer(4)));
//            
//        }
//        request.setInvocation(new TestInvocation(requestId, "CL1", text,0, null));
//        return request;
//    }
}
