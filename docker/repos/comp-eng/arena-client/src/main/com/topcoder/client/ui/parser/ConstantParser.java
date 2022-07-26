package com.topcoder.client.ui.parser;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class ConstantParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        String[] values = value.split("\\.", -1);

        if (values.length < 2) {
            throw new IllegalArgumentException("The constant must be represented by a qualified field name.");
        }

        String field = values[values.length - 1];
        StringBuffer sb = new StringBuffer(values[0]);

        for (int i = 1; i < values.length - 1; ++i) {
            sb.append('.');
            sb.append(values[i]);
        }

        String className = sb.toString();

        try {
            return Class.forName(className).getField(field).get(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("The qualified field is not available.", e);
        }
    }
}
