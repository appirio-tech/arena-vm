package com.topcoder.client.launcher.common.archive;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.topcoder.client.launcher.common.file.ApplicationFile;
import com.topcoder.client.launcher.common.task.ApplicationTaskProgressListener;

public abstract class ApplicationArchive {
    private Map files = new TreeMap();

    private Set listeners = new HashSet();
    
    public void add(ApplicationFile file) {
        files.put(file.getFilename(), file);
    }

    public boolean contains(String filename) {
        return files.containsKey(filename);
    }

    public ApplicationFile get(String filename) {
        return (ApplicationFile) files.get(filename);
    }

    public Iterator iterator() {
        return files.values().iterator();
    }

    public int size() {
        return files.size();
    }

    protected void newTask(String name, int max) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ApplicationTaskProgressListener listener = (ApplicationTaskProgressListener) iter.next();
            
            listener.newTask(name, max);
        }
    }

    protected void finish() {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ApplicationTaskProgressListener listener = (ApplicationTaskProgressListener) iter.next();
            
            listener.finish();
        }
    }

    protected void progress(int progress, String comment) {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            ApplicationTaskProgressListener listener = (ApplicationTaskProgressListener) iter.next();
            
            listener.progress(progress, comment);
        }
    }
    
    public abstract void writeTo(File out) throws IOException;
    
    public abstract void dispose();
    
    public void addTaskProgressListener(ApplicationTaskProgressListener listener) {
        listeners.add(listener);
    }
    
    public void removeTaskProgressListener(ApplicationTaskProgressListener listener) {
        listeners.remove(listener);
    }
}
