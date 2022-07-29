/*
 * BusRequestPublisher
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.messagebus;

import java.util.concurrent.Future;

/**
 * A BusRequestPublisher extends BusPublisher allowing Request/Response pattern to
 * be implemented over the bus.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface BusRequestPublisher  extends BusPublisher {
    /**
     * Sends a bus message as a request message<p>
     * 
     * The returned Future can be used for receive a incoming response message for the request.
     * 
     * @param message The message sent as a request
     * @return A future that will provide the response message as soon as the response message arrives.
     * @throws BusException If the message could not be sent.
 */
    Future<BusMessage> request(BusMessage message) throws BusException;
}
