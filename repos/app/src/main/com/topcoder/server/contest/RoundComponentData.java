/*
 * @author John Waymouth
 */
package com.topcoder.server.contest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

import java.io.*;

public class RoundComponentData implements CustomSerializable, Serializable {

    private ComponentData componentData;
    private double pointValue = 0.0;
    private Division division = new Division();
    private Difficulty difficulty = new Difficulty();
    private int openOrder = 0;
    private int submitOrder = 0;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeDouble(pointValue);
        writer.writeInt(openOrder);
        writer.writeInt(submitOrder);
        writer.writeObject(componentData);
        writer.writeObject(division);
        writer.writeObject(difficulty);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        pointValue = reader.readDouble();
        openOrder = reader.readInt();
        submitOrder = reader.readInt();
        componentData = (ComponentData)reader.readObject();
        division = (Division)reader.readObject();
        difficulty = (Difficulty)reader.readObject();
    }


    public String toString() {
        return "division=" + division +
                ", difficulty=" + difficulty +
                ", points=" + pointValue +
                ", openOrder=" + openOrder +
                ", submitOrder=" + submitOrder +
                ", component=" + componentData;
    }


    public RoundComponentData() {
    }

    public RoundComponentData(ComponentData componentData, double pointValue, Division division, Difficulty difficulty, int openOrder, int submitOrder) {
        this.difficulty = difficulty;
        this.division = division;
        this.openOrder = openOrder;
        this.pointValue = pointValue;
        this.componentData = componentData;
        this.submitOrder = submitOrder;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public int getOpenOrder() {
        return openOrder;
    }

    public void setOpenOrder(int openOrder) {
        this.openOrder = openOrder;
    }

    public double getPointValue() {
        return pointValue;
    }

    public void setPointValue(double pointValue) {
        this.pointValue = pointValue;
    }

    public ComponentData getComponentData() {
        return componentData;
    }

    public void setComponentData(ComponentData componentData) {
        this.componentData = componentData;
    }

    public int getSubmitOrder() {
        return submitOrder;
    }

    public void setSubmitOrder(int submitOrder) {
        this.submitOrder = submitOrder;
    }

    public boolean equals(Object rhs) {
        if (rhs instanceof ProblemData) {
            return componentData.equals(rhs);
        } else if (rhs instanceof RoundComponentData) {
            RoundComponentData other = (RoundComponentData) rhs;
            return other.componentData.equals(componentData) &&
                    other.division.equals(division) &&
                    other.difficulty.equals(difficulty) &&
                    other.pointValue == pointValue &&
                    other.openOrder == openOrder &&
                    other.submitOrder == submitOrder;
        }

        return false;
    }
}