package com.topcoder.client.ui.parser;

import java.awt.Font;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class FontParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        return Font.decode(value);
    }
}
