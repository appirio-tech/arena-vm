package com.topcoder.shared.util.logging;

/**
 * Defines a logger factory which creates loggers based on Log4j version 1.2.
 * 
 * @author Qi Liu
 * @version $Id$
 */
final class LoggerFactoryLog4j127 implements LoggerFactory {

    public Logger getLogger(Class clazz) {
        return new LoggerLog4j127(org.apache.log4j.Logger.getLogger(clazz));
    }

    public Logger getLogger(String categoryName) {
        return new LoggerLog4j127(org.apache.log4j.Logger.getLogger(categoryName));
    }
}
