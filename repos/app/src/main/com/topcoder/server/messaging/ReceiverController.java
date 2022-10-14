package com.topcoder.server.messaging;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;

import com.topcoder.shared.util.logging.Logger;

final class ReceiverController extends Thread {

    private static final Logger log = Logger.getLogger(ReceiverController.class);

    private final Context ctx;
    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private Queue queue;
    private QueueSession qsession;
    private QueueReceiver qreceiver;

    private String factoryName;
    private String queueName;
    private String selector;

    private int errorTime;
    private int pollTime;
    private int consoleMessageTime;

    private long timeStamp;

    private boolean receiverReady;
    private boolean active;
    private boolean initInProgress;
    private final boolean transacted;
    //boolean VERBOSE = true;

    /*
    ////////////////////////////////////////////////////////////////////////////////
    ReceiverController (String factoryName, String queueName, boolean isTransacted) throws NamingException
    ////////////////////////////////////////////////////////////////////////////////
    {
      this.transacted = isTransacted;
      this.ctx = TCContext.getInitial();
      initObject(factoryName, queueName, "");
      initJMS();
    }

    ////////////////////////////////////////////////////////////////////////////////
    ReceiverController (String factoryName, String queueName, boolean isTransacted, InitialContext ctx)
    ////////////////////////////////////////////////////////////////////////////////
    {
      this.transacted = isTransacted;
      this.ctx = ctx;
      initObject(factoryName, queueName, "");
      initJMS();
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    ReceiverController(String factoryName, String queueName, boolean isTransacted, Context ctx, String selector)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.transacted = isTransacted;
        this.ctx = ctx;
        initObject(factoryName, queueName, selector);
        initJMS();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void run() {
        ////////////////////////////////////////////////////////////////////////////////
        //if(VERBOSE) Log.msg(this.queueName + " - In run.");
        while (active) {
            //Log.msg(this.queueName + " - Run looping.");
            if (this.receiverReady || this.initInProgress) {
                //Log.msg(this.queueName + " - Everything seems fine.");
                try {
                    Thread.sleep(this.pollTime);
                } catch (Exception e) {
                }
                continue;
            }

            if (!this.receiverReady && !this.initInProgress) {
                log.error(this.queueName + " - Houston... we have a problem... attempting to resolve.");
                while (this.active && !initJMS()) {
                    log.error(this.queueName + " - Could not resolve problem... trying again...");
                    try {
                        Thread.sleep(this.errorTime);
                    } catch (Exception e) {
                    }
                }
                log.error(this.queueName + " - Houston... the problem has been resolved.");
            }

        }
        log.info(this.queueName + " - Finished running.");
        close();
    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    boolean isReady ()
    ////////////////////////////////////////////////////////////////////////////////
    {
      return this.receiverReady;
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    synchronized void setConsoleMessageTime(int in)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.consoleMessageTime = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    synchronized void setErrorTime(int in)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.errorTime = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    synchronized void setPollTime(int in)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.pollTime = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    synchronized void deactivate()
            ////////////////////////////////////////////////////////////////////////////////
    {
        //if (this.active = false)
        //  { return; }

        //Log.msg(VERBOSE,this.queueName + " - Deactivated.");
        this.active = false;
    }

    ////////////////////////////////////////////////////////////////////////////////
    synchronized ObjectMessage getMessage(int blockTime, boolean autoCommit)
            ////////////////////////////////////////////////////////////////////////////////
    {

        //boolean retVal = false;
        ObjectMessage msg = null;
        boolean alreadyWaited = false;

        while (true) {
            if (this.receiverReady) {
                try {
                    if (System.currentTimeMillis() - timeStamp > consoleMessageTime) {
                        //log.debug(this.queueName + " - Listening... " + (System.currentTimeMillis()-timeStamp));
                        timeStamp = System.currentTimeMillis();
                    }

                    try {
                        msg = (ObjectMessage) qreceiver.receive(blockTime);
                    } catch (Exception e) {
                        log.warn("Error retreiving next message.");
                        while (!initJMS()) {
                            log.error("A queue connection could not be established. Retrying...");
                            try {
                                Thread.sleep(5000);
                            } catch (Exception ex) {
                                error("", ex);
                            }
                        }
                    }

                    if (qsession.getTransacted() && autoCommit)
                    //if (this.transacted && autoCommit)
                    {
                        qsession.commit();
                    }
                    break;
                } catch (Exception e) {
                    try {
                        error("ERROR: could not get next message... rolling back QMR.", e);
                        qsession.rollback();
                        msg = null;
                    } catch (Exception e1) {
                        error("", e1);
                    }
                    this.receiverReady = false;
                    log.error("ReceiverController failed while receiving a message from the queue.");
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

    ////////////////////////////////////////////////////////////////////////////////
    synchronized void commit() throws JMSException
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.qsession.commit();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //protected synchronized void setTransacted(boolean value)
    ////////////////////////////////////////////////////////////////////////////////
    //{
    //  this.transacted = value;
    //}

    ////////////////////////////////////////////////////////////////////////////////
    synchronized void rollback() throws JMSException
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.qsession.rollback();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized void initObject(String factoryName, String queueName, String selector)
            ////////////////////////////////////////////////////////////////////////////////
    {
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

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized boolean initJMS()
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.initInProgress = true;
        close();

        boolean retVal = false;

        try {
            this.qconFactory = (QueueConnectionFactory) this.ctx.lookup(this.factoryName);
            if (qconFactory == null) {
                log.warn("qconFactory == null");
                return false;
            }
            this.qcon = this.qconFactory.createQueueConnection();
            this.qsession = this.qcon.createQueueSession(this.transacted, Session.AUTO_ACKNOWLEDGE);
            this.queue = (Queue) ctx.lookup(this.queueName);
            if ((selector == null) || (selector.equals(""))) {
                this.qreceiver = this.qsession.createReceiver(this.queue);
            } else {
                this.qreceiver = this.qsession.createReceiver(this.queue, this.selector);
            }
            this.qcon.start();
            retVal = true;
            this.receiverReady = true;
            //Log.msg(VERBOSE,this.queueName + " - JMS Initialized.");

        } catch (Exception e) {
            log.warn("ERROR: Could not initialize JMS queue: " + e);
        }

        this.initInProgress = false;
        return retVal;

    }

    ////////////////////////////////////////////////////////////////////////////////
    synchronized void close()
            ////////////////////////////////////////////////////////////////////////////////
    {
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

    private static void error(Object message, Throwable t) {
        log.error(message, t);
    }

}
