/*
 * Java13Utils
 * 
 * Created 05/13/2006
 */
package com.topcoder.server.util;

/**
 * Class containing methods of different classes that are not implemented in 
 * java 1.3.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id: Java13Utils.java 67962 2008-01-15 15:57:53Z mural $
 */
public class Java13Utils {

    /**
     * Compatible implementation of StringBuffer#replace(String search, String replace) 
     */
    public static StringBuffer replace(StringBuffer self, String search, String replace) {
        //replaces all instances of search with replace in buf
        //use buf.indexOf() w/Java 1.4+, WL requires 1.3
        int pos = 0;
        while( (pos = self.indexOf(search)) != -1) {
            self.replace(pos,pos+search.length(),replace);
        }
        return self;
    }
}
