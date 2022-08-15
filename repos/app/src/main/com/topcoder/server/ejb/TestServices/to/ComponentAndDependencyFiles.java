/*
 * ComponentAndDependencyFiles
 *
 * Created 03/21/2007
 */
package com.topcoder.server.ejb.TestServices.to;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.server.webservice.WebServiceRemoteFile;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.ExternalizableHelper;

/**
 * Transfer Object for component files and its dependencies.
 *
 * @author Diego Belfer (mural)
 * @version $Id: ComponentAndDependencyFiles.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class ComponentAndDependencyFiles implements Externalizable, CustomSerializable {
    private ComponentFiles componentFiles;
    private ComponentFiles[] dependencyComponentFiles;
    private WebServiceRemoteFile[] webServiceRemoteFiles;
    private Solution solution;

    public ComponentAndDependencyFiles() {
    }

    public ComponentAndDependencyFiles(ComponentFiles componentFiles, ComponentFiles[] dependencyComponentFiles, WebServiceRemoteFile[] webServiceRemoteFiles) {
        this.componentFiles = componentFiles;
        this.dependencyComponentFiles = dependencyComponentFiles;
        this.webServiceRemoteFiles = webServiceRemoteFiles;
    }

    public ComponentFiles getComponentFiles() {
        return componentFiles;
    }

    public void setComponentFiles(ComponentFiles componentFiles) {
        this.componentFiles = componentFiles;
    }

    public ComponentFiles[] getDependencyComponentFiles() {
        return dependencyComponentFiles;
    }

    public void setDependencyComponentFiles(
            ComponentFiles[] dependencyComponentFiles) {
        this.dependencyComponentFiles = dependencyComponentFiles;
    }

    public WebServiceRemoteFile[] getWebServiceRemoteFiles() {
        return webServiceRemoteFiles;
    }

    public void setWebServiceRemoteFiles(
            WebServiceRemoteFile[] webServiceRemoteFiles) {
        this.webServiceRemoteFiles = webServiceRemoteFiles;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ExternalizableHelper.writeExternal(out, this);
    }

    public void readExternal(ObjectInput in) throws IOException {
        ExternalizableHelper.readExternal(in, this);
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
       this.componentFiles = (ComponentFiles) reader.readObject();
       this.dependencyComponentFiles = (ComponentFiles[]) reader.readObjectArray(ComponentFiles.class);
       this.webServiceRemoteFiles= (WebServiceRemoteFile[]) reader.readObjectArray(WebServiceRemoteFile.class) ;
       this.solution = (Solution) reader.readObject();
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(componentFiles);
        writer.writeObjectArray(dependencyComponentFiles);
        writer.writeObjectArray(webServiceRemoteFiles);
        writer.writeObject(solution);

    }
}
