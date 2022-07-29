/*
 * Copyright (C) 2006-2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.view.component;

/**
 * An abstract class defining methods for a statement panel view.
 *
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Added {@link #getRoundType()}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 *      <li>Added {@link #getPythonApprovedPath()} method.</li>
 *      <li>Added {@link #getGccBuildCommand()} method.</li>
 *      <li>Added {@link #getCppApprovedPath()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Add {@link #getExecutionTimeLimit()} method</li>
 *      <li>Add {@link #getMemLimit()} method</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Added {@link #getStackLimit()} method.</li>
 * </ol>
 * </p>
 *
 * @author mitalub, savon_cn, Selena
 * @version 1.3
 */
public abstract class StatementPanelView extends ComponentView {

    public abstract String getClassName();

    public abstract String getMethodName();

    public abstract String getParameters();

    public abstract String getReturnType();

    public abstract String getIntroduction();

    public abstract String getNotes();

    public abstract String[] getSpecifiedConstraints();

    public abstract String getFreeFormConstraints();

    public abstract int getSelectedPart();

    public abstract void setConstraintText(String text, int index);

    public abstract boolean isCategoryChecked(int idx);
    
    /**
     * <p>
     * get the round type of problem.
     * </p>
     * @return the round types.
     */
    public abstract int getRoundType();
	
    /**
     * Get the GCC build command.
     * @return the GCC build command.
     * @since 1.1
     */
    public abstract String getGccBuildCommand();
	
    /**
     * Get the cpp approved path.
     * @return the cpp approved path.
     * @since 1.1
     */
    public abstract String getCppApprovedPath();
    
    /**
     * Get the python build command.
     * @return the python build command.
     * @since 1.1
     */
    public abstract String getPythonCommand();
	
    /**
     * Get the python approved path.
     * @return the python approved path.
     * @since 1.1
     */
    public abstract String getPythonApprovedPath();
    /**
     * Gets the execution time limit.
     * 
     * @return the execution time limit.
     * @since 1.2
     */
    public abstract int getExecutionTimeLimit();
    /**
     * Gets the execution memory limit.
     * @return the execution memory limit.
     * @since 1.2
     */
    public abstract int getMemLimit();

    /**
     * Gets the execution stack size limit.
     * @return the execution stack size limit.
     * @since 1.3
     */
    public abstract int getStackLimit();
}
