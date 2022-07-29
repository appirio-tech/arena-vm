package com.topcoder.client.ui.parser;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class CharacterParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        if (value.length() != 1) {
            throw new IllegalArgumentException("Value can only have one character.");
        }

        return new Character(value.charAt(0));
    }
}
