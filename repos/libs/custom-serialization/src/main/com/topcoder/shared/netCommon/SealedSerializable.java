/*
 * Copyright (C) 2008 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.netCommon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Provides a mechanism similar to <code>javax.crypto.SealedObject</code>. It contains the encrypted form of a
 * serializable object. The object can only be accessed by providing the correct cipher, since the object itself is not
 * directly stored in this class. This class provides a way to support the <code>CustomSerializable</code> interface
 * used in the custom serializable component. It simply writes the encrypted form of the serialized object as a byte
 * array. Therefore, without a proper cipher, it cannot be decrypted even if it is intercepted during transmission.
 * 
 * Changes in version 1.1 (LoginResponse sendEvent problem fix and upgrade netty-socketio library):
 *  - Adds the getter for encoded array.
 *  
 * @author Qi Liu, flytoj2ee
 * @version 1.1
 */
public class SealedSerializable implements CustomSerializable, Serializable {
    /** The encrypted form of the serialized object. * */
    private byte[] encoded;

    /**
     * Creates a new instance of <code>SealedSerializable</code> class. It is required by
     * <code>CustomSerializable</code> interface.
     */
    public SealedSerializable() {
    }

    /**
     * Creates a new instance of <code>SealedSerializable</code> class. The instance contains the given object, and
     * encrypts the given object by the given cipher. The cipher should be initilized in
     * <code>Cipher.ENCRYPT_MODE</code>.
     * 
     * @param obj the object to be encrypted and stored.
     * @param cipher the cipher used to encrypt the object.
     * @throws IllegalBlockSizeException if the given cipher is a block cipher, no padding has been requested, and the
     *             total input length (i.e., the length of the serialized object contents) is not a multiple of the
     *             cipher's block size.
     * @throws IOException if I/O error occurs during serialization.
     */
    public SealedSerializable(Object obj, Cipher cipher) throws IllegalBlockSizeException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        try {
            oos.writeObject(obj);
            encoded = cipher.doFinal(baos.toByteArray());
        } catch (BadPaddingException e) {
            // should never happen
        } finally {
            oos.close();
        }
    }

    /**
     * Retrieves the encrypted object using the given cipher. The cipher should be initilized in
     * <code>Cipher.DECRYPT_MODE</code>.
     * 
     * @param cipher the cipher used to decrypt the object.
     * @return the object encapsulated in this instance.
     * @throws IllegalBlockSizeException if the given cipher is a block cipher, no padding has been requested, and the
     *             total input length is not a multiple of the cipher's block size.
     * @throws BadPaddingException if the given cipher has been initialized for decryption, and padding has been
     *             specified, but the input data does not have proper expected padding bytes.
     * @throws ClassNotFoundException if an error occurs during de-serialiazation.
     * @throws IOException if an error occurs during de-serialiazation.
     */
    public Object getObject(Cipher cipher) throws IllegalBlockSizeException, BadPaddingException,
        ClassNotFoundException, IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(cipher.doFinal(encoded));
        ObjectInputStream ois = new ObjectInputStream(bais);
        try {
            return ois.readObject();
        } finally {
            ois.close();
        }
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeByteArray(encoded);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        encoded = reader.readByteArray();
    }
    
    /**
     * Gets the encoded array.
     * 
     * @return the encoded array.
     * @since 1.1
     */
    public byte[] getEncoded() {
        return encoded;
    }
}
