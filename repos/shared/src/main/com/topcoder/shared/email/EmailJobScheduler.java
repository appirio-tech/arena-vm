package com.topcoder.shared.email;

import com.topcoder.shared.ejb.EmailServices.*;
import com.topcoder.shared.util.StageQueue;
import com.topcoder.shared.util.logging.Logger;

import javax.naming.*;
import java.util.*;

/**
 * The EmailJobScheduler is responsible for periodically checking
 * the database for email jobs and running them when found.
 *
 * The EmailJobScheduler can be started from the command line and
 * remains resident until it is requested that it stop.
 *
 * Email jobs are run in separate threads from the scheduler, so
 * multiple jobs may be processed in parallel.
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 */
public class EmailJobScheduler {

    // the following are configuration variables that will be read out of the email.properties file
    private boolean configRead = false;
    private String contextFactory = "org.jnp.interfaces.NamingContextFactory";
    private String contextProvider = "t3://172.16.20.140:8020";
    private int pollingInterval_msec = 60000;   // how often to recheck the database for new work
    private int maxWorkerThreads = 10;          // how many different tasks to run simultaneously
    private int maxEmailsPerSec = 100;          // throttle limiter on how many emails to push send in one second
    private int jobTimeout_msec = 300000;       // minimum amount of time to wait before taking over a previously scheduled task

    // the following are state variables that keep track of the current state of the scheduler
    private long schedulerId = 0;                // used to mark tasks as being worked on, read from the database
    private int timeoutFrac = 1;
    private final int HISTORY_SIZE = 10;
    private long history[] = new long[HISTORY_SIZE];
    private boolean stopRequested = false;
    private Thread waitThread = null;

    private static Logger log = Logger.getLogger(EmailJobScheduler.class);

    /**
     * Main startup. Creates a scheduler object and starts it running.
     * @param args
     */
    public static void main(String[] args) {
        EmailJobScheduler scheduler = new EmailJobScheduler();
        scheduler.runScheduler();
    }

    /**
     * This function is the main scheduler loop.
     * Cycles repeatedly, reading the profile,
     * checking the schedule, and waiting for the next cycle.
     */
    public void runScheduler() {
        /**
         * Read the profile (which activates startup and initializes any
         * scheduler resources that will be used throughout the life-cycle
         * of the scheduler.
         */
        readProfile();

        /**
         * Until the stopRequested flag is set, loop continuouly
         * reading the profile, check the schedule, and then wait
         * for the next cycle.
         */
        for (; !stopRequested;) {
            checkSchedule();
            pause();
            readProfile();
        }

        /**
         * Last chance to cleanup before exiting.
         */
        shutdown();
    }

    /**
     * This function is used to stop the scheduler.
     * It sets the stopRequested flag and interrupts the
     * pause function so the stopRequest can be processed.
     */
    public void stopScheduler() {
        stopRequested = true;
        waitThread.interrupt();
    }

    /**
     * This function pauses for the configured polling interval.
     */
    public void pause() {
        try {
            waitThread.sleep(pollingInterval_msec);
        } catch (Exception ignore) {
        }
    }

    /**
     * This funtion initialize resources that are used
     * throughout the life-cycle of the scheduler.
     */
    public void startup() {
        stopRequested = false;
        waitThread = Thread.currentThread();
        StageQueue.start(maxWorkerThreads);
        Arrays.fill(history, 0);
        timeoutFrac = 0;
        log.info("Started EmailJobScheduler");
    }

