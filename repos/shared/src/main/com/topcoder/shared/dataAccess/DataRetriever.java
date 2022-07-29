package com.topcoder.shared.dataAccess;

import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.StringUtil;
import com.topcoder.shared.util.logging.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Retrieves data from the database.<p>
 * <p/>
 * Rerieval involves the following tables:
 * <ul>
 * <li><strong>data_type_lu</strong> - holds information about data types, like String, Date, Decimal etc.</li>
 * <li><strong>command</strong> - a command maps a single request to a number of different queries.</li>
 * <li><strong>command_query_xref</strong> - the actual mapping between a command and n queries.</li>
 * <li><strong>input_lu</strong> - holds the inputs for the various queries.</li>
 * <li><strong>query_input_xref</strong> - the mapping between a query and n inputs.</li>
 * <li><strong>query</strong> - the actual query.</li><p>
 * </ul>
 * <p/>
 * Example:
 * <pre>SELECT c.handle, r.rating
 *   FROM coder c, rating r
 *  WHERE c.coder_id = r.coder_id
 *    AND r.rating @gt; @ra@</pre><p>
 * <p/>
 * This query gives us a list of handles and ratings where the rating is greater than
 * the input "ra".  We would follow the following steps to set this up in the database.
 * <ul>
 * <li>
 * Insert the query into the query table. {@link com.topcoder.utilities.QueryLoader} can be
 * used for this purpose (one can not insert into a TEXT coulumn directly).
 * <pre>
 *       com.topcoder.utilities.QueryLoader "DW" 1 "Coder_Ratings" 0 0 "
 *       SELECT c.handle, r.rating
 *         FROM coder c, rating r
 *        WHERE c.coder_id = r.coder_id
 *          AND r.rating &gt; @ra@"
 *     </pre>
 * <ul>
 * <li><strong>query_id</strong> a unique identifier for a query</li>
 * <li><strong>text</strong> the actual text of the query</li>
 * <li><strong>name</strong> a name for the query, this is used as a key for the resultset of this query</li>
 * <li><strong>ranking</strong> 1 if this is a ranking query, 0 if it is not.  If it is a ranking query, another
 * column is added to the result set containing the rank of  a particular row.  If there is a tie, all those
 * rows that are tied get the same rank, and the next non-tied row will get the next rank as
 * if there was no tie.</li>
 * <li><strong>column_index</strong> the column that we are ranking on</li>
 * </ul>
 * <br/>
 * </li>
 * <li>
 * <pre>
 *       INSERT INTO input_lu (input_id, input_code, data_type_id, input_desc)
 *       VALUES (2, 'ra', 1001, 'Rating');
 *     </pre>
 * <ul>
 * <li><strong>input_id</strong> a unique identifier for an input</li>
 * <li><strong>input_code</strong> a code used when specifying a particular input in a query</li>
 * <li><strong>date_type_id</strong> the id of the data type of this input</li>
 * <li><strong>input_desc</strong> a text description of this input</li>
 * </ul>
 * <br/>
 * </li>
 * <li>
 * <pre>
 *       INSERT INTO command (command_id, command_desc, command_group_id)
 *       VALUES (3, 'coder_ratings', 1);
 *     </pre>
 * <ul>
 * <li><strong>command_id</strong> a unique identifier for a command</li>
 * <li><strong>command_desc</strong> a text description of the command</li>
 * <li><strong>command_group_id</strong> the id of the group this command belongs to</li>
 * </ul>
 * <br/>
 * </li>
 * <li>
 * <pre>
 *       INSERT INTO command_query_xref (command_id, query_id, sort_order)
 *       VALUES (3, 1, 1);
 *     </pre>
 * <ul>
 * <li><strong>command_id</strong> is the id of the command we are setting up</li>
 * <li><strong>query_id</strong> is the id of the query we're associating with this command</li>
 * <li><strong>sort_order</strong> is simply a way to sort the queriess for a given command, each
 * record in command_query_xref for a particular command should have a distinct
 * value for sort_order</li>
 * </ul>
 * <br/>
 * </li>
 * <li>
 * <pre>
 *       INSERT INTO query_input_xref (query_id, optional, default_value, input_id, sort_order)
 *       VALUES (1, 'Y', '1500', 2, 1);
 *     </pre>
 * <ul>
 * <li><strong>query_id</strong> is the id of the query whose inputs were are setting up</li>
 * <li><strong>optional</strong> is a flag that allows us to set defaults for this input</li>
 * <li>
 * <strong>default_value</strong> is the default value if this input was not specified
 * at execution time.  it should not include the INPUT_DELIMITER specified in the
 * DataAccess.properties file.
 * </li>
 * <li><strong>input_id</strong> is the id of the input we are associating with this query</li>
 * <li><strong>sort_order</strong> is simply a way to sort the inputs for a given query, each
 * record in query_input_xref for a particular query should have a distinct  value for sort_order</li>
 * </ul>
 * <br/>
 * </li>
 * </ul>
 *
 * @author Dave Pecora
 * @author Greg Paul
 * @version $Revision$
 * @see ResultSetContainer*
 */

