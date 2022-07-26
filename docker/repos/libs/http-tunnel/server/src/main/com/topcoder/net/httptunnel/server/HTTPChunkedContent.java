/*
 * HTTPChunkedContent
 *
 * Created 04/06/2007
 */
package com.topcoder.net.httptunnel.server;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPChunkedContent implements HTTPContainer {
    public static final HTTPChunkedContent CLOSING_CHUNK = new HTTPChunkedContent();
    private int chunkSize;
    private Object content;

    public HTTPChunkedContent(Object content) {
        this.content = content;
        this.chunkSize = 1;
    }
    
    public HTTPChunkedContent(int chunkSize, Object content) {
        this.chunkSize = chunkSize;
        this.content = content;
    }

    public HTTPChunkedContent() {
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public Object getContent() {
        return content;
    }

}