    /**
     * This function read the profile from the configuration
     * file and stores the results in static variables.
     * If the profile has changed, the function also detects
     * and handles the change.
     */
    public void readProfile() {
        ResourceBundle resource = null;
        try {
            resource = ResourceBundle.getBundle("Email");
        } catch (Exception e) {
            log.warn("Failed to find the Email resource file: " + e.getMessage());
        }
        String newContextFactory = readConfig(resource, "context_factory",
                contextFactory);
        String newContextProvider = readConfig(resource, "context_provider",
                contextProvider);
        int newPollingInterval = readConfig(resource, "polling_interval_msec",
                pollingInterval_msec);
        int newWorkerThreads = readConfig(resource, "max_worker_threads",
                maxWorkerThreads);
        int newMaxEmailsPerSec = readConfig(resource,
                "max_emails_per_second_per_job",
                maxEmailsPerSec);
        int newJobTimeout_msec = readConfig(resource, "job_timeout_msec",
                jobTimeout_msec);

        // force values into a resonable range or warn for values that don't seem right.
        if (newPollingInterval < 1000)
            newPollingInterval = 1000;

        if (newPollingInterval > 36000
                && newPollingInterval != pollingInterval_msec) {
            String msg = "WARNING: polling_interval_msec has been configured"
                    + " to a very long interval (~"
                    + (newPollingInterval + 18000) / 36000 + " hours)";
            System.out.println(msg);
            log.warn(msg);
        }

        if (newJobTimeout_msec < newPollingInterval)
            newJobTimeout_msec = newPollingInterval;

        if (newJobTimeout_msec < newPollingInterval * 2
                && newJobTimeout_msec != jobTimeout_msec) {
            String msg = "WARNING: job_timeout_msec has been configured"
                    + " to a short interval that may cause unecessary job"
                    + " transfers (" + newJobTimeout_msec + " msec).";
            System.out.println(msg);
            log.warn(msg);
        }

        if (newWorkerThreads < 1)
            newWorkerThreads = 1;

        if (newWorkerThreads > 1000 && newWorkerThreads != maxWorkerThreads) {
            String msg = "WARNING: max_worker_threads has been configured to"
                    + " a very large value (" + newWorkerThreads + ")";
            System.out.println(msg);
            log.warn(msg);
        }

        if (newMaxEmailsPerSec < 1)
            newMaxEmailsPerSec = 1;

        if (!configRead
                || !newContextFactory.equals(contextFactory)
                || !newContextProvider.equals(contextProvider)
                || newWorkerThreads != maxWorkerThreads
                || newPollingInterval != pollingInterval_msec
                || newMaxEmailsPerSec != maxEmailsPerSec
                || newJobTimeout_msec != jobTimeout_msec) {
            // profile changed
            if (configRead)
                shutdown();
            configRead = true;
            contextFactory = newContextFactory;
            contextProvider = newContextProvider;
            maxWorkerThreads = newWorkerThreads;
            pollingInterval_msec = newPollingInterval;
            maxEmailsPerSec = newMaxEmailsPerSec;
            jobTimeout_msec = newJobTimeout_msec;
            log.info("Email configuration updated.");
            log.info("Email context_factory: " + contextFactory);
            log.info("Email context_provider: " + contextProvider);
            log.info("Email polling_interval_msec: " + pollingInterval_msec);
            log.info("Email max_worker_threads: " + maxWorkerThreads);
            log.info("Email max_emails_per_second_per_job: " + maxEmailsPerSec);
            log.info("Email job_timeout_msec: " + jobTimeout_msec);
            startup();
        }
    }

    /**
     * Access member for EmailJobScheduler to get the maxEmailsPerSecond config variable
     * @return
     */
    public int getMaxEmailsPerSecond() {
        return maxEmailsPerSec;
    }

    /**
     * Access member for EmailJobScheduler to get the current schedulerId
     * @return
     */
    public long getSchedulerId() {
        return schedulerId;
    }

