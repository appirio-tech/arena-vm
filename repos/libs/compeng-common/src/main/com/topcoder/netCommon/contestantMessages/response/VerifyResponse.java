package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the verification class byte-code to the client.<br>
 * Use: This response is specific to <code>VerifyRequest</code>. When the client receives the verification class
 * byte-code, the byte-code should be loaded via a memory class loader. The method <code>int verify()</code> in class
 * <code>com.topcoder.temporary.Verify</code> should be evaluated. The returned integer value should be sent back to
 * server via <code>VerifyResultRequest</code>.<br>
 * Note: For all non-TC signed clients, this response can be ignored.
 * 
 * @author Qi Liu
 * @version $Id: VerifyResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public class VerifyResponse extends BaseResponse {
    /** Represents the byte code of the verification class. */
    private byte[] verifyCode;

    /**
     * Creates a new instance of <code>VerifyResponse</code>. It is required by custom serialization.
     */
    public VerifyResponse() {
    }

    /**
     * Creates a new instance of <code>VerifyResponse</code>. There is no copy.
     * 
     * @param verifyCode the byte code of the verification class.
     */
    public VerifyResponse(byte[] verifyCode) {
        this.verifyCode = verifyCode;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeByteArray(verifyCode);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        verifyCode = reader.readByteArray();
    }

    /**
     * Gets the byte code of the verification class. There is no copy.
     * 
     * @return the byte code.
     */
    public byte[] getVerifyCode() {
        return verifyCode;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.VerifyResponse) [codelength = ");
        ret.append(verifyCode.length);
        ret.append("]");
        return ret.toString();
    }
}
