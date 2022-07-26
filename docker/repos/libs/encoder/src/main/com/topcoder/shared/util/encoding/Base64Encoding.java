/*
 * Base64Encoding
 * 
 * Created 12/13/2006
 */
package com.topcoder.shared.util.encoding;

/**
 * Base64 Encoder/Decoder.
 * 
 * Code extracted from com.topcoder.util.config.ConfigManager.Util
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class Base64Encoding {
    private static final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    
    /**
     * Encode a byte array into a String using base 64 encoding.
     *
     * @param b
     * @return the encoded String
     */
    public static String encode64(byte[] b) {

        int outputlength = ((b.length + 2) / 3) * 4;
        StringBuffer sb = new StringBuffer(outputlength);

        int len = (b.length / 3) * 3;
        int leftover = b.length - len;

        for (int i = 0; i < len; i += 3) {
            //get next three bytes in unsigned form lined up
            int combined = b[i] & 0xff;
            combined <<= 8;
            combined |= b[i + 1] & 0xff;
            combined <<= 8;
            combined |= b[i + 2] & 0xff;

            //break those 24 bits into 4 groups of 6 bits
            int c3 = combined & 0x3f;
            combined >>>= 6;
            int c2 = combined & 0x3f;
            combined >>>= 6;
            int c1 = combined & 0x3f;
            combined >>>= 6;
            int c0 = combined & 0x3f;

            //Translate them to equivalent alphanumeric char
            sb.append(BASE64_CHARS.charAt(c0));
            sb.append(BASE64_CHARS.charAt(c1));
            sb.append(BASE64_CHARS.charAt(c2));
            sb.append(BASE64_CHARS.charAt(c3));
        }
        if (leftover == 1) {
            sb.append(encode64(new byte[]{b[len], 0, 0}
            ).substring(0, 2));
            sb.append("==");
        } else if (leftover == 2) {
            sb.append(encode64(new byte[]{b[len], b[len + 1], 0}
            ).substring(0, 3));
            sb.append("=");
        }
        return sb.toString();
    }
    
    /**
     * Decode a string that was encoded using a base 64 encoding into its
     * original bytes.
     *
     * @param s The String to be decoded
     * @return a byte[]
     */
    public static byte[] decode64(String s) {
        int len = s.length();
        byte[] b = new byte[(s.length() / 4) * 3];
        int cycle = 0;
        int combined = 0;
        int j = 0;
        int dummies = 0;
        for (int i = 0; i < len; i++) {
            int c = s.charAt(i);
            int value = (c == '=') ? -2 : ((c <= 255) ? BASE64_CHARS.indexOf(c) : -1);
            if (value == -2) {
                value = 0;
                dummies++;
            }
            if (value != -1) {
                if (cycle == 0) {
                    combined = value;
                    cycle++;
                } else {
                    combined <<= 6;
                    combined |= value;
                    cycle++;
                }
                if (cycle == 4) {
                    b[j + 2] = (byte) combined;
                    combined >>>= 8;
                    b[j + 1] = (byte) combined;
                    combined >>>= 8;
                    b[j] = (byte) combined;
                    j += 3;
                    cycle = 0;
                }
            }
        }
        if (dummies > 0) {
            j -= dummies;
            byte[] b2 = new byte[j];
            System.arraycopy(b, 0, b2, 0, j);
            b = b2;
        }
        return b;
    }
}
