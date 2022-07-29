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
public class WikiVerify {

    /** Creates a new instance of WikiLoader */
    public WikiVerify() {
    }

    private static final String PATH = "/home/rfairfax/wiki";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws PersistentCacheException, FileNotFoundException, IOException {
        // TODO code application logic here
        File dir = new File(PATH);
        File[] files = dir.listFiles();
        for(int i =0; i < files.length; i++) {
            if(files[i].getName().endsWith(".txt")) {
                long start = System.currentTimeMillis();
                //process
                BufferedReader br = new BufferedReader(new FileReader(files[i]));
                ArrayList al = new ArrayList();
                String s;
                while((s = br.readLine()) != null) {
                    al.add(s);
                }
                br.close();
                String[] ret = new String[al.size()];
                for(int j = 0; j < al.size(); j++) {
                    ret[j] = (String)al.get(j);
                }
                
                String name = files[i].getName().substring(0, files[i].getName().length() - 4);
                System.out.println("PUTTING: " + name + ", TOOK: " + (System.currentTimeMillis() - start));
                
                boolean show = true;
                for(int x = 0; x < ret.length; x++) {
                    for(int y = 0; y < ret[x].length(); y++) {
                        if(ret[x].charAt(y) > 255 && show)  {
                            show = false;
                            System.out.println("BAD:" + ret[x].charAt(y) + "/" + (int)ret[x].charAt(y));
                        }
                    }
                }
                
                
            }
        }
    }

}
