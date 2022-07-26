package com.topcoder.client.netClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import junit.framework.TestCase;

import com.topcoder.client.contestant.message.*;
import com.topcoder.netCommon.contestantMessages.request.*;
import com.topcoder.netCommon.contestantMessages.response.*;
import com.topcoder.netCommon.contestantMessages.NetCommonSocketFactory;
import com.topcoder.netCommon.io.ClientSocket;

public final class ClientTest extends TestCase {

    public static final int PORT = 6050;

    public ClientTest(String name) {
        super(name);
    }

    public void testSendSynchRequest() {
        int port = PORT + 1;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            fail();
        }
        Client client = null;
        try {
            client = new Client(InetAddress.getLocalHost().getHostAddress(), port);
        } catch (IOException e) {
            fail();
        }

        client.initContestResponseHandler(new MessageProcessor() {
            public void lostConnection() {
            }

            public void closeConnection() {
            }

            public Client getClient() {
                return null;
            }
            ////////////////////////////////////////////////////////////////////////////////
            public boolean openConnection(boolean doHTTPTunnelling) {
                return false;
            }
            ////////////////////////////////////////////////////////////////////////////////
            public void receive(BaseResponse response) {
            }

            public boolean openConnection(boolean doHTTPTunnelling, boolean goThroughProxy, boolean isSSL) {
                return false;
            }
        });
        Socket socket2 = null;
        try {
            socket2 = serverSocket.accept();
            Handler handler = new Handler(socket2);
            (new Thread(handler)).start();
        } catch (IOException e) {
            fail();
        }
        try {
            client.sendSynchRequest(new LoginRequest());
        } catch (Exception e) {
            fail("response handler thread died: " + e);
        }
        try {
            socket2.close();
            client.close();
            serverSocket.close();
        } catch (IOException e) {
            fail();
        }
    }

    private static class Handler implements Runnable {

        private final ClientSocket clientSocket;

        private Handler(Socket socket) throws IOException {
            clientSocket = NetCommonSocketFactory.newClientSocket(socket);
        }

        public void run() {
            try {
                ArrayList list = new ArrayList();
                BaseResponse response = new UnsynchronizeResponse();
                list.add(response);
                clientSocket.readObject();
                clientSocket.writeObject(list);
            } catch (IOException e) {
                fail();
            }
        }

    }

}
