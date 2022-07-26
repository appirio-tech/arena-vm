package com.topcoder.server.ejb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.topcoder.shared.util.DBMS;


/**
 * This class is intended to be a base class for all of Medecoms EJBs.  Code
 * common to all EJBs should probably be included here.
 *
 * @author  Jess Evans
 */
public abstract class BaseEJB implements SessionBean {

//****************************************************************************
//                                 Constants
//****************************************************************************

    private static final boolean VERBOSE = false;



//****************************************************************************
//                               Protected  Members
//****************************************************************************

    /**
     * This method uses the DriverManager.getConnection(String) method to get a
     * database Connection from the regular (non-transactional) service pool
     * using a jdbc connection string.  This is faster and should be used for
     * services which don't require perform multiple inserts/updates to the db.
     *
     * @param pool	the connection pool name
     * @return 		a database Connection object.
     *
     */
    protected static Connection getConnection(String pool) throws SQLException {
        return DriverManager.getConnection("jdbc:weblogic:pool:" + pool);
    }


    /**
     * This method uses the DriverManager.getConnection(String) method to get a
     * database Connection from the transactional service pool using a jdbc
     * connection string.  This should be used when multiple inserts/updates
     * must be performed in one transaction.  JTS will ensure that a bean
     * failure results in a full db rollback.
     *
     * @param pool	the connection pool name
     * @return 		a database Connection object.
     *
     */
    protected static Connection getTransConnection(String pool) throws SQLException {
        return DriverManager.getConnection("jdbc:weblogic:jts:" + pool);
    }


    /**
     * This class returns the context.
     */
    protected Context getContext() {
        try {
            if (InitContext == null)
                InitContext = new InitialContext();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred getting context");
        }

        return InitContext;
    }


    /**
     * Returns the tag to append to the default debug statement.
     * This may be overridden for verbose debugging.
     */
    protected String getTag() {
        return TAG;
    }


//****************************************************************************
//                                 Get / Set
//****************************************************************************

    protected void setString(PreparedStatement ps, int index, String s) throws SQLException {
        if (s == null || s.equals(""))
            ps.setString(index, null);
        else
            ps.setString(index, s);
    }

    protected void setInt(PreparedStatement ps, int index, Integer i) throws SQLException {
        if (i == null)
            ps.setNull(index, java.sql.Types.INTEGER);
        else
            ps.setInt(index, i.intValue());
    }

    protected void setFloat(PreparedStatement ps, int index, Float f) throws SQLException {
        if (f == null)
            ps.setNull(index, java.sql.Types.FLOAT);
        else
            ps.setFloat(index, f.floatValue());
    }

    protected void setLong(PreparedStatement ps, int index, Long l) throws SQLException {
        if (l == null)
            ps.setNull(index, java.sql.Types.INTEGER);
        else
            ps.setLong(index, l.longValue());
    }

    protected void setDate(PreparedStatement ps, int index, java.sql.Date date) throws SQLException {
        if (date == null)
            ps.setNull(index, java.sql.Types.DATE);
        else
            ps.setDate(index, date);
    }

    protected void setObject(PreparedStatement ps, int index, Object obj) throws SQLException {
        if (obj == null)
            ps.setNull(index, java.sql.Types.OTHER);
        else
            ps.setObject(index, obj);
    }

    protected Object getObject(ResultSet rs, int index) throws SQLException {
        return rs.getObject(index);
    }

    protected static java.sql.Date getDate(ResultSet rs, int index) throws SQLException {
        return rs.getDate(index);
    }

    protected String getString(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    protected Integer getInt(ResultSet rs, int index) throws SQLException {
        int i = rs.getInt(index);
        if (rs.wasNull())
            return null;
        else
            return new Integer(i);
    }

    protected Float getFloat(ResultSet rs, int index) throws SQLException {
        float f = rs.getFloat(index);
        if (rs.wasNull())
            return null;
        else
            return new Float(f);
    }

    protected Long getLong(ResultSet rs, int index) throws SQLException {
        long l = rs.getLong(index);
        if (rs.wasNull())
            return null;
        else
            return new Long(l);
    }


    protected void executeUpdate(String sql, Object[] params) throws SQLException {
        Connection conn = null;
        try {
            conn = DBMS.getConnection();
            executeUpdate(conn, sql, params);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    protected static void executeUpdate(Connection conn, String sql, Object[] params) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            if (params != null)
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            int nrows = preparedStatement.executeUpdate();
            System.out.println("" + nrows + " updated: " + preparedStatement);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }



//****************************************************************************
//                                 Data Members
//****************************************************************************

    //private SessionContext ctx;
    //private transient Properties props;
    private transient Context InitContext;
    private static final String TAG = "BaseEJB";



//****************************************************************************
//                                 EJB lifecycle
//****************************************************************************

    /**
     * This method is required by the EJB Specification
     *
     */
    public void ejbActivate() {
        if (VERBOSE) System.out.println(getTag() + ":  ejbActivate called");
    }


    /**
     * This method is required by the EJB Specification
     *
     */
    public void ejbPassivate() {
        if (VERBOSE) System.out.println(getTag() + ":  ejbPassivate called");
    }


    /**
     * This method is required by the EJB Specification.
     * Used to get the context ... for dynamic connection pools.
     *
     */
    public void ejbCreate() throws CreateException {
        if (VERBOSE) System.out.println(getTag() + ":  ejbCreate called");

        InitContext = getContext();
    }


    /**
     * This method is required by the EJB Specification
     *
     */
    public void ejbRemove() {
        if (VERBOSE) System.out.println(getTag() + ":  ejbRemove called");
    }


    /**
     * Sets the transient SessionContext.
     * Sets the transient Properties.
     *
     */
    public void setSessionContext(SessionContext ctx) {
        if (VERBOSE) System.out.println("setSessionContext called");
        //this.ctx = ctx;
        //props = ctx.getEnvironment();
    }


//****************************************************************************
//                         Dynamic Connection Pooling
//****************************************************************************

    /*
     * Check to see the the connection pool exists.
     * @param poolName - connection pool name
     * @return true if pool exists, false otherwise.
     */
    /*
	public boolean poolExists(String poolName)
	{
		boolean exists = true;

		try {
			weblogic.jdbc.common.JdbcServices jdbc = (weblogic.jdbc.common.JdbcServices) InitContext.lookup("weblogic.jdbc.JdbcServices");

			if (jdbc.poolExists(poolName)) {
				exists = true;
			} else {
				exists = false;
				System.out.println(poolName+" Does Not exists");
			}
		} catch (Exception e) {
			System.out.println("Error checking connection pool existence.");
		}

		return exists;
	}
    */


/*
    static {
        DBMS.loadJdbcDriver();
    }
*/
}
