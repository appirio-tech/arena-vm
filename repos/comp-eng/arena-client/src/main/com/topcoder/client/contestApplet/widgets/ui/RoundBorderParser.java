package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;

import com.topcoder.client.contestApplet.widgets.RoundBorder;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class RoundBorderParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        String[] values = value.split(",", -1);

        if (values.length != 3) {
            throw new IllegalArgumentException("This parser requires 3 parameters.");
        }

        return new RoundBorder(Color.decode(values[0]), Integer.parseInt(values[1]), Boolean.valueOf(values[2]).booleanValue());
    }
}
