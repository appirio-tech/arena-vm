package com.topcoder.shared.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.sql.InformixSimpleDataSource;

/**
 * A class to hold constants related to the database, and some convenience methods.
 *
 * @author Jason Evans
 * @author ademich
 */
public class DBMS {

    private static final TCResourceBundle bundle = new TCResourceBundle("DBMS");
    private static Logger log = Logger.getLogger(DBMS.class);

    public static final int INFORMIX = getIntProperty("INFORMIX", 1);
    public static int DB = getIntProperty("DB", INFORMIX);

    public final static String DW_DATASOURCE_NAME = getProperty("DW_DATASOURCE_NAME", "DW");
    public final static String OLTP_DATASOURCE_NAME = getProperty("OLTP_DATASOURCE_NAME", "java:OLTP");
    public final static String JTS_OLTP_DATASOURCE_NAME = getProperty("JTS_OLTP_DATASOURCE_NAME", "JTS_OLTP");
    public final static String CONTEST_ADMIN_DATASOURCE = getProperty("CONTEST_ADMIN_DATASOURCE", "OLTP");
    public final static String CORP_JTS_OLTP_DATASOURCE_NAME = getProperty("CORP_JTS_OLTP_DATASOURCE_NAME", "JTS_CORP_DS");
    public final static String CORP_OLTP_DATASOURCE_NAME = getProperty("CORP_OLTP_DATASOURCE_NAME", "CORP_DS");
    public final static String COMMON_JTS_OLTP_DATASOURCE_NAME = getProperty("CORP_JTS_OLTP_DATASOURCE_NAME", "JTS_DS");
    public final static String COMMON_OLTP_DATASOURCE_NAME = getProperty("COMMON_OLTP_DATASOURCE_NAME", "DS");
    public final static String SCREENING_JTS_OLTP_DATASOURCE_NAME = getProperty("SCREENING_JTS_OLTP_DATASOURCE_NAME", "JTS_SCREENING_OLTP");
    public final static String SCREENING_OLTP_DATASOURCE_NAME = getProperty("SCREENING_OLTP_DATASOURCE_NAME", "SCREENING_OLTP");
    public final static String HS_DW_DATASOURCE_NAME = getProperty("HS_DW_DATASOURCE_NAME", "HS_DW");
    public final static String HS_OLTP_DATASOURCE_NAME = getProperty("HS_OLTP_DATASOURCE_NAME", "HS_OLTP");
    public final static String HS_JTS_OLTP_DATASOURCE_NAME = getProperty("HS_JTS_OLTP_DATASOURCE_NAME", "JTS_HS_OLTP");
    public final static String TCS_OLTP_DATASOURCE_NAME = getProperty("TCS_OLTP_DATASOURCE_NAME", "TCS_CATALOG");
    public final static String TCS_JTS_OLTP_DATASOURCE_NAME = getProperty("TCS_JTS_OLTP_DATASOURCE_NAME", "JTS_TCS_CATALOG");
    public final static String TCS_DW_DATASOURCE_NAME = getProperty("TCS_DW_DATASOURCE_NAME", "TCS_DW");
    public final static String FORUMS_DATASOURCE_NAME = getProperty("FORUMS_DATASOURCE_NAME", "java:JiveDS");
    //public final static String STUDIO_FORUMS_DATASOURCE_NAME = getProperty("STUDIO_FORUMS_DATASOURCE_NAME", "java:StudioJiveDS");
    public final static String STUDIO_DATASOURCE_NAME = getProperty("STUDIO_DATASOURCE_NAME", "java:JTS_STUDIO_OLTP");
    public final static String OPENAIM_DATASOURCE_NAME = getProperty("OPENAIM_DATASOURCE_NAME", "java:JTS_OPENAIM_OLTP");
    public final static String CSF_DATASOURCE_NAME = getProperty("CSF_DATASOURCE_NAME", "java:JTS_CSF_OLTP");
    public final static String TRUVEO_DATASOURCE_NAME = getProperty("TRUVEO_DATASOURCE_NAME", "java:JTS_TRUVEO_OLTP");
    public final static String AOLICQ_DATASOURCE_NAME = getProperty("AOLICQ_DATASOURCE_NAME", "java:JTS_AOLICQ_OLTP");
    public final static String WINFORMULA_DATASOURCE_NAME = getProperty("WINFORMULA_DATASOURCE_NAME", "java:JTS_WINFORMULA_OLTP");
    public final static String PIPELINE_DATASOURCE_NAME = getProperty("PIPELINE_DATASOURCE_NAME", "java:PIPELINE");
    public final static String JIRA_DATASOURCE_NAME = getProperty("JIRA_DATASOURCE_NAME", "java:BUGS");


