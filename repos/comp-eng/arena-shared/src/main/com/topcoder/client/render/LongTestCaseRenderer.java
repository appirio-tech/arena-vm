package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.TestCase;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.TestCase</code> for marathon problems. For marathon test
 * cases, there is no expected output.
 * 
 * @author Greg Paul
 * @version $Id: LongTestCaseRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class LongTestCaseRenderer extends BaseRenderer {
    /** Represents the marathon test case to be rendered. */
    private TestCase testCase;

    /**
     * Creates a new instance of <code>LongTestCaseRenderer</code>. The test case to be rendered is set to be
     * <code>null</code>.
     */
    public LongTestCaseRenderer() {
        this.testCase = null;
    }

    /**
     * Creates a new instance of <code>LongTestCaseRenderer</code>. The test case to be rendered is given.
     * 
     * @param testCase the marathon test case to be rendered.
     */
    public LongTestCaseRenderer(TestCase testCase) {
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
     * Renders the element into HTML with proper escaping. The test case, including its annotation if any, is rendered
     * using HTML table.
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
        buf.append("<pre>");
        String output = testCase.getOutput();
        // pretty printer is enclosing it in double quotes, we're removing them here. would be nice if there was a less
        // crappy solution
        buf.append(output.substring(1, output.length() - 1));
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

        buf.append(testCase.getOutput());
        if (testCase.getAnnotation() != null) {
            buf.append("\n\n");
            buf.append(super.getRenderer(testCase.getAnnotation()).toPlainText(language));
        }
        return buf.toString();

    }
}
