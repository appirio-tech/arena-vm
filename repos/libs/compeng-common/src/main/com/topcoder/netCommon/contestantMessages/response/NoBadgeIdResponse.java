package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * Defines a response to notify the client that the current user does not have a badge ID for 'Weakest Link' round.<br>
 * Use: This response may be sent as part of the responses of a <code>LoginRequest</code>. When there is an active
 * 'Weakest Link' round, the participants are required to have a badge ID. If the current user does not have one, the
 * login attempt is refused. The current user has to reattempt by specifying the badge ID.<br>
 * Note: The encryption of the password is done by the symmetric encryption key negotiated by server and client.
 * 
 * @author Qi Liu
 * @version $Id: NoBadgeIdResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public final class NoBadgeIdResponse extends BaseResponse {
    /** Represents the handle of the user. */
    private String handle;

    /** Represents the encrypted password of the user. */
    private SealedSerializable password;

    /**
     * Creates a new instance of <code>NoBadgeIdResponse</code>. It is required by custom serialization.
     */
    public NoBadgeIdResponse() {
    }

    /**
     * Creates a new instance of <code>NoBadgeIdResponse</code>.
     * 
     * @param handle the handle of the user.
     * @param password the encrypted password of the user.
     */
    public NoBadgeIdResponse(String handle, SealedSerializable password) {
        this.handle = handle;
        this.password = password;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(handle);
        writer.writeObject(password);
    }

    public void customReadObject(CSReader reader) throws IOException {
        handle = reader.readString();
        password = (SealedSerializable) reader.readObject();
    }

    /**
     * Gets the handle of the user.
     * 
     * @return the handle of the user.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets the encrypted password of the user.
     * 
     * @return the encrypted password of the user.
     */
    public SealedSerializable getPassword() {
        return password;
    }

}
