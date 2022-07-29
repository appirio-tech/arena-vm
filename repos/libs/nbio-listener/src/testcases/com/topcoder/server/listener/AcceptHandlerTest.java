package com.topcoder.server.listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*javanio*
import java.nio.channels.SocketChannel;
/**/

/*niowrapper*/
import com.topcoder.server.listener.nio.channels.SocketChannel;
/**/

import junit.framework.TestCase;

public final class AcceptHandlerTest extends TestCase {

    public static final InetAddress HOST;
    public static final int PORT = 5200;

    static {
        InetAddress host = null;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
        }
        HOST = host;
    }

    public AcceptHandlerTest(String name) {
        super(name);
    }

    public void testSimpleStartStop() {
        AcceptHandler listener = new AcceptHandler(PORT, new Client(), 1, null, false);
        try {
            listener.start();
        } catch (IOException e) {
            fail();
        }
        listener.stop();
    }

    static class Client implements HandlerClient {

        public void acceptNewSocket(SocketChannel socketChannel) {
        }

        public void closeConnection(Integer id) {
        }

        public void receiveRequest(int connection_id, Object request) {
        }
        
        public void banIPwithExpiry(String ipAddress, long expiresAt) {
        }

        public void closeConnection(Integer id, boolean lost) {
        }
    }

}
