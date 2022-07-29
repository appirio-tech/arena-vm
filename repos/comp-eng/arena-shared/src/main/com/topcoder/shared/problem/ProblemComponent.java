/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * This class fully represents a problem statement. This consists of the following elements:
 * <ul>
 * <li>Problem name (useful for references between problem statements, e.g. in teams problems)
 * <li>Introductory text
 * <li>Signature
 * <ul>
 * <li>Class name
 * <li>Method name
 * <li>Return type
 * <li>Parameter types and names
 * </ul>
 * <li>Some additional, optional text discussing the specification in more technical detail
 * <li>One or more notes
 * <li>One or more input constraints
 * <li>One or more examples
 * <li>Various settings
 * </ul>
 * Instances of this class are serializable and are suitable for client-side use. Instances of this class should
 * generally be constructed by a <code>ProblemComponentFactory</code>. This class also provides a method to convert
 * to its language-independent XML representation. The class also provides methods for obtaining and modifying specific
 * elements.
 * 
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added {@link #DEFAULT_EXECUTION_TIME_LIMIT} constant.</li>
 *      <li>Added {@link #executionTimeLimit} filed, also the getter/setter were added.</li>
 *      <li>Updated {@link #toXML()} to export the execution time limit to XML.</li>
 *  </ul>
 * </p>
 * 
 * <p>
 * Version 1.2 (TC Competition Engine - Code Compilation Issues) change notes:
 *  <ul>
 *      <li>Added {@link #DEFAULT_COMPILE_TIME_LIMIT} constant.</li>
 *      <li>Added {@link #compileTimeLimit} filed, also the getter/setter were added.</li>
 *      <li>Updated {@link #toXML()} to export the compile time limit to XML.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #gccBuildCommand} field.</li>
 *      <li>Added {@link #cppApprovedPath} field.</li>
 *      <li>Added {@link #getGccBuildCommand()} method.</li>
 *      <li>Added {@link #setGccBuildCommand(String)} method.</li>
 *      <li>Added {@link #getCppApprovedPath()} method.</li>
 *      <li>Added {@link #setCppApprovedPath(String)} method.</li>
 *      <li>Update {@link #toXML()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #pythonCommand} field.</li>
 *      <li>Added {@link #pythonApprovedPath} field.</li>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 *      <li>Added {@link #setPythonCommand(String)} method.</li>
 *      <li>Added {@link #getPythonApprovedPath()} method.</li>
 *      <li>Added {@link #setPythonApprovedPath(String)} method.</li>
 *      <li>Update {@link #toXML()} method.</li>
 *      <li>Update {@link #toString()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in 1.5 (Release Assembly - TopCoder Competition Engine Improvement Series 1):
 * <ol>
 * <li>
 * Added {@link #submissionRate} field, along with
 * {@link #getSubmissionRate()} and {@link #setSubmissionRate(int)} methods.
 * </li>
 * <li>
 * Added {@link #exampleSubmissionRate} field, along with
 * {@link #getExampleSubmissionRate()} and {@link #setExampleSubmissionRate(int)} methods.
 * </li>
 * <li>
 * Updated {@link #customReadObject(CSReader)} and {@link #customWriteObject(CSWriter)} methods
 * to deal with newly added fields (see above).
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Add {@link #DEFAULT_SRM_EXECUTION_TIME_LIMIT} field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.7 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Add {@link #problemCustomSettings} field and getter,setter method.</li>
 *      <li>Remove {memLimit, compileTimeLimt, executionTimeLimit, gccBuildCommand, cppApprovedPath,
 *          pythonCommand, pythonApprovedPath} field and getter,setter methods</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.8 (TopCoder Competition Engine - Return Use Custom Checker Flag for Problem):
 * <ol>
 *      <li>Added {@link #customChecker} field (along with getter and setter
        and updated custom serialization methods).</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.9 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Added {@link #DEFAULT_SRM_STACK_LIMIT} constant.</li>
 *      <li>Added {@link #MAX_STACK_LIMIT} constant.</li>
 *      <li>Updated {@link #toXML()} method to support stack limit.</li>
 *      <li>Updated {@link #toString()} method to support stack limit.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 2.0 (TopCoder Competition Engine - Stack Size Configuration For MM Problems v1.0):
 * <ol>
 *      <li>Added {@link #DEFAULT_MM_STACK_LIMIT} constant.</li>
 * </ol>
 * </p>
 *
 * @see com.topcoder.shared.problemParser.ProblemComponentFactory
 * @see Element
 * @see DataType
 * @see com.topcoder.shared.language.Language
 * @author Logan Hanks, savon_cn, gevak, Selena
 * @version 2.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ProblemComponent extends BaseElement {
    /** Represents the HTML tag used to highlight headers. */
    static String SECTION_HEADER = "h3";

    /** Represents the code tag. */
    static String CODE = "<code>";

    /** Represents the default memory limit in megabytes. */
    public static int DEFAULT_MEM_LIMIT = 64;

    /**
     * Represents the default srm memory limit in megabytes.
     */
    public static int DEFAULT_SRM_MEM_LIMIT = 256;

    /**
     * Represents the default SRM stack size limit in megabytes.
     * <=0 value means that default stack size (depends on language) will be used.
     *
     * @since 1.9
     */
    public static int DEFAULT_SRM_STACK_LIMIT = 0;

    /**
     * Represents the default MM stack size limit in megabytes.
     * <=0 value means that default stack size (depends on language) will be used.
     *
     * @since 2.0
     */
    public static int DEFAULT_MM_STACK_LIMIT = 0;

    /**
     * Represents the maximum allowed stack size limit in megabytes.
     *
     * @since 1.9
     */
    public static int MAX_STACK_LIMIT = 2047;

    /**
     * Represents the default execution time limit (14 minutes).
     * @since 1.1
     */
    public static int DEFAULT_EXECUTION_TIME_LIMIT = 14 * 60 * 1000;
    
    /**
     * Represents the default compile time limit (30 seconds).
     * @since 1.2
     */
    public static int DEFAULT_COMPILE_TIME_LIMIT = 30 * 1000;
    /**
     * Represents the default srm executioon time limit (2 seconds).
     * @since 1.6
     */
    public static int DEFAULT_SRM_EXECUTION_TIME_LIMIT = 2000;

    private boolean unsafe = true;

    private boolean valid = true;

    private ArrayList messages = new ArrayList();

    private String name = "";

    private Element intro = new TextElement();

    private String className = "";

    private String exposedClassName = "";
    
    /**
     * <p>the problem custom settings.</p>
     * @since 1.4
     */
    private ProblemCustomSettings problemCustomSettings = ProblemCustomSettings.getDefaultInstance();
    
    private String[] methodNames = new String[0];

    private DataType[] returnTypes = new DataType[0];

    private DataType[][] paramTypes = new DataType[0][0];

    private String[][] paramNames = new String[0][0];

    private String[] exposedMethodNames = new String[0];

    private DataType[] exposedReturnTypes = new DataType[0];

    private DataType[][] exposedParamTypes = new DataType[0][0];

    private String[][] exposedParamNames = new String[0][0];

    private Element spec = new TextElement();

    private Element[] notes = new Element[0];

    private Constraint[] constraints = new Constraint[0];

    private TestCase[] testCases = new TestCase[0];

    private int componentTypeID = ProblemConstants.MAIN_COMPONENT;

    private int componentId = -1;

    private int problemId = -1;

    private String defaultSolution = "";

    private WebService[] webServices = new WebService[0];

    // FIXME should contain max threading allowed
    private int roundType = -1;

    private ArrayList categories = new ArrayList();
    private int codeLengthLimit = Integer.MAX_VALUE;

    /**
     * Represents a flag, indicating if custom checker is used for this problem component.
     *
     * @since 1.8
     */
    private boolean customChecker;

    /**
     * <p>
     * Submission rate, in minutes. Non-positive integer means that it's not set.
     * </p>
     *
     * <p>
     * It's -1 by default (which means that it's not set).
     * Fully mutable, has getter and setter.
     * Can be any value.
     * </p>
     *
     * @since 1.5
     */
    private int submissionRate = -1;

    /**
     * <p>
     * Example submission rate, in minutes. Non-positive integer means that it's not set.
     * </p>
     *
     * <p>
     * It's -1 by default (which means that it's not set).
     * Fully mutable, has getter and setter.
     * Can be any value.
     * </p>
     *
     * @since 1.5
     */
    private int exampleSubmissionRate = -1;

    /**
     * Creates a new instance of <code>ProblemComponent</code>. It is required by custom serialization.
     */
    public ProblemComponent() {
    }

    /**
     * A problem statement must be constructed with a set of known data types, the XML it was originally parsed from,
     * and a flag specifying whether this instance is an ``unsafe'' version.
     * 
     * @param unsafe If <code>true</code>, specifies that the problem statement contains sensitive information that
     *            should be available only to MPSQAS
     */
    public ProblemComponent(boolean unsafe) {
        this.unsafe = unsafe;
    }

    /**
     * Utility function for encoding "special" xml characters, or characters not allowing xml to properly parse.
     * Replaces bad characters with /ASCIIYYY/ where YYY is the ascii value of the character.
     * 
     * @param text the text to be encoded.
     * @return the encoded text.
     */
    static public String encodeXML(String text) {
        StringBuffer buf = new StringBuffer(text.length());
        ArrayList bad = new ArrayList();
        for (int i = 0; i < ProblemConstants.BAD_XML_CHARS.length; i++) {
            bad.add(new Character(ProblemConstants.BAD_XML_CHARS[i]));
        }

        for (int i = 0; i < text.length(); i++) {
            if (bad.indexOf(new Character(text.charAt(i))) == -1)
                buf.append(text.charAt(i));
            else
                buf.append("/ASCII" + (int) text.charAt(i) + "/");
        }
        return buf.toString();
    }

    /**
     * Undoes the encoding scheme in encodeXML.
     * 
     * @param text the text to be decoded.
     * @return the decoded text.
     */
    static public String decodeXML(String text) {
        StringBuffer buf = new StringBuffer(text.length());
        while (text.length() > 0) {
            boolean appendChar = true;
            if (text.startsWith("/ASCII") && text.indexOf("/", 2) != -1) {
                try {
                    buf.append((char) Integer.parseInt(text.substring(6, text.indexOf("/", 2))));
                    appendChar = false;
                    text = text.substring(text.indexOf("/", 2) + 1);
                } catch (NumberFormatException e) {
                }
            }
            if (appendChar) {
                buf.append(text.charAt(0));
                text = text.substring(1);
            }
        }
        return buf.toString();
    }

    /**
     * Performs serialization.
     *
     * @param writer Writer.
     *
     * @throws IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(unsafe);
        writer.writeBoolean(valid);
        writer.writeArrayList(messages);
        writer.writeString(name);
        writer.writeObject(intro);
        writer.writeString(className);
        writer.writeString(exposedClassName);
        writer.writeObjectArray(methodNames);
        writer.writeObjectArray(returnTypes);
        writer.writeObjectArrayArray(paramTypes);
        writer.writeObjectArrayArray(paramNames);
        writer.writeObjectArray(exposedMethodNames);
        writer.writeObjectArray(exposedReturnTypes);
        writer.writeObjectArrayArray(exposedParamTypes);
        writer.writeObjectArrayArray(exposedParamNames);
        writer.writeObject(spec);
        writer.writeObjectArray(notes);
        writer.writeObjectArray(constraints);
        writer.writeObjectArray(testCases);
        writer.writeInt(componentTypeID);
        writer.writeInt(componentId);
        writer.writeInt(problemId);
        writer.writeString(defaultSolution);
        writer.writeObjectArray(webServices);
        writer.writeInt(roundType);
        writer.writeArrayList(categories);
        writer.writeInt(codeLengthLimit);
        writer.writeObject(problemCustomSettings);
        writer.writeInt(submissionRate);
        writer.writeInt(exampleSubmissionRate);
        writer.writeBoolean(customChecker);
    }

    /**
     * Performs de-serialization.
     *
     * @param reader Reader.
     *
     * @throws IOException If any I/O error occurs.
     * @throws ObjectStreamException If any stream error occurs.
     **/
    public void customReadObject(CSReader reader) throws IOException {
        unsafe = reader.readBoolean();
        valid = reader.readBoolean();
        messages = reader.readArrayList();
        name = reader.readString();
        intro = (Element) reader.readObject();
        className = reader.readString();
        exposedClassName = reader.readString();
        methodNames = (String[]) reader.readObjectArray(String.class);
        returnTypes = (DataType[]) reader.readObjectArray(DataType.class);
        paramTypes = (DataType[][]) reader.readObjectArrayArray(DataType.class);
        paramNames = (String[][]) reader.readObjectArrayArray(String.class);
        exposedMethodNames = (String[]) reader.readObjectArray(String.class);
        exposedReturnTypes = (DataType[]) reader.readObjectArray(DataType.class);
        exposedParamTypes = (DataType[][]) reader.readObjectArrayArray(DataType.class);
        exposedParamNames = (String[][]) reader.readObjectArrayArray(String.class);
        spec = (Element) reader.readObject();
        notes = (Element[]) reader.readObjectArray(Element.class);
        constraints = (Constraint[]) reader.readObjectArray(Constraint.class);
        testCases = (TestCase[]) reader.readObjectArray(TestCase.class);
        componentTypeID = reader.readInt();
        componentId = reader.readInt();
        problemId = reader.readInt();
        defaultSolution = reader.readString();
        webServices = (WebService[]) reader.readObjectArray(WebService.class);
        roundType = reader.readInt();
        categories = reader.readArrayList();
        codeLengthLimit = reader.readInt();
        problemCustomSettings = (ProblemCustomSettings)reader.readObject();
        submissionRate = reader.readInt();
        exampleSubmissionRate = reader.readInt();
        customChecker = reader.readBoolean();
    }

    /**
     * If a problem component is unsafe, then it should not have all the system test cases, only those that are marked
     * as examples.
     * 
     * @return <code>true</code> if the component is unsafe; <code>false</code> otherwise.
     */
    public boolean isUnsafe() {
        return unsafe;
    }

    /**
     * If a problem component is unsafe, then it should not have all the system test cases, only those that are marked
     * as examples.
     * 
     * @param unsafe a flag indicating if the component is unsafe.
     */
    public void setUnsafe(boolean unsafe) {
        this.unsafe = unsafe;
    }

    /**
     * A problem statement is valid if it was successfully parsed without errors.
     * 
     * @return <code>true</code> if the component is valid; <code>false</code> otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets a flag indicating if the problem component is valid.
     * 
     * @param valid a flag indicating if the problem component is valid.
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Get the list of <code>ProblemMessage</code>s generated by the parsing process.
     * 
     * @return An <code>ArrayList</code> of <code>ProblemMessage</code>s
     * @see ProblemMessage
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public ArrayList getMessages() {
        return messages;
    }

    /**
     * Sets the list of <code>ProblemMessage</code> instances of this problem component.
     * 
     * @param messages the list of <code>ProblemMessage</code> instances.
     */
    public void setMessages(ArrayList messages) {
        this.messages = messages;
    }

    /**
     * Clears the list of problem messages.
     */
    public void clearMessages() {
        messages = new ArrayList();
    }

    /**
     * Append a <code>ProblemMessage</code> to the list of messages.
     * 
     * @param message the problem message to be added.
     */
    public void addMessage(ProblemMessage message) {
        if (message.getType() != ProblemMessage.WARNING)
            valid = false;
        messages.add(message);
    }

    /**
     * The ``intro'' is the required introductory text for a problem statement (shown before the signature).
     * 
     * @return the introductory text of the problem.
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Element getIntro() {
        return intro;
    }

    /**
     * Updates the ``intro'' element.
     * 
     * @see #getIntro
     * @param intro the introductory text of the problem.
     */
    public void setIntro(Element intro) {
        this.intro = intro;
    }

    /**
     * The ``spec'' is the optional text following the signature, typically giving more technical information about the
     * problem.
     * 
     * @return the optional text after the signature.
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Element getSpec() {
        return spec;
    }

    /**
     * Updates the ``spec'' element.
     * 
     * @see #getSpec
     * @param spec the optional text after the signature.
     */
    public void setSpec(Element spec) {
        this.spec = spec;
    }

    /**
     * Gets the name of the class that should be defined in solutions to this problem.
     * 
     * @return the class name of the problem.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets the writer-provided exposed class name in marathon problems.
     * 
     * @return the exposed class name.
     */
    public String getExposedClassName() {
        return exposedClassName;
    }

    /**
     * Sets the name of the class that should be defined in solutions to this problem.
     * 
     * @param className the class name of the problem.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Sets the writer-provided exposed class name in marathon problems.
     * 
     * @param className the exposed class name.
     */
    public void setExposedClassName(String className) {
        this.exposedClassName = className;
    }

    /**
     * Gets the name of the method that should be defined in solutions to this problem.
     * 
     * @return the name of the method.
     */
    @JsonIgnore
    public String getMethodName() {
        return methodNames.length > 0 ? methodNames[0] : "";
    }

    /**
     * Gets the name of the method with given index. It is used by marathon problems since it may require multiple
     * methods.
     * 
     * @param idx the index of the method to be returned.
     * @return the name of the method with the given index.
     */
    public String getMethodName(int idx) {
        return methodNames.length > idx ? methodNames[idx] : "";
    }

    /**
     * Gets the name of the writer-provided method with given index. It is used by marathon problems since it may
     * require multiple methods.
     * 
     * @param idx the index of the method to be returned.
     * @return the name of the method with the given index.
     */
    public String getExposedMethodName(int idx) {
        return exposedMethodNames.length > idx ? exposedMethodNames[idx] : "";
    }

    /**
     * Sets the name of the method that should be defined in solutions to this problem.
     * 
     * @param methodName the name of the method to be set.
     */
    public void setMethodName(String methodName) {
        this.methodNames = new String[] {methodName};
    }

    /**
     * Sets the names of the methods that should be defined in the user solutions. It is used by marathon problems.
     * There is no copy.
     * 
     * @param methodNames the method names to be set.
     */
    public void setMethodNames(String[] methodNames) {
        this.methodNames = methodNames;
    }

    /**
     * Sets the names of the methods that should be provided by writer. It is used by marathon problems. There is no
     * copy.
     * 
     * @param methodNames the method names to be set.
     */
    public void setExposedMethodNames(String[] methodNames) {
        this.exposedMethodNames = methodNames;
    }

    /**
     * Gets the return type of the method that should be defined in solutions to this problem.
     * 
     * @see DataType
     * @return the return type of the method.
     */
    @JsonIgnore
    public DataType getReturnType() {
        return returnTypes.length > 0 ? returnTypes[0] : new DataType();
    }

    /**
     * Sets the return type of the method that should be defined in solutions to this problem.
     * 
     * @param returnType the return type of the method.
     * @see DataType
     */
    public void setReturnType(DataType returnType) {
        this.returnTypes = new DataType[] {returnType};
    }

    /**
     * Sets the return types of the methods that should be defined in solutions. It is used by marathon problems. There
     * is no copy.
     * 
     * @param returnTypes the list of return types of the methods.
     */
    public void setReturnTypes(DataType[] returnTypes) {
        this.returnTypes = returnTypes;
    }

    /**
     * Sets the return type of the method that should be defined by writer. It is used by marathon problems.
     * 
     * @param returnType the return type of writer-provided method.
     */
    public void setExposedReturnType(DataType returnType) {
        this.exposedReturnTypes = new DataType[] {returnType};
    }

    /**
     * Sets the return types of the methods that should be defined by writer. It is used by marathon problems. There is
     * no copy.
     * 
     * @param returnTypes the list of return types of writer-provided methods.
     */
    public void setExposedReturnTypes(DataType[] returnTypes) {
        this.exposedReturnTypes = returnTypes;
    }

    /**
     * Gets the data type of all of the arguments to the method that should be defined in solutions to this problem.
     * 
     * @return An array of <code>DataType</code>s, where the first value is the type of the first argument, the
     *         second value is the type of the second argument, and so on
     * @see DataType
     */
    @JsonIgnore
    public DataType[] getParamTypes() {
        return paramTypes.length > 0 ? paramTypes[0] : new DataType[0];
    }

    /**
     * Gets the data type of all of the arguments to a method that should be defined in solutions to this problem. The
     * index of the method is given.
     * 
     * @param idx the index of the method whose argument types are returned.
     * @return An array of <code>DataType</code>s, where the first value is the type of the first argument, the
     *         second value is the type of the second argument, and so on
     * @see DataType
     */
    public DataType[] getParamTypes(int idx) {
        return paramTypes.length > idx ? paramTypes[idx] : new DataType[0];
    }

    /**
     * Gets the data type of all of the arguments to the method that should be provided by writer.
     * 
     * @return An array of <code>DataType</code>s, where the first value is the type of the first argument, the
     *         second value is the type of the second argument, and so on
     * @see DataType
     */
    @JsonIgnore
    public DataType[] getExposedParamTypes() {
        return exposedParamTypes.length > 0 ? exposedParamTypes[0] : new DataType[0];
    }

    /**
     * Gets the data type of all of the arguments to a method that should be provided by writer. The index of the method
     * is given.
     * 
     * @param idx the index of the method whose argument types are returned.
     * @return An array of <code>DataType</code>s, where the first value is the type of the first argument, the
     *         second value is the type of the second argument, and so on
     * @see DataType
     */
    public DataType[] getExposedParamTypes(int idx) {
        return exposedParamTypes.length > idx ? exposedParamTypes[idx] : new DataType[0];
    }

    /**
     * Sets the data type of all of the arguments to the method that should be defined in solutions to this problem.
     * 
     * @param paramTypes An array of <code>DataType</code>s, where the first value is the type of the first argument,
     *            the second value is the type of the second argument, and so on
     * @see DataType
     */
    public void setParamTypes(DataType[] paramTypes) {
        this.paramTypes = new DataType[][] {paramTypes};
    }

    /**
     * Sets the data types of all of the arguments to all methods that should be defined in solutions to this problem.
     * 
     * @param paramTypes An array of arrays of <code>DataType</code>s, where the first dimension represents the
     *            method index and the second dimension represents argument index.
     * @see DataType
     */
    public void setParamTypes(DataType[][] paramTypes) {
        this.paramTypes = paramTypes;
    }
    
    public void setAllParamTypes(DataType[][] paramTypes) {
        this.paramTypes = paramTypes;
    }

    /**
     * Sets the data type of all of the arguments to the method that should be provided by writer.
     * 
     * @param paramTypes An array of <code>DataType</code>s, where the first value is the type of the first argument,
     *            the second value is the type of the second argument, and so on
     * @see DataType
     */
    public void setExposedParamTypes(DataType[] paramTypes) {
        this.exposedParamTypes = new DataType[][] {paramTypes};
    }

    /**
     * Sets the data types of all of the arguments to all methods that should be provided by writer.
     * 
     * @param paramTypes An array of arrays of <code>DataType</code>s, where the first dimension represents the
     *            method index and the second dimension represents argument index.
     * @see DataType
     */
    public void setExposedParamTypes(DataType[][] paramTypes) {
        this.exposedParamTypes = paramTypes;
    }

    /**
     * Gets the names of the arguments to the method that should be defined in solutions to this problem.
     * 
     * @return An array of <code>String</code>s, where the first value is the name of the first argument, the second
     *         value is the name of the second argument, and so on
     */
    @JsonIgnore
    public String[] getParamNames() {
        return paramNames.length > 0 ? paramNames[0] : new String[0];
    }

    /**
     * Gets the names of the arguments to a method that should be defined in solutions to this problem. The index of the
     * method is given.
     * 
     * @param idx the index of the method whose argument names are returned.
     * @return An array of <code>String</code>s, where the first value is the name of the first argument, the second
     *         value is the name of the second argument, and so on
     */
    public String[] getParamNames(int idx) {
        return paramNames.length > idx ? paramNames[idx] : new String[0];
    }

    /**
     * Gets the names of the arguments to the method that should be provided by writer.
     * 
     * @return An array of <code>String</code>s, where the first value is the name of the first argument, the second
     *         value is the name of the second argument, and so on
     */
    public String[] getExposedParamNames() {
        return exposedParamNames.length > 0 ? exposedParamNames[0] : new String[0];
    }

    /**
     * Gets the names of the arguments to a method that should be provided by writer. The index of the method is given.
     * 
     * @param idx the index of the method whose argument names are returned.
     * @return An array of <code>String</code>s, where the first value is the name of the first argument, the second
     *         value is the name of the second argument, and so on
     */
    public String[] getExposedParamNames(int idx) {
        return exposedParamNames.length > idx ? exposedParamNames[idx] : new String[0];
    }

    /**
     * Sets the names of the arguments to the method that should be defined in solutions to this problem.
     * 
     * @param paramNames An array of <code>String</code>s, where the first value is the name of the first argument,
     *            the second value is the name of the second argument, and so on
     */
    public void setParamNames(String[] paramNames) {
        this.paramNames = new String[][] {paramNames};
    }

    /**
     * Sets the names of the arguments to the method that should be defined in solutions to this problem.
     * 
     * @param paramNames An array of arrays of <code>String</code>s, where the first dimension represents the index
     *            of methods and the second dimension represents the index of arguments.
     */
    public void setParamNames(String[][] paramNames) {
        this.paramNames = paramNames;
    }

    /**
     * Sets the names of the arguments to the method that should be provided by writer.
     * 
     * @param paramNames An array of <code>String</code>s, where the first value is the name of the first argument,
     *            the second value is the name of the second argument, and so on
     */
    @JsonIgnore
    public void setExposedParamNames(String[] paramNames) {
        this.exposedParamNames = new String[][] {paramNames};
    }

    /**
     * Sets the names of the arguments to the method that should be provided by writer.
     * 
     * @param paramNames An array of arrays of <code>String</code>s, where the first dimension represents the index
     *            of methods and the second dimension represents the index of arguments.
     */
    public void setExposedParamNames(String[][] paramNames) {
        this.exposedParamNames = paramNames;
    }

    /**
     * Gets the list of notes.
     * 
     * @return An array of <code>Element</code>s, each <code>Element</code> representing a note
     * @see Element
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Element[] getNotes() {
        return notes;
    }

    /**
     * Sets the list of notes.
     * 
     * @param notes An array of <code>Element</code>s, each <code>Element</code> representing a note
     * @see Element
     */
    public void setNotes(Element[] notes) {
        this.notes = notes;
    }

    /**
     * Gets the list of constraints.
     * 
     * @return An array of <code>Constraint</code>s, each <code>Constraint</code> representing a constraint
     * @see Constraint
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Constraint[] getConstraints() {
        return constraints;
    }

    /**
     * Sets the list of constraints.
     * 
     * @param constraints An array of <code>Constraint</code>s, each <code>Constraint</code> representing a
     *            constraint
     * @see Constraint
     */
    public void setConstraints(Constraint[] constraints) {
        this.constraints = constraints;
    }

    /**
     * Get the list of test cases. This will include at least all of the example test cases. If this is an unsafe
     * version of the problem statement, it will include the system test cases as well. There is no copy.
     * 
     * @see TestCase
     * @return the list of test cases.
     */
    public TestCase[] getTestCases() {
        return testCases;
    }

    /**
     * Set the list of test cases.
     * 
     * @param testCases the list of test cases.
     */
    public void setTestCases(TestCase[] testCases) {
        this.testCases = testCases;
    }

    /**
     * Sets the list of web services
     * 
     * @param webServices the list of referred web services.
     */
    public void setWebServices(WebService[] webServices) {
        this.webServices = webServices;
    }

    /**
     * Get the list of web services associated with this component
     * 
     * @return the list of web services referred.
     */
    public WebService[] getWebServices() {
        return webServices;
    }

    /**
     * Gets the type of the round.
     * 
     * @return Returns the roundType.
     */
    public int getRoundType() {
        return roundType;
    }

    /**
     * Sets the type of the round.
     * 
     * @param roundType The roundType to set.
     */
    public void setRoundType(int roundType) {
        this.roundType = roundType;
    }

    /**
     * Gets the length limit of solutions. The length is in bytes.
     *
     * @return the length limit of solution code.
     */
    public int getCodeLengthLimit() {
        return codeLengthLimit;
    }

    /**
     * Sets the length limit of solutions. The length is in bytes.
     *
     * @param codeLengthLimit the length limit of solution code.
     */
    public void setCodeLengthLimit(int codeLengthLimit) {
        this.codeLengthLimit = codeLengthLimit;
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
     * @param problemCustomSettings the problem custom settings.
     * @since 1.7
     */
    public void setProblemCustomSettings(ProblemCustomSettings problemCustomSettings) {
        this.problemCustomSettings = problemCustomSettings;
    }

    /**
     * Produces the XML of an element. If the given element is a text element, its content is nested under the tag given
     * as inner text. Otherwise, the XML of the given element is returned.
     * 
     * @param name the tag name where the text element content is nested.
     * @param elem the element whose XML is produced.
     * @return the XML of the element.
     */
    public static String handleTextElement(String name, Element elem) {
        if (elem instanceof TextElement) {
            return "<" + name + ">" + elem.toString() + "</" + name + ">";
        }
        return elem.toXML();
    }
    /**
     * Get the problem text as xml format.
     * @return the problem text as xml format.
     */
    public String toXML() {
        StringBuffer buf = new StringBuffer(4096);

        buf.append("<?xml version=\"1.0\"?>");
        buf.append("<problem");

        buf.append(" xmlns=\"http://topcoder.com\"");
        buf.append(" name=\"");
        buf.append(name);
        buf.append("\" code_length_limit=\"");
        buf.append(codeLengthLimit);
        buf.append("\" execution_time_limit=\"");
        buf.append(problemCustomSettings.getExecutionTimeLimit());
        buf.append("\" compile_time_limit=\"");
        buf.append(problemCustomSettings.getCompileTimeLimit());
        buf.append("\" gcc_build_command=\"");
        buf.append(encodeHTML(problemCustomSettings.getGccBuildCommand()));
        buf.append("\" cpp_approved_path=\"");
        buf.append(encodeHTML(problemCustomSettings.getCppApprovedPath()));
        buf.append("\" python_command=\"");
        buf.append(encodeHTML(problemCustomSettings.getPythonCommand()));
        buf.append("\" python_approved_path=\"");
        buf.append(encodeHTML(problemCustomSettings.getPythonApprovedPath()));
        buf.append("\"><signature><class>");
        buf.append(className);
        buf.append("</class>");
        for (int i = 0; i < methodNames.length; i++) {
            buf.append("<method><name>");
            buf.append(methodNames[i]);
            buf.append("</name><return>");
            buf.append(returnTypes[i].toXML());
            buf.append("</return><params>");
            for (int j = 0; j < paramTypes[i].length; j++) {
                buf.append("<param>");
                buf.append(paramTypes[i][j].toXML());
                buf.append("<name>");
                buf.append(paramNames[i][j]);
                buf.append("</name></param>");
            }
            buf.append("</params></method>");
        }
        if (exposedClassName != null && !exposedClassName.equals("")) {
            buf.append("<exposed_class>");
            buf.append(exposedClassName);
            buf.append("</exposed_class>");
        }
        for (int i = 0; i < exposedMethodNames.length; i++) {
            buf.append("<exposed_method><name>");
            buf.append(exposedMethodNames[i]);
            buf.append("</name><return>");
            buf.append(exposedReturnTypes[i].toXML());
            buf.append("</return><params>");
            for (int j = 0; j < exposedParamTypes[i].length; j++) {
                buf.append("<param>");
                buf.append(exposedParamTypes[i][j].toXML());
                buf.append("<name>");
                buf.append(exposedParamNames[i][j]);
                buf.append("</name></param>");
            }
            buf.append("</params></exposed_method>");
        }
        buf.append("</signature>");
        if (intro != null)
            buf.append(handleTextElement("intro", intro));
        if (spec != null)
            buf.append(handleTextElement("spec", spec));
        buf.append("<notes>");
        for (int i = 0; i < notes.length; i++) {
            buf.append(handleTextElement("note", notes[i]));
        }
        buf.append("</notes><constraints>");
        for (int i = 0; i < constraints.length; i++)
            buf.append(constraints[i].toXML());
        buf.append("</constraints><test-cases>");
        for (int i = 0; i < testCases.length; i++) {
            buf.append(testCases[i].toXML());
        }
        buf.append("</test-cases><memlimit>");
        buf.append(problemCustomSettings.getMemLimit());
        buf.append("</memlimit><stacklimit>");
        buf.append(problemCustomSettings.getStackLimit());
        buf.append("</stacklimit><roundType>");
        buf.append(roundType);
        buf.append("</roundType></problem>");
        return buf.toString();
    }
    /**
     * the default object toString method.
     */
    public String toString() {
        StringBuilder str = new StringBuilder(256);
        str.append("com.topcoder.shared.problem.ProblemComponent[");
        str.append("unsafe=");
        str.append(unsafe);
        str.append(",codeLengthLimit=");
        str.append(codeLengthLimit);
        str.append(",executionTimeLimit=");
        str.append(problemCustomSettings.getExecutionTimeLimit());
        str.append(",compileTimeLimit=");
        str.append(problemCustomSettings.getCompileTimeLimit());
        str.append(",gccBuildCommand=");
        str.append(problemCustomSettings.getGccBuildCommand());
        str.append(",cppApprovedPath=");
        str.append(problemCustomSettings.getCppApprovedPath());
        str.append(",pythonCommand=");
        str.append(problemCustomSettings.getPythonCommand());
        str.append(",pythonApprovedPath=");
        str.append(problemCustomSettings.getPythonApprovedPath());
        str.append(",valid=");
        str.append(valid);
        str.append(",messages=");
        str.append(messages);
        str.append(",name=");
        str.append(name);
        str.append(",intro=");
        str.append(intro);
        str.append(",className=");
        str.append(className);
        str.append(",methodNames=");
        str.append(Arrays.toString(methodNames));
        str.append(",returnTypes=");
        str.append(Arrays.toString(returnTypes));
        str.append(",paramTypes=");
        str.append(Arrays.toString(paramTypes));
        str.append(",paramNames=");
        str.append(Arrays.toString(paramNames));
        str.append(",exposedMethodNames=");
        str.append(Arrays.toString(exposedMethodNames));
        str.append(",exposedReturnTypes=");
        str.append(Arrays.toString(exposedReturnTypes));
        str.append(",exposedParamTypes=");
        str.append(Arrays.toString(exposedParamTypes));
        str.append(",exposedParamNames=");
        str.append(Arrays.toString(exposedParamNames));
        str.append(",memLimit=");
        str.append(problemCustomSettings.getMemLimit());
        str.append(",stackLimit=");
        str.append(problemCustomSettings.getStackLimit());
        str.append(",roundType=");
        str.append(roundType);
        str.append(",spec=");
        str.append(spec);
        str.append(",notes=");
        str.append(Arrays.toString(notes));
        str.append(",constraints=");
        str.append(Arrays.toString(constraints));
        str.append(",testCases=");
        str.append(Arrays.toString(testCases));
        str.append("]");
        return str.toString();
    }

    /**
     * Get the component type id
     * 
     * @return the component type ID.
     */
    public int getComponentTypeID() {
        return componentTypeID;
    }

    /**
     * Set the component type id
     * 
     * @param componentTypeID the component type ID.
     */
    public void setComponentTypeID(int componentTypeID) {
        this.componentTypeID = componentTypeID;
    }

    /**
     * Set the component id
     * 
     * @param componentId the component ID.
     */
    public final void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    /**
     * Set the default solution.
     * 
     * @param solution the default solution source code.
     */
    public final void setDefaultSolution(String solution) {
        this.defaultSolution = solution;
    }

    /**
     * Gets the component id.
     * 
     * @return the component ID.
     */
    public final int getComponentId() {
        return this.componentId;
    }

    /**
     * Gets the default solution
     * 
     * @return the default solution source code.
     */
    public final String getDefaultSolution() {
        return this.defaultSolution;
    }

    /**
     * Gets the problem id that this component is associated with
     * 
     * @return the problem ID of this component.
     */
    public int getProblemId() {
        return problemId;
    }

    /**
     * Sets the problem id that this component is associated with
     * 
     * @param problemId the problem ID of this component.
     */
    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    /**
     * Gets the cache key for supplied component id
     * 
     * @param componentID the component ID.
     * @return a cache key used to cache the component with the given ID.
     */
    public static String getCacheKey(int componentID) {
        return "ProblemComponent." + componentID;
    }

    /**
     * Gets the cache key for this component.
     * 
     * @return a cache key used to cache the component.
     */
    @JsonIgnore
    public final String getCacheKey() {
        return getCacheKey(componentId);
    }

    /**
     * Gets the string representation of the return type for the required method for this component for the specified
     * language
     * 
     * @param language the languageID
     * @return the return type for the languageID
     */
    public String getReturnType(int language) {
        return returnTypes[0].getDescriptor(language);
    }

    /**
     * Gets the descriptor of the return type for Java.
     * 
     * @return the descriptor of the return type for Java.
     * @deprecated
     */
    @JsonIgnore
    public String getResultType() {
        return returnTypes[0].getDescriptor(JavaLanguage.ID);
    }

    /**
     * Gets the list of argument type descriptors for Java. A copy is returned.
     * 
     * @return the list of argument type descriptors for Java.
     * @deprecated for old stuff, just gets array list of java types.
     */
    @JsonIgnore
    public ArrayList getArgs() {
        ArrayList ret = new ArrayList();
        for (int i = 0; i < paramTypes[0].length; i++) {
            ret.add(paramTypes[0][i].getDescriptor(JavaLanguage.ID));
        }
        return ret;
    }

    /**
     * Gets a list of all return types of all methods needed in the solution. There is no copy.
     * 
     * @return a list of all return types of all methods needed in the solution.
     */
    @JsonIgnore
    public DataType[] getAllReturnTypes() {
        return returnTypes;
    }

    /**
     * Gets a list of all method names needed in the solution. There is no copy.
     * 
     * @return a list of all method names needed in the solution.
     */
    @JsonIgnore
    public String[] getAllMethodNames() {
        return methodNames;
    }

    /**
     * Gets a list of all argument types of all methods needed in the solution. There is no copy. The first dimension is
     * the method index; while the second dimension is the argument index.
     * 
     * @return a list of all argument types of all methods.
     */
    public DataType[][] getAllParamTypes() {
        return paramTypes;
    }

    /**
     * Gets a list of all argument names of all methods needed in the solution. There is no copy. The first dimension is
     * the method index; while the second dimension is the argument index.
     * 
     * @return a list of all argument names of all methods.
     */
    public String[][] getAllParamNames() {
        return paramNames;
    }
    
    public void setAllParamNames(String[][] paramNames) {
    	this.paramNames = paramNames;
    }

    /**
     * Gets a list of all return types of all methods provided by writer. There is no copy.
     * 
     * @return a list of all return types of all methods provided by writer.
     */
    public DataType[] getAllExposedReturnTypes() {
        return exposedReturnTypes;
    }
    
    public void setAllExposedReturnTypes(DataType[] exposedReturnTypes) {
    	this.exposedReturnTypes = exposedReturnTypes;
    }

    /**
     * Gets a list of all method names provided by writer. There is no copy.
     * 
     * @return a list of all method names provided by writer.
     */
    @JsonIgnore
    public String[] getAllExposedMethodNames() {
        return exposedMethodNames;
    }

    /**
     * Gets a list of all argument types of all methods provided by writer. There is no copy. The first dimension is the
     * method index; while the second dimension is the argument index.
     * 
     * @return a list of all argument types of all methods.
     */
    public DataType[][] getAllExposedParamTypes() {
        return exposedParamTypes;
    }
    
    public void setAllExposedParamTypes(DataType[][] exposedParamTypes) {
    	this.exposedParamTypes = exposedParamTypes;
    }

    /**
     * Gets a list of all argument names of all methods provided by writer. There is no copy. The first dimension is the
     * method index; while the second dimension is the argument index.
     * 
     * @return a list of all argument names of all methods.
     */
    public String[][] getAllExposedParamNames() {
        return exposedParamNames;
    }
    
    public void setAllExposedParamNames(String[][] exposedParamNames) {
    	this.exposedParamNames = exposedParamNames;
    }

    /**
     * Sets the list of categories which this problem belongs to. There is no copy.
     * 
     * @param categories the list of categories.
     */
    public void setCategories(ArrayList categories) {
        this.categories = categories;
    }

    /**
     * Gets the list of categories which this problem belongs to. There is no copy.
     * 
     * @return the list of categories.
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public ArrayList getCategories() {
        return categories;
    }

    /**
     * Gets submission rate.
     *
     * @return Submission rate, in minutes. Non-positive integer means that it's not set.
     * @since 1.5
     */
    public int getSubmissionRate() {
        return submissionRate;
    }

    /**
     * Sets submission rate.
     *
     * @param submissionRate Submission rate, in minutes. Non-positive integer means that it's not set.
     * @since 1.5
     */
    public void setSubmissionRate(int submissionRate) {
        this.submissionRate = submissionRate;
    }

    /**
     * Gets example submission rate.
     *
     * @return Example submission rate, in minutes. Non-positive integer means that it's not set.
     * @since 1.5
     */
    public int getExampleSubmissionRate() {
        return exampleSubmissionRate;
    }

    /**
     * Sets example submission rate.
     *
     * @param exampleSubmissionRate Example submission rate, in minutes.
     *     Non-positive integer means that it's not set.
     * @since 1.5
     */
    public void setExampleSubmissionRate(int exampleSubmissionRate) {
        this.exampleSubmissionRate = exampleSubmissionRate;
    }

    /**
     * Sets the memory limit (in MB)
     *
     * @param memLimitMB the memory limit of execution of solution.
     * @deprecated please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public void setMemLimitMB(int memLimitMB) {
        problemCustomSettings.setMemLimit(memLimitMB);
    }

    /**
     * Gets the memory limit (in MB)
     *
     * @return the memory limit of execution of solution.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public int getMemLimitMB() {
        return problemCustomSettings.getMemLimit();
    }


    /**
     * Gets the execution time limit of solutions. The time is in ms.
     *
     * @return the execution time limit of solution code.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public int getExecutionTimeLimit() {
        return  problemCustomSettings.getExecutionTimeLimit();
    }

    /**
     * Sets the execution time limit of solutions. The time is in ms.
     *
     * @param executionTimeLimit the execution time limit of solution code.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public void setExecutionTimeLimit(int executionTimeLimit) {
        problemCustomSettings.setExecutionTimeLimit(executionTimeLimit);
    }

    /**
     * Get the gcc build command.
     * @return the gcc build command.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public String getGccBuildCommand() {
        return problemCustomSettings.getGccBuildCommand();
    }
    /**
     * Set the gcc build command.
     * @param gccBuildCommand
     *         the gcc build command.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public void setGccBuildCommand(String gccBuildCommand) {
        problemCustomSettings.setGccBuildCommand(gccBuildCommand);
    }
    /**
     * Get the cpp approved path.
     * @return the cpp approved path.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public String getCppApprovedPath() {
        return problemCustomSettings.getCppApprovedPath();
    }
    /**
     * Set the cpp approved path.
     * @param cppApprovedPath
     *         the cpp approved path.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public void setCppApprovedPath(String cppApprovedPath) {
        problemCustomSettings.setCppApprovedPath(cppApprovedPath);
    }
    /**
     * Get the python command.
     * @return the python command.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public String getPythonCommand() {
        return problemCustomSettings.getPythonCommand();
    }
    /**
     * Set the python command.
     * @param pythonCommand
     *         the python command.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public void setPythonCommand(String pythonCommand) {
        problemCustomSettings.setPythonCommand(pythonCommand);
    }
    /**
     * Get the python approved path.
     * @return the python approved path.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public String getPythonApprovedPath() {
        return problemCustomSettings.getPythonApprovedPath();
    }
    /**
     * Set the python approved path.
     * @param pythonApprovedPath
     *         the python approved path.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public void setPythonApprovedPath(String pythonApprovedPath) {
        problemCustomSettings.setPythonApprovedPath(pythonApprovedPath);
    }
    /**
     * Gets the compile time limit of solutions. The time is in ms.
     *
     * @return the compile time limit of solution code.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public int getCompileTimeLimit() {
        return problemCustomSettings.getCompileTimeLimit();
    }

    /**
     * Sets the compile time limit of solutions. The time is in ms.
     *
     * @param compileTimeLimit the compile time limit of solution code.
     * @deprecated  please use the corresponding method in ProblemCustomSettings, this is for support legacy plugin.
     */
    public void setCompileTimeLimit(int compileTimeLimit) {
       problemCustomSettings.setCompileTimeLimit(compileTimeLimit);
    }

    /**
     * Gets flag, indicating if custom checker is used for this problem component.
     *
     * @return flag, indicating if custom checker is used for this problem component.
     *
     * @since 1.8
     */
    public boolean isCustomChecker() {
        return customChecker;
    }
 
    /**
     * Sets flag, indicating if custom checker is used for this problem component.
     *
     * @param customChecker flag, indicating if custom checker is used for this problem component.
     *
     * @since 1.8
     */
    public void setCustomChecker(boolean customChecker) {
        this.customChecker= customChecker;
    }

	public String[] getMethodNames() {
		return methodNames;
	}

	public DataType[] getReturnTypes() {
		return returnTypes;
	}

	public String[] getExposedMethodNames() {
		return exposedMethodNames;
	}
    
}
