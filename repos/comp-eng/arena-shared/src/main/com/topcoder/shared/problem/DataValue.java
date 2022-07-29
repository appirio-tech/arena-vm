package com.topcoder.shared.problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Defines an abstract base class which represents all types of data values, such as 'int', 'double', 'string', or
 * arrays. The XML representation would be a encoded string.
 * 
 * @author Qi Liu
 * @version $Id: DataValue.java 71757 2008-07-17 09:13:19Z qliu $
 */
abstract public class DataValue extends BaseElement {
    /** Represents the mapping of type names and the element classes to hold the values of the type. */
    static private HashMap types = buildTypes();

    /**
     * Parses the encoded string into the given data type. The encoded string can be accessed via the given reader. The
     * parsed result should be stored in the instance.
     * 
     * @param reader the reader used to read the encoded string.
     * @param type the data type which the encoded string is parsed to.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the encoded string is malformed.
     */
    abstract public void parse(DataValueReader reader, DataType type) throws IOException, DataValueParseException;

    /**
     * Encodes the value held by the instance into a string.
     * 
     * @return the encoded value.
     */
    abstract public String encode();

    /**
     * Gets the value held by the instance.
     * 
     * @return the value held by the instance.
     */
    abstract public Object getValue();

    public String toXML() {
        return BaseElement.encodeHTML(encode());
    }

    /**
     * Parses the encoded string into a proper sub-class of <code>DataValue</code> which holds the value of the
     * encoded string.
     * 
     * @param text the encoded string of the data value.
     * @param type the type of the data value.
     * @return the parsed data value instance with proper class and the decoded value.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the encoded string is malformed.
     */
    static public DataValue parseValue(String text, DataType type) throws IOException, DataValueParseException {
        return parseValue(new DataValueReader(text), type);
    }

    /**
     * Parses the encoded string into a proper sub-class of <code>DataValue</code> which holds the value of the
     * encoded string. The encoded string can be accessed via the given reader.
     * 
     * @param reader the reader used to read the encoded string.
     * @param type the type of the data value.
     * @return the parsed data value instance with proper class and the decoded value.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the encoded string is malformed.
     */
    static public DataValue parseValue(DataValueReader reader, DataType type) throws IOException,
        DataValueParseException {
        // if it is an array, use array value to parse
        if (type.getDimension() > 0)
            return new ArrayValue(reader, type);

        // Otherwise, the sub-class is determined by the type's description
        String valuetype = (String) types.get(type.getDescription());

        if (valuetype == null)
            throw new DataValueParseException("Do not know how to handle data of type " + type.getDescription());
        try {
            // Get the sub-class of the data value according to the type
            Class c = Class.forName(valuetype);
            DataValue value = (DataValue) c.newInstance();

            // Parse it.
            value.parse(reader, type);
            return value;
        } catch (DataValueParseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataValueParseException("Error instantiating object of type " + valuetype + ": " + ex);
        }
    }

    /**
     * Parses an array of encoded strings into an array of proper sub-classes of <code>DataValue</code> which hold the
     * values of the encoded strings.
     * 
     * @param values the array of encoded strings of the data values.
     * @param types the array of types of the data values.
     * @return the array of parsed data value instances with proper classes and the decoded values.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if any encoded string is malformed.
     * @throws IllegalArgumentException if <code>values</code> and <code>types</code> have different number of
     *             elements.
     */
    static public DataValue[] parseValues(String[] values, DataType[] types) throws IOException,
        DataValueParseException {
        if (values.length != types.length) {
            throw new IllegalArgumentException("Values and types should have the same number of elements.");
        }

        DataValue[] dvs = new DataValue[values.length];
        for (int i = 0; i < values.length; i++)
            dvs[i] = parseValue(values[i], types[i]);
        return dvs;
    }

    /**
     * Parses the encoded string into a value object with proper type as given.
     * 
     * @param text the encoded string of the data value.
     * @param type the type of the data value.
     * @return the parsed value object.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the encoded string is malformed.
     */
    static public Object parseValueToObject(String text, DataType type) throws IOException, DataValueParseException {
        return convertDataValueToObject(parseValue(text, type), type);
    }

    /**
     * Parses an array of encoded strings into an array of value objects with proper types as given.
     * 
     * @param values the array of encoded strings of the data values.
     * @param types the array of types of the data values.
     * @return the array of parsed value objects.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the encoded string is malformed.
     */
    static public Object[] parseValuesToObjects(String[] values, DataType[] types) throws IOException,
        DataValueParseException {
        return convertDataValuesToObjects(parseValues(values, types), types);
    }

    /**
     * Parses the encoded string into an array of proper sub-classes of <code>DataValue</code> which hold the values
     * of the encoded string. Note <code>ArrayValue</code> can only parse list whose elements have the same type,
     * while this may parse list containing different types.
     * 
     * @param text the encoded string of the data values.
     * @param type the array of types of the data values.
     * @return the parsed data value instances with proper classes and the decoded values.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the encoded string is malformed.
     */
    static public DataValue[] parseSequence(String text, DataType[] type) throws IOException, DataValueParseException {
        return parseSequence(new DataValueReader(text), type);
    }

