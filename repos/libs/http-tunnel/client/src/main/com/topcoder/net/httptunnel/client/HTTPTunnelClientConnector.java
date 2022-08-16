/*
 * HTTPTunnelClientConnector Created 04/03/2007
 */
package com.topcoder.net.httptunnel.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import com.topcoder.net.httptunnel.common.HTTPTunnelConstants;
import com.topcoder.net.httptunnel.common.digest.TokenDigester;
import com.topcoder.netCommon.io.ClientConnector;
import com.topcoder.netCommon.io.IOConstants;
import com.topcoder.shared.util.concurrent.Waiter;

/**
 * HTTP Tunnel client connector. It establishes an HTTP connection instead of normal socket connection. All traffic over
 * the network is wrapped into HTTP requests/responses, which should appear as regular HTTP traffic to routers and
 * proxies. It is also possible to use HTTP proxy mechanism provided by Java, since it uses
 * <code>HttpURLConnection</code> provided by Java. The HTTP tunneling works as follows. One HTTP connection
 * (receiving HTTP connection) is dedicated to receive messages from the server. This connection will always be alive.
 * To send messages to the server, one or multiple HTTP connections (sending HTTP connections) will be used using 'POST'
 * method. Unlike socket, there is no reliable 'flush' feature for HTTP connections. Thus, if the HTTP chunked output
 * stream is used, for each flushing of the output stream, the HTTP connection is closed and a new HTTP connection is
 * created. By this means, it can guarantee that the data is sent via HTTP procotol.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPTunnelClientConnector implements ClientConnector {
    /** Represents the system property name whose value is used as the size of the output buffer. */
    public static final String OUTPUT_BUFFER_SIZE = "com.topcoder.net.httptunnel.client.output.buffer.size";

    /**
     * Represents the system property name whose value is used as a flag indicating if the sending HTTP connection
     * output stream should be reused after each send/receive.
     */
    public static final String USE_CHUNKED_OUTPUT = "com.topcoder.net.httptunnel.client.reuseConnection";

    /**
     * Represents the system property name whose value is used as the timeout of the HTTP tunnel connection
     * establishment.
     */
    public static final String CONNECT_TIMEOUT = "com.topcoder.net.httptunnel.client.connect.timeout";

    /** Represents the default timeout for the HTTP tunnel connection establishment. */
    private static final int CONNECT_TIMEOUT_DEFAULT = 10000;

    /** Represents the token digester used to produce authentication information. */
    private final TokenDigester digestGenerator = new TokenDigester();

    /**
     * Represents the thread which is responsible to send the buffered data to the server and disconnect the sending
     * HTTP connection.
     */
    private DisconnecterThread outputDisconnecter;

    /** Represents the thread which is responsible to disconnect the receiving HTTP connection. */
    private DisconnecterThread inputDisconnecter;

    /** Represents the URL of the HTTP tunnel for initial connection establishement. */
    private final String tunnelLocation;

    /**
     * Represents the unique ID of the HTTP tunnel connection. This ID is obtained in initial connection establishment.
     * It will be used to identify the following HTTP communications, since there is no guarantee that the HTTP
     * connection will keep alive.
     */
    private int tunnelId;

    /** Represents a flag indicating if the output stream of the sending HTTP connection should be reused. */
    private boolean reuseOutputStream;

    /** Represents the HTTP tunnel connection used to retrieve messages from server. */
    private HttpURLConnection inputConnection;

    /** Represents the HTTP tunnel connection used to send messages to server. */
    private HttpURLConnection outputConnection;

    /**
     * Represents the URL of the HTTP tunnel for the rest of the traffic. It contains the tunnel ID obtained during the
     * initial connection establishment.
     */
    private String tunnelURLString;

    /** Represents the input stream of the HTTP tunnel connection used to retrieve messages from server. */
    private InputStream inputStream;

    /** Represents the output stream of the HTTP tunnel connection used to send messages to server. */
    private OutputStream outputStream;

    /** Represents a token obtained from the server used to authenticate the following HTTP connections. */
    private String token;

    /** Represents the timestamp difference between the server and the local client. */
    private long serverTSDiff;

    /** Represents the maximum size of each chunk if chunked HTTP tunnel is used. */
    private int maxOutputChunk;

    /**
     * Creates a new instance of <code>HTTPTunnelClientConnector</code> class. The URL of the HTTP tunnel for initial
     * connection establishment is given. Reusing the sending HTTP connection or not is determined by the system
     * property {@link USE_CHUNKED_OUTPUT}.
     * 
     * @param tunnelLocation the URL of the HTTP tunnel for initial connection establishment.
     * @throws IOException if an I/O error occurs.
     */
    public HTTPTunnelClientConnector(String tunnelLocation) throws IOException {
        this(tunnelLocation, "true".equals(System.getProperty(USE_CHUNKED_OUTPUT, "true")));
    }

    /**
     * Creates a new instance of <code>HTTPTunnelClientConnector</code> class. The URL of the HTTP tunnel for initial
     * connection establishment is given. Reusing the sending HTTP connection or not is determined by
     * <code>reuseOutput</code> argument.
     * 
     * @param tunnelLocation the URL of the HTTP tunnel for initial connection establishment.
     * @param reuseOutput a flag indicating if the sending HTTP connection output stream should be reused.
     * @throws IOException if an I/O error occurs.
     */
    public HTTPTunnelClientConnector(String tunnelLocation, boolean reuseOutput) throws IOException {
        this.tunnelLocation = tunnelLocation;
        this.reuseOutputStream = reuseOutput;
        init();
        this.outputDisconnecter = new OutputDisconnecterThread();
        this.inputDisconnecter = new InputDisconnecterThread();
    }

    /**
     * Initializes the connection. It tries to establish the HTTP tunneling connection to obtain the token and tunnel ID
     * for the following communications.
     * 
     * @throws IOException if an I/O error occurs.
     */
    private void init() throws IOException {
        // Collect garbage to finalize any possible remaining connections.
        System.gc();
        System.runFinalization();

        // Use a separate connection thread to do the initial connection.
        ConnectThread connectThread = new ConnectThread();
        connectThread.start();
        try {
            // Wait for the timeout.
            connectThread.join(getInitialConnectTimeout());

            if (!connectThread.isAlive()) {
                // The connection is established.
                // Throw any exceptions during the connection establishment.
                if (connectThread.getException() != null) {
                    throw connectThread.getException();
                }
                if (connectThread.getRuntimeException() != null) {
                    throw connectThread.getRuntimeException();
                }
                // Success!
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Timed out.
        connectThread.interrupt();
        close();
        throw new IOException("Could not connect");

    }

    /**
     * Gets the timeout for the initial connection establishment. The timeout is stored in system property
     * {@link CONNECT_TIMEOUT}. If its value is invalid, a default timeout 1000 is used.
     * 
     * @return the timeout for the initial connection establishment.
     */
    private long getInitialConnectTimeout() {
        long timeout = CONNECT_TIMEOUT_DEFAULT;
        try {
            timeout = Long.parseLong(System.getProperty(CONNECT_TIMEOUT));
        } catch (Exception e) {
        }
        return timeout;
    }

    /**
     * Gets the output stream of current sending HTTP connection. If there is no current sending HTTP connection, a new
     * sending HTTP connection is established.
     * 
     * @return the output stream of current sending HTTP connection.
     * @throws IOException if an I/O error occurs.
     */
    protected OutputStream getHttpOutputStream() throws IOException {
        if (outputStream != null) {
            return outputStream;
        }
        createOutputConnection();
        return outputStream;
    }

    /**
     * Gets the input stream of the receiving HTTP connection. It should always be available after successful initial
     * connection establishment.
     * 
     * @return the input stream of the receiving HTTP connection.
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Gets the output stream which can be used to send messages to server. A wrapper to the output stream of the
     * current sending HTTP connection is provided.
     * 
     * @return the output stream which can be used to send messages to server.
     */
    public OutputStream getOutputStream() {
        // Direct use of getHttpOutputStream is not working here, since the output stream of HttpURLConnection might
        // only be an output stream to the internal buffer of the HttpURLConnection (when chunked HTTP connection is
        // not used).
        // The only difference between this output stream and the output stream of HttpURLConnection would be flush,
        // since it has to guarantee that the data is sent to the server.
        return new OutputStream() {
            /**
             * Writes the given byte to the output stream, which delegates to the output stream of current sending HTTP
             * connection directly.
             */
            public void write(int b) throws IOException {
                try {
                    getHttpOutputStream().write(b);
                } catch (IOException e) {
                    cleanOutputConnection();
                    throw e;
                }
            }

            /**
             * Writes the given bytes to the output stream, which delegates to the output stream of current sending HTTP
             * connection directly.
             */
            public void write(byte[] b) throws IOException {
                try {
                    getHttpOutputStream().write(b);
                } catch (IOException e) {
                    cleanOutputConnection();
                    throw e;
                }
            }

            /**
             * Writes the given bytes to the output stream, which delegates to the output stream of current sending HTTP
             * connection directly.
             */
            public void write(byte[] b, int off, int len) throws IOException {
                try {
                    getHttpOutputStream().write(b, off, len);
                } catch (IOException e) {
                    cleanOutputConnection();
                    throw e;
                }
            }

            /**
             * Flushes the data to the output stream. It first delegates the call to the output stream of current
             * sending HTTP connection. Then, if chunked stream is not used, the current sending HTTP connection is
             * closed and a new connection is created to be the current sending HTTP connection.
             */
            public void flush() throws IOException {
                try {
                    getHttpOutputStream().flush();
                    cleanOutputIfNeeded();
                } catch (IOException e) {
                    cleanOutputConnection();
                    throw e;
                }
            }

            /**
             * Closes the output stream. It first delegates the call to the output stream of current sending HTTP
             * connection. Then, if chunked stream is not used, the current sending HTTP connection is closed and a new
             * connection is created to be the current sending HTTP connection.
             */
            public void close() throws IOException {
                try {
                    getHttpOutputStream().close();
                    cleanOutputIfNeeded();
                } catch (IOException e) {
                    cleanOutputConnection();
                    throw e;
                }
            }
        };
    }

    /**
     * Flushes the data in current sending HTTP connection to the server. When the chunked mode is used (reusing the
     * output stream), there is no special handling. Otherwise, the data has to be forcely sent to the server, and a new
     * sending HTTP connection has to be created to be the current sending HTTP connection.
     * 
     * @throws IOException if an I/O error occurs.
     */
    protected void cleanOutputIfNeeded() throws IOException {
        if (!reuseOutputStream) {
            try {
                HttpURLConnection cnn = outputConnection;
                if (cnn != null) {
                    // We must force the sending.
                    cnn.getInputStream();
                }
            } finally {
                // Create a new current sending HTTP connection.
                cleanOutputConnection();
            }
        }
    }

    /**
     * Creates a new current sending HTTP connection.
     * 
     * @throws IOException if an I/O error occurs.
     */
    protected void createOutputConnection() throws IOException {
        try {
            Random r = new Random();
            // Use four random variables in the URL to avoid caching.
            URL tunnelURL = new URL(tunnelURLString + "&c1=" + r.nextInt() + "&c2=" + r.nextInt() + "&c3="
                + r.nextInt() + "&c4=" + r.nextInt());
            if (reuseOutputStream) {
                // We must try first a HEAD command to allow auth. And maybe we have luck and the same connection is
                // reused.
                doHeadForURL(tunnelURL);
            }
            // Create the HTTP connection, using POST and no caching.
            outputConnection = (HttpURLConnection) (tunnelURL.openConnection());
            outputConnection.setRequestMethod("POST");
            outputConnection.setDoOutput(true);
            outputConnection.setUseCaches(false);
            if (reuseOutputStream) {
                // When we reuse the output stream, it must be in chunked mode.
                outputConnection.setChunkedStreamingMode(getMaxOutputChunkSize());
            }
            // Set the connection to keep alive and binary stream as the content type.
            outputConnection.setRequestProperty(HTTPTunnelConstants.HEADER_CONNECTION,
                HTTPTunnelConstants.HEADER_CONNECTION_KEEP_ALIVE);
            outputConnection.setRequestProperty(HTTPTunnelConstants.HEADER_CONTENT_TYPE,
                HTTPTunnelConstants.HEADER_CONTENT_TYPE_APPOCTEC);
            // Add TC specific headers, including authentication header.
            addNewSecurityHeaders();
            outputStream = outputConnection.getOutputStream();
        } catch (IOException e) {
            System.out.println("Could not create output connection to tunnel server. Cleanning output connection");
            cleanOutputConnection();
            throw e;
        }
    }

    /**
     * Sends a request to the given URL using 'HEAD' method.
     * 
     * @param tunnelURL the URL where the request is sent to.
     */
    private void doHeadForURL(URL tunnelURL) {
        try {
            HttpURLConnection conn = (HttpURLConnection) (tunnelURL.openConnection());
            try {
                conn.setRequestMethod("HEAD");
                conn.setUseCaches(false);
                InputStream is = conn.getInputStream();
                is.close();
            } catch (Exception e) {
                conn.disconnect();
            }
        } catch (Exception e) {
        }
    }

    /**
     * Adds TopCoder specific headers into the HTTP request. It adds an approximate server-side timestamp and the digest
     * of the token, tunnel ID and the server-side timestamp to the header.
     */
    private void addNewSecurityHeaders() {
        if (token != null) {
            String serverTS = String.valueOf(serverTSDiff + System.currentTimeMillis());
            outputConnection.setRequestProperty(HTTPTunnelConstants.HEADER_TC_TS, serverTS);
            outputConnection.setRequestProperty(HTTPTunnelConstants.HEADER_TC_DIGEST, digestGenerator.generateDigest(
                token, tunnelId, serverTS));
        }
    }

    /**
     * Closes current sending HTTP connection. The data will be sent to the server synchronously with a timeout. After
     * the data is sent, the connection is closed. After calling this method, the current sending HTTP connection is
     * unset.
     */
    private void cleanOutputConnection() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e1) {
                System.out.println("Could not close output to tunnel server");
                e1.printStackTrace();
            }
            outputStream = null;
        }
        if (outputConnection != null) {
            // Send data and close the connection asynchronously.
            forceDisconnectOfHttp(outputConnection);
            outputConnection = null;
        }
    }

    /**
     * Closes the connections. Both receiving HTTP connection and current sending HTTP connection are closed
     * synchonously with timeout. Any pending sending data will be sent before closing.
     */
    public void close() {
        forceDisconnectOfHttp(outputConnection);
        forceDisconnectOfHttp(inputConnection);
        interrupThread(inputDisconnecter);
        interrupThread(outputDisconnecter);
    }

    /**
     * Interrupts an asynchronous closing of an HTTP tunnel connection. The thread working on the asynchronous closing
     * is given.
     * 
     * @param thread the thread to be interrupted.
     */
    private void interrupThread(DisconnecterThread thread) {
        if (thread != null) {
            thread.interrupt();
        }
    }

    /**
     * Gets the local endpoint of the connection. A string indicating HTTP tunneling is used will be returned.
     * 
     * @return the local endpoint of the connection.
     */
    public String getLocalEndpoint() {
        return "HttpTunnel(" + System.identityHashCode(this) + ")";
    }

    /**
     * Gets the remote endpoint of the connection. The returned string will be 'IP:Port'. 'IP' is the IP of the HTTP
     * tunnel, while 'Port' is the port of the HTTP tunnel.
     * 
     * @return the remote endpoint of the connection.
     */
    public String getRemoteEndpoint() {
        URL url = inputConnection.getURL();
        int port = url.getPort() < 0 ? url.getDefaultPort() : url.getPort();
        return url.getHost() + ":" + port;
    }

    /**
     * Closes the given HTTP connection synchronously with timeout. Any pending data will be sent to the server.
     * 
     * @param http the HTTP connection to be closed.
     */
    private void forceDisconnectOfHttp(final HttpURLConnection http) {
        if (http == null) {
            return;
        }

        DisconnecterThread disconnecter = null;
        // See if we are dealing with receiving or sending connection.
        if (http == inputConnection) {
            disconnecter = inputDisconnecter;
        } else {
            disconnecter = outputDisconnecter;
        }

        if (disconnecter != null && !disconnecter.disconnect()) {
            try {
                // FIXME 1.4 - When supporting 1.5+ we must use timeout for the HttpUrlConnection
                // and make a simple disconnect
                Method method = http.getClass().getDeclaredMethod("disconnectInternal", (Class[]) null);
                method.setAccessible(true);
                method.invoke(http, (Object[]) null);
            } catch (Exception e) {
                // nothing to do..
            }
        }
    }

    /**
     * Gets the size of chunks used in chunked HTTP connection. The value is specified in system property
     * {@link OUTPUT_BUFFER_SIZE}. When the value is invalid, a default size is used.
     * 
     * @see IOConstants.REQUEST_MAXIMUM_BUFFER_SIZE
     * @return the size of chunks used in chunked HTTP connection.
     */
    private int getMaxOutputChunkSize() {
        if (maxOutputChunk == 0) {
            String size = System.getProperty(OUTPUT_BUFFER_SIZE);
            if (size != null) {
                try {
                    maxOutputChunk = Integer.parseInt(size);
                } catch (Exception e) {
                }
            }
            if (maxOutputChunk == 0) {
                maxOutputChunk = IOConstants.REQUEST_MAXIMUM_BUFFER_SIZE;
            }
        }
        return maxOutputChunk;
    }

    /**
     * Defines a thread which is responsible to establish the initial connection and obtain token/tunnelID. It must be
     * done asynchronously, since <code>HttpURLConnection.getInputStream</code> happens after sending the request,
     * which may take forever.
     */
    private class ConnectThread extends Thread {
        /** Represents the I/O error occurred during the initial connection establishment. */
        private IOException exception;

        /** Represents any runtime error during the initial connection establishment. */
        private RuntimeException runtimeException;

        /**
         * Creates a new instance of <code>ConnectThread</code> class. It is always a daemon thread.
         */
        public ConnectThread() {
            super("HTTPConnector-" + System.currentTimeMillis());
            setDaemon(true);
        }

        /**
         * Executes the logic of establishing initial connection. This connection is also the receiving HTTP connection.
         */
        public void run() {
            try {
                Random r = new Random();
                // Use four random variables to avoid caching.
                URL server = new URL(tunnelLocation + "&c1=" + r.nextInt() + "&c2=" + r.nextInt() + "&c3="
                    + r.nextInt() + "&c4=" + r.nextInt());
                inputConnection = (HttpURLConnection) (server.openConnection());
                inputConnection.setRequestMethod("GET");
                inputConnection.setUseCaches(false);
                inputConnection.setRequestProperty("Content-Type", "application/octet-stream");

                // Make the request
                inputStream = inputConnection.getInputStream();
                // Get the token
                token = inputConnection.getHeaderField(HTTPTunnelConstants.HEADER_TC_TOKEN);
                if (token != null) {
                    // Allow token, more staff in headers
                    tunnelId = Integer.parseInt(inputConnection.getHeaderField(HTTPTunnelConstants.HEADER_TC_TUNNELID));
                    long serverTS = Long.parseLong(inputConnection.getHeaderField(HTTPTunnelConstants.HEADER_TC_TS));
                    serverTSDiff = serverTS - System.currentTimeMillis();
                    if (inputConnection.getHeaderField(HTTPTunnelConstants.HEADER_TC_OPENBYTE) != null) {
                        inputStream.read();
                    }
                } else {
                    // No token, tunnel ID is in the response body
                    tunnelId = new DataInputStream(inputStream).readInt();
                    if (reuseOutputStream) {
                        throw new IOException("Tunnel Mode not available.");
                    }
                }
                tunnelURLString = tunnelLocation + "&id=" + tunnelId;
            } catch (RuntimeException e) {
                this.runtimeException = e;
            } catch (IOException e) {
                this.exception = e;
            }
        }

        /**
         * Gets the I/O error during the initial connection. If there is no I/O error, <code>null</code> is returned.
         * 
         * @return the I/O error during the initial connection if any.
         */
        public IOException getException() {
            return exception;
        }

        /**
         * Gets the runtime error during the initial connection. If there is no runtime error, <code>null</code> is
         * returned.
         * 
         * @return the runtime error during the initial connection if any.
         */
        public RuntimeException getRuntimeException() {
            return runtimeException;
        }
    }

    /**
     * Defines an abstract class which has the ability to close an HTTP connection. It also can do additional process
     * before closing the HTTP connection with timeout (1000 msecs).
     */
    public abstract class DisconnecterThread extends Thread {
        /** Represents the signal used to synchronize the closing thread and the calling thread. */
        private Object mutex = new Object();

        /** Represents a flag indicating if the connection should be closed. */
        private boolean mustDisconnect = false;

        /** Represents a flag indicating if the connection is already closed. */
        private boolean disconnected = true;

        /**
         * Creates a new instance of <code>DisconnecterThread</code>. The name of the thread is given.
         * 
         * @param name the name of the thread.
         */
        public DisconnecterThread(String name) {
            super(name);
            setDaemon(true);
            start();
        }

        public void run() {
            while (true) {
                synchronized (mutex) {
                    // Wait until the disconnection request is made
                    while (!mustDisconnect) {
                        try {
                            mutex.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    mustDisconnect = false;
                }
                // Received a disconnection request.
                try {
                    closeStreamAndReturnConnection().disconnect();
                } catch (Exception e) {
                }
                synchronized (mutex) {
                    disconnected = true;
                    mutex.notify();
                }
            }
        }

        /**
         * Processes before closing the connection. It returns which HTTP connection should be closed.
         * 
         * @return the HTTP connection need to be closed.
         * @throws IOException if an I/O error occurs.
         */
        protected abstract HttpURLConnection closeStreamAndReturnConnection() throws IOException;

        /**
         * Closes the HTTP connection. Additional process can be done with a timeout (1000 msecs).
         * 
         * @return <code>true</code> if the HTTP connection has been closed; <code>false</code> otherwise.
         */
        public boolean disconnect() {
            synchronized (mutex) {
                mustDisconnect = true;
                disconnected = false;
                mutex.notify();
                try {
                    Waiter waiter = new Waiter(1000, mutex);
                    while (!waiter.elapsed() && !disconnected) {
                        waiter.await();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return disconnected;
            }
        }
    }

    /**
     * Defines a sub-class of <code>DisconnecterThread</code> which can close the receiving HTTP connection without
     * additional pre-closing process.
     */
    public class InputDisconnecterThread extends DisconnecterThread {
        /**
         * Creates a new instance of <code>InputDisconnecterThread</code> class with the thread name 'InputDisconnecter'.
         */
        public InputDisconnecterThread() {
            super("InputDisconnecter");
        }

        /**
         * Processes before closing the connection. It returns the receiving HTTP connection. It does not do any pre-closing
         * process.
         * 
         * @return the receiving HTTP connection.
         */
        protected HttpURLConnection closeStreamAndReturnConnection() {
            return inputConnection;
        }
    }

    /**
     * Defines a sub-class of <code>DisconnecterThread</code> which can close current sending HTTP connection. Any pending data
     * is sent to the server with the limit of the timeout.
     */
    public class OutputDisconnecterThread extends DisconnecterThread {
        /**
         * Creates a new instance of <code>OutputDisconnecterThread</code> class with the thread name 'OutputDisconnecter'.
         */
        public OutputDisconnecterThread() {
            super("OutputDisconnecter");
        }

        /**
         * Processes before closing the connection. It returns the current sending HTTP connection. It sends the pending data
         * to the server.
         * 
         * @return the current sending HTTP connection.
         */
        protected HttpURLConnection closeStreamAndReturnConnection() throws IOException {
            try {
                // Sending the data by getting the input stream
                InputStream is = outputConnection.getInputStream();
                // Discard any remaining data
                while (is.available() > 0) {
                    is.read();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return outputConnection;
        }
    }
}
