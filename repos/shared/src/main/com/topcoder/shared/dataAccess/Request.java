package com.topcoder.shared.dataAccess;

import com.topcoder.shared.util.logging.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * This class is a generic request-bean.  It serves as a container for all
 * information that is parsed out of the request.  Note that each key must be unique
 * so if you have 2 coder ids, you need coderA and coderB.
 *
 * @author tbone
 * @version $Revision$
 */
public class Request implements RequestInt {
    private static Logger log = Logger.getLogger(Request.class);
    //generic container for mappings
    private Properties mProp;


    //var for the c=someStuff
    private String msContentHandle;

    /**
     * Default constructor
     */
    public Request() {
        msContentHandle = "";
        mProp = new Properties();
    }

    /**
     * Constructor that takes a map and sets this object's
     * properties using that map.
     *
     * @param map
     * @throws Exception
     */
    public Request(Map map) throws Exception {
        this();
        setProperties(map);
    }

    /**
     * Gets all the property-mappings for this request bean
     *
     * @return java.util.Properties
     */
    public Map getProperties() {
        return mProp;
    }

    /**
     * Sets the properties for the bean. Called in non-default
     * constructor
     *
     * @param map A set of mappings
     * @throws Exception
     */
    public void setProperties(Map map) throws Exception {
        Iterator it = map.entrySet().iterator();
        Map.Entry me = null;
        String[] sArray = null;
        String sKey = null;
        String sValue = null;
        String arrayType = null;

        while (it.hasNext()) {
            me = (Map.Entry) it.next();
            if (me.getValue() instanceof String) {
                sKey = me.getKey().toString(); //maps can't have null-key
                sValue = (String) me.getValue();
                if (sKey.equals(DataAccessConstants.COMMAND))
                    setContentHandle(sValue);
                else if (sValue != null)
                    mProp.put(sKey, sValue);
            } else if (me.getValue().getClass().isArray()) {
                arrayType = me.getValue().getClass().getComponentType().toString();
                // only need to handle String arrays afaik.
                if (arrayType.equals("class java.lang.String")) {
                    sArray = (String[]) me.getValue();
                    sKey = me.getKey().toString();
                    if (sArray.length > 0) {
                        if (sKey.equals(DataAccessConstants.COMMAND))
                            setContentHandle(sArray[0]);
                        else if (sArray[0] != null)
                            mProp.put(sKey, sArray[0]);
                    }
                    for (int i = 1; i < sArray.length; i++) {
                        if (sArray[i] != null)
                            mProp.put(sKey + i, sArray[i]);
                    }
                }
            } else {
                throw new Exception("unrecognized class " + me.getValue().getClass());
            }
        }
    }

    /**
     * Gets the content handle for this request bean
     *
     * @return String
     */
    public String getContentHandle() {
        return msContentHandle;
    }

    /**
     * Sets the content handle for this request bean
     *
     * @param s
     */
    public void setContentHandle(String s) {
        msContentHandle = s;
        mProp.setProperty(DataAccessConstants.COMMAND, s);
    }

    /**
     * Gets a specific property for this request bean
     *
     * @param sKey the key for the property
     * @return the value of the property, or null if
     *         property is unassigned.
     */
    public String getProperty(String sKey) {
        return mProp.getProperty(sKey);
    }

    /**
     * Gets a specific property for this request bean
     *
     * @param sKey          the key for the property
     * @param sDefaultValue the default value if null
     * @return String the value of the property
     */
    public String getProperty(String sKey, String sDefaultValue) {
        return mProp.getProperty(sKey, sDefaultValue);
    }

    /**
     * Sets a specific property for this request bean
     *
     * @param sKey The property key
     * @param sVal The property value
     */
    public void setProperty(String sKey, String sVal) {
        mProp.setProperty(sKey, sVal);
        if (sKey.equals(DataAccessConstants.COMMAND))
            msContentHandle = sVal;
    }

    /**
     * Implementation of toString, it includes each
     * of the key/value pairs from the properties
     * object of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return getCacheKey();
    }

    /**
     * Generate a string from this object sutable for using
     * as a key for some key/value pair construct.
     *
     * @return
     */
    public String getCacheKey() {
        Map.Entry me = null;
        StringBuffer sb = new StringBuffer(100);
        //using a tree map so that the keys are always in the same order
        //we want the cache to pick it up regardless of order
        TreeMap t = new TreeMap(mProp);

        for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
            me = (Map.Entry) it.next();
            sb.append(me.getKey().toString());
            sb.append("=");
            sb.append(me.getValue().toString());
            sb.append("|");
        }
        return sb.toString();
    }

}
