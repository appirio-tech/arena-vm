/*
 * MapperProviderException
 * 
 * Created Oct 5, 2007
 */
package com.topcoder.shared.messagebus.jms.mapper;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MapperProviderException extends Exception {

    public MapperProviderException() {
    }

    public MapperProviderException(String message) {
        super(message);
    }

    public MapperProviderException(Throwable cause) {
        super(cause);
    }

    public MapperProviderException(String message, Throwable cause) {
        super(message, cause);
    }

}
