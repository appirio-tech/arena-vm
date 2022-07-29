package com.topcoder.client.ui;

import java.util.List;

/**
 * Defines an interfaceof UI page which contains UI components logically belongs to a group. Usually, a UI page
 * corresponds to a Java UI frame. However, it has the capability to contain several Java UI frames. The UI pages
 * should be created by UI managers. It should always has a default constructor with no argument. Each component
 * in a page must have a name. However, a name might be shared by multiple components.
 * 
 * @version 1.0
 * @author visualage
 * @see com.topcoder.client.ui.UIComponent
 * @see com.topcoder.client.ui.UIManager
 */
public interface UIPage {
    /**
     * Destroys the UI page. It should also destroy all contained UI components.
     */
    void destroy();
    /**
     * Gets all UI components associated with the given name. The components are ensured to be created.
     * @param name the name of the UI components to be retrieved.
     * @throws UIComponentNotFoundException the name cannot be found in the UI page.
     * @return a list of all UI components associated with the given name.
     */
    List getAllComponents(String name) throws UIComponentNotFoundException;
    /**
     * Gets one UI component associated with the given name. If there are multiple components sharing the same name,
     * the first UI component is returned. The component is ensured to be created.
     * @param name the name of the UI component to be retrieved.
     * @throws UIComponentNotFoundException the name cannot be found in the UI page.
     * @return one UI component associated with the given name.
     */
    UIComponent getComponent(String name) throws UIComponentNotFoundException;
    /**
     * Gets all UI components associated with the given name.
     * @param name the name of the UI components to be retrieved.
     * @param create <code>true</code> if the components should be created.
     * @throws UIComponentNotFoundException the name cannot be found in the UI page.
     * @return a list of all UI components associated with the given name.
     */
    List getAllComponents(String name, boolean create) throws UIComponentNotFoundException;
    /**
     * Gets one UI component associated with the given name. If there are multiple components sharing the same name,
     * the first UI component is returned.
     * @param name the name of the UI component to be retrieved.
     * @param create <code>true</code> if the components should be created.
     * @throws UIComponentNotFoundException the name cannot be found in the UI page.
     * @return one UI component associated with the given name.
     */
    UIComponent getComponent(String name, boolean create) throws UIComponentNotFoundException;
    /**
     * Adds a UI component to the UI page. The name associated with the component is also given.
     * @param name the name of the UI component to be added.
     * @param component the UI component to be added.
     * @throws UIPageException adding the component to the page fails.
     */
    void addComponent(String name, UIComponent component) throws UIPageException;
}
