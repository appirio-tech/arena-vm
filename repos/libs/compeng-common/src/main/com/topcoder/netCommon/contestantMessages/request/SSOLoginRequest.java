/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.SealedSerializable;


/**
 * The request to login via cookie.
 *
 * <p>
 * Version 1.1 (TCC Web Socket Refactoring) changes:
 * <ol>
 *  <li>Added {@link #jwt} field.</li>
 * </ol>
 * </p>
 * 
 * @author gondzo, gevak
 * @version 1.1
 */
public final class SSOLoginRequest extends BaseRequest {
    /** Represents the sso of the user to be logged in. */
    private String sso;

    /**
     * The jwt cookie value.
     *
     * @since 1.1
     */
    private String jwt;

    /**
     * Creates a new instance of <code>SsoLoginRequest</code>. It is required by custom serialization.
     */
    public SSOLoginRequest() {
    }

    /**
     * Creates a new instance of this class.
     *
     * @param   sso    the sso
     * @param   jwt    the jwt
     */
    public SSOLoginRequest(String sso, String jwt) {
        this.sso = sso;
        this.jwt = jwt;
    }

    /**
     * Gets the sso of the user for authentication.
     * 
     * @return the sso of the user.
     */
    public String getSSO() {
        return sso;
    }

    /**
     * Getter for the jwt.
     *
     * @return the jwt
     *
     * @since 1.1
     */
    public String getJWT() {
        return jwt;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(sso);
        writer.writeString(jwt);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        sso = reader.readString();
        jwt = reader.readString();
    }

    /**
     * Gets the string representation of this object
     * 
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.SSOLoginRequest) [");
        ret.append("sso = ");
        if (sso == null) {
            ret.append("null");
        } else {
            ret.append(sso);
        }
        ret.append(", ");
        ret.append("jwt = ");
        if (jwt == null) {
            ret.append("null");
        } else {
            ret.append(jwt);
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    public int getRequestType() {
        return ContestConstants.LOGIN;
    }

}
