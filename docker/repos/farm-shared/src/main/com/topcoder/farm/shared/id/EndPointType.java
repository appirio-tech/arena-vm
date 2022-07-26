/*
 * EndPointType
 * 
 * Created 06/22/2006
 */
package com.topcoder.farm.shared.id;

import com.topcoder.farm.shared.enumeration.EnumType;


/**
 * Type of EndPoints existing in the Farm 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class EndPointType extends EnumType {
    public static final EndPointType CONTROLLER = new EndPointType(1, "CONTROLLER");
    public static final EndPointType PROCESSOR = new EndPointType(2, "PROCESSOR");
    public static final EndPointType CLIENT = new EndPointType(3, "CLIENT");

    /**
     * Creates a new EndPointType
     * 
     * @param id EndPointType id 
     * @param display EndPointType display value
     */
    private EndPointType(int id, String display) {
        super(id, display);
    }

    /**
     * Returns the unique EndPointType instance with the give Id 
     * 
     * @param id Id of the instance
     * 
     * @return the unique instance
     */
    public static EndPointType getInstance(int id) {
        switch (id) {
            case 1: return CONTROLLER;
            case 2: return PROCESSOR;
            case 3: return CLIENT;
        }
        throw new IllegalArgumentException("Invalid id for enum type " + EndPointType.class);
    }

    /**
     * @see com.topcoder.farm.shared.enumeration.EnumType#resolveId(int)
     */
    protected Object resolveId(int id) {
       return getInstance(id);
    }
}
