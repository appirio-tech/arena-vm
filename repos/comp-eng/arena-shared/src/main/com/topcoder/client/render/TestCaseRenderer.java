package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.TestCase;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.TestCase</code> for algorithm problems.
 * 
 * @author Greg Paul
 * @version $Id: TestCaseRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class TestCaseRenderer extends BaseRenderer {
    /** Represents the algorithm test case to be rendered. */
    private TestCase testCase;

    /**
     * Creates a new instance of <code>LongTestCaseRenderer</code>. The test case to be rendered is set to be
     * <code>null</code>.
     */
    public TestCaseRenderer() {
        this.testCase = null;
    }

    /**
     * Creates a new instance of <code>LongTestCaseRenderer</code>. The test case to be rendered is given.
     * 
     * @param testCase the marathon test case to be rendered.
     */
    public TestCaseRenderer(TestCase testCase) {
        this.testCase = testCase;
    }

    /**
     * Sets the element to be rendered. The given element must be a test case.
     * 
     * @param element the test case to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a test case.
     */
    public void setElement(Element element) {
        if (element instanceof TestCase) {
            testCase = (TestCase) element;
        } else {
            throw new IllegalArgumentException("element must be a TestCase Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping. The test case, including its expected output and annotation
     * if any, is rendered using HTML table. When the input or the expected output of the test case is too long (more
     * than 80 characters), the string will break up into several lines next to ','.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the test case.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering the annotation of the test case failed.
     */
    public String toHTML(Language language) throws Exception {
        if (testCase == null) {
            throw new IllegalStateException("The test case is not set.");
        }

        StringBuffer buf = new StringBuffer(256);

        buf.append("<table>");

        buf.append("<tr><td>");
        buf.append("<table>");
        String[] inputs = testCase.getInput();
        for (int i = 0; i < inputs.length; i++) {
            buf.append("<tr><td>");
            buf.append("<pre>");
            buf.append(BaseRenderer.encodeHTML(inputs[i]));
            buf.append("</pre>");
            buf.append("</td></tr>");
        }
        buf.append("</table>");
        buf.append("</td></tr>");

        buf.append("<tr><td>");
        buf.append("<pre>Returns: ");
        buf.append(BaseRenderer.encodeHTML(breakIt(testCase.getOutput())));
        buf.append("</pre>");
        buf.append("</td></tr>");

        buf.append("<tr><td>");
        if (testCase.getAnnotation() != null) {
            buf.append("<table>");
            buf.append("<tr><td colspan=\"2\">");
            buf.append(super.getRenderer(testCase.getAnnotation()).toHTML(language));
            buf.append("</td></tr>");
            buf.append("</table>");
        }
        buf.append("</td></tr>");

        buf.append("</table>");
        return buf.toString();
    }

    /**
     * Renders the element into plain text. The test case, including its annotation if any, is rendered into multiple
     * lines.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the test case.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering the annotation of the test case failed.
     * @deprecated
     */
    public String toPlainText(Language language) throws Exception {
        if (testCase == null) {
            throw new IllegalStateException("The test case is not set.");
        }

        StringBuffer buf = new StringBuffer(256);

        for (int i = 0; i < testCase.getInput().length; i++) {
            buf.append(testCase.getInput()[i]);
            buf.append("\n");
        }
        buf.append("\nReturns: ");
        buf.append(testCase.getOutput());
        if (testCase.getAnnotation() != null) {
            buf.append("\n\n");
            buf.append(super.getRenderer(testCase.getAnnotation()).toPlainText(language));
        }
        return buf.toString();

    }

    /**
     * Breaks the given one-line string into several lines when necessary. The one-line string must represent an array.
     * It only adds end-of-line character between elements in the array (i.e. next to ','). It correctly deals with
     * strings containing ',' character.
     * 
     * @param s the one-line string to be broken.
     * @return a multi-line string having the same content as the argument.
     */
    private String breakIt(String s) {
        /*
         * hopefully a temp hoke so that we don't have to stretch out the screen. the problem is text that is i a pre
         * tag blows out the screen. generally this is a non-issue, but for String arrays it can become an issue if u
         * have large elements. so, breakIt goes through a string and looks for ", which should indicate the end of an
         * string element in an array. it then appends a line return immediately following the ,
         */
        int breakLen = 80;
        StringBuffer out = new StringBuffer(s);
        char ch;
        if (s.length() > breakLen) {
            out = new StringBuffer(s.length());
            out.append("\n");
            if (s.charAt(0) == '{' && s.indexOf('"') == -1) {
                // must be an int[]
                int last = 0;
                for (int i = 0; i < s.length(); i++) {
                    ch = s.charAt(i);
                    if (ch == ',' && out.length() - last > breakLen - 12) {
                        out.append(",\n");
                        last = out.length();
                    } else {
                        out.append(ch);
                    }
                }
            } else {
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '\"' && s.length() > i + 1 && s.charAt(i + 1) == ',') {
                        out.append("\",\n ");
                        i += 2;
                    } else {
                        out.append(s.charAt(i));
                    }
                }
            }
        }
        return out.toString();
    }

}
