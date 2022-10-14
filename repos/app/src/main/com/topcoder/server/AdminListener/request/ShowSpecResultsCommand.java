/*
 * ShowSpecResultsCommand.java
 *
 * Created on October 18, 2006, 6:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.io.ObjectStreamException;

/**
 *
 * @author rfairfax
 */
public class ShowSpecResultsCommand  extends ContestMonitorRequest {
    
    /** Creates a new instance of ShowSpecResultsCommand */
    public ShowSpecResultsCommand() {
    }    
    
    //no params

    public void customWriteObject(CSWriter writer) throws IOException {
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
    }
}
