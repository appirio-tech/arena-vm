/*
 * DirectExecutor
 * 
 * Created Oct 9, 2007
 */
package com.topcoder.shared.util.concurrent;

import java.util.concurrent.Executor;

/**
 * Direct executor implementation.<p>
 * 
 * Executes the runnable in the calling thread.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DirectExecutor implements Executor {
    /**
     * Executes the runnable synchronously in the calling thread.
     *
     * @throws NullPointerException if <code>command</code> is <code>null</code>.
     */
    public void execute(Runnable command) {
       command.run();
    }
}
