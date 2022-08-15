package com.topcoder.shared.screening.services;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.ejb.CreateException;
import javax.jms.ObjectMessage;

import com.topcoder.shared.messaging.QueueMessageSender;
import com.topcoder.shared.messaging.QueueMessageReceiver;
import com.topcoder.shared.screening.common.ScreeningContext;
import com.topcoder.shared.screening.common.ScreeningApplicationServer;
import com.topcoder.shared.netCommon.screening.request.ScreeningBaseRequest;
import com.topcoder.shared.netCommon.screening.response.ScreeningBaseResponse;
import com.topcoder.shared.util.DBMS;

/**
 * This class will contain all the static methods for use by
 * anyone who wants to send a request or response using JMS.
 * @author Lars Backstrom
 */
public final class ScreeningListenerMessageService {


    private QueueMessageSender requestSender;
    private QueueMessageReceiver responseReceiver;
    private HashMap messageToConnection = new HashMap();


    //Static initialization block for the JMS stuff
    public ScreeningListenerMessageService(long serverID) throws NamingException{
            String jmsFactory = DBMS.JMS_FACTORY;
            String requestQueueName = DBMS.REQUEST_QUEUE;
            String responseQueueName = DBMS.RESPONSE_QUEUE;
            Context context = ScreeningContext.getJMSContext();

            requestSender = new QueueMessageSender(jmsFactory, requestQueueName, context);
            responseReceiver = new QueueMessageReceiver(jmsFactory, responseQueueName, context, "serverID = "+serverID);
            //m_msgSender = new QueueMessageSender(ApplicationServer.JMS_FACTORY, DBMS.COMPILE_QUEUE);

            requestSender.setPersistent(true);
            requestSender.setDBPersistent(false);
            requestSender.setFaultTolerant(false);

            responseReceiver.setPersistent(true);
            responseReceiver.setFaultTolerant(false);

            //ejbServerIsUp();
    }

    /**
     * Sends a request to a processor.
     * @param serverID
     * @param connectionID
     * @param request
     * @return String - the messageID
     */
    public boolean sendRequest(ScreeningBaseRequest request, long connectionID){
        try {
            HashMap props = new HashMap();
            String id = requestSender.sendMessageGetID(props, request);
            if(connectionID >=0 && id != null){
                messageToConnection.put(id,new Long(connectionID));
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public ScreeningBaseResponse getResponse(){
        try {
            ObjectMessage mess = responseReceiver.getMessage();
            ScreeningBaseResponse response = (ScreeningBaseResponse) mess.getObject();
            Long connectionID = (Long)messageToConnection.remove(mess.getJMSCorrelationID());
            if(connectionID != null){
                response.setConnectionID(connectionID.longValue());
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
