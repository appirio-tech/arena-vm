package com.topcoder.shared.dataAccess;

import com.topcoder.shared.util.TCResourceBundle;

/**
 * A class to store the constants used for data access.
 * @author Greg Paul
 * @version  $Revision$ $Date$
 */
public class DataAccessConstants {
    private static TCResourceBundle bundle = new TCResourceBundle("DataAccess");

    public static String QUERY_KEY = bundle.getProperty("QUERY", "query");
    public static String COMMAND = bundle.getProperty("COMMAND", "c");
    public static String NUMBER_RECORDS = bundle.getProperty("NUMBER_RECORDS", "nr");
    public static String NUMBER_PAGE = bundle.getProperty("NUMBER_PAGE", "np");
    public static String START_RANK = bundle.getProperty("START_RANK", "sr");
    public static String END_RANK = bundle.getProperty("END_RANK", "er");
    public static String SORT_COLUMN = bundle.getProperty("SORT_COLUMN", "sc");
    public static String SORT_QUERY = bundle.getProperty("SORT_QUERY", "sq");
    public static String SORT_DIRECTION = bundle.getProperty("SORT_DIRECTION", "sd");
    public static int INTEGER_INPUT = bundle.getIntProperty("INTEGER_INPUT", 1001);
    public static int DECIMAL_INPUT = bundle.getIntProperty("DECIMAL_INPUT", 1002);
    public static int DATE_INPUT = bundle.getIntProperty("DATE_INPUT", 1003);
    public static int SORT_DIRECTION_INPUT = bundle.getIntProperty("SORT_DIRECTION_INPUT", 1004);
    public static int STRING_INPUT = bundle.getIntProperty("STRING_INPUT", 1005);
    public static String INPUT_DELIMITER = bundle.getProperty("INPUT_DELIMITER", "@");
    public static String SPECIAL_DEFAULT_MARKER = bundle.getProperty("SPECIAL_DEFAULT_MARKER", "$");
    public static String DATE_FORMAT = bundle.getProperty("DATE_FORMAT", "yyyy-MM-dd");
    public static int DEFAULT_EXPIRE_TIME = bundle.getIntProperty("DEFAULT_EXPIRE_TIME", 1000 * 60 * 60 * 24 * 3);

}
