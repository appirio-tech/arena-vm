package com.topcoder.client.ui.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentNotFoundException;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPageException;

/**
 * Defines a default implementation of the UIPage interface. It simply contains a map from the name to a list of
 * components. There is no limitation to add components. Derived classes may have restrictions such as not allowing
 * more than one component for certain component names.
 *
 * @version 1.0
 * @author visualage
 */
public class UIPageImpl implements UIPage {
    /** Represents the map from the name to a list of components.*/
    protected Map components = new HashMap();
    protected Set created = new HashSet();
    
    /**
     * Creates a new instance of UIPageImpl class.
     */
    public UIPageImpl() {
    }
    
    public void addComponent(String name, UIComponent component) throws UIPageException {
        if (!components.containsKey(name)) {
            components.put(name, new ArrayList());
        }
        
        ((List) components.get(name)).add(component);
    }

    public void destroy() {
        for (Iterator iter = components.values().iterator(); iter.hasNext(); ) {
            List list = (List) iter.next();
            
            for (Iterator listIter = list.iterator(); listIter.hasNext(); ) {
                ((UIComponent) listIter.next()).destroy();
            }
        }
    }

    public UIComponent getComponent(String name) throws UIComponentNotFoundException {
        return getComponent(name, true);
    }

    public List getAllComponents(String name) throws UIComponentNotFoundException {
        return getAllComponents(name, true);
    }
    
    public UIComponent getComponent(String name, boolean create) throws UIComponentNotFoundException {
        return (UIComponent) getAllComponents(name, create).get(0);
    }

    public List getAllComponents(String name, boolean create) throws UIComponentNotFoundException {
        if (components.containsKey(name)) {
            if (!created.contains(name) && create) {
                for (Iterator iter = ((List) components.get(name)).iterator(); iter.hasNext();) {
                    UIComponent component = (UIComponent) iter.next();
                    component.create();
                }

                created.add(name);
            }

            return (List) components.get(name);
        }
        
        throw new UIComponentNotFoundException("The component " + name + " is not available.");
    }
}
