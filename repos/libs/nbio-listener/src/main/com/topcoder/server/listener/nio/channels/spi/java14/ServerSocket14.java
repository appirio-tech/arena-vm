package com.topcoder.server.listener.nio.channels.spi.java14;

import java.io.IOException;

import com.topcoder.netCommon.io.SocketUtil;
import com.topcoder.server.listener.net.InetSocketAddress;
import com.topcoder.server.listener.net.ServerSocket;

final class ServerSocket14 extends ServerSocket {

    private final java.net.ServerSocket socket;

    ServerSocket14(java.net.ServerSocket socket) {
        this.socket = socket;
    }

    static java.net.InetSocketAddress getAddress(InetSocketAddress endpoint) {
        return new java.net.InetSocketAddress(endpoint.getAddress(), endpoint.getPort());

    }

    public void bind(InetSocketAddress endpoint, int backlog) throws IOException {
        int bufferSize = SocketUtil.getServerSocketBufferSize(endpoint.getPort());
        if (bufferSize != -1) {
            socket.setReceiveBufferSize(bufferSize);
        }
        socket.bind(getAddress(endpoint), backlog);
    }

}
