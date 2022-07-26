/*
 * GenericCounter
 * 
 * Created Jul 10, 2007
 */
package com.topcoder.server.ejb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.topcoder.shared.util.DBMS;

/**
 * @author Diego Belfer (mural)
 * @version $Id: GenericCounter.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class GenericCounter {
    private int clientId;
    
    private GenericCounter(int clientId) {
        this.clientId = clientId;
    }
    
    public static GenericCounter create(String name, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT client_id FROM generic_counter_client WHERE name = ?";            
            ps = cnn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            rs.next();
            return new GenericCounter(rs.getInt(1));
        } finally {
            DBMS.close(ps, rs);
        }
    }

    public void initCounter(String counterId, long initialValue, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE {+AVOID_FULL(generic_counter)} generic_counter " +
                         "   SET value = ?, last_update_id = NULL, last_update = CURRENT " +
                         "   WHERE client_id = ? AND counter_id = ?";
            ps = cnn.prepareStatement(sql);
            ps.setLong(1, initialValue);
            ps.setInt(2, clientId);
            ps.setString(3, counterId);
            if (ps.executeUpdate() == 0) {
                ps.close();
                ps = cnn.prepareStatement("INSERT INTO generic_counter (client_id, counter_id, value, last_update) VALUES(?, ?, ?, CURRENT)");
                ps.setInt(1, clientId);
                ps.setString(2, counterId);
                ps.setLong(3, initialValue);
                ps.executeUpdate();
            }
        } finally {
            DBMS.close(ps);
        }
    }
    
    
    public int removeOldEntries(long periodMs, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sql = "DELETE {+AVOID_FULL(generic_counter)} FROM generic_counter WHERE client_id = ? AND last_update < ?";
            ps = cnn.prepareStatement(sql);
            ps.setInt(1, clientId);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis() - periodMs));
            return ps.executeUpdate();
        } finally {
            DBMS.close(ps);
        }
        
    }

    public void clearCounter(String id, Connection cnn) throws SQLException {
        initCounter(id, 0, cnn);
    }
    
    public boolean decrementCounter(String counterId,  long value, String updateId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE {+AVOID_FULL(generic_counter)} generic_counter " +
                         "  SET value = value - ?, last_update_id = ?, last_update = CURRENT " +
                         "  WHERE client_id = ? AND counter_id = ? AND value >= ?";            
            ps = cnn.prepareStatement(sql);
            ps.setLong(1, value);
            ps.setString(2, updateId);
            ps.setInt(3, clientId);
            ps.setString(4, counterId);
            ps.setLong(5, value);
            if (ps.executeUpdate() != 1) {
                throw new IllegalStateException("The counter was not updated, either id does not exist or value is already 0");
            }
            return wasLastUpdate(counterId, updateId, cnn);
        } finally {
            DBMS.close(ps);
        }
    }
    
    public void incrementCounter(String counterId,  long value, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE {+AVOID_FULL(generic_counter)} generic_counter " +
                         "  SET value = value + ?, last_update = CURRENT " +
                         "  WHERE client_id = ? AND counter_id = ?";            
            ps = cnn.prepareStatement(sql);
            ps.setLong(1, value);
            ps.setInt(2, clientId);
            ps.setString(3, counterId);
            if (ps.executeUpdate() == 0) {
                ps.close();
                ps = cnn.prepareStatement("INSERT INTO generic_counter (client_id, counter_id, value, last_update) VALUES(?, ?, ?, CURRENT)");
                ps.setInt(1, clientId);
                ps.setString(2, counterId);
                ps.setLong(3, value);
                ps.executeUpdate();
            }
        } finally {
            DBMS.close(ps);
        }
    }
    
    public boolean wasLastUpdate(String counterId,  String updateId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT {+AVOID_FULL(generic_counter)} 1 FROM generic_counter WHERE client_id = ? AND counter_id = ? AND value == 0 AND last_update_id = ?";            
            ps = cnn.prepareStatement(sql);
            ps.setInt(1, clientId);
            ps.setString(2, counterId);
            ps.setString(3, updateId);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBMS.close(ps, rs);
        }
    }
}
