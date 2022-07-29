package com.topcoder.shared.dataAccess;

import com.topcoder.shared.util.logging.Logger;

import java.sql.Connection;

/**
 * This bean processes a {@link com.topcoder.shared.dataAccess.QueryRequest} and returns the data.
 *
 * @author  Greg Paul
 * @version $Revision$
 * @see     QueryRequest
 */
public class QueryDataAccess extends DataAccess {
    private static Logger log = Logger.getLogger(QueryDataAccess.class);
    /**
     * Default Constructor
     */
    public QueryDataAccess() {
    }

    /**
     * Construtor that takes a data source to be used.
     * @param dataSourceName
     */
    public QueryDataAccess(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    protected DataRetrieverInt getDataRetriever(Connection conn) {
        return new QueryRunner(conn);
    }


}

