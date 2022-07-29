package com.topcoder.shared.messaging;

import com.topcoder.shared.util.logging.Logger;

import javax.naming.Context;
import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import java.io.Serializable;

/**
 * This class is used in the case where you want to wait for a response from a JMS Queue.
 * It takes care of waiting for the response to a message for a specified period of time.
 * This would be very straight forward, but we want to solve the case where message A
 * and message B are dispathed in that order, but A takes a long time to come back.  The
 * response to B should be received without waiting for A to come back.
 *
 * When you create an instance of <code>QueueResponseManager</code> a thread is created
 * that polls the queue.  When it gets a message, it either puts it into a <code>ResponsePool</code>
 * or, if the response is old, it is thrown out as it is no longer relevant.
 *
 *
 * User: dok
 * Date: Dec 10, 2004
 */
public class QueueResponseManager {

    private static Logger log = Logger.getLogger(QueueResponseManager.class);


    private ReceiverController receiver;

    private boolean initialized = false;
    protected ResponsePool responses = null;

    /**
     *
     * @param factoryName
     * @param queueName
     * @param ctx
     * @param selector
     */
    public QueueResponseManager(String factoryName, String queueName, Context ctx, String selector) {
        init(factoryName, queueName, ctx, selector, new ResponsePool(10));
    }

    public QueueResponseManager(String factoryName, String queueName,
                                Context ctx, String selector, ResponsePool responses) {
        init(factoryName, queueName, ctx, selector, responses);
    }


    /**
     *
     * @param factoryName
     * @param queueName
     * @param ctx
     * @param selector
     * @param responses
     */
    protected synchronized void init(String factoryName, String queueName, Context ctx,
                                     String selector, ResponsePool responses) {
        if (!initialized) {
            this.receiver = new ReceiverController(factoryName, queueName, false, ctx, selector);
            this.receiver.start();
            ResponseLoaderThread t = new ResponseLoaderThread(100,100);
            t.start();
            this.initialized=true;
            this.responses = responses;
        }
    }


    /**
     * Gets a message from the queue.
     * @param waitTime
     * @param correlationId
     * @return
     * @throws TimeOutException
     */
    public Serializable receive(int waitTime, String correlationId) throws TimeOutException {
        return responses.get(waitTime, correlationId);
    }


    /**
     * A thread class that will check the temp storage for a response to a
     * particular request.
     */
    private class ResponseLoaderThread extends Thread {

        private int blockTime=0;
        private int sleepTime=0;

        /**
         *
         * @param blockTime the length of time in millis to wait for a response
         *        to appear on the queue
         * @param sleepTime the length of time in millis to wait if there was
         *        nothing on the queue the last time we checked.  this is just
         *        intended to save some cycles...no need to beat up the queue
         *        if nothing is going on.
         */
        private ResponseLoaderThread(int blockTime, int sleepTime) {
            this.blockTime = blockTime;
            this.sleepTime = sleepTime;
        }

        public void run() {
            while (true) {
                try {
                    //pull an item off the queue
                    ObjectMessage response = receiver.getMessage(blockTime, true);
                    if (response != null) {
                        log.debug("got a response " + response);
                        responses.put(response.getJMSCorrelationID(), response.getObject());
                    } else {
                        //log.debug("waitList size " + responses.getWaitCount());
                        Thread.sleep(sleepTime);
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    //ignoreing
                }
            }
        }

    }

}
