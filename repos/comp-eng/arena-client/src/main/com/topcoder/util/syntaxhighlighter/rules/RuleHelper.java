/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter.rules;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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
final class RuleHelper {
    /**
     * Constructor to prevent creation.
     */
    private RuleHelper() {
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
     * @throws IllegalArgumentException if no such text node found, or text is empty(trimed).
     */
    public static String getNodeText(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Null node is illegal.");
        }
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new IllegalArgumentException("The node must be an element node.");
        }
        String nodeName = node.getNodeName();

        // get the node value and perform null and empty string check
        for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                String value = node.getNodeValue();

                if ((value == null) || (value.trim().length() == 0)) {
                    throw new IllegalArgumentException("Value of property '" + nodeName + "' is illegal.");
                }

                return value;
            }
        }

        throw new IllegalArgumentException("Text node cann't be found.");
    }

    /**<p> The same as getNodeText, the only difference is that it doesn't perform trimed empty string check.
     * </p>
     * @param node the node which contains a text node as its child.
     * @return the text of node's child text node.
     * @throws IllegalArgumentException if no such text node found, or text is empty.
     */
    public static String getNodeTextWithoutTrimEmptyCheck(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Null node is illegal.");
        }
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new IllegalArgumentException("The node must be an element node.");
        }
        String nodeName = node.getNodeName();
        // get the node value and perform null and empty string check
        for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                String value = node.getNodeValue();

                if ((value == null) || (value.length() == 0)) {
                    throw new IllegalArgumentException("Value of property '" + nodeName + "' cannot be found.");
                }

                return value;
            }
        }

        throw new IllegalArgumentException("Text node cann't be found.");
    }

    /**
     * Checks if the parameter param is a valid String, i.e. non-null and non-empty string. Caller should ensure
     * that the paramName is not null nor empty. (here empty stand just for empty, not for trimed empty).
     *
     * @param param the parameter string of the caller method to be checked.
     * @param paramName a non-null non-empty string, which is the parameter name of the param in the caller method.
     * @throws NullPointerException if the param is null.
     * @throws IllegalArgumentException if the param is an empty string.
     */
    public static void checkNullEmptyParam(String param, String paramName) {
        checkNull(param, paramName);
        if (param.length() == 0) {
            throw new IllegalArgumentException("Paramteter '" + paramName + "' should not be empty.");
        }
    }
    /**
     * <p>Checks if parent have only one child element named elementName and return the child element. The caller
     * should ensure parent and elementName not be null and elementName not be empty.</p>
     * @param parent the parent from which to find element.
     * @param elementName the element name of the expected element.
     * @return the child element named elementName of parent.
     * @throws IllegalArgumentException when parent don't have such child element or have more than one such elements.
     */
    public static Element getSingleChildElementByName(Element parent, String elementName) {
        NodeList list = parent.getElementsByTagName(elementName);
        if (list.getLength() > 1) {
            throw new IllegalArgumentException(parent.getNodeName() + " have more than one " + elementName
                + " elements.");
        } else if (list.getLength() == 0) {
            throw new IllegalArgumentException(parent.getNodeName() + " don't have " + elementName
                + " element.");
        }
        Node node = list.item(0);
        if (node.getParentNode() == parent) {
            return (Element) node;
        } else {
            throw new IllegalArgumentException(parent.getNodeName() + " don't have child " + elementName
                    + " element.");
        }
    }
}
