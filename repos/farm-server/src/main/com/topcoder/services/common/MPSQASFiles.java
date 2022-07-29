/*
 * Copyright (C)  - 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.services.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 *  Used to facilitate passing information to/from the MPSQAS server and
 *  Testers/Compilers.
 *
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added {@link #compileTimeLimit} field.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #gccBuildCommand} field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #pythonCommand} field.</li>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 *      <li>Added {@link #setPythonCommand(String)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Added {@link #cppApprovedPath} field.</li>
 *      <li>Added {@link #getCppApprovedPath()} method.</li>
 *      <li>Added {@link #setCppApprovedPath(String)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (BUGR-9137 - Python Enable For SRM):
 * <ol>
 *      <li>Added {@link #pythonApprovedPath} field.</li>
 *      <li>Added {@link #getPythonApprovedPath()} method.</li>
 *      <li>Added {@link #setPythonApprovedPath(String)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Add {@link #executionTimeLimit} field and getter,setter method.</li>
 *      <li>Add {@link #memLimit} field and getter,setter method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.7 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Add {@link #problemCustomSettings} field and setter,getter method.</li>
 *      <li>Remove compileTimeLimit, memLimit, exectionTimeLimit, gccBuildCommand, cppApprovedPath,
 *          pythonCommand, pythonApprovedPath fields and it's getter,setter methods.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.8 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *      <li>Add {@link #maxMemoryUsed} field and setter/getter, update writer/reader method.</li>
 * </ol>
 * </p>
 *
 *  @author Steven Fuller, savon_cn, dexy
 *  @version 1.8
 */
@SuppressWarnings({ "serial" })
public class MPSQASFiles implements Serializable {

    /** The id. */
    private int id = -1;

    /** The solution_id. */
    private int solution_id = -1;

    /** The component type. */
    private int componentType = -1;

    /** The language. */
    private int language = -1;

    /** The source files. */
    private Map<String, String> sourceFiles = null;

    /** The class files. */
    private Map<String, byte[]> classFiles = null;

    /** The compile status. */
    private boolean compileStatus = false;

    /** The test status. */
    private boolean testStatus = false;

    /** The std out. */
    private String stdOut = null;

    /** The std err. */
    private String stdErr = null;

    /** The exception text. */
    private String exceptionText = null;

    /** The package name. */
    private String packageName = null;

    /** The class name. */
    private String className = null;

    /** The method name. */
    private String methodName = null;

    /** The arg types. */
    private List<DataType> argTypes = null;

    /** The arg vals. */
    private List<?> argVals = null;

    /** The result type. */
    private DataType resultType = null;

    /** The result. */
    private Object result = null;

    /** The result value. */
    private String resultValue = null;

    /** The execution time. */
    private double executionTime = -1;

    /**
     * The max memory used (in MB).
     * @since 1.8
     */
    private double maxMemoryUsed = -1.0;

    /** The web service files. */
    private Map<String, String> webServiceFiles = null;

    /** The exposed class name. */
    private String exposedClassName = null;

    /** The threading allowed. */
    private boolean threadingAllowed = false;

    /** The round type. */
    private int roundType = 0;
    /**
     * the problem custom settings.
     * @since 1.7
     */
    private ProblemCustomSettings problemCustomSettings;
    /**
     *  Returns the id associated with this object.
     *
     *  @return     an id
     */
    public int getId() {
        return this.id;
    }

    /**
     *  Sets the id for this object.
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the solution id.
     *
     * @return the solution id
     */
    public int getSolutionId() {
        return this.solution_id;
    }

    /**
     * Gets the exposed class name.
     *
     * @return the exposed class name
     */
    public String getExposedClassName() {
        return exposedClassName;
    }

    /**
     * Sets the exposed class name.
     *
     * @param s the new exposed class name
     */
    public void setExposedClassName(String s) {
        this.exposedClassName = s;
    }

