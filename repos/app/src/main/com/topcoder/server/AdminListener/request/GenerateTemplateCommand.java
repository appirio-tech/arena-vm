/*
 * GenerateTemplateCommand.java
 *
 * Created on October 24, 2005, 10:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 *
 * @author rfairfax
 */
public class GenerateTemplateCommand  extends ContestMonitorRequest implements ProcessedAtBackEndRequest {
    
    private int roundID = 0;
    
    public GenerateTemplateCommand() {
    }
    
    public int getRoundID() {
        return roundID;
    }
    
    public GenerateTemplateCommand(int roundID) {
        this.roundID = roundID;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readInt();
    }
    
}