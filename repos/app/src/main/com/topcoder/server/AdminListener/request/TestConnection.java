package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

// A dummy class, an object of which is sent by a monitor client to the admin listener
// server upon connection to ensure that data sending is operating properly.

public class TestConnection implements CustomSerializable {
    public TestConnection() {
    }
    
    public void customWriteObject(CSWriter writer) {
        
    }
    
    public void customReadObject(CSReader reader) {
        
    }
}
