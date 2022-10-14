package com.topcoder.shared.util.logging;

import org.apache.log4j.Category;

/**
 * Defines a logger factory which creates loggers based on Log4j version 1.1.
 * 
 * @author Qi Liu
 * @deprecated it is better to use Log4j 1.2+ library.
 * @see LoggerFactoryLog4j127
 * @version $Id$
 */
final class LoggerFactoryLog4j11 implements LoggerFactory {
    public Logger getLogger(Class clazz) {
        return new LoggerLog4j11(Category.getInstance(clazz));
    }

    public Logger getLogger(String categoryName) {
        return new LoggerLog4j11(Category.getInstance(categoryName));
    }

}
