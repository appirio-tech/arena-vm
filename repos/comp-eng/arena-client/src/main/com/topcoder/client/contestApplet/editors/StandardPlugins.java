/**
 * StandardPlugins.java
 *
 * Description:		Class containing the standard editor plugins as defined by TopCoder Servers
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors;

import java.util.HashMap;
import java.util.Collection;

public final class StandardPlugins {

    private static StandardPlugins globalPlugin = null;
    private HashMap editorList = new HashMap();

    public static final String STANDARD = "Standard";

    private StandardPlugins() {
        editorList.put(STANDARD, new EditorPlugin(STANDARD, "com.topcoder.client.contestApplet.editors.Standard.EntryPoint"));
    }

    public final static synchronized StandardPlugins getInstance() {
        // Has the instance already been created - if so, return it
        if (globalPlugin != null) return globalPlugin;

        // Create an instance and then return it
        globalPlugin = new StandardPlugins();
        return globalPlugin;
    }

    public final Collection getPlugins() {
        return editorList.values();
    }

    public final EditorPlugin getPlugin(String pluginName) {
        return (EditorPlugin) editorList.get(pluginName);
    }


}


/* @(#)StandardPlugins.java */
