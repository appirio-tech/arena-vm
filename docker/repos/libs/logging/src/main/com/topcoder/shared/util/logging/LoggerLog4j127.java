package com.topcoder.shared.util.logging;

/**
 * Defines a sub-class of <code>Logger</code> which uses Log4j 1.2+ library to log the messages. Most methods are
 * simple delegater of the corresponding Log4j <code>org.apache.log4j.Logger</code> class.
 * 
 * @author Qi Liu
 * @see Logger
 * @version $Id$
 */
final class LoggerLog4j127 extends com.topcoder.shared.util.logging.Logger {
    /** Represents the Log4j logger used to log all messages. */
    private final org.apache.log4j.Logger logger;

    /**
     * Creates a new instance of <code>LoggerLog4j127</code> class. The logging is done via the given Log4j logger. It
     * is incorrect to directly create an instance of this class. Developer should use <code>Logger.getLogger</code>.
     * 
     * @see Logger.getLogger
     * @param logger the Log4j logger used to actually log the messages.
     */
    LoggerLog4j127(org.apache.log4j.Logger logger) {
        this.logger = logger;
    }

    public void debug(Object message) {
        logger.debug(message);
    }

    public void debug(Object message, Throwable t) {
        logger.debug(message, t);
    }

    public void info(Object message) {
        logger.info(message);
    }

    public void info(Object message, Throwable t) {
        logger.info(message, t);
    }

    public void warn(Object message) {
        logger.warn(message);
    }

    public void warn(Object message, Throwable t) {
        logger.warn(message, t);
    }

    public void error(Object message) {
        logger.error(message);
    }

    public void error(Object message, Throwable t) {
        logger.error(message, t);
    }

    public void fatal(Object message) {
        logger.fatal(message);
    }

    public void fatal(Object message, Throwable t) {
        logger.fatal(message, t);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public void trace(Object message, Throwable t) {
        logger.trace(message, t);
    }

    public void trace(Object message) {
        logger.trace(message);
    }

}
