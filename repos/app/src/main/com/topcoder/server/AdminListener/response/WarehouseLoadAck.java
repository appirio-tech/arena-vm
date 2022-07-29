package com.topcoder.server.AdminListener.response;

import java.io.Serializable;

/**
 * A response to request to perform warehouse load process.
 * Instances of this class are created by 
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>WarehouseLoadRequest</code> and contain no data.
 * They just serve to indicate that warehouse load process is finished (either
 * successfully or unsuccessfully).
 *
 * @author  Giorgos Zervas
 * @version 1.0 11/21/2003
 * @since   Admin Tool 2.0
 */
public class WarehouseLoadAck extends ContestManagementAck {

    /**
     * Constructs new instance of WarehouseLoadAck. Such instance indicates
     * about successful fulfilment of request.
     */
    public WarehouseLoadAck() {
        super();
    }

    /**
     * Constructs new instance of WarehouseLoadAck specifying the
     * Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of 
     *        request
     */
    public WarehouseLoadAck(Throwable errorDetails) {
        super(errorDetails);
    }
}
