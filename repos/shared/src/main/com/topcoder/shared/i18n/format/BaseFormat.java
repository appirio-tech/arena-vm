/*
 * BaseFormat
 * 
 * Created 07/19/2007
 */
package com.topcoder.shared.i18n.format;

import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * Base Format class to simplify Format creation
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class BaseFormat extends Format {
    private String pattern;
    private Locale locale;
    
    public BaseFormat(Locale locale) {
        this.locale = locale;
    }
    
    public BaseFormat(String pattern, Locale locale) {
        this.pattern = pattern;
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }


    public String getPattern() {
        return pattern;
    }
    public Object parseObject(String source, ParsePosition pos) {
        return null;
    }
}
