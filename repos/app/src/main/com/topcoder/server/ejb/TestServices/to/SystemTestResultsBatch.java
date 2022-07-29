/*
 * SystemTestResultsBatch
 *
 * Created 03/26/2007
 */
package com.topcoder.server.ejb.TestServices.to;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.ExternalizableHelper;

/**
 * @author Diego Belfer (mural)
 * @version $Id: SystemTestResultsBatch.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class SystemTestResultsBatch implements CustomSerializable, Externalizable {
    private SystemTestResult[] results;

    public SystemTestResultsBatch(SystemTestResult[] results) {
        this.results = results;
    }

    public SystemTestResultsBatch() {
    }

    public SystemTestResult[] getResults() {
        return results;
    }

    public void setResults(SystemTestResult[] results) {
        this.results = results;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.results = (SystemTestResult[]) reader.readObjectArray(SystemTestResult.class);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObjectArray(this.results);
    }

    public void readExternal(ObjectInput in) throws IOException {
        ExternalizableHelper.readExternal(in, this);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ExternalizableHelper.writeExternal(out, this);
    }
}
