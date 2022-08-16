/*
 * InvocationContextImpl
 * 
 * Created 10/09/2006
 */
package com.topcoder.farm.processor;

import java.io.File;

import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationFeedbackPublisher;

/**
 * @author Diego Belfer (dbelfer)
 * @version $Id$
 */
public class InvocationContextImpl implements InvocationContext {
    private File rootFolder;
    private File workFolder;
    private boolean mustShutdownVM;
    private boolean permanentThreadsCreated;
    private InvocationFeedbackPublisher feedbackPublisher;


    public InvocationContextImpl() {
    }
    
    public InvocationContextImpl(File rootFolder, File workFolder,  InvocationFeedbackPublisher feedbackPublisher) {
        this.rootFolder = rootFolder;
        this.workFolder = workFolder;
        this.feedbackPublisher = feedbackPublisher;
    }


    public File getRootFolder() {
        return rootFolder;
    }

    public File getWorkFolder() {
        return workFolder;
    }
    
    public void setMustShutdownVM(boolean shutdown) {
        this.mustShutdownVM = shutdown;
    }
    
    public boolean isMustShutdownVM() {
        return mustShutdownVM;
    }
    
    public InvocationFeedbackPublisher getFeedbackPublisher() {
        return feedbackPublisher;
    }

    public boolean isPermanentThreadsCreated() {
        return permanentThreadsCreated;
    }

    public void setPermanentThreadsCreated(boolean daemonThreadsCreated) {
        this.permanentThreadsCreated = daemonThreadsCreated;
    }

}
