package com.topcoder.client.render;

import java.util.Arrays;
import java.util.Iterator;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.NodeElement;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.NodeElementRenderer</code>. A node represents an XML
 * node in the XML representation of the problem component, including the tag name, attributes, and children. When the
 * tag name is an HTML tag, the tag name and attributes are rendered in HTML rendering. Otherwise, the tag and
 * attributes are not rendered. In either case, its children will be rendered.
 * 
 * @author Greg Paul
 * @version $Id: NodeElementRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 * @see BaseRenderer.XML_ONLY_TAGS
 */
public class NodeElementRenderer extends BaseRenderer {
    /** Represents the node to be rendered. */
    private NodeElement nodeElement = null;

    /**
     * Creates a new instance of <code>NodeElementRenderer</code>. The node to be rendered is set to be
     * <code>null</code>.
     */
    public NodeElementRenderer() {
        this.nodeElement = null;
    }

    /**
     * Creates a new instance of <code>NodeElementRenderer</code>. The node to be rendered is given.
     * 
     * @param nodeElement the node to be rendered.
     */
    public NodeElementRenderer(NodeElement nodeElement) {
        this.nodeElement = nodeElement;
    }

    /**
     * Sets the element to be rendered. The given element must be a node.
     * 
     * @param element the node to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a node.
     */
    public void setElement(Element element) {
        if (element instanceof NodeElement) {
            nodeElement = (NodeElement) element;
        } else {
            throw new IllegalArgumentException("element must be a NodeElement Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping. The node, including its children if any, is rendered. When
     * the tag name of the node is not a TC-specific XML tag, it is considered as an HTML tag, and rendered to the
     * output.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the node.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering the children of the node failed.
     */
    public String toHTML(Language language) throws Exception {
        if (nodeElement == null) {
            throw new IllegalStateException("The node is not set.");
        }

        StringBuffer buf = new StringBuffer(64 * nodeElement.getChildren().size());
        boolean print = Arrays.binarySearch(BaseRenderer.XML_ONLY_TAGS, nodeElement.getName()) < 0;

        if (print) {
            buf.append('<');
            buf.append(nodeElement.getName());
            for (Iterator i = nodeElement.getAttributes().keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();

                buf.append(' ');
                buf.append(key);
                buf.append("=\"");
                buf.append(BaseRenderer.encodeHTML((String) nodeElement.getAttributes().get(key)));
                buf.append('"');
            }
            buf.append('>');
        }
        for (int i = 0; i < nodeElement.getChildren().size(); i++) {
            Element e = (Element) nodeElement.getChildren().get(i);
            buf.append(super.getRenderer(e).toHTML(language));
        }
        if (print) {
            buf.append("</");
            buf.append(nodeElement.getName());
            buf.append('>');
        }
        return buf.toString();
    }

    /**
     * Renders the element into plain text. The node, including its children if any, is rendered. All tags are omitted.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the node.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @throws Exception if rendering the children of the node failed.
     * @deprecated
     */
    public String toPlainText(Language language) throws Exception {
        if (nodeElement == null) {
            throw new IllegalStateException("The node is not set.");
        }

        StringBuffer buf = new StringBuffer(64 * nodeElement.getChildren().size());

        for (int i = 0; i < nodeElement.getChildren().size(); i++) {
            Element e = (Element) nodeElement.getChildren().get(i);
            buf.append(super.getRenderer(e).toPlainText(language));
        }

        return buf.toString();
    }
}