    /**
     * @deprecated
     */
    public final static String JDBC_DRIVER = getProperty("JDBC_DRIVER", "weblogic.jdbc.jts.Driver");
    /**
     * @deprecated
     */
    public final static String POOL_DRIVER = getProperty("POOL_DRIVER", "weblogic.jdbc.pool.Driver");
    public final static String INFORMIX_DRIVER = getProperty("INFORMIX_DRIVER", "com.informix.jdbc.IfxDriver");
    /**
     * @deprecated
     */
    public final static String JMA_INFORMIX_POOL = getProperty("JMA_INFORMIX_POOL", "jdbc:weblogic:pool:JMAInformixPool");
    /**
     * @deprecated
     */
    public final static String JMA_INFORMIX_POOL_JTS = getProperty("JMA_INFORMIX_POOL_JTS", "jdbc:weblogic:jts:JMAInformixPool");
    /**
     * @deprecated
     */
    public final static String JMA_INFORMIX_DW_POOL = getProperty("JMA_INFORMIX_DW_POOL", "jdbc:weblogic:pool:JMAInformixDWPool");
    /**
     * @deprecated
     */
    public final static String JMA_INFORMIX_DW_POOL_JTS = getProperty("JMA_INFORMIX_DW_POOL_JTS", "jdbc:weblogic:jts:JMAInformixDWPool");
    /**
     * @deprecated
     */
    public final static String JMA_POOL = getProperty("JMA_POOL", "jdbc:weblogic:pool:JMAPool");
    /**
     * @deprecated
     */
    public final static String JMA_POOL_JTS = getProperty("JMA_POOL_JTS", "jdbc:weblogic:jts:JMAPool");
    public final static String JMS_FACTORY = getProperty("JMS_FACTORY", "jms.connection.jmsFactory");
    public final static String EMAIL_QUEUE = getProperty("EMAIL_QUEUE", "queue/eMailQueue");
    public final static String COMPILE_QUEUE = getProperty("COMPILE_QUEUE", "queue/compileQueue");
    public final static String TESTING_QUEUE = getProperty("TESTING_QUEUE", "queue/testingQueue");
    public final static String REFERENCE_TESTING_QUEUE = getProperty("REFERENCE_TESTING_QUEUE", "referenceTestingQueue");
    public final static String TOPIC = getProperty("TOPIC", "contestTopic");
    public final static String WEB_SERVICE_QUEUE = getProperty("WEB_SERVICE_QUEUE", "webServiceGeneratorQueue");
    public final static String RESPONSE_QUEUE = getProperty("RESPONSE_QUEUE", "queue/screeningResponseQueue");
    public final static String REQUEST_QUEUE = getProperty("REQUEST_QUEUE", "queue/screeningRequestQueue");

    //formats used by informix for converting string to date(time)
    public final static String INFORMIX_DATE_FORMAT = bundle.getProperty("INFORMIX_DATE_FORMAT", "MM/dd/yy");
    public final static String INFORMIX_DATETIME_FORMAT = bundle.getProperty("INFORMIX_DATETIME_FORMAT", "yyyy-MM-dd HH:mm:ss.SSS");

    // Sequence Ids
    public static final String JMA_SEQ = "MAIN_SEQ";
    public static final String PROBLEM_SEQ = "PROBLEM_SEQ";
    public static final String CHALLENGE_SEQ = "CHALLENGE_SEQ";
    public static final String COMPONENT_STATE_SEQ = "COMPONENT_STATE_SEQ";
    public static final String SURVEY_SEQ = "SURVEY_SEQ";
    public static final String ROOM_SEQ = "ROOM_SEQ";
    public static final String SERVER_SEQ = "SERVER_SEQ";
    public static final String REQUEST_SEQ = "REQUEST_SEQ";
    public static final String BROADCAST_SEQ = "BROADCAST_ID_SEQ";
    public static final String PARAMETER_SEQ = "PARAMETER_SEQ";
    public static final String COMPONENT_SEQ = "COMPONENT_SEQ";
    public static final String WEB_SERVICE_SEQ = "WEB_SERVICE_SEQ";
    public static final String WEB_SERVICE_SOURCE_FILE_SEQ = "WEB_SERVICE_SOURCE_FILE_SEQ";
    public static final String PAYMENT_SEQ = "PAYMENT_SEQ";
    public static final String WEB_SERVICE_JAVA_DOC_SEQ = "WEB_SERVICE_JAVA_DOC_SEQ";
    public static final String ROUND_SEQ = "ROUND_SEQ";
    public static final String CONTEST_SEQ = "CONTEST_SEQ";
    public static final String MESSAGE_SEQ = "MESSAGE_SEQ";
    public static final String BACKUP_SEQ = "BACKUP_SEQ";
    public static final String MAIN_SEQ = "MAIN_SEQ";
    public static final String LONG_TEST_GROUP_SEQ = "LONG_TEST_GROUP_SEQ";
    public static final String LONG_TEST_CASE_SEQ = "LONG_TEST_CASE_SEQ";
    public static final String RESPONSE_SEQ = "RESPONSE_SEQ";

