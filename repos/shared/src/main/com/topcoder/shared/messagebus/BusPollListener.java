/*
 * BusPollListener
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.messagebus;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface BusPollListener {
    BusMessage receive() throws BusException;
    BusMessage receive(long ms) throws BusException;
}
