/*
 * Da Twink Daddy - 05/09/2002 - File Created
 */
package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.listener.monitor.QuestionItem;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* Da Twink Daddy - 05/13/2002 - Reworked because of synchronization issues, small changes throughout */

/**
 * TableModel for QuestionItems Allows customized sorting using the {@link
 * #layerSort} and {@link #setRatingNullsFirst} methods. Maintains the data
 * internally, question addition/removal through {@link #removeQuestion} and
 * {@link #addQuestion} methods Maintains a "rating" for each question and
 * exposes as an extra column.
 *
 *@author    Boyd Stephen Smith, Jr.
 *@created   May 10, 2002
 */
public class QuestionsTableModel extends AbstractTableModel {

    /**
     * Struct-like class to hold a sort key
     *
     *@author    Boyd Stephen Smith, Jr.
     *@created   May 10, 2002
     */
    public final static class SortInfo {

        /** The column/field to sort on */
        public int index;
        /** Whether to sort in reverse */
        public boolean reverse;

        /** Creates an empty SortInfo */
        public SortInfo() {
        }

        /**
         * Creates a SortInfo with the given values
         *
         *@param index  column/field for sort
         *@param rev    whether to sort in reverse
         */
        public SortInfo(int index, boolean rev) {
            this.index = index;
            reverse = rev;
        }
    }

    /**
     * Struct-like class to hold title and class for a column
     *
     *@author    Boyd Stephen Smith, Jr.
     *@created   May 10, 2002
     */
    private final static class ColumnInfo {

        /** Column name/title */
        public String title;
        /** Column common class */
        public Class clazz;

        /** Creates an empty ColumnInfo */
        public ColumnInfo() {
        }

        /**
         * Creates a ColumnInfo with the given values
         *
         *@param title  Description of the Parameter
         *@param clazz  Description of the Parameter
         */
        public ColumnInfo(String title, Class clazz) {
            this.title = title;
            this.clazz = clazz;
        }
    }

    /**
     * Logger for this class. All methods in this class and inner classes log to
     * this object. Nested classes that are not inner class may log to this object
     * or may have their own Logger (which should be a child of this object).
     */
    private final static Logger log = Logger.getLogger("com.topcoder.client.contestMonitor.model.QuestionsTableModel");

    /**
     * Column info for the table. This array gives the titles for the columns and
     * their common value class. It's length is also used as a count of the
     * columns.
     */
    private final static ColumnInfo[] columnInfo = new ColumnInfo[]{
        new ColumnInfo("Submission Time", String.class),
        new ColumnInfo("Submitted By", String.class),
        new ColumnInfo("Question Text", String.class),
        new ColumnInfo("Rating", String.class),
    };

    /** Column index for timestamps */
    private final static int TIMESTAMP_COLUMN_INDEX = 0;

    /** Column index for usernames */
    private final static int USERNAME_COLUMN_INDEX = 1;

    /** Column index for question text */
    private final static int QUESTION_COLUMN_INDEX = 2;

    /** Column index for question rating */
    private final static int RATING_COLUMN_INDEX = 3;

    /**
     * Mutatble Comparator implementation for flexible row sorting Ordering can be
     * changed using {@link #layerSort} and {@link #setRatingNullsFirst} methods
     *
     *@author    Boyd Stephen Smith, Jr.
     *@created   May 10, 2002
     */
    private class DynamicComparator implements Comparator {

        /** Sort keys, from most important to least important */
        protected SortInfo[] sortOrder = new SortInfo[columnInfo.length];

        /** Whether null (un-set) ratings are sorted first */
        protected boolean ratingNullsFirst = true;

        /** Creates a new DynamicComparator */
        public DynamicComparator() {
            for (int i = 0; i < sortOrder.length; ++i) {
                sortOrder[i] = new SortInfo(i, false);
            }
        }

        /**
         * Compares two objects. Both obejct must be QuestionItems, {@link #sortOrder}
         * and {@link #ratingNullsFirst} determine ordering.
         *
         *@param one  an object
         *@param two  another object
         *@return     int 3-way comparision value &lt; 0 means one *&lt; two, == 0
         *      means one == two, &gt; 0 means one &gt; two
         */
        public int compare(Object one, Object two) {
            QuestionItem q1 = (QuestionItem) one;
            QuestionItem q2 = (QuestionItem) two;

            int compVal = 0;

            for (int i = 0; i < sortOrder.length && compVal == 0; ++i) {
                compVal = partialCompare(sortOrder[i].index, q1, q2);
                if (sortOrder[i].reverse) {
                    compVal = -compVal;
                }
            }

            return compVal;
        }

