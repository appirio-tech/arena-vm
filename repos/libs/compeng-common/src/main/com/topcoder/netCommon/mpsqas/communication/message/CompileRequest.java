package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.language.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class CompileRequest
        extends Message {

    private HashMap codeFiles;
    private Language language;

    public CompileRequest() {
        this(new HashMap(), null);
    }

    public CompileRequest(HashMap codeFiles, Language language) {
        this.codeFiles = codeFiles;
        this.language = language;
    }

    public HashMap getCodeFiles() {
        return codeFiles;
    }

    public Language getLanguage() {
        return language;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeHashMap(codeFiles);
        writer.writeObject(language);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        codeFiles = reader.readHashMap();
        language = (Language) reader.readObject();
    }
}
