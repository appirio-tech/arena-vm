package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to send the popup dialog result back to the server. If there is any data needed to be attached to
 * the popup dialog, the data will be sent back as well.<br>
 * Use: At present, the only use would be if the user attepmted to resubmit a solution. When user resubmits a solution,
 * the server will respond with a popup dialog indicating the warning of the resubmission penalty. If the user agrees to
 * resubmit, this request is sent.<br>
 * Note: All other usage of this request will be ignored.
 * 
 * @author Walter Mundt
 * @version $Id: GenericPopupRequest.java 72163 2008-08-07 07:51:04Z qliu $
 */
public class GenericPopupRequest extends BaseRequest {
    /** Represents the type of the popup dialog. */
    protected int popupType;

    /** Represents the button clicked in the popup dialog. */
    protected int button;

    /** Represents the data attached to the popup dialog if any. */
    protected ArrayList surveyData;

    /**
     * Creates a new instance of <code>GenericPopupRequest</code>. It is required by custom serialization.
     */
    public GenericPopupRequest() {
    }

    /**
     * Creates a new instance of <code>GenericPopupRequest</code>.
     * 
     * @param popupType the type of the popup dialog.
     * @param button the button clicked in the popup dialog.
     * @param surveyData the data attached the popup dialog if any.
     * @see #getButton()
     * @see #getPopupType()
     * @see #getSurveyData()
     */
    public GenericPopupRequest(int popupType, int button, ArrayList surveyData) {
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
        return ContestConstants.POP_UP_GENERIC_RQ;
    }

    /**
     * Gets the type of the popup dialog.
     * 
     * @return the type of the popup dialog.
     * @see ContestConstants#SUBMIT_PROBLEM
     */
    public int getPopupType() {
        return popupType;
    }

    /**
     * Gets the button clicked in the popup dialog.
     * <ul>
     * <li>For resubmitting, 0 means OK, and 1 means cancel.</li>
     * </ul>
     * 
     * @return the button clicked in the popup dialog.
     */
    public int getButton() {
        return button;
    }

    /**
     * Gets the data attached to the popup dialog.
     * <ul>
     * <li>For resubmitting, the first item in the array list is an <code>Integer</code> object representing the
     * problem component ID of the resubmitted solution.</li>
     * </ul>
     * 
     * @return the data attached to the popup dialog.
     */
    public ArrayList getSurveyData() {
        return surveyData;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.GenericPopupRequest) [");
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
