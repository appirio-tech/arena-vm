package com.topcoder.shared.util;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.util.idgenerator.IDGenerationException;
import com.topcoder.util.idgenerator.IDGeneratorFactory;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Sep 26, 2007
 */
public class IdGeneratorClient {

    private static Logger log = Logger.getLogger(IdGeneratorClient.class);


    /**
     * Uses the IdGenerator class to retrieve a sequence value for the
     * sequence name given. Will initialize the IdGenerator if not initialized
     * yet.
     *
     * @param seqName
     * @return The next sequence val. -1 if there is an exception thrown
     *         or other error retrieving the sequence id.
     */

    public static long getSeqId(String seqName) throws IDGenerationException {
        if (log.isDebugEnabled()) {
            log.debug("getSeqId(" + seqName + ") called");
        }
        long ret = IDGeneratorFactory.getIDGenerator(seqName).getNextID();
        if (log.isDebugEnabled()) {
            log.debug("returning " + ret);
        }
        return ret;
    }
    
    /**
     * Uses the IdGenerator class to retrieve a sequence value for the
     * sequence name given. Will initialize the IdGenerator if not initialized
     * yet.
     *
     * @param seqName
     * @return The next sequence value.
     */
    public static int getSeqIdAsInt(String seqName) throws IDGenerationException {
        return (int) getSeqId(seqName);
    }

}