package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to execute the system tests in a practice room for problem components.<br>
 * Use: In practice room, execution of system tests is triggered by user instead of automatically. This request it used
 * to trigger the system test execution procedure of problem components submitted by the current user in that room.<br>
 * Note: The current user must be in the practice room. The problem components must be assigned to that practice room.
 * 
 * @author Walter Mundt
 * @version $Id: PracticeSystemTestRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class PracticeSystemTestRequest extends BaseRequest {
    /** Represents the ID of the practice room where the system test is triggered. */
    int roomID;

    /** Represents the IDs of the problem components to be system tested. */
    int[] componentIds;

    /**
     * Creates a new instance of <code>PracticeSystemTestRequest</code>. It is required by custom serialization.
     */
    public PracticeSystemTestRequest() {
    }

    /**
     * Creates a new instance of <code>PracticeSystemTestRequest</code>. There is no copy on the problem component
     * IDs.
     * 
     * @param roomID the ID of the practice room where the system test is triggered.
     * @param componentsId the IDs of the problem components to be system tested.
     */
    public PracticeSystemTestRequest(int roomID, int[] componentsId) {
        this.roomID = roomID;
        this.componentIds = componentsId;
    }

    public int getRequestType() {
        return ContestConstants.PRACTICE_SYSTEM_TEST;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roomID);
        writer.writeObject(componentIds);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        roomID = reader.readInt();
        componentIds = (int[]) reader.readObject();
    }

    /**
     * Gets the ID of the room where the system test is triggered.
     * 
     * @return the room ID.
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Gets the IDs of the problem components to be system tested. There is no copy.
     * 
     * @return the problem component IDs.
     */
    public int[] getComponentIds() {
        return componentIds;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.PracticeSystemTestRequest) [");
        ret.append("roomID = ");
        ret.append(roomID);
        ret.append(", ");
        ret.append("componentIds = [");
        for (int i = 0; i < componentIds.length; i++) {
            ret.append(",").append(componentIds[i]);
        }
        ret.append("]]");
        return ret.toString();
    }

}
