package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.ValidValuesConstraint;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.ValidValuesConstraint</code>
 * 
 * @author Greg Paul
 * @version $Id: ValidValuesConstraintRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class ValidValuesConstraintRenderer extends BaseRenderer {
    /** Represents the enumeration constraint to be rendered. */
    private ValidValuesConstraint validValuesConstraint;

    /**
     * Creates a new instance of <code>ValidValuesConstraintRenderer</code>. The enumeration constraint to be
     * rendered is set to be <code>null</code>.
     */
    public ValidValuesConstraintRenderer() {
        this.validValuesConstraint = null;
    }

    /**
     * Creates a new instance of <code>ValidValuesConstraintRenderer</code>. The enumeration constraint to be
     * rendered is given.
     * 
     * @param validValuesConstraint the enumeration constraint to be rendered.
     */
    public ValidValuesConstraintRenderer(ValidValuesConstraint validValuesConstraint) {
        this.validValuesConstraint = validValuesConstraint;
    }

    /**
     * Sets the element to be rendered. The given element must be an enumeration constraint.
     * 
     * @param element the enumeration constraint to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not an enumeration constraint.
     */
    public void setElement(Element element) {
        if (element instanceof ValidValuesConstraint) {
            validValuesConstraint = (ValidValuesConstraint) element;
        } else {
            throw new IllegalArgumentException("element must be a ValidValuesConstraint Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the enumeration constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering the enumeration constraint failed.
     */
    public String toHTML(Language language) throws Exception {
        if (validValuesConstraint == null) {
            throw new IllegalStateException("The enumeration constraint is not set.");
        }
        
        StringBuffer sb = new StringBuffer(5 * validValuesConstraint.getValidValues().size());
        for (int i = 0; i < validValuesConstraint.getDimension(); i++) {
            if (i == 0) {
                sb.append("Elements of ");
            } else {
                sb.append("elements of ");
            }
        }

        sb.append(validValuesConstraint.getParamName());
        sb.append(" must be ");

        for (int i = 0; i < validValuesConstraint.getValidValues().size(); i++) {
            sb.append(super.getRenderer(((Element) validValuesConstraint.getValidValues().get(i))).toHTML(language));
            if (i < validValuesConstraint.getValidValues().size() - 2) {
                sb.append(", ");
            } else if (i == validValuesConstraint.getValidValues().size() - 2) {
                sb.append(", or ");
            }
        }
        return sb.toString();
    }

    /**
     * Renders the element into plain text.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the enumeration constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering the enumeration constraint failed.
     * @deprecated
     */
    public String toPlainText(Language language) throws Exception {
        if (validValuesConstraint == null) {
            throw new IllegalStateException("The enumeration constraint is not set.");
        }
        
        StringBuffer sb = new StringBuffer(5 * validValuesConstraint.getValidValues().size());
        for (int i = 0; i < validValuesConstraint.getDimension(); i++) {
            if (i == 0) {
                sb.append("Elements of ");
            } else {
                sb.append("elements of ");
            }
        }

        sb.append(validValuesConstraint.getParamName());
        sb.append(" must be ");

        for (int i = 0; i < validValuesConstraint.getValidValues().size(); i++) {
            sb.append(super.getRenderer(((Element) validValuesConstraint.getValidValues().get(i)))
                .toPlainText(language));
            if (i < validValuesConstraint.getValidValues().size() - 2) {
                sb.append(", ");
            } else if (i == validValuesConstraint.getValidValues().size() - 2) {
                sb.append(", or ");
            }
        }
        return sb.toString();

    }
}
