package com.topcoder.shared.security;

/**
 * interface for classes that can persist objects.  implementing classes could use
 * a cache, an HttpSession, a database etc.   it should be assumed that it is a shared
 * resource, so keys should be unique.
 *
 * @author Greg Paul
 */
public interface Persistor {

    public Object getObject(String key);

    public void setObject(String key, Object value);

    public void removeObject(String key);
}
