/*
 * ProcessorInvocationRunner
 *
 * Created 07/05/2006
 */
package com.topcoder.farm.processor;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.processor.api.ProcessorInvocationHandler;
import com.topcoder.farm.processor.api.ProcessorInvocationHandlerException;
import com.topcoder.farm.shared.invocation.ExceptionData;
import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationFeedbackPublisher;
import com.topcoder.farm.shared.invocation.InvocationResult;


/**
 * ProcessorInvocationHandler implementation.<p>
 *
 * This implementation runs invocation in a new thread, using a thread group for monitoring
 * that all threads created by the invocation are down after the invocation execution ends.<p>
 * Generates a temporary folder inside the workFolder, that the invocations can use as temporary folder.
 *
 * The folder is named <i>task-{threadid}</i> where <i>{threadid}</i> is replaced by the thread id of
 * the calling thread.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorInvocationRunner implements ProcessorInvocationHandler {
    private static final String MAX_TASK_TIME_KEY = "com.topcoder.farm.processor.ProcessorInvocationRunner.maxTaskTime";
    private static final int MAX_TASK_TIME_DEFAULT = 10*60*1000;
    private Log log = LogFactory.getLog(ProcessorInvocationRunner.class);
    private int maxTaskTime = 0;
    private File workFolder;
    private File rootFolder;

    /**
     * Creates a new ProcessorInvocationRunner that will use the given folders as
     * for handling invocations.
     *
     * @param rootFolder The root folder where the processor is running
     * @param workFolder The work folder defined for the processor
     */
    public ProcessorInvocationRunner(File rootFolder, File workFolder) {
        this.rootFolder = rootFolder;
        this.workFolder = workFolder;
    }

    private InvocationResult run(InvocationFeedbackPublisher feedbackPublisher, final Invocation invocation) throws ProcessorInvocationHandlerException {
        final InvocationResult[] resultHolder = new InvocationResult[1];
        long threadId = Thread.currentThread().getId();
        final File taskWorkFolder = intializeTaskWorkingFolder(threadId);

        final InvocationContextImpl context = new InvocationContextImpl(rootFolder, taskWorkFolder, feedbackPublisher);
        ThreadGroup group = new ThreadGroup("TG-task-"+threadId);
        Thread mainThread = new Thread(group, new Runnable() {
            public void run() {
                try {
                    Object result = invocation.run(context);
                    resultHolder[0] = buildSuccessResult(result);
                } catch (Throwable e) {
                    log.error("Exception thrown", e);
                    resultHolder[0] = buildExceptionResult(e);
                }
            }
        }, Thread.currentThread().getName()+"-"+"Main");
        mainThread.start();

        try {
            try {
                mainThread.join(getMaxTaskTime());
            } finally {
                if (group.activeCount() > 0) {
                    if (!context.isPermanentThreadsCreated()) {
                        log.warn("Task ("+invocation.getClass().getName()+") has created threads and they are active ="+
                                 group.activeCount()+". Trying to interrupt them. MainThread active="+mainThread.isAlive());
                        try {
                            group.interrupt();
                            int tries = 0;
                            while (group.activeCount() > 0 && tries < 15) {
                                Thread.sleep(100);
                                tries++;
                            }
                            group.destroy();
                        } catch (IllegalThreadStateException e) {
                            throw new ProcessorInvocationHandlerException("Threads created by task could not be stopped. Forcing restart", e, resultHolder[0]);
                        }
                    } else {
                        log.info("Task ("+invocation.getClass().getName()+") has created threads and it indicated they are permanent threads");
                    }
                }
            }
        } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
        }
        deleteWorkFolder(taskWorkFolder);
        if (context.isMustShutdownVM()) {
            throw new ProcessorInvocationHandlerException("Invocation required to restart the VM process. Forcing restart.", resultHolder[0]);
        }
        return resultHolder[0];
    }

    private File intializeTaskWorkingFolder(long threadId) {
        File folder = getTaskWorkingFolder(threadId);
        deleteWorkFolder(folder);
        return folder;
    }

    private void deleteWorkFolder(File folder) {
        try {
            FileUtils.deleteDirectory(folder);
        } catch (Exception e) {
            log.warn("Task folder couldn't be deleted... ignoring exception: "+ e.getMessage() + ")");
        }
    }

    private File getTaskWorkingFolder(long threadId) {
        return new File(workFolder, "task-"+threadId);
    }

    private InvocationResult buildSuccessResult(Object result) {
        InvocationResult r = new InvocationResult();
        r.setExceptionThrown(false);
        r.setReturnValue(result);
        return r;
    }

    private InvocationResult buildExceptionResult(Throwable e) {
        InvocationResult r = new InvocationResult();
        r.setExceptionThrown(true);
        r.setExceptionData(new ExceptionData(e));
        return r;
    }


    /**
     * @see com.topcoder.farm.processor.api.ProcessorInvocationHandler#handle(com.topcoder.farm.shared.invocation.Invocation)
     */
    @Override
    public InvocationResult handle(InvocationFeedbackPublisher feedbackPublisher, Invocation invocation) throws ProcessorInvocationHandlerException {
        return run(feedbackPublisher, invocation);
    }

    public int getMaxTaskTime() {
        if (maxTaskTime == 0) {
            try {
                maxTaskTime = Integer.parseInt(System.getProperty(MAX_TASK_TIME_KEY, "0"));
            } catch (Exception e) {
            }
            if (maxTaskTime <= 0) {
                maxTaskTime = MAX_TASK_TIME_DEFAULT;
            }
        }
        return maxTaskTime;
    }
}
