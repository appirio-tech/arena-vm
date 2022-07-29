/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.mpsqas.communication.message;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.mpsqas.LookupValues;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;


/**
 * Represents a response to a login request.
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Added lookup values field and updated all the related methods accordingly.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is mutable and not thread-safe.
 * </p>
 *
 * @author Logan Hanks, TCSASSEMBLER
 * @version 1.1
 */
public class LoginResponse
        extends Message {

    protected boolean success;
    protected boolean admin;
    protected boolean writer;
    protected boolean tester;
    protected int id;
    protected String errorMessage;

    /**
     * <p>
     * Lookup values.
     * </p>
     *
     * <p>
     * Fully mutable, has getter and setter. Can be any value.
     * </p>
     *
     * @since 1.1
     */
    protected LookupValues lookupValues = new LookupValues();

    /**
     * Construct an empty response before deserializing by calling <tt>customReadObject</tt>.
     *
     * @see #customReadObject
     */
    public LoginResponse() {
        errorMessage = "";
    }

    /**
     * Constructs a <em>successful</em> login response.
     *
     * @param id the user's id
     * @param admin set to true if the user was authenticated as an administrator
     * @param writer whether the user has writer privileges
     * @param tester whether the user has tester privileges
     * @param lookupValues lookup values
     * @see LoginResponse(java.lang.String)
     */
    public LoginResponse(int id, boolean admin, boolean writer, boolean tester, LookupValues lookupValues) {
        this.id = id;
        this.admin = admin;
        this.writer = writer;
        this.tester = tester;
        this.lookupValues = lookupValues;
        success = true;
        errorMessage = "";
    }

    /**
     * Constructs a <em>failed</em> login response.
     * @param errorMessage message describing why the login failed
     * @see LoginResponse(int,boolean,boolean,boolean)
     */
    public LoginResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        success = admin = false;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isWriter() {
        return writer;
    }

    public boolean isTester() {
        return tester;
    }

    public int getId() {
        return id;
    }

    /**
     * Gets lookup values.
     *
     * @return Lookup values.
     * @since 1.1
     */
    public LookupValues getLookupValues() {
        return lookupValues;
    }

    /**
     * Sets lookup values.
     *
     * @param lookupValues Lookup values.
     * @since 1.1
     */
    public void setLookupValues(LookupValues lookupValues) {
        this.lookupValues = lookupValues;
    }

    /**
     * Performs serialization.
     *
     * @param writer Writer.
     *
     * @throws IOException If any I/O error occurs.
     **/
    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeBoolean(success);
        if (success) {
            writer.writeInt(id);
            writer.writeBoolean(admin);
            writer.writeBoolean(this.writer);
            writer.writeBoolean(tester);
            writer.writeObject(lookupValues);
        } else
            writer.writeString(errorMessage);
    }

    /**
     * Performs de-serialization.
     *
     * @param reader Reader.
     *
     * @throws IOException If any I/O error occurs.
     * @throws ObjectStreamException If any stream error occurs.
     **/
    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        success = reader.readBoolean();
        if (success) {
            id = reader.readInt();
            admin = reader.readBoolean();
            writer = reader.readBoolean();
            tester = reader.readBoolean();
            lookupValues = (LookupValues) reader.readObject();
        } else
            errorMessage = reader.readString();
    }

    public String toString() {
        return "LoginResponse[success=" + success + ";id=" + id + ";admin=" + admin + ";writer=" + writer + ";tester=" +
                tester + ";errorMessage=" + errorMessage + "]";
    }

    static public LoginResponse getFailedLoginResponse(String errorMessage) {
        return new LoginResponse(errorMessage);
    }

    /**
     * Creates successful login response.
     *
     * @param id the user's id
     * @param admin set to true if the user was authenticated as an administrator
     * @param writer whether the user has writer privileges
     * @param tester whether the user has tester privileges
     * @param lookupValues lookup values
     * @return Successful login response.
     */
    static public LoginResponse getSuccessfulLoginResponse(int id, boolean admin, boolean writer,
            boolean tester, LookupValues lookupValues) {
        return new LoginResponse(id, admin, writer, tester, lookupValues);
    }

}
