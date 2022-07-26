/*
 * @author John Waymouth
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.*;

public class GetRoundProblemComponentsAck extends ContestManagementAck {

    private Collection components;
    
    public GetRoundProblemComponentsAck() {
        super();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(components.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        components = Arrays.asList(reader.readObjectArray());
    }

    public GetRoundProblemComponentsAck(Throwable exception) {
        super(exception);
    }

    public GetRoundProblemComponentsAck(Collection components) {
        super();
        this.components = components;
    }

    public Collection getComponents() {
        return components;
    }

    public String toString() {
        return "GetRoundProblemComponentsAck [components=" + components + "]";
    }

}
