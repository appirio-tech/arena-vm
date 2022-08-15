package com.topcoder.server.ejb.DBServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.topcoder.server.common.WeakestLinkCoder;
import com.topcoder.server.common.WeakestLinkData;
import com.topcoder.server.common.WeakestLinkTeam;
import com.topcoder.shared.util.logging.Logger;

final class WeakestLinkCoderTable {

    private static final Logger log = Logger.getLogger(WeakestLinkCoderTable.class);

    static WeakestLinkData loadWeakestLinkData(Connection connection, int roundId) throws SQLException {
        String sql = "SELECT t.team_id, c.coder_id, t.team_name, c.points_sum, c.qual_points, c.room_no, c.badge_id " +
                "FROM wl_coder c, wl_team t WHERE c.round_id = ? AND c.team_id = t.team_id";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, roundId);
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                Collection collection = new ArrayList();
                while (resultSet.next()) {
                    int teamId = resultSet.getInt(1);
                    int coderId = resultSet.getInt(2);
                    String teamName = resultSet.getString(3);
                    double pointsSum = resultSet.getDouble(4);
                    double qualPoints = resultSet.getDouble(5);
                    int roomNo = resultSet.getInt(6);
                    String badgeId = resultSet.getString(7);
                    collection.add(new Entry(teamId, coderId, teamName, pointsSum, qualPoints, roomNo, badgeId));
                }
                double prizeThreshold = getPrizeThreshold(connection, roundId);
                return getData(collection, roundId, prizeThreshold);
            } finally {
                resultSet.close();
            }
        } finally {
            preparedStatement.close();
        }
    }

    static WeakestLinkTeam getWeakestLinkTeam(Connection connection, int teamId, int roundId) throws SQLException {
        String sql = "SELECT t.team_id, c.coder_id, t.team_name FROM wl_coder c, wl_team t " +
                "WHERE t.team_id = ? AND c.team_id = ? AND c.round_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, teamId);
            preparedStatement.setInt(3, roundId);
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                Collection collection = new ArrayList();
                while (resultSet.next()) {
                    int coderId = resultSet.getInt(2);
                    String teamName = resultSet.getString(3);
                    collection.add(new Entry(teamId, coderId, teamName));
                }
                return getSingleTeamData(collection, roundId);
            } finally {
                resultSet.close();
            }
        } finally {
            preparedStatement.close();
        }
    }

    private static final class Entry implements Comparable {

        private final int teamId;
        private final int coderId;
        private final String teamName;
        private final double pointsSum;
        private final double qualPoints;
        private final int roomNo;
        private final String badgeId;

        private Entry(int teamId, int coderId, String teamName) {
            this(teamId, coderId, teamName, 0, 0, 1, null);
        }

        private Entry(int teamId, int coderId, String teamName, double pointsSum, double qualPoints, int roomNo, String badgeId) {
            this.teamId = teamId;
            this.coderId = coderId;
            this.teamName = teamName;
            this.pointsSum = pointsSum;
            this.qualPoints = qualPoints;
            this.roomNo = roomNo;
            this.badgeId = badgeId;
        }

        private int getTeamId() {
            return teamId;
        }

        private int getCoderId() {
            return coderId;
        }

        private String getTeamName() {
            return teamName;
        }

        private double getPointsSum() {
            return pointsSum;
        }

        private double getQualPoints() {
            return qualPoints;
        }

        private int getRoomNo() {
            return roomNo;
        }

        private String getBadgeId() {
            return badgeId;
        }

        public int compareTo(Object obj) {
            return teamId - ((Entry) obj).teamId;
        }

        public boolean equals(Object obj) {
            return compareTo(obj) == 0;
        }

        public int hashCode() {
            return teamId;
        }

    }

    private static Set getTeamsSet(Collection collection) {
        Set set = new HashSet();
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Entry entry = (Entry) iterator.next();
            set.add(new Entry(entry.getTeamId(), -1, entry.getTeamName()));
        }
        return set;
    }

    private static WeakestLinkData getData(Collection collection, int roundId, double prizeThreshold) {
        if (collection.isEmpty()) {
            return null;
        }
        List list = new ArrayList(getTeamsSet(collection));
        Collections.sort(list);
        int numTeams = list.size();
        WeakestLinkTeam[] teams = new WeakestLinkTeam[numTeams];
        for (int i = 0; i < numTeams; i++) {
            Entry entry = (Entry) list.get(i);
            int teamId = entry.getTeamId();
            String teamName = entry.getTeamName();
            teams[i] = new WeakestLinkTeam(teamId, teamName, getCoders(teamId, collection), roundId);
        }
        return new WeakestLinkData(teams, prizeThreshold);
    }

    private static WeakestLinkTeam getSingleTeamData(Collection collection, int roundId) throws SQLException {
        List list = new ArrayList(getTeamsSet(collection));
        Collections.sort(list);
        int numTeams = list.size();
        if (numTeams == 0) {
            // Team ID not found in database.
            return null;
        }
        if (numTeams > 1) {
            throw new SQLException("Retrieved invalid number of teams: " + numTeams);
        }
        Entry entry = (Entry) list.get(0);
        int teamId = entry.getTeamId();
        String teamName = entry.getTeamName();
        return new WeakestLinkTeam(teamId, teamName, getCoders(teamId, collection), roundId);
    }

    /*
    private static int[] toIntArray(Collection collection) {
        int[] r=new int[collection.size()];
        int i=0;
        for (Iterator iterator=collection.iterator(); iterator.hasNext();) {
            Integer integer=(Integer) iterator.next();
            r[i++]=integer.intValue();
        }
        return r;
    }
    */

    private static WeakestLinkCoder[] toCoderArray(Collection collection) {
        WeakestLinkCoder[] result = new WeakestLinkCoder[collection.size()];
        int i = 0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            WeakestLinkCoder coder = (WeakestLinkCoder) iterator.next();
            result[i++] = coder;
        }
        log.debug("Found " + i + " coders");
        return result;
    }

    /*
    private static int[] getCoderIds(int teamId, Collection collection) {
        Collection ids=new ArrayList();
        for (Iterator iterator=collection.iterator(); iterator.hasNext();) {
            Entry entry=(Entry) iterator.next();
            if (teamId == entry.getTeamId()) {
                ids.add(new Integer(entry.getCoderId()));
            }
        }
        return toIntArray(ids);
    }
    */

    private static WeakestLinkCoder[] getCoders(int teamId, Collection collection) {
        Collection coders = new ArrayList();
        log.debug("Getting coders for team " + teamId);
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Entry entry = (Entry) iterator.next();
            log.debug("Considering entry: teamId = " + entry.getTeamId() + " coderId = " + entry.getCoderId());
            if (teamId == entry.getTeamId()) {
                int coderId = entry.getCoderId();
                double pointsSum = entry.getPointsSum();
                double qualPoints = entry.getQualPoints();
                int roomNo = entry.getRoomNo();
                String badgeId = entry.getBadgeId();
                WeakestLinkCoder coder = new WeakestLinkCoder(coderId, pointsSum, qualPoints, roomNo, badgeId);
                coders.add(coder);
            }
        }
        return toCoderArray(coders);
    }

    static void storeWeakestLinkData(Connection connection, WeakestLinkData weakestLinkData, int targetRoundId)
            throws SQLException {
        WeakestLinkTeam[] teams = weakestLinkData.getTeams();
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            int victimId = team.getVictimId();
            int teamId = team.getTeamId();
            WeakestLinkTeam.Entry[] entries = team.getEntries();
            Arrays.sort(entries);
            for (int j = 0; j < entries.length; j++) {
                WeakestLinkTeam.Entry entry = entries[j];
                WeakestLinkCoder coder = entry.getWeakestLinkCoder();
                int coderId = coder.getCoderId();
                if (coderId == victimId) {
                    if (j != entries.length - 1) {
                        throw new RuntimeException("j=" + j);
                    }
                    continue;
                }
                double qualPoints = coder.getQualPoints();
                double pointsSum = entry.getNewPointsSum();
                int roomNo = j + 1;
                String badgeId = coder.getBadgeId();
                storeCoder(connection, targetRoundId, coderId, teamId, pointsSum, qualPoints, roomNo, badgeId);
            }
        }
    }

    private static void storeCoder(Connection connection, int targetRoundId, int coderId, int teamId, double pointsSum,
            double qualPoints, int roomNo, String badgeId) throws SQLException {
        String sql = "INSERT INTO wl_coder (round_id, coder_id, team_id, points_sum, qual_points, room_no, badge_id) " +
                "VALUES (       ?,        ?,       ?,          ?,           ?,       ?,        ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, targetRoundId);
            preparedStatement.setInt(2, coderId);
            preparedStatement.setInt(3, teamId);
            preparedStatement.setDouble(4, pointsSum);
            preparedStatement.setDouble(5, qualPoints);
            preparedStatement.setInt(6, roomNo);
            preparedStatement.setString(7, badgeId);
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount != 1) {
                throw new SQLException("rowCount=" + rowCount);
            }
        } finally {
            preparedStatement.close();
        }
    }

    static void storeBadgeId(Connection connection, int roundId, int coderId, String badgeId) throws SQLException {
        String sql = "UPDATE wl_coder SET badge_id=? WHERE round_id=? AND coder_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setString(1, badgeId);
            preparedStatement.setInt(2, roundId);
            preparedStatement.setInt(3, coderId);
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount != 1) {
                throw new SQLException("rowCount=" + rowCount);
            }
        } finally {
            preparedStatement.close();
        }
    }

    private static void assertTrue(boolean b) {
        if (!b) {
            throw new RuntimeException();
        }
    }

    private static double getPrizeThreshold(Connection connection, int roundId) throws SQLException {
        String sql = "SELECT prize_threshold FROM wl_round WHERE round_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, roundId);
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                boolean next = resultSet.next();
                if (!next) {
                    return -1;
                }
                double prizeThreshold = resultSet.getDouble(1);
                assertTrue(!resultSet.next());
                return prizeThreshold;
            } finally {
                resultSet.close();
            }
        } finally {
            preparedStatement.close();
        }
    }

}
