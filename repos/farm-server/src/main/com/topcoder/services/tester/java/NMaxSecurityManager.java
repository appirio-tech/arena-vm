/*
 * NMaxSecurityManager.java
 *
 * Created on January 24, 2006, 9:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.services.tester.java;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import java.security.Permission;
import com.topcoder.shared.common.ServicesConstants;
import java.util.ArrayList;
import java.util.Iterator;

public class NMaxSecurityManager extends SecurityManager {
    //and so begins our split of code between 1.4 compilation and 1.5 compilation
    //once we get off of weblogic we'll move everything to 1.5
    Map<ThreadGroup, Integer> threadCounts = new HashMap<ThreadGroup, Integer>();

    Map<ThreadGroup, Integer> threadMaxes = new HashMap<ThreadGroup, Integer>();

    Map<ThreadGroup, ThreadGroup> threadAncestors = new HashMap<ThreadGroup, ThreadGroup>();
    
    Map<ThreadGroup, ArrayList<Thread>> registeredThreads = new HashMap<ThreadGroup, ArrayList<Thread>>();
    
    private boolean allowThreading = System.getProperty("ALLOW_THREADING").equals("true");
    private String password;
    
    
    private static final boolean DEBUG = false;
    private Thread[] results = new Thread[40];
    
    public NMaxSecurityManager() {
        super();
    }
    
    public NMaxSecurityManager(ThreadGroup tg) {
        super();
        int max = Integer.parseInt(System.getProperty("MAX_THREADS"));
        registerThreadGroup(tg, max);
        //for the main thread
        incrementThreadCount(tg);
    }
    
    private void debug(String line) {
        if(DEBUG)
            System.out.println(line);
    }
    
    public void setPass (String password) {
        this.password = password;
    }

    private boolean accessOK() {
        //Here is the password
        String response = "QuienEsSuPadre";
        try {
            if (response.equals(password))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void checkPermission(Permission perm) {
        debug("PERM CHECK: " + perm.getClass().getName() + ":"  + perm.getName());
        if (perm instanceof java.lang.RuntimePermission) {
            if (perm.getName().equals("setSecurityManager")) {
                if (!accessOK()) {
                    throw new SecurityException("No Way!");
                }
            } else if (perm.getName().equals("createSecurityManager")) {
                if (!accessOK()) {
                    throw new SecurityException("No WAY!");
                }
            } else {
                super.checkPermission(perm);
            }
        } else if (perm instanceof java.io.FilePermission) {
            if(perm.getName().equals("results." + System.getProperty("pid"))) {
                return;
            } else if(perm.getName().equals("results." + System.getProperty("pid") + "/stdout")) {
                return;
            } else if(perm.getName().equals("results." + System.getProperty("pid") + "/stderr")) {
                return;
            } else {
                super.checkPermission(perm);
            }
        } else {
            super.checkPermission(perm);
        }
    }
    
    /**
     * Register a ThreadGroup along with a maximum number of allowable threads.
     * 
     * @param g
     * @param max
     */
    public void registerThreadGroup(ThreadGroup g, int max) {
        synchronized (this) {
            threadMaxes.put(g, max);
            if (threadCounts.get(g) == null) {
                threadCounts.put(g, 0);
                registeredThreads.put(g, new ArrayList<Thread>());
            }
        }
    }

    /**
     * Locate the appropriate ThreadGroup to use as a key in the count and
     * maximum maps.
     * 
     * @param g
     */
    private ThreadGroup findAncestor(ThreadGroup g) {
        synchronized (this) {
            // look for g in the registered list
            if (threadMaxes.keySet().contains(g)) {
                return g;
            }
            // look in the cache
            if (threadAncestors.keySet().contains(g)) {
                return threadAncestors.get(g);
            }
            // look for a parent of g
            for (ThreadGroup p : threadMaxes.keySet()) {
                if (p.parentOf(g)) {
                    // cache the result
                    threadAncestors.put(g, p);
                    return p;
                }
            }
            // this is not a registered group
            return null;
        }
    }

    /**
     * Return the maximum allowed number of threads for the registered
     * ThreadGroup.
     * 
     * @param g
     */
    private Integer getThreadMax(ThreadGroup g) {
        synchronized (this) {
            ThreadGroup p = findAncestor(g);
            if (p == null) {
                return null;
            } else {
                return threadMaxes.get(p);
            }
        }
    }

    /**
     * Return the current count of threads created in the registered
     * ThreadGroup.
     * 
     * @param g
     */
    private Integer getThreadCount(ThreadGroup g) {
        synchronized (this) {
            ThreadGroup p = findAncestor(g);
            if (p == null) {
                return null;
            } else {
                return threadCounts.get(p);
            }
        }
    }

    /**
     * Increment the counter for a specific registered ThreadGroup.
     * 
     * @param g
     * @throws SecurityException
     *             if incrementing the current count would result in it
     *             exceeding the registered maximum
     */
    private void incrementThreadCount(ThreadGroup g) throws SecurityException {
        synchronized (this) {
            ThreadGroup p = findAncestor(g);
            Integer max = getThreadMax(p);
            Integer count = getThreadCount(p);
            if (count + 1 > max) {
                debug(this.getClass().getName()
                                + ": Incrementing the count for this thread group would exceed the registered maximum.");
                throw new SecurityException("Trying to create thread " + (count+1) + " (Max of " + max + ")");
            } else {
                threadCounts.put(p, count + 1);
            }
        }
    }
    
    private void decrementThreadCount(ThreadGroup g) throws SecurityException {
        synchronized (this) {
            ThreadGroup p = findAncestor(g);
            Integer max = getThreadMax(p);
            Integer count = getThreadCount(p);
            threadCounts.put(p, count - 1);
            
        }
    }
    
    public void checkAccess(Thread t) {
        //super.checkAccess(t);
        //Thread.currentThread().dumpStack();
        debug("ADDING:" + t.getName());

        ThreadGroup p = findAncestor(t.getThreadGroup());
        if(p == null || registeredThreads.get(p) == null)
            return;
        
        if(!registeredThreads.get(p).contains(t))
            registeredThreads.get(p).add(t);
            
    }

    /**
     * Override checkAccess() for ThreadGroups. If the current thread's
     * ThreadGroup is registered, check to see if the creation of another thread
     * in the group would make the count exceed the maximum allowable count. If
     * so, throw a SecurityException. Otherwise, increment the current count.
     * 
     * @param g
     */
    public void checkAccess(ThreadGroup g) {
        super.checkAccess(g);

        synchronized (this) {
            debug(this.getClass().getName()
                    + ": checkAccess() called on ThreadGroup " + g.getName());
            // Thread.dumpStack();
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            
            // only deal with Thread() constructor calls
            boolean inThreadConstructor = false;
            boolean inWrapper = false;
            for (StackTraceElement element : trace) {
                if (element.getClassName().equals("java.lang.ThreadGroup")
                        && element.getMethodName().equals("enumerate")) {
                    //always good
                    return;
                }
                if (element.getClassName().equals("java.lang.Thread")
                        && element.getMethodName().equals("<init>")) {
                    inThreadConstructor = true;
                }
                if (element.getClassName().endsWith("Wrapper$Waiter")
                        && element.getMethodName().equals("<init>")) {
                    inWrapper = true;
                }
            }
            if(inWrapper)
                return;
            
            if(inThreadConstructor && !allowThreading) {
                throw new SecurityException();
            }
            
            //here we try to release old threads that have been used
            ArrayList<Thread> al = registeredThreads.get(findAncestor(g));
            if(al != null) {
                for(Iterator<Thread> i = al.iterator(); i.hasNext();) {
                    Thread t = i.next();
                    //check status
                    debug(t.getName() + ":" + t.getState());
                    if(t.getState() == java.lang.Thread.State.TERMINATED) {
                        //this one is done
                        i.remove();
                        decrementThreadCount(g);
                    }
                }
                    
            }
            
            if (inThreadConstructor && getThreadMax(g) != null) {
                //Thread.dumpStack();
                
                // debugging info
                Integer count = getThreadCount(g);
                Integer max = getThreadMax(g);
                ThreadGroup p = findAncestor(g);
                debug(this.getClass().getName()
                        + ": by my count, ThreadGroup " + g.getName() + "("
                        + p.getName() + ") has " + count + "/" + max
                        + " threads");
                debug(this.getClass().getName() + ": " + g.getName()
                                + " has activeCount() " + g.activeCount());
                debug(this.getClass().getName()
                        + ": the system ThreadMXBean's getThreadCount() is "
                        + ManagementFactory.getThreadMXBean().getThreadCount());

                incrementThreadCount(g);
                return;
            } else {
                debug(this.getClass().getName()
                        + ": Not concerned with this.");
            }
        }
    }
}
