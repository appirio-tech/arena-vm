/*
 * EndPointIdentification
 * 
 * Created 06/26/2006
 */
package com.topcoder.farm.shared.id;

import java.io.Serializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class EndPointIdentification implements Serializable {
    private EndPointType type;
    private String id;

    public EndPointIdentification(EndPointType type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public EndPointType getType() {
        return type;
    }
    
    public void setType(EndPointType type) {
        this.type = type;
    }

    public int hashCode() {
        return type.hashCode() << 2 ^ id.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj == null) return false;
        EndPointIdentification o = (EndPointIdentification) obj;
        return type.equals(o.type) && id.equals(o.id);
    }
    
    public String toString() {
        return "[" + type.toString() + "," + id+ "]";
    }
}
