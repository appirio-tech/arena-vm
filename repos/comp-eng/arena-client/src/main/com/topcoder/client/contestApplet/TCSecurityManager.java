package com.topcoder.client.contestApplet;

/**
 * TCSecurityManager.java
 *
 * Description:		Custom Security Manager for the TC applet
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */


import java.security.*;

final class TCSecurityManager extends SecurityManager {

    // Essentially - allow all permissions by overriding the checkPermissions
    // to never throw access exceptions...
    public void checkPermission(Permission perm) {
    }

    public void checkPermission(Permission perm, Object context) {
    }
}
