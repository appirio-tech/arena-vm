/*
 * PublicKeyObtainer Created Jul 11, 2008
 */
package com.topcoder.client.security;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;

import com.topcoder.shared.util.encoding.Base64Encoding;

/**
 * Defines a helper class which can be used to obtain a public key from a resource file or from an array of bytes.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class PublicKeyObtainer {
    /**
     * Gets the public key from a property file in the resource. The propery file is stored in '/encryption.properties'
     * in the resource. It should contain two properties, 'Algorithm' and 'Key'. They define the encryption algorithm
     * used and the public key used respectively. The 'Key' property is a byte array encoded using BASE64. The byte
     * array is a X509 encoded public key.
     * 
     * @return the public key retrieved from the property file in the resource.
     * @throws IOException if the property file is missing or corrupted.
     * @throws NoSuchAlgorithmException if the encryption algorithm is not available.
     * @throws InvalidKeySpecException if the encoded key is invalid.
     */
    public static PublicKey obtainPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Properties prop = new Properties();
        prop.load(PublicKeyObtainer.class.getResourceAsStream("/encryption.properties"));
        String algorithm = prop.getProperty("Algorithm");
        byte[] decodedKey = Base64Encoding.decode64(prop.getProperty("Key"));
        return obtainKey(algorithm, decodedKey);
    }

    /**
     * Gets the public key. The algorithm and the X509-encoded key are given.
     * 
     * @param algorithm the encryption algorithm of the key.
     * @param decodedKey the byte array of the public key encoded in X509.
     * @return the public key retrieved from the algorithm and the given byte array.
     * @throws IOException if the property file is missing or corrupted.
     * @throws NoSuchAlgorithmException if the encryption algorithm is not available.
     * @throws InvalidKeySpecException if the encoded key is invalid.
     */
    public static PublicKey obtainKey(String algorithm, byte[] decodedKey) throws NoSuchAlgorithmException,
        InvalidKeySpecException {
        KeyFactory factory = KeyFactory.getInstance(algorithm);
        KeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return (PublicKey) factory.generatePublic(keySpec);
    }
}
