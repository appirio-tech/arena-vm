package com.topcoder.shared.util.loader;

import com.topcoder.shared.util.logging.Logger;

/**
 * This class represents a query to be executed by the query processing system.
 * We will be using PreparedStatements in the underlying implementation, so we require
 * that the Query object include both the actual query as a string as well
 * as a set of arguments.  Queries should be one of: insert, update or delete.
 *
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 8, 2006
 */
public interface Query {
    static final Logger log = Logger.getLogger(Query.class);
    String getQuery();
    void setQuery(String query);
    Object[] getArgs();
    void setArgs(Object[] args);
    void addArg(Object o);
    void addArg(int i);
    void addArg(long l);
}
