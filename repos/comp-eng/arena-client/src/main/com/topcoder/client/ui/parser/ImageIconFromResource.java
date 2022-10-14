package com.topcoder.client.ui.parser;

import java.net.URL;
import javax.swing.ImageIcon;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class ImageIconFromResource implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        URL url = loader.getResource(value);

        if (url == null) {
            throw new IllegalArgumentException("The resource name cannot be found.");
        }

        return new ImageIcon(url);
    }
}