    /**
     * readConfig reads a value from the configuration file, using a default value
     * if the name is not found.
     * @param resource
     * @param name
     * @param defaultValue
     * @return
     */
    public int readConfig(ResourceBundle resource, String name, int defaultValue) {
        String newValue = readConfig(resource, name, null);
        try {
            return Integer.parseInt(newValue);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    /**
     * readConfig reads a value from the configuration file, using a default value
     * if the name is not found.
     * @param resource
     * @param name
     * @param defaultValue
     * @return
     */
    public String readConfig(ResourceBundle resource, String name, String defaultValue) {
        try {
            return resource.getString(name);
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    /**
     * This function changes the state of any active job to ready.
     * The active state indicates that the job is running.
     * When this function is called, the scheduler is just starting
     * up, so it is not possible that the jobs are already running.
     * Instead, we must assume that the job were left in a invalid
     * state due to an abrupt termination of the scheduler.
     */
    public void clearActiveJobs() {
        Context ctx = null;
        Hashtable ht = new Hashtable();
        ht.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        ht.put(Context.PROVIDER_URL, contextProvider);
        try {
            ctx = new InitialContext(ht);
            EmailServerHome home = (EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome");
            EmailServer email = home.create();

            Set jobs = email.getJobs(email.ACTIVE, email.ANYRANGE);
            Iterator jobItr = jobs.iterator();
            for (; jobItr.hasNext();) {
                int jobId = ((Integer) jobItr.next()).intValue();
                email.setJobStatus(jobId, email.READY);
                log.debug("Changed job " + jobId + " status to Ready (was Active)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    /**
     * This function checks the database for jobs that are ready to
     * be run. If there are any, it changes the state to active and
     * adds the job to the queue.
     * Additionally, this checks if there are any active jobs that
     * haven't been updated within the job_timeout_msec. If there
     * are, take over scheduling them.
     * Note: there are two main cases where jobs might time out.
     * 1) The job was scheduled by another scheduler instance that
     *    crashed. The job needs to be restarted so it can complete.
     * 2) The job was scheduled and is sitting in a queue waiting
     *    for resources to become available. The job doesn't really
     *    need to be restarted. However, if there are multiple
     *    schedulers running, then rescheduling queued jobs will
     *    allow a job that is waiting for a busy scheduler to be
     *    moved to a scheduler that has resources available.
     */
    public void checkSchedule() {
        Context ctx = null;
        Hashtable ht = new Hashtable();
        Set jobs;
        Iterator jobItr;

        ht.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        ht.put(Context.PROVIDER_URL, contextProvider);
        try {
            ctx = new InitialContext(ht);
            EmailServer email = ((EmailServerHome) ctx.lookup(
                    "com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();

            schedulerId = email.getSchedulerId();

            // update history
            long clearId = history[0];
            timeoutFrac += pollingInterval_msec;
            int historyInc = jobTimeout_msec / HISTORY_SIZE;
            if (historyInc < 1) historyInc = 1;
            for (; timeoutFrac >= historyInc; timeoutFrac -= historyInc) {
                for (int i = 0; i < HISTORY_SIZE - 1; i++) history[i] = history[i + 1];
                history[HISTORY_SIZE - 1] = schedulerId;
            }


            jobs = email.getJobs(email.READY, email.AFTERRANGE);
            jobItr = jobs.iterator();
            for (; !stopRequested && jobItr.hasNext();) {
                // for each ready job that has expired, mark job as incomplete.
                int jobId = ((Integer) jobItr.next()).intValue();
                log.debug("Marking as incomplete job " + jobId);
                email.setJobStatus(jobId, email.INCOMPLETE);
            }

            jobs = email.getJobs(email.ACTIVE, email.AFTERRANGE);
            jobItr = jobs.iterator();
            for (; !stopRequested && jobItr.hasNext();) {
                // for each active job that has expired, mark job as incomplete.
                int jobId = ((Integer) jobItr.next()).intValue();
                log.debug("Marking as incomplete job " + jobId);
                email.setJobStatus(jobId, email.INCOMPLETE);
            }

            int newJobs = StageQueue.available();

            jobs = email.getJobs(email.READY, email.INRANGE);
            jobItr = jobs.iterator();
            for (; !stopRequested && jobItr.hasNext() && newJobs >= 0;) {
                // for each ready job, create job task, mark job active and
                // add to queue.
                int jobId = ((Integer) jobItr.next()).intValue();
                if (email.acquireJob(jobId, schedulerId)) {
                    log.debug("Preparing to queue job " + jobId);
                    email.setJobStatus(jobId, email.ACTIVE);
                    StageQueue.addTask(createTask(ctx, jobId));
                    newJobs--;
                }
            }

            jobs = email.getJobs(email.ACTIVE, email.INRANGE);
            jobItr = jobs.iterator();
            for (; !stopRequested && jobItr.hasNext() && newJobs >= 0;) {
                // for each active job, get its controlId from the database,
                // compare to the clearId and reschedule if appropriate.
                int jobId = ((Integer) jobItr.next()).intValue();
                long controlId = email.getJobControlId(jobId);
                if (controlId < clearId) {
                    if (email.acquireJob(jobId, schedulerId, controlId)) {
                        log.debug("Preparing to re-queue job " + jobId);
                        StageQueue.addTask(createTask(ctx, jobId));
                        newJobs--;
                    }
                }
            }

            // if we scheduled all the waiting jobs, take some extra time
            // to clear out any old controlIds
            if (newJobs >= 0) {
                email.clearJobControlIds(clearId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    /**
     * Lookup a task type and create an object to put into the queue.
     * @param ctx
     * @param jobId
     * @return
     * @throws Exception
     */
    private Runnable createTask(Context ctx, int jobId)
            throws Exception {
        try {
            EmailJob job = ((EmailJobHome) ctx.lookup(
                    "com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();

            int jobType = job.getJobTypeId(jobId);

            if (jobType == EmailServer.EMAIL_JOB_TYPE_PRE
                    || jobType == EmailServer.EMAIL_JOB_TYPE_POST) {
                return new SendEmailTask((Context) (ctx.lookup(new CompositeName())), this, jobId, schedulerId);
            }

            if (jobType == EmailServer.EMAIL_JOB_TYPE_REPORT) {
                return new EmailReportTask((Context) (ctx.lookup(new CompositeName())), this, jobId, schedulerId);
            }
            throw new Exception("Unknown job type: " + jobType);
        } finally {
        }
    }

    /**
     * This function shuts down resources that are used
     * throughout the life-cycle of the scheduler.
     */
    public void shutdown() {
        log.info("Shutting down EmailJobScheduler");
        if (waitThread == Thread.currentThread()) waitThread = null;
        StageQueue.stop();
    }

}

