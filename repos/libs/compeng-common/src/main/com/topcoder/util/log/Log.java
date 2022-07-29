/*
 * Copyright (c) 2004, TopCoder, Inc. All rights reserved
 */
package com.topcoder.util.log;

/**
 * <p>
 * The Log interface should be extended by classes that wish to provide a custom
 * logging implementation. The <tt>log</tt> method is used to log a message
 * using the underlying implementation, and the <tt>isEnabled</tt> method is
 * used to determine if a specific logging level is currently being logged.
 * </p>
 * <p>
 * This class is not meant to be instantiated directly. The LogFactory class
 * should be used to create instances. The main idea of this factory class is
 * to create actual Log instances based on a configuration file. By simply
 * changing the configuration file, different implementations will be created
 * by this class. This allows the easy swapping of different logging
 * implementation with no code modifications.
 * </p>
 * <p>
 * <b>Note:</b> All implementations of this class must implement a public constructor
 * that accepts a String as parameter (the name for the instance of the Log
 * implementation that is to be created).
 * </p>
 * <p>
 * Also, as of version 1.2, it is expected that all implementations of the Log
 * interface must swallow all exceptions.  This is to prevent an error in the 
 * logging process from causing the entire system to fail.
 * </p>
 *
 * @author StinkyCheeseMan, TCSDEVELOPER
 * @author adic, ShindouHikaru
 * @version 1.2
 */
public interface Log {

    /**
     * <p>
     * Logs a given message using a given logging level (see the Level class).
     * Any argument is accepted and no exceptions are thrown.
     * </p>
     * <p>
     * <b>Valid args:</b> any Level and Object instance, including nulls. <br />
     * <b>Invalid args:</b> N/A <br />
     * </p>
     * @param level the level at which the message should be logged
     * @param message the message to log
     */
    void log(Level level, Object message);

    /**
     * <p>
     * Returns whether the given level is enabled for a specific
     * implementation.
     * </p>
     * <p>
     * <b>Valid args:</b> any Level instance, including null <br />
     * <b>Invalid args:</b> N/A <br />
     * </p>
     * @param level the level to check
     *
     * @return true if the level is enabled, false otherwise;
     */
    boolean isEnabled(Level level);

}

