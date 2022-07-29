package com.topcoder.shared.language;

import com.topcoder.shared.problem.DataType;

/**
 * A ``C-style'' language is one in which method signatures are specified as <i>return-type</i> <i>method-name</i> (<i>params</i>...).
 * Currently every language TopCoder supports is a C-style language in this regard, and thus this class captures the
 * semantics common to C++, C#, and Java.
 * 
 * @author Logan Hanks
 * @see Language
 */
public abstract class CStyleLanguage extends BaseLanguage {
    /**
     * Creates a new instance of <code>CStyleLanguage</code> class. A default constructor is required for custom
     * serialization.
     */
    public CStyleLanguage() {
    }

    /**
     * Creates a new instance of <code>CStyleLanguage</code> class. The unique ID and the description of the
     * programming language are given.
     * 
     * @param id the unique ID of this programming language.
     * @param description the description of this programming language.
     */
    public CStyleLanguage(int id, String description) {
        super(id, description);
    }

    /**
     * Gets the method signature for this programming language according to the given method name, return type, argument
     * types and argument names. The returned C-style method signature is like the following: <code>'RETURN_TYPE
     * METHOD_NAME(PARAM_TYPE_1 PARAM_NAME_1, PARAM_TYPE_2 PARAM_NAME_2, ..., PARAM_TYPE_n PARAM_NAME_n)'</code>.
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
                "CStyleLanguage.getMethodSignature: paramTypes.length != paramNames.length (" + paramTypes.length
                    + " + " + paramNames.length + ")");

        String returns = returnType.getDescriptor(this);
        String[] params = new String[paramTypes.length];
        int len = returns.length() + methodName.length() + 3; // 3 = ' ' + '(' + ')'

        for (int i = 0; i < params.length; i++) {
            String type = paramTypes[i].getDescriptor(this);

            params[i] = type + ' ' + paramNames[i];
            len += params[i].length();
        }
        len += 2 * (params.length - 1);

        StringBuffer buf = new StringBuffer(len);

        buf.append(returns);
        buf.append(' ');
        buf.append(methodName);
        buf.append('(');
        for (int i = 0; i < params.length; i++) {
            if (i > 0)
                buf.append(", ");
            buf.append(params[i]);
        }
        buf.append(')');
        return buf.toString();
    }

    /**
     * Gets a string representing the calling of the given method in the given class using the given variable names as
     * arguments. The C-style calling is like the following:
     * <code>'val = CLASS_NAME.METHOD_NAME(PARAM_NAME_1, PARAM_NAME_2, ..., PARAM_NAME_n);'</code>.
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

        buf.append(");");

        return buf.toString();
    }
}
