/*
 * CompositeListenerTest
 *
 * Created 04/10/2007
 */
package com.topcoder.server.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * Simple unit test for CompositeListener
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class CompositeListenerTest extends TestCase {
    private List processorEvents;
    private ListenerInterface listener;
    private List[] listenerEvents;
    private ListenerInterface l1;
    private ListenerInterface l2;
    private ListenerInterface l3;

    protected void setUp() throws Exception {
        CompositeListenerBuilder builder = new CompositeListenerBuilder(buildProcessor());
        l1 = buildListener(0, 0, 5, builder.getProcessorForInnerListeners());
        builder.addListener(l1);
        l2 = buildListener(1, 6, 10, builder.getProcessorForInnerListeners());
        builder.addListener(l2);
        l3 = buildListener(2, 11, 20, builder.getProcessorForInnerListeners());
        builder.addListener(l3);
        processorEvents = new ArrayList();
        listener = builder.buildListener();
        listenerEvents = new List[3];
        for (int i = 0; i < listenerEvents.length; i++) {
            listenerEvents[i] = new ArrayList();

        }
    }

    public void testStart() throws Exception {
        listener.start();
        checkProcessorEvents("listener,start");
        checkListenerEvents("start");
    }

    public void testStop() throws Exception {
        listener.stop();
        checkProcessorEvents("listener,stop");
        checkListenerEvents("stop");
    }


    public void testBanIP() throws Exception {
        listener.banIP("x");
        checkListenerEvents("banIP");
    }

    public void testSend() throws Exception {
        listener.send(0, "0");
        listener.send(6, "6");
        listener.send(11, "11");
        checkListenerEvents("send");
    }

    public void testShutdown() throws Exception {
        listener.shutdown(0);
        listener.shutdown(6);
        listener.shutdown(11);
        checkListenerEvents("shutdown");
    }

    public void testShutdownAndNotify() throws Exception {
        listener.shutdown(0);
        listener.shutdown(6);
        listener.shutdown(11);
        checkListenerEvents("shutdown");
    }


    public void testAccum() throws Exception {
        assertEquals(listener.getConnectionsSize(), l1.getConnectionsSize()+l2.getConnectionsSize()+l3.getConnectionsSize());
        assertEquals(listener.getInTrafficSize(), l1.getInTrafficSize()+l2.getInTrafficSize()+l3.getInTrafficSize());
        assertEquals(listener.getOutTrafficSize(), l1.getOutTrafficSize()+l2.getOutTrafficSize()+l3.getOutTrafficSize());
        assertEquals(listener.getResponseQueueSize(), l1.getResponseQueueSize()+l2.getResponseQueueSize()+l3.getResponseQueueSize());
    }


    private ListenerInterface buildListener(final int id, final int min, final int max, ProcessorInterface proc) {
        return new ListenerInterface() {


            public void stop() {
                listenerEvents[id].add(event(null, "stop", null));
            }

            public void start() {
                listenerEvents[id].add(event(null, "start", null));
            }

            public void shutdown(int connection_id, boolean notifyProcessor) {
                listenerEvents[id].add(event(new Integer(connection_id), "shutdown", new Boolean(notifyProcessor)));
            }

            public void shutdown(int connection_id) {
                listenerEvents[id].add(event(new Integer(connection_id), "shutdown", null));
            }

            public void send(int connection_id, Object response) {
                listenerEvents[id].add(event(new Integer(connection_id), "send", response));
            }

            public int getResponseQueueSize() {
                return 2^id;
            }

            public int getOutTrafficSize() {
                return 2^id+1000;
            }

            public int getMinConnectionId() {
                return min;
            }

            public int getMaxConnectionId() {
                return max;
            }

            public int getInTrafficSize() {
                return 2^id+10000;
            }

            public int getConnectionsSize() {
                return 2^id+100000;
            }

            public void banIP(String ipAddress) {
                listenerEvents[id].add(event(null, "banIP", ipAddress));
            }
        };
    }

    private void checkProcessorEvents(String events) {
        String[] evs = events.split(",");
        int i = 0;
        assertEquals(evs.length, processorEvents.size());
        for (Iterator it = processorEvents.iterator(); it.hasNext(); i++) {
            List l = (List) it.next();
            assertEquals(evs[i], l.get(1));
        }
    }

    private void checkListenerEvents(String events) {
        String[] evs = events.split(",");
        for (int j = 0; j < listenerEvents.length; j++) {
            int i = 0;
            for (Iterator it = listenerEvents[j].iterator(); it.hasNext(); i++) {
                List l = (List) it.next();
                assertEquals(evs[i], l.get(1));
            }
            assertEquals(evs.length, i);
        }
    }

    protected List event(Integer id, String type, Object object) {
        return Arrays.asList(new Object[] {id, type, object});
    }

    protected List event(Integer id, String type) {
        return Arrays.asList(new Object[] {id, type, null});
    }

    private ProcessorInterface buildProcessor() {
        return new ProcessorInterface() {

            public void stop() {
                processorEvents.add(event(null, "stop"));

            }

            public void start() {
                processorEvents.add(event(null, "start"));
            }


            public void setListener(ListenerInterface listener) {
                processorEvents.add(event(null, "listener", listener));
            }

            public void receive(int connection_id, Object request) {
             }

            public void newConnection(int connection_id, String remoteIP) {
                processorEvents.add(event(new Integer(connection_id), "new", remoteIP));

            }

            public void lostConnectionTemporarily(int connection_id) {
                processorEvents.add(event(new Integer(connection_id), "lost"));

            }

            public void lostConnection(int connection_id) {
                processorEvents.add(event(new Integer(connection_id), "close"));
            }

        };
    }

}
