package com.topcoder.shared.util.logging;

/**
 * Defines a factory which can create loggers according to the class or the category name. 
 * 
 * @author Qi Liu
 * @version $Id$
 */
interface LoggerFactory {
    /**
     * Gets a logger for the given class.
     * 
     * @param clazz the class whose logger is returned.
     * @return a logger for the given class.
     */
    Logger getLogger(Class clazz);
    
    /**
     * Gets a logger for the given category name.
     * @param categoryName the category name whose logger is returned.
     * @return a logger for the given category name.
     */
    Logger getLogger(String categoryName);

}
