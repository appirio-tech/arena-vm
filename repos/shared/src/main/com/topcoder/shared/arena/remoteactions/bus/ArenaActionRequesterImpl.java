/*
 * ArenaActionRequesterImpl
 * 
 * Created Oct 25, 2007
 */
package com.topcoder.shared.arena.remoteactions.bus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.topcoder.shared.arena.remoteactions.ArenaActionRequester;
import com.topcoder.shared.arena.remoteactions.ArenaActionRequesterException;
import com.topcoder.shared.messagebus.BusFactory;
import com.topcoder.shared.messagebus.BusFactoryException;
import com.topcoder.shared.messagebus.BusRequestPublisher;
import com.topcoder.shared.messagebus.invoker.BusRemoteInvoker;
import com.topcoder.shared.messagebus.invoker.RemoteInvokerException;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ArenaActionRequesterImpl implements ArenaActionRequester {
    private BusRemoteInvoker invoker;

    public ArenaActionRequesterImpl(String moduleName) throws ArenaActionRequesterException {
        try {
            BusRequestPublisher publisher = BusFactory.getFactory().createRequestPublisher(ArenaActionRequesterConstants.DATA_BUS_REQ_CONFIG_KEY, moduleName);
            invoker = new BusRemoteInvoker(moduleName, ArenaActionRequesterConstants.NAMESPACE, publisher);
        } catch (BusFactoryException e) {
            throw new ArenaActionRequesterException("Could not create ArenaActionRequester underlying services", e);
        }
    }

    /**
     * Sends a broadcast message to the given round
     * 
     * @param roundId the id of the round that is the destination of the message 
     * @param message The message to send
     * @return A future object on which the calling thread may wait for execution completion.
     * 
     * @throws ArenaActionRequesterException If the broadcast request could not be delivered
     */
    public Future<Void> broadcast(int roundId, String message) throws ArenaActionRequesterException {
        try {
            Map map = new HashMap();
            map.put(ArenaActionRequesterConstants.ACTION_BROADCAST_ARG_ROUND_ID, new Integer(roundId));
            map.put(ArenaActionRequesterConstants.ACTION_BROADCAST_ARG_MESSAGE, message);
            return invoker.invoke(ArenaActionRequesterConstants.ACTION_BROADCAST, map);
        } catch (RemoteInvokerException e) {
            throw new ArenaActionRequesterException("Failed to submit broadcast request",e);
        }
    }
}
