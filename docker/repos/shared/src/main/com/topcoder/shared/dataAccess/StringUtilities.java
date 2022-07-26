package com.topcoder.shared.dataAccess;

import java.util.ArrayList;

/**
 * This class contains various string-related utility functions.
 *
 * @author  Dave Pecora
 * @author  Greg Paul
 * @version 1.01, 02/15/2002
 */

public class StringUtilities {
    /**
     * Replaces one string with another within a third string.
     *
     * @param   currentStr The string within which replacements will be made
     * @param   oldStr The string to replace
     * @param   newStr The string with which to replace oldStr
     * @return  The new string, with replacements made
     * @throws  IllegalArgumentException if any argument is null, or oldStr.length()==0
     */
    public static final String replace(String currentStr, String oldStr, String newStr) {
        if (currentStr == null || oldStr == null || newStr == null)
            throw new IllegalArgumentException("Null strings not allowed as parameters");
        // Discourages infinite loops - s.indexOf("") == 0 for any non-null string s!
        if (oldStr.length() == 0)
            throw new IllegalArgumentException("oldStr must have nonzero length");
        int i = currentStr.indexOf(oldStr), j;
        if (i >= 0) {
            StringBuffer sb = new StringBuffer(currentStr.length());
            sb.append(currentStr.substring(0, i));
            sb.append(newStr);
            int oldLen = oldStr.length();
            i += oldLen;
            while ((j = currentStr.indexOf(oldStr, i - 1)) >= 0) {
                sb.append(currentStr.substring(i, j));
                sb.append(newStr);
                i = j + oldLen;
            }
            sb.append(currentStr.substring(i));
            return sb.toString();
        }
        // If we got here, there was nothing to replace.
        return currentStr;
    }

    /**
     * Pretty prints the contents of an input object.  Intended
     * especially for use on ArrayLists and arrays.
     *
     * @param   result The result object for which to print out contents
     * @return  A string with the contents of the input result object.
     */
    public static String makePretty(Object result) {
        StringBuffer buf = new StringBuffer(250);
        if (result instanceof java.util.ArrayList) {
            ArrayList tmp = (ArrayList) result;
            for (int i = 0; i < tmp.size(); i++) {
                buf.append(makePrettier(tmp.get(i)));
                if (i != tmp.size() - 1)
                    buf.append(",\n");
            }
        } else if (result.getClass().isArray()) {
            buf.append(makePrettier(result));
        } else if (result instanceof String) {
            buf.append("\"");
            buf.append(result.toString());
            buf.append("\"");
        } else {
            buf.append(result.toString());
        }
        return buf.toString();
    }


    private static String makePrettier(Object result) {
        StringBuffer buf = new StringBuffer(250);
        if (result.getClass().isArray()) {
            String type = result.getClass().getComponentType().toString();

            buf.append("{");
            try {
                if (type.equals("int")) {
                    int[] temp = (int[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append(temp[i] + ", ");
                    }
                    buf.append(temp[temp.length - 1]);
                } else if (type.equals("double")) {
                    double[] temp = (double[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append(temp[i] + ", ");
                    }
                    buf.append(temp[temp.length - 1]);
                } else if (type.equals("class java.lang.String")) {
                    String[] temp = (String[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append("\"");
                        buf.append(temp[i]);
                        buf.append("\"");
                        buf.append(", ");
                    }
                    if (temp.length>0) {
                        buf.append("\"");
                        buf.append(temp[temp.length - 1]);
                        buf.append("\"");
                    }
                } else if (type.equals("float")) {
                    float[] temp = (float[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append(temp[i] + ", ");
                    }
                    buf.append(temp[temp.length - 1]);
                } else if (type.equals("boolean")) {
                    boolean[] temp = (boolean[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append(temp[i] + ", ");
                    }
                    buf.append(temp[temp.length - 1]);
                } else if (type.equals("long")) {
                    long[] temp = (long[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append(temp[i] + ", ");
                    }
                    buf.append(temp[temp.length - 1]);
                } else if (type.equals("char")) {
                    char[] temp = (char[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append(temp[i] + ", ");
                    }
                    buf.append(temp[temp.length - 1]);
                } else if (type.equals("byte")) {
                    byte[] temp = (byte[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append(temp[i] + ", ");
                    }
                    buf.append(temp[temp.length - 1]);
                } else if (type.equals("short")) {
                    short[] temp = (short[]) result;
                    for (int i = 0; i < temp.length - 1; i++) {
                        buf.append(temp[i] + ", ");
                    }
                    buf.append(temp[temp.length - 1]);
                }
            } catch (Exception e) {
            }
            buf.append("}");
        } else if (result instanceof java.util.ArrayList) {
            ArrayList tmp = (ArrayList) result;
            buf.append("{");
            for (int i = 0; i < tmp.size(); i++) {
                buf.append(makePrettier(tmp.get(i)));
                if (i != tmp.size() - 1)
                    buf.append(", ");
            }
            buf.append("}");
        } else if (result instanceof String) {
            buf.append("\"");
            buf.append(result.toString());
            buf.append("\"");
        } else
            buf.append(result.toString());

        return buf.toString();
    }
}

