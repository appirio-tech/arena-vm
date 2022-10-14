package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class InsertPracticeRoomRequest extends RoundIDCommand implements ProcessedAtBackEndRequest {

    private String name;
    private int groupID;

    public InsertPracticeRoomRequest(int roundId, String name, int groupID) {
        super(roundId);
        this.name = name;
	this.groupID = groupID;
    }
    
    public InsertPracticeRoomRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(name);
	writer.writeInt(groupID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        name = reader.readString();
	groupID = reader.readInt();
    }

    public String getName() {
        return name;
    }

    public int getGroupID() {
	return groupID;
    }
}

