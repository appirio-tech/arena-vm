/*
 * InvocationContext
 * 
 * Created 10/9/2006
 */
package com.topcoder.farm.shared.invocation;

import java.io.File;

/**
 * @author Diego Belfer (dbelfer)
 * @version $Id$
 */
public interface InvocationContext {
    public File getWorkFolder();
    public File getRootFolder();
    public void setMustShutdownVM(boolean shutdown);
    public InvocationFeedbackPublisher getFeedbackPublisher();
    void setPermanentThreadsCreated(boolean daemonThreadsCreated);
}
