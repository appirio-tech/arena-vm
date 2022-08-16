/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:12:08 PM
 */
package com.topcoder.server.contest;

import java.io.IOException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * <p>
 * Modifications for AdminTool 2.0 are :
 * </p>
 * <p>
 * New constructor RoundData(ContestData, int) added to meet the
 * Using Sequences when creating rounds requirement. Probably
 * RoundData(ContestData) constructor becomes redundant.
 * </p>
 * <p>
 * New private instance variable of type RoundRoomAssignment is added to
 * refer to algorithm that should be used to assign coders to rooms. Also
 * corresponding get- and set- methods are added.
 * </p>
 * 
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Event Support For Registration v1.0):
 * <ol>
 * <li>Added {@link #getEvent()}  to get the round event data.</li>
 * <li>Added {@link #setEvent(RoundEventData)} to store the round event data. </li>
 * <li>Updated {@link #customWriteObject(CSWriter)} to add round event data fields.s</li>
 * <li>Updated {@link #customReadObject(CSReader)} to retrieve the round event data.</li>
 * <li>Updated {@link #RoundData()} constructor to build the round event data.</li>
 * </ol>
 * </p>
 *
 * @author TCDEVELOPER
 * @version 1.0
 */
public class RoundData implements CustomSerializable, Serializable {

    private ContestData contest;
    private SurveyData survey;
    private RoundSegmentData segments;
    private RoundLanguageData languages;
    private RoundEventData event;
    
    private int id = 0;
    private String name = "";
    private String status = "F";
    private String short_name = "";
    private RoundType type = new RoundType();
    private int registrationLimit = 1024;
    private int invitationalType = ContestConstants.NOT_INVITATIONAL;
    private Region region = new Region();
    

    private int adminRoomID = 0;
    private String adminRoomName = "";

    /**
	 * An algorithm that should be used to assign coders to round's rooms.
	 *
	 * @since Admin Tool 2.0
	 */
    private RoundRoomAssignment roomAssignment = null;
    /**
     * <p>
     * the default constructor to do nothing.
     * </p>
     */
    public RoundData() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(contest);
        writer.writeObject(survey);
        writer.writeObject(segments);
        writer.writeObject(languages);
        writer.writeObject(event);
        
        writer.writeInt(id);
        writer.writeString(name);
        writer.writeString(status);
        writer.writeString(short_name);
        writer.writeObject(type);
        writer.writeInt(registrationLimit);
        writer.writeInt(invitationalType);
        writer.writeInt(adminRoomID);
        writer.writeString(adminRoomName);
        
        writer.writeObject(roomAssignment);
        writer.writeObject(region);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        contest = (ContestData)reader.readObject();
        survey = (SurveyData)reader.readObject();
        segments = (RoundSegmentData)reader.readObject();
        languages = (RoundLanguageData) reader.readObject();
        event = (RoundEventData)reader.readObject();
        
        id = reader.readInt();
        name = reader.readString();
        status = reader.readString();
        short_name = reader.readString();
        
        type = (RoundType)reader.readObject();
        registrationLimit = reader.readInt();
        invitationalType = reader.readInt();
        adminRoomID = reader.readInt();
        adminRoomName = reader.readString();
        
        roomAssignment = (RoundRoomAssignment)reader.readObject();
        region = (Region)reader.readObject();
    }
    /**
     * <p>
     * get the round event data.
     * </p>
     * @return the round event data.
     */
    public RoundEventData getEvent() {
        return event;
    }
    /**
     * <p>
     * set the round event data.
     * </p>
     * @param event the round event data.
     */
    public void setEvent(RoundEventData event) {
        this.event = event;
    }

    public String getShortName() {
        return short_name;
    }
    
    public void setShortName(String s) {
        short_name = s;
    }
    
    public int getInvitationType() {
        return invitationalType;
    }

    public void setInvitationType(int invitationalType) {
        this.invitationalType = invitationalType;
    }

    public int getRegistrationLimit() {
        return registrationLimit;
    }

    public void setRegistrationLimit(int registrationLimit) {
        this.registrationLimit = registrationLimit;
    }

    public RoundType getType() {
        return type;
    }

    public void setType(RoundType type) {
        this.type = type;
    }

    /**
     * Constructs new RoundData with specified round ID and given contest. 
     * Initializes newly added roundRoomAssignment variable with 
     * RoundRommAssignment object with default values.
     *
     * @param  contest a ContestData object representing contest that this
     *         newly created round is part of
     * @param  id an ID of newly created round
     * @throws IllegalArgumentException if given contest is null or given 
     *         id is not positive
     * @since  Admin Tool 2.0
     */
    public RoundData(ContestData contest, int id) {
        if( contest == null )
            throw new IllegalArgumentException("contest may not be null");
        if( id <= 0 )
            throw new IllegalArgumentException("round id must be positive");
        this.contest = contest;
        this.id = id;
        roomAssignment = new RoundRoomAssignment(id);
    }

    /**
     * This existing constructor was modified to initialize newly added 
     * roundRoomAssignment variable with RoundRoomAssignment object 
     * with default values.
     *
     * @param  contest a ContestData object representing contest that this
     *         newly created round is part of
     * @param  id an ID of newly created round
     * @param  sname of the round
     * @param  round type
     * @param  status of round
     * @param  registration limit
     * @param  is this an invitational
     */
    public RoundData(ContestData contest, int id, String name, 
                        RoundType type, String status, int registrationLimit,
                        int invitationalType, String short_name, Region region) {
        this.contest = contest;
        this.id = id;
        this.invitationalType = invitationalType;
        this.name = name;
        this.short_name = short_name;
        this.registrationLimit = registrationLimit;
        this.status = status;
        this.type = type;
        survey = new SurveyData(id);
        segments = new RoundSegmentData(id);
        languages = new RoundLanguageData(id);
        event = new RoundEventData(id);
        if (type.getId() == ContestConstants.LONG_ROUND_TYPE_ID) {
            segments.setRegistrationLength(0);
            segments.setCodingLength(450);
            segments.setIntermissionLength(0);
            segments.setChallengeLength(0);
        }
        roomAssignment = new RoundRoomAssignment(id);
        this.region = region;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ContestData getContest() {
        return contest;
    }

    public void setContest(ContestData contest) {
        this.contest = contest;
    }

    public SurveyData getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyData survey) {
        if (survey != null)
            this.survey = survey;
    }

    public RoundSegmentData getSegments() {
        return segments;
    }

    public void setSegments(RoundSegmentData segments) {
        if (segments != null)
            this.segments = segments;
    }
    
    public void setLanguages(RoundLanguageData languages) {
        this.languages = languages;
    }
    
    public RoundLanguageData getLanguages() {
        return languages;
    }
    
    public int getAdminRoomID() {
        return adminRoomID;
    }

    public void setAdminRoomID(int adminRoomID) {
        this.adminRoomID = adminRoomID;
    }

    public String getAdminRoomName() {
        return adminRoomName;
    }

    public void setAdminRoomName(String adminRoomName) {
        this.adminRoomName = adminRoomName;
    }

    /**
     * Sets the algorithm that should be used to assign coders to round's 
     * rooms to specified one.
     *
     * @param  roomAssignment an algorithm to be used to assign coders to rooms
     * @throws IllegalArgumentException if given parameter is null
     * @since  Admin Tool 2.0
     */
    public void setRoomAssignment(RoundRoomAssignment roomAssignment) {
        if( roomAssignment == null) 
            throw new IllegalArgumentException("null room assignment not allowed");
        this.roomAssignment = roomAssignment;
    }

    /**
     * Gets the definition of algorithm that should be used to assign coders
     * to rooms.
     *
     * @return a RoundRoomAssignment object representing algorithm to be used
     *         to assign coders to rooms
     * @since Admin Tool 2.0
     */
    public RoundRoomAssignment getRoomAssignment() {
        return roomAssignment;
    }
    
    public Region getRegion() {
        return region;
    }
    
    public void setRegion(Region region) {
        this.region = region;
    }

    public boolean equals(Object obj) {
        if (obj instanceof RoundData) {
            return id == ((RoundData) obj).getId();
        }
        return false;
    }

    public String toString() {
        return "RoundData: id=" + id +
                ", name=" + name;
    }
}


