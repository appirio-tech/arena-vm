package com.topcoder.shared.ejb.EmailServices;

import com.topcoder.shared.ejb.BaseEJB;
import com.topcoder.shared.util.ApplicationServer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;
import com.topcoder.shared.util.logging.Logger;

import javax.ejb.EJBException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eric Ellingson
 * @version $Revision$
 * @see EmailList
 */
public class EmailListBean extends BaseEJB {
    /**
     *
     */
    public void ejbCreate() {
    }

    private static final Logger log = Logger.getLogger(EmailListBean.class);

    /**
     * @param group
     * @param name
     * @return
     * @throws EJBException
     */
    public int createList(int group, String name) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        int id = 1;

        log.debug("Create list requested (group " + group + ", name " + name + ")");

        try {
            conn = DBMS.getConnection();

            id = (int) IdGeneratorClient.getSeqId("EMAIL_LIST_SEQ");

            sqlStmt.setLength(0);
            sqlStmt.append(" INSERT INTO");
            sqlStmt.append(" email_list (");
            sqlStmt.append(" email_list_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_list_name");
            sqlStmt.append(",");
            sqlStmt.append(" email_list_group_id");
            sqlStmt.append(") VALUES (?,?,?)");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, group);
            rows = ps.executeUpdate();
            if (rows != 1) {
                throw new Exception("insert command affected " + rows + " rows.");
            }
        } catch (Exception dberr) {
            String err = "Failed to create list";
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
     * @param listId
     * @param data
     * @return
     * @throws EJBException
     */
    public int addMember(int listId, String data) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        int id = 1;

        log.debug("add list member requested (list " + listId + ")");

        try {
            conn = DBMS.getConnection();

            id = (int)IdGeneratorClient.getSeqId("EMAIL_LIST_SEQ");

            sqlStmt.setLength(0);
            sqlStmt.append(" INSERT INTO");
            sqlStmt.append(" email_list_detail (");
            sqlStmt.append(" email_list_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_list_detail_id");
            sqlStmt.append(",");
            sqlStmt.append(" data");
            sqlStmt.append(") VALUES (?,?,?)");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, listId);
            ps.setInt(2, id);
            ps.setBytes(3, data.getBytes());
            rows = ps.executeUpdate();
            if (rows != 1) {
                throw new Exception("insert command affected " + rows + " rows.");
            }
        } catch (Exception dberr) {
            String err = "Failed to create list";
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
     * @param listId
     * @param memberId
     * @throws EJBException
     */
    public void removeMember(int listId, int memberId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("Remove list member requested (list " + listId + ", member " + memberId + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" DELETE FROM");
            sqlStmt.append(" email_list_detail");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_id = ?");
            sqlStmt.append(" AND");
            sqlStmt.append(" email_list_detail_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, listId);
            ps.setInt(2, memberId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Removal of list member " + listId + ":" + memberId
                        + " had no effect."
                        + " Most likely the member does not exist.");
                throw new Exception("delete command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not remove just a single"
                            + " record (id " + listId + ":" + memberId + ", " + rows
                            + " records removed).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to remove list member";
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
    public Map getLists() throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        Map ret = new HashMap();

        log.debug("getLists requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_list_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_list_name");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_list");
            ps = conn.prepareStatement(sqlStmt.toString());
            rs = ps.executeQuery();
            for (; rs.next();) {
                ret.put(new Integer(rs.getInt(1)), rs.getString(2));
            }
        } catch (Exception dberr) {
            String err = "Failed to get list names";
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
    public Map getLists(int groupId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        Map ret = new HashMap();

        log.debug("getLists for group requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_list_id");
            sqlStmt.append(",");
            sqlStmt.append(" email_list_name");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_list");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_group_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, groupId);
            rs = ps.executeQuery();
            for (; rs.next();) {
                ret.put(new Integer(rs.getInt(1)), rs.getString(2));
            }
        } catch (Exception dberr) {
            String err = "Failed to get list names for group " + groupId;
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
     * @param listId
     * @return
     * @throws EJBException
     */
    public Set getMembers(int listId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        Set ret = new HashSet();

        log.debug("getMembers for list requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_list_detail_id");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_list_detail");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, listId);
            rs = ps.executeQuery();
            for (; rs.next();) {
                ret.add(new Integer(rs.getInt(1)));
            }
        } catch (Exception dberr) {
            String err = "Failed to get members for list " + listId;
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
     * @param listId
     * @return
     * @throws EJBException
     */
    public String getListName(int listId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        String ret = "";

        log.debug("getListName requested for list " + listId);

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_list_name");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_list");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, listId);
            rs = ps.executeQuery();
            rs.next();
            ret = rs.getString(1);
        } catch (Exception dberr) {
            String err = "Failed to get data for " + listId;
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
     * @param listId
     * @return
     * @throws EJBException
     */
    public int getListGroupId(int listId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        int ret = 0;

        log.debug("getListGroupId requested for list " + listId);

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" email_list_group_id");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_list");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, listId);
            rs = ps.executeQuery();
            rs.next();
            ret = rs.getInt(1);
        } catch (Exception dberr) {
            String err = "Failed to get data for " + listId;
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
     * @param listId
     * @param memberId
     * @return
     * @throws EJBException
     */
    public String getData(int listId, int memberId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;
        String ret = "";

        log.debug("getData for list member requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" SELECT");
            sqlStmt.append(" data");
            sqlStmt.append(" FROM");
            sqlStmt.append(" email_list_detail");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_id = ?");
            sqlStmt.append(" AND");
            sqlStmt.append(" email_list_detail_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, listId);
            ps.setInt(2, memberId);
            rs = ps.executeQuery();
            if (rs.next()) {
                byte[] bytes = rs.getBytes(1);
                if (bytes != null)
                    ret = new String(bytes);
            }
        } catch (Exception dberr) {
            String err = "Failed to get member data for " + listId + ":" + memberId;
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
     * @param listId
     * @param groupId
     * @throws EJBException
     */
    public void setGroupId(int listId, int groupId) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("Update group id for list requested (list " + listId + ", group " + groupId + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" email_list");
            sqlStmt.append(" SET");
            sqlStmt.append(" email_list_group_id = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setInt(1, groupId);
            ps.setInt(2, listId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Update of list " + listId
                        + " had no effect."
                        + " Most likely the list does not exist.");
                throw new Exception("update command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not update just a single"
                            + " record (id " + listId + ", " + rows
                            + " records updated).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to update list";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param listId
     * @param name
     * @throws EJBException
     */
    public void setName(int listId, String name) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("Update name for list requested (list " + listId + ", name " + name + ")");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" email_list");
            sqlStmt.append(" SET");
            sqlStmt.append(" email_list_name = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setString(1, name);
            ps.setInt(2, listId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Update of list " + listId
                        + " had no effect."
                        + " Most likely the list does not exist.");
                throw new Exception("update command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not update just a single"
                            + " record (id " + listId + ", " + rows
                            + " records updated).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to update list";
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

    /**
     * @param listId
     * @param memberId
     * @param data
     * @throws EJBException
     */
    public void setData(int listId, int memberId, String data) throws EJBException {
        javax.naming.Context ctx = null;
        javax.sql.DataSource ds = null;
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        StringBuffer sqlStmt = new StringBuffer(500);
        int rows;

        log.debug("setData for list member requested");

        try {
            conn = DBMS.getConnection();

            sqlStmt.setLength(0);
            sqlStmt.append(" UPDATE");
            sqlStmt.append(" email_list_detail");
            sqlStmt.append(" SET");
            sqlStmt.append(" data = ?");
            sqlStmt.append(" WHERE");
            sqlStmt.append(" email_list_id = ?");
            sqlStmt.append(" AND");
            sqlStmt.append(" email_list_detail_id = ?");
            ps = conn.prepareStatement(sqlStmt.toString());
            ps.setBytes(1, data.getBytes());
            ps.setInt(2, listId);
            ps.setInt(3, memberId);
            rows = ps.executeUpdate();
            if (rows == 0) {
                log.debug("Update of list " + listId + ", member " + memberId
                        + " had no effect."
                        + " Most likely the list or member does not exist.");
                throw new Exception("update command affected " + rows + " rows.");
            } else {
                if (rows != 1) {
                    log.warn("Update request did not update just a single"
                            + " record (list " + listId + ", member " + memberId
                            + ", " + rows + " records updated).");
                }
            }
        } catch (Exception dberr) {
            String err = "Failed to set member data for " + listId + ":" + memberId;
            log.error(err, dberr);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(conn);
            ApplicationServer.close(ctx);
        }
    }

}

