package com.topcoder.server.contest;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

/**
 * A class representing room assignment algorithm for contest round. This
 * algorithm should be used to assign coders registered for specified round
 * to rooms.<p>
 * This class contains all data necessary to perform assignment coders to 
 * rooms : number of coders per room, type of seeding algorithm and other 
 * parameters that were specified in "Assign Rooms" dialog in previous version
 * of Admin Tool client application.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class RoundRoomAssignment implements CustomSerializable, Serializable {

    /**
     * A maximum number of coders to be assigned per room.
     */
    private int codersPerRoom = 0;

    /**
     * A seeding algorithm. The value should be one of <code>
     * ContestConstants.*_SEEDING</code> constants.
     *
     * @see ContestConstants#RANDOM_SEEDING
     * @see ContestConstants#IRON_MAN_SEEDING
     * @see ContestConstants#NCAA_STYLE
     * @see ContestConstants#EMPTY_ROOM_SEEDING
     * @see ContestConstants#WEEKEST_LINK_SEEDING
     */
    private int type = ContestConstants.IRON_MAN_SEEDING;

    /**
     * A flag indicating that room assignment should be performed on a
     * "by division" basis.
     */
    private boolean isByDivision = true;

    /**
     * A flag indicating that room assignment should be persisted in database
     */
    private boolean isFinal = true;

    /**
     * A flag indicating that room assignment should be performed on a
     * "by region" basis.
     */
    private boolean isByRegion = false;

    private double p = 0.00;

    /**
     * An ID of round that this room assignment algorithm is assigned to.
     */
    private int roundID = 0;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(codersPerRoom);
        writer.writeInt(type);
        writer.writeBoolean(isByDivision);
        writer.writeBoolean(isFinal);
        writer.writeBoolean(isByRegion);
        writer.writeDouble(p);
        writer.writeInt(roundID);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        codersPerRoom = reader.readInt();
        type = reader.readInt();
        isByDivision = reader.readBoolean();
        isFinal = reader.readBoolean();
        isByRegion = reader.readBoolean();
        p = reader.readDouble();
        roundID = reader.readInt();
    }
    
    public RoundRoomAssignment() {
        
    }

    /**
     * Constructs new RoundRoomAssignment object with default values.
     *
     * @param  roundID an ID of requested round
     * @throws IllegalArgumentException if given roundID is not positive
     */
    public RoundRoomAssignment(int roundID) {
        if( roundID < 0 ) throw new IllegalArgumentException("invalid roundID = " + roundID);
        this.roundID = roundID;
    }

    /**
     * Constructs new RoundRoomAssignment object with specified values.
     *
     * @param  roundID an ID of requested round
     * @param  codersPerRoom a maximum number of coders that could be assigned
     *         to single room
     * @param  type an algorithm that should be used to perform the assignment
     *         of coders to rooms
     * @param  isByDivision
     * @param  isFinal
     * @param  isByRegion
     * @param  p
     * @throws IllegalArgumentException if coderPerRoom is negative or value
     *         of type is not equal to any of ContestConstants.*_SEEDING
     *         constants
     * @see    ContestConstants#RANDOM_SEEDING
     * @see    ContestConstants#IRON_MAN_SEEDING
     * @see    ContestConstants#NCAA_STYLE
     * @see    ContestConstants#EMPTY_ROOM_SEEDING
     * @see    ContestConstants#WEEKEST_LINK_SEEDING
     */
    public RoundRoomAssignment(int roundID, int codersPerRoom, int type, 
        boolean isByDivision, boolean isFinal, boolean isByRegion, double p) {

        this(roundID);
        setCodersPerRoom(codersPerRoom);
        setType(type);
        setByDivision(isByDivision);
        setFinal(isFinal);
        setByRegion(isByRegion);
        setP(p);
    }

    /**
     * Get round ID this assignment data is associated with.
     *
     * @return an int containining round id.
     */
    public int getRoundId() {
        return this.roundID;
    }
    
    /**
     * Get maximum allowed number of coders per room.
     *
     * @return an int containining maximum number of coders per room.
     */
    public int getCodersPerRoom() {
        return this.codersPerRoom;
    }

    /**
     * Gets the algorithm that should be used to assign coders to rooms. 
     * Returned value is one of ContestConstants.*_SEEDING constants.
     *
     * @return an int representing the seeding algorithm.
     * @see    ContestConstants#RANDOM_SEEDING
     * @see    ContestConstants#IRON_MAN_SEEDING
     * @see    ContestConstants#NCAA_STYLE
     * @see    ContestConstants#EMPTY_ROOM_SEEDING
     * @see    ContestConstants#WEEKEST_LINK_SEEDING
     */
    public int getType() {
        return this.type;
    }

    /**
     * Checks whether round room assignment should be performed on "by region"
     * basis or not.
     *
     * @return true if assignment should be performed on "by region" basis
     */
    public boolean isByRegion() {
        return this.isByRegion;
    }

    /**
     * Checks whether round room assignment should be performed on "by division"
     * basis or not.
     *
     * @return true if assignment should be performed on "by division" basis
     */
    public boolean isByDivision() {
        return this.isByDivision;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public double getP() {
        return this.p;
    }

    /**
     * Sets the seeding algorithm that should be used to assign coders to 
     * rooms.
     *
     * @param  type an int representing seeding algorithm
     * @throws IllegalArgumentException if given type is not equal to any of
     *         ContestConstants#*_SEEDING constants.
     * @see    ContestConstants#RANDOM_SEEDING
     * @see    ContestConstants#IRON_MAN_SEEDING
     * @see    ContestConstants#NCAA_STYLE
     * @see    ContestConstants#EMPTY_ROOM_SEEDING
     * @see    ContestConstants#WEEKEST_LINK_SEEDING
     */
    public void setType(int type) {
    	if( type != ContestConstants.RANDOM_SEEDING       &&
            type != ContestConstants.IRON_MAN_SEEDING     &&
            type != ContestConstants.NCAA_STYLE           &&
            type != ContestConstants.EMPTY_ROOM_SEEDING   &&
            type != ContestConstants.WEEKEST_LINK_SEEDING && 
            type != ContestConstants.ULTRA_RANDOM_SEEDING && 
            type != ContestConstants.TCO05_SEEDING        && 
            type != ContestConstants.DARTBOARD_SEEDING    && 
            type != ContestConstants.ULTRA_RANDOM_DIV2_SEEDING    && 
            type != ContestConstants.TCHS_SEEDING) {
            throw new IllegalArgumentException("setType() - invalid type = " + type );
        }
        this.type = type;
    }

    /**
     * Sets the maximum allowed number of coders per room.
     *
     * @param  codersPerRoom a maximum allowed number of coders per room
     * @throws IllegalArgumentException if given parameter is negative
     */
    public void setCodersPerRoom(int codersPerRoom) {
        if( codersPerRoom < 0 )
            throw new IllegalArgumentException("setCodersPerRoom() - invalid number = " + codersPerRoom );
        this.codersPerRoom = codersPerRoom;
    }

    /** 
     * Sets (if true) or unsets (if false) the flag indicating that room
     * assignment should be performed on "by division" basis.
     *
     * @param isByDivision
     */
    public void setByDivision(boolean isByDivision) {
        this.isByDivision = isByDivision;
    }

    /** 
     * Sets (if true) or unsets (if false) the flag indicating that room
     * assignment should be performed on "by region" basis.
     *
     * @param isByRegion
     */
    public void setByRegion(boolean isByRegion) {
        this.isByRegion = isByRegion;
    }

    /** 
     * Sets (if true) or unsets (if false) the flag indicating that room
     * assignment should be persisted in database.
     *
     * @param isFinal
     */
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    /**
     * Sets the value of 'p'
     * 
     * @param  p
     * @throws IllegalArgumentException if given argument is less than 0
     */
    public void setP(double p) {
        if( p < 0.0 )
            throw new IllegalArgumentException("setP() - invalid number = " + p );
        this.p = p;
    }
}
