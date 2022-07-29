package com.topcoder.server.messaging;

//import java.util.*;
//import java.io.*;

import javax.jms.*;
import javax.naming.*;

//import org.apache.log4j.Category;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.TCContext;

public class TopicMessageSubscriber {

    private static final Logger cat = Logger.getLogger(TopicMessageSubscriber.class);

    Context ctx;

    String factoryName;
    String topicName;
    String factoryName_BKP;
    String topicName_BKP;

    protected boolean primaryReady = false;
    protected boolean backupReady;
    private boolean faultTolerant;
    private boolean persistent;
    //boolean VERBOSE = true;

    private boolean alive = true;

    private int pollTime;
    private int errorTime;
    private int consoleMessageTime;
    private String topicSelector = "";

    SubscriberController controller;
    SubscriberController controller_BKP;

    private boolean ctxCreated = true;

    ////////////////////////////////////////////////////////////////////////////////
    public TopicMessageSubscriber(String factoryName, String topicName) throws NamingException
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.ctx = TCContext.getJMSContext();
        this.ctxCreated = true;
        initObject(factoryName, topicName);
    }


    ////////////////////////////////////////////////////////////////////////////////
    public TopicMessageSubscriber(String factoryName, String topicName, InitialContext ctx)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.ctx = ctx;
        this.ctxCreated = false;
        initObject(factoryName, topicName);
    }


    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void setFaultTolerant(boolean value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.faultTolerant = value;
    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void setPersistent (boolean value) {
    ////////////////////////////////////////////////////////////////////////////////
      this.persistent = value;
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void setPollTime(int in) {
        ////////////////////////////////////////////////////////////////////////////////
        this.pollTime = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void setErrorTime(int in) {
        ////////////////////////////////////////////////////////////////////////////////
        this.errorTime = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void setSelector(String in) {
        ////////////////////////////////////////////////////////////////////////////////
        this.topicSelector = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void setConsoleMessageTime(int in) {
        ////////////////////////////////////////////////////////////////////////////////
        this.consoleMessageTime = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ObjectMessage getMessage()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return getMessage(500);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ObjectMessage getMessage(int time)
            ////////////////////////////////////////////////////////////////////////////////
    {

        //boolean reInitPrimary = false;
        //boolean reInitBackup = false;
        ObjectMessage retVal = null;

        if (!primaryReady) {
            setPrimaryController();
        }

        if (faultTolerant && !backupReady) {
            setBackupController();
        }

        while (isAlive()) {
            retVal = controller.getMessage(time);
            if (retVal != null) {
                debug("got message, primary");
                break;
            }

            if (faultTolerant) {
                retVal = controller_BKP.getMessage(time);
                if (retVal != null) {
                    debug("got message, backup");
                    break;
                }
            }
        }

        // Close the topic resources unless they are set to persist.
        if (!persistent) {
            close();
        }

        return retVal;

    }

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized void setPrimaryController()
            ////////////////////////////////////////////////////////////////////////////////
    {
        debug("Initializing primary subscriber.");

        if (this.topicSelector.length() > 0) {
            controller = new SubscriberController(factoryName, topicName, this.topicSelector, ctx);
        } else {
            controller = new SubscriberController(factoryName, topicName, ctx);
        }

        if (this.pollTime > 0) {
            controller.setPollTime(this.pollTime);
        }
        if (this.errorTime > 0) {
            controller.setErrorTime(this.errorTime);
        }
        if (this.consoleMessageTime > 0) {
            controller.setConsoleMessageTime(this.consoleMessageTime);
        }

        controller.start();
        primaryReady = true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized void setBackupController()
            ////////////////////////////////////////////////////////////////////////////////
    {
        debug("Initializing backup subscriber.");

        if (this.topicSelector.length() > 0) {
            controller_BKP = new SubscriberController(factoryName_BKP, topicName_BKP, this.topicSelector, ctx);
        } else {
            controller_BKP = new SubscriberController(factoryName_BKP, topicName_BKP, ctx);
        }

        if (this.pollTime > 0) {
            controller_BKP.setPollTime(this.pollTime);
        }
        if (this.errorTime > 0) {
            controller_BKP.setErrorTime(this.errorTime);
        }
        if (this.consoleMessageTime > 0) {
            controller_BKP.setConsoleMessageTime(this.consoleMessageTime);
        }

        controller_BKP.start();
        backupReady = true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized void initObject(String factoryName, String topicName)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.persistent = true;
        this.faultTolerant = true;
        this.primaryReady = false;
        this.backupReady = false;
        this.factoryName = factoryName;
        this.topicName = topicName;
        this.factoryName_BKP = factoryName + "_BKP";
        this.topicName_BKP = topicName + "_BKP";
        this.pollTime = 0;
        this.errorTime = 0;
        this.consoleMessageTime = 0;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void close()
            ////////////////////////////////////////////////////////////////////////////////
    {

        debug("Closing resources");
        try {
            this.controller.deactivate();
            this.primaryReady = false;

            if (this.faultTolerant || this.backupReady) {
                this.controller_BKP.deactivate();
            }
            this.backupReady = false;

            if (this.ctxCreated) {
                this.ctx.close();
            }

        } catch (Exception e) {
            error("", e);
        }

    }

    /////////////////////////////////////////////////////////////////////////////////
    private boolean isAlive()
            /////////////////////////////////////////////////////////////////////////////////
    {
        return this.alive;
    }

    /////////////////////////////////////////////////////////////////////////////////
    public void deactivate()
            /////////////////////////////////////////////////////////////////////////////////
    {
        this.alive = false;
    }

    private static void debug(Object message) {
        cat.debug(message);
    }

    private static void error(Object message, Throwable t) {
        cat.error(message, t);
    }

}
