package com.topcoder.shared.language;

/**
 * Implements the <code>Language</code> interface for the C++ programming language.
 * 
 * @author Qi Liu
 * @version $Id: CPPLanguage.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class CPPLanguage extends CStyleLanguage {
    /** Represents the unique ID for C++. */
    public final static int ID = 3;

    /** Represents the description for C++. */
    public final static String DESCRIPTION = "C++";

    /** Represents the singleton instance for C++ language. */
    public static CPPLanguage CPP_LANGUAGE = new CPPLanguage();

    /**
     * Creates a new instance of <code>CPPLanguage</code> class. A default constructor must be available as required
     * by custom serialization. Developers should not directly use this constructor, but use
     * <code>Language.getLanguage</code>
     * 
     * @see Language.getLanguage
     */
    public CPPLanguage() {
        super(ID, DESCRIPTION);
    }

    /**
     * Gets the default file extension for C++, which is 'c'.
     * 
     * @return a string 'c'.
     */
    public String getDefaultExtension() {
        return "c";
    }

    /**
     * Gets a string representing the calling of the given method in the given class using the given variable names as
     * arguments. C++ is slightly different than other C-style language definition, since it uses '::' instead of '.'
     * between class name and method name. The calling is like the following:
     * <code>'val = CLASS_NAME::METHOD_NAME(PARAM_NAME_1, PARAM_NAME_2, ..., PARAM_NAME_n);'</code>.
     * 
     * @param className the class name where the called method is located.
     * @param methodName the method name to be called.
     * @param paramNames the variable names to be used as the arguments.
     * @return a string representation of the call.
     */
    public String exampleExposedCall(String className, String methodName, String[] paramNames) {
        StringBuffer buf = new StringBuffer();

        buf.append("val = ");
        buf.append(className);
        buf.append("::");
        buf.append(methodName);
        buf.append("(");

        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0)
                buf.append(", ");
            buf.append(paramNames[i]);
        }

        buf.append(");");

        return buf.toString();
    }
}
