package com.topcoder.server.util.sql.executor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

abstract class Column {

    private final int column;

    Column(int column) {
        this.column = column;
    }

    final int getColumn() {
        return column;
    }

    abstract Item getItem(ResultSet resultSet) throws SQLException;

    private static Column getDecimalColumn(ResultSetMetaData metaData, int column, JdbcDriverInfo jdbcDriverInfo)
            throws SQLException {
        Column resultSetColumn;
        int scale = metaData.getScale(column);
        scale = jdbcDriverInfo.getScale(scale);
        if (scale == 0) {
            int precision = metaData.getPrecision(column);
            if (1 <= precision && precision <= 2) {
                resultSetColumn = new ByteColumn(column);
            } else if (3 <= precision && precision <= 4) {
                resultSetColumn = new ShortColumn(column);
            } else if (5 <= precision && precision <= 9) {
                resultSetColumn = new IntColumn(column);
            } else if (10 <= precision && precision <= 19) {
                resultSetColumn = new LongColumn(column);
            } else {
                throw new UnsupportedOperationException("not implemented, DECIMAL, scale: " + scale + ", precision: " + precision +
                        ", column name: " + metaData.getColumnName(column));
            }
        } else {
            System.out.println("scale = " + scale + " " + metaData.getColumnName(column) + " " + metaData.getPrecision(column));
            resultSetColumn = new DoubleColumn(column);
        }
        return resultSetColumn;
    }

    static Column getColumn(ResultSetMetaData metaData, int column, JdbcDriverInfo jdbcDriverInfo) throws SQLException {
        Column resultSetColumn;
        int columnType = metaData.getColumnType(column);
        switch (columnType) {
        case Types.DECIMAL:
            resultSetColumn = getDecimalColumn(metaData, column, jdbcDriverInfo);
            break;
        case Types.TINYINT:
            resultSetColumn = new ByteColumn(column);
            break;
        case Types.SMALLINT:
            resultSetColumn = new ShortColumn(column);
            break;
        case Types.INTEGER:
            resultSetColumn = new IntColumn(column);
            break;
        case Types.BIGINT:
            resultSetColumn = new LongColumn(column);
            break;
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
            resultSetColumn = new StringColumn(column);
            break;
        case Types.CHAR:
            resultSetColumn = new CharColumn(column);
            break;
        case Types.TIMESTAMP:
            resultSetColumn = new TimestampColumn(column);
            break;
        case Types.LONGVARBINARY:
            resultSetColumn = new ByteArrayColumn(column);
            break;
        default:
            throw new UnsupportedOperationException("not implemented, column type: " + columnType);
        }
        return resultSetColumn;
    }

}
