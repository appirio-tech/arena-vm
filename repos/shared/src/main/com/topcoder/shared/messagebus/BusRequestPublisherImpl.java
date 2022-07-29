/*
 * BusRequestPublisherImpl
 * 
 * Created Oct 24, 2007
 */
package com.topcoder.shared.messagebus;

import java.util.concurrent.Future;

import com.topcoder.shared.util.concurrent.FutureImplManager;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BusRequestPublisherImpl implements BusRequestPublisher {
    //FIXME DOC
    private Logger log = Logger.getLogger(getClass());
    private FutureImplManager<String, BusMessage> futures = new FutureImplManager();
    private BusPublisher producer;
    private BusListener  listener;
    
    
    public BusRequestPublisherImpl(BusPublisher producer, BusListener listener) throws BusException {
        this.producer = producer;
        this.listener = listener;
        this.listener.setHandler(new BusListener.Handler() {
            public void handle(BusMessage message) {
                processIncomingMessage(message);
            }
        }); 
    }

    protected void processIncomingMessage(BusMessage message) {
        String correlatedId = message.getMessageCorrelationId();
        if (correlatedId == null) {
            log.info("Receive message without correlated ID: "+message);
            return;
        }
        futures.setValue(correlatedId, message);
    }

    public void close() {
        log.debug("Closing BusRequestPublisher");
        try { producer.close(); } catch (Exception e) { log.error(e,e); }
        try { listener.stop(); } catch (Exception e) { log.error(e,e); }
    }
    
    public Future<BusMessage> request(BusMessage message) throws BusException {
        listener.start();
        producer.publish(message);
        return futures.newFuture(message.getMessageId());
    }
    
    public void publish(BusMessage message) throws BusException {
        producer.publish(message);
    }
}
