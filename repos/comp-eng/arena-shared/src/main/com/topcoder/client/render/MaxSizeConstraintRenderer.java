package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.MaxSizeConstraint;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.MaxSizeConstraint</code>
 * 
 * @author Greg Paul
 * @version $Id: MaxSizeConstraintRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class MaxSizeConstraintRenderer extends BaseRenderer {
    /** Represents the maximum size constraint to be rendered. */
    private MaxSizeConstraint maxSizeConstraint;

    /**
     * Creates a new instance of <code>MaxSizeConstraintRenderer</code>. The constraint to be rendered is set to be
     * <code>null</code>.
     */
    public MaxSizeConstraintRenderer() {
        this.maxSizeConstraint = null;
    }

    /**
     * Creates a new instance of <code>MaxSizeConstraintRenderer</code>. The constraint to be rendered is given.
     * 
     * @param maxSizeConstraint the maximum size constraint to be rendered.
     */
    public MaxSizeConstraintRenderer(MaxSizeConstraint maxSizeConstraint) {
        this.maxSizeConstraint = maxSizeConstraint;
    }

    /**
     * Sets the element to be rendered. The given element must be a maximum size constraint.
     * 
     * @param element the maximum size constraint to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a maximum size constraint.
     */
    public void setElement(Element element) {
        if (element instanceof MaxSizeConstraint) {
            maxSizeConstraint = (MaxSizeConstraint) element;
        } else {
            throw new IllegalArgumentException("element must be a MaxSizeConstraint Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the maximum size constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     */
    public String toHTML(Language language) {
        // There is no character need to be escaped.
        return toPlainText(language);
    }

    /**
     * Renders the element into plain text.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the maximum size constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        if (maxSizeConstraint == null) {
            throw new IllegalStateException("The maximum size constraint is not set.");
        }

        StringBuffer buf = new StringBuffer(256);
        for (int i = 0; i < maxSizeConstraint.getDimension(); i++) {
            if (i == 0) {
                buf.append("Elements of ");
            } else {
                buf.append("elements of ");
            }
        }
        buf.append(maxSizeConstraint.getParamName());
        buf.append(" must have a maximum length of ");
        buf.append(maxSizeConstraint.getSize());
        return buf.toString();
    }
}
