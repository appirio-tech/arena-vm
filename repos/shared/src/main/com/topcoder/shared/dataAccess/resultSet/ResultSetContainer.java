package com.topcoder.shared.dataAccess.resultSet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.topcoder.shared.dataAccess.StringUtilities;
import com.topcoder.shared.docGen.xml.RecordTag;
import com.topcoder.shared.docGen.xml.ValueTag;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;


/**
 * A read-only container of data retrieved from a database.  The
 * list interface implemented by this class operates upon the data.
 * Unless specified otherwise, row and column indices are 0-based
 * for all methods in this class. <p>
 * <p/>
 * This class has only been tested with Informix, and if other databases
 * are subsequently used, will need to be revisited. <p>
 * <p/>
 * The complete list of Informix data types and the java.sql.Types constant
 * they map to when retrieved under the EJB drivers is as follows: <p>
 * <p/>
 * Blob - BLOB.  Currently unsupported by this class. <br>
 * Boolean - OTHER <br>
 * Byte - LONGVARBINARY <br>
 * Char - CHAR <br>
 * Clob - CLOB.  Currently unsupported by this class. <br>
 * Date - DATE <br>
 * Datetime - TIME or TIMESTAMP, depending on the special attributes
 * of the column.  For example, a datetime year to fraction will be
 * a TIMESTAMP because it includes both date and time components, while
 * a datetime hour to second will be a TIME because it only includes
 * a time component. <br>
 * Decimal - DECIMAL <br>
 * Float - DOUBLE <br>
 * Int - INTEGER <br>
 * Int8 - BIGINT <br>
 * Interval - CHAR <br>
 * Lists - OTHER.  Currently unsupported by this class. <br>
 * Lvarchar - LONGVARCHAR <br>
 * Money - DECIMAL(16,2) <br>
 * Multisets - OTHER.  Currently unsupported by this class. <br>
 * Nchar - CHAR <br>
 * Nvarchar - VARCHAR <br>
 * Serial - INTEGER <br>
 * Serial8 - BIGINT <br>
 * Sets - OTHER.  Currently unsupported by this class. <br>
 * Smallfloat - FLOAT <br>
 * Smallint - SMALLINT <br>
 * Text - LONGVARCHAR <br>
 * Varchar - VARCHAR
 *
 * @author Dave Pecora
 * @author Tony Barrile
 * @version 1.01, 02/14/2002
 */
public class ResultSetContainer implements Serializable, List<ResultSetContainer.ResultSetRow>, Cloneable {

    /**
     * 
     */
    private static final long serialVersionUID = -6750551108318797975L;

    private static Logger log = Logger.getLogger(ResultSetContainer.class);

    // Stores ArrayList of ResultSetRow
    private ArrayList<ResultSetRow> data;
    private ResultColumn columns[];
    private HashMap<String, Integer> columnNameMap;
    private int startRow;
    private int endRow;

    // Variables indicating whether there is other relevant data which
    // was not placed in this ResultSetContainer because the row numbers
    // were out of range
    boolean dataBefore, dataAfter;

    /*******************************************************************/
    // Constructor section

    /**
     * ***************************************************************
     */

    /**
     * This has marginal value and shoult not be used in practice,
     * but it helps us fullfill the contract of a java bean.
     */
    public ResultSetContainer() {
        data = new ArrayList<ResultSetRow>();
        columnNameMap = new HashMap<String, Integer>();
        dataBefore = false;
        dataAfter = false;
        startRow = 1;
        endRow = Integer.MAX_VALUE;
    }

    /**
     * ResultSetContainers may only be used in conjunction with a ResultSet,
     * to store database query results.  This version of the constructor
     * adds all rows of the ResultSet into the container, replacing any
     * nulls retrieved from the database with a default value.  This default
     * value is "" for strings, 0 for numbers, false for booleans, and the
     * current date/time for date and time fields.
     *
     * @param rs A ResultSet containing data to be added to the container
     * @throws Exception If there is some problem retrieving the data
     */
    public ResultSetContainer(ResultSet rs) throws Exception {
        this();
        log.debug("ResultSetContainer(ResultSet) called...");
        initializeMetaData(rs);
        while (rs.next()) {
            addRow(rs);
        }
        endRow = data.size();

    }

    /**
     * This constructor version has a boolean parameter specifying whether or
     * not to replace nulls retrieved from the database with a default value
     * (see above constructor for default values).
     *
     * @param rs           A ResultSet containing data to be added to the container
     * @param replaceNulls A boolean parameter specifying whether or not to
     *                     replace nulls with default values
     * @throws Exception If there is some problem retrieving the data
     */
    public ResultSetContainer(ResultSet rs, boolean replaceNulls) throws Exception {
        this();
        log.debug("ResultSetContainer(ResultSet, boolean) called...");
        initializeMetaData(rs);
        while (rs.next()) {
            if (replaceNulls) {
                addRow(rs);
            } else {
                addRowWithNulls(rs);
            }
        }
        endRow = data.size();
    }

    /**
     * Start and end row control.  The row number arguments to this
     * constructor are 1-based for convenience.  If later on it
     * is desired to have finer-grained control over which rows
     * are added into the result, another constructor could be added
     * to take in a tester object.  This object would have a function
     * specifying whether or not to include a row in the container.
     * The constructor would then call this function on each row of
     * data. <p>
     * <p/>
     * This constructor version replaces nulls retrieved from the
     * database with default values.
     * <p/>
     * If start > end, an IllegalArgumentException is thrown.
     *
     * @param rs    A ResultSet containing data to be added to the container
     * @param start The 1-based row number at which to start adding data.
     *              Thus, a start number of 1 begins adding with the first row.
     * @param end   The 1-based row number which indicates the final row of
     *              data to be added.  Thus, an end number of 5 will stop adding
     *              at the fifth row.
     * @throws Exception If there is some problem retrieving the data
     */
    public ResultSetContainer(ResultSet rs, int start, int end) throws Exception {
        this();
        log.debug("ResultSetContainer(ResultSet, int, int) called...");
        if (start > end)
            throw new IllegalArgumentException("Start row cannot exceed end row");
        initializeMetaData(rs);
        int row = 0;
        while (rs.next()) {
            row++;
            if (row < start) {
                dataBefore = true;
                continue;
            }
            if (row > end) {
                dataAfter = true;
                break;
            }
            addRow(rs);
        }
        startRow = start;
        endRow = end;
    }

