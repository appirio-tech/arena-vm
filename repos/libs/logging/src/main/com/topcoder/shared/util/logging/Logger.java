package com.topcoder.shared.util.logging;

/**
 * Defines an abstract class which provides common features of all different logging mechanisms. It provides different
 * logging levels. From the lowest level to the highest level are <code>TRACE</code>, <code>DEBUG</code>,
 * <code>INFO</code>, <code>WARN</code>, <code>ERROR</code>, and <code>FATAL</code> respectively. Presumbly, lower level
 * should contain more detailed information than higher level.
 * 
 * @author Qi Liu
 * @version $Id$
 */
public abstract class Logger {
    /** Represents the singleton instance of the logger factory used. */
    private static final LoggerFactory LOGGER_FACTORY;

    /**
     * Initializes the logger factory singleton instance.
     */
    static {
        // At present, we use Log4j 1.2+ library
        LOGGER_FACTORY = new LoggerFactoryLog4j127();
    }

    /**
     * Gets a logger for the given class.
     * 
     * @param clazz the class whose logger is returned.
     * @return a logger for the given class.
     */
    public static Logger getLogger(Class clazz) {
        return LOGGER_FACTORY.getLogger(clazz);
    }

    /**
     * Gets a logger for the given category.
     * 
     * @param categoryName the name of the category whose logger is returned.
     * @return a logger for the given category.
     */
    public static Logger getLogger(String categoryName) {
        return LOGGER_FACTORY.getLogger(categoryName);
    }

    /**
     * Logs a message object at <code>DEBUG</code> level.
     * 
     * @param message the message object to be logged.
     */
    public abstract void debug(Object message);

    /**
     * Logs a message object and a stack trace at <code>DEBUG</code> level.
     * 
     * @param message the message object to be logged.
     * @param t the stack trace to be logged.
     */
    public abstract void debug(Object message, Throwable t);

    /**
     * Logs a message object at <code>INFO</code> level.
     * 
     * @param message the message object to be logged.
     */
    public abstract void info(Object message);

    /**
     * Logs a message object and a stack trace at <code>INFO</code> level.
     * 
     * @param message the message object to be logged.
     * @param t the stack trace to be logged.
     */
    public abstract void info(Object message, Throwable t);

    /**
     * Logs a message object at <code>WARN</code> level.
     * 
     * @param message the message object to be logged.
     */
    public abstract void warn(Object message);

    /**
     * Logs a message object and a stack trace at <code>WARN</code> level.
     * 
     * @param message the message object to be logged.
     * @param t the stack trace to be logged.
     */
    public abstract void warn(Object message, Throwable t);

    /**
     * Logs a message object at <code>ERROR</code> level.
     * 
     * @param message the message object to be logged.
     */
    public abstract void error(Object message);

    /**
     * Logs a message object and a stack trace at <code>ERROR</code> level.
     * 
     * @param message the message object to be logged.
     * @param t the stack trace to be logged.
     */
    public abstract void error(Object message, Throwable t);

    /**
     * Logs a message object at <code>FATAL</code> level.
     * 
     * @param message the message object to be logged.
     */
    public abstract void fatal(Object message);

    /**
     * Logs a message object and a stack trace at <code>FATAL</code> level.
     * 
     * @param message the message object to be logged.
     * @param t the stack trace to be logged.
     */
    public abstract void fatal(Object message, Throwable t);

    /**
     * Gets a flag indicating if <code>DEBUG</code> level logging is available.
     * 
     * @return <code>true</code> if <code>DEBUG</code> level logging is available; <code>false</code> otherwise.
     */
    public abstract boolean isDebugEnabled();

    /**
     * Gets a flag indicating if <code>INFO</code> level logging is available.
     * 
     * @return <code>true</code> if <code>INFO</code> level logging is available; <code>false</code> otherwise.
     */
    public abstract boolean isInfoEnabled();

    /**
     * Gets a flag indicating if <code>TRACE</code> level logging is available.
     * 
     * @return <code>true</code> if <code>TRACE</code> level logging is available; <code>false</code> otherwise.
     */
    public abstract boolean isTraceEnabled();

    /**
     * Logs a message object and a stack trace at <code>TRACE</code> level.
     * 
     * @param message the message object to be logged.
     * @param t the stack trace to be logged.
     */
    public abstract void trace(Object message, Throwable t);

    /**
     * Logs a message object at <code>TRACE</code> level.
     * 
     * @param message the message object to be logged.
     */
    public abstract void trace(Object message);
}
