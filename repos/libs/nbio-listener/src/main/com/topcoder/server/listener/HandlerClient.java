package com.topcoder.server.listener;

/*javanio*
import java.nio.channels.SocketChannel;
/**/

/*niowrapper*/

import com.topcoder.server.listener.nio.channels.SocketChannel;

/**/

interface HandlerClient {

    void acceptNewSocket(SocketChannel socketChannel);

    void closeConnection(Integer id, boolean lost);
    
    void closeConnection(Integer id);

    void receiveRequest(int connection_id, Object request);

    void banIPwithExpiry(String ipAddress, long expiresAt);
}
