package com.topcoder.shared.ejb.EmailServices;

import com.topcoder.shared.ejb.BaseEJB;
import com.topcoder.shared.util.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.shared.util.logging.Logger;

import javax.ejb.EJBException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eric Ellingson
 * @version $Revision$
 * @see EmailJob
 */
public class EmailServerBean extends BaseEJB {
    /**
     *
     */
    public static final int SCHEDULER_SEQUENCE_ID = 74;

    /**
     *
     */
    public void ejbCreate() {
    }

    private static final Logger log = Logger.getLogger(EmailServerBean.class);

    /**
     * @return
     * @throws EJBException
     */
    public Date getDate() throws EJBException {
        try {
            return new Date();
        } catch (Exception e) {
            throw new EJBException("Failed to get date", e);
        }
    }

    /**
     * @param status
     * @param dateRange
     * @return
     * @throws EJBException
     */
    public Set getJobs(int status, int dateRange) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        Set ret = new HashSet();

        //log.debug("getJobs based on status=" + status);

        try {
            conn = getConnection();

            java.sql.Timestamp now = new java.sql.Timestamp(new Date().getTime());

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" sched_job_id");
            sqlStmt.append(" FROM");
            sqlStmt.append(" sched_job");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_status_id = ?");
            switch (dateRange) {
                case EmailServer.ANYRANGE:
                    break;
                case EmailServer.BEFORERANGE:
                    sqlStmt.append(" AND start_after_date > '" + now.toString() + "'");
                    break;
                case EmailServer.INRANGE:
                    sqlStmt.append(" AND start_after_date <= '" + now.toString() + "'");
                    sqlStmt.append(" AND end_before_date > '" + now.toString() + "'");
                    break;
                case EmailServer.AFTERRANGE:
                    sqlStmt.append(" AND end_before_date <= '" + now.toString() + "'");
                    break;
            }
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, status);
            rs = ps.executeQuery();
            for (; rs.next();) {
                ret.add(new Integer(rs.getInt(1)));
            }
        } catch (Exception dberr) {
            String err = "Failed to get job ids";
            log.error(err, dberr);
            throw new EJBException(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }

        return ret;
    }

    /**
     * @param jobId
     * @param status
     * @throws EJBException
     */
    public void setJobStatus(int jobId, int status) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("setJobStatus (jobId " + jobId + ", status " + status + ")");

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" sched_job");
            sqlStmt.append(" SET");
            sqlStmt.append(" sched_job_status_id");
            sqlStmt.append(" = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id");
            sqlStmt.append(" = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, status);
            ps.setInt(2, jobId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("The update had no effect."
                        + " Most likely the job does not exist.");
                throw new Exception("The update command affected "
                        + rows + " rows.");
            }
        } catch (Exception dberr) {
            String err = "Failed to update job status";
            log.error(err, dberr);
            throw new EJBException(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param jobId
     * @param type
     * @throws EJBException
     */
    public void setJobType(int jobId, int type) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("setJobType (jobId " + jobId + ", type " + type + ")");

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" sched_job");
            sqlStmt.append(" SET");
            sqlStmt.append(" sched_job_type_id");
            sqlStmt.append(" = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id");
            sqlStmt.append(" = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, type);
            ps.setInt(2, jobId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("The update had no effect."
                        + " Most likely the job does not exist.");
                throw new Exception("The update command affected "
                        + rows + " rows.");
            }
        } catch (Exception dberr) {
            String err = "Failed to update job type";
            log.error(err, dberr);
            //throw new EJBException(err, dberr);
            throw new EJBException(dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param jobId
     * @throws EJBException
     */
    public void clearDetailRecords(int jobId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("clearDetailRecords jobId " + jobId);

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" DELETE");
            sqlStmt.append(" FROM");
            sqlStmt.append(" sched_job_detail");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, jobId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                //normal result
            } else {
                // must have been an aborted job
                log.info("clearDetailRecords removed " + rows + " records from job " + jobId);
            }
        } catch (Exception dberr) {
            String err = "Failed to clearDetailRecords";
            log.error(err, dberr);
            throw new EJBException(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param jobId
     * @param data
     * @return
     * @throws EJBException
     */
    public int addDetailRecord(int jobId, String data) throws EJBException {
        javax.naming.Context ctx = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.PreparedStatement ps1 = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        int id = 0;

        log.debug("addDetailRecord (jobId " + jobId + ")");

        try {

            id = (int) IdGeneratorClient.getSeqId("SCHED_JOB_DETAIL_SEQ");
            try {
                conn = getConnection();

                sqlStmt.setLength(0);
                sqlStmt.append(" INSERT INTO");
                sqlStmt.append(" sched_job_detail (");
                sqlStmt.append(" sched_job_id");
                sqlStmt.append(",");
                sqlStmt.append(" sched_job_detail_id");
                sqlStmt.append(",");
                sqlStmt.append(" sched_job_detail_status_id");
                sqlStmt.append(",");
                sqlStmt.append(" data");
                sqlStmt.append(") VALUES (?,?,?,?)");
                ps1 = conn.prepareStatement(sqlStmt.toString());
                ps1.setInt(1, jobId);
                ps1.setInt(2, id);
                ps1.setInt(3, EmailServer.MSG_NONE);
                ps1.setBytes(4, data.getBytes());
                rows = ps1.executeUpdate();
                if (rows != 1) {
                    throw new Exception("insert command affected " + rows + " rows.");
                }
            } finally {
                DBMS.close(ps1);
                DBMS.close(conn);
                ApplicationServer.close(ctx);
            }

        } catch (Exception dberr) {
            String err = "Failed to update job status";
            log.error(err, dberr);
            throw new EJBException(err, dberr);
        }


        return id;
    }

    /**
     * @param jobId
     * @param detailId
     * @param status
     * @param reason
     * @throws EJBException
     */
    public void setDetailStatus(int jobId, int detailId, int status, String reason) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("setDetailStatus (jobId " + jobId + ", detailId " + detailId + ", status " + status + ":" + reason + ")");

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" sched_job_detail");
            sqlStmt.append(" SET");
            sqlStmt.append(" sched_job_detail_status_id = ?");
            sqlStmt.append(",");
            sqlStmt.append(" reason = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id = ?");
            sqlStmt.append(" AND");
            sqlStmt.append(" sched_job_detail_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, status);
            ps.setString(2, reason);
            ps.setInt(3, jobId);
            ps.setInt(4, detailId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("The update had no effect."
                        + " Most likely the job detail record does not exist.");
                throw new Exception("The update command affected "
                        + rows + " rows.");
            }
        } catch (Exception dberr) {
            String err = "Failed to update job status";
            log.error(err, dberr);
            throw new EJBException(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param jobId
     * @throws EJBException
     */
    public void setJobBuilt(int jobId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("setJobBuilt (jobId " + jobId + ")");

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" sched_job");
            sqlStmt.append(" SET");
            sqlStmt.append(" sched_job_type_id = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id = ?");
            sqlStmt.append(" AND");
            sqlStmt.append(" sched_job_type_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, EmailServer.EMAIL_JOB_TYPE_POST);
            ps.setInt(2, jobId);
            ps.setInt(3, EmailServer.EMAIL_JOB_TYPE_PRE);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("The update had no effect."
                        + " Most likely the job detail record does not exist"
                        + " or the job was already built.");
                throw new Exception("The update command affected "
                        + rows + " rows.");
            }
        } catch (Exception dberr) {
            String err = "Failed to update job type";
            log.error(err, dberr);
            throw new EJBException(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param jobId
     * @throws EJBException
     */
    public void archiveDetail(int jobId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("archiveDetail (jobId " + jobId + ")");

        try {
            conn = DBMS.getTransConnection();
            conn.setAutoCommit(false);

            java.sql.Timestamp now = new java.sql.Timestamp(new Date().getTime());

            sqlStmt.setLength(0);
            sqlStmt.append(" INSERT INTO");
            sqlStmt.append(" archive_sched_job_detail (sched_job_id, sched_job_detail_id, sched_job_detail_status_id, data, reason, insert_date)");
            sqlStmt.append(" SELECT sched_job_id, sched_job_detail_id, sched_job_detail_status_id, data, reason, '" + now.toString() + "' as insert_date");
            sqlStmt.append(" FROM");
            sqlStmt.append(" sched_job_detail");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, jobId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                conn.rollback();  // not that this will do anything...
                log.debug("The update had no effect."
                        + " Most likely the job detail record do not exist or"
                        + " have already been archived.");
                throw new Exception("The update command affected "
                        + rows + " rows.");
            }
            DBMS.close(ps);

            sqlStmt.setLength(0);
            sqlStmt.append(" DELETE FROM");
            sqlStmt.append(" sched_job_detail");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, jobId);
            int drows = ps.executeUpdate();
            if (drows != rows) {
                conn.rollback();
                log.debug("The delete did not modify the same number of rows"
                        + " that the insert added (" + rows + " inserted, "
                        + drows + " deleted). Transaction rolled back.");
                throw new Exception("Mismatched insert/delete count."
                        + " Transaction rolled back (" + rows + " inserted, "
                        + drows + " deleted)");
            }
            conn.commit();
        } catch (Exception dberr) {
            String err = "Failed to archive detail records";
            log.error(err, dberr);
            throw new EJBException(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @return
     * @throws EJBException
     */
    public long getSchedulerId() throws EJBException {

        try {
            return IdGeneratorClient.getSeqId("SCHEDULER_SEQ");
        } catch (Exception dberr) {
            log.error("Failed to get schedulerId", dberr);
            throw new EJBException("Failed to get schedulerId", dberr);
        } 
    }

    /**
     * @param jobId
     * @param controlId
     * @return
     * @throws EJBException
     */
    public boolean acquireJob(int jobId, long controlId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        StringBuffer sqlStmt = new StringBuffer(500);

        log.debug("acquireJob(" + jobId + "," + controlId + ") requested");

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" INSERT INTO");
            sqlStmt.append(" sched_control (");
            sqlStmt.append(" sched_control_id");
            sqlStmt.append(",");
            sqlStmt.append(" sched_job_id");
            sqlStmt.append(") VALUES (?,?)");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setLong(1, controlId);
            ps.setInt(2, jobId);
            try {
                int rows = ps.executeUpdate();
                if (rows == 1) {
                    return true;
                } else if (rows > 1) {
                    throw new Exception("insert command affected " + rows + " rows.");
                }
                return false;
            } catch (java.sql.SQLException e) {
                // this is probably an unique constraint exception caused
                // by two schedulers trying to acquire the same job.
                return false;
            }
        } catch (Exception dberr) {
            throw new EJBException("Error while attempting to acquireJob", dberr);
        } finally {
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param jobId
     * @param controlId
     * @param oldId
     * @return
     * @throws EJBException
     */
    public boolean acquireJob(int jobId, long controlId, long oldId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        StringBuffer sqlStmt = new StringBuffer(500);

        log.debug("acquireJob(" + jobId + "," + controlId + "," + oldId + ") requested");

        if (oldId == 0 && acquireJob(jobId, controlId)) return true;

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE ");
            sqlStmt.append(" sched_control");
            sqlStmt.append(" SET");
            sqlStmt.append(" sched_control_id = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id = ?");
            sqlStmt.append(" AND");
            sqlStmt.append(" sched_control_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setLong(1, controlId);
            ps.setInt(2, jobId);
            ps.setLong(3, oldId);
            int rows = ps.executeUpdate();
            if (rows >= 1) {
                return true;
            }
            return false;
        } catch (Exception dberr) {
            throw new EJBException("Error while attempting to acquireJob", dberr);
        } finally {
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param jobId
     * @return
     * @throws EJBException
     */
    public long getJobControlId(int jobId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);

        log.debug("getJobControlId(" + jobId + ") requested");

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT ");
            sqlStmt.append(" sched_control_id");
            sqlStmt.append(" FROM");
            sqlStmt.append(" sched_control");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_job_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, jobId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (Exception dberr) {
            throw new EJBException("Error while attempting to getJobControlId", dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param controlId
     * @throws EJBException
     */
    public void clearJobControlIds(long controlId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        StringBuffer sqlStmt = new StringBuffer(500);

        //log.debug("clearJobControlIds(" + controlId + ") requested");

        try {
            conn = getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" DELETE");
            sqlStmt.append(" FROM");
            sqlStmt.append(" sched_control");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" sched_control_id < ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setLong(1, controlId);
            ps.executeUpdate();
        } catch (Exception dberr) {
            throw new EJBException("Error while attempting to clearJobControlIds", dberr);
        } finally {
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }


    /**
     * Creates a DB connection. The method tries to get a connection few times in a row.
     * @return DB Connection
     * @throws Exception
     */
    private java.sql.Connection getConnection() throws Exception {
        Exception lastException = null;
        java.sql.Connection conn = null;

        for(int i=0;i<10;i++) {
            try {
                conn = DBMS.getConnection();
                setLockMode(conn);
                return conn;
            } catch (Exception e) {
                lastException = e;
                DBMS.close(conn);
                conn = null;
            }
        }

        throw lastException;
    }

    private void setLockMode(java.sql.Connection conn) throws Exception {
        java.sql.PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("SET LOCK MODE TO WAIT 100;");
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }
}


