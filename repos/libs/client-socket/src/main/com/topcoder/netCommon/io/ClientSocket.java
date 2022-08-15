package com.topcoder.netCommon.io;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.topcoder.net.httptunnel.client.HTTPTunnelClientConnector;
import com.topcoder.shared.netCommon.CSHandler;

/**
 * The default <code>SocketInterface</code> implementation.
 *
 * @author  Timur Zambalayev
 */
public final class ClientSocket {

    private ObjectWriter writer;
    private ObjectReader reader;
    private OutputStream outputStream;
    private final DataInput dataInput;
    private InputChannelActivityListener inputChannelActivityListener = new NullInputChannelActivityListener();
    private ClientConnector connector;

    /**
     * Constructs a new client socket connected to the specified IP address and port using the specified CS handler.
     *
     * @param   address                         the IP address.
     * @param   port                            the port number.
     * @param   csHandler                       the custom serialization handler.
     * @throws  java.io.IOException             if an I/O error occurs when creating the socket.
     */
    public ClientSocket(InetAddress address, int port, CSHandler csHandler) throws IOException {
        this(new Socket(address, port), csHandler);
    }

    /**
     * Constructs a new client socket that will backed by the specified socket using the specified CS handler.
     *
     * @param   socket                  the underlying <code>java.net.Socket</code> instance.
     * @param   csHandler               the custom serialization handler.
     * @throws  java.io.IOException     if an I/O error occurs when creating the socket.
     */
    public ClientSocket(Socket socket, CSHandler csHandler) throws IOException {
        this(new SocketClientConnectorAdapter(socket), csHandler);
    }


    /**
     * Creates a new instance of <code>ClientSocket</code> class. The socket and the custom-serialization handler
     * are given. A HTTP tunnel URL is also given. If the tunnel URL is <code>null</code> or empty string, no HTTP
     * tunneling will be used. Otherwise, all traffic will be tunneled via the given URL.
     * 
     * @param socket the Java socket instance to communicate via network.
     * @param csHandler the custom-serialization handler used to serialize/de-serialize data.
     * @param tunnelLocation the HTTP tunnel URL, if available, used to tunnel the traffic.
     * @throws IOException if an I/O error occurs.
     */
    public ClientSocket(Socket socket, CSHandler csHandler, String tunnelLocation) throws IOException {
        this((tunnelLocation != null && tunnelLocation.length() > 0) ?
                        (ClientConnector) new HTTPTunnelClientConnector(tunnelLocation) :
                        new SocketClientConnectorAdapter(socket), csHandler);
    }

    /**
     * Creates a new instance of <code>ClientSocket</code> class. The network communication is via the given
     * <code>ClientConnector</code> instance, and the serialization is via the given custom-serialization handler.
     *
     * @param connector the network layer instance by which all communication is through.
     * @param csHandler the custom serialization handler used to serialize/de-serialize data.
     * @throws IOException if an I/O error occurs.
     */
    public ClientSocket(ClientConnector connector, CSHandler csHandler) throws IOException {
        init(csHandler);
        this.connector = connector;
        this.outputStream = connector.getOutputStream();
        this.dataInput = buildDataInputStream(connector.getInputStream());
    }

    /**
     * Initializes the custom serialization handler used to serialize and deserialize data.
     * 
     * @param csHandler the custom serialization handler used in this instance.
     */
    private void init(CSHandler csHandler) {
        writer = new ObjectWriter(IOConstants.REQUEST_INITIAL_BUFFER_SIZE, IOConstants.REQUEST_BUFFER_INCREMENT, IOConstants.REQUEST_MAXIMUM_BUFFER_SIZE, csHandler);
        reader = new ObjectReader(IOConstants.RESPONSE_INITIAL_BUFFER_SIZE, IOConstants.RESPONSE_BUFFER_INCREMENT, IOConstants.RESPONSE_MAXIMUM_BUFFER_SIZE, csHandler);
    }

    /**
     * Gets the local end point used in the network communication.
     * 
     * @return the string representing the local end point.
     */
    public String getLocalEndpoint() {
        return connector.getLocalEndpoint();
    }

