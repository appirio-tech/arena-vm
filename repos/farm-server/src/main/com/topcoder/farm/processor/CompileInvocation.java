package com.topcoder.farm.processor;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.invoke.FarmCompiler;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class CompileInvocation implements Invocation {

	private static final long serialVersionUID = 1L;

	private MPSQASFiles mpsqasFiles;

	public MPSQASFiles getMpsqasFiles() {
		return mpsqasFiles;
	}

	public void setMpsqasFiles(MPSQASFiles mpsqasFiles) {
		this.mpsqasFiles = mpsqasFiles;
	}

	@Override
	public void customReadObject(CSReader arg0) throws IOException, ObjectStreamException {
		throw new IOException("not implemented");

	}

	@Override
	public void customWriteObject(CSWriter arg0) throws IOException {
		throw new IOException("not implemented");

	}

	@Override
	public Object run(InvocationContext context) throws InvocationException {
		FarmCompiler fc = new FarmCompiler();
		if (mpsqasFiles != null) {
			MPSQASFiles files = fc.compileMPSQAS(mpsqasFiles);
			return files;
		}
		return null;
	}

}
