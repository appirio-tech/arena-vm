package com.topcoder.shared.netCommon.screening.request;

import com.topcoder.shared.netCommon.*;
import com.topcoder.shared.netCommon.screening.ScreeningConstants;

import java.io.*;

public class ScreeningTunnelIPRequest extends ScreeningBaseRequest {

    protected String IP;

    public ScreeningTunnelIPRequest() {
        sync = false;
    }

    public ScreeningTunnelIPRequest(String IP) {
        sync = false;
        this.IP = IP;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        IP = reader.readString();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(IP);
    }

    public int getRequestType() {
        return ScreeningConstants.TUNNEL_IP_REQUEST;
    }

    public String getIP() {
        return IP;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.request.ScreeningTunnelIPRequest) [");
        ret.append("IP = ");
        if (IP == null) {
            ret.append("null");
        } else {
            ret.append(IP.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