    /**
     * Parses the encoded string into an array of proper sub-classes of <code>DataValue</code> which hold the values
     * of the encoded string. The encoded string can be accessed via the given reader. Note <code>ArrayValue</code>
     * can only parse list whose elements have the same type, while this may parse list containing different types.
     * 
     * @param reader the reader used to read the encoded string.
     * @param type the array of types of the data values.
     * @return the parsed data value instances with proper classes and the decoded values.
     * @throws IOException if an I/O error occurs.
     * @throws DataValueParseException if the encoded string is malformed.
     */
    static public DataValue[] parseSequence(DataValueReader reader, DataType[] type) throws IOException,
        DataValueParseException {
        reader.expect('{', true);

        DataValue[] result = new DataValue[type.length];

        for (int i = 0; i < type.length; i++) {
            if (i > 0)
                reader.expect(',', true);
            result[i] = parseValue(reader, type[i]);
        }
        reader.expect('}', true);
        return result;
    }

    /**
     * Unwraps the value held by data value instance. The data value instance and the type of the value are given. The
     * value will be in proper Java type according to the given data type.
     * 
     * @param value the data value instance to be unwrapped.
     * @param type the type of the value.
     * @return the unwrapped value.
     * @throws DataValueParseException if the data value unwrapping is not supported or fails.
     */
    public static Object convertDataValueToObject(DataValue value, DataType type) throws DataValueParseException {
        String desc = type.getDescription();
        Object object = null;
        try {
            if (desc.equals("int") || desc.equals("Integer")) {
                object = new Integer((int) ((Long) value.getValue()).longValue());
            } else if (desc.equals("long") || desc.equals("Long")) {
                object = (Long) value.getValue();
            } else if (desc.equals("float") || desc.equals("Float")) {
                object = new Float(((Double) value.getValue()).floatValue());
            } else if (desc.equals("double") || desc.equals("Double")) {
                object = (Double) value.getValue();
            } else if (desc.equals("short") || desc.equals("Short")) {
                object = new Short((short) ((Long) value.getValue()).longValue());
            } else if (desc.equals("byte") || desc.equals("Byte")) {
                throw new DataValueParseException("byte and Byte not supported.");
            } else if (desc.equals("boolean") || desc.equals("Boolean")) {
                throw new DataValueParseException("boolean and Boolean not supported.");
            } else if (desc.equals("char") || desc.equals("Character")) {
                object = (Character) value.getValue();
            } else if (desc.equals("String")) {
                object = (String) value.getValue();
            } else if (desc.equals("ArrayList")) {
                throw new DataValueParseException("ArrayList not supported.");
            } else if (desc.equals("int[]")) {
                Object[] arrayO = (Object[]) value.getValue();
                int[] arrayI = new int[arrayO.length];
                for (int j = 0; j < arrayO.length; j++) {
                    arrayI[j] = (int) ((Long) ((DataValue) arrayO[j]).getValue()).longValue();
                }
                object = arrayI;
            } else if (desc.equals("long[]")) {
                Object[] arrayO = (Object[]) value.getValue();
                long[] arrayI = new long[arrayO.length];
                for (int j = 0; j < arrayO.length; ++j) {
                    arrayI[j] = ((Long) ((DataValue) arrayO[j]).getValue()).longValue();
                }
                object = arrayI;
            } else if (desc.equals("double[]")) {
                Object[] arrayO = (Object[]) value.getValue();
                double[] arrayI = new double[arrayO.length];
                for (int j = 0; j < arrayO.length; j++) {
                    arrayI[j] = ((Double) ((DataValue) arrayO[j]).getValue()).doubleValue();
                }
                object = arrayI;
            } else if (desc.equals("String[]")) {
                Object[] arrayO = (Object[]) value.getValue();
                String[] arrayS = new String[arrayO.length];
                for (int j = 0; j < arrayO.length; j++) {
                    arrayS[j] = (String) ((DataValue) arrayO[j]).getValue();
                }
                object = arrayS;
            }
        } catch (DataValueParseException de) {
            throw de;
        } catch (Exception e) {
            throw new DataValueParseException(e.toString());
        }
        return object;
    }

    /**
     * Unwraps the values held by data value instances. The array of data value instances and the array of types of the
     * values are given. The values will be in proper Java types according to the given data types.
     * 
     * @param values the data value instances to be unwrapped.
     * @param types the types of the values.
     * @return the unwrapped values.
     * @throws DataValueParseException if the data value unwrapping is not supported or fails.
     * @throws IllegalArgumentException if <code>values</code> and <code>types</code> have different number of
     *             elements.
     */
    public static Object[] convertDataValuesToObjects(DataValue[] values, DataType[] types)
        throws DataValueParseException {
        if (values.length != types.length) {
            throw new IllegalArgumentException("Values and types should have the same number of elements.");
        }

        Object[] objects = new Object[values.length];

        try {
            for (int i = 0; i < objects.length; i++) {
                objects[i] = convertDataValueToObject(values[i], types[i]);
            }
        } catch (DataValueParseException de) {
            throw de;
        } catch (Exception e) {
            throw new DataValueParseException(e.toString());
        }
        return objects;
    }

