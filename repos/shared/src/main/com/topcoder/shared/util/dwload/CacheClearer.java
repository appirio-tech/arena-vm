/*
 * CacheClearer.java
 *
 * Created on September 13, 2005, 11:33 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.shared.util.dwload;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.topcoder.shared.util.TCContext;
import com.topcoder.shared.util.TCResourceBundle;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class provides cache clearing services for clients. 
 * 
 * Note: The process no longer run locally but instead the CacheAdmin mbean 
 * located in the cache server is used.
 * 
 * @author rfairfax, pulky
 */
public class CacheClearer {
    private static final Logger log = Logger.getLogger(CacheClearer.class);

    /**
     * Creates a new instance of CacheClearer
     */
    public CacheClearer() {
    }

    /**
     * Removes entries from the cache "like" the specified argument  
     * 
     * @param s the string compare and remove items
     */
    public static void removelike(String s) {
        InitialContext ctx = null;
        TCResourceBundle b = new TCResourceBundle("cache");
        try {
            ctx = TCContext.getInitial(b.getProperty("host_url"));

            // This method no longer does the job, it calls the cache admin mbean to speed up the process (all the tree lookup will be made locally on the cache server)            
            // Object o = ctx.lookup(b.getProperty("jndi_name"));
            // new CacheClearer().removelike(s, "/", o);

            log.debug("Performing a single call to remove like : " + s);
            Object o = ctx.lookup(b.getProperty("cache_admin_jndi_name"));
            
            //using reflection so that we don't a lot of nasty dependencies when using the class.
            Method removeLikeMethod=null;
            Method[] methods = o.getClass().getDeclaredMethods();
            for (Method m : methods) {
//                log.debug("method " + m.getName() + " params: ");
//                for (Class<?> c : m.getParameterTypes()) {
//                    log.debug(" - " + c.getName());
//                }
                if ("removelike".equals(m.getName()) &&
                      m.getParameterTypes().length == 1 &&
                      m.getParameterTypes()[0].equals(String.class)) {
                    removeLikeMethod = m;
                    break;
                }
            }
            if (removeLikeMethod==null) {
                throw new RuntimeException("Couldn't find removeLike(String) method");
            } else {
                removeLikeMethod.invoke(o, s);
            }

            log.info("removed " + s);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            TCContext.close(ctx);
        }

    }

    /**
     * Removes entries from the cache "like" any of the the specified strings in the argument
     * 
     * @param s the set of strings to compare and remove items
     */
    public static void removelike(Set<String> s) {
        InitialContext ctx = null;
        TCResourceBundle b = new TCResourceBundle("cache");
        try {
            ctx = TCContext.getInitial(b.getProperty("host_url"));
            // This method no longer does the job, it calls the cache admin mbean to speed up the process (all the tree lookup will be made locally on the cache server)            
            // Object o = ctx.lookup(b.getProperty("jndi_name"));
            // new CacheClearer().removelike(s, "/", o);

            for (String st : s) {
                log.debug("Performing a single call to remove like (set) : " + st);
            }

            Object o = ctx.lookup(b.getProperty("cache_admin_jndi_name"));
            
            //using reflection so that we don't a lot of nasty dependencies when using the class.
            Method removeLikeMethod=null;
            Method[] methods = o.getClass().getDeclaredMethods();
            for (Method m : methods) {
//                log.debug("method " + m.getName() + " params: ");
//                for (Class<?> c : m.getParameterTypes()) {
//                    log.debug(" - " + c.getName());
//                }
                if ("removelike".equals(m.getName()) &&
                      m.getParameterTypes().length == 1 &&
                      m.getParameterTypes()[0].equals(Set.class)) {
                    removeLikeMethod = m;
                    break;
                }
            }
            if (removeLikeMethod==null) {
                throw new RuntimeException("Couldn't find removelikeSet(set) method");
            } else {
                removeLikeMethod.invoke(o, s);
            }
            
            log.info("removed " + s);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            TCContext.close(ctx);
        }
    }
}



