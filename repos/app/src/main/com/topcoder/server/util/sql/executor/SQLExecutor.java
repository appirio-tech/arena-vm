package com.topcoder.server.util.sql.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public final class SQLExecutor {

    private SQLExecutor() {
    }

    private static final Item[] NO_PARAMS = new Item[0];

    private static void setParameters(PreparedStatement preparedStatement, Item[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            params[i].set(preparedStatement, i + 1);
        }
    }

    private static Column[] getColumns(ResultSetMetaData metaData, int columnCount, JdbcDriverInfo jdbcDriverInfo)
            throws SQLException {
        Column[] columns = new Column[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columns[i] = Column.getColumn(metaData, i + 1, jdbcDriverInfo);
        }
        return columns;
    }

    public static SimpleResultSet executeQuery(DataSource dataSource, String sql) throws SQLException {
        return executeQuery(dataSource, sql, NO_PARAMS);
    }

    public static SimpleResultSet executeQuery(DataSource dataSource, String sql, Item[] params) throws SQLException {
        Connection connection = dataSource.getConnection();
        SimpleResultSet resultSet;
        try {
            resultSet = executeQuery(connection, sql, params);
        } finally {
            connection.close();
        }
        return resultSet;
    }

    public static SimpleResultSet executeQuery(Connection connection, String sql, Item[] params)
            throws SQLException {
        SimpleResultSet resultSet;
        JdbcDriverInfo jdbcDriverInfo = new JdbcDriverInfo(connection);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = executeQuery(preparedStatement, params, jdbcDriverInfo);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e) {
                }
            }
        }
        return resultSet;
    }

    private static SimpleResultSet executeQuery(PreparedStatement preparedStatement, Item[] params, JdbcDriverInfo jdbcDriverInfo)
            throws SQLException {
        setParameters(preparedStatement, params);
        ResultSet resultSet = null;
        SimpleResultSet simpleResultSet;
        try {
            resultSet = preparedStatement.executeQuery();
            simpleResultSet = executeQuery(resultSet, jdbcDriverInfo);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                }
            }
        }
        return simpleResultSet;
    }

    private static SimpleResultSet executeQuery(ResultSet resultSet, JdbcDriverInfo jdbcDriverInfo) throws SQLException {
        List resultSetRowList = new ArrayList();
        int columnCount;
        ColumnNameMap columnNameMap;
        Column[] columns;
        {
            ResultSetMetaData metaData = resultSet.getMetaData();
            columnCount = metaData.getColumnCount();
            columnNameMap = new ColumnNameMap(metaData, columnCount);
            columns = getColumns(metaData, columnCount, jdbcDriverInfo);
        }
        while (resultSet.next()) {
            Item[] items = new Item[columnCount];
            for (int i = 0; i < columnCount; i++) {
                items[i] = columns[i].getItem(resultSet);
            }
            ResultSetRow row = new ResultSetRow(items, columnNameMap);
            resultSetRowList.add(row);
        }
        return new SimpleResultSet(resultSetRowList);
    }

    public static int executeUpdate(DataSource dataSource, String sql) throws SQLException {
        return executeUpdate(dataSource, sql, NO_PARAMS);
    }

    public static int executeUpdate(DataSource dataSource, String sql, Item[] params) throws SQLException {
        Connection connection = dataSource.getConnection();
        int numRows;
        try {
            numRows = executeUpdate(connection, sql, params);
        } finally {
            connection.close();
        }
        return numRows;
    }

    private static int executeUpdate(Connection connection, String sql, Item[] params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        int numRows;
        try {
            numRows = executeUpdate(preparedStatement, params);
        } finally {
            preparedStatement.close();
        }
        return numRows;
    }

    private static int executeUpdate(PreparedStatement preparedStatement, Item[] params) throws SQLException {
        setParameters(preparedStatement, params);
        int numRows = preparedStatement.executeUpdate();
        return numRows;
    }

}
