package com.topcoder.client.contestant.message;

/**
 * MessageProcesor.java
 *
 *
 * Created on May 11, 2001, 11:30 AM
 */

import com.topcoder.client.netClient.*;
//import com.topcoder.netCommon.*;
//import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;

/**
 * This class is responsible for managing a client connection to a server and
 * sending/recieving requests/responses from the server or client. Classes which
 * implement this interface will act like adapters between specific client/server
 * types.
 *
 * @author Alex Roman
 * @version 1.0
 */
public interface MessageProcessor {

    /**
     * Initialize the client.
     */
    ////////////////////////////////////////////////////////////////////////////////
    //public void initClient();
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Open a connection to a server.
     *
     * @param doHTTPTunnelling a flag indicating if HTTP tunneling should be used.
     * @return      a boolean indicating whether the connection was opened or not.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public boolean openConnection(boolean doHTTPTunnelling);
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Open a connection to a server.
     *
     * @param doHTTPTunnelling a flag indicating if HTTP tunneling should be used.
     * @param goThroughProxy a flag indicating if the connection should be done via HTTP CONNECT proxy.
     * @param isSSL a flag indicating if the connection should use SSL.
     * @return      a boolean indicating whether the connection was opened or not.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public boolean openConnection(boolean doHTTPTunnelling, boolean goThroughProxy, boolean isSSL);
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * If there is a problem with the connection, gracefully close
     * it and notify the client.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void lostConnection();
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Gracefully close the client connection to the server.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void closeConnection();
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Process a client message that needs to go out to the server.
     */
    ////////////////////////////////////////////////////////////////////////////////
    //public void send();
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Process an incoming response from the server and give it
     * to the client.
     *
     * @param  response a Response object containing the information that needs to be sent
     *         to the server.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void receive(BaseResponse response);
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Process an incoming response from the server and give it
     * to the client.
     *
     * @param  a Response object containing the information that needs to be sent
     *         to the server.
     */
    ////////////////////////////////////////////////////////////////////////////////
    //public void receive(NewResponse response);
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * temporary -- retrieve the client.
     *
     * @return     a handle to the current Client object.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public Client getClient();
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Register one component with the MessageProcessor. A handle to the component
     * is stored in a hash table by the unique component id.
     *
     * @param key    representing a unique component identifier.
     * @param value  a handle to the component needed to be referenced.
     */
    ////////////////////////////////////////////////////////////////////////////////
    //public void registerComponent(Integer key, Object value);
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Register a hiearchy of Container/child objects with the MessageProcessor
     *
     * @param o      a container object with possible child objects.
     */
    ////////////////////////////////////////////////////////////////////////////////
    //public void registerCompTree(Object o);
    ////////////////////////////////////////////////////////////////////////////////
}
