package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to exchange a partial encryption key used to encrypt sensitive data transmitting over the network.
 * Once the server sends back the another partial encryption key, the two parts will be assembled to form a full working
 * encryption key used by a fast symmetric encryption algorithm, such as AES.<br>
 * Use: This request must be sent once the connection is made before any other requests. Any requests involving
 * <code>SealedSerializable</code> cannot be sent prior to exchange the symmetric encryption keys.<br>
 * Note: The key exchange protocol is similar to SSL. The partial keys are encrypted and exchanged using slow asymmetric
 * encryption algorithm such as RSA. The public key of the asymmetric encryption algorithm is pre-shared on the
 * client-side, while the private key is kept as a secret on the server-side. By doing this, the client-generated
 * partial keys cannot be intercepted by third parties and thus renders the whole communication secure. Replay attacks
 * is also difficult since the partial keys are generated randomly for each different connection.<br>
 * Important: This response contains the client-generated partial key only.
 * 
 * @author Qi Liu
 * @version $Id: ExchangeKeyRequest.java 72300 2008-08-13 08:33:29Z qliu $
 * @see LoginRequest
 * @see ReconnectRequest
 */
public class ExchangeKeyRequest extends BaseRequest {
    /** Represents the client-generated partial symmetric encryption key. */
    private byte[] key;

    /**
     * Creates a new instance of <code>ExchangeKeyRequest</code>. It is required by custom serialization.
     */
    public ExchangeKeyRequest() {
    }

    /**
     * Creates a new instance of <code>ExchangeKeyRequest</code>.
     * 
     * @param key the client-generated partial symmetric encryption key.
     */
    public ExchangeKeyRequest(byte[] key) {
        this.key = key;
    }

    /**
     * Gets the client-generated partial symmetric encryption key.
     * 
     * @return the client-generated partial symmetric encryption key.
     */
    public byte[] getKey() {
        return key;
    }

    public int getRequestType() {
        return ContestConstants.EXCHANGE_KEY_REQUEST;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        // Use asymmetric encryption
        writer.writeEncrypt(key);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        // Use asymmetric encryption
        key = (byte[]) reader.readEncrypt();
    }

    /**
     * Gets the string representation of this object
     * 
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.ExchangeKeyRequest) [");
        ret.append("keylength = ");
        ret.append(key.length);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
