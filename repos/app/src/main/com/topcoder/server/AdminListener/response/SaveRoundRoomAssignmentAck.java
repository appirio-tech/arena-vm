package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;



/**
 * A response to request to save details of round room assignment algorithm
 * for some round.
 * Instances of this class are created by 
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>SaveRoundRoomAssignmentRequest</code> and contain 
 * an ID of round which room assignment details were saved to database.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class SaveRoundRoomAssignmentAck extends ContestManagementAck {

    /**
     * An ID of round which room assignment details were successfully saved
     * to database.
     */
    private int roundID = 0;
    
    public SaveRoundRoomAssignmentAck() {
        
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
     * Constructs new instance of SaveRoundRoomAssignmentAck. Such instance
     * indicates about successful fulfilment of request.
     *
     * @param  roundID an ID of round which room assignment details were
     *         successfully saved
     * @throws IllegalArgumentException if given argument is less than zero
     */
    public SaveRoundRoomAssignmentAck(int roundID) {
        if( roundID < 0)
            throw new IllegalArgumentException("roundID must be greater than 0");
        this.roundID = roundID;
    }

    /**
     * Constructs new instance of SaveRoundRoomAssignmentAck specifying 
     * the Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of 
     *        request
     */
    public SaveRoundRoomAssignmentAck(Throwable errorDetails) {
        super( errorDetails);
    }

    /**
     * Gets the ID of round which room assignment details were successfully
     * saved to database. Prior to call this method <code>isSuccess()</code>
     * method should be called to check if the request was fulfiled 
     * successfully.
     *
     * @return an ID of round which room assignment details were saved to 
     *         database successfully.
     * @see    isSuccess()
     * @see    hasException()
     */
    public int getRoundID() {
        return roundID;
    }
}
