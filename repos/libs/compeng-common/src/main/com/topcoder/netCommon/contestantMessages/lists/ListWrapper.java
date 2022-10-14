/*
 * ListWrapper.java Created on June 26, 2002, 11:48 PM
 */

package com.topcoder.netCommon.contestantMessages.lists;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Defines an extension to array list, which provides the feature of setting and getting primitive types directly. The
 * primitives are boxed and unboxed automatically.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: ListWrapper.java 72143 2008-08-06 05:54:59Z qliu $
 */
public abstract class ListWrapper extends ArrayList {
    /**
     * Creates a new instance of <code>ListWrapper</code>. The list is initialized to have the initial size. The list
     * is filled with <code>Object</code> instances.
     * 
     * @param size the initial size of the list.
     */
    ListWrapper(int size) {
        super(size);
        for (int i = 0; i < size; i++)
            add(new Object());
    }

    /**
     * Creates a new instance of <code>ListWrapper</code>. The content is copied from the given list.
     * @param al the list whose content is copied.
     * 
     */
    ListWrapper(Collection al) {
        super(al);
    }

    /**
     * Sets the item at index with a byte value.
     * 
     * @param index the index to be set.
     * @param x the byte value.
     */
    protected void set(int index, byte x) {
        set(index, new Byte(x));
    }

    /**
     * Sets the item at index with a char value.
     * 
     * @param index the index to be set.
     * @param x the char value.
     */
    protected void set(int index, char x) {
        set(index, new Character(x));
    }

    /**
     * Sets the item at index with a short value.
     * 
     * @param index the index to be set.
     * @param x the short value.
     */
    protected void set(int index, short x) {
        set(index, new Short(x));
    }

    /**
     * Sets the item at index with an integer value.
     * 
     * @param index the index to be set.
     * @param x the integer value.
     */
    protected void set(int index, int x) {
        set(index, new Integer(x));
    }

    /**
     * Sets the item at index with a long value.
     * 
     * @param index the index to be set.
     * @param x the long value.
     */
    protected void set(int index, long x) {
        set(index, new Long(x));
    }

    /**
     * Sets the item at index with a float value.
     * 
     * @param index the index to be set.
     * @param x the float value.
     */
    protected void set(int index, float x) {
        set(index, new Float(x));
    }

    /**
     * Sets the item at index with a double value.
     * 
     * @param index the index to be set.
     * @param x the double value.
     */
    protected void set(int index, double x) {
        set(index, new Double(x));
    }

    /**
     * Gets the byte value at the index.
     * 
     * @param index the index to be get.
     * @return the byte value.
     * @throws ClassCastException if the value at the index is not a byte.
     */
    protected byte getByte(int index) {
        return ((Byte) get(index)).byteValue();
    }

    /**
     * Gets the char value at the index.
     * 
     * @param index the index to be get.
     * @return the char value.
     * @throws ClassCastException if the value at the index is not a char.
     */
    protected char getChar(int index) {
        return ((Character) get(index)).charValue();
    }

    /**
     * Gets the short value at the index.
     * 
     * @param index the index to be get.
     * @return the short value.
     * @throws ClassCastException if the value at the index is not a short.
     */
    protected short getShort(int index) {
        return ((Short) get(index)).shortValue();
    }

    /**
     * Gets the integer value at the index.
     * 
     * @param index the index to be get.
     * @return the integer value.
     * @throws ClassCastException if the value at the index is not an integer.
     */
    protected int getInt(int index) {
        return ((Integer) get(index)).intValue();
    }

    /**
     * Gets the long value at the index.
     * 
     * @param index the index to be get.
     * @return the long value.
     * @throws ClassCastException if the value at the index is not a long.
     */
    protected long getLong(int index) {
        return ((Long) get(index)).longValue();
    }

    /**
     * Gets the float value at the index.
     * 
     * @param index the index to be get.
     * @return the float value.
     * @throws ClassCastException if the value at the index is not a float.
     */
    protected float getFloat(int index) {
        return ((Float) get(index)).floatValue();
    }

    /**
     * Gets the double value at the index.
     * 
     * @param index the index to be get.
     * @return the double value.
     * @throws ClassCastException if the value at the index is not a double.
     */
    protected double getDouble(int index) {
        return ((Double) get(index)).doubleValue();
    }
}
