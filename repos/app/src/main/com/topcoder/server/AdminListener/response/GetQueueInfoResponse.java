/*
 * GetQueueInfoResponse.java
 *
 * Created on April 6, 2005, 2:07 PM
 */

package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 *
 * @author rfairfax
 */
public class GetQueueInfoResponse extends ContestManagementAck {
    
    private String response;
    
    public GetQueueInfoResponse() {
        super();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(response);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        response = reader.readString();
    }
    
    public GetQueueInfoResponse(String response) {
        super();
        this.response = response;
    }
    
    public GetQueueInfoResponse(Throwable errorDetails) {
        super( errorDetails);
    }
    
    public String getResponse() {
        return response;
    }
}
