/*
 * Copyright (C) 2008 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a simple component. A problem can have multiple components.
 *
 * <p>
 *  Version 1.1 (Release Assembly - Round Type Option Support For SRM Problem version 1.0) change notes:
 *  <ul>
 *      <li>Added {@link #roundType} field, also the getter/setter were added.</li>
 *      <li>Updated {@link #customReadObject(CSReader)} and {@link #customWriteObject(CSWriter)} methods to support
 *      field {@link #roundType}.</li>
 *      <li>Updated {@link #toString()} method to support field {@link #roundType}.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Added {@link #gccBuildCommand} field.</li>
 *      <li>Added {@link #cppApprovedPath} field.</li>
 *      <li>Added {@link #getGccBuildCommand()} method.</li>
 *      <li>Added {@link #setGccBuildCommand(String)} method.</li>
 *      <li>Added {@link #getCppApprovedPath()} method.</li>
 *      <li>Added {@link #setCppApprovedPath(String)} method.</li>
 *      <li>Added {@link #pythonCommand} field.</li>
 *      <li>Added {@link #pythonApprovedPath} field.</li>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 *      <li>Added {@link #setPythonCommand(String)} method.</li>
 *      <li>Added {@link #getPythonApprovedPath()} method.</li>
 *      <li>Added {@link #setPythonApprovedPath(String)} method.</li>
 *      <li>Update {@link #toString()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Add {@link #executionTimeLimit} field and getter,setter method.</li>
 *      <li>Add {@link #memLimit} field and getter,setter method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Add {@link #problemCustomSettings} field and getter,setter method..</li>
 *      <li>Remove memLimit, exectionTimeLimit, gccBuildCommand, cppApprovedPath, pythonCommand,
 *          pythonApprovedPath fields and it's getter,setter methods.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Updated {@link #toString()} method to support stack limit.</li>
 * </ol>
 * </p>
 *
 * @author Qi Liu, savon_cn, Selena
 * @version 1.5
 */
public class SimpleComponent implements Serializable, CustomSerializable {
    /** Represents the problem ID of this component. */
    int problemID;

    /** Represents the component ID. */
    int componentID;

    /** Represents the component type. */
    int componentTypeID;

    /**
     * Represents the round type of the component.
     *
     * @since 1.1
     */
    int roundType;

    /** Represents the solution class name of this component. */
    String className;

    /** Represents the solution method name of this component. */
    String methodName;

    /** Represents the argument types of the solution method. */
    DataType[] paramTypes;

    /** Represents the return type of the solution method. */
    DataType returnType;

    /** Represents the IDs of any web service used by this component. */
    Long[] webServiceDependencies = null;
    /**
     * <p>the problem custom settings.</p>
     * @since 1.4
     */
    private ProblemCustomSettings problemCustomSettings = new ProblemCustomSettings();
    /**
     * Creates a new instance of <code>SimpleComponent</code>. It is required by custom serialization.
     */
    public SimpleComponent() {
    }

    /**
     * Writes this object to the writer.
     *
     * @param writer the writer to write to.
     * @throws IOException if any error occurs.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(className);
        writer.writeString(methodName);
        writer.writeObject(returnType);
        writer.writeObjectArray(paramTypes);
        writer.writeInt(componentID);
        writer.writeInt(componentTypeID);
        writer.writeInt(problemID);
        writer.writeInt(roundType);
        writer.writeObject(problemCustomSettings);
        writer.writeObjectArray(webServiceDependencies);
    }

    /**
     * Reads this object from the reader.
     *
     * @param reader the reader to read from.
     * @throws IOException if any error occurs.
     */
    public void customReadObject(CSReader reader) throws IOException {
        Object[] o_paramTypes;

        className = reader.readString();
        methodName = reader.readString();
        returnType = (DataType) reader.readObject();
        o_paramTypes = reader.readObjectArray();
        componentID = reader.readInt();
        componentTypeID = reader.readInt();
        problemID = reader.readInt();
        roundType = reader.readInt();
        problemCustomSettings = (ProblemCustomSettings)reader.readObject();

        if (o_paramTypes == null)
            o_paramTypes = new Object[0];
        paramTypes = new DataType[o_paramTypes.length];
        for (int i = 0; i < o_paramTypes.length; i++)
            paramTypes[i] = (DataType) o_paramTypes[i];
        webServiceDependencies = (Long[]) reader.readObjectArray(Long.class);
        
    }

    /**
     * Gets the problem ID of this component.
     * 
     * @return the problem ID of this component.
     */
    public int getProblemID() {
        return problemID;
    }

    /**
     * Sets the problem ID of the problem which this component belongs to.
     * 
     * @param problemID the problem ID.
     */
    public void setProblemID(int problemID) {
        this.problemID = problemID;
    }

    /**
     * Gets the unique ID of this component.
     * 
     * @return the unique ID of this component.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Sets the unique ID of this component.
     * 
     * @param componentID the component ID.
     */
    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    /**
     * Gets the type of this component.
     * 
     * @return the type of this component.
     */
    public int getComponentTypeID() {
        return componentTypeID;
    }

