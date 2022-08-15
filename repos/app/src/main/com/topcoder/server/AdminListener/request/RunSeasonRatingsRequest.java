package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class RunSeasonRatingsRequest extends RoundIDCommand implements ProcessedAtBackEndRequest {

    private boolean shouldCommit, runByDivision;
    private int season;
    public RunSeasonRatingsRequest() {
        
    }

    public RunSeasonRatingsRequest(int roundId, boolean shouldCommit, boolean runByDivision, int season) {
        super(roundId);
        this.shouldCommit = shouldCommit;
        this.runByDivision = runByDivision;
        this.season = season;
    }
    
    public int getSeason() {
        return season;
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
        writer.writeInt(season);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        shouldCommit = reader.readBoolean();
        runByDivision = reader.readBoolean();
        season = reader.readInt();
    }
}

