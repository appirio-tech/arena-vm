package com.topcoder.shared.language;

/**
 * Implements the <code>Language</code> interface for the JavaScript programming language.
 */
public class JavaScriptLanguage extends CStyleLanguage {
    /** Represents the unique ID for JavaScript, not to be confused with applet constants. */
    public final static int ID = 8;

    /** Represents the description for Java. */
    public static String DESCRIPTION = "JavaScript";

    /** Represents the singleton instance for Java language. */
    public static JavaScriptLanguage JAVASCRIPT_LANGUAGE = new JavaScriptLanguage();

    public JavaScriptLanguage() {
        super(ID, DESCRIPTION);
    }

    public String getDefaultExtension() {
        return "js";
    }
}
