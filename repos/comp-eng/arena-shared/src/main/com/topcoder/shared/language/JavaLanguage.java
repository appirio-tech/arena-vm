package com.topcoder.shared.language;

/**
 * Implements the <code>Language</code> interface for the Java programming language.
 * 
 * @author Qi Liu
 * @version $Id: JavaLanguage.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class JavaLanguage extends CStyleLanguage {
    /** Represents the unique ID for Java. */
    public final static int ID = 1;

    /** Represents the description for Java. */
    public static String DESCRIPTION = "Java";

    /** Represents the singleton instance for Java language. */
    public static JavaLanguage JAVA_LANGUAGE = new JavaLanguage();

    /**
     * Creates a new instance of <code>JavaLanguage</code> class. A default constructor must be available as required
     * by custom serialization. Developers should not directly use this constructor, but use
     * <code>Language.getLanguage</code>
     * 
     * @see Language.getLanguage
     */
    public JavaLanguage() {
        super(ID, DESCRIPTION);
    }

    /**
     * Gets the default file extension for Java, which is 'java'.
     * 
     * @return a string 'java'.
     */
    public String getDefaultExtension() {
        return "java";
    }
}
