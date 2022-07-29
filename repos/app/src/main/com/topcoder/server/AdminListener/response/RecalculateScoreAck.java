package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class RecalculateScoreAck extends ContestManagementAck {
	
	private int roundId;
	private String handle;
        
        public RecalculateScoreAck() {
            super();
        }
        
        public void customWriteObject(CSWriter writer) throws IOException {
            super.customWriteObject(writer);
            writer.writeInt(roundId);
            writer.writeString(handle);
        }
        
        public void customReadObject(CSReader reader) throws IOException {
            super.customReadObject(reader);
            roundId = reader.readInt();
            handle = reader.readString();
        }
	
	public RecalculateScoreAck(int roundId, String handle) {
		this.roundId = roundId;
		this.handle = handle;
	}
	
	public RecalculateScoreAck(Throwable e) {
		super(e);
	}
	
	public int getRoundId() {
		return roundId;
	}
	
	public String getHandle() {
		return handle;
	}

}
