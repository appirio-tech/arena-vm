/*
* Copyright (C) 2013 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.SimpleComponent;

/**
 * <p>
 * this is the problem custom settings pojo.
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Add {@link #compileTimeLimit} field and getter,setter method.</li>
 *      <li>Add {@link #gccBuildCommand} field and getter,setter method.</li>
 *      <li>Add {@link #pythonCommand} field and getter,setter method.</li>
 *      <li>Add {@link #cppApprovedPath} field and getter,setter method.</li>
 *      <li>Add {@link #pythonApprovedPath} field and getter,setter method.</li>
 *      <li>Add {@link #executionTimeLimit} field and getter,setter method.</li>
 *      <li>Add {@link #memLimit} field and getter,setter method.</li>
 *      <li>Add {@link #getDefaultInstance()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Add {@link #stackLimit} field and getter,setter method.</li>
 *      <li>Updated {@link #customWriteObject(CSWriter)} method to support stack limit.</li>
 *      <li>Updated {@link #customReadObject(CSReader)} method to support stack limit.</li>
 *      <li>Updated {@link #getDefaultInstance()} method to support stack limit.</li>
 * </ol>
 * </p>
 *
 * @author Selena
 * @version 1.2
 */
public class ProblemCustomSettings implements Serializable, CustomSerializable {
    /**
     * Represents the compile time limit.
     */
    private int compileTimeLimit;
    /**
     * Represents the gcc build command.
     */
    private String gccBuildCommand;
    /**
     * Represents the python command.
     */
    private String pythonCommand;
    /**
     * Represents the cpp approved path.
     */
    private String cppApprovedPath;
    
    /**
     * Represents the python approved path.
     */
    private String pythonApprovedPath;
    
    /**
     * the execution time limit.
     */
    private int executionTimeLimit;
    
    /**
     * the execution memory limit.
     */
    private int memLimit;

    /**
     * the execution stack size limit.
     * @since 1.2
     */
    private int stackLimit;

    /**
     * serialize the object.
     * @throws IOException any error occur while serialize the object.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(this.compileTimeLimit);
        writer.writeString(gccBuildCommand);
        writer.writeString(pythonCommand);
        writer.writeString(cppApprovedPath);
        writer.writeString(pythonApprovedPath);
        writer.writeInt(executionTimeLimit);
        writer.writeInt(memLimit);
        writer.writeInt(stackLimit);
    }
    /**
     * deserialize the object.
     * @throws IOException any error occur while serialize the object.
     * @throws ObjectStreamException any error occur while serialize the object.
     */
    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        this.compileTimeLimit = reader.readInt();
        this.gccBuildCommand = reader.readString();
        this.pythonCommand = reader.readString();
        this.cppApprovedPath = reader.readString();
        this.pythonApprovedPath = reader.readString();
        this.executionTimeLimit = reader.readInt();
        this.memLimit = reader.readInt();
        this.stackLimit = reader.readInt();
    }
    /**
     * Getter the execution time limit.
     * @return the execution time limit.
     */
    public int getExecutionTimeLimit() {
        return executionTimeLimit;
    }
    /**
     * Setter the execution time limit.
     * @param executionTimeLimit the execution time limit.
     */
    public void setExecutionTimeLimit(int executionTimeLimit) {
        this.executionTimeLimit = executionTimeLimit;
    }
    /**
     * Getter the memory limit.
     * @return the memory limit.
     */
    public int getMemLimit() {
        return memLimit;
    }
    /**
     * Setter the memory limit.
     * @param memLimit the memory limit.
     */
    public void setMemLimit(int memLimit) {
        if (memLimit <= 0) {
            this.memLimit = ProblemComponent.DEFAULT_MEM_LIMIT;
        } else {
            this.memLimit = memLimit;
        }
    }

    /**
     * Getter the stack size limit.
     * @return the stack size limit.
     * @since 1.2
     */
    public int getStackLimit() {
        return stackLimit;
    }

    /**
     * Setter the stack size limit.
     * @param memLimit the stack size limit.
     * @since 1.2
     */
    public void setStackLimit(int stackLimit) {
        if (stackLimit <= 0) {
            this.stackLimit = ProblemComponent.DEFAULT_SRM_STACK_LIMIT;
        } else {
            this.stackLimit = stackLimit;
        }
    }

    /**
     * Gets the compile time limit.
     * 
     * @return the compile time limit.
     */
    public int getCompileTimeLimit() {
        return compileTimeLimit;
    }

    /**
     * Sets the compile time limit.
     * 
     * @param compileTimeLimit the compile time limit.
     */
    public void setCompileTimeLimit(int compileTimeLimit) {
        this.compileTimeLimit = compileTimeLimit;
    }

    /**
     * Gets the gcc build command.
     * @return the gcc build command.
     */
    public String getGccBuildCommand() {
        return gccBuildCommand;
    }

    /**
     * Sets the gcc build command.
     * @param gccBuildCommand the gcc build command.
     */
    public void setGccBuildCommand(String gccBuildCommand) {
        this.gccBuildCommand = gccBuildCommand;
    }

    /**
     * Gets the python command.
     * @return the python command.
     */
    public String getPythonCommand() {
        return pythonCommand;
    }

    /**
     * Sets the python command.
     * @param pythonCommand the python command.
     */
    public void setPythonCommand(String pythonCommand) {
        this.pythonCommand = pythonCommand;
    }
    
    /**
     * Gets the python approved path.
     * @return the python approved path.
     */
    public String getPythonApprovedPath() {
        return pythonApprovedPath;
    }

    /**
     * Sets the python approved path.
     * @param pythonApprovedPath the python approved path.
     */
    public void setPythonApprovedPath(String pythonApprovedPath) {
        this.pythonApprovedPath = pythonApprovedPath;
    }
    /**
     * Get the cpp approved path.
     * @return the cpp approved path.
     */
    public String getCppApprovedPath() {
        return cppApprovedPath;
    }
    /**
     * Set the cpp approved path.
     * @param cppApprovedPath
     *         the cpp approved path.
     */
    public void setCppApprovedPath(String cppApprovedPath) {
        this.cppApprovedPath = cppApprovedPath;
    }
    /**
     * <p>get the problem custom settings instance.</p>
     * @return the problem custom settings.
     */
    public static ProblemCustomSettings getDefaultInstance() {
        ProblemCustomSettings pcs = new ProblemCustomSettings();
        pcs.setMemLimit(ProblemComponent.DEFAULT_MEM_LIMIT);
        pcs.setStackLimit(ProblemComponent.DEFAULT_SRM_STACK_LIMIT);
        pcs.setCompileTimeLimit(ProblemComponent.DEFAULT_COMPILE_TIME_LIMIT);
        pcs.setExecutionTimeLimit(ProblemComponent.DEFAULT_EXECUTION_TIME_LIMIT);
        return pcs;
    }
}
