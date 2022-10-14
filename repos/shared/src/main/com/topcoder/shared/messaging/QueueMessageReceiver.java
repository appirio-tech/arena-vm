package com.topcoder.shared.messaging;

import java.io.Serializable;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.NamingException;

import com.topcoder.shared.util.TCContext;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author mike lydon
 * @version $Revision$
 */
public class QueueMessageReceiver {
    private static Logger log = Logger.getLogger(QueueMessageReceiver.class);

    Context ctx;

    String factoryName;
    String queueName;
    String factoryName_BKP;
    String queueName_BKP;

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
    private boolean autoCommit;
    private boolean transacted;

    private boolean alive = true;
    private boolean honorBlockTime;
    private int pollTime;
    private int errorTime;
    private int consoleMessageTime;

    private ReceiverController controller;
    private ReceiverController controller_BKP;

    private boolean ctxCreated = true;

    private String selector;

    /**
     *
     * @param factoryName
     * @param queueName
     * @throws NamingException
     */
    public QueueMessageReceiver(String factoryName, String queueName) throws NamingException {
        this.ctx = (Context) TCContext.getInitial();
        this.ctxCreated = true;
        initObject(factoryName, queueName, "");
    }
    

    /**
     *
     * @param factoryName
     * @param queueName
     * @param ctx
     */
    public QueueMessageReceiver(String factoryName, String queueName, Context ctx) {
        this.ctx = ctx;
        this.ctxCreated = false;
        initObject(factoryName, queueName, "");
    }

    /**
     *
     * @param factoryName
     * @param queueName
     * @param ctx
     * @param selector
     */
    public QueueMessageReceiver(String factoryName, String queueName, Context ctx, String selector) {
        this.ctx = ctx;
        this.ctxCreated = false;
        initObject(factoryName, queueName, selector);
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
    public synchronized void setAutoCommit(boolean value) {
        this.autoCommit = value;
    }

    /**
     *
     * @param value
     */
    public synchronized void setTransacted(boolean value) {
        this.transacted = value;
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
    public Serializable receive(){
        try{
            return getMessage().getObject();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param time
     * @return
     */
    public ObjectMessage getMessage(int time) {
        ObjectMessage retVal = null;

        initIfNecessary();

        while (isAlive()) {
            retVal = controller.getMessage(time, this.autoCommit);

            if (retVal != null) {
                break;
            }

            if (retVal == null && faultTolerant) {
                retVal = controller_BKP.getMessage(time, this.autoCommit);
                if (retVal != null) {
                    break;
                }
            }
            if (honorBlockTime) {
                break;
            }
        }

        // Close the queue resources unless they are set to persist.
        if (!persistent) {
            close();
        }

        return retVal;

    }


    public void initIfNecessary() {
        if (!primaryReady) {
            setPrimaryController();
        }

        if (faultTolerant && !backupReady) {
            setBackupController();
        }
    }

    /**
     *
     */
    private synchronized void setPrimaryController() {
        //log.debug("Initializing primary receiver.");
        controller = new ReceiverController(factoryName, queueName, this.transacted, ctx, selector);

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
        //log.debug("Initializing backup receiver.");
        controller_BKP = new ReceiverController(factoryName_BKP, queueName_BKP, this.transacted, ctx, selector);

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
     * @return
     */
    public synchronized boolean commit() {
        boolean retVal = true;

        try {

            controller.commit();

            if (this.faultTolerant) {
                controller_BKP.commit();
            }

        } catch (Exception e) {
            log.error("ERROR:  Could not commit JMS transaction.");
            e.printStackTrace();
            retVal = false;
        }

        return retVal;

    }

    /**
     *
     */
    public synchronized void rollback() {

        try {

            controller.rollback();

            if (this.faultTolerant) {
                controller_BKP.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param factoryName
     * @param queueName
     * @param selector
     */
    private synchronized void initObject(String factoryName, String queueName, String selector) {
        this.persistent = true;
        this.autoCommit = true;
        this.transacted = true;
        this.faultTolerant = true;
        this.primaryReady = false;
        this.backupReady = false;
        this.factoryName = factoryName;
        this.queueName = queueName;
        this.factoryName_BKP = factoryName + "_BKP";
        this.queueName_BKP = queueName + "_BKP";
        this.pollTime = 0;
        this.errorTime = 0;
        this.consoleMessageTime = 0;
        this.selector = selector;
    }

    /**
     *
     */
    public synchronized void close() {

        try {
            this.controller.deactivate();
            this.controller.close();
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
    
    public Queue getQueue() {
        if (controller != null) {
            return controller.getQueue();
        }
        return null;
    }


    public void setHonorBlockTime(boolean honorBlockTime) {
        this.honorBlockTime = honorBlockTime;
    }
}
