package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.Value;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.Value</code>
 * 
 * @author Greg Paul
 * @version $Id: ValueRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class ValueRenderer extends BaseRenderer {
    /** Represents the value to be rendered. */
    private Value value;

    /**
     * Creates a new instance of <code>ValueRenderer</code>. The value to be rendered is set to be <code>null</code>.
     */
    public ValueRenderer() {
        this.value = null;
    }

    /**
     * Creates a new instance of <code>ValueRenderer</code>. The value to be rendered is given.
     * 
     * @param value the value to be rendered.
     */
    public ValueRenderer(Value value) {
        this.value = value;
    }

    /**
     * Sets the element to be rendered. The given element must be a value.
     * 
     * @param element the value to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a value.
     */
    public void setElement(Element element) throws Exception {
        if (element instanceof Value) {
            value = (Value) element;
        } else {
            throw new IllegalArgumentException("element must be a Value Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the value.
     * @throws IllegalStateException if the element to be rendered is not set.
     */
    public String toHTML(Language language) {
        return BaseRenderer.encodeHTML(toPlainText(language));
    }

    /**
     * Renders the element into plain text.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the value.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        if (value == null) {
            throw new IllegalStateException("The value is not set.");
        }

        return value.getValue();
    }
}
