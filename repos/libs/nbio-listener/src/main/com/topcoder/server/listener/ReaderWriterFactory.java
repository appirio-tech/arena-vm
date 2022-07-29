/*
 * ReaderWriterFactory
 *
 * Created 04/04/2007
 */
package com.topcoder.server.listener;

import com.topcoder.netCommon.io.ObjectReader;
import com.topcoder.netCommon.io.ObjectWriter;

/**
 * Factory fo ObjectReader and ObjectWriter used in the NBIOListener class.<p>
 *
 * It provides a way for change serialiazation of messages read/written to TCP connections.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ReaderWriterFactory {
    /**
     * @return a new reader
     */
    ObjectReader newObjectReader();
    /**
     * @return a new writer
     */
    ObjectWriter newObjectWriter();
}
