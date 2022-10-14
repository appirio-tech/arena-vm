package com.topcoder.shared.netCommon.screening.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Send notifications to client to be popped up in applet frame.
 */
public class ScreeningPopUpGenericResponse extends ScreeningBaseResponse {

    // Title to be displayed in popup.
    private String title;
    // Message to be displayed in popup.
    private String msg;
    private int type1;
    private int type2;
    // Buttons to be added to popup box.
    private ArrayList data;
    Object o;

    /**
     * Constructor used by CS.
     */
    public ScreeningPopUpGenericResponse() {
        sync = false;
    }

    /**
     *  @param title Title shown in popup box.
     *  @param msg Message shown in popup box.
     *
     */
    public ScreeningPopUpGenericResponse(
            String title,
            String msg,
            int type1,
            int type2,
            ArrayList buttons,
            Object o) {

        super();
        sync = false;
        this.title = title;
        this.msg = msg;
        this.type1 = type1;
        this.type2 = type2;
        this.data = buttons;
        this.o = o;
    }

    /**
     *  @param title Title shown in popup box.
     *  @param msg Message shown in popup box.
     *
     */
    public ScreeningPopUpGenericResponse(
            String title,
            String msg,
            int type1,
            int type2) {

        super();
        sync = false;
        this.title = title;
        this.msg = msg;
        this.type1 = type1;
        this.type2 = type2;
    }

    /**
     * @return Title for this popup.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return Message for this popup.
     */
    public String getMessage() {
        return msg;
    }

    public int getType1() {
        return type1;
    }

    public int getType2() {
        return type2;
    }

    /**
     * @return Buttons for this popup.
     */
    public ArrayList getButtons() {
        return data;
    }


    public Object getMoveData() {
        return o;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(title);
        writer.writeString(msg);
        writer.writeInt(type1);
        writer.writeInt(type2);

        writer.writeArrayList(data);
        writer.writeObject(o);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param reader the custom serialization reader
     * @throws IOException           exception during reading
     * @throws ObjectStreamException exception during reading
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        title = reader.readString();
        msg = reader.readString();
        type1 = reader.readInt();
        type2 = reader.readInt();

        data = reader.readArrayList();
        o = reader.readObject();
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.response.ScreeningPopUpGenericResponse) [");
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
