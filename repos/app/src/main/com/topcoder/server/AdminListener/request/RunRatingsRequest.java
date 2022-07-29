package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class RunRatingsRequest extends RoundIDCommand implements ProcessedAtBackEndRequest {

    private boolean shouldCommit, runByDivision;
    private int ratingType;
    public RunRatingsRequest() {
        
    }

    public RunRatingsRequest(int roundId, boolean shouldCommit, boolean runByDivision, int ratingType) {
        super(roundId);
        this.shouldCommit = shouldCommit;
        this.runByDivision = runByDivision;
        this.ratingType = ratingType;
    }
    
    public int getRatingType() {
        return ratingType;
    }

    public boolean getShouldCommit() {
        return shouldCommit;
    }

    public boolean getRunByDivision() {
        return runByDivision;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(shouldCommit);
        writer.writeBoolean(runByDivision);
        writer.writeInt(ratingType);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        shouldCommit = reader.readBoolean();
        runByDivision = reader.readBoolean();
        ratingType = reader.readInt();
    }
}

