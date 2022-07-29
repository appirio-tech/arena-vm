package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 * A response to request to perform warehouse load response
 * Instances of this class are created by
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>SetRoundTermsRequest</code> and contain no data.
 * They just serve to indicate that terms for specified round were persisted
 * in database (either successfully or unsuccessfully).
 *
 * @author  TCSDESIGNER
 * @author  TCSDEVELOPER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class SetRoundTermsAck extends ContestManagementAck {

    /**
     * An ID of round which terms were set to.
     */
    private int roundID = 0;
    
    public SetRoundTermsAck() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roundID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roundID = reader.readInt();
    }

    /**
     * Constructs new instance of SetRoundTermsAck. Such instance indicates
     * about successful fulfilment of request.
     *
     * @param roundID an ID of round which terms were set to.
     */
    public SetRoundTermsAck(int roundID) {
        this.roundID = roundID;
    }

    /**
     * Constructs new instance of SetRoundTermsAck specifying the
     * Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of
     *        request
     */
    public SetRoundTermsAck(Throwable errorDetails) {
        super(errorDetails);
    }

    /**
     * Gets the ID of round which terms were set to. Prior to call this method
     * <code>isSuccess()</code> method should be called to check if the request
     * was fulfiled successfully.
     *
     * @return an ID of round.
     * @see    isSuccess()
     * @see    hasException()
     */
    public int getRoundID() {
        return roundID;
    }
}
