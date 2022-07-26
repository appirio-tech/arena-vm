package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to exchange a partial encryption key used to encrypt sensitive data transmitting over the network.
 * Once this response is sent back the another partial encryption key, the two parts will be assembled to form a full
 * working encryption key used by a fast symmetric encryption algorithm, such as AES.<br>
 * Use: This response must be received before any other requests. Any requests involving <code>SealedSerializable</code>
 * cannot be sent prior to exchange the symmetric encryption keys.<br>
 * Note: The key exchange protocol is similar to SSL. The partial keys are encrypted and exchanged using slow asymmetric
 * encryption algorithm such as RSA. The public key of the asymmetric encryption algorithm is pre-shared on the
 * client-side, while the private key is kept as a secret on the server-side. By doing this, the client-generated
 * partial keys cannot be intercepted by third parties and thus renders the whole communication secure. Replay attacks
 * is also difficult since the partial keys are generated randomly for each different connection.<br>
 * Important: This response contains the server-generated partial key only.
 * 
 * @author Qi Liu
 * @version $Id: ExchangeKeyResponse.java 72300 2008-08-13 08:33:29Z qliu $
 * @see ExchangeKeyRequest
 */
public class ExchangeKeyResponse extends BaseResponse {
    /** Represents the server-generated partial symmetric encryption key. */
    private byte[] key;

    /**
     * Creates a new instance of <code>ExchangeKeyResponse</code>. It is required by custom serialization.
     */
    public ExchangeKeyResponse() {
    }

    /**
     * Creates a new instance of <code>ExchangeKeyResponse</code>.
     * 
     * @param key the server-generated partial symmetric encryption key.
     */
    public ExchangeKeyResponse(byte[] key) {
        this.key = key;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        // Use asymmetric encryption
        writer.writeEncrypt(key);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        // Use asymmetric encryption
        key = (byte[]) reader.readEncrypt();
    }

    /**
     * Gets the server-generated partial symmetric encryption key.
     * 
     * @return the server-generated partial symmetric encryption key.
     */
    public byte[] getKey() {
        return key;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.ExchangeKeyResponse) [keylength = ");
        ret.append(key.length);
        ret.append("]");
        return ret.toString();
    }
}
