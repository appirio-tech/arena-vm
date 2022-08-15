/*
 * CompilationTimeoutException
 * 
 * Created 11/29/2006
 */
package com.topcoder.server.ejb.TestServices;


/**
 * If code compilation timeout.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: CompilationTimeoutException.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class CompilationTimeoutException extends LongContestServicesException {

    public CompilationTimeoutException(String message) {
        super(message);
    }
}
