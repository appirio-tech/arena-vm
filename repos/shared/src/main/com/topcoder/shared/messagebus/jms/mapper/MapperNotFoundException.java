/*
 * MapperNotFoundException
 * 
 * Created Oct 5, 2007
 */
package com.topcoder.shared.messagebus.jms.mapper;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MapperNotFoundException extends Exception {

    public MapperNotFoundException() {
        super();
    }

    public MapperNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperNotFoundException(String message) {
        super(message);
    }

    public MapperNotFoundException(Throwable cause) {
        super(cause);
    }

}
