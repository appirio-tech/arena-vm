package com.topcoder.services.util.datatype;

/**
 * ArgumentCloner.java
 *
 * Created on October 10, 2001
 */


/**
 * The object of this class is to clone an ArrayList of arguments.
 *
 * @author Alex Roman
 * @version 1.0
 */
public final class ArgumentCloner {

    private ArgumentCloner() {
    }

    /**
     * Clone an ArrayList of Objects.
     *
     * @param args            The arguments as an ArrayList of Objects
     * @return                 ArrayList containing a new ArrayList of cloned arguments
     */
    ////////////////////////////////////////////////////////////////////////////////
    public static Object[] cloneArgs(Object[] args)
            ////////////////////////////////////////////////////////////////////////////////
    {
        Object[] clone = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            Object o = args[i];
            if (o instanceof int[]) {
                clone[i] = (((int[]) o).clone());
            } else if (o instanceof String[]) {
                clone[i] = (((String[]) o).clone());
            } else if (o instanceof char[]) {
                clone[i] = (((char[]) o).clone());
            } else if (o instanceof float[]) {
                clone[i] = (((float[]) o).clone());
            } else if (o instanceof boolean[]) {
                clone[i] = (((boolean[]) o).clone());
            } else if (o instanceof double[]) {
                clone[i] = (((double[]) o).clone());
            } else if (o instanceof short[]) {
                clone[i] = (((short[]) o).clone());
            } else if (o instanceof long[]) {
                clone[i] = (((long[]) o).clone());
            } else {
                clone[i] = o;
            }
        }

        return (clone);
    }

}
