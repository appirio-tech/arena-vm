/*
 * RoundDao
 * 
 * Created 10/23/2006
 */
package com.topcoder.server.ejb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.netCommon.contest.round.RoundCustomPropertiesImpl;
import com.topcoder.server.util.DBUtils;
import com.topcoder.shared.util.DBMS;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Data Access Object (DAO) class for querying/accessing  
 * Round table objects.
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
 * @version $Id: RoundDao.java 68217 2008-01-28 13:11:24Z mural $
 */
public class RoundDao {
    private static final int ROUND_PROPERTY_CLASSROOM_ID = 1;
    private static final int ROUND_PROPERTY_PER_USER_CODING_TIME = 2;
    private static final int ROUND_PROPERTY_SHOW_ALL_SCORES = 3;
    private static final int ROUND_PROPERTY_SCORE_TYPES = 4;
    private static final int ROUND_PROPERTY_CODING_LENGTH_OVERRIDE = 5;
    
    public int getContestIdOfRound(int roundId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT contest_id FROM round WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs  = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    /**
     * Returns a list containing the ids of all languages which were specifically assigned to the round.<p>
     * 
     * @param roundId The id of the round
     * @param conn The connection to use
     * @return a list containing ids of the assigned languages, <code>null</code> if none was assigned.
     *  
     * @throws SQLException  if an SQLException was thrown during the process
     */
    public List getLanguageIdsOfRound(int roundId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("SELECT language_id FROM round_language WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs  = ps.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                List result = new ArrayList(5);
                do {
                    result.add(new Integer(rs.getInt(1)));
                } while (rs.next());
                return result;
            }
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
    
    public int getRoundTypeId(long rid, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        sql = "SELECT round_type_id FROM round WHERE round_id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setLong(1, rid);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
    
   
    private static final String ROUND_SEGMENT_ACTIVE = 
        "SELECT current, start_time, end_time " +  
        "   from round_segment rs " +
        "   WHERE rs.round_id = ? AND rs.segment_id = ? ";
        
    
    public Timestamp[] getSegmentInterval(int roundId, int segmentType, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement(ROUND_SEGMENT_ACTIVE);
            ps.setInt(1, roundId);
            ps.setInt(2, segmentType);
            rs = ps.executeQuery();
            rs.next();
            return new Timestamp[] {rs.getTimestamp(1), rs.getTimestamp(2), rs.getTimestamp(3)};
        } finally {
            DBMS.close(null, ps, rs);
        }
    }


    public boolean isInvitational(int roundId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement("SELECT invitational FROM round WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) != ContestConstants.NOT_INVITATIONAL;
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    public int getInvitationType(int roundId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement("SELECT invitational FROM round WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            if (!rs.next()) return ContestConstants.NOT_INVITATIONAL;
            return rs.getInt(1);
        } finally {
            DBMS.close(null, ps, rs);
        }
    }

    public boolean isInvited(int roundId, int userId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean inList;
        int type = getInvitationType(roundId, cnn);
        try {
            ps = cnn.prepareStatement("SELECT coder_id, seed FROM invite_list WHERE round_id = ? AND coder_id = ?");
            ps.setInt(1, roundId);
            ps.setInt(2, userId);
            rs = ps.executeQuery();
            inList = rs.next();
        } finally {
            DBMS.close(ps, rs);
        }

        switch (type) {
        case ContestConstants.NOT_INVITATIONAL:
            // If the round is not invitational, always return true.
            return true;
        case ContestConstants.NORMAL_INVITATIONAL:
            return inList;
        case ContestConstants.NEGATE_INVITATIONAL:
            return !inList;
        default:
            throw new SQLException("Illegal invitational type!");
        }
    }


    public int getRegistrationLimit(int roundId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement("SELECT registration_limit FROM round WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } finally {
            DBMS.close(ps, rs);
        }
    }
    
    public int getRegistrationCount(int roundId, Connection cnn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cnn.prepareStatement("SELECT count(*) FROM round_registration WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    /**
     * This method check if the coder is registered for the given round.
     * 
     * @param coderId The coder id 
     * @param roundId The round id
     * @return true if the coder is registered for the given round, false otherwise
     * 
     * @throws SQLException if an SQLException was thrown during the process
     */
    public boolean isCoderRegistered(int userId, int roundId, Connection c) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = c.prepareStatement("SELECT coder_id FROM round_registration WHERE round_id = ? AND coder_id = ?");
            ps.setInt(1, roundId);
            ps.setInt(2, userId);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBMS.close(ps, rs);
        }
    }
    
    public boolean deleteRoundRegistration(int userId, int roundId, Connection c) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = c.prepareStatement("DELETE FROM round_registration WHERE round_id = ? AND coder_id = ? ");
            ps.setInt(1, roundId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    public int getSegmentPhaseStatus(int roundId, int segmentType, Connection cnn) throws SQLException {
        Timestamp[] segment = getSegmentInterval(roundId, segmentType, cnn);
        if (segment[0].before(segment[1])) {
            return -1;
        }
        if (segment[0].before(segment[2])) {
            return 0;
        }
        return 1;
    }
    
    public RoundCustomPropertiesImpl getRoundDynamicProperties(int roundId, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        RoundCustomPropertiesImpl props = null;
        try {
            ps = conn.prepareStatement("SELECT round_property_type_id, int_value, string_value FROM round_property WHERE round_id = ?");
            ps.setInt(1, roundId);
            rs = ps.executeQuery();
            while (rs.next()) {
                
                switch (rs.getInt(1)) {
                    case ROUND_PROPERTY_CLASSROOM_ID : //classroom
                        continue;
                    case ROUND_PROPERTY_PER_USER_CODING_TIME : //coding phase per coder time
                        Long seconds = DBUtils.getLong(rs, 2);
                        if (seconds != null) {
                            props = ensureProps(props);
                            props.setPerUserCodingTime(new Long(seconds.longValue()*1000));
                        }
                        break;
                    case ROUND_PROPERTY_SHOW_ALL_SCORES : //Show Scores of All Coders
                        Integer value = DBUtils.getInt(rs, 2);
                        if (value != null) {
                            props = ensureProps(props);
                            props.setShowScoresOfOtherCoders(Boolean.valueOf(value.intValue()==1));
                        }
                        break;
                    case ROUND_PROPERTY_SCORE_TYPES : //Score Type
                        props = ensureProps(props);
                        props.setAllowedScoreTypesToShow(new ResultDisplayType[]{ResultDisplayType.get(rs.getInt(2))});
                        break;
                    case ROUND_PROPERTY_CODING_LENGTH_OVERRIDE : //Coding Length override
                        Long ms = DBUtils.getLong(rs, 2);
                        if (ms != null) {
                            props = ensureProps(props);
                            props.setCodingLengthOverride(ms);
                        }
                        break;
                }
            }
            return props;
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
    
    private RoundCustomPropertiesImpl ensureProps(RoundCustomPropertiesImpl props) {
        if (props == null) {
            props = new RoundCustomPropertiesImpl();
        }
        return props;
    }

    public void insertRoundPropertyCodingLenghtOverride(int roundId, long codingLength, Connection c) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement("INSERT INTO round_property (round_id, round_property_type_id, int_value) VALUES (?,?,?)");
            ps.setInt(1, roundId);
            ps.setInt(2, ROUND_PROPERTY_CODING_LENGTH_OVERRIDE);
            ps.setInt(3, (int) codingLength);
            ps.execute();
        } finally {
            DBMS.close(ps);
        }
        
    }
}
