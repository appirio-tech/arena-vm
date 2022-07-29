package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.RoundData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the client of updates/changes to the list of active rounds.<br>
 * Use: This response is sent whenever a new contest is loaded, or whever a contest is unloaded. A
 * <code>PhaseDataResponse</code> is usually sent for each of these responses the client receives.<br>
 * Note: This response is sent as updates to the list in <code>CreateRoundListResponse</code>. However, unlike
 * <code>CreateRoundListResponse</code>, this response only contains updates to the active round list, not practice
 * round list. The practice round list will always be sent by <code>CreateRoundListResponse</code>.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: UpdateRoundListResponse.java 72385 2008-08-19 07:00:36Z qliu $
 * @see CreateRoundListResponse
 */
public class UpdateRoundListResponse extends BaseResponse {
    /** Represents the round to be loaded or unloaded. */
    private RoundData roundData;

    /** Represents the action of loading or unloading. */
    private int action;

    /**
     * Creates a new instance of <code>UpdateRoundListResponse</code>. It is required by custom serialization.
     */
    public UpdateRoundListResponse() {
    }

    /** Represents the action of loading a round. */
    public static final int ACTION_ADD = 1;

    /** Represents the action of unloading a round. */
    public static final int ACTION_REMOVE = 2;

    /**
     * Creates a new instance of <code>UpdateRoundListResponse</code>.
     * 
     * @param action the action of loading or unloading.
     * @param roundData the round to be loaded or unloaded.
     */
    public UpdateRoundListResponse(int action, RoundData roundData) {
        this.action = action;
        this.roundData = roundData;
    }

    /**
     * Gets the round to be loaded or unloaded.
     * 
     * @return the round data.
     */
    public RoundData getRoundData() {
        return roundData;
    }

    /**
     * Gets the action of loading or unloading.
     * 
     * @return the action.
     */
    public int getAction() {
        return action;
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        super.customReadObject(csReader);
        this.action = csReader.readInt();
        this.roundData = (RoundData) csReader.readObject();
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        super.customWriteObject(csWriter);
        csWriter.writeInt(action);
        csWriter.writeObject(getRoundData());
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UpdateRoundListResponse) [");
        ret.append("roundData = ");
        if (roundData == null) {
            ret.append("null");
        } else {
            ret.append(roundData.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
