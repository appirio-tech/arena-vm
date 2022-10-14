/*
 * PersistentCacheTool
 * 
 * Created 05/21, 2007
 */
package com.topcoder.services.persistentcache.impl;

import java.util.Iterator;
import java.util.List;

import com.topcoder.services.persistentcache.PersistentCacheException;
import com.topcoder.shared.util.StringUtil;

/**
 * Command line tool to manage PersistentCache
 *  
 * @author Diego Belfer (mural)
 * @version $Id: PersistentCacheTool.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class PersistentCacheTool {
   
    public static void main(String[] args) throws PersistentCacheException {
        if (args.length == 0) {
            displayUsage("No option specified");
            return;
        }
        String cmd = args[0];
        if ("info".equals(cmd)) {
            doInfo();
        } else if ("delete".equals(cmd)) {
            if (args.length != 2) {
                displayUsage("No ID specified");
            } else {
                doDelete(args[1]);
            }
        } else if ("set".equals(cmd)) {
            if (args.length != 3) {
                displayUsage("No folder specified");
            } else {
                doSet(args[1], args[2]);
            }
        }
        
    }

    private static void displayUsage(String err) {
        if (err != null) {
            System.out.println("ERR: "+err);
        }
        System.out.println("Usage:  java -cp .... "+PersistentCacheTool.class.getName()+" {info|delete|set} ....");
        System.out.println("info              Display cache information");
        System.out.println("delete ID         Deletes the cache instance with the given ID");
        System.out.println("set    ID folder  Sets folder as the cache instance folder for the cache, whose cache id is equal to the given one.");
        System.out.println("                  folder must be realtive to the root path");
    }
    
    private static void doDelete(String id) throws PersistentCacheException {
        new PersistentCacheManager().delete(id);
        System.out.print("Cache instance deleted");
    }
    
    private static void doSet(String id, String folder) throws PersistentCacheException {
        int size = new PersistentCacheManager().setInstanceFolder(id, folder);
        System.out.print("Folder set successfully, # items found =" + size);
    }

    private static void doInfo() throws PersistentCacheException {
        List caches = new PersistentCacheManager().getCacheSummary();
        System.out.println("Cache instances");
        System.out.println("Id                             version   size  folder");
        for (Iterator it = caches.iterator(); it.hasNext();) {
            PersistentCacheManager.CacheInfo info = (PersistentCacheManager.CacheInfo) it.next();
            System.out.print(StringUtil.padRight(info.getId(), 30));
            System.out.print(" ");
            System.out.print(StringUtil.padLeft(""+info.getVersion(), 7));
            System.out.print(" ");
            System.out.print(StringUtil.padLeft(""+info.getSize(), 6));
            System.out.print("  ");
            System.out.println(info.getPath());
        }
    }
}
