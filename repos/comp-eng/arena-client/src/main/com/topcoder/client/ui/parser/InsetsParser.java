package com.topcoder.client.ui.parser;

import java.awt.Insets;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class InsetsParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        String[] values = value.split(",", -1);

        if (values.length != 4) {
            throw new IllegalArgumentException("Insets need four numbers.");
        }

        return new Insets(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]),
                          Integer.parseInt(values[3]));
    }
}
