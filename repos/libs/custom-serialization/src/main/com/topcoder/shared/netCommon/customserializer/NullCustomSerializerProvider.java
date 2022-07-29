/*
 * NullCustomSerializerProvider Created 10/20/2006
 */
package com.topcoder.shared.netCommon.customserializer;

/**
 * Null pattern for CustomSerializerProvider
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NullCustomSerializerProvider implements CustomSerializerProvider {

    public boolean canHandle(Class clazz) {
        return false;
    }

    public CustomSerializer getSerializer(Class clazz) {
        return null;
    }
}
