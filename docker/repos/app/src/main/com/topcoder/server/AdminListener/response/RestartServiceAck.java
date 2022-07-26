package com.topcoder.server.AdminListener.response;

import java.io.Serializable;

/**
 * A response to request to restart some service (compilers or testers).
 * Instances of this class are created by 
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>RestartServiceRequest</code> and contain no data.
 * They just serve to indicate that specified service(s) was restarted.
 *
 * @author  TCSDESIGNER
 * @author  TCSDEVELOPER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class RestartServiceAck extends ContestManagementAck {

    /**
     * Constructs new instance of RestartServiceAck. Such instance indicate
     * about successful fulfilment of request.
     */
    public RestartServiceAck() {
        super();
    }

    /**
     * Constructs new instance of RestartServiceAck specifying the
     * Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of 
     *        request
     */
    public RestartServiceAck(Throwable errorDetails) {
        super(errorDetails);
    }
}
