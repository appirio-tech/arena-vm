package com.topcoder.shared.ejb.EmailServices;

import com.topcoder.shared.ejb.BaseEJB;
import com.topcoder.shared.util.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.shared.util.logging.Logger;

import javax.ejb.EJBException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eric Ellingson
 * @version $Revision$
 * @see EmailTemplate
 */
public class EmailTemplateBean extends BaseEJB {

    /**
     *
     */
    public void ejbCreate() {
    }

    private static final Logger log = Logger.getLogger(EmailTemplateBean.class);


    /**
     * @param group
     * @param name
     * @param data
     * @return
     * @throws EJBException
     */
    public int createTemplate(int group, String name, String data) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        int id = 1;

        log.debug("Create template requested (group " + group + ", name " + name + ")");

        try {
            conn = DBMS.getConnection();

            id = (int) IdGeneratorClient.getSeqId("EMAIL_TEMPLATE_SEQ");


            sqlStmt.setLength(0);
            sqlStmt.append(" INSERT INTO");
            sqlStmt.append(" email_template (");
            sqlStmt.append(" email_template_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_template_name");
            sqlStmt.append(",");
            sqlStmt.append(" email_template_group_id");
            sqlStmt.append(",");
            sqlStmt.append(" data");
            sqlStmt.append(") VALUES (?,?,?,?)");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, group);
//            ps.setBytes(4, data.getBytes());
            ps.setString(4, data);
            rows = ps.executeUpdate();
            if (rows != 1) {
                throw new Exception("insert command affected " + rows + " rows.");
            }
        } catch (Exception dberr) {
            String err = "Failed to create template";
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
     * @return
     * @throws EJBException
     */
    public Map getTemplates() throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        Map ret = new HashMap();

        log.debug("getTemplates requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_template_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_template_name");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_template");
            ps = conn.prepareStatement(sqlStmt.toString());
            rs = ps.executeQuery();
            for (; rs.next();) {
                ret.put(new Integer(rs.getInt(1)), rs.getString(2));
            }
        } catch (Exception dberr) {
            String err = "Failed to get template names";
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
     * @param groupId
     * @return
     * @throws EJBException
     */
    public Map getTemplates(int groupId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        Map ret = new HashMap();

        log.debug("getTemplates for group requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_template_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_template_name");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_template");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_template_group_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, groupId);
            rs = ps.executeQuery();
            for (; rs.next();) {
                ret.put(new Integer(rs.getInt(1)), rs.getString(2));
            }
        } catch (Exception dberr) {
            String err = "Failed to get template names for group " + groupId;
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
     * @param templateId
     * @return
     * @throws EJBException
     */
    public String getTemplateName(int templateId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        String ret = "";

        log.debug("getTemplateName requested for template " + templateId);

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_template_name");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_template");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_template_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, templateId);
            rs = ps.executeQuery();
            rs.next();
            ret = rs.getString(1);
        } catch (Exception dberr) {
            String err = "Failed to get data for " + templateId;
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
     * @param templateId
     * @return
     * @throws EJBException
     */
    public int getTemplateGroupId(int templateId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        int ret = 0;

        log.debug("getTemplateGroupId requested for template " + templateId);

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_template_group_id");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_template");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_template_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, templateId);
            rs = ps.executeQuery();
            rs.next();
            ret = rs.getInt(1);
        } catch (Exception dberr) {
            String err = "Failed to get data for " + templateId;
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
     * @param templateId
     * @return
     * @throws EJBException
     */
    public String getData(int templateId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        String ret = "";

        log.debug("getData for template requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" data");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_template");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_template_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, templateId);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString(1) != null)
                    ret = rs.getString(1);
            }
        } catch (Exception dberr) {
            String err = "Failed to get data for template " + templateId;
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
     * @param templateId
     * @return
     * @throws EJBException
     */
    public boolean isInUse(int templateId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        int ret = 0;

        log.debug("isInUse for template requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" COUNT(*)");
            sqlStmt.append(" FROM");
            sqlStmt.append(" sched_job s,");
            sqlStmt.append(" sched_email_job e");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" s.sched_job_id = e.sched_email_job_id");
            sqlStmt.append(" AND");
            sqlStmt.append(" e.email_template_id = ?");
            sqlStmt.append(" AND");
            sqlStmt.append(" (s.sched_job_status_id = ?");
            sqlStmt.append(" OR");
            sqlStmt.append("  s.sched_job_status_id = ?)");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, templateId);
            ps.setInt(2, EmailJobBean.JOB_STATUS_READY);
            ps.setInt(3, EmailJobBean.JOB_STATUS_ACTIVE);
            rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getInt(1);
            }
        } catch (Exception dberr) {
            String err = "Failed to get data for template " + templateId;
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }

        return (ret > 0);
    }

    /**
     * @param templateId
     * @param groupId
     * @throws EJBException
     */
    public void setGroupId(int templateId, int groupId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("Update group id for template requested (template " + templateId + ", group " + groupId + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" email_template");
            sqlStmt.append(" SET");
            sqlStmt.append(" email_template_group_id = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_template_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, groupId);
            ps.setInt(2, templateId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Update of template " + templateId
                        + " had no effect."
                        + " Most likely the template does not exist.");
                throw new Exception("update command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not update just a single"
                            + " record (id " + templateId + ", " + rows
                            + " records updated).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to update template";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param templateId
     * @param name
     * @throws EJBException
     */
    public void setName(int templateId, String name) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("Update name for template requested (template " + templateId + ", name " + name + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" email_template");
            sqlStmt.append(" SET");
            sqlStmt.append(" email_template_name = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_template_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setString(1, name);
            ps.setInt(2, templateId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Update of template " + templateId
                        + " had no effect."
                        + " Most likely the template does not exist.");
                throw new Exception("update command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not update just a single"
                            + " record (id " + templateId + ", " + rows
                            + " records updated).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to update template";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param templateId
     * @param data
     * @throws EJBException
     */
    public void setData(int templateId, String data) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("Update data for template requested (template " + templateId + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" email_template");
            sqlStmt.append(" SET");
            sqlStmt.append(" data = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_template_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setString(1, data);
            ps.setInt(2, templateId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Update of template " + templateId
                        + " had no effect."
                        + " Most likely the template does not exist.");
                throw new Exception("update command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not update just a single"
                            + " record (id " + templateId + ", " + rows
                            + " records updated).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to update template";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }
}
