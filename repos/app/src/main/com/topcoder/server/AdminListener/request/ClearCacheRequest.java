/*
 * ClearCacheRequest.java
 *
 * Created on July 13, 2005, 4:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 *
 * @author rfairfax
 */
public class ClearCacheRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {
    
    /** Creates a new instance of ClearCacheRequest */
    public ClearCacheRequest() {
    }
    
    public void customWriteObject(CSWriter writer) {
        
    }
    
    public void customReadObject(CSReader reader) {
        
    }
    
}
