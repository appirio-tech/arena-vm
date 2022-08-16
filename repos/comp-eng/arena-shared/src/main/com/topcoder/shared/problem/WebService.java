package com.topcoder.shared.problem;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;

/**
 * An element to hold information about a Web Service.
 * 
 * @author mitalub
 * @version $Id: WebService.java 71757 2008-07-17 09:13:19Z qliu $
 */

// Note: this is part of the plugin API javadoc. Please be sure to
// keep the javadoc comments up to date. When implementing changes,
// be sure to regenerate/repackage the plugin API javadoc.
public class WebService extends BaseElement {
    /** The name of the web service. */
    private String name = "";

    /** The javadoc address for the web service. */
    private String javaDocAddress = "";

    /** The unique identifier for the web service. */
    private int webServiceId = -1;

    /** The unique problem identifer of the related problem. */
    private int problemId = -1;

    /** Empty constructor required by custom serialization. */
    public WebService() {
    }

    /**
     * Sets the name of the web service.
     * 
     * @param name the name of the web service.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the web service.
     * 
     * @return the name of the web service.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the javadoc address.
     * 
     * @param javaDocAddress the java doc address.
     */
    public void setJavaDocAddress(String javaDocAddress) {
        this.javaDocAddress = javaDocAddress;
    }

    /**
     * Returns the javadoc address.
     * 
     * @return the javadoc address.
     */
    public String getJavaDocAddress() {
        return javaDocAddress;
    }

    /**
     * Sets the unique identifier for the web service.
     * 
     * @param webServiceId the unique identifier of the web service.
     */
    public void setWebServiceId(int webServiceId) {
        this.webServiceId = webServiceId;
    }

    /**
     * Returns the unique identifier of the web service.
     * 
     * @return the unique identifier of the web service.
     */
    public int getWebServiceId() {
        return webServiceId;
    }

    /**
     * Sets the identifier of the problem related to the web service.
     * 
     * @param problemId the problem identifier related to the web service.
     */
    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    /**
     * Returns the problem identifier related to the web service.
     * 
     * @return the problem identifier related to the web service.
     */
    public int getProblemId() {
        return problemId;
    }

    /** Custom serialization */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(name);
        writer.writeString(javaDocAddress);
        writer.writeInt(webServiceId);
        writer.writeInt(problemId);
    }

    /** Custom serialization */
    public void customReadObject(CSReader reader) throws IOException {
        name = reader.readString();
        javaDocAddress = reader.readString();
        webServiceId = reader.readInt();
        problemId = reader.readInt();
    }

    /**
     * Returns an XML representation of the web service. Formatted like
     * <code>&lt;web-service&gt;&lt;name&gt;the name&lt;/name&gt;&lt;/webservice&gt;</code>.
     * 
     * @return the XML representation of the web service.
     */
    public String toXML() {
        StringBuffer xml = new StringBuffer();
        xml.append("<web-service>");
        xml.append("<name>");
        xml.append(name);
        xml.append("</name>");
        xml.append("</web-service>");
        return xml.toString();
    }
}
