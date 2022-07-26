package com.topcoder.server.AdminListener.request;
import java.util.Iterator;
import java.util.Set;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * This is a request to be sent to Admin Listener server to perform some
 * warehouse data load process specified by TCLoad class and parameters
 * contained within this request.
 *
 * @author  Giorgos Zervas
 * @version 1.0 11/21/2003
 * @since   Admin Tool 2.0
 */
public class WarehouseLoadRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    /**
     * A Hashtable mapping the String parameter names to String parameter 
     * values
     */
    private Map params = null;

    /** 
     * An ID of warehouse load request that should be equal to value of one of
     * <code>AdminConstants.REQUEST_WAREHOUSE_LOAD_*</code> constants.
     *
     * @see AdminConstants#REQUEST_WAREHOUSE_LOAD_AGGREGATE
     * @see AdminConstants#REQUEST_WAREHOUSE_LOAD_CODER
     * @see AdminConstants#REQUEST_WAREHOUSE_LOAD_EMPTY
     * @see AdminConstants#REQUEST_WAREHOUSE_LOAD_RANK
     * @see AdminConstants#REQUEST_WAREHOUSE_LOAD_REQUESTS
     * @see AdminConstants#REQUEST_WAREHOUSE_LOAD_ROUND
     */
    private int requestID = 0;
    
    public WarehouseLoadRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(requestID);
        writer.writeHashMap(new HashMap(params));
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        requestID = reader.readInt();
        params = reader.readHashMap();
    }

    /**
     * Constructs new WarehouseLoadRequest with specified TCLoad extending
     * class name and Hashtable containing the parameters and request ID.
     * Specified request ID will be used to check the permission of requestor
     * to perform such action.
     *
     * @param  requestID an ID of warehouse load request that should be equal
     *         value of on the <code>AdminConstants.REQUEST_WAREHOUSE_LOAD_*
     *         </code> constants.
     * @param  params a Hashtable mapping parameter names to parameter values
     * @throws IllegalArgumentException if any of given parameters is null or
     *         tcLoadClass is empty
     */
    public WarehouseLoadRequest(int requestID, Map params) {
        super();
        if (!
                (requestID == AdminConstants.REQUEST_WAREHOUSE_LOAD_AGGREGATE
                || requestID == AdminConstants.REQUEST_WAREHOUSE_LOAD_CODER
                || requestID == AdminConstants.REQUEST_WAREHOUSE_LOAD_EMPTY
                || requestID == AdminConstants.REQUEST_WAREHOUSE_LOAD_RANK
                || requestID == AdminConstants.REQUEST_WAREHOUSE_LOAD_REQUESTS
                || requestID == AdminConstants.REQUEST_WAREHOUSE_LOAD_ROUND)
        ) {
            throw new IllegalArgumentException("invalid requestID: " + requestID);
        }

        if (params == null) {
            throw new IllegalArgumentException("null params for new WarehouseLoadRequest");
        }

        Set keys = params.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            Object key = i.next();
            if (key == null) {
                throw new IllegalArgumentException("Null key in params for new WarehouseLoadRequest");
            } else {
                Object value = params.get(key);
                if (value == null) {
                    throw new IllegalArgumentException("Null value in params for new WarehouseLoadRequest for key " + key);
                }
            }

        }
        this.requestID = requestID;
        this.params = params;
    }

    /**
     * Gets the parameters that should be passed to TCLoad class to configure
     * warehouse load process.
     *
     * @return a Hashtable mapping String parameter names to String parameter
     *         values
     */
    public Map getParams() {
        return params;
    }

    /**
     * Gets the ID of warehouse load request that should be used to check the
     * permission of requestor to perform such action.
     *
     * @return an ID of warehouse load request 
     */
    public int getRequestID() {
        return requestID;
    }
}
