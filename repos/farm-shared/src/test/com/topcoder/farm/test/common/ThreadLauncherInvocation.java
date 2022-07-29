/*
 * TestInvocation
 * 
 * Created 09/28/2006
 */
package com.topcoder.farm.test.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$$
 */
public class ThreadLauncherInvocation implements Invocation {
    private static Log log = LogFactory.getLog(ThreadLauncherInvocation.class);
    private String invocationId;
    private String clientId;
    private String threadName ;
    private int threadToLaunch;
    private int threadsToStop;
    private int threadsToInterrupt;
    private int mainThreadWait;

    public ThreadLauncherInvocation() {
    }
    
    public ThreadLauncherInvocation(String invocationId , String clientId, int mainThreadWait, int threadToLaunch, int threadsToStop, int threadsToInterrupt) {
        this.invocationId = invocationId;
        this.clientId = clientId;
        this.threadName = Thread.currentThread().getName();
        this.threadToLaunch = threadToLaunch;
        this.threadsToStop = threadsToStop;
        this.threadsToInterrupt = threadsToInterrupt;
        this.mainThreadWait = mainThreadWait;
    }
    
    public Object run(InvocationContext context) {
        Thread[] threads = new Thread[threadToLaunch];
        for (int i = 0; i < threads.length; i++) {
            final int thId = i;
            threads[i] = new Thread("TH-"+i) {
                public void run() {
                    log.info("Starting thread "+getName());
                    while(true) {
                        try {
                            Thread.sleep(200);
                            if (thId < threadsToStop) break;
                        } catch (Exception e) {
                            if (thId < (threadsToStop+threadsToInterrupt)) break;
                            log.info("Ignoring interrupt request for thread "+getName());
                        }
                    }
                    log.info("Exiting thread "+getName());
                }
            };
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        try {
            Thread.sleep(mainThreadWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }
    
    public String toString() {
        return "[invocationId=" + invocationId + ", clientId=" + clientId
                + ", threadIndex=" + threadName + ", launch=" + threadToLaunch  
                + ", stop=" + threadsToStop + ",interrupt=" + threadsToInterrupt
                + "]";
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getThreadsToStop() {
        return threadsToStop;
    }

    public void setThreadsToStop(int threadsToStop) {
        this.threadsToStop = threadsToStop;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getThreadToLaunch() {
        return threadToLaunch;
    }

    public void setThreadToLaunch(int timeToWait) {
        this.threadToLaunch = timeToWait;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.invocationId = reader.readString();
        this.clientId = reader.readString();
        this.threadName = reader.readString();
        this.mainThreadWait = reader.readInt();
        this.threadsToInterrupt = reader.readInt();
        this.threadsToStop = reader.readInt();
        this.threadToLaunch = reader.readInt();
        
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(this.invocationId);
        writer.writeString(this.clientId);
        writer.writeString(this.threadName);
        writer.writeInt(this.mainThreadWait);
        writer.writeInt(this.threadsToInterrupt);
        writer.writeInt(this.threadsToStop);
        writer.writeInt(this.threadToLaunch);
    }
}