    /**
     * Start and end row control.  The row number arguments to this
     * constructor are 1-based for convenience.  If later on it
     * is desired to have finer-grained control over which rows
     * are added into the result, another constructor could be added
     * to take in a tester object.  This object would have a function
     * specifying whether or not to include a row in the container.
     * The constructor would then call this function on each row of
     * data. <p>
     * <p/>
     * If start > end, an IllegalArgumentException is thrown.
     *
     * @param rs           A ResultSet containing data to be added to the container
     * @param start        The 1-based row number at which to start adding data.
     *                     Thus, a start number of 1 begins adding with the first row.
     * @param end          The 1-based row number which indicates the final row of
     *                     data to be added.  Thus, an end number of 5 will stop adding
     *                     at the fifth row.
     * @param replaceNulls A boolean parameter specifying whether or not to
     *                     replace nulls with default values
     * @throws Exception If there is some problem retrieving the data
     */
    public ResultSetContainer(ResultSet rs, int start, int end, boolean replaceNulls) throws Exception {
        this();
        log.debug("ResultSetContainer(ResultSet, int, int, boolean) called...");
        if (start > end)
            throw new IllegalArgumentException("Start row cannot exceed end row");
        initializeMetaData(rs);
        int row = 0;
        while (rs.next()) {
            row++;
            if (row < start) {
                dataBefore = true;
                continue;
            }
            if (row > end) {
                dataAfter = true;
                break;
            }
            if (replaceNulls) {
                addRow(rs);
            } else {
                addRowWithNulls(rs);
            }
        }
        startRow = start;
        endRow = end;
    }


    /**
     * This constructor version, in addition to offering start and end
     * row control as above, also enables the construction of a ranklist.
     * The extra ranklist column created by this function will assign
     * numerical ranks, assigning equal rank to any ties.  <p>
     * <p/>
     * This constructor version replaces nulls retrieved from the
     * database with default values.
     *
     * @param rs          A ResultSet containing data to be added to the container
     * @param start       The 1-based row number at which to start adding data.
     *                    Thus, a start number of 1 begins adding with the first row.
     * @param end         The 1-based row number which indicates the final row of
     *                    data to be added.  Thus, an end number of 5 will stop adding
     *                    at the fifth row.
     * @param ranklistCol The 1-based index of the column to assign ranks by.  It is
     *                    assumed this column is already sorted from a
     *                    suitable ORDER BY clause.
     * @throws Exception If there is some problem retrieving the data
     */
    public ResultSetContainer(ResultSet rs, int start, int end,
                              int ranklistCol) throws Exception {
        this();
        log.debug("ResultSetContainer(ResultSet, int, int, int) called...");
        if (start > end)
            throw new IllegalArgumentException("Start row cannot exceed end row");
        initializeMetaData(rs);
        ranklistCol--;
        if (!isValidColumn(ranklistCol))
            throw new IllegalArgumentException("Ranklist column index " + ranklistCol + " out of range");

        // Build the extra ranklist column
        ResultColumn tempColumns[] = new ResultColumn[columns.length + 1];
        System.arraycopy(columns, 0, tempColumns, 0, columns.length);
        tempColumns[columns.length] = new ResultColumn(Types.INTEGER, "rank", 9, 0, "");
        columnNameMap.put("rank", columns.length);
        columns = tempColumns;

        if (start > end)
            return;

        int row = 0, rank = 1;
        TCResultItem lastItem = null;
        while (rs.next()) {
            row++;
            if (row == 1) {
                lastItem = getItem(rs, ranklistCol);
            } else {
                TCResultItem thisItem = getItem(rs, ranklistCol);
                if (lastItem.compareTo(thisItem) != 0) {
                    lastItem = thisItem;
                    rank = row;
                }
            }
            if (row < start) {
                dataBefore = true;
                continue;
            }
            if (row > end) {
                dataAfter = true;
                break;
            }
            addRanklistRow(rs, rank);
        }
        startRow = start;
        endRow = end;
    }

    /**
     * This constructor version, in addition to offering start and end
     * row control as above, also enables the construction of a ranklist.
     * The extra ranklist column created by this function will assign
     * numerical ranks, assigning equal rank to any ties.  <p>
     *
     * @param rs           A ResultSet containing data to be added to the container
     * @param start        The 1-based row number at which to start adding data.
     *                     Thus, a start number of 1 begins adding with the first row.
     * @param end          The 1-based row number which indicates the final row of
     *                     data to be added.  Thus, an end number of 5 will stop adding
     *                     at the fifth row.
     * @param ranklistCol  The 1-based index of the column to assign ranks by.  It is
     *                     assumed this column is already sorted from a
     *                     suitable ORDER BY clause.
     * @param replaceNulls A boolean parameter specifying whether or not to
     *                     replace nulls with default values*
     * @throws Exception If there is some problem retrieving the data
     */
    public ResultSetContainer(ResultSet rs, int start, int end,
                              int ranklistCol, boolean replaceNulls) throws Exception {
        this();
        log.debug("ResultSetContainer(ResultSet, int, int, int, boolean) called...");
        if (start > end)
            throw new IllegalArgumentException("Start row cannot exceed end row");
        initializeMetaData(rs);
        ranklistCol--;
        if (!isValidColumn(ranklistCol))
            throw new IllegalArgumentException("Ranklist column index " + ranklistCol + " out of range");

        // Build the extra ranklist column
        ResultColumn tempColumns[] = new ResultColumn[columns.length + 1];
        System.arraycopy(columns, 0, tempColumns, 0, columns.length);
        tempColumns[columns.length] = new ResultColumn(Types.INTEGER, "rank", 9, 0, "");
        columnNameMap.put("rank", columns.length);
        columns = tempColumns;

        if (start > end)
            return;

        int row = 0, rank = 1;
        TCResultItem lastItem = null;
        while (rs.next()) {
            row++;
            if (row == 1) {
                lastItem = getItem(rs, ranklistCol);
            } else {
                TCResultItem thisItem = getItem(rs, ranklistCol);
                if (lastItem.compareTo(thisItem) != 0) {
                    lastItem = thisItem;
                    rank = row;
                }
            }
            if (row < start) {
                dataBefore = true;
                continue;
            }
            if (row > end) {
                dataAfter = true;
                break;
            }
            if (replaceNulls) {
                addRanklistRow(rs, rank);
            } else {
                addRanklistRowWithNulls(rs, rank);
            }
        }
        startRow = start;
        endRow = end;
    }

    public ResultSetContainer(ResultSetContainer rs, ResultFilter f) {
        this();
        log.debug("ResultSetContainer(ResultSetContainer, ResultFilter) called...");
        initializeMetaData(rs);

        ResultSetRow rsr = null;
        for (Object r1 : rs) {
            Object r = (Object) r1;
            rsr = (ResultSetRow) r;
            if (f.include(rsr)) {
                addRow(rsr);
            }
        }
        endRow = data.size();
    }

    public ResultSetContainer(ResultSetContainer rs, ResultFilter[] f) {
        this();
        log.debug("ResultSetContainer(ResultSetContainer, ResultFilter[]) called...");
        initializeMetaData(rs);

        ResultSetRow rsr = null;
        for (Iterator it = rs.iterator(); it.hasNext();) {
            rsr = (ResultSetRow) it.next();
            boolean include = true;
            for (int i = 0; i < f.length && include; i++) {
                include = f[i].include(rsr);
            }
            //log.debug("include " + include);
            if (include) {
                addRow(rsr);
            }
        }
        endRow = data.size();
    }

