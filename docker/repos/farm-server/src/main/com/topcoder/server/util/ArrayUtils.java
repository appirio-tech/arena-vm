/*
 * ArrayUtils
 *
 * Created 04/22/2006
 */
package com.topcoder.server.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains various methods commonly
 * used when working with arrays
 *
 * @author Diego Belfer (mural)
 * @version $Id: ArrayUtils.java 67962 2008-01-15 15:57:53Z mural $
 */
public class ArrayUtils {
    /**
     * Returns a string representation of the array
     *
     * @param a Array to be represented as string
     * @return String representing the array <code>a</code>.
     *          "null" if the <code>a</code> is null
     */
    public static String asString(int[] a) {
        if (a == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer(10 + (a.length << 3));
        sb.append("[");
        if (a.length > 0) {
            sb.append(a[0]);
            for (int i = 1; i < a.length; i++) {
                sb.append(",");
                sb.append(a[i]);

            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a string representation of the array
     *
     * @param a Array to be represented as string
     * @return String representing the array <code>a</code>.
     *          "null" if the <code>a</code> is null
     */
    public static String asString(long[] a) {
        if (a == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer(10 + (a.length << 3));
        sb.append("[");
        if (a.length > 0) {
            sb.append(a[0]);
            for (int i = 1; i < a.length; i++) {
                sb.append(",");
                sb.append(a[i]);

            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a string representation of the array
     *
     * @param a Array to be represented as string
     * @return String representing the array <code>a</code>.
     *          "null" if the <code>a</code> is null
     */
    public static String asString(Object[] a) {
        if (a == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer(10 + (a.length << 3));
        sb.append("[");
        if (a.length > 0) {
            sb.append(a[0]);
            for (int i = 1; i < a.length; i++) {
                sb.append(",");
                sb.append(a[i]);

            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a string representation of the array
     *
     * @param a Array to be represented as string
     * @return String representing the array <code>a</code>.
     *          "null" if the <code>a</code> is null
     */
    public static String asString(int[][] a) {
        if (a == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer(10 + (a.length << 3));
        sb.append("[");
        if (a.length > 0) {
            sb.append(asString(a[0]));
            for (int i = 1; i < a.length; i++) {
                sb.append(",\n");
                sb.append(asString(a[i]));

            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a string representation of the array
     *
     * @param a Array to be represented as string
     * @return String representing the array <code>a</code>.
     *          "null" if the <code>a</code> is null
     */
    public static String asString(long[][] a) {
        if (a == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer(10 + (a.length << 3));
        sb.append("[");
        if (a.length > 0) {
            sb.append(asString(a[0]));
            for (int i = 1; i < a.length; i++) {
                sb.append(",\n");
                sb.append(asString(a[i]));

            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a string representation of the array
     *
     * @param a Array to be represented as string
     * @return String representing the array <code>a</code>.
     *          "null" if the <code>a</code> is null
     */
    public static String asString(Object[][] a) {
        if (a == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer(10 + (a.length << 3));
        sb.append("[");
        if (a.length > 0) {
            sb.append(asString(a[0]));
            for (int i = 1; i < a.length; i++) {
                sb.append(",\n");
                sb.append(asString(a[i]));

            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns an array containg all long values in the list.<p>
     *
     * @param longValues A list containing numbers {@link Number}
     *
     * @return an long[] with all the numbers in the list in the same order
     */
    public static long[] getLongArrayFromList(List longValues) {
        long[]  r = new long[longValues.size()];
        int i = 0;
        for (Iterator it = longValues.iterator(); it.hasNext(); i++) {
            r[i] = ((Number) it.next()).longValue();
        }
        return r;
    }

    /**
     * Returns an array containg all int values in the collection.<p>
     *
     * @param intValues A list containing numbers {@link Number}
     *
     * @return an int[] with all the numbers in the collection in the same order
     */
    public static int[] getIntArray(Collection values) {
        int[]  r = new int[values.size()];
        int i = 0;
        for (Iterator it = values.iterator(); it.hasNext(); i++) {
            r[i] = ((Number) it.next()).intValue();
        }
        return r;
    }


    /**
     * Compares the arrays. Objects contained in the arrays
     * must implement Comparable interfase.
     *
     * @param a1 The first array
     * @param a2 The seconds array
     * @return The result of the comparation {@link Comparable#compareTo(Object)}
     */
    public int compare(Object[] a1, Object[] a2) {
        return compare(a1, a2, new Comparator() {
            public int compare(Object arg0, Object arg1) {
                return ((Comparable) arg0).compareTo(arg1);
            }
        });
    }

    /**
     * Compares the arrays using the given comparator
     *
     * @param a1 The first array
     * @param a2 The seconds array
     * @param comparator The comparator to use
     * @return The result of the comparation {@link Comparator#compare(Object, Object)}
     */
    public int compare(Object[] a1, Object[] a2, Comparator comparator) {
        int maxSize = Math.min(a1.length, a2.length);
        int value = 0;
        for (int i = 0; i < maxSize && value == 0; i++) {
            value = comparator.compare(a1[i], a2[i]);
        }
        if (value == 0) {
            return a1.length > a2.length ? 1 : (a1.length < a2.length ? -1 : 0);
        }
        return value;
    }
    
    /**
     * Find the first matching object in the array starting at position <code>startOffset</code>,
     * for which matcher.match is <code>true</code>
     * 
     * @param a The array to be searched
     * @param startOffset The initial position
     * @param matcher The matcher to use
     * 
     * @return the position of the first matching object or -1 is none was found.
     */
    public static int firstMatch(Object[] a, int startOffset, Matcher matcher) {
        for (int i = startOffset; i < a.length; i++) {
            if (matcher.match(a[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public interface Matcher {
        boolean match(Object object);
    }
    
    /**
     * Returns a copy of a portion of the original array
     * 
     * @param source The source array
     * @param offset The initial offset
     * @return a new Array of size source.length - offset where result[i] = source[offset+i]
     */
    public static Object[] subArray(Object[] source, int offset) {
        if (source == null) {
            return null;
        }
        return subArray(source, offset, source.length - offset);
    }
    
    
    /**
     * Returns a copy of a portion of the original array
     * 
     * @param source The source array
     * @param offset The initial offset
     * @param lenght The length of the portion
     * @return a new Array of size length where result[i] = source[offset+i]
     */
    public static Object[] subArray(Object[] source, int offset, int lenght) {
        if (source == null) {
            return null;
        }
        if (offset+lenght > source.length) {
            throw new IllegalArgumentException("Invalid offset and lenght for array size (offset="+offset+", lenght="+lenght+", sourceLenght="+source.length);
        }
        int newLenght = lenght;
        Object[] dst = (Object[]) Array.newInstance(source.getClass().getComponentType(), newLenght);
        for (int i = 0; i < newLenght; i++) {
            dst[i] = source[i+offset];
        }
        return dst;
    }
}