    public final static String INFORMIX_CONNECT_STRING = getProperty("INFORMIX_CONNECT_STRING", "");
    public final static String EVENT_TOPIC = getProperty("EVENT_TOPIC", "eventTopic");
    public final static String MPSQAS_TOPIC = getProperty("MPSQAS_TOPIC", "mpsqasTopic");
    public final static String RESTART_TOPIC = getProperty("RESTART_TOPIC", "restartTopic");
    public final static String EVENT_QUEUE = getProperty("EVENT_QUEUE", "eventQueue");
    public final static String LONG_CONTEST_SVC_EVENT_TOPIC = getProperty("LONG_CONTEST_SVC_EVENT_TOPIC", "servicesEventTopic");
    public final static String LONG_TEST_SVC_EVENT_TOPIC = getProperty("LONG_TEST_SVC_EVENT_TOPIC", "servicesEventTopic");
    public final static String MPSQAS_SVC_EVENT_TOPIC = getProperty("MPSQAS_SVC_EVENT_TOPIC", "servicesEventTopic");
    

    private static final String getProperty(String key, String defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getProperty(key, defaultValue);
    }

    private static final int getIntProperty(String key, int defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getIntProperty(key, defaultValue);
    }

    /**
     * @return
     * @throws SQLException
     */
    public static final java.sql.Connection getConnection() throws SQLException {
        return getConnection(OLTP_DATASOURCE_NAME);
    }
    
