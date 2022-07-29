/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.listener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.shared.util.concurrent.ConcurrentHashSet;
import com.topcoder.shared.util.logging.Logger;

/**
 * <p>
 * Changes in version 1.1 (Make Admin Listener Work With Main Listener Through Loopback Address v1.0)
 * <ol>
 *      <li>Update {@link #IPBlocker(Collection ips, boolean isAllowedSet)} method.</li>
 *      <li>Add {@link #ALLOWED_LOOP_BACK_IP} method.</li>
 * </ol>
 * </p>
 * @author savon_cn
 * @version 1.1
 *
 */
public final class IPBlocker {

    private static final Logger cat = Logger.getLogger(IPBlocker.class);

    private final Map banExpiration = new ConcurrentHashMap();
    private final Collection ipsSet = new ConcurrentHashSet();
    /**
     * The loop back ip
     * @since 1.1
     */
    public static final String ALLOWED_LOOP_BACK_IP = "127.0.0.1";
    private final boolean isAllowedSet;

    public IPBlocker(Collection ips, boolean isAllowedSet) {
        this.isAllowedSet = isAllowedSet;
        if (ips != null) {
            ipsSet.addAll(ips);
        }
        if (isAllowedSet) {
            try {
                String localIP = InetAddress.getLocalHost().getHostAddress();
                ipsSet.add(localIP);
                //we can set loop back as allowed
                ipsSet.add(ALLOWED_LOOP_BACK_IP);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        info("isAllowedSet=" + isAllowedSet + ", ips=" + ipsSet);
    }

    public void banIP(String ipAddress) {
        if (isAllowedSet) {
            throw new RuntimeException("you cannot ban an IP address if isAllowedSet==true");
        }
        ipsSet.add(ipAddress);
    }
    
    //rfairfax 6-11
    public void banIPwithExpiry(String ipAddress, long expiresAt)
    {
        if (isAllowedSet) {
            throw new RuntimeException("you cannot ban an IP address if isAllowedSet==true");
        }
        ipsSet.add(ipAddress);
        banExpiration.put(ipAddress, new Long(expiresAt));
    }

    public boolean isBlocked(String ipAddress) {
        if (isAllowedSet) {
            return !ipsSet.contains(ipAddress);
        } else {
            //check for expiration
            if(banExpiration.containsKey(ipAddress))
            {
                long time = ((Long)banExpiration.get(ipAddress)).longValue();
                
                if(time <= System.currentTimeMillis())
                {
                    //they're ok again
                    banExpiration.remove(ipAddress);
                    ipsSet.remove(ipAddress);
                }
            }
            return ipsSet.contains(ipAddress);
        }
    }

    private static void info(String message) {
        cat.info(message);
    }

}
