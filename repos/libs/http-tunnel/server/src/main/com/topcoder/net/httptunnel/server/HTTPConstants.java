/*
 * HTTPConstants
 *
 * Created 04/04/2007
 */
package com.topcoder.net.httptunnel.server;

import com.topcoder.net.httptunnel.common.HTTPTunnelConstants;

/**
 * Contains Constants used by Http tunnel classes.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface HTTPConstants extends HTTPTunnelConstants {

    /**
     * Separator char for Headers. eg. Name: Value
     */
    public static final char HEADER_STRING_NAME_VALUE_SEPARATOR = ':';

    /**
     * Host header name
     */
    public static final String HEADER_HOST = "Host";

    /**
     * Header name. Represents the Content lenght of the data included in the
     * Request.
     */
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";

    /**
     * Header name. Indicates the transfer enconding of the data included in the
     * request.
     */
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    /**
     * Header Value, indicates that the data of the request is sent in chunks
     */
    public static final String HEADER_TRANSFER_ENCODING_CHUNKED = "chunked";

    /**
     * Cache control header
     */
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    /**
     * Cache control header, value no-cache
     */
    public static final String HEADER_CACHE_CONTROL_NO_CACHE = "no-cache";
    /**
     * Pragma header
     */
    public static final String HEADER_PRAGMA = "Pragma";
    /**
     * Header Value, Close the connection
     */
    public static final String HEADER_CONNECTION_CLOSE = "close";
    
    /**
     * Pragma header, value no-cache
     */
    public static final String HEADER_PRAGMA_NO_CACHE = "no-cache";

    /**
     * Parameters separator char used in QueryString
     */
    public static final String QUERY_STRING_SEPARATOR_CHAR = "&";

    /**
     * Parameter/value separator char used in QueryString
     */
    public static final char QUERY_STRING_NAME_VALUE_SEPARATOR = '=';

    /**
     * Method GET.
     */
    public static final String REQ_METHOD_GET = "GET";
    /**
     * Method POST
     */
    public static final String REQ_METHOD_POST = "POST";

    /**
     * Unsupported methods
     */
    public static final String REQ_METHOD_OPTIONS = "OPTIONS";
    public static final String REQ_METHOD_HEAD = "HEAD";
    public static final String REQ_METHOD_PUT = "PUT";
    public static final String REQ_METHOD_DELETE = "DELETE";
    public static final String REQ_METHOD_TRACE = "TRACE";
    public static final String REQ_METHOD_CONNECT = "CONNECT";
    /**
     * All HTTP methods
     */
    public static final String[] REQ_METHODS = new String[] {REQ_METHOD_POST, REQ_METHOD_GET, REQ_METHOD_OPTIONS,
                                                             REQ_METHOD_HEAD, REQ_METHOD_PUT, REQ_METHOD_DELETE, REQ_METHOD_TRACE,
                                                             REQ_METHOD_CONNECT};

    public static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    public static final String CRLF = "\r\n";
    public static final byte CR = '\r';
    public static final byte LF = '\n';

    /**
     * Date header name
     */
    public static final String HEADER_DATE = "Date";

    /**
     * Server header name
     */
    public static final String HEADER_SERVER = "Server";

    /**
     * Server header value to use in responses
     */
    public static final String HEADER_SERVER_VALUE = "TcTunnel/1.0";

    /**
     * Date formats to use in responses
     */
    public static final String DATE_FORMAT_1123 = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    public static final String DATE_FORMAT_DEFAULT = DATE_FORMAT_1123;


    /**
     * Response codes
     */
    public static final int RESPONSE_OK = 200;
    public static final int RESPONSE_METHOD_NOT_ALLOWED = 405;
    public static final int RESPONSE_SERVER_ERROR = 500;
}
