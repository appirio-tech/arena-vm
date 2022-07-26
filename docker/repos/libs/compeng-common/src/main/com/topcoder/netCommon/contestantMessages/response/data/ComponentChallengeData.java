package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.DataType;

/**
 * Defines the information needed to challenge a team problem component, including the param types, method name, and
 * class name of the component being challenged. This is needed because for team problems, challenging a component
 * should actually enter the argument values for the problem's main component.
 * 
 * @author mitalub
 * @version $Id: ComponentChallengeData.java 72424 2008-08-20 08:06:01Z qliu $
 */
public class ComponentChallengeData implements CustomSerializable {
    /** Represents the argument types for the challenge. */
    private DataType[] paramTypes;

    /** Represents the name of the class to be challenged. */
    private String className;

    /** Represents the name of the method to be challenged. */
    private String methodName;

    /** Represents the ID of the problem component to be challenged. */
    private int componentID;

    /**
     * Creates a new instance of <code>ComponentChallengeData</code>. It is required by custom serialization.
     */
    public ComponentChallengeData() {
    }

    /**
     * Creates a new instance of <code>ComponentChallengeData</code>. There is no copy.
     * 
     * @param className the name of the class to be challenged.
     * @param methodName the name of the method to be challenged.
     * @param paramTypes the argument types for the challenge.
     * @param componentID the ID of the problem component to be challenged.
     */
    public ComponentChallengeData(String className, String methodName, DataType[] paramTypes, int componentID) {
        this.className = className;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.componentID = componentID;
    }

    /**
     * Gets the ID of the problem component to be challenged.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Gets the name of the class to be challenged.
     * 
     * @return the class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets the name of the method to be challenged.
     * 
     * @return the method name.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Gets the argument types for the challenge. There is no copy.
     * 
     * @return the argument types.
     */
    public DataType[] getParamTypes() {
        return paramTypes;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(className);
        writer.writeString(methodName);
        writer.writeObjectArray(paramTypes);
        writer.writeInt(componentID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        className = reader.readString();
        methodName = reader.readString();
        paramTypes = (DataType[]) reader.readObjectArray(DataType.class);
        componentID = reader.readInt();
    }
}
