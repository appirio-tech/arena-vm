package com.topcoder.client.ui;

import java.awt.GridBagConstraints;

/**
 * Defines an interface of a UI component in a UI page of the TopCoder UI Design framework.
 * Each UI component actually corresponds to a Java UI component. Usually, this interface should
 * be implemented based on existing Java UI component. Every implementation should always contains
 * a constructor with an argument of <code>Object</code>. Other settings should be able to be done
 * via <code>setProperty</code> method.
 * 
 * @version 1.0
 * @author visualage
 */
public interface UIComponent {
    /**
     * Creates this component.
     */
    void create();
    /**
     * Destroys this component.
     */
    void destroy();
    /**
     * Adds a child to this component, using the given GridBagConstraints. If the constraints is not needed,
     * the GridBagConstraints can be ignored.
     * @param component the UI component to be added as the child.
     * @param constraints the layout constraints used by the added component.
     * @throws UIComponentException if this component is not a container.
     */
    void addChild(UIComponent component, GridBagConstraints constraints) throws UIComponentException;
    /**
     * Sets a property of the component.
     * @param name the name of the property to be set.
     * @param value the value of the property to be set.
     * @throws UIComponentException if the property does not exist, is readonly, or the value type is invalid;
     * or any other error occured when setting the property.
     */
    void setProperty(String name, Object value) throws UIComponentException;
    /**
     * Gets a property value of the component.
     * @param name the name of the property to be retrieved.
     * @return the value of the property.
     * @throws UIComponentException if the property does not exist, or is writeonly; or any other error occured
     * when reading the property.
     */
    Object getProperty(String name) throws UIComponentException;
    /** 
     * Adds an event listener to the component.
     * @param name the event to be listened.
     * @param listener the event listener to be added.
     * @throws UIComponentException if the event does not exist; or the listener type is invalid.
     * @throws IllegalArgumentException if the listener is <code>null</code>.
     */
    void addEventListener(String name, UIEventListener listener) throws UIComponentException;
    /**
     * Removes an event listener from the component.
     * @param name the event whose listener is removed.
     * @param listener the event listener to be removed.
     * @throws UIComponentException if the event does not exist; or the listener type is invalid.
     * @throws IllegalArgumentException if the listener is <code>null</code>.
     */
    void removeEventListener(String name, UIEventListener listener) throws UIComponentException;
    /**
     * Performs an action of the component. There is no argument needed for this action.
     * @param name the action to be performed.
     * @throws UIComponentException if the action does not exist; or any other error occured when performing the action.
     * @return The result of the action. It can be <code>null</code>.
     */
    Object performAction(String name) throws UIComponentException;

    /**
     * Performs an action of the component. The arguments are given.
     * @param name the action to be performed.
     * @param args the arguments of the action.
     * @throws UIComponentException if the action does not exist; or the argument is invalid; or any other error
     * occured when performing the action.
     * @return The result of the action. It can be <code>null</code>.
     */
    Object performAction(String name, Object[] args) throws UIComponentException;

    /** 
     * Gets the event source object if the UI component triggers an event. It might be null if the component
     * will never trigger an event.
     * @return The event source object of this UI component.
     */
    Object getEventSource();

    /**
     * Gets the parent of this component. If the component is top-level, <code>null</code> is returned.
     * @return The parent of this component.
     */
    UIComponent getParent();

    /**
     * Sets the parent of this component.
     * @param parent the parent of this component.
     */
    void setParent(UIComponent parent);
}
