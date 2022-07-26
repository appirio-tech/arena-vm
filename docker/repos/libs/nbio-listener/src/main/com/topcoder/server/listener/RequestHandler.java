package com.topcoder.server.listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.server.listener.nio.channels.ClosedByInterruptException;
import com.topcoder.server.listener.nio.channels.ClosedChannelException;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.SocketChannel;

final class RequestHandler extends BaseHandler {

    private static final String CLASS_NAME = "RequestHandler";

    private final MonitorInterface monitor;
    private final ReaderWriterFactory rwFactory;

    private int trafficSize;

    RequestHandler(int port, HandlerClient controller, int numWorkerThreads, MonitorInterface monitor,
            ReaderWriterFactory rwFactory) {
        super(port, ListenerConstants.PACKAGE_NAME + CLASS_NAME, numWorkerThreads, controller);
        this.monitor = monitor;
        this.rwFactory = rwFactory;
    }

    int getOps() {
        return SelectionKey.OP_READ;
    }

    int getTrafficSize() {
        return trafficSize;
    }

    void register(Integer id, SocketChannel socketChannel) {
        if (log.isDebugEnabled()) log.debug("Registering request reader: " + socketChannel.socket().getPort());
        RequestReader requestReader = new RequestReader(id, socketChannel, rwFactory.newObjectReader());
        if (log.isDebugEnabled()) log.debug("Reader created: " + socketChannel.socket().getPort());
        register(socketChannel, requestReader);
        if (log.isDebugEnabled()) log.debug("Reader registered: " + socketChannel.socket().getPort());
    }
    
    private final Map errorMap = new HashMap();

    public void processKey(Object obj) throws InterruptedException {
        SelectionKey key = (SelectionKey) obj;
        RequestReader reader = (RequestReader) key.attachment();
//      THIS SHOULD BE ONLY FOR DEBUGGING, THIS CAN'T HAPPEN UNDER NORMAL OPERATION
//        boolean isAcquired = reader.attemptNow();
//        if (!isAcquired) {
//            throw new ConcurrentModificationException();
//        }
//        try {
            int id = reader.getConnectionId().intValue();
            try {
                int bytesRead = reader.read();
                if (bytesRead < 0) {
//                    debug("reader.read(): EOF " + reader);
                    closeConnectionDueToErrors(reader.getConnectionId());
                    return;
                }
                trafficSize += bytesRead;
                monitor.bytesRead(id, bytesRead);
            } catch (SocketException e) {
                return;
            } catch (ClosedByInterruptException e) {
                return;
            } catch (ClosedChannelException e) {
                return;
            } catch (IOException e) {
                //error("reader.read()",e);
                return;
            }
            for (; ;) {
                Object object;
                try {
                    object = reader.readObject();
                } catch (IOException e) {
                    log.error("reader.readObject(): ObjectStreamException", e);
                    closeConnection(reader.getConnectionId());
                    break;
                }
                catch (Exception e)
                {
                    log.error("BAD REQUEST FROM " + ((InetSocketAddress)reader.getSocketChannel().socket().getRemoteSocketAddress()).getAddress().getHostAddress(), e);
                    if(incrementErrorCount(((InetSocketAddress)reader.getSocketChannel().socket().getRemoteSocketAddress()).getAddress().getHostAddress()))
                    {
                        //temp ban
                        banIPwithExpiry(((InetSocketAddress)reader.getSocketChannel().socket().getRemoteSocketAddress()).getAddress().getHostAddress(), System.currentTimeMillis() + (60 * 60 * 1000));
                        closeConnection(reader.getConnectionId());
                    }
                    break;
                }
                if (object == null) {
                    break;
                }
                if (log.isDebugEnabled()) log.debug("receiveRequest, id=" + id);
                receiveRequest(id, object);
            }
//        }
//        finally {
//            reader.release();
//        }
    }
    
    private boolean incrementErrorCount(String ipAddress) {
        synchronized (errorMap) {
            Integer val = (Integer) errorMap.get(ipAddress);
            if (val != null) {
                val = new Integer(val.intValue() + 1);

                if (val.intValue() >= 5) {
                    errorMap.remove(ipAddress);
                    return true;
                } else {
                    errorMap.put(ipAddress, val);
                    return false;
                }
            } else {
                errorMap.put(ipAddress, new Integer(1));
                return false;
            }
        }        
    }

}
