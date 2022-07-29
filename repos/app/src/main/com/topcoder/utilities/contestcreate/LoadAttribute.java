package com.topcoder.utilities.contestcreate;

import java.util.Date;


public class LoadAttribute {

    String _name;
    Object _value;
    boolean _iskey;

    public LoadAttribute(String name, Object value, boolean iskey) {
        _name = name;
        _value = value;
        _iskey = iskey;
    }

    public String getColumnName() {
        return _name;
    }

    public Object getValue() {
        return _value;
    }

    public String sqlValue() {
        if (_value == null) {
            return "null";

        } else if (_value instanceof Integer) {
            return _value.toString();

        } else if (_value instanceof Date) {
            String text = (new java.sql.Timestamp(((Date) _value).getTime())).toString();
            return "'" + text + "'";

        } else if (_value instanceof String) {
            return "'" + quote((String) _value) + "'";

        } else if (_value instanceof Double) {
            return _value.toString();

        } else {
            // only int and string and date ?
            return "XXX";
        }
    }

    public boolean isKey() {
        return _iskey;
    }


    String quote(String orig) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < orig.length(); i++) {
            char c = orig.charAt(i);
            buf.append(c);
            if (c == '\'') {
                buf.append(c);
            }
        }

        return buf.toString();
    }


}
