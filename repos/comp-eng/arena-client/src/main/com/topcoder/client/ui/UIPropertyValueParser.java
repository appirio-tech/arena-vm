package com.topcoder.client.ui;

/**
 * Defines an interface which will parse a property value from string to a desired instance.
 *
 * @version 1.0
 * @author visualage
 */
public interface UIPropertyValueParser {
    /**
     * Parses the given value into a proper instance.
     * @param page the UI page where the parser is used.
     * @param value the string value to be parsed.
     * @param loader the class loader used in this class.
     * @return the parsed instance.
     * @throws IllegalArgumentException the given value is invalid for this parser.
     */
    Object parse(UIPage page, String value, ClassLoader loader);
}
