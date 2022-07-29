/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import java.awt.Color;
import java.awt.Font;

import java.util.Arrays;

/**
 * <p>
 * The HtmlOutput class implements the HighlightedOutput interface in order to provide an output plugin that outputs a
 * highlighted HTML String. This class escapes the text provided and encases each highlighted segment within a span tag
 * in order to format it. This is retrieved via the getText() method.
 * </p>
 * <p>
 * Thread Safety: This class is not thread safe.
 * </p>
 * 
 * @author duner, still
 * @version 2.0
 */
public class HtmlOutput implements HighlightedOutput {
    /**
     * The max value of charactor that can be normally displayed in HTML.
     */
    private static final int MAX = 160;

    /**
     * The number 16.
     */
    private static final int SIXTEEN = 16;

    /**
     * <p>
     * This represents a highlighted String subsequent to a call to setText. Prior to this call, it is null to indicate
     * no highlighting has taken place. Thus, a call to getText() should only be performed subsequent to a completed
     * call to setText. This is set in the setText() method and retrieved via the getText() method.
     * </p>
     */
    private String text = null;

    /**
     * <p>
     * Default constructor create a new HtmlOutput.
     * </p>
     */
    public HtmlOutput() {
        // empty constructor
    }

    /**
     * Creates a new instance of <code>HtmlOutput</code>. The number of spaces replacing the TAB character is given.
     * 
     * @param tab the number of spaces replacing the TAB character.
     */
    public HtmlOutput(int tab) {
        tabSize = tab;
    }

    /**
     * Represents the number of spaces replacing the TAB character.
     */
    private static int tabSize = 4;

    /**
     * <p>
     * This method visit all the segments and form an HTML String from them.
     * </p>
     * 
     * @param contentSegments An array of ContentSegments instances use to form the document. This should be ordered
     *            appropriately such that the document can be created simply be iterating the array.
     * @throws NullPointerException if contentSegment is null.
     * @throws IllegalArgumentException if any elements of contentSegment are null
     */
    public void setText(ContentSegment[] contentSegments) {
        SHHelper.checkNull(contentSegments, "contentSegments");
        if (contentSegments.length == 0) {
            throw new IllegalArgumentException("contentSegments should not contain no elements.");
        }
        // always here contentSegments is sorted by HighlightedSequence.getOrderedSegments
        // however sort it here make this method always do the right thing.
        try {
            Arrays.sort(contentSegments);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Some elements of contentSegments are of wrong type.");
        }

        // use StringBuffer to save the result
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < contentSegments.length; ++i) {
            if (contentSegments[i] == null) {
                throw new IllegalArgumentException("Some elements of contentSegments are null or illegal.");
            }

            // ContentSegment should never contain null or empty content
            String contentText = stringToHtml(contentSegments[i].getContent());
            TextStyle style = contentSegments[i].getStyle();

            if (style == null) {
                // default text
                buffer.append(contentText);
            } else {
                // make style string
                StringBuffer styleBuf = new StringBuffer();
                Font font = style.getFont();
                Color color = style.getColor();
                Color bgColor = style.getBGColor();

                // append font color and bgColor if needed
                if (font != null) {
                    styleBuf.append("font-family: " + font.getFamily());
                    styleBuf.append("; font-size: " + font.getSize() + "pt");

                    if (font.isItalic()) {
                        styleBuf.append("; font-style: italic");
                    }

                    if (font.isBold()) {
                        styleBuf.append("; font-weight: bold");
                    }

                    styleBuf.append("; ");
                }

                if (color != null) {
                    styleBuf.append("color: " + colorToString(color) + "; ");
                }

                if (bgColor != null) {
                    styleBuf.append("background-color: " + colorToString(bgColor) + "; ");
                }

                String styleStr = styleBuf.toString();

                // make the result html segment
                if (styleStr.trim().length() > 0) {
                    buffer.append("<font style=\"" + styleStr + "\">" + contentText + "</font>");
                } else {
                    buffer.append(contentText);
                }
            }
        }

