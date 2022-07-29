package com.topcoder.shared.docGen.xml;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;


/**
 * @author Steve Burrows
 * @version  $Revision$
 */
public final class ValueTag extends Tag {

    private String name;
    private String value;
    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm:ss aaa";

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, double value) {
        this.name = name;
        this.value = Double.toString(value);
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, Double value) {
        this.name = name;
        this.value = value.toString();
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, int value) {
        this.name = name;
        this.value = Integer.toString(value);
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, Character value) {
        this.name = name;
        this.value = value.toString();
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, long value) {
        this.name = name;
        this.value = Long.toString(value);
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, float value) {
        this.name = name;
        this.value = Float.toString(value);
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, boolean value) {
        this.name = name;
        this.value = new Boolean(value).toString();
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, java.sql.Date value) {
        this.name = name;
        this.value = dateToString(value);
    }

    /**
     *
     * @param name
     * @param value
     * @param dateFormat
     */
    public ValueTag(String name, java.sql.Date value, SimpleDateFormat dateFormat) {
        this.name = name;
        this.value = dateToString(value, dateFormat);
    }

    /**
     *
     * @param name
     * @param value
     */
    public ValueTag(String name, java.sql.Timestamp value) {
        this.name = name;
        this.value = dateToString(value);
    }

    /**
     *
     * @param name
     * @param value
     * @param dateFormat
     */
    public ValueTag(String name, java.sql.Timestamp value, SimpleDateFormat dateFormat) {
        this.name = name;
        this.value = dateToString(value, dateFormat);
    }

    /**
     *
     */
    public ValueTag() {
        this.name = null;
        this.value = null;
    }

    /**
     *
     * @param date
     * @return
     */
    private final static String dateToString(java.util.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateToString(date, dateFormat);
    }

    /**
     *
     * @param date
     * @param dateFormat
     * @return
     */
    private final static String dateToString(java.util.Date date, SimpleDateFormat dateFormat) {
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

    /**
     *
     * @param name
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param value
     */
    public final void setValue(String value) {
        this.value = value;
    }

    /**
     *
     */
    public final void reset() {
        this.name = null;
        this.value = null;
    }

    /**
     *
     * @return
     */
    public final String getXML() {
        return createXML(true);
    }

    /**
     *
     * @param filterOn
     * @return
     */
    public final String getXML(boolean filterOn) {
        return createXML(filterOn);
    }

    /**
     *
     * @param offSet
     * @return
     */
    public final String getXML(int offSet) {
        if (offSet > 0) {
            return createOffset(offSet).append(createXML(true)).toString();
        } else {
            return createXML(true);
        }
    }

    /**
     *
     * @param offSet
     * @param filterOn
     * @return
     */
    public final String getXML(boolean filterOn,int offSet) {
        if (offSet > 0) {
            return createOffset(offSet).append(createXML(filterOn)).toString();
        } else {
            return createXML(filterOn);
        }
    }

    /**
     *
     * @param filterOn
     * @return
     */
    private final String createXML(boolean filterOn) {

        StringBuffer retVal = new StringBuffer(32);

        retVal.append("<");
        retVal.append(this.name);
        retVal.append(">");

        retVal.append(filterChars(this.value, filterOn));

        retVal.append("</");
        retVal.append(this.name);
        retVal.append(">\n");

        return retVal.toString();

    }

    /**
     *
     * @param offSet
     * @return
     */
    private final StringBuffer createOffset(int offSet) {
        StringBuffer offSetString = new StringBuffer();
        for (int i = 0; i < offSet; i++) {
            offSetString.append(" ");
        }

        return offSetString;
    }

    /**
     *
     * @param str
     * @param filterOn 
     * @return
     */
    private final String filterChars(String str, boolean filterOn) {
        /*
        <       lt	        <		Less than sign
        >       gt	        >		Greater than sign
        &       amp	        &		Ampersand
        "       quot	"		Double quote sign
        '       apos        '       apostrophe
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
                    case '\'':
                        buffer.replace(i, i + 1, "&apos;");
                        i += 5;
                        break;
                    default:
                        int thisCode = (int) thisChar;
                        if (filterOn &&
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

