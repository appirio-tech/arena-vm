/*
 * TestInvocation
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.test.dev;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class TestInvocation implements Invocation {
    private String text;

    public TestInvocation() {
    }
    
    public TestInvocation(String text) {
        this.text = text;;
    }

    public Object run(InvocationContext context) {
        return text;
    }
    
    public String toString() {
        return text == null ? "null" : text.substring(0, Math.min(20, text.length()));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        text = reader.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(text);
    }
}
