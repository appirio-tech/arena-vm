/*
 * LongCompilationInvocation
 * 
 * Created 10/16/2006
 */
package com.topcoder.server.farm.compiler;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.services.compiler.invoke.FarmCompiler;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongCompilationInvocation.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class LongCompilationInvocation implements Invocation {
    private ProblemComponent problemComponent;
    private LongSubmission longSubmission;

    public LongCompilationInvocation() {
    }
    
    public LongCompilationInvocation(LongSubmission longSubmission, ProblemComponent problemComponent) {
        this.longSubmission = longSubmission;
        this.problemComponent = problemComponent;
        
    }

    public Object run(InvocationContext context) throws InvocationException {
        try {
            return new FarmCompiler().compileLong(longSubmission, problemComponent);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        longSubmission = (LongSubmission) reader.readObject();
        problemComponent = (ProblemComponent) reader.readObject();
        
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(this.longSubmission);
        writer.writeObject(this.problemComponent);
    }
}
