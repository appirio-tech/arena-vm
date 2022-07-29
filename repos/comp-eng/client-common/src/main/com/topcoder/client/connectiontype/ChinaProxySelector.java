/*
 * ChinaProxySelector.java
 * 
 * Created on May 23, 2007, 11:52:58 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.connectiontype;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a proxy selector which is used to connect the server via a proxy in China.
 * 
 * @author rfairfax
 * @version $Id$
 */
public class ChinaProxySelector extends ProxySelector {
    /** Represents the previous proxy selector if any. */
    private ProxySelector oldSelector = null;
    
    /**
     * Creates a new instance of <code>ChinaProxySelector</code>.
     */
    public ChinaProxySelector() {
    }
    
    /**
     * Sets up the china proxy selector.
     */
    public void setup() {
        oldSelector = ProxySelector.getDefault();
        ProxySelector.setDefault(this);
        Authenticator.setDefault(new Authenticator() {
            protected  PasswordAuthentication  getPasswordAuthentication() {
                return new PasswordAuthentication("wishingbone", new char[] {'q','e','e','h','o','u'});
            }
        });
    }
    
    /**
     * Cleans up the china proxy selector.
     */
    public void unsetup() {
        ProxySelector.setDefault(oldSelector);
        Authenticator.setDefault(null);
    }

    public List<Proxy> select(URI arg0) {
        List<Proxy> results = new ArrayList<Proxy>();
        results.add(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("202.152.183.47", 5001)));
        return results;
    }

    public void connectFailed(URI arg0, SocketAddress arg1, IOException arg2) {
        System.err.println("FAILED: " + arg0);
    }

}
