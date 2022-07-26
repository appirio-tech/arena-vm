package com.topcoder.shared.util.loader;

import com.topcoder.shared.util.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * Implementations of this interface are responsible for
 * taking configuration, and calling the individual DataRetriever's
 * as specified by the configuration.
 *
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 11, 2006
 */
public interface Launcher {

    static final Logger log = Logger.getLogger(Launcher.class);

    public static final String OLTP = "OLTP";
    public static final String DW = "DW";
    public static final String SORT = "sort";
    public static final String CLASS = "class";
    public static final String SOURCE_DB = "source_db";
    public static final String TARGET_DB = "target_db";

    void setConnections(Map connections);
    void setRetrievers(List retrievers);
    void setConfigurations(List configurations);
    void run() throws Exception;
}
