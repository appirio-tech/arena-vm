/**
 * EditorPlugin.java
 *
 * Description:		Class holding information about a single editor plugin
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors;

public final class EditorPlugin implements Cloneable {

    public static int STANDARD = 1;
    public static int LOCAL = 2;

    private int pluginType;
    private String name;
    private String entryPoint;
    private String classPath;
    private boolean eager = false;

    public EditorPlugin() {
    }
    
    public EditorPlugin(String name, String entryPoint) {
        this(name, entryPoint, "");
        this.pluginType = STANDARD;
    }

    public EditorPlugin(String name, String entryPoint, String classPath) {
        this(name, entryPoint, classPath, false);
    }

    public EditorPlugin(String name, String entryPoint, String classPath, boolean eager) {
        this.pluginType = LOCAL;
        this.name = name;
        this.entryPoint = entryPoint;
        this.classPath = classPath;
        this.eager = eager;
    }

    public final int getType() {
        return pluginType;
    }

    public final String getName() {
        return name;
    }

    public final String getEntryPoint() {
        return entryPoint;
    }

    public final String getClassPath() {
        return classPath;
    }

    public final boolean getEager() {
        return eager;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public final void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public final void setEager(boolean eager) {
        this.eager = eager;
    }

    public final boolean equals(Object o) {
        if(!(o instanceof EditorPlugin)) return false;
        EditorPlugin ep = (EditorPlugin)o;
        
        return ep.getType()==getType()
            && ep.getName().equals(getName())
            && ep.getEntryPoint().equals(getEntryPoint())
            && ep.getClassPath().equals(getClassPath())
            && ep.getEager()==getEager();
    }
    
    public Object clone() {
        EditorPlugin temp = new EditorPlugin();
        temp.pluginType = pluginType;
        temp.name = name;
        temp.entryPoint = entryPoint;
        temp.classPath = classPath;
        temp.eager = eager;
        return temp;
    }
        
    public final String toString() {
        StringBuffer str = new StringBuffer(pluginType == STANDARD ? "Standard " : "Local ");
        str.append(name);
        str.append(" ");
        str.append(entryPoint);
        str.append(" ");
        str.append(classPath);
        str.append(" ");
        str.append(eager);
        return str.toString();
    }

}


/* @(#)EditorPlugin.java */
