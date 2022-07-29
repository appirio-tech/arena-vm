/*
 * ClassNameVisitorState.java
 *
 * Created on June 21, 2006, 3:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.services.compiler.util.MSILParser.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rfairfax
 */
public class ClassNameVisitorState {
   
    private List base = new ArrayList();
    
    /** Creates a new instance of ClassNameVisitorState */
    public ClassNameVisitorState() {
    }
    
    public void addBase(String n) {
        base.add(n);
    }
    
    public String getBase() {
        String s = "";
        for(int i = 0; i < base.size(); i++) {
            s += (String)base.get(i);
            s += ".";
        }
        return s;
    }
    
    public void removeBase() {
        base.remove(base.size()-1);
    }
    
    public String toString() {
        return "BASE: " + base;
    }
    
}
