/*
 * UserDao
 * 
 * Created 10/23/2006
 */
package com.topcoder.server.ejb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.shared.util.DBMS;

/**
 * Data Access Object (DAO) class for querying/accessing  
 * User table objects.
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
 * @version $Id: UserDao.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class UserDao {
    public String[] getHandleAndEmail(int coderId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT u.handle, e.address FROM user u, email e WHERE e.user_id = u.user_id and e.primary_ind = 1 and u.user_id = ?");
            ps.setInt(1, coderId);
            rs  = ps.executeQuery();
            rs.next();
            String handle = rs.getString(1);
            String email = rs.getString(2);
            return new String[]{handle, email};
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    public boolean isAdminUser(int coderId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT FIRST 1 user_id FROM group_user " +
                                       "       WHERE user_id = ? AND " +
                                       "             (group_id = "+ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID+" OR group_id = "+ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID+")");
            ps.setInt(1, coderId);
            rs  = ps.executeQuery();
            return rs.next();
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
}
