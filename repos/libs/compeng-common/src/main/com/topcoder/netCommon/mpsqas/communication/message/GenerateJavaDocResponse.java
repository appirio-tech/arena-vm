package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * @author mitalub
 */
public class GenerateJavaDocResponse extends Message {

    public GenerateJavaDocResponse() {
    }

    public GenerateJavaDocResponse(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeString(html);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        html = reader.readString();
    }

    protected String html;
}
