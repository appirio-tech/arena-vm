/*
 * @author John Waymouth
 */
package com.topcoder.server.contest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

import java.io.*;

public class RoundProblemData implements CustomSerializable, Serializable {

    private ProblemData problemData;
    private Division division = new Division();


    public String toString() {
        return "division=" + division +
                "problemData=" + problemData;
    }


    public RoundProblemData() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(problemData);
        writer.writeObject(division);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        problemData = (ProblemData)reader.readObject();
        division = (Division) reader.readObject();
    }

    public RoundProblemData(ProblemData problemData, Division division) {
        this.division = division;
        this.problemData = problemData;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public ProblemData getProblemData() {
        return problemData;
    }

    public void setProblemData(ProblemData ProblemData) {
        // change this to ProblemData?
        this.problemData = problemData;
    }

    public boolean equals(Object rhs) {
        if (rhs instanceof ProblemData) {
            return problemData.equals(rhs);
        } else if (rhs instanceof RoundProblemData) {
            RoundProblemData other = (RoundProblemData) rhs;
            return other.problemData.equals(problemData) &&
                    other.division.equals(division);
        }

        return false;
    }
}