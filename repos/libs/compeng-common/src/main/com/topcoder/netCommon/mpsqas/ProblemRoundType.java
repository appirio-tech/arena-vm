/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.mpsqas;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * <p>
 * This class is a DTO for problem round type.
 * </p>
 * 
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is mutable and not thread-safe. Can be used concurrently only for reading (calling getters).
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class ProblemRoundType implements CustomSerializable, Cloneable, Serializable {
    /**
     * Serial version UID constant.
     */
    private static final long serialVersionUID = -1251382572105597625L;

    /**
     * <p>
     * The id.
     * Can be any value. Fully mutable, has getter and setter.
     * </p>
     */

    private int id = -1;
    /**
     * <p>
     * The description.
     * Can be any value. Fully mutable, has getter and setter.
     * </p>
     */
    private String description = "";

    /**
     * <p>
     * The problem type. 1 for individual problem, and 2 for long problem.
     * Can be any value. Fully mutable, has getter and setter.
     * </p>
     */
    private int problemType = -1;

    /**
     * <p>
     * Creates instance.
     * </p>
     */
    public ProblemRoundType() {
    }

    /**
     * Performs de-serialization.
     *
     * @param reader Reader.
     *
     * @throws IOException If any I/O error occurs.
     * @throws ObjectStreamException If any stream error occurs.
     **/
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        id = reader.readInt();
        description = reader.readString();
        problemType = reader.readInt();
    }

    /**
     * Performs serialization.
     *
     * @param writer Writer.
     *
     * @throws IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(description);
        writer.writeInt(problemType);
    }

    /**
     * <p>
     * Gets id. 
     * </p>
     * 
     * @return Id.
     */
     public int getId() {
         return id;
     }

    /**
     * <p>
     * Sets id. 
     * </p>
     * 
     * @param id Id.
     */
     public void setId(int id) {
         this.id = id;
     }

    /**
     * <p>
     * Gets description. 
     * </p>
     * 
     * @return Description.
     */
     public String getDescription() {
         return description;
     }

    /**
     * <p>
     * Sets description. 
     * </p>
     * 
     * @param description Description.
     */
     public void setDescription(String description) {
         this.description = description;
     }

    /**
     * <p>
     * Gets problem type. 
     * </p>
     * 
     * @return Problem type.
     */
     public int getProblemType() {
         return problemType;
     }

    /**
     * <p>
     * Sets problem type. 
     * </p>
     * 
     * @param problemType Problem type.
     */
     public void setProblemType(int problemType) {
         this.problemType = problemType;
     }
}
