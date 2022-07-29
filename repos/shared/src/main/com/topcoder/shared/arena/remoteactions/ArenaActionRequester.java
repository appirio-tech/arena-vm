/*
 * ArenaActionRequester
 * 
 * Created Oct 25, 2007
 */
package com.topcoder.shared.arena.remoteactions;

import java.util.concurrent.Future;

/**
 * The ArenaActionRequester allows action requests to be delivered to the 
 * arena engine. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ArenaActionRequester {
    
    /**
     * Send a broadcast request for a given round.
     * 
     * @param roundId The round id which the message is addressed to
     * @param message The message to send
     * @return A future which allows waiting for execution completion
     * @throws ArenaActionRequesterException If the broadcast could not be delivered.
     */
    Future<Void> broadcast(int roundId, String message) throws ArenaActionRequesterException;
}
