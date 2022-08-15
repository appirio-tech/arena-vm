package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * This class represents a test case. It can be either an example test case (as shown in a problem statement), or it may
 * represent a system test case (the former are actually instances of the latter). All test cases consist of one or more
 * input strings and one output string. Example test cases may have an optional annotation, represented as an
 * <code>Element</code>.
 * 
 * @see Element
 * @author Logan Hanks
 * @version $Id: TestCase.java 72733 2008-09-08 08:19:44Z qliu $
 */
public class TestCase extends BaseElement {
    /** Represents the default output when the expected output is unknown. */
    public static final String UNKNOWN_OUTPUT = "UNKNOWN-OUTPUT10291821323";

    /** Represents the output when the execution of primary solution fails. */
    public static final String ERROR = "ERROR-GENERATING-OUTPUT10291821323";

    /** Represents the input arguments of the test case. */
    private String[] input;

    /** Represents the expected output of the test case. */
    private String output;

    /** Represents the annotation element of the test case. */
    private Element annotation;

    /** Represents a flag indicating if the test case is an example test case in the problem statement. */
    private boolean example;

    /** Indicates if this test is part of the system test cases. */
    private boolean systemTest;

    /** Represents the unique ID of the test case. */
    private Integer id;

    /**
     * Creates a new instance of <code>TestCase</code>. It is required by custom serialization.
     */
    public TestCase() {
    }

    /**
     * Constructs an unannotated test case.
     * 
     * @param id The unique ID of the test case.
     * @param input An array of input values. The first value should be the value for the first argument, etc.
     * @param output A string representation of the expected output for this test case
     * @param example Specifies whether or not this is an example test case
     */
    public TestCase(Integer id, String[] input, String output, boolean example) {
        this(id, input, output, null, example);
    }

    /**
     * Creates a test case, which may contain annotation.
     * 
     * @param id The unique ID of the test case.
     * @param input An array of input values. The first value should be the value for the first argument, etc.
     * @param output A string representation of the expected output for this test case
     * @param annotation An <code>Element</code> representing a writer's annotation, or explanation of this test case.
     *            This value can be <code>null</code> if no annotation exists, and should only be non-<code>null</code>
     *            when <code>example</code> is <code>true</code>.
     * @param example Specifies whether or not this is an example test case
     * @see Element
     */
    public TestCase(Integer id, String[] input, String output, Element annotation, boolean example) {
        this(id, input, output, annotation, example, false);
    }

    /**
     * Creates a test case, which may contain annotation and be included/excluded from system test.
     * 
     * @param id The unique ID of the test case.
     * @param input An array of input values. The first value should be the value for the first argument, etc.
     * @param output A string representation of the expected output for this test case
     * @param annotation An <code>Element</code> representing a writer's annotation, or explanation of this test case.
     *            This value can be <code>null</code> if no annotation exists, and should only be non-<code>null</code>
     *            when <code>example</code> is <code>true</code>.
     * @param example Specifies whether or not this is an example test case
     * @param systemTest Specifies whether or not this is a system test case
     * @see Element
     */
    public TestCase(Integer id, String[] input, String output, Element annotation, boolean example, boolean systemTest) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.annotation = annotation;
        this.example = example;
        this.systemTest = systemTest;

        this.output = ProblemComponent.decodeXML(this.output);
        for (int i = 0; i < this.input.length; i++) {
            this.input[i] = ProblemComponent.decodeXML(this.input[i]);
        }
    }

    /**
     * Constructs a TestCase whose output is yet unknown.
     * 
     * @param id The unique ID of the test case.
     * @param input An array of input values. The first value should be the value for the first argument, etc.
     * @param annotation An <code>Element</code> representing a writer's annotation, or explanation of this test case.
     *            This value can be <code>null</code> if no annotation exists, and should only be non-<code>null</code>
     *            when <code>example</code> is <code>true</code>.
     * @param example Specifies whether or not this is an example test case
     * @see Element
     */
    public TestCase(Integer id, String[] input, Element annotation, boolean example) {
        this(id, input, UNKNOWN_OUTPUT, annotation, example);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(id);
        writer.writeObjectArray(input);
        writer.writeString(output);
        writer.writeObject(annotation);
        writer.writeBoolean(example);
        writer.writeBoolean(systemTest);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {

        Object[] o_input;

        id = (Integer) reader.readObject();
        o_input = reader.readObjectArray();
        output = reader.readString();
        annotation = (Element) reader.readObject();
        example = reader.readBoolean();
        systemTest = reader.readBoolean();
        input = new String[o_input.length];
        for (int i = 0; i < o_input.length; i++)
            input[i] = (String) o_input[i];
    }

    /**
     * Gets a flag indicating if this test case is an example test case.
     * 
     * @return <code>true</code> if the test case is an example; <code>false</code> otherwise.
     */
    public boolean isExample() {
        return example;
    }

    /**
     * Sets a flag indicating if the test case is an example test case.
     *  
     * @param example a flag indicating if the test case is an example.
     */
    public void setExample(boolean example) {
        this.example = example;
    }

    /**
     * Returns the string representations of the input values as an array. The order of the array corresponds to the
     * order of parameters to the solution. There is no copy.
     * 
     * @return the string representations of the input arguments.
     */
    public String[] getInput() {
        return input;
    }

    /**
     * Sets the string representation of the expected output.
     * 
     * @param output the expected output.
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * Returns the string representation of the expected output for this test case.
     * 
     * @return the string representation of the expected output.
     */
    public String getOutput() {
        return output;
    }

    /**
     * Returns the annotation associated with this test case. Returns <code>null</code> if no annotation exists.
     * 
     * @return the annotation of the test case.
     */
    public Element getAnnotation() {
        return annotation;
    }

    /**
     * Gets a flag indicating if this test case is a system test case.
     * 
     * @return <code>true</code> if the test case is a system test case; <code>false</code> otherwise.
     */
    public boolean isSystemTest() {
        return systemTest;
    }

    /**
     * Sets a flag indicating if this test case is a system test case.
     * 
     * @param systemTest a flag indicating if this test case is a system test case.
     */
    public void setSystemTest(boolean systemTest) {
        this.systemTest = systemTest;
    }

    /**
     * Gets the unique ID of this test case.
     *
     * @return the unique ID of this test case.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique ID of this test case.
     *
     * @param id the unique ID of this test case.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer(256);

        buf.append("<test-case");
        if (id != null) {
            buf.append(" id=\"");
            buf.append(id);
            buf.append('"');
        }
        if (example)
            buf.append(" example=\"1\"");
        if (systemTest)
            buf.append(" systemTest=\"1\"");
        buf.append('>');

        for (int i = 0; i < input.length; i++) {
            buf.append("<input>");
            buf.append(ProblemComponent.encodeXML(input[i]));
            buf.append("</input>");
        }
        buf.append("<output>");
        buf.append(ProblemComponent.encodeXML(output));
        buf.append("</output>");
        if (annotation != null) {
            buf.append(ProblemComponent.handleTextElement("annotation", annotation));
        }
        buf.append("</test-case>");
        return buf.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TestCase))
            return false;

        TestCase t = (TestCase) obj;
        return toXML().equals(t.toXML());
    }
}
