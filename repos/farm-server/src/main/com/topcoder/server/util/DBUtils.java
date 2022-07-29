/*
 * DBUtils
 *
 * Created 04/19/2006
 */
package com.topcoder.server.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class contains various methods that simplify programming
 * database access code.
 *
 * @author Diego Belfer (mural)
 * @version $Id: DBUtils.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class DBUtils {
    private static final Logger log = Logger.getLogger(DBUtils.class);
    
    /**
     * Contains connection information for each thread
     */
    private static final ThreadLocal connection = new ThreadLocal(){
        protected Object initialValue() {
            return new ConnectionCount();
        }
    }; 
    
    /**
     * Gets an integer value from the column <code>columnIndex</code> of the
     * ResultSet <code>rs</code>
     *
     * @param rs ResultSet used to obtain the value
     * @param columnIndex Index of the column where the value is
     *
     * @return an Integer value from the column or null if the value for the column was null.
     *
     * @throws SQLException If an SQLException is thrown during the process
     */
    public static Integer getInt(ResultSet rs, int columnIndex) throws SQLException {
        int v =  rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return new Integer(v);
    }


    /**
     * Gets a long value from the column <code>columnIndex</code> of the
     * ResultSet <code>rs</code>
     *
     * @param rs ResultSet used to obtain the value
     * @param columnIndex Index of the column where the value is
     *
     * @return a Long value from the column or null if the value for the column was null.
     *
     * @throws SQLException If an SQLException is thrown during the process
     */
    public static Long getLong(ResultSet rs, int columnIndex) throws SQLException {
        long v =  rs.getLong(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return new Long(v);
    }

    /**
     * Gets a double value from the column <code>columnIndex</code> of the
     * ResultSet <code>rs</code>
     *
     * @param rs ResultSet used to obtain the value
     * @param columnIndex Index of the column where the value is
     *
     * @return a Double value from the column or null if the value for the column was null.
     *
     * @throws SQLException If an SQLException is thrown during the process
     */
    public static Double getDouble(ResultSet rs, int columnIndex) throws SQLException {
        double v =  rs.getDouble(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return new Double(v);
    }

    /**
     * Gets a boolean value from the column <code>columnIndex</code> of the
     * ResultSet <code>rs</code>
     *
     * @param rs ResultSet used to obtain the value
     * @param columnIndex Index of the column where the value is
     *
     * @return a Boolean value from the column or null if the value for the column was null.
     *
     * @throws SQLException If an SQLException is thrown during the process
     */
    public static Boolean getBoolean(ResultSet rs, int columnIndex) throws SQLException {
        boolean v =  rs.getBoolean(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return BooleanUtils.valueOf(v);
    }

    /**
     * Gets a String value from the column <code>columnIndex</code> of the
     * ResultSet <code>rs</code>, if the value is null returns an empty String
     *
     * @param rs ResultSet used to obtain the value
     * @param columnIndex Index of the column where the value is
     *
     * @return a String value from the column or
     *          an empty String if the value for the column was null.
     *
     * @throws SQLException If an SQLException is thrown during the process
     */
    public static String getStringEmpty(ResultSet rs, int columnIndex) throws SQLException {
        String v =  rs.getString(columnIndex);
        if (rs.wasNull()) {
            return "";
        }
        return v;
    }


    /**
     * Helper method that sets the string argument in the PreparedStatement, if and only if
     * the value is not null and is not empty
     *
     * @param ps The PreparedStatement where to set the value
     * @param index The parameter index to use
     * @param value The value to set
     *
     * @return index if value is null or empty, ++index if the parameter was set
     *
     * SQLException If an SQLException is thrown during the process
     */
    public static int setIfNotEmptyOrNull(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.length() == 0) {
            return index;
        }
        ps.setString(index, value);
        return ++index;
    }

    /**
     * Generates a IN SQL expression for the specified field and array
     * if the array is null or its length is 0, an empty String is
     * returned.
     *
     * For example:
     *      sqlStrInList("field1", new int[] {1,2,3})
     * will generated a string
     *      field1 IN (1, 2 ,3)
     *
     * @param fieldName Name of the field to use in the expression
     * @param intArray Array containing the list values.
     *
     * @return IN SQL expression or empty string
     */
    public static String sqlStrInList(String fieldName, int[] intArray) {
        if (intArray == null || intArray.length == 0) {
            return "";
        }
        StringBuffer inSql = new StringBuffer(50);
        inSql.append(fieldName);
        inSql.append(" IN (");
        int lastIndex = intArray.length-1;
        for (int i = 0; i < lastIndex; i++) {
            inSql.append(intArray[i]);
            inSql.append(",");
        }
        inSql.append(intArray[lastIndex]);
        inSql.append(")");
        return inSql.toString();
    }
    
    /**
     * Generates a IN SQL expression for the specified field and collection
     * if the collection is null or its size is 0, an empty String is
     * returned.
     *
     * For example:
     *      sqlStrInList("field1", Collection<Integer>{1,2,3})
     * will generated a string
     *      field1 IN (1, 2 ,3)
     *
     * @param fieldName Name of the field to use in the expression
     * @param col Collection containing the list values. Values are added as returned by toString
     *
     * @return IN SQL expression or empty string
     */
    public static String sqlStrInList(String fieldName, Collection col) {
        if (col == null || col.size() == 0) {
            return "";
        }
        StringBuffer inSql = new StringBuffer(50);
        inSql.append(fieldName);
        inSql.append(" IN (");
        for (Iterator iter = col.iterator(); iter.hasNext();) {
            inSql.append(iter.next());
            inSql.append(",");
        }
        inSql.setLength(inSql.length()-1);
        inSql.append(")");
        return inSql.toString();
    }

    /**
     * Generates a IN SQL expression for the specified field and array
     * if the array is null or its length is 0, an empty String is
     * returned.
     *
     * For example:
     *      sqlStrInList("field1", new long[] {1,2,3})
     * will generated a string
     *      field1 IN (1, 2 ,3)
     *
     * @param fieldName Name of the field to use in the expression
     * @param longArray Array containing the list values.
     *
     * @return IN SQL expression or empty string
     */
    public static String sqlStrInList(String fieldName, long[] longArray) {
        if (longArray == null || longArray.length == 0) {
            return "";
        }
        StringBuffer inSql = new StringBuffer(50);
        inSql.append(fieldName);
        inSql.append(" IN (");
        int lastIndex = longArray.length-1;
        for (int i = 0; i < lastIndex; i++) {
            inSql.append(longArray[i]);
            inSql.append(",");
        }
        inSql.append(longArray[lastIndex]);
        inSql.append(")");
        return inSql.toString();
    }

    /**
     * Adds to expression the expressionToAdd using AND operator.
     * If the expressionToAdd is empty or null, expression is not changed
     *
     * @param expression Expression where to and the expressionToAdd
     * @param expressionToAdd expression to add with an AND operator
     */
    public static void andSql(StringBuffer expression, String expressionToAdd) {
        if (expressionToAdd == null || expressionToAdd.length() == 0) {
            return;
        }
        expression.append(" AND (");
        expression.append(expressionToAdd);
        expression.append(")");
    }

    /**
     * Adds to the sqlStatement the where clause if the expression if not null and
     * if it is not an empty string
     * If the expression starts with " AND ", expression is added starting from character 5
     *
     * @param sqlStatement Statement where to add the where clause
     * @param expression Expression to add as where clause
     */
    public static void addWhereClause(StringBuffer sqlStatement, String expression) {
        if (expression == null || expression.length() == 0) {
            return;
        }
        sqlStatement.append(" WHERE ");
        if (expression.startsWith(" AND ")){
            sqlStatement.append( expression.substring(5));
        } else {
            sqlStatement.append(expression);
        }
        sqlStatement.append(' ');
    }

    /**
     * Executes the code of the unitOfWork in a transaction.
     * This methods set autoCommit to false, invokes transactionalMethod
     * of the unit of work and if no exception is thrown, commits the connection.
     * If an exception is thrown by transactionalMethod, connection is rolledback.
     *
     * If connection autocommit is false, status of the connection is not changed.
     * Neither setAutoCommit, commit or rollback are invoked on the connection.
     *
     * @param cnn
     * @param work
     * @return
     * @throws Exception
     */
    public static Object invoke(Connection cnn, UnitOfWork work) throws Exception {
        boolean autoCommit = cnn.getAutoCommit();
        if (autoCommit)  {
            if (log.isDebugEnabled()) {
                log.debug("setting auto commit to false on connection: " + cnn);
            }
            cnn.setAutoCommit(false);
        }
        try {
            Object result = work.doWork(cnn);
            if (autoCommit) {
                if (log.isDebugEnabled()) {
                    log.debug("commiting on connection: " + cnn);
                }
                cnn.commit();
            }
            return result;
        } catch (Exception e) {
            if (autoCommit) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("rolling back on connection: " + cnn + " on exception", e);
                    }
                    cnn.rollback();
                } catch (Exception e1) {
                    log.error("exception rollingback on connection : " + cnn, e);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Exception on transactional method. Leaving rollback for parent. connection: " + cnn);
                }
                // Parent transaction should rollback
            }
            throw e;
        } finally {
            if (autoCommit) {
                if (log.isDebugEnabled()) {
                    log.debug("Restoring autocommit to true on connection: " + cnn);
                }
                try {
                    cnn.setAutoCommit(true);
                } catch (Exception e) {
                    log.error("exception restoring autocommit on connection : " + cnn, e);
                }
            }
        }
    }

    /**
     * Calls invoke and close the connection.
     * @see DBUtils#invoke(Connection, UnitOfWork)
     */
    public static Object invokeAndClose(Connection cnn, UnitOfWork work) throws Exception {
        try {
            return invoke(cnn, work);
        } finally {
            DBMS.close(cnn);
        }
    }
    
    
    /**
     * Associates the given connection with the current thread, if no connection
     * is already associated. If a connection was already associated with the Thread throws 
     * an IllegalStateException
     */
    public static void initDBBlock(Connection cnn) {
        ConnectionCount cnc;
        cnc = (ConnectionCount) connection.get();
        if (cnc.count != 0) {
            throw new IllegalStateException("Connection already created");
        }
        cnc.cnn = cnn;
        cnc.count++;
    }

    /**
     * Associates a connection {@link DBMS#getConnection()} with the current thread, if no connection
     * is already associated. If a connection was already associated with the Thread the reference count
     * is incremented by 1. 
     * 
     * @return The connection 
     * @throws SQLException If the connection could not be obtained
     */
    public static Connection initDBBlock() throws SQLException {
        ConnectionCount cnc;
        cnc = (ConnectionCount) connection.get();
        if (cnc.count == 0) {
            cnc.cnn = DBMS.getConnection();
        }
        cnc.count++;
        return cnc.cnn;
    }

    /**
     * Decrement the connection reference count for the calling thread, if after decrementing
     * the count is 0, the connection is close. If is greater than 0, it is left open.
     * 
     * Calling this method when no connection is associated to the calling thread, won't throw any
     * {@link Exception}.
     * 
     */
    public static void endDBBlock() {
        ConnectionCount cnc = (ConnectionCount) connection.get();
        if (cnc.count == 0) {
            return;
        }
        cnc.count--;
        if (cnc.count == 0) {
            DBMS.close(cnc.cnn);
            cnc.cnn = null;
        }
    }

    /**
     * Return the connection associated with the calling thread
     * 
     * @return The connection
     * @throws IllegalStateException if no connection is associated with the calling thread.
     */
    public static Connection getCurrentConnection() {
        ConnectionCount cnc;
        cnc = (ConnectionCount) connection.get();
        if (cnc.count > 0) {
            return cnc.cnn;
        }
        throw new IllegalStateException("You must call initDBBlock before getting current connection");
    }
    
    

    /**
     * An UnitOfWork represents code that must be executed in a transaction
     *
     */
    public static interface UnitOfWork {

        /**
         * The method containing the code to be run in a transaction
         *
         * @param cnn The connection that the code must use.
         *            commit, rollback or setAutoCommit should not be called

         * @return Value to return to the caller
         *
         * @throws Exception
         */
        public Object doWork(Connection cnn) throws Exception;
    }
    
    
    
    private static class ConnectionCount {
        Connection cnn;
        int count;
        
        public ConnectionCount() {
        }
    }

}
