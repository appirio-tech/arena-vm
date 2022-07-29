/*
 * InputChannelActivityListener
 * 
 * Created 05/23/2006
 */
package com.topcoder.netCommon.io;

/**
 * Listener of input channel activity.
 * This listener is invoked in the calling thread of read method , the
 * reading is interrupted and the result is not returned until
 * the listener finalizes its execution. This listener is called
 * everytime a byte block is read from the input channel, a readObject
 * on the client socket could report many invocations of the listener.
 *   
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface InputChannelActivityListener {

    /**
     * This method is invoked every time a byte block
     * is read from the input channel
     * 
     * @param cntBytes Number of bytes read always greater than 0
     */
    public void bytesRead(int cntBytes);
}
