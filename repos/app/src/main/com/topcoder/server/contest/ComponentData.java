/*
 * User: John Waymouth
 * Date: July 28, 2002
 * Time: 15:53 PM
 */
package com.topcoder.server.contest;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.*;
import java.util.*;

public class ComponentData implements CustomSerializable, Serializable {

    private int id = 0;
    private int problemId = 0;
    private String className = "";
    private String methodName = "";
    private String resultType = "";
    private List paramTypes = new Vector();
    private ComponentType type;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeInt(problemId);
        writer.writeString(className);
        writer.writeString(methodName);
        writer.writeString(resultType);
        writer.writeObjectArray(paramTypes.toArray());
        writer.writeObject(type);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        problemId = reader.readInt();
        className = reader.readString();
        methodName = reader.readString();
        resultType = reader.readString();
        paramTypes = Arrays.asList(reader.readObjectArray());
        type = (ComponentType)reader.readObject();
    }

    public String toString() {
        return "Component: id=" + id +
                ", problemID=" + problemId +
                ", class=" + className +
                ", method=" + methodName +
                ", result type=" + resultType;
    }

    public ComponentData() {
    }

    public ComponentData(int id, int problemID, String className, String methodName, String resultType, List paramTypes, ComponentType type) {
        this.id = id;
        this.problemId = problemID;
        this.className = className;
        this.methodName = methodName;
        this.resultType = resultType;
        this.paramTypes = paramTypes;
        this.type = type;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(List paramTypes) {
        this.paramTypes = paramTypes;
    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public boolean equals(Object rhs) {
        if (rhs instanceof RoundComponentData) {
            return equals(((RoundComponentData) rhs).getComponentData());
        } else if (rhs instanceof ComponentData) {
            ComponentData other = (ComponentData) rhs;
            return other.id == id;
        }

        return false;
    }
}
