package com.topcoder.server.services;

/**
 * Title:        WebServiceGeneratorService
 * Description:  Puts a WebServiceProblem on the Web Service Queue
 * Copyright:    Copyright (c) 2002
 * Company:      TopCoder
 * @author       Jeremy Nuanes
 * @version 1.0
 */

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.server.webservice.WebServiceProblem;
import com.topcoder.server.webservice.WebServiceDeploymentResult;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.messaging.QueueMessageSender;
import com.topcoder.server.messaging.TopicMessagePublisher;
import com.topcoder.shared.util.DBMS;

import java.util.HashMap;

public final class WebServiceGeneratorService {

    /**
     * Category for logging.
     */
    private static Logger logger = Logger.getLogger(WebServiceGeneratorService.class);

    private static QueueMessageSender m_msgSender;
    private static TopicMessagePublisher _mpsqasPublisher;

    /* Static initialization block for the topic stuff */
    static {
        logger.debug("Initializing WebServiceGeneratorService...");
        try {
            logger.debug("Initializing TopicMessagePublisher for MPSQAS...");

            _mpsqasPublisher = new TopicMessagePublisher(ApplicationServer.JMS_FACTORY, DBMS.MPSQAS_TOPIC);
            _mpsqasPublisher.setPersistent(true);
            _mpsqasPublisher.setFaultTolerant(false);
        } catch (Exception e) {
            logger.fatal("Failed to initialize MPSQASService", e);
        }
        try {
            m_msgSender = new QueueMessageSender(ApplicationServer.JMS_FACTORY, DBMS.WEB_SERVICE_QUEUE, TCContext.getJMSContext());
            m_msgSender.setPersistent(true);
            m_msgSender.setDBPersistent(false);
            m_msgSender.setFaultTolerant(false);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static boolean sendBuildWebService(WebServiceProblem problem, int id) {
        logger.debug("In WebServiceGeneratorService.sendBuildWebService()...");
        try {
            HashMap props = new HashMap();
            props.put("id", new Integer(id));
            return m_msgSender.sendMessage(props, problem);
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    public static boolean sendWebServiceDeploymentResult(WebServiceDeploymentResult result, int id) {
        logger.debug("In WebServiceGeneratorService.sendWebServiceDeploymentResult()...");
        try {
            HashMap props = new HashMap();
            props.put("completedAction",
                    new Integer(ServicesConstants.WEB_SERVICE_DEPLOY_ACTION));
            props.put("id", new Integer(id));
            return _mpsqasPublisher.pubMessage(props, result);
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }
}
