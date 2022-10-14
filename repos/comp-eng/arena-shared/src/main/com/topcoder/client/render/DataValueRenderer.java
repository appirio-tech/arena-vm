package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.DataValue;
import com.topcoder.shared.problem.Element;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.DataValue</code>
 * 
 * @author Greg Paul
 * @version $Id: DataValueRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class DataValueRenderer extends BaseRenderer {
    /** Represents the data value to be rendered. */
    private DataValue dataValue;

    /**
     * Creates a new instance of <code>DataValueRenderer</code>. The data value to be rendered is set to
     * <code>null</code>.
     */
    public DataValueRenderer() {
        this.dataValue = null;
    }

    /**
     * Creates a new instance of <code>DataValueRenderer</code>. The data value to be rendered is given.
     * 
     * @param dataValue the data value to be rendered.
     */
    public DataValueRenderer(DataValue dataValue) {
        this.dataValue = dataValue;
    }

    /**
     * Sets the element to be rendered. The given element must be a data value.
     * 
     * @param element the data value to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a data value.
     */
    public void setElement(Element element) {
        if (element instanceof DataValue) {
            dataValue = (DataValue) element;
        } else {
            throw new IllegalArgumentException("element must be a DataValue Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping. The data value is encoded as a string.
     * 
     * @param language the programming language to be rendered with.
     * @return the data value encoded as a string.
     * @throws IllegalStateException if the data value is not set.
     */
    public String toHTML(Language language) {
        return BaseRenderer.encodeHTML(toPlainText(language));
    }

    /**
     * Renders the element into plain text. The data value is encoded as a string.
     * 
     * @param language the programming language to be rendered with.
     * @return the data value encoded as a string.
     * @throws IllegalStateException if the data value is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        if (dataValue == null) {
            throw new IllegalStateException("The data value is not set.");
        }

        return dataValue.encode();
    }
}
