package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Element;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.DataType</code>
 * 
 * @author Greg Paul
 * @version $Id: DataTypeRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class DataTypeRenderer extends BaseRenderer {
    /** Represents the data type to be rendered. */
    private DataType dataType;

    /**
     * Creates a new instance of <code>DataTypeRenderer</code>. The data type to be rendered is set to be
     * <code>null</code>.
     */
    public DataTypeRenderer() {
        this.dataType = null;
    }

    /**
     * Creates a new instance of <code>DataTypeRenderer</code>. The data type to be rendered is given.
     * 
     * @param dataType the data type to be rendered.
     */
    public DataTypeRenderer(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Sets the element to be rendered. The given element must be a data type.
     * 
     * @param element the data type to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a data type.
     */
    public void setElement(Element element) {
        if (element instanceof DataType) {
            dataType = (DataType) element;
        } else {
            throw new IllegalArgumentException("element must be a DataType Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping. The descriptor of the data type in the given programming
     * language is returned.
     * 
     * @param language the programming language to be rendered with.
     * @return the descriptor of the data type in the given programming language.
     * @throws IllegalStateException if the element to be rendered is not set.
     */
    public String toHTML(Language language) {
        return BaseRenderer.encodeHTML(toPlainText(language));
    }

    /**
     * Renders the element into plain text. The descriptor of the data type in the given programming language is
     * returned.
     * 
     * @param language the programming language to be rendered with.
     * @return the descriptor of the data type in the given programming language.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        if (dataType == null) {
            throw new IllegalStateException("The data type is not set.");
        }

        String desc = dataType.getDescriptor(language);

        if (desc == null)
            return "null";
        return desc;
    }
}
