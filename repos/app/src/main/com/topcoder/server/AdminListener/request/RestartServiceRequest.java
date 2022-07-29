package com.topcoder.server.AdminListener.request;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 * This is a request that should be sent to Admin Listener server by Admin
 * Tool client in order to restart compilers and testers. Contains an <code>
 * int</code> type representing the concrete type of request, for example :
 * restart testers or compilers only or restart all.
 *
 * @author  TCSDESIGNER
 * @version 1.0  07/31/2003
 * @since Admin Tool 2.0
 */
public class RestartServiceRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    /**
     * Type of request. Specifies the concrete action that should be performed.
     * Contains a value that should be equal to value of one of <code>
     * AdminConstants.REQUEST_RESTART_*</code> constants.
     */
    private int requestType = 0;
    
    private int restartMode = 1;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(requestType);
        writer.writeInt(restartMode);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        requestType = reader.readInt();
        restartMode = reader.readInt();
    }
    
    public RestartServiceRequest() {
        
    }

    /**
     * Constructs new RestartServiceRequest with specified type. Given type 
     * should be one of <code>AdminConstants.REQUEST_RESTART_*</code> 
     * constants.
     *
     * @param  requestType a concrete type of request to restart some service
     * @throws IllegalArgumentException if given parameter contains value that
     *         is not equal to any of <code>AdminConstants.REQUEST_RESTART_*
     *         </code> constants.
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_COMPILERS
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_TESTERS
     * @see    com.topcoder.server.AdminListener.AdminConstants#REQUEST_RESTART_ALL
     */
    public RestartServiceRequest(int requestType, int restartMode) {
        if (requestType != AdminConstants.REQUEST_RESTART_ALL
        && requestType != AdminConstants.REQUEST_RESTART_COMPILERS
        & requestType != AdminConstants.REQUEST_RESTART_TESTERS) {
            throw new IllegalArgumentException();
        }
        this.requestType = requestType;
        this.restartMode = restartMode;
    }

    /**
     * Gets the concrete type of request. Returned value is one of <code>
     * AdminConstants.REQUEST_RESTART_*</code> constants.
     * 
     * @return a type of request to restart the service
     * @see    AdminConstants#REQUEST_RESTART_COMPILERS
     * @see    AdminConstants#REQUEST_RESTART_TESTERS
     * @see    AdminConstants#REQUEST_RESTART_ALL
     */
    public int getRequestType() {
        return requestType;
    }
    
    public int getRestartMode() {
        return restartMode;
    }
}