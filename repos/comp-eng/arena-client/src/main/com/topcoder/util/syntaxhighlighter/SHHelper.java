/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.List;


/**
 * <p>
 * This is a helper class providing routine processing for this component.
 * This class and the checkNull, checkNegative and checkString methods are made public so that the three
 * methods can be used in the com.topcoder.util.syntaxhighlighter.rules package, application should not use
 * these methods.
 * </p>
 *
 * @author duner, still
 * @version 2.0
 */
final class SHHelper {
    /**
     * Constructor to prevent creation.
     */
    private SHHelper() {
        // do nothing
    }

    /**
     * Checks if the parameter param is null, if it is, throw NullPointerException. Caller should ensure that
     * the paramName is not null nor empty.
     *
     * @param param the parameter of the caller method to be checked.
     * @param paramName a non-null non-empty string, which is the parameter name of the param in the caller method.
     * @throws NullPointerException if the param is null.
     */
    public static void checkNull(Object param, String paramName) {
        if (param == null) {
            throw new NullPointerException("The parameter '" + paramName + "' should not be null.");
        }
    }

    /**
     * <p> Get the text of node's child text node. </p>
     *
     * @param node the node which contains a text node as its child.
     * @return the text of node's child text node.
     * @throws ConfigurationException if no such text node found, or text is empty(trimed).
     */
    public static String getNodeText(Node node) throws ConfigurationException {
        if (node == null) {
            throw new ConfigurationException("Null node is illegal.");
        }
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new ConfigurationException("The node must be an element node.");
        }
        String nodeName = node.getNodeName();

        // get the node value and perform null and empty string check
        for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                String value = node.getNodeValue();

                if ((value == null) || (value.trim().length() == 0)) {
                    throw new ConfigurationException("Value of property '" + nodeName + "' is illegal.");
                }

                return value;
            }
        }

        throw new ConfigurationException("Text node can't be found.");
    }



    /**
     * <p> Get the value of node's child text node as a integer.</p>
     * @param node the node to whose child text to be get as integer.
     * @return the int value of the only text node of node's child.
     * @throws ConfigurationException if node text is not existed or are not number string.
     */
    public static int getNodeInteger(Node node) throws ConfigurationException {
        if (node == null) {
            throw new ConfigurationException("Null node is illegal.");
        }
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new ConfigurationException("The node must be an element node.");
        }
        String nodeName = node.getNodeName();
        String value = getNodeText(node);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Parse integer error in property '" + nodeName + "'.");
        }
    }

    /**
     * Checks if the parameter param is a valid String, i.e. non-null and non-empty string. Caller should ensure
     * that the paramName is not null nor empty.
     *
     * @param param the parameter string of the caller method to be checked.
     * @param paramName a non-null non-empty string, which is the parameter name of the param in the caller method.
     * @throws NullPointerException if the param is null.
     * @throws IllegalArgumentException if the param is an empty string(trimed).
     */
    public static void checkString(String param, String paramName) {
        checkNull(param, paramName);

        if (param.trim().length() == 0) {
            throw new IllegalArgumentException("The parameter '" + paramName
                + "' should not be an empty string.");
        }
    }

    /**
     * Checks if the parameter param is negative, if it is, throw IllegalArgumentException. Caller should ensure that
     * the paramName is not null nor empty.
     *
     * @param param the parameter of the caller method to be checked.
     * @param paramName a non-null non-empty string, which is the parameter name of the param in the caller method.
     * @throws IllegalArgumentException if the param is negative.
     */
    public static void checkNegative(int param, String paramName) {
        if (param < 0) {
            throw new IllegalArgumentException("The parameter '" + paramName + "' should not be negative.");
        }
    }

    /**
     * Checks if the list is valid, it is not null and should contain only instances of the given type.
     * Caller should ensure that the paramName is not null nor empty and the type is not null.
     *
     * @param list the list to be checked.
     * @param paramName a non-null non-empty string, which is the parameter name of the param in the caller method.
     * @param type the expected type of the elements of the list, it must not be null.
     * @throws NullPointerException if the list is null.
     * @throws IllegalArgumentException if the list contains null or invalid element.
     */
    public static void checkList(List list, String paramName, Class type) {
        checkNull(list, paramName);

        for (Iterator i = list.iterator(); i.hasNext();) {
            Object o = i.next();

            if (o == null) {
                throw new IllegalArgumentException("The " + paramName + " contains a null element.");
            }

            if (!type.isInstance(o)) {
                throw new IllegalArgumentException("The " + paramName
                    + " contains an element not implementing " + type.getName() + ".");
            }
        }
    }

    /**
     * Checks if the configuration string is valid, i.e. non-null and non-empty string. Caller should ensure that
     * the propertyName is not null nor empty.
     *
     * @param param
     *            the configuration string to be checked.
     * @param propertyName
     *            a non-null non-empty string, which is the property name of the param.
     * @throws ConfigurationException
     *             if the param is null or empty.
     */
    public static void checkConfigString(String param, String propertyName)
        throws ConfigurationException {

        if (param == null) {
            throw new ConfigurationException("The property '" + propertyName + "' is missing.");
        }

        if (param.trim().length() == 0) {
            throw new ConfigurationException("Value of the property '" + propertyName
                    + "' should not be an empty string(trimed).");
        }
    }

    /**
     * <p>
     * This method check if
     *
     * <pre>
     *  0  =&lt; start &lt; end ,
     * </pre>
     *
     * if not ,IllegalArgumentException is thrown.
     * </p>
     *
     * @param start
     *            the start to be checked.
     * @param end
     *            the end to be checked.
     * @throws IllegalArgumentException
     *             if  start or end is negative, or start is larger than end
     */
    public static void checkStartEnd(int start, int end) {
        if (start < 0) {
            throw new IllegalArgumentException("Paramenter 'start' with negative value is illegal.");
        }
        if (end < 0) {
            throw new IllegalArgumentException("Paramenter 'end' with negative value is illegal.");
        }
        if (start > end) {
            throw new IllegalArgumentException("Parameter 'start' larger than 'end' was illegal.");
        }
    }


}
