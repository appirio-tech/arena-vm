/*
 * HTTPRequestReader
 *
 * Created 04/04/2007
 */
package com.topcoder.net.httptunnel.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.topcoder.netCommon.io.ObjectReader;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.util.encoding.HexEncoding;

/**
 * HTTPRequestReader is responsible for reading http request messages into the byte buffer.<p>
 *
 * Chunked input state is handled in this class, it just deserializes http request into HTTPRequest objects and
 * custom serializable objects from chunked content.<p>
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPRequestReader extends ObjectReader {
    private final Logger log = Logger.getLogger(HTTPTunnelListener.class);

    /**
     * If we are expecting chunked content on the connection
     */
    private boolean receivingChunkedData;


    private int lastCheckedPos = -1;
    private int expectedSize = 5;

    public HTTPRequestReader(int bufferSize, int bufferIncrement, int maxBufferSize, CSReader csReader) {
        super(bufferSize, bufferIncrement, maxBufferSize, csReader);
    }

    protected Object readStreamObject() throws IOException {
        try {
            if (receivingChunkedData) {
                return readObjectFromChunk();
            } else {
                return readHTTPRequest();
            }
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Error while parsing request: ",e);
            log.error("ByteBuffer Was (0..1024): "
                    + HexEncoding.toHexString(getByteBuffer().array(),
                            getByteBuffer().position(),
                            Math.min(1024, getBuffer().remaining())));
            throw e;
        }
    }

    private Object readHTTPRequest() throws IOException {
        java.nio.ByteBuffer byteBuffer = getByteBuffer();
        //We need at least expectedSize chars.
        if (byteBuffer.remaining() < expectedSize) {
            return null;
        }
        if (lastCheckedPos < 10 && !areInitialBytesValids(byteBuffer)) {
            throw new StreamCorruptedException("Unexpected method name");
        }
        //Detect end of headers: 2 CRLF
        int initialPos = byteBuffer.position();
        int maxPos = initialPos + byteBuffer.remaining()-3;
        int i = lastCheckedPos < initialPos ? initialPos : lastCheckedPos;
        byte[] data = byteBuffer.array();
        while(i < maxPos && (data[i] != HTTPConstants.CR || data[i+2] != HTTPConstants.CR)) {
            i++;
        }
        if (i == maxPos) {
            if (byteBuffer.capacity() == byteBuffer.remaining()) {
                //we must expand out buffer.. If it is possible
                ensureCapacityAndFilled(byteBuffer.capacity()+1);
            }
            lastCheckedPos = i-2;
            //We need more bytes
            return null;
        }
        //Move the byte buffer to the end of the headers and CRLFs
        //Advance buffer pointer after CRLFCRLF
        lastCheckedPos = i;
        byteBuffer.position(i+4);
        int headerSize = (i+4) - initialPos;

        HTTPRequest request = null;
        //We found 2 CR, so the end of Headers Request is there
        try {
            request = extractRequestFromEnvelope(data, initialPos, i - initialPos + 1);
        } catch (Exception e) {
            log.error("Cannot parse HTTP message ", e);
            throw new StreamCorruptedException("Cannot parse HTTP message: "+e.getMessage());
        }

        if (HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED.equals(request.getHeader(HTTPConstants.HEADER_TRANSFER_ENCODING))) {
            receivingChunkedData = true;
        } else if (request.getHeader(HTTPConstants.HEADER_CONTENT_LENGTH) != null) {
            int lenght = Integer.parseInt(request.getHeader(HTTPConstants.HEADER_CONTENT_LENGTH));
            //log.error("Content-Length is not support currently");
            if (byteBuffer.remaining() < lenght) {
                byteBuffer.position(initialPos);
                //We set the expected size to avoid parse again this buffer until we get all bytes
                expectedSize = headerSize+lenght;
                //And we created ensure buffer is big enough
                ensureRequiredDataAvailable(expectedSize);
                return null;
            }
            request.setContent(readObjectFromHTTPStream(byteBuffer));
        } else {
            //We just skip CRLF
            //byteBuffer.position(byteBuffer.position()+2);
        }
        lastCheckedPos = -1;
        expectedSize = 5;
        return request;
    }


    private HTTPRequest extractRequestFromEnvelope(byte[] data, int offset, int len) throws IOException, MalformedURLException {
        String httpHeaders = new String(data, offset, len);
        HTTPRequest request = new HTTPRequest();
        BufferedReader reader = new BufferedReader(new StringReader(httpHeaders));
        String firstLine = reader.readLine();
        parseHeaders(request, reader);
        parseRequestLine(request, firstLine);
        return request;
    }


    private void parseRequestLine(HTTPRequest request, String firstLine) throws MalformedURLException {
        String[] requestLine = firstLine.split(" ");
        request.setMethod(resolveMethod(requestLine));
        request.setUrl(resolveURL(request, requestLine));
        request.setHttpVersion(requestLine[2]);
        extractParametersFromURL(request);
    }


    private void extractParametersFromURL(HTTPRequest request) {
        String query = request.getUrl().getQuery();
        if (query!=null) {
            String[] parameters = query.split(HTTPConstants.QUERY_STRING_SEPARATOR_CHAR);
            for (int i = 0; i < parameters.length; i++) {
                int separator = parameters[i].indexOf(HTTPConstants.QUERY_STRING_NAME_VALUE_SEPARATOR);
                if (separator == -1 || separator == parameters[i].length()) {
                    request.setParameter(parameters[i]);
                } else {
                    request.setParameter(parameters[i].substring(0, separator), parameters[i].substring(separator+1));
                }
            }
        }
    }


    private URL resolveURL(HTTPRequest request, String[] requestLine) throws MalformedURLException {
        URL url = null;
        try {
            url = new URL(requestLine[1]);
        } catch (Exception e) {
            if (request.getHeader(HTTPConstants.HEADER_HOST) != null) {
                url = new URL("http://"+request.getHeader(HTTPConstants.HEADER_HOST)+requestLine[1]);
            } else {
                url = new URL("http://locahost"+requestLine[1]);
            }
        }
        return url;
    }


    private String resolveMethod(String[] requestLine) {
        for (int i = 0; i < HTTPConstants.REQ_METHODS.length; i++) {
            if (HTTPConstants.REQ_METHODS[i].equals(requestLine[0])) {
                return  HTTPConstants.REQ_METHODS[i];
            }
        }
        return requestLine[0];
    }

    private boolean areInitialBytesValids(java.nio.ByteBuffer buffer) {
        for (int i = 0; i < HTTPConstants.REQ_METHODS.length; i++) {
            String method = HTTPConstants.REQ_METHODS[i];
            if (buffer.remaining() > method.length()) {
                if  (checkEqual(buffer.array(), buffer.position(), method, method.length()) &&
                        buffer.array()[method.length()] == ' ') {
                    return true;
                }
            } else if (checkEqual(buffer.array(), buffer.position(), method, buffer.remaining())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkEqual(byte[] bs, int offset, String string, int size) {
        for(int i = 0; i < size; i++) {
            if (bs[offset+i] != string.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private void parseHeaders(HTTPRequest request, BufferedReader reader) throws IOException {
        String line=reader.readLine();
        while (line != null) {
            int separator = line.indexOf(HTTPConstants.HEADER_STRING_NAME_VALUE_SEPARATOR);
            request.setHeader(line.substring(0, separator), line.substring(separator+2));
            line = reader.readLine();
        }
    }

    private Object readObjectFromChunk() throws StreamCorruptedException, IOException {
        java.nio.ByteBuffer byteBuffer = getByteBuffer();
        //We need at least 3 chars  One digit + CRLF
        if (byteBuffer.remaining() < 3) {
            return null;
        }
        int initialPos = byteBuffer.position();
        int maxPos = initialPos + byteBuffer.remaining();
        int i = initialPos;
        byte[] data = byteBuffer.array();
        while(i < maxPos && data[i] != HTTPConstants.CR) {
            i++;
        }
        if (i == maxPos) {
            return null;
        }
        int chunkSize = Integer.parseInt(new String(data, initialPos, i - initialPos), 16);
        if (!ensureRequiredDataAvailable(chunkSize+4+i)) {
            return null;
        }
        if (chunkSize > 0) {
            byteBuffer.position(i+2);
            //TODO Verify no Proxy firewall splits chunks. We require a full message be included in only one chunk.
            Object object = readObjectFromHTTPStream(byteBuffer);
            byteBuffer.position(byteBuffer.position()+2);
            return new HTTPChunkedContent(chunkSize, object);
        } else {
            byteBuffer.position(i+4);
            receivingChunkedData = false;
            return HTTPChunkedContent.CLOSING_CHUNK;
        }
    }

    private Object readObjectFromHTTPStream(java.nio.ByteBuffer byteBuffer) throws IOException {
        Object object = super.readStreamObject();
        return object;
    }
}
