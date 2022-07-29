package com.topcoder.shared.netCommon.screening.response;

import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.screening.response.data.ScreeningProblemSet;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Respond to client request to login.
 */
public final class ScreeningGetProblemSetsResponse extends ScreeningBaseResponse {

    // The problem sets assigned for this session.
    private ScreeningProblemSet[] m_apProblemSets;
    // The languages allowed for this session.
    private ArrayList allowedLanguages;

    /**
     * Constructor needed for CS.
     */
    public ScreeningGetProblemSetsResponse () {
    }

    public ScreeningGetProblemSetsResponse (ScreeningProblemSet[] problemSets, ArrayList languages) {
        super();
        this.m_apProblemSets = problemSets;
        this.allowedLanguages = languages;
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
        writer.writeObjectArray(m_apProblemSets);
        writer.writeArrayList(allowedLanguages);
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
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_apProblemSets = (ScreeningProblemSet[])reader.readObjectArray(ScreeningProblemSet.class);
        allowedLanguages = reader.readArrayList();
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string repre8sentation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.response.ScreeningGetProblemSetsResponse) [");
        if(m_apProblemSets!=null)
            for(int i = 0; i<m_apProblemSets.length;i++){
                ret.append("m_apProblemSets["+i+"] = ");
                ret.append(m_apProblemSets[i].toString());
                ret.append(", ");
            }
        ret.append("]");
        return ret.toString();
    }
    /**
     * @return The problem sets for this session.
     */
    public ScreeningProblemSet[] getProblemSets() {
        return m_apProblemSets;
    }

    /**
     * @return The languages permitted for this session.
     */
    public ArrayList getAllowedLanguages() {
        return allowedLanguages;
    }
}
