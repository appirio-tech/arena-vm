/*
 * NetMessageProcessor
 * 
 * Created 03/24/2006
 */
package com.topcoder.client.netClient;

import com.topcoder.netCommon.contestantMessages.response.EndSyncResponse;
import com.topcoder.netCommon.contestantMessages.response.KeepAliveInitializationDataResponse;
import com.topcoder.netCommon.contestantMessages.response.StartSyncResponse;
import com.topcoder.netCommon.contestantMessages.response.UnsynchronizeResponse;

/**
 * This class is responsible to handle all incoming net mesages.
 * We don't want these messages reach to higher layers 
 * Eg: unblocking msg, net configuration messages
 *  
 * @autor Diego Belfer (Mural)
 * @version $Id: NetMessageProcessor.java 44257 2006-04-18 14:40:19Z thefaxman $
 */
public class NetMessageProcessor {
    /**
     * Client object associated to this NetMessageProcessor
     */
     private Client client;
        
    /**
     * ResponseWaiterManager used to notify about incoming messages to synch request
     */
    private ResponseWaiterManager waiterManager;
    
    
    /**
     * Creates a new NetMessageProcessor for the specified client and waiterManager
     * 
     * @param client Client object associated to this NetMessageProcessor
     * @param waiterManager ResponseWaiterManager that will be used to notify about 
     *                         incoming messages to synch request
     */
    public NetMessageProcessor(Client client, ResponseWaiterManager waiterManager) {
        this.client =  client;
        this.waiterManager = waiterManager;
    }


    /**
     * Process the message object.
     * 
     * @param message The message to be processed
     * 
     * @return  true  if the message was processed. 
     *             false otherwise
     */
    public boolean processIncomingMessage(Object message) {
        if (message instanceof UnsynchronizeResponse) {
            waiterManager.unblock(((UnsynchronizeResponse) message).getID());
        } else if (message instanceof StartSyncResponse) {
            waiterManager.startOfSyncResponse(((StartSyncResponse) message).getRequestId());
        } else if (message instanceof EndSyncResponse) {
            waiterManager.endOfSyncResponse(((EndSyncResponse) message).getRequestId());
        } else if (message instanceof KeepAliveInitializationDataResponse) {
            /*
             * keep-alive initialization response message, used to update keep-alive parameters
             * of the client connection heartbeat. We don't want this message get higher layers.
             */
            KeepAliveInitializationDataResponse keepAliveResponse = (KeepAliveInitializationDataResponse) message;
            client.updateKeepAliveParameters(keepAliveResponse.getTimeout(), keepAliveResponse.getHttpTimeout());
        } else {
            return false;
        }
        return true;
    }
}
