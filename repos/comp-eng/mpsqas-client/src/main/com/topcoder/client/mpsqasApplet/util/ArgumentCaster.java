package com.topcoder.client.mpsqasApplet.util;

import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.DataValue;
import com.topcoder.shared.problem.DataValueParseException;

import java.util.ArrayList;
import java.io.IOException;

/**
 * A class which converts arguments back and forth between Strings and
 * Objects.
 *
 * @author mitalub
 */
public class ArgumentCaster {

    /**
     * castArgs converts a String[] arguments to an DataType[]
     * containing Objects that are of the types specified in paramTypes.  If
     * this is not possible, an exception is thrown.
     *
     * @param paramTypes  What the arguements should be.
     * @param uncastedArgs String representations of the argument values.
     */
    public static Object[] castArgs(DataType[] paramTypes,
            String[] uncastedArgs) throws IOException, DataValueParseException {
        return DataValue.parseValuesToObjects(uncastedArgs, paramTypes);
    }

    /**
     * castArgs converts an Object[] of arguments to an ArrayList of String
     * representations of the Objects
     *
     * @param paramTypes  What types the arguments should are.
     * @param castedArgs  An DataValue[] of args
     */
    public static String[] uncastArgs(DataType[] paramTypes,
            DataValue[] castedArgs) {
        String[] strings = new String[castedArgs.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = castedArgs[i].encode();
        }
        return strings;
    }
}
