package com.topcoder.shared.netCommon.screening.response;

import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.screening.response.data.ScreeningProblemSet;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Respond to client request to login.
 */
public final class ScreeningLoginResponse extends ScreeningBaseResponse {

    // Message to explain result.
    private String msg;
    // Whether or not login was successful.
    private boolean isSuccess;
    private long userID;

    /**
     * Constructor needed for CS.
     */
    public ScreeningLoginResponse() {
        super();
        this.sync = false;
        System.out.println("sync is " + sync);
    }

    /**
     * @param success Whether or not login was successful.
     */
    public ScreeningLoginResponse(boolean success) {
        this(success, null);
        System.out.println("sync is " + sync);
    }

    /**
     * @param success Whether or not login was successful.
     * @param msg Message to explain result of login attempt.
     */
    public ScreeningLoginResponse(boolean success, String msg) {
        this();
        isSuccess = success;
        this.msg = msg;
        System.out.println("sync is " + sync);
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(isSuccess);
        writer.writeString(msg);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param reader the custom serialization reader
     * @throws IOException           exception during reading
     * @throws ObjectStreamException exception during reading
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        isSuccess = reader.readBoolean();
        msg = reader.readString();
    }

    /**
     * @return Message explaining result of login.
     */
    public String getMessage() {
        return msg;
    }

    /**
     * @return Whether or not login was successful.
     */
    public boolean isSuccess() {
        return isSuccess;
    }


    /**
     * Gets the string representation of this object
     *
     * @return the string repre8sentation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.response.ScreeningLoginResponse) [");
        ret.append("success = ");
        ret.append(isSuccess);
        return ret.toString();
    }
    public long getUserID(){
        return userID;
    }
    public void setUserID(long id){
        userID = id;
    }
}
