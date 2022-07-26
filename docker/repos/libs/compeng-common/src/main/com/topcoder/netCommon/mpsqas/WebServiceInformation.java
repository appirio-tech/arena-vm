package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;
import com.topcoder.shared.problem.*;

import java.io.*;
import java.util.*;

/**
 * Class to contain information on a Web Service for sending back and
 * forth between client and listener.
 *
 * @author mitalub
 */
public class WebServiceInformation extends WebService
        implements CustomSerializable, Cloneable, Serializable {

    private String interfaceClass = "";
    private String implementationClass = "";
    private ArrayList helperClasses = new ArrayList();
    private HashMap source = new HashMap();
    private int userType;

    public WebServiceInformation() {
    }

    public void setInterfaceClass(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getInterfaceClass() {
        return interfaceClass;
    }

    public void setImplementationClass(String implementationClass) {
        this.implementationClass = implementationClass;
    }

    public String getImplementationClass() {
        return implementationClass;
    }

    public void setHelperClasses(ArrayList helperClasses) {
        this.helperClasses = helperClasses;
    }

    public ArrayList getHelperClasses() {
        return helperClasses;
    }

    public void setSource(String className, String source) {
        this.source.put(className, source);
    }

    public String getSource(String className) {
        return (String) this.source.get(className);
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserType() {
        return userType;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(interfaceClass);
        writer.writeString(implementationClass);
        writer.writeArrayList(helperClasses);
        writer.writeHashMap(source);
        writer.writeInt(userType);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        interfaceClass = reader.readString();
        implementationClass = reader.readString();
        helperClasses = reader.readArrayList();
        source = reader.readHashMap();
        userType = reader.readInt();
    }
}
