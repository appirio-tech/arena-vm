/*
 * BusRemoteInvokerTest
 * 
 * Created Nov 2, 2007
 */
package com.topcoder.shared.messagebus.invoker;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.topcoder.shared.exception.BaseLocalizableException;
import com.topcoder.shared.i18n.Message;
import com.topcoder.shared.messagebus.BusFactory;
import com.topcoder.shared.messagebus.jms.JMSConfigurationParser;
import com.topcoder.shared.messagebus.jms.activemq.ActiveMQBusFactory;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BusRemoteInvokerTest extends TestCase {
    
    private BusRemoteInvoker invoker;
    private BusRemoteInvocationListener listener;
    private List messages;

    protected void setUp() throws Exception {
        System.setProperty("VM_INSTANCE_ID", "testVM");
        BusFactory.configureFactory(new ActiveMQBusFactory(new JMSConfigurationParser().getConfiguration(BusRemoteInvokerTest.class.getResourceAsStream("test-config.xml"))));
        invoker = new BusRemoteInvoker("test-mod", "test", BusFactory.getFactory().createRequestPublisher("test-cfg-req", "test-mod"));
        listener = new BusRemoteInvocationListener("test-mod", "test", BusFactory.getFactory().createRequestListener("test-cfg-res", "test-mod"));
        messages = Collections.synchronizedList(new LinkedList());
        
        
        listener.registerActionProcessor("test", "call", new BusRemoteInvocationListener.ActionProcessor() {
            public Object process(String action, Map namedArguments) throws Exception {
                System.out.println("Action: "+action+ "args = "+ namedArguments);
                messages.add(namedArguments);
                return namedArguments;
            }
        });
        
        listener.registerActionProcessor("test", "exception", new BusRemoteInvocationListener.ActionProcessor() {
            public Object process(String action, Map namedArguments) throws Exception {
                System.out.println("Action: "+action+ "args = "+ namedArguments);
                throw new BaseLocalizableException(new Message("Exception_Message"));
            }
        });

        listener.registerActionProcessor("test", "sysexception", new BusRemoteInvocationListener.ActionProcessor() {
            public Object process(String action, Map namedArguments) throws Exception {
                System.out.println("Action: "+action+ "args = "+ namedArguments);
                throw new RuntimeException("RunTimeException Message");
            }
        });
    }
    
    protected void tearDown() throws Exception {
        listener.stop();
        invoker.release();
        BusFactory.getFactory().release();
        System.gc();
    }
    
    public void testInitialConnection() throws Exception {
        listener.start();
    }
    
    public void testInvoke() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Map> result = invoker.<Map>invoke("call", srcMap);
        Map map = result.get(1000, TimeUnit.MILLISECONDS);
        assertTrue(map.equals(srcMap));
        checkMap(srcMap);
    }
   
    public void testInvokeAck() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Map> result = invoker.<Map>invokeAck("call", srcMap);
        Object v = result.get(1000, TimeUnit.MILLISECONDS);
        assertNull(v);
        checkMap(srcMap);
    }
    
    public void testInvokeVoid() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Void> result = invoker.invokeVoid("call", srcMap);
        Object v = result.get(1000, TimeUnit.MILLISECONDS);
        assertNull(v);
        checkMap(srcMap);
    }

    public void testInvokeAsync() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        invoker.invokeAsync("call", srcMap);
        checkMap(srcMap);
    }

    
    
    public void testInvokeBE() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Map> result = invoker.<Map>invoke("exception", srcMap);
        try {
            result.get(1000, TimeUnit.MILLISECONDS);
            fail("Expected exception");
        } catch (ExecutionException e) {
            RemoteInvocationException rie = ((RemoteInvocationException) e.getCause());
            assertEquals("Exception_Message", rie.getRemoteLocalizableMessage().getKey());
            assertEquals(BaseLocalizableException.class.getName(), rie.getRemoteExceptionClassName());
            assertEquals(rie.getRemoteMessage(), "Exception_Message");
            assertTrue(rie.getRemoteStackTrace().startsWith(BaseLocalizableException.class.getName()+": Exception_Message"));
        }
    }
   
    public void testInvokeAckBE() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Map> result = invoker.<Map>invokeAck("exception", srcMap);
        Object v = result.get(1000, TimeUnit.MILLISECONDS);
        assertNull(v);
    }
    
    public void testInvokeVoidBE() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Void> result = invoker.invokeVoid("exception", srcMap);
        try {
            result.get(1000, TimeUnit.MILLISECONDS);
            fail("Expected exception");
        } catch (ExecutionException e) {
            RemoteInvocationException rie = ((RemoteInvocationException) e.getCause());
            assertEquals("Exception_Message", rie.getRemoteLocalizableMessage().getKey());
            assertEquals(BaseLocalizableException.class.getName(), rie.getRemoteExceptionClassName());
            assertEquals(rie.getRemoteMessage(), "Exception_Message");
            assertTrue(rie.getRemoteStackTrace().startsWith(BaseLocalizableException.class.getName()+": Exception_Message"));
        }
    }

    public void testInvokeAsyncBE() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        invoker.invokeAsync("exception", srcMap);
    }

    
    
    
    public void testInvokeSE() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Map> result = invoker.<Map>invoke("sysexception", srcMap);
        try {
            result.get(1000, TimeUnit.MILLISECONDS);
            fail("Expected exception");
        } catch (ExecutionException e) {
            RemoteInvocationException rie = ((RemoteInvocationException) e.getCause());
            assertEquals(RuntimeException.class.getName(), rie.getRemoteExceptionClassName());
            assertEquals(rie.getRemoteMessage(), "RunTimeException Message");
            assertTrue(rie.getRemoteStackTrace().startsWith("java.lang.RuntimeException: RunTimeException Message"));
        }
    }
   
    public void testInvokeAckSE() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Map> result = invoker.<Map>invokeAck("sysexception", srcMap);
        Object v = result.get(1000, TimeUnit.MILLISECONDS);
        assertNull(v);
    }
    
    public void testInvokeVoidSE() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        Future<Void> result = invoker.invokeVoid("sysexception", srcMap);
        try {
            result.get(1000, TimeUnit.MILLISECONDS);
            fail("Expected exception");
        } catch (ExecutionException e) {
            RemoteInvocationException rie = ((RemoteInvocationException) e.getCause());
            assertEquals(RuntimeException.class.getName(), rie.getRemoteExceptionClassName());
            assertEquals(rie.getRemoteMessage(), "RunTimeException Message");
            assertTrue(rie.getRemoteStackTrace().startsWith("java.lang.RuntimeException: RunTimeException Message"));
        }
    }

    public void testInvokeAsyncSE() throws Exception {
        listener.start();
        Map srcMap = buildMap();
        invoker.invokeAsync("sysexception", srcMap);
    }
    
    private void checkMap(Map srcMap) {
        waitThread(100);
        assertEquals(srcMap, messages.get(0));
    }
    
    private Map buildMap() {
        HashMap map = new HashMap();
        map.put("arg1",new Integer(1));
        map.put("arg2", "Arg2-value");
        map.put("arg3", new Double(3));
        return map;
    }
    
    private void waitThread(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
