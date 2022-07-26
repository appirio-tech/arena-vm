/**
 * Class RoundProblem
 *
 * Author: Hao Kung
 *
 * Description: This class will contain information about a RoundProblem
 */
package com.topcoder.server.common;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.SimpleComponent;

public final class RoundComponent implements Serializable, CustomSerializable {

    private int points;
    private SimpleComponent component;

    public RoundComponent() {
    }

    public RoundComponent(int points, SimpleComponent c) {
        this.points = points;
        component = c;
    }

    @JsonIgnore
    @Deprecated
    public final int getPointVal() {
        return points;
    }
    
    public int getPoints() {
    	return points;
    }
    
    public void setPoints(int points) {
    	this.points = points;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(points);
        writer.writeObject(component);
    }

    public void customReadObject(CSReader reader) throws IOException {
        points = reader.readInt();
        component = (SimpleComponent) reader.readObject();
    }

    public final SimpleComponent getComponent() {
        return component;
    }
    
    public void setComponent(SimpleComponent component) {
    	this.component = component;
    }

    public String toString() {
        return "(RoundComponent)[points = " + points + ", component = " + component + "]";
    }

}
