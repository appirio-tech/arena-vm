package com.topcoder.server.mpsqas.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import javax.jms.ObjectMessage;

import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.messaging.QueueMessageSender;
import com.topcoder.server.messaging.TopicMessageSubscriber;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.server.webservice.WebServiceDeploymentResult;
import com.topcoder.server.webservice.WebServiceProblem;
import com.topcoder.shared.common.ServicesConstants;

/**
 *
 *
 *
 *
 *
 * @author mitalub
 */
public class WebServiceWaiter extends Thread {

    private static final Logger logger = Logger.getLogger(WebServiceWaiter.class);
    private static QueueMessageSender qms;
    private static TopicMessageSubscriber tms;

    private final static int TIMEOUT = 100000;
    private final static int MAX_ERRORS = 3;

    private static boolean connected = false;

    /**
     * ArrayList of ids waiting for web serivce responses
     *
     */
    private static ArrayList waiting = new ArrayList();

    /**
     * HashMap of generationId -> WebServiceDeploymentResult,
     * for looking up web service results.
     */
    private static HashMap results = new HashMap();

    /**
     * The web serivce generation id sequence.
     */
    private static int generationId = 1;

    private static Thread topicListener;

    /**
     * Get connections to the queue and topic
     */
    public static void getConnections() {
        try {
            qms = new QueueMessageSender(ApplicationServer.JMS_FACTORY,
                    DBMS.WEB_SERVICE_QUEUE, TCContext.getJMSContext());
            qms.setPersistent(true);
            qms.setDBPersistent(false);
            qms.setFaultTolerant(false);
            logger.info("Got connection to WEB_SERVICE_QUEUE.");

            tms = new TopicMessageSubscriber(ApplicationServer.JMS_FACTORY,
                    DBMS.MPSQAS_TOPIC);
            tms.setFaultTolerant(false);
            logger.info("Got connection to MPSQAS_TOPIC.");

            topicListener = new WebServiceWaiter();
            topicListener.start();
            logger.info("WebServiceWaiter listener started, connected = true.");

            connected = true;
        } catch (Exception e) {
            logger.error("Error initializing WebServiceWaiter.", e);
            connected = false;
        }
    }

    /**
     * Returns a unique generation id.
     */
    private static synchronized int getGenerationId() {
        return generationId++;
    }

    /**
     *  This method puts a request on the web service queue to deploy the service
     *  and locks
     *  the thread until the topic listener unlocks it when the results are back.
     */
    public static WebServiceDeploymentResult deployService(WebServiceProblem
            webService)
            throws Exception {
        WebServiceDeploymentResult wsdr = null;

        if (!connected) {
            getConnections();
        }

        if (!connected) {
            wsdr = new WebServiceDeploymentResult(
                    false, "No connection to web service queue available.");
            return wsdr;
        }

        int generationId = getGenerationId();
        int tries = 0;
        boolean sent = false;

        HashMap props = new HashMap();
        props.put("id", new Integer(generationId));

        waiting.add(new Integer(generationId));

        logger.info("Generation with id=" + generationId + " being submitted.");

        while (sent == false && tries < MAX_ERRORS) {
            try {
                sent = qms.sendMessage(props, webService);
            } catch (Exception e) {
                logger.error("Error putting generation request on queue.", e);
            }
            tries++;
        }

        if (sent) {
            logger.info("Generation with id=" + generationId + " submitted, waiting.");

            long startTime = System.currentTimeMillis();
            while (results.get(new Integer(generationId)) == null &&
                    System.currentTimeMillis() - startTime < TIMEOUT) {
                try {
                    Thread.sleep(100);
                } catch (Exception ignore) {
                }
            }
            logger.info("Done waiting.");

            if (results.get(new Integer(generationId)) == null) {
                wsdr = new WebServiceDeploymentResult(
                        false, "Web Service Generation timed out.");
            } else {
                wsdr = (WebServiceDeploymentResult)
                        results.get(new Integer(generationId));
                waiting.remove(new Integer(generationId));
                results.remove(new Integer(generationId));
            }
        } else {
            wsdr = new WebServiceDeploymentResult(
                    false, "Couldn't put generation request on queue.");
            logger.error("Too many errors, giving up connections.");
            connected = false;
            topicListener.interrupt();
        }
        return wsdr;
    }

    /**
     *  Listens for messages on the topic.  When a message comes in, the method
     *  first checks that the result is being waited on, and if it is,
     *  stores the result in the table for the deployService() thread to pick up.
     */
    public void run() {
        ObjectMessage message;
        int type;
        int id;
        int errorCount = 0;

        while (errorCount < MAX_ERRORS) {
            message = null;
            try {
                logger.info("Trying to get message.");
                message = tms.getMessage(TIMEOUT);
                logger.info("Done trying, message=" + message);
            } catch (Exception e) {
                errorCount++;
                logger.error("Error reading message from topic. ", e);
            }

            if (message != null) {
                try {
                    if (message.propertyExists("completedAction")) {
                        type = message.getIntProperty("completedAction");
                        if (type == ServicesConstants.WEB_SERVICE_DEPLOY_ACTION) {
                            logger.info("WEB_SERVICE_DEPLOY_ACTION received..");
                            id = message.getIntProperty("id");
                            logger.info("Generation with id=" + id + " completed.");
                            if (waiting.indexOf(new Integer(id)) != -1) {
                                results.put(new Integer(id), message.getObject());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Invalid message received.", e);
                }
            }
        }

        logger.error("Too many errors in WebServiceWaiter, giving up connections.");
        connected = false;
    }
}
