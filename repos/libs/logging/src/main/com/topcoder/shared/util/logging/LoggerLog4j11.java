package com.topcoder.shared.util.logging;

import org.apache.log4j.Category;

/**
 * Defines a sub-class of <code>Logger</code> which uses Log4j 1.1 library to log the messages. Most methods are
 * simple delegater of the corresponding Log4j <code>org.apache.log4j.Category</code> class. Since Log4j 1.1 does
 * not have <code>TRACE</code> and <code>INFO</code> level, <code>DEBUG</code> level is used instead.
 * 
 * @author Qi Liu
 * @see Logger
 * @see LoggerLog4j127
 * @deprecated it is replaced by <code>LoggerLog4j127</code>.
 * @version $Id$
 */
final class LoggerLog4j11 extends Logger {
    /** Represents the Log4j logger used to log all messages. */
    private final Category category;

    /**
     * Creates a new instance of <code>LoggerLog4j11</code> class. The logging is done via the given Log4j logger. It
     * is incorrect to directly create an instance of this class. Developer should use <code>Logger.getLogger</code>.
     * 
     * @see Logger.getLogger
     * @param category the Log4j logger used to actually log the messages.
     */
    LoggerLog4j11(Category category) {
        this.category = category;
    }

    public void debug(Object message) {
        category.debug(message);
    }

    public void debug(Object message, Throwable t) {
        category.debug(message, t);
    }

    public void info(Object message) {
        category.info(message);
    }

    public void info(Object message, Throwable t) {
        category.info(message, t);
    }

    public void warn(Object message) {
        category.warn(message);
    }

    public void warn(Object message, Throwable t) {
        category.warn(message, t);
    }

    public void error(Object message) {
        category.error(message);
    }

    public void error(Object message, Throwable t) {
        category.error(message, t);
    }

    public void fatal(Object message) {
        category.fatal(message);
    }

    public void fatal(Object message, Throwable t) {
        category.fatal(message, t);
    }

    public boolean isDebugEnabled() {
        return category.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return category.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return category.isDebugEnabled();
    }

    public void trace(Object message, Throwable t) {
        category.debug(message, t);
    }

    public void trace(Object message) {
        category.debug(message);
    }

}
