package com.topcoder.client.mpsqasApplet.server;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.netCommon.mpsqas.communication.message.Message;

import java.io.IOException;

/**
 * An interface for the class which polls the connection to the server
 * and sends messages through the port.
 *
 * @author mitalub
 */
public interface PortHandler {

    public void init(String address, int portNumber, String tunnel);

    public void startListening(ConnectionType type, boolean useSSL) throws IOException;

    public void sendMessage(Message message);

    public void close();
}
