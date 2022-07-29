package com.topcoder.shared.dataAccess;

import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;

import java.util.Map;

/**
 * This interface needs to be implemented for any data-accessors.  A class that implements <tt>DataAccessInt</tt>
 * is intended to be a layer between the layer containing a <tt>RequestInt</tt> and the actual data layer would
 * is likely a database.  Classes that implement <tt>DataAccessInt</tt> should know how to connect to a data source
 * and retrieve data.
 *
 * @author tbone
 * @version $Revision$
 */

public interface DataAccessInt {
    /**
     * Takes a request and produces a Map filled with the requested data.
     * @param request
     * @return the requested data
     * @throws Exception
     */
    Map<String, ResultSetContainer> getData(RequestInt request) throws Exception;


}

