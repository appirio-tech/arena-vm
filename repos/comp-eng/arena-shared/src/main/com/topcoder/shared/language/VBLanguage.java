package com.topcoder.shared.language;

import com.topcoder.shared.problem.DataType;

/**
 * Implements the <code>Language</code> interface for the Visual Basic .NET (VB.NET) programming language.
 * 
 * @author Qi Liu
 * @version $Id: VBLanguage.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class VBLanguage extends BaseLanguage {
    /** Represents the unique ID for VB.NET. */
    public final static int ID = 5;

    /** Represents the description for VB.NET. */
    public final static String DESCRIPTION = "VB";

    /** Represents the singleton instance for VB.NET language. */
    public final static VBLanguage VB_LANGUAGE = new VBLanguage();

    /**
     * Creates a new instance of <code>VBLanguage</code> class. A default constructor must be available as required by
     * custom serialization. Developers should not directly use this constructor, but use
     * <code>Language.getLanguage</code>
     * 
     * @see Language.getLanguage
     */
    public VBLanguage() {
        super(ID, DESCRIPTION);
    }

    /**
     * Gets the method signature for this programming language according to the given method name, return type, argument
     * types and argument names. The returned VB.NET method signature is like the following: <code>'Public Function
     * METHOD_NAME(PARAM_NAME_1 As PARAM_TYPE_1, PARAM_NAME_2 As PARAM_TYPE_2, ..., PARAM_NAME_n As PARAM_TYPE_n) As RETURN_TYPE'</code>.
     * 
     * @param methodName the method name.
     * @param returnType the return type of the method.
     * @param paramTypes the argument types of the method.
     * @param paramNames the argument names of the method.
     * @return the method signature.
     * @throws IllegalArgumentException if the numbers of elements in <code>paramTypes</code> and
     *             <code>paramNames</code> are different.
     */
    public String getMethodSignature(String methodName, DataType returnType, DataType[] paramTypes, String[] paramNames) {
        if (paramTypes.length != paramNames.length)
            throw new IllegalArgumentException(
                "VBLanguage.getMethodSignature: paramTypes.length != paramNames.length (" + paramTypes.length + " + "
                    + paramNames.length + ")");

        String returns = returnType.getDescriptor(this);
        String[] params = new String[paramTypes.length];
        int len = returns.length() + methodName.length() + 3; // 3 = ' ' + '(' + ')'

        for (int i = 0; i < params.length; i++) {
            String type = paramTypes[i].getDescriptor(this);
            params[i] = paramNames[i] + " As " + type;
            len += params[i].length();
        }
        len += 2 * (params.length - 1);

        StringBuffer buf = new StringBuffer(len);

        buf.append("Public Function ");
        buf.append(methodName);
        buf.append('(');
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(params[i]);
        }
        buf.append(')');
        buf.append(" As ");
        buf.append(returns);
        return buf.toString();
    }

    /**
     * Gets a string representing the calling of the given method in the given class using the given variable names as
     * arguments. The VB.NET calling is like the following:
     * <code>'val = CLASS_NAME.METHOD_NAME(PARAM_NAME_1, PARAM_NAME_2, ..., PARAM_NAME_n)'</code>.
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
        buf.append(".");
        buf.append(methodName);
        buf.append("(");

        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0)
                buf.append(", ");
            buf.append(paramNames[i]);
        }

        buf.append(")");

        return buf.toString();
    }

    /**
     * Gets the default file extension for VB.NET, which is 'vb'.
     * 
     * @return a string 'vb'.
     */
    public String getDefaultExtension() {
        return "vb";
    }
}