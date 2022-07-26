/*
 * HTTPResponseWriter
 *
 * Created 04/04/2007
 */
package com.topcoder.net.httptunnel.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import com.topcoder.netCommon.io.ObjectWriter;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * HTTPResponseWriter is responsible for writing response messages into the byte buffer.<p>
 *
 * The current HTTP state is not handled in this class, it just serializes HTTPResponse objects and
 * custom serializable objects as chunked content.<p>
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPResponseWriter extends ObjectWriter {
    private static final byte[] paddingZeros = new byte[] {'0','0','0','0','0','0','0','0','0'};

    private SimpleDateFormat dateFormat = new SimpleDateFormat(HTTPConstants.DATE_FORMAT_DEFAULT, Locale.US);

    private boolean usingChunkedOutput = false;
    
    public HTTPResponseWriter(int regularBufferSize, int bufferIncrement, int maxBufferSize, CSWriter csWriter) {
        super(regularBufferSize, bufferIncrement, maxBufferSize, csWriter);
        dateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
    }

    protected void writeObjectAppend(Object object) throws IOException {
        boolean mustCloseAfterContent = false;
        ByteBuffer byteBuffer = getBuffer();
        if (object instanceof HTTPResponse) {
            /*
             * HTTPResponse indicates that a full HTTP response must be written to the buffer: Headers, and content
             */
            HTTPResponse response = (HTTPResponse) object;
            StringBuffer sb = new StringBuffer();
            sb.append(HTTPConstants.HTTP_VERSION_1_1).
                append(" ").append(response.getResponseCode()).
                append(" ").append(response.getMessage()).append(HTTPConstants.CRLF);
            addHeader(HTTPConstants.HEADER_SERVER, HTTPConstants.HEADER_SERVER_VALUE, sb);
            addHeader(HTTPConstants.HEADER_DATE, formatDate(response.getDate()), sb);
            addHeader(HTTPConstants.HEADER_CACHE_CONTROL, HTTPConstants.HEADER_CACHE_CONTROL_NO_CACHE, sb);
            addHeader(HTTPConstants.HEADER_PRAGMA, HTTPConstants.HEADER_PRAGMA_NO_CACHE, sb);
            boolean chunked = HTTPHelper.isChunked(response);
            mustCloseAfterContent = !response.isOpeningOutputChannel();
            object = response.getContent();
            usingChunkedOutput = false;
            if (!response.isOpeningOutputChannel()) {
                if (response.getContent() == null) {
                    if (!chunked) {
                        addHeader(HTTPConstants.HEADER_CONTENT_LENGTH, "0", sb);
                    } else {
                        object = HTTPChunkedContent.CLOSING_CHUNK;
                    }
                } else {
                    if (!chunked) {
                        throw new UnsupportedOperationException("Cannot send content in not chunked mode within the response");
                    }
                }
            } else {
                if (!chunked ) {
                    response.setHeader(HTTPConstants.HEADER_CONNECTION, HTTPConstants.HEADER_CONNECTION_CLOSE);
                    addHeader(HTTPConstants.HEADER_TC_OPENBYTE, "Yes", sb);
                } else {
                    usingChunkedOutput = true;
                }
            }
            for (Iterator iter = response.getHeaders().entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                addHeader((String) entry.getKey(), (String) entry.getValue(), sb);
            }
            
            sb.append(HTTPConstants.CRLF);
            byte[] bytes = sb.toString().getBytes();
            byteBuffer.put(bytes, 0, bytes.length);
            
            if (response.isOpeningOutputChannel() && !chunked) {
                //If this is not a chunked channel we send a byte first to open the ouput, if we don't send
                //anything it will fail
                byteBuffer.put((byte)121);
            }
        }

        if (HTTPChunkedContent.CLOSING_CHUNK == object) {
            //This is "close the logical connection output" we close the chunked output.
            writeEndChunk(byteBuffer);
        } else if (object != null) {
            if (usingChunkedOutput) {
                //Any other object is serialized as chunked content
                writeObjectAsChunk(object, byteBuffer);
                if (mustCloseAfterContent) {
                    writeEndChunk(byteBuffer);
                }
            } else {
                super.writeObjectAppend(object);
            }
        }
    }

    private void writeEndChunk(java.nio.ByteBuffer byteBuffer) {
        byteBuffer.put((byte)'0');
        byteBuffer.put(HTTPConstants.CR);
        byteBuffer.put(HTTPConstants.LF);
        byteBuffer.put(HTTPConstants.CR);
        byteBuffer.put(HTTPConstants.LF);
    }

    private void writeObjectAsChunk(Object object, java.nio.ByteBuffer byteBuffer) throws IOException {
        int initialPosition = byteBuffer.position();
        byteBuffer.position(initialPosition + 9);
        int start = byteBuffer.position();
        super.writeObjectAppend(object);
        int pos = byteBuffer.position();
        byteBuffer.position(initialPosition);
        int size = pos - start;
        byte[] chunkSize = Integer.toHexString(size).getBytes();
        byteBuffer.put(paddingZeros, 0, 7 - chunkSize.length);
        byteBuffer.put(chunkSize, 0, chunkSize.length);
        byteBuffer.put(HTTPConstants.CR);
        byteBuffer.put(HTTPConstants.LF);
        byteBuffer.position(pos);
        byteBuffer.put(HTTPConstants.CR);
        byteBuffer.put(HTTPConstants.LF);
    }

    private void addHeader(String name, String value, StringBuffer sb) {
        sb.append(name).append(HTTPConstants.HEADER_STRING_NAME_VALUE_SEPARATOR).append(" ").append(value).append(HTTPConstants.CRLF);
    }

    private String formatDate(Date date) {
        synchronized (dateFormat) {
            return dateFormat.format(date);
        }
    }
}
