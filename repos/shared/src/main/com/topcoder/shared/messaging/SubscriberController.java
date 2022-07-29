package com.topcoder.shared.messaging;

import com.topcoder.shared.util.TCContext;
import com.topcoder.shared.util.logging.Logger;

import javax.jms.*;
import javax.naming.*;

/**
 * @author mike lydon
 * @version $Revision$
 */
public class SubscriberController extends Thread {

    Context ctx;
    TopicConnectionFactory tconFactory;
    TopicConnection tcon;
    Topic topic;
    TopicSession tsession;
    TopicSubscriber tsubscriber;

    String factoryName;
    String topicName;
    String topicSelector;

    int errorTime;
    int pollTime;
    int consoleMessageTime;

    long timeStamp;

    boolean subscriberReady;
    volatile boolean active;
    boolean initInProgress;
    private static Logger log = Logger.getLogger(SubscriberController.class);

    /**
     *
     * @param factoryName
     * @param topicName
     * @throws NamingException
     */
    public SubscriberController(String factoryName, String topicName) throws NamingException {
        this.ctx = TCContext.getInitial();
        initObject(factoryName, topicName);
        initJMS();
    }

    /**
     *
     * @param factoryName
     * @param topicName
     * @param ctx
     */
    public SubscriberController(String factoryName, String topicName, InitialContext ctx) {
        this.ctx = ctx;
        initObject(factoryName, topicName);
        initJMS();
    }

    /**
     *
     * @param factoryName
     * @param topicName
     * @param topicSelector
     * @throws NamingException
     */
    public SubscriberController(String factoryName, String topicName, String topicSelector) throws NamingException {
        this.ctx = TCContext.getInitial();
        this.topicSelector = topicSelector;
        initObject(factoryName, topicName);
        initJMS();
    }

    /**
     *
     * @param factoryName
     * @param topicName
     * @param topicSelector
     * @param ctx
     */
    public SubscriberController(String factoryName, String topicName,
                                String topicSelector, InitialContext ctx) {
        this.ctx = ctx;
        this.topicSelector = topicSelector;
        initObject(factoryName, topicName);
        initJMS();
    }

    /**
     *
     */
    public void run() {
        log.debug(this.topicName + " - In run.");
        while (this.active) {
            //log.debug(this.topicName + " - Run looping.");
            if (this.subscriberReady || this.initInProgress) {
                //log.debug(this.topicName + " - Everything seems fine.");
                try {
                    Thread.sleep(this.pollTime);
                } catch (Exception e) {
                }
                continue;
            }

            if (!this.subscriberReady && !this.initInProgress) {
                log.debug(this.topicName + " - Houston... we have a problem... attempting to resolve.");
                while (this.active && !initJMS()) {
                    log.debug(this.topicName + " - Could not resolve problem... trying again...");
                    try {
                        Thread.sleep(this.errorTime);
                    } catch (Exception e) {
                    }
                }
                log.debug(this.topicName + " - Houston... the problem has been resolved.");
            }

        }
        log.debug(this.topicName + " - Finished running.");
        close();
    }

    /**
     *
     * @return
     */
    public boolean isReady() {
        return this.subscriberReady;
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
    public void deactivate() {
        if (this.active = false) {
            return;
        }

        log.debug(this.topicName + " - Deactivated.");
        this.active = false;
    }

    /**
     *
     * @param blockTime
     * @return
     */
    public synchronized ObjectMessage getMessage(int blockTime) {

        boolean retVal = false;
        ObjectMessage msg = null;
        boolean alreadyWaited = false;

        while (true) {
            if (this.subscriberReady) {
                try {
                    if (System.currentTimeMillis() - timeStamp > consoleMessageTime) {
                        log.debug(this.topicName + " - Listening...");
                        timeStamp = System.currentTimeMillis();
                    }

                    try {
                        msg = (ObjectMessage) tsubscriber.receive(blockTime);
                    } catch (Exception e) {
                        log.debug("ERROR: Error retreiving next message.");
                        while (!initJMS()) {
                            log.debug("A topic connection could not be established. Retrying...");
                            try {
                                Thread.sleep(5000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    break;
                } catch (Exception e) {
                    try {
                        e.printStackTrace();
                        msg = null;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    this.subscriberReady = false;
                    log.debug("SubscriberController error occurred while retrieving message from topic");
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
     * @param factoryName
     * @param topicName
     */
    private synchronized void initObject(String factoryName, String topicName) {
        this.subscriberReady = false;
        this.factoryName = factoryName;
        this.topicName = topicName;
        this.topicSelector = "";
        this.active = true;
        this.initInProgress = false;
        this.pollTime = 500;
        this.errorTime = 2000;
        this.consoleMessageTime = 10000;
        this.timeStamp = 0;
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
            this.tconFactory = (TopicConnectionFactory) this.ctx.lookup(this.factoryName);
            this.tcon = this.tconFactory.createTopicConnection();
            this.tsession = this.tcon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            this.topic = (Topic) ctx.lookup(this.topicName);
            this.tsubscriber = this.tsession.createSubscriber(this.topic, this.topicSelector, true);
            this.tcon.start();
            retVal = true;
            this.subscriberReady = true;
            log.debug(this.topicName + " - Subscriber Initialized.");

        } catch (Exception e) {
            log.debug("ERROR: Could not initialize JMS subscriber.");
        }

        this.initInProgress = false;
        return retVal;

    }

    /**
     *
     */
    public void close() {
        this.subscriberReady = false;

        try {
            if (!(tsubscriber == null)) {
                tsubscriber.close();
            }
            if (!(tsession == null)) {
                tsession.close();
            }
            if (!(tcon == null)) {
                tcon.close();
            }
            tsubscriber = null;
            tsession = null;
            tcon = null;
        } catch (Exception e) {
        }

    }

}
