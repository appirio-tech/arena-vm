package com.topcoder.shared.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public class LongRoundScores implements Serializable, CustomSerializable {
    /**
     * ordered GenerationId obtained when the results were obtained from the repository
     */
    private long resultGenerationId;
    private List scores, testCaseIds, coderIds, finalScores;
    private int componentID, roundID;
    private List records;

    public LongRoundScores() {
        
    }

    public LongRoundScores(long resultGenerationId, List scores, List testCaseIds, List coderIds, List finalScores, List coderHandles,  int componentID, int roundID){
        this.resultGenerationId = resultGenerationId;
        this.scores = scores;
        this.testCaseIds = testCaseIds;
        this.coderIds = coderIds;
        this.finalScores = finalScores;
        this.componentID = componentID;
        this.roundID = roundID;
        records = new ArrayList();
        for(int i = 0; i < scores.size(); i++){
            List al = (List) scores.get(i);
            double score = Double.NaN;
            int cr = ((Integer)coderIds.get(i)).intValue();
            String handle = null;
            if(finalScores != null)
                score = ((Double)finalScores.get(i)).doubleValue();
            if(coderHandles != null)
                handle = (String)coderHandles.get(i);
            records.add(new Record(cr,handle,score,al));

        }
    }
    
    public LongRoundScores(long resultGenerationId, ArrayList s, ArrayList tc, ArrayList c, int componentID, int roundID){
        this(resultGenerationId, s,tc,c,null,null,componentID, roundID);
    }
    
    public int getComponentID(){
        return componentID;
    }
    public int getRoundID(){
        return roundID;
    }
    public void setFinalScores(ArrayList al){
        finalScores = al;
    }
    public List getScores(){
        return scores;
    }
    public void setFinalScores(double[] d){
        if(d.length != scores.size()){
            throw new IllegalArgumentException("Length mismatch in setFinalScores!");
        }
        finalScores = new ArrayList();
        for(int i = 0; i<d.length; i++){
            finalScores.add(new Double(d[i]));
        }
    }
    public List getCoders(){
        return coderIds;
    }
    public List getFinalScores(){
        return finalScores;
    }
    public List getRecords(){
        return records;
    }
    public List getTestCaseIds(){
        return testCaseIds;
    }
    public static class Record implements Serializable, CustomSerializable {
        public static final int CODER_SORT = Integer.MAX_VALUE-1;
        public static final int TOTAL_SORT = Integer.MAX_VALUE;
        private double score;
        private int coderID;
        private String handle;
        private List tests;
        
        public Record() {
        }
        
        public Record(int coderID, String handle, double score, List tests){
            this.coderID = coderID;
            this.handle = handle;
            this.score = score;
            this.tests = tests;
        }

        public double getScore(){return score;}
        public int getCoderID(){return coderID;}
        public String getHandle(){return handle;}
        public List getTests(){return tests;}
        public double getTestScore(int idx){
            return ((Double)tests.get(idx)).doubleValue();
        }
        /**
         * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
         */
        public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
            this.coderID = reader.readInt();
            this.handle = reader.readString();
            this.score = reader.readDouble();
            this.tests  = reader.readArrayList();
        }
        /**
         * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
         */
        public void customWriteObject(CSWriter writer) throws IOException {
            writer.writeInt(this.coderID);
            writer.writeString(this.handle);
            writer.writeDouble(this.score);
            writer.writeList(this.tests );
        }
    }
    public long getResultGenerationId() {
        return resultGenerationId;
    }
    public void setResultGenerationId(long resultTimestamp) {
        this.resultGenerationId = resultTimestamp;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.resultGenerationId = reader.readLong();
        this.scores= reader.readArrayList();
        this.testCaseIds = reader.readArrayList();
        this.coderIds = reader.readArrayList();
        this.finalScores = reader.readArrayList();
        this.componentID = reader.readInt();
        this.roundID = reader.readInt();
        this.records = reader.readArrayList();
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(this.resultGenerationId);
        writer.writeList(this.scores);
        writer.writeList(this.testCaseIds);
        writer.writeList(this.coderIds);
        writer.writeList(this.finalScores);
        writer.writeInt(this.componentID);
        writer.writeInt(this.roundID);
        writer.writeList(this.records);
    }
}
