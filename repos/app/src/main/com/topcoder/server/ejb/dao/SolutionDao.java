/*
* Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.ejb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.tester.Solution;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.DBMS;

/**
 * Data Access Object (DAO) class for querying/accessing  
 * Solution table objects.
 * 
 * All new SQL queries accessing this table should be placed in this class to 
 * to avoid code duplication.
 * 
 * This is not a DAO pattern exactly, but it is a step to remove SQL code from
 * services. 
 * Connection is passed to all methods because currently 
 * we don't manage connection and transaction using the app server.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Updated {@link #getComponentSolution(String, Object, Connection)}
 *     to handle changes (added property resultType) in {@link com.topcoder.server.tester.Solution} class.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), gevak
 * @version 1.1
 */
public class SolutionDao {
    /**
     * Category for logging.
     */
    private static final Logger log = Logger.getLogger(SolutionDao.class);
    
    /**
     * Returns the language for the solution
     * 
     * @param solutionId Id of the solution
     * @param conn Connection to use
     * 
     * @return languageId of the solution
     * 
     * @throws SQLException if an SQLException is thrown during the process 
     */
    public int getLanguageForSolution(int solutionId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT language_id FROM solution WHERE solution_id = ?");
            ps.setInt(1, solutionId);
            rs = ps.executeQuery();
            rs.next();
            return  rs.getInt(1);
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
    
    /**
     * Returns the Primary Solution for the given component
     * 
     * @param componentId The id of the component
     * @param conn The connection to use
     * 
     * @return The solution 
     * 
     * @throws SQLException if an SQLException is thrown during the process or not primary solution exists for the given component 
     */
    public Solution getComponentSolution(int componentId, Connection conn) throws SQLException {
        return getComponentSolution("component_id", new Integer(componentId), conn);
    }
    
    /**
     * Returns the Primary Solution for the component with the given className
     * 
     * @param className The className of the component
     * @param conn The connection to use
     * 
     * @return The solution 
     * 
     * @throws SQLException if an SQLException is thrown during the process or not primary solution exists for the given component 
     */
    public Solution getComponentSolution(String className, Connection conn) throws SQLException {
        return getComponentSolution("class_name", className, conn);
    }
     
    public List getClassFilesForSolution(int solutionID, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT scf.path, scf.class_file, scf.sort_order FROM solution_class_file scf WHERE scf.solution_id = ? ORDER BY scf.sort_order";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, solutionID);
            rs = ps.executeQuery();
            List classFiles = new Vector();
            while (rs.next()) {
                String path = rs.getString(1);
                byte[] clazzBytes = rs.getBytes(2);
                classFiles.add(new RemoteFile(path, clazzBytes));
            }
            return classFiles;
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    public List getParamTypesForComponent(int componentID, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList r = new ArrayList();
        String sql;
        if (DBMS.DB == DBMS.INFORMIX) {
            sql = "SELECT dt.data_type_desc,p.sort_order FROM parameter p JOIN data_type dt ON p.data_type_id=dt.data_type_id " +
                    "WHERE p.component_id = ? ORDER BY p.sort_order";
        } else {
            sql = "SELECT dt.data_type_desc, p.sort_order FROM parameter p, data_type dt " +
                    "WHERE p.component_id = ? AND p.data_type_id = dt.data_type_id ORDER BY p.sort_order";
        }
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, componentID);
            rs = ps.executeQuery();
            while (rs.next()) {
                r.add(rs.getString(1));
            }
            return r;
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    /**
     * Retrieves component solution by specified key.
     *
     * @param keyField Key field for searching.
     * @param keyValue Value of key for searching.
     * @param conn DB connection.
     * @return Solution.
     * @throws SQLException If any DB error occurs.
     */
    private Solution getComponentSolution(String keyField, Object keyValue, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuffer sqlStr = new StringBuffer(256);
            sqlStr.replace(0, sqlStr.length(), "SELECT c.component_id, c.method_name, s.solution_id"
                    + ", s.language_id, s.package, c.class_name, dt.data_type_desc, s.has_check_answer");
            sqlStr.append(" FROM component c"
                    + " LEFT JOIN component_solution_xref cs ON cs.component_id = c.component_id"
                    + " LEFT JOIN solution s ON cs.solution_id = s.solution_id"
                    + " LEFT JOIN data_type dt ON dt.data_type_id = c.result_type_id");
            sqlStr.append(" WHERE cs.primary_solution = 1");
            sqlStr.append(" AND c.").append(keyField).append(" = ?");
            ps = conn.prepareStatement(sqlStr.toString());
            ps.setObject(1, keyValue);
            rs = ps.executeQuery();
            rs.next();
            int componentID = rs.getInt(1);
            String methodName = rs.getString(2);
            int solutionID = rs.getInt(3);
            int languageID = rs.getInt(4);
            String packageName = rs.getString(5);
            String className = rs.getString(6);
            List classFiles = getClassFilesForSolution(solutionID, conn);
            List paramTypes = getParamTypesForComponent(componentID, conn);
            String resultType = rs.getString(7);
            boolean hasCheckAnswer = rs.getBoolean(8);
            Solution solution = new Solution(languageID, packageName, className,
                    methodName, paramTypes, classFiles, resultType, hasCheckAnswer);
            if (log.isDebugEnabled()) {
                log.debug("Returning: " + solution);
            }
            return solution;
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
}
