package com.topcoder.shared.dataAccess.resultSet;

import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer.ResultSetRow;

/**
 * Represents a column in a ResultSetRow that is calculated using other fields from the same row.
 * 
 * @author Cucu
 */
public abstract class CalculatedColumn extends ResultColumn {
    
    public CalculatedColumn(int colType, String name, int precision, int scale,  String sourceType) {
        super(colType, name, precision, scale, sourceType);
    }

    /**
     * Inheriting classes must implement this method to calculate the value for the column using
     * values from other columns in the same row, and return a TCResultItem with the value.
     * 
     * @param rsr
     * @return
     */
    public abstract TCResultItem calculate(ResultSetRow rsr);
    
}
