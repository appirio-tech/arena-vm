/*
 * ClientConnector
 *
 * Created 04/03/2007
 */
package com.topcoder.netCommon.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ClientConnector is the common interface ClientSocket uses to interact with the underlying
 * connection.<p>
 *
 * @author Diego Belfer (mural)
 * @version $Id: ClientConnector.java 71125 2008-06-10 04:45:51Z dbelfer $
 */
public interface ClientConnector {
    /**
     * Gets the OutputStream associated to this connector
     *
     * @return The OutputStream
     * @throws IOException If an IO error occurs when obtaining the OutputStream
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Gets the InputStream associated to this connector
     *
     * @return The InputStream
     * @throws IOException If an IO error occurs when obtaining the InputStream
     */
    InputStream getInputStream() throws IOException;

    /**
     * Closes this connector and releases all associated resources
     *
     * @throws IOException if an I/O error occurs when closing this connector
     */
    void close() throws IOException;

    /**
     * Returns the Local endpoint this connector is using.
     *
     * @return The local endpoint
     */
    String getLocalEndpoint();

    /**
     * Returns the Remote endpoint this connector is connected to.
     *
     * @return The remote endpoint
     */
    String getRemoteEndpoint();
}