        /**
         * "Layers" a sort key Updates the {@link #sortOrder} so that field is the
         * primary key. The ordering for item where the given field is equivalent are
         * not changed. (The re-ordering is stable.)
         *
         *@param field  the new primary sort column/field
         *@param rev    whether to sort that field in reverse
         */
        public void layerSort(int field, boolean rev) {
            for (int i = 0; i < sortOrder.length; ++i) {
                if (sortOrder[i].index == field) {
                    System.arraycopy(sortOrder, 0, sortOrder, 1, i);
                    sortOrder[0] = new SortInfo(field, rev);
                    return;
                }
            }
            log.warn("No such field (" + field + ").");
        }


        /**
         * Changes the sort order for null (un-set) ratings. The ordering for any two
         * rows where both have un-set ratings or where both have set ratings is
         * unchanged.
         *
         *@param newVal  true to sort un-set ratings first
         */
        public void setRatingNullsFirst(boolean newVal) {
            ratingNullsFirst = newVal;
        }

        /**
         * Indicates whether null (un-set) ratings are sorted first
         *
         *@return   boolean true if un-set ratings are before set ratings
         */
        public boolean isRatingNullsFirst() {
            return ratingNullsFirst;
        }

        /**
         * Compares two question items on a single field. This method simply reads off
         * the particular field from each question and call one of the specific
         * sorting methods.
         *
         *@param field  the column/field to compare
         *@param q1     a QuestionItem
         *@param q2     another QuestionItem
         *@return       int 3-way comparision value for q1 and q2 on field
         */
        protected int partialCompare(int field, QuestionItem q1, QuestionItem q2) {
            switch (field) {
            case TIMESTAMP_COLUMN_INDEX:
                return compareTimestamp(q1.getTime(), q2.getTime());
            case USERNAME_COLUMN_INDEX:
                return compareUsername(q1.getUsername(), q2.getUsername());
            case QUESTION_COLUMN_INDEX:
                return compareQuestionText(q1.getMessage(), q2.getMessage());
            case RATING_COLUMN_INDEX:
                return compareRatings(getRating(q1), getRating(q2));
            default:
                log.error("No such field (" + field + ").");
                return 0;
            }
        }

        /**
         * <P class="method.summary">Compares two timestamps</P>
         *
         * <P>First the timestamps are converted to {@link java.util.Date} objects using {@link #parseDateString(java.lang.String)}.
         * Then, they are compared using their natural ordering, {@link java.util.Date#compareTo(java.util.Date)}.</P>
         *
         * <P>If one of the {@link java.lang.String}s cannot be coverted, that is {@link #parseDateString(java.lang.String)} throws a {@link java.text.ParseException},
         * it is considered greater than the parsable {@link java.lang.String} and 1 or -1, as appropriate, is returned.
         * If neither {@link java.lang.String} can be converted, they are considered equal and 0 is returned.</P>
         *
         *@param ts1  a timestamp string
         *@param ts2  another timestamp string
         *@return     int The ordering of the two {@link java.util.Date}s represented by the timestamp strings
         */
        protected int compareTimestamp(String ts1, String ts2) {
            Date date1, date2;
            try {
                date1 = parseDateString(ts1);
            } catch (ParseException pe) {
                date1 = null;
            }
            try {
                date2 = parseDateString(ts2);
            } catch (ParseException pe) {
                date2 = null;
            }
            return date1 == null
                    ? (date2 == null ? 0 : 1)
                    : (date2 == null ? -1 : date1.compareTo(date2));
        }

        /**
         * <P class="method.summary">Attempts to parse a String produces by {@link java.util.Date#toString}.</P>
         *
         * <P>First, an attempt is made to parse the date using a {@link java.text.SimpleDateFormat} with patterm "EEE MMM dd HH:mm:ss zzz yyyy".
         * If a {@link java.text.ParseException} is thrown, the date is assumed to not contain timezone information, and an
         * attempt is made to parse the date using the pattern "EEE MMM dd HH:mm:ss yyyy".
         * If a {@link java.text.ParseException} is thrown during this second attempt, it will be thrown by this method.</P>
         *
         * @param   date    date string produced by {@link java.util.Date#toString}
         * @return  Date    parsed {@link java.util.Date} object
         * @throws  java.text.ParseException  If all attempts to parse the string result in a ParseException being thrown,
         *                          the ParseException from the last attempt is thrown.
         * @see     java.util.Date#toString
         */
        private Date parseDateString(String date) throws ParseException {
            SimpleDateFormat fmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            try {
                return fmt.parse(date);
            } catch (ParseException pe) {
                /* Date my not have timezone information attached. */
                fmt.applyPattern("EEE MMM dd HH:mm:ss yyyy");
                return fmt.parse(date);
            }
        }

