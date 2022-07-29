package com.topcoder.server.docGen.xml;

import java.io.*;
import java.util.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.text.FieldPosition;


public final class ValueTag extends Tag {

    private static final boolean VERBOSE = false;
    private String name;
    private String value;
    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm:ss aaa";


    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, double value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = Double.toString(value);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, Double value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = value.toString();
    }


    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, String value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = value;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, int value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = Integer.toString(value);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, Character value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = value.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, long value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = Long.toString(value);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, float value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = Float.toString(value);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, boolean value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = new Boolean(value).toString();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, java.sql.Date value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = dateToString(value);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, java.sql.Date value, SimpleDateFormat dateFormat) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = dateToString(value, dateFormat);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, java.sql.Timestamp value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = dateToString(value);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag(String name, java.sql.Timestamp value, SimpleDateFormat dateFormat) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
        this.value = dateToString(value, dateFormat);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ValueTag() {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = null;
        this.value = null;
    }


    ////////////////////////////////////////////////////////////////////////
    private final static String dateToString(java.util.Date date) {
        ////////////////////////////////////////////////////////////////////////
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateToString(date, dateFormat);
    }


    ////////////////////////////////////////////////////////////////////////
    private final static String dateToString(java.util.Date date, SimpleDateFormat dateFormat) {
        ////////////////////////////////////////////////////////////////////////
        String result = null;
        if (date != null) {
            dateFormat.setLenient(false);
            StringBuffer buffer = new StringBuffer(dateFormat.toPattern().length());
            buffer = dateFormat.format(date, buffer, new FieldPosition(0));
            if (buffer != null) {
                result = buffer.toString();
            }
        }
        return result;
    }


    ////////////////////////////////////////////////////////////////////////////////
    public final void setName(String name) {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public final void setValue(String value) {
        ////////////////////////////////////////////////////////////////////////////////
        this.value = value;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public final void reset() {
        ////////////////////////////////////////////////////////////////////////////////
        this.name = null;
        this.value = null;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public final String getXML()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return createXML(true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public final String getXML(boolean filterOn)
            ////////////////////////////////////////////////////////////////////////////////
    {
        return createXML(filterOn);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public final String getXML(int offSet)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (offSet > 0) {
            return createOffset(offSet).append(createXML(true)).toString();
        } else {
            return createXML(true);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public final String getXML(int offSet, boolean filterOn)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (offSet > 0) {
            return createOffset(offSet).append(createXML(filterOn)).toString();
        } else {
            return createXML(filterOn);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private final String createXML(boolean filterOn)
            ////////////////////////////////////////////////////////////////////////////////
    {

        StringBuffer retVal = new StringBuffer(32);

        retVal.append("<");
        retVal.append(this.name);
        retVal.append(">");

        if (filterOn)
            retVal.append(filterChars(this.value));
        else
            retVal.append(this.value);

        retVal.append("</");
        retVal.append(this.name);
        retVal.append(">\n");

        return retVal.toString();

    }

    ////////////////////////////////////////////////////////////////////////////////
    private final StringBuffer createOffset(int offSet)
            ////////////////////////////////////////////////////////////////////////////////
    {
        StringBuffer offSetString = new StringBuffer();
        for (int i = 0; i < offSet; i++) {
            offSetString.append(" ");
        }

        return offSetString;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private final String filterChars(String str) {
        ////////////////////////////////////////////////////////////////////////////////
        /*
        <       lt	        <		Less than sign
        >       gt	        >		Greater than sign
        &       amp	        &		Ampersand
        "       quot	"		Double quote sign
        */
        if (str == null) {
            return "";
        } else {
            StringBuffer buffer = new StringBuffer(str);
            for (int i = 0; i < buffer.length(); i++) {
                char thisChar = buffer.charAt(i);
                switch (thisChar) {
                case '<':
                    buffer.replace(i, i + 1, "&lt;");
                    i += 3;
                    break;
                case '>':
                    buffer.replace(i, i + 1, "&gt;");
                    i += 3;
                    break;
                case '&':
                    buffer.replace(i, i + 1, "&amp;");
                    i += 4;
                    break;
                case '"':
                    buffer.replace(i, i + 1, "&quot;");
                    i += 5;
                    break;
                default:
                    int thisCode = (int) thisChar;
                    if (
                            !(
                            (thisCode > 31 && thisCode < 127)
                            || thisCode == 9
                            || thisCode == 10
                            || thisCode == 13
                            )
                    ) {
                        buffer.replace(i, i + 1, "[\\u" + thisCode + "]");
                    }

                }
            }
            return buffer.toString();
        }
    }

}

