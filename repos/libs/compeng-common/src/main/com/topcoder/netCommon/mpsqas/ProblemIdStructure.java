package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 * Represents the id structure of a problem and its components.  Used to
 * allow the server to let the client know of any id changes in the problem
 * the client is viewing.
 *
 * @author mitalub
 */
public class ProblemIdStructure
        implements CustomSerializable, Cloneable, Serializable {

    private String problemName;
    private int problemId;
    private ComponentIdStructure[] components;
    private WebServiceIdStructure[] webServices;

    /**
     * For custom serialization only.
     */
    public ProblemIdStructure() {
    }

    public ProblemIdStructure(int problemId, String problemName) {
        this.problemName = problemName;
        this.problemId = problemId;
        components = new ComponentIdStructure[0];
        webServices = new WebServiceIdStructure[0];
    }

    public int getProblemId() {
        return problemId;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setComponents(ArrayList components) {
        this.components = new ComponentIdStructure[components.size()];
        for (int i = 0; i < components.size(); i++) {
            this.components[i] = (ComponentIdStructure) components.get(i);
        }
    }

    public void setComponents(ComponentIdStructure[] components) {
        this.components = components;
    }

    public void addComponent(ComponentIdStructure component) {
        ComponentIdStructure[] old = this.components;
        this.components = new ComponentIdStructure[components.length + 1];
        System.arraycopy(old, 0, this.components, 0, old.length);
        this.components[old.length] = component;
    }

    public ComponentIdStructure[] getComponents() {
        return components;
    }

    public void setWebServices(ArrayList webServices) {
        this.webServices = new WebServiceIdStructure[webServices.size()];
        for (int i = 0; i < webServices.size(); i++) {
            this.webServices[i] = (WebServiceIdStructure) webServices.get(i);
        }
    }

    public void setWebServices(WebServiceIdStructure[] webServices) {
        this.webServices = webServices;
    }

    public void addWebService(WebServiceIdStructure webService) {
        WebServiceIdStructure[] old = this.webServices;
        this.webServices = new WebServiceIdStructure[webServices.length + 1];
        System.arraycopy(old, 0, this.webServices, 0, old.length);
        this.webServices[old.length] = webService;
    }

    public WebServiceIdStructure[] getWebServices() {
        return webServices;
    }


    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(problemName);
        writer.writeInt(problemId);
        writer.writeInt(components.length);
        for (int i = 0; i < components.length; i++) {
            writer.writeObject(components[i]);
        }
        writer.writeInt(webServices.length);
        for (int i = 0; i < webServices.length; i++) {
            writer.writeObject(webServices[i]);
        }
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        problemName = reader.readString();
        problemId = reader.readInt();
        int count = reader.readInt();
        components = new ComponentIdStructure[count];
        for (int i = 0; i < count; i++) {
            components[i] = (ComponentIdStructure) reader.readObject();
        }
        count = reader.readInt();
        webServices = new WebServiceIdStructure[count];
        for (int i = 0; i < count; i++) {
            webServices[i] = (WebServiceIdStructure) reader.readObject();
        }
    }
}
