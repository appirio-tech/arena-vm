package com.topcoder.client.ui.parser;

import java.math.BigDecimal;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class NumberParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        return new BigDecimal(value);
    }
}
