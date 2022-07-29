package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;

import java.awt.Color;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.Problem</code>
 * 
 * @author Greg Paul
 * @version $Id: ProblemRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class ProblemRenderer extends BaseRenderer {
    /** Represents the problem to be rendered. */
    private Problem problem;

    /** Represents the background color of the rendered HTML context. */
    private Color backgroundColor = null;

    /** Represents the foreground color of the rendered HTML context. */
    private Color foregroundColor = null;

    /** Represents the legal statement appended to each of the problem statement. */
    public static final String LEGAL = "This problem statement is the exclusive and proprietary property of TopCoder, Inc.  Any unauthorized use or reproduction of this information without the prior written consent of TopCoder, Inc. is strictly prohibited.  (c)2003, TopCoder, Inc.  All rights reserved.  ";

    /**
     * Creates a new instance of <code>ProblemRenderer</code>. The problem to be rendered is given.
     * 
     * @param problem the problem to be rendered.
     */
    public ProblemRenderer(Problem problem) {
        this.problem = problem;
    }

    /**
     * Sets the element to be rendered. The given element must be a problem.
     * 
     * @param element the problem to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a problem.
     */
    public void setElement(Element element) throws Exception {
        if (element instanceof Problem) {
            problem = (Problem) element;
        } else {
            throw new IllegalArgumentException("element must be a Problem Object.");
        }
    }

    /**
     * Render the <code>Problem</code> in HTML.
     * 
     * @param language the language for all language specific information in the problem statement.
     * @return the problem statement rendered in HTML.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering component of the problem failed.
     */
    public String toHTML(Language language) throws Exception {
        if (problem == null) {
            throw new IllegalStateException("The problem is not set.");
        }

        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<body");
        if (backgroundColor != null) {
            html.append(" bgcolor=\"#");
            html.append(rgbColor(backgroundColor));
            html.append("\"");
        } else {
            html.append(" bgcolor='black'");
        }
        if (foregroundColor != null) {
            html.append(" text=\"#");
            html.append(rgbColor(foregroundColor));
            html.append("\"");
        } else {
            html.append(" text='white'");
        }
        html.append(">");

        // Problem intro
        if (problem.getProblemText() != null && !problem.getProblemText().equals("")) {
            html.append(problem.getProblemText());
            html.append("<hr>");
        }

        int primID = -1;
        if (problem.getPrimaryComponent() != null) {
            html.append(new ProblemComponentRenderer(problem.getPrimaryComponent()).toHTML(language, false, problem
                .getProblemTypeID() == ProblemConstants.TEAM_PROBLEM));
            primID = problem.getPrimaryComponent().getComponentId();
            html.append("<hr>");
        }
        ProblemComponent components[] = problem.getProblemComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].getComponentId() != primID) {
                html.append(new ProblemComponentRenderer(components[i]).toHTML(language, false, false));
                html.append("<hr>");
            }
        }

        html.append("<p>");
        html.append(LEGAL);
        html.append("</p>");
        html.append("</body></html>");

        return html.toString();
    }

    /**
     * Render the <code>Problem</code> in plain text.
     * 
     * @param language the language for all language specific information in the problem statement.
     * @return the problem statement rendered in plain text.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering component of the problem failed.
     * @deprecated
     */
    public String toPlainText(Language language) throws Exception {
        if (problem == null) {
            throw new IllegalStateException("The problem is not set.");
        }

        StringBuffer text = new StringBuffer(1000);

        if (!problem.getProblemText().equals("")) {
            text.append(problem.getProblemText());
            text.append("\n\n\n");
        }

        int primID = -1;
        if (problem.getPrimaryComponent() != null) {
            text.append(new ProblemComponentRenderer(problem.getPrimaryComponent()).toPlainText(language));
            primID = problem.getPrimaryComponent().getComponentId();
        }
        ProblemComponent components[] = problem.getProblemComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].getComponentId() != primID) {
                text.append(new ProblemComponentRenderer(components[i]).toPlainText(language));
            }
        }

        text.append(LEGAL);
        return text.toString();
    }

    /**
     * Creates a hex string representing a <code>Color</code>
     * 
     * @param c the color we want to convert to a hex string
     * @return the color in hex
     */
    static String rgbColor(Color c) {
        String red = lpad(Integer.toString(c.getRed(), 16), '0', 2);
        String green = lpad(Integer.toString(c.getGreen(), 16), '0', 2);
        String blue = lpad(Integer.toString(c.getBlue(), 16), '0', 2);
        return red + green + blue;
    }

    /**
     * Pad a String on the left.
     * 
     * @param s the String to pad
     * @param c the character to pad the String with
     * @param len the intended length of the return String.
     * @return the String after having been padded
     */
    private static String lpad(String s, char c, int len) {
        StringBuffer buf = new StringBuffer(len);
        for (int i = 0; i < len - s.length(); i++) {
            buf.append(c);
        }
        buf.append(s);
        return buf.toString();
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
