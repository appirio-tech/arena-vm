package com.topcoder.server.tester;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;




public class LongSubmission implements CodeCompilation, Serializable, CustomSerializable {
    private long coderID, componentID, roundID, contestID;
    private int serverID, submissionNumber, languageID;
    private String code, compileError;
    private ComponentFiles classFiles;
    private boolean compileStatus;
    private boolean example;
    private String wrapperClassName;
    
    //stupid java security, needs to be refactored later
    public void setWrapperClassName(String s) {
        this.wrapperClassName = s;
    }
    
    public String getWrapperClassName() {
        return wrapperClassName;
    }
    
    public LongSubmission(){
        
    }
    public LongSubmission(long coderID, long componentID, long roundID, long contestID, int languageID, String code, boolean example) {
        this.coderID = coderID;
        this.componentID = componentID;
        this.roundID = roundID;
        this.contestID = contestID;
        this.languageID = languageID;
        this.code = code;
        this.example = example;
    }
    
    public boolean isExample() {
        return example;
    }
    
    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }
    public String getProgramText(){
        return getCode();
    }
    /**
     * @return Returns the coderID.
     */
    public int getCoderID() {
        return (int)coderID;
    }
    /**
     * @return Returns the componentID.
     */
    public int getComponentID() {
        return (int)componentID;
    }
    /**
     * @return Returns the contestID.
     */
    public int getContestID() {
        return (int)contestID;
    }
    /**
     * @return Returns the roundID.
     */
    public int getRoundID() {
        return (int)roundID;
    }
    /**
     * @return Returns the languageID.
     */
    public int getLanguageID() {
        return languageID;
    }
    public int getLanguage() {
        return languageID;
    }
    /**
     * @return Returns the compileError.
     */
    public String getCompileError() {
        return compileError;
    }
    /**
     * @param compileError The compileError to set.
     */
    public void setCompileError(String compileError) {
        this.compileError = compileError;
    }
    public void setClassFiles(ComponentFiles in) {
        this.classFiles = in;
    }
    public ComponentFiles getClassFiles() {
        return classFiles;
    }
    /**
     * @return Returns the compileStatus.
     */
    public boolean getCompileStatus() {
        return compileStatus;
    }
    /**
     * @param compileStatus The compileStatus to set.
     */
    public void setCompileStatus(boolean compileStatus) {
        this.compileStatus = compileStatus;
    }
    /**
     * @return Returns the submissionNumber.
     */
    public int getSubmissionNumber() {
        return submissionNumber;
    }
    /**
     * @param submissionNumber The submissionNumber to set.
     */
    public void setSubmissionNumber(int submissionNumber) {
        this.submissionNumber = submissionNumber;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        coderID = reader.readLong();
        componentID = reader.readLong();
        roundID = reader.readLong();
        contestID = reader.readLong();
        serverID = reader.readInt();
        submissionNumber = reader.readInt();
        languageID = reader.readInt(); 
        code = reader.readString();
        compileError = reader.readString();
        compileStatus = reader.readBoolean();
        example = reader.readBoolean();
        wrapperClassName = reader.readString();
        classFiles = (ComponentFiles) reader.readObject();

        
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(this.coderID);
        writer.writeLong(this.componentID);
        writer.writeLong(this.roundID);
        writer.writeLong(this.contestID);
        writer.writeInt(this.serverID);
        writer.writeInt(this.submissionNumber);
        writer.writeInt(this.languageID); 
        writer.writeString(this.code);
        writer.writeString(this.compileError);
        writer.writeBoolean(this.compileStatus);
        writer.writeBoolean(this.example);
        writer.writeString(this.wrapperClassName);
        writer.writeObject(this.classFiles);
    }
}