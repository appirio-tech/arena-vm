package com.topcoder.server.AdminListener.response;

import java.io.Serializable;

/**
 * A response to request to restore specified tables from specified backup copy.
 * Instances of this class are created by 
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>RestoreTablesRequest</code> and contain no data.
 * They just serve to indicate that restore process is finished (either
 * successfully or unsuccessfully).
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class RestoreTablesAck extends ContestManagementAck {

    /**
     * Constructs new instance of RestoreTablesAck as result of successful
     * fulfilment of request.
     */
    public RestoreTablesAck() {
        super();
    }

    /**
     * Constructs new instance of RestoreTablesAck specifying the
     * Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of 
     *        request
     */
    public RestoreTablesAck(Throwable errorDetails) {
        super(errorDetails);
    }
}
