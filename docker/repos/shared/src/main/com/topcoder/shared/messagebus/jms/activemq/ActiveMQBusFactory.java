/*
 * ActiveMQBusFactory
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.messagebus.jms.activemq;


import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;

import com.topcoder.shared.messagebus.jms.AbstractJMSBusFactory;
import com.topcoder.shared.messagebus.jms.JMSBusConfiguration;
import com.topcoder.shared.messagebus.jms.JMSChannelConfiguration;
import com.topcoder.shared.messagebus.jms.JMSConnection;
import com.topcoder.shared.util.logging.Logger;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ActiveMQBusFactory extends AbstractJMSBusFactory {
    private Logger log = Logger.getLogger(getClass());
    
    public ActiveMQBusFactory(JMSBusConfiguration configuration) {
        super(configuration);
    }
    
    protected JMSConnection newConnection(JMSChannelConfiguration cfg) throws JMSException {
        ConnectionFactory factory = getConnectionFactory(cfg);
        long connectTimeout = getConnectTimeout(cfg);
        ActiveMQConnection cnn = (ActiveMQConnection) factory.createConnection();
        return new WrappedConnection(cnn, connectTimeout);
    }

    private long getConnectTimeout(JMSChannelConfiguration cfg) {
        String connectTimeoutString = cfg.getProperties().get("connectTimeout");
        long connectTimeout;
        try {
            connectTimeout = Long.parseLong(connectTimeoutString);
        } catch (Exception e) {
            log.error("connectTimeout property invalid or not set, using default: 5000ms");
            connectTimeout = 5000;
        }
        return connectTimeout;
    }

    protected String getFactoryKey(JMSChannelConfiguration cfg) {
        return getURL(cfg);
    }

    protected ConnectionFactory newFactory(JMSChannelConfiguration cfg) {
        return new ActiveMQConnectionFactory(getURL(cfg));
    }
    
    private String getURL(JMSChannelConfiguration cfg) {
        return cfg.getProperties().get("URL");
    }
    
    private static class WrappedConnection implements Connection, TransportListener, JMSConnection {
        private Logger log = Logger.getLogger(getClass());
        private ActiveMQConnection connection;
        private volatile int status = 0;
        private long connectTimeout;
        

        private WrappedConnection(ActiveMQConnection connection, long connectTimeout) {
            this.connection = connection;
            this.connection.addTransportListener(this);
            this.connectTimeout = connectTimeout;
        }

        public void close() throws JMSException {
            connection.close();
        }

        public ConnectionConsumer createConnectionConsumer(Destination arg0, String arg1, ServerSessionPool arg2,
                int arg3) throws JMSException {
            ensureStarted();
            return connection.createConnectionConsumer(arg0, arg1, arg2, arg3);
        }

        public ConnectionConsumer createDurableConnectionConsumer(Topic arg0, String arg1, String arg2,
                ServerSessionPool arg3, int arg4) throws JMSException {
            ensureStarted();
            return connection.createDurableConnectionConsumer(arg0, arg1, arg2, arg3, arg4);
        }

        public Session createSession(boolean arg0, int arg1) throws JMSException {
            ensureStarted();
            return connection.createSession(arg0, arg1);
        }

        public String getClientID() throws JMSException {
            return connection.getClientID();
        }

        public ExceptionListener getExceptionListener() throws JMSException {
            return connection.getExceptionListener();
        }

        public ConnectionMetaData getMetaData() throws JMSException {
            return connection.getMetaData();
        }

        public void setClientID(String arg0) throws JMSException {
            connection.setClientID(arg0);
        }

        public void setExceptionListener(ExceptionListener arg0) throws JMSException {
            connection.setExceptionListener(arg0);
        }

        public void start() throws JMSException {
            ensureStarted();
        }

        private void ensureStarted() throws JMSException {
            if (connection.isStarted()) {
                //If the connection is not connected currently the current thread will block,
                //so we throw an exception if this does not hold.
                assertConnected();
                return;
            }

            //This has to be done as a workaround.
            //A connection start would block until the connection is made
            //In addition FailoverTransport does not handle interrupt,
            //So another workaround is used to stop the connection attempt
            final Object[] result = new Object[2];
            result[0] = Boolean.FALSE;
            Thread thread = new Thread() {
                public void run() {
                    try {
                        connection.start();
                        result[0] = Boolean.TRUE;
                    } catch (Exception e) {
                        result[0] = Boolean.FALSE;
                        result[1] = e;
                    }
                }
            };
            thread.start();
            try {
                thread.join(connectTimeout);
                if (!((Boolean) result[0]).booleanValue()) {
                    try {
                        connection.getTransportChannel().stop();
                    } catch (Exception e) {
                        log.error("Exception stopping channel",e);
                    }
                    Thread.sleep(1000);
                    thread.interrupt();
                    connection.close();
                    if (result[1]!= null) {
                        JMSException exception = new JMSException("Could not connect");
                        exception.setLinkedException((Exception) result[1]);
                        throw exception;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void stop() throws JMSException {
            connection.stop();
        }

        public void onCommand(Object command) {
        }

        public void onException(IOException error) {
            log.error("Non recoverable Error on JMS transport: "+this.connection, error);
            status |= 2;
        }

        public void transportInterupted() {
            log.error("Connection lost temporarily for JMS transport: "+this.connection);
            this.status |= 0x1;
        }

        public void transportResumed() {
            log.error("Connection recovered for JMS transport: "+this.connection);
            this.status &= 0xFE;
        }

        public ActiveMQConnection getConnection() {
            return connection;
        }

        public boolean isConnected() {
            return status == 0;
        }
        
        public boolean canRecoverConnection() {
            return status < 2;
        }
        
        public void assertConnected() throws JMSException {
            if (!isConnected()) {
                throw new JMSException("Currently not connected!. Can connection be recovered="+ canRecoverConnection());
            }
        }
    }
}
