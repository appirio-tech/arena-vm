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


public class ChangeRoundResponse implements CustomSerializable, Serializable {

    private boolean succeeded;
    private int roundId;
    private String roundName;
    private String message;
    private Set allowedFunctions;
    
    public ChangeRoundResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(succeeded);
        writer.writeInt(roundId);
        writer.writeString(roundName);
        writer.writeString(message);
        writer.writeInt(allowedFunctions.size());
        for(Iterator i = allowedFunctions.iterator(); i.hasNext();) {
            GenericPermission gp = (GenericPermission)i.next();
            writer.writeString(gp.getName());
        }   
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        succeeded = reader.readBoolean();
        roundId = reader.readInt();
        roundName = reader.readString();
        message = reader.readString();
        
        int sz = reader.readInt();
        allowedFunctions = new HashSet();
        for(int i = 0; i < sz; i++) {
            String h = reader.readString();
            allowedFunctions.add(new GenericPermission(h));
        }
    }

    public ChangeRoundResponse(boolean succeeded, int roundId, String roundName, String message, Set allowedFunctions) {
        this.succeeded = succeeded;
        this.roundId = roundId;
        this.roundName = roundName;
        this.message = message;
        this.allowedFunctions = allowedFunctions;
    }

    public boolean getSucceeded() {
        return succeeded;
    }

    public int getRoundId() {
        return roundId;
    }

    public String getRoundName() {
        return roundName;
    }

    public String getMessage() {
        return message;
    }

    public Set getAllowedFunctions() {
        return allowedFunctions;
    }
}
