/*
 * ControllerData
 * 
 * Created 07/27/2006
 */
package com.topcoder.farm.controller.model;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerData {
    /**
     * The database primary key for this ControllerData
     */
    private Long id;
    
    /**
     * The name of the controller, it is actually an external identifier
     */
    private String name;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
