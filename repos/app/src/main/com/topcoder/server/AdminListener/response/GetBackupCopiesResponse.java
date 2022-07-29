package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * A response to request for list of existing backup copies for specified round.
 * Instances of this class are created by 
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>GetBackupCopiesRequest</code> and contain the 
 * <code>Collection</code> containing BackupCopy objects representing 
 * the exisiting backup copies for specified round.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class GetBackupCopiesResponse extends ContestManagementAck {

    /**
     * A List of BackupCopy objects representing the existing
     * backup copies for requested round.
     * 
     * @see com.topcoder.server.common.BackupCopy
     */
    private List copies = null;                               

    /**
     * An int representing the ID of specified round.
     */
    private int roundID = 0;
    
    public GetBackupCopiesResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roundID);
        writer.writeObjectArray(copies.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roundID = reader.readInt();
        copies = Arrays.asList(reader.readObjectArray());
    }

    /**
     * Constructs new GetBackupCopiesResponse with specified List of
     * BackupCopy objects.
     *
     * @param  roundID an int representing the ID of requested round
     * @param  copies a List of BackupCopy objects representing
     *         existing backup copies for specified round
     * @throws IllegalArgumentException if given List is null
     */
    public GetBackupCopiesResponse(int roundID, List copies) {
        super();
        if (copies == null) {
            throw new IllegalArgumentException("List of copies is null");
        }
        this.roundID = roundID;
        this.copies = copies;
    }

    /**
     * Constructs new instance of GetBackupCopiesResponse specifying the
     * Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of 
     *        request
     */
    public GetBackupCopiesResponse(Throwable errorDetails) {
        super(errorDetails);
    }

    /**
     * Gets the List of BackupCopy objects representing existing backup copies
     * for requested round. Never returns null.
     *
     * @return a List of BackupCopy objects representing existing backup copies
     *         for specified round
     */
    public List getBackupCopies() {
        return copies;
    }

    /**
     * Gets the ID of requested round.
     *
     * @return an int representing the ID of requested round.
     */
    public int getRoundID() {
        return roundID;
    }
}
