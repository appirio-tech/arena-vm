package com.topcoder.client.ui.parser;

import java.awt.Cursor;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class CursorParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        value = value.toUpperCase();

        try {
            return new Cursor(((Integer) Cursor.class.getField(value).get(null)).intValue());
        } catch (Exception e) {
            throw new IllegalArgumentException("The cursor cannot be created.", e);
        }
    }
}
