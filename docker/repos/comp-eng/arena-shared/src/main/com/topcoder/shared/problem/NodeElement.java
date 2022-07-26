package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * A <code>NodeElement</code> represents an XML element. It has a name, a (possibly empty) set of attributes, and a
 * sequence of children that consists of any number of <code>Elements</code>. This structure is necessary so that we
 * do not lose the structure of writer-submitted text, so that we can treat the <code>type</code> element properly,
 * for instance.
 * 
 * @author Qi Liu
 * @version $Id: NodeElement.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class NodeElement extends BaseElement {
    /** Represents the name of the element. */
    private String name;

    /** Represents the map of attributes of the element. */
    private HashMap attributes;

    /** Represents the list of children of the element. */
    private ArrayList children;

    /** Represents the XML fragment of the content of this element. */
    private String text;

    /**
     * Creates a new instance of <code>NodeElement</code>. It is required by custom serialization.
     */
    public NodeElement() {
    }

    /**
     * Creates a new instance of <code>NodeElement</code>. The name, attributes, children and inner text are given.
     * 
     * @param name The name of the element
     * @param attributes A mapping of attribute names to attribute values
     * @param children A sequence of elements that are children of the element
     * @param text An XML fragment corresponding to the content of this element
     */
    public NodeElement(String name, HashMap attributes, ArrayList children, String text) {
        this.name = name;
        this.attributes = attributes;
        this.children = children;
        this.text = text;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(name);
        writer.writeHashMap(attributes);
        writer.writeArrayList(children);
        writer.writeString(text);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        name = reader.readString();
        attributes = reader.readHashMap();
        children = reader.readArrayList();
        text = reader.readString();
    }

    /**
     * Gets the XML fragment of the content of this element.
     * 
     * @return the XML fragment of the content of this element.
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the attribute map of this element. There is no copy.
     * 
     * @return the attribute map of this element.
     */
    public HashMap getAttributes() {
        return attributes;
    }

    /**
     * Gets the name of the element.
     * 
     * @return the name of the element.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of children of this element. There is no copy.
     * 
     * @return the list of children of this element.
     */
    public ArrayList getChildren() {
        return children;
    }

    public String toXML() {
        StringBuffer buf = new StringBuffer(64 * children.size());

        buf.append('<');
        buf.append(name);
        for (Iterator i = attributes.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();

            buf.append(' ');
            buf.append(key);
            buf.append("=\"");
            buf.append(ProblemComponent.encodeHTML((String) attributes.get(key)));
            buf.append('"');
        }
        buf.append('>');
        for (int i = 0; i < children.size(); i++) {
            Element e = (Element) children.get(i);
            buf.append(e.toXML());
        }
        buf.append("</");
        buf.append(name);
        buf.append('>');
        return buf.toString();

    }

    public String toString() {
        StringBuffer buf = new StringBuffer(64 * children.size());
        boolean print = USER_ONLY_TAGS_LIST.contains(name);
        if (print) {
            buf.append('<');
            buf.append(name);
            for (Iterator i = attributes.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();

                buf.append(' ');
                buf.append(key);
                buf.append("=\"");
                buf.append(ProblemComponent.encodeHTML((String) attributes.get(key)));
                buf.append('"');
            }
            buf.append('>');
        }
        for (int i = 0; i < children.size(); i++) {
            Element e = (Element) children.get(i);
            buf.append(e.toString());
        }
        if (print) {
            buf.append("</");
            buf.append(name);
            buf.append('>');
        }
        return buf.toString();
    }
}
