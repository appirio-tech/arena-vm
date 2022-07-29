package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.response.data.CoderHistoryData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the receiver the submission/challenge/system testing history of a coder in a round.<br>
 * Use: This response is specific to <code>CoderHistoryRequest</code>. When receiving this response, the client
 * should show a window containing the history data of a coder.<br>
 * Note: This response is for all rounds.
 * 
 * @author Qi Liu
 * @version $Id: CoderHistoryResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class CoderHistoryResponse extends BaseResponse {
    /** Represents a flag indicating if the history is for a marathon round. */
    private boolean isLong;

    /** Represents the history data of the coder. */
    private CoderHistoryData[] data;

    /** Represents the handle of the coder whose history data is in the response. */
    private String name;

    /**
     * Creates a new instance of <code>CoderHistoryResponse</code>. It is required by custom serialization.
     */
    public CoderHistoryResponse() {
    }

    /**
     * Creates a new instance of <code>CoderHistoryResponse</code>. There is no copy.
     * 
     * @param name the handle of the coder whose history data is in the response.
     * @param isLong <code>true</code> if the history is for a marathon round; <code>false</code> otherwise.
     * @param data the history data of the coder.
     */
    public CoderHistoryResponse(String name, boolean isLong, CoderHistoryData[] data) {
        this.isLong = isLong;
        this.data = data;
        this.name = name;
    }

    /**
     * Gets the history data of the coder. There is no copy.
     * 
     * @return the history data.
     */
    public CoderHistoryData[] getHistoryData() {
        return data;
    }

    /**
     * Gets a flag indicating if the history is for a marathon round.
     * 
     * @return <code>true</code> if the history is for a marathon round; <code>false</code> otherwise.
     */
    public boolean isLongRound() {
        return isLong;
    }

    /**
     * Gets the handle of the coder whose history data is in the response.
     * 
     * @return the handle of the coder.
     */
    public String getName() {
        return name;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        isLong = reader.readBoolean();
        name = reader.readString();
        data = (CoderHistoryData[]) reader.readObjectArray(CoderHistoryData.class);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(isLong);
        writer.writeString(name);
        writer.writeObjectArray(data);
    }

    public String toString() {
        return "CoderHistoryResponse[name=" + name + ",isLong=" + isLong + ",data=" + data + "]";
    }
}
