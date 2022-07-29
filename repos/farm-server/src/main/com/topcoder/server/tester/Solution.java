/*
/*
* Copyright (C) 2002 - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.tester;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.ExternalizableHelper;

/**
 * Test services interface.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Added {@link #resultType} field and added/updated all corresponding methods.</li>
 *     <li>Added {@link #checkAnswer} field and added/updated all corresponding methods.</li>
 * </ol>
 * </p>
 *
 * @author Michael Cervantes (emcee), gevak
 * @version 1.1
 */
public final class Solution implements Externalizable, CustomSerializable {

    private int languageID;
    private String packageName;
    private String className;
    private String methodName;
    private List paramTypes;
    private List classFiles;

    /**
     * Result type.
     *
     * @since 1.1
     */
    private String resultType;

    /**
     * Check answer method presence flag.
     */
    private boolean checkAnswer;

    public Solution() {
    }

    /**
     * Creates instance.
     *
     * @param languageID Language ID.
     * @param packageName Package name.
     * @param className Class name.
     * @param methodName Solution method name.
     * @param paramTypes Parameter types.
     * @param classFiles Class files.
     * @param resultType Result type.
     * @param checkAnswer Check answer.
     */
    public Solution(int languageID, String packageName, String className, String methodName,
            List paramTypes, List classFiles, String resultType, boolean checkAnswer) {
        this.languageID = languageID;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.classFiles = classFiles;
        this.resultType = resultType;
        this.checkAnswer = checkAnswer;
    }

    /*
    public int getLanguageID() {
        return languageID;
    }
    */

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public List getParamTypes() {
        return paramTypes;
    }

    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public List getClassFiles() {
        return classFiles;
    }

    /**
     * Gets result type.
     *
     * @return Result type.
     * @since 1.1
     */
    public String getResultType() {
        return resultType;
    }

    /**
     * Gets check answer presence flag.
     *
     * @return Check answer presence flag.
     */
    public boolean isCheckAnswer() {
        return checkAnswer;
    }

    /**
     * Provides textual representation of this object.
     *
     * @return Textual representation.
     */
    public String toString() {
        return "languageID=" + languageID +
                ",packageName=" + packageName +
                ",className=" + className +
                ",methodName=" + methodName +
                ",paramTypes=" + paramTypes +
                ",classFiles=" + classFiles +
                ",resultType=" + resultType +
                ",checkAnswer=" + checkAnswer;
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
        this.languageID = reader.readInt();
        this.packageName = reader.readString();
        this.className = reader.readString();
        this.methodName = reader.readString();
        this.paramTypes = reader.readArrayList();
        this.classFiles = reader.readArrayList();
        this.resultType = reader.readString();
        this.checkAnswer = reader.readBoolean();
    }

    /**
     * Performs serialization.
     *
     * @param writer Writer.
     *
     * @throws IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(this.languageID);
        writer.writeString(this.packageName);
        writer.writeString(this.className);
        writer.writeString(this.methodName);
        writer.writeList(this.paramTypes);
        writer.writeList(this.classFiles);
        writer.writeString(this.resultType);
        writer.writeBoolean(this.checkAnswer);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ExternalizableHelper.writeExternal(out, this);
    }

    public void readExternal(ObjectInput in) throws IOException {
        ExternalizableHelper.readExternal(in, this);
    }
}
