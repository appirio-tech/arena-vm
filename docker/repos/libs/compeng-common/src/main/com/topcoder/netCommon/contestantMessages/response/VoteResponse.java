package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.util.ArrayList;

import com.topcoder.netCommon.contestantMessages.UserInfo;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to the voting information to the client in the 'Weakest Link' round. The information includes
 * basic user information, such as rating, rated events, etc. For tie-breaking voting, the list of participants who tied
 * with maximum votes is sent as well.<br>
 * Use: At the beginning of a voting phase in a 'Weakest Link' round, <code>VoteRequest</code> is sent to all
 * participants to vote for the eliminiation. The vote is done by sending <code>VoteRequest</code> to the server.
 * 
 * @author Qi Liu
 * @version $Id: VoteResponse.java 72385 2008-08-19 07:00:36Z qliu $
 */
public final class VoteResponse extends BaseResponse {
    /** Represents the type of vote is a normal vote. */
    public static final byte VOTING = 1;

    /** Represents the type of vote is a tie breaking vote. */
    public static final byte TIE_BREAK_VOTING = 2;

    /** Represents the ID of the 'Weakest Link' round. */
    private int roundId;

    /** Represents the name of the round. */
    private String roundName;

    /** Represents the information of participants to be voted. */
    private UserInfo[] coders;

    /** Represents the title of the vote. */
    private String title;

    /** Represents the tied users in a tie breaking vote. */
    private ArrayList maxList;

    /** Represents the type of the vote. */
    private byte type;

    /**
     * Creates a new instance of <code>VoteResponse</code>. It is required by custom serialization.
     */
    public VoteResponse() {
    }

    /**
     * Creates a new instance of <code>VoteRequest</code>. The title of the vote is 'Voting', and the type of voting
     * is a normal vote. There is no copy.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     * @param roundName the name of the round.
     * @param coders the participants to be voted.
     */
    public VoteResponse(int roundId, String roundName, UserInfo[] coders) {
        this(roundId, roundName, coders, "Voting");
    }

    /**
     * Creates a new instance of <code>VoteResponse</code>. The type of the voting is a normal vote. There is no
     * copy.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     * @param roundName the name of the round.
     * @param coders the participants to be voted.
     * @param title the title of the vote.
     */
    private VoteResponse(int roundId, String roundName, UserInfo[] coders, String title) {
        this(roundId, roundName, coders, title, new ArrayList(), VOTING);
    }

    /**
     * Creates a new instance of <code>VoteResponse</code>. The type of the voting is a tie-breaking vote. There is
     * no copy. The list of tied users is a list of IDs (<code>Integer</code>) of tied users.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     * @param roundName the name of the round.
     * @param coders the participants in the round.
     * @param title the title of the vote.
     * @param maxList the list of tied users to be voted.
     */
    public VoteResponse(int roundId, String roundName, UserInfo[] coders, String title, ArrayList maxList) {
        this(roundId, roundName, coders, title, maxList, TIE_BREAK_VOTING);
    }

    /**
     * Creates a new instance of <code>VoteResponse</code>.
     * 
     * @param roundId the ID of the 'Weakest Link' round.
     * @param roundName the name of the round.
     * @param coders the participants in the round.
     * @param title the title of the vote.
     * @param maxList the list of tied users to be voted if it is a tie-breaking vote.
     * @param type the type of the vote.
     */
    private VoteResponse(int roundId, String roundName, UserInfo[] coders, String title, ArrayList maxList, byte type) {
        this.roundId = roundId;
        this.roundName = roundName;
        this.coders = coders;
        this.title = title;
        this.maxList = maxList;
        this.type = type;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
        writer.writeString(roundName);
        writer.writeObjectArray(coders);
        writer.writeString(title);
        writer.writeArrayList(maxList);
        writer.writeByte(type);
    }

    public void customReadObject(CSReader reader) throws IOException {
        roundId = reader.readInt();
        roundName = reader.readString();
        coders = (UserInfo[]) reader.readObjectArray(UserInfo.class);
        title = reader.readString();
        maxList = reader.readArrayList();
        type = reader.readByte();
    }

    /**
     * Gets the name of the 'Weakest Link' round.
     * 
     * @return the round name.
     */
    public String getRoundName() {
        return roundName;
    }

    /**
     * Gets the participants in the round. If it is a normal voting, it is the users to be voted. There is no copy.
     * 
     * @return the participants in the round.
     */
    public UserInfo[] getCoders() {
        return coders;
    }

    /**
     * Gets the ID of the 'Weakest Link' round.
     * 
     * @return the round ID.
     */
    public int getRoundId() {
        return roundId;
    }

    /**
     * Gets the title of the vote.
     * 
     * @return the title of the vote.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the list of tied users to be voted if it is a tie-breaking vote. The list of tied users is a list of IDs (<code>Integer</code>)
     * of tied users. There is no copy.
     * 
     * @return the list of tied users to be voted if it is a tie-breaking vote; or empty list otherwise.
     */
    public ArrayList getMaxList() {
        return maxList;
    }

    /**
     * Gets the type of the vote.
     * 
     * @return the type of the vote.
     */
    public byte getType() {
        return type;
    }

}
