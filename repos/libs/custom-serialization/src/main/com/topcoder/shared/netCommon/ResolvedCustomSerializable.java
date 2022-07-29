/*
 * ResolvedCustomSerializable Created 10/11/2006
 */
package com.topcoder.shared.netCommon;

/**
 * A ResolvedCustomSerializable class is a class that in addition to CustomSerializable it provides a mean to obtain an
 * specific instance of the object like readResolve method provides to {@link java.io.Serializable} types.
 * <p>
 * <b>Note:</b><br>
 * Using the readResolve method is possible to deserialize Singleton instances.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ResolvedCustomSerializable extends CustomSerializable {

    /**
     * Returns the deserialized instance.
     * <p>
     * This methods gets called after customReadObject method.
     * 
     * @return the instance
     */
    public Object readResolve();
}
