package com.topcoder.server.listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;

import com.topcoder.netCommon.io.SocketUtil;
import com.topcoder.server.listener.net.InetSocketAddress;
import com.topcoder.server.listener.nio.channels.ClosedByInterruptException;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.ServerSocketChannel;
import com.topcoder.server.listener.nio.channels.SocketChannel;

/**/

final class AcceptHandler extends BaseHandler {

    private static final int BACKLOG = 511;
    private static final String CLASS_NAME = "AcceptHandler";
    private String ipAddress = null;
    private final int port;
    private final IPBlocker ipBlocker;

    private ServerSocketChannel serverSocketChannel;
    private int bufferSize;

    AcceptHandler(int port, HandlerClient client, int numWorkerThreads, Collection ips, boolean isAllowedSet) {
        this(null, port, client, numWorkerThreads, ips, isAllowedSet);
    }

    AcceptHandler(String ipAddress, int port, HandlerClient client, int numWorkerThreads, Collection ips, boolean isAllowedSet) {
        super(port, ListenerConstants.PACKAGE_NAME + CLASS_NAME, numWorkerThreads, client);
        this.port = port;
        this.ipAddress = ipAddress;
        ipBlocker = new IPBlocker(ips, isAllowedSet);
        bufferSize = SocketUtil.getServerSocketBufferSize(port);
        if (bufferSize != -1) {
            if (log.isInfoEnabled()) log.info("Using buffer size " + bufferSize);
        }
    }

    int getOps() {
        return SelectionKey.OP_ACCEPT;
    }

    void open() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        InetSocketAddress address = resolveAddress();
        serverSocketChannel.socket().bind(address, BACKLOG);
        if (log.isInfoEnabled()) log.info("bound to " + address);
        register(serverSocketChannel, null);
        if (log.isInfoEnabled()) log.info("server socket created");
    }

    private InetSocketAddress resolveAddress() throws UnknownHostException {
        if (ipAddress != null && ipAddress.trim().length() > 0) {
            return new InetSocketAddress(InetAddress.getByName(ipAddress), port);
        }
        return new InetSocketAddress(InetAddress.getLocalHost(), port);
    }

    private static String getRemoteAddress(Socket socket) {
        String hostAddress = socket.getInetAddress().getHostAddress();
        if (hostAddress == null) {
            hostAddress = "0.0.0.0";
        }
        return hostAddress;
    }

    static String getRemoteAddress(SocketChannel socketChannel) {
        return getRemoteAddress(socketChannel.socket());
    }

    public void processKey(Object object) {
        if (log.isDebugEnabled()) log.debug("Accepting a socket");
        SocketChannel socketChannel;
        try {
            socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) {
                if (log.isDebugEnabled()) log.debug("Accepted socket was null");
                return;
            }
        } catch (ClosedByInterruptException e) {
		 if (log.isDebugEnabled()) log.debug("Error while acception socket",e);

            return;
        } catch (SocketException e) {
            if (log.isDebugEnabled()) log.debug("Error while acception socket",e);
            return;
        } catch (IOException e) {
            log.error("", e);
            return;
        }
        String remoteIP = getRemoteAddress(socketChannel);
        if (ipBlocker.isBlocked(remoteIP)) {
             if (log.isDebugEnabled()) log.debug("closing connection, this IP is blocked: " + remoteIP);
            try {
                socketChannel.close();
            } catch (IOException e) {
                log.error("", e);
            }
            return;
        }
        try {
            socketChannel.configureBlocking(false);
            if (bufferSize != -1) {
                Socket socket = socketChannel.socket();
                socket.setTcpNoDelay(true);
                socket.setSendBufferSize(bufferSize);
                socket.setReceiveBufferSize(bufferSize);
            }
        } catch (IOException e) {
            log.error("socketChannel.configureBlocking(false)", e);
            return;
        }
        if (log.isInfoEnabled()) log.info("socket accepted: " + socketChannel.socket().getPort());
        acceptNewSocket(socketChannel);
        if (log.isDebugEnabled()) log.debug("done accepting socket");
    }

    void close() {
        if (serverSocketChannel != null) {
            try {
                if (log.isInfoEnabled()) log.info("closing server socket");
                serverSocketChannel.close();
            } catch (IOException e) {
                log.error("serverSocketChannel.close()", e);
            }
        }
    }

    void banIP(String ipAddress) {
        ipBlocker.banIP(ipAddress);
    }

    //rfairfax 6-11
    public void banIPwithExpiry(String ipAddress, long expiresAt)
    {
        if(!ipAddress.equals("192.168.10.55") && !ipAddress.equals("192.168.12.56") && !ipAddress.equals("192.168.12.57"))
            ipBlocker.banIPwithExpiry(ipAddress, expiresAt);
        else
            if (log.isInfoEnabled()) log.info("INVALID BAN ATTEMPT " + ipAddress);
    }

}
