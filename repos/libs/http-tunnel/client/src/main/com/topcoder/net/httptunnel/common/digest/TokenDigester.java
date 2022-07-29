/*
 * TokenDigester
 *
 * Created 05/04/2007
 */
package com.topcoder.net.httptunnel.common.digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.topcoder.shared.util.encoding.HexEncoding;

/**
 * TokenDigester is responsible for generating a digest to authenticate a
 * new connection with the server. <p>
 *
 * When a client must create a new connection to send a message to the server, this new connection
 * must be associatted to the connection the client has open for input messages. A digest must be included
 * as a header in the new output connection request. The digest is generated and validated using this class<p>
 *
 * This class can be used from multiple threads simultaneously.<p>
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class TokenDigester {
    /** Represents the encoding used to convert strings to bytes. */
    private static final String DIGEST_ENCODING = "UTF-8";
    
    /** Represents the message digester used to get the hash of the content. */
    private final MessageDigest digester;

    /**
     * Creates a new TokenDigester using SHA algorithm for digest generation
     *
     * @throws TokenDigesterException if The provider for the algorithm could not be found.
     */
    public TokenDigester() throws TokenDigesterException {
        try {
            digester = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
           throw new TokenDigesterException("Could not find algorithm for digest",e);
        }
    }

    /**
     * Generates a digest using the given arguments.
     *
     * @param token The token associated to the connection
     * @param connectionId The connection id.
     * @param extra Any extra variable value to add to the digest.
     *
     * @return An hexadecimal encoded digest
     */
    public String generateDigest(String token, int connectionId, Object extra) {
        byte[] digest;
        synchronized (digester) {
            try {
                digester.update(extra.toString().getBytes(DIGEST_ENCODING));
                digester.update(token.getBytes(DIGEST_ENCODING));
                digest = digester.digest(String.valueOf(connectionId).getBytes(DIGEST_ENCODING));
            } catch (UnsupportedEncodingException e) {
                throw (IllegalStateException) new IllegalStateException().initCause(e);
            }
        }
        return toHexString(digest);
    }

    /**
     * Encodes the array of bytes into hex representation. Each byte will be exactly two characters.
     * 
     * @param bytes the array of bytes to encode.
     * @return the encoded array of bytes.
     */
    private String toHexString(byte[] bytes) {
        return HexEncoding.toHexString(bytes);
    }

    /**
     * Validates that a given digest was generated using the given arguments
     *
     * @param token The token associatted to the connection
     * @param connectionId The connection id
     * @param extra The extra value used while generating the digest
     * @param digest The digest to validate
     *
     * @return true if the digest is valid.
     */
    public boolean isValidDigest(String token, int connectionId, Object extra, String digest) {
        return generateDigest(token, connectionId, extra).equals(digest);
    }
}
