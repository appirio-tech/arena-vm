package com.topcoder.server.messaging;

//import java.util.*;
//import java.io.*;

import javax.jms.*;
import javax.naming.*;

import com.topcoder.shared.util.logging.Logger;

//import org.apache.log4j.Category;

//import com.topcoder.server.common.Log;

class SubscriberController extends Thread {

    private static final Logger cat = Logger.getLogger(SubscriberController.class);

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
    boolean active;
    boolean initInProgress;
    //boolean VERBOSE = false;

    /*
    ////////////////////////////////////////////////////////////////////////////////
    SubscriberController (String factoryName, String topicName) throws NamingException
    ////////////////////////////////////////////////////////////////////////////////
    {
      this.ctx = TCContext.getInitial();
      initObject(factoryName, topicName);
      initJMS();
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    SubscriberController(String factoryName, String topicName, Context ctx)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.ctx = ctx;
        initObject(factoryName, topicName);
        initJMS();
    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    SubscriberController (String factoryName, String topicName, String topicSelector) throws NamingException
    ////////////////////////////////////////////////////////////////////////////////
    {
      this.ctx = TCContext.getInitial();
      this.topicSelector = topicSelector;
      initObject(factoryName, topicName);
      initJMS();
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    SubscriberController(String factoryName, String topicName,
            String topicSelector, Context ctx)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.ctx = ctx;
        this.topicSelector = topicSelector;
        initObject(factoryName, topicName);
        initJMS();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void run() {
        ////////////////////////////////////////////////////////////////////////////////
        debug(this.topicName + " - In run.");
        while (this.active) {
            //Log.msg(this.topicName + " - Run looping.");
            if (this.subscriberReady || this.initInProgress) {
                //Log.msg(this.topicName + " - Everything seems fine.");
                try {
                    Thread.sleep(this.pollTime);
                } catch (Exception e) {
                }
                continue;
            }

            if (!this.subscriberReady && !this.initInProgress) {
                error(this.topicName + " - Houston... we have a problem... attempting to resolve.");
                while (this.active && !initJMS()) {
                    error(this.topicName + " - Could not resolve problem... trying again...");
                    try {
                        Thread.sleep(this.errorTime);
                    } catch (InterruptedException e) {
                        error("", e);
                    }
                }
                info(this.topicName + " - Houston... the problem has been resolved.");
            }

        }
        debug(this.topicName + " - Finished running.");
        close();
    }

    ////////////////////////////////////////////////////////////////////////////////
    boolean isReady()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return this.subscriberReady;
    }

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
        if (this.active = false) {
            return;
        }

        debug(this.topicName + " - Deactivated.");
        this.active = false;
    }

    ////////////////////////////////////////////////////////////////////////////////
    synchronized ObjectMessage getMessage(int blockTime)
            ////////////////////////////////////////////////////////////////////////////////
    {

        //boolean retVal = false;
        ObjectMessage msg = null;
        boolean alreadyWaited = false;

        while (true) {
            if (this.subscriberReady) {
                try {
                    if (System.currentTimeMillis() - timeStamp > consoleMessageTime) {
                        //debug(this.topicName + " - Listening...");
                        timeStamp = System.currentTimeMillis();
                    }

                    try {
                        msg = (ObjectMessage) tsubscriber.receive(blockTime);
                        if (msg != null) {
                            debug("received message, topic=" + tsubscriber.getTopic().getTopicName());
                        }
                    } catch (Exception e) {
                        error("ERROR: Error retreiving next message.", e);
                        while (!initJMS()) {
                            error("A topic connection could not be established. Retrying...");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                error("", ex);
                            }
                        }
                    }

                    break;
                } catch (Exception e) {
                    error("SubscriberController error occurred while retrieving message from topic", e);
                    msg = null;
                    /*
                    try{
                      e.printStackTrace();
                      msg = null;
                    }catch (Exception e1) {e1.printStackTrace();}
                    */
                    this.subscriberReady = false;
                    //Log.msg("SubscriberController error occurred while retrieving message from topic");
                    break;
                }
            } else {
                if (alreadyWaited) {
                    break;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        error("", e);
                    }
                    alreadyWaited = true;
                    continue;
                }
            }
        }
        return msg;

    }

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized void initObject(String factoryName, String topicName)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.subscriberReady = false;
        this.factoryName = factoryName;
        this.topicName = topicName;
        //this.topicSelector = "";
        this.active = true;
        this.initInProgress = false;
        this.pollTime = 500;
        this.errorTime = 2000;
        this.consoleMessageTime = 10000;
        this.timeStamp = 0;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized boolean initJMS()
            ////////////////////////////////////////////////////////////////////////////////
    {
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
            debug(this.topicName + " - Subscriber Initialized.");

        } catch (Exception e) {
            if (e instanceof InvalidSelectorException) {
                InvalidSelectorException invalidSelectorException = (InvalidSelectorException) e;
                Exception linkedException = invalidSelectorException.getLinkedException();
                error("", linkedException);
            }
            error("ERROR: Could not initialize JMS subscriber.", e);
        }

        this.initInProgress = false;
        return retVal;

    }

    ////////////////////////////////////////////////////////////////////////////////
    synchronized void close()
            ////////////////////////////////////////////////////////////////////////////////
    {
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
            error("", e);
        }

    }

    private static void debug(Object message) {
        cat.debug(message);
    }

    private static void info(Object message) {
        cat.info(message);
    }

    private static void error(Object message) {
        cat.error(message);
    }

    private static void error(Object message, Throwable t) {
        cat.error(message, t);
    }

}
