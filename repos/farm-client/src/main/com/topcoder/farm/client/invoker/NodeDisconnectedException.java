/*
 * NodeDisconnectedException
 * 
 * Created 08/11/2006
 */
package com.topcoder.farm.client.invoker;


/**
 * Exception thrown by the AsyncInvocationResponse if the node disconnect
 * while waiting for a response
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NodeDisconnectedException extends FarmException {
    public NodeDisconnectedException(String msg) {
        super(msg);
    }
}
