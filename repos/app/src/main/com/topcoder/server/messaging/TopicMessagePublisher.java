package com.topcoder.server.messaging;

import java.util.*;
import java.io.*;
import javax.jms.*;
import javax.naming.*;

//import org.apache.log4j.Category;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.TCContext;

public class TopicMessagePublisher {

    /**
     * Category for logging.
     */
    private static Logger trace = Logger.getLogger(TopicMessagePublisher.class);


    private static final int PRIMARY = 0;
    private static final int BACKUP = 1;

    Context ctx;
    TopicConnectionFactory tconFactory;
    TopicConnection tcon;
    Topic topic;
    TopicSession tsession;
    TopicPublisher tpub;

    TopicConnectionFactory tconFactory_BKP;
    TopicConnection tcon_BKP;
    Topic topic_BKP;
    TopicSession tsession_BKP;
    TopicPublisher tpub_BKP;

    String factoryName;
    String topicName;
    String factoryName_BKP;
    String topicName_BKP;

    boolean persistent;
    boolean primaryReady = false;
    boolean backupReady;
    boolean faultTolerant;
    public static boolean VERBOSE = true;

    ////////////////////////////////////////////////////////////////////////////////
    public TopicMessagePublisher(String factoryName, String topicName) throws NamingException
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.primaryReady = false;
        this.backupReady = false;
        this.ctx = TCContext.getJMSContext();
        initObject(factoryName, topicName);
    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    public TopicMessagePublisher (String factoryName, String topicName, InitialContext ctx)
    ////////////////////////////////////////////////////////////////////////////////
    {
      this.primaryReady = false;
      this.backupReady = false;
      this.ctx = ctx;
      initObject(factoryName, topicName);
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void setPersistent(boolean value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.persistent = value;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void setFaultTolerant(boolean value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.faultTolerant = value;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean pubMessage(HashMap props)
            ////////////////////////////////////////////////////////////////////////////////
    {
        Object temp = null;
        return pubMessage(props, temp);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized boolean pubMessage(HashMap props, Object messObject)
            ////////////////////////////////////////////////////////////////////////////////
    {
        int activeTopic = PRIMARY;
        boolean retVal = false;
        boolean reInitPrimary = false;
        boolean reInitBackup = false;

        while (true) {

            if (activeTopic == PRIMARY) {
                if (!this.primaryReady) {
                    reInitPrimary = true;
                    if (!initJMS(PRIMARY)) {
                        trace.error("INITIALIZE PRIMARY FAILED");
                        if (faultTolerant) {
                            trace.info("CHANGING ACTIVE TOPIC TO BACKUP");
                            // Tried to reinit the primary topic but we were unsuccessful...
                            // switch over to the backup topic.
                            activeTopic = BACKUP;
                            continue;
                        } else {
                            // Couldn't reinit the primary, and we have no fault tolerance...
                            // time to give up
                            retVal = false;
                            break;
                        }
                    }
                }
                if (pubMessage(this.tsession, this.tpub, props, messObject)) {
                    // Message was sent successfully... let's break out.
                    retVal = true;
                    break;
                } else {
                    trace.error("Could not publish message on primary topic.");
                    if (reInitPrimary) {
                        if (faultTolerant) {
                            // We've already tried reinit of the primary topic.
                            // Switch the activeQueue over to the backup... continue loop.
                            activeTopic = BACKUP;
                            continue;
                        } else {
                            // Can't set the activeQueue to backup... no fault tolerance.
                            // Time to give up.
                            retVal = false;
                            break;
                        }
                    } else {
                        // We havn't tried reinit on the primary topic yet...
                        // stay with primary topic for now, but force it to reinit.
                        this.primaryReady = false;
                        continue;
                    }
                }
            } else {
                if (!this.backupReady) {
                    reInitBackup = true;
                    if (!initJMS(BACKUP)) {
                        // Could not reinit the backup topic... sincee we're here, we couldn't publish on
                        // the primary topic either... give up.
                        retVal = false;
                        break;
                    }
                }
                if (pubMessage(this.tsession_BKP, this.tpub_BKP, props, messObject)) {
                    // Got the message to go on the backup topic... let's break out.
                    retVal = true;
                    break;
                } else {
                    trace.error("Could not publish message on backup topic.");
                    if (reInitBackup) {
                        // We couldn't publish the message, but we've already tried a
                        // reinit on the backup topic... let's give up.
                        retVal = false;
                        break;
                    } else {
                        // We couldn't publish the message, but we havn't yet tried a
                        // reinit on the backup topic... force the topic to reinit
                        // on the next pass.
                        this.backupReady = false;
                        continue;
                    }
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
    private boolean pubMessage(TopicSession tSess, TopicPublisher tPub, HashMap props, Object messObject)
            ////////////////////////////////////////////////////////////////////////////////
    {
        boolean retVal = false;
        try {

            ObjectMessage msg = tSess.createObjectMessage();
            msg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);

            //trace.debug("GT: pubMessage1");
            if (!props.isEmpty()) {
                Set keys = props.keySet();
                Iterator iter = keys.iterator();
                while (iter.hasNext()) {
                    //trace.debug("GT: pubMessage2");
                    String key = (String) iter.next();
                    Object value = props.get(key);

                    if (value instanceof String) {
                        msg.setStringProperty(key, (String) value);
                    } else if (value instanceof Integer) {
                        Integer holder = (Integer) value;
                        msg.setIntProperty(key, holder.intValue());
                    } else if (value instanceof Boolean) {
                        Boolean holder = (Boolean) value;
                        msg.setBooleanProperty(key, holder.booleanValue());
                    } else if (value instanceof Double) {
                        Double holder = (Double) value;
                        msg.setDoubleProperty(key, holder.doubleValue());
                    } else if (value instanceof Long) {
                        Long holder = (Long) value;
                        msg.setLongProperty(key, holder.longValue());
                    } else if (value instanceof Short) {
                        Short holder = (Short) value;
                        msg.setShortProperty(key, holder.shortValue());
                    } else if (value instanceof Float) {
                        Float holder = (Float) value;
                        msg.setFloatProperty(key, holder.floatValue());
                    }
                }
                //trace.debug("GT: pubMessage3");
            }

            if (!(messObject == null)) {
                //trace.debug("GT: pubMessage4");
                //trace.debug("Here it is: "+messObject.toString());
                //try {
                //    ByteArrayOutputStream byteout = new ByteArrayOutputStream();
                //    ObjectOutputStream    objout = new ObjectOutputStream(byteout);
                //    objout.writeObject(messObject);
                //    objout.close();
                //    trace.info("GT: and the size is... "+byteout.size());
                //} catch (java.io.IOException ioe) {
                //    trace.error("IOE Error", ioe);
                //}

                //Byte[] big = new Byte[messObject];
                //trace.debug("Size is: "+ big.length);
                msg.setObject((Serializable) messObject);
                //trace.debug("GT: pubMessage5");
            }
            //trace.debug("GT: pubMessage6");
            trace.debug("publish, topic=" + tpub.getTopic().getTopicName());
            //trace.debug("GT: pubMessage7");
            tPub.publish(msg);
            //trace.debug("GT: pubMessage8");
            retVal = true;

        } catch (JMSException e) {
            trace.error("Error occurred while publishing a message to the topic", e);
            retVal = false;
        }

        return retVal;

    }

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized void initObject(String factoryName, String topicName)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.persistent = false;
        this.faultTolerant = true;
        this.primaryReady = false;
        this.backupReady = false;
        this.factoryName = factoryName;
        this.topicName = topicName;
        this.factoryName_BKP = factoryName + "_BKP";
        this.topicName_BKP = topicName + "_BKP";
    }

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized boolean initJMS(int topicType)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (topicType == PRIMARY) {
            trace.info("Initializing primary JMS topic.");
        } else {
            trace.info("Initializing backup JMS topic.");
        }

        String factoryName = "";
        String topicName = "";
        boolean retVal = false;

        if (topicType == PRIMARY) {
            factoryName = this.factoryName;
            topicName = this.topicName;
        } else {
            factoryName = this.factoryName_BKP;
            topicName = this.topicName_BKP;
        }

        try {
            this.tconFactory = (TopicConnectionFactory) ctx.lookup(factoryName);
            this.tcon = this.tconFactory.createTopicConnection();
            this.tsession = this.tcon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            this.topic = (Topic) ctx.lookup(topicName);
            this.tpub = this.tsession.createPublisher(this.topic);
            this.tpub.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            retVal = true;
            if (topicType == PRIMARY) {
                this.primaryReady = true;
            } else {
                this.backupReady = true;
            }

            this.tcon.start();

        } catch (Exception e) {
            if (topicType == PRIMARY) {
                trace.error("Could not initialize primary JMS topic.", e);
            } else {
                trace.error("Could not initialize backup JMS topic.", e);
            }
        }

        return retVal;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public synchronized void close()
            ////////////////////////////////////////////////////////////////////////////////
    {
        trace.info("close");
        this.primaryReady = false;
        this.backupReady = false;

        try {
            if (!(tpub == null)) {
                tpub.close();
            }
            if (!(tsession == null)) {
                tsession.close();
            }
            if (!(tcon == null)) {
                tcon.close();
            }
            tpub = null;
            tsession = null;
            tcon = null;
        } catch (JMSException e) {
        }

        try {
            if (!(tpub_BKP == null)) {
                tpub_BKP.close();
            }
            if (!(tsession_BKP == null)) {
                tsession_BKP.close();
            }
            if (!(tcon_BKP == null)) {
                tcon_BKP.close();
            }
            tpub_BKP = null;
            tsession_BKP = null;
            tcon_BKP = null;
        } catch (JMSException e) {
        }

    }

}
