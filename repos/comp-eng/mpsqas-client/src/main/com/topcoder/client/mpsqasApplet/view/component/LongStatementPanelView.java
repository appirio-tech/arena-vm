/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.view.component;

import com.topcoder.shared.problem.DataType;

/**
 * An abstract class defining methods for a long statement panel view.
 *
 * <p>
 *  Version 1.1(TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added {@link #getExecutionTimeLimit()} to get the execution time limit.</li>
 *  </ul>
 * </p>
 * 
 * <p>
 *  Version 1.2(TC Competition Engine - Code Compilation Issues) change notes:
 *  <ul>
 *      <li>Added {@link #getCompileTimeLimit()} to get the compile time limit.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #getGccBuildCommand()} method.</li>
 *      <li>Added {@link #getCppApprovedPath()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 *      <li>Added {@link #getPythonApprovedPath()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in 1.5 (Release Assembly - TopCoder Competition Engine Improvement Series 1):
 * <ol>
 * <li>Added {@link #getSubmissionRate()} method.</li>
 * <li>Added {@link #getExampleSubmissionRate()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (TopCoder Competition Engine - Stack Size Configuration For MM Problems v1.0):
 * <ol>
 *      <li>Added {@link #getStackLimit()} method.</li>
 * </ol>
 * </p>
 *
 * @author mktong, savon_cn, gevak, Selena
 * @version 1.6
 */
public abstract class LongStatementPanelView extends ComponentView {

    public abstract String getClassName();
    
    public abstract String getExposedClassName();
    
    public abstract int getMethodCount();
    
    public abstract String[] getMethodNames();

    public abstract String[] getParameters();
    
    public abstract String[] getReturnTypes();
    
    public abstract DataType[] getReturnDataTypes();
    
    public abstract int getExposedMethodCount();
    
    public abstract String[] getExposedMethodNames();

    public abstract String[] getExposedParameters();
    
    public abstract String[] getExposedReturnTypes();
    
    public abstract DataType[] getExposedReturnDataTypes();
    
    public abstract int getMemLimit();

    /**
     * Gets the execution stack size limit.
     * @return the execution stack size limit.
     * @since 1.6
     */
    public abstract int getStackLimit();

    public abstract int getCodeLengthLimit();
    /**
     * Get the GCC build command.
     * @return the GCC build command.
     * @since 1.3
     */
    public abstract String getGccBuildCommand();
    /**
     * Get the cpp approved path.
     * @return the cpp approved path.
     * @since 1.3
     */
    public abstract String getCppApprovedPath();
    
    /**
     * Get the python build command.
     * @return the python build command.
     * @since 1.4
     */
    public abstract String getPythonCommand();
    /**
     * Get the python approved path.
     * @return the python approved path.
     * @since 1.4
     */
    public abstract String getPythonApprovedPath();
    /**
     * Gets the execution time limit.
     * 
     * @return the execution time limit.
     * @since 1.1
     */
    public abstract int getExecutionTimeLimit();

    /**
     * Gets submission rate.
     *
     * @return Submission rate, in minutes. Non-positive integer means that it's not set.
     * @since 1.5
     */
    public abstract int getSubmissionRate();

    /**
     * Gets example submission rate.
     *
     * @return Example submission rate, in minutes. Non-positive integer means that it's not set.
     * @since 1.5
     */
    public abstract int getExampleSubmissionRate();

    /**
     * Gets the compile time limit.
     * 
     * @return the compile time limit.
     * @since 1.2
     */
    public abstract int getCompileTimeLimit();
    
    public abstract int getRoundType();

    public abstract String getIntroduction();

    public abstract String getNotes();

    public abstract String[] getSpecifiedConstraints();

    public abstract String getFreeFormConstraints();

    public abstract int getSelectedPart();

    public abstract void setConstraintText(String text, int index);

    public abstract boolean isCategoryChecked(int idx);
}
