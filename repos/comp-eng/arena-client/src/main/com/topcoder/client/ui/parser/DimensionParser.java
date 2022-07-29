package com.topcoder.client.ui.parser;

import java.awt.Dimension;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class DimensionParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        Dimension dim = new Dimension();
        String[] values = value.split(",", -1);

        if (values.length != 2) {
            throw new IllegalArgumentException("Dimension needs two numbers.");
        }

        dim.setSize(Double.parseDouble(values[0]), Double.parseDouble(values[1]));

        return dim;
    }
}
