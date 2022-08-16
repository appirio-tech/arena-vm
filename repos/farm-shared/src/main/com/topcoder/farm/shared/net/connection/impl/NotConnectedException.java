/*
 * NotConnectedException
 * 
 * Created 07/10/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import java.io.IOException;

/**
 * Exception thrown when an operation that requires an opened connection
 * is invoked on a closed one
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NotConnectedException extends IOException {

    public NotConnectedException() {
    }

    public NotConnectedException(String message) {
        super(message);
    }
}
