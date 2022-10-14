package com.topcoder.shared.util.loader;

import com.topcoder.shared.util.DBMS;

import java.sql.*;
import java.util.Properties;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 12, 2006
 */
public abstract class BaseDataRetriever implements DataRetriever {
    public static final int CODER_LOG_TYPE = 2;

    protected Queue processingQueue;
    protected Connection sourceConn;
    protected Connection targetConn;
    protected Properties configuration;

    public void registerTargetProcessingQueue(Queue q) {
        this.processingQueue = q;
    }

    public void setSourceDatabase(Connection conn) {
        this.sourceConn = conn;
    }

    public void setTargetDatabase(Connection conn) {
        this.targetConn = conn;
    }

    public void setConfiguration(Properties p) {
        this.configuration = p;
    }

    protected static Timestamp getLastUpdateItem(Connection conn, int logTypeId) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer query = null;

        query = new StringBuffer(100);
        query.append("select timestamp from update_log where log_id = ");
        query.append("(select max(log_id) from update_log where log_type_id = ").append(logTypeId).append(")");

        Timestamp ret;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query.toString());
            if (rs.next()) {
                ret = rs.getTimestamp(1);
                log.info("Date is " + ret.toString());
            } else {
                throw new RuntimeException("Last log time not found in " +
                        "update_log table for type " + logTypeId);
            }
        } finally {
            DBMS.close(rs);
            DBMS.close(stmt);
        }
        return ret;
    }

}
