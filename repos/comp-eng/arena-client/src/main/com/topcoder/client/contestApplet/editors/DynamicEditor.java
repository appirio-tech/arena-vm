package com.topcoder.client.contestApplet.editors;

/**
 * DynamicEditor.java
 *
 * Description:		This class is a specialized proxy for the real plugin class
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

import javax.swing.JPanel;
import java.util.*;
import java.lang.reflect.Method;
import java.net.*;

import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.shared.problem.Renderer;
import com.topcoder.shared.language.Language;

public class DynamicEditor {

    // Static vars....
    private static boolean debug = LocalPreferences.getInstance().isEditorDebug();

    // Editor Object and it's methods
    private EditorPlugin plugin;
    private Object editor = null;
    private HashMap methodCache;

    // Functions Constants
    private final static String CONFIGURE = "configure";
    private final static String CLEAR = "clear";
    private final static String GETEDITORPANEL = "getEditorPanel";
    private final static String GETSOURCE = "getSource";
    private final static String SETCOMPILERESULTS = "setCompileResults";
    private final static String SETTEXTENABLED = "setTextEnabled";
    private final static String SETLANGUAGE = "setLanguage";
    private final static String SETSOURCE = "setSource";
    private final static String SETPROBLEM = "setProblem";
    private final static String SETSIGNATURE = "setSignature";
    private final static String SETPROBLEMCOMPONENT = "setProblemComponent";

    private final static String SETNAME = "setName";
    private final static String ISCACHEABLE = "isCacheable";
    private final static String STARTUSING = "startUsing";
    private final static String STOPUSING = "stopUsing";
    private final static String DISPOSE = "dispose";
    private final static String INSTALL = "install";
    private final static String UNINSTALL = "uninstall";
    //deprecated
    //private final static String SETCONTESTAPPLET = "setContestApplet";

    /** Create the editor */
    public DynamicEditor(EditorPlugin plugin) throws InstantiationError, NoSuchMethodError {

        if (plugin == null) throw instantiationError("Null Plugin", "Null Plugin");

        this.plugin = plugin;
        
        // Write a debug message about instantiating
        if (debug) System.out.println("Trying to instantiate " + plugin.getName());

        // URL Cache
        ArrayList urlCache = new ArrayList();
        
        // Get the global path
        String globalPath = LocalPreferences.getInstance().getPluginCommonPath();
        if(globalPath==null) globalPath="";
        
        // Add the URL's to the class loader (if they don't already exist)
        addURLs(urlCache, globalPath);
        addURLs(urlCache, plugin.getClassPath());
        
        // Create a new class loader
        URL[] classPath = (URL[])urlCache.toArray(new URL[0]);

        Class pluginClass;

        // Try to load from our custom loader
        // (If a problem occurs, load from the existing loader)
        try {
            CustomLoader classLoader = new CustomLoader(classPath);

            // Load the class for the entry point
            try {
                pluginClass = classLoader.loadClass(plugin.getEntryPoint());
            } catch (ClassNotFoundException e) {
                throw instantiationError(plugin.getName(), e.toString());
            }
        } catch (Throwable t) {
            try {
                pluginClass = Class.forName(plugin.getEntryPoint());
            } catch (ClassNotFoundException e) {
                throw instantiationError(plugin.getName(), e.toString());
            }
        }


        // Store all the methodCache into the cache
        methodCache = new HashMap();
        methodCache.put(CONFIGURE, getMethod(pluginClass, CONFIGURE, null));
        methodCache.put(CLEAR, getMethod(pluginClass, CLEAR, null));
        methodCache.put(GETEDITORPANEL, getMethod(pluginClass, GETEDITORPANEL, null));
        methodCache.put(GETSOURCE, getMethod(pluginClass, GETSOURCE, null));
        methodCache.put(SETCOMPILERESULTS, getMethod(pluginClass, SETCOMPILERESULTS, new Class[]{Boolean.class, String.class}));
        methodCache.put(SETTEXTENABLED, getMethod(pluginClass, SETTEXTENABLED, new Class[]{Boolean.class}));
        methodCache.put(SETLANGUAGE, getMethod(pluginClass, SETLANGUAGE, new Class[]{Integer.class}));
        methodCache.put(SETSOURCE, getMethod(pluginClass, SETSOURCE, new Class[]{String.class}));
        methodCache.put(SETPROBLEM, getMethod(pluginClass, SETPROBLEM, new Class[]{String.class}));
        methodCache.put(SETPROBLEMCOMPONENT, getMethod(pluginClass, SETPROBLEMCOMPONENT, new Class[]{ProblemComponentModel.class, Language.class, Renderer.class}));
        methodCache.put(SETSIGNATURE, getMethod(pluginClass, SETSIGNATURE, new Class[]{String.class, String.class, List.class, String.class}));
        methodCache.put(SETNAME, getMethod(pluginClass, SETNAME, new Class[]{String.class}));
        methodCache.put(ISCACHEABLE, getMethod(pluginClass, ISCACHEABLE, null));
        methodCache.put(STARTUSING, getMethod(pluginClass, STARTUSING, null));
        methodCache.put(STOPUSING, getMethod(pluginClass, STOPUSING, null));
        methodCache.put(DISPOSE, getMethod(pluginClass, DISPOSE, null));
        methodCache.put(INSTALL, getMethod(pluginClass, INSTALL, null));
        methodCache.put(UNINSTALL, getMethod(pluginClass, UNINSTALL, null));
        //methodCache.put(SETCONTESTAPPLET, getMethod(pluginClass, SETCONTESTAPPLET, new Class[]{ContestApplet.class}));

        // Verify that the REQUIRED methods were present
        if (methodCache.get(SETSOURCE) == null) {
            throw noSuchMethod(SETSOURCE);
        }
        if (methodCache.get(GETSOURCE) == null) {
            throw noSuchMethod(GETSOURCE);
        }
        if (methodCache.get(GETEDITORPANEL) == null) {
            throw noSuchMethod(GETEDITORPANEL);
        }


        // Create the editor object
        try {
            editor = pluginClass.newInstance();
        } catch (IllegalAccessException e) {
            throw instantiationError(plugin.getEntryPoint(), e.toString());
        } catch (InstantiationException e) {
            throw instantiationError(plugin.getEntryPoint(), e.toString());
        }

    }

    /** Private helper method to get URLs */
    private void addURLs(ArrayList urlCache, String path) {
    
        // Add the URL's to the class loader (if they don't already exist)
        for (StringTokenizer str = new StringTokenizer(path, java.io.File.pathSeparator); str.hasMoreTokens();) {

            // Get the URL of the classpath
            String url = str.nextToken();
            URL newURL;
            try {
                newURL = new java.io.File(url).toURL();
                if (debug) System.out.println("Adding classpath: " + newURL);
            } catch (java.security.AccessControlException e) {
                // Ignore any file's we don't have access to
                continue;
            } catch (MalformedURLException e) {
                printBadClassPath(plugin.getName(), url);
                continue;
            }

            // Add the URL to the cache
            urlCache.add(newURL);
        }
    }

    
    /** Return the plugin */
    public EditorPlugin getPlugin() {
        return plugin;
    }

    /* ---------------------------- INTERFACE methods -------------------------*/
    public void setTextEnabled(Boolean enable) {
        invokeMethod(SETTEXTENABLED, new Object[]{enable});
    }

    public void setSource(String source) {
        invokeMethod(SETSOURCE, new Object[]{source});
    }

    public void setLanguage(Integer language) {
        invokeMethod(SETLANGUAGE, new Object[]{language});
    }

    public void setProblem(String problemDescription) {
        invokeMethod(SETPROBLEM, new Object[]{problemDescription});
    }

    public void setProblemComponent(ProblemComponentModel component, Language language, Renderer renderer) {
        invokeMethod(SETPROBLEMCOMPONENT, new Object[]{component, language, renderer});
    }

    public void setSignature(String className, String methodName, List parms, String rc) {
        invokeMethod(SETSIGNATURE, new Object[]{className, methodName, parms, rc});
    }

    public void setName(String name) {
        invokeMethod(SETNAME, new Object[]{name});
    }

    public JPanel getEditorPanel() {
        try {
            return (JPanel) invokeMethod(GETEDITORPANEL, null);
        } catch (ClassCastException e) {
            printBadRC(GETEDITORPANEL, JPanel.class.toString());
            return null;
        }
    }

    public String getSource() {
        try {
            return (String) invokeMethod(GETSOURCE, null);
        } catch (ClassCastException e) {
            printBadRC(GETSOURCE, String.class.toString());
            return null;
        }
    }

    public void clear() {
        invokeMethod(CLEAR, null);
    }

    public void startUsing() {
        invokeMethod(STARTUSING, null);
    }

    public void stopUsing() {
        invokeMethod(STOPUSING, null);
    }

    public void dispose() {
        invokeMethod(DISPOSE, null);
    }

    public void install() {
        invokeMethod(INSTALL, null);
    }

    public void uninstall() {
        invokeMethod(UNINSTALL, null);
    }

    //public void setContestApplet(ContestApplet contestApplet) {
    //    invokeMethod(SETCONTESTAPPLET, new Object[] { contestApplet });
    //}

    public boolean isCacheable() {
        // Return TRUE if there is NO configure method
        if (methodCache.get(ISCACHEABLE) == null) return true;

        // Call iscacheable and return the value
        try {
            return ((Boolean)invokeMethod(ISCACHEABLE, null)).booleanValue();
        } catch (ClassCastException e) {
            printBadRC(ISCACHEABLE, Boolean.class.toString());
            return true;
        }
    }

    public boolean configure() {
        // Return FALSE if there is NO configure method
        if (methodCache.get(CONFIGURE) == null) return false;

        // Call configure and return true
        invokeMethod(CONFIGURE, null);
        return true;
    }

    public boolean setCompileResults(boolean success, String message) {
        // Return FALSE if there is NO configure method
        if (methodCache.get(SETCOMPILERESULTS) == null) return false;

        // Call configure and return true
        try {
            return ((Boolean) invokeMethod(SETCOMPILERESULTS, new Object[]{new Boolean(success), message})).booleanValue();
        } catch (ClassCastException e) {
            printBadRC(SETCOMPILERESULTS, Boolean.class.toString());
            return false;
        }
    }

    /* ---------------------------- HELPER methods ----------------------------*/
    // Invokes a given method with the passed parameters
    private final Object invokeMethod(String methodName, Object[] parms) {
        // Return null if no object is current
        if (methodCache == null) return null;

        try {
            // Invoke the proper method with the passed parameters
            Method mthd = (Method) methodCache.get(methodName);
            return mthd == null ? null : mthd.invoke(editor, parms);
        } catch (Exception e) {
            if(parms==null) {
                System.err.println("Error invoking method " + methodName + "()");
            } else {
                System.err.println("Error invoking method " + methodName + "(" + Arrays.asList(parms).toString() + ")");
            }
            e.fillInStackTrace().printStackTrace();
            return null;
        }
    }

    // Adds the specified Method (or null if not found) to the method cache
    private final Method getMethod(Class pluginClass, String methodName, Class[] parms) {
        try {
            Method method = pluginClass.getMethod(methodName, parms);
            if (debug) System.out.println("Method " + methodName + " was found");
            return method;
        } catch (NoSuchMethodException e) {
            if (debug) System.out.println("Method " + methodName + " was not defined in the plugin editor and will be ignored");
            return null;
        }
    }

    // Print the bad classpath message
    private final void printBadClassPath(String pluginName, String classPath) {
        StringBuffer str = new StringBuffer("The classpath ");
        str.append(classPath);
        str.append(" for the plugin ");
        str.append(pluginName);
        str.append(" threw a MalFormedURLException and will be ignored.");
        System.err.println(str.toString());
    }

    // Print the bad return code message
    private final void printBadRC(String methodName, String expected) {
        StringBuffer str = new StringBuffer("Method ");
        str.append(methodName);
        str.append(" did not return an object of type ");
        str.append(expected);
        str.append(".  Returned object ignored.");
        System.err.println(str.toString());
    }

    // Creates the NoSuchMethodError
    private final NoSuchMethodError noSuchMethod(String methodName) {
        // Format the error message
        StringBuffer str = new StringBuffer("Required Method: ");
        str.append(methodName);
        str.append(" is not defined by the editor plugin.");

        // Write to console and throw error
        NoSuchMethodError error = new NoSuchMethodError(str.toString());
        error.fillInStackTrace().printStackTrace();
        return error;
    }

    // Creates the InstantiationEror
    private final InstantiationError instantiationError(String className, String reason) {
        // Format the error message
        StringBuffer str = new StringBuffer("Cannot instantiate ");
        str.append(className);
        str.append(". ");
        str.append(reason);

        // Write to console and throw error
        InstantiationError error = new InstantiationError(str.toString());
        error.fillInStackTrace().printStackTrace();
        return error;
    }

    // The custom class loader
    // Needs to delegate to the current classloader to retrieve classes that are in contestapplet.jar
    private final class CustomLoader extends URLClassLoader {

        // Create the URLClassLoader with the current class loader as it's parent
        public CustomLoader(URL[] urls) {
            super(urls);
        }

        // Override loadClass to look FIRST locally then at the parent
        // This is important since the parent is a network resource and
        // plugins will ALWAYS be loaded locally.
        public final Class loadClass(String name) throws ClassNotFoundException {

            try {
                Class temp = super.findClass(name);
                if (debug) System.out.println("Loaded from this classloader: " + name);
                return temp;
            } catch (ClassNotFoundException e) {
                if (debug) System.out.println("Loaded from parent classloader: " + name);
                return Class.forName(name);
            }
        }
    }
}
