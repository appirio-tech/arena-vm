package com.topcoder.shared.docGen.xml.xsl;

import com.topcoder.shared.distCache.Cache;
import com.topcoder.shared.util.logging.Logger;

import java.io.FileNotFoundException;

/**
 * XSLTransformerCache.java
 * <p/>
 * Description: A Singleton Cache for XSLTransformerWrappers
 *
 * @author Steve Burrows (chuck)
 * @version 1.0
 */

public class XSLTransformerCache {

    private static Cache cache = null;
    private static final int DEFAULT_EXPIRE_TIME = 1000 * 60 * 60 * 6;  //cache em for 6 hours
    private static final int MAX_CACHE_SIZE = 100;
    private static XSLTransformerCache xslTransformerCache = null;
    private static Logger log = Logger.getLogger(XSLTransformerCache.class);


    /* Singleton -- the constructor is private, must use getInstance. */
    private XSLTransformerCache() {
        cache = new Cache(MAX_CACHE_SIZE);
    }


    /**
     * Singleton method for getting the one instance of this class (per VM)
     *
     * @return
     */
    public static XSLTransformerCache getInstance() {
        if (xslTransformerCache == null) xslTransformerCache = new XSLTransformerCache();
        return xslTransformerCache;
    }


    /**
     * Get the XSLTransformerWrapper indicated by the cacheKey.
     * If the XSLTransformerWrapper does not exist in the cache, it is created and put in the cache.
     * This method artificially requires that the cacheKey be the path/filename of the xsl template
     * this is not strictly necessary, but we would need another method taking both a key and
     * a template stream.
     *
     * @param fileName the unique identifier for the XSLTransformerWrapper possibly in the cache.
     * @return a XSLTransformerWrapper identified by the cacheKey.
     * @throws Exception
     */
    public XSLTransformerWrapper getXSLTransformerWrapper(String fileName)
            throws Exception {
        log.debug("XSLTransformerCache.getXSLTransformerWrapper for " + fileName);
        XSLTransformerWrapper result = null;
        if (fileName == null) throw new Exception("The file name can not be null.");
        result = (XSLTransformerWrapper) (cache.get(fileName));
        if (result == null) {
            java.io.File file = new java.io.File(fileName);
            if (!file.exists()) throw new FileNotFoundException("Unable to find file " + fileName + ".");
            result = new XSLTransformerWrapper(file);
        }
        log.debug("adding " + fileName + " to cache.");
        cache.update(fileName, result, DEFAULT_EXPIRE_TIME);
        log.debug("cache size is now: " + cache.size());
        return result;
    }


    /**
     * Remove all XSLTransformerWrapper instances from the cache.
     */
    public void clear() throws Exception {
        cache.clearCache();
    }

    /**
     * The number of objects in the cache.
     *
     * @return the integer number of entries in the cache.
     */
    public int size() {
        return cache.size();
    }


}