    /**
     * Gets the remote end point used in the network communication.
     * 
     * @return the string representing the remote end point.
     */
    public String getRemoteEndpoint() {
        return connector.getRemoteEndpoint();
    }

    /**
     * Writes the given object to the remote side via the network. The object is serialized by the custom serialization handler.
     * This method is thread-safe.
     * 
     * @param object the object to be written to the remote side.
     * @return the size of the largest possible object to be written to the remote side.
     * @throws IOException if an I/O error occurs.
     */
    public int writeObject(Object object) throws IOException {
        return writeObject(object, writer, outputStream);
    }

    /**
     * Writes the given object to the given output stream using the given object writer. The object
     * is serialized by the given object writer. This method is thread-safe.
     * 
     * @param object the object to be written to the remote side.
     * @param writer the object writer used to serialize object.
     * @param outputStream the output stream where the serialized data is written to.
     * @return the size of the largest possible object to be written to the remote side.
     * @throws IOException if an I/O error occurs.
     */
    private synchronized int writeObject(Object object, ObjectWriter writer, OutputStream outputStream) throws IOException {
        return writer.writeObject(outputStream, object);
    }

    /**
     * Reads an object from the network. The object must be serialized by the same custom serialization handler.
     * 
     * @return an object deserialized from the data read from the network.
     * @throws ObjectStreamException if the serialized data is corrupted.
     * @throws IOException if an I/O error occurs. 
     */
    public Object readObject() throws ObjectStreamException, IOException {
        Object object = readObject(reader, dataInput);
        return object;
    }

    /**
     * Reads an object from the network. The deserialization is handled by the given object reader, and the serialized data
     * is read from the given data input.
     *
     * @param reader the object reader used to deserialize the data.
     * @param dataInput the data input where the serialized data is read from.
     * @return an object deserialized from the data read from the network.
     * @throws IOException if an I/O error occurs. 
     */
    private Object readObject(ObjectReader reader, DataInput dataInput) throws IOException {
        return reader.readObject(dataInput);
    }

    /**
     * Closes the network connection.
     * 
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        connector.close();
    }


    /**
     * Gets the input channel activity listener which will be notified whenever a read from the network occurs.
     * 
     * @return Returns the inputChannelActivityListener.
     */
    public InputChannelActivityListener getInputChannelActivityListener() {
        return inputChannelActivityListener;
    }

    /**
     * Sets the input channel activity listener which will be notified whenever a read from the network occurs.
     * 
     * @param listener The inputChannelActivityListener to set. If this value
     *               is <code>null</code>, a <code>NullInputChannelActivityListener</code> is used
     */
    public void setInputChannelActivityListener(InputChannelActivityListener listener) {
        if (listener != null) {
            this.inputChannelActivityListener = listener;
        } else {
            this.inputChannelActivityListener = new NullInputChannelActivityListener();
        }
    }

    /**
     * Returns a string representation of the <code>ClientSocket</code> instance in the form "host:port".
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        return connector.toString();
    }

    /**
     * Builds the <code>DataInputStream</code> for the inputStream specified as argument.
     * Adds all necessary decorators before creating the <code>DataInputStream</code>
     *
     * @param inputStream The InputStream
     *
     * @return The DataInputStream built
     */
    private DataInputStream buildDataInputStream(InputStream inputStream) {
        inputStream =  new BufferedInputStream(inputStream);
        inputStream =  new NotificationInputStreamDecorator(inputStream);
        return new DataInputStream(inputStream);
    }

    /**
     * Helper class used for InputStream decoration to allow
     * notification on every read block
     *
     * @author Diego Belfer (mural)
     * @version $Id$
     */
    private class NotificationInputStreamDecorator extends FilterInputStream {
        /**
         * Creates a new instance of <code>NotificationInputStreamDecorator</code> class. The wrapped input stream is given.
         * 
         * @param decorated the wrapped input stream.
         */
        public NotificationInputStreamDecorator(InputStream decorated) {
            super(decorated);
        }

        public int read() throws IOException {
            int value = super.read();
            if (value > -1) {
                inputChannelActivityListener.bytesRead(1);
            }
            return value;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int amount = super.read(b, off, len);
            if (amount > 0) {
	            inputChannelActivityListener.bytesRead(amount);
			}
            return amount;
        }
    }
}
