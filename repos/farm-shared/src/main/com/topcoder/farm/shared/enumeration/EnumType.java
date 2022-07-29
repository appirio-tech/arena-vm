/*
 * EnumType
 * 
 * Created 07/10/2006
 */
package com.topcoder.farm.shared.enumeration;

import java.io.InvalidObjectException;
import java.io.Serializable;

/**
 * Base class for enumerate types.
 * Java 1.4 compatible.
 * 
 * Enum types extending this class must implement the resolveId method
 * and provide a way to obtain instances of the Type.
 * If two instances of a class extending this one are <code>equal</code> 
 * then they must be the same instance.
 * 
 *  Subclasses of this class must not create more than one instance for each
 *  id of the EnumType.
 *  
 *  This class maintains uniqueness during serialization.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class EnumType implements Serializable, Comparable {
    private int id;
    private transient String display;
    
    
    /**
     * Implementators of this method must return the unique instance
     * of the EnumType with id equals to <code>id</code> 
     * 
     * @param id Id of the EnumType
     * 
     * @return The unique instance of the EnumType with id equals <code>id</code>
     */
    protected abstract Object resolveId(int id);
    
    
    @SuppressWarnings("unused")
    private EnumType() {
    }
    
    /**
     * Creates a new instance with the specified id,
     * and display equals to String.valueOf(id)
     * 
     * @param id Id of the EnumType
     */
    protected EnumType(int id) {
        this.id = id;
        this.display = String.valueOf(id);
    }
    
    
    /**
     * Creates a new instance with the specified id 
     * and display values
     * 
     * @param id Id of the EnumType
     * @param display Value to display
     */
    protected EnumType(int id, String display) {
        this.id = id;
        this.display = display;
    }

    /**
     * @return The display value
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @return The id of the EnumType
     */
    public int getId() {
        return id;
    }
    
    public String toString() {
        return display;
    }
    
    public int hashCode() {
        return id;
    }

    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass()) && this.id == ((EnumType) obj).id;
    }

    public int compareTo(Object o) {
        if (!getClass().equals(o.getClass())) {
            throw new ClassCastException("Illegal object in compare method: " + o.getClass());
        }
        return id - ((EnumType) o).id;
    }

    /**
     * This method provides uniquessness during serialization
     */
    private Object readResolve () throws java.io.ObjectStreamException {
        try {
            return resolveId(id);
        } catch (RuntimeException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }
}
