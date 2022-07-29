/*
* Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.client.render;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Locale;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.language.PythonLanguage;
import com.topcoder.shared.language.Python3Language;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.problem.TestCase;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.ProblemComponent</code>.
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Add {@link #getExecutionTimeLimitPresent()} method.</li>
 *      <li>Add {@link #toHTML(Language language, boolean withHeaders, boolean specifyPrimary)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #toHTML(Language language, boolean withHeaders, boolean specifyPrimary)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Updated {@link #toHTML(Language, boolean, boolean)} method to render stack limit if non-default.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Python3 Support):
 * <ol>
 *      <li>Updated {@link #toHTML(Language, boolean, boolean)} method to generate HTML for Python3.</li>
 * </ol>
 * </p>
 *
 * @author Greg Paul, savon_cn, Selena, liuliquan
 * @version 1.4
 */
public class ProblemComponentRenderer extends BaseRenderer {
    /** Represents the HTML tag used to render section header. */
    private static final String SECTION_HEADER = "h3";

    /** Represents the HTML context used to produce left margin (4 spaces). */
    private static final String LEFT_MARGIN = "&#160;&#160;&#160;&#160;";

    /** Represents the problem component to be rendered. */
    private ProblemComponent problemComponent;

    /** Represents the background color of the rendered HTML context. */
    private Color backgroundColor = null;

    /** Represents the foreground color of the rendered HTML context. */
    private Color foregroundColor = null;

    /**
     * Creates a new instance of <code>ProblemComponentRenderer</code>. The problem component to be rendered is set
     * as <code>null</code>.
     */
    public ProblemComponentRenderer() {
        this.problemComponent = null;
    }

    /**
     * Creates a new instance of <code>ProblemComponentRenderer</code>. The problem component to be rendered is
     * given.
     * 
     * @param problemComponent the problem component to be rendered.
     */
    public ProblemComponentRenderer(ProblemComponent problemComponent) {
        this.problemComponent = problemComponent;
    }

    /**
     * Sets the element to be rendered. The given element must be a problem component.
     * 
     * @param element the problem component to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a problem component.
     */
    public void setElement(Element element) {
        if (element instanceof ProblemComponent) {
            problemComponent = (ProblemComponent) element;
        } else {
            throw new IllegalArgumentException("element must be a ProblemComponent Object.");
        }
    }

    /**
     * Render the <code>Problem</code> in HTML. Note headers are put in output (HTML, BODY).
     * 
     * @param language The language for all language specific information in the problem statement.
     * @return the problem component in html
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering component of the problem failed.
     */
    public String toHTML(Language language) throws Exception {
        return toHTML(language, true, false);
    }

