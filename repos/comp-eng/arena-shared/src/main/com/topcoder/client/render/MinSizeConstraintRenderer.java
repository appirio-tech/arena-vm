package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.MinSizeConstraint;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.MinSizeConstraint</code>
 * @author Greg Paul
 * @version $Id: MinSizeConstraintRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class MinSizeConstraintRenderer extends BaseRenderer {
    /** Represents the minimum size constraint to be rendered. */
    private MinSizeConstraint minSizeConstraint;

    /**
     * Creates a new instance of <code>MinSizeConstraintRenderer</code>. The constraint to be rendered is set to be
     * <code>null</code>.
     */
    public MinSizeConstraintRenderer() {
        this.minSizeConstraint = null;
    }

    /**
     * Creates a new instance of <code>MinSizeConstraintRenderer</code>. The constraint to be rendered is given.
     * 
     * @param minSizeConstraint the minimum size constraint to be rendered.
     */
    public MinSizeConstraintRenderer(MinSizeConstraint minSizeConstraint) {
        this.minSizeConstraint = minSizeConstraint;
    }

    /**
     * Renders the element into HTML with proper escaping.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the minimum size constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     */
    public String toHTML(Language language) {
        return toPlainText(language);
    }

    /**
     * Sets the element to be rendered. The given element must be a minimum size constraint.
     * 
     * @param element the minimum size constraint to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a minimum size constraint.
     */
    public void setElement(Element element) throws Exception {
        if (element instanceof MinSizeConstraint) {
            minSizeConstraint = (MinSizeConstraint) element;
        } else {
            throw new IllegalArgumentException("element must be a MinSizeConstraint Object.");
        }
    }

    /**
     * Renders the element into plain text.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the minimum size constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        if (minSizeConstraint == null) {
            throw new IllegalStateException("The minimum size constraint is not set.");
        }

        StringBuffer buf = new StringBuffer(256);
        for (int i = 0; i < minSizeConstraint.getDimension(); i++) {
            if (i == 0) {
                buf.append("Elements of ");
            } else {
                buf.append("elements of ");
            }
        }
        buf.append(minSizeConstraint.getParamName());
        buf.append(" must have a minimum length of ");
        buf.append(minSizeConstraint.getSize());
        return buf.toString();
    }
}
