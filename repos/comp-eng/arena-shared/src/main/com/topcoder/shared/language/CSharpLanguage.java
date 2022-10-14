package com.topcoder.shared.language;

/**
 * Implements the <code>Language</code> interface for the C# programming language.
 * 
 * @author Qi Liu
 * @version $Id: CSharpLanguage.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class CSharpLanguage extends CStyleLanguage {
    /** Represents the unique ID for C#. */
    public final static int ID = 4;

    /** Represents the description for C#. */
    public final static String DESCRIPTION = "C#";

    /** Represents the singleton instance for C# language. */
    public final static CSharpLanguage CSHARP_LANGUAGE = new CSharpLanguage();

    /**
     * Creates a new instance of <code>CSharpLanguage</code> class. A default constructor must be available as required
     * by custom serialization. Developers should not directly use this constructor, but use
     * <code>Language.getLanguage</code>
     * 
     * @see Language.getLanguage
     */
    public CSharpLanguage() {
        super(ID, DESCRIPTION);
    }

    /**
     * Gets the default file extension for C#, which is 'cs'.
     * 
     * @return a string 'cs'.
     */
    public String getDefaultExtension() {
        return "cs";
    }
}
