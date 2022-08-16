package com.topcoder.shared.ejb.EmailServices;

import com.topcoder.shared.ejb.BaseEJB;
import com.topcoder.shared.util.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import javax.ejb.EJBException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VolodymyrK
 * @see EmailJobGroup
 */
public class EmailJobGroupBean extends BaseEJB {
    /**
     *
     */
    public void ejbCreate() {
    }

    private static final Logger log = Logger.getLogger(EmailJobGroupBean.class);

    /**
     * @param name
     * @return
     * @throws EJBException
     */
    public int addGroup(String name) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        int id = 1;

        log.debug("Add job group requested (name " + name + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" MAX(email_job_group_id)");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_job_group_lu");
            ps = conn.prepareStatement(sqlStmt.toString());
            rs = ps.executeQuery();
            if (rs.next())
                id = rs.getInt(1) + 1;
            else
                log.warn("Failed to get max email job group id,"
                        + " using default value of 1.");

            sqlStmt.setLength(0);
            sqlStmt.append(" INSERT INTO");
            sqlStmt.append(" email_job_group_lu (");
            sqlStmt.append(" email_job_group_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_job_group_name");
            sqlStmt.append(") VALUES (?,?)");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, id);
            ps.setString(2, name);
            rows = ps.executeUpdate();
            if (rows != 1) {
                throw new Exception("insert command affected " + rows + " rows.");
            }
        } catch (Exception dberr) {
            String err = "Failed to add job group";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }

        return id;
    }

    /**
     * @param id
     * @param name
     * @throws EJBException
     */
    public void updateGroup(int id, String name) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("Update job group requested (id " + id + ", name " + name + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" email_job_group_lu");
            sqlStmt.append(" SET");
            sqlStmt.append(" email_job_group_name = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_job_group_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setString(1, name);
            ps.setInt(2, id);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Update of job group " + id
                        + " had no effect."
                        + " Most likely the group does not exist.");
                throw new Exception("update command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not update just a single"
                            + " record (id " + id + ", " + rows
                            + " records updated).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to update job group";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param id
     * @throws EJBException
     */
    public void removeGroup(int id) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("Remove job group requested (id " + id + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" DELETE FROM");
            sqlStmt.append(" email_job_group_lu");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_job_group_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, id);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Removal of job group " + id
                        + " had no effect."
                        + " Most likely the group does not exist.");
                throw new Exception("delete command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not remove just a single"
                            + " record (id " + id + ", " + rows
                            + " records removed).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to remove job group";
            log.error(err, dberr);
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
    public Map getGroups() throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        Map ret = new HashMap();

        log.debug("getGroups for job groups requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_job_group_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_job_group_name");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_job_group_lu");
            ps = conn.prepareStatement(sqlStmt.toString());
            rs = ps.executeQuery();
            for (; rs.next();) {
                ret.put(new Integer(rs.getInt(1)), rs.getString(2));
            }
        } catch (Exception dberr) {
            String err = "Failed to get job group names";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }

        return ret;
    }

    /**
     * @param id
     * @return
     * @throws javax.ejb.EJBException
     */
    public String getName(int id) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        String name = null;

        log.debug("getName for job group requested (id " + id + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_job_group_name");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_job_group_lu");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_job_group_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString(1);
            } else {
                throw new Exception("record not found");
            }
        } catch (Exception dberr) {
            String err = "Failed to get job group name";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }

        return name;
    }
}
