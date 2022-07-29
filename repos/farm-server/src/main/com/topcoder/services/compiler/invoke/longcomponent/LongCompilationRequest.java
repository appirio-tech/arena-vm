/*
 * Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
 */
 
/*
 * LongCompilationRequest
 * 
 * Created 05/12/2006
 */
package com.topcoder.services.compiler.invoke.longcomponent;

import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 * Interface used for long compilers to compile long
 * component solutions/submission code.
 *     
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added {@link #getCompileTimeLimit()} method.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #getGccBuildCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Added {@link #getProblemCustomSettings()} method.</li>
 *      <li>Remove {getCompileTimeLimit(), getGccBuildCommand(), getPythonCommand()} methods.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.4
 */
public interface LongCompilationRequest {
    
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
    
    /**
     * @return The source code of the user wrapper class
     */
    String getUserWrapperSource();
    
    String getExposedWrapperSource();
    
    String getExposedClassName();
    
    /**
     * @return The language of the source code
     */
    int getLanguage();
    
    boolean isThreadingAllowed();
    
    int getRoundType();
    /**
     * the custom problem settings
     * @return the custom problem settings
     * @since 1.4
     */
    ProblemCustomSettings getProblemCustomSettings();
}
