package com.topcoder.shared.problem;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * A <code>DataType</code> represents a data type in a language-independent manner. This class has all the semantics
 * of an <code>Element</code>.
 * 
 * @see Element
 * @author Logan Hanks
 */
public class DataType extends BaseElement {
    private Map<String, String> typeMapping = new HashMap<String, String>();

    private int id = -1;

    private String description = "";

    private String baseName;

    private int dim;

    /**
     * This is the default constructor, available for serialization.
     */
    public DataType() {
    }

    /**
     * A <code>DataType</code> consists of a numeric <code>id</code> a description, and a mapping between language
     * ids and language-specific descriptors.
     * 
     * @param description For example, <code>"String[]"</code> (pulled from <code>data_type.data_type_desc</code>)
     */
    public DataType(String description) {
        this(-1, description, new HashMap<String, String>());
    }

    /**
     * A <code>DataType</code> consists of a numeric <code>id</code> a description, and a mapping between language
     * ids and language-specific descriptors.
     * 
     * @param id A unique integer identifier (pulled from <code>data_type.data_type_id</code>)
     * @param description For example, <code>"String[]"</code> (pulled from <code>data_type.data_type_desc</code>)
     */
    public DataType(int id, String description) {
        this(id, description, new HashMap<String, String>());
    }

    /**
     * A <code>DataType</code> consists of a numeric <code>id</code> a description, and a mapping between language
     * ids and language-specific descriptors.
     * 
     * @param id A unique integer identifier (pulled from <code>data_type.data_type_id</code>)
     * @param description For example, <code>"String[]"</code> (pulled from <code>data_type.data_type_desc</code>)
     * @param typeMapping A <code>HashMap</code> containing all of the language-&gt;descriptor mappings that are
     *            defined for this data type. Each mapping is from <code>Integer</code> to <code>String</code>, and
     *            should be populated with information obtained from the <code>data_type_mapping</code> table.
     */
    public DataType(int id, String description, Map<String, String> typeMapping) {
        this.id = id;
        this.description = description;
        this.typeMapping = typeMapping;
        parseDescription(); 
        SimpleDataTypeFactory.registerDataType(this);
    }

    /**
     * Sets the mapping between languages (key) and language-specific data types (value). There is no copy.
     * 
     * @param typeMapping the mapping between languages and language-specific data types.
     */
    public void setTypeMapping(Map<String, String> typeMapping) {
        this.typeMapping = typeMapping;
    }

    /**
     * Gets the mapping between languages (key) and language-specific data types (value). There is no copy.
     * 
     * @return the mapping between languages and language-specific data types.
     */
    public Map<String, String> getTypeMapping() {
        return typeMapping;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(description);
        writer.writeHashMap(new HashMap<String, String>(typeMapping));
    }

    @SuppressWarnings("unchecked")
	public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        id = reader.readInt();
        description = reader.readString();
        typeMapping = reader.readHashMap();
        parseDescription();
        SimpleDataTypeFactory.registerDataType(this);
    }

    /**
     * Parses the description of the data type to get the dimensions and base name of the data type.
     */
    private void parseDescription() {
        int x = description.indexOf('[');

        if (x == -1) {
            baseName = description;
            dim = 0;
            return;
        }
        baseName = description.substring(0, x);
        for (dim = 0; x != -1; dim++, x = description.indexOf('[', x + 1))
            ;
    }

    /**
     * Gets the description of the data type.
     * 
     * @return the description of the data type.
     */
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String d) {
    	this.description = d;
    }

    /**
     * The <code>getDescriptor</code> method generates a type descriptor for this data type that is appropriate for
     * the given language.
     * 
     * @param language The language in which to present the type descriptor.
     * @return A string reperesentation of the data type, presented in the given language. If the descriptor for the
     *         given language is undefined (in the database), this method returns <code>null</code>.
     * @see Language
     */
    public String getDescriptor(Language language) {
        String desc = typeMapping.get(Integer.toString(language.getId()));

        return desc == null ? getDescription() : desc;
    }

    /**
     * Gets the type descriptor of this data type for the language with given ID.
     * 
     * @param id the ID of the language whose type descriptior is returned.
     * @return the type descriptor of this data type for the language with given ID.
     */
    public String getDescriptor(int id) {
        String desc = typeMapping.get(Integer.toString(id));
        return desc == null ? getDescription() : desc;
    }

    /**
     * The <i>base name</i> of a data type is the type of elements it ultimately contains (regardless of
     * dimensionality). E.g., the base name of <code>String[][]</code> is <code>String</code>.
     * 
     * @return the base name of the data type.
     */
    public String getBaseName() {
        return baseName;
    }
    
    public void setBaseName(String baseName) {
    	this.baseName = baseName;
    }

    /**
     * The <i>dimensionality</i> of a data type refers to how many dimensions the type has. 1, etc. E.g., the
     * dimensionality of <code>String[][]</code> is 2.
     * 
     * @return the dimension of the array.
     */
    public int getDimension() {
        return dim;
    }
    
    public void setDimension(int dim) {
    	this.dim = dim;
    }

    /**
     * Constructs and returns a new type, similar to this type, but with its dimension reduced.
     * 
     * @return A new <code>DataType</code> with the same base type as this one, but with a dimension of one less.
     * @throws InvalidTypeException if the dimension of this type is 0
     */
    public DataType reduceDimension() throws InvalidTypeException {
        StringBuffer buf = new StringBuffer(description);
        int i = description.indexOf("[]");

        if (i != -1)
            buf.delete(i, i + 2);
        else
            throw new InvalidTypeException("Attempt to reduce dimension of type " + description);

        return SimpleDataTypeFactory.getDataType(buf.toString());
    }

    public String toXML() {
        return "<type>" + ProblemComponent.encodeHTML(description) + "</type>";
    }

    /**
     * Returns true if <code>o</code> is a DataType with the same description as <code>this</code>.
     */
    public boolean equals(Object o) {
        return (o != null) && (o instanceof DataType) && description.equals(((DataType) o).getDescription());
    }

    /**
     * Clones this data type.
     * 
     * @return the cloned data type. 
     */
    DataType cloneDataType() {
        try {
            return (DataType) clone();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets the unique ID of this data type.
     * 
     * @return the unique ID of this data type.
     */
    public int getID() {
        return id;
    }

    public String toString() {
        return toXML();
    }
}