    /**
     * @return
     * @throws SQLException
     */
    public static final java.sql.Connection getConnection(String dataSourceName) throws SQLException {
        Connection conn = null;
        InitialContext ctx = null;
        try {
            ctx = TCContext.getInitial();
            conn = getConnection(ctx, dataSourceName);
        } catch (NamingException e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException ne) {
                    throw new SQLException(ne.getMessage());
                }
            }
        }

        return conn;
    }

    /**
     * @return
     * @throws SQLException
     */
    public static final java.sql.Connection getDWConnection() throws SQLException {
        return getConnection(DW_DATASOURCE_NAME);
    }


    public static final java.sql.Connection getConnection(InitialContext context, String dataSourceName) throws SQLException {
        DataSource ds = null;
        try {
            ds = (DataSource) PortableRemoteObject.narrow(
                    context.lookup(dataSourceName), DataSource.class);
        } catch (NamingException e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return  ds.getConnection();
    }

    /**
     * @return
     * @throws SQLException
     */
    public static final java.sql.Connection getTransConnection() throws SQLException {
        return getConnection(JTS_OLTP_DATASOURCE_NAME);
//        java.sql.Connection result = null;
//        result = DriverManager.getConnection(DBMS.JMA_INFORMIX_POOL_JTS);
//        return result;
    }

    /**
     * This generic method deserializes a Blob from the ResultSet and returns the
     * deserialized object.
     *
     * @param rs     - ResultSet - The ResultSet that the blob should be retrieved from
     * @param column - int - The number of the column in the ResultSet that the blob should be
     *               retrieved from.
     * @return Object - the blob object retrieved from the ResultSet
     * @throws SQLException
     */
    public static Object getBlobObject(ResultSet rs, int column)
            throws SQLException {
        Object retVal = null;
        ObjectInputStream ois = null;
        try {

            InputStream is = rs.getBinaryStream(column);
            if (is != null)
                ois = new ObjectInputStream(is);
            if (ois != null) {
                retVal = ois.readObject();
            }

            if (ois != null) {
                ois.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException("DBMS: getBlobObject - error: " + e);
        }

        return retVal;

    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // This is a generic method deserializes a Blob from the database and returns the
    // deserialized object.
    ///////////////////////////////////////////////////////////////////////////////////////
    public static Object getBlobObject(String tableName, String fieldName, String whereClause) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object retVal = new Object();

        try {
            conn = DBMS.getDirectConnection();
            conn.setAutoCommit(false);

            String sqlStr;
            if (whereClause.equals(""))
                sqlStr = "SELECT " + fieldName + " FROM " + tableName;
            else
                sqlStr = "SELECT " + fieldName + " FROM " + tableName + " WHERE " + whereClause;
            ps = conn.prepareStatement(sqlStr);
            rs = ps.executeQuery();

            if (rs.next()) {
                retVal = DBMS.getBlobObject(rs, 1);
                System.out.println("RetVal :" + retVal);

            } else {
                System.out.println("getBlobObject did not find row for query :\n" + sqlStr);
            }
        } catch (Exception e) {
            error(e);
        } finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (conn != null) {
                    conn.commit();
                    conn.close();
                }
            } catch (Exception ignore) {
                error(ignore);
            }
        }
        return retVal;
    }


    /**
     * This method is used for serializing large (BLOB) objects because it can't be done through
     * SQL.
     *
     * @param obj - Object - the large object to be serialized.
     * @return byte[] - the serialized object as a byte array
     */
    public static byte[] serializeBlobObject(Object obj) {
        if (obj == null)
            throw new IllegalArgumentException("DBMS.serializeBlobObject:ERROR obj is NULL");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = null;


        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytes;

    }

    /**
     * This method is used for serializing text Strings because it can't be done through
     * SQL. This method should be called when updating or inserting text data types into
     * an INFORMIX database.
     * Example Usage:
     * String text = "text";
     * PreparedStatement ps = connectionVariable.prepareStatement(insertOrUpdateStatement);
     * if (DBMS.DB == DBMS.INFORMIX) {
     * ps.setBytes(column, DBMS.serializeTextString(text);
     * }
     *
     * @param text - String - the text String to be serialized.
     * @return byte[] - the serialized text String as a byte array
     */
    public static byte[] serializeTextString(String text) {
        if (text == null)
            throw new IllegalArgumentException("DBMS.serializeTextString:ERROR text is NULL");

        return text.getBytes();

    }

    /**
     * This generic method deserializes a text data type from the ResultSet and returns the
     * deserialized String. This method should be called when retrieving a text data type from
     * ANY database.
     * Example Usage:
     * String text = DBMS.getTextString(rs, columnNum);
     *
     * @param rs     - ResultSet - The ResultSet that the String should be retrieved from
     * @param column - int - The number of the column in the ResultSet that the String should be
     *               retrieved from.
     * @return String - the String retrieved from the Result Set at the column
     * @throws SQLException
     */
    public static String getTextString(ResultSet rs, int column)
            throws SQLException {
        if (rs == null)
            throw new IllegalArgumentException("DBMS.getTextString:ERROR ResultSet is NULL");
        try {
            byte[] bytes = rs.getBytes(column);
            String text = "";

            if (bytes == null) {
                text = "";
            } else {
                text = new String(bytes);
            }

            return new String(text);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }
    }

    public static String getTextString(ResultSet rs, String columnName) throws SQLException {
        if (rs == null)
            throw new IllegalArgumentException("ResultSet is NULL");
        byte[] bytes = rs.getBytes(columnName);
        if (bytes == null)
            return "";
        else
            return new String(bytes);
    }


    /**
     * printSqlException()
     * Iterate through and print out informix sql exception information.  Can be called
     * on non-informix sql exceptions.
     *
     * @param verbose - whether or not it should print the stack trace
     * @param sqle    - a SQL exception
     */
    public static void printSqlException(boolean verbose, SQLException sqle) {
        int i = 1;
        if (verbose) {
            System.out.println("*******************************");
            do {
                System.out.println("  Error #" + i + ":");
                System.out.println("    SQLState = " + sqle.getSQLState());
                //System.out.println("    Message = " + sqle.getMessage());
                System.out.println("    SQLCODE = " + sqle.getErrorCode());
                sqle.printStackTrace();
                sqle = sqle.getNextException();
                i++;
            } while (sqle != null);
        }
    }


    /**
     * This generic method deserializes a text data type from the ResultSet and returns the
     * deserialized String. This method should be called when retrieving a text data type from
     * ANY database.  Unlike the getTextString() method, this method will return a null String
     * if the database value is null (the getTextString() method returns an empty String).
     * <p/>
     * Example Usage:
     * String text = DBMS.getTextStringWithNulls(rs, columnNum);
     *
     * @param rs     - ResultSet - The ResultSet that the String should be retrieved from
     * @param column - int - The number of the column in the ResultSet that the String should be
     *               retrieved from.
     * @return String - the String retrieved from the Result Set at the column
     * @throws Exception
     */
    public static String getTextStringWithNulls(ResultSet rs, int column)
            throws Exception {
        if (rs == null)
            throw new Exception("DBMS.getTextStringWithNulls:ERROR ResultSet is NULL");
        try {
            byte[] bytes = rs.getBytes(column);
            if (bytes == null)
                return null;

            return new String(bytes);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new Exception(sqle.getMessage());
        }
    }


    /**
     * getSqlExceptionString()
     * Iterate through and collect sql exception information.  Can be called
     * on non-informix sql exceptions.  Returns a string containing the information. <p>
     * <p/>
     * One use for this method is with log4j when it is desired to collect exception
     * information into a logfile.
     *
     * @param sqle - a SQL exception
     * @return
     */
    public static String getSqlExceptionString(SQLException sqle) {
        int i = 1;
        StringBuffer sb = new StringBuffer(500);
        sb.append("*******************************\n");
        do {
            sb.append("  Error #" + i + ":\n");
            sb.append("    SQLState = " + sqle.getSQLState() + "\n");
            sb.append("    Message = " + sqle.getMessage() + "\n");
            sb.append("    SQLCODE = " + sqle.getErrorCode() + "\n");
            sqle = sqle.getNextException();
            i++;
        } while (sqle != null);

        return sb.toString();
    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        close(rs);
        close(ps);
        close(conn);
    }
    
    public static void close(PreparedStatement ps, ResultSet rs) {
        close(rs);
        close(ps);
    }
    
    public static void closeAndResetAC(Connection conn, PreparedStatement ps, ResultSet rs) {
        close(rs);
        close(ps);
        closeAndResetAC(conn);
    }

    public static void close(Connection conn) {
        if (conn == null) return;
        try {
            conn.close();
        } catch (Exception e) {
            printException(e);
        }
    }
    
    public static void closeAndResetAC(Connection conn) {
        if (conn == null) return;
        try {
            try {
                conn.setAutoCommit(true);
            } finally {
                conn.close();
            }
        } catch (Exception e) {
            printException(e);
        }
    }


    public static void close(PreparedStatement ps) {
        if (ps == null) return;
        try {
            ps.close();
        } catch (Exception e) {
            printException(e);
        }
    }

    public static void close(Statement s) {
        if (s == null) return;
        try {
            s.close();
        } catch (Exception e) {
            printException(e);
        }
    }

    public static void close(ResultSet rs) {
        if (rs == null) return;
        try {
            rs.close();
        } catch (Exception e) {
            printException(e);
        }
    }

    public static void printException(Exception e) {
        try {
            if (e instanceof SQLException) {
                //Avoiding Jboss bug
                if (e.getMessage() == null || e.getMessage().indexOf("Already closed") == -1) {
                    String sqlErrorDetails = DBMS.getSqlExceptionString((SQLException) e);
                    log.error("EJB: SQLException caught\n" + sqlErrorDetails, e);
                }
            } else {
                log.error("EJB: Exception caught", e);
            }
        } catch (Exception ex) {
            log.error("EJB: Error printing exception!", ex);
        }
    }

    private static DataSource directDataSource;

    public static Connection getDirectConnection() throws SQLException {
        DataSource dataSource = getDirectDataSource();
        Connection connection = dataSource.getConnection();
        return connection;
    }

    private static synchronized DataSource getDirectDataSource() {
        if (directDataSource == null) {
            try {
                directDataSource = new InformixSimpleDataSource(DBMS.INFORMIX_CONNECT_STRING);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            }
        }
        return directDataSource;
    }

    private static void error(Object message) {
        log.error(message);
    }

    // Returns the database name associated with the given datasource.
    public static String getDbName(String datasourceName) throws SQLException {
        Connection conn = null;
        String dbName = "";
        try {
            conn = getConnection(datasourceName);
            String jiveUrl = conn.getMetaData().getURL();
            String jiveDbStr = jiveUrl.substring(jiveUrl.indexOf("informix-sqli://"), jiveUrl.indexOf(":INFORMIXSERVER="));
            dbName = jiveDbStr.substring(jiveDbStr.lastIndexOf('/') + 1);
        } catch (SQLException e) {
            throw e;
        } finally {
            close(conn);
        }
        return dbName;
    }
}
