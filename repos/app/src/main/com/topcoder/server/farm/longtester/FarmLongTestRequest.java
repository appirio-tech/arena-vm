/*
 * Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
 */
 
/*
 * FarmLongTestRequest
 * 
 * Created 04/12/2006
 */
package com.topcoder.server.farm.longtester;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 * Bean containing all necessary information that a tester requires 
 * to do a test on a solution/submission
 * 
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added field {@link #executionTimeLimit}.</li>
 *  </ul> 
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added field {@link #cppApprovedPath}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added field {@link #pythonApprovedPath}.</li>
 *      <li>Added field {@link #pythonCommand}.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Add {@link #problemCustomSettings} field.</li>
 *      <li>Remove memLimit, exectionTimeLimit, cppApprovedPath, pythonCommand, pythonApprovedPath fields
 *                 and it's getter,setter methods.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.4
 */
public class FarmLongTestRequest implements Serializable, CustomSerializable {
    public static final int CODE_TYPE_SOLUTION = 0;
    public static final int CODE_TYPE_SUBMISSION = 1;

    private Object[] arguments;
    private Solution solution;
    private ComponentFiles componentFiles;
    private int codeType;
    /**
     * the problem custom settings.
     * @since 1.4
     */
    private ProblemCustomSettings problemCustomSettings;

    private int maxThreadCount;
    
    public FarmLongTestRequest() {
    }
    
    /**
     * @return Returns the arguments.
     */
    public Object[] getArguments() {
        return arguments;
    }
    /**
     * @param arguments The arguments to set.
     */
    public void setArguments(Object[] argument) {
        this.arguments = argument;
    }
    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return componentFiles.getComponentName();
    }
    /**
     * @return Returns the componentFiles.
     */
    public ComponentFiles getComponentFiles() {
        return componentFiles;
    }
    /**
     * @param componentFiles The componentFiles to set.
     */
    public void setComponentFiles(ComponentFiles componentFiles) {
        this.componentFiles = componentFiles;
    }
    /**
     * @return Returns the codeType.
     */
    public int getCodeType() {
        return codeType;
    }
    /**
     * @param codeType The codeType to set.
     */
    public void setCodeType(int testType) {
        this.codeType = testType;
    }
 
    public Solution getSolution() {
        return solution;
    }
    public void setSolution(Solution solution) {
        this.solution = solution;
    }
    public int getLanguageId() {
        return componentFiles.getLanguageId();
    }
  
    public int getMaxThreadCount() {
        return maxThreadCount;
    }

    public void setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }
    /**
     * Getter the problem custom settings.
     * @return the problem custom settings.
     * @since 1.4
     */
    public ProblemCustomSettings getProblemCustomSettings() {
        return problemCustomSettings;
    }
    /**
     * Setter the problem custom settings.
     * @param customSettings the problem custom settings.
     * @since 1.4
     */
    public void setProblemCustomSettings(ProblemCustomSettings problemCustomSettings) {
        this.problemCustomSettings = problemCustomSettings;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        arguments = reader.readObjectArray();
        solution = (Solution) reader.readObject();
        componentFiles = (ComponentFiles) reader.readObject();
        codeType = reader.readInt();
        maxThreadCount = reader.readInt();
        problemCustomSettings = (ProblemCustomSettings)reader.readObject();
    }
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObjectArray(this.arguments);
        writer.writeObject(this.solution);
        writer.writeObject(this.componentFiles);
        writer.writeInt(this.codeType);
        writer.writeInt(this.maxThreadCount);
        writer.writeObject(problemCustomSettings);
    }
}
