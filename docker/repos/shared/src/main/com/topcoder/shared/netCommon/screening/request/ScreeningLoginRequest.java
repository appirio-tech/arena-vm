package com.topcoder.shared.netCommon.screening.request;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * ScreeningLoginRequest.java         2002/12/22
 *
 * Copyright (c) 2002 TopCoder, Inc.  All rights reserved.
 *
 * @author:  Budi Kusmiantoro
 * @version: 1.00
 */
public final class ScreeningLoginRequest extends ScreeningBaseRequest {

    private String username;
    private String password;
    private long companyID;
    private int protocolVersion;

    //No-arg constructor needed by customserialization
    public ScreeningLoginRequest() {
        sync = false;
    }

    public ScreeningLoginRequest(String username, String password,
            long companyID) {
        sync = false;
        this.username = username;
        this.password = password;
        this.companyID = companyID;
        protocolVersion = ScreeningConstants.PROTOCOL_VERSION;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(username);
        writer.writeString(password);
        writer.writeLong(companyID);
        writer.writeInt(protocolVersion);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        username = reader.readString();
        password = reader.readString();
        companyID = reader.readLong();
        protocolVersion = reader.readInt();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public long getCompanyID() {
        return companyID;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public int getRequestType() {
        return ScreeningConstants.LOGIN;
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.request.ScreeningLoginRequest) [");
        ret.append("userid = ");
        ret.append((username == null) ? "null" : username.toString());
        ret.append(", ");
        ret.append("password = ");
        ret.append((password == null) ? "null" : password.toString());
        ret.append("]");
        return ret.toString();
    }
}
