package com.topcoder.server.services.authenticate;

public class HandleTakenException extends Exception {

    public HandleTakenException(String s) {
        super(s);
    }
}
