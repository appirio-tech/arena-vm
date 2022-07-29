package com.topcoder.shared.messaging;

import com.topcoder.shared.util.TCContext;
import com.topcoder.shared.util.logging.Logger;

import javax.jms.ObjectMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author mike lydon
 * @version $Revision$
 */
public class TopicMessageSubscriber {
    private static Logger log = Logger.getLogger(TopicMessageSubscriber.class);
    InitialContext ctx;

    String factoryName;
    String topicName;
    String factoryName_BKP;
    String topicName_BKP;

    /**
     *
     */
    protected boolean primaryReady = false;
    /**
     *
     */
    protected boolean backupReady;
    private boolean faultTolerant;
    private boolean persistent;

    private boolean alive = true;

    private int pollTime;
    private int errorTime;
    private int consoleMessageTime;
    private String topicSelector = "";

    SubscriberController controller;
    SubscriberController controller_BKP;

    private boolean ctxCreated = true;

    /**
     *
     * @param factoryName
     * @param topicName
     * @throws NamingException
     */
    public TopicMessageSubscriber(String factoryName, String topicName) throws NamingException {
        this.ctx = (InitialContext) TCContext.getContestInitial();
        this.ctxCreated = true;
        initObject(factoryName, topicName);
    }

    /**
     *
     * @param factoryName
     * @param topicName
     * @param ctx
     */
    public TopicMessageSubscriber(String factoryName, String topicName, InitialContext ctx) {
        this.ctx = ctx;
        this.ctxCreated = false;
        initObject(factoryName, topicName);
    }

    /**
     *
     * @param value
     */
    public synchronized void setFaultTolerant(boolean value) {
        this.faultTolerant = value;
    }

    /**
     *
     * @param value
     */
    public synchronized void setPersistent(boolean value) {
        this.persistent = value;
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
     * @param in
     */
    public synchronized void setErrorTime(int in) {
        this.errorTime = in;
    }

    /**
     *
     * @param in
     */
    public synchronized void setSelector(String in) {
        this.topicSelector = in;
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
     * @return
     */
    public ObjectMessage getMessage() {
        return getMessage(500);
    }

    /**
     *
     * @param time
     * @return
     */
    public ObjectMessage getMessage(int time) {

        boolean reInitPrimary = false;
        boolean reInitBackup = false;
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
                break;
            }

            if (faultTolerant) {
                retVal = controller_BKP.getMessage(time);
                if (retVal != null) {
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

    /**
     *
     */
    private synchronized void setPrimaryController() {
        log.debug("Initializing primary subscriber.");

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

    /**
     *
     */
    private synchronized void setBackupController() {
        log.debug("Initializing backup subscriber.");

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

    /**
     *
     * @param factoryName
     * @param topicName
     */
    private synchronized void initObject(String factoryName, String topicName) {
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

    /**
     *
     */
    public synchronized void close() {

        System.out.println("Closing resources");
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
        }

    }

    /**
     *
     * @return
     */
    private boolean isAlive() {
        return this.alive;
    }

    /**
     *
     */
    public void deactivate() {
        this.alive = false;
    }

}
