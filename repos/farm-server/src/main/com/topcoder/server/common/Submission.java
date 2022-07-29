package com.topcoder.server.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.server.tester.CodeCompilation;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.ExternalizableHelper;
import com.topcoder.shared.problem.SimpleComponent;

public final class Submission implements Externalizable, CustomSerializable, CodeCompilation {

    private int coderId;
    //private int teamId;				// team's id, -1 if no team (default)
    private Location location;
    private RoundComponent component; // Everything about the component
    private long submitTime;
    private String programText;			// the coder's submitted code
    private String compileError;
    private boolean compileStatus;
    private int pointValue;
    private int updatedPoints;
    //private byte[] classFile;
    //private int selectedComponentID;
    //private boolean isFinal;
    //classFiles: Key - className (path), Value - byte array representing the class file
    private ComponentFiles classFiles;
    private int language;
    private int testNumberStart; // test number to start w/ for reference testing.

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(coderId);
        //writer.writeInt(teamId);
        writer.writeObject(location);
        writer.writeObject(component);
        writer.writeLong(submitTime);
        writer.writeString(programText);
        writer.writeString(compileError);
        writer.writeBoolean(compileStatus);
        writer.writeInt(pointValue);
        writer.writeInt(updatedPoints);
//      writer.writeByteArray(classFile);
//      writer.writeInt(selectedComponentID);
//      writer.writeBoolean(isFinal);
        writer.writeObject(classFiles);
        writer.writeInt(language);
        writer.writeInt(testNumberStart);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        coderId = reader.readInt();
        //teamId = reader.readInt();
        location = (Location) reader.readObject();
        component = (RoundComponent) reader.readObject();
        submitTime = reader.readLong();
        programText = reader.readString();
        compileError = reader.readString();
        compileStatus = reader.readBoolean();
        pointValue = reader.readInt();
        updatedPoints = reader.readInt();
//      classFile = reader.readByteArray();
//      selectedComponentID = reader.readInt();
//      isFinal = reader.readBoolean();
        classFiles = (ComponentFiles)reader.readObject();
        language = reader.readInt();
        testNumberStart = reader.readInt();
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        ExternalizableHelper.writeExternal(out, this);
    }

    public void readExternal(ObjectInput in) throws IOException {
        ExternalizableHelper.readExternal(in, this);
    }

    /*
    public Submission () {
        this.teamId = -1;			// default to no team
    }
    */

    public Submission() {
        //Required by Custom serialization
    }

    ////////////////////////////////////////////////////////////////////////////////
    public Submission(Location location, RoundComponent roundComponent, String programText, int language)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.location = location;
        this.component = roundComponent;
        this.programText = programText;
        this.pointValue = -1;
        this.updatedPoints = 0;
        //this.isFinal = isFinal;
        this.language = language;
        //this.teamId = -1;			// default to no team
    }

    @JsonIgnore
    public int getRoundPointVal() {
        return component.getPointVal();
    }

    ////////////////////////////////////////////////////////////////////////////////
    @JsonIgnore
    @Deprecated
    public int getCoderID() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.coderId;

    }

    public int getCoderId() {
		return coderId;
	}
    
    ////////////////////////////////////////////////////////////////////////////////
    public void setCoderId(int in) {
        ////////////////////////////////////////////////////////////////////////////////

        this.coderId = in;

    }

    /**
     * Get the team id of this submission.
     *
     * @return  the team id
     */
    /*
    public int getTeamId() {
        return this.teamId;
    }
    */

    /**
     * Set the team id for this submission.
     *
     * @param   in      the team id
     */
    /*
    public void setTeamId(int in) {
        this.teamId = in;
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public Location getLocation() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.location;

    }

    
    ////////////////////////////////////////////////////////////////////////////////
    public void setLocation( Location in )
    {
        ////////////////////////////////////////////////////////////////////////////////

        this.location = in;

    }
    

    ////////////////////////////////////////////////////////////////////////////////
    @JsonIgnore
    public SimpleComponent getComponent() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.component.getComponent();

    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    public void setProblem( RoundProblem in )
    {
        ////////////////////////////////////////////////////////////////////////////////

        this.problem = in;

    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public void setSubmitTime(long in) {
        ////////////////////////////////////////////////////////////////////////////////

        this.submitTime = in;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public long getSubmitTime() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.submitTime;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setProgramText(String in) {
        ////////////////////////////////////////////////////////////////////////////////

        this.programText = in;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public String getProgramText() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.programText;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setCompileStatus(boolean in) {
        ////////////////////////////////////////////////////////////////////////////////

        this.compileStatus = in;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean getCompileStatus() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.compileStatus;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setCompileError(String in) {
        ////////////////////////////////////////////////////////////////////////////////

        this.compileError = in;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public String getCompileError() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.compileError;

    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    public int getSelectedComponentID()
    {
        ////////////////////////////////////////////////////////////////////////////////

        return this.selectedComponentID;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setSelectedComponentID( int in )
    {
        ////////////////////////////////////////////////////////////////////////////////

        this.selectedComponentID = in;

    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public void setPointValue(int in) {
        ////////////////////////////////////////////////////////////////////////////////
        this.pointValue = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public int getPointValue() {
        ////////////////////////////////////////////////////////////////////////////////
        return this.pointValue;

    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setUpdatedPoints(int in) {
        ////////////////////////////////////////////////////////////////////////////////
        this.updatedPoints = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public int getUpdatedPoints() {
        ////////////////////////////////////////////////////////////////////////////////
        return this.updatedPoints;

    }

    public int getTestNumberStart() {
        return testNumberStart;
    }

    public void setTestNumberStart(int testNumberStart) {
        this.testNumberStart = testNumberStart;
    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    public void setClassFile( byte[] in )
    {
        ////////////////////////////////////////////////////////////////////////////////

        this.classFile = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public byte[] getClassFile()
    {
        ////////////////////////////////////////////////////////////////////////////////

        return this.classFile;

    }
    */

    public void setClassFiles(ComponentFiles in) {
        this.classFiles = in;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ComponentFiles getClassFiles() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.classFiles;

    }


    /*
    ////////////////////////////////////////////////////////////////////////////////
    public boolean isFinal()
    {
        ////////////////////////////////////////////////////////////////////////////////

        return this.isFinal;

    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public int getLanguage() {
        ////////////////////////////////////////////////////////////////////////////////

        return this.language;

    }

    
    ////////////////////////////////////////////////////////////////////////////////
    public void setLanguage( int in )
    {
        ////////////////////////////////////////////////////////////////////////////////

        this.language = in;

    }
    
    @JsonIgnore
    public int getComponentID(){
        return component.getComponent().getComponentID();
    }
    
    @JsonIgnore
    public int getRoundID(){
        return location.getRoundID();
    }

    public RoundComponent getRoundComponent() {
    	return component;
    }
    
	public void setRoundComponent(RoundComponent component) {
		this.component = component;
	}
    
    
}

