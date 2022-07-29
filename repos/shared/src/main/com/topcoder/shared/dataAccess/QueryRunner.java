package com.topcoder.shared.dataAccess;

import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import java.sql.*;
import java.util.*;

/**
 *
 * A class which handles the retrieving of data via a provided
 * query, unlike {@link com.topcoder.shared.dataAccess.DataRetriever} which pulls query information
 * from a database.
 *
 * @author  Greg Paul
 * @version $Revision$
 * @see     ResultSetContainer
 */

public class QueryRunner implements DataRetrieverInt {
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private static Logger log = Logger.getLogger(QueryRunner.class);

    /**
     * Constructor that takes a connection object
     * @param conn
     */
    public QueryRunner(Connection conn) {
        this.conn = conn;
    }

    private void closeObject(Object o) {
        if (o == null)
            return;
        try {
            if (o instanceof ResultSet)
                ((ResultSet) o).close();
            else if (o instanceof PreparedStatement)
                ((PreparedStatement) o).close();
            else if (o instanceof Connection)
                ((Connection) o).close();
        } catch (Exception e) {
            try {
                System.out.println("Statistics EJB:  Error closing " + o.getClass());
                System.out.println(e.getMessage());
            } catch (Exception ex) {
            }
        }
    }

    private void closeConnections() {
        closeObject(rs);
        closeObject(ps);
//        closeObject(conn);
        rs = null;
        ps = null;
//        conn = null;
    }

    private void handleException(Exception e, String lastQuery, Map inputs) {
        try {
            System.out.println("Exception caught: " + e.toString());
            System.out.println("The last query run was: ");
            System.out.println(lastQuery);
            System.out.println("Exception details:");
            if (e instanceof SQLException)
                DBMS.printSqlException(true, (SQLException) e);
            else
                e.printStackTrace();
        } catch (Exception ex) {
        }
        closeConnections();
    }

    /**
     * The function which does the actual retrieval of data.  Returns a
     * <tt>Map</tt> in which each key is a <tt>String</tt> representing
     * a query name, and each value is a <tt>ResultSetContainer</tt> containing the
     * data returned by that query.  It has been striped down from what
     * <tt>DataRetriever</tt> can do.  It does not accept intputs, ranking
     * information, sorting etc.  But, <tt>QueryRequest</tt> does support
     * that, so it could be added here.
     *
     * @param      inputs  A map of input data.
     * @throws      Exception If some problem is encountered while executing
     *                              the queries.
     * @return      The data requested by the input.
     */
    public Map<String, ResultSetContainer> executeCommand(Map inputs) throws Exception {
        Iterator queryIterator = null;
        Map.Entry me = null;
        ResultSetContainer rsc = null;
        String queryText = null;
        String queryName = null;
        Map queryMap = null;
        Map<String, ResultSetContainer> resultMap = null;

        if (!inputs.containsKey(DataAccessConstants.QUERY_KEY)) {
            throw new Exception("Missing query");
        }

        try {

            resultMap = new HashMap<String, ResultSetContainer>();
            queryMap = (Map) inputs.get(DataAccessConstants.QUERY_KEY);
            queryIterator = queryMap.entrySet().iterator();
            log.debug("executing: " + queryMap.size() + " queries");
            for (; queryIterator.hasNext();) {
                me = (Map.Entry) queryIterator.next();
                queryText = (String) me.getValue();
                queryName = (String) me.getKey();
                int startRank =inputs.containsKey(queryName+DataAccessConstants.START_RANK)?
                        Integer.parseInt((String)inputs.get(queryName+DataAccessConstants.START_RANK)):1;
                int endRank =inputs.containsKey(queryName+DataAccessConstants.END_RANK)?
                        Integer.parseInt((String)inputs.get(queryName+DataAccessConstants.END_RANK)):Integer.MAX_VALUE;
                log.debug("start: " + startRank + " end: " + endRank);

                ps = conn.prepareStatement(queryText);
                rs = ps.executeQuery();
                rsc = new ResultSetContainer(rs, startRank, endRank, false);
                log.debug("size: " + rsc.size());
                log.debug(queryText);
                rs.close();
                rs = null;
                ps.close();
                ps = null;
                resultMap.put(queryName, rsc);
            }
        } catch (Exception e) {
            handleException(e, queryText, inputs);
            throw new Exception("Error while retrieving query data");
        }

        // Done!
        closeConnections();
        return resultMap;
    }
}

