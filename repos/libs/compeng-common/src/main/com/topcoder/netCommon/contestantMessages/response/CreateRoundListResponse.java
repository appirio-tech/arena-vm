package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.RoundData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the list of available rounds, including loaded contest rounds and practice rounds.<br>
 * Use: When receiving this response, the client should update the mechanism for the current user to choose a round to
 * enter, register, etc.. All previous rounds with the same type should be replaced by this new list.<br>
 * Note: The response is sent directly by server without corresponding request. It can be sent during loading/updating a
 * contest, or refreshing the practice room list.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: CreateRoundListResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class CreateRoundListResponse extends BaseResponse {
    /** Represents the type of practice rounds. */
    public static final byte PRACTICE = 1;

    /** Represents the type of contest rounds. */
    public static final byte ACTIVE = 2;

    /**
     * Validates the round type.
     * 
     * @param type the type of the round.
     * @throws IllegalArgumentException if the round type is not practice or contest.
     */
    private static void validateType(int type) {
        if (type != PRACTICE && type != ACTIVE) {
            throw new IllegalArgumentException("Bad type: " + type);
        }
    }

    /** Represents the type of the round list. */
    private byte type;

    /** Represents the information of the rounds. */
    private RoundData[] roundData;

    /**
     * Creates a new instance of <code>CreateRoundListResponse</code>. It is required by custom serialization.
     */
    public CreateRoundListResponse() {
    }

    /**
     * Creates a new instance of <code>CreateRoundListResponse</code>. The list can only be a practice round list or
     * a contest round list. There is no copy.
     * 
     * @param type the type of the round list.
     * @param roundData the information of the rounds.
     * @throws IllegalArgumentException if <code>type</code> is not <code>PRACTICE</code> or <code>ACTIVE</code>.
     */
    public CreateRoundListResponse(byte type, RoundData[] roundData) {
        validateType(type);
        this.type = type;
        this.roundData = roundData;
    }

    /**
     * Gets the type of the round list.
     * 
     * @return the type of the round list.
     * @see #PRACTICE
     * @see #ACTIVE
     */
    public byte getType() {
        return type;
    }

    /**
     * Gets the information of the rounds. There is no copy.
     * 
     * @return the information of the rounds.
     */
    public RoundData[] getRoundData() {
        return roundData;
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        super.customReadObject(csReader);
        type = csReader.readByte();
        roundData = (RoundData[]) csReader.readObjectArray(RoundData.class);
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        super.customWriteObject(csWriter);
        csWriter.writeByte(type);
        csWriter.writeObjectArray(getRoundData());
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateRoundListResponse) [");
        ret.append("roundData = ");
        if (roundData == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < roundData.length; i++) {
                ret.append(roundData[i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