    public ResultSetContainer(ResultSetContainer rs, int start, int end) throws Exception {
        this();
        log.debug("ResultSetContainer(ResultSetContainer, int, int) called...");
        if (start > end)
            throw new IllegalArgumentException("Start row cannot exceed end row");
        initializeMetaData(rs);
        int row = 1;
        ResultSetRow rsr = null;
        for (Iterator it = rs.iterator(); it.hasNext(); row++) {
            rsr = (ResultSetRow) it.next();
            if (row < start) {
                dataBefore = true;
                continue;
            }
            if (row > end) {
                dataAfter = true;
                break;
            }
            addRow(rsr);
        }
        startRow = start;
        endRow = end;
    }

    /**
     * Build a container with an extra calculated column
     * 
     * @param rs container to be copied
     * @param cf extra calculated column 
     * @throws Exception
     */
    public ResultSetContainer(ResultSetContainer rs, CalculatedColumn cc) throws Exception {
        this(rs, new CalculatedColumn[]{cc});
    }

    /**
     * Build a container with extra calculated columns
     * 
     * @param rs container calculated be copied
     * @param cf extra calculated columns 
     * @throws Exception
     */
    public ResultSetContainer(ResultSetContainer rs, CalculatedColumn []cc) throws Exception {
        this();
        initializeMetaData(rs);

        this.dataAfter = rs.croppedDataAfter(); 
        this.dataBefore = rs.croppedDataBefore(); 

        // Add the extra columns
        ResultColumn tempColumns[] = new ResultColumn[columns.length + cc.length];
        System.arraycopy(columns, 0, tempColumns, 0, columns.length);
        
        for (int i = 0; i < cc.length; i++) { 
            tempColumns[columns.length + i] = cc[i]; 
            columnNameMap.put(cc[i].getName(), columns.length + i);
        }
        
        columns = tempColumns;

        for (ResultSetRow rsr : rs) {
            TCResultItem[] items = rsr.getItems();
            
            TCResultItem[] allItems = new TCResultItem[items.length + cc.length];
            System.arraycopy(items, 0, allItems, 0, items.length);

            for (int i = 0; i < cc.length; i++) { 
                allItems[items.length + i] = cc[i].calculate(rsr);
            }
            
            data.add(new ResultSetRow(allItems));
        }
    }

    // Data item retrieval
    private TCResultItem getItem(ResultSet rs, int i) throws Exception {
        String s;

        switch (columns[i].getType()) {

            case Types.CHAR:
            case Types.VARCHAR:
                s = rs.getString(i + 1);
                if (s == null) {
                    return new TCStringResult("");
                }
                return new TCStringResult(s);

                // Both TEXT and LVARCHAR fields in the DB map to LONGVARCHAR.
                // Text fields are serialized and must be read using getBytes(),
                // while lvarchar fields must be read with getString().
            case Types.LONGVARCHAR:
                if (columns[i].getSourceType().equals("text"))
                    return new TCStringResult(DBMS.getTextString(rs, i + 1));
                else {
                    s = rs.getString(i + 1);
                    if (s == null)
                        return new TCStringResult("");
                    return new TCStringResult(s);
                }

            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return new TCIntResult(rs.getInt(i + 1));

            case Types.BIGINT:
                return new TCLongResult(rs.getLong(i + 1));

                // Booleans in the DB map to OTHER, but this is left in here
                // for completeness.
            case Types.BIT:
                return new TCBooleanResult(rs.getBoolean(i + 1));

            case Types.FLOAT:
            case Types.DOUBLE:
                return new TCDoubleResult(rs.getDouble(i + 1));

            case Types.DATE:
                java.sql.Date dt = rs.getDate(i + 1);
                if (dt == null)
                    return new TCDateResult(new java.sql.Date(System.currentTimeMillis()));
                return new TCDateResult(dt);

            case Types.TIME:
                Time tm = rs.getTime(i + 1);
                if (tm == null)
                    return new TCTimeResult(new Time(System.currentTimeMillis()));
                return new TCTimeResult(tm);

            case Types.TIMESTAMP:
                Timestamp ts = rs.getTimestamp(i + 1);
                if (ts == null)
                    return new TCTimestampResult(new Timestamp(System.currentTimeMillis()));
                return new TCTimestampResult(ts);

            case Types.DECIMAL:
            case Types.NUMERIC:
                // Requires some special handling as large numbers might
                // not fit in a regular int or float value, but it is desirable
                // to use the int or float types when possible for performance
                // reasons.
                int pr = columns[i].getPrecision();
                if (columns[i].getScale() == 0) {
                    // Integer.  log_10(2^31) = 9.33 while log_10(2^63) = 18.96
                    // Thus, we are guaranteed that 9 digits or less fit into
                    // a regular int while 18 digits or less fit into a regular long.
                    if (pr <= 9) {
                        return new TCIntResult(rs.getInt(i + 1));
                    } else if (pr <= 18) {
                        return new TCLongResult(rs.getLong(i + 1));
                    } else {
                        // getBigDecimal is broken when called from the EJB.  Possible
                        // driver issue.  Use getObject() instead
                        // return new TCBigIntegerResult(rs.getBigDecimal(i+1).toBigInteger());
                        BigDecimal bd = (BigDecimal) rs.getObject(i + 1);
                        if (bd == null)
                            return new TCBigIntegerResult(new BigInteger("0"));
                        return new TCBigIntegerResult(bd.toBigInteger());
                    }
                } else {
                    // Decimal.  Single-precision floats have 24 apparent bits of
                    // precision while double-precision doubles have 53 apparent
                    // bits of precision.  log_10(2^24) = 7.22 while
                    // log_10(2^53) = 15.95, and thus:
                    if (pr <= 7) {
                        return new TCFloatResult(rs.getFloat(i + 1));
                    } else if (pr <= 15) {
                        return new TCDoubleResult(rs.getDouble(i + 1));
                    } else {
                        // Avoid broken getBigDecimal method call, as per above
                        // return new TCBigDecimalResult(rs.getBigDecimal(i+1));
                        BigDecimal bd = (BigDecimal) rs.getObject(i + 1);
                        if (bd == null)
                            return new TCBigDecimalResult(new BigDecimal(0.0));
                        return new TCBigDecimalResult(bd);
                    }
                }

                // BYTE fields in the database map to LONGVARBINARY
            case Types.LONGVARBINARY:
                s = "";
                try {
                    Object o = DBMS.getBlobObject(rs, i + 1);
                    if (o != null)
                        s = StringUtilities.makePretty(o);
                } catch (Exception e) {
                    log.error("Exception while retrieving blob object into result set");
                    log.error(e.getMessage());
                }
                return new TCStringResult(s);

                // Booleans map to OTHER
            case Types.BOOLEAN:
            case Types.OTHER:
                if (columns[i].getSourceType().equals("boolean"))
                    return new TCBooleanResult(rs.getBoolean(i + 1));

            default:
                throw new SQLException("Unsupported data type in ResultSetContainer.getItem() type="+columns[i].getType()+" name="+columns[i].getSourceType());

        } // end switch statement
    }

