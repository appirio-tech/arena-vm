/*
 * SimpleCustomSerializerProvider Created 10/20/2006
 */
package com.topcoder.shared.netCommon.customserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple CustomSerializerProvider implementation, it allows to register a CustomSerializer for a specific clazz.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SimpleCustomSerializerProvider implements CustomSerializerProvider {
    private Map clazzMap = new HashMap(50);

    public boolean canHandle(Class clazz) {
        return clazzMap.containsKey(clazz);
    }

    public CustomSerializer getSerializer(Class clazz) {
        return (CustomSerializer) clazzMap.get(clazz);
    }

    /**
     * Register the <code>serializer</code> for the given class.
     * <p>
     * If one serializer was already registered for the class, it will be unregistered.
     * 
     * @param clazz The class for which the serializer is registered
     * @param serializer The serializer to register.
     */
    public void registerSerializer(Class clazz, CustomSerializer serializer) {
        clazzMap.put(clazz, serializer);
    }

    /**
     * Unregisters the serializer registered for the given class, if any.
     * 
     * @param clazz The class for which the serializer is unregistered
     * @param serializer The serializer to unregister.
     */
    public void unregisterSerializer(Class clazz, CustomSerializer serializer) {
        clazzMap.remove(clazz);
    }
}
