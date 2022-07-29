/*
 * HTTPRequest
 *
 * Created 04/03/2007
 */
package com.topcoder.net.httptunnel.server;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTPRequest represents an HTTP request that
 * has been read from the connection
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPRequest implements HTTPContainer {
    public static final String NO_VALUE = "NO_VALUE_SET";

    private Map headers = new HashMap();
    private Map parameters = new HashMap();
    private String method;
    private URL url;
    private String httpVersion;
    private Date date = new Date();
    private Object content;

    public Map getParameters() {
        return parameters;
    }

    public URL getUrl() {
        return url;
    }

    public Map getHeaders() {
        return headers;
    }

    public Object getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public String getMethod() {
        return method;
    }

    public String getParameter(String parameterName) {
        return (String) parameters.get(parameterName);
    }

    public String getHeader(String headerName) {
        return (String) headers.get(headerName);
    }


    public void setParameter(String parameterName, String value) {
         parameters.put(parameterName, value);
    }

    public void setParameter(String parameterName) {
        parameters.put(parameterName, NO_VALUE);
   }

    public void setHeader(String headerName, String value) {
        headers.put(headerName, value);
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String toString() {
        return "Request:["+httpVersion+",\n  "+method+",\n  "+url.toExternalForm()+",\n  headers="+headers+",\n  params="+parameters+",\n  date="+date+"]";
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
}
