package com.topcoder.shared.problem;

/**
 * Constants for use by problem
 * 
 * @author Qi Liu
 * @version $Id: ProblemConstants.java 71771 2008-07-18 05:34:07Z qliu $
 */
public class ProblemConstants {
    /** Represents the primary component of an algorithm problem. */
    public static int MAIN_COMPONENT = 1;

    /** Represents a marathon problem. */
    public static int LONG_COMPONENT = 2;

    /** Represents the secondary component of a team algorithm problem. */
    public static int SECONDARY_COMPONENT = 0;

    /** Problem Type ID for a team problem. */
    public static int TEAM_PROBLEM = 2;

    /** Represents the array of characters need to be escaped in XML. */
    public static char[] BAD_XML_CHARS = {'<', '>', '&', ':', ';', '\'', '"'};

    /** Represents the class prefix responsible for I/O access during a test of marathon solution. */
    public static String TESTER_IO_CLASS = "LongTest";

    /** Represents the wrapper class prefix during a test of marathon solution. */
    public static String WRAPPER_CLASS = "Wrapper";
}
