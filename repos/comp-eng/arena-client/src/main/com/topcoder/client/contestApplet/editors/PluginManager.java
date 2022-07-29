package com.topcoder.client.contestApplet.editors;

/**
 * PluginManager
 *
 * Description:		This class manages the plugins - this is a singleton
 *                  pattern since the plugins are specific to the 
 *                  application (rather than specific to a contest)
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

import com.topcoder.client.contestApplet.common.*;
import java.util.*;

public class PluginManager {

	/** The system property to turn on the NoPluginManager */
	public static final String NOPLUGINPROPERTY = "com.topcoder.client.contestApplet.editors.NoPluginManager";
	
    /** The local preferences */
    private LocalPreferences pref = LocalPreferences.getInstance();
    
    /** Editor plugins by name */
    private HashMap plugins = new HashMap();
    
    /** Singleton instance of the plugin manager */
    private static PluginManager pluginManager = null;
    
    /** Cache of instantiated editors */
    private HashMap cache = new HashMap();

    /** Constructs the plugin manager */
    private PluginManager() {
        // Add the standard/local plugins
        addPlugins(StandardPlugins.getInstance().getPlugins());
        addPlugins(pref.getPlugins());        
    }

    /** Attempt to notify editors they are no longer in use */
    public void finalize() {
        // Loop through the editors disposing of each...
        for(Iterator itr=cache.values().iterator();itr.hasNext();) {
            ((DynamicEditor)itr.next()).dispose();
            itr.remove();
        }
    }
    
    /** Helper method to add the collection of EditorPlugins */
    protected void addPlugins(Collection col) {
        for(Iterator itr = col.iterator();itr.hasNext();) {
            EditorPlugin plugin = (EditorPlugin)itr.next();
            plugins.put(plugin.getName(), plugin);
        }
    }
        
    /** Get the singleton instance */
    public synchronized static PluginManager getInstance() {
        /** Decide which plugin manager to run */
        if(pluginManager==null) {
        	if(System.getProperty(PluginManager.NOPLUGINPROPERTY)!=null) {
        		pluginManager = new NoPluginManager();
        	} else {
        		pluginManager = new PluginManager();
        	}
        }
        
        return pluginManager;
    }
    
    /** Returns all the plugins currently available */
    public synchronized EditorPlugin[] getEditorPlugins() {
        EditorPlugin[] temp = (EditorPlugin[])plugins.values().toArray(new EditorPlugin[0]);
        for(int x=temp.length-1;x>=0;x--) temp[x] = (EditorPlugin)temp[x].clone();
        return temp;
    }
    
    /** Sets the plugins that are available */
    public synchronized void setEditorPlugins(EditorPlugin[] newPlugins) throws java.io.IOException {
        // Get a copy of the the existing keys
        HashSet existSet = new HashSet(plugins.keySet());
        ArrayList savePlugins = new ArrayList();
        
        // Loop through the new stuff
        for(int x=newPlugins.length-1;x>=0;x--) {

            // Remove it from the cache and dispose of it
            DynamicEditor edit = (DynamicEditor)cache.remove(newPlugins[x].getName());
            if(edit!=null) edit.dispose();

            // Remove it from our existing set
            existSet.remove(newPlugins[x].getName());        

            // If it doesn't exist - call it's install method
            EditorPlugin existing = (EditorPlugin)plugins.get(newPlugins[x].getName());
            if(existing==null) {
                DynamicEditor editor = createEditor(newPlugins[x]);
                editor.install();
                editor.dispose();
            }
            
            // Put it to our list (or overwrite it if it exists)
            plugins.put(newPlugins[x].getName(), newPlugins[x]);

            // If not standard - add to the save stuff            
            if(newPlugins[x].getType()!=EditorPlugin.STANDARD) savePlugins.add(newPlugins[x]);
        }
        
        // Anything left over was deleted
        for(Iterator itr=existSet.iterator();itr.hasNext();) {
            // Get the definition to delete
            EditorPlugin temp = (EditorPlugin)plugins.remove(itr.next());
            if(temp==null) continue;
            
            // Remove it from the cache
            DynamicEditor edit = (DynamicEditor)cache.remove(temp.getName());
            
            // If not found - create one
            if(edit==null) edit = createEditor(temp);

            // Now uninstall it
            edit.uninstall();

            // Dispose of it
            edit.dispose();
        }


        // Set and save
        pref.setPlugins(savePlugins);
        pref.savePreferences();
                    
    }
    
    /** Convience method */
    public synchronized DynamicEditor getEditor(EditorPlugin plugin) throws InstantiationError, NoSuchMethodError {
        return getEditor(plugin.getName());
    }
    
    /** Instantiates and returns a plugin editor */
    public synchronized DynamicEditor getEditor(String name) throws InstantiationError, NoSuchMethodError {
    
        // First lookup in the cache
        DynamicEditor editor = (DynamicEditor)cache.remove(name);
        
        // If not found - instantiate it
        if(editor==null) editor = createEditor(name);
        
        // Notify the editor it's about to be used
        editor.startUsing();
        
        // Return the editor
        return editor;
    }

    /** Disposes (or puts into the cache) the editor */
    public synchronized void disposeEditor(DynamicEditor editor) {

        // Notify editor we are no longer using it
        editor.stopUsing();

        // Get the definition
        EditorPlugin temp = (EditorPlugin)plugins.get(editor.getPlugin().getName());
        
        // Determine if the definition has changed
        boolean changed = (temp==null ? true : !temp.equals(editor.getPlugin()));
        
        // If it hasn't changed and the editor is cacheable
        if(!changed && editor.isCacheable()) {
            // Put it into the cache
            cache.put(editor.getPlugin().getName(), editor);
        } else {
            // Notify editor we are disposing it
            editor.dispose();
        }
    }
    
    /** Get the default editor name */
    public String getDefaultEditorName() {
        String r = LocalPreferences.getInstance().getDefaultEditorName();
        if(r==null || r.equals("") || plugins.get(r)==null) r = "Standard";
        return r;
    }
    
    /** Creates the editor */
    public synchronized DynamicEditor createEditor(String name) throws InstantiationError, NoSuchMethodError {

        // Find the plugin           
        EditorPlugin plugin = (EditorPlugin)plugins.get(name);
        if(plugin==null) throw new InstantiationError("Unknown plugin name: " + name);

        // Return the creation 
        return createEditor(plugin);
    }    

    /** Creates the editor */
    public DynamicEditor createEditor(EditorPlugin plugin) throws InstantiationError, NoSuchMethodError {

        // Create the editor and execute the setName() method
        DynamicEditor editor = new DynamicEditor(plugin);
        editor.setName(plugin.getName());
        
        // Return it
        return editor;
    }    
    
    /** Class to disallow all plugins */
    private static class NoPluginManager extends PluginManager {
	    /** Adds ONLY the StandardPlugins */
	    private NoPluginManager() {
	        // Add the standard/local plugins
	        super.addPlugins(StandardPlugins.getInstance().getPlugins());
	    }

  	    /** overridden to disallow plugins */
    	protected void addPlugins(Collection col) {
	
		}
	    
	    /** Overridden to disallow plugins */
	    public synchronized void setEditorPlugins(EditorPlugin[] newPlugins) throws java.io.IOException {
	    	throw new java.io.IOException("Plugins have been disabled");
	    }

    }
}
