package com.topcoder.server.listener;

import junit.framework.TestCase;

public final class ListenerInterfaceTest3 extends TestCase {

    private static final int PORT = AcceptHandlerTest.PORT + 700;
    private static final ListenerFactory[] FACTORY = {
        new NBIOListenerFactory(),
    };

    private int port_id = PORT;

    public ListenerInterfaceTest3(String name) {
        super(name);
    }

    private void testManyManyAccepts(ListenerFactory factory, int port) {
        int n = 1500;
        long expected = 24;
        ListenerInterfaceTest.manyAccepts(factory, port, n, expected);
    }

    public void testManyManyAccepts() {
        for (int i = 0; i < FACTORY.length; i++) {
            testManyManyAccepts(FACTORY[i], port_id++);
        }
    }

}
