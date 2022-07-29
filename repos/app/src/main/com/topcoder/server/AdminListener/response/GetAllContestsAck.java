/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 10, 2002
 * Time: 2:09:45 AM
 */
package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.*;

public class GetAllContestsAck extends ContestManagementAck {

    private Collection contests;
    
    public GetAllContestsAck() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(contests.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        contests = Arrays.asList(reader.readObjectArray());
    }

    public GetAllContestsAck(Throwable exception) {
        super(exception);
    }

    public GetAllContestsAck(Collection contests) {
        super();
        if (contests != null)
            this.contests = contests;
    }

    public Collection getContests() {
        return contests;
    }
}