        /**
         * "Intelligently" compares two usernames. First, the usernames are compared
         * case-insensitively. Then, if they a case-insensitively equivalent, they are
         * case-sensitively compared. This gives an ordering like: "A" &lt; "a" &lt;
         * "Aa" &lt; "aA" &lt; "aa" &lt; "B".
         *
         *@param user1  a username
         *@param user2  another username
         *@return       int 3-way "intelligent" comparision value
         */
        protected int compareUsername(String user1, String user2) {
            int compVal = user1.compareToIgnoreCase(user2);
            if (compVal == 0) {
                compVal = user1.compareTo(user2);
            }
            return compVal;
        }

        /**
         * Compares two texts using default string comparision ({@link
         * String#compareTo}).
         *
         *@param text1  text of a question
         *@param text2  text of another question
         *@return       3-way comparision value for text1 and text2
         */
        protected int compareQuestionText(String text1, String text2) {
            return text1.compareTo(text2);
        }

        /**
         * "Intelligently" compare two rating strings. First, checks for nulls are
         * done and they are sorted according to {@link #ratingNullsFirst}. If no
         * order is established, conversion to doubles are done. Ratings that can be
         * interpreted as doubles are sorted before ratings that cannot. When both
         * ratings can be treated as doubles, they are sorted numerically. When both
         * ratings cannot be treated as doubles, they are sorted case-insesitively
         * Sample ordering when ratingNullsFirst if true: null &lt; "-2" == "-02.0"
         * &lt; "0" &lt; ".5" == "0.5" &lt; "bar" == "Bar" == "BAR" &lt; "foo"
         *
         *@param r1  a rating string
         *@param r2  another rating string
         *@return    int 3-way "intelligent" comparison value
         */
        protected int compareRatings(String r1, String r2) {
            if (r1 == null) {
                return r2 == null
                        ? 0
                        : (ratingNullsFirst ? -1 : 1);
            } else if (r2 == null) {
                return ratingNullsFirst ? 1 : -1;
            } else {
                Double val1 = null;
                Double val2 = null;

                try {
                    val1 = Double.valueOf(r1);
                } catch (NumberFormatException nfe) {
                    /*
                     * Ignore on purpose
                     */
                }

                try {
                    val2 = Double.valueOf(r2);
                } catch (NumberFormatException nfe) {
                    /*
                     * Ignore on purpose
                     */
                }

                return val1 == null
                        ? (val2 == null ? r1.compareToIgnoreCase(r2) : 1)
                        : (val2 == null ? -1 : val1.compareTo(val2));
            }
        }
    }

    /** Table data. Each element is a QuestionItem. */
    private List questions = new LinkedList();

    /**
     * Storage for ratings. A mapping rather than a list was used so that sorting
     * could be done on {@link #questions} w/o have to simultaeously sort the
     * ratings.
     */
    private Map ratings = new HashMap();

    /**
     * DynamicComparator that determines the row ordering for this object.
     */
    private DynamicComparator comparison = new DynamicComparator();

    /** Creates a new, empty QuestionsTableModel */
    public QuestionsTableModel() {
    }

    /* Da Twink Daddy - 05/13/2002 - Replaced indexed remove with new method */
    /**
     * Removes the question the table model.
     *
     *@param	q	the question to remove
     */
    public void removeQuestion(QuestionItem q) {
        int index;
        synchronized (questions) {
            index = Collections.binarySearch(questions, q, comparison);
            if (index >= 0) {
                questions.remove(index);
            }
        }
        ratings.remove(q);
        if (index >= 0) {
            fireTableRowsDeleted(index, index);
        }
    }

    /**
     * Retrives the QuestionItem for the given row
     *
     *@param index  the row
     *@return       QuestionItem the QuestionItem for that row
     */
    public QuestionItem getQuestion(int index) {
        synchronized (questions) {
            return (QuestionItem) questions.get(index);
        }
    }

    /**
     * Retrieves the assigned rating for a given question.
     *
     *@param q  the QuestionItem
     *@return   String the assigned rating string
     */
    public String getRating(QuestionItem q) {
        return (String) ratings.get(q);
    }

