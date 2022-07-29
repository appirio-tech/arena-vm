/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:07:44 AM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.*;

public class SetComponentsRequest extends ContestManagementRequest {

    private int id;
    private Collection components;
    
    public SetComponentsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeObjectArray(components.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        components = Arrays.asList(reader.readObjectArray());
    }

    public SetComponentsRequest(int id, Collection components) {
        this.id = id;
        this.components = components;
    }

    public int getRoundID() {
        return id;
    }

    public Collection getComponents() {
        return components;
    }
}
