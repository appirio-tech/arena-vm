/*
 * ReconnectingException
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import java.io.IOException;

/**
 * Exception throw by a connection if a reconnect attempt is
 * being made and a send invocation is received
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReconnectingException extends IOException {

    public ReconnectingException() {
        super();
    }

    public ReconnectingException(String s) {
        super(s);
    }
}
