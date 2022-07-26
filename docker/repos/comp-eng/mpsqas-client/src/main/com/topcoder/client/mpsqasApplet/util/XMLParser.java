package com.topcoder.client.mpsqasApplet.util;

/**
 * XML parser contains quick methods to work with XML.
 *
 * @author mitalub
 */
public class XMLParser {

    /**
     * Removes the outer most tag in a String of XML.  For example,
     * <code>removeOuterTag("<intro><b>text</b></intro>");</code>
     * will return <code>"<b>text</b>"</code>.  If two tags cannot be found
     * at the beginning and end of the xml string, the original argument is
     * returned.
     *
     * @param xml The xml string to modify.
     */
    public static String removeOuterTag(String xml) {
        int endStartTag = xml.indexOf(">");
        int startEndTag = xml.lastIndexOf("<");
        if (endStartTag < startEndTag && endStartTag != -1 && startEndTag != -1) {
            return xml.substring(endStartTag + 1, startEndTag);
        } else {
            return xml;
        }
    }
}
