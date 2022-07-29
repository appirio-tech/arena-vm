package com.topcoder.shared.dataAccess;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * This class provides a way to build a request for data
 * that includes a query.
 *
 * @author Greg Paul
 * @version $Revision$
 * @see com.topcoder.shared.dataAccess.QueryRunner
 */
public class QueryRequest implements RequestInt {
    private Map mProp;
    private Map queries;

    /**
     * Default Constructor
     */
    public QueryRequest() {
        queries = new HashMap();
        mProp = new Properties();
    }

    /**
     * Constructor that takes a map that it calls <code>setProperties()</code> on
     *
     * @param map
     * @throws Exception
     */
    public QueryRequest(Map map) throws Exception {
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
                sValue = sValue == null ? "" : sValue; //nulls not allowed in Properties
                if (sKey.equals(DataAccessConstants.QUERY_KEY)) {
                    addQuery(sKey, sValue);
                } else
                    mProp.put(sKey, sValue);
            } else if (me.getValue().getClass().isArray()) {
                arrayType = me.getValue().getClass().getComponentType().toString();
                // only need to handle String arrays afaik.
                if (arrayType.equals("class java.lang.String")) {
                    sArray = (String[]) me.getValue();
                    sKey = me.getKey().toString();
                    if (sArray.length > 0) {
                        if (sKey.equals(DataAccessConstants.QUERY_KEY))
                            addQuery(sKey + 0, sArray[0]);
                        else
                            mProp.put(sKey, sArray[0]);
                    }
                }
                for (int i = 1; i < sArray.length; i++) {
                    if (sKey.equals(DataAccessConstants.QUERY_KEY))
                        addQuery(sKey + i, sArray[i]);
                    else
                        mProp.put(sKey + i, sArray[i]);
                }
            } else {
                throw new Exception("unrecognized class " + me.getValue().getClass());
            }
        }
    }

    /**
     * Gets a specific property for this request bean
     *
     * @param sKey the key for the property
     * @return String the value of the property, or null if
     *         property is unassigned.
     */
    public String getProperty(String sKey) {
        Object o = mProp.get(sKey);
        if (o == null) {
            return null;
        } else {
            return o.toString();
        }
    }

    /**
     * Gets a specific property for this request bean
     *
     * @param sKey          the key for the property
     * @param sDefaultValue the default value if null
     * @return String the value of the property
     */
    public String getProperty(String sKey, String sDefaultValue) {
        if (mProp.containsKey(sKey))
            return mProp.get(sKey).toString();
        else
            return sDefaultValue;
    }

    /**
     * Sets a specific property for this request bean
     *
     * @param sKey The property key
     * @param sVal The property value
     */
    public void setProperty(String sKey, String sVal) {
        mProp.put(sKey, sVal);
        if (sKey.equals(DataAccessConstants.QUERY_KEY))
            addQuery(sKey, sVal);
    }

    /**
     * Gets the queries associated with this object.
     *
     * @return Map the queries.
     */
    public Map getQueries() {
        return queries;
    }

    /**
     * Sets the queries associated with this object.
     *
     * @param queries the queries.
     */
    public void setQueries(Map queries) {
        this.queries = queries;
        mProp.put(DataAccessConstants.QUERY_KEY, queries);
    }

    /**
     * Gets a particular query associated with this object.
     *
     * @param key
     * @return String a key used to retrieve the query.
     */
    public String getQuery(String key) {
        return (String) queries.get(key);
    }

    /**
     * Adds the given query.
     *
     * @param name
     * @param query
     */
    public void addQuery(String name, String query) {
        queries.put(name, query);
        mProp.put(DataAccessConstants.QUERY_KEY, queries);
    }

    /**
     * Implementation of toString, it includes each
     * of the key/value pairs from the properties
     * object of this object.
     *
     * @return a String representation of this object
     */
    public String toString() {
        Iterator it = mProp.entrySet().iterator();
        Map.Entry me = null;
        StringBuffer sb = new StringBuffer();

        for (; it.hasNext();) {
            me = (Map.Entry) it.next();
            sb.append(me.getKey().toString());
            sb.append('=');
            sb.append(me.getValue().toString());
        }
        return sb.toString();
    }

    /**
     * Generate a string from this object sutable for using
     * as a key for some key/value pair construct.
     *
     * @return the String
     */
    public String getCacheKey() {
        Map.Entry me = null;
        StringBuffer sb = new StringBuffer(100);
        //using a tree map so that the keys are always in the same order
        //we want the cache to pick it up regardless of order
        TreeMap t = new TreeMap(mProp);

        for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
            me = (Map.Entry) it.next();
            if (me.getKey().equals(DataAccessConstants.QUERY_KEY)) {
                TreeMap queries = new TreeMap((Map) me.getValue());
                Map.Entry me1 = null;
                for (Iterator qIt = queries.entrySet().iterator(); qIt.hasNext();) {
                    me1 = (Map.Entry) qIt.next();
                    sb.append(me1.getKey().toString());
                    sb.append("=");
                    sb.append(md5((String) me1.getValue()));  //it's a whole query, so use the md5 sum to save space
                    sb.append("|");
                }
            } else {
                sb.append(me.getKey().toString());
                sb.append("=");
                sb.append(me.getValue().toString());
                sb.append("|");
            }
        }
        return sb.toString();
    }

    private String md5(String s) {
        String ret = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte[] bytes = md.digest();
            ret = new String(bytes);
        } catch (Exception e) {
            //we'll just ignore it, i'm confident md5 exists
        }
        return ret;

    }

}
