package com.topcoder.client.ui.parser;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class ComponentReferenceParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        return page.getComponent(value).getEventSource();
    }
}