        text = buffer.toString();
        text = text.replaceAll("\r", "");
    }

    /**
     * <p>
     * Returns a highlighted HTML string that was highlighted in setText().
     * </p>
     * 
     * @return retuns the text attribute.
     */
    public String getText() {
        return text;
    }

    /**
     * <p>
     * This method takes a normal String and makes it suitable string for display as part of an HTML document.
     * </p>
     * 
     * @param content The String that is to be HTML-ized.
     * @return The content String suitable for use in an HTML document.
     */
    private String stringToHtml(String content) {
        return convertStringToHtml(content);
    }

    /**
     * <p>
     * This method make the color to string (#RRGGBB).
     * </p>
     * 
     * @param color the color to be converted.
     * @return html color expression of the color.
     */
    private static String colorToString(Color color) {

        String str = "#";
        str += HexString(color.getRed());
        str += HexString(color.getGreen());
        str += HexString(color.getBlue());

        return str;
    }

    /**
     * <p>
     * This method makes number from 0 to 255 to a hex string. If the number is larger than 255, its low part(i%25) is
     * converted.
     * </p>
     * 
     * @param i the number to be converted
     * @return return hexstring of i
     */
    private static String HexString(int i) {
        return Integer.toHexString(i / SIXTEEN) + Integer.toHexString(i % SIXTEEN);
    }

    /**
     * Converts a string into HTML text. All TAB characters are replaced by spaces. The returned HTML text is properly
     * escaped.
     * 
     * @param content the string to be converted into HTML.
     * @return the converted HTML text.
     */
    public static String convertStringToHtml(String content) {
        if (content == null) {
            return null;
        }

        String tabs = "";
        for (int i = 0; i < tabSize; i++) {
            tabs += " ";
        }

        content = content.replaceAll("\\t", tabs);

        StringBuffer buffer = new StringBuffer(content.length());

        // to record the number of spaces
        int spaceCount = 0;
        // change special charactors to the relative string
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i); // get the next character, nextChar() is some made up method
            if (c == ' ') {
                // see the doc for space processing
                buffer.append("&nbsp;");
                spaceCount++;
                continue;
            }
            spaceCount = 0;

            switch (c) {
            case '"':
                buffer.append("&quot;");
                break;

            case '&':
                buffer.append("&amp;");
                break;

            case '<':
                buffer.append("&lt;");
                break;

            case '>':
                buffer.append("&gt;");
                break;

            case '\n':
                buffer.append("\n<br>");
                break;

            default:
                if (c >= MAX) {
                    buffer.append("&#" + (short) c + ";");
                } else {
                    buffer.append(c);
                }
                break;
            }
        }

        return buffer.toString();
    }

    /**
     * Creates a SPAN tag around the given text. The font, text color and background color of the text are given. The
     * given text is expected to be properly escaped.
     * 
     * @param contentText the text to be wrapped by SPAN tag.
     * @param font the font used to render the text.
     * @param color the text color used to render the text.
     * @param bgColor the background color used to render the text.
     * @return the HTML text which can render the given text using the specified font, text color and background color.
     */
    public static String span(String contentText, Font font, Color color, Color bgColor) {
        StringBuffer buffer = new StringBuffer();
        StringBuffer styleBuf = new StringBuffer();

        // append font color and bgColor if needed
        if (font != null) {
            styleBuf.append("font-family: " + font.getFamily());
            styleBuf.append("; font-size: " + font.getSize() + "pt");

            if (font.isItalic()) {
                styleBuf.append("; font-style: italic");
            }

            if (font.isBold()) {
                styleBuf.append("; font-weight: bold");
            }

            styleBuf.append("; ");
        }

        if (color != null) {
            styleBuf.append("color: " + colorToString(color) + "; ");
        }

        if (bgColor != null) {
            styleBuf.append("background-color: " + colorToString(bgColor) + "; ");
        }

        String styleStr = styleBuf.toString();

        // make the result html segment
        if (styleStr.trim().length() > 0) {
            buffer.append("<font style=\"" + styleStr + "\">" + contentText + "</font>");
        } else {
            buffer.append(contentText);
        }
        return buffer.toString();
    }
}
