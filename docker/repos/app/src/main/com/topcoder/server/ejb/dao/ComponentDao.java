/*
 * ComponentDao
 *
 * Created 10/23/2006
 */
package com.topcoder.server.ejb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.topcoder.shared.util.DBMS;

/**
 * Data Access Object (DAO) class for querying/accessing
 * Component table objects.
 *
 * All new SQL queries accessing this table should be placed in this class to
 * to avoid code duplication.
 *
 * This is not a DAO pattern exactly, but it is a step to remove SQL code from
 * services.
 * Connection is passed to all methods because currently
 * we don't manage connection and transaction using the app server.
 *
 * @author Diego Belfer (mural)
 * @version $Id: ComponentDao.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class ComponentDao {

    /**
     * Returns the className for the component
     *
     * @param componentId Id of the component
     * @param conn Connection to use
     *
     * @return ClassName for the component
     *
     * @throws SQLException if an SQLException is thrown during the process
     */
    public String getClassNameForComponent(int componentId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT class_name FROM component WHERE component_id = ?");
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();
            return  rs.getString(1);
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    public Object[] getClassNameAndProblemIdForComponent(int componentId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT class_name, problem_id FROM component WHERE component_id = ?");
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();
            String className = rs.getString(1);
            Integer problemId = new Integer(rs.getInt(2));
            return  new Object[] {className, problemId};
        } finally {
            DBMS.close(null, ps, rs);
        }
    }


    public String getComponentText(int componentId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT component_text FROM component WHERE component_id = ?");
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            rs.next();
            return DBMS.getTextString(rs, 1);
        }  finally {
            DBMS.close(null, ps, rs);
        }
    }


    public boolean setComponentText(int componentId, String xmlText, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE component SET component_text = ? WHERE component_id = ?");
            ps.setBytes(1, DBMS.serializeTextString(xmlText));
            ps.setInt(2, componentId);
            return ps.executeUpdate() == 1;
        }  finally {
            DBMS.close(ps);
        }
    }
    
    private static final String COMP_ACCEPT_SUBMISSION = 
        "select 1 from component c, problem p " +
        "   where p.problem_id = c.problem_id and p.accept_submissions = 1 and c.component_id = ?";

    public boolean isComponentAccepttingSubmissions(int componentId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement(COMP_ACCEPT_SUBMISSION);
            ps.setInt(1, componentId);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
}
