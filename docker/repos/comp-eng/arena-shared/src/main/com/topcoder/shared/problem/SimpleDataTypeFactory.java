package com.topcoder.shared.problem;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * This class implements a global database of known data types.  Ideally it would be populated
 * at some appropriate initialization time with the set of valid data types.  Construction of
 * any new <code>DataType</code> adds to the data type population.
 *
 * @author Logan Hanks
 * @see DataType
 * @version $Id: SimpleDataTypeFactory.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class SimpleDataTypeFactory {
    /** Represents the mapping between type descriptions/IDs and data types. */
    static protected Map<String, DataType> types = new HashMap<String, DataType>();
    
    /** Represents a flag indicating if the data type factory has been initialized */
    static protected boolean initialized = false;
    
    /**
     * Initializes the data type factory. The data types are retrieved from DB.
     */
    static public void initialize() {
        if(initialized)
            return;
        //Ugly patch to solve cross dependencies.
        try {
            Method method = Class.forName("com.topcoder.shared.problem.DataTypeFactory").getDeclaredMethod("initializeFromDB", new Class[] {});
            method.setAccessible(true);
            method.invoke(null, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Obtain a <code>DataType</code> object with the given description. A copy is returned.
     *
     * @param description A type description (e.g. <code>"String[]"</code>)
     * @return the data type matching the given description.
     * @throws InvalidTypeException if there is no data type matching the description.
     */
    static public DataType getDataType(String description)
            throws InvalidTypeException {
        SimpleDataTypeFactory.initialize();
        
        DataType type = (DataType) types.get(description);

        if (type == null)
            throw new InvalidTypeException(description);
        return type.cloneDataType();
    }

    /**
     * Obtain a <code>DataType</code> object with the given unique ID. A copy is returned.
     *
     * @param typeID the unique ID of the data type.
     * @return the data type with the given unique ID.
     * @throws InvalidTypeException if there is no data type matching the unique ID.
     */
    static public DataType getDataType(int typeID)
            throws InvalidTypeException {
        SimpleDataTypeFactory.initialize();
        
        DataType type = (DataType) types.get(Integer.toString(typeID));

        if (type == null)
            throw new InvalidTypeException("" + typeID);
        return type.cloneDataType();
    }

    /**
     * Registers the given data type to this factory.
     * 
     * @param type the data type to be registered.
     */
    static void registerDataType(DataType type) {
        initialized = true;
        if (types.containsKey(type.getDescription()))
            return;
        types.put(type.getDescription(), type.cloneDataType());
        types.put(Integer.toString(type.getID()), type.cloneDataType());
    }

    /**
     * Gets all registered data types. There is no copy.
     * 
     * @return a collection of all registered data types.
     */
    static public Collection<DataType> getDataTypes() {
        SimpleDataTypeFactory.initialize();
        
        return types.values();
    }
}
