/*
 * DefaultReaderWriterFactory
 *
 * Created 04/04/2007
 */
package com.topcoder.server.listener;

import com.topcoder.netCommon.io.IOConstants;
import com.topcoder.netCommon.io.ObjectReader;
import com.topcoder.netCommon.io.ObjectWriter;
import com.topcoder.shared.netCommon.CSHandlerFactory;

/**
 * Default ReaderWriterFactory. <p>
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DefaultReaderWriterFactory implements ReaderWriterFactory {
    private CSHandlerFactory csHandlerFactory;

    public DefaultReaderWriterFactory(CSHandlerFactory csHandlerFactory) {
        this.csHandlerFactory = csHandlerFactory;
    }

    public ObjectReader newObjectReader() {
        return new ObjectReader(IOConstants.REQUEST_INITIAL_BUFFER_SIZE, IOConstants.REQUEST_BUFFER_INCREMENT, IOConstants.REQUEST_MAXIMUM_BUFFER_SIZE, csHandlerFactory.newInstance());
    }

    public ObjectWriter newObjectWriter() {
        return new ObjectWriter(IOConstants.RESPONSE_INITIAL_BUFFER_SIZE, IOConstants.RESPONSE_BUFFER_INCREMENT, IOConstants.RESPONSE_MAXIMUM_BUFFER_SIZE, csHandlerFactory.newInstance());
    }

}
