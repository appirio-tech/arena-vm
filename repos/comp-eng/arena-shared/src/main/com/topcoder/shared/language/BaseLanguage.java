/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.language;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;

/**
 * The <code>Language</code> class is the implementation of all the semantics associated with a supported programming
 * language. This basically consists of the logic for generating language-dependent method signatures. An instance of
 * type <code>Language</code> also servers as a convenient identifier for a particular language.
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - R Language Compilation Support):
 * <ol>
 *      <li>Update {@link #getLanguage(int typeID)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Python3 Support):
 * <ol>
 *      <li>Updated {@link #getLanguage(int)} method to generate Python3 language.</li>
 * </ol>
 * </p>
 * @author Logan Hanks, savon_cn, liuliquan
 * @version 1.2
 */
abstract public class BaseLanguage implements Language, ResolvedCustomSerializable {
    /** Represents the unique ID of this programming language. */
    private int id;

    /** Represents the description of this programming language. */
    private transient String name;

    /**
     * Creates a new instance of <code>BaseLanguage</code> class. A default constructor is required by custom
     * serialization.
     */
    public BaseLanguage() {
    }

    /**
     * Creates a new instance of <code>BaseLanguage</code> class. The unique ID and the description of the programming
     * language are given.
     * 
     * @param id the unique ID of this programming language.
     * @param name the description of this programming language.
     */
    protected BaseLanguage(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the unique ID of this programming language.
     * 
     * @return the unique ID of this programming language.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the description of this programming language.
     * 
     * @return the description of this programming language.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a flag indicating if two programming languages are the same. They are the same if the IDs are the same.
     */
    public boolean equals(Object o) {
        return (o != null) && (o instanceof Language) && (((Language) o).getId() == id);
    }

    /**
     * Gets the hash code of this programming language. It is the same as the unique ID.
     */
    public int hashCode() {
        return id;
    }

    /**
     * Serializes this object to the given custom serialization writer.
     * 
     * @param writer the custom serialization writer.
     * @throws IOException if an I/O error occurs.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
    }

    /**
     * Deserializes this object from the given custom serialization reader.
     * 
     * @param reader the custom serialization reader.
     * @throws IOException if an I/O error occurs.
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        id = reader.readInt();
    }

    /**
     * Gets a singleton instance for this programming language.
     * 
     * @return a singleton instance for this programming language.
     */
    public Object readResolve() {
        return getLanguage(id);
    }

    /**
     * Gets the singleton instance of a programming language according to the given unique ID.
     * 
     * @param typeID the unique ID of the programming language.
     * @return a singleton instance of the programming language.
     * @throws IllegalArgumentException if there is no programming language with the given unique ID.
     */
    public static Language getLanguage(int typeID) {
        switch (typeID) {
        case JavaLanguage.ID:
            return JavaLanguage.JAVA_LANGUAGE;
        case CPPLanguage.ID:
            return CPPLanguage.CPP_LANGUAGE;
        case CSharpLanguage.ID:
            return CSharpLanguage.CSHARP_LANGUAGE;
        case VBLanguage.ID:
            return VBLanguage.VB_LANGUAGE;
        case PythonLanguage.ID:
            return PythonLanguage.PYTHON_LANGUAGE;
        case Python3Language.ID:
            return Python3Language.PYTHON3_LANGUAGE;
        case RLanguage.ID:
            return RLanguage.R_LANGUAGE;
        default:
            throw new IllegalArgumentException("Invalid language: " + typeID);
        }
    }
}
