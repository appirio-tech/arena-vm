package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.language.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class SolutionInformation
        implements CustomSerializable, Cloneable, Serializable {

    private int solutionId = -1;
    private String solutionText = "";
    private String handle = "";
    private boolean primary = false;
    private Language language = JavaLanguage.JAVA_LANGUAGE;

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(solutionId);
        writer.writeString(solutionText);
        writer.writeString(handle);
        writer.writeBoolean(primary);
        writer.writeObject(language);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        solutionId = reader.readInt();
        solutionText = reader.readString();
        handle = reader.readString();
        primary = reader.readBoolean();
        language = (Language) reader.readObject();
    }

    public void setSolutionId(int solutionId) {
        this.solutionId = solutionId;
    }

    public int getSolutionId() {
        return solutionId;
    }

    public void setText(String solutionText) {
        this.solutionText = solutionText;
    }

    public String getText() {
        return solutionText;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getHandle() {
        return handle;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }
}
