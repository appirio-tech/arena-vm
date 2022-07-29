/*
 * HexEncoding
 *
 * Created 04/13/2007
 */
package com.topcoder.shared.util.encoding;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HexEncoding {
    private static final byte[] DIGITS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    /**
     * Returns a hexdecimal string representation of a <code>byte[]</code>
     *
     * @param bytes the array whose hex string representation is required
     *
     * @return The hex string representation of the array.
     */
    public static String toHexString(byte[] bytes) {
        return toHexString(bytes, 0, bytes.length);
    }


    /**
     * Returns a hexdecimal string representation of a <code>byte[]</code>
     * section determined by offset and size.
     *
     * @param bytes the array whose hex string representation is required
     * @param offset Initial position of the section
     * @param size  Size of the section
     *
     * @return The hex string representation of the array.
     */
    public static String toHexString(byte[] bytes, int offset, int size) {
        byte[] hexBytes = new byte[size << 1];
        int j = 0;
        int maxPos = size + offset;
        for (int i = offset; i < maxPos; i++) {
            hexBytes[j++] = DIGITS[(0xf0 & bytes[i]) >>> 4];
            hexBytes[j++] = DIGITS[0x0f & bytes[i]];
        }
        return new String(hexBytes);
    }
}
