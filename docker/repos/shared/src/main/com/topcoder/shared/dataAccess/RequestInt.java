package com.topcoder.shared.dataAccess;

import java.util.Map;

/**
 * This interface must be implemented for any request wrappers.
 *
 *
 * @author Greg Paul
 * @version $Revision$
 */
public interface RequestInt {
    /**
     *
     * @return
     */
    Map getProperties();

    /**
     *
     * @param map
     * @throws Exception
     */
    void setProperties(Map map) throws Exception;

    /**
     *
     * @param sKey
     * @return
     */
    String getProperty(String sKey);

    /**
     *
     * @param sKey
     * @param sDefaultValue
     * @return
     */
    String getProperty(String sKey, String sDefaultValue);

    /**
     *
     * @param sKey
     * @param sVal
     */
    void setProperty(String sKey, String sVal);

    String getCacheKey();

}
