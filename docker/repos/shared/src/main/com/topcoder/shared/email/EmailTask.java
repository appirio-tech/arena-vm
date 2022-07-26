package com.topcoder.shared.email;

import com.topcoder.shared.ejb.EmailServices.EmailServer;
import com.topcoder.shared.ejb.EmailServices.EmailServerHome;
import com.topcoder.shared.util.logging.Logger;

import javax.naming.Context;
import java.rmi.RemoteException;

/**
 * The EmailTask is a base class responsible for coordinating the
 * running of tasks with the scheduler.
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 *
 */
abstract class EmailTask implements Runnable {

    private static Logger log = Logger.getLogger(EmailTask.class);

    protected int jobId;
    protected long controlId;
    protected Context ctx;
    protected EmailJobScheduler scheduler;

    /**
     * Creates a new object.  The object will be able to send a set of emails
     * when the run method is called (probably from a new thread).
     * @param ctx
     * @param scheduler
     * @param jobId
     * @param controlId
     */
    public EmailTask(Context ctx, EmailJobScheduler scheduler, int jobId, long controlId) {
        this.ctx = ctx;
        this.scheduler = scheduler;
        this.jobId = jobId;
        this.controlId = controlId;
    }

    /**
     * This function does creates a thread to keep the server updated that this
     * task is still running.
     */
    public void run() {

        keepAlive kaThread = null;

        try {
            EmailServer server = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();

            // verify that the job is still scheduled for this instance...
            if (!verifyJob(server)) {
                return;
            }

            kaThread = new keepAlive(this, server);
            kaThread.start();
            doWork();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (kaThread != null) {
                kaThread.finish();
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    abstract void doWork();

    /*
     * verifyJob checks the database to see if the task is still
     * assigned to this scheduler, and if so, it may update the
     * database to indicate that the task is still being worked on.
     *
     * The method is synchronized as it will be called from the
     * keepAlive thread and may also be called from the task itself
     * (if the task would like to check if it has been reassigned).
     */
    public synchronized boolean verifyJob(EmailServer server)
            throws RemoteException {
        long newControlId = scheduler.getSchedulerId();
        if (newControlId == controlId) {
            if (controlId != server.getJobControlId(jobId))
                return false;
        } else {
            if (!server.acquireJob(jobId, newControlId, controlId))
                return false;
            controlId = newControlId;
        }
        return true;
    }

    /*
     * The keepAlive thread calls the verifyJob function to keep the database
     * up to date that this task is still working on the job.
     * It is necessary to do this when running (potentially) long running
     * queries that may not return soon enough to keep the job from being
     * rescheduled. Without it, a long running query may appear to other
     * schedulers as an abandoned task.
     */
    private static class keepAlive extends Thread {
        private EmailTask task;
        private EmailServer server;
        private boolean keepgoing;

        keepAlive(EmailTask task, EmailServer server) {
            this.task = task;
            this.server = server;
            setDaemon(true);
        }

        public void run() {
            keepgoing = true;
            for (; keepgoing;) {
                try {
                    sleep(1000);
                    if (!task.verifyJob(server)) return;
                } catch (Exception e) {
                    return;
                }
            }
        }

        public void finish() {
            // kill the thread and cycle until it exits (or until it is
            // obvious that it isn't going to exit).
            for (int i = 0; i < 100 && isAlive(); i++) {
                keepgoing = false;
                interrupt();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}

