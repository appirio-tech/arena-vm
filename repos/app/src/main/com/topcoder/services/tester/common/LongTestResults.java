/*
* Copyright (C) 2005-2014 TopCoder Inc., All Rights Reserved.
*/

/*
 * LongTestResults.java
 *
 * Created on November 15, 2005, 11:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.services.tester.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 *
 * <p>
 * Changes in version 1.1 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Add {@link #peakMemoryUsed} field.</li>
 *      <li>Add {@link #getPeakMemoryUsed()} method.</li>
 *      <li>Add {@link #setPeakMemoryUsed(long peakMemoryUsed)} method.</li>
 *      <li>Update {@link #customReadObject(CSReader reader)} method.</li>
 *      <li>Update {@link #customWriteObject(CSWriter writer)} method.</li>
 * </ol>
 * </p>
 * @author rfairfax, TCSASSEMBLER
 * @version 1.1
 */
public class LongTestResults implements Serializable, CustomSerializable {
    
    private boolean success = false;
    private String message = null;
    private String stdout = null;
    private String stderr = null;
    private long time = 0;
    private double score = 0.0;
    private Object resultObject;
    /**
     * The peak memory used.
     * @since 1.1
     */
    private long peakMemoryUsed;
    
    /** Creates a new instance of LongTestResults */
    public LongTestResults() {
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long l) {
        this.time = l;
    }
    /**
     * Getter the peak memory used.
     * @return the peak memory used.
     */
    public long getPeakMemoryUsed() {
        return peakMemoryUsed;
    }
    /**
     * Setter the peak memory used.
     * @param peakMemoryUsed the peak memory used.
     */
    public void setPeakMemoryUsed(long peakMemoryUsed) {
        this.peakMemoryUsed = peakMemoryUsed;
    }
    
    public String getStdout() {
        return stdout;
    }
    
    public void setStdout(String s) {
        this.stdout = s;
    }
    
    public String getStderr() {
        return stderr;
    }
    
    public void setStderr(String s) {
        this.stderr = s;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double d) {
        score = d;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String m) {
        message = m;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean s) {
        success = s;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }
    
    public String toString() {
        return "success="+success+", message=\""+message+"\"";
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        success = reader.readBoolean(); 
        message = reader.readString(); 
        stdout =  reader.readString();
        stderr =  reader.readString();
        time =  reader.readLong();
        score =  reader.readDouble();
        peakMemoryUsed = reader.readLong();
        resultObject = reader.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(success) ;
        writer.writeString(message); 
        writer.writeString(stdout);
        writer.writeString(stderr);
        writer.writeLong(time);
        writer.writeDouble(score);
        writer.writeLong(peakMemoryUsed);
        writer.writeObject(resultObject);
    }

    
}
