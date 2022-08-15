package com.topcoder.netCommon.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;

import com.topcoder.shared.netCommon.SimpleCSHandler;

public final class ClientSocketTest extends TestCase {

    private static final int PORT = 6050;

    public static void testReadByteArray() throws IOException {
        int port = PORT + 1;
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = new Socket(InetAddress.getLocalHost(), port);
        Socket socket2 = serverSocket.accept();
        ClientSocket clientSocket = new ClientSocket(socket, new SimpleCSHandler(), "");
        ClientSocket clientSocket2 = new ClientSocket(socket2, new SimpleCSHandler(), "");
        Object object2 = RandomUtils.randomString();
        clientSocket2.writeObject(object2);
        Object object = clientSocket.readObject();
        assertEquals(object2, object);
        clientSocket2.close();
        clientSocket.close();
        serverSocket.close();
    }

}
