/*
 * CancellationException
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.invoker;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class CancellationException extends IllegalStateException {

    public CancellationException() {
        super();
    }

    public CancellationException(String s) {
        super(s);
    }

}
