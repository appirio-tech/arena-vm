package com.topcoder.shared.ejb.EmailServices;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;

/**
 * This class allows the creation of an email job.
 * The job requires that a email template and email list have already been created.
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 */
public interface EmailJob extends EJBObject {
    /**
     * Creates a new email job with the requested template, list, command, timeframe, and sender information.
     *
     * @param templateId
     * @param listId
     * @param commandId
     * @param jobGroupId
     * @param startAfter
     * @param stopBefore
     * @param fromAddress
     * @param fromPersonal
     * @param subject
     * @return     the job id for the newly created email job.
     * @throws RemoteException
     */
    int createEmailJob(int templateId,
                              int listId,
                              int commandId,
                              int jobGroupId,
                              Date startAfter,
                              Date stopBefore,
                              String fromAddress,
                              String fromPersonal,
                              String subject) throws RemoteException;

    /**
     * Creates a new email report job with the requested template, list, command, timeframe, and sender information.
     * An email report will wait until the sourceJobId is done running before running itself.
     * Be sure to allow some time after the source job expires for the report to run.
     *
     * @param sourceJobId
     * @param templateId
     * @param listId
     * @param commandId
     * @param jobGroupId
     * @param startAfter
     * @param stopBefore
     * @param fromAddress
     * @param fromPersonal
     * @param subject
     * @return     the job id for the newly created email job.
     * @throws RemoteException
     */
    int createEmailReportJob(
            int sourceJobId,
            int templateId,
            int listId,
            int commandId,
            int jobGroupId,
            Date startAfter,
            Date stopBefore,
            String fromAddress,
            String fromPersonal,
            String subject) throws RemoteException;

    /**
     * Cancels an email job.
     * For a job that hasn't yet started, this will prevent it from ever running.
     * For a job that is in progress, this will cause it to stop at the next acceptable point.
     * If the job is already complete, this has no effect.
     * @param jobId
     * @throws RemoteException
     */
    void cancelEmailJob(int jobId) throws RemoteException;

