package com.topcoder.shared.messaging;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;

import com.topcoder.shared.util.TCContext;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author mike lydon
 * @version $Revision$
 */
public class ReceiverController extends Thread {

    Context ctx;
    QueueConnectionFactory qconFactory;
    QueueConnection qcon;
    Queue queue;
    QueueSession qsession;
    QueueReceiver qreceiver;

    String factoryName;
    String queueName;
    String selector;

    /**
     * In the case where we can't get an initial connection to the queue (initJMS() fails), this is the length
     * of time to wait before trying again
     */
    int errorTime;

    /**
     * This is the time to wait in between checking that the JMS connection is still working
     */
    int pollTime;

    /**
     * In the case there is a problem receiving a message from the queue, this is the time to wait
     * in between the printing of messages to the log.
     */
    int consoleMessageTime;

    long timeStamp;

    boolean receiverReady;
    boolean active;
    boolean initInProgress;
    boolean transacted;

    private static Logger log = Logger.getLogger(ReceiverController.class);

    /**
     *
     * @param factoryName
     * @param queueName
     * @param isTransacted
     * @throws NamingException
     */
    public ReceiverController(String factoryName, String queueName, boolean isTransacted) throws NamingException {
        this.transacted = isTransacted;
        this.ctx = TCContext.getInitial();
        initObject(factoryName, queueName, "");
        initJMS();
    }

    /**
     *
     * @param factoryName
     * @param queueName
     * @param isTransacted
     * @param ctx
     */
    public ReceiverController(String factoryName, String queueName, boolean isTransacted, Context ctx) {
        this.transacted = isTransacted;
        this.ctx = ctx;
        initObject(factoryName, queueName, "");
        initJMS();
    }

    /**
     *
     * @param factoryName
     * @param queueName
     * @param isTransacted
     * @param ctx
     * @param selector
     */
    public ReceiverController(String factoryName, String queueName, boolean isTransacted, Context ctx, String selector) {
        this.transacted = isTransacted;
        this.ctx = ctx;
        initObject(factoryName, queueName, selector);
        initJMS();
    }

    /**
     *
     */
    public void run() {
        while (active) {
            //log.debug(this.queueName + " - Run looping.");
            if (this.receiverReady || this.initInProgress) {
                //log.debug(this.queueName + " - Everything seems fine.");
                try {
                    Thread.sleep(this.pollTime);
                } catch (Exception e) {
                    log.debug("exception putting the thread to sleep");
                }
                continue;
            }

            if (!this.receiverReady && !this.initInProgress) {
                log.debug(this.queueName + " - Houston... we have a problem... attempting to resolve.");
                while (this.active && !initJMS()) {
                    log.debug(this.queueName + " - Could not resolve problem... trying again...");
                    try {
                        Thread.sleep(this.errorTime);
                    } catch (Exception e) {
                        log.debug("exception putting the thread to sleep");
                    }
                }
                log.debug(this.queueName + " - Houston... the problem has been resolved.");
            }

        }
        log.debug(this.queueName + " - Finished running.");
        close();
    }

    /**
     *
     * @return
     */
    public boolean isReady() {
        return this.receiverReady;
    }

    /**
     *
     * @param in
     */
    public synchronized void setConsoleMessageTime(int in) {
        this.consoleMessageTime = in;
    }

    /**
     *
     * @param in
     */
    public synchronized void setErrorTime(int in) {
        this.errorTime = in;
    }

    /**
     *
     * @param in
     */
    public synchronized void setPollTime(int in) {
        this.pollTime = in;
    }

    /**
     *
     */
    public synchronized void deactivate() {
        if (this.active = false) {
            return;
        }

        this.active = false;
    }

