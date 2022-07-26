package com.topcoder.shared.dataAccess;

import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;

import java.util.Map;

/**
 * This interface needs to be implemented for a data retriever.  Data
 * retrievers should take in request and produce data stored in a Map
 * as a response.
 *
 * @author Greg Paul
 * @version $Revision$
 */

public interface DataRetrieverInt {
    /**
     * Uses a it's input to retrieve data which it returns.
     * @param map
     * @return
     * @throws Exception
     */
    public Map<String, ResultSetContainer> executeCommand(Map map) throws Exception;
}

