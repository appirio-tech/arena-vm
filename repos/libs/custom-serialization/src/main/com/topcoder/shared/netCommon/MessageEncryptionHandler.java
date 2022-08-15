package com.topcoder.shared.netCommon;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Defines a unified tool to handle a two-stage SSL-like protocol negotiation. This tool should be used by both clients
 * and servers. At present, we use a 128-bit AES encryption algorithm. <br>
 * When the tool is used by a client, the client should first call <code>generateRequestKey()</code>, and send the
 * generated data to the server. When the client receives the reply from the server, it should call
 * <code>setReplyKey(byte[] key)</code>. Then the client can get the final key used for future encryptions by
 * <code>getFullKey()</code>. <br>
 * When the tool is used by a server, when the server receives the key exchange request, it should call
 * <code>setRequestKey(byte[] key)</code>. Then, a reply with the result of <code>generateReplyKey()</code> should
 * be sent back to the client. The server can call <code>getFullKey()</code> to retrieve the encryption key for future
 * communication with the specific client. <br>
 * This tool also contains two utility methods to encrypt and decrypt an object.
 * 
 * @author Qi Liu
 * @version $Id$
 */
public class MessageEncryptionHandler {
    /** The partial data of the request key during the key exchange. * */
    private byte[] requestKey;

    /** The partial data of the reply key during the key exchange. * */
    private byte[] replyKey;

    /** The key generator used to generate partial keys. * */
    private static final KeyGenerator keygen;

    /** The cipher object used to encrypt and decrypt an object. * */
    private static final Cipher cipher;

    /**
     * Static initializer, which initialize the cipher and the key generator.
     */
    static {
        try {
            keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            cipher = Cipher.getInstance("AES");
        } catch (GeneralSecurityException e) {
            throw new UnsupportedOperationException("AES 128-bit is not supported by Java.", e);
        }
    }

    /**
     * Decrypts an object using the given key.
     * 
     * @param obj the encrypted object.
     * @param key the key to decrypt the object.
     * @return the decrypted object from <code>obj</code>.
     * @throws GeneralSecurityException if decrypting the object fails.
     */
    public static Object unsealObject(SealedSerializable obj, Key key) throws GeneralSecurityException {
        try {
            synchronized (cipher) {
                cipher.init(Cipher.DECRYPT_MODE, key);
                return obj.getObject(cipher);
            }
        } catch (ClassNotFoundException e) {
            throw new GeneralSecurityException("Decryption failed.", e);
        } catch (IOException e) {
            throw new GeneralSecurityException("Decryption failed.", e);
        }
    }

    /**
     * Encrypts an object using the given key.
     * 
     * @param obj the plain object to be encrypted.
     * @param key the key to encrypt the object.
     * @return the encrypted object from <code>obj</code>.
     * @throws GeneralSecurityException if encrypting the object fails.
     */
    public static SealedSerializable sealObject(Object obj, Key key) throws GeneralSecurityException {
        try {
            synchronized (cipher) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return new SealedSerializable(obj, cipher);
            }
        } catch (IOException e) {
            throw new GeneralSecurityException("Encryption failed.", e);
        }
    }

    /**
     * Creates a new instances of <code>MessageEncryptionHandler</code>. A new instance is used during the key
     * exchange stage to obtain the final key.
     */
    public MessageEncryptionHandler() {
    }

    /**
     * Generates a partial key using the key generator. The 128-bit AES algorithm allows to use any 128-bit data as the
     * key.
     * 
     * @return the partial key generated by the key generator.
     */
    private byte[] generateKey() {
        synchronized (keygen) {
            return keygen.generateKey().getEncoded();
        }
    }

    /**
     * Generates the partial key sent by the client.
     * 
     * @return the partial key sent by the client.
     */
    public byte[] generateRequestKey() {
        requestKey = generateKey();
        return requestKey;
    }

    /**
     * Generates the partial key sent by the server.
     * 
     * @return the partial key sent by the server.
     */
    public byte[] generateReplyKey() {
        replyKey = generateKey();
        return replyKey;
    }

    /**
     * Sets the partial key sent by the client.
     * 
     * @param key the partial key sent by the client.
     * @throws IllegalArgumentException if the partial key is not acceptable.
     */
    public void setRequestKey(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("The request key must be 16 bytes.");
        }

        requestKey = key;
    }

    /**
     * Sets the partial key sent by the server.
     * 
     * @param key the partial key sent by the server.
     * @throws IllegalArgumentException if the partial key is not acceptable.
     */
    public void setReplyKey(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("The reply key must be 16 bytes.");
        }

        replyKey = key;
    }

    /**
     * Gets the final encryption key used for further communication. It should be called when both partial keys are
     * available (generated or set).
     * 
     * @return the final encryption key used for further communication.
     * @throws IllegalStateException if not both partial keys are available (generated or set).
     */
    public Key getFinalKey() {
        if (requestKey == null || replyKey == null) {
            throw new IllegalStateException("Either request key or reply key is missing.");
        }

        byte[] key = new byte[16];
        for (int i = 0; i < 16; ++i) {
            key[i] = (byte) (requestKey[i] + replyKey[i]);
        }

        return new SecretKeySpec(key, "AES");
    }
    
    /**
     * Gets a flag indicating if the keys are already exchanged.
     * 
     * @return <code>true</code> if the keys are already exchanged and ready; <code>false</code> otherwise.
     */
    public boolean isKeyFinal() {
        return requestKey != null && replyKey != null;
    }
}