    /**
     *
     * @param blockTime
     * @param autoCommit
     * @return
     */
    public synchronized ObjectMessage getMessage(int blockTime, boolean autoCommit) {
        ObjectMessage msg = null;
        boolean alreadyWaited = false;

        while (true) {
            if (this.receiverReady) {
                try {
                    if (System.currentTimeMillis() - timeStamp > consoleMessageTime) {
                        log.debug(this.queueName + " - Listening... " + (System.currentTimeMillis() - timeStamp));
                        timeStamp = System.currentTimeMillis();
                    }

                    try {
                        //log.debug("RECEIVING");
                        msg = (ObjectMessage) qreceiver.receive(blockTime);
                        //log.debug("POSTRECEIVING");
                        //log.debug("got a message " + msg);
                        if (qsession.getTransacted() && autoCommit) {
                        //if (this.transacted && autoCommit)
                            qsession.commit();
                        }
                        //log.debug("POSTPOSTRECEIVING");
                    break;
                    } catch (Exception e) {
                        log.debug("ERROR: Error retreiving next message.");
                        while (!initJMS()) {
                            System.out.println("A queue connection could not be established. Retrying...");
                            try {
                                Thread.sleep(5000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                } catch (Exception e) {
                    try {
                        log.debug("ERROR: could not get next message... rolling back QMR.");
                        e.printStackTrace();
                        qsession.rollback();
                        msg = null;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    this.receiverReady = false;
                    log.debug("ReceiverController failed while receiving a message from the queue.");
                    break;
                }
            } else {
                if (alreadyWaited) {
                    break;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                    alreadyWaited = true;
                    continue;
                }
            }
        }
        return msg;

    }

    /**
     *
     * @throws JMSException
     */
    protected synchronized void commit() throws JMSException {
        this.qsession.commit();
    }

    //protected synchronized void setTransacted(boolean value)
    //{
    //  this.transacted = value;
    //}

    /**
     *
     * @throws JMSException
     */
    protected synchronized void rollback() throws JMSException {
        this.qsession.rollback();
    }

    /**
     *
     * @param factoryName
     * @param queueName
     * @param selector
     */
    private synchronized void initObject(String factoryName, String queueName, String selector) {
        this.receiverReady = false;
        this.factoryName = factoryName;
        this.queueName = queueName;
        this.active = true;
        this.initInProgress = false;
        this.pollTime = 500;
        this.errorTime = 2000;
        this.consoleMessageTime = 15000;
        this.timeStamp = 0;
        this.selector = selector;
    }

    /**
     *
     * @return
     */
    private synchronized boolean initJMS() {
        this.initInProgress = true;
        close();

        boolean retVal = false;

        try {
            try{
                log.debug("q factory " + factoryName);
                this.qconFactory = (QueueConnectionFactory) ctx.lookup(factoryName);
            }catch(Exception e){
                log.error("Failed to get QueueConnectionFactory, attempting to reinitialize InitialContext",e);
                Hashtable env = null;
                try {
                    env = ctx.getEnvironment();
                } catch (Exception ignore) {}
                log.info("env.toString() = "+(env==null?"null":env.toString()));
                this.ctx = TCContext.getContext((String)env.get(Context.INITIAL_CONTEXT_FACTORY), (String)env.get(Context.PROVIDER_URL));
                this.qconFactory = (QueueConnectionFactory) ctx.lookup(factoryName);
            }
            try{
                if(this.qcon != null){
                    this.qcon.close();
                }
            }catch(Exception e){
                log.error("Error closing connection");
            }
            this.qcon = this.qconFactory.createQueueConnection();
            this.qsession = this.qcon.createQueueSession(this.transacted, Session.AUTO_ACKNOWLEDGE);
            this.queue = (Queue) ctx.lookup(this.queueName);
            log.info(queueName+" has been restarted");
            if ((selector == null) || (selector.equals(""))) {
                this.qreceiver = this.qsession.createReceiver(this.queue);
            } else {
                this.qreceiver = this.qsession.createReceiver(this.queue, this.selector);
            }
            this.qcon.start();
            retVal = true;
            this.receiverReady = true;

        } catch (Exception e) {
            log.debug("ERROR: Could not initialize JMS queue.");
            // Matt Murphy 4/14/02 Uncommented the line below to debug.
            // Feel free to comment it out if it gets in the way.
            //e.printStackTrace();
        }

        this.initInProgress = false;
        return retVal;

    }

    /**
     *
     */
    public synchronized void close() {
        this.receiverReady = false;

        try {
            if (!(qreceiver == null)) {
                qreceiver.close();
            }
            if (!(qsession == null)) {
                qsession.close();
            }
            if (!(qcon == null)) {
                qcon.close();
            }
            qreceiver = null;
            qsession = null;
            qcon = null;
        } catch (Exception e) {
        }

    }

    public Queue getQueue() {
        return queue;
    }
}