    /**
     * Wraps the value into a data value instance. The Java type value and the type of the value are given.
     * 
     * @param obj the Java type value to be wrapped.
     * @param type the type of the value.
     * @return the wrapped data value instance.
     * @throws DataValueParseException if the Java type value wrapping is not supported or fails.
     */
    public static DataValue convertObjectToDataValue(Object obj, DataType type) throws DataValueParseException {
        DataValue dataValue = null;
        try {
            String desc = type.getDescription();
            if (desc.equals("int") || desc.equals("Integer")) {
                dataValue = new IntegralValue(((Integer) obj).longValue());
            } else if (desc.equals("long") || desc.equals("Long")) {
                dataValue = new IntegralValue(((Long) obj).longValue());
            } else if (desc.equals("float") || desc.equals("Float")) {
                dataValue = new DecimalValue(((Float) obj).doubleValue());
            } else if (desc.equals("double") || desc.equals("Double")) {
                dataValue = new DecimalValue(((Double) obj).doubleValue());
            } else if (desc.equals("short") || desc.equals("Short")) {
                dataValue = new IntegralValue(((Long) obj).longValue());
            } else if (desc.equals("byte") || desc.equals("Byte")) {
                throw new DataValueParseException("byte and Byte not supported.");
            } else if (desc.equals("boolean") || desc.equals("Boolean")) {
                throw new DataValueParseException("boolean and Boolean not supported.");
            } else if (desc.equals("char") || desc.equals("Character")) {
                dataValue = new CharacterValue(((Character) obj).charValue());
            } else if (desc.equals("String")) {
                dataValue = new StringValue((String) obj);
            } else if (desc.equals("ArrayList")) {
                throw new DataValueParseException("ArrayList not supported.");
            } else if (desc.equals("int[]")) {
                ArrayList values = new ArrayList();
                int[] ints = (int[]) obj;
                for (int i = 0; i < ints.length; i++)
                    values.add(new IntegralValue((long) ints[i]));
                dataValue = new ArrayValue(values);
            } else if (desc.equals("long[]")) {
                ArrayList values = new ArrayList();
                long[] longs = (long[]) obj;
                for (int i = 0; i < longs.length; i++)
                    values.add(new IntegralValue(longs[i]));
                dataValue = new ArrayValue(values);
            } else if (desc.equals("double[]")) {
                ArrayList values = new ArrayList();
                double[] nums = (double[]) obj;
                for (int i = 0; i < nums.length; i++)
                    values.add(new DecimalValue(nums[i]));
                dataValue = new ArrayValue(values);
            } else if (desc.equals("String[]")) {
                ArrayList values = new ArrayList();
                String[] strings = (String[]) obj;
                for (int i = 0; i < strings.length; i++)
                    values.add(new StringValue(strings[i]));
                dataValue = new ArrayValue(values);
            }
        } catch (ClassCastException e) {
            throw new DataValueParseException("Error converting Object to DataValue. " + "Expected "
                + type.getDescription() + ", got " + e.getMessage());
        }

        return dataValue;
    }

    /**
     * Wraps the values into data value instances. The Java type values and the types of the values are given.
     * 
     * @param objs the Java type values to be wrapped.
     * @param types the types of the values.
     * @return the wrapped data value instances.
     * @throws DataValueParseException if the Java type value wrapping is not supported or fails.
     * @throws IllegalArgumentException if <code>objs</code> and <code>types</code> have different number of
     *             elements.
     */
    public static DataValue[] convertObjectsToDataValues(Object[] objs, DataType[] types)
        throws DataValueParseException {
        if (objs.length != types.length) {
            throw new IllegalArgumentException("Objects and types should have the same number of elements.");
        }

        DataValue[] values = new DataValue[objs.length];
        for (int i = 0; i < values.length; i++)
            values[i] = convertObjectToDataValue(objs[i], types[i]);
        return values;
    }

    /**
     * Creates the mapping of type names and the element classes to hold the values of the type.
     * 
     * @return the mapping of type names and the element classes to hold the values of the type.
     */
    static private HashMap buildTypes() {
        HashMap types = new HashMap();

        types.put("char", "com.topcoder.shared.problem.CharacterValue");
        types.put("Character", "com.topcoder.shared.problem.CharacterValue");
        types.put("int", "com.topcoder.shared.problem.IntegralValue");
        types.put("Integer", "com.topcoder.shared.problem.IntegralValue");
        types.put("long", "com.topcoder.shared.problem.IntegralValue");
        types.put("Long", "com.topcoder.shared.problem.IntegralValue");
        types.put("short", "com.topcoder.shared.problem.IntegralValue");
        types.put("Short", "com.topcoder.shared.problem.IntegralValue");
        types.put("double", "com.topcoder.shared.problem.DecimalValue");
        types.put("Double", "com.topcoder.shared.problem.DecimalValue");
        types.put("float", "com.topcoder.shared.problem.DecimalValue");
        types.put("Float", "com.topcoder.shared.problem.DecimalValue");
        types.put("String", "com.topcoder.shared.problem.StringValue");
        return types;
    }
}
