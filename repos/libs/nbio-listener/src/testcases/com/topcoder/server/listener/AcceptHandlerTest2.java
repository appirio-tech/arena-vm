package com.topcoder.server.listener;

import java.io.IOException;

import junit.framework.TestCase;

public final class AcceptHandlerTest2 extends TestCase {

    private static final int PORT = AcceptHandlerTest.PORT + 100;


    public AcceptHandlerTest2(String name) {
        super(name);
    }

    public void testAlreadyBound() {
        int port = PORT;
        AcceptHandlerTest.Client client = new AcceptHandlerTest.Client();
        AcceptHandler listener = new AcceptHandler(port, client, 1, null, false);
        try {
            listener.start();
        } catch (IOException e) {
            fail();
        }
        AcceptHandlerTest.Client client2 = new AcceptHandlerTest.Client();
        AcceptHandler listener2 = new AcceptHandler(port, client2, 1, null, false);
        try {
            listener2.start();
            fail("allowed to bind to the same port");
        } catch (IOException e) {
        }
        listener2.stop();
        listener.stop();
    }

}
