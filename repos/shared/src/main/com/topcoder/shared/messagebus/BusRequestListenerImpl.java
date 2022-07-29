/*
 * BusRequestPublisherImpl
 * 
 * Created Oct 24, 2007
 */
package com.topcoder.shared.messagebus;

import java.util.concurrent.Executor;

import com.topcoder.shared.util.concurrent.DirectExecutor;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BusRequestListenerImpl implements BusRequestListener {
    //FIXME DOC
    private Logger log = Logger.getLogger(getClass());
    private BusListener  listener;
    private BusPublisher producer;
    private Handler handler;
    private Executor runner = new DirectExecutor();
    
    
    public BusRequestListenerImpl(BusListener listener, BusPublisher producer) throws BusException {
        this.producer = producer;
        this.listener = listener;
        this.listener.setHandler(new BusListener.Handler() {
            public void handle(final BusMessage message) {
                runner.execute(new Runnable() {
                    public void run() {
                        processIncomingRequest(message);
                    }
                });
            }
        }); 
    }

    public void start() throws BusException {
       log.debug("Starting bus requestListener");
       if (handler == null) {
           throw new IllegalStateException("Handler has not been set");
       }
       listener.start();
    }
    
    protected void processIncomingRequest(final BusMessage message) {
        handler.handle(message, new ResponseMessageHolder() {
            private boolean resultSet = false;
            public void setResponse(BusMessage result) {
                if (resultSet) {
                    throw new IllegalStateException("The response for this request was already set.");
                }
                resultSet = true;
                if (result != null) {
                    result.setMessageCorrelationId(message.getMessageId());
                    try {
                        producer.publish(result);
                    } catch (Exception e) {
                        log.error("Could not send response for request", e);
                    }
                }
            }
        });
    }

    public void stop() {
        log.debug("Stop BusRequestListenerImpl");
        producer.close();
        listener.stop();
    }

    public void setHandler(Handler h) throws BusException {
        this.handler = h;
    }

    public Executor setRunner(Executor runner) {
        Executor old = this.runner;
        this.runner = runner;
        return old;
    }
}
