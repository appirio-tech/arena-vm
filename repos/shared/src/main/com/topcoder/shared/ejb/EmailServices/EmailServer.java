package com.topcoder.shared.ejb.EmailServices;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Set;

/**
 * This class provides access to the database for the EmailJobScheduler.
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 */
public interface EmailServer extends EJBObject {

    /**
     *
     */
    public static final int CREATING = 0;
    /**
     *
     */
    public static final int READY = 1;
    /**
     *
     */
    public static final int ACTIVE = 2;
    /**
     *
     */
    public static final int COMPLETE = 3;
    /**
     *
     */
    public static final int INCOMPLETE = 4;
    /**
     *
     */
    public static final int CANCELED = 5;

    /**
     *
     */
    public static final int ANYRANGE = 0;
    /**
     *
     */
    public static final int BEFORERANGE = 1;
    /**
     *
     */
    public static final int INRANGE = 2;
    /**
     *
     */
    public static final int AFTERRANGE = 3;
    /**
     *
     */

    /**
     *
     */
    public static final int MSG_NONE = 0;
    /**
     *
     */
    public static final int MSG_SENT = 1;
    /**
     *
     */
    public static final int MSG_FAILED = 2;

    /**
     *
     */
    public static final int EMAIL_JOB_TYPE_PRE = 1;
    /**
     *
     */
    public static final int EMAIL_JOB_TYPE_POST = 2;
    /**
     *
     */
    public static final int EMAIL_JOB_TYPE_REPORT = 3;

    /**
     * Returns the Date according to the remote system that the bean is running on.
     *
     * @return     the current date
     * @throws RemoteException
     */
    public Date getDate() throws RemoteException;

    /**
     * Returns the Set of jobs that are currently marked with the requested status.
     *
     * @param status
     * @param dateRange
     * @return     a Set of job ids (type Integer)
     * @throws RemoteException
     */
    public Set getJobs(int status, int dateRange) throws RemoteException;

    /**
     * Changes the status of a job to the requested status.
     * @param jobId
     * @param status
     * @throws RemoteException
     */
    public void setJobStatus(int jobId, int status) throws RemoteException;

    /**
     * Changes the type of a job to the requested type.
     * @param jobId
     * @param type
     * @throws RemoteException
     */
    public void setJobType(int jobId, int type) throws RemoteException;

    /**
     * Removes any detail records for the job that may have been added in
     * an aborted run.
     *
     * Under normal circumstances this function will not have anything to
     * do when it is called.
     * @param jobId
     * @throws RemoteException
     */
    public void clearDetailRecords(int jobId) throws RemoteException;

    /**
     * Adds a detail record to the job.  The detail record will be assigned a
     * new id and have a status of NONE.
     *
     * @param jobId
     * @param data
     * @return     the id of the new detail record
     * @throws RemoteException
     */
    public int addDetailRecord(int jobId, String data) throws RemoteException;

    /**
     * Changes the status of a job detail record to the requested status.
     * @param jobId
     * @param detailId
     * @param status
     * @param reason
     * @throws RemoteException
     */
    public void setDetailStatus(int jobId, int detailId, int status, String reason) throws RemoteException;

    /**
     * Marks the job as having been built so we don't try to build it again.
     * @param jobId
     * @throws RemoteException
     */
    public void setJobBuilt(int jobId) throws RemoteException;

    /**
     * Moves the job's detail records from the detail table to the archive table.
     * @param jobId
     * @throws RemoteException
     */
    public void archiveDetail(int jobId) throws RemoteException;

    /**
     * Returns the next scheduler id from the database.
     *
     * @return     the id the scheduler should use.
     * @throws RemoteException
     */
    public long getSchedulerId() throws RemoteException;

    /**
     * Attempts to assign an unassigned job to a scheduler.
     *
     * @param jobId
     * @param controlId
     * @return  true if the assignment succeeded or false if
     *          another scheduler already acquired the job.
     * @throws RemoteException
     */
    public boolean acquireJob(int jobId, long controlId) throws RemoteException;

    /**
     * Attempts to transfer control of an assigned job to
     * a new scheduler.
     *
     * @param jobId
     * @param controlId
     * @param oldId
     * @return  true if the assignment succeeded or false if
     *          another scheduler already transfered the job.
     * @throws RemoteException
     */
    public boolean acquireJob(int jobId, long controlId, long oldId) throws RemoteException;

    /**
     * Returns the controlId for a job.
     *
     * @param jobId
     * @return     the controlId that has been assigned to a job
     * @throws RemoteException
     */
    public long getJobControlId(int jobId) throws RemoteException;

    /**
     * Clear controlIds that are before the given id.
     * These are old control ids with completed jobs.
     * @param controlId
     * @throws RemoteException
     */
    public void clearJobControlIds(long controlId) throws RemoteException;

}

