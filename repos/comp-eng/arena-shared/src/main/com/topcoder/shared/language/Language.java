package com.topcoder.shared.language;

import java.io.Serializable;

import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.DataType;

/**
 * The interface for the implementation of all the semantics associated with a supported programming language. This
 * basically consists of the logic for generating language-dependent method signatures. An instance of type
 * <code>Language</code> also serves as a convenient identifier for a particular language.
 * 
 * @author Logan Hanks
 * @see DataType
 */
public interface Language extends Serializable, Cloneable, CustomSerializable {

    /**
     * Gets the unique ID of this programming language.
     * 
     * @return the unique ID of this programming language.
     */
    int getId();

    /**
     * Gets the description of this programming language.
     * 
     * @return the description of this programming language.
     */
    String getName();

    /**
     * Gets a flag indicating if two programming languages are the same. They are the same if the IDs are the same.
     */
    boolean equals(Object o);

    /**
     * Gets the method signature for this programming language according to the given method name, return type, argument
     * types and argument names.
     * 
     * @param methodName the method name.
     * @param returnType the return type of the method.
     * @param paramTypes the argument types of the method.
     * @param paramNames the argument names of the method.
     * @return the method signature.
     * @throws IllegalArgumentException if the numbers of elements in <code>paramTypes</code> and
     *             <code>paramNames</code> are different.
     */
    String getMethodSignature(String methodName, DataType returnType, DataType[] paramTypes, String[] paramNames);

    /**
     * Gets a string representing the calling of the given method in the given class using the given variable names as
     * arguments.
     * 
     * @param className the class name where the called method is located.
     * @param methodName the method name to be called.
     * @param paramNames the variable names to be used as the arguments.
     * @return a string representation of the call.
     */
    String exampleExposedCall(String className, String methodName, String[] paramNames);

    /**
     * Gets the default file extension for this language.
     * 
     * @return the default file extension for the language.
     */
    String getDefaultExtension();
}
