/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.mpsqas;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * <p>
 * This class holds custom build setting data and also declares constants for custom build setting types.
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is mutable and not thread-safe. It's expected to be populated once (from a single thread),
 * so later it can be used concurrently only for reading (calling getters).
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class CustomBuildSetting implements CustomSerializable, Cloneable, Serializable {
    /**
     * Constant for MM GCC build command setting type.
     */
    public static final int MM_GCC_BUILD_COMMAND = 1;

    /**
     * Constant for MM CPP approved path setting type.
     */
    public static final int MM_CPP_APPROVED_PATH = 2;

    /**
     * Constant for MM Python command setting type.
     */
    public static final int MM_PYTHON_COMMAND = 3;

    /**
     * Constant for MM Python approved path setting type.
     */
    public static final int MM_PYTHON_APPROVED_PATH = 4;

    /**
     * Constant for SRM GCC build command setting type.
     */
    public static final int SRM_GCC_BUILD_COMMAND = 5;

    /**
     * Constant for SRM CPP approved path setting type.
     */
    public static final int SRM_CPP_APPROVED_PATH = 6;

    /**
     * Constant for SRM Python command setting type.
     */
    public static final int SRM_PYTHON_COMMAND = 7;

    /**
     * Constant for SRM Python approved path setting type.
     */
    public static final int SRM_PYTHON_APPROVED_PATH = 8;

    /**
     * Serial version UID constant.
     */
    private static final long serialVersionUID = 1757104592875010346L;

    /**
     * <p>
     * ID.
     * </p>
     *
     * <p>
     * Fully mutable, has getter and setter. Can be any value.
     * </p>
     */
    private int id;

    /**
     * <p>
     * Type.
     * </p>
     *
     * <p>
     * Fully mutable, has getter and setter. Can be any value.
     * </p>
     */
    private int type;

    /**
     * <p>
     * Value.
     * </p>
     *
     * <p>
     * Fully mutable, has getter and setter. Can be any value.
     * </p>
     */
    private String value;

    /**
     * <p>
     * Description.
     * </p>
     *
     * <p>
     * Fully mutable, has getter and setter. Can be any value.
     * </p>
     */
    private String description;

    /**
     * Creates instance.
     */
    public CustomBuildSetting() {
    }

    /**
     * <p>
     * Gets ID. 
     * </p>
     * 
     * @return ID.
     */
     public int getId() {
         return id;
     }

    /**
     * <p>
     * Sets ID. 
     * </p>
     * 
     * @param id ID.
     */
     public void setId(int id) {
         this.id = id;
     }

    /**
     * <p>
     * Gets type. 
     * </p>
     * 
     * @return Type.
     */
     public int getType() {
         return type;
     }

    /**
     * <p>
     * Sets type. 
     * </p>
     * 
     * @param type Type.
     */
     public void setType(int type) {
         this.type = type;
     }

    /**
     * <p>
     * Gets value. 
     * </p>
     * 
     * @return Value.
     */
     public String getValue() {
         return value;
     }

    /**
     * <p>
     * Sets value. 
     * </p>
     * 
     * @param value Value.
     */
     public void setValue(String value) {
         this.value = value;
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
      * Performs de-serialization.
      *
      * @param reader Reader.
      *
      * @throws IOException If any I/O error occurs.
      * @throws ObjectStreamException If any stream error occurs.
      **/
     @SuppressWarnings("unchecked")
     public void customReadObject(CSReader reader) throws IOException,
             ObjectStreamException {
         id = reader.readInt();
         type = reader.readInt();
         value = reader.readString();
         description = reader.readString();
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
         writer.writeInt(type);
         writer.writeString(value);
         writer.writeString(description);
     }
}
