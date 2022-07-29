package com.topcoder.shared.dataAccess.resultSet;

/**
 * User: dok
 * Date: Sep 8, 2004
 * Time: 2:10:05 AM
 */
public interface ResultFilter {

    boolean include(ResultSetContainer.ResultSetRow rsr);

}