    /**
     * Render the <code>Problem</code> in HTML.
     * 
     * @param language The language for all language specific information in the problem statement.
     * @param withHeaders Whether HTML and BODY headers should be printed.
     * @return the problem component in html
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering component of the problem failed.
     */
    public String toHTML(Language language, boolean withHeaders) throws Exception {
        return toHTML(language, withHeaders, false);
    }
    /**
     * <p>
     * get the execution time limit presentation
     * </p>
     * @return the execution time limit presentation.
     * @since 1.1
     */
    private String getExecutionTimeLimitPresent() {
        String ret = "";
        int executionTimeLimit;
        ProblemCustomSettings pcs = problemComponent.getProblemCustomSettings();
        if(problemComponent.getComponentTypeID() == ProblemConstants.MAIN_COMPONENT
                && pcs.getExecutionTimeLimit() == ProblemComponent.DEFAULT_EXECUTION_TIME_LIMIT) {
            executionTimeLimit = ProblemComponent.DEFAULT_SRM_EXECUTION_TIME_LIMIT;
        } else {
            executionTimeLimit = pcs.getExecutionTimeLimit();
        }
        double timeInSecond = (double)executionTimeLimit/1000;
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumFractionDigits(3);
        format.setMaximumFractionDigits(3);
        format.setMaximumIntegerDigits(10);
        format.setMinimumIntegerDigits(1);
        ret = format.format(timeInSecond);
        return ret;
    }
    /**
     * Render the <code>Problem</code> in HTML.
     * 
     * @param language The language for all language specific information in the problem statement.
     * @param withHeaders Whether HTML and BODY headers should be printed.
     * @param specifyPrimary Whether "Primary Component" should be specified in the output.
     * @return the problem component in html
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering component of the problem failed.
     */
    public String toHTML(Language language, boolean withHeaders, boolean specifyPrimary) throws Exception {
        if (problemComponent == null) {
            throw new IllegalStateException("The problem component is not set.");
        }

        StringBuffer buf = new StringBuffer(4096);

        if (withHeaders) {
            // renders the header
            buf.append("<html>");
            buf.append("<body");
            if (backgroundColor != null) {
                buf.append(" bgcolor=\"#");
                buf.append(ProblemRenderer.rgbColor(backgroundColor));
                buf.append("\"");
            } else {
                // if the background color is not set, use black.
                buf.append(" bgcolor='black'");
            }
            if (foregroundColor != null) {
                buf.append(" text=\"#");
                buf.append(ProblemRenderer.rgbColor(foregroundColor));
                buf.append("\"");
            } else {
                // if the text color is not set, use white.
                buf.append(" text='white'");
            }
            buf.append(">");
        }

        buf.append("<table>");

        /* Intro */
        buf.append("<tr>");
        buf.append("<td colspan=\"2\">");
        if (specifyPrimary) {
            appendTag(buf, SECTION_HEADER, "Problem Statement for Primary Component");
        } else {
            appendTag(buf, SECTION_HEADER, "Problem Statement");
        }
        buf.append("</td>");
        buf.append("</tr>");
        if (problemComponent.getIntro() != null) {
            buf.append("<tr>");
            buf.append("<td>");
            buf.append(LEFT_MARGIN);
            buf.append("</td>");
            buf.append("<td>");
            buf.append(super.getRenderer(problemComponent.getIntro()).toHTML(language));
            buf.append("</td>");
            buf.append("</tr>");
        }

        /* Signature */
        buf.append("<tr>");
        buf.append("<td colspan=\"2\">");
        appendTag(buf, SECTION_HEADER, "Definition");
        buf.append("</td>");
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append("<td>");
        buf.append(LEFT_MARGIN);
        buf.append("</td>");
        buf.append("<td>");
        buf.append("<table>");
        buf.append("<tr>");
        buf.append("<td>Class:</td>");
        buf.append("<td>");
        buf.append(problemComponent.getClassName());
        buf.append("</td>");
        buf.append("</tr>");

        String[] methodNames = problemComponent.getAllMethodNames();
        DataType[][] allParamTypes = problemComponent.getAllParamTypes();
        String[][] allParamNames = problemComponent.getAllParamNames();
        DataType[] returnTypes = problemComponent.getAllReturnTypes();

        boolean longProb = false;
        if (methodNames.length > 0) {
            int n = (methodNames.length == 1) ? 0 : 1; // hide first method in a group of multiple methods
            if (n == 1)
                longProb = true;

            for (; n < methodNames.length; n++) {
                buf.append("<tr><td>Method:</td>");
                buf.append("<td>");
                buf.append(methodNames[n]);
                buf.append("</td>");
                buf.append("</tr>");
                buf.append("<tr><td>Parameters:</td>");
                buf.append("<td>");
                DataType[] paramTypes = allParamTypes[n];
                for (int i = 0; i < paramTypes.length; i++) {
                    if (i > 0)
                        buf.append(", ");
                    buf.append(new DataTypeRenderer(paramTypes[i]).toHTML(language));
                }
                buf.append("</td>");
                buf.append("</tr>");
                buf.append("<tr><td>Returns:</td>");
                buf.append("<td>");
                buf.append(new DataTypeRenderer(returnTypes[n]).toHTML(language));

                buf.append("</td>");
                buf.append("</tr>");
                buf.append("<tr><td>Method signature:</td>");
                buf.append("<td>");
                buf.append(encodeHTML(language.getMethodSignature(methodNames[n], returnTypes[n], paramTypes,
                    allParamNames[n])));
                buf.append("</td>");
                buf.append("</tr>");
                if (language.getId() != PythonLanguage.ID && language.getId() != Python3Language.ID && n == methodNames.length - 1) {
                    if (methodNames.length > 1) {
                        buf.append("<tr><td colspan=\"2\">(be sure your methods are public)</td></tr>");
                    } else {
                        buf.append("<tr><td colspan=\"2\">(be sure your method is public)</td></tr>");
                    }
                } else {
                    buf.append("<tr><td colspan=\"2\"></td></tr>");
                }
            }
            buf.append("</table>");
            buf.append("</td>");
            buf.append("</tr>");

            // long problems can have exposed methods in them
            String[] exposedMethodNames = problemComponent.getAllExposedMethodNames();
            DataType[][] exposedAllParamTypes = problemComponent.getAllExposedParamTypes();
            String[][] exposedAllParamNames = problemComponent.getAllExposedParamNames();
            DataType[] exposedReturnTypes = problemComponent.getAllExposedReturnTypes();
            if (exposedMethodNames.length > 0) {
                buf.append("<tr>");
                buf.append("<td colspan=\"2\">");
                appendTag(buf, SECTION_HEADER, "Available Libraries");
                buf.append("</td>");
                buf.append("</tr>");
                buf.append("<tr>");
                buf.append("<td>");
                buf.append(LEFT_MARGIN);
                buf.append("</td>");
                buf.append("<td>");
                buf.append("<table>");
                buf.append("<tr>");
                buf.append("<td>Class:</td>");
                buf.append("<td>");
                buf.append(problemComponent.getExposedClassName());
                buf.append("</td>");
                buf.append("</tr>");

                n = 0;
                for (; n < exposedMethodNames.length; n++) {
                    buf.append("<tr><td>Method:</td>");
                    buf.append("<td>");
                    buf.append(exposedMethodNames[n]);
                    buf.append("</td>");
                    buf.append("</tr>");
                    buf.append("<tr><td>Parameters:</td>");
                    buf.append("<td>");
                    DataType[] paramTypes = exposedAllParamTypes[n];
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (i > 0)
                            buf.append(", ");
                        buf.append(new DataTypeRenderer(paramTypes[i]).toHTML(language));
                    }
                    buf.append("</td>");
                    buf.append("</tr>");
                    buf.append("<tr><td>Returns:</td>");
                    buf.append("<td>");
                    buf.append(new DataTypeRenderer(exposedReturnTypes[n]).toHTML(language));

                    buf.append("</td>");
                    buf.append("</tr>");
                    buf.append("<tr><td>Sample Call:</td>");
                    buf.append("<td>");
                    buf.append(encodeHTML(language.exampleExposedCall(problemComponent.getExposedClassName(),
                        exposedMethodNames[n], exposedAllParamNames[n])));

                    buf.append("</td>");
                    buf.append("</tr>");
                }

                buf.append("</table>");
                buf.append("</td>");
                buf.append("</tr>");

            }
        } else {
            buf.append("<tr><td>Method:</td>");
            buf.append("<td>");
            buf.append(problemComponent.getMethodName());
            buf.append("</td>");
            buf.append("</tr>");
            buf.append("<tr><td>Parameters:</td>");
            buf.append("<td>");
            DataType[] paramTypes = problemComponent.getParamTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0)
                    buf.append(", ");
                buf.append(new DataTypeRenderer(paramTypes[i]).toHTML(language));
            }
            buf.append("</td>");
            buf.append("</tr>");
            buf.append("<tr><td>Returns:</td>");
            buf.append("<td>");
            buf.append(new DataTypeRenderer(problemComponent.getReturnType()).toHTML(language));

            buf.append("</td>");
            buf.append("</tr>");
            buf.append("<tr><td>Method signature:</td>");
            buf.append("<td>");
            buf.append(encodeHTML(language.getMethodSignature(problemComponent.getMethodName(), problemComponent
                .getReturnType(), problemComponent.getParamTypes(), problemComponent.getParamNames())));
            buf.append("</td>");
            buf.append("</tr>");
            if (language.getId() != PythonLanguage.ID && language.getId() != Python3Language.ID) {
                buf.append("<tr><td colspan=\"2\">(be sure your method is public)</td></tr>");
            }
            buf.append("</table>");
            buf.append("</td>");
            buf.append("</tr>");
        }
        /* set the time limit and mem limit */
        buf.append("<tr>");
        buf.append("<td colspan=\"2\">");
        appendTag(buf, SECTION_HEADER, "Limits");
        buf.append("</td>");
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append("<td>");
        buf.append(LEFT_MARGIN);
        buf.append("</td>");
        buf.append("<td>");
        buf.append("<table>");
        buf.append("<tr>");
        buf.append("<td>Time limit (s):</td>");
        buf.append("<td>");
        buf.append(getExecutionTimeLimitPresent());
        buf.append("</td>");
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append("<td>");
        buf.append("Memory limit (MB):");
        buf.append("</td>");
        buf.append("<td>");
        buf.append(problemComponent.getProblemCustomSettings().getMemLimit());
        buf.append("</td>");
        buf.append("</tr>");
        if (problemComponent.getProblemCustomSettings().getStackLimit() > 0) {
            buf.append("<tr>");
            buf.append("<td>");
            buf.append("Stack limit (MB):");
            buf.append("</td>");
            buf.append("<td>");
            buf.append(problemComponent.getProblemCustomSettings().getStackLimit());
            buf.append("</td>");
            buf.append("</tr>");
        }
        buf.append("</table>");
        buf.append("</td>");
        buf.append("</tr>");
        
        /* Spec */
        if (problemComponent.getSpec() != null) {
            String specContent = super.getRenderer(problemComponent.getSpec()).toHTML(language);
            if(specContent != null && specContent.trim().length() > 0) {
                buf.append("<tr>");
                buf.append("<td>");
                buf.append(LEFT_MARGIN);
                buf.append("</td>");
                buf.append("</tr>");
                buf.append("<tr>");
                buf.append("<td>");
                buf.append(specContent);
                buf.append("</td>");
                buf.append("</tr>");
            }
        }

        /* Notes */
        Element[] notes = problemComponent.getNotes();
        if (notes != null && notes.length > 0) {
            buf.append("<tr>");
            buf.append("<td colspan=\"2\">");
            appendTag(buf, SECTION_HEADER, "Notes");
            buf.append("</td>");
            buf.append("</tr>");
            for (int i = 0; i < notes.length; i++) {
                buf.append("<tr>");
                buf.append("<td align=\"center\" valign=\"top\">");
                buf.append("-");
                buf.append("</td>");
                buf.append("<td>");
                buf.append(super.getRenderer(notes[i]).toHTML(language));
                buf.append("</td>");
                buf.append("</tr>");
            }
        }

        /* Constraints */
        Element[] constraints = problemComponent.getConstraints();
        if (constraints != null && constraints.length > 0) {
            buf.append("<tr>");
            buf.append("<td colspan=\"2\">");
            appendTag(buf, SECTION_HEADER, "Constraints");
            buf.append("</td>");
            buf.append("</tr>");
            for (int i = 0; i < constraints.length; i++) {
                buf.append("<tr>");
                buf.append("<td align=\"center\" valign=\"top\">");
                buf.append("-");
                buf.append("</td>");
                buf.append("<td>");
                buf.append(super.getRenderer(constraints[i]).toHTML(language));
                buf.append("</td>");
                buf.append("</tr>");
            }
        }

        /* Examples */
        TestCase[] testCases = problemComponent.getTestCases();
        if (testCases != null && testCases.length > 0) {
            boolean hasExamples = false;
            for (int i = 0; i < testCases.length && !hasExamples; i++) {
                hasExamples = testCases[i].isExample();
            }
            if (hasExamples) {
                buf.append("<tr>");
                buf.append("<td colspan=\"2\">");
                appendTag(buf, SECTION_HEADER, "Examples");
                buf.append("</td>");
                buf.append("</tr>");
                int count = 0;
                for (int i = 0; i < testCases.length; i++) {
                    if (testCases[i].isExample()) {
                        buf.append("<tr>");
                        buf.append("<td align=\"center\" nowrap=\"true\">");
                        buf.append(count + ")");
                        buf.append("</td>");
                        buf.append("<td>");
                        buf.append("</td>");
                        buf.append("</tr>");
                        buf.append("<tr>");
                        buf.append("<td>");
                        buf.append(LEFT_MARGIN);
                        buf.append("</td>");
                        buf.append("<td>");
                        if (longProb) {
                            buf.append(new LongTestCaseRenderer(testCases[i]).toHTML(language));
                        } else {
                            buf.append(new TestCaseRenderer(testCases[i]).toHTML(language));
                        }
                        buf.append("</td>");
                        buf.append("</tr>");
                        count++;
                    }
                }
            }
        }
        buf.append("</table>");

        if (withHeaders) {
            buf.append("<p>");
            buf.append(ProblemRenderer.LEGAL);
            buf.append("</p>");

            buf.append("</body>");
            buf.append("</html>");
        }

        return buf.toString();
    }

    /**
     * Adds an html tag and some context to a <code>StringBuffer</code>. ex: &lt;tag_name&gt;content&lt;/tag_name&gt;
     * 
     * @param buf the <code>StringBuffer</code>
     * @param tag the tag to add
     * @param content the content to add
     */
    private static void appendTag(StringBuffer buf, String tag, String content) {
        buf.append('<');
        buf.append(tag);
        buf.append('>');
        buf.append(content);
        buf.append("</");
        buf.append(tag);
        buf.append('>');
    }

    /**
     * Render the <code>Problem</code> in plain text.
     * 
     * @param language the language for all language specific information in the problem statement.
     * @return the problem component in plain text
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering component of the problem failed.
     * @deprecated
     */
    public String toPlainText(Language language) throws Exception {
        if (problemComponent == null) {
            throw new IllegalStateException("The problem component is not set.");
        }

        StringBuffer text = new StringBuffer(4000);

        /* Intro */
        text.append("PROBLEM STATEMENT\n");
        if (problemComponent.getIntro() != null)
            text.append(super.getRenderer(problemComponent.getIntro()).toPlainText(language));

        /* Signature */
        text.append("\n\nDEFINITION");
        text.append("\nClass:");
        text.append(problemComponent.getClassName());
        text.append("\nMethod:");
        text.append(problemComponent.getMethodName());
        text.append("\nParameters:");
        DataType[] paramTypes = problemComponent.getParamTypes();
        for (int j = 0; j < paramTypes.length; j++) {
            if (j > 0)
                text.append(", ");
            text.append(new DataTypeRenderer(paramTypes[j]).toPlainText(language));
        }
        text.append("\nReturns:");
        text.append(new DataTypeRenderer(problemComponent.getReturnType()).toPlainText(language));

        text.append("\nMethod signature:");
        text.append(language.getMethodSignature(problemComponent.getMethodName(), problemComponent.getReturnType(),
            problemComponent.getParamTypes(), problemComponent.getParamNames()));
        text.append("\n");

        /* Spec */
        if (problemComponent.getSpec() != null)
            text.append(super.getRenderer(problemComponent.getSpec()).toPlainText(language));

        /* Notes */
        Element[] notes = problemComponent.getNotes();
        if (notes != null && notes.length > 0) {
            text.append("\n\nNOTES\n");
            for (int j = 0; j < notes.length; j++) {
                text.append("-");
                text.append(super.getRenderer(notes[j]).toPlainText(language));
                text.append("\n");
            }
        }

        /* Constraints */
        Element[] constraints = problemComponent.getConstraints();
        if (constraints != null && constraints.length > 0) {
            text.append("\n\nCONSTRAINTS\n");
            for (int j = 0; j < constraints.length; j++) {
                text.append("-");
                text.append(super.getRenderer(constraints[j]).toPlainText(language));
                text.append("\n");
            }
        }

        /* Examples */

        TestCase[] testCases = problemComponent.getTestCases();
        if (testCases != null && testCases.length > 0) {
            text.append("\n\nEXAMPLES\n");
            int count = 0;
            for (int j = 0; j < testCases.length; j++)
                if (testCases[j].isExample()) {
                    text.append("\n" + count + ")\n");
                    text.append(new TestCaseRenderer(testCases[j]).toPlainText(language));
                    text.append("\n");
                    count++;
                }
        }
        return super.removeHtmlTags(text.toString());
    }

    /**
     * Set the background color to be used when rendering HTML. If the background color is <code>null</code>, black
     * is used.
     * 
     * @param backgroundColor the background color to be used.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Set the foreground (text) color to be used when rendering HTML. If the foreground color is <code>null</code>,
     * white is used.
     * 
     * @param foregroundColor the foreground color to be used.
     */
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }
}