    /**
     * Sets the type of this component.
     * 
     * @param componentTypeID the type of this component.
     */
    public void setComponentTypeID(int componentTypeID) {
        this.componentTypeID = componentTypeID;
    }

    /**
     * Gets the name of the solution class.
     * 
     * @return the solution class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the name of the solution class.
     * 
     * @param className the solution class name.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the name of the solution method.
     * 
     * @return the solution method name.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the name of the solution method.
     * 
     * @param methodName the solution method name.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Gets the argument types of the solution method. There is no copy.
     * 
     * @return the solution argument types.
     */
    public DataType[] getParamTypes() {
        return paramTypes;
    }

    /**
     * Sets the argument types of the solution method. There is no copy.
     * 
     * @param paramTypes the solution argument types.
     */
    public void setParamTypes(DataType[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    /**
     * Gets the return type of the solution method.
     * 
     * @return the solution return type.
     */
    public DataType getReturnType() {
        return returnType;
    }

    /**
     * Sets the return type of the solution method.
     * 
     * @param returnType the solution return type.
     */
    public void setReturnType(DataType returnType) {
        this.returnType = returnType;
    }

    /**
     * Gets a string representation of the component which can be used as a key in a cache.
     * 
     * @param componentID the component ID.
     * @return a string representation of the component.
     */
    public static String getCacheKey(int componentID) {
        return "SimpleProblemComponent." + componentID;
    }

    /**
     * Gets a string representation of the component which can be used as a key in a cache.
     * 
     * @return a string representation of the component.
     */
    @JsonIgnore
    public String getCacheKey() {
        return getCacheKey(componentID);
    }

    /**
     * Gets the string descriptor of the return type for the given language.
     * 
     * @param language the ID of the language.
     * @return the string descriptor of the return type for the language.
     */
    public String getReturnType(int language) {
        return returnType.getDescriptor(language);
    }

    /**
     * Gets a flag indicating if the component refers to any web services.
     * 
     * @return <code>true</code> if the component refers to at least one web service; <code>false</code> otherwise.
     */
    public boolean hasWebServiceDependencies() {
        return webServiceDependencies != null && webServiceDependencies.length > 0;
    }

    /**
     * Gets the list of web service IDs which are referred by this component. There is no copy.
     * 
     * @return the list of web service IDs.
     */
    public Long[] getWebServiceDependencies() {
        return webServiceDependencies;
    }

    /**
     * Sets the list of web service IDs which are referred by this component. There is no copy.
     * 
     * @param webServiceIDs the list of web service IDs.
     */
    public void setWebServiceDependencies(Long[] webServiceIDs) {
        webServiceDependencies = webServiceIDs;
    }

    /**
     * Gets the round type.
     *
     * @return the round type.
     * @since 1.1
     */
    public int getRoundType() {
        return roundType;
    }

    /**
     * Sets the round type.
     *
     * @param roundType the round type.
     * @since 1.1
     */
    public void setRoundType(int roundType) {
        this.roundType = roundType;
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
     * @param problemCustomSettings the problem custom settings.
     * @since 1.4
     */
    public void setProblemCustomSettings(ProblemCustomSettings problemCustomSettings) {
        this.problemCustomSettings = problemCustomSettings;
    }

    /**
     * Gets the content of this object.
     *
     * @return the content of this object.
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.problem.SimpleComponent) [");
        ret.append("problemID = ");
        ret.append(problemID);
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("componentTypeID = ");
        ret.append(componentTypeID);
        ret.append(", ");
        ret.append("roundType = ");
        ret.append(roundType);
        ret.append(", ");
        ret.append("executionTimeLimit = ");
        ret.append(problemCustomSettings.getExecutionTimeLimit());
        ret.append(", ");
        ret.append("memLimit = ");
        ret.append(problemCustomSettings.getMemLimit());
        ret.append(", ");
        ret.append("stackLimit = ");
        ret.append(problemCustomSettings.getStackLimit());
        ret.append(", ");
        ret.append("gccBuildCommand = ");
        ret.append(problemCustomSettings.getGccBuildCommand());
        ret.append(", ");
        ret.append("cppApprovedPath = ");
        ret.append(problemCustomSettings.getCppApprovedPath());
        ret.append(", ");
        ret.append("pythonCommand = ");
        ret.append(problemCustomSettings.getPythonCommand());
        ret.append(", ");
        ret.append("pythonApprovedPath = ");
        ret.append(problemCustomSettings.getPythonApprovedPath());
        ret.append(", ");
        
        ret.append("className = ");
        if (className == null) {
            ret.append("null");
        } else {
            ret.append(className.toString());
        }
        ret.append(", ");
        ret.append("methodName = ");
        if (methodName == null) {
            ret.append("null");
        } else {
            ret.append(methodName.toString());
        }
        ret.append(", ");
        ret.append("paramTypes = ");
        if (paramTypes == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < paramTypes.length; i++) {
                ret.append(paramTypes[i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("returnType = ");
        if (returnType == null) {
            ret.append("null");
        } else {
            ret.append(returnType.toString());
        }
        ret.append(", ");
        ret.append("webServiceDependencies = ");
        if (webServiceDependencies == null) {
            ret.append("null");
        } else {
            ret.append(Arrays.asList(webServiceDependencies));
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
    
    
}
