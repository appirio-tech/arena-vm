/*
 * Copyright (c) 2004, TopCoder, Inc. All rights reserved
 */
package com.topcoder.util.log;

import com.topcoder.util.collection.typesafeenum.Enum;

/**
 * <p>
 * The Level class maintains the list of acceptable logging levels.
 * It provides the user this easy access to predefined logging levels thought
 * the constants defined in this class.
 * </p>
 * <p>
 * Extends the Enum class from the Typesafe Enumeration component to make serialization
 * safe to be consistent across the TopCoder Catalog.
 * </p>
 * <p>
 * The levels in descending order are:
 * <ul>
 * <li>OFF (lowest)</li>
 * <li>FATAL</li>
 * <li>ERROR</li>
 * <li>WARN</li>
 * <li>INFO</li>
 * <li>CONFIG</li>
 * <li>DEBUG</li>
 * <li>TRACE</li>
 * <li>FINEST</li>
 * <li>ALL (highest)</li>
 * </ul>
 * </p>
 *
 * @author StinkyCheeseMan, TCSDEVELOPER
 * @author adic, ShindouHikaru
 * @version 1.2
 */
public final class Level extends Enum {

    /**
     * Constant for the OFF logging level (the int value).
     */
    private static final int OFF_LEVEL = 0;

    /**
     * Constant for the FINEST logging level (the int value).
     */
    private static final int FINEST_LEVEL = 100;

    /**
     * Constant for the TRACE logging level (the int value).
     */
    private static final int TRACE_LEVEL = 200;

    /**
     * Constant for the DEBUG logging level (the int value).
     */
    private static final int DEBUG_LEVEL = 300;

    /**
     * Constant for the CONFIG logging level (the int value).
     */
    private static final int CONFIG_LEVEL = 400;

    /**
     * Constant for the INFO logging level (the int value).
     */
    private static final int INFO_LEVEL = 500;

    /**
     * Constant for the WARN logging level (the int value).
     */
    private static final int WARN_LEVEL = 600;

    /**
     * Constant for the ERROR logging level (the int value).
     */
    private static final int ERROR_LEVEL = 700;

    /**
     * Constant for the FATAL logging level (the int value).
     */
    private static final int FATAL_LEVEL = 800;

    /**
     * Constant for the ALL logging level (the int value).
     */
    private static final int ALL_LEVEL = 900;

    /**
     * Public constant for the OFF logging level.
     */
    public static final Level OFF = new Level(OFF_LEVEL);

    /**
     * Public constant for the FINEST logging level.
     */
    public static final Level FINEST = new Level(FINEST_LEVEL);

    /**
     * Public constant for the TRACE logging level.
     */
    public static final Level TRACE = new Level(TRACE_LEVEL);

    /**
     * Public constant for the DEBUG logging level.
     */
    public static final Level DEBUG = new Level(DEBUG_LEVEL);

    /**
     * Public constant for the CONFIG logging level.
     */
    public static final Level CONFIG = new Level(CONFIG_LEVEL);

    /**
     * Public constant for the INFO logging level.
     */
    public static final Level INFO = new Level(INFO_LEVEL);

    /**
     * Public constant for the WARN logging level.
     */
    public static final Level WARN = new Level(WARN_LEVEL);

    /**
     * Public constant for the ERROR logging level.
     */
    public static final Level ERROR = new Level(ERROR_LEVEL);

    /**
     * Public constant for the FATAL logging level.
     */
    public static final Level FATAL = new Level(FATAL_LEVEL);

    /**
     * Public constant for the ALL logging level.
     */
    public static final Level ALL = new Level(ALL_LEVEL);

    /**
     * Member field to indicate the value of the level of this instance.
     */
    private int level = 0;

    /**
     * <p>
     * Contructor is private to prevent instantiation outside of the class.
     * </p>
     * <p>
     * <b>Valid args:</b> any integer <br />
     * <b>Invalid args:</b> N/A <br />
     * </p>
     * @param level The value of this logging level.
     */
    private Level(int level) {
        super();
        this.level = level;
    }

    /**
     * <p>
     * This method returns the integer representation of the level.
     * </p>
     * <p>
     * <b>Valid args:</b> N/A <br />
     * <b>Invalid args:</b> N/A <br />
     * </p>
     * @return the integer representation of the level
     */
    public final int intValue() {
        return level;
    }

    /**
     * <p>
     * Override the equals method to allow Level objects to be compared for
     * equality.
     * </p>
     * <p>
     * <b>Valid args:</b> any object, including null <br />
     * <b>Invalid args:</b> N/A <br />
     * </p>
     * @param level the level object to be compared to this object for equality
     *
     * @return true if the Level objects are equal, false otherwise
     */
    public final boolean equals(Object level) {
        if (level == this) {
            return true;
        }

        if (!(level instanceof Level)) {
            return false;
        }

        Level comparisonLevel = (Level) level;

        return (comparisonLevel.intValue() == this.level);
    }

    /**
     * <p>
     * Override the hashCode method (required because equals is overriden).
     * </p>
     * <p>
     * <b>Valid args:</b> N/A <br />
     * <b>Invalid args:</b> N/A <br />
     * </p>
     * @return the hash code for this instance (the value of the level
     * attribute)
     */
    public final int hashCode() {
        return level;
    }

    /**
     * <p>
     * Overrides the toString method to return a human-readable representation
     * of the Level object.
     * </p>
     * <p>
     * <b>Valid args:</b> N/A <br />
     * <b>Invalid args:</b> N/A <br />
     * </p>
     * @return String - human-readable representation of the Level
     */
    public final String toString() {
        if (this == OFF) {
            return "OFF";
        } else if (this == FINEST) {
            return "FINEST";
        } else if (this == TRACE) {
            return "TRACE";
        } else if (this == DEBUG) {
            return "DEBUG";
        } else if (this == CONFIG) {
            return "CONFIG";
        } else if (this == INFO) {
            return "INFO";
        } else if (this == WARN) {
            return "WARN";
        } else if (this == ERROR) {
            return "ERROR";
        } else if (this == FATAL) {
            return "FATAL";
        } else if (this == ALL) {
            return "ALL";
        } else {
            return "INVALID LEVEL";
        }
    }

}

