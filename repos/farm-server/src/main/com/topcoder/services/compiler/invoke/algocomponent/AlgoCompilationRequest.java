/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.services.compiler.invoke.algocomponent;

import java.util.List;

import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 * Interface used for algorithm compilers to compile algo
 * component solutions/submission code.
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Add {@link #getGccBuildCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (BUGR-9137 - Python Enable For SRM):
 * <ol>
 *      <li>Add {@link #getPythonCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Add {@link #getProblemCustomSettings()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.3
 */
public interface AlgoCompilationRequest {
    
    /**
     * Set the compilation success status
     * @param b True if compilation succeeded, False otherwise
     */
    void setCompileStatus(boolean b);
    
    /**
     * Sets the compilation error message
     * @param msg Message
     */
    void setCompileError(String msg);
    
    /**
     * @return the source code to compule
     */
    String getProgramText();
    
    /**
     * @return The path to the working directory for the solution / submission
     */
    String getPath();
    
    /**
     * Sets the standard error output
     * @param stderr Text to set as standard error output
     */
    void setStdErr(String stderr);
    
    /**
     * Sets the standard output
     * @param stdout Text to set as standard output
     */
    void setStdOut(String stdout);
    
    /**
     * @return The className correspoding to the compiled code 
     */
    String getClassName();

    String getMethodName();
    
    List getParamTypes();

    String getReturnType(int language);

    /**
     * @return The language of the source code
     */
    int getLanguage();
    /**
     * get the problem custom settings.
     * @return the problem custom settings.
     * @since 1.3
     */
    ProblemCustomSettings getProblemCustomSettings();
}
