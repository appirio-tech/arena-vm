/*
 * GetQueueInfoRequest.java
 *
 * Created on April 6, 2005, 1:48 PM
 */

package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class GetQueueInfoRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    private String queueName;
    
    public GetQueueInfoRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(queueName);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        queueName = reader.readString();
    }

    public GetQueueInfoRequest(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }
}
