/*
 * WikiLoader.java
 * 
 * Created on Jun 12, 2007, 11:01:28 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.services.persistentcache.PersistentCache;
import com.topcoder.services.persistentcache.PersistentCacheException;
import com.topcoder.services.persistentcache.impl.PersistentCacheManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author rfairfax
 */
public class WikiLoader {

    /** Creates a new instance of WikiLoader */
    public WikiLoader() {
    }

    private static final String PATH = "C:/cygwin/home/farm/wiki";
    //private static final String PATH = "/home/farm/wiki";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws PersistentCacheException, FileNotFoundException, IOException {
        // TODO code application logic here
        PersistentCacheManager mgr = new PersistentCacheManager();
        PersistentCache cache = mgr.getCache("MM_BlockEditDistance2");
        cache.setMinimalVersion(1);
        cache.put("filled", new Boolean(true));
        File dir = new File(PATH);
        File[] files = dir.listFiles();
        for(int i =0; i < files.length; i++) {
            if(files[i].getName().endsWith(".txt")) {
                long start = System.currentTimeMillis();
                //process
                BufferedReader br = new BufferedReader(new FileReader(files[i]));
                ArrayList al = new ArrayList();
                String s;
                StringBuffer sb = new StringBuffer();
                while((s = br.readLine()) != null) {
                    for(int j = 0; j < s.length(); j++) {
                        int n = s.charAt(j);
                        if(n>=32&&n<=126 ) {
                            sb.append((char)n);
                        }else{
                            sb.append('?');
                        }
                    }
                    s = sb.toString();
                    
                    al.add(s);
                    sb.delete(0, sb.length());
                }
                br.close();
                String[] ret = new String[al.size()];
                for(int j = 0; j < al.size(); j++) {
                    ret[j] = (String)al.get(j);
                }
                
                
                String name = files[i].getName().substring(0, files[i].getName().length() - 4);
                cache.put(name, ret);
                System.out.println("PUTTING: " + name + ", TOOK: " + (System.currentTimeMillis() - start));
            }
        }
    }

}
