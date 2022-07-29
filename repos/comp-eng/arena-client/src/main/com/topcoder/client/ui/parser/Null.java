package com.topcoder.client.ui.parser;

import java.awt.Color;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class Null implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        return null;
    }
}
