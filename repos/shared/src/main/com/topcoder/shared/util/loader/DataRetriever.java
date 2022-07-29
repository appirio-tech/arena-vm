package com.topcoder.shared.util.loader;

import com.topcoder.shared.util.logging.Logger;

import java.sql.Connection;
import java.util.Properties;

/**
 * Implementing classes will select data from a source database
 * build a query object using that data, and put that data on a queue
 * to be inserted/updated/delete from the database.
 * 
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 8, 2006
 */
public interface DataRetriever {
static final Logger log = Logger.getLogger(DataRetriever.class);
    void registerTargetProcessingQueue(Queue q);
    void run() throws Exception;

    void setSourceDatabase(Connection conn);
    void setTargetDatabase(Connection conn);

    void setConfiguration(Properties p);


}
