/*
 * JMSConnection
 * 
 * Created Oct 8, 2007
 */
package com.topcoder.shared.messagebus.jms;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface JMSConnection extends Connection {
    boolean isConnected();
    boolean canRecoverConnection();
    void assertConnected() throws JMSException;
}
