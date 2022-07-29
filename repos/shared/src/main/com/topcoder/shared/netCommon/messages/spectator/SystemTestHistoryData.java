/*
 * SystemTestHistoryData.java
 *
 * Created on October 17, 2006, 7:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Date;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 *
 * @author rfairfax
 */
public class SystemTestHistoryData extends RoomMessage {
    
    private int problemID;
    private String coder;
    private Date timestamp;
    private int deductAmt;
    private String problemVal;
    private String args;
    private String results;
    private boolean succeeded;
    
    /** Creates a new instance of SystemTestHistoryData */
    public SystemTestHistoryData() {
    }
    
    public SystemTestHistoryData(RoomData room, int problemID, String coder, Date timestamp, int deductAmt, String problemVal,
                String args, String results, boolean succeeded) {
        super(room);
        this.problemID = problemID;
        this.coder = coder;
        this.timestamp = timestamp;
        this.deductAmt = deductAmt;
        this.problemVal = problemVal;
        this.args = args;
        this.results = results;
        this.succeeded = succeeded;
    }
    
    public void setProblemID(int problemID) {
        this.problemID = problemID;
    }
    
    public int getProblemID() {
        return problemID;
    }
    
    public void setCoder(String coder) {
        this.coder = coder;
    }
    
    public String getCoder() {
        return coder;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public int getDeductAmt() {
        return deductAmt;
    }
    
    public String getProblemVal() {
        return problemVal;
    }
    
    public String getArgs() {
        return args;
    }
    
    public String getResults() {
        return results;
    }
    
    public boolean isSucceeded() {
        return succeeded;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(problemID);
        writer.writeString(coder);
        writer.writeLong(timestamp.getTime());
        writer.writeInt(deductAmt);
        writer.writeString(problemVal);
        writer.writeString(args);
        writer.writeString(results);
        writer.writeBoolean(succeeded);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        problemID = reader.readInt();
        coder = reader.readString();
        timestamp = new Date(reader.readLong());
        deductAmt = reader.readInt();
        problemVal = reader.readString();
        args = reader.readString();
        results = reader.readString();
        succeeded = reader.readBoolean();
    }
    
}
