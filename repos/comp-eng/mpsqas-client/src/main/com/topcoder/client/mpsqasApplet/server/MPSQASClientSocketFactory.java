package com.topcoder.client.mpsqasApplet.server;

import java.io.IOException;
import java.security.Key;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.netCommon.io.ClientConnector;
import com.topcoder.netCommon.io.ClientConnectorFactory;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.netCommon.mpsqas.communication.MPSQASMessageHandler;

public class MPSQASClientSocketFactory {
    private static ConnectionType lastConnectionType = null;

    public static ClientSocket createClientSocket(ConnectionType type, String address, int port, String tunnel, boolean useSSL, Key key) throws IOException {
        if (lastConnectionType != type) {
            if (lastConnectionType != null) {
                lastConnectionType.unselect();
            }
            type.select();
            lastConnectionType = type;
        }
        ClientConnector connector;
        if (type.isTunneled()) {
            connector = ClientConnectorFactory.createTunneledConnector(tunnel, useSSL);
        } else  {
            connector = ClientConnectorFactory.createSocketConnector(address, port, useSSL);
        }
        return new ClientSocket(connector, new MPSQASMessageHandler(key));
    }
}
