package com.topcoder.shared.dataAccess.resultSet;

/**
 * This class records all necessary metadata information for a
 * column inside a <tt>ResultSetContainer</tt>.  The specific
 * items recorded are the type of data stored, column name,
 * precision (if applicable), and scale (if applicable). <p>
 *
 * Once constructed, an object of this class is read-only.
 *
 * @author  Dave Pecora
 * @version 1.01, 02/14/2002
 * @see     ResultSetContainer
 */

public class ResultColumn implements java.io.Serializable {
    // Data fields should not be modified once set
    private int columnType, columnPrecision, columnScale;
    private String columnName, sourceColumnType;

    /**
     * Constructs a ResultColumn with the indicated type, name,
     * precision, and scale.
     *
     * @param   colType The type of this column, as defined in
     *                  <tt>java.sql.Types</tt>
     * @param   name    The name of the column
     * @param   precision   The precision of the column
     * @param   scale       The scale of the column
     * @param   sourceType  The type of the column as defined in the data source database
     */
    public ResultColumn(int colType, String name, int precision, int scale, String sourceType) {
        columnType = colType;
        columnPrecision = precision;
        columnScale = scale;
        columnName = name;
        sourceColumnType = sourceType;
    }

    /**
     * Returns the type of this column, as defined in <tt>java.sql.Types</tt>
     * @return  The column type.
     */
    public int getType() {
        return columnType;
    }

    /**
     * Returns the type of this column, as defined in the data source database
     * @return  The column type.
     */
    public String getSourceType() {
        return sourceColumnType;
    }

    /**
     * Returns the precision of this column.
     * @return  The column precision.
     */
    public int getPrecision() {
        return columnPrecision;
    }

    /**
     * Returns the scale of this column.
     * @return  The column scale.
     */
    public int getScale() {
        return columnScale;
    }

    /**
     * Returns the name of this column.
     * @return  The column name.
     */
    public String getName() {
        return columnName;
    }
}

