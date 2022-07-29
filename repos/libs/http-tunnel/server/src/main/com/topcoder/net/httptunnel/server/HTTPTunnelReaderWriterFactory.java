/*
 * HTTPTunnelReaderWriterFactory
 *
 * Created 04/05/2007
 */
package com.topcoder.net.httptunnel.server;

import com.topcoder.netCommon.io.IOConstants;
import com.topcoder.netCommon.io.ObjectReader;
import com.topcoder.netCommon.io.ObjectWriter;
import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.server.listener.ReaderWriterFactory;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HTTPTunnelReaderWriterFactory implements ReaderWriterFactory {
    private CSHandlerFactory csHandlerFactory;

    public HTTPTunnelReaderWriterFactory(CSHandlerFactory csHandlerFactory) {
        this.csHandlerFactory = csHandlerFactory;
    }

    public ObjectReader newObjectReader() {
        return new HTTPRequestReader(IOConstants.REQUEST_INITIAL_BUFFER_SIZE, IOConstants.REQUEST_BUFFER_INCREMENT, IOConstants.REQUEST_MAXIMUM_BUFFER_SIZE, csHandlerFactory.newInstance());
    }

    public ObjectWriter newObjectWriter() {
        return new HTTPResponseWriter(IOConstants.RESPONSE_INITIAL_BUFFER_SIZE, IOConstants.RESPONSE_BUFFER_INCREMENT, IOConstants.RESPONSE_MAXIMUM_BUFFER_SIZE, csHandlerFactory.newInstance());
    }
}