    // Data item retrieval
    private TCResultItem getItemWithNulls(ResultSet rs, int i) throws Exception {
        int irv;
        long lrv;
        double drv;
        float frv;
        boolean brv;

        switch (columns[i].getType()) {

            case Types.CHAR:
            case Types.VARCHAR:
                return new TCStringResult(rs.getString(i + 1));

                // Both TEXT and LVARCHAR fields in the DB map to LONGVARCHAR.
                // Text fields are serialized and must be read using getBytes(),
                // while lvarchar fields must be read with getString().
            case Types.LONGVARCHAR:
                if (columns[i].getSourceType().equals("text"))
                    return new TCStringResult(DBMS.getTextStringWithNulls(rs, i + 1));
                else
                    return new TCStringResult(rs.getString(i + 1));

            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                irv = rs.getInt(i + 1);
                if (rs.wasNull())
                    return new TCIntResult(null);
                return new TCIntResult(irv);

            case Types.BIGINT:
                lrv = rs.getLong(i + 1);
                if (rs.wasNull())
                    return new TCLongResult(null);
                return new TCLongResult(lrv);

                // Booleans in the DB map to OTHER, but this is left in here
                // for completeness.
            case Types.BIT:
                brv = rs.getBoolean(i + 1);
                if (rs.wasNull())
                    return new TCBooleanResult(null);
                return new TCBooleanResult(brv);

            case Types.FLOAT:
            case Types.DOUBLE:
                drv = rs.getDouble(i + 1);
                if (rs.wasNull())
                    return new TCDoubleResult(null);
                return new TCDoubleResult(drv);

            case Types.DATE:
                return new TCDateResult(rs.getDate(i + 1));

            case Types.TIME:
                return new TCTimeResult(rs.getTime(i + 1));

            case Types.TIMESTAMP:
                return new TCTimestampResult(rs.getTimestamp(i + 1));

            case Types.DECIMAL:
            case Types.NUMERIC:
                // Requires some special handling as large numbers might
                // not fit in a regular int or float value, but it is desirable
                // to use the int or float types when possible for performance
                // reasons.
                int pr = columns[i].getPrecision();
                if (columns[i].getScale() == 0) {
                    // Integer.  log_10(2^31) = 9.33 while log_10(2^63) = 18.96
                    // Thus, we are guaranteed that 9 digits or less fit into
                    // a regular int while 18 digits or less fit into a regular long.
                    if (pr <= 9) {
                        irv = rs.getInt(i + 1);
                        if (rs.wasNull())
                            return new TCIntResult(null);
                        return new TCIntResult(irv);
                    } else if (pr <= 18) {
                        lrv = rs.getLong(i + 1);
                        if (rs.wasNull())
                            return new TCLongResult(null);
                        return new TCLongResult(lrv);
                    } else {
                        // getBigDecimal is broken when called from the EJB.  Possible
                        // driver issue.  Use getObject() instead
                        // return new TCBigIntegerResult(rs.getBigDecimal(i+1).toBigInteger());
                        BigDecimal bd = (BigDecimal) rs.getObject(i + 1);
                        if (bd == null)
                            return new TCBigIntegerResult(null);
                        return new TCBigIntegerResult(bd.toBigInteger());
                    }
                } else {
                    // Decimal.  Single-precision floats have 24 apparent bits of
                    // precision while double-precision doubles have 53 apparent
                    // bits of precision.  log_10(2^24) = 7.22 while
                    // log_10(2^53) = 15.95, and thus:
                    if (pr <= 7) {
                        frv = rs.getFloat(i + 1);
                        if (rs.wasNull())
                            return new TCFloatResult(null);
                        return new TCFloatResult(frv);
                    } else if (pr <= 15) {
                        drv = rs.getDouble(i + 1);
                        if (rs.wasNull())
                            return new TCDoubleResult(null);
                        return new TCDoubleResult(drv);
                    } else {
                        // Avoid broken getBigDecimal method call, as per above
                        // return new TCBigDecimalResult(rs.getBigDecimal(i+1));
                        BigDecimal bd = (BigDecimal) rs.getObject(i + 1);
                        return new TCBigDecimalResult(bd);
                    }
                }

                // BYTE fields in the database map to LONGVARBINARY
            case Types.LONGVARBINARY:
                String s = null;
                try {
                    Object o = DBMS.getBlobObject(rs, i + 1);
                    if (o != null)
                        s = StringUtilities.makePretty(o);
                } catch (Exception e) {
                    log.error("Exception while retrieving blob object into result set");
                    log.error(e.getMessage());
                }
                return new TCStringResult(s);

                // Booleans map to OTHER
            case Types.BOOLEAN:
                boolean r = rs.getBoolean(i + 1);
                if (rs.wasNull())
                    return new TCBooleanResult(null);
                return new TCBooleanResult(r);
            case Types.OTHER:
                if (columns[i].getSourceType().equals("boolean")) {
                    boolean rv = rs.getBoolean(i + 1);
                    if (rs.wasNull())
                        return new TCBooleanResult(null);
                    return new TCBooleanResult(rv);
                }
                throw new SQLException("Unsupported data type " + columns[i].getSourceType() + " in ResultSetContainer.getItem()");

            default:
                throw new SQLException("Unsupported data type " + columns[i].getSourceType() + " in ResultSetContainer.getItem()");

        } // end switch statement
    }

    // Data addition routine, called from constructor only.
    private void addRow(ResultSet rs) throws Exception {
        TCResultItem ri[] = new TCResultItem[columns.length];
        for (int i = 0; i < columns.length; i++)
            ri[i] = getItem(rs, i);
        data.add(new ResultSetRow(ri));
    }

    private void addRow(ResultSetRow rsr) {
        data.add((ResultSetRow) rsr.clone());
    }


    // Data addition routine, called from constructor only.
    private void addRowWithNulls(ResultSet rs) throws Exception {
        TCResultItem ri[] = new TCResultItem[columns.length];
        for (int i = 0; i < columns.length; i++)
            ri[i] = getItemWithNulls(rs, i);
        data.add(new ResultSetRow(ri));
    }

    // Data addition routine, called from constructor only.
    private void addRanklistRow(ResultSet rs, int rank) throws Exception {
        TCResultItem ri[] = new TCResultItem[columns.length];
        for (int i = 0; i < columns.length - 1; i++)
            ri[i] = getItem(rs, i);
        ri[columns.length - 1] = new TCIntResult(rank);
        data.add(new ResultSetRow(ri));
    }

    // Data addition routine, called from constructor only.
    private void addRanklistRowWithNulls(ResultSet rs, int rank) throws Exception {
        TCResultItem ri[] = new TCResultItem[columns.length];
        for (int i = 0; i < columns.length - 1; i++)
            ri[i] = getItemWithNulls(rs, i);
        ri[columns.length - 1] = new TCIntResult(rank);
        data.add(new ResultSetRow(ri));
    }

