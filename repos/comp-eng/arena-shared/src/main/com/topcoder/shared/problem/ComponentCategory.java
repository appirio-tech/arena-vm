package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a class representing a component category. The component category contains a name, an unique ID and a flag
 * indicating it has been checked or not.
 * 
 * @author Qi Liu
 * @version $Id: ComponentCategory.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class ComponentCategory implements Serializable, Cloneable, CustomSerializable {
    /** Represents the name of the category. */
    private String name;

    /** Represents a flag indicating if the category has been checked. */
    private boolean checked;

    /** Represents the unique ID of the category. */
    private int id;

    /**
     * Creates a new instance of <code>ComponentCategory</code>. It is required by custom serialization.
     */
    public ComponentCategory() {
    }

    /**
     * Creates a new instance of <code>ComponentCategory</code>. The name, ID and checked flag are given.
     * 
     * @param name the name of the category.
     * @param checked the checked flag of the category.
     * @param id the unique ID of the category.
     */
    public ComponentCategory(String name, boolean checked, int id) {
        this.name = name;
        this.checked = checked;
        this.id = id;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(name);
        writer.writeBoolean(checked);
        writer.writeInt(id);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        name = reader.readString();
        checked = reader.readBoolean();
        id = reader.readInt();
    }

    /**
     * Gets the name of the category.
     * 
     * @return the name of the category.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the checked flag of the category.
     * 
     * @return the checked flag of the category.
     */
    public boolean getChecked() {
        return checked;
    }

    /**
     * Gets the unique ID of the category.
     * 
     * @return the unique ID of the category.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the name of the category.
     * 
     * @param name the name of the category.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the checked flag of the category.
     * 
     * @param checked the checked flag of the category.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * Sets the unique ID of the category.
     * 
     * @param id the unique ID of the category.
     */
    public void setId(int id) {
        this.id = id;
    }
}
