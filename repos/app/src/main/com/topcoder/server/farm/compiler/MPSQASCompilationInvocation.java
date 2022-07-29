/*
 * MPSQASCompilationInvocation
 * 
 * Created 10/16/2006
 */
package com.topcoder.server.farm.compiler;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.invoke.FarmCompiler;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id: MPSQASCompilationInvocation.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class MPSQASCompilationInvocation implements Invocation {
    private MPSQASFiles mpsqasFiles;

    public MPSQASCompilationInvocation() {
    }
    
    public MPSQASCompilationInvocation(MPSQASFiles mpsqasFiles) {
        this.mpsqasFiles = mpsqasFiles;
    }

    public Object run(InvocationContext context) throws InvocationException {
        try {
            return new FarmCompiler().compileMPSQAS(mpsqasFiles);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        mpsqasFiles = (MPSQASFiles) reader.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(mpsqasFiles);
    }

	public MPSQASFiles getMpsqasFiles() {
		return mpsqasFiles;
	}

	public void setMpsqasFiles(MPSQASFiles mpsqasFiles) {
		this.mpsqasFiles = mpsqasFiles;
	}
}
