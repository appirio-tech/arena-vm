package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.response.data.VoteResultsCoder;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the handle, rating and total score of all team members in the team of the current user
 * during a 'Weakest Link' round.<br>
 * Use: This response is specific to <code>WLMyTeamInfoRequest</code>. The information in the response should be
 * shown to the current user.
 * 
 * @author Qi Liu
 * @version $Id: WLMyTeamInfoResponse.java 72385 2008-08-19 07:00:36Z qliu $
 * @see WLMyTeamInfoRequest
 */
public final class WLMyTeamInfoResponse extends BaseResponse {
    /** Represents the information of all team members in the team. */
    private VoteResultsCoder[] coders;

    /**
     * Creates a new instance of <code>WLMyTeamInfoResponse</code>. It is required by custom serialization.
     */
    public WLMyTeamInfoResponse() {
    }

    /**
     * Creates a new instance of <code>WLMyTeamInfoResponse</code>. There is no copy.
     * 
     * @param coders the information of all team members in the team of the current user.
     */
    public WLMyTeamInfoResponse(VoteResultsCoder[] coders) {
        this.coders = coders;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObjectArray(coders);
    }

    public void customReadObject(CSReader reader) throws IOException {
        coders = (VoteResultsCoder[]) reader.readObjectArray(VoteResultsCoder.class);
    }

    /**
     * Gets the information of all team members in the team of the current user. There is no copy.
     * 
     * @return the information of all team members.
     */
    public VoteResultsCoder[] getCoders() {
        return coders;
    }
}