    // Metadata construction routine, called from constructor only.
    private void initializeMetaData(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        columns = new ResultColumn[rsmd.getColumnCount()];
        for (int i = 1; i <= columns.length; i++) {
            int precision = 0, scale = 0, colType = rsmd.getColumnType(i);
            String colName = rsmd.getColumnLabel(i);
            String colSourceType = rsmd.getColumnTypeName(i);

            if (colType == Types.DECIMAL || colType == Types.NUMERIC) {
                precision = rsmd.getPrecision(i);
                scale = rsmd.getScale(i);
            } else if (colType == Types.FLOAT) {
                precision = rsmd.getPrecision(i);
            }

            columns[i - 1] = new ResultColumn(colType, colName, precision, scale, colSourceType);
            // Unlike ResultSets, column indices are zero-based.
            columnNameMap.put(colName, i - 1);
        }
    }

    private void initializeMetaData(ResultSetContainer rs) {
        columns = new ResultColumn[rs.columns.length];
        System.arraycopy(rs.columns, 0, columns, 0, rs.columns.length);
        for (int i = 0; i < columns.length; i++) {
            columnNameMap.put(rs.getColumnName(i), i);
        }
    }

    /********************************************************************/
    // Inner class section

    /**
     * ****************************************************************
     */

    // This class is used for comparison purposes; it is called by the
    // sortByColumn() function
    private class DataRowComparator implements Comparator {
        int columnToCompare;
        int secondaryColumn;
        boolean sortAscending;
        boolean sortSecondaryAscending;

        public DataRowComparator(int i, boolean ascending) {
            if (!isValidColumn(i))
                throw new IllegalArgumentException("Column index " + i + " out of bounds");
            columnToCompare = i;
            secondaryColumn = i;
            sortAscending = ascending;
            sortSecondaryAscending = ascending;
        }

        public DataRowComparator(int i, int j, boolean ascending, boolean secondaryAscending) {
            if (!isValidColumn(i))
                throw new IllegalArgumentException("Column index " + i + " out of bounds");
            if (!isValidColumn(j))
                throw new IllegalArgumentException("Column index " + j + " out of bounds");
            columnToCompare = i;
            secondaryColumn = j;
            sortAscending = ascending;
            sortSecondaryAscending = secondaryAscending;
        }

        public int compare(Object o1, Object o2) {
            ResultSetRow rsr1 = (ResultSetRow) o1;
            ResultSetRow rsr2 = (ResultSetRow) o2;
            TCResultItem ri1 = rsr1.getItem(columnToCompare);
            TCResultItem ri2 = rsr2.getItem(columnToCompare);
            int temp = ri1.compareTo(ri2);

            //always sort nulls down
            //if ascending, null is greater than everything


            if (temp == 0) {
                ri1 = rsr1.getItem(secondaryColumn);
                ri2 = rsr2.getItem(secondaryColumn);
                temp = ri1.compareTo(ri2);
                if (sortSecondaryAscending) {
                    if (ri1.getResultData() == null && ri2.getResultData() != null) {
                        return 1;
                    } else if (ri1.getResultData() != null && ri2.getResultData() == null) {
                        return -1;
                    } else {
                        return temp;
                    }
                } else {
                    return -temp;
                }
            } else {
                if (sortAscending) {
                    if (ri1.getResultData() == null && ri2.getResultData() != null) {
                        return 1;
                    } else if (ri1.getResultData() != null && ri2.getResultData() == null) {
                        return -1;
                    } else {
                        return temp;
                    }
                } else {
                    return -temp;
                }
            }
        }
    }

    /**
     * This class implements a read-only list iterator.  This is required
     * as <tt>ResultSetContainer</tt> implements the <tt>List<tt> interface.
     * A call to the <tt>ResultSetContainer.listIterator<tt> routine will
     * return an instance of this class.
     */
    public class ReadOnlyListIterator implements ListIterator<ResultSetRow> {
        private int nextIndex;

        /**
         * Constructs a read-only list iterator, with iteration starting
         * from the first element of the class.
         */
        ReadOnlyListIterator() {
            nextIndex = 0;
        }

        /**
         * Constructs a read-only list iterator, with iteration starting
         * from the indicated element.
         *
         * @param index to start from.
         * @throws IndexOutOfBoundsException if the passed-in index is
         *                                   not in range (less than zero or greater than the size
         *                                   of the data)
         */
        ReadOnlyListIterator(int index) {
            if (index < 0 || index > data.size()) {
                throw new IndexOutOfBoundsException();
            }
            nextIndex = index;
        }

        /**
         * @param o
         */
        public void add(ResultSetRow o) {
            throw new UnsupportedOperationException();
        }

        /**
         * This function will return true iff a call to the <tt>next()</tt> function
         * will return valid data.
         *
         * @return Whether or not there is more data which can be retrieved by a call
         *         to <tt>next()</tt>.
         */
        public boolean hasNext() {
            return (nextIndex < data.size());
        }

        /**
         * This function will return true iff a call to the <tt>previous()</tt> function
         * will return valid data.
         *
         * @return Whether or not there is more data which can be retrieved by a call
         *         to <tt>previous()</tt>.
         */
        public boolean hasPrevious() {
            return (nextIndex > 0);
        }

        /**
         * This function will return the next item in the list.
         *
         * @return The next item in the list.
         * @throws NoSuchElementException If we are already at the end of the list.
         */
        public ResultSetRow next() {
            if (nextIndex >= data.size()) {
                throw new NoSuchElementException();
            }
            return data.get(nextIndex++);
        }

        /**
         * This function will return the index of the next item in the list.
         *
         * @return The index of the next item in the list.
         */
        public int nextIndex() {
            return nextIndex;
        }

        /**
         * This function will return the previous item in the list.
         *
         * @return The previous item in the list.
         * @throws NoSuchElementException If we are already at the beginning of the list.
         */
        public ResultSetRow previous() {
            if (nextIndex <= 0) {
                throw new NoSuchElementException();
            }
            return data.get(--nextIndex);
        }

        /**
         * This function will return the index of the previous item in the list.
         *
         * @return The index of the previous item in the list.
         */
        public int previousIndex() {
            return nextIndex - 1;
        }

        /**
         *
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * @param o
         */
        public void set(ResultSetRow o) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * This class is meant to be a container for specific rows of data.
     * It provides methods for getting specific elements out.
     */
    public class ResultSetRow implements Cloneable, Serializable {
        private static final long serialVersionUID = 1242749104807930817L;

        private TCResultItem[] mtcItems;
        private Map<String, Object> itemMap;

        /**
         * Constructor to initialize the row data container
         *
         * @param tcri Contains an array of data elements which
         *             together comprise the row.  Each element should be an
         *             instance of a subclass of <tt>TCResultItem</tt>.
         */
        public ResultSetRow(TCResultItem tcri[]) {
            mtcItems = tcri;
            itemMap = new HashMap<String, Object>();
            for (int i = 0; i < columns.length; i++) {
                itemMap.put(columns[i].getName(), mtcItems[i].getResultData());
            }


        }

        /**
         * Returns the item at the specified index, or throws an exception
         * if the index is out of range.
         *
         * @param iIndex Index of the requested element
         * @return Item at the requested index
         * @throws IllegalArgumentException if the specified index is out of range.
         */
        public TCResultItem getItem(int iIndex) {
            if (!isValidColumn(iIndex))
                throw new IllegalArgumentException("Index " + iIndex + " out of range");
            return mtcItems[iIndex];
        }