    /**
     * Sets the solution id.
     *
     * @param solution_id the new solution id
     */
    public void setSolutionId(int solution_id) {
        this.solution_id = solution_id;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public int getLanguage() {
        return this.language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(int language) {
        this.language = language;
    }

    /**
     * Gets the source files.
     *
     * @return the source files
     */
    public Map<String, String> getSourceFiles() {
        return this.sourceFiles;
    }

    /**
     * Sets the source files.
     *
     * @param sourceFiles the new source files
     */
    public void setSourceFiles(Map<String, String> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    /**
     * Gets the class files.
     *
     * @return the class files
     */
    public Map<String, byte[]> getClassFiles() {
        return this.classFiles;
    }

    /**
     * Gets the loadable class files.
     *
     * @return the loadable class files
     */
    @SuppressWarnings("rawtypes")
	@JsonIgnore
    public Map getLoadableClassFiles() {
        return dotize(this.classFiles);
    }

    /**
     * Sets the class files.
     *
     * @param classFiles the new class files
     */
    public void setClassFiles(Map<String, byte[]> classFiles) {
        this.classFiles = classFiles;
    }

    /**
     * Gets the compile status.
     *
     * @return the compile status
     */
    public boolean getCompileStatus() {
        return this.compileStatus;
    }

    /**
     * Sets the compile status.
     *
     * @param compileStatus the new compile status
     */
    public void setCompileStatus(boolean compileStatus) {
        this.compileStatus = compileStatus;
    }

    /**
     * Gets the test status.
     *
     * @return the test status
     */
    public boolean getTestStatus() {
        return this.testStatus;
    }

    /**
     * Sets the test status.
     *
     * @param testStatus the new test status
     */
    public void setTestStatus(boolean testStatus) {
        this.testStatus = testStatus;
    }

    /**
     * Gets the std out.
     *
     * @return the std out
     */
    public String getStdOut() {
        return this.stdOut;
    }

    /**
     * Sets the std out.
     *
     * @param stdOut the new std out
     */
    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }

    /**
     * Gets the std err.
     *
     * @return the std err
     */
    public String getStdErr() {
        return this.stdErr;
    }

    /**
     * Sets the std err.
     *
     * @param stdErr the new std err
     */
    public void setStdErr(String stdErr) {
        this.stdErr = stdErr;
    }

    /**
     * Gets the exception text.
     *
     * @return the exception text
     */
    public String getExceptionText() {
        return this.exceptionText;
    }

    /**
     * Sets the exception text.
     *
     * @param exceptionText the new exception text
     */
    public void setExceptionText(String exceptionText) {
        this.exceptionText = exceptionText;
    }

    /**
     * Gets the class name.
     *
     * @return the class name
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Gets the package name.
     *
     * @return the package name
     */
    public String getPackageName() {
        return this.packageName;
    }

    /**
     * Sets the package name.
     *
     * @param packageName the new package name
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Sets the class name.
     *
     * @param className the new class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the method name.
     *
     * @return the method name
     */
    public String getMethodName() {
        return this.methodName;
    }

    /**
     * Sets the method name.
     *
     * @param methodName the new method name
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Gets the arg types.
     *
     * @return the arg types
     */
    public List<DataType> getArgTypes() {
        return this.argTypes;
    }

    /**
     * Sets the arg types.
     *
     * @param argTypes the new arg types
     */
    public void setArgTypes(List<DataType> argTypes) {
        this.argTypes = argTypes;
    }

    /**
     * Gets the arg vals.
     *
     * @return the arg vals
     */
    @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="@class")
    public List<?> getArgVals() {
        return this.argVals;
    }

    /**
     * Sets the arg vals.
     *
     * @param argVals the new arg vals
     */
    public void setArgVals(List<?> argVals) {
        this.argVals = argVals;
    }

    /**
     * Gets the result type.
     *
     * @return the result type
     */
    public DataType getResultType() {
        return this.resultType;
    }

    /**
     * Sets the result type.
     *
     * @param resultType the new result type
     */
    public void setResultType(DataType resultType) {
        this.resultType = resultType;
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="@class")
    public Object getResult() {
        return this.result;
    }

    /**
     * Sets the result.
     *
     * @param result the new result
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * Gets the result value.
     *
     * @return the result value
     */
    public String getResultValue() {
        return this.resultValue;
    }

    /**
     * Sets the result value.
     *
     * @param resultValue the new result value
     */
    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    /**
     * Gets the execution time.
     *
     * @return the execution time
     */
    public double getExecutionTime() {
        return this.executionTime;
    }

    /**
     * Sets the execution time.
     *
     * @param executionTime the new execution time
     */
    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * Gets the max memory used (in MB).
     *
     * @return the max memory used (in MB).
     * @since 1.8
     */
    public double getMaxMemoryUsed() {
        return this.maxMemoryUsed;
    }

    /**
     * Sets the max memory used (in MB).
     *
     * @param maxMemoryUsed the new max memory used (in MB).
     * @since 1.8
     */
    public void setMaxMemoryUsed(double maxMemoryUsed) {
        this.maxMemoryUsed = maxMemoryUsed;
    }

    /**
     * Gets the web service files.
     *
     * @return the web service files
     */
    public Map<String, String> getWebServiceFiles() {
        return this.webServiceFiles;
    }

    /**
     * Sets the web service files.
     *
     * @param webServiceFiles the new web service files
     */
    public void setWebServiceFiles(Map<String, String> webServiceFiles) {
        this.webServiceFiles = webServiceFiles;
    }
    /**
     * Getter the problem custom settings.
     * @return the problem custom settings.
     * @since 1.7
     */
    public ProblemCustomSettings getProblemCustomSettings() {
        return problemCustomSettings;
    }

    /**
     * Setter the problem custom settings.
     *
     * @param problemCustomSettings the problem custom settings.
     * @since 1.7
     */
    public void setProblemCustomSettings(ProblemCustomSettings problemCustomSettings) {
        this.problemCustomSettings = problemCustomSettings;
    }

    /* return a new hashmap, but with all '/' replaced with '.' */
    /**
     * Dotize.
     *
     * @param hm the hm
     * @return the map
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private Map dotize(Map hm) {
        HashMap dhm = new HashMap();
        Iterator iter = hm.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry me = (Map.Entry) iter.next();

            String fileName = (String) me.getKey();

            if (fileName.endsWith(".class")) {
                fileName = fileName.replace('/', '.').substring(0,
                        fileName.length() - 6);
            }

            dhm.put(fileName, me.getValue());
        }

        return dhm;
    }

    /**
     * Gets the component type.
     *
     * @return Returns the componentType.
     */
    public int getComponentType() {
        return componentType;
    }

    /**
     * Sets the component type.
     *
     * @param componentType The componentType to set.
     */
    public void setComponentType(int componentType) {
        this.componentType = componentType;
    }



//    /* (non-Javadoc)
//     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
//     */
//    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
//        this.id = reader.readInt();
//        this.solution_id = reader.readInt();
//        this.componentType = reader.readInt();
//        this.language = reader.readInt();
//        this.sourceFiles = reader.readHashMap();
//        this.classFiles = reader.readHashMap();
//        this.compileStatus = reader.readBoolean();
//        this.testStatus = reader.readBoolean();
//        this.stdOut = reader.readString();
//        this.stdErr = reader.readString();
//        this.exceptionText = reader.readString();
//        this.packageName = reader.readString();
//        this.className = reader.readString();
//        this.methodName = reader.readString();
//        this.argTypes = reader.readArrayList();
//        this.argVals = reader.readArrayList();
//        this.resultType = (DataType) reader.readObject();
//        this.result = reader.readObject();
//        this.resultValue = reader.readString();
//        this.executionTime = reader.readDouble();
//        this.maxMemoryUsed = reader.readDouble();
//        this.webServiceFiles = reader.readHashMap();
//        this.exposedClassName = reader.readString();
//        this.threadingAllowed = reader.readBoolean();
//        this.roundType = reader.readInt();
//        this.problemCustomSettings = (ProblemCustomSettings)reader.readObject();
//    }
//
//    /* (non-Javadoc)
//     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
//     */
//    public void customWriteObject(CSWriter writer) throws IOException {
//        writer.writeInt(this.id);
//        writer.writeInt(this.solution_id);
//        writer.writeInt(this.componentType);
//        writer.writeInt(this.language);
//        writer.writeMap(this.sourceFiles);
//        writer.writeMap(this.classFiles);
//        writer.writeBoolean(this.compileStatus);
//        writer.writeBoolean(this.testStatus);
//        writer.writeString(this.stdOut);
//        writer.writeString(this.stdErr);
//        writer.writeString(this.exceptionText);
//        writer.writeString(this.packageName);
//        writer.writeString(this.className);
//        writer.writeString(this.methodName);
//        writer.writeList(this.argTypes);
//        writer.writeList(this.argVals);
//        writer.writeObject(this.resultType);
//        writer.writeObject(this.result);
//        writer.writeString(this.resultValue);
//        writer.writeDouble(this.executionTime);
//        writer.writeDouble(this.maxMemoryUsed) ;
//        writer.writeMap(this.webServiceFiles);
//        writer.writeString(exposedClassName);
//        writer.writeBoolean(threadingAllowed);
//        writer.writeInt(roundType);
//        writer.writeObject(this.problemCustomSettings);
//    }

    /**
     * Checks if is threading allowed.
     *
     * @return true, if is threading allowed
     */
    public boolean isThreadingAllowed() {
        return threadingAllowed;
    }

    /**
     * Sets the threading allowed.
     *
     * @param threadingAllowed the new threading allowed
     */
    public void setThreadingAllowed(boolean threadingAllowed) {
        this.threadingAllowed = threadingAllowed;
    }

    /**
     * Gets the round type.
     *
     * @return the round type
     */
    public int getRoundType(){
        return roundType;
    }

    /**
     * Sets the round type.
     *
     * @param roundType the new round type
     */
    public void setRoundType(int roundType){
        this.roundType = roundType;
    }
}
