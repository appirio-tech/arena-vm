/*
 * HTTPResponse
 *
 * Created 04/03/2007
 */
package com.topcoder.net.httptunnel.server;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTPResponse represents an HTTP response that
 * must be written to the connection.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPResponse implements HTTPContainer {
    private Map headers = new LinkedHashMap();
    private int responseCode;
    private String responseMessage;
    private Date date;
    private Object content;
    private boolean openingOutputChannel;

    public HTTPResponse(int responseCode, Date date)    {
        this(responseCode, date, null);
    }

    private HTTPResponse(int responseCode, Date date, Object content) {
        this.responseCode = responseCode;
        this.responseMessage = (responseCode == HTTPConstants.RESPONSE_OK) ? "OK" : "Error";
        this.date = date;
        this.content = content;
    }

    public Date getDate() {
        return date;
    }
    public Map getHeaders() {
        return headers;
    }
    public int getResponseCode() {
        return responseCode;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String headerName) {
        return (String) headers.get(headerName);
    }

    public Object getContent() {
        return content;
    }

    public String getMessage() {
        return responseMessage;
    }

    public String toString() {
        return "Response:["+responseCode+",\n  "+responseMessage+",\n  headers="+headers+",\n  date="+date+"]";
    }

    boolean isOpeningOutputChannel() {
        return openingOutputChannel;
    }

    void setOpeningOutputChannel(boolean openOutputChannel) {
        this.openingOutputChannel = openOutputChannel;
    }
}
