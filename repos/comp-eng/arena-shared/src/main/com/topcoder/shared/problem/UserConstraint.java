package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * A user constraint is where the problem writer writes arbitrary text to specify a constraint. This class is basically
 * just a container for some <code>Element</code>.
 * 
 * @see Element
 * @author Logan Hanks
 * @version $Id: UserConstraint.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class UserConstraint extends Constraint {
    /** Represents the element which contains the actual content of the user-defined constraint. */
    private Element elem;

    /**
     * Creates a new instance of <code>UserConstraint</code>. It is required by custom serialization.
     */
    public UserConstraint() {
    }

    /**
     * Creates a new instance of <code>UserConstraint</code>. The user-defined constraint text is given. The text is
     * HTML content with proper escapes.
     * 
     * @param text the user-defined constraint HTML text.
     */
    public UserConstraint(String text) {
        super("");
        this.elem = new StructuredTextElement("user-constraint", text);
    }

    /**
     * Creates a new instance of <code>UserConstraint</code>. The user-defined constraint element is
     * given.
     * 
     * @param elem the element containing the actual user-defined constraint content.
     */
    public UserConstraint(Element elem) {
        super("");
        this.elem = elem;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(elem);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        elem = (Element) reader.readObject();
    }

    public String toXML() {
        return elem.toXML();
    }

    /**
     * Gets the content element of the user-defined constraint.
     * 
     * @return the content element of the user-defined constraint.
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public Element getUserConstraint() {
        return elem;
    }
    
    public void setUserConstraint(Element e) {
    	elem = e;
    }

    /**
     * Gets the text of the user-defined constraint content element.
     * 
     * @return the text of the user-defined constraint content element.
     */
    @JsonIgnore
    public String getText() {
        return elem.toString();
    }
}
