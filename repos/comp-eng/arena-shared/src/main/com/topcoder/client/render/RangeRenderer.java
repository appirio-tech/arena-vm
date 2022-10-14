package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.*;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.Range</code>
 * @author Greg Paul
 * @version $Id: RangeRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class RangeRenderer extends BaseRenderer {
    /** Represents the data type to be rendered. */
    private Range range = null;

    /**
     * Creates a new instance of <code>RangeRenderer</code>. The range restriction to be rendered is set to be
     * <code>null</code>.
     */
    public RangeRenderer() {
        this.range = null;
    }

    /**
     * Creates a new instance of <code>RangeRenderer</code>. The range restriction to be rendered is given.
     * 
     * @param range the range restriction to be rendered.
     */
    public RangeRenderer(Range range) {
        this.range = range;
    }

    /**
     * Sets the element to be rendered. The given element must be a range restriction.
     * 
     * @param element the range restriction to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a range restriction.
     */
    public void setElement(Element element) {
        if (element instanceof Range) {
            range = (Range) element;
        } else {
            throw new IllegalArgumentException("element must be a Range Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the range constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     */
    public String toHTML(Language language) {
        return BaseRenderer.encodeHTML(toPlainText(language));
    }

    /**
     * Renders the element into plain text.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the range constraint.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        if (range == null) {
            throw new IllegalStateException("The range is not set.");
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append(range.getMin().toString());
        sb.append(" - ");
        sb.append(range.getMax().toString());
        sb.append(" (inclusive)");
        return sb.toString();
    }
}
