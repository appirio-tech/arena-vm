package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.Question;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to make the client showing a popup dialog. The dialog may contain feedbacks that need the current
 * user to enter data.<br>
 * Use: This response can be sent as responses of multiple requests, or be sent by server directly without any
 * corresponding requests. Once received the response, the client should show a dialog frame immediately with required
 * elements in it. When necessary, the feedback should be sent back to the server via <code>GenericPopupRequest</code>
 * or <code>RegisterRequest</code>.<br>
 * Note: The type of the popup dialog (<code>type1</code>) can be <code>ContestConstants.ROOM_MOVE</code>,
 * <code>ContestConstants.CONTEST_REGISTRATION</code>, <code>ContestConstants.CONTEST_REGISTRATION_SURVEY</code>,
 * or <code>ContestConstants.SUBMIT_RESULTS</code>. The type of the text message can be
 * <code>ContestConstants.TEXT_AREA</code>, <code>ContestConstants.LABEL</code>, or
 * <code>ContestConstants.WRAPPING_TEXT_AREA</code>.
 * 
 * @author Lars Backstrom
 * @version $Id: PopUpGenericResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class PopUpGenericResponse extends BaseResponse {
    /** Represents the title of the popup dialog. */
    private String title;

    /** Represents the text message in the popup dialog. */
    private String msg;

    /** Represents the type of the popup dialog. */
    private int type1;

    /** Represents the type of the text message in the popup dialog. */
    private int type2;

    /** Represents the texts on buttons in the popup dialog. */
    private ArrayList data;

    /**
     * Represents the survey questions if the popup dialog is <code>ContestConstants.CONTEST_REGISTRATION_SURVEY</code>
     * type.
     */
    ArrayList surveyQuestions;

    /**
     * Represents the survey message if the popup dialog is <code>ContestConstants.CONTEST_REGISTRATION_SURVEY</code>
     * type.
     */
    String surveyMessage;

    /** Represents some data attached to the popup dialog. */
    Object o;

    /**
     * Creates a new instance of <code>PopUpGenericResponse</code>. It is required by custom serialization.
     */
    public PopUpGenericResponse() {
    }

    /**
     * Creates a new instance of <code>PopUpGenericResponse</code>. This constructor cannot be used for
     * <code>ContestConstants.CONTEST_REGISTRATION_SURVEY</code> type, since the survey-related fields are
     * uninitialized. There is no copy.
     * 
     * @param title the title of the popup dialog.
     * @param msg the text message in the popup dialog.
     * @param type1 the type of the popup dialog.
     * @param type2 the type of the text message in the popup dialog.
     * @param buttons the list of texts on the buttons in the popup dialog.
     * @param o some data attached to the popup dialog.
     */
    public PopUpGenericResponse(String title, String msg, int type1, int type2, ArrayList buttons, Object o) {
        this.title = title;
        this.msg = msg;
        this.type1 = type1;
        this.type2 = type2;
        this.data = buttons;
        this.o = o;
    }

    /**
     * Creates a new instance of <code>PopUpGenericResponse</code>. All lists are not copied. The list of survey
     * questions contains instance of <code>Question</code>.
     * 
     * @param title the title of the popup dialog.
     * @param msg the text message in the popup dialog.
     * @param type1 the type of the popup dialog.
     * @param type2 the type of the text message in the popup dialog.
     * @param buttons the list of texts on the buttons in the popup dialog.
     * @param surveyQuestions the survey questions.
     * @param surveyMessage the survey message.
     * @param o some data attached to the popup dialog.
     * @see Question
     */
    public PopUpGenericResponse(String title, String msg, int type1, int type2, ArrayList buttons,
        ArrayList surveyQuestions, String surveyMessage, Object o) {
        this.title = title;
        this.msg = msg;
        this.type1 = type1;
        this.type2 = type2;
        this.data = buttons;
        this.o = o;
        this.surveyQuestions = surveyQuestions;
        this.surveyMessage = surveyMessage;
    }

    /**
     * Creates a new instance of <code>PopUpGenericResponse</code>. It is intended to be used to show simple messages
     * to the current user. The buttons in the dialog will depend on the type of the popup dialog.
     * 
     * @param title the title of the popup dialog.
     * @param msg the text message in the popup dialog.
     * @param type1 the type of the popup dialog.
     * @param type2 the type of the text message in the popup dialog.
     */
    public PopUpGenericResponse(String title, String msg, int type1, int type2) {
        this.title = title;
        this.msg = msg;
        this.type1 = type1;
        this.type2 = type2;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(title);
        writer.writeString(msg);
        writer.writeInt(type1);
        writer.writeInt(type2);
        if (type1 != ContestConstants.GENERIC) {
            writer.writeArrayList(data);
            writer.writeObject(o);
        }
        if (type1 == ContestConstants.CONTEST_REGISTRATION_SURVEY) {
            writer.writeArrayList(surveyQuestions);
            writer.writeString(surveyMessage);
        }
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        title = reader.readString();
        msg = reader.readString();
        type1 = reader.readInt();
        type2 = reader.readInt();
        if (type1 != ContestConstants.GENERIC) {
            data = reader.readArrayList();
            o = reader.readObject();
        }
        if (type1 == ContestConstants.CONTEST_REGISTRATION_SURVEY) {
            surveyQuestions = reader.readArrayList();
            surveyMessage = reader.readString();
        }
    }

    /**
     * Gets the title of the popup dialog.
     * 
     * @return the title of the popup dialog.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the text message in the popup dialog.
     * 
     * @return the text message in the popup dialog.
     */
    public String getMessage() {
        return msg;
    }

    /**
     * Gets the type of the popup dialog.
     * 
     * @return the type of the popup dialog.
     * @see ContestConstants#ROOM_MOVE
     * @see ContestConstants#CONTEST_REGISTRATION
     * @see ContestConstants#CONTEST_REGISTRATION_SURVEY
     * @see ContestConstants#SUBMIT_RESULTS
     */
    public int getType1() {
        return type1;
    }

    /**
     * Gets the type of the text message in the popup dialog.
     * 
     * @return the type of the text message in the popup dialog.
     * @see ContestConstants#TEXT_AREA
     * @see ContestConstants#LABEL
     * @see ContestConstants#WRAPPING_TEXT_AREA
     */
    public int getType2() {
        return type2;
    }

    /**
     * Gets the list of texts on the buttons in the popup dialog. There is no copy.
     * 
     * @return the list of texts on the buttons in the popup dialog.
     */
    public ArrayList getButtons() {
        return data;
    }

    /**
     * Gets the data attached to the popup dialog.
     * 
     * @return the data attached to the popup dialog.
     */
    public Object getMoveData() {
        return o;
    }

    /**
     * Gets the survey questions. There is no copy. The list contains instances of <code>Question</code>.
     * 
     * @return the survey questions.
     */
    public ArrayList getSurveyQuestions() {
        return surveyQuestions;
    }

    /**
     * Gets the survey message.
     * 
     * @return the survey message.
     */
    public String getSurveyMessage() {
        return surveyMessage;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.PopUpGenericResponse) [");
        ret.append("title = ");
        if (title == null) {
            ret.append("null");
        } else {
            ret.append(title.toString());
        }
        ret.append(", ");
        ret.append("msg = ");
        if (msg == null) {
            ret.append("null");
        } else {
            ret.append(msg.toString());
        }
        ret.append(", ");
        ret.append("type1 = ");
        ret.append(type1);
        ret.append(", ");
        ret.append("type2 = ");
        ret.append(type2);
        ret.append(", ");
        ret.append("data = ");
        if (data == null) {
            ret.append("null");
        } else {
            ret.append(data.toString());
        }
        ret.append(", ");
        ret.append("surveyQuestions = ");
        if (surveyQuestions == null) {
            ret.append("null");
        } else {
            ret.append(surveyQuestions.toString());
        }
        ret.append(", ");
        ret.append("surveyMessage = ");
        if (surveyMessage == null) {
            ret.append("null");
        } else {
            ret.append(surveyMessage.toString());
        }
        ret.append(", ");
        ret.append("o = ");
        if (o == null) {
            ret.append("null");
        } else {
            ret.append(o.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}