public class DataRetriever implements DataRetrieverInt {
    private static Logger log = Logger.getLogger(DataRetriever.class);
    private Connection conn;
    /* Keeps track of the most recent query run, for exception handling purposes */
    private StringBuffer query;

    /**
     * Constructor that takes a connection object.
     *
     * @param conn
     */
    protected DataRetriever(Connection conn) {
        this.conn = conn;
    }

    private void handleException(Exception e, String lastQuery, Map inputs) {
        try {
            log.error("Exception caught: " + e.toString());
            log.error("The last query run was: ");
            log.error(lastQuery);
            log.error("Function inputs were: ");
            Iterator i = inputs.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                String value = (String) inputs.get(key);
                log.error("Input code: " + key + " --- Input value: " + value);
            }
            log.error("Exception details:");
            if (e instanceof SQLException)
                DBMS.printSqlException(true, (SQLException) e);
            else
                e.printStackTrace();
        } catch (Exception ex) {
        }
    }

    /**
     * In addition to checking correctness of the input, we're also preventing
     * a clever user who knows how this system works from embedding SQL in the
     * input.  TODO If we ever need to add string input support at a later time, this
     * should be explicitly checked for.
     *
     * @param input
     * @param dataType
     * @return true if the input is valid, false if not
     */
    private boolean validateInput(String input, int dataType) {
        if (dataType == DataAccessConstants.INTEGER_INPUT) {
            try {
                new BigInteger(input);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else if (dataType == DataAccessConstants.DECIMAL_INPUT) {
            try {
                new BigDecimal(input);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else if (dataType == DataAccessConstants.DATE_INPUT) {
            try {
                // Check that what we have first in the string is a valid date,
                // in the expected yyyy-mm-dd format.
                SimpleDateFormat sdf = new SimpleDateFormat(DataAccessConstants.DATE_FORMAT);
                sdf.setLenient(false);
                ParsePosition pp = new ParsePosition(0);
                sdf.parse(input, pp);

                // Check for the presence of unwanted stuff after it
                if (pp.getIndex() < input.length())
                    return false;

                // Passed checks OK
                return true;
            } catch (Exception e) {
                return false;
            }
        } else if (dataType == DataAccessConstants.STRING_INPUT) {
            try {
                /* not doing any checking here cuz i can't think of anything that makes
                 * sense to check for
                 */
                return true;
            } catch (Exception e) {
                return false;
            }
        } else if (dataType == DataAccessConstants.SORT_DIRECTION_INPUT) {
            String s = input.trim().toUpperCase();
            return (s.equals("ASC") || s.equals("DESC"));
        }

        // Unknown input type!
        return false;
    }

    private String runDefaultInputQuery(String defaultQueryId, Map inputs) throws Exception {
        // Before running, check to see if this special query has already been
        // run this time around.
        String input = (String) inputs.get(defaultQueryId);
        if (input != null)
            return input;

        int specialQueryId = Integer.parseInt(defaultQueryId.substring(1));

        query = new StringBuffer(300);
        query.append("SELECT text FROM query WHERE query_id=");
        query.append(specialQueryId);
        String specialQuery = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            if (!rs.next())
                throw new Exception("Query text for query ID " + specialQueryId + " missing from DB");
            specialQuery = DBMS.getTextString(rs, 1);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }

        int i, j;
        // For default input queries, all inputs are required to avoid circularity hassles.
        // Thus the substitution process is not table-based (no query_input_xref entries here).
        // It is assumed that inputs have already passed validation in executeCommand(),
        // which should be the case if the input resolution order is specified properly in
        // query_input_xref.  default inputs can not include the INPUT_DELIMITER or this will fail.
        while ((i = specialQuery.indexOf(DataAccessConstants.INPUT_DELIMITER)) >= 0) {
            j = specialQuery.indexOf(DataAccessConstants.INPUT_DELIMITER, i + 1);
            if (j < 0)
                throw new Exception("Unterminated input in default input query " + defaultQueryId);
            String inputCode = specialQuery.substring(i + 1, j);
            String inputValue = (String) inputs.get(inputCode);
            if (inputValue == null)
                throw new Exception("Missing required input " + inputCode +
                        " for default input query " + defaultQueryId);
            String oldStr = specialQuery.substring(i, j + 1);
            specialQuery = StringUtilities.replace(specialQuery, oldStr, inputValue);
        }

        try {
            // Save query for exception handling
            query = new StringBuffer(specialQuery);
            ps = conn.prepareStatement(specialQuery);
            rs = ps.executeQuery();
            if (!rs.next())
                throw new Exception("Default input query " + defaultQueryId +
                        " did not return a value");
            input = rs.getString(1);

            // If input is still null, we're hosed - this indicates
            // some problem with the DW data or the default input
            // query.
            if (input == null)
                throw new Exception("Default input query " + defaultQueryId +
                        " did not return a value");

            // Save this result to avoid query rerunning
            inputs.put(defaultQueryId, input);
            return input;
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }
    }

    /**
     * The function which does the actual retrieval of statistics data.  Returns a
     * <tt>Map</tt> in which each key is a <tt>String</tt> representing
     * a query name, and each value is a <tt>ResultSetContainer</tt> containing the
     * data returned by that query.
     *
     * @param inputMap A map of inputs to this command.  Each key in this map
     *                 is a valid input code in DataAccess.properties, and
     *                 each value is a <tt>String</tt> containing the value passed in
     *                 for the given input code.  One key-value pair must
     *                 contain a valid command description as specified in the
     *                 "command" table.
     * @return The statistical data requested by the command.
     * @throws Exception If some problem is encountered while executing
     *                   the queries specified by the passed-in command.
     */
    public Map<String, ResultSetContainer> executeCommand(Map inputMap) throws Exception {
        //create a new map to avoid mutating the passed in version.
        //log.debug("input: " + inputMap.toString());
        Map inputs = new HashMap(inputMap);
        String commandDesc = (String) inputs.get(DataAccessConstants.COMMAND);
        if (commandDesc == null)
            throw new Exception("Missing command description");

        int i, rowcount;
        query = null;
        ArrayList qid;
        int queryIdList[];
        HashMap queryTextMap, queryNameMap, querySortMap, queryStartRow, queryEndRow;
        HashMap<String, ResultSetContainer> resultMap;

        // Get the list of queries to execute, and the names and texts of the queries
        ResultSet rs = null;
        PreparedStatement ps = null;
        long commandId = 0;
        try {
            query = new StringBuffer(300);
            query.append("SELECT cqx.query_id, ");
            query.append(" q.text, ");
            query.append(" q.name, ");
            query.append(" q.ranking, ");
            query.append(" q.column_index, ");
            query.append(" cqx.sort_order, ");
            query.append(" c.command_id ");
            query.append("FROM command c, query q, command_query_xref cqx ");
            query.append("WHERE c.command_desc = ? ");
            query.append("AND cqx.command_id = c.command_id ");
            query.append("AND q.query_id = cqx.query_id ");
            query.append("ORDER BY cqx.sort_order ASC ");
            ps = conn.prepareStatement(query.toString());
            ps.setString(1, commandDesc);
            rs = ps.executeQuery();
            rowcount = 0;
            qid = new ArrayList();
            queryTextMap = new HashMap();
            queryNameMap = new HashMap();
            querySortMap = new HashMap();
            boolean tracked = false;
            while (rs.next()) {
                commandId = rs.getLong("command_id");
                rowcount++;
                Integer tempId = new Integer(rs.getInt(1));
                qid.add(tempId);
                queryTextMap.put(tempId, DBMS.getTextString(rs, 2));
                queryNameMap.put(tempId, rs.getString(3));
                int isRanking = rs.getInt(4);
                if (isRanking == 1)
                    querySortMap.put(tempId, new Integer(rs.getInt(5)));
            }
            queryIdList = new int[rowcount];
            for (i = 0; i < rowcount; i++) {
                queryIdList[i] = ((Integer) qid.get(i)).intValue();
            }
        } catch (Exception e) {
            handleException(e, query.toString(), inputs);
            throw new Exception("Invalid command: " + commandDesc);
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
        }

        ResultSet rs1 = null;
        PreparedStatement ps1 = null;

        // Now get the inputs of the queries
        try {
            if (queryIdList.length == 0) {
                throw new Exception("Query information for command " + commandDesc +
                        " missing from DB");
            }
            queryStartRow = new HashMap();
            queryEndRow = new HashMap();

            // Get all at once to avoid multiple DB hits.
            query = new StringBuffer(300);
            query.append("SELECT i.input_code, ");
            query.append(" qi.optional, ");
            query.append(" i.data_type_id, ");
            query.append(" qi.default_value, ");
            query.append(" qi.query_id, ");
            query.append(" qi.sort_order ");
            query.append("FROM command c, command_query_xref cqx, input_lu i, query_input_xref qi ");
            query.append("WHERE c.command_desc = ? ");
            query.append("AND cqx.command_id = c.command_id ");
            query.append("AND cqx.query_id = qi.query_id ");
            query.append("AND qi.input_id = i.input_id ");
            query.append("ORDER BY qi.query_id ASC, qi.sort_order ASC ");
            ps1 = conn.prepareStatement(query.toString());
            ps1.setString(1, commandDesc);
            rs1 = ps1.executeQuery();

            // Put these in a result set container to avoid requiring the
            // connection to have two open prepared statements at the same
            // time.
            ResultSetContainer rsc = new ResultSetContainer(rs1);

            rowcount = rsc.getRowCount();

            for (i = 0; i < rowcount; i++) {
                String inputCode = rsc.getItem(i, 0).toString();
                String optional = rsc.getItem(i, 1).toString();
                int dataType = Integer.parseInt(rsc.getItem(i, 2).toString());
                String defaultValue = rsc.getItem(i, 3).toString();
                Integer tempId = new Integer(rsc.getItem(i, 4).toString());
                String input = (String) inputs.get(inputCode);

                if (input == null) {
                    if (!optional.equals("Y")) {
                        throw new Exception("Missing required query input: " + inputCode);
                    }

                    // Any special default value processing goes here
                    if (defaultValue.startsWith(DataAccessConstants.SPECIAL_DEFAULT_MARKER)) {
                        // Runs an database query to get the input value.
                        input = runDefaultInputQuery(defaultValue, inputs);
                    } else {
                        // Simple fixed default value
                        input = defaultValue;
                    }

                    // Add to inputs list; other inputs may depend on this value.
                    inputs.put(inputCode, input);
                } // end input fillin

                // Remove leading/trailing input whitespace
                input = input.trim();
                if (dataType == DataAccessConstants.STRING_INPUT) {
                    //escape single quotes for informix
                    input = StringUtil.replace(input, "\'", "\'\'");
                }


                if (!validateInput(input, dataType))
                    throw new Exception("Invalid data for input " + inputCode + ": " + input);

                if (inputCode.equals(DataAccessConstants.START_RANK)) {
                    queryStartRow.put(tempId, new Integer(input));
                    continue;
                } else if (inputCode.equals(DataAccessConstants.END_RANK) ||
                        inputCode.equals(DataAccessConstants.NUMBER_RECORDS)) {
                    queryEndRow.put(tempId, new Integer(input));
                    continue;
                }

                // Sort columns represent user-requested sorts on a particular
                // column.  They can be used in two different ways:  to
                // instruct the database to do a sort on a column via an
                // ORDER BY @sc@ clause, or to instruct the EJB to do the
                // sort on the ResultSetContainer after the data has been
                // retrieved.  Both must be supported because sometimes you
                // want to do a sort by a column before choosing a part
                // of the data to display (as with earnings history when sorting
                // by amount of money paid) while other times you want to choose
                // a part of the data to display before doing a sort (as with
                // highest submission accuracy when sorting by coder handle).
                //
                // Unfortunately ResultSetContainer columns are 0-based and
                // database columns are 1-based.  To improve transparency for
                // the front-end coder, we add one to the input here so that
                // it's always 0-based from the front end's perspective (and
                // from the perspective of default sort column arguments in
                // the query_input database).
                if (inputCode.equals(DataAccessConstants.SORT_COLUMN)) {
                    int colValue = Integer.parseInt(input);
                    colValue++;
                    input = String.valueOf(colValue);
                }

                String old = DataAccessConstants.INPUT_DELIMITER + inputCode +
                        DataAccessConstants.INPUT_DELIMITER;
                String queryText = (String) queryTextMap.get(tempId);
                queryText = StringUtilities.replace(queryText, old, input);
                queryTextMap.put(tempId, queryText);
            } // end loop over query inputs

            /* Check we filled in all the inputs.
             * this should be fine unless input was filled in with
             * itself surrounded by the INPUT_DELIMITER.  example:
             * input code = cr, and value = @cr@
             */
            for (i = 0; i < queryIdList.length; i++) {
                String queryText = (String) queryTextMap.get(new Integer(queryIdList[i]));
                for (int j = 0; j < rowcount; j++) {
                    if (queryText.indexOf(DataAccessConstants.INPUT_DELIMITER +
                            rsc.getItem(j, "input_code").toString() +
                            DataAccessConstants.INPUT_DELIMITER) > -1) {
                        throw new Exception("Query input entries missing from database: " + queryText);
                    }
                }
            }
        } catch (Exception e) {
            handleException(e, query.toString(), inputs);
            throw e;
        } finally {
            DBMS.close(rs1);
            DBMS.close(ps1);
        }

        // At this point we've built all queries to run.
        // Execute them and fill the ResultSetContainers.
        String queryText = "", queryName = "";
        String sortQueryName = (String) inputs.get(DataAccessConstants.SORT_QUERY);
        String sortQueryCol = (String) inputs.get(DataAccessConstants.SORT_COLUMN);
        String sortDir = (String) inputs.get(DataAccessConstants.SORT_DIRECTION);
        boolean sortCalled = (sortQueryName != null && sortQueryCol != null);
        try {
            //todo we can improve this and solve sql injection hacks
            //todo by replacing the inputs with ?'s and keeping track of
            //todo what goes where and then calling setXXX on the preparedstatement
            resultMap = new HashMap();
            long start = System.currentTimeMillis();
            for (i = 0; i < queryIdList.length; i++) {
                try {
                    Integer lookup = new Integer(queryIdList[i]);
                    queryText = (String) queryTextMap.get(lookup);
                    queryName = (String) queryNameMap.get(lookup);
                    Integer ranklistCol = (Integer) querySortMap.get(lookup);
                    int startRow, endRow;
                    Integer tempInt = (Integer) queryStartRow.get(lookup);
                    if (tempInt == null)
                        startRow = 1;
                    else
                        startRow = tempInt.intValue();
                    tempInt = (Integer) queryEndRow.get(lookup);
                    if (tempInt == null)
                        endRow = Integer.MAX_VALUE;
                    else
                        endRow = tempInt.intValue();
                    ps = conn.prepareStatement(queryText);
                    rs = ps.executeQuery();
                    //log.debug("startrow: " + startRow + " endRow: " + endRow);
                    // Call different constructors depending on if we have to
                    // generate a ranklist column or not.
                    ResultSetContainer rsc;
                    if (ranklistCol == null)
                        rsc = new ResultSetContainer(rs, startRow, endRow, false);
                    else
                        rsc = new ResultSetContainer(rs, startRow, endRow, ranklistCol.intValue(), false);

                    // Sort if necessary
                    if (sortCalled && queryName.equals(sortQueryName)) {
                        int col = Integer.parseInt(sortQueryCol);
                        boolean ascending = true;
                        if (sortDir != null && sortDir.equals("desc"))
                            ascending = false;
                        if (rsc.isValidColumn(col))
                            rsc.sortByColumn(col, ascending);
                    }

                    resultMap.put(queryName, rsc);
                } finally {
                    DBMS.close(rs);
                    DBMS.close(ps);

                }
            }
            trackExecution(commandId, conn, System.currentTimeMillis()-start, inputs);

        } catch (Exception e) {
            handleException(e, queryText, inputs);
            throw new Exception("Error while retrieving query data:" + queryText);
        }

        return resultMap;
    }

    private void trackExecution(long commandId, Connection conn, long time, Map inputs) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("insert into command_execution (command_id, execution_time, inputs) values (?, ?, ?)");
            ps.setLong(1, commandId);
            ps.setLong(2, time);
            StringBuffer sb = new StringBuffer();
            if(inputs != null) {
                Iterator i = inputs.keySet().iterator();
                while (i.hasNext()) {
                    String key = (String) i.next();
                    sb.append(key);
                    sb.append(':');
                    sb.append((String) inputs.get(key));
                    sb.append('|');
                }
                ps.setString(3, sb.toString());
            } else {
                ps.setString(3, null);
            }

            ps.executeUpdate();
        } catch (Exception e) {
            log.error("Couldn't insert row to track the execution of command " + commandId);
            e.printStackTrace();
        } finally {
            DBMS.close(ps);
        }
    }
}


