/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 2, 2002
 * Time: 7:35:34 PM
 *
 * Largely based on the original log4j SocketAppender.  Should be a subclass, but the original
 * wasn't really geared towards it.
 */

package com.topcoder.server.util.logging.net;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.listener.monitor.MonitorCSHandler;
import com.topcoder.shared.util.logging.Logger;

/**
 */
public class TCSocketAppender extends AppenderSkeleton {
    
    private static final Logger log = Logger.getLogger(TCSocketAppender.class);
    
    /**
     * The default reconnection delay (30000 milliseconds or 30 seconds).
     */
    static final int DEFAULT_RECONNECTION_DELAY = 30000;
    
    /**
     * We remember host name as String in addition to the resolved
     * InetAddress so that it can be returned via getOption().
     */
    String remoteHost;
    
    Layout layout;
    InetAddress address;
    int port;
    ClientSocket client;
    int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
    int processingDelay = 1000;
    boolean locationInfo = false;
    
    private TCSocketAppender.Processor connector;
    
    int counter = 0;
    
    
    // reset the ObjectOutputStream every 70 calls
    private static final int RESET_FREQUENCY = 70;
    
    private String identifier = "TopCoder Server ProblemComponent";
    private Date bornOn = new Date();
    private String owner = "<unknown>";
    private StreamID streamID;
    
    
    public TCSocketAppender() {
    }
    
    public TCSocketAppender(StreamID id) {
        streamID = id;
    }
    
    public StreamID getStreamID() {
        return streamID;
    }
    
    /**
     * Connect to the specified <b>RemoteHost</b> and <b>Port</b>.
     */
    public void activateOptions() {
        log.debug("TCSocketAppender: activateOptions()");
        if (streamID == null) {
            streamID = new StreamID(identifier,"",owner,bornOn);
        }
        connect(address, port);
    }
    
    /**
     * Close this appender.
     * <p>This will mark the appender as closed and
     * call then {@link #cleanUp} method.
     */
    synchronized public void close() {
        log.debug("TCSocketAppender: close()");
        if (closed) {
            return;
        }
        
        this.closed = true;
        cleanUp();
    }
    
    /**
     * Drop the connection to the remote host and release the underlying
     * connector thread if it has been created
     */
    public void cleanUp() {
        log.debug("TCSocketAppender: cleanUp()");
        synchronized (this) {
            if (connector != null) {
                //LogLog.debug("Interrupting the connector.");
                connector.interrupted = true;
                connector = null;  // allow gc
            }
            
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    log.error("Could not close client.", e);
                }
                client = null;
            }
            
        }
    }
    
    void connect(InetAddress address, int port) {
        fireConnector();
    }
    
    int MAX_SIZE = 10000;
    
    public void append(LoggingEvent event) {
        if (event == null)
            return;

        TCLoggingEvent evt = new TCLoggingEvent(layout.format(event), event.getLevel());
        synchronized(itemsLock) {
            if(items.size() == MAX_SIZE) {
                items.remove(0);
            }
            items.add(evt);
        }
    }
    
    void fireConnector() {
        log.debug("TCSocketAppender: fireConnector()");
        if (connector == null) {
            log.debug("Starting a new connector thread.");
            connector = new TCSocketAppender.Processor();
            connector.setDaemon(true);
            connector.setPriority(Thread.MIN_PRIORITY);
            connector.start();
        }
    }
    
    static InetAddress getAddressByName(String host) {
        log.debug("TCSocketAppender: getAddressByName()");
        try {
            return InetAddress.getByName(host);
        } catch (Exception e) {
            LogLog.error("Could not find address of [" + host + "].", e);
            return null;
        }
    }
    
    public boolean requiresLayout() {
        return true;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public void setLayout(Layout layout) {
        this.layout = layout;
    }
    
    /**
     * The <b>RemoteHost</b> option takes a string value which should be
     * the host name of the server where a {@link org.apache.log4j.net.SocketNode} is running.
     */
    public void setRemoteHost(String host) {
        log.debug("TCSocketAppender: setRemoteHost()");
        address = getAddressByName(host);
        remoteHost = host;
    }
    
    /**
     * Returns value of the <b>RemoteHost</b> option.
     */
    public String getRemoteHost() {
        return remoteHost;
    }
    
    /**
     * The <b>Port</b> option takes a positive integer representing
     * the port where the server is waiting for connections.
     */
    public void setPort(int port) {
        log.debug("TCSocketAppender: setPort()");
        this.port = port;
    }
    
    /**
     * Returns value of the <b>Port</b> option.
     */
    public int getPort() {
        return port;
    }
    
    /**
     * The <b>LocationInfo</b> option takes a boolean value. If true,
     * the information sent to the remote host will include location
     * information. By default no location information is sent to the server.
     */
    public void setLocationInfo(boolean locationInfo) {
        log.debug("TCSocketAppender: setLocationInfo()");
        this.locationInfo = locationInfo;
    }
    
    /**
     * Returns value of the <b>LocationInfo</b> option.
     */
    public boolean getLocationInfo() {
        return locationInfo;
    }
    
    /**
     * The <b>ReconnectionDelay</b> option takes a positive integer
     * representing the number of milliseconds to wait between each
     * failed connection attempt to the server. The default value of
     * this option is 30000 which corresponds to 30 seconds.
     *
     * <p>Setting this option to zero turns off reconnection
     * capability.
     */
    public void setReconnectionDelay(int delay) {
        log.debug("TCSocketAppender: setReconnectionDelay()");
        this.reconnectionDelay = delay;
    }
    
    /**
     * Returns value of the <b>ReconnectionDelay</b> option.
     */
    public int getReconnectionDelay() {
        return reconnectionDelay;
    }
    
    private List items = new LinkedList();
    private Object itemsLock = new Object();
    
    class Processor extends Thread {
        public boolean interrupted = false;
        
        public void run() {
            while(!interrupted) {
                while(client == null) {
                    //reconnect
                    Socket socket;
                    try {
                        sleep(reconnectionDelay);
                        socket = new Socket(address, port);
                        client = new ClientSocket(socket, new MonitorCSHandler());
                        try {
                            client.writeObject(streamID);
                        } catch (Exception e) {
                            
                        }

                    } catch (InterruptedIOException e) {
                        log.debug("Connector interrupted. Leaving loop.");
                        client = null;
                        //return;
                    } catch (InterruptedException e) {
                        log.debug("Connector interrupted. Leaving loop.");
                        client = null;
                        //return;
                    } catch (java.net.ConnectException e) {
                        client = null;
                        //log.debug("Remote host " + address.getHostName()
                        //+ " " + address.getHostAddress() + " refused connection.");
                    } catch (IOException e) {
                        log.debug("Could not connect to " + address.getHostName() +
                                ": " + e.getMessage());
                        client = null;
                    }
                }

                //write out the queue
                List list;
                synchronized(itemsLock) {
                    list = items;
                    items = new LinkedList();
                }

                if(list.size() > 0) {
                    TCBatchLoggingEvent batchItem = new TCBatchLoggingEvent();
                    for(int i = 0; i < list.size(); i++) {
                        batchItem.addItem((TCLoggingEvent)list.get(i));
                    }
                    
                    if (client != null) {
                        try {
                            client.writeObject(batchItem);
                        } catch (IOException e) {
                            client = null;
                            log.warn("Detected problem with connection: " + e);
                        }
                    }
                }
                
                try {
                    sleep(processingDelay);
                } catch (InterruptedException e) {
                    
                }
            }
        }
    }
    
}
