package com.topcoder.shared.netCommon.screening.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.Problem;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Open component to be loaded into editor window.
 */
public class ScreeningOpenComponentResponse extends ScreeningBaseResponse {

    // Identifier for this component.
    private long componentID;
    // Code completed thus far.
    private String code;
    private int editable;
    // Language used in coding this component.
    private Integer languageID;
    // Type for problem to be delivered to client.
    private int type;            //RO, RW, View
    // Problem object to be delivered to client.
    private Problem problem;
    private long openTime;
    private long length;

    /**
     * Constructor needed for CS.
     */
    public ScreeningOpenComponentResponse() {
    }

    /**
     * Main constructor.
     * @param componentID Identifier for this component.
     * @param code Code completed for this component.
     * @param editable
     * @param languageID Language used in coding this component.
     */
    public ScreeningOpenComponentResponse(long componentID, String code, int editable, int languageID, int type, Problem problem, long openTime, long length) {
        this.componentID = componentID;
        this.code = code;
        this.editable = editable;
        this.languageID = new Integer(languageID);
        this.problem = problem;
        this.type = type;
        this.openTime = openTime;
        this.length = length;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(componentID);
        writer.writeString(code);
        writer.writeInt(editable);
        writer.writeObject(languageID);
        writer.writeInt(type);
        writer.writeObject(problem);
        writer.writeLong(openTime);
        writer.writeLong(length);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param reader the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws java.io.ObjectStreamException exception during reading
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        componentID = reader.readLong();
        code = reader.readString();
        editable = reader.readInt();
        languageID = (Integer) reader.readObject();
        type = reader.readInt();
        problem = (Problem) reader.readObject();
        openTime = reader.readLong();
        length = reader.readLong();
    }

    /**
     * @return Identifier for this component.
     */
    public long getComponentID() {
        return componentID;
    }

    /**
     * @return Code finished for this component.
     */
    public String getCode() {
        return code;
    }

    /**
     * @return
     */
    public int getEditable() {
        return editable;
    }

    /**
     * @return Language used in coding this component.
     */
    public Integer getLanguageID() {
        return languageID;
    }
    public Problem getProblem() {
        return problem;
    }
    public int getProblemType() {
        return type;
    }

    public long getOpenTime() {
        return openTime;
    }
    public long getLength() {
        return length;
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.response.ScreeningOpenComponentResponse) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("code = ");
        if (code == null) {
            ret.append("null");
        } else {
            ret.append(code.toString());
        }
        ret.append(", ");
        ret.append("editable = ");
        ret.append(editable);
        ret.append(", ");
        ret.append("language = ");
        ret.append(languageID);
        ret.append(", ");
        ret.append("type = ");
        ret.append(type);
        ret.append(", ");
        ret.append("openTime = ");
        ret.append(openTime);
        ret.append(", ");
        ret.append("length = ");
        ret.append(length);
        ret.append(", ");
        ret.append("problem = ");
        if (problem == null) {
            ret.append("null");
        } else {
            ret.append(problem.toString());
        }
        ret.append("]");
        return ret.toString();
    }
}
