/*
 * NullInputChannelActivityListener
 * 
 * Created 05/23/2006
 */
package com.topcoder.netCommon.io;

/**
 * Null pattern for InputChannelActivityListener
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NullInputChannelActivityListener implements InputChannelActivityListener {

    /**
     * @see InputChannelActivityListener#bytesRead(int)
     */
    public void bytesRead(int cntBytes) {
        //Nothing to do
    }

}
