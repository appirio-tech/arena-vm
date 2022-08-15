/*
 * UCRProcessor.java
 *
 * Created on May 10, 2005, 2:41 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.client.contestApplet.unusedCodeProcessor;

import java.util.StringTokenizer;

/**
 *
 * @author rfairfax
 */
public abstract class UCRProcessor {
    
    static final boolean DEBUG = false;
    static final int CODE_LIMIT = 300;
    static final double CODE_PERCENT_LIMIT = .3;
    static final String INVALID_MESSAGE = "Your submission may contain more than 30% unused code which would violate the Unused Code Rule.  Are you sure you want to submit this code?";
    
    public abstract String checkCode() throws RuntimeException;
    public abstract void initialize(String className, String methodName, String originalCode);
    
    //to handle java 1.3
    public String[] split(String orig, String token) {
        StringTokenizer st = new StringTokenizer(orig, token);
        int n = st.countTokens();
        String[] ret = new String[n];
        for(int i = 0; i < n; i++) {
            ret[i] = st.nextToken();
        }
        for(int i = n-1; i >= 0; i--) {
            if(ret[i].equals("")) {
                n--;
            }
            else
                break;
        }
        
        String[] ret2 = new String[n];
        for(int i = 0; i < n; i++) 
            ret2[i] = ret[i];
        
        return ret2;
    }
    
}
