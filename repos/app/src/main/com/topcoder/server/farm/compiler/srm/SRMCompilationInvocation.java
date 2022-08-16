/*
 * SRMCompilationInvocation
 * 
 * Created 12/14/2006
 */
package com.topcoder.server.farm.compiler.srm;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.server.common.Submission;
import com.topcoder.services.compiler.invoke.FarmCompiler;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * SRMCompilationInvocation
 * 
 * @author Diego Belfer (mural)
 * @version $Id: SRMCompilationInvocation.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class SRMCompilationInvocation implements Invocation {
    private Submission submission;

    public SRMCompilationInvocation() {
    }
    
    public SRMCompilationInvocation(Submission submission) {
        this.submission = submission;
        
    }

    public Object run(InvocationContext context) throws InvocationException {
        try {
            return new FarmCompiler().compile(submission);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        submission = (Submission) reader.readObject();
        
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(this.submission);
    }
}
