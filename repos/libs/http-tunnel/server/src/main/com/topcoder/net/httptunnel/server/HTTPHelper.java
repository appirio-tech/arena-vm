/*
 * HTTPHelper
 *
 * Created Apr 6, 2007
 */
package com.topcoder.net.httptunnel.server;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPHelper {
    public static boolean isChunked(HTTPRequest obj) {
        return HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED.equals(obj.getHeader(HTTPConstants.HEADER_TRANSFER_ENCODING));
    }

    public static boolean isChunked(HTTPResponse obj) {
        return HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED.equals(obj.getHeader(HTTPConstants.HEADER_TRANSFER_ENCODING));
    }

    public static void setChunked(HTTPRequest obj) {
        obj.setHeader(HTTPConstants.HEADER_TRANSFER_ENCODING, HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED);
    }

    public static void setChunked(HTTPResponse obj) {
        obj.setHeader(HTTPConstants.HEADER_TRANSFER_ENCODING, HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED);
    }
}
