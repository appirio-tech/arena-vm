/*
 * TesterSecurityManager.java
 *
 * Created on February 8, 2005, 4:16 PM
 */

package com.topcoder.services.tester.java;


import java.security.Permission;

/**
 *
 * @author rfairfax
 */
public class TesterSecurityManager extends SecurityManager {
    
    private String password;

    TesterSecurityManager(String password) {
        super();
        this.password = password;
    }

    TesterSecurityManager() {
        super();
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

        if (perm instanceof java.lang.RuntimePermission) {
            //trace.info("perm is: "+perm.toString());
            if (perm.getName().equals("setSecurityManager")) {
                if (!accessOK()) {
                    try {
                        if (1==1) {
                            throw new Exception();
                         }
                    } catch (Exception e) {
                            //trace.debug(e);
                    }
                    System.exit(1);
                    throw new SecurityException("No Way!");
                }
            } else if (perm.getName().equals("createSecurityManager")) {
                if (!accessOK()) {
                    try {
                        if (1==1) {
                            throw new Exception();
                         }
                    } catch (Exception e) {
                            //trace.debug(e);
                    }
                    System.exit(1);
                    throw new SecurityException("No WAY!");
                }
            } else {
                //trace.debug("in here: "+perm.toString());
                super.checkPermission(perm);
            }
        } else if (perm instanceof java.io.FilePermission) {
            super.checkPermission(perm);
        } else if (perm instanceof java.lang.reflect.ReflectPermission) {
            //pour through the stack for sun.text.resources
            //Allow these
            Exception e = (Exception)new Exception().fillInStackTrace();
            StackTraceElement[] elm = e.getStackTrace();
            for(int i = 0; i < elm.length; i++ ) {
                if(elm[i].getClassName().indexOf("sun.text.resources") != -1 || elm[i].getClassName().indexOf("com.sun.xml.rpc") != -1) {
                    return;
                }
            }
            super.checkPermission(perm);
        }
    }


}
