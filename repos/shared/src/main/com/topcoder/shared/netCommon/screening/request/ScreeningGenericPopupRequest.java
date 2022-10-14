package com.topcoder.shared.netCommon.screening.request;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

import java.util.ArrayList;
import java.io.IOException;

public class ScreeningGenericPopupRequest extends ScreeningBaseRequest {

    protected int popupType;
    protected int button;
    protected ArrayList surveyData;

    public ScreeningGenericPopupRequest() {
        sync = false;
    }

    public ScreeningGenericPopupRequest(int popupType, int button, ArrayList surveyData) {
        sync = false;
        this.popupType = popupType;
        this.button = button;
        this.surveyData = surveyData;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(popupType);
        writer.writeInt(button);
        writer.writeArrayList(surveyData);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        popupType = reader.readInt();
        button = reader.readInt();
        surveyData = reader.readArrayList();
    }

    public int getRequestType() {
        return ScreeningConstants.POP_UP_GENERIC_RQ;
    }

    public int getPopupType() {
        return popupType;
    }

    public int getButton() {
        return button;
    }

    public ArrayList getSurveyData() {
        return surveyData;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.request.ScreeningGenericPopupRequest) [");
        ret.append("popupType = ");
        ret.append(popupType);
        ret.append(", ");
        ret.append("button = ");
        ret.append(button);
        ret.append(", ");
        ret.append("surveyData = ");
        if (surveyData == null) {
            ret.append("null");
        } else {
            ret.append(surveyData.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
