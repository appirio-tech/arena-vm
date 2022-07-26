/*
 * ArenaActionListenerException
 * 
 * Created Nov 5, 2007
 */
package com.topcoder.shared.arena.remoteactions;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ArenaActionListenerException extends Exception {

    public ArenaActionListenerException() {
    }

    public ArenaActionListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArenaActionListenerException(String message) {
        super(message);
    }

    public ArenaActionListenerException(Throwable cause) {
        super(cause);
    }

}
