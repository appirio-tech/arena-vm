package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.UserConstraint;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.UserConstraint</code>
 * @author Greg Paul
 * @version $Id: UserConstraintRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class UserConstraintRenderer extends BaseRenderer {
    /** Represents the user-defined constraint to be rendered. */
    private UserConstraint userConstraint;

    /**
     * Creates a new instance of <code>UserConstraintRenderer</code>. The user-defined constraint to be rendered is set to be
     * <code>null</code>.
     */
    public UserConstraintRenderer() {
        this.userConstraint = null;
    }

    /**
     * Creates a new instance of <code>UserConstraintRenderer</code>. The user-defined constraint to be rendered is given.
     * 
     * @param userConstraint the user-defined constraint to be rendered.
     */
    public UserConstraintRenderer(UserConstraint userConstraint) {
        this.userConstraint = userConstraint;
    }

    /**
     * Sets the element to be rendered. The given element must be a user-defined constraint.
     * 
     * @param element the user-defined constraint to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a user-defined constraint.
     */
    public void setElement(Element element) {
        if (element instanceof UserConstraint) {
            userConstraint = (UserConstraint) element;
        } else {
            throw new IllegalArgumentException("element must be a UserConstraint Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the user-defined constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering the user-defined constraint failed.
     */
    public String toHTML(Language language) throws Exception {
        if (userConstraint == null) {
            throw new IllegalStateException("The user-defined constraint is not set.");
        }

        return getRenderer(userConstraint.getUserConstraint()).toHTML(language);
    }

    /**
     * Renders the element into plain text.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the user-defined constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering the user-defined constraint failed.
     * @deprecated
     */
    public String toPlainText(Language language) throws Exception {
        if (userConstraint == null) {
            throw new IllegalStateException("The user-defined constraint is not set.");
        }

        return getRenderer(userConstraint.getUserConstraint()).toPlainText(language);
    }
}