        /**
         * Returns the item at the specified column name, or null if
         * the column name is invalid.
         *
         * @param sCol Name of the column for the requested element
         * @return Item at the requested index
         * @throws IllegalArgumentException if the specified column does not exist.
         */
        public TCResultItem getItem(String sCol) {
            if (!isValidColumn(sCol))
                throw new IllegalArgumentException("Column name " + sCol + " is not valid");
            int iCol = getColumnIndex(sCol);
            return mtcItems[iCol];
        }

        /**
         * @param index
         * @return
         * @throws NullPointerException if the item is null
         */
        public int getIntItem(int index) throws NullPointerException {
            return ((Number) getItem(index).getResultData()).intValue();
        }

        /**
         * @param col
         * @return
         * @throws NullPointerException if the item is null
         */
        public int getIntItem(String col) throws NullPointerException {
            return ((Number) getItem(col).getResultData()).intValue();
        }

        public float getFloatItem(int index) throws NullPointerException {
            return ((Number) getItem(index).getResultData()).floatValue();
        }

        public float getFloatItem(String col) throws NullPointerException {
            return ((Number) getItem(col).getResultData()).floatValue();
        }

        public double getDoubleItem(int index) throws NullPointerException {
            return ((Number) getItem(index).getResultData()).doubleValue();
        }

        public double getDoubleItem(String col) throws NullPointerException {
            return ((Number) getItem(col).getResultData()).doubleValue();
        }

        public boolean getBooleanItem(int index) throws NullPointerException {
            return ((Boolean) getItem(index).getResultData()).booleanValue();
        }

        public boolean getBooleanItem(String col) throws NullPointerException {
            return ((Boolean) getItem(col).getResultData()).booleanValue();
        }

        /**
         * @param index
         * @return
         * @throws NullPointerException if the item is null
         */
        public long getLongItem(int index) throws NullPointerException {
            return ((Number) getItem(index).getResultData()).longValue();
        }

        /**
         * @param col
         * @return
         * @throws NullPointerException if the item is null
         */
        public long getLongItem(String col) throws NullPointerException {
            return ((Number) getItem(col).getResultData()).longValue();
        }

        /**
         * @param index
         * @return String
         */
        public String getStringItem(int index) {
            Object ret = getItem(index).getResultData();
            return ret == null ? null : ret.toString();
        }

        /**
         * @param col
         * @return String
         */
        public Timestamp getTimestampItem(String col) {
            Object ret = getItem(col).getResultData();
            return ret == null ? null : (Timestamp) ret;
        }

        public Timestamp getTimestampItem(int index) {
            Object ret = getItem(index).getResultData();
            return ret == null ? null : (Timestamp) ret;
        }

        /**
         * @param col
         * @return String
         */
        public String getStringItem(String col) {
            Object ret = getItem(col).getResultData();
            return ret == null ? null : ret.toString();
        }


        /**
         * This method creates a cloned copy of this row.
         *
         * @return A cloned ResultSetRow instance.
         */
        public Object clone() {
            try {
                ResultSetRow rsr = (ResultSetRow) super.clone();
                rsr.mtcItems = new TCResultItem[this.mtcItems.length];
                System.arraycopy(mtcItems, 0, rsr.mtcItems, 0, mtcItems.length);
                return rsr;
            } catch (CloneNotSupportedException cnse) {
                //Should never get here because we implement Cloneable
                return null;
            }
        }

        /**
         * ResultSetContainer.ResultSetRow implementation of toString.
         * This method will output all data, separated by \t
         *
         * @return The row data in string form.
         */
        public String toString() {
            return toString("\t");
        }

        /**
         * ResultSetContainer.ResultSetRow implementation of toString.
         * This method will output all data, separated by colDelim
         *
         * @param colDelim
         * @return The row data in string form.
         */
        public String toString(String colDelim) {
            StringBuffer sbReturn = new StringBuffer();
            for (TCResultItem mtcItem : mtcItems) {
                sbReturn.append(mtcItem.toString()).append(colDelim);
            }
            sbReturn.setLength(sbReturn.length() - colDelim.length());
            return sbReturn.toString();
        }

        /**
         * Validates that the column specified is valid
         *
         * @param i The column index accessed
         * @return True iff the column index is valid.
         */
        public boolean isValidColumn(int i) {
            return (i >= 0 && i < columns.length);
        }

        /**
         * Validates that the column specified is valid
         *
         * @param name The column name accessed
         * @return True iff the column name is valid.
         */
        public boolean isValidColumn(String name) {
            return columnNameMap.containsKey(name);
        }


        public Map<String, Object> getMap() {
            return Collections.unmodifiableMap(itemMap);
        }
        
        public TCResultItem[] getItems() {
            TCResultItem[] items = new TCResultItem[mtcItems.length];
            System.arraycopy(mtcItems, 0, items, 0, mtcItems.length);
            return items;
        }

    }

    /**********************************************************************/
    // List interface implementation section
    /**********************************************************************/

