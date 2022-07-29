/*
 * UserTestGroupAssociationDao
 * 
 * Created 04/25/2006
 */
package com.topcoder.server.ejb.MPSQASServices.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.server.ejb.TestServices.longtest.model.LongTestGroup;
import com.topcoder.shared.util.DBMS;

/**
 * @author Diego Belfer (mural)
 * @version $Id: UserTestGroupAssociationDao.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class UserTestGroupAssociationDao {
    
    /**
     * Gets the user id associated with the test group and 
     * removes the association between them  
     * 
     * @param testGroupId Id of the test group
     * @param cnn Connection to use for database access
     * 
     * @return Id of the User, -1 if no association is found for the test group
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */
    public int findUserForTestGroupAndRemove(int testGroupId, Connection cnn) throws SQLException {
        int userId = -1;
        userId = findUserIdForTestGroup(testGroupId, cnn);
        deleteAssociationForTestGroup(testGroupId, cnn);
        return userId;
    }

    /**
     * Gets the all test group ids associated with the user and
     * removes the association between them
     * 
     * @param userId Id of the user
     * @param cnn Connection to use for database access
     * 
     * @return a List with all the test group ids associated with the user
     *         not null
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */
    public List findTestGroupIdsForUserAndRemove(int userId, Connection cnn) throws SQLException {
        List ids = findTestGroupIdsForUser(userId, cnn);
        deleteAssociationForUser(userId, cnn);
        return ids;
    }

    
    /**
     * Creates an association between the user and the test group
     * 
     * @param userId Id of the user to associate
     * @param testGroupId Id of the test group to associate
     * @param cnn Connection to use for database access
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */
    public void associateUserAndTestGroup(int userId, int testGroupId,
            Connection cnn) throws SQLException {
        
        String sqlIns = "INSERT INTO mpsqas_user_ltg_xref (user_id, ltg_id) VALUES (?, ?)";
        PreparedStatement ps = cnn.prepareStatement(sqlIns);
        try {
            ps.setInt(1, userId);
            ps.setInt(2, testGroupId);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }
    
    /**
     * Removes the all existent associations for the test group  
     * 
     * @param testGroupId Id of the test group
     * @param cnn Connection to use for database access
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */ 
    public void deleteAssociationForTestGroup(int testGroupId, Connection cnn)
            throws SQLException {
        
        String sqlIns = "DELETE FROM mpsqas_user_ltg_xref WHERE ltg_id = ?";
        PreparedStatement ps = cnn.prepareStatement(sqlIns);
        try {
            ps.setInt(1, testGroupId);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }

    /**
     * Removes the all existent associations for the user  
     * 
     * @param userId Id of the user
     * @param cnn Connection to use for database access
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */ 
    public void deleteAssociationForUser(int userId, Connection cnn) throws SQLException {
        String sqlIns = "DELETE FROM mpsqas_user_ltg_xref WHERE user_id = ?";
        PreparedStatement ps = cnn.prepareStatement(sqlIns);
        try {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
    }
    
    /**
     * Gets the user id associated with the test group
     * 
     * @param testGroupId Id of the test group
     * @param cnn Connection to use for database access
     * 
     * @return Id of the User, -1 if no association is found for the test group
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */
    public int findUserIdForTestGroup(int testGroupId, Connection cnn) throws SQLException {
        String sqlStr = "SELECT user_id FROM mpsqas_user_ltg_xref WHERE ltg_id = ?";
        ResultSet rs = null;
        PreparedStatement ps = cnn.prepareStatement(sqlStr);
        try {
            ps.setInt(1, testGroupId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
    /**
     * Gets the all test group ids associated with the user 
     * 
     * @param userId Id of the user
     * @param cnn Connection to use for database access
     * 
     * @return a List with all the test group ids associated with the user
     *         not null
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */
    public List findTestGroupIdsForUser(int userId, Connection cnn) throws SQLException {
        String sqlStr = "SELECT ltg_id FROM mpsqas_user_ltg_xref WHERE user_id = ?";
        ResultSet rs = null;
        PreparedStatement ps = cnn.prepareStatement(sqlStr);
        try {
            List ids = new ArrayList();
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(new Integer(rs.getInt(1)));
            }
            return ids;
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
    /**
     * Count the number of test groups associated with the user
     * 
     * @param userId If of the User
     * @param cnn Connection to use
     * 
     * @return the number of test groups associated with the use
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */
    public int findTestGroupCountForUser(int userId, Connection cnn) throws SQLException {
        String sqlStr = "SELECT count(ltg_id) FROM mpsqas_user_ltg_xref WHERE user_id = ?";
        ResultSet rs = null;
        PreparedStatement ps = cnn.prepareStatement(sqlStr);
        try {
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
    

    /**
     * Count the number of pending test groups associated with the user
     * 
     * @param userId If of the User
     * @param cnn Connection to use
     * 
     * @return the number of pending test groups associated with the use
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */
    public int findPendingTestGroupCountForUser(int userId, Connection cnn) throws SQLException {
        String sqlStr = "SELECT count(mp.ltg_id) " +
                        "   FROM mpsqas_user_ltg_xref mp INNER JOIN long_test_group lt ON lt.ltg_id = mp.ltg_id" +
                        "   WHERE mp.user_id = ? AND lt.status_id = ? AND lt.ltg_id = mp.ltg_id";
        ResultSet rs = null;
        PreparedStatement ps = cnn.prepareStatement(sqlStr);
        try {
            ps.setInt(1, userId);
            ps.setInt(2, LongTestGroup.STATUS_PENDING);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
    
    /**
     * Returns the last finished test group for the given user
     * 
     * @param userId Id of the User
     * @param cnn Connection to use
     * 
     * @return The id of the last finished test group, -1 if not found
     * 
     * @throws SQLException If a SQLException is thrown when accessing database
     */
    public int findLastTestGroupFinishedForUser(int userId, Connection cnn) throws SQLException {
        String sqlStr = "SELECT FIRST 1 mp.ltg_id " +
                        "   FROM mpsqas_user_ltg_xref mp INNER JOIN long_test_group lt ON lt.ltg_id = mp.ltg_id" +
                        "   WHERE mp.user_id = ? AND lt.status_id IN (?,?)" +
                        "   ORDER BY lt.status_date DESC, mp.ltg_id DESC";
        ResultSet rs = null;
        PreparedStatement ps = cnn.prepareStatement(sqlStr);
        try {
            ps.setInt(1, userId);
            ps.setInt(2, LongTestGroup.STATUS_COMPLETED);
            ps.setInt(3, LongTestGroup.STATUS_CANCELLED);            
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }
}
