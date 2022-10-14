/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.language;
/**
 * Implements the <code>R</code> interface for the R programming language.
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class RLanguage extends CStyleLanguage {
    /** Represents the unique ID for R. */
    public final static int ID = 7;

    /** Represents the description for R. */
    public static String DESCRIPTION = "R";

    /** Represents the singleton instance for R language. */
    public static RLanguage R_LANGUAGE = new RLanguage();

    /**
     * Creates a new instance of <code>RLanguage</code> class. A default constructor must be available as required
     * by custom serialization. Developers should not directly use this constructor, but use
     * <code>Language.getLanguage</code>
     * 
     * @see Language.getLanguage
     */
    public RLanguage() {
        super(ID, DESCRIPTION);
    }

    /**
     * Gets the default file extension for R, which is 'R'.
     * 
     * @return a string 'R'.
     */
    public String getDefaultExtension() {
        return DESCRIPTION;
    }
}
