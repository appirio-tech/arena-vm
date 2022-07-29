/*
 * ClientManager
 * 
 * Created 08/11/2006
 */
package com.topcoder.farm.controller.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.exception.ClientNotListeningException;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.controller.exception.UnregisteredClientException;
import com.topcoder.farm.controller.model.ClientData;
import com.topcoder.farm.controller.services.DataServices;
import com.topcoder.farm.controller.services.DataServicesImpl;
import com.topcoder.farm.shared.invocation.InvocationFeedback;

/**
 * The ClientManager is responsible for handling clients connected to the controller.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientManager {
    private Log log = LogFactory.getLog(ClientManager.class);
    
    /**
     * This map contains all registered clients 
     */
    private ConcurrentMap<String, Client>  clients = new ConcurrentHashMap();
    
    /**
     * The dataServices used to obtain client information 
     */
    private DataServices dataServices = new DataServicesImpl();
    
    /**
     * Creates a new ClientManager.
     */
    public ClientManager() {
    }
    

    /**
     * Adds the client to the client manager
     * If a client was already registered in this manager using the same id,
     * it will removed and notified about the disconnection  
     * 
     * @param id The id of the client to register
     * @param client The client callback
     * @throws NotAllowedToRegisterException If the client is not allowed to register. (Unknown client)
     */
    public void addClient(String id, ClientNodeCallback client) throws NotAllowedToRegisterException {
        if (log.isDebugEnabled()) {
            log.debug("Adding client: "+id);
        }
        ClientData clientData = dataServices.findClientByName(id);
        if (clientData == null) {
            throw new NotAllowedToRegisterException("Unknown client node: "+id);
        }
        Client  oldClient = clients.put(id, new Client(client));
        if (oldClient != null) {
            try {
                oldClient.callback.unregistered("RE-REGISTERED");
            } catch (RuntimeException e) {
                log.info("Impossible to notify unregistration to client",e);
            }
        }
    }

    
    /**
     * Remove the client with the given Id from this manager.
     * The client is not notified about the unregistration
     *  
     * @param id The id of the client to remove
     * 
     * @return true if the client was registered in this manager
     */
    public boolean removeClient(String id) {
        if (log.isDebugEnabled()) {
            log.debug("Removing client: "+id);
        }
        return clients.remove(id) != null;
    }
    
    /**
     * Removes all references to the registred clients
     */
    public void release() {
        clients.clear();
    }
    
    /**
     * Returns the number of registered clients
     * @return the size
     */
    public int size() {
        return clients.size();
    }
    
    /**
     * Returns the name of all registered clients
     * @return a List with the names
     */
    public List<String> getNames() {
        return new ArrayList(clients.keySet());
    }

    /**
     * Notifies all registred clients that they must disconnect.
     * 
     * @param message A message describing the reason why a diconnected is requested
     */
    public void notifyDisconnect(String message) {
        log.info("Notifiying disconnect to all clients");
        for (Iterator<Client> it = clients.values().iterator(); it.hasNext();) {
            Client client = it.next();
            try {
                client.callback.disconnect(message);
            } catch (Exception e) {
                //Try next one
            }
            client.setListening(false);
        }
    }
    
    /**
     * Notifies the response to the client
     *  
     * @param clientId The client identification to which the notification should be made 
     * @param response The response to notify to the client
     * @throws UnregisteredClientException If the client is not registered on the farm
     * @throws ClientNotListeningException If the client is not listening for notifications
     */
    public void notifyClientResponse(String clientId, InvocationResponse response) throws UnregisteredClientException, ClientNotListeningException {
        if (log.isInfoEnabled()) {
            log.info("Notifiying response to client: "+ clientId +" for requestId="+response.getRequestId());
        }
        Client client = clients.get(clientId);
        if (client == null) {
            throw new UnregisteredClientException("The client " + clientId + " is not registered in the controller");
        }
        if (!client.isListening()) {
            throw new ClientNotListeningException("The client " + clientId + " is registered but not ready to receive results");
        }
        try {
            client.callback.reportInvocationResult(response);
        } catch (RuntimeException e) {
            log.error("Exception while notifying response. Throwing not listening exception",e);
            throw new ClientNotListeningException("The client " + clientId + " is registered but is unable to receive results");
        }
    }

    public void notifyClientFeedback(String clientId, InvocationFeedback invocationFeedback) {
        if (log.isInfoEnabled()) {
            log.info("Notifiying feedback to client: "+ clientId +" for requestId="+invocationFeedback.getRequestId());
        }
        Client client = clients.get(clientId);
        if (client == null || !client.isListening()) {
            log.info("Client is not listening, dropping feedback: "+invocationFeedback);
            return;
        }
        try {
            client.callback.reportInvocationFeedback(invocationFeedback);
        } catch (RuntimeException e) {
            log.error("Exception while notifying feedback. Dropping feedback",e);
        }
    }


    /**
     * Notifies the responses to the client
     *  
     * @param id The client identification to which the notification should be made 
     * @param responses The response list with all the responses to notify to the client
     * @throws UnregisteredClientException If the client is not registered on the farm
     * @throws ClientNotListeningException If the client is not listening for notifications
     */
    public void notifyClientResponses(String id, List<InvocationResponse> responses) throws UnregisteredClientException, ClientNotListeningException {
        if (log.isInfoEnabled()) {
            log.info("Notifiying responses to client: "+ id);
        }
        Client client = clients.get(id);
        if (client == null) {
            throw new UnregisteredClientException("The client " + id + " is not registered in the controller");
        }
        if (!client.listening) {
            throw new ClientNotListeningException("The client " + id + " is registered but not ready to receive results");
        }
        for (InvocationResponse response : responses) {
            try {
                client.callback.reportInvocationResult(response);
            } catch (RuntimeException e) {
                log.error("Exception while notifying response. Throwing not listening exception",e);
                throw new ClientNotListeningException("The client " + id + " is registered but is unable to receive results");
            }
        }
    }


    /**
     * Sets the client as listening for result notifications if it is
     * registered in this Manager
     *  
     * @param id The client identification
     */
    public void setAsListeningResultsIfRegistered(String id) {
        Client client = clients.get(id);
        if (client != null) {
            if (log.isInfoEnabled()) {
                log.info("Setting client: "+ id +" as listening");
            }
            if (!client.listening) {
                client.listening = true;
            }
        }
    }
    
    
    /**
     * Class holding state of the client
     */
    static class Client {
        /**
         * Callback for the client
         */
        ClientNodeCallback callback;
        
        /**
         * Listening state. If true, response notification can be sent to 
         * through the callback
         */
        volatile boolean listening;
        
        public Client(ClientNodeCallback callback) {
            this.callback = callback;
        }

        public boolean isListening() {
            return listening;
        }
        
        public void setListening(boolean listening) {
            this.listening = listening;
        }
    }



}
