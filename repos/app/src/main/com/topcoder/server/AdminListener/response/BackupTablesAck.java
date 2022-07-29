package com.topcoder.server.AdminListener.response;

import java.io.Serializable;

/**
 * A response to request to create backup copies of some tables
 * Instances of this class are created by 
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>BackupTablesRequest</code> and contain no data.
 * They just serve to indicate that backup process is finished (either
 * successfully or unsuccessfully).
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class BackupTablesAck extends ContestManagementAck 
    implements Serializable {

    /**
     * Constructs new instance of BackupTablesAck as result of successful
     * fulfilment of request.
     */
    public BackupTablesAck() {
        super();
    }

    /**
     * Constructs new instance of BackupTablesAck specifying the
     * Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of 
     *        request
     */
    public BackupTablesAck(Throwable errorDetails) {
        super(errorDetails);
    }
}
