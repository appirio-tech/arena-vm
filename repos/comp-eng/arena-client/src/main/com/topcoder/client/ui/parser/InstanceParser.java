package com.topcoder.client.ui.parser;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class InstanceParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        try {
            return Class.forName(value, true, loader).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("The instance cannot be created.", e);
        }
    }
}