    /**
     * Adds the given question to our data
     *
     *@param q  the new QuestionItem
     */
    public void addQuestion(QuestionItem q) {
        int index;
        synchronized (questions) {
            index = Collections.binarySearch(questions, q, comparison);
            if (index < 0) {
                index = -(index + 1);
                questions.add(index, q);
            } else {
                log.warn("Duplicate question received; ignoring");
            }
        }
        if (index >= 0) {
            fireTableRowsInserted(index, index);
        }
    }

    /**
     * Returns the common value class for the given column
     *
     *@param col  the index of the column
     *@return     Class the common value class for the column
     */
    public Class getColumnClass(int col) {
        return columnInfo[col].clazz;
    }

    /**
     * Determines is a certain cell is editable
     *
     *@param row  row of the cell
     *@param col  coluumn index of the cell
     *@return     boolean true if col == QUESTION_COLUMN_INDEX or
     *      RATING_COLUMN_INDEX
     */
    public boolean isCellEditable(int row, int col) {
        if (col == QUESTION_COLUMN_INDEX || col == RATING_COLUMN_INDEX) {
            return true;
        }
        return false;
    }

    /**
     * Returns the name of the column to be used as a column header
     *
     *@param col  index of the column
     *@return     String the name/title of the column
     */
    public String getColumnName(int col) {
        return columnInfo[col].title;
    }

    /**
     * Returns the number of columns in the table.
     *
     *@return   the number of columns in the table ({@link #columnInfo}<code>.length</code>
     *      )
     */
    public int getColumnCount() {
        return columnInfo.length;
    }

    /**
     * Return the number of rows in the table.
     *
     *@return   the number of rows in the table ({@link #questions}<code>.size()</code>
     *      )
     */
    public int getRowCount() {
        synchronized (questions) {
            return questions.size();
        }
    }

    /**
     * Retrieves a single value from the table
     *
     *@param row  row index
     *@param col  column index
     *@return     Object the entry value
     */
    public Object getValueAt(int row, int col) {
        QuestionItem rowItem = getQuestion(row);
        switch (col) {
        case TIMESTAMP_COLUMN_INDEX:
            return rowItem.getTime();
        case USERNAME_COLUMN_INDEX:
            return rowItem.getUsername();
        case QUESTION_COLUMN_INDEX:
            return rowItem.getMessage().trim();
        case RATING_COLUMN_INDEX:
            String rating = getRating(rowItem);
            return rating == null ? "" : rating;
        default:
            log.error("Invalid column index (" + col + ").");
            return null;
        }
    }

    /**
     * Sets a single value in the table
     *
     *@param newValue  the new value for the entry
     *@param row       row index
     *@param col       column index
     */
    public void setValueAt(Object newValue, int row, int col) {
        QuestionItem rowItem = getQuestion(row);
        switch (col) {
        case TIMESTAMP_COLUMN_INDEX:
            log.error("Attempt to modify question timestamp.");
            break;
        case USERNAME_COLUMN_INDEX:
            log.error("Attempt to modify question username.");
            break;
        case QUESTION_COLUMN_INDEX:
            rowItem.setMessage(newValue == null ? "" : newValue.toString());
            break;
        case RATING_COLUMN_INDEX:
            ratings.put(rowItem, newValue.equals("") ? null : newValue.toString());
            break;
        default:
            log.error("Invalid column index (" + col + ").");
            break;
        }
    }

    /**
     * "Layers" a new sort on the table. Stably sorts the table on column <code>col</code>
     * , reversing the normal ordering if <code>rev</code> is <code>true</code>
     *
     *@param col  column on which to sort
     *@param rev  whether to use reverse ordering
     */
    public void layerSort(int col, boolean rev) {
        comparison.layerSort(col, rev);
        Collections.sort(questions, comparison);
        fireTableDataChanged();
    }

    /**
     * Changes the way null (un-set) ratings are sorted in the table
     *
     *@param newVal  true to treat un-set ratings as less than any set rating
     */
    public void setRatingNullsFirst(boolean newVal) {
        if (newVal != comparison.isRatingNullsFirst()) {
            comparison.setRatingNullsFirst(newVal);
            Collections.sort(questions, comparison);
            fireTableDataChanged();
        }
    }

    /**
     * Determines ordering for null (un-set) ratings.
     *
     *@return   boolean true if null (un-set) ratings are sorted before any set
     *      ratings.
     */
    public boolean isRatingNullsFirst() {
        return comparison.isRatingNullsFirst();
    }
}

