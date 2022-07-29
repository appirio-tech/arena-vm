/*
 * LongRoundOverallScore
 * 
 * Created 07/17/2007
 */
package com.topcoder.server.ejb.TestServices;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;


/**
 * @author Diego Belfer (mural)
 * @version $Id: LongRoundOverallScore.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongRoundOverallScore implements Serializable, CustomSerializable {
    private int roundId;
    private int componentId;
    private List scores;
    
    public LongRoundOverallScore() {
    }

    public LongRoundOverallScore(int roundId, int componentId) {
        this.roundId = roundId;
        this.componentId = componentId;
        this.scores = new LinkedList();
    }
    
    public void addScore(int coderId, double score) {
        scores.add(new ScoreEntry(coderId, score));
    }
    
    public int getRoundId() {
        return roundId;
    }

    public List getScores() {
        return scores;
    }
    
    public int getComponentId() {
        return componentId;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.roundId = reader.readInt();
        this.componentId = reader.readInt();
        this.scores = reader.readArrayList();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(this.roundId);
        writer.writeInt(this.componentId);
        writer.writeList(this.scores);
    }
    
    public static class ScoreEntry implements Serializable, CustomSerializable{
        private int coderId;
        private double score;
        
        public ScoreEntry() {
        }
        
        public ScoreEntry(int coderId, double score) {
            this.coderId = coderId;
            this.score = score;
        }

        public int getCoderId() {
            return coderId;
        }

        public double getScore() {
            return score;
        }
        
        public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
            this.coderId = reader.readInt();
            this.score = reader.readDouble();
        }

        public void customWriteObject(CSWriter writer) throws IOException {
            writer.writeInt(this.coderId);
            writer.writeDouble(this.score);
        }
    }
}