    // These functions mostly just pass along the request to the data object.
    // Note that mutator functions are not supported.  This is deliberate:
    // no modifications should be made after the container is built.

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param index
     * @param element
     */
    public void add(int index, ResultSetRow element) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param o
     * @return boolean
     */
    public boolean add(ResultSetRow o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param c
     * @return boolean
     */
    public boolean addAll(Collection<? extends ResultSetRow> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param index
     * @param c
     * @return boolean
     */
    public boolean addAll(int index, Collection<? extends ResultSetRow> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns true iff this collection contains the element specified.
     *
     * @param o Element to be checked for containment in this collection.
     * @return true iff this collection contains the element specified
     */
    public boolean contains(Object o) {
        return data.contains(o);
    }

    /**
     * Returns true iff this collection contains all of the elements in the specified collection.
     *
     * @param c Elements to be checked for containment in this collection.
     * @return true iff this collection contains all of the elements in the specified collection
     */
    public boolean containsAll(Collection<?> c) {
        return data.containsAll(c);
    }

    public Map<String, Integer> getColumnNameMap() {
        return Collections.unmodifiableMap(columnNameMap);
    }

    /**
     * Returns the element (row) at the specified position (row number) in this list.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     * @throws IndexOutOfBoundsException if the specified index is out of
     *                                   range (<tt>index &lt; 0 || index &gt; size()</tt>).
     */
    public ResultSetRow get(int index) {
        return data.get(index);
    }

    /**
     * Returns the index in this list of the first occurence of the specified
     * element, or -1 if the list does not contain this element.
     *
     * @param o element to search for.
     * @return The index in this list of the first occurence of the specified
     *         element, or -1 if the list does not contain this element.
     */
    public int indexOf(Object o) {
        return data.indexOf(o);
    }

    /**
     * Returns true iff this collection contains no elements.
     *
     * @return Whether or not this collection contains elements.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Returns an iterator over the elements (rows) in this collection.
     *
     * @return A data iterator starting at the first element in the list.
     */
    public Iterator<ResultSetRow> iterator() {
        return data.iterator();
    }

    /**
     * Returns the index in this list of the last occurence of the specified
     * element, or -1 if the list does not contain this element.
     *
     * @param o element to search for.
     * @return The index in this list of the last occurence of the specified
     *         element, or -1 if the list does not contain this element.
     */
    public int lastIndexOf(Object o) {
        return data.lastIndexOf(o);
    }

    /**
     * Returns a read-only iterator of the elements in this list (in proper sequence).
     *
     * @return A read-only iterator of the elements in this list (in proper sequence).
     */
    public ListIterator<ResultSetRow> listIterator() {
        return new ReadOnlyListIterator();
    }

    /**
     * Returns a list iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     *
     * @param index index of the first element to be returned from the list
     *              iterator (by a call to the <tt>next</tt> method).
     * @return a list iterator of the elements in this list (in proper
     *         sequence), starting at the specified position in the list.
     * @throws IndexOutOfBoundsException if the specified index is out of
     *                                   range (<tt>index &lt; 0 || index &gt; size()</tt>).
     */
    public ListIterator<ResultSetRow> listIterator(final int index) {
        return new ReadOnlyListIterator(index);
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param index
     * @return Object
     */
    public ResultSetRow remove(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param o
     * @return boolean
     */
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param c
     * @return boolean
     */
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param c
     * @return boolean
     */
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported mutator function - throws an <tt>UnsupportedOperationException</tt>
     *
     * @param index
     * @param element
     * @return Object
     */
    public ResultSetRow set(int index, ResultSetRow element) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number of items in the list.
     *
     * @return The number of items in the list.
     */
    public int size() {
        return data.size();
    }

    /**
     * Returns a view of the portion of this list between the specified fromIndex, inclusive,
     * and toIndex, exclusive.  This method behaves much like a shallow clone, in that all
     * properties of this instance are backed by this instance.
     *
     * @param fromIndex Low endpoint (inclusive) of the subList.
     * @param toIndex   high endpoint (exclusive) of the subList.
     * @return List    a view of the specified range within this list.
     * @throws IllegalArgumentException If fromIndex > toIndex
     */
    public List<ResultSetRow> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (toIndex > data.size()) {
            toIndex = data.size();
        }
        ResultSetContainer rsc = new ResultSetContainer();
        rsc.columns = this.columns;
        rsc.columnNameMap = this.columnNameMap;
        rsc.dataBefore = fromIndex > 0;
        rsc.dataAfter = toIndex < data.size();
        rsc.data = new ArrayList<ResultSetRow>(this.data.subList(fromIndex, toIndex));
        rsc.startRow = fromIndex + 1;
        rsc.endRow = toIndex + 1;
        return rsc;
    }

    /**
     * Returns an array containing all of the elements (row data) in this collection.
     *
     * @return Array of row data
     */

    public Object[] toArray() {
        return data.toArray();
    }

    /**
     * Returns an array containing all of the elements (row data) in this collection.
     *
     * @param a Runtime type of the return array will be that of the specified array
     * @return Array of row data
     */
    public <ResultSetRow> ResultSetRow[] toArray(ResultSetRow[] a) {
        return data.toArray(a);
    }

    /**************************************************************************/
    // Data access routine section
    /**************************************************************************/

    /**
     * Returns true iff this <tt>ResultSetContainer</tt> was built with
     * a end row specification that was less than the number of data rows
     * in the <tt>ResultSet</tt>, meaning that there existed later rows of
     * data which were not placed in the container.
     *
     * @return See above.
     */
    public boolean croppedDataAfter() {
        return dataAfter;
    }

    /**
     * Returns true iff this <tt>ResultSetContainer</tt> was built with
     * a start row specification greater than one, implying that there
     * existed earlier rows of data which were not placed in the container.
     *
     * @return See above.
     */
    public boolean croppedDataBefore() {
        return dataBefore;
    }

    /**
     * Returns the column information for the data in this container.
     *
     * @return An array of <tt>ResultColumn</tt>, where each array entry
     *         contains information for the corresponding column.
     */
    public ResultColumn[] getColumns() {
        return columns;
    }

    /**
     * Returns the column information for the data in this container.
     *
     * @return An array of <tt>ResultColumn</tt>, where each array entry
     *         contains information for the corresponding column.
     */
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Returns the column index of the name provided, or -1 if no such
     * name exists.
     *
     * @param name Column name to look up
     * @return The corresponding column index
     */
    public int getColumnIndex(String name) {
        Integer i = ((Integer) columnNameMap.get(name));
        if (i == null)
            return -1;
        else
            return i;
    }

    /**
     * Returns the column information for the column specified.
     *
     * @param i the column for which to retrieve information
     * @return A <tt>ResultColumn</tt> object containing information
     *         for the given column.
     * @throws IllegalArgumentException if the column index specified
     *                                  is invalid.
     */
    public ResultColumn getColumnInfo(int i) {
        if (!isValidColumn(i))
            throw new IllegalArgumentException("Column index " + i + " out of bounds");
        return columns[i];
    }

    /**
     * Returns the name of the column at the specified location
     *
     * @param i The column number
     * @return String  The column name
     */
    public String getColumnName(int i) {
        return columns[i].getName();
    }

    /**
     * Returns the item at the specified location
     *
     * @param iRow The row index of the item
     * @param iCol The column index of the item
     * @return The specified item
     */
    public TCResultItem getItem(int iRow, int iCol) {
        return this.getRow(iRow).getItem(iCol);
    }

    public long getLongItem(int iRow, int iCol) {
        return this.getRow(iRow).getLongItem(iCol);
    }

    public int getIntItem(int iRow, int iCol) {
        return this.getRow(iRow).getIntItem(iCol);
    }

    public String getStringItem(int iRow, int iCol) {
        return this.getRow(iRow).getStringItem(iCol);
    }

    public Timestamp getTimestampItem(int iRow, int iCol) {
        return this.getRow(iRow).getTimestampItem(iCol);
    }

    public float getFloatItem(int iRow, int iCol) {
        return this.getRow(iRow).getFloatItem(iCol);
    }

    public double getDoubleItem(int iRow, int iCol) {
        return this.getRow(iRow).getDoubleItem(iCol);
    }

    public boolean getBooleanItem(int iRow, int iCol) {
        return this.getRow(iRow).getBooleanItem(iCol);
    }

    /**
     * Returns the item at the specified location
     *
     * @param iRow The row index of the item
     * @param sCol The column name from which to retrieve the item
     * @return The specified item
     */
    public TCResultItem getItem(int iRow, String sCol) {
        return this.getRow(iRow).getItem(sCol);
    }

    public int getIntItem(int iRow, String sCol) {
        return this.getRow(iRow).getIntItem(sCol);
    }

    public long getLongItem(int iRow, String sCol) {
        return this.getRow(iRow).getLongItem(sCol);
    }

    public String getStringItem(int iRow, String sCol) {
        return this.getRow(iRow).getStringItem(sCol);
    }

    public Timestamp getTimestampItem(int iRow, String sCol) {
        return this.getRow(iRow).getTimestampItem(sCol);
    }

    public float getFloatItem(int iRow, String sCol) {
        return this.getRow(iRow).getFloatItem(sCol);
    }

    public double getDoubleItem(int iRow, String sCol) {
        return this.getRow(iRow).getDoubleItem(sCol);
    }

    public boolean getBooleanItem(int iRow, String sCol) {
        return this.getRow(iRow).getBooleanItem(sCol);
    }

    /**
     * Returns the <tt>ResultSetContainer.ResultSetRow</tt> at the specified index
     *
     * @param iRow Row index
     * @return The <tt>ResultSetContainer.ResultSetRow</tt> instance at this location
     */
    public ResultSetRow getRow(int iRow) {
        return (ResultSetRow) data.get(iRow);
    }

    /**
     * Returns the number of data rows in the container.
     *
     * @return The number of rows in the container.
     */
    public int getRowCount() {
        return data.size();
    }

    /**
     * Gets the 1 based index of the first row in the container with respect to what
     * the full set might be.  If > 1, this means that a
     * start rank > 1 was provided to the contructor
     *
     * @return int
     */
    public int getStartRow() {
        return startRow;
    }

    /**
     * Gets the 1 based infex of the last row in the container with respect to what
     * the full set might be.  If an end rank was provided in the contructor
     * that what will be returned, otherwise, it'll be the size of the data set.
     *
     * @return int
     */
    public int getEndRow() {
        return endRow;
    }

    /**************************************************************************/
    // Utility routine section
    /**************************************************************************/
    /**
     * This method performs a clone of the object.
     *
     * @return A cloned ResultSetContainer instance.
     */
    public Object clone() {
        ResultSetContainer rsc = new ResultSetContainer();
        rsc.columns = new ResultColumn[columns.length];
        System.arraycopy(columns, 0, rsc.columns, 0, columns.length);
        Iterator it = this.iterator();
        ResultSetRow rsr;
        while (it.hasNext()) {
            rsr = (ResultSetRow) it.next();
            rsc.data.add((ResultSetRow) rsr.clone());
        }
        return rsc;
    }

    /**
     * Validates that the column specified is valid
     *
     * @param i The column index accessed
     * @return True iff the column index is valid.
     */
    public boolean isValidColumn(int i) {
        return (i >= 0 && i < columns.length);
    }

    /**
     * Validates that the column specified is valid
     *
     * @param name The column name accessed
     * @return True iff the column name is valid.
     */
    public boolean isValidColumn(String name) {
        return columnNameMap.containsKey(name);
    }

    /**
     * Validates that the row specified is valid
     *
     * @param i The row index accessed
     * @return True iff the row index is valid.
     */
    public boolean isValidRow(int i) {
        return (i >= 0 && i < data.size());
    }

    /**
     * This routine sorts the data in the container by the given column,
     * in the given direction.
     *
     * @param i         The column index to sort by.
     * @param ascending whether to sort the data in ascending or descending order.
     */
    public void sortByColumn(int i, boolean ascending) {
        Collections.sort(data, new DataRowComparator(i, ascending));
    }

    /**
     * This routine sorts the data in the container by the given column,
     * in the given direction.
     *
     * @param sCol      The column to sort by.
     * @param ascending whether to sort the data in ascending or descending order.
     */
    public void sortByColumn(String sCol, boolean ascending) {
        if (!isValidColumn(sCol))
            throw new IllegalArgumentException("Column name " + sCol + " is not valid");
        Collections.sort(data, new DataRowComparator(getColumnIndex(sCol), ascending));
    }

    /**
     * This routine sorts the data in the container by the given column,
     * in the given direction, if two rows are equal on this crteria, it
     * uses the second column to break the tie.
     *
     * @param i             The column index to sort by.
     * @param otherCol      The column index to break ties.
     * @param ascending     whether to sort the data in ascending or descending order.
     * @param backAscending whether to sort the data in ascending or descending order.
     */
    public void sortByColumn(int i, int otherCol, boolean ascending, boolean backAscending) {
        Collections.sort(data, new DataRowComparator(i, otherCol, ascending, backAscending));
    }

    /**
     * This routine sorts the data in the container by the given column,
     * in the given direction, if two rows are equal on this crteria, it
     * uses the second column to break the tie.
     *
     * @param sCol          The column to sort by.
     * @param sBackCol      The column to break ties.
     * @param ascending     whether to sort the data in ascending or descending order.
     * @param backAscending whether to sort the data in ascending or descending order.
     */
    public void sortByColumn(String sCol, String sBackCol, boolean ascending, boolean backAscending) {
        if (!isValidColumn(sCol))
            throw new IllegalArgumentException("Column name " + sCol + " is not valid");
        if (!isValidColumn(sBackCol))
            throw new IllegalArgumentException("Column name " + sBackCol + " is not valid");
        Collections.sort(data, new DataRowComparator(getColumnIndex(sCol), getColumnIndex(sBackCol), ascending, backAscending));
    }

    /**
     * This method will output all columns and data, rows separated by \n, columns separated by \t
     *
     * @return The columns and data, in string form.
     */
    public String toString() {
        return toString("\n", "\t");
    }

    /**
     * This method will output all columns and data.
     *
     * @param rowDelim
     * @param colDelim
     * @return The columns and data, in string form.
     */
    public String toString(String rowDelim, String colDelim) {

        StringBuffer sbReturn = new StringBuffer();
        Iterator it;
        for (int i = 0; i < this.getColumnCount(); i++) {
            sbReturn.append(this.getColumnName(i)).append(colDelim);
        }
        sbReturn.setLength(sbReturn.length() - colDelim.length());
        sbReturn.append(rowDelim);
        it = data.iterator();
        while (it.hasNext()) {
            sbReturn.append(((ResultSetContainer.ResultSetRow) it.next()).
                    toString(colDelim)).append(rowDelim);
        }
        sbReturn.setLength(sbReturn.length() - rowDelim.length());
        return sbReturn.toString();
    }

    /**
     * Returns a RecordTag that will allow us to get XML for
     * this ResultSetContainer
     *
     * @param rootName
     * @param rowName
     * @return The data contained in this result set within a <tt>RecordTag</tt>
     * @throws Exception
     */
    public RecordTag getTag(String rootName, String rowName) throws Exception {
        RecordTag result = null;
        result = new RecordTag(rootName);
        ResultSetRow row = null;
        RecordTag rowRecord = null;
        try {
            for (int k = 0; k < data.size(); k++) {
                row = (ResultSetRow) data.get(k);
                rowRecord = new RecordTag(rowName);
                for (int i = 0; i < this.getColumnCount(); i++) {
                    rowRecord.addTag(new ValueTag(this.getColumnName(i), row.getItem(i).toString()));
                }
                result.addTag(rowRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    /**
     * Returns a RecordTag that will allow us to get XML for
     * this ResultSetContainer
     *
     * @return The data contained in this result set within a <tt>RecordTag</tt>
     * @throws Exception
     */
    public RecordTag getTag() throws Exception {
        return getTag("ResultSet", "ResultRow");
    }
}

