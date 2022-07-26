package com.topcoder.server.AdminListener.response;

import java.io.Serializable;

/**
 * A response to request to perform some security schema management operation.
 * Instances of this class are created by 
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>SecurityManagementRequest</code> and contain no data.
 * They just serve to indicate that security management operation is finished (either
 * successfully or unsuccessfully).
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class SecurityManagementAck extends ContestManagementAck {

    /**
     * Constructs new instance of SecurityManagementAck. Such instance indicates
     * about successful fulfilment of request.
     */
    public SecurityManagementAck() {
        super();
    }

    /**
     * Constructs new instance of SecurityManagementAck specifying the
     * Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of 
     *        request
     */
    public SecurityManagementAck(Throwable errorDetails) {
        super(errorDetails);
    }
}
