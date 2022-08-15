package com.topcoder.server.AdminListener.response;
import com.topcoder.security.policy.GenericPermission;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class RefreshAccessResponse implements CustomSerializable, Serializable {

    private boolean succeeded;
    private Set allowedFunctions;
    
    public RefreshAccessResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(succeeded);
        writer.writeInt(allowedFunctions.size());
        for(Iterator i = allowedFunctions.iterator(); i.hasNext();) {
            GenericPermission gp = (GenericPermission)i.next();
            writer.writeString(gp.getName());
        }  
    }

    public void customReadObject(CSReader reader) throws IOException {
        succeeded = reader.readBoolean();
        int sz = reader.readInt();
        allowedFunctions = new HashSet();
        for(int i = 0; i < sz; i++) {
            String h = reader.readString();
            allowedFunctions.add(new GenericPermission(h));
        }
    }

    public RefreshAccessResponse(boolean succeeded, Set allowedFunctions) {
        this.succeeded = succeeded;
        this.allowedFunctions = allowedFunctions;
    }

    public boolean getSucceeded() {
        return succeeded;
    }

    public Set getAllowedFunctions() {
        return allowedFunctions;
    }
}
