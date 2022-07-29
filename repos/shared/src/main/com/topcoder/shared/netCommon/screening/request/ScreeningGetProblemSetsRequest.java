package com.topcoder.shared.netCommon.screening.request;

import java.io.IOException;
import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ScreeningGetProblemSetsRequest extends ScreeningBaseRequest{
    public ScreeningGetProblemSetsRequest(){
        super();
    }
    public int getRequestType(){
        return ScreeningConstants.GET_PROBLEM_SETS;
    }
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
    }
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
    }
}
