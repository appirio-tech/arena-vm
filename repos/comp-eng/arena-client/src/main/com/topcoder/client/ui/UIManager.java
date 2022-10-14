package com.topcoder.client.ui;

/**
 * Defines an interface of UI manager which is used to manage a set of UI pages. It contains a list of UI pages with
 * unique names. It also has a name and a description. Normally, a UI manager represents a UI complete theme of an
 * application. It is created by UIFactory.
 *
 * @version 1.0
 * @author visualage
 */
public interface UIManager {
    /**
     * Destroys the UI manager. It also destroys all the UI pages.
     */
    void destroy();
    /**
     * Creates the UI pages and components in the manager.
     * @throws UIManagerConfigurationException if the manager configuration is invalid.
     */
    void create() throws UIManagerConfigurationException;
    /**
     * Gets a UI page without re-creation. The name of the UI page is given. It is the same as
     * <code>getUIPage(name, false)</code>.
     * @param name the name of the UI page.
     * @throws UIPageNotFoundException the name of the UI page does not exist.
     * @return the UI page retrieved according to the name.
     */
    UIPage getUIPage(String name) throws UIPageNotFoundException;
    /**
     * Gets a UI page with re-creation option. When <code>recreate</code> is <code>true</code>, the existing UI page
     * is destroyed first, and then re-created. Otherwise, the existing UI page is returned. The name of the UI page
     * is given.
     * @param name the name of the UI page.
     * @param recreate <code>true</code> if the UI page need to be destroyed and re-created; <code>false</code> otherwise.
     * @throws UIPageNotFoundException the name of the UI page does not exist.
     * @return the UI page retrieved according to the name.
     */
    UIPage getUIPage(String name, boolean recreate) throws UIPageNotFoundException;
    /**
     * Gets the name of the UI manager.
     * @return the name of the UI manager.
     */
    String getName();
    /**
     * Gets the description of the UI manager.
     * @return the description of the UI manager.
     */
    String getDescription();
}
