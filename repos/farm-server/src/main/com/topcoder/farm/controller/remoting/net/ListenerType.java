/*
 * ListernerType
 * 
 * Created 07/26/2006
 */
package com.topcoder.farm.controller.remoting.net;

import com.topcoder.farm.shared.enumeration.EnumType;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ListenerType extends EnumType {
    public static final ListenerType NBIOListenerType = new ListenerType(1, "NBIOListener");

    protected ListenerType(int id, String display) {
        super(id, display);
    }

    public static ListenerType getInstance(int id) {
        switch (id) {
            case 1: return NBIOListenerType; 
        }
        throw new IllegalArgumentException("Invalid id for enum type " + ListenerType.class);
    }
    
    protected Object resolveId(int id) {
        return getInstance(id);
        
    }
}
