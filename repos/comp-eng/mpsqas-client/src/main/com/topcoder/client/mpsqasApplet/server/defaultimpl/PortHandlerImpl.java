package com.topcoder.client.mpsqasApplet.server.defaultimpl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.server.EncryptionHandler;
import com.topcoder.client.mpsqasApplet.server.MPSQASClientSocketFactory;
import com.topcoder.client.mpsqasApplet.server.PortHandler;
import com.topcoder.client.mpsqasApplet.server.ResponseHandler;
import com.topcoder.client.security.PublicKeyObtainer;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.netCommon.mpsqas.communication.message.ExchangeKeyRequest;
import com.topcoder.netCommon.mpsqas.communication.message.Message;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;

/**
 * The default implementation of the Port Handler, contains a listener
 * thread to listen for incoming messages and a method to send messages
 * through the socket.
 *
 * @author mitalub
 */
public class PortHandlerImpl implements Runnable, PortHandler {

    private ClientSocket socket;
    private ResponseHandler responseHandler;
    private MessageEncryptionHandler encryptionHandler;
    private String address, tunnel;
    private int port;
    private boolean useSSL;
    private boolean quit;
    private Thread receiver;

    /**
     * Gets the connection to the server.  Must be called before sendObject
     * or startListener.
     */
    public void init(String address, int portNumber, String tunnel) {
        this.address = address;
        this.port = portNumber;
        this.tunnel = tunnel;
        
        
        
        
        responseHandler = MainObjectFactory.getResponseHandler();
    }

    /**
     * Starts the listening Thread / loop.
     */
    public void startListening(ConnectionType type, boolean useSSL) throws IOException {
        try {
            PublicKey publicKey = PublicKeyObtainer.obtainPublicKey();
            socket = MPSQASClientSocketFactory.createClientSocket(type, address, port, tunnel, useSSL && type.isSSLSupported(), publicKey);
        } catch (GeneralSecurityException e) {
            throw (IOException) new IOException("Encryption property invalid.").initCause(e);
        }
        EncryptionHandler encryptionHandler = MainObjectFactory.getEncryptionHandler();
        sendMessage(new ExchangeKeyRequest(encryptionHandler.generateRequestKey()));
        receiver = new Thread(this);
        receiver.start();
        // Wait for 5 seconds or until the keys are exchanged.
        long timestamp = System.currentTimeMillis();
        while (!encryptionHandler.ready() && System.currentTimeMillis() - timestamp < 5000) {
            Thread.yield();
        }
        if (!encryptionHandler.ready()) {
            throw new IOException("Exchanging keys timed out.");
        }
    }

    /**
     * Continuously waits for Objects to come in from server and then sends them
     * to the ResponseHandler.
     */
    public void run() {
        Message message;
        boolean failed = false;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                message = (Message) socket.readObject();
                responseHandler.processMessage(message);
                failed = false;
            } catch (ClassCastException cce) {
                //The input Object didn't cast as Message
                System.out.println("The server sent an Object that was not an "
                        + "instance of "
                        + "com.topcoder.netCommon.mpsqas.communication.message.Message"
                        + ":");
                cce.printStackTrace();
            } catch (IOException ioe) {
                if (failed) {
                    //it failed twice in a row, give up..
                    //System.out.println("Connection to server lost: ");
                    //ioe.printStackTrace();

                    MainObjectFactory.getMainApplet().processConnectionLoss();
                    return;
                } else {
                    failed = true;
                    //let it try again
                }
            } catch (Exception e) //we don't want an exception to kill the listener
            {
                System.out.println("Error processing message:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a message using the ClientSocket's writeObject() method.
     */
    public void sendMessage(Message message) {
        try {
            socket.writeObject(message);
        } catch (Exception e) {
            //try one more time
            try {
                socket.writeObject(message);
            } catch (Exception e2) {
                //System.out.println("Connection to server lost: ");
                //e.printStackTrace();
                if (receiver != null) {
                    receiver.interrupt();
                }//shut down the listener thread (if it exists)
                MainObjectFactory.getMainApplet().processConnectionLoss();
            }
        }
    }

    /**
     * Closes the socket, shuts down the listener.
     */
    public void close() {
        if (receiver != null) {
            receiver.interrupt();
        }
        try {
            socket.close();
        } catch (Exception e) {
        }
    }
}
