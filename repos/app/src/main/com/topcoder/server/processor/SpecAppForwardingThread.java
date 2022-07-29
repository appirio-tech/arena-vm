/*
 * SpecAppForwardingThread.java
 *
 * Created on September 25, 2006, 7:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.server.processor;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.Key;

import com.topcoder.client.security.PublicKeyObtainer;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.NetCommonCSHandler;
import com.topcoder.netCommon.contestantMessages.request.ExchangeKeyRequest;
import com.topcoder.netCommon.contestantMessages.request.KeepAliveRequest;
import com.topcoder.netCommon.contestantMessages.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.response.ExchangeKeyResponse;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;
import com.topcoder.shared.netCommon.messages.spectator.SpectatorLoginResult;
import com.topcoder.shared.util.StoppableThread;
import com.topcoder.shared.util.logging.Logger;

/**
 *
 * @author rfairfax
 */
public class SpecAppForwardingThread implements StoppableThread.Client {
    
    private String host;
    private int port;
    private String user;
    private String password;
    
    private static final Logger log = Logger.getLogger(SpecAppForwardingThread.class);
    
    private boolean connected = false;
    
    private ClientSocket cs = null;
    
    private KeepAliveThread keepalives = new KeepAliveThread();
    private StoppableThread thread = new StoppableThread(keepalives, "Forwarder Heartbeat");
    private StoppableThread mainThread = new StoppableThread(this, "Forwarder");
    private SpecAppProcessor proc = null;
    private Key encryptKey;
    private Key fastEncryptKey;
    private MessageEncryptionHandler encryptionHandler;
    
    /** Creates a new instance of SpecAppForwardingThread */
    public SpecAppForwardingThread(String host, int port, SpecAppProcessor proc, String user, String password) {
        this.host = host;
        this.port = port;
        this.proc = proc;
        this.user = user;
        this.password = password;
        try {
            encryptKey = PublicKeyObtainer.obtainPublicKey();
        } catch (Exception e) {
            log.error("Message encryption key invalid.");
            log.error(e);
        }
    }
    
    public void disconnect() {
        connected = false;
        //cs = null;
        proc.forwarderShutdown(this);
    }

    public void cycle() throws InterruptedException {
        //read stuff
        if(!connected) {
            //try to connect
            try {
                cs = new ClientSocket(InetAddress.getByName(host),port,new NetCommonCSHandler(encryptKey));
                encryptionHandler = new MessageEncryptionHandler();
                cs.writeObject(new ExchangeKeyRequest(encryptionHandler.generateRequestKey()));
                connected = true;
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
                disconnect();
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
                disconnect();
                return;
            }
        } else {
            try {
                Object o = cs.readObject();
                if (log.isDebugEnabled()) {
                    log.debug("Forwarder Received: " + o);
                }
                if(o instanceof SpectatorLoginResult) {
                    //good login
                    proc.forwarderLoginSuccess(this);
                } else if (o instanceof ExchangeKeyResponse) {
                    // Key exchanged
                    encryptionHandler.setReplyKey(((ExchangeKeyResponse) o).getKey());
                    fastEncryptKey = encryptionHandler.getFinalKey();
                    encryptionHandler = null;
                    cs.writeObject(new LoginRequest(user, MessageEncryptionHandler.sealObject(password, fastEncryptKey), ContestConstants.FORWARDER_LOGIN));
                }
            } catch (ObjectStreamException ex) {
                ex.printStackTrace();
                disconnect();
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
                disconnect();
                return;
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                disconnect();
                return;
            }
            
        }
    }
    
    public void writeObject(Object o) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Writing: " + o);
            }
            cs.writeObject(o);
        } catch (IOException ex) {
            disconnect();
            ex.printStackTrace();
        }
    }
    
    public void start() {
        mainThread.start();
        thread.start();
    }
    
    public void stop() {
        try {
            mainThread.stopThread();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        try {
            thread.stopThread();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        disconnect();
    }
    
    public String toString() {
        return "Forwarder." + host + ":" + port;
    }
    
    private class KeepAliveThread implements StoppableThread.Client {
        public void cycle() throws InterruptedException {
            //send keepalive
            if(!connected) {
                Thread.sleep(1000);
                return;
            } else {
                try {
                    cs.writeObject(new KeepAliveRequest());
                    Thread.sleep(45000);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    disconnect();
                }
            }
        }
        
    }
}