    /**
     * Restarts an email job that was previously canceled.
     * For a job that had never started, this will allow the job to run.
     * For a job that was in progress, this will cause it to resume from the point the job was stopped.
     * If the job was already complete, this has no effect.
     * @param jobId
     * @throws RemoteException
     */
    void resumeEmailJob(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     * @param jobId
     * @return     the type of the job
     * @throws RemoteException
     */
    int getJobTypeId(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the type name of the job
     * @throws RemoteException
     */
    String getJobTypeText(int jobId) throws RemoteException;

    /**
     * Converts a typeId into a name.
     *
     * @param typeId
     * @return     the name for the job typeId
     * @throws RemoteException
     */
    String getJobTypeIdText(int typeId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the current statusId for the job
     * @throws RemoteException
     */
    int getStatusId(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the status string for the job
      * @throws RemoteException
     */
    String getStatusText(int jobId) throws RemoteException;

    /**
     * Converts a status id into a status string.
     *
     * @param statusId
     * @return     the status string for the statusId
     * @throws RemoteException
     */
    String getStatusIdText(int statusId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the templateId for the job
     * @throws RemoteException
     */
    int getTemplateId(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the listId for the job
     * @throws RemoteException
     */
    int getListId(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the commandId for the job
     * @throws RemoteException
     */
    int getCommandId(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the commandName for the job
     * @throws RemoteException
     */
    String getCommandName(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the startAfter date for the job
     * @throws RemoteException
     */
    Date getStartAfterDate(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the stopBefore date for the job
     * @throws RemoteException
     */
    Date getStopBeforeDate(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the fromAddress for the job
     * @throws RemoteException
     */
    String getFromAddress(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the fromPersonal for the job
     * @throws RemoteException
     */
    String getFromPersonal(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the subject for the job
     * @throws RemoteException
     */
    String getSubject(int jobId) throws RemoteException;

    /**
     * Returns the requested information about the job.
     *
     * @param jobId
     * @return     the group ID for the job
     * @throws RemoteException
     */
    int getJobGroupId(int jobId) throws RemoteException;

    /**
     * Returns a map of the results for the job.
     * For a job that has not yet started, this will be an empty list.
     *
     * The map has jobDetailIds as the keys (type Integer) and
     * jobDetailStatusIds as the data (type Integer).
     *
     * @param jobId
     * @return     a map of jobDetailIds and jobDetailStatusIds for a job
     * @throws RemoteException
     */
    Map getJobDetailResults(int jobId) throws RemoteException;

    /**
     * Returns a three part array containing the results for a subset of
     * a job's results. The offsets are 0 based and include the last
     * record requested.
     * (for example, to get the first 10 results, request records 0 to 9).
     *
     * At index 0 is a Map of the results for for the requested subset.
     * The map has jobDetailIds as the keys (type Integer) and
     * jobDetailStatusIds as the data (type Integer).
     *
     * At index 1 is an Integer with a value of 0 or 1 that indicates if
     * there are additional results before the requested range.
     * A value of 0 indicates there not previous results.
     * A value of 1 indicates that there are previous results.
     *
     * At index 2 is an Integer with a value of 0 or 1 that indicates if
     * there are additional results after the requested range.
     * A value of 0 indicates there not additional results.
     * A value of 1 indicates that there are additional results.
     * @param jobId
     * @param firstRecordOffset
     * @param lastRecordOffset
     * @return     an array containing the results, a flag for previous results,
     * and a flag for additional results (type {Map, Integer, Integer}).
     * @throws RemoteException
     *
     * @see #getJobDetailResults(int jobId)
     */
    Object[] getJobDetailResults(int jobId, int firstRecordOffset, int lastRecordOffset) throws RemoteException;

    /**
     * Returns the jobDetailReason for a jobDetailId.
     *
     * @param jobId
     * @param jobDetailId
     * @return     the reason for the current status
     * @throws RemoteException
     */
    String getJobDetailReason(int jobId, int jobDetailId) throws RemoteException;

    /**
     * Returns the jobDetailData for a jobDetailId.
     *
     * @param jobId
     * @param jobDetailId
     * @return     the data for the jobDetailId for the current status
     * @throws RemoteException
     */
    String getJobDetailData(int jobId, int jobDetailId) throws RemoteException;

    /**
     * Returns true if the job's detail records have been archived.
     *
     * @param jobId
     * @return     Returns true if the job's detail records have been archived.
     *             Returns false if the job does not have archived records.
     * @throws RemoteException
     */
    boolean isJobDetailArchived(int jobId) throws RemoteException;

    /**
     * Converts a jobDetailStatusId into a status string.
     *
     * @param jobDetailStatusId
     * @return     the status string for the jobDetailStatusId
     * @throws RemoteException
     */
    String getDetailStatusIdText(int jobDetailStatusId) throws RemoteException;

    /**
     * Changes the information about the job.
     *
     * @param jobId
     * @param templateId
     * @throws     RemoteException if the job is already active or the job does not exist.
     */
    void setTemplateId(int jobId, int templateId) throws RemoteException;

    /**
     * Changes the information about the job.
     *
     * @param jobId
     * @param listId
     * @throws     RemoteException if the job is already active or the job does not exist.
     */
    void setListId(int jobId, int listId) throws RemoteException;

    /**
     * Changes the information about the job.
     *
     * @param jobId
     * @param commandId
     * @throws     RemoteException if the job is already active or the job does not exist.
     */
    void setCommandId(int jobId, int commandId) throws RemoteException;

    /**
     * Changes the information about the job.
     *
     * @param jobId
     * @param startAfterDate
     * @throws     RemoteException if the job is already active or the job does not exist.
     */
    void setStartAfterDate(int jobId, Date startAfterDate) throws RemoteException;

    /**
     * Changes the information about the job.
     *
     * @param jobId
     * @param stopBeforeDate
     * @throws     RemoteException if the job is already active or the job does not exist.
     */
    void setStopBeforeDate(int jobId, Date stopBeforeDate) throws RemoteException;

    /**
     * Changes the information about the job.
     *
     * @param jobId
     * @param fromAddress
     * @throws     RemoteException if the job is already active or the job does not exist.
     */
    void setFromAddress(int jobId, String fromAddress) throws RemoteException;

    /**
     * Changes the information about the job.
     *
     * @param jobId
     * @param fromPersonal
     * @throws     RemoteException if the job is already active or the job does not exist.
     */
    void setFromPersonal(int jobId, String fromPersonal) throws RemoteException;

    /**
     * Changes the information about the job.
     *
     * @param jobId
     * @param subject
     * @throws     RemoteException if the job is already active or the job does not exist.
     */
    void setSubject(int jobId, String subject) throws RemoteException;

    /**
     * Creates or updates command parameters for a job that has a commandId as its data source.
     *
     * @param jobId
     * @param inputId
     * @param param
     * @throws     RemoteException if the parameter can not be set.
     */
    void setCommandParam(int jobId, int inputId, String param) throws RemoteException;

    /**
     * Returns a Map of the command parameters for a job.
     *
     * @param jobId
     * @return     A Map of the command parameters with the map keys being the inputId (Integer)
     *             and the map values being the param (String) for the jobId.
     * @throws RemoteException
     */
    Map getCommandParams(int jobId) throws RemoteException;

    /**
     * Returns the inputName for the inputId.
     *
     * @param inputId
     * @return     The name of the input.  This can be fed to the Statistics bean.
     * @throws RemoteException
     */
    String getCommandParamName(int inputId) throws RemoteException;

}

