package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 * Represents the id structure of a component and its web services.  Used to
 * allow the server to let the client know of any id changes in the component
 * the client is viewing.
 *
 * @author mitalub
 */
public class ComponentIdStructure
        implements CustomSerializable, Cloneable, Serializable {

    private String componentName;
    private int componentId;
    private Integer[] testCaseIds;

    /**
     * For custom serialization only.
     */
    public ComponentIdStructure() {
    }

    public ComponentIdStructure(int componentId, String componentName, Integer[] testCaseIds) {
        this.componentName = componentName;
        this.componentId = componentId;
        this.testCaseIds = testCaseIds;
    }

    public int getComponentId() {
        return componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public Integer[] getTestCaseIds() {
        return testCaseIds;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(componentName);
        writer.writeInt(componentId);
        writer.writeInt(testCaseIds.length);
        for (int i=0;i<testCaseIds.length;++i) {
            writer.writeInt(testCaseIds[i].intValue());
        }
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        componentName = reader.readString();
        componentId = reader.readInt();
        testCaseIds = new Integer[reader.readInt()];
        for (int i=0;i<testCaseIds.length;++i) {
            testCaseIds[i] = new Integer(reader.readInt());
        }
    }
}
